#include "MyNet.h"
#include <QTextStream>
#include <QList>
#include <QMap>
#include <QPointF>

#include "tessinterface.h"
#include "netinterface.h"
#include "UnitChange.h"
#include "ilink.h"
#include "ILane.h"
#include "IConnector.h"
#include "IDispatchPoint.h"
#include "IRouting.h"
#include "IBusLine.h"
#include "IBusStation.h"
#include "IGuidArrow.h"
#include "IPedestrianRectRegion.h"
#include "IPedestrianSideWalkRegion.h"
#include "IPedestrianCrossWalkRegion.h"
#include "IPedestrianPathPoint.h"
#include "IPedestrianPath.h"
#include "IPedestrianStairRegion.h"
#include "ICrosswalkSignalLamp.h"
#include "ISignalPlan.h"
#include "ISignalPhase.h"
#include "ITrafficLight.h"

MyNet::MyNet(){
}

void MyNet::afterLoadNet() {
    auto ni = gpTessInterface->netInterface();
    QTextStream out(stdout);

    out << "step1: set simulation scale to 1\n";

    out << "step2: create approaches and outgoing links\n";
    out << "    create link west approach, 3 lanes, metric...\n";
    ILink* w_approach = ni->createLink(QList<QPointF>() << QPointF(m2p(-300), m2p(6)) << QPointF(m2p(-25), m2p(6)), 3, "西进口");
    if (w_approach) {
        QList<ILane*> lanes = w_approach->lanes();
        QList<long> laneIds; for (auto* l : lanes) laneIds << l->id();
        QStringList idStrs; for (long id : laneIds) idStrs << QString::number(id);
        out << "    west approach lane IDs: " << idStrs.join(", ") << "\n";
        out << "    set limit speed 30 km/h\n";
        w_approach->setLimitSpeed(30, UnitOfMeasure::Metric);
    }
    out << "    create link west outgoing, 3 lanes, metric...\n";
    ILink* w_outgoing = ni->createLink(QList<QPointF>() << QPointF(m2p(-25), m2p(-6)) << QPointF(m2p(-300), m2p(-6)), 3, "西出口");
    if (w_outgoing) {
        out << "    设置限速30km/h\n";
        w_outgoing->setLimitSpeed(50, UnitOfMeasure::Metric);
    }

    out << "    create link east approach, 3 lanes, metric...\n";
    ILink* e_approach = ni->createLink(QList<QPointF>() << QPointF(m2p(300), m2p(-6)) << QPointF(m2p(25), m2p(-6)), 3, "东进口");
    if (e_approach) e_approach->setLimitSpeed(40, UnitOfMeasure::Metric);
    out << "    create link east outgoing, 3 lanes, metric...\n";
    ILink* e_outgoing = ni->createLink(QList<QPointF>() << QPointF(m2p(25), m2p(6)) << QPointF(m2p(300), m2p(6)), 3, "东出口");
    if (e_outgoing) e_outgoing->setLimitSpeed(50, UnitOfMeasure::Metric);

    out << "    create link south approach, 3 lanes, metric...\n";
    ILink* s_approach = ni->createLink(QList<QPointF>() << QPointF(m2p(6), m2p(300)) << QPointF(m2p(6), m2p(25)), 3, "南进口");
    if (s_approach) s_approach->setLimitSpeed(40, UnitOfMeasure::Metric);
    out << "    create link south outgoing, 3 lanes, metric...\n";
    ILink* s_outgoing = ni->createLink(QList<QPointF>() << QPointF(m2p(-6), m2p(25)) << QPointF(m2p(-6), m2p(300)), 3, "南出口");
    if (s_outgoing) s_outgoing->setLimitSpeed(50, UnitOfMeasure::Metric);

    out << "    create link north approach, 3 lanes, metric...\n";
    ILink* n_approach = ni->createLink(QList<QPointF>() << QPointF(m2p(-6), m2p(-300)) << QPointF(m2p(-6), m2p(-25)), 3, "北进口");
    if (n_approach) {
        n_approach->setLimitSpeed(40, UnitOfMeasure::Metric);
        IDispatchPoint* dpn = ni->createDispatchPoint(n_approach);
        if (dpn) dpn->addDispatchInterval(1, 300, 100);
    }
    out << "    create link north outgoing, 3 lanes, metric...\n";
    ILink* n_outgoing = ni->createLink(QList<QPointF>() << QPointF(m2p(6), m2p(-25)) << QPointF(m2p(6), m2p(-300)), 3, "北出口");
    if (n_outgoing) n_outgoing->setLimitSpeed(50, UnitOfMeasure::Metric);

    out << "step3: create connectors\n";
    out << "    create connectors (metric)...\n";
    IConnector* w_e_straight_connector = ni->createConnector(w_approach ? w_approach->id() : -1, e_outgoing ? e_outgoing->id() : -1, QList<int>() << 2, QList<int>() << 2, "东西直行");
    IConnector* w_s_right_connector = ni->createConnector(w_approach ? w_approach->id() : -1, s_outgoing ? s_outgoing->id() : -1, QList<int>() << 1, QList<int>() << 1, "西右转");
    IConnector* w_n_left_connector = ni->createConnector(w_approach ? w_approach->id() : -1, n_outgoing ? n_outgoing->id() : -1, QList<int>() << 3, QList<int>() << 3, "西左转");
    IConnector* e_w_straight_connector = ni->createConnector(e_approach ? e_approach->id() : -1, w_outgoing ? w_outgoing->id() : -1, QList<int>() << 2, QList<int>() << 2, "东西直行");
    IConnector* e_n_right_connector = ni->createConnector(e_approach ? e_approach->id() : -1, n_outgoing ? n_outgoing->id() : -1, QList<int>() << 1, QList<int>() << 1, "东右转");
    IConnector* e_s_left_connector = ni->createConnector(e_approach ? e_approach->id() : -1, s_outgoing ? s_outgoing->id() : -1, QList<int>() << 3, QList<int>() << 3, "东左转");
    IConnector* s_n_straight_connector = ni->createConnector(s_approach ? s_approach->id() : -1, n_outgoing ? n_outgoing->id() : -1, QList<int>() << 2, QList<int>() << 2, "南北直行");
    IConnector* s_e_right_connector = ni->createConnector(s_approach ? s_approach->id() : -1, e_outgoing ? e_outgoing->id() : -1, QList<int>() << 1, QList<int>() << 1, "南右转");
    IConnector* s_w_left_connector = ni->createConnector(s_approach ? s_approach->id() : -1, w_outgoing ? w_outgoing->id() : -1, QList<int>() << 3, QList<int>() << 3, "南左转");
    IConnector* n_w_right_connector = ni->createConnector(n_approach ? n_approach->id() : -1, w_outgoing ? w_outgoing->id() : -1, QList<int>() << 1, QList<int>() << 1, "北右转");
    IConnector* n_e_left_connector = ni->createConnector(n_approach ? n_approach->id() : -1, e_outgoing ? e_outgoing->id() : -1, QList<int>() << 3, QList<int>() << 3, "北左转");
    IConnector* n_s_straight_connector = ni->createConnector(n_approach ? n_approach->id() : -1, s_outgoing ? s_outgoing->id() : -1, QList<int>() << 2, QList<int>() << 2, "南北直行");

    out << "step4: create dispatch points\n";
    IDispatchPoint* w_dispatchPoint = ni->createDispatchPoint(w_approach, "西进口发车");
    QList<Online::VehiComposition> vehiTypeProportion;
    vehiTypeProportion << Online::VehiComposition(1, 0.3) << Online::VehiComposition(2, 0.2) << Online::VehiComposition(3, 0.1) << Online::VehiComposition(4, 0.4);
    long vehiCompositionID = ni->createVehicleComposition("动态创建车型组成", vehiTypeProportion);
    if (w_dispatchPoint) {
        w_dispatchPoint->addDispatchInterval(1, 200, 28);
        w_dispatchPoint->addDispatchInterval(vehiCompositionID, 500, 100);
        w_dispatchPoint->setDynaModified(true);
    }
    IDispatchPoint* dp_e = ni->createDispatchPoint(e_approach);
    if (dp_e) dp_e->addDispatchInterval(1, 300, 100);
    IDispatchPoint* dp_s = ni->createDispatchPoint(s_approach);
    if (dp_s) dp_s->addDispatchInterval(1, 300, 100);

    out << "step5: create decision points and routes\n";
    out << "    create static decision points\n";
    auto w_dec = ni->createDecisionPoint(w_approach, 50, "w_approach_decisionPoint", UnitOfMeasure::Metric);
    auto e_dec = ni->createDecisionPoint(e_approach, 50, "e_approach_decisionPoint", UnitOfMeasure::Metric);
    auto s_dec = ni->createDecisionPoint(s_approach, 50, "s_approach_decisionPoint", UnitOfMeasure::Metric);
    auto n_dec = ni->createDecisionPoint(n_approach, 50, "n_approach_decisionPoint", UnitOfMeasure::Metric);
    out << "    create decision routes\n";
    auto w_left = ni->createDeciRouting(w_dec, QList<ILink*>() << w_approach << n_outgoing);
    auto w_straight = ni->createDeciRouting(w_dec, QList<ILink*>() << w_approach << e_outgoing);
    auto w_right = ni->createDeciRouting(w_dec, QList<ILink*>() << w_approach << s_outgoing);
    auto e_left = ni->createDeciRouting(e_dec, QList<ILink*>() << e_approach << s_outgoing);
    auto e_straight = ni->createDeciRouting(e_dec, QList<ILink*>() << e_approach << w_outgoing);
    auto e_right = ni->createDeciRouting(e_dec, QList<ILink*>() << e_approach << n_outgoing);
    auto s_straight = ni->createDeciRouting(s_dec, QList<ILink*>() << s_approach << n_outgoing);
    auto s_right = ni->createDeciRouting(s_dec, QList<ILink*>() << s_approach << e_outgoing);
    auto routing_sw = ni->shortestRouting(s_approach, w_outgoing);
    auto s_left = ni->createDeciRouting(s_dec, routing_sw->getLinks());
    auto n_straight = ni->createDeciRouting(n_dec, QList<ILink*>() << n_approach << s_outgoing);
    auto routing_ne = ni->createRouting(QList<ILink*>() << n_approach << e_outgoing);
    auto n_left = ni->createDeciRouting(n_dec, routing_ne->getLinks());
    auto n_right = ni->createDeciRouting(n_dec, QList<ILink*>() << n_approach << w_outgoing);
    ni->removeDeciRouting(w_dec, w_right);
    auto routing_ws = ni->createRouting(QList<ILink*>() << w_approach << s_outgoing);
    auto w_right1 = ni->createDeciRouting(w_dec, QList<ILink*>() << w_approach << s_outgoing);

    out << "step6: create pedestrian system\n";
    out << "    create pedestrian composition\n";
    QMap<int, qreal> compostion; compostion.insert(1, 0.8); compostion.insert(2, 0.2);
    ni->createPedestrianComposition("自定义1", compostion);
    out << "    create pedestrian layers\n";
    auto pedLayer = ni->addLayerInfo("行人图层", 0.0, true, false);
    auto pedLayer1 = ni->addLayerInfo("行人图层1", 10.0, true, false);
    ni->removeLayerInfo(pedLayer1.id);
    ni->updateLayerInfo(pedLayer.id, "基础行人图层", 0.0, true, false);
    out << "    create sidewalks\n";
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(-300, 14) << QPointF(-25, 14));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(-300, -14) << QPointF(-25, -14));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(300, -14) << QPointF(25, -14));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(300, 14) << QPointF(25, 14));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(-14, -300) << QPointF(-14, -25));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(14, -25) << QPointF(14, -300));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(14, 300) << QPointF(14, 25));
    ni->createPedestrianSideWalkRegion(QList<QPointF>() << QPointF(-14, 300) << QPointF(-14, 25));
    out << "    create four waiting areas at intersection\n";
    ni->createPedestrianFanShapeRegion(QPointF(-26, -26), QPointF(-15, -15));
    ni->createPedestrianFanShapeRegion(QPointF(-26, 26), QPointF(-15, 15));
    ni->createPedestrianFanShapeRegion(QPointF(26, -26), QPointF(15, -15));
    ni->createPedestrianFanShapeRegion(QPointF(26, 26), QPointF(15, 15));
    out << "    create crosswalks\n";
    auto n_crosswalk = ni->createPedestrianCrossWalkRegion(QPointF(14, -22), QPointF(-14, -22));
    auto s_crosswalk = ni->createPedestrianCrossWalkRegion(QPointF(14, 22), QPointF(-14, 22));
    auto w_crosswalk = ni->createPedestrianCrossWalkRegion(QPointF(-22, -14), QPointF(-22, 14));
    auto e_crosswalk = ni->createPedestrianCrossWalkRegion(QPointF(22, -14), QPointF(22, 14));
    out << "    create pedestrian start/end points and paths\n";
    auto w_path_start1 = ni->createPedestrianPathStartPoint(QPointF(-250, 14));
    ni->removePedestrianPathStartPoint(w_path_start1);
    auto w_path_start = ni->createPedestrianPathStartPoint(QPointF(-280, 14));
    auto configInfo = ni->findPedestrianStartPointConfigInfo(w_path_start->getId());
    Online::Pedestrian::GenPedestrianInfo genInfo; genInfo.pedestrianCount = 1000; genInfo.timeInterval = 2000;
    Online::Pedestrian::PedestrianTrafficDistributionInfo disInfo; disInfo.timeInterval = 2000;
    Online::Pedestrian::PedestrianPathStartPointConfigInfo cfg = configInfo;
    cfg.genPedestrianConfigInfo = QList<Online::Pedestrian::GenPedestrianInfo>() << genInfo;
    cfg.pedestrianTrafficDistributionConfigInfo = QList<Online::Pedestrian::PedestrianTrafficDistributionInfo>() << disInfo;
    ni->updatePedestrianStartPointConfigInfo(cfg);
    auto w_path_end = ni->createPedestrianPathEndPoint(QPointF(-280, -14));
    auto w_path_end1 = ni->createPedestrianPathEndPoint(QPointF(10, 14));
    ni->removePedestrianPathEndPoint(w_path_end1);
    auto e_path_start = ni->createPedestrianPathStartPoint(QPointF(280, 14));
    auto e_path_end = ni->createPedestrianPathEndPoint(QPointF(280, -14));
    auto n_path_start = ni->createPedestrianPathStartPoint(QPointF(-14, -280));
    auto n_path_end = ni->createPedestrianPathEndPoint(QPointF(14, -280));
    auto s_path_start = ni->createPedestrianPathStartPoint(QPointF(14, 280));
    auto s_path_end = ni->createPedestrianPathEndPoint(QPointF(-14, 280));
    auto p1 = ni->createPedestrianPath(w_path_start, w_path_end, QList<QPointF>());
    ni->removePedestrianPath(p1);
    auto pww = ni->createPedestrianPath(w_path_start, w_path_end, QList<QPointF>() << QPointF(-22, -1));
    ni->createPedestrianPath(w_path_start, e_path_end, QList<QPointF>());
    ni->createPedestrianPath(w_path_start, s_path_end, QList<QPointF>());
    ni->createPedestrianPath(w_path_start, n_path_end, QList<QPointF>());
    ni->createPedestrianPath(e_path_start, w_path_end, QList<QPointF>());
    ni->createPedestrianPath(e_path_start, e_path_end, QList<QPointF>());
    ni->createPedestrianPath(e_path_start, s_path_end, QList<QPointF>());
    ni->createPedestrianPath(e_path_start, n_path_end, QList<QPointF>());
    ni->createPedestrianPath(s_path_start, w_path_end, QList<QPointF>());
    ni->createPedestrianPath(s_path_start, e_path_end, QList<QPointF>());
    ni->createPedestrianPath(s_path_start, s_path_end, QList<QPointF>());
    ni->createPedestrianPath(s_path_start, n_path_end, QList<QPointF>());
    ni->createPedestrianPath(n_path_start, w_path_end, QList<QPointF>());
    ni->createPedestrianPath(n_path_start, e_path_end, QList<QPointF>());
    ni->createPedestrianPath(n_path_start, s_path_end, QList<QPointF>());
    ni->createPedestrianPath(n_path_start, n_path_end, QList<QPointF>());

    out << "    create pedestrian upstairs/downstairs\n";
    auto pedLayer2 = ni->addLayerInfo("行人图层2", 10.0, true, false);
    auto yilou = ni->createPedestrianRectRegion(QPointF(30, 30), QPointF(40, 100));
    yilou->setLayerId(pedLayer.id);
    auto erlou = ni->createPedestrianRectRegion(QPointF(50, 30), QPointF(60, 100));
    erlou->setLayerId(pedLayer2.id);
    auto stair1 = ni->createPedestrianStairRegion(QPointF(38, 35), QPointF(52, 35));
    stair1->setStartLayerId(pedLayer.id); stair1->setEndLayerId(pedLayer2.id);
    auto testStair = ni->createPedestrianStairRegion(QPointF(40, 32), QPointF(60, 38));
    ni->removePedestrianStairRegion(testStair);
    auto stair2 = ni->createPedestrianStairRegion(QPointF(38, 65), QPointF(52, 65));
    stair2->setStartLayerId(pedLayer.id); stair2->setEndLayerId(pedLayer2.id);
    auto sps = ni->createPedestrianPathStartPoint(QPointF(32, 55));
    auto spe = ni->createPedestrianPathEndPoint(QPointF(55, 55));
    auto pp = ni->createPedestrianPath(sps, spe, QList<QPointF>() << QPointF(40, 35));
    auto sdp1 = ni->createPedestrianDecisionPoint(QPointF(36, 50));
    auto se1 = ni->createPedestrianPathEndPoint(QPointF(55, 35));
    auto p1_1 = ni->createPedestrianPath(sdp1, se1, QList<QPointF>() << QPointF(45, 35));
    auto se2 = ni->createPedestrianPathEndPoint(QPointF(55, 65));
    auto p1_2 = ni->createPedestrianPath(sdp1, se2, QList<QPointF>() << QPointF(45, 65));
    Online::Pedestrian::PedestrianDecisionPointConfigInfo dcfg; dcfg.id = sdp1->getId();
    Online::Pedestrian::PedestrianTrafficDistributionInfo distribute; distribute.timeInterval = 1000; distribute.trafficRatio.insert(p1_1->getId(), 2); distribute.trafficRatio.insert(p1_2->getId(), 1);
    QMap<int, QList<Online::Pedestrian::PedestrianTrafficDistributionInfo>> mp; mp.insert(pp->getId(), QList<Online::Pedestrian::PedestrianTrafficDistributionInfo>() << distribute);
    dcfg.pedestrianTrafficDistributionConfigInfo = mp;
    ni->updatePedestrianDecisionPointConfigInfo(dcfg);

    out << "step7: create bus and boarding/alighting system\n";
    out << "    create bus line\n";
    IBusLine* busline = ni->createBusLine(QList<ILink*>() << w_approach << e_outgoing);
    IBusLine* busline1 = ni->createBusLine(QList<ILink*>() << w_approach << s_outgoing);
    ni->removeBusLine(busline1);
    if (busline) busline->setDesirSpeed(60, UnitOfMeasure::Metric);
    out << "    create bus stations\n";
    auto busstation1 = ni->createBusStation(w_approach->lanes().first(), 30, 100, "西进口公交站点1", UnitOfMeasure::Metric);
    auto busstation2 = ni->createBusStation(w_approach->lanes().first(), 30, 200, "西进口公交站点2", UnitOfMeasure::Metric);
    auto busstation3 = ni->createBusStation(e_outgoing->lanes().first(), 30, 200, "东出口公交站点1", UnitOfMeasure::Metric);
    ni->addBusStationToLine(busline, busstation1);
    ni->addBusStationToLine(busline, busstation2);
    ni->addBusStationToLine(busline, busstation3);
    ni->removeBusStationFromLine(busline, busstation2);
    ni->removeBusStation(busstation2);
    out << "    create pedestrian boarding/alighting areas\n";
    auto up_ped_area = ni->createPedestrianRectRegion(QPointF(-200, 10), QPointF(-170, 12));
    up_ped_area->setIsBoardingArea(true);
    auto up_down_ped_area = ni->createPedestrianRectRegion(QPointF(260, 10), QPointF(200, 30));
    up_down_ped_area->setIsBoardingArea(true);
    up_down_ped_area->setIsAlightingArea(true);
    ni->createPedestrianRectRegion(QPointF(170, 28), QPointF(300, 40));
    auto s_dp1 = ni->createPedestrianDecisionPoint(QPointF(14, 280));
    ni->removePedestrianDecisionPoint(s_dp1);
    auto s_dp = ni->createPedestrianDecisionPoint(QPointF(-250, 15));
    auto down_dp = ni->createPedestrianDecisionPoint(QPointF(240, 15));
    auto s_pe = ni->createPedestrianPathEndPoint(QPointF(-180, 11));
    auto s_pe1 = ni->createPedestrianPathEndPoint(QPointF(240, 35));
    ni->createPedestrianPath(s_dp, s_pe, QList<QPointF>());
    ni->createPedestrianPath(down_dp, s_pe1, QList<QPointF>());

    out << "step8: create pedestrian/vehicle signals and plan\n";
    out << "    create signal controller\n";
    auto trafficController = ni->createSignalController("交叉口1");
    out << "    create signal plan\n";
    auto signalPlan = ni->createSignalPlan(trafficController, "早高峰", 150, 0, 0, 1800);
    out << "    create phases\n";
    QList<Online::ColorInterval> w_e_straight_phasecolor; w_e_straight_phasecolor << Online::ColorInterval("绿",50) << Online::ColorInterval("黄",3) << Online::ColorInterval("红",97);
    auto w_e_straight_phase = ni->createSignalPlanSignalPhase(signalPlan, "东西直行", w_e_straight_phasecolor);
    auto we_ped_phase = ni->createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor);
    QList<Online::ColorInterval> w_e_left_phasecolor; w_e_left_phasecolor << Online::ColorInterval("红",53) << Online::ColorInterval("绿",30) << Online::ColorInterval("黄",3) << Online::ColorInterval("红",64);
    auto w_e_left_phase = ni->createSignalPlanSignalPhase(signalPlan, "东西左转", w_e_left_phasecolor);
    QList<Online::ColorInterval> s_n_straight_phasecolor; s_n_straight_phasecolor << Online::ColorInterval("红",86) << Online::ColorInterval("绿",30) << Online::ColorInterval("黄",3) << Online::ColorInterval("红",31);
    auto s_n_straight_phase = ni->createSignalPlanSignalPhase(signalPlan, "南北直行", s_n_straight_phasecolor);
    auto ns_ped_phase = ni->createSignalPlanSignalPhase(signalPlan, "南北直行行人", s_n_straight_phasecolor);
    QList<Online::ColorInterval> s_n_left_phasecolor; s_n_left_phasecolor << Online::ColorInterval("红",119) << Online::ColorInterval("绿",29) << Online::ColorInterval("黄",3);
    auto s_n_left_phase = ni->createSignalPlanSignalPhase(signalPlan, "南北左转", s_n_left_phasecolor);

    out << "    create vehicle signal lamps and bind phases\n";
    for (auto* lane : w_approach->lanes()) {
        if (lane->number() < w_approach->laneCount() - 1 && lane->number() > 0) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "东西直行信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(w_e_straight_phase->id(), lamp);
        }
    }
    for (auto* lane : e_approach->lanes()) {
        if (lane->number() < e_approach->laneCount() - 1 && lane->number() > 0) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "东西直行信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(w_e_straight_phase->id(), lamp);
        }
    }
    for (auto* lane : w_approach->lanes()) {
        if (lane->number() == w_approach->laneCount() - 1) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "东西左转信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(w_e_left_phase->id(), lamp);
        }
    }
    for (auto* lane : e_approach->lanes()) {
        if (lane->number() == e_approach->laneCount() - 1) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "东西左转信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(w_e_left_phase->id(), lamp);
        }
    }
    for (auto* lane : n_approach->lanes()) {
        if (lane->number() < n_approach->laneCount() - 1 && lane->number() > 0) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "南北直行信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(s_n_straight_phase->id(), lamp);
        }
    }
    for (auto* lane : s_approach->lanes()) {
        if (lane->number() < s_approach->laneCount() - 1 && lane->number() > 0) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "南北直行信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(s_n_straight_phase->id(), lamp);
        }
    }
    for (auto* lane : n_approach->lanes()) {
        if (lane->number() == n_approach->laneCount() - 1) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "南北左转信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(s_n_left_phase->id(), lamp);
        }
    }
    for (auto* lane : s_approach->lanes()) {
        if (lane->number() == s_approach->laneCount() - 1) {
            auto lamp = ni->createTrafficSignalLamp(trafficController, "南北左转信号灯", lane->id(), -1, lane->length(UnitOfMeasure::Pixel) - 0.5);
            ni->addSignalPhaseToLamp(s_n_left_phase->id(), lamp);
        }
    }

    out << "    create pedestrian signal lamps and bind phases\n";
    auto signalLamp1_positive = ni->createCrossWalkSignalLamp(trafficController, "南斑马线信号灯", s_crosswalk->getId(), QPointF(13, 22), true);
    auto signalLamp1_negetive = ni->createCrossWalkSignalLamp(trafficController, "南斑马线信号灯", s_crosswalk->getId(), QPointF(-13, 22), false);
    ni->addCrossWalkSignalPhaseToLamp(we_ped_phase->id(), signalLamp1_positive);
    ni->addCrossWalkSignalPhaseToLamp(we_ped_phase->id(), signalLamp1_negetive);
    auto signalLamp2_positive = ni->createCrossWalkSignalLamp(trafficController, "北斑马线信号灯", n_crosswalk->getId(), QPointF(13, -22), true);
    auto signalLamp2_negetive = ni->createCrossWalkSignalLamp(trafficController, "北斑马线信号灯", n_crosswalk->getId(), QPointF(-13, -22), false);
    ni->addCrossWalkSignalPhaseToLamp(we_ped_phase->id(), signalLamp2_positive);
    ni->addCrossWalkSignalPhaseToLamp(we_ped_phase->id(), signalLamp2_negetive);
    auto signalLamp3_positive = ni->createCrossWalkSignalLamp(trafficController, "东斑马线信号灯", e_crosswalk->getId(), QPointF(22, -13), true);
    auto signalLamp3_negetive = ni->createCrossWalkSignalLamp(trafficController, "东斑马线信号灯", e_crosswalk->getId(), QPointF(22, 13), false);
    ni->addCrossWalkSignalPhaseToLamp(ns_ped_phase->id(), signalLamp3_positive);
    ni->addCrossWalkSignalPhaseToLamp(ns_ped_phase->id(), signalLamp3_negetive);
    auto signalLamp4_positive = ni->createCrossWalkSignalLamp(trafficController, "西斑马线信号灯", w_crosswalk->getId(), QPointF(-22, -13), true);
    auto signalLamp4_negetive = ni->createCrossWalkSignalLamp(trafficController, "西斑马线信号灯", w_crosswalk->getId(), QPointF(-22, 13), false);
    ni->addCrossWalkSignalPhaseToLamp(ns_ped_phase->id(), signalLamp4_positive);
    ni->addCrossWalkSignalPhaseToLamp(ns_ped_phase->id(), signalLamp4_negetive);

    out << "step9: create guide arrows\n";
    ni->createGuidArrow(w_approach->lanes().first(), 4, 10, Online::GuideArrowType::StraightRight);
}

MyNet::~MyNet(){
}