#include "MySimulator.h"

#include <QDebug>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "ivehicle.h"
#include "ivehicledriving.h"
#include "UnitChange.h"

MySimulator::MySimulator() {
}

void MySimulator::initVehicle(IVehicle *pIVehicle) {
    long tmpId = pIVehicle->id() % 100000;
    QString roadName = pIVehicle->roadName();
    long roadId = pIVehicle->roadId();
    if (roadName == QString("公路1")) {
        if (tmpId == 1) {
            pIVehicle->setVehiType(9);
            pIVehicle->initLane(1, m2p(60), 5);
            pIVehicle->initSpeed(0);
        } else if (tmpId == 2) {
            pIVehicle->setVehiType(1);
            pIVehicle->initLane(1, m2p(10), 8);
        } else if (tmpId == 3) {
            pIVehicle->setVehiType(2);
            pIVehicle->initLane(0, m2p(10), 5);
        }
    }
}

bool MySimulator::reCalcdesirSpeed(IVehicle *pIVehicle, qreal &inOutDesirSpeed) {
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    long tmpId = pIVehicle->id() % 100000;
    QString roadName = pIVehicle->roadName();
    if (roadName == QString("公路1") && simuTime >= 6000) {
        if (pIVehicle->vehicleTypeCode() == 9) {
            inOutDesirSpeed = m2p(50);
            return true;
        } else {
            inOutDesirSpeed = m2p(60);
            return true;
        }
    }
    return false;
}

bool MySimulator::reSetAcce(IVehicle *pIVehicle, qreal &inOutAcce) {
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    QString roadName = pIVehicle->roadName();
    if (roadName == QString("公路1") && simuTime >= 6000) {
        if (pIVehicle->vehicleTypeCode() == 1) {
            inOutAcce = m2p(8);
            return true;
        } else {
            inOutAcce = m2p(3);
            return true;
        }
    }
    return false;
}

bool MySimulator::reSetSpeed(IVehicle *pIVehicle, qreal &inOutSpeed) {
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    QString roadName = pIVehicle->roadName();
    if (roadName == QString("公路1") && simuTime >= 6000) {
        if (pIVehicle->vehicleTypeCode() == 1) {
            inOutSpeed = m2p(32);
            return true;
        } else {
            inOutSpeed = m2p(30);
            return true;
        }
    }
    return false;
}

bool MySimulator::reCalcToRightFreely(IVehicle *pIVehicle) {
    if (pIVehicle->vehicleDriving()->distToEndpoint() - pIVehicle->length() / 2 < m2p(20)) {
        return false;
    }
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    if (simuTime >= 6000) {
        if (pIVehicle->vehicleTypeCode() == 10) {
            if (pIVehicle->vehiDistFront() <= m2p(65)) {
                return true;
            }
        } else {
            return false;
        }
    }
    int carType = pIVehicle->vehicleTypeCode();
    if (carType == 9) {
        return true;
    }
    return false;
}

bool MySimulator::boundingRect(IVehicle *pIVehicle, QRectF &outRect) const {
    if (pIVehicle->vehicleTypeCode() == 1) {
        qreal length = pIVehicle->length() + 200;
        qreal w = length * 2;
        outRect.setLeft(m2p(-w / 2));
        outRect.setTop(m2p(-w / 2));
        outRect.setWidth(m2p(w));
        outRect.setHeight(m2p(w));
        return true;
    }
    return false;
}

bool MySimulator::paintVehicle(IVehicle *pIVehicle, QPainter *painter) {
    if (pIVehicle->vehicleTypeCode() == 1) {
        painter->setPen(Qt::NoPen);
        painter->setBrush(QBrush(QColor(254, 174, 165, 200), Qt::SolidPattern));
        painter->drawPie(-50, -50, 100, 103, 70 * 16, 40 * 16);
        return false;
    }
    return false;
}

void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    auto netiface = gpTessInterface->netInterface();
    long batchNum = simuiface->batchNumber();
    long simuTime = simuiface->simuTimeIntervalWithAcceMutiples();
    QList<IVehicle*> lAllVehi = simuiface->allVehiStarted();

    if (batchNum % 20 == 0) {
        QString runInfo = QString("路段数：%1\n运行车辆数：%2\n仿真时间：%3(毫秒)")
            .arg(netiface->linkCount())
            .arg(lAllVehi.size())
            .arg(simuTime);
        qDebug() << runInfo;
    }

    if (batchNum % 50 == 1) {
        QString color = QString("#%1%2%3")
            .arg(QString::number(qrand() % 256, 16).right(2))
            .arg(QString::number(qrand() % 256, 16).right(2))
            .arg(QString::number(qrand() % 256, 16).right(2));

        Online::DynaVehiParam dvp;
        dvp.vehiTypeCode = qrand() % 4 + 1;
        dvp.roadId = 6;
        dvp.laneNumber = qrand() % 3;
        dvp.dist = 50;
        dvp.speed = 20;
        dvp.color = color;
        simuiface->createGVehicle(dvp);

        Online::DynaVehiParam dvp2;
        dvp2.vehiTypeCode = qrand() % 4 + 1;
        dvp2.roadId = 3;
        dvp2.laneNumber = qrand() % 3;
        dvp2.toLaneNumber = dvp2.laneNumber;
        dvp2.dist = 50;
        dvp2.speed = 20;
        dvp2.color = color;
        simuiface->createGVehicle(dvp2);
    }
}

MySimulator::~MySimulator() {
}