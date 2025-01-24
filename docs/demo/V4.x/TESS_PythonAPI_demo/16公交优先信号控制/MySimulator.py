from PySide2.QtCore import *
from shiboken2.shiboken2 import wrapInstance

from Tessng import TessInterface, SimuInterface, PyCustomerSimulator, IVehicle, ILink
from Tessng import m2p, p2m, tngIFace, tngPlugin
from Tessng import *
import random
from datetime import datetime

#  这个是你预估公交从感应发现它到它经过进口要多久，如果要走6、7秒就要控制改变至8秒左右，早断或延长，我这边好像没有考虑插入的情况
tChange = 8


# 用户插件子类，代表用户自定义与仿真相关的实现逻辑，继承自PyCustomerSimulator
#     多重继承中的父类QObject，在此目的是要能够自定义信号signlRunInfo
class MySimulator(QObject, PyCustomerSimulator):
    signalRunInfo = Signal(str)
    def __init__(self):
        QObject.__init__(self)
        PyCustomerSimulator.__init__(self)
        # 车辆方阵的车辆数
        self.mrSquareVehiCount = 28
        # 飞机速度，飞机后面的车辆速度会被设定为此数据
        self.mrSpeedOfPlane = 0
        #  下面这些解释过了就不说了
        self.phaseChange = 0
        self.__mTg2r = 30
        self.__mTr2g = 0
        self.__mrT1 = 0
        self.__mrT2BusArrival = 0
        #  公交车车速信息，用于判断是否有公交车经过
        self.__mlOutVehiInfo = []
        

    #  上面的东西都没啥关系，从这里开始
    def calcLampColor(self, signalLamp):
        iface = tngIFace()
        simuiface = iface.simuInterface()
        batchNum = simuiface.batchNumber()
        #  当前已仿真时间，单位：毫秒
        simuTime = iface.simuInterface().simuTimeIntervalWithAcceMutiples()

        #  当前的仿真时间，为t
        t = simuTime/1000
        #  获取信号灯相位号
        signalePhaseId = signalLamp.signalPhase().id()
        # print("signalePhaseId={}".format(signalePhaseId))
        empty = []

        #  判断是否有公交车到达
        if self.__mrT1 != t:
            self.__mrT1 = t
            #  mrT1用来储存当前的仿真秒数，如果储存的仿真秒数不等于当前，则证明仿真时间已经往前走了，可以处理数据
            if self.__mlOutVehiInfo != empty:
                # print(self.__mlOutVehiInfo[0].avgSpeed)
                if self.__mlOutVehiInfo[0].avgSpeed < 200:
                    self.__mrT2BusArrival = t
            #  清除数据，如果不做清除，会导致变卡数据会一直存储，后面调用的时候也会有问题
            self.__mlOutVehiInfo.clear()

        #  判断仿真时间是否在可延长的绿色相位内
        #  根据情况的不同要计算的量有，红变绿的时刻、绿变红的时刻、还有可能要算的是相位改变量
        if (self.__mrT2BusArrival > (self.__mTg2r - tChange) and self.__mrT2BusArrival < self.__mTg2r):
            self.__mTg2r += tChange
            self.phaseChange += tChange
            self.__mTr2g = 90 + self.__mTg2r
        #  改下个相位的时刻
        elif (self.__mrT1 >= self.__mTr2g and self.__mTg2r >= self.__mrT1):
            self.__mTg2r = self.__mTg2r
            self.__mTr2g = 90 + self.__mTg2r

        #  判断仿真时间是否在可缩短的红色相位内
        if (self.__mrT2BusArrival > self.__mTr2g - 35 and self.__mrT2BusArrival < self.__mTr2g - tChange):
            self.__mTr2g = self.__mrT1 + tChange
            self.phaseChange = self.__mrT1 + tChange  #  这里相当于重置改变量
            self.__mTg2r = 30 + self.__mTr2g

        elif ((self.__mTg2r < self.__mrT1 and self.__mrT1 < self.__mTr2g - 35) or (self.__mrT1 >= self.__mTr2g - tChange and self.__mrT1 < self.__mTr2g)):
            self.__mTr2g = self.__mTr2g
            self.__mTg2r = 30 + self.__mTr2g

        #  在相位时刻为0＋累积相位改变量的时刻，西向信号灯（也既公交车进入的信号灯）转为绿色，北向信号灯转为红色
        w2GandN2R = (0 + self.phaseChange) % 120
        #  在相位时刻为0＋累积相位改变量的时刻，南向信号灯转为绿色，西向信号灯转为红色
        s2GandW2R = (30 + self.phaseChange) % 120
        #  在相位时刻为0＋累积相位改变量的时刻，东向信号灯转为绿色，南向信号灯转为红色
        e2GandS2R = (60 + self.phaseChange) % 120
        #  在相位时刻为0＋累积相位改变量的时刻，北向信号灯转为绿色，东向信号灯转为红色
        n2GandE2R = (90 + self.phaseChange) % 120


        #  这里是分界，从这里往上的分部是用于计算最关键的几个值，w2GandN2R等，就是算出这些值用这些值和当前时刻进行对比，如果符合这些值就进行改变
        #  从这里往下的部分就是单纯的根据时刻与上面计算的各个进口需要的值的对比，然后进行颜色的改变
        #  南向和东向的初始变化
        if t == 0:
            #  这里需要加入判断是否为南向向信号灯的语句，如果是则改变红色
            if signalePhaseId == 418:#  这种是相位id去看路网信息里面有
                signalLamp.setLampColor("红")
            #  同上需加入东向判断
            if signalePhaseId == 415:
                signalLamp.setLampColor("红")
        #  下面这些和上面一样，判断不同进口方向的变化
        if t % 120 == w2GandN2R - 3:
            if signalePhaseId == 417:
                signalLamp.setLampColor("黄")
        elif t % 120 == s2GandW2R - 3:
            if signalePhaseId == 416:
                signalLamp.setLampColor("黄")
        elif t % 120 == e2GandS2R - 3:
            if signalePhaseId == 418:
                signalLamp.setLampColor("黄")
        elif t % 120 == n2GandE2R - 3:
            if signalePhaseId == 415:
                signalLamp.setLampColor("黄")

        #  判断当前模拟时间与某个信号灯的转变时刻相符，并改变那个信号灯颜色
        if t % 120 == w2GandN2R:
            if signalePhaseId == 416:
                signalLamp.setLampColor("绿")
            if signalePhaseId == 417:
                signalLamp.setLampColor("红")
        elif t % 120 == s2GandW2R:
            if signalePhaseId == 418:
                signalLamp.setLampColor("绿")
            if signalePhaseId == 416:
                signalLamp.setLampColor("红")
        elif t % 120 == e2GandS2R:
            if signalePhaseId == 415:
                signalLamp.setLampColor("绿")
            if signalePhaseId == 418:
                signalLamp.setLampColor("红")
        elif t % 120 == n2GandE2R:
            if signalePhaseId == 417:
                signalLamp.setLampColor("绿")
            if signalePhaseId == 415:
                signalLamp.setLampColor("红")

        return True


    # # 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法，大量用户逻辑在此实现，注意耗时大的计算要尽可能优化，否则影响运行效率
    def afterOneStep(self):
        #= == == == == == =以下是获取一些仿真过程数据的方法 == == == == == ==
        # TESSNG 顶层接口
        iface = tngIFace()
        # TESSNG 仿真子接口
        simuiface = iface.simuInterface()
        # TESSNG 路网子接口
        netiface = iface.netInterface()
        # 当前仿真计算批次
        batchNum = simuiface.batchNumber()
        # 当前已仿真时间，单位：毫秒
        simuTime = simuiface.simuTimeIntervalWithAcceMutiples()
        # 开始仿真的现实时间
        startRealtime = simuiface.startMSecsSinceEpoch()
        print("信号灯组相位颜色")
        # 信号灯组相位颜色
        lPhoneColor = simuiface.getSignalPhasesColor()
        print("信号灯组相位颜色", [(pcolor.signalGroupId, pcolor.phaseNumber, pcolor.color, pcolor.mrIntervalSetted, pcolor.mrIntervalByNow) for pcolor in lPhoneColor])

        # 获取当前仿真时间完成穿越采集器的所有车辆信息
        lVehiInfo = simuiface.getVehisInfoCollected()    #Tessng.pyi里定义有两个参数，实际不需要

        if len(lVehiInfo) > 0 and lVehiInfo[0].vehiType == 2:
            self.__mlOutVehiInfo.append(lVehiInfo[0])
            print("车辆信息采集器采集信息：", [(vinfo.collectorId, vinfo.vehiId) for vinfo in lVehiInfo])






















