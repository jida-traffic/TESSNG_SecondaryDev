import typing
from PySide2.QtCore import *
from shiboken2.shiboken2 import wrapInstance

from Tessng import TessInterface, SimuInterface, PyCustomerSimulator, IVehicle, ILink
from Tessng import m2p, p2m, tessngIFace, tessngPlugin
from Tessng import *
import random
import math
from datetime import datetime


# 用户插件子类，代表用户自定义与仿真相关的实现逻辑，继承自PyCustomerSimulator
#     多重继承中的父类QObject，在此目的是要能够自定义信号signlRunInfo


class MySimulator(QObject, PyCustomerSimulator):
    signalRunInfo = Signal(str)
    forStopSimu = Signal()
    forReStartSimu = Signal()


    def __init__(self):
        QObject.__init__(self)
        PyCustomerSimulator.__init__(self)
        # 车辆方阵的车辆数
        self.mrSquareVehiCount = 28
        # 飞机速度，飞机后面的车辆速度会被设定为此数据
        self.mrSpeedOfPlane = 0
        # 当前正在仿真计算的路网名称
        self.mNetPath = None
        # 相同路网连续仿真次数
        self.mSimuCount = 0
        # 通行的绿灯时间
        self.greenTime=60
        # 采集器1过车数
        self.vehiCount=0

    #设置本类实现的过载方法被调用频次，即多少个计算周期调用一次。过多的不必要调用会影响运行效率
    def setStepsPerCall(self, vehi):
        #设置当前车辆及其驾驶行为过载方法被TESSNG调用频次，即多少个计算周调用一次指定方法。如果对运行效率有极高要求，可以精确控制具体车辆或车辆类型及具体场景相关参数
        iface = tessngIFace()
        netface = iface.netInterface()
        netFileName = netface.netFilePath()
        #范例打开临时路段会会创建车辆方阵，需要进行一些仿真过程控制
        if "Temp" in netFileName:
            #允许对车辆重绘方法的调用
            vehi.setIsPermitForVehicleDraw(True)
            #计算限制车道方法每10个计算周期被调用一次
            vehi.setSteps_calcLimitedLaneNumber(10)
            #计算安全变道距离方法每10个计算周期被调用一次
            vehi.setSteps_calcChangeLaneSafeDist(10)
            #重新计算车辆期望速度方法每一个计算周期被调用一次
            vehi.setSteps_reCalcdesirSpeed(1)
            #重新设置车速方法每一个计算周期被调用一次
            vehi.setSteps_reSetSpeed(1)
        else:
            simuface = iface.simuInterface()
            #仿真精度，即每秒计算次数
            steps = simuface.simuAccuracy()

            pass



    # 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法，大量用户逻辑在此实现，注意耗时大的计算要尽可能优化，否则影响运行效率
    def afterOneStep(self):
        #= == == == == == =以下是获取一些仿真过程数据的方法 == == == == == ==
        # TESSNG 顶层接口
        iface = tessngIFace()
        # TESSNG 仿真子接口
        simuiface = iface.simuInterface()
        # TESSNG 路网子接口
        netiface = iface.netInterface()
        # 当前仿真计算批次
        # 当前已仿真时间，单位：毫秒
        simuTime = simuiface.simuTimeIntervalWithAcceMutiples()

        lVehiInfo = simuiface.getVehisInfoAggregated()
        # 上游初始占有率，固定
        lVehiInfoFromPartly=[]
        # 上游初始占有率平均值，固定
        O_bar=0
        # 下游占有率，实时每周期60s计算
        lVehiInfoToPartly=[]
        # 下游占有率平均值，实时每周期60s计算
        O_k_1=0
        K_r = 70
        if len(lVehiInfo) > 0:
            if 90*1000<simuTime < 110 * 1000:
                for vehiInfo in lVehiInfo:
                    vehiCollectorId = vehiInfo.collectorId
                    # 定位到示例路段上的排队计数器
                    if vehiCollectorId in [10, 11, 12]:
                        lVehiInfoFromPartly.append(vehiInfo.occupancy)
                        print("上游初始饱和占有率：", vehiInfo.collectorId, vehiInfo.occupancy)
                if (len(lVehiInfoFromPartly) > 0):
                    O_bar=sum(lVehiInfoFromPartly) / len(lVehiInfoFromPartly)
                    print("上游初始饱和平均占有率", O_bar)
            elif simuTime>110*1000:
                for vinfo in lVehiInfo:
                    vehiCollectorId = vinfo.collectorId
                    if vehiCollectorId in [7, 8, 9]:
                        lVehiInfoToPartly.append(vinfo.occupancy)
                        print("下游初始饱和占有率：", vinfo.collectorId, vinfo.occupancy)
                    if vehiCollectorId == 1:
                        self.vehiCount=vinfo.vehiCount
                        print("采集器1过车数：", vinfo.vehiCount)
                if (len(lVehiInfoToPartly) > 0):
                    O_k_1=sum(lVehiInfoToPartly) / len(lVehiInfoToPartly)
                    print("下游初始饱和平均占有率", O_k_1)
                    lVehiInfoToPartly.clear()
                    print("vehiCount",self.vehiCount)
                    r_k=self.vehiCount*60+K_r*((O_bar-O_k_1)/100)
                    # T=饱和车头时距
                    T=3600/1800
                    self.greenTime=int(round((r_k/60)*T) if round((r_k/60)*T) > 0 else 0)
                    print("greenTime",self.greenTime)
                    print("r_k",r_k)
                    # 设置信号灯绿灯时间
                    # 根据id获取信号灯
                    signalLamp = netiface.findSignalLamp(1)
                    print(signalLamp)
                    # 根据信号灯获取信控方案
                    signalPhase = signalLamp.signalPhase()
                    print(signalPhase)

                    # signalPhase= netiface.findSignalPhase(1) # todo 根据相位ID获取相位报错，返回值为None

                    color_list = []  # 按照红灯、绿灯顺序计算
                    color_list.append(Online.ColorInterval('红', 60 - self.greenTime))
                    color_list.append(Online.ColorInterval('绿', self.greenTime))
                    signalPhase.setColorList(color_list)




















