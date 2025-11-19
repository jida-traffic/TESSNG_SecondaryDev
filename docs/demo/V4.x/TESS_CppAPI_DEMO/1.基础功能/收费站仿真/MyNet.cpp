#include "MyNet.h"

#include <QDebug>

#include "tessinterface.h"
#include "netinterface.h"
#include "ITollLane.h"
#include "ITollRouting.h"
#include "ITollDecisionPoint.h"
#include "ilink.h"
#include "IRouting.h"
#include "UnitChange.h"

MyNet::MyNet(){
}

//加载完路网后的行为
void MyNet::afterLoadNet() {
    // 路网为空时不执行
    if (gpTessInterface->netInterface()->linkCount() == 0) {
        return;
    }

    NetInterface* net = gpTessInterface->netInterface();

    // 创建收费停车时间分布
    Online::TollStation::DynaMtcTimeDetail mtc1; mtc1.time = 3; mtc1.prop = 1;
    Online::TollStation::DynaMtcTimeDetail mtc2; mtc2.time = 5; mtc2.prop = 1;
    Online::TollStation::DynaEtcSpeedDetail etc1; etc1.limitSpeed = 15; etc1.prop = 1;
    Online::TollStation::DynaEtcSpeedDetail etc2; etc2.limitSpeed = 20; etc2.prop = 1;

    Online::TollStation::DynaTollParkingTime tpt;
    tpt.vehicleTypeId = 1;
    tpt.timeDisList << mtc1 << mtc2;
    tpt.speedDisList << etc1 << etc2;

    Online::TollStation::DynaTollParkingTimeDis tptd;
    tptd.name = QString("新建停车分布");
    tptd.parkingTimeList << tpt;
    Online::TollStation::DynaTollParkingTimeDis created = net->createTollParkingTimeDis(tptd);
    qDebug() << "创建了停车分布，id=" << created.id;

    // 收费区域定义
    Online::TollStation::DynaTollPoint tollPoint;
    tollPoint.location = m2p(40);
    tollPoint.tollType = 1;
    tollPoint.timeDisId = created.id;
    tollPoint.enable = true;

    // 在link=1的12个车道上创建收费车道
    QList<ITollLane*> lTollLane;
    for (int laneNumber = 0; laneNumber < 12; ++laneNumber) {
        Online::TollStation::DynaTollLane laneParam;
        laneParam.roadId = 1;
        laneParam.location = m2p(30);
        laneParam.length = m2p(40);
        laneParam.laneNumber = laneNumber;
        laneParam.startTime = 0;
        laneParam.endTime = 3600;
        laneParam.tollPoint << tollPoint;
        laneParam.tollPointLen = 8;
        ITollLane* pLane = net->createTollLane(laneParam);
        if (pLane) {
            lTollLane << pLane;
            qDebug() << "在序号为" << laneNumber << "的车道上创建了收费车道，id=" << pLane->id();
        }
    }
    qDebug() << "收费车道创建完成，共" << lTollLane.size() << "条";

    // 创建收费路径决策点，位于link=2
    ILink* pLink2 = net->findLink(2);
    ITollDecisionPoint* pTollDeciPoint = nullptr;
    if (pLink2) {
        pTollDeciPoint = net->createTollDecisionPoint(pLink2, m2p(30));
        if (pTollDeciPoint) {
            qDebug() << "创建了收费路径决策点，id=" << pTollDeciPoint->id();
        }
    }
    if (!pTollDeciPoint) {
        qDebug() << "未创建收费路径决策点，终止后续收费路径创建";
        return;
    }

    // 在所有收费车道上创建收费路径
    QList<ITollRouting*> lTollRouting;
    for (ITollLane* pLane : lTollLane) {
        ITollRouting* pRouting = net->createTollRouting(pTollDeciPoint, pLane);
        if (pRouting) {
            lTollRouting << pRouting;
            qDebug() << "在收费路径决策点" << pTollDeciPoint->id() << "创建了收费路径，id=" << pRouting->id();
        }
    }

    // 更新收费决策点的车型对车道选择分布比重
    if (!lTollRouting.isEmpty() && !lTollLane.isEmpty()) {
        long tollRoutingId = lTollRouting.last()->id();
        long tollLaneId = lTollLane.last()->id();

        Online::TollStation::DynaEtcTollInfo etcInfo;
        etcInfo.tollRoutingID = tollRoutingId;
        etcInfo.tollLaneID = tollLaneId;
        etcInfo.etcRatio = 0.5;

        Online::TollStation::DynaVehicleTollDisDetail disDetail;
        disDetail.tollRoutingID = tollRoutingId;
        disDetail.tollLaneID = tollLaneId;
        disDetail.prop = 0.5;

        Online::TollStation::DynaVehicleTollDisInfo disInfo;
        disInfo.vehicleType = 1;
        disInfo.list << disDetail;

        Online::TollStation::DynaRoutingDisTollInfo routingDis;
        routingDis.ectTollInfoList << etcInfo;
        routingDis.vehicleDisInfoList << disInfo;

        Online::TollStation::DynaTollDisInfo tollDis;
        tollDis.pIRouting = nullptr;
        tollDis.disTollInfoList << routingDis;

        QList<Online::TollStation::DynaTollDisInfo> l;
        l << tollDis;
        pTollDeciPoint->updateTollDisInfoList(l);
        qDebug() << "更新了收费决策点的车型对车道选择的分布比重";
    }

    // 输出当前对象数量
    qDebug() << "收费停车分布数量:" << net->tollParkingTimeDis().size();
    qDebug() << "收费车道数量:" << net->tollLanes().size();
    qDebug() << "收费路径决策点数量:" << net->tollDecisionPoints().size();

    // 展示收费分布信息
    qDebug() << "收费分布信息列表";
    QList<Online::TollStation::DynaTollDisInfo> disList = pTollDeciPoint->tollDisInfoList();
    for (const auto& td : disList) {
        IRouting* route = td.pIRouting;
        qDebug() << "\t静态决策路径:" << (route ? route->id() : -1);
        for (const auto& rdi : td.disTollInfoList) {
            qDebug() << "\t\t开始时间:" << rdi.startTime << "s";
            qDebug() << "\t\t结束时间:" << rdi.endTime << "s";
            for (const auto& ei : rdi.ectTollInfoList) {
                qDebug() << "\t\t\t收费路径ID:" << ei.tollRoutingID;
                qDebug() << "\t\t\t\t收费车道ID:" << ei.tollLaneID;
                qDebug() << "\t\t\t\tETC占比:" << ei.etcRatio;
            }
            for (const auto& vi : rdi.vehicleDisInfoList) {
                qDebug() << "\t\t\t车型ID:" << vi.vehicleType;
                for (const auto& cd : vi.list) {
                    qDebug() << "\t\t\t\t收费路径ID:" << cd.tollRoutingID;
                    qDebug() << "\t\t\t\t收费车道ID:" << cd.tollLaneID;
                    qDebug() << "\t\t\t\t分布比重:" << cd.prop;
                }
            }
        }
    }
}

MyNet::~MyNet(){
}