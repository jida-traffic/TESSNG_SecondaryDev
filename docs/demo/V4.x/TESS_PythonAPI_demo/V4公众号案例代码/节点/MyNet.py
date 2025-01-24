# -*- coding: utf-8 -*-
import time
import PySide2
from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *

from Tessng import TessInterface, TessPlugin, NetInterface, PyCustomerNet
from Tessng import tessngPlugin, tessngIFace, m2p, p2m
from Tessng import NetItemType, GraphicsItemPropName




# 用户插件子类，代表用户自定义与路网相关的实现逻辑，继承自MyCustomerNet
class MyNet(PyCustomerNet):
    def __init__(self):
        super(MyNet, self).__init__()
        iface = tessngIFace()
        netiface = iface.netInterface()
    #
    # # 过载的父类方法，当打开网后TESS NG调用此方法
    # #     实现的逻辑是：路网加载后获取路段数，如果路网数为0则调用方法createNet构建路网，之后再次获取路段数，如果大于0则启动仿真
    def afterLoadNet(self):
        # 代表TESS NG的接口
        iface = tessngIFace()
        # 代表TESS NG的路网子接口
        netiface = iface.netInterface()

        if len(netiface.links()) == 0:
            return

        # # step1：创建节点
        x1 = -1000
        y1 = -40
        x2 = -800
        y2 = 180
        junctionName = 'newJunction'
        res = netiface.createJunction(QPointF(m2p(x1), m2p(y1)), QPointF(m2p(x2), m2p(y2)), junctionName)
        print(f"createJunction: {junctionName} done!")
        time.sleep(3)

        # # step2：创建静态路径
        netiface.buildAndApplyPaths(3)    # 设置每个OD最多搜索3条路径
        netiface.reSetDeciPoint()    # 优化决策点位置
        for dp in netiface.decisionPoints():
            for routing in dp.routings():
                netiface.reSetLaneConnector(routing)    # 优化路径中的车道连接
        print(f"buildAndApplyPaths done!")
        time.sleep(3)


        # # step3：为节点中每个转向设置流量
        # 由于没有已知值，这里针对不同转向类型为其赋流量初值
        turnVolumeReduct = {'左转': 400, '直行': 1200, '右转': 200, '掉头': 0}
        # # step3-1：添加流量时间段
        timeInterval = netiface.addFlowTimeInterval()
        timeId = timeInterval.timeId
        startTime = 0
        endTime = 3600
        netiface.updateFlowTimeInterval(timeId, startTime, endTime)
        # # step3-2：遍历转向，为其设置流量
        junctions = netiface.getAllJunctions()
        for junction in junctions:
            junctionId = junction.getId()
            for turning in junction.getAllTurnningInfo():
                turningId = turning.turningId
                turnType = turning.strTurnType
                inputVolume = turnVolumeReduct.get(turnType, 0)
                # 为该转向设置输入流量
                netiface.updateFlow(timeId, junctionId, turningId, inputVolume)
        print(f"updateFlow done!")


        # # step4：进行流量分配计算
        # 设置BPR路阻函数参数，流量分配算法参数
        theta = 0.1
        bpra = 0.15
        bprb = 4
        maxIterateNum = 300
        netiface.updateFlowAlgorithmParams(theta, bpra, bprb, maxIterateNum)    # 更新计算参数
        result = netiface.calculateFlows()    # 计算路径流量分配并应用，返回分配结果
        print(f"calculateFlows done!")
        time.sleep(3)

        # 解析流量分配结果
        for timeId, turningFlow in result.items():
            for i in turningFlow:
                junction = i.pJunction.getId()
                turning = f"{i.turningBaseInfo.strDirection}-{i.turningBaseInfo.strTurnType}"
                inputVolume = i.inputFlowValue    # 该转向输入流量
                realVolume = i.realFlow    # 该转向实际分配到的流量
                relativeError = i.relativeError    # 分配的相对误差
                interval = i.flowTimeInterval
                startTime = interval.startTime
                endTime = interval.endTime
                resultJson = {'节点': junction, '转向': turning, '输入流量': inputVolume, '分配流量': realVolume, '相对误差': relativeError}
                print(f"{startTime}-{endTime}: {resultJson}")


