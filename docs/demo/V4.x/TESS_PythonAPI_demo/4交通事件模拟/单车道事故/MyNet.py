# -*- coding: utf-8 -*-

import os
from pathlib import Path
import sys

from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *

import Tessng
from Tessng import PyCustomerNet, TessInterface, TessPlugin, NetInterface, tngPlugin, tngIFace, m2p
from Tessng import NetItemType, GraphicsItemPropName

# 用户插件子类，代表用户自定义与路网相关的实现逻辑，继承自MyCustomerNet
class MyNet(PyCustomerNet):
    def __init__(self):
        super(MyNet, self).__init__()




















