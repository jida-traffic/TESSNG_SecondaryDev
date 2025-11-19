/****************************************************************************
* 支持在线仿真的几种数据结构，用于在仿真过程中参数的动态输入以及仿真结果的动态输出
*****************************************************************************/

#ifndef __DataStruct__
#define __DataStruct__

#include "../tessinterfaces_global.h"

#include <QObject>
#include <QPointF>
#include <QVector2D>
#include <QVector3D>
#include <QColor>
#include <QList>
#include <QJsonObject>
#include <QJsonArray>
#include <mutex>
#include <vector>

class ILaneObject;
class ILaneConnector;
class IVehicle;
class IPedestrianRegion;
class IRouting;
class ILink;
class IConnector;
class IJunction;

namespace Online {
	/*
	机动车或非机动车分类
	*/
	enum MotorOrNonmotor { Motor, Nonmotor };

	/*
	直行：straight
	左转：turn left
	右转：right
	直行或左转：straight and left
	直行或右转：straight and right
	直行左转或右转：straight and left and right
	左转或右转：left and right
	掉头：U turn
	直行或掉头：Straight and u turn
	左转或掉头：Left and u turn
	*/
	enum GuideArrowType { Straight, Left, Right, StraightLeft, StraightRight, StraightLeftRight, LeftRight, Uturn, StraightUturn, LeftUturn };

	/*
	部分接口方法调用返回类型 success：是否成功，message：success为false时保存错误信息，one：保存当前操作对象，list:保存相关对象列表
	*/
	struct TESSINTERFACES_EXPORT CallResult {
		bool success;
		QString message;
		QVariant one;
		QList<QVariant> list;
		CallResult() {
			success = false;
		}
	};

	/*
	仿真参数
	*/
	struct TESSINTERFACES_EXPORT SimuConfig {
		int simuTimeInterval;
		int acceMultiples;
		int simuAccuracy;
		int threadCount;
		SimuConfig() {
			simuTimeInterval = -1;
			acceMultiples = -1;
			simuAccuracy = -1;
			threadCount = -1;
		}
	};

	/*
	车型组成 vehiTypeCode:车型编码，proportion：分配比
	*/
	struct TESSINTERFACES_EXPORT VehiComposition {
		/*车型编码*/
		long vehiTypeCode;
		/*分配比*/
		qreal proportion;
		VehiComposition(long c, qreal p) {
			vehiTypeCode = c;
			proportion = p;
		}
	};

	/*
	车型组成列表
	*/
	struct TESSINTERFACES_EXPORT VehicleCompositionList {
		/*车型组成编号*/
		long vehicleConsCode;
		/*车辆组成名称 */
		QString name;
		/*车型组成详细列表*/
		QList<VehiComposition> lVehiComposition;
	};

	/*
	发车间隔 dispatchId:发车点ID，toTime:该时间段结束时间(秒), vehiCount:发车数
	*/
	struct TESSINTERFACES_EXPORT DispatchInterval {
		/*发车间隔ID*/
		long departureIntervald;
		/*发车点ID*/
		long dispatchId;
		/*起始时间 单位秒*/
		long fromTime;
		/*结束时间 单位秒*/
		long toTime;
		/*车辆数*/
		int vehiCount;
		/*车辆组成ID*/
		long vehiConsId;
		/*车型组成详细列表*/
		QList<VehiComposition> mlVehicleConsDetail;

		//为java二次开发设置此方法
		void setVehicleConsDetails(QList<VehiComposition> lDetail) {
			mlVehicleConsDetail = lDetail;
		}
	};

	/*
	路径流量分配比
	*/
	struct TESSINTERFACES_EXPORT RoutingFlowRatio {
		/* 路径编号 */
		long routingID;
		/* 分配比 */
		qreal ratio;
		RoutingFlowRatio(long id, qreal r) {
			routingID = id;
			ratio = r;
		}
	};

	/*
	一个决策点某个时段各路径车辆分配比
	*/
	struct TESSINTERFACES_EXPORT DecipointFlowRatioByInterval {
		/*决策点编号*/
		long deciPointID;
		/* 起始时间 单位秒*/
		long startDateTime;
		/* 结束时间 单位秒*/
		long endDateTime;
		QList<RoutingFlowRatio> mlRoutingFlowRatio;

		//为java二次开发设置此方法
		void setRoutingFlowRatios(QList<RoutingFlowRatio> lRoutingFlowRatio) {
			mlRoutingFlowRatio = lRoutingFlowRatio;
		}
	};

	/*
	车辆状态含轨迹
	*/
	struct TESSINTERFACES_EXPORT VehicleStatus {
		/*车辆ID*/
		long vehiId;
		/*车长(米)*/
		qreal mrLength;
		/*车宽(米)*/
		qreal mrWidth;
		/*颜色*/
		QColor mColor;
		/*车辆类型编码*/
		long vehiType;
		/*起始路段ID*/
		long startLinkId;
		/*车辆启动时仿真时间*/
		long startSimuTime;
		/*仿真时间(毫秒)*/
		long mrDatetime;
		/*速度(米/秒)*/
		qreal mrSpeed;
		/*加速度(米/秒/秒)*/
		qreal mrAcce;
		/*已行驶里程(米)*/
		qreal mrDrivDistance;
		/*当前点位*/
		QVector3D mPoint;
		/*当前3维空间的欧拉角*/
		QVector3D mEuler;
		/*方向角，0度指向北，顺时针*/
		qreal mrAngle;
		/*道路ID*/
		long mrRoadId;
		/*道路类型, "L":路段, "C":连接段*/
		QString roadType;
		/*车道序号，从0开始，如果车辆在"车道连接"上，值为-1*/
		long mrLaneNumber;
		/*计算批次*/
		long mrBatchNumber;
		/*停车次数*/
		long mrParkingCount;
		/* 车辆到其所在车道终点的距离 */
		qreal mrDistToLaneEnd;

		VehicleStatus()
		{
			vehiId = 0;
			mrDatetime = 0;
			mrSpeed = 0;
			mrAngle = 0;
			mrRoadId = 0;
			mrBatchNumber = 0;
			mrParkingCount = 0;
			mrDistToLaneEnd = 0;
		}
	};

	/*
	车辆位置信息
	*/
	struct TESSINTERFACES_EXPORT VehiclePosition {
		/*车辆ID*/
		long vehiId;
		/*仿真时间(毫秒)*/
		long mrDatetime;
		/*当前点位*/
		QVector3D mPoint;
		/*当前3维空间的欧拉角*/
		QVector3D mEuler;
		/*方向角，0度指向北，顺时针*/
		qreal mrAngle;
		/*计算批次*/
		long mrBatchNumber;

		VehiclePosition()
		{
			vehiId = 0;
			mrDatetime = 0;
			mrAngle = 0;
			mrBatchNumber = 0;
		}
	};

	/*
	一次停车信息
	*/
	struct TESSINTERFACES_EXPORT VehicleParking {
		/*车辆ID*/
		long vehiId;
		/*车辆开始停车时间，单位毫秒*/
		long mrStartParkTime;
		/*开始停车时的点位*/
		QVector3D mPoint;
		/*距道路末端距离，单位米*/
		qreal mrDistToEnd;
		/*开始停车时的计算批次*/
		long mrStartParkBatchNumber;
		/*道路ID*/
		long mrRoadId;
		/*车道或车道连接上游车道ID*/
		long mrLaneId;
		/*车道连接下游车道ID*/
		long mrToLaneId;
		/*道路类型, "L":路段, "C":连接段*/
		QString roadType;
		/*车道序号，从0开始，如果车辆在"车道连接"上，值为-1*/
		long mrLaneNumber;
		/*到目前已停车次数*/
		long mrParkingCount;
		VehicleParking()
		{
			vehiId = 0;
			mrRoadId = 0;
			mrLaneId = 0;
			mrToLaneId = 0;
			roadType = "L";
			mrLaneNumber = 0;
			mrDistToEnd = 0.0;
			mrStartParkTime = 0;
			mrStartParkBatchNumber = 0;
			mrParkingCount = 0;
		}
	};

	/*
	数据采集点数据
	*/
	struct TESSINTERFACES_EXPORT VehiInfoCollected {
		/*采集器ID*/
		long collectorId;
		/*车辆ID*/
		long vehiId;
		/*仿真时间(s)*/
		long simuInterval;
		/*已行驶时间(s)*/
		long drivInterval;
		/*车辆类型*/
		int vehiType;
		/*车辆长度(m)*/
		qreal length;
		/*期望速度(km/h)*/
		qreal desirSpeed;
		/*平均速度(km/h)*/
		qreal avgSpeed;
		/*加速度(m/s2)*/
		qreal acce;
		/*跟车距离*/
		qreal distFront;
		/*跟车时距*/
		qreal intervalFront;

		VehiInfoCollected() {
			collectorId = 0;
			vehiId = 0;
			simuInterval = 0;
			drivInterval = 0;
			vehiType = 0;
			length = 0;
			desirSpeed = 0;
			avgSpeed = 0;
			acce = 0;
			distFront = 0;
			intervalFront = 0;
		}
	};

	/*
	数据采集点信息集计数据
	*/
	struct TESSINTERFACES_EXPORT VehiInfoAggregated {
		/*采集器ID*/
		long collectorId;
		/*时间段*/
		int timeId;
		/*起始时间(秒)*/
		long fromTime;
		/*结束时间(秒)*/
		long toTime;
		/*平均速度(千米/小时)*/
		qreal avgSpeed;
		/*平均占有率*/
		qreal occupancy;
		/*车辆数*/
		int vehiCount;

		VehiInfoAggregated() {
			collectorId = 0;
			timeId = 0;
			fromTime = 0;
			toTime = 0;
			avgSpeed = 0;
			occupancy = 0;
			vehiCount = 0;
		}
	};

	/*
	排队计数信息
	*/
	struct TESSINTERFACES_EXPORT VehiQueueCounted {
		/*排除计数器ID*/
		long counterId;
		/*时间段*/
		int timeId;
		/*起始时间(秒)*/
		long fromTime;
		/*结束时间(秒)*/
		long toTime;
		/*仿真时间(秒)*/
		long simuTime;
		/*排队车辆数*/
		int vehiCount;
		/*排队长度(米)*/
		qreal queueLength;

		VehiQueueCounted() {
			counterId = 0;
			timeId = 0;
			fromTime = 0;
			toTime = 0;
			simuTime = 0;
			vehiCount = 0;
			queueLength = 0;
		}
	};

	/*
	排队计数器集计数据
	*/
	struct TESSINTERFACES_EXPORT VehiQueueAggregated {
		/*排除计数器ID*/
		long counterId;
		/*时间段*/
		int timeId;
		/*起始时间(秒)*/
		long fromTime;
		/*结束时间(秒)*/
		long toTime;
		/*最大排队长度*/
		qreal maxQueueLength;
		/*最小排队长度*/
		qreal minQueueLength;
		/*平均排队长度(米)*/
		qreal avgQueueLength;
		/*平均排队车辆数*/
		qreal avgVehiCount;

		VehiQueueAggregated() {
			counterId = 0;
			timeId = 0;
			fromTime = 0;
			toTime = 0;
			maxQueueLength = 0;
			minQueueLength = 0;
			avgQueueLength = 0;
			avgVehiCount = 0;
		}
	};

	/*
	行程时间检测器数据
	*/
	struct TESSINTERFACES_EXPORT VehiTravelDetected {
		/*检测器ID*/
		long detectedId;
		/*车辆ID*/
		long vehiId;
		/*起始时间(秒)*/
		qreal fromTime;
		/*结束时间(秒)*/
		qreal toTime;
		/*行驶时间*/
		qreal travelTime;
		/*行驶距离*/
		qreal travelDistance;
		/*延误*/
		qreal delay;
		/*期望速度(千米/小时)*/
		qreal desireSpeed;

		VehiTravelDetected() {
			detectedId = 0;
			vehiId = 0;
			fromTime = 0;
			toTime = 0;
			travelTime = 0;
			travelDistance = 0;
			delay = 0;
			desireSpeed = 0;
		}
	};

	/*
	行程时间集计数据
	*/
	struct TESSINTERFACES_EXPORT VehiTravelAggregated {
		/*检测器ID*/
		long detectedId;
		/*时间段*/
		int timeId;
		/*起始时间(秒)*/
		long fromTime;
		/*结束时间(秒)*/
		long toTime;
		/*平均行程时间(秒)*/
		qreal avgTravelTime;
		/*平均行程距离(米)*/
		qreal avgTravelDistance;
		/*平均延误*/
		qreal avgDelay;
		/*车辆数*/
		int vehiCount;

		VehiTravelAggregated() {
			detectedId = 0;
			timeId = 0;
			fromTime = 0;
			toTime = 0;
			avgTravelTime = 0;
			avgTravelDistance = 0;
			avgDelay = 0;
			vehiCount = 0;
		}
	};

	//灯色时长结构
	struct TESSINTERFACES_EXPORT ColorInterval
	{
		//灯色
		QString color;
		//时长 单位秒
		int interval;

		ColorInterval()
		{
			color = "gray";
			interval = 0;
		}

		ColorInterval(QString c, int t)
		{
			color = c;
			interval = t;
		}
	};

	/*
	信号灯组相位颜色，用于表示当前信号灯相位灯色等数据
	*/
	struct TESSINTERFACES_EXPORT SignalPhaseColor {
		/*信号灯组ID*/
		long signalGroupId;
		/*相位ID*/
		long phaseId;
		/*相位序号*/
		long phaseNumber;
		/*颜色，"R":红色，"Y":黄, "G":绿色, "gray"：灰色*/
		QString color;
		/*当前颜色设置时间(毫秒）*/
		long mrIntervalSetted;
		/*当前颜色已持续时间(毫秒)*/
		long mrIntervalByNow;
	};

	/*
	信号控制
	*/
	struct TESSINTERFACES_EXPORT SignalContralParam {
		/*灯组ID*/
		long signalGroupId;
		/*日期 格式：yyyyMMdd */
		QString day;
		/*起始时间*/
		long fromTime;
		/*结束时间*/
		long toTime;
		/*周期(秒)*/
		int period;
		/*协调相位序号*/
		int crdinatedPhaseNum;
		/*协调相位差(秒)*/
		int crdinatedPhaseDiff;
		/*黄色时长*/
		int yellowInterval;
		/*全红时长*/
		int redInterval;
		//各相位绿时(秒),按相位序号顺序排列
		QList<int> mlPhaseGreen;
		SignalContralParam() {
			signalGroupId = 0;
			fromTime = 1;
			toTime = 3600;
			period = 60;
			crdinatedPhaseNum = 1;
			crdinatedPhaseDiff = 0;
			yellowInterval = 3;
			redInterval = 2;
		}
	};

	/*
	一条路段某个时段各车道限制车型
	*/
	struct TESSINTERFACES_EXPORT LimitLaneByVehiType {
		/*路段ID*/
		long linkId;
		/*车型*/
		long vehiType;
		/*起始时间 单位秒*/
		long fromTime;
		/*结束时间 单位秒*/
		long toTime;
		/*限行车道*/
		QList<int> mlLaneNumber;
	};

	/*
	车道最高速度(km/h)
	*/
	struct TESSINTERFACES_EXPORT LimitSpeedByLane {
		/*车道编号*/
		int laneNumber;
		/* 限速，即最高速度：千米/小时 */
		qreal limitSpeed;
		LimitSpeedByLane() {}
		LimitSpeedByLane(int number, qreal speed) {
			laneNumber = number;
			limitSpeed = speed;
		}
	};

	/*
	一条路段某个时段各车道最高速度(km/h)
	*/
	struct TESSINTERFACES_EXPORT LimitSpeedByLanes {
		/*路段ID*/
		long linkId;
		/*起始时间 单位秒*/
		long fromTime;
		/*结束时间 单位秒*/
		long toTime;
		QList<LimitSpeedByLane> mlLimitSpeedByLane;

		//for java
		void setLimitSpeedByLanes(QList<LimitSpeedByLane> lLimitSpeedByLane) {
			mlLimitSpeedByLane = lLimitSpeedByLane;
		}
	};

	/*
	跟驰模型参数
	*/
	struct TESSINTERFACES_EXPORT FollowingModelParam {
		/*车辆类型，机动车或非机动车*/
		MotorOrNonmotor vtype;
		/*安全时距，单位：s */
		qreal safeInterval;
		/*安全距离，单位：m */
		qreal safeDistance;
		qreal alfa;
		qreal beit;
		FollowingModelParam()
		{
			vtype = MotorOrNonmotor::Motor;
			safeInterval = 1.5;
			safeDistance = 2.0;
			alfa = 4;
			beit = 2;
		}
	};

	/*
	具体车辆类型相关跟驰模型参数
	*/
	struct TESSINTERFACES_EXPORT FollowingParamByVehiType {
		/*车辆类型，机动车或非机动车*/
		int vtype;
		/*安全时距，单位：s */
		qreal safeInterval;
		/*安全距离，单位：m */
		qreal safeDistance;
		qreal alfa;
		qreal beit;
		FollowingParamByVehiType()
		{
			vtype = 1;
			safeInterval = 1.5;
			safeDistance = 2.0;
			alfa = 4;
			beit = 2;
		}
	};


	/*
	动态创建车辆基本参数
	*/
	struct TESSINTERFACES_EXPORT DynaVehiParam {
		/*名称，可以是车牌或其它标识*/
		QString name;
		/*路段或连接段ID*/
		long roadId;
		/*车辆类型编码*/
		long vehiTypeCode;
		/*车道编号，从0开始*/
		int laneNumber;
		/*连接段目标车道编号*/
		int toLaneNumber;
		/*距车道起点距离：m */
		qreal dist;
		/*当前速度 单位：m/s */
		qreal speed;
		/*颜色：RGB，举例："#EE0000" */
		QString color;
		/*其它信息*/
		QJsonObject others;

		DynaVehiParam()
		{
			roadId = 0;
			laneNumber = 0;
			toLaneNumber = -1;
			vehiTypeCode = 1;
			dist = 0;
			speed = 0;
		}
	};

	/*
	动态创建车辆基本参数，附带时间戳
	*/
	struct TESSINTERFACES_EXPORT DynsVehiWithTimestamp {
		qint64 mrTimestamp;
		QList<DynaVehiParam> mlDynaVehi;
		DynsVehiWithTimestamp()
		{
			mrTimestamp = 0;
		}

		// for java
		void setDynaVehis(QList<DynaVehiParam> lDynaVehi) {
			mlDynaVehi = lDynaVehi;
		}
	};

	/*
	动态创建施工区参数
	*/
	struct TESSINTERFACES_EXPORT DynaRoadWorkZoneParam
	{
		/*施工区ID，更新时可用*/
		long id;
		/*道路ID*/
		long roadId;
		/*施工区名称*/
		QString name;
		/*位置，距路段或连接段起点距离，单位米*/
		qreal location;
		/*施工区长度，单位米*/
		qreal length;
		/*车辆经过施工区的最大车速，单位千米/小时*/
		qreal limitSpeed;
		/*施工区开始时间，单位秒，仿真过程中创建的开始时间应与仿真时间一致*/
		long startTime;
		/*施工区施工时长，单位秒*/
		long duration;
		/*上游警示区长度，单位米*/
		qreal upCautionLength;
		/*上游过渡区长度，单位米*/
		qreal upTransitionLength;
		/*上游缓冲区长度，单位米*/
		qreal upBufferLength;
		/*下游过渡区长度，单位米*/
		qreal downTransitionLength;
		/*下游终止区长度，单位米*/
		qreal downTerminationLength;
		/*施工区起始车道序号列表，如果mlToLaneNumber为空，则施工区在路段上*/
		QList<int> mlFromLaneNumber;
		/*施工区目标车道序号列表，如果不空，则施工区在连接段上*/
		QList<int> mlToLaneNumber;
		/*施工区各管控标志，下标0-6依次对应施工区编辑窗口从上到下的标志序，值为该标志下拉框中的序号*/
		QList<int> lLogoIndex;
		/*初始创建CPU时间，单位毫秒*/
		qint64 createCPUTime;
		/*是否生成管控区域*/
		bool bGenerateControlArea;

		DynaRoadWorkZoneParam()
		{
			/*施工区ID，更新时可用*/
			id = -1;
			/*道路ID*/
			roadId = -1;
			/*位置，距路段或连接段起点距离，单位米*/
			location = 1;
			/*施工区长度，单位米*/
			length = 50;
			/*车辆经过施工区的最大车速*/
			limitSpeed = 50;
			/*施工区开始时间，单位秒，仿真过程中创建的开始时间应与仿真时间一致*/
			startTime = 0;
			/*施工区施工时长，单位秒*/
			duration = 3600;
			/*上游警示区长度，单位米*/
			upCautionLength = -1;
			/*上游过渡区长度，单位米*/
			upTransitionLength = -1;
			/*上游缓冲区长度，单位米*/
			upBufferLength = -1;
			/*下游过渡区长度，单位米*/
			downTransitionLength = -1;
			/*下游终止区长度，单位米*/
			downTerminationLength = -1;
			/*施工区各管控标志，下标0-6依次对应施工区编辑窗口从上到下的标志序，值为该标志下拉框中的序号*/
			for (int i = 0; i < 7; ++i) {
				lLogoIndex << 0;
			}
			createCPUTime = 0;
			bGenerateControlArea = false;
		}
	};

	/*
	动态创建事故区参数
	*/
	struct TESSINTERFACES_EXPORT DynaAccidentZoneParam
	{
		/*事故区ID，更新事故区可用*/
		long id;
		/*道路ID*/
		long roadId;
		/*事故区名称*/
		QString name;
		/*位置，距路段或连接段起点距离，单位米*/
		qreal location;
		/*事故长度，单位米*/
		qreal length;
		/*事故区起始车道序号列表，如果mlToLaneNumber为空，则事故区在路段上*/
		QList<int> mlFromLaneNumber;
		/*事故区目标车道序号列表，如果不空，则事故区在连接段上*/
		QList<int> mlToLaneNumber;
		/*事故区开始时间*/
		long startTime;
		//事故持续时间，单位秒。
		long duration;
		//是否需要仿真中创建的事故区在结束仿真后存在
		bool needStayed;
		/*事故区限速，单位千米/小时*/
		qreal limitSpeed;
		/*事故区控制距离，单位米*/
		qreal controlLength;

		//======以下是弃用属性
		//事故等级，分4级，默认为未定等级(0级)持续时间未定，事故区不会自动移除，一般事故(1级)持续时间10分钟，普通事故(2)级持续时间1小时，重大事故(3级)持续时间3小时
		int level;
		//是否需要救援，如果为-1，由事故等级决定，重大事故需要求援，如为0不需救援，如果为1需要救援
		int needRescue;
		//救援时间
		long rescueTime;
		//救援车辆发车时间距事故产生的时间，单位秒，默认60秒
		long waitTimeBeforeRescue;

		DynaAccidentZoneParam()
		{
			roadId = 0;
			/*位置，距路段或连接段起点距离，单位米*/
			location = 1;
			/*事故区长度，单位米*/
			length = 0;
			//======事故管理属性默认值
			/*事故区时长，单位秒*/
			startTime = 0;
			duration = 0;
			level = 0;
			needRescue = -1;
			waitTimeBeforeRescue = 60;
			needStayed = true;
			limitSpeed = 0;
			controlLength = 0;
		}
	};

	/*
	事故时段参数
	*/
	struct TESSINTERFACES_EXPORT DynaAccidentZoneIntervalParam
	{
		/*事故时段ID*/
		long id;
		/*事故区ID*/
		long accidentZoneId;
		/*事故时段开始时间*/
		long startTime;
		/*事故时段结束时间*/
		long endTime;
		/*该时段事故区长度*/
		qreal length;
		/*该时段事故区距起点距离*/
		qreal location;
		/*该时段事故区限速*/
		qreal limitedSpeed;
		/*该时段事故区控制距离*/
		qreal controlLength;
		/*该时段事故区占用车道*/
		QList<int> mlFromLaneNumber;

		DynaAccidentZoneIntervalParam()
		{
			id = -1;
			accidentZoneId = -1;
			startTime = 0;
			endTime = 0;
			length = 0;
			location = 0;
			limitedSpeed = 0;
			controlLength = 0;
		}
	};

	/*
	动态创建限行区参数
	*/
	struct TESSINTERFACES_EXPORT DynaLimitedZoneParam
	{
		/* 限行区ID */
		long id;
		/* 名称 */
		QString name;
		/* 距道路起点距离 单位：米*/
		qreal location;
		/* 限行区长度 单位：米*/
		qreal length;
		//限行区限速（最大车速:千米/小时）
		qreal limitSpeed;
		/* 道路ID */
		long roadId;
		/* 道路类型 */
		QString roadType;

		/*限行区起始车道序号列表，如果mlToLaneNumber为空，则限行区在路段上*/
		QList<int> mlFromLaneNumber;
		/*限行区目标车道序号列表，如果不空，则限行区在连接段上*/
		QList<int> mlToLaneNumber;

		//======以下是限行区管理相关属性======
		//限行持续时间，单位秒，自仿真过程创建后，持续时间大于此值，则删除
		long duration;

		DynaLimitedZoneParam() {
			/* 限行区ID */
			id = -1;
			/*道路ID*/
			roadId = -1;
			/*位置，距路段或连接段起点距离，单位米*/
			location = 1;
			/*限行区长度，单位米*/
			length = 50;
			/*车辆经过施工区的最大车速*/
			limitSpeed = 50;
			/*限行区施工时长，单位秒*/
			duration = 3600;
		}
	};

	/*
	动态创建改扩建参数
	*/
	struct TESSINTERFACES_EXPORT DynaReconstructionParam
	{
		/*改扩建ID，更新时可用*/
		long id;
		/*起始施工区ID*/
		long roadWorkZoneId;
		/*被借道路段ID*/
		long beBorrowedLinkId;
		/*保通长度，单位米，如未初始化，则默认计算*/
		qreal passagewayLength;
		/*保通限速，单位米/秒*/
		qreal passagewayLimitedSpeed;
		/*借道数*/
		int borrowedNum;
		/*----保通详细参数，如初始化，则以该详细参数计算出的保通长度为准----*/
		/*中央分隔带开口处转弯圆曲线半径(m)*/
		qreal R;
		/*中央分隔带保通开口的设计速度(km/h)*/
		qreal V;
		/*路面与轮胎之间的横向摩阻系数*/
		qreal phih;
		/*超高横坡度(%)*/
		qreal ih;
		/*车辆中心与中间带之间的距离(m),Da=0.5Dn*/
		qreal Da;
		/*第一车道的宽度(m)*/
		qreal Dn;
		/*中间带(包括中央分隔带和两侧路缘带)宽度(m)*/
		qreal Dc;

		/*对应的限行区id，适应分布式*/
		long limitedZoneId;

		DynaReconstructionParam()
		{
			/*改扩建ID，更新时可用*/
			id = -1;
			/*起始施工区ID*/
			roadWorkZoneId = -1;
			/*被借道路段ID*/
			beBorrowedLinkId = -1;
			/*保通长度，单位米*/
			passagewayLength = -1;
			/*保通限速，单位米/秒*/
			passagewayLimitedSpeed = 15;
			/*借道数*/
			borrowedNum = 1;
			/*----保通详细参数，如初始化，则以该详细参数计算出的保通长度为准----*/
			/*中央分隔带开口处转弯圆曲线半径(m)*/
			R = -1;
			/*中央分隔带保通开口的设计速度(km/h)*/
			V = -1;
			/*路面与轮胎之间的横向摩阻系数*/
			phih = -1;
			/*超高横坡度(%)*/
			ih = -1;
			/*车辆中心与中间带之间的距离(m),Da=0.5Dn*/
			Da = -1;
			/*第一车道的宽度(m)*/
			Dn = -1;
			/*中间带(包括中央分隔带和两侧路缘带)宽度(m)*/
			Dc = -1;

			limitedZoneId = -1;
		}
	};

	/*
	动态创建限速车型参数
	*/
	struct TESSINTERFACES_EXPORT DynaReduceSpeedVehiTypeParam
	{
		/*限速车型ID,ID均由系统自动生成,创建时无需指定,更新时需指定*/
		long id;
		/*限速区ID*/
		long reduceSpeedAreaId;
		/*限速时段ID*/
		long reduceSpeedIntervalId;
		/*车型编码*/
		long vehicleTypeCode;
		/*平均车速*/
		qreal avgSpeed;
		/*车速标准差*/
		qreal speedSD;

		DynaReduceSpeedVehiTypeParam()
		{
			id = -1;
			reduceSpeedAreaId = -1;
			reduceSpeedIntervalId = -1;
			vehicleTypeCode = -1;
			avgSpeed = 0;
			speedSD = 0;
		}
	};

	/*
	动态创建限速时段参数
	*/
	struct TESSINTERFACES_EXPORT DynaReduceSpeedIntervalParam
	{
		/*限速时段ID*/
		long id;
		/*限速区ID*/
		long reduceSpeedAreaId;
		/*限速时段开始时间*/
		long startTime;
		/*限速时段结束时间*/
		long endTime;
		/*限速车型列表*/
		QList<DynaReduceSpeedVehiTypeParam> mlReduceSpeedVehicleTypeParam;

		DynaReduceSpeedIntervalParam()
		{
			id = -1;
			reduceSpeedAreaId = -1;
			startTime = 0;
			endTime = 0;
		}

		// for java
		void setReduceSpeedVehicleTypeParams(QList<DynaReduceSpeedVehiTypeParam> lReduceSpeedVehicleTypeParam){
			mlReduceSpeedVehicleTypeParam = lReduceSpeedVehicleTypeParam;
		}
	};

	/*
	动态创建限速区参数
	*/
	struct TESSINTERFACES_EXPORT DynaReduceSpeedAreaParam
	{
		/*限速区ID，更新时可用*/
		long id;
		/*限速区名称*/
		QString name;
		/*距起点距离，单位：米*/
		qreal location;
		/*限速区长度，单位：米*/
		qreal areaLength;
		/*路段或连接段ID*/
		long roadId;
		/*车道序号*/
		int laneNumber;
		/*目标车道序号，车道连接下游车道序号，路段可不填*/
		int toLaneNumber;
		/*限速时段列表*/
		QList<DynaReduceSpeedIntervalParam> mlReduceSpeedIntervalParam;

		DynaReduceSpeedAreaParam()
		{
			id = -1;
			roadId = -1;
			location = 0;
			areaLength = 0;
			laneNumber = -1;
			toLaneNumber = -1;
		}

		// for java
		void setReduceSpeedIntervalParams(QList<DynaReduceSpeedIntervalParam> lReduceSpeedIntervalParam) {
			mlReduceSpeedIntervalParam = lReduceSpeedIntervalParam;
		}
	};

	/*
	一个或一次数据来源里保存的所有发车点一次发车间隔信息 sourceKey:来源(如：文件路径)
	*/
	struct TESSINTERFACES_EXPORT DispatchWithSource {
		QString sourceKey;
		QList<DispatchInterval> mlDispatchInterval;
		DispatchWithSource() {
		}
		DispatchWithSource(QString key, QList<Online::DispatchInterval>& lDi) {
			sourceKey = key;
			mlDispatchInterval << lDi;
		}

		// for java
		void setDispatchIntervals(QList<DispatchInterval> lDispatchInterval) {
			mlDispatchInterval = lDispatchInterval;
		}
	};

	/*
	一个或一次数据来源里保存的所有决策点在一个时间间隔的路径流量分配信息 sourceKey:来源(如：文件路径)
	*/
	struct TESSINTERFACES_EXPORT FlowRatioWithSource {
		QString sourceKey;
		QList<DecipointFlowRatioByInterval> mlDecipointFlowRatioByInterval;
		FlowRatioWithSource() {
		}
		FlowRatioWithSource(QString& key, QList<DecipointFlowRatioByInterval>& lRfi) {
			sourceKey = key;
			mlDecipointFlowRatioByInterval << lRfi;
		}

		// for java
		void setDecipointFlowRatioByIntervals(QList<DecipointFlowRatioByInterval> lDecipointFlowRatioByInterval) {
			mlDecipointFlowRatioByInterval = lDecipointFlowRatioByInterval;
		}
	};

	/*
	一个或一次数据来源里保存的所有信号灯组在一个时间间隔各相位灯色信息 sourceKey:来源(如：文件路径)
	*/
	struct TESSINTERFACES_EXPORT SignalContralWithSource {
		QString sourceKey;
		QList<SignalContralParam> mlSignalContral;
		SignalContralWithSource() {
		}
		SignalContralWithSource(QString& key, QList<SignalContralParam>& lSC) {
			sourceKey = key;
			mlSignalContral << lSC;
		}

		//for java
		void setSignalContrals(QList<SignalContralParam> lSignalContral) {
			mlSignalContral = lSignalContral;
		}
	};

	/*
	定位，与LaneObject最近距离，及与LaneObject的交点、交点到起点的距离
	*/
	struct TESSINTERFACES_EXPORT Location {
		//相关车道或"车道连接"
		ILaneObject* pLaneObject;
		//pLaneObject上的一点
		QPointF point;
		//到最点的距离
		qreal leastDist;
		//point到起点的里程
		qreal distToStart;
		//最近点所在分段序号
		int segmIndex;
		//最近点所在分段上下游两点构成的航向角
		qreal angle;

		Location() {
			segmIndex = 0;
			distToStart = 0.0;
			angle = 0;
		}
	};

	struct TESSINTERFACES_EXPORT CrossPoint
	{
		//主车道连接，即被交叉的"车道连接"，可以被多条"车道连接"交叉
		ILaneConnector* mpMainLaneConnector;
		//交叉的"车道连接"
		ILaneConnector* mpLaneConnector;
		//交叉点距离主交叉车道连接的起点距离
		qreal mrDistance;
		//交叉点在主"车道连接"上的分段序号
		int mrSegmIndex;
		//交叉点
		QPointF mCrossPoint;

		CrossPoint()
		{
			mpMainLaneConnector = nullptr;
			mpLaneConnector = nullptr;
			mrDistance = 0;
			mrSegmIndex = 0;
		}

	};

	// b2h
	struct TESSINTERFACES_EXPORT CmdBatch {
		// 发车点的DispatchInterval指令
		QMap<long, QJsonArray> mDispatchPointJsonArrayMap;
		// 路径流量指令
		//Online::FlowRatioWithSource mFlowRatioWithSource;
		QMap<long, QJsonArray> mDeciPointFlowRatioJsonArrayMap;
		// 更新车辆组成指令
		QMap<long, QHash<long, QList<Online::VehiComposition>>> mVehiComposMap;
		// 动态创建车辆指令
		QMap<long, Online::DynsVehiWithTimestamp> mDynaVehiParamsMap;
		// 删除车辆
		QMap<long, QJsonArray> mDelVehiclesMap;

		// 配置
		int mNeededDispatchCmdCnt{ 1 };
		int mNeededDeciPointCmdCnt{ 1 };
		int mNeededVehiComposCmdCnt{ 1 };
		int mNeededDynaVehiCmdCnt{ 1 };
		int mNeededDelVehiclesCnt{ 0 };
		std::mutex mMuOfCmdBatch;

		// 该指令批次的时间戳
		time_t mTimestamp{ 0 };
		long mSimuTime{ LONG_MAX };
	};

	struct TESSINTERFACES_EXPORT PassengerArrivings {
		long passengerArrivingID;
		long startTime;
		long endTime;
		int passengerCount;
	};

	//行人相关
	namespace Pedestrian
	{
		struct TESSINTERFACES_EXPORT PedestrianStatus
		{
			long id;
			QPointF pos{ 0,0 }; //当前位置 米
			qreal elevation{ 0.0 };//高程 米
			QVector2D speed{ 0,0 }; //当前速度 //单位 米每秒
			QVector2D acce{ 0,0 }; //当前加速度
			QVector2D mDirection{ 0,0 }; //单位向量方向
			long pedestrianTypeId; //行人类型id
			QVector3D euler{ 0, 0, 0 };//欧拉角
			qreal radius; //行人半径
		};

		struct TESSINTERFACES_EXPORT GenPedestrianInfo
		{
			int timeInterval; // 时间 秒
			int pedestrianCount; // 需要生成行人总数
			int pedestrianCompositionCode{ -1 }; //行人组成id
		};

		struct TESSINTERFACES_EXPORT PedestrianTrafficDistributionInfo
		{
			int timeInterval; //时间段 秒
			QMap<int, int> trafficRatio; //路径id：流量比例
		};

		struct TESSINTERFACES_EXPORT PedestrianPathStartPointConfigInfo
		{
			int id;//同starPoint id
			QList<Online::Pedestrian::GenPedestrianInfo> genPedestrianConfigInfo; //行人生成配置
			QList<Online::Pedestrian::PedestrianTrafficDistributionInfo> pedestrianTrafficDistributionConfigInfo; // 流量分配设置
		};

		struct TESSINTERFACES_EXPORT PedestrianDecisionPointConfigInfo
		{
			int id; // 决策点ID
			QMap<int, QList<PedestrianTrafficDistributionInfo>> pedestrianTrafficDistributionConfigInfo; // 针对每条路径的流量分配设置
		};

		struct TESSINTERFACES_EXPORT LayerInfo
		{
			long id{ 0 };
			QString name{ "图层" };
			qreal height{ 0 };
			bool visible{ true };
			bool locked{ false };

			QList<IPedestrianRegion*> pedestrianRegions;
		};

		struct TESSINTERFACES_EXPORT PedestrianComposition
		{
			long compositionCode; //行人组成编号
			QString compositionName; //行人组成名称
			QMap<int, qreal> compositionRatio; //行人组成比例 行人类型：比例
		};
	}

	namespace Intersection {
		struct IntersectionParam
		{
			bool dontCare{ false }; //同等路权，只要此值为true，就不用看手动设定的值
			int priorityType{ 3 }; //路权优先级, 0-3, 0 根据组合后车道连接id，id较小的具有优先权；1 根据组合后的车道连接id, id较大的具有优先权；2 忽略优先权，直接通行；3 同等路权，按照既有的算法判断谁先通行
			QString interactionSolutionName; //冲突参数组合名称
		};

		struct ConnectorAreaIntersectionParam
		{
			long connectorAreaId{ -1 }; //面域id
			QPointF connectorAreaCenterPos{ 0,0 }; //面域中心点位
			QHash<QString, IntersectionParam> intersectionParams; //面域中所有冲突点的冲突参数 冲突点id：冲突参数
		};
	}

	namespace TollStation {

		struct TESSINTERFACES_EXPORT DynaTollPoint {
			long id = 0;
			long tollLaneId = 0;
			long timeDisId = 0;	// 停车时间分布ID
			qreal location = 0;			// 距离路段起始位置距离
			int tollType = -1;			// 收费类型 MTC = 1,ETC = 2, ETC&MTC = 3,
			bool enable = false;
		};

		struct TESSINTERFACES_EXPORT DynaTollLane {
			long id = 0;
			QString name;
			qreal location = 0;		// 距路段起始距离 单位米
			qreal length = 0;		// 收费区域长度 单位米
			long roadId = 0;		// 所在路段id
			int laneNumber = 0;		// 所在车道编号，从右向左从0开始
			qreal tollPointLen = 0;	// 收费区域(点)长度
			long speedId = 0;		// 期望速度id
			long startTime = 0;// 仿真开始生效时间
			long endTime = 0;  // 仿真生效结束时间

			QList<DynaTollPoint> tollPoint;	// 收费点列表
		};

		// ETC速度分布
		struct DynaEtcSpeedDetail {
			qreal limitSpeed = 10.00;	// km/h
			qreal prop = 1.00;
		};

		// MTC收费停车时间
		struct DynaMtcTimeDetail {
			qreal time = 3.00;			// 单位秒
			qreal prop = 1.00;
		};

		struct DynaTollParkingTime {
			int vehicleTypeId = 0;		// 车辆类型ID
			QList<DynaMtcTimeDetail> timeDisList;
			QList<DynaEtcSpeedDetail> speedDisList;
		};

		// 停车时间分布
		struct DynaTollParkingTimeDis {
			long id = -1;			// id
			QString name = "";			// 名称
			QList<DynaTollParkingTime> parkingTimeList;
		};

		// 决策分配信息相关结构

		//静态路径到收费路径车辆etc比例
		struct DynaEtcTollInfo {
			// 收费决策点ID
			// long tollDeciPointID = 0;
			// 收费路径ID
			long tollRoutingID = 0;
			// 收费车道ID
			long tollLaneID = 0;
			// ETC车辆比例
			qreal etcRatio = 0;
		};

		struct DynaVehicleTollDisDetail {
			// long staticPathVehicleDisID = 0;
			// 收费路径ID
			long tollRoutingID = 0;
			// 收费车道ID
			long tollLaneID = 0;
			// 通过当前车道比例
			qreal prop = 1.00;
		};

		struct DynaVehicleTollDisInfo {
			int vehicleType = 0;
			QList<DynaVehicleTollDisDetail> list;
		};

		struct DynaRoutingDisTollInfo {
			long startTime = 0;
			long endTime = 0;
			QList<DynaEtcTollInfo> ectTollInfoList;
			QList<DynaVehicleTollDisInfo> vehicleDisInfoList;
		};

		struct DynaTollDisInfo {
			IRouting* pIRouting = nullptr;
			QList<DynaRoutingDisTollInfo> disTollInfoList;
		};
	}

	namespace ParkingLot {

		struct DynaParkingStall {
			long parkingRegionID = 0;
			// long linkID = 0;
			// int laneNumber = 0;
			// 	0-车道内;1-车道右侧;2-车道左侧
			// int parkingStallPos = -1;
			// 0-垂直式;1- 倾斜式-30°;2-倾斜式-45°;3-倾斜式-60°;4-平行式
			// int arrangeType = 0;

			// 停车位类型
			int parkingStallType = 0;	// 小客车/大客车/其他
			qreal length = 0;	// 车位长度单位米
			qreal width = 0;	// 车位宽度单位米
			// 单位米
			qreal location = 0;			// 停车位距离路段起始位置距离
		};

		struct TESSINTERFACES_EXPORT DynaParkingRegion {
			long id = 0;
			QString name;
			qreal location = 0;		// 距离路段起点位置 (单位m，两位小数)
			qreal length = 0;
			long roadId = 0;
			// 所在车道编号，从右向左从0开始
			int laneNumber = 0;
			//0-随机泊车;1-均匀泊车;2-顺序泊车;3-偏好吸引
			int	findParkingStallStrategy = -1;	//寻位策略
			// 	0-车道内;1-车道右侧;2-车道左侧
			int parkingStallPos = -1;
			// 车位角度类型 
			// 0-垂直式;1-倾斜式-30°;2-倾斜式-45°;3-倾斜式-60°;4-平行式
			int arrangeType = 0;

			// 车位吸引力
			int firstParkingStallAttract = 0;
			int middleParkingStallAttract = 0;
			int lastParkingStallAttract = 0;

			// 停车运动参数
			qreal menaValue = 0;	// 均值
			qreal variance = 1.0;	// 方差
			qreal parkingSpeed = 5.0;	// 泊车速度
			qreal joinGap = 5.0;	// 汇入间隙 s
			// 0-前进->前进;1-前进->后退;2-后退->前进
			int parkingType = 0;
			// 运营参数
			int32_t attract = 0;		// 吸引力
			long startTime = 0;	// 开始时间
			long endTime = 999999;		// 结束时间
			// ChargeType chargeType = ChargeType::kTimes;		// 计费方式
			// qreal price = 15.0;
			//qreal stallLength = 0;	// 车位长度单位米
			//qreal stallWidth = 0;	// 车位宽度单位米
			// 停车位列表
			QList<DynaParkingStall> parkingStalls;
		};

		struct TESSINTERFACES_EXPORT DynaParkingParkTime {
			long parkingTimeDisId = 0;		// id
			int time = 0;			// 停车时间 (s)
			qreal prop = 0;			// 比例
		};

		// 
		struct TESSINTERFACES_EXPORT DynaParkingTimeDis {
			long id = -1;	// id  // 获取使用，新增更新时不赋值
			QString name = ""; // 名称
			QList<DynaParkingParkTime> parkingTimeList;
		};

		// 停车区域车辆分配详情
		struct TESSINTERFACES_EXPORT DynaParkRegionVehicleDisDetail {
			// 停车路径ID
			long parkingRoutingID = 0;
			// 停车区域ID
			long parkingRegionID = 0;
			// 车辆类型
			long vehicleType = 1;
			// 停车比例
			qreal prop = 0;
			// 停车时间分布ID
			long parkingTimeDisId = 0;
			// 无余位时动作
			long parkingSelection = 0;
		};

		// 静态路径分布车辆信息
		struct TESSINTERFACES_EXPORT DynaRoutingDisVehicleInfo {
			long startTime = 0;
			long endTime = 0;
			QList<DynaParkRegionVehicleDisDetail> vehicleDisDetailList;
		};

		// 停车分布信息
		struct TESSINTERFACES_EXPORT DynaParkDisInfo {
			// pIRouting 为空时对未有路径的车辆信息进行分配
			// 也表示没有路径经过停车决策点和停车路径关联的停车区域
			IRouting* pIRouting = nullptr;
			QList<DynaRoutingDisVehicleInfo> disVehiclsInfoList;
		};

	}

	namespace Junction
	{
		//转向基础信息
		struct TESSINTERFACES_EXPORT TurnningBaseInfo
		{
			long turningId;                    // 流向ID 
			long turningNum;                   // 流向编号
			QString strTurningName;            // 流向名称
			ILink* fromLink;                   // 起始路段
			ILink* toLink;                     // 终点路段
			QString strDirection;              // 方向
			QString strTurnType;               // 转向类型
			QList<IConnector*> connectors;     // 转向连接段

			TurnningBaseInfo()
				: turningId(0)
				, turningNum(0)
				, fromLink(nullptr)
				, toLink(nullptr)
			{
			}
		};

		// 流量时间段
		struct TESSINTERFACES_EXPORT FlowTimeInterval
		{
			long timeId;             // 时间段ID
			long startTime;          // 开始时间(秒)
			long endTime;            // 结束时间(秒)

			FlowTimeInterval()
			{
				timeId = 0;
				startTime = 0;
				endTime = 0;
			}
		};

		// 节点流向信息
		struct TESSINTERFACES_EXPORT FlowTurning
		{
			IJunction* pJunction;									// 所属节点
			FlowTimeInterval flowTimeInterval;        // 所属时间段
			TurnningBaseInfo turningBaseInfo;    // 转向基础信息
			long inputFlowValue;                  // 输入流量(辆/小时)
			long realFlow;                        // 实际流量(辆/小时)
			double relativeError;                 // 相对误差

			FlowTurning()
				: pJunction(nullptr)
				, flowTimeInterval()
				, turningBaseInfo()              // 显式调用 TurnningBaseInfo 的默认构造函数
				, inputFlowValue(0)
				, realFlow(0)
				, relativeError(0.0)
			{
			}
		};

		// 节点流量数据
		struct TESSINTERFACES_EXPORT JunctionFlow
		{
			long timeId;                      // 时间段ID
			QList<FlowTurning> turningFlows;  // 该时间段内各流向的流量

			JunctionFlow()
			{
				timeId = 0;
			}
		};
	}
}

#endif
