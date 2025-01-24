# -*- coding: utf-8 -*-

from PySide2.QtGui import *
from PySide2.QtWidgets import *

from Tessng import TessPlugin
from MyNet import *
from MySimulator import *
from TESS_API_EXAMPLE import *
from function_test import IFunctionTest

# # 用户插件，继承自TessPlugin
class MyPlugin(TessPlugin):
    def __init__(self):
        super(MyPlugin, self).__init__()
        self.mNetInf = None
        self.mSimuInf = None

    def init(self):
        self.secondary_dev = IFunctionTest(1)
        self.mNetInf = MyNet(self.secondary_dev)
        self.mSimuInf = MySimulator(self.secondary_dev)

        iface = tngIFace()
        win = iface.guiInterface().mainWindow()

    # 过载父类方法，返回插件路网子接口，此方法由TESS NG调用
    def customerNet(self):
        return self.mNetInf

    # 过载父类方法，返回插件仿真子接口，此方法由TESS NG调用
    def customerSimulator(self):
        return self.mSimuInf
