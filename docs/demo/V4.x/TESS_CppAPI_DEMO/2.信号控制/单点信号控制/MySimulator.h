#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>

#include "Plugin/customersimulator.h"
#include <QMap>
#include <QList>

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    //一个批次计算后的处理
    void afterOneStep() override;
private:
    // 配置：每相位采集器ID列表
    QMap<long, QList<long>> mPhase2Collector{
        {8, {2, 3, 14, 15}},
        {7, {4, 5, 6, 16, 17, 18}},
        {6, {7, 8, 19, 20, 21}},
        {5, {9, 10, 11, 12, 22, 23, 24, 25}},
    };
    // 配置：采集器集计周期(秒)
    const int mAggregateTime = 180;
    // 配置：单车道通行能力(辆/小时)
    const int mCapacity = 1400;
    // 计算韦伯斯特配时
    void webster(int periodTime,
                 const QMap<long, QMap<QString, int>>& phaseInterval,
                 const QMap<long, double>& phaseFlow,
                 int& outNewPeriod,
                 QMap<long, QMap<QString, int>>& outNewPhaseInterval);
};

#endif