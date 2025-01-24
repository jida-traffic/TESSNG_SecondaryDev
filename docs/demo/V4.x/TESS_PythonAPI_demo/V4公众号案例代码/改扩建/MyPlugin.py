import os
import sys
from PySide2.QtWidgets import QApplication

from DLLs.Tessng import TessPlugin, TessngFactory, tessngIFace
from MyNet import MyNet


class MyPlugin(TessPlugin):
    def __init__(self, tess_file_path):
        super().__init__()
        self.tess_file_path: str = tess_file_path
        self.my_net = None
        self.my_simulator = None

    def start(self) -> None:
        workspace = os.path.join(os.getcwd(), "WorkSpace")
        os.makedirs(workspace, exist_ok=True)

        config = {
            '__workspace': workspace,
            '__netfilepath': self.tess_file_path,
            '__simuafterload': False,
            '__custsimubysteps': False,
            '__allowspopup': False,  # 禁止弹窗
            '__cacheid': True,  # 快速创建路段
        }

        app = QApplication()
        factory = TessngFactory()
        tessng = factory.build(self, config)
        if tessng is None:
            sys.exit(0)
        else:
            sys.exit(app.exec_())

    def init(self):
        self.my_net = MyNet()
        iface = tessngIFace()
        win = iface.guiInterface().mainWindow()
        win.showOsmInline(False)

    def customerNet(self):
        return self.my_net
