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
    //一个批次计算后的处理
    void afterOneStep() override;
private:
    // 绿灯时间(秒)
    int mGreenTime{60};
    // 采集器1过车数
    int mVehiCount{0};
    // 上游初始饱和平均占有率
    qreal mOBar{0};
    // 参数K_r
    int mKr{70};
};

#endif