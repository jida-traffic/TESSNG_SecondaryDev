#ifndef __IReduceSpeedInterval_H__
#define __IReduceSpeedInterval_H__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

#include <QObject>

class IReduceSpeedVehiType;

class TESSINTERFACES_EXPORT IReduceSpeedInterval
{
public:
    virtual ~IReduceSpeedInterval() {}
    /*限速时段ID*/
    virtual long id() = 0;
    /*所属限速区ID*/
    virtual long reduceSpeedAreaId() = 0;
    /*开始时间*/
    virtual long intervalStartTime() = 0;
    /*结束时间*/
    virtual long intervalEndTime() = 0;
    /*添加限速车型*/
    virtual IReduceSpeedVehiType* addReduceSpeedVehiType(Online::DynaReduceSpeedVehiTypeParam param) = 0;
    /*移除限速车型*/
    virtual void removeReduceSpeedVehiType(long id) = 0;
    /*更新限速车型*/
    virtual bool updateReduceSpeedVehiType(Online::DynaReduceSpeedVehiTypeParam param) = 0;
    /*本时段限速车型及限速参数*/
    virtual QList<IReduceSpeedVehiType*> reduceSpeedVehiTypes() = 0;
    /*根据id获取限速车型*/
    virtual IReduceSpeedVehiType* findReduceSpeedVehiTypeById(long id) = 0;
    /*根据车型代码获取限速车型*/
    virtual IReduceSpeedVehiType* findReduceSpeedVehiTypeByCode(long vehicleTypeCode) = 0;
};

#endif  