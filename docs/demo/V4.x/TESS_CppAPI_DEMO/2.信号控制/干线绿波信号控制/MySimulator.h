#ifndef __MySimulator__
#define __MySimulator__

#include "Plugin/customersimulator.h"

class MySimulator : public CustomerSimulator
{
public:
    MySimulator();
    ~MySimulator() override;

    QList<Online::SignalContralParam> calcDynaSignalContralParameters() override;
};

#endif