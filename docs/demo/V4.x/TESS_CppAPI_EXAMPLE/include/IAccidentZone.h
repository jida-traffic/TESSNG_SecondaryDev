#ifndef __IAccidentZone__
#define __IAccidentZone__

#include <QObject>
#include "tessinterfaces_global.h"
#include "UnitChange.h"
#include "Plugin/_datastruct.h"

class ILaneObject;
class ISection;
class IAccidentZoneInterval;

class TESSINTERFACES_EXPORT IAccidentZone
{
public:
	virtual ~IAccidentZone()=default;
	virtual long id() = 0;
	/* 名称 */
	virtual QString name() = 0;
	/* 事故区当前时段的距起点距离，默认单位：像素*/
	virtual qreal location(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 事故区当前时段的长度，默认单位：像素*/
	virtual qreal zoneLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 路段或连接段*/
	virtual ISection* section() = 0;
	/* 道路ID */
	virtual long roadId() = 0;
	/* 道路类型（路段或连接段） */
	virtual QString roadType() = 0;
	/* 事故区当前时段占用车道*/
	virtual QList<ILaneObject*> laneObjects() = 0;
	/*事故区当前时段的限速，默认单位：像素(km/h)*/
	virtual qreal limitedSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;

	/*事故区当前时段的控制距离（车辆距离事故区起点该距离内，强制变道），默认单位：像素*/
	virtual qreal controlLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*添加事故时段*/
	virtual IAccidentZoneInterval* addAccidentZoneInterval(Online::DynaAccidentZoneIntervalParam param) = 0;
	/*移除事故时段*/
	virtual void removeAccidentZoneInterval(long accidentZoneIntervalId) = 0;
	/*更新事故时段*/
	virtual bool updateAccidentZoneInterval(Online::DynaAccidentZoneIntervalParam param) = 0;
	/*获取所有事故时段*/
	virtual QList<IAccidentZoneInterval*> accidentZoneIntervals() = 0;
	/*根据ID查询事故时段*/
	virtual IAccidentZoneInterval* findAccidentZoneIntervalById(long accidentZoneIntervalId) = 0;
	/*根据起始时间查询事故时段*/
	virtual IAccidentZoneInterval* findAccidentZoneIntervalByStartTime(long startTime) = 0;
};

#endif

