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
    //一个批次计算后的处理
    void afterOneStep() override;
    //动态修改决策点路径流量比参数
    QList<Online::DecipointFlowRatioByInterval> calcDynaFlowRatioParameters() override;
    //仿真结束后的处理
    void afterStop() override;

private:
    QMap<long, QList<qreal>> mVehiQueueAggregationDict;
    bool mVehiQueueAggregateFlag{ false };
    QList<qreal> mAggrIntervals;
    QList<QList<qreal>> mDecisionPointFlowRatio;
    QString mIntervalKey;
    QMap<long, int> mRoutingInitFlow; // routingId -> flow
};

#endif