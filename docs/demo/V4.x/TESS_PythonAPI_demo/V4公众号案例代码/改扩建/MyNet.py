from DLLs.Tessng import PyCustomerNet, tessngIFace, Online, m2p, p2m


class MyNet(PyCustomerNet):
    def __init__(self):
        super(MyNet, self).__init__()
        self.iface = tessngIFace()
        self.netiface = self.iface.netInterface()

    def afterLoadNet(self):
        # 创建施工区
        rwz = self._create_road_work_zone()
        if rwz is not None:
            print("施工区创建成功")
            # 获取施工区信息
            rwz_id: int = rwz.id()
            # 施工区位置，单位：m
            location: float = p2m(rwz.location())
            # 施工区长度，单位：m
            zoneLength: float = p2m(rwz.zoneLength())
            # 施工区上游警示区长度，单位：m
            upCautionLength: float = p2m(rwz.upCautionLength())
            # 施工区上游过渡区长度，单位：m
            upTransitionLength: float = p2m(rwz.upTransitionLength())
            # 施工区缓冲区长度，单位：m
            upBufferLength: float = p2m(rwz.upBufferLength())
            # 施工区下游过渡区长度，单位：m
            downTransitionLength: float = p2m(rwz.downTransitionLength())
            # 施工区下游终止区长度，单位：m
            downTerminationLength: float = p2m(rwz.downTerminationLength())
            # 施工区持续时间，单位：s
            duration: float = rwz.duration()
            # 施工区限速，单位：m/s
            limitSpeed: float = rwz.limitSpeed()
            print("施工区信息：")
            print(f"\t施工区ID：{rwz_id}")
            print(f"\t施工区位置：{location}m")
            print(f"\t施工区长度：{zoneLength}m")
            print(f"\t施工区上游警示区长度：{upCautionLength}m")
            print(f"\t施工区上游过渡区长度：{upTransitionLength}m")
            print(f"\t施工区上游缓冲区长度：{upBufferLength}m")
            print(f"\t施工区下游过渡区长度：{downTransitionLength}m")
            print(f"\t施工区下游终止区长度：{downTerminationLength}m")
            print(f"\t施工区持续时间：{duration}s")
            print(f"\t施工区限速：{limitSpeed*3.6}km/h")

            # 创建借道
            reconstruction = self._create_reconstruction(rwz.id())
            if reconstruction is not None:
                print("借道创建成功")
                # 获取借道信息
                reconstruction_id: int = reconstruction.id()
                # 借用的车道数
                borrowedNum: int = reconstruction.borrowedNum()
                # 保通开口长度，单位：m
                passagewayLength: float = p2m(reconstruction.passagewayLength())
                # 保通开口限速，单位：m/s
                passagewayLimitedSpeed: int = reconstruction.passagewayLimitedSpeed()
                print("借道信息：")
                print(f"\t借道ID：{reconstruction_id}")
                print(f"\t借用的车道数：{borrowedNum}")
                print(f"\t保通开口长度：{passagewayLength}m")
                print(f"\t保通开口限速：{passagewayLimitedSpeed*3.6}km/h")

    def _create_road_work_zone(self):
        param = Online.DynaRoadWorkZoneParam()
        # 施工区名称
        param.name = "施工区"
        # 施工区所在路段ID
        param.roadId = 1
        # 施工区开始位置
        param.location = m2p(2500)
        # 施工区长度，单位：m
        param.length = m2p(50)
        # 施工区上游警示区长度，单位：m
        param.upCautionLength = m2p(70)
        # 施工区上游过渡区长度，单位：m
        param.upTransitionLength = m2p(60)
        # 施工区缓冲区长度，单位：m
        param.upBufferLength = m2p(50)
        # 施工区下游过渡区长度，单位：m
        param.downTransitionLength = m2p(40)
        # 施工区下游终止区长度，单位：m
        param.downTerminationLength = m2p(30)
        # 施工区占用车道序号，从右向左，从0开始
        param.mlFromLaneNumber = [3]
        # 开始时间，单位：s
        param.startTime = 0
        # 持续时间，单位：s
        param.duration = 400
        # 限速，单位：km/h
        param.limitSpeed = 72
        # 创建施工区
        rwz = self.netiface.createRoadWorkZone(param)
        return rwz

    def _create_reconstruction(self, rwz_id: int):
        param = Online.DynaReconstructionParam()
        # 施工区ID
        param.roadWorkZoneId = rwz_id
        # 被借道的路段ID
        param.beBorrowedLinkId = 2
        # 被借道的车道数量
        param.borrowedNum = 1
        # 保通开口长度，单位：m
        param.passagewayLength = 80
        # 保通开口限速，单位：m/s
        param.passagewayLimitedSpeed = 40 / 3.6
        # 创建借道
        reconstruction = self.netiface.createReconstruction(param)
        return reconstruction
