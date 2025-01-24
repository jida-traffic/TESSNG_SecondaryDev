import json
import random
import time
import collections
from PySide2.QtCore import *
from PySide2.QtGui import *
from Tessng import tessngPlugin, tessngIFace, m2p, p2m
from Tessng import Online, UnitOfMeasure
from Tessng import _RoutingFLowRatio, _DecisionPoint
from Tessng import *

import functions

class IFunctionTest:
    """ 定义二次开发案例类 SecondaryDevCases
        Attributes:
            id: 二次开发案例对象编号
    """

    def __init__(self, _id):
        self.id = _id

    # 信控编辑案例
    def edit_signal_controller(self):
        """ 信控编辑
        :return:
        """
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()

        # 创建两条新路段和一条连接段作为示例
        startPoint1 = QPointF(m2p(-300), m2p(-200))
        endPoint1 = QPointF(m2p(-50), m2p(-200))
        lPoint1 = [startPoint1, endPoint1]
        link1 = netiface.createLink(lPoint1, 3, "信控编辑路段1")

        startPoint2 = QPointF(m2p(50), m2p(-200))
        endPoint2 = QPointF(m2p(300), m2p(-200))
        lPoint2 = [startPoint2, endPoint2]
        link2 = netiface.createLink(lPoint2, 3, "信控编辑路段2")

        # 连接段车道连接列表
        lLaneObjects = []
        if link1 and link2:
            lFromLaneNumber = [1, 2, 3]
            lToLaneNumber = [1, 2, 3]
            connector = netiface.createConnector(link1.id(), link2.id(), lFromLaneNumber, lToLaneNumber,
                                                 "信控编辑连接段", True)
            if connector:
                lLaneObjects = connector.laneObjects()
                for laneObj in lLaneObjects:
                    print("上游车道ID", laneObj.fromLane().id(), "下游车道ID", laneObj.toLane().id())
        # 创建发车点
        if link1:
            dp = netiface.createDispatchPoint(link1)
            if dp:
                dp.addDispatchInterval(1, 3600, 3600)

        # 创建信号灯组
        signalGroup = netiface.createSignalGroup("信号灯组1", 60, 1, 3600)
        # 创建相位,40秒绿灯，黄灯3秒，全红3秒
        red = Online.ColorInterval("G", 40)
        green = Online.ColorInterval("Y", 3)
        yellow = Online.ColorInterval("R", 3)
        signalPhase = netiface.createSignalPhase(signalGroup, "信号灯组1相位1",
                                                 [green, yellow, red])
        # 创建信号灯
        for index, laneObj in enumerate(lLaneObjects):
            signalLamp = netiface.createSignalLamp(signalPhase, "信号灯{}".format(index + 1), laneObj.fromLane().id(),
                                                   laneObj.toLane().id(), m2p(2.0))

    # 双环信控方案下发
    def double_ring_signal_control(self, current_simuTime):
        """ 双环信控方案下发
        :param current_simuTime: 当前仿真时间
        :return:
        """

        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()

        # 读取方案数据
        with open('./Data/Signal_Plan_Data_1109.json', 'r', encoding='utf-8') as json_file:
            signal_groups_dict = json.load(json_file)

        # 创建信号灯组和相位
        for group_name, group in signal_groups_dict.items():
            # 当前灯组
            current_signal_group = None
            # 通过灯组名称查询到灯组
            all_signal_groups_lst = netiface.signalGroups()
            for signal_group in all_signal_groups_lst:
                if signal_group.groupName() == group_name:
                    current_signal_group = signal_group
                    break
            if current_signal_group:
                current_signal_group_phases_lst = current_signal_group.phases()
            else:
                print("FindError: The signalGroup not in current net.")
                break

            # 获取所有灯组的起始时间
            signal_group_startTime_lst = list(group.keys())
            for index, group_data in enumerate(group.values()):
                start_time = signal_group_startTime_lst[index]
                end_time = signal_group_startTime_lst[index + 1] if index != len(
                    signal_group_startTime_lst) - 1 else "24:00"
                # 起始时间和结束时间的秒数表示
                start_time_seconds = functions.time_to_seconds(start_time)
                end_time_seconds = functions.time_to_seconds(end_time)
                # 若当前仿真时间位于当前时段内，修改当前时段信号灯组的相位
                if start_time_seconds <= current_simuTime < end_time_seconds:
                    period_time = group_data['cycle_time']
                    phases = group_data['phases']
                    # 修改周期
                    current_signal_group.setPeriodTime(int(period_time))
                    for phase in phases:
                        phase_name = phase['phase_name']
                        phase_number = int(phase['phase_number'])
                        color_list = []  # 按照红灯、绿灯、黄灯、红灯顺序计算
                        color_list.append(Online.ColorInterval('红', int(phase['start_time'])))
                        color_list.append(Online.ColorInterval('绿', int(phase['green_time'])))
                        color_list.append(Online.ColorInterval('黄', 3))
                        if int(period_time - phase['start_time'] - phase['green_time'] - 3) > 0:
                            color_list.append(
                                Online.ColorInterval('红',
                                                     int(period_time - phase['start_time'] - phase['green_time'] - 3)))

                        # 当前灯组包含的相位序号
                        current_phase = None
                        for current_signal_group_phase in current_signal_group_phases_lst:
                            if phase_number == int(current_signal_group_phase.number()):
                                current_phase = current_signal_group_phase
                                break

                        # 若已存在该相位，修改相位灯色顺序，否则添加相位
                        if current_phase:
                            # 修改相位
                            current_phase.setColorList(color_list)
                        else:
                            signal_phase = netiface.createSignalPhase(current_signal_group, phase_name, color_list)
                            # 设置相位序号
                            signal_phase.setNumber(phase_number)
                        # 设置相位包含的信号灯
                        for lampId in phase["lamp_lst"]:
                            lamp = netiface.findSignalLamp(int(lampId))
                            if lamp:
                                '''
                                目前一个信号灯属于多个相位，相位间不交叉。因此如果要实际下发方案时，应按照仿真时间实时管理相位序号。
                                '''
                                lamp.setPhaseNumber(phase_number)
                            else:
                                print("FindError:未查找到信号灯:", lampId)

    # 双环信控方案下发测试
    def double_ring_signal_control_test(self, planNumber):
        """ 双环信控方案下发测试
        :param planNumber: 方案序号
        :return:
        """
        # 读取方案数据
        with open('./Data/Signal_Plan_Data_1109.json', 'r', encoding='utf-8') as json_file:
            signal_groups_dict = json.load(json_file)
        # 所有灯组的起始时间
        signal_groups_startTime_lst = []
        for group in signal_groups_dict.values():
            for startTime in group.keys():
                signal_groups_startTime_lst.append(functions.time_to_seconds(startTime))
        # 当前方案序号
        current_planNumber = planNumber % len(signal_groups_startTime_lst)
        print(signal_groups_startTime_lst[current_planNumber], ":双环信控方案更改。")
        self.double_ring_signal_control((signal_groups_startTime_lst[current_planNumber]))

    # 流量加载
    def traffic_loading(self):
        """ 流量加载
        :return:
        """
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()
        # 代表TESS NG的仿真子接口
        simuiface = iface.simuInterface()

        '''1.新建发车点'''
        # 创建两条新路段和一条连接段作为示例
        startPoint1 = QPointF(m2p(-300), m2p(-180))
        endPoint1 = QPointF(m2p(-50), m2p(-180))
        lPoint1 = [startPoint1, endPoint1]
        link1 = netiface.createLink(lPoint1, 3, "流量加载路段1")

        startPoint2 = QPointF(m2p(50), m2p(-180))
        endPoint2 = QPointF(m2p(300), m2p(-180))
        lPoint2 = [startPoint2, endPoint2]
        link2 = netiface.createLink(lPoint2, 3, "流量加载路段2")

        # 连接段车道连接列表
        lLaneObjects = []
        if link1 and link2:
            lFromLaneNumber = [1, 2, 3]
            lToLaneNumber = [1, 2, 3]
            connector = netiface.createConnector(link1.id(), link2.id(), lFromLaneNumber, lToLaneNumber,
                                                 "流量加载连接段", True)
            if connector:
                lLaneObjects = connector.laneObjects()
                for laneObj in lLaneObjects:
                    print("上游车道ID", laneObj.fromLane().id(), "下游车道ID", laneObj.toLane().id())

            # 创建车辆组成及指定车辆类型
            vehiType_proportion_lst = []
            # 车型组成：小客车0.3，大客车0.2，公交车0.1，货车0.4
            vehiType_proportion_lst.append(Online.VehiComposition(1, 0.3))
            vehiType_proportion_lst.append(Online.VehiComposition(2, 0.2))
            vehiType_proportion_lst.append(Online.VehiComposition(3, 0.1))
            vehiType_proportion_lst.append(Online.VehiComposition(4, 0.4))
            vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiType_proportion_lst)
            if vehiCompositionID != -1:
                print("车型组成创建成功，id为：", vehiCompositionID)
                # 新建发车点,车型组成ID为动态创建的，600秒发300辆车
                if link1:
                    dp = netiface.createDispatchPoint(link1)
                    if dp:
                        dp.addDispatchInterval(vehiCompositionID, 600, 300)

            '''2.动态发车'''
            # 创建两条新路段和一条连接段作为示例
            startPoint3 = QPointF(m2p(-300), m2p(-160))
            endPoint3 = QPointF(m2p(-50), m2p(-160))
            lPoint3 = [startPoint3, endPoint3]
            link3 = netiface.createLink(lPoint3, 3, "动态加载车辆段")

            startPoint4 = QPointF(m2p(50), m2p(-160))
            endPoint4 = QPointF(m2p(300), m2p(-160))
            lPoint4 = [startPoint4, endPoint4]
            link4 = netiface.createLink(lPoint4, 3, "动态加载车辆段")

            # 连接段车道连接列表
            lLaneObjects = []
            if link3 and link4:
                lFromLaneNumber = [1, 2, 3]
                lToLaneNumber = [1, 2, 3]
                connector = netiface.createConnector(link3.id(), link4.id(), lFromLaneNumber, lToLaneNumber,
                                                     "动态加载加载连接段", True)
                if connector:
                    lLaneObjects = connector.laneObjects()
                    for laneObj in lLaneObjects:
                        print("上游车道ID", laneObj.fromLane().id(), "下游车道ID", laneObj.toLane().id())

            # 在指定车道和位置动态加载车辆(示例：在0,1,2车道不同位置动态加载车辆)
            dvp_lane0 = Online.DynaVehiParam()
            dvp_lane1 = Online.DynaVehiParam()
            dvp_lane2 = Online.DynaVehiParam()
            dvp_lane0.vehiTypeCode = 1
            dvp_lane1.vehiTypeCode = 2
            dvp_lane2.vehiTypeCode = 3
            dvp_lane0.roadId = link3.id()
            dvp_lane1.roadId = link3.id()
            dvp_lane2.roadId = link4.id()
            dvp_lane0.laneNumber = 0
            dvp_lane1.laneNumber = 1
            dvp_lane2.laneNumber = 2
            dvp_lane0.dist = m2p(50)
            dvp_lane1.dist = m2p(100)
            dvp_lane2.dist = m2p(50)
            dvp_lane0.speed = 20
            dvp_lane0.speed = 30
            dvp_lane0.speed = 40
            dvp_lane0.color = "#FF0000"
            dvp_lane1.color = "#008000"
            dvp_lane2.color = "#0000FF"
            vehi_lane0 = simuiface.createGVehicle(dvp_lane0)
            vehi_lane1 = simuiface.createGVehicle(dvp_lane1)
            vehi_lane2 = simuiface.createGVehicle(dvp_lane2)

    # 路径加载
    def flow_loading(self):
        """ 路径加载
        :return:
        """
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()
        # 代表TESS NG的仿真子接口
        simuiface = iface.simuInterface()

        # 以标准四岔路口为例 (L3-C2-L10)
        link3 = netiface.findLink(3)
        link10 = netiface.findLink(10)
        link6 = netiface.findLink(6)
        link7 = netiface.findLink(7)
        link8 = netiface.findLink(8)
        # 新建发车点
        if link3:
            dp = netiface.createDispatchPoint(link3)
            if dp:
                dp.addDispatchInterval(1, 1800, 900)
        # 创建决策点
        decisionPoint = netiface.createDecisionPoint(link3, m2p(30))
        # 创建路径(左，直，右)
        decisionRouting1 = tessngIFace().netInterface().createDeciRouting(decisionPoint, [link3, link10, link6])
        decisionRouting2 = tessngIFace().netInterface().createDeciRouting(decisionPoint, [link3, link10, link8])
        decisionRouting3 = tessngIFace().netInterface().createDeciRouting(decisionPoint, [link3, link10, link7])

        # 分配左、直、右流量比
        flowRatio_left = _RoutingFLowRatio()
        flowRatio_left.RoutingFLowRatioID = 1
        flowRatio_left.routingID = decisionRouting1.id()
        flowRatio_left.startDateTime = 0
        flowRatio_left.endDateTime = 999999
        flowRatio_left.ratio = 2.0
        flowRatio_straight = _RoutingFLowRatio()
        flowRatio_straight.RoutingFLowRatioID = 2
        flowRatio_straight.routingID = decisionRouting2.id()
        flowRatio_straight.startDateTime = 0
        flowRatio_straight.endDateTime = 999999
        flowRatio_straight.ratio = 3.0
        flowRatio_right = _RoutingFLowRatio()
        flowRatio_right.RoutingFLowRatioID = 3
        flowRatio_right.routingID = decisionRouting3.id()
        flowRatio_right.startDateTime = 0
        flowRatio_right.endDateTime = 999999
        flowRatio_right.ratio = 1.0

        # 决策点数据
        decisionPointData = _DecisionPoint()
        decisionPointData.deciPointID = decisionPoint.id()
        decisionPointData.deciPointName = decisionPoint.name()
        decisionPointPos = QPointF()
        if decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos):
            decisionPointData.X = decisionPointPos.x()
            decisionPointData.Y = decisionPointPos.y()
            decisionPointData.Z = decisionPoint.link().z()
        # 更新决策点及其各路径不同时间段流量比
        updated_decision_point = netiface.updateDecipointPoint(
            decisionPointData, [flowRatio_left, flowRatio_straight, flowRatio_right]
        )
        if updated_decision_point:
            print("决策点创建成功。")
            # 删除右转路径
            if (netiface.removeDeciRouting(decisionPoint, decisionRouting3)):
                print("删除右转路径成功。")

    # 路径断面流量加载
    def flow_loading_section(self, current_time):
        """ 路径断面流量加载
        :param current_time: 当前仿真时间
        :return:
        """
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()
        # 代表TESS NG的仿真子接口
        simuiface = iface.simuInterface()
        # 读取方案数据
        with open('./Data/flow_ratio_quarter.json', 'r', encoding='utf-8') as json_file:
            flow_ratio_quarter_dict = json.load(json_file)
        for linkId, quarter_ratios in flow_ratio_quarter_dict.items():
            decisionPoint = None
            # 查找到决策点
            decisionPoints_lst = netiface.decisionPoints()
            for _decisionPoint in decisionPoints_lst:
                if _decisionPoint.link().id() == int(linkId):
                    decisionPoint = _decisionPoint
                    break
            if decisionPoint:
                quarter_startTime_lst = list(quarter_ratios.keys())
                for index, quarter_ratio in enumerate(quarter_ratios.values()):
                    quarter_time_seconds = functions.time_to_seconds(quarter_startTime_lst[index])
                    if index != len(quarter_startTime_lst) - 1:
                        quarter_time_seconds_next = functions.time_to_seconds(quarter_startTime_lst[index + 1])
                    else:
                        quarter_time_seconds_next = quarter_time_seconds + 1
                    if quarter_time_seconds <= current_time < quarter_time_seconds_next:
                        # 获取决策点现有路径
                        decision_routings_lst = decisionPoint.routings()
                        if (len(decision_routings_lst) == 3):
                            # 分配左、直、右流量比
                            flowRatio_left = _RoutingFLowRatio()
                            flowRatio_left.RoutingFLowRatioID = decision_routings_lst[0].id()
                            flowRatio_left.routingID = decision_routings_lst[0].id()
                            flowRatio_left.startDateTime = 0
                            flowRatio_left.endDateTime = 999999
                            flowRatio_left.ratio = quarter_ratio["left"]
                            flowRatio_straight = _RoutingFLowRatio()
                            flowRatio_straight.RoutingFLowRatioID = decision_routings_lst[1].id()
                            flowRatio_straight.routingID = decision_routings_lst[1].id()
                            flowRatio_straight.startDateTime = 0
                            flowRatio_straight.endDateTime = 999999
                            flowRatio_straight.ratio = quarter_ratio["straight"]
                            flowRatio_right = _RoutingFLowRatio()
                            flowRatio_right.RoutingFLowRatioID = decision_routings_lst[2].id()
                            flowRatio_right.routingID = decision_routings_lst[2].id()
                            flowRatio_right.startDateTime = 0
                            flowRatio_right.endDateTime = 999999
                            flowRatio_right.ratio = quarter_ratio["right"]
                            # 决策点数据
                            decisionPointData = _DecisionPoint()
                            decisionPointData.deciPointID = decisionPoint.id()
                            decisionPointData.deciPointName = decisionPoint.name()
                            decisionPointPos = QPointF()
                            if decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos):
                                decisionPointData.X = decisionPointPos.x()
                                decisionPointData.Y = decisionPointPos.y()
                                decisionPointData.Z = decisionPoint.link().z()
                            # 更新决策点及其各路径不同时间段流量比
                            updated_decision_point = netiface.updateDecipointPoint(
                                decisionPointData, [flowRatio_left, flowRatio_straight, flowRatio_right]
                            )
                            if updated_decision_point:
                                print("{}流量更新成功。".format(quarter_startTime_lst[index]))
                        else:
                            print("DecisionRoutingsError:决策点{}需要包含左、直、右三条路径。".format(decisionPoint.id()))
            else:
                # 需路段存在决策点，才可更新，因此可用flow_loading函数新建决策点
                print("FindError:ID为{}的路段不存在决策点".format(linkId))

    # 动作控制
    def action_control(self, planNumber):
        """ 动作控制
        :param planNumber: 方案序号
        :return:
        """
        # 以动作控制案例-机动车交叉口路网的L5路段为例
        '''1. 修改发车流量信息，删除发车点'''
        # 修改发车流量信息需在MySimulator中的calcDynaDispatchParameters函数,删除发车点位于afterOneStep函数中
        '''2. 修改决策路径的属性，删除决策路径'''
        # 见路径加载/路径管理模块
        '''3. 修改减速区，施工区，事故区信息；删除减速区，施工区，事故区'''
        # 减速区见MySimulator中的ref_reCalcdesirSpeed函数
        '''4. 车辆位置移动'''
        # 见afterOneStep函数
        '''5. 修改车辆速度'''
        # 同3减速区
        '''6. 修改车辆路径'''
        # 以L1路段上的路径为例，见afterOneStep
        '''7. 强制车辆不变道'''
        # 见MySimulator中的reCalcDismissChangeLane函数
        '''8. 强制车辆变道'''
        # MySimulator中的reCalcToLeftFreely和reCalcToRightFreely,return true即可
        '''9. 强制车辆闯红灯'''
        # 见MySimulator的ref_reSetSpeed函数
        '''10. 强制车辆停车'''
        # 见MySimulator的ref_reSetSpeed函数
        '''11. 强制清除车辆（车辆消失）'''
        # 以L5路段上的路径为例，见afterStep
        '''12. 修改车辆航向角'''
        # 以L5路段上的路径为例，见afterStep
        '''13. 修改车辆速度，加速度'''
        # 同5，修改加速度函数为MySimulator的ref_reSetAcce，用法与设置速度相同
        '''14. 车道关闭，恢复'''
        # 几种方法都可以实现：1.设置事件区。2.MySimulator中的自由变道，以L5路段50-100m处最右侧封闭30秒为例

        functions.action_control_methodNumber = planNumber
        print(functions.action_control_methodNumber)

    # 创建施工区和删除施工区示例,施工区和事故区的删除有两种方式，duration结束后自动删除以及主动删除(removeRoadWorkZone)，此处初始化前者
    def createworkZone(self):
        """ 创建施工区
        :param :
        :return:
        """
        # 创建施工区
        workZone = Online.DynaRoadWorkZoneParam()
        # 道路ID
        workZone.roadId = int(5)
        # 施工区名称
        workZone.name = "施工区，限速40,持续20秒"
        # 位置，距离路段或连接段起点距离，单位米
        workZone.location = 50
        # 施工区长度，单位米
        workZone.length = 50
        # 车辆经过施工区的最大车速，单位千米/小时
        workZone.limitSpeed = 40
        # 施工区施工时长，单位秒
        workZone.duration = 20
        # 施工区起始车道
        workZone.mlFromLaneNumber = [0]
        # 创建施工区
        zone = tessngIFace().netInterface().createRoadWorkZone(workZone)

    # 管控手段控制
    def control_Measures(self, method_number):
        """ 管控手段控制
        :param method_number:调用的方法序号
        :return:
        """
        # TESSNG 顶层接口
        iface = tessngIFace()
        # TESSNG 仿真子接口
        simuiface = iface.simuInterface()
        # TESSNG 路网子接口
        netiface = iface.netInterface()
        '''1. 修改信号灯灯色'''
        # 见MySimulator的afterOneStep函数，L5路段信号灯第10秒红灯变绿灯，持续20秒。
        '''2. 修改信号灯组方案'''
        # 见双环管控方案下发。
        '''3. 修改相位绿灯时间长度'''
        # 除双环管控方案下所包含方法外，还有相位类自带的修改方法,以L12路段相位直行信号灯相位为例（ID为7），由红90绿32黄3红25改为红10绿110黄3红28
        if method_number == 3:
            signalPhase_L12_7 = netiface.findSignalPhase(7)
            color_list = []  # 按照红灯、绿灯、黄灯、红灯顺序计算
            color_list.append(Online.ColorInterval('红', 10))
            color_list.append(Online.ColorInterval('绿', 110))
            color_list.append(Online.ColorInterval('黄', 3))
            color_list.append(Online.ColorInterval('红', 28))
            signalPhase_L12_7.setColorList(color_list)
        '''5. 修改link, connector 限速'''
        # 以L5路段最高限速由80调整至20，连接段无法修改限速。
        if method_number == 5:
            link5 = netiface.findLink(5)
            link5.setLimitSpeed(20)

    # 换道模型
    def lane_changing_model(self, method_number):
        """ 换道模型
        :param method_number：调用的方法序号
        :return:
        """
        # TESSNG 顶层接口
        iface = tessngIFace()
        # TESSNG 仿真子接口
        simuiface = iface.simuInterface()
        # TESSNG 路网子接口
        netiface = iface.netInterface()
        '''1. 选择变道类型：强制变道，压迫变道，自由变道'''
        '''2. 设置强制变道，压迫变道参数'''
        # 目前仅有MySimulator中的ref_reSetChangeLaneFreelyParam函数设置安全操作时间、安全变道(完成变道前半段)后距前车距离、目标车道后车影响系数
        # 以L5路段两侧车道往中间变道为例

    # 流程控制
    def process_control(self, method_number):
        """ 流程控制
              :param method_number：调用的方法序号
              :return:
              """
        # TESSNG 顶层接口
        iface = tessngIFace()
        # TESSNG 仿真子接口
        simuiface = iface.simuInterface()
        # TESSNG 路网子接口
        netiface = iface.netInterface()
        '''1. 启动、暂停、恢复、停止仿真'''
        if method_number == 1:
            simuiface.startSimu()
        elif method_number == 2:
            simuiface.pauseSimu()
        elif method_number == 3:
            simuiface.stopSimu()
        elif method_number == 4:
            simuiface.pauseSimuOrNot()
        '''8. 获取运动信息'''
        # 8.1 获取路网在途车辆，见MySimulator中afterOneStep的simuiface.allVehiStarted()
        # 8.2 根据路段|车道获取车辆list
        if method_number == 8.2:
            vehiOnRoad5_lst = simuiface.vehisInLink(5)
            vehiOnLane20_lst = simuiface.vehisInLane(20)
            print("L5路段车辆id：")
            for vehi in vehiOnRoad5_lst:
                print(vehi.id())
            print("lane20车道车辆id：")
            for vehi in vehiOnLane20_lst:
                print(vehi.id())
        # 8.3 根据车辆id获取具体的车辆信息,以id为300001的车辆为例
        if method_number == 8.3:
            vehi_300001 = simuiface.getVehicle(300001)
            print("300001车辆的具体信息：")
            print("所在路段:", vehi_300001.roadId())
            print("所在车道:", vehi_300001.lane().id())
            print("当前车速:", vehi_300001.currSpeed())
            print("当前加速度:", vehi_300001.acce())
            print("当前角度:", vehi_300001.angle())
            print("当前位置:", vehi_300001.pos())
            print("其它:", "......")
        '''10. 设置仿真精度'''
        if method_number == 10:
            simuiface.setSimuAccuracy(10)
        '''11. 设置仿真开始结束时间'''
        # 可以设置仿真时长，无法设置仿真开始的时间，不过可以由定时器定时启动和结束仿真实现设置仿真开始结束时间，此处仅展示二次开发的设置仿真时长方法
        if method_number == 11:
            simuiface.setSimuIntervalScheming(30)
        '''12. 设置仿真加速比'''
        if method_number == 12:
            simuiface.setAcceMultiples(10)

    # 强制车辆不变道可用
    def judge_vehicle_laneChange_direction(self, vehi):
        '''
        判断车辆是左变道还是右变道。
        :param vehi: 运行车辆
        :return:
        '''
        lane = vehi.lane()
        vehi_currPos = vehi.pos()
        vehi_currDistToStart = lane.distToStartPoint(vehi_currPos)
        lane_centerBreakPoints = lane.centerBreakPoints()
        vehi_segmentIndex = -1
        # 获取车辆所在的道路分段号
        for index, centerBreakPoint in enumerate(lane_centerBreakPoints):
            lane_centerBreakPoints_distToStart = lane.distToStartPoint(centerBreakPoint)
            if vehi_currDistToStart < lane_centerBreakPoints_distToStart:
                vehi_segmentIndex = index
                break
        if 0 < vehi_segmentIndex < len(lane_centerBreakPoints):
            start_breakPoint = lane_centerBreakPoints[vehi_segmentIndex - 1]
            end_breakPoint = lane_centerBreakPoints[vehi_segmentIndex]
            # 以点积判断车辆处于中心线左侧还是右侧
            vehi_direction = functions.car_position_road(start_breakPoint, end_breakPoint, vehi_currPos)
            # 判断车头角度偏度
            breakLane_angle = functions.calculate_angle(start_breakPoint, end_breakPoint)
            # 若车辆处于中心线右侧且车头右偏，则判定为右变道意图
            if vehi_direction == "right" and vehi.angle() > breakLane_angle:
                return "right"
            # 若车辆处于中心线左侧且车头左偏，则判定为左变道意图
            elif vehi_direction == "left" and vehi.angle() < breakLane_angle:
                return "left"
            else:
                return "noChange"
        else:
            print("FindError:can't find the segment,relevant info:", vehi_segmentIndex, vehi_currDistToStart,
                  vehi_currPos)

    # 创建交通事件：施工区，事故区，限速区
    def create_traffic_incident(self, netiface):
        """
        创建正反两条非常长的路段（可以是圆弧）， 上边新建停车位，施工区，事故区，借到施工区
        Args:
            netiface:

        Returns:

        """
        # 第一条路段,传入
        startPoint = QPointF(-900, 900+1500)
        midPoint1 = QPointF(900, 900+1500)
        midPoint2 = QPointF(900, 700+1500)
        midPoint3 = QPointF(-900, 700+1500)
        midPoint4 = QPointF(-900, 500+1500)
        midPoint5 = QPointF(900, 500+1500)
        midPoint6 = QPointF(900, 300+1500)
        midPoint7 = QPointF(-900, 300+1500)
        midPoint8 = QPointF(-900, 100+1500)
        midPoint9 = QPointF(900, 100+1500)
        midPoint10 = QPointF(900, -100+1500)
        midPoint11 = QPointF(-900, -100+1500)
        midPoint12 = QPointF(-900, -300+1500)
        midPoint13 = QPointF(900, -300+1500)
        midPoint14 = QPointF(900, -500+1500)
        midPoint15 = QPointF(-900, -500+1500)
        midPoint16 = QPointF(-900, -700+1500)
        midPoint17 = QPointF(900, -700+1500)
        midPoint18 = QPointF(900, -900+1500)
        midPoint19 = QPointF(-900, -900+1500)
        lPoint = [startPoint, midPoint1,midPoint2,midPoint3,midPoint4,midPoint5,midPoint6,midPoint7,midPoint8
                  ,midPoint9,midPoint10,midPoint11,midPoint12,midPoint13,midPoint14,midPoint15,midPoint16,midPoint17,midPoint18
                  ,midPoint19]
        lPoint.reverse()
        upstream = netiface.createLink(lPoint, 4, "上行", UnitOfMeasure.Metric)
        upstream.setLimitSpeed(120, UnitOfMeasure.Metric)

        startPoint1 = QPointF(-880, 920+1500)
        midPoint20 = QPointF(920, 920+1500)
        midPoint21 = QPointF(920, 680+1500)
        midPoint22 = QPointF(-880, 680+1500)
        midPoint23 = QPointF(-880, 520+1500)
        midPoint24 = QPointF(920, 520+1500)
        midPoint25 = QPointF(920, 280+1500)
        midPoint26 = QPointF(-880, 280+1500)
        midPoint27 = QPointF(-880, 120+1500)
        midPoint28= QPointF(920, 120+1500)
        midPoint29 = QPointF(920, -120+1500)
        midPoint30 = QPointF(-880, -120+1500)
        midPoint31 = QPointF(-880, -280+1500)
        midPoint32 = QPointF(920, -280+1500)
        midPoint33 = QPointF(920, -520+1500)
        midPoint34 = QPointF(-880, -520+1500)
        midPoint35 = QPointF(-880, -680+1500)
        midPoint36 = QPointF(920, -680+1500)
        midPoint37 = QPointF(920, -920+1500)
        midPoint38 = QPointF(-880, -920+1500)
        lPoint = [startPoint1, midPoint20,midPoint21,midPoint22,midPoint23,midPoint24,midPoint25,midPoint26,midPoint27
                  ,midPoint28,midPoint29,midPoint30,midPoint31,midPoint32,midPoint33,midPoint34,midPoint35,midPoint36,midPoint37
                  ,midPoint38]

        downstream = netiface.createLink(lPoint,4, "下行", UnitOfMeasure.Metric)
        downstream.setLimitSpeed(120, UnitOfMeasure.Metric)



        # 创建发车点
        upstream_dispatchPoint = netiface.createDispatchPoint(upstream, "上行发车")
        downstream_dispatchPoint = netiface.createDispatchPoint(downstream, "上行发车")

        # 车型组成
        # 创建车辆组成及指定车辆类型
        vehiType_proportion_lst = []
        # 车型组成：小客车0.3，大客车0.2，货车0.4
        vehiType_proportion_lst.append(Online.VehiComposition(1, 0.8))
        vehiType_proportion_lst.append(Online.VehiComposition(2, 0.1))
        vehiType_proportion_lst.append(Online.VehiComposition(4, 0.1))
        vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiType_proportion_lst)
        # 设置发车间隔，含车型组成、时间间隔、发车数
        upstream_dispatchPoint.addDispatchInterval(vehiCompositionID, 3600, 3600)
        downstream_dispatchPoint.addDispatchInterval(vehiCompositionID, 3600, 3600)


        # 创建事故区
        accidentZoneParam = Online.DynaAccidentZoneParam()
        # 道路ID
        accidentZoneParam.roadId = upstream.id()
        # 事故区名称
        accidentZoneParam.name = "最左侧车道发生事故"
        # 位置，距离路段或连接段起点距离，单位米
        accidentZoneParam.location = 700
        # 事故区长度，单位米
        accidentZoneParam.length = 50
        # 事故区起始车道序号列表
        accidentZoneParam.mlFromLaneNumber = [2]
        accidentZoneParam.startTime= 0
        accidentZoneParam.duration = 500
        accidentZoneParam.needStayed = True
        accidentZoneParam.limitSpeed = 55
        accidentZoneParam.controlLength = 100
        # 创建事故区
        accidentZone = netiface.createAccidentZone(accidentZoneParam)
        # 创建事故区时间段信息
        accidentZoneIntervalParam = Online.DynaAccidentZoneIntervalParam()
        accidentZoneIntervalParam.accidentZoneId = accidentZone.id()
        accidentZoneIntervalParam.startTime = 90
        accidentZoneIntervalParam.endTime = 120
        accidentZoneIntervalParam.length = 50
        accidentZoneIntervalParam.location = 200
        accidentZoneIntervalParam.limitedSpeed = 10
        accidentZoneIntervalParam.controlLength = 100
        accidentZoneIntervalParam.mlFromLaneNumber = [0, 1, 3]
        accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam)

        # 限速区
        print("创建限速区")
        type1 = Online.DynaReduceSpeedVehiTypeParam()
        type1.vehicleTypeCode = 1
        type1.avgSpeed = 10
        type1.speedSD = 5
        type2 = Online.DynaReduceSpeedVehiTypeParam()
        type2.vehicleTypeCode = 2
        type2.avgSpeed = 20
        type2.speedSD = 0

        reduceSpeedIntervalParam = Online.DynaReduceSpeedIntervalParam()
        reduceSpeedIntervalParam.startTime = 0
        reduceSpeedIntervalParam.endTime = 50
        reduceSpeedIntervalParam.mlReduceSpeedVehicleTypeParam = [type1, type2]
        #
        #
        param1 = Online.DynaReduceSpeedAreaParam()
        param1.name="限速区"
        param1.location = 2500
        param1.areaLength =100
        param1.roadId=upstream.id()
        param1.laneNumber = 0
        param1.toLaneNumber = -1
        param1.mlReduceSpeedIntervalParam = [reduceSpeedIntervalParam]
        reduceSpeedArea = netiface.createReduceSpeedArea(param1)
        print(reduceSpeedArea)

        param2 = param1
        param2.linkId= downstream.id()
        reduceSpeedArea1 = netiface.createReduceSpeedArea(param2)
        netiface.removeReduceSpeedArea(reduceSpeedArea1)


        # 收费站
        # 创建停车分布
        time_dis_list = [
            self._create_mtc_time_detail(3, 1),
            self._create_mtc_time_detail(5, 1),
        ]
        speed_dis_list: list = [
            self._create_etc_speed_detail(15, 1),
            self._create_etc_speed_detail(20, 1),
        ]
        vehicle_type_id: int = 1
        toll_parking_time = self._create_toll_parking_time(vehicle_type_id, time_dis_list, speed_dis_list)
        toll_parking_time_dis = self._create_toll_parking_time_dis(netiface,[toll_parking_time], "新建停车分布")
        print(f"创建了停车分布，id={toll_parking_time_dis.id}")

        # 创建收费车道
        toll_lanes = []
        for lane_number in range(len(upstream.lanes())):
            # 创建收费区域
            toll_point = self._create_toll_point(40, toll_type=1)
            link_id: int = upstream.id()
            location: float = 5500
            length: float = 40
            toll_lane = self._create_toll_lane(netiface, link_id, location, length, lane_number, 0, 3600, [toll_point])
            toll_lanes.append(toll_lane)
            print(f"在序号为{lane_number}的车道上创建了收费车道，id={toll_lane.id()}")

        # 创建收费路径决策点
        link_id: int = upstream.id()
        location: float = 5000
        toll_deci_point = self._create_toll_decision_point(netiface,link_id, location)
        print(f"创建了收费路径决策点，id={toll_deci_point.id()}")

        # 创建收费路径
        tollDisInfoList = []
        for toll_lane in toll_lanes:
            toll_routing = self._create_toll_routing(netiface, toll_deci_point, toll_lane)
            print(f"在收费路径决策点{toll_deci_point.id()}创建了收费路径，id={toll_routing.id()}")
            # 更新收费决策点的当前路径的车道分配信息
            tollDisInfo = Online.TollStation.DynaTollDisInfo()
            tollDisInfo.pIRouting = None # pIRouting 为空时对未有路径的车辆信息进行分配, 如果有路径则这里就是对应的静态路径
            # 静态路径下车辆分布到收费车道信息;
            routingDisTollInfo = Online.TollStation.DynaRoutingDisTollInfo()
            print(f" routingDisTollInfo={dir(routingDisTollInfo)}")
            routingDisTollInfo.startTime = 0
            routingDisTollInfo.endTime = 3600
            # etc 收费信息
            ectTollInfo = Online.TollStation.DynaEtcTollInfo()
            ectTollInfo.etcRatio = 0.2
            ectTollInfo.tollLaneID = toll_lane.id()
            ectTollInfo.tollRoutingID = toll_routing.id()
            routingDisTollInfo.ectTollInfoList.append(ectTollInfo)
            # 车辆类型
            vehicleTollDisInfo = Online.TollStation.DynaVehicleTollDisInfo()
            vehicleTollDisInfo.vehicleType = 3
            # 车辆分配详情
            vehicleTollDisDetail = Online.TollStation.DynaVehicleTollDisDetail()
            vehicleTollDisDetail.prop = 10
            vehicleTollDisDetail.tollLaneID = toll_lane.id()
            vehicleTollDisDetail.tollRoutingID = toll_routing.id()
            vehicleTollDisInfo.list.append(vehicleTollDisDetail)
            # 车辆分布信息
            routingDisTollInfo.vehicleDisInfoList.append(vehicleTollDisInfo)

            # 组合收费车道路径车道分布
            tollDisInfo.disTollInfoList.append(routingDisTollInfo)
            tollDisInfoList.append(tollDisInfo)

        # 更新到决策点
        toll_deci_point.updateTollDisInfoList(tollDisInfoList)
        # 查看所有收费停车分布
        toll_parking_time_dis = netiface.tollParkingTimeDis()
        print(f"收费停车分布的数量是：{len(toll_parking_time_dis)}")
        # 查看所有收费车道
        toll_lanes = netiface.tollLanes()
        print(f"收费车道的数量是：{len(toll_lanes)}")
        # 查看所有收费路径决策点
        toll_deci_points = netiface.tollDecisionPoints()
        print(f"收费路径决策点的数量是：{len(toll_deci_points)}")



        # # 路边停车场
        self._create_parking(netiface,downstream)

        self._create_roadworkzone(netiface, upstream, downstream)

    # 新建路段，连接段，发车点，决策点，决策路径
    def create_junction(self,netiface):
        # print("将仿真的像素比设置为2， 为了人为对比UnitOfMeasure.Metric 参数的作用，其实二次开发建模不需要特意像素比，默认是1")
        print("step1：设置仿真比例尺为1")
        # netiface.setPixelRatio(2)
        netiface.buildNetGrid(10,UnitOfMeasure.Metric)

        print("step2：创建路口各个进口道和出口道路段")
        # create link, connector, 等所有静态元素
        print("    create link  西进口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QPointF(-300, 6)
        endPoint = QPointF(-25, 6)
        lPoint = [startPoint, endPoint]
        w_approach = netiface.createLink(lPoint, 3, "西进口",  True, UnitOfMeasure.Metric)
        # 车道列表
        lanes = w_approach.lanes()
        # 打印该路段所有车道ID列表
        print("    西进口车道ID列表：", [lane.id() for lane in lanes])
        print("    设置限速30km/h")
        w_approach.setLimitSpeed(30, UnitOfMeasure.Metric)
        # w_approach.setLaneTypes(['公交专用道', '机动车道', '机动车道'])
        # create link, connector, 等所有静态元素
        print("    create link  西出口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QPointF(-25, -6)
        endPoint = QPointF(-300, -6)
        lPoint = [startPoint, endPoint]
        w_outgoing = netiface.createLink(lPoint, 3, "西出口", True, UnitOfMeasure.Metric)
        print("    设置限速30km/h")
        w_outgoing.setLimitSpeed(50, UnitOfMeasure.Metric)

        print("    create link  createLink3D 东进口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QVector3D(300, -6, 0)
        endPoint = QVector3D(25, -6, 0)
        # endPoint = QVector3D(25, -6, 5)
        lPoint = [startPoint, endPoint]
        e_approach = netiface.createLink3D(lPoint, 3, linkName="东进口creatlink3d", bAddToScene=True,
                                           unit=UnitOfMeasure.Metric)
        # 车道列表
        e_approach.setLimitSpeed(40, UnitOfMeasure.Metric)
        # create link, connector, 等所有静态元素
        print("     create link  东出口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QPointF(25, 6)
        endPoint = QPointF(300, 6)
        lPoint = [startPoint, endPoint]
        e_outgoing = netiface.createLink(lPoint, 3, "东出口", True, UnitOfMeasure.Metric)
        e_outgoing.setLimitSpeed(50, UnitOfMeasure.Metric)
        print("     create link creatlink3dWithLaneWidth  南进口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QVector3D(6, 300, 0)
        endPoint = QVector3D(6, 25, 0)
        lPoint = [startPoint, endPoint]
        s_approach = netiface.createLink3DWithLaneWidth(lPoint, [3.5, 3.0, 3.0],
                                                        linkName="南进口creatlink3dWithLaneWidth", bAddToScene=True,
                                                        unit=UnitOfMeasure.Metric)
        # 车道列表
        s_approach.setLimitSpeed(40, UnitOfMeasure.Metric)
        # create link, connector, 等所有静态元素
        print("     create link  南出口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QPointF(-6, 25)
        endPoint = QPointF(-6, 300)
        lPoint = [startPoint, endPoint]
        s_outgoing = netiface.createLink(lPoint, 3, "南出口", UnitOfMeasure.Metric)
        s_outgoing.setLimitSpeed(50, UnitOfMeasure.Metric)

        print("     create link createLink3DWithLanePoints  北进口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QVector3D(-6, -300, 0)
        endPoint = QVector3D(-6, -25, 0)
        lPoint = [startPoint, endPoint]
        lane3_left = [QVector3D(-3, -300, 0), QVector3D(-3, -25, 0)]
        lane3_mid = [QVector3D(-1.5, -300, 0), QVector3D(-1.5, -25, 0)]
        lane3_right = [QVector3D(0, -300, 0), QVector3D(0, -25, 0)]

        lane2_left = [QVector3D(-6, -300, 0), QVector3D(-6, -25, 0)]
        lane2_mid = [QVector3D(-4.5, -300, 0), QVector3D(-4.5, -25, 0)]
        lane2_right = [QVector3D(-3, -300, 0), QVector3D(-3, -25, 0)]

        lane1_left = [QVector3D(-9, -300, 0), QVector3D(-9, -25, 0)]
        lane1_mid = [QVector3D(-7.5, -300, 0), QVector3D(-7.5, -25, 0)]
        lane1_right = [QVector3D(-6, -300, 0), QVector3D(-6, -25, 0)]

        # n_approach = netiface.createLink3DWithLanePoints(lPoint, [{'left':lane1_left,'center':lane1_mid,'right':lane1_right},
        #                                                           {'left':lane2_left,'center':lane2_mid,'right':lane2_right},
        #                                                           {'left':lane3_left,'center':lane3_mid,'right':lane3_right}],
        #                                                  linkName="北进口createLink3DWithLanePoints", bAddToScene=True, unit =UnitOfMeasure.Metric)

        n_approach = netiface.createLink3D(lPoint, 3, linkName="北进口createLink3DWithLanePoints", bAddToScene=True,
                                           unit=UnitOfMeasure.Metric)
        # 车道列表
        n_approach.setLimitSpeed(40, UnitOfMeasure.Metric)
        # 在当前路段创建发车点
        dp = netiface.createDispatchPoint(n_approach)
        if dp != None:
            # 设置发车间隔，含车型组成、时间间隔、发车数
            dp.addDispatchInterval(1, 300, 100)
        # create link, connector, 等所有静态元素
        print("     create link  北出口三车道， 米制单位...")
        # 第一条路段,传入
        startPoint = QPointF(6, -25)
        endPoint = QPointF(6, -300)
        lPoint = [startPoint, endPoint]
        n_outgoing = netiface.createLink(lPoint, 3, "北出口", UnitOfMeasure.Metric)
        n_outgoing.setLimitSpeed(50, UnitOfMeasure.Metric)


        print("step3: 创建连接段")
        print("     create connector createConnector & createConnector3DWithPoints 米制单位...")
        # 创建第一条连接段
        lFromLaneNumber = [2]
        lToLaneNumber = [2]
        w_e_straight_connector = netiface.createConnector(w_approach.id(), e_outgoing.id(), lFromLaneNumber,
                                                          lToLaneNumber, "东西直行", True)
        w_s_right_connector = netiface.createConnector(w_approach.id(), s_outgoing.id(), [1], [1], "西右转", True)
        w_n_left_connector = netiface.createConnector(w_approach.id(), n_outgoing.id(), [3], [3], "西左转", True)

        e_w_straight_connector = netiface.createConnector(e_approach.id(), w_outgoing.id(), lFromLaneNumber,
                                                          lToLaneNumber, "东西直行", True)
        e_n_right_connector = netiface.createConnector(e_approach.id(), n_outgoing.id(), [1], [1], "东右转", True)
        e_s_left_connector = netiface.createConnector(e_approach.id(), s_outgoing.id(), [3], [3], "东左转", True)

        s_n_straight_connector = netiface.createConnector(s_approach.id(), n_outgoing.id(), [2], [2], "南北直行", True)
        s_e_right_connector = netiface.createConnector(s_approach.id(), e_outgoing.id(), [1], [1], "南右转", True)
        s_w_left_connector = netiface.createConnector(s_approach.id(), w_outgoing.id(), [3], [3], "南左转", True)

        n_w_right_connector = netiface.createConnector(n_approach.id(), w_outgoing.id(), [1], [1], "北右转", True)
        n_e_left_connector = netiface.createConnector(n_approach.id(), e_outgoing.id(), [3], [3], "北左转", True)
        lane_left = [QVector3D(-6, -25, 5), QVector3D(-6, -15, 3.5), QVector3D(-6, -5, 2),
                     QVector3D(-6, 5, 0.5), QVector3D(-6, 15, 0), QVector3D(-6, 25, 0)]
        lane_mid = [QVector3D(-4.5, -25, 5), QVector3D(-4.5, -15, 3.5), QVector3D(-4.5, -5, 2),
                    QVector3D(-4.5, 5, 0.5), QVector3D(-4.5, 15, 0), QVector3D(-4.5, 25, 0)]
        lane_right = [QVector3D(-3, -25, 5), QVector3D(-3, -15, 3.5), QVector3D(-3, -5, 2),
                      QVector3D(-3, 5, 0.5), QVector3D(-3, 15, 0), QVector3D(-3, 25, 0)]
        laneConnectorWithPoints = [{'left': lane_left, 'center': lane_mid, 'right': lane_right}]

        # n_s_straight_connector = netiface.createConnector3DWithPoints(n_approach.id(), s_outgoing.id(), [2], [2],
        #                                                               laneConnectorWithPoints,"南北直行", True, UnitOfMeasure.Metric)

        n_s_straight_connector = netiface.createConnector(n_approach.id(), s_outgoing.id(), [2], [2], "南北直行", True)


        print("step4: 创建路段发车点")
        # 在当前路段创建发车点
        w_dispatchPoint = netiface.createDispatchPoint(w_approach, "西进口发车")
        # 车型组成
        # 创建车辆组成及指定车辆类型
        vehiType_proportion_lst = []
        # 车型组成：小客车0.3，大客车0.2，公交车0.1，货车0.4
        vehiType_proportion_lst.append(Online.VehiComposition(1, 0.3))
        vehiType_proportion_lst.append(Online.VehiComposition(2, 0.2))
        vehiType_proportion_lst.append(Online.VehiComposition(3, 0.1))
        vehiType_proportion_lst.append(Online.VehiComposition(4, 0.4))
        vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiType_proportion_lst)

        if w_dispatchPoint != None:
            # 设置发车间隔，含车型组成、时间间隔、发车数
            w_dispatchPoint.addDispatchInterval(1, 200, 28)
            w_dispatchPoint.addDispatchInterval(vehiCompositionID, 500, 100)
            w_dispatchPoint.setDynaModified(True) # 可在仿真过程中修改发车点信息，比如仿真500秒后，修改发车点信息

        # 在当前路段创建发车点
        dp = netiface.createDispatchPoint(e_approach)
        if dp != None:
            # 设置发车间隔，含车型组成、时间间隔、发车数
            dp.addDispatchInterval(1, 300, 100)

        # 在当前路段创建发车点
        dp = netiface.createDispatchPoint(s_approach)
        if dp != None:
            # 设置发车间隔，含车型组成、时间间隔、发车数
            dp.addDispatchInterval(1, 300, 100)

        print("step5: 创建路径决策点和决策路径")
        print("     创建静态路径决策点")
        w_approach_decisionPoint = netiface.createDecisionPoint(w_approach, 50, "w_approach_decisionPoint", UnitOfMeasure.Metric)
        e_approach_decisionPoint = netiface.createDecisionPoint(e_approach, 50, "e_approach_decisionPoint",
                                                                UnitOfMeasure.Metric)
        s_approach_decisionPoint = netiface.createDecisionPoint(s_approach, 50, "s_approach_decisionPoint",
                                                                UnitOfMeasure.Metric)
        n_approach_decisionPoint = netiface.createDecisionPoint(n_approach, 50, "n_approach_decisionPoint")
        print("     创建决策路径")
        w_approach_decisionPoint_left = netiface.createDeciRouting(w_approach_decisionPoint, [w_approach, n_outgoing])
        w_approach_decisionPoint_straight = netiface.createDeciRouting(w_approach_decisionPoint, [w_approach, e_outgoing])
        w_approach_decisionPoint_right = netiface.createDeciRouting(w_approach_decisionPoint, [w_approach, s_outgoing])


        e_approach_decisionPoint_left = netiface.createDeciRouting(e_approach_decisionPoint, [e_approach, s_outgoing])

        e_approach_decisionPoint_left = netiface.createDeciRouting(e_approach_decisionPoint, [e_approach, s_outgoing])

        e_approach_decisionPoint_straight = netiface.createDeciRouting(e_approach_decisionPoint, [e_approach, w_outgoing])

        e_approach_decisionPoint_right = netiface.createDeciRouting(e_approach_decisionPoint, [e_approach, n_outgoing])


        routing = netiface.shortestRouting(s_approach, w_outgoing)
        s_approach_decisionPoint_left = netiface.createDeciRouting(s_approach_decisionPoint, routing.getLinks())
        # print(f"s_approach_decisionPoint_left={s_approach_decisionPoint_left}")

        s_approach_decisionPoint_straight = netiface.createDeciRouting(s_approach_decisionPoint, [s_approach, n_outgoing])

        s_approach_decisionPoint_right = netiface.createDeciRouting(s_approach_decisionPoint,[s_approach, e_outgoing])

        routing = netiface.createRouting([n_approach, e_outgoing])
        n_approach_decisionPoint_left = netiface.createDeciRouting(n_approach_decisionPoint,routing.getLinks())

        n_approach_decisionPoint_straight = netiface.createDeciRouting(n_approach_decisionPoint, [n_approach, s_outgoing])

        n_approach_decisionPoint_right = netiface.createDeciRouting(n_approach_decisionPoint,[n_approach, w_outgoing])


        # print("删除决策路径")
        netiface.removeDeciRouting(w_approach_decisionPoint, w_approach_decisionPoint_right)
        # print("根据首尾路段创建路径，并将其添加到决策点")
        routing = netiface.createRouting([w_approach, s_outgoing])
        routing1 = netiface.shortestRouting(w_approach, s_outgoing)
        w_approach_decisionPoint_right1 = netiface.createDeciRouting(w_approach_decisionPoint, [w_approach, s_outgoing])

        print("step6: 创建行人系统")
        n_crosswalk,   s_crosswalk,  w_crosswalk,  e_crosswalk = self.create_ped(netiface)

        print("step7: 创建公交和行人上下客系统")
        self.create_bus_ped(netiface, w_approach, e_outgoing, s_outgoing)
        print("step8: 创建行人和机动车信号灯和信控方案")
        self.create_signal_control(netiface, w_approach, e_approach, n_approach, s_approach, n_crosswalk,   s_crosswalk,  w_crosswalk,  e_crosswalk)
        print("step9: 创建车道箭头")
        self.createGuidArrow(netiface,w_approach)

    # def create_decision_route_ratio(self, decision, right_route, left_route, straight_route):
    #     # 一个决策点某个时段各路径车辆分配比
    #     decipointFlowRatioByInterval = Online.DecipointFlowRatioByInterval()
    #     # 决策点编号
    #     decipointFlowRatioByInterval.deciPointID = decision.id()
    #     # 起始时间 单位秒
    #     decipointFlowRatioByInterval.startDateTime =0
    #     # 结束时间 单位秒
    #     decipointFlowRatioByInterval.endDateTime = 3600
    #     lRoutingFlowRatio = []
    #
    #     # 右转
    #     lRoutingFlowRatio.append(Online.RoutingFlowRatio(right_route.id(), 1))
    #     # 左转
    #     lRoutingFlowRatio.append(Online.RoutingFlowRatio(left_route.id(), 1))
    #     # 直行
    #     lRoutingFlowRatio.append(Online.RoutingFlowRatio(straight_route.id(),2))
    #
    #     decipointFlowRatioByInterval.mlRoutingFlowRatio = lRoutingFlowRatio


    # 创建行人面域
    def create_ped(self,netiface):
        # pedTypes = netiface.pedestrianTypes()
        # print(f"获取行人类型={pedTypes},行人类型具体信息: 行人类型编码={pedTypes[0].pedestrianTypeCode}, 行人类型名称={pedTypes[0].pedestrianTypeName},"
        #       f"期望速度={pedTypes[0].desiredSpeed}, 期望速度方差={pedTypes[0].desiredSpeedStandardDeviation},"
        #       f"行人半径={pedTypes[0].radius}, 行人半径方差={pedTypes[0].radiusStandardDeviation},"
        #       f"行人重量={pedTypes[0].weight},  行人重量方差={pedTypes[0].weightStandardDeviation},")

        print("     创建行人组成")
        # # 创建行人组成
        compostion = {1:0.8, 2:0.2}
        pedComposition = netiface.createPedestrianComposition("自定义1", compostion)

        print("     创建行人图层")
        # 创建行人图层
        pedLayer = netiface.addLayerInfo("行人图层",0.0,True, False)
        pedLayer1 = netiface.addLayerInfo("行人图层1",10.0,True, False)
        netiface.removeLayerInfo(pedLayer1.id)
        netiface.updateLayerInfo(pedLayer.id, "基础行人图层",0.0, True, False)

        print("     创建行人面域")
        # # 创建行人面域
        # leftupArea = netiface.createPedestrianRectRegion(QPointF(-300,-300), QPointF(-400,-400))
        # leftupArea1 = netiface.createPedestrianRectRegion(QPointF(-400, -400), QPointF(-500, -500))
        # netiface.removePedestrianRectRegion(leftupArea1)
        #
        # ta = netiface.createPedestrianTriangleRegion(QPointF(300, -300), QPointF(330, -330))
        # ta1 = netiface.createPedestrianTriangleRegion(QPointF(100, -100), QPointF(130, -130))
        # netiface.removePedestrianTriangleRegion(ta1)
        #
        # elr = netiface.createPedestrianEllipseRegion(QPointF(500, -500), QPointF(550, -550))
        # elr1 = netiface.createPedestrianEllipseRegion(QPointF(500, -500), QPointF(560, -520))
        # netiface.removePedestrianEllipseRegion(elr1)
        #
        # # fsr = netiface.createPedestrianFanShapeRegion(QPointF(200, -200), QPointF(230, -230))
        # fsr1 = netiface.createPedestrianFanShapeRegion(QPointF(180, -180), QPointF(230, -230))
        # netiface.removePedestrianFanShapeRegion(fsr1)
        #

        # polygonShape = QPolygonF([QPointF(300, 300), QPointF(320, 310), QPointF(270, 270), QPointF(270, 275)])
        # pp = netiface.createPedestrianPolygonRegion(polygonShape)
        # polygonShape1 = QPolygonF([QPointF(-100, -500), QPointF(-200, -500), QPointF(-200, -25), QPointF(-100, -25)])
        # pp1 = netiface.createPedestrianPolygonRegion(polygonShape1)
        # netiface.removePedestrianPolygonRegion(pp1)





        # 人行道
        print("     创建人行道")
        w_approach_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(-300, 14), QPointF(-25, 14)])
        w_outgoing_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(-300, -14), QPointF(-25, -14)])
        e_approach_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(300, -14), QPointF(25, -14)])
        e_outgoing_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(300, 14), QPointF(25, 14)])
        n_approach_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(-14, -300),  QPointF(-14, -25)])
        n_outgoing_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(14, -25), QPointF(14, -300)])
        s_approach_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(14,300), QPointF(14, 25)])
        s_outgoing_sidewalk = netiface.createPedestrianSideWalkRegion([QPointF(-14,300), QPointF(-14, 25)])


        cwr = netiface.createPedestrianCrossWalkRegion(QPointF(300, -300), QPointF(30, -30))
        netiface.removePedestrianCrossWalkRegion(cwr)

        print("     创建交叉口四个等待区面域")
        fsr2 = netiface.createPedestrianFanShapeRegion(QPointF(-26, -26), QPointF(-15, -15))
        fsr3 = netiface.createPedestrianFanShapeRegion(QPointF(-26, 26), QPointF(-15, 15))
        fsr4 = netiface.createPedestrianFanShapeRegion(QPointF(26, -26), QPointF(15, -15))
        fsr5 = netiface.createPedestrianFanShapeRegion(QPointF(26, 26), QPointF(15, 15))
        # 创建斑马线
        print("     创建斑马线")
        n_crosswalk = netiface.createPedestrianCrossWalkRegion(QPointF(14, -22), QPointF(-14, -22))
        s_crosswalk = netiface.createPedestrianCrossWalkRegion(QPointF(14, 22), QPointF(-14, 22))
        w_crosswalk = netiface.createPedestrianCrossWalkRegion(QPointF(-22, -14), QPointF(-22, 14))
        e_crosswalk = netiface.createPedestrianCrossWalkRegion(QPointF(22, -14), QPointF(22, 14))
        # print(f"n_crosswalk={n_crosswalk}, s_crosswalk={s_crosswalk}, w_crosswalk={w_crosswalk}, e_crosswalk={e_crosswalk} ")

        # 创建行人发生点, 更新行人发生点配置信息
        print("     创建行人发生点和路径")
        w_path_start1 = netiface.createPedestrianPathStartPoint(QPointF(-250, 14))
        netiface.removePedestrianPathStartPoint(w_path_start1)
        w_path_start = netiface.createPedestrianPathStartPoint(QPointF(-280, 14))
        # print(f"Online.Pedestrian={dir(Online.Pedestrian)}")


        configInfo = netiface.findPedestrianStartPointConfigInfo(w_path_start.getId())
        genPedestrianConfigInfo = Online.Pedestrian.GenPedestrianInfo()
        genPedestrianConfigInfo.pedestrianCount = 1000
        genPedestrianConfigInfo.timeInterval = 2000
        pedestrianTrafficDistributionConfigInfo = Online.Pedestrian.PedestrianTrafficDistributionInfo()
        pedestrianTrafficDistributionConfigInfo.timeInterval = 2000
        configInfo.genPedestrianConfigInfo = [genPedestrianConfigInfo]
        configInfo.pedestrianTrafficDistributionConfigInfo = [pedestrianTrafficDistributionConfigInfo]
        r = netiface.updatePedestrianStartPointConfigInfo(configInfo)


        w_path_end = netiface.createPedestrianPathEndPoint(QPointF(-280, -14))
        w_path_end1 = netiface.createPedestrianPathEndPoint(QPointF(10, 14))
        netiface.removePedestrianPathEndPoint(w_path_end1)
        e_path_start = netiface.createPedestrianPathStartPoint(QPointF(280, 14))
        e_path_end = netiface.createPedestrianPathEndPoint(QPointF(280, -14))
        # e_path_start1 = netiface.createPedestrianPathStartPoint(QPointF(-280, 14))
        # e_path_end1 = netiface.createPedestrianPathEndPoint(QPointF(20, 14))
        n_path_start = netiface.createPedestrianPathStartPoint(QPointF(-14,-280))
        n_path_end = netiface.createPedestrianPathEndPoint(QPointF(14, -280))
        s_path_start = netiface.createPedestrianPathStartPoint(QPointF(14, 280))
        s_path_end = netiface.createPedestrianPathEndPoint(QPointF(-14, 280))


        # 创建行人路径
        #
        p1 = netiface.createPedestrianPath(w_path_start,w_path_end,[])
        netiface.removePedestrianPath(p1)
        pww = netiface.createPedestrianPath(w_path_start, w_path_end, [QPointF(-22,-1),])
        pwe  = netiface.createPedestrianPath(w_path_start, e_path_end,[])
        pws = netiface.createPedestrianPath(w_path_start, s_path_end,[])
        pwn = netiface.createPedestrianPath(w_path_start, n_path_end, [])

        pew = netiface.createPedestrianPath(e_path_start, w_path_end, [])
        pee  = netiface.createPedestrianPath(e_path_start, e_path_end,[])
        pes = netiface.createPedestrianPath(e_path_start, s_path_end,[])
        pen = netiface.createPedestrianPath(e_path_start, n_path_end, [])

        psw = netiface.createPedestrianPath(s_path_start, w_path_end, [])
        pse  = netiface.createPedestrianPath(s_path_start, e_path_end,[])
        pss = netiface.createPedestrianPath(s_path_start, s_path_end,[])
        psn = netiface.createPedestrianPath(s_path_start, n_path_end, [])

        pnw = netiface.createPedestrianPath(n_path_start, w_path_end, [])
        pne  = netiface.createPedestrianPath(n_path_start, e_path_end,[])
        pns = netiface.createPedestrianPath(n_path_start, s_path_end,[])
        pnn = netiface.createPedestrianPath(n_path_start, n_path_end, [])


        #
        # print(f"行人类型={netiface.pedestrianTypes}")
        # print(f"行人类型={netiface.pedestrianTypes()}, 获取所有行人组成={netiface.pedestrianCompositions()},"
        #       f"获取行人层级信息={netiface.layerInfos()}, 获取所有行人面域={netiface.pedestrianRegions()},"
        #       f"获取所有矩形面域={netiface.pedestrianRectRegions()}, 获取所有椭圆形面域={netiface.pedestrianEllipseRegions()},"
        #       f"获取所有三角形面域={netiface.pedestrianTriangleRegions()},获取所有扇形面域={netiface.pedestrianFanShapeRegions()},"
        #       f"获取所有多边形面域={netiface.pedestrianPolygonRegions()}, 获取所有人行道={netiface.pedestrianSideWalkRegions()},"
        #       f"获取所有人行横道={netiface.pedestrianCrossWalkRegions()}, 获取所有行人发生点={netiface.pedestrianPathStartPoints()},"
        #       f"获取所有行人结束点={netiface.pedestrianPathEndPoints()}, 获取所有行人决策点={netiface.pedestrianPathDecisionPoints()},"
        #       f"获取行人楼梯区域={netiface.pedestrianStairRegions()},"
        #       f"根据id获取行人面域={netiface.findPedestrianRegion(netiface.pedestrianRegions()[0].getId() if netiface.pedestrianRegions() is not None and len(netiface.pedestrianRegions())>0 else 0)},"
        #       f"根据id获取矩形面域={netiface.findPedestrianRectRegion(netiface.pedestrianRectRegions()[0].getId() if netiface.pedestrianRectRegions() is not None and len(netiface.pedestrianRectRegions())>0 else 0)},"
        #       f"根据id获取椭圆形面域={netiface.findPedestrianEllipseRegion(netiface.pedestrianEllipseRegions()[0].getId() if netiface.pedestrianEllipseRegions() is not None and len(netiface.pedestrianEllipseRegions())>0 else 0)},"
        #       f"根据id获取三角形面域={netiface.findPedestrianTriangleRegion(netiface.pedestrianTriangleRegions()[0].getId() if netiface.pedestrianTriangleRegions() is not None and len(netiface.pedestrianTriangleRegions())>0 else 0)},"
        #       f"根据id获取扇形面域={netiface.findPedestrianFanShapeRegion(netiface.pedestrianFanShapeRegions()[0].getId() if netiface.pedestrianFanShapeRegions() is not None and len(netiface.pedestrianFanShapeRegions())>0 else 0)},"
        #       f"根据id获取多边形面域={netiface.findPedestrianPolygonRegion(netiface.pedestrianPolygonRegions()[0].getId() if netiface.pedestrianPolygonRegions() is not None and len(netiface.pedestrianPolygonRegions())>0 else 0)},"
        #       f"根据id获取人行道={netiface.findPedestrianSideWalkRegion(netiface.pedestrianSideWalkRegions()[0].getId() if netiface.pedestrianSideWalkRegions() is not None and len(netiface.pedestrianSideWalkRegions())>0 else 0)},"
        #       f"根据id获取人行横道={netiface.findPedestrianCrossWalkRegion(netiface.pedestrianCrossWalkRegions()[0].getId() if netiface.pedestrianCrossWalkRegions() is not None and len(netiface.pedestrianCrossWalkRegions())>0 else 0)},"
        #       f"根据id获取行人发生点={netiface.findPedestrianPathStartPoint(netiface.pedestrianPathStartPoints()[0].getId() if netiface.pedestrianPathStartPoints() is not None and len(netiface.pedestrianPathStartPoints())>0 else 0)},"
        #       f"根据id获取行人结束点={netiface.findPedestrianPathEndPoint(netiface.pedestrianPathEndPoints()[0].getId() if netiface.pedestrianPathEndPoints() is not None and len(netiface.pedestrianPathEndPoints())>0 else 0)},"
        #       f"根据id获取行人决策点={netiface.findPedestrianDecisionPoint(netiface.pedestrianPathDecisionPoints()[0].getId() if netiface.pedestrianPathDecisionPoints() is not None and len(netiface.pedestrianPathDecisionPoints())>0 else 0)},"
        #       f"根据id获取行人楼梯区域={netiface.findPedestrianStairRegion(netiface.pedestrianStairRegions()[0].getId() if netiface.pedestrianStairRegions() is not None and len(netiface.pedestrianStairRegions())>0 else 0)},"
        #       f"根据id获取行人路径，包括局部路径={netiface.findPedestrianPath(netiface.pedestrianPaths()[0].getId() if netiface.pedestrianPaths() is not None and len(netiface.pedestrianPaths())>0 else 0)},"
        #       f"根据id获取人行横道红绿灯={netiface.findCrosswalkSignalLamp(netiface.crosswalkSignalLamps()[0].getId() if netiface.crosswalkSignalLamps() is not None and len(netiface.crosswalkSignalLamps())>0 else 0)},"
        #       f"根据id获取行人发生点配置信息，id为行人发生点ID={netiface.findPedestrianStartPointConfigInfo(netiface.pedestrianPathStartPoints()[0].getId() if netiface.pedestrianPathStartPoints() is not None and len(netiface.pedestrianPathStartPoints())>0 else 0)},"
        #       f"根据id获取行人决策点配置信息，id为行人决策点ID={netiface.findPedestrianDecisionPointConfigInfo(netiface.pedestrianPathDecisionPoints()[0].getId() if netiface.pedestrianPathDecisionPoints() is not None and len(netiface.pedestrianPathDecisionPoints())>0 else 0)},"
        #       f"")

        print("     创建行人上下楼梯")
        self.create_ped_updownstairs(netiface, pedLayer)

        return  n_crosswalk,   s_crosswalk,  w_crosswalk,  e_crosswalk

    def create_bus_ped(self,netiface, w_approach, e_outgoing, s_outgoing):
        """
        行人上下公交站台案例
        Returns:

        """

        # 创建公交线路
        print("     创建公交线路")
        busline = netiface.createBusLine([w_approach,e_outgoing])
        busline1 = netiface.createBusLine([w_approach, s_outgoing])
        netiface.removeBusLine(busline1)
        if busline is not None:
            busline.setDesirSpeed(60, UnitOfMeasure.Metric)


        # 创建公交站
        print("     创建公交站点")
        busstation1 = netiface.createBusStation(w_approach.lanes()[0],30, 100,"西进口公交站点1", UnitOfMeasure.Metric)
        busstation2 = netiface.createBusStation(w_approach.lanes()[0], 30, 200, "西进口公交站点2", UnitOfMeasure.Metric)
        busstation3 = netiface.createBusStation(e_outgoing.lanes()[0], 30, 200, "东出口公交站点1", UnitOfMeasure.Metric)
        # 将公交站点关联到公交线路上
        netiface.addBusStationToLine(busline, busstation1)
        netiface.addBusStationToLine(busline, busstation2)
        netiface.addBusStationToLine(busline, busstation3)
        netiface.removeBusStationFromLine(busline, busstation2)
        netiface.removeBusStation(busstation2)

        print("     创建公交站点行人上下客面域")
        # 创建行人上下客面域，行人决策点
        up_ped_area = netiface.createPedestrianRectRegion(QPointF(-200, 10),QPointF(-170, 12))
        up_ped_area.setIsBoardingArea(True)
        up_down_ped_area = netiface.createPedestrianRectRegion(QPointF(260, 10),QPointF(200, 30))
        up_down_ped_area.setIsBoardingArea(True)
        up_down_ped_area.setIsAlightingArea(True)
        leave_area = netiface.createPedestrianRectRegion(QPointF(170, 28),QPointF(300, 40)) # 行人下客后离开的行人区域

        s_path_start_and_decision_point1 = netiface.createPedestrianDecisionPoint(QPointF(14, 280))
        netiface.removePedestrianDecisionPoint(s_path_start_and_decision_point1)

        s_path_start_and_decision_point = netiface.createPedestrianDecisionPoint(QPointF(-250, 15))
        downped_path_start_and_decision_point = netiface.createPedestrianDecisionPoint(QPointF(240, 15))
        s_path_end = netiface.createPedestrianPathEndPoint(QPointF(-180, 11))
        s_path_end1 = netiface.createPedestrianPathEndPoint(QPointF(240, 35))
        upped_path = netiface.createPedestrianPath(s_path_start_and_decision_point,s_path_end,[])
        downped_path = netiface.createPedestrianPath(downped_path_start_and_decision_point, s_path_end1, [])

    def create_ped_updownstairs(self,netiface, pedLayer):
        """
        行人上下楼梯的案例
        Returns:

        """
        # 创建行人图层
        pedLayer2 = netiface.addLayerInfo("行人图层2", 10.0, True, False)

        # 创建行人面域
        yilou = netiface.createPedestrianRectRegion(QPointF(30, 30), QPointF(40, 100))
        yilou.setLayerId(pedLayer.id)
        erlou = netiface.createPedestrianRectRegion(QPointF(50, 30), QPointF(60, 100))
        erlou.setLayerId(pedLayer2.id)

        # 创建楼梯
        stair_obj1 = netiface.createPedestrianStairRegion(QPointF(38, 35), QPointF(52, 35))
        stair_obj1.setStartLayerId(pedLayer.id)
        stair_obj1.setEndLayerId(pedLayer2.id)
        testStair = netiface.createPedestrianStairRegion(QPointF(40, 32), QPointF(60, 38))
        netiface.removePedestrianStairRegion(testStair)
        stair_obj2 = netiface.createPedestrianStairRegion(QPointF(38, 65), QPointF(52, 65))
        stair_obj2.setStartLayerId(pedLayer.id)
        stair_obj2.setEndLayerId(pedLayer2.id)
        # 创建行人决策点+楼梯，进行楼梯分流
        s_path_start = netiface.createPedestrianPathStartPoint(QPointF(32, 55))
        s_path_end = netiface.createPedestrianPathEndPoint(QPointF(55, 55))
        p1 = netiface.createPedestrianPath(s_path_start, s_path_end, [QPointF(40, 35)])

        s_path_start_and_decision_point1 = netiface.createPedestrianDecisionPoint(QPointF(36, 50))
        s_path_end = netiface.createPedestrianPathEndPoint(QPointF(55, 35))
        p1_1 = netiface.createPedestrianPath(s_path_start_and_decision_point1, s_path_end, [QPointF(45, 35)])
        s_path_end = netiface.createPedestrianPathEndPoint(QPointF(55, 65))
        p1_2 = netiface.createPedestrianPath(s_path_start_and_decision_point1,s_path_end,[QPointF(45, 65)])

        pedDecisionPointConfigInfo = Online.Pedestrian.PedestrianDecisionPointConfigInfo()
        pedDecisionPointConfigInfo.id= s_path_start_and_decision_point1.getId()
        distributeInfo =  Online.Pedestrian.PedestrianTrafficDistributionInfo()
        distributeInfo.timeInterval = 1000
        distributeInfo.trafficRatio = {p1_1.getId():2, p1_2.getId():1}
        # print(f"distributeInfo.trafficRatio={distributeInfo.trafficRatio}")
        pedDecisionPointConfigInfo.pedestrianTrafficDistributionConfigInfo = {p1.getId():[distributeInfo]}
        netiface.updatePedestrianDecisionPointConfigInfo(pedDecisionPointConfigInfo)

    def createGuidArrow(self, netiface, w_approach):
        arrowType = Online.GuideArrowType.StraightRight
        straitghtRightArrow = netiface.createGuidArrow(w_approach.lanes()[0], 4,10,arrowType)

    def create_signal_control(self, netiface,w_approach, e_approach, n_approach, s_approach,
                              n_crosswalk,   s_crosswalk,  w_crosswalk,  e_crosswalk):
        # 创建信号机
        print("     创建信号机")
        trafficController = netiface.createSignalController("交叉口1")
        # 创建信控方案
        print("     创建信控方案")
        signalPlan = netiface.createSignalPlan(trafficController, "早高峰", 150, 0,0,1800) # 150, 50,0,1800
        # 创建方向详情--相位
        print("     创建相位")
        green = Online.ColorInterval("绿",50)
        yellow = Online.ColorInterval("黄",3)
        red = Online.ColorInterval("红",97)
        w_e_straight_phasecolor = [green, yellow, red]
        w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行",w_e_straight_phasecolor)

        we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor)

        red = Online.ColorInterval("红",53)
        green = Online.ColorInterval("绿",30)
        yellow = Online.ColorInterval("黄",3)
        red1 = Online.ColorInterval("红",64)
        w_e_left_phasecolor = [red, green, yellow, red1]
        w_e_left_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西左转",w_e_left_phasecolor)


        red = Online.ColorInterval("红",86)
        green = Online.ColorInterval("绿",30)
        yellow = Online.ColorInterval("黄",3)
        red1 = Online.ColorInterval("红",31)
        s_n_straight_phasecolor = [red, green, yellow, red1]
        s_n_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "南北直行", s_n_straight_phasecolor)
        ns_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "南北直行行人", s_n_straight_phasecolor)

        red = Online.ColorInterval("红",119)
        green = Online.ColorInterval("绿",29)
        yellow = Online.ColorInterval("黄",3)
        s_n_left_phasecolor = [red, green, yellow]
        s_n_left_phase = netiface.createSignalPlanSignalPhase(signalPlan, "南北左转",s_n_left_phasecolor)

        # 创建机动车信号灯
        print("     创建机动车信号灯并绑定相位")
        w_e_straight_lamps = []
        for lane in w_approach.lanes():
            if lane.number()< w_approach.laneCount()-1 and lane.number()>0:
                signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length()-0.5)
                w_e_straight_lamps.append(signalLamp)

        for lane in e_approach.lanes():
            if lane.number()< e_approach.laneCount()-1 and lane.number()>0:
                signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length()-0.5)
                w_e_straight_lamps.append(signalLamp)



        w_e_left_lamps = []
        for lane in w_approach.lanes():
            if lane.number()== w_approach.laneCount()-1:
                signalLamp = netiface.createSignalLamp(w_e_left_phase, "东西左转信号灯", lane.id(), -1,
                                                       lane.length() - 0.5)
                w_e_left_lamps.append(signalLamp)

        for lane in e_approach.lanes():
            if lane.number()== e_approach.laneCount()-1:
                signalLamp = netiface.createSignalLamp(w_e_left_phase, "东西左转信号灯", lane.id(), -1,
                                                       lane.length() - 0.5)
                w_e_left_lamps.append(signalLamp)


        n_s_straight_lamps = []
        for lane in n_approach.lanes():
            if lane.number()< n_approach.laneCount()-1 and lane.number()>0:
                signalLamp = netiface.createSignalLamp(s_n_straight_phase, "南北直行信号灯", lane.id(), -1,
                                                       lane.length() - 0.5)
                n_s_straight_lamps.append(signalLamp)


        for lane in s_approach.lanes():
            if lane.number()< s_approach.laneCount()-1 and lane.number()>0:
                signalLamp = netiface.createSignalLamp(s_n_straight_phase, "南北直行信号灯", lane.id(), -1,
                                                       lane.length() - 0.5)
                n_s_straight_lamps.append(signalLamp)


        n_s_left_lamps = []
        for lane in n_approach.lanes():
            if lane.number()== n_approach.laneCount()-1:
                signalLamp = netiface.createSignalLamp(s_n_left_phase, "南北左转信号灯", lane.id(), -1,
                                                       lane.length() - 0.5)

                n_s_left_lamps.append(signalLamp)
        for lane in s_approach.lanes():
                if lane.number()== s_approach.laneCount()-1:
                    signalLamp = netiface.createSignalLamp(s_n_left_phase, "南北左转信号灯", lane.id(), -1,
                                                           lane.length() - 0.5)
                    n_s_left_lamps.append(signalLamp)

        print("     创建行人信号灯并绑定相位")
        # 创建行人信号灯, 并行人信号灯关联相位
        signalLamp1_positive = netiface.createCrossWalkSignalLamp(trafficController, "南斑马线信号灯", s_crosswalk.getId() ,QPointF(13, 22),True)
        signalLamp1_negetive = netiface.createCrossWalkSignalLamp(trafficController, "南斑马线信号灯", s_crosswalk.getId() ,QPointF(-13, 22),False)
        netiface.addCrossWalkSignalPhaseToLamp(we_ped_phase.id(), signalLamp1_positive)
        netiface.addCrossWalkSignalPhaseToLamp(we_ped_phase.id(), signalLamp1_negetive)

        signalLamp2_positive = netiface.createCrossWalkSignalLamp(trafficController, "北斑马线信号灯", n_crosswalk.getId() ,QPointF(13, -22),True)
        signalLamp2_negetive = netiface.createCrossWalkSignalLamp(trafficController, "北斑马线信号灯", n_crosswalk.getId(),QPointF(-13, -22), False)
        netiface.addCrossWalkSignalPhaseToLamp(we_ped_phase.id(), signalLamp2_positive)
        netiface.addCrossWalkSignalPhaseToLamp(we_ped_phase.id(), signalLamp2_negetive)

        signalLamp3_positive = netiface.createCrossWalkSignalLamp(trafficController, "东斑马线信号灯", e_crosswalk.getId() ,QPointF(22, -13),True)
        signalLamp3_negetive = netiface.createCrossWalkSignalLamp(trafficController, "东斑马线信号灯", e_crosswalk.getId() ,QPointF(22, 13),False)
        netiface.addCrossWalkSignalPhaseToLamp(ns_ped_phase.id(), signalLamp3_positive)
        netiface.addCrossWalkSignalPhaseToLamp(ns_ped_phase.id(), signalLamp3_negetive)

        signalLamp4_positive = netiface.createCrossWalkSignalLamp(trafficController, "西斑马线信号灯", w_crosswalk.getId() ,QPointF(-22, -13),True)
        signalLamp4_negetive = netiface.createCrossWalkSignalLamp(trafficController, "西斑马线信号灯", w_crosswalk.getId() ,QPointF(-22, 13),False)
        netiface.addCrossWalkSignalPhaseToLamp(ns_ped_phase.id(), signalLamp4_positive)
        netiface.addCrossWalkSignalPhaseToLamp(ns_ped_phase.id(), signalLamp4_negetive)

    def showRoadNetAttr(self, netiface):

        netpath = netiface.netFilePath()
        print(f"===保存路网，文件路径={netpath}")
        roadNet = netiface.roadNet()
        print(f"路网属性netAttrs={netiface.netAttrs()}")
        print(f"===获取当前路网基本信息:  id={roadNet.id()}, netName={roadNet.netName()},url={roadNet.url()},"
              f"路网来源type={roadNet.type()}, 背景图片路径={roadNet.bkgUrl()},其他属性={roadNet.otherAttrs()},"
              f"路网说明={roadNet.explain()}, 路网中心点位置(像素)={roadNet.centerPoint()}，路网中心点位置(米制)={roadNet.centerPoint(UnitOfMeasure.Metric)} ")

        print("===获取场景信息")
        graphicScene = netiface.graphicsScene()
        graphicsView = netiface.graphicsView()
        sceneScale = netiface.sceneScale()
        print(f"像素比={sceneScale}，场景宽度={netiface.sceneWidth()}，"
              # f"高度={netiface.sceneHeight()}, "
              f"背景图={netiface.backgroundMap()},")

    def showSectionAttr(self, netiface):
        print(
            f"===场景中的section个数（路段与连接段的父类对象）={len(netiface.sections())}，第一个section的属性={netiface.sections()[0].id()}")
        section = netiface.sections()[0]
        for s in netiface.sections():
            if s.id()==135 and s.isLink():
                print(f"link={s.id()}, v3z={s.v3z()},m2p_v3z={m2p(s.v3z())},p2m_v3z={p2m(s.v3z())}v3z_m={s.v3z(UnitOfMeasure.Metric)}")
                print(
                    f"link={s.id()}, v3z={s.v3z()},m2p_v3z={m2p(s.v3z())},p2m_v3z={p2m(s.v3z())}v3z_m={s.v3z(UnitOfMeasure.Metric)}")

                print(f"该section的属性：id(linkid or connectorid)={section.id()}, 类型gtype={section.gtype()}, "
                      f"是否为link={section.isLink()}, sectionId={section.sectionId()}, name={section.name()}, 设置新的name={section.setName(section.name() + str(1))},"
                      f"v3z(像素制)={section.v3z()}，v3z(米制)={section.v3z(UnitOfMeasure.Metric)}，长度length（像素制）={section.length()}， 米制={section.length(UnitOfMeasure.Metric)}"
                      f"section下包含的laneObject（lane和lanconnector的父类）={section.laneObjects()}, fromSection={section.fromSection()}, toSection={section.toSection()},"
                      f"设置自定义属性setOtherAttr={section.setOtherAttr({'newAttr': 'add a new attr'})}, 将section强转为子类link={section.castToLink()},"
                      f"将section强转为子类Iconnector={section.castToLink()}, 获取section外轮廓={section.polygon()},"
                      f" 分布式worker节点标识={section.workerKey()},将section分配给另一个节点={section.setWorkerKey(1)}, "
                      f"上游worker={section.fromWorkerKey()},设置上游worker={section.setFromWorkerKey(1)}")
        print()
        print(f"该section的属性：id(linkid or connectorid)={section.id()}, 类型gtype={section.gtype()}, "
              f"是否为link={section.isLink()}, sectionId={section.sectionId()}, name={section.name()}, 设置新的name={section.setName(section.name() + str(1))},"
              f"v3z(像素制)={section.v3z()}，v3z(米制)={section.v3z(UnitOfMeasure.Metric)}，长度length（像素制）={section.length()}， 米制={section.length(UnitOfMeasure.Metric)}"
              f"section下包含的laneObject（lane和lanconnector的父类）={section.laneObjects()}, fromSection={section.fromSection()}, toSection={section.toSection()},"
              f"设置自定义属性setOtherAttr={section.setOtherAttr({'newAttr': 'add a new attr'})}, 将section强转为子类link={section.castToLink()},"
              f"将section强转为子类Iconnector={section.castToLink()}, 获取section外轮廓={section.polygon()},"
              f" 分布式worker节点标识={section.workerKey()},将section分配给另一个节点={section.setWorkerKey(1)}, "
              f"上游worker={section.fromWorkerKey()},设置上游worker={section.setFromWorkerKey(1)}")

    def showLaneObjectAttr(self, netiface):
        section = netiface.findLink(netiface.sections()[0].id())
        laneObject = section.laneObjects()[0]
        for i in section.laneObjects():
            if i.number() == 0:
                laneObject = i
                break
        print(laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[1],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric))
        print(f"===section中的第一个laneObject(最右侧) id(linkid or connectorid)={laneObject.id()}, 类型gtype={laneObject.gtype()}, "
              f"是否为link={laneObject.isLane()}, 所属section={laneObject.section()},长度length（像素制）={laneObject.length()}， 米制={laneObject.length(UnitOfMeasure.Metric)}"
              f"fromLaneObject={laneObject.fromLaneObject()}, toLaneObject={laneObject.toLaneObject()},"
              f"centerBreakPoints(像素制)={laneObject.centerBreakPoints()},centerBreakPoints(米制)={laneObject.centerBreakPoints(UnitOfMeasure.Metric)},"
              f"leftBreakPoints(像素制)={laneObject.leftBreakPoints()},leftBreakPoints(米制)={laneObject.leftBreakPoints(UnitOfMeasure.Metric)},"
              f"rightBreakPoints(像素制)={laneObject.rightBreakPoints()},rightBreakPoints(米制)={laneObject.rightBreakPoints(UnitOfMeasure.Metric)},"
              f"centerBreakPoint3Ds(像素制)={laneObject.centerBreakPoint3Ds()},centerBreakPoint3Ds(米制)={laneObject.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"leftBreakPoint3Ds(像素制)={laneObject.leftBreakPoint3Ds()},leftBreakPoint3Ds(米制)={laneObject.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"rightBreakPoint3Ds(像素制)={laneObject.rightBreakPoint3Ds()},rightBreakPoint3Ds(米制)={laneObject.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"leftBreak3DsPartly(像素制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints()[1],laneObject.leftBreakPoints()[-1])},"
              f"leftBreak3DsPartly(米制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[1],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
              f"rightBreak3DsPartly(像素制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints()[1],laneObject.leftBreakPoints()[-1])},"
              f"rightBreak3DsPartly(米制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[1],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
              f"distToStartPoint(像素制)={laneObject.distToStartPoint(laneObject.centerBreakPoints()[0])}，distToStartPoint(米制)={laneObject.distToStartPoint(laneObject.centerBreakPoints()[0],UnitOfMeasure.Metric)}，"
              f"设置自定义属性setOtherAttr={laneObject.setOtherAttr({'newAttr':'add a new attr'})}, 将section强转为子类link={laneObject.castToLane()},"
              f"将section强转为子类Iconnector={laneObject.castToLaneConnector()}")
        outPoint = QPointF()
        outIndex = 0
        outPoint1 = QPointF()
        outIndex1 = 0
        laneObject.getPointAndIndexByDist(2.0, outPoint, outIndex)
        laneObject.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
        print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

        outPoint2 = QPointF()
        outPoint3 = QPointF()
        laneObject.getPointByDist(2.0, outPoint2)
        laneObject.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
        print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")

    def showLinkAttr(self,netiface):
        print(f"===场景中的link总数={netiface.linkCount()}，第一个link的id={netiface.linkIds()[0]}")
        link = netiface.findLink(netiface.linkIds()[0])
        link1 = netiface.links()[0]
        print(link1)
        print(f"该link的属性：id={link.id()}, ")
        print(f"link.fromConnectors()={link.fromConnectors()}")
        print(f"该link的最右侧车道为：id={link.id()}, 其属性为：路段类型={link.gtype()}, 路段长度（像素制）={link.length()}，米制={link.length(UnitOfMeasure.Metric)},"
              f"宽度（像素制）={link.width()}，米制={link.width(UnitOfMeasure.Metric)}， 高程（像素制）={link.z()}， 米制={link.z(UnitOfMeasure.Metric)},"
              f"高程v3z(像素制)={link.v3z()},米制={link.v3z(UnitOfMeasure.Metric)}, 设置新名字={link.setName('test_name')}name={link.name()},linkType={link.linkType()},"
              f"设置路段类型为城市次干道={link.setType('次要干道')}，再次获取城市类型={link.linkType()}, 车道数={link.laneCount()},"
              f"路段最高限速(像素制)={link.limitSpeed()}， 米制（km/h）={link.limitSpeed(UnitOfMeasure.Metric)},路段最低限速(像素制)={link.minSpeed()}， 米制（km/h）={link.minSpeed(UnitOfMeasure.Metric)},"
              f"将路段最高限速提高百分之20={link.setLimitSpeed(link.limitSpeed()*1.2)} or {link.setLimitSpeed(link.limitSpeed(UnitOfMeasure.Metric)*1.2, UnitOfMeasure.Metric)},"
              f"路段最高限速(像素制)={link.limitSpeed()}， 米制（km/h）={link.limitSpeed(UnitOfMeasure.Metric)}, 路段包含的车道对象={link.lanes()},"
              f"路段包含的laneObject对象={link.laneObjects()},"
              f"路段中心线（像素制）={link.centerBreakPoints()},米制={link.centerBreakPoints(UnitOfMeasure.Metric)},"
              f"路段左侧线（像素制）={link.leftBreakPoints()},米制={link.leftBreakPoints(UnitOfMeasure.Metric)},"
              f"路段右侧线（像素制）={link.rightBreakPoints()},米制={link.rightBreakPoints(UnitOfMeasure.Metric)},"
              f"路段中心线3D（像素制）={link.centerBreakPoint3Ds()},米制={link.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"路段左侧线3D（像素制）={link.leftBreakPoint3Ds()},米制={link.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"路段右侧线3D（像素制）={link.rightBreakPoint3Ds()},米制={link.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"fromConnector={link.fromConnectors()}, toConnectors={link.toConnectors()},"
              f"fromSection={link.fromConnectors()[0].id() if link.fromConnectors() is not None and len(link.fromConnectors()) > 0 else 0},"
              f"toSection={link.toSection(link.toConnectors()[0].id() if link.toConnectors() is not None and len(link.toConnectors() )> 0 else 0)}, "
              f"自定义其他属性： setOtherAttr={link.setOtherAttr({'new_msg':'this is a av car'})},"
              f"从右到左依次为车道设置类别={link.setLaneTypes(['公交专用道','机动车道','机动车道'])}，为车道设置其他属性={link.setLaneOtherAtrrs([{'new_name':'自定义公交专用车道'},{'new_name':'自定义机动车道'},{'new_name':'自定义机动车道'}])}，"
              f"distToStartPoint距离起点长度（像素制）={link.distToStartPoint(link.centerBreakPoints()[-1])}, 米制={link.distToStartPoint(link.centerBreakPoints()[-1],UnitOfMeasure.Metric)},"
              f"polygon={link.polygon()}"
              )
        outPoint = QPointF()
        outIndex = 0
        outPoint1 = QPointF()
        outIndex1 = 0
        link.getPointAndIndexByDist(2.0, outPoint, outIndex)
        link.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
        print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

        outPoint2 = QPointF()
        outPoint3 = QPointF()
        link.getPointByDist(2.0, outPoint2)
        link.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
        print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")

    def showLaneAttr(self, netiface):
        link = netiface.links()[0]
        lane = link.lanes()[0]
        for i in link.lanes():
            if i.number() == 0:
                lane = i
                break
        print(f"===link中的第一个lane(最右侧) id={lane.id()}, 类型gtype={lane.gtype()}, "
              f"是否为lane={lane.isLane()}, 所属link={lane.link()}, 所属section={lane.section()},长度length（像素制）={lane.length()}， 米制={lane.length(UnitOfMeasure.Metric)},"
              f"宽度width（像素制）={lane.width()}， 米制={lane.width(UnitOfMeasure.Metric)}, 车道序号number={lane.number()},行为类型={lane.actionType()}"
              f"fromLaneObject={lane.fromLaneObject()}, toLaneObject={lane.toLaneObject()},"
              f"centerBreakPoints(像素制)={lane.centerBreakPoints()},centerBreakPoints(米制)={lane.centerBreakPoints(UnitOfMeasure.Metric)},"
              f"leftBreakPoints(像素制)={lane.leftBreakPoints()},leftBreakPoints(米制)={lane.leftBreakPoints(UnitOfMeasure.Metric)},"
              f"rightBreakPoints(像素制)={lane.rightBreakPoints()},rightBreakPoints(米制)={lane.rightBreakPoints(UnitOfMeasure.Metric)},"
              f"centerBreakPoint3Ds(像素制)={lane.centerBreakPoint3Ds()},centerBreakPoint3Ds(米制)={lane.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"leftBreakPoint3Ds(像素制)={lane.leftBreakPoint3Ds()},leftBreakPoint3Ds(米制)={lane.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"rightBreakPoint3Ds(像素制)={lane.rightBreakPoint3Ds()},rightBreakPoint3Ds(米制)={lane.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"leftBreak3DsPartly(像素制)={lane.leftBreak3DsPartly(lane.leftBreakPoints()[1],lane.leftBreakPoints()[-1])},"
              f"leftBreak3DsPartly(米制)={lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[1],lane.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
              f"rightBreak3DsPartly(像素制)={lane.leftBreak3DsPartly(lane.leftBreakPoints()[1],lane.leftBreakPoints()[-1])},"
              f"rightBreak3DsPartly(米制)={lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[1],lane.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
              f"distToStartPoint(像素制)={lane.distToStartPoint(lane.centerBreakPoints()[0])}，distToStartPoint(米制)={lane.distToStartPoint(lane.centerBreakPoints()[0],UnitOfMeasure.Metric)}，"
              f"设置自定义属性setOtherAttr={lane.setOtherAttr({'newAttr':'add a new attr'})}, setLaneType={lane.setLaneType('机动车道')},action Type={lane.actionType()}"
              f"polygon={lane.polygon()}")
        outPoint = QPointF()
        outIndex = 0
        outPoint1 = QPointF()
        outIndex1 = 0
        lane.getPointAndIndexByDist(2.0, outPoint, outIndex)
        lane.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
        print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

        outPoint2 = QPointF()
        outPoint3 = QPointF()
        lane.getPointByDist(2.0, outPoint2)
        lane.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
        print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")

    def showConnectorAttr(self, netiface):
        print(
            f"===场景中的connector个数（连接段对象）={len(netiface.connectors())},{netiface.connectorCount()}, {len(netiface.connectorIds())}，"
            f"第一个connector的属性={netiface.connectors()[0].id()}")
        connector = netiface.connectors()[0]
        connector1 = netiface.findConnector(netiface.connectorIds()[0])
        # print(type(connector), dir(connector))
        print(f"该connectors的属性：id(连接段和路段ID是独立的，因此两者ID可能会重复)={connector.id()}, 类型gtype={connector.gtype()},"
              f"name={connector.name()}, setName={connector.setName('new connector')} "
              f"长度length（像素制）={connector.length()}， 米制={connector.length(UnitOfMeasure.Metric)},"
              f"高程={connector.z()}, toLaneObject={connector.v3z()},"
              f"fromLink={connector.fromLink()}, toLink={connector.toLink()},fromSection={connector.fromSection(id=0)}, toSection={connector.toSection(id=0)},"
              f"最高限速(像素制)={connector.limitSpeed()},最高限速(米制)={connector.limitSpeed(UnitOfMeasure.Metric)},"
              f"最低限速(像素制)={connector.minSpeed()},最低限速(米制)={connector.minSpeed(UnitOfMeasure.Metric)},"
              f"laneConnectors={connector.laneConnectors()},laneObjects={connector.laneObjects()},"
              f"设置自定义属性setLaneConnectorOtherAtrrs={connector.setLaneConnectorOtherAtrrs([{'newAttr':i} for i in range(len(connector.laneConnectors()))])},"
              f"设置自定义属性setOtherAttr={connector.setOtherAttr({'newAttr':'add a new attr'})},"
              f"polygon={connector.polygon()}")

    def showLaneConnectorAttr(self, netiface):
        connector = netiface.findConnector(netiface.connectorIds()[0])
        laneConnector = connector.laneConnectors()[0]
        print(f"===laneConnector id={laneConnector.id()}, 类型gtype={laneConnector.gtype()}, "
              f"其所属的连接段={laneConnector.connector()}, 所属section={laneConnector.section()},"
              f"fromLane={laneConnector.fromLane()}, toLane={laneConnector.toLane()},"
              f"fromLaneObject={laneConnector.fromLaneObject()}, toLaneObject={laneConnector.toLaneObject()},"
              f"长度length（像素制）={laneConnector.length()}， 米制={laneConnector.length(UnitOfMeasure.Metric)},"
              f"centerBreakPoints(像素制)={laneConnector.centerBreakPoints()},centerBreakPoints(米制)={laneConnector.centerBreakPoints(UnitOfMeasure.Metric)},"
              f"leftBreakPoints(像素制)={laneConnector.leftBreakPoints()},leftBreakPoints(米制)={laneConnector.leftBreakPoints(UnitOfMeasure.Metric)},"
              f"rightBreakPoints(像素制)={laneConnector.rightBreakPoints()},rightBreakPoints(米制)={laneConnector.rightBreakPoints(UnitOfMeasure.Metric)},"
              f"centerBreakPoint3Ds(像素制)={laneConnector.centerBreakPoint3Ds()},centerBreakPoint3Ds(米制)={laneConnector.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"leftBreakPoint3Ds(像素制)={laneConnector.leftBreakPoint3Ds()},leftBreakPoint3Ds(米制)={laneConnector.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"rightBreakPoint3Ds(像素制)={laneConnector.rightBreakPoint3Ds()},rightBreakPoint3Ds(米制)={laneConnector.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
              f"leftBreak3DsPartly(像素制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[1],laneConnector.leftBreakPoints()[-1])},"
              f"leftBreak3DsPartly(米制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[1],laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
              f"rightBreak3DsPartly(像素制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[1],laneConnector.leftBreakPoints()[-1])},"
              f"rightBreak3DsPartly(米制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[1],laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
              f"distToStartPoint(像素制)={laneConnector.distToStartPoint(laneConnector.centerBreakPoints()[0])}，distToStartPoint(米制)={laneConnector.distToStartPoint(laneConnector.centerBreakPoints()[0],UnitOfMeasure.Metric)}，"
              f"设置自定义属性setOtherAttr={laneConnector.setOtherAttr({'newAttr':'add a new attr'})}")
        outPoint = QPointF()
        outIndex = 0
        outPoint1 = QPointF()
        outIndex1 = 0
        laneConnector.getPointAndIndexByDist(2.0, outPoint, outIndex)
        laneConnector.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
        print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

        outPoint2 = QPointF()
        outPoint3 = QPointF()
        laneConnector.getPointByDist(2.0, outPoint2)
        laneConnector.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
        print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")


        dist = laneConnector.distToStartPointWithSegmIndex(outPoint, outIndex)
        dist1 = laneConnector.distToStartPointWithSegmIndex(outPoint1, outIndex1, UnitOfMeasure.Metric)
        print(f"distToStartPointWithSegmIndex(像素制)={dist}, distToStartPointWithSegmIndex(米制)={dist1}")

    def showConnectorAreaAttr(self, netiface):
        allConnectorArea = netiface.allConnectorArea()
        connectorArea = allConnectorArea[0]
        connectorArea1 = netiface.findConnectorArea(connectorArea.id())
        print(
            f"===场景中的connectorArea个数={len(allConnectorArea)}，第一个connectorArea的属性={connectorArea.id()}")
        print(f"该connectorArea的属性：id={connectorArea.id()}, 包含的所有connector={connectorArea.allConnector()}, "
              f"面域中心点(像素制)={connectorArea.centerPoint()}，(米制)={connectorArea.centerPoint(UnitOfMeasure.Metric)}")

    def showDispatchPointAttr(self, netiface):
        dispatchPoints = netiface.dispatchPoints()
        dispatchPoint = dispatchPoints[0]
        connectorArea1 = netiface.findDispatchPoint(dispatchPoint.id())
        print(
            f"===场景中的dispatchPoint个数={len(dispatchPoints)}，第一个dispatchPoint的属性={dispatchPoint.id()}")
        print(f"该dispatchPoint的属性：id={dispatchPoint.id()}, dispatchPoint name ={dispatchPoint.name()}, "
              f"所在路段名称={dispatchPoint.link().name()}，polygon={dispatchPoint.polygon()},设置为仿真过程中可被动态修改={dispatchPoint.setDynaModified(True)}")

    def showDecisionPointAttr(self, netiface):
        decisionPoints = netiface.decisionPoints()
        decisionPoint = decisionPoints[0]
        decisionPoint1 = netiface.findDispatchPoint(decisionPoint.id())
        print(
            f"===场景中的decisionPoint个数={len(decisionPoints)}，第一个decisionPoint的属性={decisionPoint.id()}")
        print(f"该decisionPoint的属性：id={decisionPoint.id()}, dispatchPoint name ={decisionPoint.name()}, "
              f"所在路段名称={decisionPoint.link().name()}，距离路段起点距离distance(像素制)={decisionPoint.distance()}, 米制={decisionPoint.distance(UnitOfMeasure.Metric)},"
              f"控制的决策路径id={[(route.id(), [link.name() for link in route.getLinks()]) for route in decisionPoint.routings()]}"
              f"polygon={decisionPoint.polygon()},设置为仿真过程中可被动态修改={decisionPoint.setDynaModified(True)}")

    def showRoutingAttr(self, netiface):
        decisionPoints = netiface.decisionPoints()
        decisionPoint = decisionPoints[0]
        routes = decisionPoint.routings()
        route =routes[0]
        route1 = netiface.findRouting(route.id())

        print(f"该route的属性：id={route.id()},"
              f"route 长度 calcuLength (像素制)={route.calcuLength()}, 米制={route.calcuLength(UnitOfMeasure.Metric)},"
              f"所属决策点id={ route.deciPointId()}, 路径上的links={[link.name() for link in route.getLinks()]}")
        links = route.getLinks()
        print(f"判断link是否在路径上={route.contain(links[0])}")
        print(f"获取路径上，指定link的下游道路，返回值可能是link也可能是connector={route.nextRoad(links[0])}")
        links1 = netiface.links()
        print(f"判断link是否在路径上={route.contain(links1[0])}")
        if route.contain(links1[0]):
            print(f"获取路径上，指定link的下游道路，返回值可能是link也可能是connector={route.nextRoad(links1[0])}")

    def showPedestrianAttr(self,simuiface):
        allPedestrian = simuiface.allPedestrianStarted()
        if len(allPedestrian) > 0:
            ped = allPedestrian[0]
            print(
                f"获取行人ID={ped.getId()},"
                f"获取行人半径大小， 单位：米={ped.getRadius()},"
                f"获取行人质量， 单位：千克={ped.getWeight()},"
                f"获取行人颜色， 十六进制颜色代码，如#EE0000={ped.getColor()},"
                f"设置面域颜色={ped.setRegionColor(QColor('red'))},"
                f"获取行人当前位置（瞬时位置），像素坐标系下的坐标点，单位：米; ={ped.getPos()},"
                f"获取行人当前角度，QT像素坐标系下，X轴正方向为0，逆时针为正，单位：度; ={ped.getAngle()},"
                f"获取行人当前方向向量，二维向量；={ped.getDirection()},"
                f"获取行人当前位置的高程，单位：米={ped.getElevation()},"
                f"获取行人当前速度，单位：米/秒={ped.getSpeed()},"
                f"获取行人期望速度，单位：米/秒={ped.getDesiredSpeed()},"
                f"获取行人最大速度限制，单位：米/秒={ped.getMaxSpeed()},"
                f"获取行人当前加速度，单位：米/秒²={ped.getAcce()},"
                f"获取行人最大加速度限制，单位：米/秒²={ped.getMaxAcce()},"
                f"获取行人欧拉角，用于三维的信息展示和计算，单位：度={ped.getEuler()},"
                f"获取行人速度欧拉角，用于三维的信息展示和计算，单位：度={ped.getSpeedEuler()},"
                f"获取墙壁方向单位向量={ped.getWallFDirection()},"
                f"获取行人当前所在面域={ped.getRegion()},"
                f"获取行人类型ID={ped.getPedestrianTypeId()},"
                f"停止当前行人仿真运动，会在下一个仿真批次移除当前行人，释放资源={ped.stop()}")

    def showPedestrianCrossWalkRegionAttr(self, netiface):
        allCrossWalkRegion = netiface.pedestrianCrossWalkRegions()
        if len(allCrossWalkRegion) > 0:
            crossWalkRegion = allCrossWalkRegion[0]
            print(
                  f"获取面域ID={crossWalkRegion.getId()},"
                  f"获取面域名称={crossWalkRegion.getName()},"
                  f"设置面域名称={crossWalkRegion.setName('test_area')}," 
                  f"获取面域颜色={crossWalkRegion.getRegionColor()},"
                  f"设置面域颜色={crossWalkRegion.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={crossWalkRegion.getPosition()},"
                  f"获取面域位置,米制={crossWalkRegion.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={crossWalkRegion.setPosition(crossWalkRegion.getPosition())},"
                  f"设置面域位置，米制={crossWalkRegion.setPosition(crossWalkRegion.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={crossWalkRegion.getGType()},"
                  f"获取期望速度系数={crossWalkRegion.getExpectSpeedFactor()},"
                  f"设置期望速度系数={crossWalkRegion.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={crossWalkRegion.getElevation() },"
                  f"设置面域高程={crossWalkRegion.setElevation(0.1)},"
                  f"获取面域多边形={crossWalkRegion.getPolygon()}," 
                  f"获取面域所在图层ID={crossWalkRegion.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={crossWalkRegion.setLayerId(crossWalkRegion.getLayerId())}")
            print(f"仿真路网中人行横道区域总数={len(allCrossWalkRegion)},"
                  f"获取人行横道宽度，单位：米={crossWalkRegion.getWidth()},设置人行横道宽度，单位：米={crossWalkRegion.setWidth(crossWalkRegion.getWidth()+0.1)},"
                  f"获取人行横道起点到终点的线段，场景坐标系下={crossWalkRegion.getSceneLine()},获取人行横道倾斜角度={crossWalkRegion.getAngle()},"
                  f"设置人行横道倾斜角度={crossWalkRegion.setAngle(5)}, 获取红灯清尾速度系数={crossWalkRegion.getRedLightSpeedFactor()},"
                  f"设置红灯清尾速度系数={crossWalkRegion.setRedLightSpeedFactor(1.5)},"
                  f"获取场景坐标系下从起点到终点的单位方向向量={crossWalkRegion.getUnitDirectionFromStartToEnd()},"
                  f"获取人行横道本身坐标系下从起点到终点的单位方向={crossWalkRegion.getLocalUnitDirectionFromStartToEnd()},"
                  f"获取起点控制点={crossWalkRegion.getStartControlPoint()},"
                  f"获取终点控制点={crossWalkRegion.getEndControlPoint()},"
                  f"获取左侧控制点={crossWalkRegion.getLeftControlPoint()},"
                  f"获取右侧控制点={crossWalkRegion.getRightControlPoint()},"
                  f"判断是否添加了管控正向通行的信号灯={crossWalkRegion.isPositiveTrafficLightAdded()},"
                  f"判断是否添加了管控反向通行的信号灯={crossWalkRegion.isReverseTrafficLightAdded()},"
                  f"获取管控正向通行的信号灯={crossWalkRegion.getPositiveDirectionSignalLamp()},"
                  f"获取管控反向通行的信号灯={crossWalkRegion.getNegativeDirectionSignalLamp()},")

    def showPedestrianEllipseRegionAttr(self, netiface):
        areas = netiface.pedestrianEllipseRegions()
        if len(areas) > 0:
            r = areas[0]
            print(
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')}," 
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r. getElevation() },"
                  f"设置面域高程={r. setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()}," 
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())},"
                  f"获取面域是否为障碍物={r.isObstacle()},获取面域是否为上客区域={r.isBoardingArea()},"
                  f"获取面域是否为下客区域={r.isAlightingArea()}" 
                  f"仿真路网中pedestrianEllipseRegions总数={len(areas)}")

    def showPedestrianTriangleRegionAttr(self, netiface):
        areas = netiface.pedestrianTriangleRegions()
        if len(areas) > 0:
            r = areas[0]
            print(
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')}," 
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r. getElevation() },"
                  f"设置面域高程={r. setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()}," 
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())},"
                  f"获取面域是否为障碍物={r.isObstacle()},获取面域是否为上客区域={r.isBoardingArea()},"
                  f"获取面域是否为下客区域={r.isAlightingArea()}" 
                  f"仿真路网中pedestrianTriangleRegions总数={len(areas)}")

    def showPedestrianFanShapRegionAttr(self, netiface):
        areas = netiface.pedestrianFanShapeRegions()
        if len(areas) > 0:
            r = areas[0]
            print(
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')}," 
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r. getElevation() },"
                  f"设置面域高程={r. setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()}," 
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")

            print(f"仿真路网中人行横道区域总数={len(areas)},"
                  f"获取内半径，单位：米={r.getInnerRadius()},获取外半径，单位：米={r.getOuterRadius()},"
                  f"获取起始角度，单位：度={r.getStartAngle()},获取扫过角度，单位：度={r.getSweepAngle()},")

    def showPedestrianRectRegionAttr(self, netiface):
        areas = netiface.pedestrianRectRegions()
        if len(areas) > 0:
            r = areas[0]
            print(
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')}," 
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r.getElevation() },"
                  f"设置面域高程={r.setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()}," 
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())},"
                  f"获取面域是否为障碍物={r.isObstacle()},获取面域是否为上客区域={r.isBoardingArea()},"
                  f"获取面域是否为下客区域={r.isAlightingArea()}" 
                  f"仿真路网中pedestrianTriangleRegions总数={len(areas)}")

    def showPedestrianPolygonRegionAttr(self, netiface):
        areas = netiface.pedestrianPolygonRegions()
        if len(areas) > 0:
            r = areas[0]
            print(
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')}," 
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r.getElevation() },"
                  f"设置面域高程={r.setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()}," 
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())},"
                  f"获取面域是否为障碍物={r.isObstacle()},获取面域是否为上客区域={r.isBoardingArea()},"
                  f"获取面域是否为下客区域={r.isAlightingArea()}" 
                  f"仿真路网中pedestrianTriangleRegions总数={len(areas)}")

    def showPedestrianRegionAttr(self, netiface):
        """
        所有类型的行人面域的公共属性，特殊类型的私有属性可以进一步根据面域类型获取指定类型后获取
        Args:
            netiface:

        Returns:

        """
        allRegion = netiface.pedestrianRegions()
        if len(allRegion) > 0:
            r = allRegion[0]


            print(f"仿真路网中各种人行面域的总数 test={len(allRegion)},"
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')},"
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r. getElevation() },"
                  f"设置面域高程={r. setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()},"
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")


    def showPedestrianSideWalkRegionAttr(self, netiface):
        regions = netiface.pedestrianSideWalkRegions()
        if len(regions) > 0:
            r = regions[0]
            print(
                  f"获取面域ID={r.getId()},"
                  f"获取面域名称={r.getName()},"
                  f"设置面域名称={r.setName('test_area')}," 
                  f"获取面域颜色={r.getRegionColor()},"
                  f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                  f"获取面域位置，默认单位：像素={r.getPosition()},"
                  f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                  f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                  f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                  f"获取面域类型={r.getGType()},"
                  f"获取期望速度系数={r.getExpectSpeedFactor()},"
                  f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
                  f"获取面域高程={r. getElevation() },"
                  f"设置面域高程={r. setElevation(0.1)},"
                  f"获取面域多边形={r.getPolygon()}," 
                  f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")
            print(f"仿真路网中人行道区域总数={len(regions)},"
                  f"获取人行道宽度={r.getWidth()},设置人行道宽度={r.setWidth(r.getWidth()+0.5)},"
                  f"获取人行道顶点，即初始折线顶点={r.getVetexs()},获取人行道贝塞尔曲线控制点P1={r.getControl1Vetexs()},"
                  f"获取人行道贝塞尔曲线控制点P2={r.getControl2Vetexs()}, 获取候选顶点={r.getCandidateVetexs()}")
            print(f"在第index个位置插入顶点，初始位置为pos={r.insertVetex(QPointF(r.getCandidateVetexs()[0].pos().x()+0.1, r.getCandidateVetexs()[0].pos().y()+0.1), 0)},"
                  f"删除第index个顶点={r.removeVetex(1)}")
            #
            print(f"在第index个位置插入顶点，初始位置为pos={r.insertVetex(QPointF(100,100), 0)},"
                  f"删除第index个顶点={r.removeVetex(1)}")

    def showPedestrianStairRegionAttr(self, netiface):
        stairRegions = netiface.pedestrianStairRegions()
        if len(stairRegions) > 0:
            r = stairRegions[0]
            print(
                f"获取面域ID={r.getId()},"
                f"获取面域名称={r.getName()},"
                f"设置面域名称={r.setName('test_area')},"
                f"获取面域颜色={r.getRegionColor()},"
                f"设置面域颜色={r.setRegionColor(QColor('red'))},"
                f"获取面域位置，默认单位：像素={r.getPosition()},"
                f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
                f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
                f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
                f"获取面域类型={r.getGType()},")

            print(f"仿真路网中楼梯区域总数={len(stairRegions)},"
                  f"获取楼梯宽度，单位：米={r.getWidth()},设置楼梯宽度，单位：米度={r.setWidth(r.getWidth()+0.2)},"
                  f"获取起始点，场景坐标系下={r.getStartPoint()},获取终止点，场景坐标系下={r.getEndPoint()},"
                  f"获取起始衔接区域长度，单位：米={r.getStartConnectionAreaLength()}, 获取终止衔接区域长度，单位：米={r.getEndConnectionAreaLength()},"
                  f"获取起始衔接区域中心，场景坐标系下={r.getStartRegionCenterPoint()},获取终止衔接区域中心，场景坐标系下={r.getEndRegionCenterPoint()},"
                  f"获取起始衔接区域形状，场景坐标系下={r.getStartSceneRegion()},获取终止衔接区域形状，场景坐标系下={r.getEndSceneRegion()},"
                  f"获取楼梯主体形状，场景坐标系下={r.getMainQueueRegion()}, 获取楼梯整体形状，场景坐标系下={r.getFullQueueregion()},"
                  f"获取楼梯主体多边形，场景坐标系下={r.getMainQueuePolygon()} "
                  f"获取楼梯类型={r.getStairType()},设置楼梯类型={r.setStairType(r.getStairType())},"
                  f"获取起始层级={r.getStartLayerId()},设置起始层级={r.setStartLayerId(r.getStartLayerId())},"
                  f"获取终止层级={r.getEndLayerId()}, 设置终止层级={r.setEndLayerId(r.getEndLayerId())},获取传送速度，单位：米/秒={r.getTransmissionSpeed()},"
                  f"设置传送速度，单位：米/秒={r.setTransmissionSpeed(r.getTransmissionSpeed())},"
                  f"获取楼梯净高={r.getHeadroom()},设置楼梯净高={r.setHeadroom(r.getHeadroom())},获取起点控制点={r.getStartControlPoint()},"
                  f"获取终点控制点={r.getEndControlPoint()}, 获取左侧控制点={r.getLeftControlPoint()},获取右侧控制点={r.getRightControlPoint()},"
                  f"获取起始衔接区域长度控制点={r.getStartConnectionAreaControlPoint() },获取终止衔接区域长度控制点={r.getEndConnectionAreaControlPoint()}")

    def showPedestrianCrosswalkSignalLampAttr(self, netiface):
        signalLamps = netiface.crosswalkSignalLamps()
        if len(signalLamps) > 0:
            r = signalLamps[0]
            print(f"仿真路网中行人信号灯总数={len(signalLamps)},"
                  f"获取所属人行横道={r.getICrossWalk()}")

    def showPedestrianPathAttr(self, netiface):
        paths = netiface.pedestrianPaths()
        if len(paths) > 0:
            path = paths[0]
            print(f"仿真路网中行人路径总数={len(paths)},"
                  f"获取行人路径起始点={path.getPathStartPoint()},获取行人路径终点={path.getPathEndPoint()},"
                  f"获取行人路径中间点={path.getPathMiddlePoints()},判断是否是局部路径={path.isLocalPath()},")

    def showPedestrianPathPointAttr(self, netiface):
        paths = netiface.pedestrianPaths()
        if len(paths) > 0:
            path = paths[0]
            sp = path.getPathStartPoint()
            if sp is not None:
                print(f"获取行人路径点ID={sp.getId()},获取行人路径点场景坐标系下的位置,单位：像素={sp.getScenePos()},"
                      f"获取行人路径点场景坐标系下的位置,单位：像素={sp.getScenePos(UnitOfMeasure.Metric)},获取行人路径点的半径，单位：米={sp.getRadius()},")

    def showBusLineAttr(self,netiface):
        busLines = netiface.buslines()
        if len(busLines) > 0:
            path = busLines[0]
            path2 = netiface.findBusline(path.id())
            path3 = netiface.findBuslineByFirstLinkId(path.links()[0].id())
            print(f"获取当前公交线路的ID={path.id()},获取当前公交线路的名称={path.name()},"
                  f"获取当前公交线路长度，单位：像素={path.length()},单位：米={path.length(UnitOfMeasure.Metric)},"
                  f"获取当前公交线路的发车间隔，单位：秒={path.dispatchFreq()},获取当前公交线路的发车开始时间，单位：秒={path.dispatchStartTime()},"
                  f"获取当前公交线路的发车结束时间，单位：秒， 即当前线路的公交调度表的结束时刻={path.dispatchEndTime()},"
                  f"获取当前公交线路的期望速度，像素制：={path.desirSpeed()}, 米制km/h={path.desirSpeed(UnitOfMeasure.Metric)},"
                  f"公交线路中公交车的起始载客人数={path.passCountAtStartTime()},获取公交线路经过的路段序列={path.links()},"
                  f"获取公交线路上的所有站点={path.stations()}, 公交站点线路，当前线路相关站点的上下客等参数 ， 所有参数的列表={path.stationLines()},"
                  f"设置当前公交线路的名称={path.setName('new name')},设置当前公交线路的发车间隔，单位：秒={path.setDispatchFreq(20)},"
                  f"设置当前公交线路上的公交首班车辆的开始发车时间={path.setDispatchStartTime(0)},设置当前公交线路上的公交末班车的发车时间={path.setDispatchEndTime(300)},"
                  f"设置当前公交线路的期望速度,默认输入是像素，可通过unit参数设置单位={path.setDesirSpeed(40, UnitOfMeasure.Metric)},"
                  f"设置当前公交线路的起始载客人数={path.setPassCountAtStartTime(60)}")

    def showBusStationAttr(self, netiface):
        busStations = netiface.busStations()
        if len(busStations) > 0:
            busStation = busStations[0]
            busStation1 = netiface.findBusStation(busStation.id())
            print(f"获取当前公交站点ID={busStation.id()},获取当前公交线路名称={busStation.name()},"
                  f"获取当前公交站点所在车道序号={busStation.laneNumber()},"
                  f"获取当前公交站点的中心点的位置， X坐标 像素制={busStation.x()},米制={busStation.x()},"
                  f"获取当前公交站点的中心点的位置， y坐标 像素制={busStation.y()},米制={busStation.y()},"
                  f"获取当前公交站点的长度，，像素制：={busStation.length()}, 米制km/h={busStation.length(UnitOfMeasure.Metric)},"
                  f"获取当前公交站点所在路段={busStation.link()},"
                  f"获取当前公交站点所在车道={busStation.lane()}, 设置站点名称={busStation.setName('new trans station')},"
                  f"获取当前公交站点的起始位置距路段起点的距离，像素制：={busStation.distance()}, 米制km/h={busStation.distance(UnitOfMeasure.Metric)},"
                  f"设置站点起始点距车道起点距离，像素制：={busStation.setDistToStart(busStation.distance()+1)}, 米制km/h={busStation.setDistToStart(busStation.distance(UnitOfMeasure.Metric)+1,UnitOfMeasure.Metric)},"
                  f"设置站点长度，像素制：={busStation.setLength(busStation.length()+1)}, 米制km/h={busStation.setLength(busStation.length(UnitOfMeasure.Metric)+1,UnitOfMeasure.Metric)},"
                  f"设置当前公交站点类型={busStation.setType(2)}, {busStation.setType(1)},获取 公交站点多边型轮廓的顶点={busStation.polygon()} "
                  )

    def showBusStationLineAttr(self, netiface):
        busStations = netiface.busStations()
        if len(busStations) > 0:
            busStation = busStations[0]
            busStationLines = netiface.findBusStationLineByStationId(busStation.id())
            if len(busStationLines)>0:
                busStationLine = busStationLines[0]
                print(f"获取公交“站点-线路”ID={busStationLine.id()},获取当前公交站点的ID={busStationLine.stationId()},"
                      f"获取当前公交站台所属的公交线路ID={busStationLine.lineId()},"
                      f"获取当前公交线路下该站台的公交车辆停靠时间(秒)={busStationLine.busParkingTime()},"
                      f"获取当前公交线路下该站台的下客百分比={busStationLine.getOutPercent()},"
                      f"获取当前公交线路下该站台下的平均每位乘客上车时间，单位：秒={busStationLine.getOnTimePerPerson()},"
                      f"获取当前公交线路下该站台下的平均每位乘客下车时间，单位：秒={busStationLine.getOutTimePerPerson()},"
                      f"设置当前公交线路下该站台下的车辆停靠时间(秒)={busStationLine.setBusParkingTime(20)}, "
                      f"设置当前公交线路下的该站台的下客百分比={busStationLine.setGetOutPercent(0.60)},"
                      f"设置当前公交线路下的该站台的平均每位乘客上车时间={busStationLine.setGetOnTimePerPerson(2.0)},"
                      f"设置当前公交线路下的该站台的平均每位乘客下车时间：={busStationLine.setGetOutTimePerPerson(1.0)}"
                      )

    def showGuidArrowAttr(self, netiface):
        guidArrow = netiface.findGuidArrow(netiface.guidArrows()[0].id())
        print(f"导向箭头数={netiface.guidArrowCount()},导向箭头集={netiface.guidArrows()},")
        print(f"导向箭头ID={guidArrow.id()},"
              f"获取导向箭头所在的车道={guidArrow.lane()},"
              f"获取导向箭头长度，像素制={guidArrow.length()}， 米制={guidArrow.length(UnitOfMeasure.Metric)},"
              f"获取导向箭头到终点距离，像素制， 米制={guidArrow.distToTerminal()}, 米制={guidArrow.distToTerminal(UnitOfMeasure.Metric)},"
              f"获取导向箭头的类型，导向箭头的类型分为：直行、左转、右转、直行或左转、直行或右转、"
              f"直行左转或右转、左转或右转、掉头、直行或掉头和左转或掉头={guidArrow.arrowType()},"
              f"获取导向箭头的多边型轮廓的顶点={guidArrow.polygon()}")

    def showAccidentZoneAttr(self, netiface):
        acczones = netiface.accidentZones()
        acczone = netiface.findAccidentZone(acczones[0].id())
        print(f"获取事故区ID={acczone.id()},获取事故区名称={acczone.name()},"
              f"获取事故区当前时段距所在路段起点的距离,像素制={acczone.location()},米制={acczone.location(UnitOfMeasure.Metric)},"
              f"获取事故区当前时段长度,像素制={acczone.zoneLength()},米制={acczone.zoneLength(UnitOfMeasure.Metric)},"
              f"获取事故区当前时段限速,像素制 km/h={acczone.limitedSpeed()},米制={acczone.limitedSpeed(UnitOfMeasure.Metric)},"
              f"获取事故区所在的路段或连接段={acczone.section()},获取事故区所在路段的ID={acczone.roadId()},"
              f"获取事故区所在的道路类型(路段或连接段)={acczone.roadType()}，"
              f"获取事故区当前时段占用的车道列表={acczone.laneObjects()}, "
              f"获取事故区当前时段控制距离（车辆距离事故区起点该距离内，强制变道, 像素制={acczone.controlLength()}, 米制={acczone.controlLength(UnitOfMeasure.Metric)},"
              )
        print("添加事故时段")

        accidentZoneIntervals=acczone.accidentZoneIntervals()
        param = accidentZoneIntervals[-1]
        accidentZoneIntervalParam = Online.DynaAccidentZoneIntervalParam()
        accidentZoneIntervalParam.accidentZoneId = param.accidentZoneId()
        accidentZoneIntervalParam.startTime =param.endTime()
        accidentZoneIntervalParam.endTime = param.endTime()+300
        accidentZoneIntervalParam.length = param.length()
        accidentZoneIntervalParam.location = param.location()
        accidentZoneIntervalParam.limitedSpeed = param.limitedSpeed()
        accidentZoneIntervalParam.controlLength = param.controlLength()
        laneNumbers =  [lane.number() for lane in netiface.findLink(acczone.roadId()).lanes()]
        accidentZoneIntervalParam.mlFromLaneNumber = list(set(laneNumbers) - set( param.laneNumbers()))

        accidentZoneInterval = acczone.addAccidentZoneInterval(accidentZoneIntervalParam)

        accidentZoneIntervalParam1 = accidentZoneIntervalParam
        accidentZoneIntervalParam1.startTime =accidentZoneIntervalParam1.endTime
        accidentZoneIntervalParam1.endTime = accidentZoneIntervalParam1.endTime+300
        accidentZoneInterval1 = acczone.addAccidentZoneInterval(accidentZoneIntervalParam)

        acczone.removeAccidentZoneInterval(accidentZoneInterval1.intervalId())
        accidentZoneIntervalParam.controlLength = param.controlLength() + 10
        acczone.updateAccidentZoneInterval(accidentZoneIntervalParam)
        print(f"获取所有事故时段={acczone.accidentZoneIntervals()},"
              # f"根据ID查询事故时段={acczone.findAccidentZoneIntervalById(accidentZoneInterval.id())},"
              f"根据开始时间查询事故时段={acczone.findAccidentZoneIntervalByStartTime(accidentZoneInterval.startTime())}")


        self._showAccidentZoneIntervalAttr(acczone)

    def _showAccidentZoneIntervalAttr(self, acczone):
        interval = acczone.accidentZoneIntervals()[0]
        print(
            # f"获取事故时段ID={interval.id()},"
              f"获取所属事故区ID={interval.accidentZoneId()},"
              f"获取事故时段开始时间={interval.startTime()},获取事故时段结束时间={interval.endTime()},"
              f"获取事故区在该时段的长度,像素制={interval.location()},米制={interval.location(UnitOfMeasure.Metric)},"
              f"获取事故区在该时段的限速，像素制 km/h={interval.limitedSpeed()},米制={interval.limitedSpeed(UnitOfMeasure.Metric)},"
              f"获取事故区在该时段的控制距离（车辆距离事故区起点该距离内，强制变道），像素制 km/h={interval.controlLength()},"
              f"米制={interval.controlLength(UnitOfMeasure.Metric)},"
              f"获取事故区在该时段的占用车道序号={interval.laneNumbers()}")

    def showRoadWorkZoneAttr(self, netiface):
        roadworkzones = netiface.roadWorkZones()
        roadworkzone = roadworkzones[0]

        print(f"roadWorkZones={roadworkzone.id()},获取施工区名称={roadworkzone.name()}"
              f",获取施工区距所在路段起点的距离,像素制={roadworkzone.location()}, 米制={roadworkzone.location(UnitOfMeasure.Metric)},"
              f"获取施工区长度,像素制={roadworkzone.zoneLength()},米制={roadworkzone.zoneLength(UnitOfMeasure.Metric)},"
              f"获取施工区限速，像素制={roadworkzone.limitSpeed()}, 米制={roadworkzone.limitSpeed(UnitOfMeasure.Metric)},"
              f"获取施工区所在路段或连接段的ID={roadworkzone.sectionId()},"
              f"获取施工区所在路段或连接段的名称={roadworkzone.sectionName()},"
              f"获取施工区所在道路的道路类型，link:路段, connector:连接段={roadworkzone.sectionType()},"
              f"获取施工区所占的车道列表={roadworkzone.laneObjects()},"
              f"获取施工区所占的车道ID列表={roadworkzone.laneObjectIds()},"
              f"获取施工区上游警示区长度,像素制={roadworkzone.upCautionLength()}， 米制={roadworkzone.upCautionLength(UnitOfMeasure.Metric)},"
              f"获取施工区上游过渡区长度,像素制={roadworkzone.upTransitionLength()}， 米制={roadworkzone.upTransitionLength(UnitOfMeasure.Metric)},"
              f"获取施工区上游缓冲区长度,像素制={roadworkzone.upBufferLength()},米制={roadworkzone.upBufferLength(UnitOfMeasure.Metric)},"
              f"获取施工区下游过渡区长度，,像素制={roadworkzone.downTransitionLength()},米制={roadworkzone.downTransitionLength(UnitOfMeasure.Metric)},"
              f"施工持续时间，单位：秒。自仿真过程创建后，持续时间大于此值，则移除={roadworkzone.duration()},"
              f"获取施工区是否被借道={roadworkzone.isBorrowed()}")

    def showLimitZoneAttr(self, netiface):
        limitedZones = netiface.limitedZones()
        limitedZone = limitedZones[0]
        limitedZone1 = netiface.findLimitedZone(limitedZone.id())
        # print(type(limitedZone), dir(limitedZone))
        print(f"获取限行区ID={limitedZone.id()},获取限行区名称={limitedZone.name()}"
              f",获取限行区距所在路段起点的距离,像素制={limitedZone.location()}, 米制={limitedZone.location(UnitOfMeasure.Metric)},"
              f"获取限行区长度,像素制={limitedZone.zoneLength()},米制={limitedZone.zoneLength(UnitOfMeasure.Metric)},"
              f"获取限行区限速，像素制={limitedZone.limitSpeed()}, 米制={limitedZone.limitSpeed(UnitOfMeasure.Metric)},"
              f"获取路段或连接段ID={limitedZone.sectionId()},"
              f"获取Section名称={limitedZone.sectionName()},"
              f"获取道路类型，link表示路段，connector表示连接段={limitedZone.sectionType()},"
              f"获取相关车道对象列表={limitedZone.laneObjects()},"
              f"获取限行持续时间，单位：秒。自仿真过程创建后，持续时间大于此值则删除={limitedZone.duration()},")

    def showReconstructionAttr(self, netiface):
        reconstructions = netiface.reconstructions()
        if len(reconstructions) > 0:
            reconstruction = reconstructions[0]
            reconstruction1 = netiface.findReconstruction(reconstruction.id())

            print(f"获取改扩建ID={reconstruction.id()},获取改扩建起始施工区ID={reconstruction.roadWorkZoneId()}"
                  f",获取被借道限行区ID={reconstruction.limitedZoneId()},"
                  f"获取保通长度,像素制={reconstruction.passagewayLength()},米制={reconstruction.passagewayLength(UnitOfMeasure.Metric)},"
                  f"获取保通开口限速，像素制={reconstruction.passagewayLimitedSpeed()}, 米制={reconstruction.passagewayLimitedSpeed(UnitOfMeasure.Metric)},"
                  f"获取改扩建持续时间={reconstruction.duration()},"
                  f"获取借道数量={reconstruction.borrowedNum()},"
                  f"获取改扩建动态参数，返回参数为米制={reconstruction.dynaReconstructionParam()},")

    def showReduceSpeedAreaAttr(self, netiface):
        reduceSpeedAreas = netiface.reduceSpeedAreas()
        if len(reduceSpeedAreas) > 0:
            reduceSpeedArea = reduceSpeedAreas[0]
            reduceSpeedArea1 = netiface.findReduceSpeedArea(reduceSpeedAreas[0].id())

            print(f"获取限速区ID={reduceSpeedArea.id()},获取限速区名称={reduceSpeedArea.name()}"
                  f",获取距起点距离，={reduceSpeedArea.location()},米制={reduceSpeedArea.location(UnitOfMeasure.Metric)}"
                  f"获取限速区长度，像素制={reduceSpeedArea.areaLength()},米制={reduceSpeedArea.areaLength(UnitOfMeasure.Metric)},"
                  f"获取路段或连接段ID={reduceSpeedArea.sectionId()},"
                  f"获取车道序号={reduceSpeedArea.laneNumber()},"
                  f"获取目标车道序号={reduceSpeedArea.toLaneNumber()},"
                  f"获取限速时段列表={reduceSpeedArea.reduceSpeedIntervals()},"
                  f"获取多边型轮廓={reduceSpeedArea.polygon()}")
            print("添加限速时段时段")
            print(f"获取所有限速时段={reduceSpeedArea.reduceSpeedIntervals()},"
                  f"根据ID获取限速时段 ={reduceSpeedArea.findReduceSpeedIntervalById(reduceSpeedArea.reduceSpeedIntervals()[0].id())},"
                  f"根据开始时间查询事故时段={reduceSpeedArea.findReduceSpeedIntervalByStartTime(reduceSpeedArea.reduceSpeedIntervals()[0].intervalStartTime())}")


            param = Online.DynaReduceSpeedIntervalParam()
            param.startTime = 100 # 需要注意新增的时段要和已有时段不冲突
            param.endTime = 500
            type1 = Online.DynaReduceSpeedVehiTypeParam()
            type1.vehicleTypeCode = 2
            type1.avgSpeed = 10
            type1.speedSD = 5
            param.mlReduceSpeedVehicleTypeParam = [type1, ]
            interval = reduceSpeedArea.addReduceSpeedInterval(param)
            reduceSpeedArea.removeReduceSpeedInterval(interval.id())
            interval1 = reduceSpeedArea.addReduceSpeedInterval(param)
            print(f" reduceSpeedArea.addReduceSpeedInterval(param) 添加成功={interval1}")
            flag = reduceSpeedArea.updateReduceSpeedInterval(param)
            print(f" reduceSpeedArea.updateReduceSpeedInterval(param) 更新成功={flag}")

            self._showReduceSpeedAreaIntervalAttr(reduceSpeedArea)

    def _showReduceSpeedAreaIntervalAttr(self, reduceSpeedArea):
        interval = reduceSpeedArea.reduceSpeedIntervals()[0]
        print(f"获取限速时段ID={interval.id()},获取所属限速区ID={interval.reduceSpeedAreaId()},"
              f"获取开始时间={interval.intervalStartTime()},获取结束时间={interval.intervalEndTime()},")

        reduceSpeedVehiTypes = interval.reduceSpeedVehiTypes()
        reduceSpeedVehiType = interval.findReduceSpeedVehiTypeById(reduceSpeedVehiTypes[0].id())

        reduceSpeedVehiType1 = interval.findReduceSpeedVehiTypeByCode(reduceSpeedVehiTypes[0].vehiTypeCode())


        print(f"获取限速车型ID={reduceSpeedVehiType.id()},"
              f"获取所属限速时段ID ={reduceSpeedVehiType.intervalId()},"
              f"获取所属限速区ID={reduceSpeedVehiType.reduceSpeedAreaId()},"
              f"获取车型编码={reduceSpeedVehiType.vehiTypeCode()},"
              f"获取平均车速，像素制={reduceSpeedVehiType.averageSpeed()}, 米制={reduceSpeedVehiType.averageSpeed(UnitOfMeasure.Metric)},"
              f"获取车速标准差，像素制={reduceSpeedVehiType.speedStandardDeviation()},米制={reduceSpeedVehiType.speedStandardDeviation(UnitOfMeasure.Metric)}")

        interval.removeReduceSpeedVehiType(reduceSpeedVehiType.id())

        print("新增限速车型限制。。。。 DynaReduceSpeedVehiTypeParam")
        param = Online.DynaReduceSpeedVehiTypeParam()
        param.vehicleTypeCode = 2
        param.avgSpeed = 20
        param.speedSD = 0
        param.reduceSpeedIntervalId = interval.id()
        param.reduceSpeedAreaId = reduceSpeedVehiType.reduceSpeedAreaId()


        interval.updateReduceSpeedVehiType(param)
        print(f"获取限速车型ID={reduceSpeedVehiType.id()},"
              f"获取所属限速时段ID ={reduceSpeedVehiType.intervalId()},"
              f"获取所属限速区ID={reduceSpeedVehiType.reduceSpeedAreaId()},"
              f"获取车型编码={reduceSpeedVehiType.vehiTypeCode()},"
              f"获取平均车速，像素制={reduceSpeedVehiType.averageSpeed()}, 米制={reduceSpeedVehiType.averageSpeed(UnitOfMeasure.Metric)},"
              f"获取车速标准差，像素制={reduceSpeedVehiType.speedStandardDeviation()},米制={reduceSpeedVehiType.speedStandardDeviation(UnitOfMeasure.Metric)}")


    def showTollLaneAttr(self, netiface):
        rs = netiface.tollLanes()
        r = rs[0]
        r1 = netiface.findTollLane(rs[0].id())
        print(f"获取收费车道ID={r.id()},获取收费车道名称={r.name()}"
              f",获取距路段起始位置，单位：米={r.distance()},"
              f"设置收费车道名称={r.setName('test lane toll')},"
              f"设置工作时间，工作时间与仿真时间对应={r.setWorkTime(0, 3000)},"
              f"获取动态收费车道信息={r.dynaTollLane()},"
              f"获取收费车道所有收费点={r.tollPoints()},")

    def showTollDecisionPointAttr(self, netiface):
        rs = netiface.tollDecisionPoints()
        r = rs[0]
        r1 = netiface.findDecisionPoint(rs[0].id())

        print(f"获取收费决策点ID={r.id()},获取收费决策点名称={r.name()},获取收费决策点所在路段={r.link()},"
              f",获取距路段起始位置，单位：米={r.distance()},"
              f"获取相关收费路径={r.routings()},"
              f"获取收费分配信息列表={r.tollDisInfoList()},"
              f"更新收费分配信息列表={r.updateTollDisInfoList(r.tollDisInfoList())}," 
              f"获取收费决策点多边型轮廓={r.polygon()},")

    def showTollRoutingAttr(self, netiface):
        tollDecisionPoints = netiface.tollDecisionPoints()
        tollDecisionPoint = tollDecisionPoints[0]
        routes = tollDecisionPoint.routings()
        r = routes[0]
        print(f"获取路径ID={r.id()},获取所属收费决策点ID={r.tollDeciPointId()},"
              f",计算路径长度，单位：米={r.calcuLength()},"
              f"根据所给道路判断是否在当前路径上={r.contain(tollDecisionPoint.link())},"
              f"根据所给道路求下一条道路={r.nextRoad(tollDecisionPoint.link())},"
              f"获取路段序列={r.getLinks()},")

    def showTollPointAttr(self, netiface):
        tollLanes = netiface.tollLanes()
        tollLane = tollLanes[0]
        rs= tollLane.tollPoints()
        r = rs[0]

        print(f"获取收费点ID={r.id()},获取所属收费车道ID={r.tollLaneId()}"
              f",获取距路段起始位置，单位：米={r.distance()},"
              f"获取是否启用={r.isEnabled()},"
              f"设置启用状态={r.setEnabled(True)},"
              f"获取收费类型={r.tollType()},设置收费类型={r.setTollType(r.tollType())},"
              f"获取停车时间分布ID={r.timeDisId()},设置停车时间分布ID={r.setTimeDisId(r.timeDisId())}")

    def showParkingStallAttr(self, netiface):
        parkingRegions = netiface.parkingRegions()
        parkingRegion = parkingRegions[0]
        rs = parkingRegion.parkingStalls()
        if len(rs) > 0:
            r = rs[0]
            print(f"获取停车位ID={r.id()},获取所属停车区域ID={r.parkingRegionId()},"
                  f"获取距路段起始位置，单位：米={r.distance()},"
                  f"获取车位类型，与车辆类型编码一致={r.stallType()},")

    def showParkingRegionAttr(self, netiface):
        parkingRegions = netiface.parkingRegions()
        parkingRegion = parkingRegions[0]
        r1 = netiface.findParkingRegion(parkingRegion.id())
        r = parkingRegion
        print(f"获取停车区域ID={r.id()},获取停车区域名称={r.name()}"
              f",获取所有停车位={r.parkingStalls()},"
              f"设置停车区域名称={r.setName('test parking name')},"
              f"获取动态停车区域信息={r.dynaParkingRegion()},")

    def showParkingDecisionPointAttr(self, netiface):
        parkingDecisionPoints = netiface.parkingDecisionPoints()
        parkingDecisionPoint = parkingDecisionPoints[0]
        r1 = netiface.findParkingDecisionPoint(parkingDecisionPoint.id())
        r = parkingDecisionPoint
        print(f"获取停车决策点ID={r.id()},获取停车决策点名称={r.name()}"
              f",获取停车决策点所在路段={r.link()},"
              f"获取停车决策点距路段起点距离，单位：米={r.distance()},获取停车分配信息列表={r.parkDisInfoList()},"
              f"获取相关停车路径={r.routings()},更新停车分配信息={r.updateParkDisInfo(r.parkDisInfoList())},"
              f"获取停车决策点多边型轮廓={r.polygon()}")

    def showParkingRoutingAttr(self, netiface):
        parkingDecisionPoints = netiface.parkingDecisionPoints()
        parkingDecisionPoint = parkingDecisionPoints[0]
        r1 = parkingDecisionPoint.routings()
        r = r1[0]
        print(f"获取路径ID={r.id()},获取所属决策点ID={r.parkingDeciPointId()}"
              f",计算路径长度={r.calcuLength()},"
              f"根据所给道路判断是否在当前路径上={r.contain(parkingDecisionPoint.link())},"
              f"根据所给道路求下一条道路={r.nextRoad(parkingDecisionPoint.link())},"
              f"获取路段序列={r.getLinks()}")




    def showSignalLampAttr(self, netiface):
        signalLampCount = netiface.signalLampCount()
        print(f"signalLampCount={signalLampCount}")
        signalLampIds = netiface.signalLampIds()
        print(f"signalLampIds={signalLampIds}")
        signalLamps = netiface.signalLamps()
        signalLamp = netiface.findSignalLamp(signalLampIds[0])

        print(
            f"机动车信号灯总数={signalLampCount},编号列表={signalLampIds},{signalLamp.id()}的具体信息："
            f"编号={signalLamp.id()},获取信号灯当前信号灯色={signalLamp.color()}, 名称={signalLamp.name()},"
            f"设置信号灯名称={signalLamp.setName('new_' + signalLamp.name())},"
            f"获取当前信号灯所在的相位={signalLamp.signalPhase()},获取当前信号灯所在的灯组={signalLamp.signalPlan()},"
            f"获取所在车道或车道连接={signalLamp.laneObject()}，获取信号灯多边型轮廓={signalLamp.polygon()}, "
            f"获取信号灯角度，正北为0顺时针方向={signalLamp.angle()}")

    def showCrossWalkSignalLampAttr(self, netiface):
        crosswalkSignalLamps = netiface.crosswalkSignalLamps()
        crosswalkSignalLamp = netiface.findCrosswalkSignalLamp(crosswalkSignalLamps[0].id())

        print(
            f"行人信号灯列表={crosswalkSignalLamps},行人信号灯{crosswalkSignalLamp.id()}的具体信息："
            f"编号={crosswalkSignalLamp.id()},获取信号灯当前信号灯色={crosswalkSignalLamp.color()}, 名称={crosswalkSignalLamp.name()},"
            f"设置信号灯名称={crosswalkSignalLamp.setName('new_' + crosswalkSignalLamp.name())},"
            f"获取当前信号灯所在的相位={crosswalkSignalLamp.signalPhase()},获取当前信号灯所在的灯组={crosswalkSignalLamp.signalPlan()},"
            f"获取所在车道或车道连接={crosswalkSignalLamp.getICrossWalk()}，获取信号灯多边型轮廓={crosswalkSignalLamp.polygon()}, "
            f"获取信号灯角度，正北为0顺时针方向={crosswalkSignalLamp.angle()}")





    def showSignalPhaseAttr(self, netiface):
        signalPlans = netiface.signalPlans()
        signalPlan = netiface.findSignalPlanById(signalPlans[0].id())
        print(f"signalPlan={dir(signalPlan)}")
        signalPhases = signalPlan.Iphases()
        signalPhase = signalPhases[0]
        print(
            f"信控方案={signalPlans[0].name()},的所有相位列表={signalPhases},第一相位={signalPhase.id()}的具体信息："
            f"编号={signalPhase.id()},名称={signalPhase.phaseName()}, 获取本相位下的信号灯列表={signalPhase.signalLamps()},"
            f"获取本相位的相位灯色列表={signalPhase.listColor()},"
            f"相位周期，单位：秒={signalPhase.cycleTime()},当前相位灯色，Online.SignalPhaseColor={signalPhase.phaseColor()},"
            f"所在信控方案={signalPhase.signalPlan()}")


    def showSignalPlanAttr(self, netiface):
        signalPlans = netiface.signalPlans()
        signalPlanCount = netiface.signalPlanCount()
        signalPlanIds = netiface.signalPlanIds()
        signalPlan = netiface.findSignalPlanById(signalPlanIds[0])
        print(f"signalPlan={dir(signalPlan)}")
        signalPlan = netiface.findSignalPlanByName(signalPlans[0].name())
        print(
            f"路网中的信控方案总数={signalPlanCount},所有信控方案列表={signalPlanIds},信控方案编号={signalPlanIds[0]}的具体信息："
            f"编号={signalPlan.id()},名称={signalPlan.name()}, 所属信号机名称={signalPlan.trafficName()},设置新名字={signalPlan.setName('new_' + signalPlan.name())},"
            f"获取信控方案信控周期={signalPlan.cycleTime()},开始时间-结束时间={signalPlan.fromTime()}-{signalPlan.toTime()},"
            f"所有相位信息={signalPlan.phases()}")



    def showTrafficControllerAttr(self, netiface):
        controllers = netiface.trafficControllers()
        controllerCount = netiface.trafficControllerCount()
        trafficControllerIds = netiface.trafficControllerIds()
        controller = netiface.findTrafficControllerById(trafficControllerIds[0])
        controller = netiface.findTrafficControllerByName(controllers[0].name())
        print(f"路网中的信号机总数={controllerCount},所有的信号机id列表={trafficControllerIds},信号机编号={trafficControllerIds[0]}的具体信息："
              f"编号={controller.id()},名称={controller.name()}, 设置新名字={controller.setName('new_'+controller.name())},"
              f"获取信号机的信控方案={controller.plans()}")
        plans = controller.plans()
        print(f"移除/删除信号机的信控方案={controller.removePlan(plans[0])}")
        print(f"为信号机添加信控方案,添加回原有信控方案={controller.addPlan(plans[0])}")
        print(f"信号机当前信控方案={controller.plans()}")


    def showVehicleDrivInfoCounter(self, netiface):
        collectors = netiface.vehiInfoCollectors()
        if len(collectors)>0:
            collector = netiface.findVehiInfoCollector(collectors[0].id())

            print(
                f"获取采集器ID={collector.id()},获取采集器名称={collector.collName()},"
                f"判断当前数据采集器是否在路段上，返回值为True表示检测器在路段上，返回值False则表示在connector上={collector.onLink()}"
                f"获取采集器所在的路段={collector.link()},获取采集器所在的连接段={collector.connector()},"
                f"如果采集器在路段上，则返回ILane对象，否则范围None={collector.lane()},如果采集器在连接段上，则返回laneConnector“车道连接”对象，否则返回None={collector.laneConnector()}"
                f" 获取采集器的工作起始时间,工作停止时间={collector.fromTime()}-{collector.toTime()}, 采集器所在点，像素坐标={collector.point()}"
                f"获取采集器距离路段|连接段起点的距离={collector.distToStart()}")

    def showVehicleQueueCounter(self, netiface):
        collectors = netiface.vehiQueueCounters()
        if len(collectors)>0:
            collector = netiface.findVehiQueueCounter(collectors[0].id())

            print(
                f"获取当前排队计数器ID={collector.id()},获取当前排队计数器名称={collector.counterName()},"
                f"判断当前数据采集器是否在路段上，返回值为True表示检测器在路段上，返回值False则表示在connector上={collector.onLink()},"
                f"获取当前排队计数器所在路段={collector.link()},获取当前排队计数器所在连接段={collector.connector()},"
                f"如果计数器在路段上则lane()返回所在车道，laneConnector()返回None={collector.lane()},"
                f"如果计数器在连接段上则laneConnector返回“车道连接”,lane()返回None={collector.laneConnector()}"
                f" 获取当前计数器工作起始时间,工作停止时间={collector.fromTime()}-{collector.toTime()}, "
                f"计数器所在点，像素坐标={collector.point()},计数集计数据时间间隔={collector.aggregateInterval()}"
                f"计数器距离起点距离，默认单位：像素 ={collector.distToStart()}")

    def showVehicleTravelDetector(self, netiface):
        collectors = netiface.vehiTravelDetectors()
        if len(collectors) > 0:
            collector = netiface.findVehiTravelDetector(collectors[0].id())

            print(
                f"获取检测器ID={collector.id()},获取检测器名称={collector.detectorName()},是否检测器起始点={collector.isStartDetector()},"
                f"判断当前数据采集器是否在路段上，返回值为True表示检测器在路段上，返回值False则表示在connector上={collector.onLink()},"
                f"检测器起点是否在路段上，如果否，则起点在连接段上={collector.isOnLink_startDetector()},"
                f"检测器终点是否在路段上，如果否，则终点在连接段上={collector.isOnLink_endDetector()},"
                f"如果检测器起点在路段上则link_startDetector()返回起点所在路段，laneConnector_startDetector()返回None={collector.link_startDetector()},"
                f"如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”,link_startDetector()返回None={collector.laneConnector_startDetector()}"
                f"如果检测器终点在路段上则link_endDetector()返回终点所在路段，laneConnector_endDetector()返回None={collector.link_endDetector()}, "
                f"如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”,link_endDetector()返回None={collector.laneConnector_endDetector()},"
                f"检测器起点距离所在车道起点或“车道连接”起点距离，默认单位：像素={collector.distance_startDetector()},"
                f"检测器终点距离所在车道起点或“车道连接”起点距离，默认单位：像素={collector.distance_endDetector()},"
                f"检测器起点位置,默认单位：像素，可通过可选参数：unit设置单位，={collector.point_startDetector()},"
                f"检测器终点位置,默认单位：像素，可通过可选参数：unit设置单位，  ={collector.point_endDetector()},"
                f"检测器工作起始时间，单位：秒={collector.fromTime()},检测器工作停止时间，单位：秒={collector.toTime()},"
                f"集计数据时间间隔，单位：秒={collector.aggregateInterval()},"
                f"获取行程时间检测器起始点多边型轮廓的顶点={collector.polygon_startDetector()},"
                f"获取行程时间检测器终止点多边型轮廓的顶点={collector.polygon_endDetector()}")


    def createJunctionNode(self,netiface):
        # # step1：创建节点
        x1 = -500
        y1 = 500
        x2 = 500
        y2 = -500
        junctionName = 'newJunction'
        netiface.createJunction(QPointF(m2p(x1), m2p(y1)), QPointF(m2p(x2), m2p(y2)), junctionName)

        # # step2：创建静态路径
        netiface.buildAndApplyPaths(3)    # 设置每个OD最多搜索3条路径
        netiface.reSetDeciPoint()    # 优化决策点位置
        for dp in netiface.decisionPoints():
            for routing in dp.routings():
                netiface.reSetLaneConnector(routing)    # 优化路径中的车道连接


        # # step3：为节点中每个转向设置流量
        # 由于没有已知值，这里针对不同转向类型为其赋流量初值
        turnVolumeReduct = {'左转': 400, '直行': 1200, '右转': 200, '掉头': 0}
        # # step3-1：添加流量时间段
        timeInterval = netiface.addFlowTimeInterval()
        timeId = timeInterval.timeId
        startTime = 0
        endTime = 3600
        netiface.updateFlowTimeInterval(timeId, startTime, endTime)
        # # step3-2：遍历转向，为其设置流量
        junctions = netiface.getAllJunctions()
        for junction in junctions:
            junctionId = junction.getId()
            for turning in junction.getAllTurnningInfo():
                turningId = turning.turningId
                turnType = turning.strTurnType
                inputVolume = turnVolumeReduct.get(turnType, 0)
                # 为该转向设置输入流量
                netiface.updateFlow(timeId, junctionId, turningId, inputVolume)


        # # step4：进行流量分配计算
        # 设置BPR路阻函数参数，流量分配算法参数
        theta = 0.1
        bpra = 0.15
        bprb = 4
        maxIterateNum = 300
        netiface.updateFlowAlgorithmParams(theta, bpra, bprb, maxIterateNum)    # 更新计算参数

        result = netiface.calculateFlows()    # 计算路径流量分配并应用，返回分配结果


        # 取流量分配结果
        resultJson = collections.defaultdict(list)
        for timeId, turningFlow in result.items():
            for i in turningFlow:
                junction = i.pJunction.getId()
                turning = f"{i.turningBaseInfo.strDirection}-{i.turningBaseInfo.strTurnType}"
                inputVolume = i.inputFlowValue    # 该转向输入流量
                realVolume = i.realFlow    # 该转向实际分配到的流量
                relativeError = i.relativeError    # 分配的相对误差
                interval = i.flowTimeInterval
                startTime = interval.startTime
                endTime = interval.endTime
                resultJson[f"{startTime}-{endTime}"].append({'junction': junction, 'turning': turning, 'inputVolume': inputVolume, 'realVolume': realVolume, 'relativeError': relativeError})
        print(f"result：{resultJson}")




    def showJunctionAttr(self,netiface):
        nodes = netiface.getAllJunctions()
        node = netiface.findJunction(nodes[0].getId())
        print(
            f"路网中的节点总数={len(nodes)},节点编号={nodes[0].getId()}的具体信息："
            f"获取节点ID={node.getId()},名称={node.name()}, 设置新名字={node.setName('new_' + node.name())},"
            f"获取节点内的路段={node.getJunctionLinks()},"
            f"获取节点内的连接段={node.getJunctionConnectors()},"
            f"获取节点内的流向信息， Online.Junction.TurnningBaseInfo 数据结构见 pyi文件={node.getAllTurnningInfo()},"
            f"获取节点内的流向信息={node.getAllTurnningInfo()[0]},"
            f"根据转向编号获取节点内的流向信息， Online.Junction.TurnningBaseInfo 数据结构见 pyi文件={node.getTurnningInfo(node.getAllTurnningInfo()[0].turningId)}")

    def test(self,netiface):
        startPoint = QPointF(-300, 6)
        endPoint = QPointF(-25, 6)
        lPoint = [startPoint, endPoint]
        w_approach = netiface.createLink(lPoint, 3, "西进口", True, UnitOfMeasure.Metric)

        w_approach1 = netiface.createLink(lPoint, 3, "西进口", True, UnitOfMeasure.Metric)
        netiface.moveLinks([w_approach1], QPointF(12, 0), UnitOfMeasure.Metric)
        # netiface.saveRoadNet()
        # self.showPedestrianRegionAttr(netiface)
        self.showPedestrianCrossWalkRegionAttr(netiface)
        self.showPedestrianSideWalkRegionAttr(netiface)



    def showTessObjAttr(self, netiface):
        # netiface.saveRoadNet()
        self.showRoadNetAttr(netiface)
        self.showSectionAttr(netiface)
        self.showLaneObjectAttr(netiface)
        self.showLinkAttr(netiface)
        self.showLaneAttr(netiface)
        self.showConnectorAttr(netiface)
        self.showLaneConnectorAttr(netiface)
        self.showConnectorAreaAttr(netiface)
        self.showDispatchPointAttr(netiface)
        self.showDecisionPointAttr(netiface)
        self.showRoutingAttr(netiface)
        self.showPedestrianCrossWalkRegionAttr(netiface)
        self.showPedestrianFanShapRegionAttr(netiface)
        self.showPedestrianTriangleRegionAttr(netiface)
        self.showPedestrianEllipseRegionAttr(netiface)
        self.showPedestrianRectRegionAttr(netiface)
        self.showPedestrianPolygonRegionAttr(netiface)
        self.showPedestrianPathAttr(netiface)
        self.showPedestrianPathPointAttr(netiface)
        self.showPedestrianRegionAttr(netiface)
        self.showPedestrianSideWalkRegionAttr(netiface)
        self.showPedestrianStairRegionAttr(netiface)
        self.showPedestrianCrosswalkSignalLampAttr(netiface)
        self.showBusLineAttr(netiface)
        self.showBusStationAttr(netiface)
        self.showBusStationLineAttr(netiface)
        self.showGuidArrowAttr(netiface)
        self.showAccidentZoneAttr(netiface)
        self.showRoadWorkZoneAttr(netiface)
        self.showLimitZoneAttr(netiface)
        self.showReconstructionAttr(netiface)
        self.showReduceSpeedAreaAttr(netiface)
        self.showTollLaneAttr(netiface)
        self.showTollDecisionPointAttr(netiface)
        self.showTollRoutingAttr(netiface)
        self.showTollPointAttr(netiface)
        self.showParkingStallAttr(netiface)
        self.showParkingRegionAttr(netiface)
        self.showParkingDecisionPointAttr(netiface)
        self.showParkingRoutingAttr(netiface)
        self.showSignalLampAttr(netiface)
        self.showCrossWalkSignalLampAttr(netiface)
        self.showSignalPhaseAttr(netiface)
        self.showSignalPlanAttr(netiface)
        self.showTrafficControllerAttr(netiface)
        # # 检测器
        self.showVehicleDrivInfoCounter(netiface)
        self.showVehicleQueueCounter(netiface)
        self.showVehicleTravelDetector(netiface)



    # 以下是simulation过程中的函数
    def pedestrianAttr(self,ped):
        print(f"行人id={ped.getId()},获取行人半径大小， 单位：米={ped.getRadius()},"
              f"获取行人质量， 单位：千克={ped.getWeight()}, 获取行人颜色， 十六进制颜色代码，如#EE0000={ped.getColor()},"
              f"获取行人当前位置（瞬时位置），像素坐标系下的坐标点，单位：米={ped.getPos()},"
              f"获取行人当前角度，QT像素坐标系下，X轴正方向为0，逆时针为正，单位：度;={ped.getAngle()},"
              f"获取行人当前方向向量，二维向量={ped.getDirection()}, 获取行人当前位置的高程，单位：米={ped.getElevation()},"
              f"获取行人当前速度，单位：米/秒={ped.getSpeed()}, 获取行人期望速度，单位：米/秒={ped.getDesiredSpeed()},"
              f"获取行人最大速度限制，单位：米/秒={ped.getMaxSpeed()},获取行人当前加速度，单位：米/秒²={ped.getAcce()},"
              f"获取行人最大加速度限制，单位：米/秒²={ped.getMaxAcce()},"
              f"获取行人欧拉角={ped.getEuler()}, 获取行人速度欧拉角={ped.getSpeedEuler()}，获取墙壁方向单位向量={ped. getWallFDirection()},"
              f"获取行人当前所在面域={ped.getRegion() }, 获取行人类型ID={ped.getPedestrianTypeId()},"
              f"停止仿真，会在下一个仿真批次移除当前行人，释放资源={ped.stop()}")

    def _create_toll_point(self, distance: float, toll_type: int = 1, time_dis_id: int = 1):
        """创建收费区域"""
        TollStation = Online.TollStation.DynaTollPoint()
        # 距离路段起点的距离，单位：m
        TollStation.location = distance
        # 收费类型：1=MTC，2=ETC，3=ETC&MTC
        TollStation.tollType = toll_type
        # 停车时间分布的ID
        TollStation.timeDisId = time_dis_id
        # 是否启用
        TollStation.enable = True
        return TollStation


    def _create_toll_lane(self,netiface,  link_id: int, location: float, length: float, lane_number: int, start_time: float, end_time: float, toll_point_list: list, toll_point_length: float = 8):
        """创建收费车道"""
        toll_lane_param = Online.TollStation.DynaTollLane()
        # toll_lane_param.name = "123"
        # 路段ID
        toll_lane_param.roadId = link_id
        # 距离路段起点的距离，单位：m
        toll_lane_param.location = location
        # 收费车道长度，单位：m
        toll_lane_param.length = length
        # 车道序号，从右向左，从0开始
        toll_lane_param.laneNumber = lane_number
        # 开始时间，单位：秒
        toll_lane_param.startTime = start_time
        # 结束时间，单位：秒
        toll_lane_param.endTime = end_time
        # 收费区域列表
        toll_lane_param.tollPoint = toll_point_list
        # 收费区域长度
        toll_lane_param.tollPointLen = toll_point_length
        # 创建收费车道
        toll_lane = netiface.createTollLane(toll_lane_param)
        return toll_lane

    def _create_toll_decision_point(self, netiface, link_id: int, location: float, name: str = ""):
        """创建收费路径决策点"""
        link = netiface.findLink(link_id)
        if not link:
            return None
        toll_deci_point = netiface.createTollDecisionPoint(link, location, name)
        return toll_deci_point

    def _create_toll_routing(self, netiface, toll_deci_point, toll_lane):
        """创建收费路径"""
        toll_routing = netiface.createTollRouting(toll_deci_point, toll_lane)
        return toll_routing

    def _create_mtc_time_detail(self, time: float, prop: float):
        """创建MTC时间分布"""
        mtc_time_detail = Online.TollStation.DynaMtcTimeDetail()
        mtc_time_detail.time = time
        mtc_time_detail.prop = prop
        return mtc_time_detail

    def _create_etc_speed_detail(self, speed: float, prop: float):
        """创建ETC速度分布"""
        etc_speed_detail = Online.TollStation.DynaEtcSpeedDetail()
        etc_speed_detail.limitSpeed = speed
        etc_speed_detail.prop = prop
        return etc_speed_detail

    def _create_toll_parking_time(self, vehicle_type_id: int, time_dis_list: list, speed_dis_list: list):
        """为一种车型创建MTC停车时间分布和ECT速度分布"""
        toll_parking_time = Online.TollStation.DynaTollParkingTime()
        toll_parking_time.vehicleTypeId = vehicle_type_id
        toll_parking_time.timeDisList = time_dis_list
        toll_parking_time.speedDisList = speed_dis_list
        return toll_parking_time

    def _create_toll_parking_time_dis(self, netiface, parking_time_list: list, name: str = ""):
        """创建收费时间分布"""
        toll_parking_time_dis = Online.TollStation.DynaTollParkingTimeDis()
        toll_parking_time_dis.name = name
        toll_parking_time_dis.parkingTimeList = parking_time_list
        time_dis = netiface.createTollParkingTimeDis(toll_parking_time_dis)
        return time_dis



    def _create_parking(self, netiface, downstream):

        print(f"创建停车时间分布")
        new_pt = Online.ParkingLot.DynaParkingParkTime()
        new_pt.time = 3
        new_pt.prop = 100
        new_pt1 = Online.ParkingLot.DynaParkingParkTime()
        new_pt1.time = 5
        new_pt1.prop = 100
        new_ptd = Online.ParkingLot.DynaParkingTimeDis()
        new_ptd.name = "新增停车时间分布"
        new_ptd.parkingTimeList = [new_pt, new_pt1]
        netiface.createParkingTimeDis(new_ptd)

        print(f"创建停车区域")
        dynaParkingRegion = Online.ParkingLot.DynaParkingRegion()
        dynaParkingRegion.name = "test Parking region"
        dynaParkingRegion.location = 7800
        dynaParkingRegion.length = 100
        dynaParkingRegion.roadId = downstream.id()
        dynaParkingRegion.laneNumber = 0
        dynaParkingRegion.findParkingStallStrategy = 1
        # 0 - 车道内;1 - 车道右侧; 2 - 车道左侧
        dynaParkingRegion.parkingStallPos = 1
        dynaParkingRegion.arrangeType = 1
        # 车位吸引力
        dynaParkingRegion.firstParkingStallAttract = 1
        dynaParkingRegion.middleParkingStallAttract = 1
        dynaParkingRegion.lastParkingStallAttract = 1
        # 停车运动参数
        dynaParkingRegion.menaValue = 0 #  均值
        dynaParkingRegion.variance = 1.0 # 方差
        dynaParkingRegion.parkingSpeed = 5.0 # 泊车速度
        dynaParkingRegion.joinGap = 5.0 # 汇入间隙
        # 0 - 前进->前进;1 - 前进->后退; 2 - 后退->前进
        dynaParkingRegion.parkingType = 1
        # 运营参数
        dynaParkingRegion.attract = 0 #吸引力
        dynaParkingRegion.startTime = 0 # 开始时间
        dynaParkingRegion.endTime = 999999 #结束时间
        dynaParkingRegion.stallLength = 6 # 车位长度单位米
        dynaParkingRegion.stallWidth = 3 # 车位宽度单位米
        # 根据区域长度和车位长度，创建停车位
        stallLen = 6
        parkingStalls= []
        for i in range(int(dynaParkingRegion.length/stallLen)-1):
            parkingStall = Online.ParkingLot.DynaParkingStall()
            parkingStall.location = dynaParkingRegion.location + stallLen * i
            parkingStall.linkID = dynaParkingRegion.roadId
            parkingStall.laneNumber = dynaParkingRegion.laneNumber
            # 0 - 车道内; 1 - 车道右侧; 2 - 车道左侧
            parkingStall.parkingStallPos = 1
            # 0 - 垂直式; 1 - 倾斜式 - 30°;2 - 倾斜式 - 45°;3 - 倾斜式 - 60°;4 - 平行式
            parkingStall.arrangeType = 0
            # 停车位类型
            parkingStall.parkingStallType = 1 # 小客车 / 大客车 / 其他
            parkingStalls.append(parkingStall)
        dynaParkingRegion.parkingStalls = parkingStalls

        parkingRegion = netiface.createParkingRegion(dynaParkingRegion)

        distance = 7500
        new_pdp = netiface.createParkingDecisionPoint(downstream, distance, "上游路段停车场")
        new_pr = netiface.createParkingRouting(new_pdp, parkingRegion)


        # 更新停车时间分布
        origin_ptd = netiface.parkingTimeDis()[-1]
        origin_ptl = origin_ptd.parkingTimeList
        print(origin_ptd.id, [(i.parkingTimeDisId, i.time, i.prop) for i in origin_ptl])
        update_ptd = Online.ParkingLot.DynaParkingTimeDis()
        update_ptd.id = origin_ptd.id
        pt = Online.ParkingLot.DynaParkingParkTime()
        pt.time = 60
        pt.prop = 0.5
        new_ptl = origin_ptl + [pt]
        update_ptd.parkingTimeList = new_ptl
        netiface.updateParkingTimeDis(update_ptd)

        # 更新停车决策分配
        pdp = netiface.parkingDecisionPoints()[-1]
        pdi = pdp.parkDisInfoList()[-1]
        print(f'len(pdi.disVehiclsInfoList): {len(pdi.disVehiclsInfoList)}')
        dvi = pdi.disVehiclsInfoList[-1]
        dvi.startTime = 0
        dvi.endTime = 1800
        pdi.disVehiclsInfoList = [dvi]
        print(f"len(pdi.disVehiclsInfoList): {len(pdi.disVehiclsInfoList)}")
        for i in pdi.disVehiclsInfoList:
            print(f"{i.startTime}-{i.endTime}")
        print(f"{pdi.disVehiclsInfoList[0].startTime}-{pdi.disVehiclsInfoList[0].endTime}")
        pdp.updateParkDisInfo([pdi])

        # 移除
        # last_pr = netiface.parkingDecisionPoints()[-1].routings()[-1]
        # netiface.removeParkingRouting(last_pr)
        # pr = netiface.findParkingRegion(2)
        # netiface.removeParkingRegion(pr)    # 停车区相关的路径会被同步移除
        # last_ptd = netiface.parkingTimeDis()[-1]
        # netiface.removeParkingTimeDis(last_ptd.id)

        # 停车测试
        parkingRegions = netiface.parkingRegions()
        print(netiface.vehicleTypes())
        for pr in parkingRegions:
            print(f"id: {pr.id()}")
            print(f"name: {pr.name()}")
            print(f"parkingStalls: {pr.parkingStalls()}")
            pr.setName(f'停车区{pr.name()+"_new"}')
            parkingStalls = pr.parkingStalls()
            for ps in parkingStalls:
                # print(dir(ps))
                print(f"id: {ps.id()}")
                print(f"distance: {ps.distance()}")
                print(f"parkingRegion: {ps.parkingRegionId()}")
                print(f"type: {ps.stallType()}")

        parkingDecisionPoints = netiface.parkingDecisionPoints()
        # 停车决策点
        for pdp in parkingDecisionPoints:
            # print(f"parkingDecisionPoints={dir(pdp)}")
            print(f"id: {pdp.id()}")
            print(f"distance: {pdp.distance()}")
            print(f"link: {pdp.link().id()}")
            print(f"name: {pdp.name()}")
            print(f"parkDisInfoList: {pdp.parkDisInfoList()}")
            # 静态决策路径
            for pdi in pdp.parkDisInfoList():
                print(f"disVehiclsInfoList: {pdi.disVehiclsInfoList}")
                # 静态路径时间段
                for dvi in pdi.disVehiclsInfoList:
                    print(f"startTime: {dvi.startTime}")
                    print(f"endTime: {dvi.endTime}")
                    print(f"vehicleDisDetailList: {dvi.vehicleDisDetailList}")
                    # 停车分配详细信息
                    for vdd in dvi.vehicleDisDetailList:
                        print(f"vehicleDisDetailList={type(vdd)}, {dir(vdd)}")
                        print(f"parkingRegionID: {vdd.parkingRegionID}")
                        print(f"parkingRoutingID: {vdd.parkingRoutingID}")
                        print(f"parkingSelection: {vdd.parkingSelection}")
                        print(f"parkingTimeDisId: {vdd.parkingTimeDisId}")
                        print(f"prop: {vdd.prop}")
                        print(f"vehicleType: {vdd.vehicleType}")
                print(f"pIRouting: {pdi.pIRouting}")
            print(f"polygon: {pdp.polygon()}")
            print(f"routings: {pdp.routings()}")
        for routing in pdp.routings():
            print(f"routingId: {routing.id()}")
            print(f"routingLinks: {routing.getLinks()}")
            link = netiface.findLink(21)
            print(f"nextRoad: {routing.nextRoad(link)}")
            print(f"deciPoint: {routing.parkingDeciPointId()}")
            print(f"contain: {routing.contain(link)}")
            print(f"length: {routing.calcuLength()}")

        parkingTimeDisArray = netiface.parkingTimeDis()


    def _create_roadworkzone(self,netiface, upstream, downstream):
        param = Online.DynaRoadWorkZoneParam()
        param.name= '施工区'
        param.roadId = upstream.id()
        param.location = 200
        param.length = 100
        param.upCautionLength = 70
        param.upTransitionLength = 50
        param.upBufferLength = 50
        param.downTransitionLength = 40
        param.downTerminationLength = 40
        param.mlFromLaneNumber =  [3]
        param.startTime = 0
        param.duration = 3600
        param.limitSpeed = 42
        workzone = netiface.createRoadWorkZone(param, UnitOfMeasure.Metric)

        if workzone is not None:
            param2 = Online.DynaReconstructionParam()
            param2.roadWorkZoneId = workzone.id()
            param2.beBorrowedLinkId = downstream.id()
            param2.passagewayLimitedSpeed = 42
            param2.borrowedNum = 1
            # 下列参数可选
            # param2.passagewayLength = 100
            reconstruction = netiface.createReconstruction(param2, UnitOfMeasure.Metric)








class VehDriveFunctionTest:
    pass



class SimuInterfaceFunctionTest:
    pass



class GuiInterfaceFunctionTest:
    pass





class TessPluginFunctionTest:
    pass