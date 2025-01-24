import os

from PySide2.QtCore import *
from PySide2.QtGui import *
from shiboken2.shiboken2 import wrapInstance

import config
from Tessng import TessInterface, SimuInterface, PyCustomerSimulator, IVehicle, ILink
from Tessng import m2p, p2m, tessngIFace, tessngPlugin
from Tessng import *

import time
import json
# import config

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
        self.lastSimuTime = 0



    # 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法，大量用户逻辑在此实现，注意耗时大的计算要尽可能优化，否则影响运行效率
    def afterOneStep(self):
        #= == == == == == =以下是获取一些仿真过程数据的方法 == == == == == ==
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

        # 仿真180秒后，关闭路段4左右两侧的停车场
        # 判断仿真时间
        if self.lastSimuTime < 180 * 1000 and simuTime >= 180 * 1000:
            # 更新停车路径分配
            for parkingDecisionPoint in netiface.parkingDecisionPoints():
                update_parkDisInfoList = []
                for parkDisInfo in parkingDecisionPoint.parkDisInfoList():
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
                            update_vehicleDisDetail.parkingRegionID = vehicleDisDetail.parkingRegionID
                            update_vehicleDisDetail.parkingRoutingID = vehicleDisDetail.parkingRoutingID
                            update_vehicleDisDetail.parkingSelection = vehicleDisDetail.parkingSelection
                            update_vehicleDisDetail.parkingTimeDisId = vehicleDisDetail.parkingTimeDisId
                            update_vehicleDisDetail.vehicleType = vehicleDisDetail.vehicleType
                            linkId = netiface.findParkingRegion(vehicleDisDetail.parkingRegionID).dynaParkingRegion().roadId
                            if linkId == 4:
                                update_vehicleDisDetail.prop = 0
                            else:
                                update_vehicleDisDetail.prop = vehicleDisDetail.prop
                            update_vehicleDisDetailList.append(update_vehicleDisDetail)
                        update_disVehiclsInfo.vehicleDisDetailList = update_vehicleDisDetailList
                        update_disVehiclsInfoList.append(update_disVehiclsInfo)
                    update_parkDisInfo.disVehiclsInfoList = update_disVehiclsInfoList
                    update_parkDisInfoList.append(update_parkDisInfo)
                result = parkingDecisionPoint.updateParkDisInfo(update_parkDisInfoList)
                print(f"仿真{simuTime // 1000}秒，关闭路段4两侧的停车区; result: {result}")

        # 更新仿真时间
        self.lastSimuTime = simuTime










