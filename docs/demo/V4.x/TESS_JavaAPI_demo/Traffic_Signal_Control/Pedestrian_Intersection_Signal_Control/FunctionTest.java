//package Traffic_Signal_Control.Pedestrian_Intersection_Signal_Control;
//
//import com.jidatraffic.tessng.*;
//import com.jidatraffic.tessng.ColorInterval;
//import com.jidatraffic.tessng.VehiComposition;
//import com.jidatraffic.tessng.Pedestrian;
//import java.util.ArrayList;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//
//public class FunctionTest {
//    private int id;
//
//    public FunctionTest(int id) {
//        this.id = id;
//    }
//
//    // 创建路口、路段、连接段、发车点、决策点和路径
//    public void createJunction(NetInterface netiface) {
//        System.out.println("step1：设置仿真比例尺为1");
//        netiface.buildNetGrid(10, UnitOfMeasure.Metric);
//
//        System.out.println("step2：创建路口各个进口道和出口道路段");
//
//        // 创建西进口路段
//        Point startPoint = new Point(-300, 6);
//        Point endPoint = new Point(-25, 6);
//        ArrayList<Point> lPoint = new ArrayList<Point>();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        ILink wApproach = netiface.createLink(lPoint, 3, "西进口", true, UnitOfMeasure.Metric);
//        System.out.println("    西进口车道ID列表：" + getLaneIds(wApproach.lanes()));
//        System.out.println("    设置限速30km/h");
//        wApproach.setLimitSpeed(30, UnitOfMeasure.Metric);
//
//        // 创建西出口路段
//        startPoint = new Point(-25, -6);
//        endPoint = new Point(-300, -6);
//        lPoint.clear();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        ILink wOutgoing = netiface.createLink(lPoint, 3, "西出口", true, UnitOfMeasure.Metric);
//        wOutgoing.setLimitSpeed(50, UnitOfMeasure.Metric);
//
//        // 创建东进口3D路段
//        Vector3D startPoint3D = new Vector3D(300, -6, 0);
//        Vector3D endPoint3D = new Vector3D(25, -6, 0);
//        ArrayList<Vector3D> lPoint3D = new ArrayList<Vector3D>();
//        lPoint3D.add(startPoint3D);
//        lPoint3D.add(endPoint3D);
//        ILink eApproach = netiface.createLink3D(lPoint3D, 3, "东进口creatlink3d", true, UnitOfMeasure.Metric);
//        eApproach.setLimitSpeed(40, UnitOfMeasure.Metric);
//
//        // 创建东出口路段
//        startPoint = new Point(25, 6);
//        endPoint = new Point(300, 6);
//        lPoint.clear();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        ILink eOutgoing = netiface.createLink(lPoint, 3, "东出口", true, UnitOfMeasure.Metric);
//        eOutgoing.setLimitSpeed(50, UnitOfMeasure.Metric);
//
//        // 创建南进口3D路段（带车道宽度）
//        startPoint3D = new Vector3D(6, 300, 0);
//        endPoint3D = new Vector3D(6, 25, 0);
//        lPoint3D.clear();
//        lPoint3D.add(startPoint3D);
//        lPoint3D.add(endPoint3D);
//        ArrayList<Double> laneWidths = new ArrayList<Double>();
//        laneWidths.add(3.5);
//        laneWidths.add(3.0);
//        laneWidths.add(3.0);
//        ILink sApproach = netiface.createLink3DWithLaneWidth(lPoint3D, laneWidths,
//                "南进口creatlink3dWithLaneWidth", true, UnitOfMeasure.Metric);
//        sApproach.setLimitSpeed(40, UnitOfMeasure.Metric);
//
//        // 创建南出口路段
//        startPoint = new Point(-6, 25);
//        endPoint = new Point(-6, 300);
//        lPoint.clear();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        ILink sOutgoing = netiface.createLink(lPoint, 3, "南出口", true, UnitOfMeasure.Metric);
//        sOutgoing.setLimitSpeed(50, UnitOfMeasure.Metric);
//
//        // 创建北进口3D路段
//        startPoint3D = new Vector3D(-6, -300, 0);
//        endPoint3D = new Vector3D(-6, -25, 0);
//        lPoint3D.clear();
//        lPoint3D.add(startPoint3D);
//        lPoint3D.add(endPoint3D);
//        ILink nApproach = netiface.createLink3D(lPoint3D, 3, "北进口createLink3DWithLanePoints", true, UnitOfMeasure.Metric);
//        nApproach.setLimitSpeed(40, UnitOfMeasure.Metric);
//
//        // 在北进口创建发车点
//        IDispatchPoint dp = netiface.createDispatchPoint(nApproach);
//        if (dp != null) {
//            dp.addDispatchInterval(1, 300, 100);
//        }
//
//        // 创建北出口路段
//        startPoint = new Point(6, -25);
//        endPoint = new Point(6, -300);
//        lPoint.clear();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        ILink nOutgoing = netiface.createLink(lPoint, 3, "北出口", true, UnitOfMeasure.Metric);
//        nOutgoing.setLimitSpeed(50, UnitOfMeasure.Metric);
//
//        System.out.println("step3: 创建连接段");
//        ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
//        ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
//        lFromLaneNumber.add(2);
//        lToLaneNumber.add(2);
//
//        IConnector wEStraightConnector = netiface.createConnector(wApproach.id(), eOutgoing.id(),
//                lFromLaneNumber, lToLaneNumber, "东西直行", true);
//
//        // 西右转连接段
//        ArrayList<Integer> westRightFromLanes = new ArrayList<Integer>();
//        westRightFromLanes.add(1);
//        ArrayList<Integer> westRightToLanes = new ArrayList<Integer>();
//        westRightToLanes.add(1);
//        IConnector wSRightConnector = netiface.createConnector(wApproach.id(), sOutgoing.id(),
//                westRightFromLanes, westRightToLanes, "西右转", true);
//
//        // 西左转连接段
//        ArrayList<Integer> westLeftFromLanes = new ArrayList<Integer>();
//        westLeftFromLanes.add(3);
//        ArrayList<Integer> westLeftToLanes = new ArrayList<Integer>();
//        westLeftToLanes.add(3);
//        IConnector wNLeftConnector = netiface.createConnector(wApproach.id(), nOutgoing.id(),
//                westLeftFromLanes, westLeftToLanes, "西左转", true);
//
//        IConnector eWStraightConnector = netiface.createConnector(eApproach.id(), wOutgoing.id(),
//                lFromLaneNumber, lToLaneNumber, "东西直行", true);
//
//        // 东右转连接段
//        ArrayList<Integer> eastRightFromLanes = new ArrayList<Integer>();
//        eastRightFromLanes.add(1);
//        ArrayList<Integer> eastRightToLanes = new ArrayList<Integer>();
//        eastRightToLanes.add(1);
//        IConnector eNRightConnector = netiface.createConnector(eApproach.id(), nOutgoing.id(),
//                eastRightFromLanes, eastRightToLanes, "东右转", true);
//
//        // 东左转连接段
//        ArrayList<Integer> eastLeftFromLanes = new ArrayList<Integer>();
//        eastLeftFromLanes.add(3);
//        ArrayList<Integer> eastLeftToLanes = new ArrayList<Integer>();
//        eastLeftToLanes.add(3);
//        IConnector eSLeftConnector = netiface.createConnector(eApproach.id(), sOutgoing.id(),
//                eastLeftFromLanes, eastLeftToLanes, "东左转", true);
//
//        // 南北直行连接段（南到北）
//        ArrayList<Integer> southStraightFromLanes = new ArrayList<Integer>();
//        southStraightFromLanes.add(2);
//        ArrayList<Integer> southStraightToLanes = new ArrayList<Integer>();
//        southStraightToLanes.add(2);
//        IConnector sNStraightConnector = netiface.createConnector(sApproach.id(), nOutgoing.id(),
//                southStraightFromLanes, southStraightToLanes, "南北直行", true);
//
//        // 南右转连接段
//        ArrayList<Integer> southRightFromLanes = new ArrayList<Integer>();
//        southRightFromLanes.add(1);
//        ArrayList<Integer> southRightToLanes = new ArrayList<Integer>();
//        southRightToLanes.add(1);
//        IConnector sERightConnector = netiface.createConnector(sApproach.id(), eOutgoing.id(),
//                southRightFromLanes, southRightToLanes, "南右转", true);
//
//        // 南左转连接段
//        ArrayList<Integer> southLeftFromLanes = new ArrayList<Integer>();
//        southLeftFromLanes.add(3);
//        ArrayList<Integer> southLeftToLanes = new ArrayList<Integer>();
//        southLeftToLanes.add(3);
//        IConnector sWLeftConnector = netiface.createConnector(sApproach.id(), wOutgoing.id(),
//                southLeftFromLanes, southLeftToLanes, "南左转", true);
//
//        // 北右转连接段
//        ArrayList<Integer> northRightFromLanes = new ArrayList<Integer>();
//        northRightFromLanes.add(1);
//        ArrayList<Integer> northRightToLanes = new ArrayList<Integer>();
//        northRightToLanes.add(1);
//        IConnector nWRightConnector = netiface.createConnector(nApproach.id(), wOutgoing.id(),
//                northRightFromLanes, northRightToLanes, "北右转", true);
//
//        // 北左转连接段
//        ArrayList<Integer> northLeftFromLanes = new ArrayList<Integer>();
//        northLeftFromLanes.add(3);
//        ArrayList<Integer> northLeftToLanes = new ArrayList<Integer>();
//        northLeftToLanes.add(3);
//        IConnector nELeftConnector = netiface.createConnector(nApproach.id(), eOutgoing.id(),
//                northLeftFromLanes, northLeftToLanes, "北左转", true);
//
//        // 南北直行连接段（北到南）
//        ArrayList<Integer> northStraightFromLanes = new ArrayList<Integer>();
//        northStraightFromLanes.add(2);
//        ArrayList<Integer> northStraightToLanes = new ArrayList<Integer>();
//        northStraightToLanes.add(2);
//        IConnector nSStraightConnector = netiface.createConnector(nApproach.id(), sOutgoing.id(),
//                northStraightFromLanes, northStraightToLanes, "南北直行", true);
//
//        System.out.println("step4: 创建路段发车点");
//        IDispatchPoint wDispatchPoint = netiface.createDispatchPoint(wApproach, "西进口发车");
//
//        // 创建车辆组成
//        ArrayList<VehiComposition> vehiTypeProportionList = new ArrayList<VehiComposition>();
//        vehiTypeProportionList.add(new VehiComposition(1, 0.3));
//        vehiTypeProportionList.add(new VehiComposition(2, 0.2));
//        vehiTypeProportionList.add(new VehiComposition(3, 0.1));
//        vehiTypeProportionList.add(new VehiComposition(4, 0.4));
//        long vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiTypeProportionList);
//
//        if (wDispatchPoint != null) {
//            wDispatchPoint.addDispatchInterval(1, 200, 28);
//            wDispatchPoint.addDispatchInterval(vehiCompositionID, 500, 100);
//            wDispatchPoint.setDynaModified(true);
//        }
//
//        // 东进口发车点
//        dp = netiface.createDispatchPoint(eApproach);
//        if (dp != null) {
//            dp.addDispatchInterval(1, 300, 100);
//        }
//
//        // 南进口发车点
//        dp = netiface.createDispatchPoint(sApproach);
//        if (dp != null) {
//            dp.addDispatchInterval(1, 300, 100);
//        }
//
//        System.out.println("step5: 创建路径决策点和决策路径");
//        System.out.println("     创建静态路径决策点");
//        IDecisionPoint wApproachDecisionPoint = netiface.createDecisionPoint(wApproach, 50, "w_approach_decisionPoint", UnitOfMeasure.Metric);
//        IDecisionPoint eApproachDecisionPoint = netiface.createDecisionPoint(eApproach, 50, "e_approach_decisionPoint", UnitOfMeasure.Metric);
//        IDecisionPoint sApproachDecisionPoint = netiface.createDecisionPoint(sApproach, 50, "s_approach_decisionPoint", UnitOfMeasure.Metric);
//        IDecisionPoint nApproachDecisionPoint = netiface.createDecisionPoint(nApproach, 50, "n_approach_decisionPoint");
//
//        System.out.println("     创建决策路径");
//        ArrayList<ILink> westToNorthLinks = new ArrayList<ILink>();
//        westToNorthLinks.add(wApproach);
//        westToNorthLinks.add(nOutgoing);
//        IRouting wApproachDecisionPointLeft = netiface.createDeciRouting(wApproachDecisionPoint, westToNorthLinks);
//
//        ArrayList<ILink> westToEastLinks = new ArrayList<ILink>();
//        westToEastLinks.add(wApproach);
//        westToEastLinks.add(eOutgoing);
//        IRouting wApproachDecisionPointStraight = netiface.createDeciRouting(wApproachDecisionPoint, westToEastLinks);
//
//        ArrayList<ILink> westToSouthLinks = new ArrayList<ILink>();
//        westToSouthLinks.add(wApproach);
//        westToSouthLinks.add(sOutgoing);
//        IRouting wApproachDecisionPointRight = netiface.createDeciRouting(wApproachDecisionPoint, westToSouthLinks);
//
//        ArrayList<ILink> eastToSouthLinks = new ArrayList<ILink>();
//        eastToSouthLinks.add(eApproach);
//        eastToSouthLinks.add(sOutgoing);
//        IRouting eApproachDecisionPointLeft = netiface.createDeciRouting(eApproachDecisionPoint, eastToSouthLinks);
//
//        ArrayList<ILink> eastToWestLinks = new ArrayList<ILink>();
//        eastToWestLinks.add(eApproach);
//        eastToWestLinks.add(wOutgoing);
//        IRouting eApproachDecisionPointStraight = netiface.createDeciRouting(eApproachDecisionPoint, eastToWestLinks);
//
//        ArrayList<ILink> eastToNorthLinks = new ArrayList<ILink>();
//        eastToNorthLinks.add(eApproach);
//        eastToNorthLinks.add(nOutgoing);
//        IRouting eApproachDecisionPointRight = netiface.createDeciRouting(eApproachDecisionPoint, eastToNorthLinks);
//
//        IRouting routing = netiface.shortestRouting(sApproach, wOutgoing);
//        IRouting sApproachDecisionPointLeft = netiface.createDeciRouting(sApproachDecisionPoint, routing.getLinks());
//
//        ArrayList<ILink> southToNorthLinks = new ArrayList<ILink>();
//        southToNorthLinks.add(sApproach);
//        southToNorthLinks.add(nOutgoing);
//        IRouting sApproachDecisionPointStraight = netiface.createDeciRouting(sApproachDecisionPoint, southToNorthLinks);
//
//        ArrayList<ILink> southToEastLinks = new ArrayList<ILink>();
//        southToEastLinks.add(sApproach);
//        southToEastLinks.add(eOutgoing);
//        IRouting sApproachDecisionPointRight = netiface.createDeciRouting(sApproachDecisionPoint, southToEastLinks);
//
//        routing = netiface.createRouting(createLinkList(nApproach, eOutgoing));
//        IRouting nApproachDecisionPointLeft = netiface.createDeciRouting(nApproachDecisionPoint, routing.getLinks());
//
//        ArrayList<ILink> northToSouthLinks = new ArrayList<ILink>();
//        northToSouthLinks.add(nApproach);
//        northToSouthLinks.add(sOutgoing);
//        IRouting nApproachDecisionPointStraight = netiface.createDeciRouting(nApproachDecisionPoint, northToSouthLinks);
//
//        ArrayList<ILink> northToWestLinks = new ArrayList<ILink>();
//        northToWestLinks.add(nApproach);
//        northToWestLinks.add(wOutgoing);
//        IRouting nApproachDecisionPointRight = netiface.createDeciRouting(nApproachDecisionPoint, northToWestLinks);
//
//        // 删除决策路径并重新创建
//        netiface.removeDeciRouting(wApproachDecisionPoint, wApproachDecisionPointRight);
//        routing = netiface.createRouting(createLinkList(wApproach, sOutgoing));
//        IRouting routing1 = netiface.shortestRouting(wApproach, sOutgoing);
//        IRouting wApproachDecisionPointRight1 = netiface.createDeciRouting(wApproachDecisionPoint, createLinkList(wApproach, sOutgoing));
//
//        System.out.println("step6: 创建行人系统");
//        IPedestrianRegion[] crosswalks = createPed(netiface);
//        IPedestrianRegion nCrosswalk = crosswalks[0];
//        IPedestrianRegion sCrosswalk = crosswalks[1];
//        IPedestrianRegion wCrosswalk = crosswalks[2];
//        IPedestrianRegion eCrosswalk = crosswalks[3];
//
//        System.out.println("step7: 创建公交和行人上下客系统");
//        createBusPed(netiface, wApproach, eOutgoing, sOutgoing);
//
//        System.out.println("step8: 创建行人和机动车信号灯和信控方案");
//        createSignalControl(netiface, wApproach, eApproach, nApproach, sApproach,
//                nCrosswalk, sCrosswalk, wCrosswalk, eCrosswalk);
//
//        System.out.println("step9: 创建车道箭头");
//        createGuidArrow(netiface, wApproach);
//    }
//
//    // 创建行人面域
//    private IPedestrianRegion[] createPed(NetInterface netiface) {
//        System.out.println("     创建行人组成");
//        Map<Integer, Double> composition = new HashMap<Integer, Double>();
//        composition.put(1, 0.8);
//        composition.put(2, 0.2);
//        int pedComposition = netiface.createPedestrianComposition("自定义1", composition);
//
//        System.out.println("     创建行人图层");
//        LayerInfo pedLayer = netiface.addLayerInfo("行人图层", 0.0, true, false);
//        LayerInfo pedLayer1 = netiface.addLayerInfo("行人图层1", 10.0, true, false);
//        netiface.removeLayerInfo(pedLayer1.getId());
//        netiface.updateLayerInfo(pedLayer.getId(), "基础行人图层", 0.0, true, false);
//
//        System.out.println("     创建行人面域");
//
//        System.out.println("     创建人行道");
//        ArrayList<Point> sidewalkPoints = new ArrayList<Point>();
//        sidewalkPoints.add(new Point(-300, 14));
//        sidewalkPoints.add(new Point(-25, 14));
//        IPedestrianRegion wApproachSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(-300, -14));
//        sidewalkPoints.add(new Point(-25, -14));
//        IPedestrianRegion wOutgoingSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(300, -14));
//        sidewalkPoints.add(new Point(25, -14));
//        IPedestrianRegion eApproachSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(300, 14));
//        sidewalkPoints.add(new Point(25, 14));
//        IPedestrianRegion eOutgoingSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(-14, -300));
//        sidewalkPoints.add(new Point(-14, -25));
//        IPedestrianRegion nApproachSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(14, -25));
//        sidewalkPoints.add(new Point(14, -300));
//        IPedestrianRegion nOutgoingSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(14, 300));
//        sidewalkPoints.add(new Point(14, 25));
//        IPedestrianRegion sApproachSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        sidewalkPoints.clear();
//        sidewalkPoints.add(new Point(-14, 300));
//        sidewalkPoints.add(new Point(-14, 25));
//        IPedestrianRegion sOutgoingSidewalk = netiface.createPedestrianSideWalkRegion(sidewalkPoints);
//
//        IPedestrianRegion cwr = netiface.createPedestrianCrossWalkRegion(new Point(300, -300), new Point(30, -30));
//        netiface.removePedestrianCrossWalkRegion(cwr);
//
//        System.out.println("     创建交叉口四个等待区面域");
//        IPedestrianRegion fsr2 = netiface.createPedestrianFanShapeRegion(new Point(-26, -26), new Point(-15, -15));
//        IPedestrianRegion fsr3 = netiface.createPedestrianFanShapeRegion(new Point(-26, 26), new Point(-15, 15));
//        IPedestrianRegion fsr4 = netiface.createPedestrianFanShapeRegion(new Point(26, -26), new Point(15, -15));
//        IPedestrianRegion fsr5 = netiface.createPedestrianFanShapeRegion(new Point(26, 26), new Point(15, 15));
//
//        // 创建斑马线
//        System.out.println("     创建斑马线");
//        IPedestrianRegion nCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(14, -22), new Point(-14, -22));
//        IPedestrianRegion sCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(14, 22), new Point(-14, 22));
//        IPedestrianRegion wCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(-22, -14), new Point(-22, 14));
//        IPedestrianRegion eCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(22, -14), new Point(22, 14));
//
//        // 创建行人发生点和路径
//        System.out.println("     创建行人发生点和路径");
//        IPedestrianPathStartPoint wPathStart = netiface.createPedestrianPathStartPoint(new Point(-280, 14));
//
//        // 更新行人发生点配置信息
//        IPedestrianStartPointConfigInfo configInfo = netiface.findPedestrianStartPointConfigInfo(wPathStart.getId());
//        Pedestrian.GenPedestrianInfo genPedInfo = new Pedestrian.GenPedestrianInfo();
//        genPedInfo.pedestrianCount = 1000;
//        genPedInfo.timeInterval = 2000;
//
//        ArrayList<Pedestrian.GenPedestrianInfo> genPedList = new ArrayList<Pedestrian.GenPedestrianInfo>();
//        genPedList.add(genPedInfo);
//
//        Pedestrian.PedestrianTrafficDistributionInfo distInfo = new Pedestrian.PedestrianTrafficDistributionInfo();
//        distInfo.timeInterval = 2000;
//
//        ArrayList<Pedestrian.PedestrianTrafficDistributionInfo> distList = new ArrayList<Pedestrian.PedestrianTrafficDistributionInfo>();
//        distList.add(distInfo);
//
//        configInfo.genPedestrianConfigInfo = genPedList;
//        configInfo.pedestrianTrafficDistributionConfigInfo = distList;
//        netiface.updatePedestrianStartPointConfigInfo(configInfo);
//
//        IPedestrianPathEndPoint wPathEnd = netiface.createPedestrianPathEndPoint(new Point(-280, -14));
//        IPedestrianPathStartPoint ePathStart = netiface.createPedestrianPathStartPoint(new Point(280, 14));
//        IPedestrianPathEndPoint ePathEnd = netiface.createPedestrianPathEndPoint(new Point(280, -14));
//        IPedestrianPathStartPoint nPathStart = netiface.createPedestrianPathStartPoint(new Point(-14, -280));
//        IPedestrianPathEndPoint nPathEnd = netiface.createPedestrianPathEndPoint(new Point(14, -280));
//        IPedestrianPathStartPoint sPathStart = netiface.createPedestrianPathStartPoint(new Point(14, 280));
//        IPedestrianPathEndPoint sPathEnd = netiface.createPedestrianPathEndPoint(new Point(-14, 280));
//
//        // 创建行人路径
//        ArrayList<Point> pathPoints = new ArrayList<Point>();
//        pathPoints.add(new Point(-22, -1));
//        IPedestrianPath pww = netiface.createPedestrianPath(wPathStart, wPathEnd, pathPoints);
//
//        pathPoints.clear();
//        IPedestrianPath pwe = netiface.createPedestrianPath(wPathStart, ePathEnd, pathPoints);
//        IPedestrianPath pws = netiface.createPedestrianPath(wPathStart, sPathEnd, pathPoints);
//        IPedestrianPath pwn = netiface.createPedestrianPath(wPathStart, nPathEnd, pathPoints);
//
//        IPedestrianPath pew = netiface.createPedestrianPath(ePathStart, wPathEnd, pathPoints);
//        IPedestrianPath pee = netiface.createPedestrianPath(ePathStart, ePathEnd, pathPoints);
//        IPedestrianPath pes = netiface.createPedestrianPath(ePathStart, sPathEnd, pathPoints);
//        IPedestrianPath pen = netiface.createPedestrianPath(ePathStart, nPathEnd, pathPoints);
//
//        IPedestrianPath psw = netiface.createPedestrianPath(sPathStart, wPathEnd, pathPoints);
//        IPedestrianPath pse = netiface.createPedestrianPath(sPathStart, ePathEnd, pathPoints);
//        IPedestrianPath pss = netiface.createPedestrianPath(sPathStart, sPathEnd, pathPoints);
//        IPedestrianPath psn = netiface.createPedestrianPath(sPathStart, nPathEnd, pathPoints);
//
//        IPedestrianPath pnw = netiface.createPedestrianPath(nPathStart, wPathEnd, pathPoints);
//        IPedestrianPath pne = netiface.createPedestrianPath(nPathStart, ePathEnd, pathPoints);
//        IPedestrianPath pns = netiface.createPedestrianPath(nPathStart, sPathEnd, pathPoints);
//        IPedestrianPath pnn = netiface.createPedestrianPath(nPathStart, nPathEnd, pathPoints);
//
//        System.out.println("     创建行人上下楼梯");
//        createPedUpdownstairs(netiface, pedLayer);
//
//        return new IPedestrianRegion[]{nCrosswalk, sCrosswalk, wCrosswalk, eCrosswalk};
//    }
//
//    private void createBusPed(NetInterface netiface, ILink wApproach, ILink eOutgoing, ILink sOutgoing) {
//        """
//        行人上下公交站台案例
//        """
//
//        // 创建公交线路
//        System.out.println("     创建公交线路");
//        ArrayList<ILink> busLineLinks = new ArrayList<ILink>();
//        busLineLinks.add(wApproach);
//        busLineLinks.add(eOutgoing);
//        IBusLine busline = netiface.createBusLine(busLineLinks);
//
//        ArrayList<ILink> busLineLinks1 = new ArrayList<ILink>();
//        busLineLinks1.add(wApproach);
//        busLineLinks1.add(sOutgoing);
//        IBusLine busline1 = netiface.createBusLine(busLineLinks1);
//        netiface.removeBusLine(busline1);
//
//        if (busline != null) {
//            busline.setDesirSpeed(60, UnitOfMeasure.Metric);
//        }
//
//        // 创建公交站
//        System.out.println("     创建公交站点");
//        IBusStation busstation1 = netiface.createBusStation(wApproach.lanes().get(0), 30, 100, "西进口公交站点1", UnitOfMeasure.Metric);
//        IBusStation busstation2 = netiface.createBusStation(wApproach.lanes().get(0), 30, 200, "西进口公交站点2", UnitOfMeasure.Metric);
//        IBusStation busstation3 = netiface.createBusStation(eOutgoing.lanes().get(0), 30, 200, "东出口公交站点1", UnitOfMeasure.Metric);
//
//        // 将公交站点关联到公交线路上
//        netiface.addBusStationToLine(busline, busstation1);
//        netiface.addBusStationToLine(busline, busstation2);
//        netiface.addBusStationToLine(busline, busstation3);
//        netiface.removeBusStationFromLine(busline, busstation2);
//        netiface.removeBusStation(busstation2);
//
//        System.out.println("     创建公交站点行人上下客面域");
//        // 创建行人上下客面域
//        IPedestrianRegion upPedArea = netiface.createPedestrianRectRegion(new Point(-200, 10), new Point(-170, 12));
//        upPedArea.setIsBoardingArea(true);
//
//        IPedestrianRegion upDownPedArea = netiface.createPedestrianRectRegion(new Point(260, 10), new Point(200, 30));
//        upDownPedArea.setIsBoardingArea(true);
//        upDownPedArea.setIsAlightingArea(true);
//
//        IPedestrianRegion leaveArea = netiface.createPedestrianRectRegion(new Point(170, 28), new Point(300, 40));
//
//        IPedestrianDecisionPoint sPathStartAndDecisionPoint1 = netiface.createPedestrianDecisionPoint(new Point(14, 280));
//        netiface.removePedestrianDecisionPoint(sPathStartAndDecisionPoint1);
//
//        IPedestrianDecisionPoint sPathStartAndDecisionPoint = netiface.createPedestrianDecisionPoint(new Point(-250, 15));
//        IPedestrianDecisionPoint downpedPathStartAndDecisionPoint = netiface.createPedestrianDecisionPoint(new Point(240, 15));
//        IPedestrianPathEndPoint sPathEnd = netiface.createPedestrianPathEndPoint(new Point(-180, 11));
//        IPedestrianPathEndPoint sPathEnd1 = netiface.createPedestrianPathEndPoint(new Point(240, 35));
//
//        ArrayList<Point> pathPoints = new ArrayList<Point>();
//        IPedestrianPath uppedPath = netiface.createPedestrianPath(sPathStartAndDecisionPoint, sPathEnd, pathPoints);
//        IPedestrianPath downpedPath = netiface.createPedestrianPath(downpedPathStartAndDecisionPoint, sPathEnd1, pathPoints);
//    }
//
//    private void createPedUpdownstairs(NetInterface netiface, ILayerInfo pedLayer) {
//        """
//        行人上下楼梯的案例
//        """
//        // 创建行人图层
//        ILayerInfo pedLayer2 = netiface.addLayerInfo("行人图层2", 10.0, true, false);
//
//        // 创建行人面域
//        IPedestrianRegion yilou = netiface.createPedestrianRectRegion(new Point(30, 30), new Point(40, 100));
//        yilou.setLayerId(pedLayer.id());
//
//        IPedestrianRegion erlou = netiface.createPedestrianRectRegion(new Point(50, 30), new Point(60, 100));
//        erlou.setLayerId(pedLayer2.id());
//
//        // 创建楼梯
//        IPedestrianStairRegion stairObj1 = netiface.createPedestrianStairRegion(new Point(38, 35), new Point(52, 35));
//        stairObj1.setStartLayerId(pedLayer.id());
//        stairObj1.setEndLayerId(pedLayer2.id());
//
//        IPedestrianStairRegion testStair = netiface.createPedestrianStairRegion(new Point(40, 32), new Point(60, 38));
//        netiface.removePedestrianStairRegion(testStair);
//
//        IPedestrianStairRegion stairObj2 = netiface.createPedestrianStairRegion(new Point(38, 65), new Point(52, 65));
//        stairObj2.setStartLayerId(pedLayer.id());
//        stairObj2.setEndLayerId(pedLayer2.id());
//
//        // 创建行人决策点+楼梯，进行楼梯分流
//        IPedestrianPathStartPoint sPathStart = netiface.createPedestrianPathStartPoint(new Point(32, 55));
//        IPedestrianPathEndPoint sPathEnd = netiface.createPedestrianPathEndPoint(new Point(55, 55));
//
//        ArrayList<Point> pathPoints = new ArrayList<Point>();
//        pathPoints.add(new Point(40, 35));
//        IPedestrianPath p1 = netiface.createPedestrianPath(sPathStart, sPathEnd, pathPoints);
//
//        IPedestrianDecisionPoint sPathStartAndDecisionPoint1 = netiface.createPedestrianDecisionPoint(new Point(36, 50));
//        sPathEnd = netiface.createPedestrianPathEndPoint(new Point(55, 35));
//
//        pathPoints.clear();
//        pathPoints.add(new Point(45, 35));
//        IPedestrianPath p1_1 = netiface.createPedestrianPath(sPathStartAndDecisionPoint1, sPathEnd, pathPoints);
//
//        sPathEnd = netiface.createPedestrianPathEndPoint(new Point(55, 65));
//        pathPoints.clear();
//        pathPoints.add(new Point(45, 65));
//        IPedestrianPath p1_2 = netiface.createPedestrianPath(sPathStartAndDecisionPoint1, sPathEnd, pathPoints);
//
//        Pedestrian.PedestrianDecisionPointConfigInfo pedDecisionPointConfigInfo = new Pedestrian.PedestrianDecisionPointConfigInfo();
//        pedDecisionPointConfigInfo.id = sPathStartAndDecisionPoint1.getId();
//
//        Pedestrian.PedestrianTrafficDistributionInfo distributeInfo = new Pedestrian.PedestrianTrafficDistributionInfo();
//        distributeInfo.timeInterval = 1000;
//
//        Map<Integer, Integer> trafficRatio = new HashMap<Integer, Integer>();
//        trafficRatio.put(p1_1.getId(), 2);
//        trafficRatio.put(p1_2.getId(), 1);
//        distributeInfo.trafficRatio = trafficRatio;
//
//        Map<Integer, ArrayList<Pedestrian.PedestrianTrafficDistributionInfo>> distConfigMap = new HashMap<Integer, ArrayList<Pedestrian.PedestrianTrafficDistributionInfo>>();
//        ArrayList<Pedestrian.PedestrianTrafficDistributionInfo> distList = new ArrayList<Pedestrian.PedestrianTrafficDistributionInfo>();
//        distList.add(distributeInfo);
//        distConfigMap.put(p1.getId(), distList);
//
//        pedDecisionPointConfigInfo.pedestrianTrafficDistributionConfigInfo = distConfigMap;
//        netiface.updatePedestrianDecisionPointConfigInfo(pedDecisionPointConfigInfo);
//    }
//
//    private void createGuidArrow(NetInterface netiface, ILink wApproach) {
//        GuideArrowType arrowType = GuideArrowType.StraightRight;
//        netiface.createGuidArrow(wApproach.lanes().get(0), 4, 10, arrowType);
//    }
//
//    private void createSignalControl(NetInterface netiface, ILink wApproach, ILink eApproach,
//                                     ILink nApproach, ILink sApproach,
//                                     IPedestrianRegion nCrosswalk, IPedestrianRegion sCrosswalk,
//                                     IPedestrianRegion wCrosswalk, IPedestrianRegion eCrosswalk) {
//        // 创建信号机
//        System.out.println("     创建信号机");
//        ISignalController trafficController = netiface.createSignalController("交叉口1");
//
//        // 创建信控方案
//        System.out.println("     创建信控方案");
//        ISignalPlan signalPlan = netiface.createSignalPlan(trafficController, "早高峰", 150, 0, 0, 1800);
//
//        // 创建相位
//        System.out.println("     创建相位");
//        ArrayList<ColorInterval> wEStraightPhaseColor = new ArrayList<ColorInterval>();
//        wEStraightPhaseColor.add(new ColorInterval("绿", 50));
//        wEStraightPhaseColor.add(new ColorInterval("黄", 3));
//        wEStraightPhaseColor.add(new ColorInterval("红", 97));
//        ISignalPhase wEStraightPhase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行", wEStraightPhaseColor);
//
//        ISignalPhase wePedPhase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", wEStraightPhaseColor);
//
//        ArrayList<ColorInterval> wELeftPhaseColor = new ArrayList<ColorInterval>();
//        wELeftPhaseColor.add(new ColorInterval("红", 53));
//        wELeftPhaseColor.add(new ColorInterval("绿", 30));
//        wELeftPhaseColor.add(new ColorInterval("黄", 3));
//        wELeftPhaseColor.add(new ColorInterval("红", 64));
//        ISignalPhase wELeftPhase = netiface.createSignalPlanSignalPhase(signalPlan, "东西左转", wELeftPhaseColor);
//
//        ArrayList<ColorInterval> sNStraightPhaseColor = new ArrayList<ColorInterval>();
//        sNStraightPhaseColor.add(new ColorInterval("红", 86));
//        sNStraightPhaseColor.add(new ColorInterval("绿", 30));
//        sNStraightPhaseColor.add(new ColorInterval("黄", 3));
//        sNStraightPhaseColor.add(new ColorInterval("红", 31));
//        ISignalPhase sNStraightPhase = netiface.createSignalPlanSignalPhase(signalPlan, "南北直行", sNStraightPhaseColor);
//        ISignalPhase nsPedPhase = netiface.createSignalPlanSignalPhase(signalPlan, "南北直行行人", sNStraightPhaseColor);
//
//        ArrayList<ColorInterval> sNLeftPhaseColor = new ArrayList<ColorInterval>();
//        sNLeftPhaseColor.add(new ColorInterval("红", 119));
//        sNLeftPhaseColor.add(new ColorInterval("绿", 29));
//        sNLeftPhaseColor.add(new ColorInterval("黄", 3));
//        ISignalPhase sNLeftPhase = netiface.createSignalPlanSignalPhase(signalPlan, "南北左转", sNLeftPhaseColor);
//
//        // 创建机动车信号灯并绑定相位
//        System.out.println("     创建机动车信号灯并绑定相位");
//        ArrayList<ISignalLamp> wEStraightLamps = new ArrayList<ISignalLamp>();
//        for (ILane lane : wApproach.lanes()) {
//            if (lane.number() < wApproach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(wEStraightPhase, "东西直行信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                wEStraightLamps.add(signalLamp);
//            }
//        }
//
//        for (ILane lane : eApproach.lanes()) {
//            if (lane.number() < eApproach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(wEStraightPhase, "东西直行信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                wEStraightLamps.add(signalLamp);
//            }
//        }
//
//        ArrayList<ISignalLamp> wELeftLamps = new ArrayList<ISignalLamp>();
//        for (ILane lane : wApproach.lanes()) {
//            if (lane.number() == wApproach.laneCount() - 1) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(wELeftPhase, "东西左转信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                wELeftLamps.add(signalLamp);
//            }
//        }
//
//        for (ILane lane : eApproach.lanes()) {
//            if (lane.number() == eApproach.laneCount() - 1) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(wELeftPhase, "东西左转信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                wELeftLamps.add(signalLamp);
//            }
//        }
//
//        ArrayList<ISignalLamp> nSStraightLamps = new ArrayList<ISignalLamp>();
//        for (ILane lane : nApproach.lanes()) {
//            if (lane.number() < nApproach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(sNStraightPhase, "南北直行信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                nSStraightLamps.add(signalLamp);
//            }
//        }
//
//        for (ILane lane : sApproach.lanes()) {
//            if (lane.number() < sApproach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(sNStraightPhase, "南北直行信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                nSStraightLamps.add(signalLamp);
//            }
//        }
//
//        ArrayList<ISignalLamp> nSLeftLamps = new ArrayList<ISignalLamp>();
//        for (ILane lane : nApproach.lanes()) {
//            if (lane.number() == nApproach.laneCount() - 1) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(sNLeftPhase, "南北左转信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                nSLeftLamps.add(signalLamp);
//            }
//        }
//        for (ILane lane : sApproach.lanes()) {
//            if (lane.number() == sApproach.laneCount() - 1) {
//                ISignalLamp signalLamp = netiface.createSignalLamp(sNLeftPhase, "南北左转信号灯",
//                        lane.id(), -1, lane.length() - 0.5);
//                nSLeftLamps.add(signalLamp);
//            }
//        }
//
//        System.out.println("     创建行人信号灯并绑定相位");
//        // 创建行人信号灯并关联相位
//        ICrosswalkSignalLamp signalLamp1Positive = netiface.createCrossWalkSignalLamp(trafficController,
//                "南斑马线信号灯", sCrosswalk.getId(), new Point(13, 22), true);
//        ICrosswalkSignalLamp signalLamp1Negetive = netiface.createCrossWalkSignalLamp(trafficController,
//                "南斑马线信号灯", sCrosswalk.getId(), new Point(-13, 22), false);
//        netiface.addCrossWalkSignalPhaseToLamp(wePedPhase.id(), signalLamp1Positive);
//        netiface.addCrossWalkSignalPhaseToLamp(wePedPhase.id(), signalLamp1Negetive);
//
//        ICrosswalkSignalLamp signalLamp2Positive = netiface.createCrossWalkSignalLamp(trafficController,
//                "北斑马线信号灯", nCrosswalk.getId(), new Point(13, -22), true);
//        ICrosswalkSignalLamp signalLamp2Negetive = netiface.createCrossWalkSignalLamp(trafficController,
//                "北斑马线信号灯", nCrosswalk.getId(), new Point(-13, -22), false);
//        netiface.addCrossWalkSignalPhaseToLamp(wePedPhase.id(), signalLamp2Positive);
//        netiface.addCrossWalkSignalPhaseToLamp(wePedPhase.id(), signalLamp2Negetive);
//
//        ICrosswalkSignalLamp signalLamp3Positive = netiface.createCrossWalkSignalLamp(trafficController,
//                "东斑马线信号灯", eCrosswalk.getId(), new Point(22, -13), true);
//        ICrosswalkSignalLamp signalLamp3Negetive = netiface.createCrossWalkSignalLamp(trafficController,
//                "东斑马线信号灯", eCrosswalk.getId(), new Point(22, 13), false);
//        netiface.addCrossWalkSignalPhaseToLamp(nsPedPhase.id(), signalLamp3Positive);
//        netiface.addCrossWalkSignalPhaseToLamp(nsPedPhase.id(), signalLamp3Negetive);
//
//        ICrosswalkSignalLamp signalLamp4Positive = netiface.createCrossWalkSignalLamp(trafficController,
//                "西斑马线信号灯", wCrosswalk.getId(), new Point(-22, -13), true);
//        ICrosswalkSignalLamp signalLamp4Negetive = netiface.createCrossWalkSignalLamp(trafficController,
//                "西斑马线信号灯", wCrosswalk.getId(), new Point(-22, 13), false);
//        netiface.addCrossWalkSignalPhaseToLamp(nsPedPhase.id(), signalLamp4Positive);
//        netiface.addCrossWalkSignalPhaseToLamp(nsPedPhase.id(), signalLamp4Negetive);
//    }
//
//    // 辅助方法：获取车道ID列表
//    private ArrayList<Integer> getLaneIds(ArrayList<ILane> lanes) {
//        ArrayList<Integer> laneIds = new ArrayList<Integer>();
//        for (ILane lane : lanes) {
//            laneIds.add(lane.id());
//        }
//        return laneIds;
//    }
//
//    // 辅助方法：创建包含两个路段的列表
//    private ArrayList<ILink> createLinkList(ILink link1, ILink link2) {
//        ArrayList<ILink> links = new ArrayList<ILink>();
//        links.add(link1);
//        links.add(link2);
//        return links;
//    }
//}
