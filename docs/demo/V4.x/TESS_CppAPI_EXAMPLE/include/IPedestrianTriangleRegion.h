#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsPolygonItem>

class IPedestrianTriangleRegion : public QGraphicsPolygonItem, virtual public IPedestrianRegion, virtual public IObstacleRegion, virtual public IPassengerRegion
{
public:
    virtual ~IPedestrianTriangleRegion() = default;
};