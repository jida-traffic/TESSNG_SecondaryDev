#pragma once

/**
 *
 * @brief 人行横道红绿灯
 * 通常作为人行横道面域的子项使用，位置坐标为人行横道坐标系下的点位
 * @author 孙文光
 * 
 */

#include "isignallamp.h"
#include "tessinterfaces_global.h"
#include <QGraphicsPathItem>

class IPedestrianCrossWalkRegion;

class TESSINTERFACES_EXPORT ICrosswalkSignalLamp :public ISignalLamp, public QGraphicsPathItem
{
public:
    ICrosswalkSignalLamp(QGraphicsItem* parent);
    virtual ~ICrosswalkSignalLamp() = default;

    virtual IPedestrianCrossWalkRegion* getICrossWalk() const = 0; //获取所属人行横道 
};