#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsRectItem>

class IPedestrianRectRegion : public QGraphicsRectItem, virtual public IPedestrianRegion, virtual public IObstacleRegion, virtual public IPassengerRegion
{
public:
    virtual ~IPedestrianRectRegion() = default;
};