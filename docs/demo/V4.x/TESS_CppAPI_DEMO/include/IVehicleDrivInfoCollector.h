#ifndef __IVehicleDrivInfoCollector__
#define __IVehicleDrivInfoCollector__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>
#include <QPolygonF>

class ILink;
class IConnector;
class ILane;
class ILaneConnector;

class TESSINTERFACES_EXPORT IVehicleDrivInfoCollector
{
public:
	virtual ~IVehicleDrivInfoCollector() = default;
	//采集器ID
	virtual long id();
	//采集名称
	virtual QString collName();
	//是否在路段上，如果true则connector()返回nullptr
	virtual bool onLink();
	//采集器所在路段
	virtual ILink* link();
	//采集器所在连接段
	virtual IConnector* connector();
	//如果采集器在路段上则lane()返回所在车道，laneConnector()返回nullptr
	virtual ILane* lane();
	//如果采集器在连接段上则laneConnector返回“车道连接”,lane()返回nullptr
	virtual ILaneConnector* laneConnector();
	//采集器距离起点距离，单位像素
	virtual qreal distToStart(UnitOfMeasure unit = UnitOfMeasure::Default);
	//采集器所在点
	virtual QPointF point(UnitOfMeasure unit = UnitOfMeasure::Default);
	//采集器工作起始时间，单位秒
	virtual long fromTime();
	//采集器工作停止时间，单位秒
	virtual long toTime();
	//集计数据时间间隔，单位秒
	virtual long aggregateInterval();

	//设置采集器名称
	virtual void setName(QString name);
	//设置采集器车道起点（或“车道连接”起点）距离，单位像素
	virtual void setDistToStart(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//设置工作起始时间
	virtual void setFromTime(long time);
	//设置工作结束时间
	virtual void setToTime(long time);
	//设置集计数据时间间隔
	virtual void setAggregateInterval(int interval);
	//多边型轮廓
	virtual QPolygonF polygon();
	//获取angle
	virtual qreal angle();
};


#endif
