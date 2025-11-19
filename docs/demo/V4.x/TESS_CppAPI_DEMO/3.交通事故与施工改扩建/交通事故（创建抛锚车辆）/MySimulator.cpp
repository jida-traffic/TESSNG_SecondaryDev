#include "MySimulator.h"

#include <QDebug>
#include <QDateTime>

#include "tessinterface.h"
#include "netinterface.h"
#include "simuinterface.h"
#include "ilink.h"
#include "ivehicle.h"
#include "ivehicledriving.h"
#include "ILane.h"
#include "UnitChange.h"
#include "Plugin/_datastruct.h"

MySimulator::MySimulator()
    : mBreakdownCreated(false), mBreakdownVehiId(0) {
}

//仿真前的准备
void MySimulator::beforeStart(bool &keepOn) {
    qDebug() << "beforeStart";
    keepOn = true;
}

//仿真起动后的处理
void MySimulator::afterStart() {
    qDebug() << "afterStart";
}

//仿真结束后的处理
void MySimulator::afterStop() {
    qDebug() << "afterStop";
}

//初始车辆
void MySimulator::initVehicle(IVehicle *pIVehicle) {
    if (!pIVehicle) return;
    long tmpId = pIVehicle->id() % 100000;
    QString roadName = pIVehicle->roadName();
    if (roadName == QString("路段1")) {
        pIVehicle->initSpeed(m2p(150));
    }
    qDebug() << "initVehicle" << tmpId << pIVehicle->vehicleTypeCode();
}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    //仿真时间（毫秒）
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    qDebug() << simuTime;

    //在仿真启动后创建一次抛锚车辆
    if (!mBreakdownCreated && simuTime >= 1000) {
        QList<ILink*> links = gpTessInterface->netInterface()->links();
        if (links.isEmpty()) return;

        ILink* pLink = links.first();
        //动态创建车辆参数（单位：米 / 米每秒）
        Online::DynaVehiParam dvp;
        dvp.name = QString("Breakdown_%1").arg(QDateTime::currentMSecsSinceEpoch());
        dvp.roadId = pLink->id();
        dvp.vehiTypeCode = 1;      //小客车
        dvp.laneNumber = 0;        //最右侧车道
        dvp.dist = 50;             //距起点50米
        dvp.speed = 0;             //初始速度为0（抛锚）
        dvp.color = QString("#AA0000");

        IVehicle* pVehi = gpTessInterface->simuInterface()->createGVehicle(dvp);
        if (pVehi) {
            QJsonObject info = pVehi->jsonInfo();
            info.insert("breakdown", true);
            info.insert("reason", QString("抛锚"));
            pVehi->setJsonInfo(info);

            mBreakdownVehiId = pVehi->id();
            mBreakdownCreated = true;

            qDebug() << "创建抛锚车辆" << "ID:" << mBreakdownVehiId
                     << "RoadId:" << dvp.roadId << "Lane:" << dvp.laneNumber
                     << "Dist(m):" << dvp.dist << "Speed(m/s):" << dvp.speed;
        }
    }

    long batchNum = gpTessInterface->simuInterface()->batchNumber();
    if (batchNum % 50 == 1) {
        QList<ILink*> links = gpTessInterface->netInterface()->links();
        if (!links.isEmpty()) {
            ILink* pLink = links.first();
            QString color = QString("#%1%2%3")
                .arg(QString::number(qrand() % 256, 16))
                .arg(QString::number(qrand() % 256, 16))
                .arg(QString::number(qrand() % 256, 16));

            Online::DynaVehiParam dvp;
            dvp.name = QString("Dyna_%1").arg(QDateTime::currentMSecsSinceEpoch());
            dvp.vehiTypeCode = qrand() % 4 + 1;
            dvp.roadId = pLink->id();
            dvp.laneNumber = 0;
            dvp.dist = 50;
            dvp.speed = 20;
            dvp.color = color;
            gpTessInterface->simuInterface()->createGVehicle(dvp);
        }
    }
}

//是否停车运行（用于抛锚车辆）
bool MySimulator::isStopDriving(IVehicle* pIVehicle) {
    if (!pIVehicle) return false;
    //若设置了抛锚标记则停止运行
    QVariant v = pIVehicle->jsonProperty("breakdown");
    if (v.isValid() && v.toBool()) {
        //持续打印一次性提示（首次进入停止逻辑时）
        if (pIVehicle->id() == mBreakdownVehiId) {
            static bool printed = false;
            if (!printed) {
                qDebug() << "抛锚车辆已停止" << "ID:" << pIVehicle->id();
                printed = true;
            }
        }
        return true;
    }
    if (pIVehicle->roadIsLink()) {
        qreal dist = pIVehicle->vehicleDriving()->distToEndpoint(true);
        if (dist < m2p(5)) {
            return false;
        }
    }
    return false;
}

//重新设置跟驰的安全时距及安全距离
bool MySimulator::reSetFollowingParam(IVehicle *pIVehicle, qreal &inOutSi, qreal &inOutSd) {
    if (!pIVehicle) return false;
    QString roadName = pIVehicle->roadName();
    if (roadName == QString("连接段1")) {
        inOutSd = m2p(5);
        return true;
    }
    return false;
}

//重新设置速度（抛锚车辆在指定时间窗内速度设为0）
bool MySimulator::reSetSpeed(IVehicle *pIVehicle, qreal &inOutSpeed) {
    if (!pIVehicle) return false;
    long tmpId = pIVehicle->id() % 100000;
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    if (tmpId == 11 && simuTime >= 78000 && simuTime <= 308000) {
        qDebug() << tmpId << pIVehicle->vehicleTypeCode();
        if (pIVehicle->vehicleTypeCode() != 12) {
            inOutSpeed = m2p(0);
            return true;
        }
    }
    return false;
}

//计算限制车道序号：如管制、危险等
QList<int> MySimulator::calcLimitedLaneNumber(IVehicle *pIVehicle) {
    QList<int> limited; //默认不限制
    if (!pIVehicle) return limited;
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    ILane *lane = pIVehicle->lane();
    if (lane && pIVehicle->roadIsLink()) {
        ILink *link = pIVehicle->startLink();
        if (link && link->id() == 3 && simuTime >= 25000) {
            return limited; 
        }
    }
    return limited;
}

MySimulator::~MySimulator() {
}