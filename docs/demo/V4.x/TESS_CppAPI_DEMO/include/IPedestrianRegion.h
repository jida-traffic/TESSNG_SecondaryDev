#pragma once

#include "IObstacleRegion.h"
#include "IPassengerRegion.h"
#include "IPedestrianPathRegionBase.h"

class IPedestrianRegion : virtual public IPedestrianPathRegionBase
{
public:
    virtual ~IPedestrianRegion() = default;

    virtual long getId() const = 0; //获取面域ID
    virtual QString getName() const = 0; //获取面域名称
    virtual void setName(QString name) = 0; //设置面域名称
    virtual QColor getRegionColor() const = 0; //获取面域颜色
    virtual void setRegionColor(QColor color) = 0; //设置面域颜色
    virtual QPointF getPosition(UnitOfMeasure unit = UnitOfMeasure::Default) const = 0; //获取面域位置
    virtual void setPosition(QPointF scenePos, UnitOfMeasure unit = UnitOfMeasure::Default) = 0; //设置面域位置, scenePos Qt场景坐标系下, 默认单位为像素
    virtual int getGType() const = 0; //获取面域类型

    virtual qreal getExpectSpeedFactor() const = 0; //获取期望速度系数
    virtual void setExpectSpeedFactor(qreal val) = 0; //设置期望速度系数
    virtual qreal getElevation() const = 0; //获取面域高程
    virtual void setElevation(qreal elevation) = 0; //设置面域高程
    virtual QPolygonF getPolygon(bool sceneCoords = false) const = 0; //获取面域多边形
    virtual long getLayerId() const = 0; //获取面域所在图层id
    virtual void setLayerId(long id) = 0; //设置面域所在图层，如果图层id非法，则不做任何改变
};