#include "MySimulator.h"

#include <QDebug>

#include "tessinterface.h"
#include "netinterface.h"
#include "simuinterface.h"
#include "IParkingRegion.h"
#include "IParkingDecisionPoint.h"

MySimulator::MySimulator() : mLastSimuTime(0) {
}

// 一个批次计算后的处理
void MySimulator::afterOneStep() {
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();

    // 仿真180秒后，关闭路段4两侧的停车区
    if (mLastSimuTime < 180 * 1000 && simuTime >= 180 * 1000) {
        NetInterface* net = gpTessInterface->netInterface();
        QList<IParkingDecisionPoint*> lDp = net->parkingDecisionPoints();
        for (IParkingDecisionPoint* dp : lDp) {
            QList<Online::ParkingLot::DynaParkDisInfo> updateList;
            for (const auto& info : dp->parkDisInfoList()) {
                Online::ParkingLot::DynaParkDisInfo upd;
                upd.pIRouting = info.pIRouting;
                QList<Online::ParkingLot::DynaRoutingDisVehicleInfo> infoList;
                for (const auto& di : info.disVehiclsInfoList) {
                    Online::ParkingLot::DynaRoutingDisVehicleInfo ri;
                    ri.startTime = di.startTime;
                    ri.endTime   = di.endTime;
                    QList<Online::ParkingLot::DynaParkRegionVehicleDisDetail> vdList;
                    for (const auto& vd : di.vehicleDisDetailList) {
                        Online::ParkingLot::DynaParkRegionVehicleDisDetail nv;
                        nv.parkingRegionID  = vd.parkingRegionID;
                        nv.parkingRoutingID = vd.parkingRoutingID;
                        nv.parkingSelection = vd.parkingSelection;
                        nv.parkingTimeDisId = vd.parkingTimeDisId;
                        nv.vehicleType      = vd.vehicleType;
                        long linkId = net->findParkingRegion(vd.parkingRegionID)->dynaParkingRegion().roadId;
                        nv.prop = (linkId == 4) ? 0.0 : vd.prop;
                        vdList << nv;
                    }
                    ri.vehicleDisDetailList = vdList;
                    infoList << ri;
                }
                upd.disVehiclsInfoList = infoList;
                updateList << upd;
            }
            bool ok = dp->updateParkDisInfo(updateList);
            qDebug() << "仿真" << (simuTime / 1000) << "秒，关闭路段4两侧的停车区; result:" << ok;
        }
    }

    mLastSimuTime = simuTime;
}

MySimulator::~MySimulator() {
}