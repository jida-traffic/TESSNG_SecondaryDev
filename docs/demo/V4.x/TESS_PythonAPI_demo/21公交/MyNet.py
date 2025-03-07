# -*- coding: utf-8 -*-



import Tessng
from Tessng import PyCustomerNet, tngIFace, m2p


# 用户插件子类，代表用户自定义与路网相关的实现逻辑，继承自MyCustomerNet
class MyNet(PyCustomerNet):

    def __init__(self):
        super(MyNet, self).__init__()
        self.iface = tngIFace()
        self.netiface = self.iface.netInterface()


    def afterLoadNet(self) -> None:
        self.netiface.buildNetGrid(5)

        link1: Tessng.ILink = self.netiface.findLink(1)
        link104: Tessng.ILink = self.netiface.findLink(104)
        link2: Tessng.ILink = self.netiface.findLink(2)
        link103: Tessng.ILink = self.netiface.findLink(103)
        print("step1: create bus line")
        busLine1_LinkIds= [link1,link104]
        busline2_linkids=[link2,link103]
        busline1:Tessng.IBusLine = self.netiface.createBusLine(busLine1_LinkIds)
        busline2: Tessng.IBusLine = self.netiface.createBusLine(busline2_linkids)

        print("step2: 创建公交站台, 每条link的最右侧车道新建一个站台，站台位置距离link起点500m,站台长度20m")

        for lane in link1.lanes():
            if lane.number() == 0:
                busStation1: Tessng.IBusStation = self.netiface.createBusStation(lane,20,500, "busStation1")

        for lane in link2.lanes():
            if lane.number() == 0:
                busStation2: Tessng.IBusStation = self.netiface.createBusStation(lane,20,500, "busStation2")

        for lane in link103.lanes():
            if lane.number() == 0:
                busStation103: Tessng.IBusStation = self.netiface.createBusStation(lane,20,500, "busStation3")


        for lane in link104.lanes():
            if lane.number() == 0:
                busStation104: Tessng.IBusStation = self.netiface.createBusStation(lane,20,500, "busStation4")



        print("step3: 将公交站与公交线路绑定")
        self.netiface.addBusStationToLine(busline1, busStation1)
        self.netiface.addBusStationToLine(busline1, busStation104)

        self.netiface.addBusStationToLine(busline2, busStation2)
        self.netiface.addBusStationToLine(busline2, busStation103)

        print("step4: 编辑公交站点基础信息（公交站点，当前公交线路，基础停靠时间，下客百分比，上车时间，下车时间）")
        self.netiface.editBusStationBasicInformation(busline1, busStation1,5,10,30,30)
        self.netiface.editBusStationBasicInformation(busline1, busStation104, 5, 10, 60, 60)
        self.netiface.editBusStationBasicInformation(busline2, busStation2, 10, 5, 40, 20)
        self.netiface.editBusStationBasicInformation(busline2, busStation103, 10, 5, 40, 20)


        print("step5: 编辑站点与线路关联信息（公交站点，公交线路起终止时间，到站人数）")
        passengerArrivingInfoList = []
        passengerArrivingInfo = Tessng.Online.PassengerArrivings()
        # passengerArrivingInfo.passengerArrivingID = # 公交站点，公交线路关联id， 其为数据库自动常见的，不用赋值
        passengerArrivingInfo.endTime = 1800 # 线路结束时间
        passengerArrivingInfo.startTime = 0  # 线路开始时间
        passengerArrivingInfo.passengerCount = 100  # 到站人数
        passengerArrivingInfoList.append(passengerArrivingInfo)


        passengerArrivingInfo2 = Tessng.Online.PassengerArrivings()
        # passengerArrivingInfo.passengerArrivingID = # 公交站点，公交线路关联id， 其为数据库自动常见的，不用赋值
        passengerArrivingInfo2.endTime = 3600 # 线路结束时间
        passengerArrivingInfo2.startTime = 1801  # 线路开始时间
        passengerArrivingInfo2.passengerCount = 100  # 到站人数
        passengerArrivingInfoList.append(passengerArrivingInfo2)


        self.netiface.editBusStation(busline1, busStation1, passengerArrivingInfoList)
        self.netiface.editBusStation(busline1, busStation104, passengerArrivingInfoList)
        self.netiface.editBusStation(busline2, busStation2, passengerArrivingInfoList)
        self.netiface.editBusStation(busline2, busStation103, passengerArrivingInfoList)

        print("step6: 公交线路上公交发车信息")
        busline1.setName("公交1路")
        busline1.setDispatchStartTime(0)
        busline1.setDispatchEndTime(3600)
        busline1.setDesirSpeed(14) # 公交车期望速度 m/s
        busline1.setDispatchFreq(200) # 公交发车间隔，秒
        busline1.setPassCountAtStartTime(20) # 初始载客人数

        busline2.setName("公交2路")
        busline2.setDispatchStartTime(300)
        busline2.setDispatchEndTime(3000)
        busline2.setDesirSpeed(14) # 公交车期望速度 m/s
        busline2.setDispatchFreq(500) # 公交发车间隔，秒
        busline2.setPassCountAtStartTime(30) # 初始载客人数