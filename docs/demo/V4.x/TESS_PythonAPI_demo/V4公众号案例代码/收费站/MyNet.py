from DLLs.Tessng import PyCustomerNet, tessngIFace, Online, m2p, p2m


class MyNet(PyCustomerNet):
    def __init__(self):
        super(MyNet, self).__init__()
        self.iface = tessngIFace()
        self.netiface = self.iface.netInterface()
        self.simuiface = self.iface.simuInterface()

    def afterLoadNet(self):
        # 创建停车分布
        time_dis_list = [
            self._create_mtc_time_detail(3, 1),
            self._create_mtc_time_detail(5, 1),
        ]
        speed_dis_list: list = [
            self._create_etc_speed_detail(15, 1),
            self._create_etc_speed_detail(20, 1),
        ]
        # 车型ID
        vehicle_type_id: int = 1
        toll_parking_time = self._create_toll_parking_time(vehicle_type_id, time_dis_list, speed_dis_list)
        toll_parking_time_dis = self._create_toll_parking_time_dis([toll_parking_time], "新建停车分布")
        print(f"创建了停车分布，id={toll_parking_time_dis.id}\n")

        # 创建收费车道
        toll_lanes = []
        for lane_number in range(12):
            # 创建收费区域
            toll_point = self._create_toll_point(m2p(40), toll_type=1)
            link_id: int = 1
            location: float = m2p(30)
            length: float = m2p(40)
            toll_lane = self._create_toll_lane(link_id, location, length, lane_number, 0, 3600, [toll_point])
            toll_lanes.append(toll_lane)
            print(f"在序号为{lane_number}的车道上创建了收费车道，id={toll_lane.id()}")
        print()

        # 创建收费路径决策点
        link_id: int = 2
        location: float = m2p(30)
        toll_deci_point = self._create_toll_decision_point(link_id, location)
        print(f"创建了收费路径决策点，id={toll_deci_point.id()}\n")

        # 创建收费路径
        for toll_lane in toll_lanes:
            toll_routing = self._create_toll_routing(toll_deci_point, toll_lane)
            print(f"在收费路径决策点{toll_deci_point.id()}创建了收费路径，id={toll_routing.id()}")
        print()

        # 更新收费决策点的车型对车道选择的分布比重
        toll_routing_id = toll_routing.id()
        toll_lane_id = toll_lane.id()
        toll_deci_point = self.netiface.tollDecisionPoints()[0]
        ect_toll_info = self._create_ect_toll_info(toll_routing_id, toll_lane_id, 0.5)
        vehicle_dis_detail = self._create_vehicle_toll_dis_detail(toll_routing_id, toll_lane_id, 0.5)
        vehicle_dis_info = self._create_vehicle_toll_dis_info(1, [vehicle_dis_detail])
        routing_dis_info = self._create_routing_dis_info([ect_toll_info], [vehicle_dis_info])
        toll_dis_info = self._create_toll_dis_info(None, [routing_dis_info])
        toll_deci_point.updateTollDisInfoList([toll_dis_info])
        print(f"更新了收费决策点的车型对车道选择的分布比重\n")

        # 查看所有收费停车分布
        toll_parking_time_dis = self.netiface.tollParkingTimeDis()
        print(f"收费停车分布的数量是：{len(toll_parking_time_dis)}")
        # 查看所有收费车道
        toll_lanes = self.netiface.tollLanes()
        print(f"收费车道的数量是：{len(toll_lanes)}")
        # 查看所有收费路径决策点
        toll_deci_points = self.netiface.tollDecisionPoints()
        print(f"收费路径决策点的数量是：{len(toll_deci_points)}")

        # 查看收费分布信息列表
        print("收费分布信息列表")
        toll_deci_point = self.netiface.tollDecisionPoints()[0]
        toll_dis_info_list = toll_deci_point.tollDisInfoList()
        for toll_dis_info in toll_dis_info_list:
            # 静态决策路径
            route = toll_dis_info.pIRouting
            print(f"\t静态决策路径：{route}")
            # 收费需求设置
            dis_toll_info_list = toll_dis_info.disTollInfoList
            for dis_toll_info in dis_toll_info_list:
                # 开始时间，单位：秒
                start_time = dis_toll_info.startTime
                # 结束时间，单位：秒
                end_time = dis_toll_info.endTime
                print(f"\t\t开始时间：{start_time}s")
                print(f"\t\t结束时间：{end_time}s")
                # 用户编辑混合车道的ETC车辆比率
                ect_toll_info_list = dis_toll_info.ectTollInfoList
                for ect_toll_info in ect_toll_info_list:
                    # 收费路径编号
                    toll_routing_id = ect_toll_info.tollRoutingID
                    # 收费车道编号
                    toll_lane_id = ect_toll_info.tollLaneID
                    # 车道内ETC占比
                    etc_ratio = ect_toll_info.etcRatio
                    print(f"\t\t\t收费路径ID：{toll_routing_id}")
                    print(f"\t\t\t\t收费车道ID：{toll_lane_id}")
                    print(f"\t\t\t\tETC占比：{etc_ratio}")
                # 车型对车道选择的分布比重
                vehicle_dis_info_list = dis_toll_info.vehicleDisInfoList
                for vehicle_dis_info in vehicle_dis_info_list:
                    # 车型ID
                    vehicle_type_id = vehicle_dis_info.vehicleType
                    print(f"\t\t\t车型ID：{vehicle_type_id}")
                    # 选择分布列表
                    choice_dis_list = vehicle_dis_info.list
                    for choice_dis in choice_dis_list:
                        # 收费路径编号
                        toll_routing_id = choice_dis.tollRoutingID
                        # 收费车道编号
                        toll_lane_id = choice_dis.tollLaneID
                        # 车辆分布比重
                        prop = choice_dis.prop
                        print(f"\t\t\t\t收费路径ID：{toll_routing_id}")
                        print(f"\t\t\t\t收费车道ID：{toll_lane_id}")
                        print(f"\t\t\t\t分布比重：{prop}")
        print()

    def _create_mtc_time_detail(self, time: float, prop: float):
        """创建MTC时间分布"""
        mtc_time_detail = Online.TollStation.DynaMtcTimeDetail()
        # 停车收费时长，单位：m
        mtc_time_detail.time = time
        # 占比
        mtc_time_detail.prop = prop
        return mtc_time_detail

    def _create_etc_speed_detail(self, speed: float, prop: float):
        """创建ETC速度分布"""
        etc_speed_detail = Online.TollStation.DynaEtcSpeedDetail()
        # 通过速度，单位：km/h
        etc_speed_detail.limitSpeed = speed
        # 占比
        etc_speed_detail.prop = prop
        return etc_speed_detail

    def _create_toll_parking_time(self, vehicle_type_id: int, time_dis_list: list, speed_dis_list: list):
        """为一种车型创建MTC停车时间分布和ECT速度分布"""
        toll_parking_time = Online.TollStation.DynaTollParkingTime()
        # 车型ID
        toll_parking_time.vehicleTypeId = vehicle_type_id
        # MTC时间分布
        toll_parking_time.timeDisList = time_dis_list
        # ETC速度分布
        toll_parking_time.speedDisList = speed_dis_list
        return toll_parking_time

    def _create_toll_parking_time_dis(self, parking_time_list: list, name: str = ""):
        """创建收费时间分布"""
        toll_parking_time_dis = Online.TollStation.DynaTollParkingTimeDis()
        # 收费时间分布的名称
        toll_parking_time_dis.name = name
        # 各车型的分布
        toll_parking_time_dis.parkingTimeList = parking_time_list
        time_dis = self.netiface.createTollParkingTimeDis(toll_parking_time_dis)
        return time_dis

    def _create_toll_point(self, distance: float, toll_type: int = 1, time_dis_id: int = 1):
        """创建收费区域"""
        TollStation = Online.TollStation.DynaTollPoint()
        # 距离路段起点的距离，单位：m
        TollStation.location = distance
        # 收费类型：1=MTC，2=ETC，3=ETC&MTC
        TollStation.tollType = toll_type
        # 停车时间分布的ID
        TollStation.timeDisId = time_dis_id
        # 是否启用
        TollStation.enable = True
        return TollStation

    def _create_toll_lane(self, link_id: int, location: float, length: float, lane_number: int, start_time: float, end_time: float, toll_point_list: list, toll_point_length: float = 8):
        """创建收费车道"""
        toll_lane_param = Online.TollStation.DynaTollLane()
        # toll_lane_param.name = "123"
        # 路段ID
        toll_lane_param.roadId = link_id
        # 距离路段起点的距离，单位：m
        toll_lane_param.location = location
        # 收费车道长度，单位：m
        toll_lane_param.length = length
        # 车道序号，从右向左，从0开始
        toll_lane_param.laneNumber = lane_number
        # 开始时间，单位：秒
        toll_lane_param.startTime = start_time
        # 结束时间，单位：秒
        toll_lane_param.endTime = end_time
        # 收费区域列表
        toll_lane_param.tollPoint = toll_point_list
        # 收费区域长度
        toll_lane_param.tollPointLen = toll_point_length
        # 创建收费车道
        toll_lane = self.netiface.createTollLane(toll_lane_param)
        return toll_lane

    def _create_toll_decision_point(self, link_id: int, location: float, name: str = ""):
        """创建收费路径决策点"""
        link = self.netiface.findLink(link_id)
        if not link:
            return None
        toll_deci_point = self.netiface.createTollDecisionPoint(link, location, name)
        return toll_deci_point

    def _create_toll_routing(self, toll_deci_point, toll_lane):
        """创建收费路径"""
        toll_routing = self.netiface.createTollRouting(toll_deci_point, toll_lane)
        return toll_routing

    def _create_ect_toll_info(self, toll_routing_id: int, toll_lane_id: int, etc_ratio: float):
        """创建ETC收费信息"""
        ect_toll_info = Online.TollStation.DynaEtcTollInfo()
        ect_toll_info.tollRoutingID = toll_routing_id
        ect_toll_info.tollLaneID = toll_lane_id
        ect_toll_info.etcRatio = etc_ratio
        return ect_toll_info

    def _create_vehicle_toll_dis_detail(self, toll_routing_id: int, toll_lane_id: int, prop: float):
        """创建某一车型对某一车道选择的分布比重"""
        vehicle_dis_info = Online.TollStation.DynaVehicleTollDisDetail()
        vehicle_dis_info.toll_routing_id = toll_routing_id
        vehicle_dis_info.tollLaneID = toll_lane_id
        vehicle_dis_info.prop = prop
        return vehicle_dis_info

    def _create_vehicle_toll_dis_info(self, vehicle_type_id: int, vehicle_toll_dis_detail_list: list):
        """创建某一车型对所有车道选择的分布比重"""
        vehicle_dis_info = Online.TollStation.DynaVehicleTollDisInfo()
        vehicle_dis_info.vehicleType = vehicle_type_id
        vehicle_dis_info.list = vehicle_toll_dis_detail_list
        return vehicle_dis_info

    def _create_routing_dis_info(self, ect_toll_info_list: list, vehicle_dis_info_list: list):
        """创建路径分布信息"""
        routing_dis_info = Online.TollStation.DynaRoutingDisTollInfo()
        # routing_dis_info.startTime = start_time
        # routing_dis_info.endTime = end_time
        routing_dis_info.ectTollInfoList = ect_toll_info_list
        routing_dis_info.vehicleDisInfoList = vehicle_dis_info_list
        return routing_dis_info

    def _create_toll_dis_info(self, route, routing_dis_info_list: list):
        """创建收费信息"""
        toll_dis_info = Online.TollStation.DynaTollDisInfo()
        toll_dis_info.pIRouting = route
        toll_dis_info.disTollInfoList = routing_dis_info_list
        return toll_dis_info
