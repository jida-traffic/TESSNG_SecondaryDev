
import os, sys
# 将项目根目录添加到系统路径
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from PySide2.QtCore import *
import math
from Tessng import *
from typing import Optional
import os, logging


class MySimulator(QObject, PyCustomerSimulator, SimuInterface, IVehicleDrivingManagerTask):
    signalRunInfo = Signal(str)
    forReStartSimu = Signal()
    signalKeyPoint = Signal(dict)
    signalShowVehInfo = Signal(dict)


    def __init__(self, obstructionInfo):
        super().__init__()
        PyCustomerSimulator.__init__(self)
        self.simuTime = 0
        self.iface = tngIFace()
        # TESSNG 仿真子接口
        self.simuiface = self.iface.simuInterface()
        # TESSNG 路网子接口
        self.netiface =  self.iface.netInterface()





    def afterOneStep(self) -> None:



        # 当前已仿真时间，单位：秒
        self.simuTime = self.simuiface.simuTimeIntervalWithAcceMutiples()/1000
        # 当前正在运行车辆列表
        lAllVehi = self.simuiface.allVehiStarted()



    def stopSimu(self):
        iface = tngIFace()
        if not iface:
            return
        if iface.simuInterface().isRunning():
            iface.simuInterface().stopSimu()


