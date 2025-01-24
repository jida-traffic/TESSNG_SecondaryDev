# -*- coding: utf-8 -*-
import collections
import os
import time
from pathlib import Path
import sys
import PySide2
from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *


from Tessng import TessInterface, TessPlugin, NetInterface, PyCustomerNet
from Tessng import tessngPlugin, tessngIFace, m2p, p2m, Online
from Tessng import NetItemType, GraphicsItemPropName


# 用户插件子类，代表用户自定义与路网相关的实现逻辑，继承自MyCustomerNet
class MyNet(PyCustomerNet):
    def __init__(self):
        super(MyNet, self).__init__()
        iface = tessngIFace()
        netiface = iface.netInterface()
    #
    # # 过载的父类方法，当打开网后TESS NG调用此方法
    # #     实现的逻辑是：路网加载后获取路段数，如果路网数为0则调用方法createNet构建路网，之后再次获取路段数，如果大于0则启动仿真
    def afterLoadNet(self):
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()

        # 创建人行道，将路段向右侧平移offset的距离，得到路侧人行道的形状
        offset = 3
        linkPairs = {"西进口": (1, 6), "南进口": (5, 2), "东进口": (3, 7), "北进口": (8, 4)}
        pathStartPoints = list()  # 记录行人发生点，用于创建行人路径
        pathEndPoints = list()  # 记录行人结束点，用于创建行人路径
        crossWalks = list()  # 记录创建的人行横道，用于在人行横道上创建信号灯
        pedestrianStartPoint = None
        for direction, linkPair in linkPairs.items():
            update_vertexs = []
            for link in linkPair:
                link = netiface.findLink(link)
                vertexs = list()
                rightBreakPoints = link.rightBreakPoints()
                startPoint = rightBreakPoints[0]
                endPoint = rightBreakPoints[-1]
                delta_x = p2m(endPoint.x()) - p2m(startPoint.x())
                delta_y = p2m(endPoint.y()) - p2m(startPoint.y())
                for point in link.rightBreakPoints():
                    x = p2m(point.x())
                    y = p2m(point.y())
                    vertexs.append((x, y))
                if abs(delta_x) > abs(delta_y):
                    if delta_x > 0:
                        update_vertexs += [QPointF(p[0], p[1] + offset) for p in vertexs]
                    else:
                        update_vertexs += [QPointF(p[0], p[1] - offset) for p in vertexs]
                else:
                    if delta_y > 0:
                        update_vertexs += [QPointF(p[0] - offset, p[1]) for p in vertexs]
                    else:
                        update_vertexs += [QPointF(p[0] + offset, p[1]) for p in vertexs]
            new_pedestrianSideWalkRegion = netiface.createPedestrianSideWalkRegion(update_vertexs)
            # 创建行人发生点
            pathStartPoint_x = (update_vertexs[0].x() * 2 + update_vertexs[1].x()) / 3
            pathStartPoint_y = (update_vertexs[0].y() * 2 + update_vertexs[1].y()) / 3
            pathStartPoint = QPointF(pathStartPoint_x, pathStartPoint_y)
            new_pathStartPoint = netiface.createPedestrianPathStartPoint(pathStartPoint)
            pathStartPoints.append(new_pathStartPoint)
            # 创建行人结束点
            pathEndPoint_x = (update_vertexs[-1].x() * 2 + update_vertexs[-2].x()) / 3
            pathEndPoint_y = (update_vertexs[-1].y() * 2 + update_vertexs[-2].y()) / 3
            pathEndPoint = QPointF(pathEndPoint_x, pathEndPoint_y)
            new_pathEndPoint = netiface.createPedestrianPathEndPoint(pathEndPoint)
            pathEndPoints.append(new_pathEndPoint)
            # 记录西进口行人发生点
            if direction == "西进口":
                pedestrianStartPoint = new_pathStartPoint
            print(f"创建{direction}人行道{new_pedestrianSideWalkRegion.getId()},行人发生点{new_pathStartPoint.getId()},行人结束点{new_pathEndPoint.getId()}")
        time.sleep(5)

        # 创建人行横道
        crossWalkPoints = {"西进口": ((-32, -35), (-32, 2)), "北进口": ((-21, 10), (35, 10)),
                           "南进口": ((-24, -39), (33, -39)), "东进口": ((45, -29), (45, 4))}  # 人行横道起终点坐标
        pathMiddlePoints = list()  # 记录人行横道中心点，用于创建行人路径
        for direction, crossWalkPoint in crossWalkPoints.items():
            startPointCoor = crossWalkPoint[0]
            startPoint = QPointF(startPointCoor[0], -startPointCoor[1])
            endPointCoor = crossWalkPoint[1]
            endPoint = QPointF(endPointCoor[0], -endPointCoor[1])
            # 取人行横道中心点，用于创建行人路径
            pathMiddlePoint_x = (startPointCoor[0] + endPointCoor[0]) / 2
            pathMiddlePoint_y = - (startPointCoor[1] + endPointCoor[1]) / 2
            pathMiddlePoint = QPointF(pathMiddlePoint_x, pathMiddlePoint_y)
            crossWalk = netiface.createPedestrianCrossWalkRegion(startPoint, endPoint)
            crossWalk.setName(f"{direction}人行横道")
            crossWalks.append(crossWalk)
            pathMiddlePoints.append(pathMiddlePoint)
            print(f"创建{direction}人行横道{crossWalk.getId()}")
        time.sleep(5)

        # 创建过街行人路径
        straightPath = netiface.createPedestrianPath(pathStartPoints[0], pathEndPoints[1], [pathMiddlePoints[2]])  # 西进口直行过街
        print("创建西进口直行过街路径")
        leftPath = netiface.createPedestrianPath(pathStartPoints[0], pathEndPoints[3], [pathMiddlePoints[0]])  # 西进口左转过街
        print("创建西进口左转过街路径")
        netiface.createPedestrianPath(pathStartPoints[1], pathEndPoints[2], [pathMiddlePoints[3]])  # 南进口直行过街
        print("创建南进口直行过街路径")
        netiface.createPedestrianPath(pathStartPoints[2], pathEndPoints[3], [pathMiddlePoints[1]])  # 东进口直行过街
        print("创建东进口直行过街路径")
        netiface.createPedestrianPath(pathStartPoints[3], pathEndPoints[0], [pathMiddlePoints[0]])  # 北进口直行过街
        print("创建北进口直行过街路径")
        time.sleep(5)

        # # 创建人行横道信号灯
        crossWalkPhaseId = {"西进口": 5, "东进口": 5, "南进口": 4, "北进口": 4}  # 东西进口行人采用南北方向机动车相位，南北进口行人采用东西方向机动车相位
        trafficController = netiface.findSignalControllerById(1)
        # 遍历人行横道创建信号灯
        for crossWalk in crossWalks:
            crossWalkId = crossWalk.getId()
            crossWalkName = crossWalk.getName()
            crossWalkDirection = crossWalkName.replace('人行横道', '')
            crossWalkPoint = crossWalkPoints.get(crossWalkDirection)
            crossWalkStartPoint = crossWalkPoint[0]
            crossWalkEndPoint = crossWalkPoint[1]
            name = f"{crossWalkName}信号灯"
            isPositive = True
            position_x = (crossWalkStartPoint[0] * 9 + crossWalkEndPoint[0]) / 10
            position_y = (crossWalkStartPoint[1] * 9 + crossWalkEndPoint[1]) / 10
            position = QPointF(position_x, -position_y)
            signalLamp_positive = netiface.createCrossWalkSignalLamp(trafficController, name, crossWalkId, position,
                                                                     isPositive)
            isPositive = False
            position_x = (crossWalkStartPoint[0] + crossWalkEndPoint[0] * 9) / 10
            position_y = (crossWalkStartPoint[1] + crossWalkEndPoint[1] * 9) / 10
            position = QPointF(position_x, -position_y)
            signalLamp_negtive = netiface.createCrossWalkSignalLamp(trafficController, name, crossWalkId, position,
                                                                    isPositive)

            # 为信号灯分配相位
            phase = netiface.findSignalPhase(crossWalkPhaseId.get(crossWalkDirection))
            netiface.addCrossWalkSignalPhaseToLamp(phase.id(), signalLamp_positive)
            netiface.addCrossWalkSignalPhaseToLamp(phase.id(), signalLamp_negtive)
            print(f"为{crossWalkDirection}人行横道创建双向信号灯{signalLamp_positive.id()},{signalLamp_negtive}; 并分配相位{phase.id()}")

        # 更新行人发生点流量和路径分配比例
        configInfo = netiface.findPedestrianStartPointConfigInfo(pedestrianStartPoint.getId())
        update_configInfo = Online.Pedestrian.PedestrianPathStartPointConfigInfo()
        # # 更新行人发生点流量
        update_genPedestrianConfigInfo = Online.Pedestrian.GenPedestrianInfo()
        update_genPedestrianConfigInfo.pedestrianCount = 6
        update_genPedestrianConfigInfo.timeInterval = configInfo.genPedestrianConfigInfo[0].timeInterval
        update_configInfo.genPedestrianConfigInfo = [update_genPedestrianConfigInfo]
        update_configInfo.id = configInfo.id
        distributionInfo = configInfo.pedestrianTrafficDistributionConfigInfo[0]
        update_distributionInfo = Online.Pedestrian.PedestrianTrafficDistributionInfo()
        update_distributionInfo.timeInterval = distributionInfo.timeInterval
        update_distributionInfo.trafficRatio = {straightPath.getId(): 2, leftPath.getId(): 1}
        update_configInfo.pedestrianTrafficDistributionConfigInfo = [update_distributionInfo]
        result = netiface.updatePedestrianStartPointConfigInfo(update_configInfo)
        print(f"更新西进口行人发生点流量:{update_genPedestrianConfigInfo.pedestrianCount}人/{update_genPedestrianConfigInfo.timeInterval}秒,result:{result}")
        print(f"更新西进口行人发生点路径分配:{update_distributionInfo.trafficRatio},result:{result}")

