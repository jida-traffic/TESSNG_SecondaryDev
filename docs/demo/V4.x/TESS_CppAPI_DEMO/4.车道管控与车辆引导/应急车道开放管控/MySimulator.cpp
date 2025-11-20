#include "MySimulator.h"
#include <QDebug>
#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "IDecisionPoint.h"
#include "ILane.h"
#include "ivehicle.h"
#include <QtMath>

MySimulator::MySimulator() {

}

QString MySimulator::judgeVehicleLaneChangeDirection(IVehicle *pIVehicle) {
    ILane *lane = pIVehicle->lane();
    if (!lane) return QString("noChange");
    QPointF vehiPos = pIVehicle->pos();
    qreal distToStart = lane->distToStartPoint(vehiPos);
    QList<QPointF> centerBreakPoints = lane->centerBreakPoints();
    int segIndex = -1;
    for (int i = 0; i < centerBreakPoints.size(); ++i) {
        if (distToStart < lane->distToStartPoint(centerBreakPoints[i])) { segIndex = i; break; }
    }
    if (segIndex > 0 && segIndex < centerBreakPoints.size()) {
        QPointF startP = centerBreakPoints[segIndex - 1];
        QPointF endP = centerBreakPoints[segIndex];
        qreal dx = endP.x() - startP.x();
        qreal dy = endP.y() - startP.y();
        qreal angle_deg = qRadiansToDegrees(std::atan2(dy, dx));
        angle_deg = std::fmod(angle_deg + 360.0, 360.0);
        angle_deg = std::fmod(angle_deg - 90.0 + 360.0, 360.0);
        angle_deg = std::fmod(angle_deg + 180.0, 360.0);

        QPointF lineVec(dx, dy);
        QPointF normalVec(-lineVec.y(), lineVec.x());
        QPointF carVec(vehiPos.x() - startP.x(), vehiPos.y() - startP.y());
        qreal cross = carVec.x() * normalVec.x() + carVec.y() * normalVec.y();
        if (cross > 0) {
            if (pIVehicle->angle() > angle_deg) return QString("right");
        } else if (cross < 0) {
            if (pIVehicle->angle() < angle_deg) return QString("left");
        }
        return QString("noChange");
    }
    return QString("noChange");
}

bool MySimulator::reCalcDismissChangeLane(IVehicle *pIVehicle) {
    if (!mOpenEmergencyLaneFlag) {
        if (pIVehicle->roadId() == 1044) {
            ILane *lane = pIVehicle->lane();
            if (lane && lane->number() == 1) {
                if (judgeVehicleLaneChangeDirection(pIVehicle) == QString("right")) {
                    return true;
                }
            }
        }
    }
    return false;
}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    auto netiface = gpTessInterface->netInterface();
    long simuTime = simuiface->simuTimeIntervalWithAcceMutiples();

    if (simuTime > 500 * 1000 && simuTime <= 1000 * 1000) {
        mOpenEmergencyLaneFlag = true;
    }
    if (mOpenEmergencyLaneFlag) {
        qDebug() << "提示：\nL1033路段发生事故，车辆拥堵加剧，应急车道开放！\nL1044路段发生事故，车辆拥堵加剧，应急车道开放！";
        QList<IDecisionPoint*> lDeci = netiface->decisionPoints();
        for (IDecisionPoint* pDeci : lDeci) {
            QList<IRouting*> lRouting = pDeci->routings();
            for (IRouting* pRouting : lRouting) {
                if (netiface->removeDeciRouting(pDeci, pRouting)) {
                    qDebug() << "应急车道开放，删除路段决策点！";
                }
            }
        }
    }
}

MySimulator::~MySimulator() {
}