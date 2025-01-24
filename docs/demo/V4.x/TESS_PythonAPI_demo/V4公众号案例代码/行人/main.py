# -*- coding: utf-8 -*-
import os
from pathlib import Path
import sys
from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *

from Tessng import *
from MyPlugin import *

if __name__ == '__main__':
    app = QApplication()

    workspace = os.fspath(Path(__file__).resolve().parent)
    config = {'__workspace': workspace,
              '__netfilepath': r"./network\行人案例-天津津静公路-海泰南北大街交叉口-无行人.tess",
              '__simuafterload': True,
              '__custsimubysteps': False,
              "__enablecachepedestriansinregion": True,
              }
    plugin = MyPlugin()
    factory = TessngFactory()
    tessng = factory.build(plugin, config)
    if tessng is None:
        sys.exit(0)
    else:
        sys.exit(app.exec_())



