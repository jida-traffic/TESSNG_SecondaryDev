package Fundamental_Functions.Node_Evaluation;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;
import java.util.List;

public class TestSimulator extends JCustomerSimulator {
    //车辆方阵的车辆数
    int mrSquareVehiCount = 28;
    //飞机速度，飞机后面的车辆速度会被设定为此数据
    double mrSpeedOfPlane = 0;
    //当前正在仿真计算的路网名称
    String mNetPath;
    //相同路网连续仿真次数
    int mSimuCount = 0;
    private boolean isTestExecuted = false;
    public TestSimulator(){
        super();
    }

    public long getCptr(){
        return JCustomerSimulator.getCPtr(this);
    }

    @Override
    public void initVehicle(IVehicle vehi){
        vehi.setIsPermitForVehicleDraw(true);
        vehi.setIsPermitForVehicleDraw(true);
        vehi.setSteps_calcLimitedLaneNumber(10);
        vehi.setSteps_calcChangeLaneSafeDist(10);
        vehi.setSteps_reCalcdesirSpeed(1);
        vehi.setSteps_reSetSpeed(1);

        // 车辆 ID，不含首位数，首位数与车辆来源有关，如发车点、公交线路
        int tmpId = (int)(vehi.id() % 100000);
        // 车辆所在路段名或连接段名
        String roadName = vehi.roadName();
        // 车辆所在路段 ID 或连接段 ID
        int roadId = (int)(vehi.roadId());
        if ("曹安公路".equals(roadName)) {
            // 飞机
            if (tmpId == 1) {
                vehi.setVehiType(12);
                vehi.initSpeed(10000,UnitOfMeasure.Metric);
                vehi.initLane(3, TESSNG.m2p(0), 100);
            }
            // 工程车
            else if (tmpId >= 2 && tmpId <= 8) {
                vehi.setVehiType(8);
                vehi.initLane((tmpId - 2) % 7, TESSNG.m2p(80), 0);
            }
            // 消防车
            else if (tmpId >= 9 && tmpId <= 15) {
                vehi.setVehiType(9);
                vehi.initLane((tmpId - 2) % 7, TESSNG.m2p(65), 0);
            }
            // 消防车
            else if (tmpId >= 16 && tmpId <= 22) {
                vehi.setVehiType(10);
                vehi.setColor("#EEFFFF");
                vehi.initLane((tmpId - 2) % 7, TESSNG.m2p(50), 0);
            }
            // 最后两队列小车
            else if (tmpId == 23) {
                vehi.setVehiType(1);
                vehi.setColor("#EEFFFF");
//                vehi.setLength(100,false);
                vehi.initLane(1, TESSNG.m2p(35), 0);
            } else if (tmpId == 24) {
                vehi.setVehiType(1);
                vehi.setColor("#EEFFFF");
                vehi.initLane(5, TESSNG.m2p(35), 0);
            } else if (tmpId == 25) {
                vehi.setVehiType(1);
                vehi.setColor("#EEFFFF");
                vehi.initLane(1, TESSNG.m2p(20), 0);
            } else if (tmpId == 26) {
                vehi.setVehiType(1);
                vehi.setColor("#EEFFFF");
                vehi.initLane(5, TESSNG.m2p(20), 0);
            } else if (tmpId == 27) {
                vehi.setVehiType(1);
                vehi.setColor("#EEFFFF");
                vehi.initLane(1, TESSNG.m2p(5), 0);
            } else if (tmpId == 28) {
                vehi.setVehiType(1);
                vehi.setColor("#EEFFFF");
                vehi.initLane(5, TESSNG.m2p(5), 0);
            }
            // 最后两列小车的长度设为一样长，这个很重要，如果车长不一样长，加上导致的前车距就不一样，会使它们变道轨迹长度不一样，就会乱掉
            if (tmpId >= 23 && tmpId <= 28) {
                vehi.setLength(TESSNG.m2p(4.5), true);
            }
        }
    }

    @Override
    public void afterOneStep() {

        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netInterface = iface.netInterface();
        SimuInterface simuIFace = iface.simuInterface();
        long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
        if(simuTime % 10000 != 0){
            //return;
        }
        if (simuTime > 5000 && !isTestExecuted) { // 等待5秒后再打印
            testPrintVehicleIds();
        }
        ArrayList<IVehicle> vehis = simuIFace.allVehicle();
        for(int i = 0, size = vehis.size(); i < size; ++i){
            IVehicle vehi = vehis.get(i);
            long id = vehi.id();
            double len = vehi.length();
            String name = vehi.name();
            double speed = vehi.currSpeed();
            double acce = vehi.acce();
            vehi.delete();
        }

        ArrayList<VehicleStatus> statusList = simuIFace.getVehisStatus();
        for(int i = 0, size = statusList.size(); i < size; ++i){
            VehicleStatus status = statusList.get(i);
            long roadId = status.getMrRoadId();
            long id = status.getVehiId();
            double speed = status.getMrSpeed();
            double angle = status.getMrAngle();
            double acce = status.getMrAcce();
            status.delete();
        }
    }
//    public void afterOneStep() {
//        // 获取 TESSNG 顶层接口
//        TessInterface iface = TESSNG.tessngIFace();
//
//        // 获取 TESSNG 仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//
//        // 获取 TESSNG 路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        // 当前仿真计算批次
//        long batchNum = simuiface.batchNumber();
//
//        // 当前已仿真时间，单位：毫秒
//        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
//
//        // 获取当前正在运行的车辆列表
//        List<IVehicle> vehis = simuiface.allVehiStarted();
//
//        // 你可以在此添加你的逻辑，比如打印信息或处理车辆数据
//        System.out.println("仿真批次: " + batchNum);
//        System.out.println("当前仿真时间: " + simuTime + " ms");
//        System.out.println("当前运行车辆数量: " + vehis.size());
//    }
    // 测试方法：打印已启动车辆的ID
    private void testPrintVehicleIds() {
        // 控制只执行一次（避免每步仿真都打印，可选）
        if (isTestExecuted) {
            return;
        }

        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netIFace = iface.netInterface();

//        simuiface.pauseSimu();
//        simuiface.stopSimu();
//        simuiface.pauseSimuOrNot();
        // 获取所有已启动的车辆
        List<IVehicle> startedVehicles = simuiface.allVehiStarted();
        System.out.println("已启动车辆数量：" + startedVehicles.size());
        for (IVehicle vehicle : startedVehicles) {
            System.out.println(vehicle.roadId());
        }

//        ArrayList<VehiInfoCollected> collectedVehicles = simuiface.getVehisInfoCollected();
//        System.out.println("已收集车辆数量：" + collectedVehicles.size());

        // 获取 TESS NG 接口

//        SimuInterface simuInterface = iface.simuInterface();
//// 示例 link ID，实际应使用真实路段对象或 ID
//        long link3Id = 20266; // 示例路段 ID，替换为你实际创建的路段 ID
//        long link4Id = 20269;
//// 创建车道 0 的车辆参数
//        DynaVehiParam dvp_lane0 = new DynaVehiParam();
//        dvp_lane0.setVehiTypeCode(1);           // 设置车辆类型代码
//        dvp_lane0.setRoadId(link3Id);           // 设置路段 ID
//        dvp_lane0.setLaneNumber(0);             // 设置车道编号
//        dvp_lane0.setDist(TESSNG.m2p(50));      // 设置距离路段起点位置（转换为像素）
//        dvp_lane0.setSpeed(TESSNG.m2p(20));     // 设置速度（转换为像素/秒）
//        dvp_lane0.setColor("#FF0000");          // 设置颜色：红色
//// 创建车道 1 的车辆参数
//        DynaVehiParam dvp_lane1 = new DynaVehiParam();
//        dvp_lane1.setVehiTypeCode(2);           // 设置车辆类型代码
//        dvp_lane1.setRoadId(link3Id);           // 设置路段 ID
//        dvp_lane1.setLaneNumber(1);             // 设置车道编号
//        dvp_lane1.setDist(TESSNG.m2p(100));     // 设置距离路段起点位置
//        dvp_lane1.setSpeed(TESSNG.m2p(30));     // 设置速度
//        dvp_lane1.setColor("#008000");          // 设置颜色：绿色
//// 创建车道 2 的车辆参数
//        DynaVehiParam dvp_lane2 = new DynaVehiParam();
//        dvp_lane2.setVehiTypeCode(3);           // 设置车辆类型代码
//        dvp_lane2.setRoadId(link4Id);           // 设置路段 ID
//        dvp_lane2.setLaneNumber(2);             // 设置车道编号
//        dvp_lane2.setDist(TESSNG.m2p(50));      // 设置距离路段起点位置
//        dvp_lane2.setSpeed(TESSNG.m2p(40));     // 设置速度
//        dvp_lane2.setColor("#0000FF");          // 设置颜色：蓝色
//// 动态创建车辆
//        IVehicle vehi_lane0 = simuInterface.createGVehicle(dvp_lane0);
//        IVehicle vehi_lane1 = simuInterface.createGVehicle(dvp_lane1);
//        IVehicle vehi_lane2 = simuInterface.createGVehicle(dvp_lane2);
//// 检查是否创建成功
//        if (vehi_lane0 != null) {
//            System.out.println("车道0车辆创建成功，ID: " + vehi_lane0.id());
//        } else {
//            System.out.println("车道0车辆创建失败");
//        }
//        if (vehi_lane1 != null) {
//            System.out.println("车道1车辆创建成功，ID: " + vehi_lane1.id());
//        } else {
//            System.out.println("车道1车辆创建失败");
//        }
//        if (vehi_lane2 != null) {
//            System.out.println("车道2车辆创建成功，ID: " + vehi_lane2.id());
//        } else {
//            System.out.println("车道2车辆创建失败");
//        }

        // 获取 TESS NG 接口

//        IBusLine busLine = netIFace.findBusline(801);
//// 假设 busLine 已经创建成功
//// 动态创建公交车，延迟 10 秒（单位：毫秒）
//        long delayMs = 10 * 1000; // 10 秒后发车
//        IVehicle bus = simuiface.createBus(busLine, delayMs);
//        System.out.println("公交车创建成功，车辆ID: " + bus.id());

//        List<IVehicle> vehicleList = simuiface.vehisInLink(20265);
//        System.out.println("车辆数量：" + vehicleList.size());


        List<IVehicle> allVehicles = simuiface.allVehicle(); // 可替换为 allVehiStarted()
        List<IVehicle> allVehiStarted_lst = simuiface.allVehiStarted();
        List<IDecisionPoint> decisionPoints_lst = netIFace.decisionPoints();
        if (allVehicles.isEmpty()) {
            System.out.println("当前没有任何车辆");
        } else {
            System.out.println("===== 车辆详细位置信息 =====");
            for (IVehicle vehi : allVehicles) {
                long vehicleId = vehi.id();


                // 获取起始信息
                ILink startLink = vehi.startLink();
                String startLinkName = startLink != null ? startLink.name() : "未知";
                long startSimuTime = vehi.startSimuTime();

//                System.out.println("车辆ID：" + vehicleId);
//                System.out.println("  起始路段: " + startLinkName);
//                System.out.println("  进入时间: " + startSimuTime + " ms");
//                System.out.println("  当前路段ID: " + vehi.roadId());
//                System.out.println("  所在道路名: " + vehi.roadName());
//                System.out.println("  所在Section: " + vehi.section().name());
//                System.out.println("  所在车道: " + vehi.laneObj());
//                System.out.println("  分段序号: " + vehi.segmIndex());
//                System.out.println("  是否是路段: " + vehi.roadIsLink());
//                System.out.println("  道路名称: " + vehi.roadName());
//                System.out.println("  车辆长度: " + vehi.length());
//                System.out.println("  下游车道ID: " + vehi.toLaneId());
//                System.out.println("  toLane: " + vehi.lane());
//                System.out.println("  下游LaneConnector: " + vehi.toLane());
//                System.out.println("  当前批次号: " + vehi.currBatchNumber());
//                System.out.println("  道路类型: " + vehi.roadType());
//                System.out.println("  最大限速: " + vehi.limitMaxSpeed());
//                System.out.println("  最小限速: " + vehi.limitMinSpeed());
//                System.out.println("  车辆类型代码: " + vehi.vehicleTypeCode());
//                System.out.println("  车辆类型名称: " + vehi.vehicleTypeName());
//                System.out.println("  车辆名称: " + vehi.name());
//                System.out.println("  车辆驾驶行为接口: " + vehi.vehicleDriving());
//                System.out.println("  车辆位置: " + vehi.pos());
//                System.out.println("  车辆当前高程=" + vehi.zValue());
//                System.out.println("  车辆当前速度=" + vehi.currSpeed());
//                System.out.println("  车辆当前加速度=" + vehi.acce());
//                System.out.println("  车辆当前角度=" + vehi.angle());
//                System.out.println("  车辆是否在运行=" + vehi.isStarted());
//                System.out.println("  前车=" + (vehi.vehicleFront() != null ? vehi.vehicleFront().id() : "null"));
//                System.out.println("  后车=" + (vehi.vehicleRear() != null ? vehi.vehicleRear().id() : "null"));
//                System.out.println("  左前车=" + (vehi.vehicleLFront() != null ? vehi.vehicleLFront().id() : "null"));
//                System.out.println("  左前车=" + (vehi.vehicleLRear() != null ? vehi.vehicleLRear().id() : "null"));
//                System.out.println("  右前车=" + (vehi.vehicleRFront() != null ? vehi.vehicleRFront().id() : "null"));
//                System.out.println("  右后车=" + (vehi.vehicleRRear() != null ? vehi.vehicleRRear().id() : "null"));
//                System.out.println("  前车间距=" + vehi.vehiDistFront());
//                System.out.println("  前车速度=" + vehi.vehiSpeedFront());
//                System.out.println("  后车间距=" + vehi.vehiDistRear());
//                System.out.println("  后车速度=" + vehi.vehiSpeedRear());
//                System.out.println("  距前车时距=" + vehi.vehiHeadwayFront());
//                System.out.println("  距后车时距=" + vehi.vehiHeadwaytoRear());
//                System.out.println("  相邻左车道前车间距=" + vehi.vehiDistLLaneFront());
//                System.out.println("  相邻左车道前车速度=" + vehi.vehiSpeedLLaneFront());
//                System.out.println("  相邻左车道后车间距=" + vehi.vehiDistLLaneRear());
//                System.out.println("  相邻左车道后车速度=" + vehi.vehiSpeedLLaneRear());
//                System.out.println("  相邻右车道前车间距=" + vehi.vehiDistRLaneFront());
//                System.out.println("  相邻右车道前车速度=" + vehi.vehiSpeedRLaneFront());
//                System.out.println("  相邻右车道后车间距=" + vehi.vehiDistRLaneRear());
//                System.out.println("  相邻右车道后车速度=" + vehi.vehiSpeedRLaneRear());
//                System.out.println("  车道或车道连接中心线内点集=" + vehi.lLaneObjectVertex());
//                System.out.println("  车辆当前路径=" + (vehi.routing() != null ? vehi.routing().toString() : "null"));
//                System.out.println("  车辆图片=" + (vehi.picture() != null ? vehi.picture() : "null"));
//                System.out.println("  车辆由方向和长度决定的四个拐角构成的多边型=" +
//                        (vehi.boundingPolygon() != null ? vehi.boundingPolygon().toString() : "null"));
//                vehi.setTag(1);
//                System.out.println("标签表示的状态=" + vehi.tag());
//                vehi.setTextTag("test");
//                System.out.println("文本信息=" + (vehi.textTag() != null ? vehi.textTag() : "null"));
//                JsonObject json = Json.createObjectBuilder()
//                        .add("test", "test")
//                        .build();
//                vehi.setJsonInfo(json);
//                System.out.println("  json格式数据=" + (vehi.jsonInfo() != null ? vehi.jsonInfo().toString() : "null"));
//                JsonObject jsonInfo = vehi.jsonInfo();
//// 设置一个 JSON 字段
//                vehi.setJsonProperty("test", "active");
//
//// 读取该字段
//
//                System.out.println("json字段值=" + (vehi.jsonProperty("test") != null ? vehi.jsonProperty("test").toString() : "null"));
//                IVehicle drivingVehicle = vehi.vehicleDriving().vehicle();
//                System.out.println("当前驾驶车辆=" + (drivingVehicle != null ? drivingVehicle.id() : "null"));
//                double randomNumber = vehi.vehicleDriving().getRandomNumber();
//                System.out.printf("车辆ID=%d 的随机数=%f%n", vehi.id(), randomNumber);
//                System.out.println("计算下一点位置=" + vehi.vehicleDriving().nextPoint());
//                System.out.println("当前车速为零持续时间(毫秒)=" + vehi.vehicleDriving().zeroSpeedInterval());
//                System.out.println("当前是否在路段上且有决策点=" + vehi.vehicleDriving().isHavingDeciPointOnLink());
//                System.out.println("车辆的跟驰类型=" + vehi.vehicleDriving().followingType());
//                System.out.println("当前是否在路径上=" + vehi.vehicleDriving().isOnRouting());
//                vehi.vehicleDriving().stopVehicle();
//                System.out.println("旋转角=" + vehi.vehicleDriving().angle());
//                vehi.vehicleDriving().setAngle(vehi.angle() + 45.0);
//                System.out.println("旋转角=" + vehi.vehicleDriving().angle());
//                System.out.println("车辆欧拉角=" + vehi.vehicleDriving().euler().getX());
//                System.out.println("当前期望速度=" + vehi.vehicleDriving().desirSpeed());
//                System.out.println("当前所在路段或连接段=" + vehi.vehicleDriving().getCurrRoad().name());
//                System.out.println("当前所在路段或连接段=" + vehi.vehicleDriving().getCurrRoad());
//                System.out.println("下一路段或连接段=" + vehi.vehicleDriving().getNextRoad());
//                System.out.println("与目标车道序号的差值=" + vehi.vehicleDriving().differToTargetLaneNumber());
//                vehi.vehicleDriving().toLeftLane();
//                vehi.vehicleDriving().toRightLane();
//                System.out.println("当前车道序号=" + vehi.vehicleDriving().laneNumber());
//                System.out.println("轨迹类型=" + vehi.vehicleDriving().tracingType());
//                SWIGTYPE_UnitOfMeasure metric = SWIGTYPE_UnitOfMeasure.swigToEnum(0); // 假设 0 表示 Metric
//                System.out.println("当前计算周期移动距离=" + vehi.vehicleDriving().currDistance());
//                System.out.println("当前计算周期移动距离, 米制=" + vehi.vehicleDriving().currDistance(metric));
//                System.out.println("当前路段或连接上已行驶距离=" + vehi.vehicleDriving().currDistanceInRoad());
//                System.out.println("已行驶总里程=" + vehi.vehicleDriving().getVehiDrivDistance());
//                System.out.println("当前分段已行驶距离=" + vehi.vehicleDriving().currDistanceInSegment());
//                System.out.println("当前时间段移动距离=" + vehi.vehicleDriving().currDistance());
//                if (vehi.roadId() == 1) {
//                    IDecisionPoint decisionPoint_link1 = null;
//                    for (IDecisionPoint decisionPoint : decisionPoints_lst) {
//                        if (decisionPoint.link().id() == 1) {
//                            decisionPoint_link1 = decisionPoint;
//                            break;
//                        }
//                    }
//
//                    List<IRouting> decisionPoint_link1_routings_lst = null;
//                    if (decisionPoint_link1 != null) {
//                        decisionPoint_link1_routings_lst = decisionPoint_link1.routings();
//                    }
//
//                    if (decisionPoint_link1_routings_lst != null && !decisionPoint_link1_routings_lst.isEmpty()) {
//                        IRouting lastRouting = decisionPoint_link1_routings_lst.get(decisionPoint_link1_routings_lst.size() - 1);
//                        if (!vehi.routing().equals(lastRouting)) {
//                            if (vehi.vehicleDriving().setRouting(lastRouting)) {
//                                System.out.println(vehi.id() + "车辆修改路径成功。");
//                            }
//                        }
//                    }
//                }
//                System.out.println("当前在分段上已行驶距离=" + vehi.vehicleDriving().currDistanceInSegment());
//                System.out.println("变轨点集=" + vehi.vehicleDriving().changingTrace());
//                System.out.println("变轨长度=" + vehi.vehicleDriving().changingTraceLength());
//                System.out.println("在车道或车道连接上到起点距离=" + vehi.vehicleDriving().distToStartPoint());
//                System.out.println("在车道或“车道连接”上车辆到终点距离=" + vehi.vehicleDriving().distToEndpoint());

//                IVehicle vehicle = simuiface.getVehicle(100001);
//                ILaneConnector laneConnector = netIFace.findLaneConnector(20296);
//                System.out.println("车辆是否在目标车道连接上=" + laneConnector.id());
//                if (vehi.vehicleDriving().moveToLaneConnector(laneConnector, 100)) {
//                    System.out.println("车辆成功移动到目标车道连接");
//                }

//                List<IVehicle> allStartedVehicles = simuiface.allVehiStarted();
//                // 遍历所有已启动的车辆
//                for (int vehicleIndex = 0; vehicleIndex < allStartedVehicles.size(); vehicleIndex++) {
//                    IVehicle currentVehicle = allStartedVehicles.get(vehicleIndex);
//                    System.out.println(vehicleIndex + " " + currentVehicle.id());
//                    // 筛选出L5路段上的车辆（roadId为5）
//                    if (currentVehicle.roadId() == 13715) {
//                        // 查找目标Link（id为9）
//                        ILink targetLink = netIFace.findLink(13714);
//                        List<ILaneObject> targetLaneObjects = targetLink.laneObjects();
//                        // 计算目标车道和距离
//                        int targetLaneIndex = vehicleIndex % targetLaneObjects.size();
//                        ILaneObject targetLane = targetLaneObjects.get(targetLaneIndex);
//                        double distanceOnLane = vehicleIndex % 100;
//                        // 执行车辆位置移动
//                        if (currentVehicle.vehicleDriving().move(targetLane, distanceOnLane)) {
//                            System.out.println(currentVehicle.id() + "车辆移动成功。");
//                        }
//                    }
//                }


                // 调用config()方法获取配置信息（返回值为Map，对应Python的Dict）
//                JsonObject configMap = iface.config();
//                iface.setConfigProperty("__httpserverport",8080);
//                JsonObject configMap = iface.config();
//                System.out.println("配置中的maxSpeed: " + configMap);
                // 获取TESSNG主接口
                // 通过netInterface()获取路网控制接口
//                NetInterface netIface = iface.netInterface();
//                System.out.println("配置中的maxSpeed: " + netIface);
//                GuiInterface guiIface = iface.guiInterface();
//                System.out.println("用于控制仿真过程的接口SimuInterface: " + guiIface);


                // 获取 ID 为 6 的 Connector
//                IConnector connector = netIFace.findConnector(11708);
//
//                if (connector != null) {
//                    // 获取该 Connector 的所有车道连接（ILaneConnector）
//                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
//
//                    if (laneConnectors != null && !laneConnectors.isEmpty()) {
//                        for (ILaneConnector laneConnector1 : laneConnectors) {
//                            // 获取当前车道连接的交叉点列表
//                            ArrayList<CrossPoint> crossPoints = netIFace.crossPoints(laneConnector1);
//
//                            if (crossPoints != null && !crossPoints.isEmpty()) {
//                                for (CrossPoint crossPoint : crossPoints) {
//                                    // 获取主车道连接（即被交叉的车道连接）
//                                    ILaneConnector mainLaneConnector = crossPoint.getMpLaneConnector();
//                                    // 获取交叉点坐标
//                                    Point crossPointCoord = crossPoint.getMCrossPoint();
//
//                                    // 输出信息
//                                    System.out.printf("主车道连接, 即被交叉的“车道连接”: %d%n", mainLaneConnector.id());
//                                    System.out.printf("交叉点坐标为: (%.2f, %.2f)%n",
//                                            crossPointCoord.getX(), crossPointCoord.getY());
//                                }
//                            }
//                        }
//                    }
//                }

//
//                ArrayList<Point> centerPoints1 = new ArrayList<>();
//                Point startPoint1 = new Point(6, 300); // 起点
//                Point endPoint1 = new Point(6, 25);     // 终点
//                centerPoints1.add(startPoint1);
//                centerPoints1.add(endPoint1);
//// 设置每条车道的宽度（从左到右）
//                ArrayList<Double> laneWidths = new ArrayList<>();
//                laneWidths.add(3.5);
//                laneWidths.add(3.0);
//                laneWidths.add(3.0);
//                String linkName = "南进口creatlinkWithLaneWidth"; // 路段名称
//                boolean bAddToScene = true;                       // 是否添加到场景
//                ILink s_approach = netIFace.createLinkWithLaneWidth(centerPoints1, laneWidths, linkName, bAddToScene);

        }}

        isTestExecuted = true; // 标记为已执行
    }

    @Override
    public void afterStep(IVehicle pIVehicle) {
        //System.out.println("vehiid:" + pIVehicle.id());
    }

    @Override
    public boolean isStopDriving(IVehicle pIVehicle) {
        return false;
    }

    @Override
    public boolean ref_reCalcAngle(IVehicle pIVehicle, ObjReal ref_outAngle){
        double angle = ref_outAngle.getValue();
        ref_outAngle.setValue(45);
        return false;
    }

//    @Override
//    public ArrayList<DispatchInterval> calcDynaDispatchParameters() {
//        // 获取 TESSNG 顶层接口
//        TessInterface iface = TESSNG.tessngIFace();
//        SimuInterface simuInterface = iface.simuInterface();
//
//        // 获取当前仿真时间（单位：毫秒）
//        long currSimuTime = simuInterface.simuTimeIntervalWithAcceMutiples();
//
//        // 每10秒执行一次，且仿真时间小于60秒
//        if (currSimuTime % (10 * 1000) == 0 && currSimuTime < 60 * 1000) {
//            // 获取 ID 等于 5 的路段上的车辆
//            List<IVehicle> lVehi = simuInterface.vehisInLink(5);
//
//            if (currSimuTime < 1000 * 30 || lVehi.size() > 0) {
//                return new ArrayList<>();
//            } else {
//                // 获取当前时间
//                LocalDateTime now = LocalDateTime.now();
//                // 当前时间换算为秒
//                int currSecs = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
//
//                // 创建发车间隔对象
//                DispatchInterval di = new DispatchInterval();
//                // 动作控制案例 - 机动车交叉口 L5 路段发车点 ID 为 11
//                di.setDispatchId(11);
//                di.setFromTime(currSecs);
//                di.setToTime(currSecs + 300 - 1);
//                di.setVehiCount(300);
//
//                // 设置车辆组成详情
//                List<VehiComposition> vehiConsDetail = new ArrayList<>();
//                vehiConsDetail.add(new VehiComposition(1, 60)); // 小客车 60%
//                vehiConsDetail.add(new VehiComposition(2, 40)); // 大客车 40%
//                di.setMlVehicleConsDetail(vehiConsDetail);
//
//                System.out.println("流量修改完成，当前时间为" + currSimuTime);
//
//                ArrayList<DispatchInterval> result = new ArrayList<>();
//                result.add(di);
//                return result;
//            }
//        }
//
//        return new ArrayList<>();
//    }

    // 自由左变道前预处理
    public void ref_beforeToLeftFreely(IVehicle pIVehicle, ObjBool ref_keepOn) {
        pIVehicle.setColor("#0000FF"); // 设置为蓝色
        ref_keepOn.setValue(true);
        System.out.println("ref_beforeToLeftFreely 被调用，车辆ID：" + pIVehicle.id());
    }

    // 自由右变道前预处理
//    public void ref_beforeToRightFreely(IVehicle pIVehicle, ObjBool ref_keepOn) {
//        if (pIVehicle.roadId() == 20268) {
//            pIVehicle.setColor("#EE0000"); // 红色
//        }
//    }
    // 重写父类方法：重新计算期望速度（带单位）
//    public boolean ref_reCalcdesirSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutDesirSpeed, objUnitOfMeasure ref_unit) {
//        // 设置单位为米（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 打印调试信息
//        System.out.println("test: " + ref_inOutDesirSpeed.getValue() + ", " + pIVehicle.currSpeed() + ", " + pIVehicle.currSpeed(UnitOfMeasure.Metric));
//        // 设置期望速度为 20 m/s
//        ref_inOutDesirSpeed.setValue(30);
//        // 返回 true 表示采用修改后的值
//        return true;
//    }

    // 重写父类方法：计算车道限速（带单位）
//    public boolean ref_calcSpeedLimitByLane_unit(ILink pILink, int laneNumber, ObjReal ref_outSpeed, objUnitOfMeasure ref_unit) {
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置车道限速为 10 m/s
//        ref_outSpeed.setValue(10);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }


    // 重写父类方法：计算车辆最大限速（带单位）
//    public boolean ref_calcMaxLimitedSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutLimitedSpeed, objUnitOfMeasure ref_unit){
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置车辆最大限速为 10 m/s
//        ref_inOutLimitedSpeed.setValue(10);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

//    public boolean ref_calcDistToEventObj_unit(IVehicle pIVehicle, ObjReal ref_dist, objUnitOfMeasure ref_unit) {
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置距离为 10 米
//        ref_dist.setValue(20);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

//    public boolean ref_calcChangeLaneSafeDist_unit(IVehicle pIVehicle, ObjReal ref_dist, objUnitOfMeasure ref_unit){
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置变道安全距离为 10 米
//        ref_dist.setValue(50);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

//    public boolean ref_calcAcce_unit(IVehicle pIVehicle, ObjReal ref_acce, objUnitOfMeasure ref_unit) {
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置车辆加速度为 2.0 m/s²
//        ref_acce.setValue(2);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

//    public boolean ref_reSetAcce_unit(IVehicle pIVehicle, ObjReal ref_inOutAcce, objUnitOfMeasure ref_unit) {
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置车辆加速度为 2.0 m/s²
//        ref_inOutAcce.setValue(2);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

    // 过载的父类方法，重新计算期望速度
// vehi：车辆
// ref_desirSpeed：返回结果,ref_desirSpeed.value是TESS NG计算好的期望速度，可以在此方法改变它
// return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
//    public boolean ref_reCalcdesirSpeed(IVehicle vehi, ObjReal ref_desirSpeed) {
//        long tmpId = vehi.id() % 100000;
//        String roadName = vehi.roadName();
//        if ("曹安公路".equals(roadName)) {
//            if (tmpId <= this.mrSquareVehiCount) {
//                TessInterface iface = TESSNG.tessngIFace();
//                SimuInterface simuIFace = iface.simuInterface();
//                long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
//                if (simuTime < 5 * 1000) {
//                    ref_desirSpeed.setValue(0);
//                } else if (simuTime < 10 * 1000) {
//                    ref_desirSpeed.setValue(TESSNG.m2p(20 / 3.6));
//                } else {
//                    ref_desirSpeed.setValue(TESSNG.m2p(80 / 3.6));
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    // 过载的父类方法，重新计算跟驰参数：时距及安全距离
    // vehi:车辆
    // ref_inOutSi，安全时距，ref_inOutSi.value是TESS NG已计算好的值，此方法可以改变它
    // ref_inOutSd，安全距离，ref_inOutSd.value是TESS NG已计算好的值，此方法可以改变它
    // return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
//    public boolean ref_reSetFollowingParam(IVehicle vehi, ObjReal ref_inOutSi, ObjReal ref_inOutSd) {
//        String roadName = vehi.roadName();
//        if ("次干道".equals(roadName)) {
//            ref_inOutSd.setValue(TESSNG.m2p(30));
//            return true;
//        }
//        return false;
//    }
    // 重写父类方法：重新设置跟驰参数（带单位）
//    public boolean ref_reSetFollowingParam_unit(IVehicle pIVehicle, ObjReal ref_inOutSafeInterval, ObjReal ref_inOutSafeDistance, objUnitOfMeasure ref_unit) {
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//
//        // 设置安全距离为 10 米
//        ref_inOutSafeDistance.setValue(30);
//
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

//    public boolean ref_reSetDistanceFront_unit(IVehicle pIVehicle, ObjReal ref_distance, ObjReal ref_s0, objUnitOfMeasure ref_unit){
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置前车间距为 10 米
//        ref_distance.setValue(10.0f);
//        // 设置 s0（静止时最小间距）为 10 米
//        ref_s0.setValue(10);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

    // 过载的父类方法，重新计算加速度
    // vehi：车辆
    // inOutAce：加速度，inOutAcce.value是TESS NG已计算的车辆加速度，此方法可以改变它
    // return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
//    public boolean ref_reSetAcce(IVehicle vehi, ObjReal inOutAcce) {
//        String roadName = vehi.roadName();
//        if ("曹安公路".equals(roadName)) {
//            if (vehi.currSpeed() > TESSNG.m2p(20 / 3.6)) {
//                inOutAcce.setValue(TESSNG.m2p(-5));
//                return true;
//            }
//        }
//        return false;
//    }

    // 过载的父类方法，重新计算当前速度
    // vehi:车辆
    // ref_inOutSpeed，速度ref_inOutSpeed.value，是已计算好的车辆速度，此方法可以改变它
    // return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
//    public boolean ref_reSetSpeed(IVehicle vehi, ObjReal ref_inOutSpeed) {
//        long tmpId = vehi.id() % 100000;
//        String roadName = vehi.roadName();
//        if ("曹安公路".equals(roadName)) {
//            if (tmpId == 1) {
//                this.mrSpeedOfPlane = vehi.currSpeed();
//            } else if (tmpId >= 2 && tmpId <= this.mrSquareVehiCount) {
//                ref_inOutSpeed.setValue(this.mrSpeedOfPlane);
//            }
//            return true;
//        }
//        return false;
//    }

//    public boolean ref_reSetSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutSpeed, objUnitOfMeasure ref_unit){
//        // 设置单位为米制（Metric）
//        ref_unit.setValue(UnitOfMeasure.Metric);
//        // 设置车辆速度为 20 m/s
//        ref_inOutSpeed.setValue(20);
//        // 返回 true 表示 TESS NG 应采用修改后的值
//        return true;
//    }

    // 过载的父类方法，计算是否要左自由变道
    // vehi:车辆
    // return结果，True：变道、False：不变道
//    public boolean reCalcToLeftFreely(IVehicle vehi) {
//        // 车辆到路段终点距离小于20米不变道
//        if (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < TESSNG.m2p(20)) {
//            return false;
//        }
//        long tmpId = vehi.id() % 100000;
//        String roadName = vehi.roadName();
//        if ("曹安公路".equals(roadName)) {
//            if (tmpId >= 23 && tmpId <= 28) {
//                int laneNumber = vehi.vehicleDriving().laneNumber();
//                if (laneNumber == 1 || laneNumber == 4) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    // 过载的父类方法，计算是否要右自由变道
    // vehi:车辆
    // return结果，True：变道、False：不变道
//    public boolean reCalcToRightFreely(IVehicle vehi) {
//        long tmpId = vehi.id() % 100000;
//        // 车辆到路段终点距离小于20米不变道
//        if (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < TESSNG.m2p(20)) {
//            return false;
//        }
//        String roadName = vehi.roadName();
//        if ("曹安公路".equals(roadName)) {
//            if (tmpId >= 23 && tmpId <= 28) {
//                int laneNumber = vehi.vehicleDriving().laneNumber();
//                if (laneNumber == 2 || laneNumber == 5) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public void afterStop() {
        System.out.println("after stop");
    }


}
