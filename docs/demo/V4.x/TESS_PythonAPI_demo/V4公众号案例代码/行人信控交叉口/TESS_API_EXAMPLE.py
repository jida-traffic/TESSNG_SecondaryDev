# -*- coding: utf-8 -*-

import os
from pathlib import Path
import sys

from DockWidget import *
from Tessng import *

from MyNet import MyNet
from function_test import IFunctionTest


class TESS_API_EXAMPLE(QMainWindow):
    def __init__(self, parent=None):
        super(TESS_API_EXAMPLE, self).__init__(parent)
    #     # 初始化二次开发对象
        self.secondary_dev = IFunctionTest(3)
