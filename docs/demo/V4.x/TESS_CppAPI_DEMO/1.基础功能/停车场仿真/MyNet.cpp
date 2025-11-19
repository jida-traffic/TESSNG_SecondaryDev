#include "MyNet.h"

#include <QDebug>

#include "tessinterface.h"
#include "netinterface.h"
#include "ilink.h"
#include "UnitChange.h"
#include "IParkingRegion.h"
#include "IParkingDecisionPoint.h"
#include "IParkingRouting.h"

MyNet::MyNet(){
}

// 加载完路网后的行为
void MyNet::afterLoadNet() {
    // 路网为空时不执行
    if (gpTessInterface->netInterface()->linkCount() == 0) {
        return;
    }
    createParkingRegionsAndRouting();
}

// 创建停车区与停车决策点及路径
void MyNet::createParkingRegionsAndRouting() {
    NetInterface* net = gpTessInterface->netInterface();

    // step1：创建停车区（路段1~4，左右两侧）
    QList<long> parkingRegionLinks = {1, 2, 3, 4};
    QHash<int, QString> posName; posName.insert(1, QString("右侧")); posName.insert(2, QString("左侧")); posName.insert(0, QString("车道内"));
    int parkingStallLengthM = 6;   // 米
    int parkingStallWidthM  = 3;   // 米
    int parkingStallCount   = 25;
    QList<int> positions = {1, 2}; // 右侧/左侧
    QHash<int,int> pos2Lane; pos2Lane.insert(1, 0); pos2Lane.insert(2, 1);

    QList<IParkingRegion*> createdRegions;
    for (long roadId : parkingRegionLinks) {
        for (int p : positions) {
            Online::ParkingLot::DynaParkingRegion pr;
            pr.name = QString("路段%1%2停车场").arg(roadId).arg(posName.value(p));
            pr.location = 0.5; // 起点位置（米）
            pr.length = parkingStallCount * parkingStallWidthM; // 总长度（米）
            pr.roadId = roadId;
            pr.laneNumber = pos2Lane.value(p, 0);
            pr.findParkingStallStrategy = 1;
            pr.parkingStallPos = p; // 0-车道内;1-右侧;2-左侧
            pr.arrangeType = 0;     // 0-垂直式

            // 车位吸引力
            pr.firstParkingStallAttract  = 1;
            pr.middleParkingStallAttract = 1;
            pr.lastParkingStallAttract   = 1;

            // 停车运动参数
            pr.menaValue    = 0;
            pr.variance     = 1.0;
            pr.parkingSpeed = 5.0;
            pr.joinGap      = 5.0;
            pr.parkingType  = 0; // 0-前进->前进

            // 运营参数
            pr.attract   = 0;
            pr.startTime = 0;
            pr.endTime   = 999999;
            // 生成停车位
            QList<Online::ParkingLot::DynaParkingStall> stalls;
            for (int k = 0; k < parkingStallCount; ++k) {
                Online::ParkingLot::DynaParkingStall sd;
                sd.length = parkingStallLengthM; // 米
                sd.width  = parkingStallWidthM;  // 米
                sd.location = pr.location + parkingStallWidthM * k; // 米
                sd.parkingStallType = 1; // 小客车
                stalls << sd;
            }
            pr.parkingStalls = stalls;

            IParkingRegion* r = net->createParkingRegion(pr);
            if (r) {
                createdRegions << r;
                qDebug() << "创建停车区" << r->id();
            }
        }
    }

    // step2：创建停车决策点与到各停车区的路径（位于路段6）
    ILink* link6 = net->findLink(6);
    if (!link6) {
        qDebug() << "未找到路段6，跳过停车决策点与路径创建";
        return;
    }
    qreal distance = link6->length() / 3.0;
    QString dpName = QString("路段%1上的停车决策点").arg(link6->id());
    IParkingDecisionPoint* dp = net->createParkingDecisionPoint(link6, distance, dpName);
    if (!dp) {
        qDebug() << "创建停车决策点失败";
        return;
    }
    for (IParkingRegion* r : createdRegions) {
        IParkingRouting* prt = net->createParkingRouting(dp, r);
        if (prt) {
            qDebug() << "创建到停车区" << r->id() << "的路径" << prt->id();
        }
    }

    // step3：平均分配停车区
    int regionCount = net->parkingRegions().size();
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
                nv.prop = regionCount > 0 ? 1.0 / regionCount : 0.0;
                nv.parkingRegionID  = vd.parkingRegionID;
                nv.parkingRoutingID = vd.parkingRoutingID;
                nv.parkingSelection = vd.parkingSelection;
                nv.parkingTimeDisId = vd.parkingTimeDisId;
                nv.vehicleType      = vd.vehicleType;
                vdList << nv;
            }
            ri.vehicleDisDetailList = vdList;
            infoList << ri;
        }
        upd.disVehiclsInfoList = infoList;
        updateList << upd;
    }
    bool ok = dp->updateParkDisInfo(updateList);
    qDebug() << "为每个停车区平均分配流量, result:" << ok;
}

MyNet::~MyNet(){
}