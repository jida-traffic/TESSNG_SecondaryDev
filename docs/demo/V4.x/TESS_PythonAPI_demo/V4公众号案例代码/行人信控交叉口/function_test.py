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



