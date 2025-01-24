from PySide2.QtCore import *
from shiboken2.shiboken2 import wrapInstance

from Tessng import TessInterface, SimuInterface, PyCustomerSimulator, IVehicle, ILink
from Tessng import m2p, p2m, tessngIFace, tngPlugin
from Tessng import *
import random
from datetime import datetime

# 用户插件子类，代表用户自定义与仿真相关的实现逻辑，继承自PyCustomerSimulator
#     多重继承中的父类QObject，在此目的是要能够自定义信号signlRunInfo
class MySimulator(QObject, PyCustomerSimulator, SimuInterface, IVehicleDrivingManagerTask):
    signalRunInfo = Signal(str)
    forReStartSimu = Signal()
        


    def __init__(self):
        QObject.__init__(self)
        PyCustomerSimulator.__init__(self)

        # 相同路网连续仿真次数
        self.mAutoStartSimuCount = 0

    def stopSimu(self):
        iface = tngIFace()
        if not iface:
            return
        if iface.simuInterface().isRunning():
            iface.simuInterface().stopSimu()
    

    def afterStop(self):
        # 这里设置要停止后仿真几次，
        print(self.mAutoStartSimuCount)
        if self.mAutoStartSimuCount >= 10:
            return
        iface = tessngIFace()
        netface = iface.netInterface()
        filePath = netface.netFilePath()
        #范例打开临时路段会会创建车辆方阵，需要进行一些仿真过程控制
        if "Temp" not in filePath:
            self.mAutoInOutSi = random.randint(1,15)
            self.mAutoInOutSd = random.randint(1,15)
            self.mAutoStartSimuCount += 1
            self.forReStartSimu.emit()























