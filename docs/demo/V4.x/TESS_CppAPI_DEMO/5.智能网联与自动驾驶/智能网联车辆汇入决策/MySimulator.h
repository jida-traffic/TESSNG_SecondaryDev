#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>
#include <QSet>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    // 仿真前的准备
    void beforeStart(bool &keepOn) override;
    // 计算是否要左自由变道
    bool reCalcToLeftFreely(IVehicle *pIVehicle) override;
    // 计算车辆当前限制车道序号列表
    QList<int> calcLimitedLaneNumber(IVehicle *pIVehicle) override;
    //一个批次计算后的处理
    void afterOneStep() override;

private:
    QSet<long> mGetPermission; // 获得换道允许的车辆ID集合
};

#endif