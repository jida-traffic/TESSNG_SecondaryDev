import json
import math
import random
from PySide2.QtCore import *
from shiboken2.shiboken2 import wrapInstance

from Tessng import TessInterface, SimuInterface, PyCustomerSimulator, IVehicle, ILink
from Tessng import m2p, p2m, tessngIFace, tessngPlugin
from Tessng import Online, UnitOfMeasure
from Tessng import *
import random
from datetime import datetime

from function_test import (IFunctionTest, VehDriveFunctionTest,
                           SimuInterfaceFunctionTest,GuiInterfaceFunctionTest,TessPluginFunctionTest)
import functions


# 用户插件子类，代表用户自定义与仿真相关的实现逻辑，继承自PyCustomerSimulator
#     多重继承中的父类QObject，在此目的是要能够自定义信号signlRunInfo
class MySimulator(QObject, PyCustomerSimulator):
    signalRunInfo = Signal(str)
    forStopSimu = Signal()
    forReStartSimu = Signal()


    def __init__(self, IFunctionTest):
        QObject.__init__(self)
        PyCustomerSimulator.__init__(self)
        # 车辆方阵的车辆数
