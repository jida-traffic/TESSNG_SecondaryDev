# -*- coding: utf-8 -*-

import os
from pathlib import Path
import sys
import time
import json

from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *

from Tessng import TessInterface, TessPlugin, NetInterface, PyCustomerNet
from Tessng import tessngPlugin, tessngIFace, m2p, p2m
from Tessng import NetItemType, GraphicsItemPropName
from Tessng import Online, UnitOfMeasure
from function_test import IFunctionTest
from functions import time_to_seconds


# 用户插件子类，代表用户自定义与路网相关的实现逻辑，继承自MyCustomerNet
class MyNet(PyCustomerNet):
    def __init__(self,IFunctionTest):
        super(MyNet, self).__init__()
        self.ifunctionTest = IFunctionTest

    # 过载的父类方法，当打开网后TESS NG调用此方法
    # 原createNet见V2.1二次开发案例
    def afterLoadNet(self):
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()

        '''调用相应二次开发案例函数'''
        count = netiface.linkCount()

        if(count == 0):
            self.ifunctionTest.create_junction(netiface)



