#ifndef __IReduceSpeedVehiType_H__
#define __IReduceSpeedVehiType_H__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>

class TESSINTERFACES_EXPORT IReduceSpeedVehiType
{
public:
    virtual ~IReduceSpeedVehiType() {}
    /*限速车型ID*/
    virtual long id() = 0;
    /*所属限速时段ID*/
    virtual long intervalId() = 0;
    /* 所属限速区ID */
    virtual long reduceSpeedAreaId() = 0;
    /* 车型编码 */
    virtual long vehiTypeCode() = 0;
    /* 平均车速, 像素/秒*/
    virtual qreal averageSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
    /* 车速标准差, 像素/秒 */
    virtual qreal speedStandardDeviation(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
};

#endif