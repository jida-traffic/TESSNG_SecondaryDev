/********************************************************************************
* 车辆接口，由TESS NG实现，用户可以借此调用接口方法，获取车辆一些基本属性，以及运动过程中的一些动态数据，
* 还可以调用一些接口方法对车辆进行初始化，设置动态信息
*********************************************************************************/

#ifndef IVEHICLE_H
#define IVEHICLE_H

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>
#include <QPicture>
#include <QJsonObject>

class Vehicle;
class IVehicleDriving;
class ILink;
class ILaneObject;
class ILane;
class ILaneConnector;
class IRouting;
class ISection;

class TESSINTERFACES_EXPORT IVehicle
{
public:
	virtual ~IVehicle() = default;
	//车辆ID
	virtual long id();
	//设置车辆ID
	virtual void setId(long id);
	//车辆名称
	virtual QString name();
	//长度，单位：像素或米
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置车辆长度，单位：像素, bRestrictWidth:是否同比例约束宽度
	virtual void setLength(qreal len, bool bRestWidth = false, UnitOfMeasure unit = UnitOfMeasure::Default);
	//宽度，单位：像素或米
	virtual qreal width();
	//车辆颜色RGB，举例："#EE0000"
	virtual QString color();
	//颜色：RGB，举例："#EE0000"
	virtual void setColor(QString color);
	//设置车辆类型
	virtual void setVehiType(int code);
	//创建时的仿真时间
	virtual qreal simuTimeOnCreate();
	//停止仿真时的仿真时间
	virtual long simuTimeOnStop();
	//基本期望速度，与驾驶行为接口IVehicleDriving的desirSpeed()有区别，驾驶行为接口desirSpeed()综合考虑了基本期望速度和道路限速
	virtual qreal desirSpeed();
	//设置基本期望速度
	virtual void setDesirSpeed(qreal speed);
	//最大速度，单位：像素/秒或米/秒
	virtual qreal maxSpeed();
	//设置最大速度
	virtual void setMaxSpeed(qreal maxSpeed);
	//最大加速度度
	virtual qreal maxAcce();
	//设置最大加速度
	virtual void setMaxAcce(qreal acce);
	//最大减速度
	virtual qreal maxDece();
	//设置最大减速度
	virtual void setMaxDece(qreal dece);
	//车辆所在道路最大限速，与IVehicleDriving的limitMaxSpeed()相同
	virtual qreal limitMaxSpeed(UnitOfMeasure unit = UnitOfMeasure::Default);
	//车辆所在道路最小限速，与IVehicleDriving的limitMinSpeed()相同
	virtual qreal limitMinSpeed(UnitOfMeasure unit = UnitOfMeasure::Default);
	//期望加速度
	virtual qreal desirAcce();
	//设置期望加速度
	virtual void setDesirAcce(qreal acce);
	//期望减速度
	virtual qreal desireDece();
	//设置期望减速度
	virtual void setDesireDece(qreal dece);


	//起始路段
	virtual ILink* startLink();
	//起始仿真时间
	virtual long startSimuTime();
	//道路，如果在路段上返回ILink, 如果在连接段上返回IConnector
	virtual void* road();
	//道路ID
	virtual long roadId();
	//ISection
	virtual ISection* section();
	//当前道路是否路段，如果不是则在连接段上
	virtual bool roadIsLink();
	//道路名
	virtual QString roadName();
	//返回当前车道或"车道连接"
	virtual ILaneObject* laneObj();
	//车道ID
	virtual long laneId();
	//连接段的去向路段上车道ID
	virtual long toLaneId();
	//获取当前车道，如果在"车道连接"上，返回空指针
	virtual ILane* lane();
	//获取当前"车道连接"的下游车道，如果当前不在"车道连接"上，返回空指针
	virtual ILane* toLane();
	//获取当前"车道连接"，如果在车道上，返回空指针
	virtual ILaneConnector* laneConnector();
	//当前LaneObject上分段序号
	virtual int segmIndex();
	//当前仿真计算批次
	virtual long currBatchNumber();
	//道路类型，即路段或连接段
	virtual int roadType();
	//车道或车道连接中心线内点集
	virtual QList<QPointF> lLaneObjectVertex(UnitOfMeasure unit = UnitOfMeasure::Default);
	virtual long vehicleTypeCode();
	virtual QString vehicleTypeName();
	virtual IVehicleDriving* vehicleDriving();
	virtual void driving();
	virtual qreal initSpeed(qreal speed = -1, UnitOfMeasure unit = UnitOfMeasure::Default);
	//在路段上用车道序号、距起点距离、当前速度进行初始化
	virtual void initLane(int laneNumber, qreal dist = -1, qreal speed = -1, UnitOfMeasure unit = UnitOfMeasure::Default);
	//在连接段上用车道连接起始车道序号和目标车道序号以及距起点距离、当前速度进行初始化
	virtual void initLaneConnector(int laneNumber, int toLaneNumber, qreal dist = -1, qreal speed = -1, UnitOfMeasure unit = UnitOfMeasure::Default);
	//是否使用缓存
	virtual void useCache(bool bCache);
	//当前路径
	virtual IRouting* routing();
	//当前点位，单位：像素或米
	virtual QPointF pos(UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前高程，单位：像素或米，由当前所在路段或连接段的高程决定
	virtual qreal zValue(UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前高程，由当前所在路段或连接段的分段两个端点高程决定
	virtual qreal v3z();
	//当前加速度
	virtual qreal acce(UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前速度，单位：像素/秒或米/秒
	virtual qreal currSpeed(UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前角度
	virtual qreal angle();
	//车辆图
	virtual QPicture picture();
	//是否已启动
	virtual bool isStarted();

	//前车
	virtual IVehicle* vehicleFront();
	//后车
	virtual IVehicle* vehicleRear();
	//左前车
	virtual IVehicle* vehicleLFront();
	//左后车
	virtual IVehicle* vehicleLRear();
	//右前车
	virtual IVehicle* vehicleRFront();
	//右后车
	virtual IVehicle* vehicleRRear();
	/* 前车距离，单位：像素或米 */
	virtual qreal vehiDistFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 前车速度*/
	virtual qreal vehiSpeedFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 后车距，单位：像素或米 */
	virtual qreal vehiDistRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 后车速度*/
	virtual qreal vehiSpeedRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 距前车时距 */
	virtual qreal vehiHeadwayFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 距后车时距 */
	virtual qreal vehiHeadwaytoRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻左车道前车距离，单位：像素或米 */
	virtual qreal vehiDistLLaneFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻左车道前车速度 */
	virtual qreal vehiSpeedLLaneFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻左车道后车距离 */
	virtual qreal vehiDistLLaneRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻左车道后车速度 */
	virtual qreal vehiSpeedLLaneRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻右车道前车距离，单位：像素或米 */
	virtual qreal vehiDistRLaneFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻右车道前车速度 */
	virtual qreal vehiSpeedRLaneFront(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻右车道后车距离 */
	virtual qreal vehiDistRLaneRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 相邻右车道后车速度 */
	virtual qreal vehiSpeedRLaneRear(UnitOfMeasure unit = UnitOfMeasure::Default);
	//获取车辆由方向和长度决定的四个拐角构成的多边型
	virtual QPolygonF boundingPolygon();
	//是否是营运车辆
	virtual bool isCommercialVehicle();
	//核载人数
	virtual int maxPassengerCapacity();
	//当前乘客数量 算着司机
	virtual int passengerCount();
	//剩余可载乘客数量
	virtual int availableSeatCount();
	//上车n个人 返回实际上车人数
	virtual int boardPassengers(int n);
	//下车n个人 返回实际下车人数
	virtual int alightPassengers(int n);
	//是否已经被预定 营运车辆
	virtual bool isHookedUp();
	//设置是否已经被预定
	virtual void setHookedUp(bool bHookedUp);
	//设置标签表示的状态
	virtual void setTag(long tag);
	//获取标签表示的状态
	virtual long tag();
	//设置文本信息，用于在运行过程保存临时信息，方便开发
	virtual void setTextTag(QString text);
	//文本信息，运行过程临时保存的信息，方便开发
	virtual QString textTag();
	//设置动态信息
	virtual void setDynaInfo(void* pDynaInfo);
	//获取动态信息
	virtual void* dynaInfo();
	//设置json格式数据
	virtual void setJsonInfo(QJsonObject info);
	//返回json格式数据
	virtual QJsonObject jsonInfo();
	//返回json字段值
	virtual QVariant jsonProperty(QString propName);
	//设置json数据属性
	virtual void setJsonProperty(QString key, QVariant value);
	//获取自定义属性数据, vehiOtherPropertyType 二次开发新增类型使用[0-1000], 内部使用VehicleOtherPropertyType中定义类型
	virtual QVariant otherProperty(int vehiOtherPropertyType);
	//设置自定义属性数据, vehiOtherPropertyType 二次开发新增类型使用[0-1000], 内部使用VehicleOtherPropertyType中定义类型。注意，vehicle不管理传入指针类型对象的生命周期
	virtual void setOtherProperty(int vehiOtherPropertyType, QVariant value);


	//====设置插件方法调用步长，即多少个计算周期调用一次，以下方法的目的在于python环境下设置指定方法调用步长，减少不必要的对python过载方法调用频次，以减少效率的损失
	//设置是否允许客户对于车辆的绘制
	virtual void setIsPermitForVehicleDraw(bool bDraw);
	//每steps个计算周期调用一次 CustomerSimulator::beforeNextPoint(...)方法  
	virtual void setSteps_beforeNextPoint(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::nextPoint(...)方法  
	virtual void setSteps_nextPoint(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::setSteps_afterStep(...)方法  
	virtual void setSteps_afterStep(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::isStopDriving(...)方法  
	virtual void setSteps_isStopDriving(int steps);

	//每steps个计算周期调用一次 CustomerSimulator::judgeIfOnTargetLane(...)方法  
	virtual void setSteps_judgeIfOnTargetLane(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reCalcdesirSpeed(...)方法  
	virtual void setSteps_reCalcdesirSpeed(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::calcMaxLimitedSpeed(...)方法  
	virtual void setSteps_calcMaxLimitedSpeed(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::calcLimitedLaneNumber(...)方法  
	virtual void setSteps_calcLimitedLaneNumber(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::calcSpeedLimitByLane(...)方法  
	virtual void setSteps_calcSpeedLimitByLane(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::calcDistToEventObj(...)方法  
	virtual void setSteps_calcDistToEventObj(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::calcChangeLaneSafeDist(...)方法  
	virtual void setSteps_calcChangeLaneSafeDist(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reCalcToLeftLane(...)方法  
	virtual void setSteps_reCalcToLeftLane(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reCalcToRightLane(...)方法  
	virtual void setSteps_reCalcToRightLane(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reCalcToLeftFreely(...)方法  
	virtual void setSteps_reCalcToLeftFreely(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reCalcToRightFreely(...)方法  
	virtual void setSteps_reCalcToRightFreely(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::afterCalcTracingType(...)方法  
	virtual void setSteps_afterCalcTracingType(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::beforeMergingToLane(...)方法  
	virtual void setSteps_beforeMergingToLane(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reSetFollowingType(...)方法  
	virtual void setSteps_reSetFollowingType(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::calcAcce(...)方法  
	virtual void setSteps_calcAcce(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reSetAcce(...)方法  
	virtual void setSteps_reSetAcce(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reSetSpeed(...)方法  
	virtual void setSteps_reSetSpeed(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::reCalcAngle(...)方法  
	virtual void setSteps_reCalcAngle(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::recentTimeOfSpeedAndPos(...)方法  
	virtual void setSteps_recentTimeOfSpeedAndPos(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::travelOnChangingTrace(...)方法  
	virtual void setSteps_travelOnChangingTrace(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::leaveOffChangingTrace(...)方法  
	virtual void setSteps_leaveOffChangingTrace(int steps);
	//每steps个计算周期调用一次 CustomerSimulator::beforeNextRoad(...)方法  
	virtual void setSteps_beforeNextRoad(int steps);

private:

};

#endif // IVEHICLE_H