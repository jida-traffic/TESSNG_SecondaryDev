#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>
#include <QMap>
#include <QList>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    // 仿真前的准备
    void beforeStart(bool &keepOn) override;
    // 重新设置跟驰参数
    bool reSetFollowingParam(IVehicle *pIVehicle, qreal &inOutSi, qreal &inOutSd) override;
    // 重新计算期望速度
    bool reCalcdesirSpeed(IVehicle *pIVehicle, qreal &inOutDesirSpeed) override;
    // 计算是否要左自由变道
    bool reCalcToLeftFreely(IVehicle *pIVehicle) override;
    // 计算限制车道序号列表
    QList<int> calcLimitedLaneNumber(IVehicle *pIVehicle) override;
    //一个批次计算后的处理
    void afterOneStep() override;

private:
    QMap<long, int> mPlatoon; // 车辆是否编队标记
};

#endif