#pragma once

#include <QObject>

#include "ISignalPlan.h"
#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

class TESSINTERFACES_EXPORT ISignalController
{
public:
    virtual ~ISignalController() = default;
    // 信号机属性
    virtual long id();
    virtual QString name();
    virtual void setName(const QString& name);

    // 信控方案操作
    virtual void addPlan(ISignalPlan* plan);
    virtual void removePlan(ISignalPlan* plan);
    virtual QList<ISignalPlan*> plans();

    // 检查信号机的有效性
    virtual bool isValid();

};
