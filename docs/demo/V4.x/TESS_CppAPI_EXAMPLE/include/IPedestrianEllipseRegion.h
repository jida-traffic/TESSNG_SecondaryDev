#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsEllipseItem>

class IPedestrianEllipseRegion : public QGraphicsEllipseItem, virtual public IPedestrianRegion, virtual public IObstacleRegion, virtual public IPassengerRegion
{
public:
    virtual ~IPedestrianEllipseRegion() = default;
};