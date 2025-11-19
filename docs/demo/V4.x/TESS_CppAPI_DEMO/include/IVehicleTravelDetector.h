#ifndef __IVehicleTravelDetector__
#define __IVehicleTravelDetector__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>
#include <QPolygonF>

class ILink;
class ILaneConnector;

class TESSINTERFACES_EXPORT IVehicleTravelDetector
{
public:
	virtual ~IVehicleTravelDetector() = default;
	//采集器ID
	virtual long id();
	//采集名称
	virtual QString detectorName();
	//是否检测器起始点
	virtual bool isStartDetector();
	//检测器起点是否在路段上，如果否，则起点在连接段上
	virtual bool isOnLink_startDetector();
	//检测器终点是否在路段上，如果否，则终点在连接段上
	virtual bool isOnLink_endDetector();
	//如果检测器起点在路段上则link_startDetector()返回起点所在路段，laneConnector_startDetector()返回nullptr
	virtual ILink* link_startDetector();
	//如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”,link_startDetector()返回nullptr
	virtual ILaneConnector* laneConnector_startDetector();
	//如果检测器终点在路段上则link_endDetector()返回终点所在路段，laneConnector_endDetector()返回nullptr
	virtual ILink* link_endDetector();
	//如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”,link_endDetector()返回nullptr
	virtual ILaneConnector* laneConnector_endDetector();
	//检测器起点距离所在车道起点或“车道连接”起点距离，单位像素
	virtual qreal distance_startDetector(UnitOfMeasure unit = UnitOfMeasure::Default);
	//检测器终点距离所在车道起点或“车道连接”起点距离，单位像素
	virtual qreal distance_endDetector(UnitOfMeasure unit = UnitOfMeasure::Default);
	//检测器起点位置
	virtual QPointF point_startDetector(UnitOfMeasure unit = UnitOfMeasure::Default);
	//检测器终点位置
	virtual QPointF point_endDetector(UnitOfMeasure unit = UnitOfMeasure::Default);
	//检测器工作起始时间，单位秒
	virtual long fromTime();
	//检测器工作停止时间，单位秒
	virtual long toTime();
	//集计数据时间间隔，单位秒
	virtual long aggregateInterval();
	//设置检测器名称
	virtual void setName(QString name);
	//设置检测器起点距车道起点（或“车道连接”起点）距离，单位像素
	virtual void setDistance_startDetector(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置检测器终点距车道起点（或“车道连接”起点）距离，单位像素
	virtual void setDistance_endDetector(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置工作起始时间，单位秒
	virtual void setFromTime(long time);
	//设置工作结束时间，单位秒
	virtual void setToTime(long time);
	//设置集计数据时间间隔，单位秒
	virtual void setAggregateInterval(int interval);

	//行程时间检测器起始点多边型轮廓
	virtual QPolygonF polygon_startDetector();
	//行程时间检测器终止点多边型轮廓
	virtual QPolygonF polygon_endDetector();
	//获取angle
	virtual qreal angle();
};

#endif