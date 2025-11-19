/********************************************************************************
* 驾驶行为接口，由TESS NG实现，用户可以借此调用接口方法，获取驾驶过程动态数据，
* 还可以调用一些接口方法对改变原有的驾驶行为或参数
*********************************************************************************/

#ifndef IVEHICLEDRIVING_H
#define IVEHICLEDRIVING_H

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>
#include <QGraphicsItem>
#include <QVector3D>

class IVehicle;
class IRouting;
class ISection;
class ILane;
class ILaneConnector;
class ILaneObject;
class ISignalLamp;

class TESSINTERFACES_EXPORT IVehicleDriving
{
public:
	virtual ~IVehicleDriving() = default;
	virtual IVehicle* vehicle();
	//获取随机数
	virtual long getRandomNumber();
	//计算到下一点，travelInterval：到下点经历的时间，单位毫秒
	virtual bool nextPoint();
	//当前车速为零持续时间(毫秒)
	virtual long zeroSpeedInterval();
	//当前是否在路径上
	virtual bool isOnRouting();
	//当前是否在路段上且有决策点
	virtual bool isHavingDeciPointOnLink();
	//停止运行
	virtual void stopVehicle();

	virtual int followingType();
	//初始化轨迹
	virtual void initTrace();
	//设置轨迹
	virtual void setTrace(QList<QPointF>& lPoint, UnitOfMeasure unit = UnitOfMeasure::Default);
	//计算轨迹长度
	virtual void calcTraceLength();
	//返回轨迹类型
	virtual int tracingType();
	//设置轨迹类型
	virtual void setTracingType(int type);
	//返回车辆角度
	virtual qreal angle();
	//设置车辆角度
	virtual void setAngle(qreal angle);
	//返回车辆欧拉角
	virtual QVector3D euler(bool bPositive = false);
	//期望速度，综合了道路限速，与IVehicle的desirSpeed()有所不同，IVehicle的desirSpeed()是基本期望速度，
	virtual qreal desirSpeed(UnitOfMeasure unit = UnitOfMeasure::Default);
	//车辆所在道路最大限速，与IVehicle的limitMaxSpeed()相同
	virtual qreal limitMaxSpeed(UnitOfMeasure unit = UnitOfMeasure::Default);
	//车辆所在道路最小限速，与IVehicle的limitMinSpeed()相同
	virtual qreal limitMinSpeed(UnitOfMeasure unit = UnitOfMeasure::Default);
	//返回当前所在路段或连接段
	virtual ISection* getCurrRoad();
	//下一路段或连接段
	virtual ISection* getNextRoad();
	//返回当前受其影响的信号灯
	virtual ISignalLamp* getCurrSignalLamp();
	//与目标车道编号的差值
	virtual int differToTargetLaneNumber();
	//左变道
	virtual void toLeftLane(bool bFource = false);
	//右变道
	virtual void toRightLane(bool bFource = false);
	//车道序号
	virtual int laneNumber();
	//设置车道序号
	virtual void setLaneNumber(int number);
	//设置当前路段或连接段已行驶的距离
	virtual void setCurrDistanceInRoad(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前路段或连接段已行驶距离
	virtual qreal currDistanceInRoad(UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置当前分段已行驶的距离
	virtual void setCurrDistanceInSegment(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前分段已行驶距离
	virtual qreal currDistanceInSegment(UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置分段序号
	virtual void setSegmentIndex(int index);
	//设置当前已行驶总里程
	virtual void setVehiDrivDistance(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//当前已行驶总里程
	virtual qreal getVehiDrivDistance(UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置曲化轨迹上行驶的距离
	virtual void setCurrDistanceInTrace(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置曲化轨迹上的分段序号
	virtual void setIndexOfSegmInTrace(int index);
	//设置是否改变轨迹
	virtual void setChangingTracingType(bool b);
	//当前时间段移动距离
	virtual qreal currDistance(UnitOfMeasure unit = UnitOfMeasure::Default);
	virtual void setX(qreal posX, UnitOfMeasure unit = UnitOfMeasure::Default);
	virtual void setY(qreal posY, UnitOfMeasure unit = UnitOfMeasure::Default);
	virtual void setV3z(qreal v3z, UnitOfMeasure unit = UnitOfMeasure::Default);
	/*设置跟驰状态*/
	virtual void setFollowingType(int followingType, int interval = 0, IVehicle* pIVehicleNegotiated = 0);
	virtual IVehicle* getVehicleNegotiated();
	/*变轨点集,车辆不在车道中心线或“车道连接”中心线上时的轨迹，如变道过程的轨迹*/
	virtual QList<QPointF> changingTrace(UnitOfMeasure unit = UnitOfMeasure::Default);
	/*变轨长度*/
	virtual qreal changingTraceLength(UnitOfMeasure unit = UnitOfMeasure::Default);
	/*在车道或车道连接上到起点距离 fromVehiHead:是否从车头计算，bOnCentLine:当前是否在中心线上*/
	virtual qreal distToStartPoint(bool fromVehiHead = false, bool bOnCentLine = true, UnitOfMeasure unit = UnitOfMeasure::Default);
	/*在车道或车道连接上到终端距离 fromVehiHead:是否从车头计算*/
	virtual qreal distToEndpoint(bool fromVehiHead = false, bool bOnCentLine = true, UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置路径，外界设置的路径不一定有决策点，可能是临时创建的，如果车辆不在此路径上则设置不成功并返回false
	virtual bool setRouting(IRouting* pRouting);
	//当前路径
	virtual IRouting* routing();

	/*将车辆移到另一条车道上
	参数 pLane：目标车道
		 dist:到目标车道起点距离，单位像素
	*/
	virtual bool moveToLane(ILane* pLane, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);

	/*将车辆移到另一条车道连接上
	参数 pLaneConnector：目标车道
		dist:到目标车道起点距离，单位像素
	*/
	virtual bool moveToLaneConnector(ILaneConnector* pLaneConnector, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);

	/* 移动车辆到到另一条车道或“车道连接”
	参数 pILaneObject：目标车道或“车道连接”
		 dist:到目标车道起点距离，单位像素
	 */
	virtual bool move(ILaneObject* pILaneObject, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);

	/* 获取时距 */
	virtual qreal safeTimeInterval();
	/* 获取停车距离 */
	virtual qreal stoppingDistance();
	/* 修改时距 */
	virtual void setSafeTimeInterval(qreal time);
	/* 修改停车距离 */
	virtual void setStoppingDistance(qreal dist);

private:
	IVehicle* mpVehicle;
};

#endif // IVEHICLEDRIVING_H