#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>

#include "Plugin/customersimulator.h"
#include "isignallamp.h"
#include "Plugin/_datastruct.h"
#include <QList>

class MySimulator : public QObject, public CustomerSimulator
{
	Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    //一个批次计算后的处理
    void afterOneStep() override;
    bool calcLampColor(ISignalLamp *pSignalLamp) override;
private:
    long mPrevSec{0};
    long mBusArrivalSec{0};
    long mTg2r{30};
    long mTr2g{0};
    long mPhaseChange{0};
    QList<Online::VehiInfoCollected> mOutVehiInfo;
};

#endif