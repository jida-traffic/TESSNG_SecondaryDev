#include "MySimulator.h"
#include <QDebug>
#include <QtMath>
#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "IPedestrianCrossWalkRegion.h"
#include "UnitChange.h"
#include "ivehicle.h"
#include "ISection.h"

MySimulator::MySimulator() {

}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    auto si = gpTessInterface->simuInterface();
    auto ni = gpTessInterface->netInterface();
    long simuTime = si->simuTimeIntervalWithAcceMutiples();
    if (simuTime / 1000 == mLastSimuTime / 1000) {
        return;
    }

    QHash<long, QList<Online::Pedestrian::PedestrianStatus>> crossWalkPedestrians;
    for (auto crossWalk : ni->pedestrianCrossWalkRegions()) {
        long regionId = crossWalk->getId();
        auto pedestrians = si->getPedestriansStatusByRegionId(regionId);
        crossWalkPedestrians.insert(regionId, pedestrians);

        if (!mCrossConflict.contains(regionId)) {
            QPolygonF poly = crossWalk->getPolygon(true);
            QPointF pos = crossWalk->getPosition();
            QList<QPointF> pointsCor;
            for (const QPointF& pt : poly) {
                pointsCor.append(QPointF(p2m(pos.x() + pt.x()), -p2m(pos.y() + pt.y())));
            }
            qreal left = std::numeric_limits<qreal>::infinity();
            qreal right = -std::numeric_limits<qreal>::infinity();
            qreal bottom = std::numeric_limits<qreal>::infinity();
            qreal top = -std::numeric_limits<qreal>::infinity();
            for (const QPointF& p : pointsCor) {
                left = qMin(left, p.x());
                right = qMax(right, p.x());
                bottom = qMin(bottom, p.y());
                top = qMax(top, p.y());
            }
            Range r{ top + mOffset, left - mOffset, right + mOffset, bottom - mOffset };
            mCrossConflict.insert(regionId, r);
        }
    }

    auto vehicles = si->allVehicle();
    for (auto veh : vehicles) {
        QPointF vpos = veh->pos();
        qreal veh_x = p2m(vpos.x());
        qreal veh_y = p2m(vpos.y());
        qreal veh_angle = veh->angle();
        for (auto it = mCrossConflict.constBegin(); it != mCrossConflict.constEnd(); ++it) {
            auto r = it.value();
            if (!(veh_x >= r.left && veh_x <= r.right && veh_y >= r.bottom && veh_y <= r.top)) {
                continue;
            } else {
                for (const auto& ped : crossWalkPedestrians.value(it.key())) {
                    QPointF pos = ped.pos;
                    qreal ped_x = p2m(pos.x());
                    qreal ped_y = p2m(pos.y());
                    QVector2D dir = ped.mDirection;
                    qreal ped_angle = qRadiansToDegrees(qAtan2(p2m(dir.y()), p2m(dir.x()))) + 90.0;
                    qreal delta_angle = qAbs(ped_angle - veh_angle);
                    qreal distance = qSqrt(qPow(veh_x - ped_x, 2) + qPow(veh_y - ped_y, 2));
                    if (delta_angle <= 30.0 || (delta_angle >= 150.0 && delta_angle <= 210.0)) {
                        continue;
                    }
                    if (distance <= 0.5 + veh->length() / 2.0 && (ped_angle - veh_angle) != 0) {
                        long ped_id = ped.id;
                        long veh_id = veh->id();
                        ISection* section = veh->section();
                        qreal d = qMax(distance - veh->length() / 2.0, 0.0);
                        if (section && section->isLink()) {
                            qDebug() << "仿真时间" << (simuTime / 1000) << "s，行人与机动车冲突信息：{"
                                     << "行人ID:" << ped_id << ", 人行横道ID:" << it.key() << ", 车辆ID:" << veh_id
                                     << ", 路段ID:" << section->id() << ", 距离:" << d << "}";
                        } else if (section) {
                            qDebug() << "仿真时间" << (simuTime / 1000) << "s，行人与机动车冲突信息：{"
                                     << "行人ID:" << ped_id << ", 人行横道ID:" << it.key() << ", 车辆ID:" << veh_id
                                     << ", 连接段ID:" << section->id() << ", 距离:" << d << "}";
                        }
                    }
                }
                break;
            }
        }
    }
    mLastSimuTime = simuTime;
}

MySimulator::~MySimulator() {
}