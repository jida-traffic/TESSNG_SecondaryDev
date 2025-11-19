#pragma once

#include "UnitChange.h"

class IPedestrianPathRegionBase
{
public:
    virtual ~IPedestrianPathRegionBase() = default;
    virtual long getId() const = 0; //获取面域ID
    virtual QString getName() const = 0; //获取面域名称
    virtual void setName(QString name) = 0; //设置面域名称
    virtual QColor getRegionColor() const = 0; //获取面域颜色
    virtual void setRegionColor(QColor color) = 0; //设置面域颜色
    virtual QPointF getPosition(UnitOfMeasure unit = UnitOfMeasure::Default) const = 0; //获取面域位置
    virtual void setPosition(QPointF scenePos, UnitOfMeasure unit = UnitOfMeasure::Default) = 0; //设置面域位置, scenePos Qt场景坐标系下, 默认单位为像素
    virtual int getGType() const = 0; //获取面域类型
    virtual bool isEvaluationRegion() const = 0; //获取是否进行面域评价
    virtual void setEvaluationRegion(bool b) = 0; //设置是否进行面域评价
};