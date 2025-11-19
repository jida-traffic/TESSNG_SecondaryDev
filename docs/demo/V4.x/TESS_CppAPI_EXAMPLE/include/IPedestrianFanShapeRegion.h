#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsPathItem>

class IPedestrianFanShapeRegion : public QGraphicsPathItem, virtual public IPedestrianRegion, virtual public IObstacleRegion, virtual public IPassengerRegion
{
public:
    virtual ~IPedestrianFanShapeRegion() = default;
    virtual qreal getInnerRadius() const = 0; //获取内半径 单位米
    virtual qreal getOuterRadius() const = 0; //获取外半径 单位米
    virtual qreal getStartAngle() const = 0; //获取起始角度 单位度，Qt坐标系下，x轴正方向为0，逆时针为正
    virtual qreal getSweepAngle() const = 0; //获取扫过角度 单位度，Qt坐标系下，x轴正方向为0，逆时针为正
};