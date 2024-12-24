Ø **bool queueRecently(long queueCounterId, qreal& queueLength, int& vehiCount)**

获取排队计数器最近一次排队信息

参数：

[in] queueCounterId：排队计数器ID

[out] queueLength：排队长度

[out] vehiCount：排队车辆数

返回：是否获取成功

Ø **QList<Online::Pedestrian::PedestrianStatus> getPedestriansStatusByRegionId(long regionId)**

根据行人面域id获取当前时间面域上所有行人的状态信息

参数：

[in] regionId：面域ID

返回：行人状态信息列表

Ø **QList<IPedestrian*> allPedestrianStarted()**

获取所有正在运行的行人

返回：行人对象列表
