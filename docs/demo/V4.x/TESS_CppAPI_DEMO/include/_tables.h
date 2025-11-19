#ifndef __tables__
#define __tables__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QJsonObject>

/*const*/ static double NanV = 1e+300 * 1e+300;

class _Link {
public:
	_Link() {
		linkID = 0;
		netId = 0;
		roadId = 0;
		laneNumber = 0;
		laneWidth = 0;
		length = 0;
		curvature = 0;
		nonLinearCoefficient = NanV;
		linkSaturationFlow = NanV;
		linkTrafficFlow = NanV;
		desiredSpeed = 0;
		limitSpeed = 0;
		minSpeed = 0;
		addValue = NanV;
		mbAutoZ = true;
	}
public:
	/* 路段编号 */
	long linkID;
	/* 道路名称 */
	QString linkName;
	/*路网ID*/
	long netId;
	/*道路ID*/
	long roadId;
	/* 车道数 */
	double laneNumber;
	/* 车道宽度 */
	double laneWidth;
	/* 车道颜色 */
	QString laneColor;
	/* 道路类型 */
	QString linkType;
	/* 路段长度 */
	double length;
	/* 曲率 */
	double curvature;
	/* 道路非直线系数 */
	double nonLinearCoefficient;
	/* 饱和流率 */
	double linkSaturationFlow;
	/* 路段流量 */
	double linkTrafficFlow;
	/* 期望速度(自由流速度) 千米/小时*/
	double desiredSpeed;
	/* 限速 千米/小时*/
	double limitSpeed;
	/* 最小速度 千米/小时*/
	double minSpeed;
	/* 附加值 */
	double addValue;
	/* 高程是否自动计算 */
	bool mbAutoZ;
	/* 中心线断点json数组 */
	QJsonObject centerLinePointsJson;
	/* 左侧断点json数据 */
	QJsonObject leftBreakPointsJson;
	/* 右侧断点json数据 */
	QJsonObject rightBreakPointsJson;
	/* 其它属性json数据 */
	QJsonObject otherAttrsJson;
};

class _Lane {
public:
	_Lane() {
		laneID = 0;
		linkID = 0;
		serialNumber = 0;
		width = 0;
	}
public:
	/* 车道编号 */
	long laneID;
	/* 路段ID*/
	long linkID;
	/* 车道序号 */
	int serialNumber;
	/* 车道宽度 */
	double width;
	/* 应急车道 */
	bool emergencyLane;
	/* 希望行驶方向，L：左，R：右，A：任意 */
	QString expectTravelDirection;
	/* 行为类型 */
	QString actionType;
	/* 中心线断点json数据 */
	QJsonObject centerLinePointsJson;
	/* 左侧断点json数据 */
	QJsonObject leftBreakPointsJson;
	/* 右侧断点json数据 */
	QJsonObject rightBreakPointsJson;
	/* 其它属性json数据 */
	QJsonObject otherAttrsJson;
};

class _Connector {
public:
	_Connector() {
		connID = 0;
		roadId = 0;
		connAreaId = 0;
		length = 0;
		curvature = NanV;
		nonLinearCoefficient = NanV;
		desiredSpeed = 0;
		limitSpeed = 0;
		ext1 = 0;
		ext2 = 0;
		extSize = 0;
	}
public:
	/* 编号 */
	long connID;
	/* 道路ID */
	long roadId;
	/* 面域ID*/
	long connAreaId;
	/* 连接段名称 */
	QString connName;
	/* 连接段长度 */
	double length;
	/* 曲率 */
	double curvature;
	/* 连接段非直线系数 */
	double nonLinearCoefficient;
	/* 路面颜色 */
	QString color;
	/* 期望速度 */
	double desiredSpeed;
	/* 限速 */
	double limitSpeed;
	/* 左侧断点json数据 */
	QJsonObject leftBreakPointsJson;
	/* 右侧断点json数据 */
	QJsonObject rightBreakPointsJson;
	/* 其它属性json数据 */
	QJsonObject otherAttrsJson;
	/* 首部扩展参考点系数 */
	qreal ext1;
	/* 尾部扩展参考点系数 */
	qreal ext2;
	/* 扩展点个数 */
	int extSize;
};

class _LaneConnector {
public:
	_LaneConnector(){
		laneConnID = 0;
		mrLength = 0.0;
		weight = 0;
	}
public:
	//暂不用
	long laneConnID;
	qreal mrLength;
	/* 权重 */
	int weight;
	/* 中心线断点json数据 */
	QJsonObject centerLinePointsJson;
	/* 左侧断点json数据 */
	QJsonObject leftBreakPointsJson;
	/* 右侧断点json数据 */
	QJsonObject rightBreakPointsJson;
	/* 其它属性json数据 */
	QJsonObject otherAttrsJson;
};

//决策点
class _DecisionPoint {
public:
	_DecisionPoint() {
		deciPointID = 0;
		X = 0;
		Y = 0;
		Z = 0;
	}
public:
	long deciPointID;
	QString deciPointName;
	/* 坐标X */
	qreal X;
	/* 坐标Y */
	qreal Y;
	/* 高程 */
	qreal Z;
};

//路径流量比
class _RoutingFLowRatio {
public:
	_RoutingFLowRatio() {
		RoutingFLowRatioID = 0;
		routingID = 0;
		startDateTime = 0;
		endDateTime = 0;
		ratio = 1;
	}
	bool  operator < (const _RoutingFLowRatio& other) const {
		if (endDateTime == other.endDateTime)
		{
			return routingID < other.routingID;
		}
		else
		{
			return endDateTime < other.endDateTime;
		}
	}
public:
	/* 路径车辆分配比编号 */
	long RoutingFLowRatioID;
	/* 路径编号 */
	long routingID;
	/* 起始时间 */
	long startDateTime;
	/* 结束时间 */
	long endDateTime;
	/* 分配比 */
	qreal ratio;
};

class _VehicleType {
public:
	_VehicleType() {
		vehicleTypeCode = 0;
		vehicleTypeName = "新建车型";
		Length = 4.5;
		lengthSD = 0.5;
		width = 2;
		widthSD = 0.2;
		avgSpeed = 60;
		speedSD = 10;
		avgAcce = 4;
		acceSD = 0.5;
		avgDece = 6;
		deceSD = 0.5;
		maxAcce = 5.5;
		maxDece = 7.5;
		maxSpeed = 80;
		commercialVehicle = false;
		maxPassengerCapacity = 1;
		initialPassengerCount = 1;
	}
public:
	/* 车型编码 */
	long vehicleTypeCode;
	/* 车型名 */
	QString vehicleTypeName;
	/* 车辆长度，单位：m */
	qreal Length;
	/* 车长标准差 */
	qreal lengthSD;
	/* 车辆宽度 单位：m */
	qreal width;
	/* 车宽标准差 */
	qreal widthSD;
	/* 平均速度 单位：km/h */
	qreal avgSpeed;
	/* 车速标准差 */
	qreal speedSD;
	/* 最大行驶速度 单位：km/h*/
	qreal maxSpeed;
	/* 平均加速度 单位：m/s^2 */
	qreal avgAcce;
	/* 加速度标准差 */
	qreal acceSD;
	/* 最大加速度 单位：m/s^2 */
	qreal maxAcce;
	/* 平均减速度 单位：m/s^2 */
	qreal avgDece;
	/* 减速度标准差 */
	qreal deceSD;
	/* 最大减速度 单位：m/s^2 */
	qreal maxDece;
	/* 是否是营运车辆 */
	bool commercialVehicle;
	/* 核载人数 */
	int maxPassengerCapacity;
	/* 初始载客数 */
	int initialPassengerCount;
};

enum class ChangeDirection {
	kNormal = 0,
	kLeft = 1,	// 不允许左转
	kRight = 2, // 不允许右转
};

class _LaneLimitChange {

public:
	long id = 0;
	QString name = "";
	long laneId = 0;
	// 距离路段起始位置 / m
	qreal location = 0;
	// 长度 m
	qreal length = 0;
	qreal distToLeft = 0; // 距左边线距离
	qreal distToRight = 0; // 距右边线距离
	bool allowForcedChange = false;
	// 全车道时限制整个车道不可变道
	bool fullLane = false;

	int direction = static_cast<int>(ChangeDirection::kNormal);
};

/**
	采集器
**/
class _VehicleDrivInfoCollector
{
public:
	_VehicleDrivInfoCollector(void) {
		collecterID = 0;
		name = "0";
		roadID = 0;
		laneNumber = -1;
		toLaneNumber = -1;
		distance = 0;
		x = 0;
		y = 0;
		z = 0;
		startTime = 1;
		endTime = 3600;
		dataInterval = 300;
	}
public:
	long collecterID;
	QString name;
	long roadID;
	int laneNumber;
	int toLaneNumber;
	qreal distance;
	qreal x;
	qreal y;
	qreal z;
	long startTime;
	long endTime;
	long dataInterval;
};

class _VehicleQueueCounter
{
public:
	_VehicleQueueCounter(void){
		laneNumber = 0;
		toLaneNumber = 0;
		speedLowLimit = m2p(5 / 3.6);
		speedUpLimit = m2p(10 / 3.6);
		maxDistInterval = m2p(20);
		maxQueueLength = m2p(500);
		startTime = 1;
		endTime = 3600;
		x = 0;
		y = 0;
		z = 0;
		distance = 0;
		//数据间隔 秒
		dataInterval = 300;
		//采集间隔 秒
		countInterval = 1;
	}
public:
	/* 排队计数器编号 */
	long queueCounterID;
	/* 计数器名称 */
	QString name;
	/* 道路(路段或连接段)ID */
	long roadID;
	/* 采集器所在车道序号 */
	int laneNumber;
	/* 目标车道*/
	int toLaneNumber;
	qreal x;
	/* 位置Y */
	qreal y;
	/* 高程*/
	qreal z;
	/* 速度下限 */
	qreal speedLowLimit;
	/* 速度上限 */
	qreal speedUpLimit;
	/* 最大车辆间距 */
	double maxDistInterval;
	/* 最大排队长度 */
	double maxQueueLength;
	/* 距起点距离 */
	double distance;
	/* 起始时间 */
	long startTime;
	/* 结束时间 */
	long endTime;
	/* 数据间隔*/
	long dataInterval;
	/* 采集间隔*/
	long countInterval;
};

#endif
