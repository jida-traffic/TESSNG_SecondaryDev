#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsPolygonItem>

class IPedestrianPolygonRegion : public QGraphicsPolygonItem, virtual public IPedestrianRegion, virtual public IObstacleRegion, virtual public IPassengerRegion
{
public:
    virtual ~IPedestrianPolygonRegion() = default;
};