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
    //仿真前的准备
    void beforeStart(bool &keepOn) override;
    //仿真起动后的处理
    void afterStart() override;
    //仿真结束后的处理
    void afterStop() override;
    //初始车辆
    void initVehicle(IVehicle *pIVehicle) override;
    //一个批次计算后的处理
    void afterOneStep() override;
    //是否停车运行（用于抛锚车辆）
    bool isStopDriving(IVehicle *pIVehicle) override;
    //重新设置跟驰的安全时距及安全距离
    bool reSetFollowingParam(IVehicle *pIVehicle, qreal &inOutSi, qreal &inOutSd) override;
    //重新设置速度
    bool reSetSpeed(IVehicle *pIVehicle, qreal &inOutSpeed) override;
    //计算限制车道序号：如管制、危险等
    QList<int> calcLimitedLaneNumber(IVehicle *pIVehicle) override;

private:
    //是否已创建抛锚车辆
    bool mBreakdownCreated;
    //抛锚车辆ID
    long mBreakdownVehiId;
};

#endif