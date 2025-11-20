#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>
#include <QRectF>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    // 初始化车辆
    void initVehicle(IVehicle *pIVehicle) override;
    // 重新计算期望速度
    bool reCalcdesirSpeed(IVehicle *pIVehicle, qreal &inOutDesirSpeed) override;
    // 重新设置加速度
    bool reSetAcce(IVehicle *pIVehicle, qreal &inOutAcce) override;
    // 重新设置速度
    bool reSetSpeed(IVehicle *pIVehicle, qreal &inOutSpeed) override;
    // 计算是否要右自由变道
    bool reCalcToRightFreely(IVehicle *pIVehicle) override;
    // 调整车辆绘制边界
    bool boundingRect(IVehicle *pIVehicle, QRectF &outRect) const override;
    // 绘制车辆（雷达扇形）
    bool paintVehicle(IVehicle *pIVehicle, QPainter *painter) override;
    //一个批次计算后的处理
    void afterOneStep() override;
};

#endif