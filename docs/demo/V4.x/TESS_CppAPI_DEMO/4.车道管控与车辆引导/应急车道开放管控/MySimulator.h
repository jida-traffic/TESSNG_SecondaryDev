#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    bool reCalcDismissChangeLane(IVehicle *pIVehicle) override;
    //一个批次计算后的处理
    void afterOneStep() override;

private:
    bool mOpenEmergencyLaneFlag{ false };
    QString judgeVehicleLaneChangeDirection(IVehicle *pIVehicle);
};

#endif