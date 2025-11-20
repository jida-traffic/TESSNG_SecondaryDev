#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>
#include <QList>
#include <QMap>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    // 初始化车辆
    void initVehicle(IVehicle *pIVehicle) override;
    // 计算限制车道序号列表
    QList<int> calcLimitedLaneNumber(IVehicle *pIVehicle) override;
    //一个批次计算后的处理
    void afterOneStep() override;
    // 降低自由变道频率（左/右）
    void beforeToLeftFreely(IVehicle *pIVehicle, bool &bKeepOn) override;
    void beforeToRightFreely(IVehicle *pIVehicle, bool &bKeepOn) override;
    // 车辆强制换道时，提高车速
    bool reSetSpeed(IVehicle *pIVehicle, qreal &inOutSpeed) override;

private:
    // 基本图参数
    qreal Kj_GL{56};
    qreal Qc_GL{1680};
    qreal Vf_GL{71.14};
    qreal Vm_GL{39};

    qreal Kj_ML{50};
    qreal Qc_ML{1680};
    qreal Vf_ML{72};
    qreal Vm_ML{40};

    // 采集值与计算值
    qreal Q_GL{0}, V_GL{0};
    qreal Q_ML{0}, V_ML{0};
    qreal Q6{0}, Q7{0}, Q8{0};
    qreal V6{0}, V7{0}, V8{0};
    qreal V_GL1{0}, V_GL2{0}, V_GL3{0}, V_ML_out{0};
    qreal MLFlow{0}, GLFlow{0};
    qreal MLSpeed{0}, GLSpeed{0};
    qreal solutionMLFlow{0}, solutionGLFlow{0};
    qreal solutionMLSpeed{0}, solutionGLSpeed{0};
    qreal tempSpeedDiffRate{0}, solutionSpeedDiffRate{0};
    qreal solutionProductivity{0};
    qreal AllowVehsFlow{0};
    qreal speedDiffRate{0.1};

    // 变道控制
    int GL2ML{0};
    int ML2GL{0};
    int count{0};
    QMap<long, bool> managedCars;
    QList<long> Cars;

    // 工具函数
    qreal SpeedFlowFunction(qreal x, qreal kj, qreal qc, qreal vf, qreal vm);
    qreal CalcSpeedbyFlow(qreal Y, bool isGL = true, bool getBigX = true);
};

#endif