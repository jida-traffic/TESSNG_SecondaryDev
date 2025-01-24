import collections
import os

from PySide2.QtCore import *
from PySide2.QtGui import *
from shiboken2.shiboken2 import wrapInstance

import config
from Tessng import TessInterface, SimuInterface, PyCustomerSimulator, IVehicle, ILink
from Tessng import m2p, p2m, tessngIFace, tessngPlugin
from Tessng import *
import math


# 用户插件子类，代表用户自定义与仿真相关的实现逻辑，继承自PyCustomerSimulator
#     多重继承中的父类QObject，在此目的是要能够自定义信号signlRunInfo
class MySimulator(QObject, PyCustomerSimulator):
    signalRunInfo = Signal(str)
    forStopSimu = Signal()
    forReStartSimu = Signal()
    getSignalTime = 0

    def __init__(self, net):
        QObject.__init__(self)
        PyCustomerSimulator.__init__(self)
        self.mNetInf = net
        self.lastSimuTime = 0  # 记录上一次调用计算的仿真时间
        self.offset = 10  # 每个人行横道向外延伸offset，记录为其潜在冲突范围
        self.crossConflict = {}  # 记录每个人行横道的潜在冲突范围
        self.conflictDistanceThreshold = 0.5  # 判断为冲突的最大距离

    # 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法，大量用户逻辑在此实现，注意耗时大的计算要尽可能优化，否则影响运行效率
    def afterOneStep(self):
        # = == == == == == =以下是获取一些仿真过程数据的方法 == == == == == ==
        # TESSNG 顶层接口
        iface = tessngIFace()
        # TESSNG 仿真子接口
        simuiface = iface.simuInterface()
        # TESSNG 路网子接口
        netiface = iface.netInterface()
        pynetiface = PyCustomerNet()
        # 当前仿真计算批次
        batchNum = simuiface.batchNumber()
        # 当前已仿真时间，单位：毫秒
        simuTime = simuiface.simuTimeIntervalWithAcceMutiples()


        # 每秒调用计算冲突
        if simuTime // 1000 == self.lastSimuTime // 1000:
            return
        crossWalkPedestrians = {}
        conflictInfo = collections.defaultdict(list)
        # 遍历人行横道
        for crossWalk in netiface.pedestrianCrossWalkRegions():
            regionId = crossWalk.getId()
            # 获取当前人行横道上所有行人
            pedestrians = simuiface.getPedestriansStatusByRegionId(regionId)
            crossWalkPedestrians[regionId] = pedestrians

            if not self.crossConflict.get(regionId):
                # 计算当前人行横道冲突面域，人行横道范围向外延申offset的距离
                points = crossWalk.getPolygon()
                pointsCor = list()
                for point in points:
                    pointsCor.append((p2m(crossWalk.x() + point.x()), -p2m(crossWalk.y() + point.y())))
                left = min([i[0] for i in pointsCor]) - self.offset
                right = max([i[0] for i in pointsCor]) + self.offset
                bottom = min([i[1] for i in pointsCor]) - self.offset
                top = max([i[1] for i in pointsCor]) + self.offset
                self.crossConflict[regionId] = {"top": top, "left": left, "right": right, "bottom": bottom}

        # 取人行横道冲突范围内的车辆
        vehicles = simuiface.allVehicle()
        for veh in vehicles:
            veh_x = p2m(veh.pos().x())
            veh_y = p2m(veh.pos().y())
            veh_angle = veh.angle()
            for k, v in self.crossConflict.items():
                if not (veh_x >= v['left'] and veh_x <= v['right'] and veh_y >= v['bottom'] and veh_y <= v['top']):
                    continue
                else:
                    # 与人行横道上的所有行人，计算距离，判断冲突
                    for ped in crossWalkPedestrians.get(k):
                        pos = ped.pos
                        ped_x = p2m(pos.x())
                        ped_y = p2m(pos.y())
                        direction = ped.mDirection  # 行人角度，用向量表示
                        ped_angle = math.degrees(
                            math.atan(p2m(direction.y()) / p2m(direction.x()))) + 90  # 行人角度以正东为0，机动车角度以正北为0
                        delta_angle = abs(ped_angle - veh_angle)
                        distance = ((veh_x - ped_x) ** 2 + (veh_y - ped_y) ** 2) ** 0.5
                        if delta_angle <= 30 or (delta_angle >= 150 and delta_angle <= 210):
                            continue
                        if distance <= self.conflictDistanceThreshold + veh.length() / 2 and ped_angle - veh_angle:
                            ped_id = ped.id
                            veh_id = veh.id()
                            section = veh.section()
                            if section.isLink():
                                conflictInfo = {"行人ID": ped_id, "人行横道ID": k, "车辆ID": veh_id, "路段ID": section.id(), "距离": max(distance - veh.length() / 2, 0)}
                            else:
                                conflictInfo = {"行人ID": ped_id, "人行横道ID": k, "车辆ID": veh_id, "连接段ID": section.id(), "距离": max(distance - veh.length() / 2, 0)}
                            print(f"仿真时间{simuTime // 1000}s，行人与机动车冲突信息：{conflictInfo}")
                    break

