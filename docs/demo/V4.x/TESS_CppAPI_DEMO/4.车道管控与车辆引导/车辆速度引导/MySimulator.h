#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>
#include <QList>
#include <QMap>
#include <QPair>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    // 仿真开始前
    void beforeStart(bool &keepOn) override;
    //一个批次计算后的处理
    void afterOneStep() override;
    // 重新计算期望速度
    bool reCalcdesirSpeed(IVehicle *pIVehicle, qreal &inOutDesirSpeed) override;
    // 仿真结束后
    void afterStop() override;

private:
    // 配置参数
    bool Is_Guidance = true;
    int SimTime = 3600; // s
    qreal acceleration = 3.5; // m/s^2
    qreal deceleration = 3.0; // m/s^2
    qreal stop_weight = 5.0;
    qreal Guidance_Length = 200.0; // m
    qreal defaultDesSpeed = 60.0; // km/h
    qreal fromSpeed = 30.0; // km/h
    qreal toSpeed = 80.0;   // km/h
    qreal desGap = 5.0;     // km/h

    QList<int> LinksID{1, 2, 3};
    QList<int> allLinksID{1, 101, 2, 102, 3, 103, 4};
    QList<qreal> LinksLength{921.4, 1912.7, 987.3};
    QList<qreal> SignalHeadsPos{858.7, 1838.7, 982.7};
    int SignalCycle = 72; // s
    int SignalRed = 54;   // s
    QMap<int, int> SignalHead_offset{{1, 0}, {2, 0}, {3, 0}};

    // 运行时数据
    QList<QList<qreal>> GuidancePoints; // 每个目标路段的引导点
    QList<QList<qreal>> datas; // currentTime,vehID,vehType,vehSpeed,vehLink,vehPos,desSpeed,realVehPos
    QMap<int, qreal> QueueLength_list; // linkId -> length(m)
    QMap<long, qreal> Speed_Guidance;  // vehId -> guidance speed (m/s)

    // 工具函数
    qreal CalcFU(qreal v, qreal d);
    qreal CalcAccFU(qreal v0, qreal vt);
    qreal CalcStopFU(qreal finalSpeed = 60.0 / 3.6);
    qreal CalcWholeFU(const QList<qreal> &speedList);
    QPair<qreal, QPair<qreal, qreal>> CalcSegmentFU(qreal currentSpeed, qreal targetSpeed, qreal QL = 0.0);
    bool CheckSignalHeadRed(qreal currentTime, qreal t, int linkid);
    qreal GetRealVehPos(int linkid, qreal vehpos);
    QList<qreal> GetQueueLength(const QList<IVehicle*> &vehs_info, const QList<int> &target_linksIDs);
    qreal FindOptimalSpeed(qreal currentSpeed, qreal currentTime, int guidancePointCount, int linkid, qreal QL);
    void CookDatas();

signals:
    void forStopSimu();
    void forReStartSimu();
};

#endif