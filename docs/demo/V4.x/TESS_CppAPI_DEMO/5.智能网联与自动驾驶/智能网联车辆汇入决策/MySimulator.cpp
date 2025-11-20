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

void MySimulator::beforeStart(bool &keepOn) {
    keepOn = true;
}

bool MySimulator::reCalcToLeftFreely(IVehicle *pIVehicle) {
    if (mGetPermission.contains(pIVehicle->id()) && pIVehicle->vehicleDriving()->laneNumber() == 0) {
        return true;
    }
    return false;
}

QList<int> MySimulator::calcLimitedLaneNumber(IVehicle *pIVehicle) {
    if (pIVehicle->vehicleTypeCode() == 8 && pIVehicle->roadId() == 5 && pIVehicle->vehicleDriving()->laneNumber() == 0) {
        qreal distToEnd = pIVehicle->vehicleDriving()->distToEndpoint() - pIVehicle->length() / 2;
        if (mGetPermission.contains(pIVehicle->id()) || distToEnd < m2p(10)) {
            return QList<int>();
        }
        return QList<int>() << 1 << 2 << 3;
    }
    return QList<int>();
}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    long batchNum = simuiface->batchNumber();
    QList<IVehicle*> lAllVehi = simuiface->allVehiStarted();

    QList<IVehicle*> lVehisPre = simuiface->vehisInLink(5);
    QList<IVehicle*> targetLane;
    for (IVehicle* v : lVehisPre) {
        if (v->vehicleDriving()->laneNumber() == 1) targetLane.append(v);
    }

    QList<IVehicle*> lVehisPost = simuiface->vehisInLink(1);
    QList<IVehicle*> postLane;
    for (IVehicle* v : lVehisPost) {
        if (v->vehicleDriving()->laneNumber() == 1) postLane.append(v);
    }

    for (IVehicle* vehi : lAllVehi) {
        if (vehi->vehicleTypeCode() == 10 && vehi->roadId() == 5 && vehi->vehicleDriving()->laneNumber() == 0) {
            IVehicle *leftFront = nullptr, *leftRear = nullptr;
            for (IVehicle* vTL : targetLane) {
                if (vehi->vehicleDriving()->currDistanceInRoad() < vTL->vehicleDriving()->currDistanceInRoad()) {
                    if (!leftFront || leftFront->vehicleDriving()->currDistanceInRoad() > vTL->vehicleDriving()->currDistanceInRoad()) leftFront = vTL;
                } else if (vehi->vehicleDriving()->currDistanceInRoad() > vTL->vehicleDriving()->currDistanceInRoad()) {
                    if (!leftRear || leftRear->vehicleDriving()->currDistanceInRoad() < vTL->vehicleDriving()->currDistanceInRoad()) leftRear = vTL;
                }
            }
            if (!leftRear && !postLane.isEmpty()) {
                IVehicle* temp = postLane.first();
                for (int i = 1; i < postLane.size(); ++i) {
                    if (postLane[i]->vehicleDriving()->distToEndpoint() < temp->vehicleDriving()->distToEndpoint()) temp = postLane[i];
                }
                leftRear = temp;
            }

            double leftFrontTHW = std::numeric_limits<double>::infinity();
            double leftRearTHW = std::numeric_limits<double>::infinity();
            if (leftFront && p2m(vehi->currSpeed()) > p2m(leftFront->currSpeed())) {
                leftFrontTHW = (p2m(leftFront->vehicleDriving()->currDistanceInRoad()) - p2m(vehi->vehicleDriving()->currDistanceInRoad())) /
                               (p2m(vehi->currSpeed()) - p2m(leftFront->currSpeed()));
            }
            if (leftRear && p2m(vehi->currSpeed()) < p2m(leftRear->currSpeed())) {
                leftRearTHW = (p2m(vehi->vehicleDriving()->currDistanceInRoad()) - p2m(leftRear->vehicleDriving()->currDistanceInRoad())) /
                              (p2m(vehi->currSpeed()) - p2m(leftRear->currSpeed()));
            }

            if (leftFront && leftRear) {
                qDebug() << batchNum << vehi->id() << leftFront->id() << leftRear->id() << leftFrontTHW << leftRearTHW;
            } else {
                qDebug() << batchNum << vehi->id() << QVariant() << QVariant() << leftFrontTHW << leftRearTHW;
            }

            if (leftFrontTHW >= 0.9 && leftRearTHW >= 1.5) {
                qDebug() << "ke";
                mGetPermission.insert(vehi->id());
            }
        }
    }
}

MySimulator::~MySimulator() {
}