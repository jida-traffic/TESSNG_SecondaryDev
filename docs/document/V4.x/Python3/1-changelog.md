# 变更记录

## 接口变更列表

 【 2025-01-21 】

- TESSNG内核由V3.1升级到V4.0，二次开发版本可使用V4.0专业版基础功能
- 【新增】ISignalPlan 信控方案接口，支持多时段信控方案，替代原有的ISignalGroup接口
- 【新增】ISignalLamp 信号灯接口，支持创建信号灯并关联信号机
- 【新增】ICrosswalkSignalLamp 行人信号灯接口，支持创建信号灯并关联信号机
- 【新增】IRoadWorkZone 占道施工接口，支持创建施工区
- 【新增】IAccidentZone 事故区接口升级，包含IAccidentZoneInterval事故时段，可创建多时段不同参数的事故区。
- 【新增】ILimitedZone 限制区，其本身为借道施工的一部分，可不直接使用，但用户也可以用它做一些自定义的限制形式的区域
- 【新增】IReconstruction 借道施工（改扩建），可精细化生成，控制改扩建路网
- 【新增】IReduceSpeedArea 新增限速区接口，可支持分时段IReduceSpeedInterval分车型IReduceSpeedVehiType进行车道限速
- 【新增】新增收费站系列接口，包括收费车道ITollLane，收费决策点ITollDecisionPoint，收费路径ITollRouting，收费站停车点ITollPoint
- 【新增】停车场系列接口，包括停车位IParkingStall，停车区域IParkingRegion，停车决策点IParkingDecisionPoint，停车路径IParkingRouting
- 【新增】节点构建及路径重构接口IJunction，包括自动计算节点流向，输入观测值进行路径重构
- 【新增】行人面域接口包括：行人面域基类IPedestrian，障碍物面域IObstacleRegion，行人上下客面域IPassengerRegion，椭圆面域IPedestrianEllipseRegion，扇形面域IPedestrianFanShapeRegion，多边形面域IPedestrianPolygonRegion，矩形面域IPedestrianRectRegion，三角形面域IPedestrianTriangleRegion。
- 【新增】行人人行道面域IPedestrianSideWalkRegion接口
- 【新增】行人人行横道（斑马线）IPedestrianCrossWalkRegion接口
- 【新增】行人楼梯面域IPedestrianStairRegion接口
- 【新增】行人人行横道信号灯接口ICrosswalkSignalLamp
- 【新增】行人路径系列接口，包括：行人路径IPedestrianPath，行人路径途经点IPedestrianPathPoint
- 【新增】NetInterface类下新增IJunction节点对象的增删改查，新增行人面域，路径的增删改查，新增事故区，施工区，改扩建，限速区，信号灯，信号机，信控方案，星空相位的增删改查，新增停车场，收费站的增删改查
- 【新增】SimuInterface类下新增获取行人状态，运动中行人信息的接口，新增单步仿真的接口
- 【优化】统一接口的单位，V3系列默认像素制，V4常用的速度，长度等概念改为米制单位，并提供unitOfMeasure参数显示指定单位类型，从而避免繁琐的m2p, p2m的转化。

 

<!-- ex_nonav -->
