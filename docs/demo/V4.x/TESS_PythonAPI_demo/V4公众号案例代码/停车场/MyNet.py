# -*- coding: utf-8 -*-
import collections
import os
import time
from pathlib import Path
import sys

import PySide2
import pandas as pd

from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *

import json
import pandas
from functions import tess_function

from Tessng import TessInterface, TessPlugin, NetInterface, PyCustomerNet
from Tessng import tessngPlugin, tessngIFace, m2p, p2m, Online, _DecisionPoint
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


        # # step1：创建停车区
        parkingRegionLink = [1, 2, 3, 4]    # 指定停车区所在路段
        strPosition = {2: '左侧', 1: '右侧', 0: '车道内'}    # 停车区相对路段的方向
        parkingStallLength = 6    # 停车位长度
        parkingStallWidth = 3    # 停车位宽度
        parkingStallCount = 25    # 每个停车区停车位数量
        positionArray = [1, 2]    # 停车区相对路段的方向
        position2laneNumber = {1: 0, 2: 1}        # 停车区所在车道，右侧停车区在车道0，左侧停车区在车道1
        # 为每个路段创建停车区
        for i in range(len(parkingRegionLink)):
            # 在当前路段的左右两侧分别创建停车区
            for p in positionArray:
                parkingRegionPosition = p
                strParkingRegionPosition = strPosition.get(parkingRegionPosition)
                parkingRegion = Online.ParkingLot.DynaParkingRegion()
                roadId = parkingRegionLink[i]
                laneNumber = position2laneNumber[p]
                parkingRegion.name = f"路段{roadId}{strParkingRegionPosition}停车场"
                parkingRegion.location = 0.5
                parkingRegion.length = parkingStallCount * parkingStallWidth
                parkingRegion.roadId = roadId
                parkingRegion.laneNumber = laneNumber
                parkingRegion.findParkingStallStrategy = 1
                parkingRegion.parkingStallPos = parkingRegionPosition    #  0-车道内;1-车道右侧;2-车道左侧
                parkingRegion.arrangeType = 0     # 0-垂直式;1- 倾斜式-30°;2-倾斜式-45°;3-倾斜式-60°;4-平行式
                # 车位吸引力
                parkingRegion.firstParkingStallAttract = 1
                parkingRegion.middleParkingStallAttract = 1
                parkingRegion.lastParkingStallAttract = 1

                # 停车运动参数
                parkingRegion.menaValue = 0   # 均值
                parkingRegion.variance = 1.0   # 方差
                parkingRegion.parkingSpeed = 5.0   # 泊车速度
                parkingRegion.joinGap = 5.0   # 汇入间隙
                parkingRegion.parkingType = 0    # 0 - 前进->前进;1 - 前进->后退;2 - 后退->前进
                # 运营参数
                parkingRegion.attract = 0   # 吸引力
                parkingRegion.startTime = 0   # 开始时间
                parkingRegion.endTime = 999999   # 结束时间
                parkingRegion.stallLength = parkingStallLength   # 车位长度单位米
                parkingRegion.stallWidth = parkingStallWidth   # 车位宽度单位米
                parkingStalls = []
                # 为当前停车区创建停车位
                for k in range(parkingStallCount):
                    parkingStallData = Online.ParkingLot.DynaParkingStall()
                    parkingStallData.location = parkingRegion.location + parkingRegion.stallWidth * k    # 根据停车区起点位置，计算每个停车位起点位置
                    parkingStallData.parkingStallType = 1    # 停车位类型 // 小客车 / 大客车 / 其他
                    parkingStalls.append(parkingStallData)
                parkingRegion.parkingStalls = parkingStalls
                new_parkingRegion = netiface.createParkingRegion(parkingRegion)
                print(f"创建路段{roadId}{strParkingRegionPosition}停车区{new_parkingRegion.id()}")
        parkingRegionCount = len(netiface.parkingRegions())    # 记录已经创建的停车区数量
        time.sleep(5)

        # # step2：创建停车决策路径
        linkId = 6    # 指定决策点所在路段id
        link = netiface.findLink(linkId)
        distance = link.length() / 3
        name = f"路段{link.id()}上的停车决策点"
        new_parkingDecisionPoint = netiface.createParkingDecisionPoint(link, distance, name)
        print(f"创建路段{link.id()}上的停车决策点")
        for parkingRegion in netiface.parkingRegions():
            parkingRegionId = parkingRegion.id()
            targetParkingRegion = netiface.findParkingRegion(parkingRegionId)
            new_parkingRouting = netiface.createParkingRouting(new_parkingDecisionPoint, targetParkingRegion)
            print(f"创建到停车区{parkingRegionId}的路径{new_parkingRouting.id()}")
        time.sleep(5)

        # step3：平均分配停车区
        update_parkDisInfoList = []
        for parkDisInfo in new_parkingDecisionPoint.parkDisInfoList():
            update_parkDisInfo = Online.ParkingLot.DynaParkDisInfo()
            update_parkDisInfo.pIRouting = parkDisInfo.pIRouting
            update_disVehiclsInfoList = []
            for disVehiclsInfo in parkDisInfo.disVehiclsInfoList:
                update_disVehiclsInfo = Online.ParkingLot.DynaRoutingDisVehicleInfo()
                update_disVehiclsInfo.startTime = disVehiclsInfo.startTime
                update_disVehiclsInfo.endTime = disVehiclsInfo.endTime
                update_vehicleDisDetailList = []
                for vehicleDisDetail in disVehiclsInfo.vehicleDisDetailList:
                    update_vehicleDisDetail = Online.ParkingLot.DynaParkRegionVehicleDisDetail()
                    update_vehicleDisDetail.prop = 1 / parkingRegionCount
                    update_vehicleDisDetail.parkingRegionID = vehicleDisDetail.parkingRegionID
                    update_vehicleDisDetail.parkingRoutingID = vehicleDisDetail.parkingRoutingID
                    update_vehicleDisDetail.parkingSelection = vehicleDisDetail.parkingSelection
                    update_vehicleDisDetail.parkingTimeDisId = vehicleDisDetail.parkingTimeDisId
                    update_vehicleDisDetail.vehicleType = vehicleDisDetail.vehicleType
                    update_vehicleDisDetailList.append(update_vehicleDisDetail)
                update_disVehiclsInfo.vehicleDisDetailList = update_vehicleDisDetailList
                update_disVehiclsInfoList.append(update_disVehiclsInfo)
            update_parkDisInfo.disVehiclsInfoList = update_disVehiclsInfoList
            update_parkDisInfoList.append(update_parkDisInfo)
        result = new_parkingDecisionPoint.updateParkDisInfo(update_parkDisInfoList)
        print(f"为每个停车区平均分配流量, result: {result}")





