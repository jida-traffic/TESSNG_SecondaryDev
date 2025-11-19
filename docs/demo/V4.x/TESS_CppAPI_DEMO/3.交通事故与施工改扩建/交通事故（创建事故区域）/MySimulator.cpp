#include "MySimulator.h"

#include <QDebug>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "IAccidentZone.h"
#include "IAccidentZoneInterval.h"
#include "UnitChange.h"
#include "ilink.h"

MySimulator::MySimulator() {

}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    long simuTimeMs = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();

    QList<IAccidentZone*> zonesInitCheck = gpTessInterface->netInterface()->accidentZones();
    if (zonesInitCheck.isEmpty() && simuTimeMs >= 1000) {
        QList<ILink*> lLinks = gpTessInterface->netInterface()->links();
        if (!lLinks.isEmpty()) {
            ILink* pLink = lLinks.first();
            Online::DynaAccidentZoneParam param;
            param.name = QString("事故区域示例");
            param.roadId = pLink->id();
            param.location = 60;
            param.length = 80;
            param.startTime = 0;
            param.duration = 600;
            param.limitSpeed = 20;
            param.controlLength = 100;
            param.needStayed = true;
            param.mlFromLaneNumber << 1 << 2;
            IAccidentZone* pZone = gpTessInterface->netInterface()->createAccidentZone(param);
            if (pZone) {
                qDebug() << "[事故区域] 创建成功"
                         << "id=" << pZone->id()
                         << "name=" << pZone->name()
                         << "roadId=" << pZone->roadId()
                         << "location(m)=" << pZone->location()
                         << "length(m)=" << pZone->zoneLength()
                         << "limitSpeed(km/h)=" << pZone->limitedSpeed()
                         << "controlLength(m)=" << pZone->controlLength();
            } else {
                qDebug() << "[事故区域] 创建失败";
            }
        }
    }
    // 每 5 秒打印一次事故区当前信息
    if (simuTimeMs % (5 * 1000) == 0) {
        QList<IAccidentZone*> zones = gpTessInterface->netInterface()->accidentZones();
        if (zones.isEmpty()) {
            qDebug() << "[事故区域] 当前无事故区";
            return;
        }
        for (IAccidentZone* z : zones) {
            QList<IAccidentZoneInterval*> intervals = z->accidentZoneIntervals();
            IAccidentZoneInterval* curItv = nullptr;
            // 查找当前时间命中的时段
            long simuSec = simuTimeMs / 1000;
            for (IAccidentZoneInterval* itv : intervals) {
                if (simuSec >= itv->startTime() && (itv->endTime() == 0 || simuSec <= itv->endTime())) {
                    curItv = itv;
                    break;
                }
            }

            if (curItv) {
                qDebug() << "[事故区域] 当前时段"
                         << "id=" << z->id()
                         << "name=" << z->name()
                         << "start=" << curItv->startTime()
                         << "end=" << curItv->endTime()
                         << "location(m)=" << curItv->location()
                         << "length(m)=" << curItv->length()
                         << "limitSpeed(km/h)=" << curItv->limitedSpeed()
                         << "controlLength(m)=" << curItv->controlLength()
                         << "laneNumbers=" << curItv->laneNumbers();
            } else {
                qDebug() << "[事故区域] 区域" << z->id() << z->name()
                         << "location(m)=" << z->location()
                         << "length(m)=" << z->zoneLength()
                         << "limitSpeed(km/h)=" << z->limitedSpeed()
                         << "controlLength(m)=" << z->controlLength();
            }
        }
    }
}

MySimulator::~MySimulator() {
}