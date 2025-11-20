#include "MySimulator.h"

#include <QDebug>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "ivehicle.h"
#include "ivehicledriving.h"
#include "UnitChange.h"
#include "Plugin/_datastruct.h"

MySimulator::MySimulator() {
}

void MySimulator::beforeStart(bool &keepOn) {
    keepOn = true;
}

bool MySimulator::reSetFollowingParam(IVehicle *pIVehicle, qreal &inOutSi, qreal &inOutSd) {
    IVehicle *front = pIVehicle->vehicleFront();
    if (pIVehicle->vehicleTypeCode() == 13 && front && front->vehicleTypeCode() == 13) {
        inOutSi = 0.8; // 编队CV缩短安全时距
        return true;
    }
    return false;
}

bool MySimulator::reCalcdesirSpeed(IVehicle *pIVehicle, qreal &inOutDesirSpeed) {
    if (pIVehicle->vehicleTypeCode() == 13) {
        IVehicle *front = pIVehicle->vehicleFront();
        if (!front) {
            inOutDesirSpeed = m2p(120.0 / 3.6);
            return true;
        } else if (front->vehicleTypeCode() == 13) {
            if (pIVehicle->vehiHeadwayFront() > 1.0) {
                inOutDesirSpeed = front->currSpeed() + m2p(20.0 / 3.6);
                return true;
            } else {
                inOutDesirSpeed = front->currSpeed();
                return true;
            }
        } else {
            if (pIVehicle->vehiHeadwayFront() > 2.0) {
                inOutDesirSpeed = front->currSpeed() + m2p(20.0 / 3.6);
                return true;
            } else {
                inOutDesirSpeed = front->currSpeed();
                return true;
            }
        }
    }
    return false;
}

bool MySimulator::reCalcToLeftFreely(IVehicle *pIVehicle) {
    IVehicle *leftFront = pIVehicle->vehicleLFront();
    IVehicle *leftRear = pIVehicle->vehicleLRear();
    if (pIVehicle->vehicleTypeCode() == 13 && ((leftFront && leftFront->vehicleTypeCode() == 13) || (leftRear && leftRear->vehicleTypeCode() == 13))) {
        mPlatoon[pIVehicle->id()] = 1;
        if (leftFront && leftFront->vehicleTypeCode() == 13) mPlatoon[leftFront->id()] = 1;
        if (leftRear && leftRear->vehicleTypeCode() == 13) mPlatoon[leftRear->id()] = 1;
        return true;
    }
    if (pIVehicle->vehicleDriving()->distToEndpoint() - pIVehicle->length() / 2 < m2p(20)) {
        return false;
    }
    return false;
}

QList<int> MySimulator::calcLimitedLaneNumber(IVehicle *pIVehicle) {
    if (pIVehicle->vehicleTypeCode() == 13 && mPlatoon.value(pIVehicle->id(), 0)) {
        int laneNum = pIVehicle->vehicleDriving()->laneNumber();
        if (laneNum == 1) {
            return QList<int>() << 0; // 限制右变道
        } else if (laneNum == 2) {
            return QList<int>() << 0 << 1; // 限制右变道
        }
    }
    return QList<int>();
}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    long batchNum = simuiface->batchNumber();

    if (batchNum % 3 == 0) {
        long second = batchNum / 3; // 0.4s间隔归一到秒级判断渗透率
        bool makeCV = (second % 10) < 5; // 约30%渗透率示例

        auto makeColor = [](int r, int g, int b) {
            QString rs = QString::number(r, 16).right(2).toUpper();
            QString gs = QString::number(g, 16).right(2).toUpper();
            QString bs = QString::number(b, 16).right(2).toUpper();
            if (rs.size() < 2) rs = QString("0") + rs;
            if (gs.size() < 2) gs = QString("0") + gs;
            if (bs.size() < 2) bs = QString("0") + bs;
            return QString("#") + rs + gs + bs;
        };

        Online::DynaVehiParam dvp;
        dvp.vehiTypeCode = makeCV ? 13 : 1;
        dvp.roadId = 1;
        dvp.laneNumber = qrand() % 4;
        dvp.dist = 0.01;
        dvp.speed = m2p((makeCV ? 80.0 : 60.0) / 3.6);
        dvp.color = makeCV ? makeColor(255, 105, 180) : makeColor(135, 206, 235);
        IVehicle *pVehi = simuiface->createGVehicle(dvp);
        if (pVehi) {
            if (makeCV) mPlatoon.insert(pVehi->id(), 0);
        }
    }
}

MySimulator::~MySimulator() {
}