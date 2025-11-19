#ifndef __IAccidentZoneInterval__
#define __IAccidentZoneInterval__

#include <QObject>
#include "tessinterfaces_global.h"
#include "UnitChange.h"

class TESSINTERFACES_EXPORT IAccidentZoneInterval
{
public:
    virtual ~IAccidentZoneInterval() = default;
    /* 事故区时段ID*/
    virtual long intervalId() = 0;
    /* 所属事故区ID */
    virtual long accidentZoneId() = 0;
    /* 事故区时段开始时间 */
    virtual long startTime() = 0;
    /* 事故区时段结束时间 */
    virtual long endTime() = 0;
    /* 事故区在该时段的长度，默认单位：像素*/
    virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
    /* 事故区在该时段的距起点距离，默认单位：像素*/
    virtual qreal location(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
    /* 事故区在该时段的限速，默认单位：像素(km/h)*/
    virtual qreal limitedSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
    /* 事故区在该时段的控制距离（车辆距离事故区起点该距离内，强制变道），默认单位：像素*/
    virtual qreal controlLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
    /* 事故区在该时段的占用车道序号*/
    virtual QList<int> laneNumbers() = 0;
};

#endif