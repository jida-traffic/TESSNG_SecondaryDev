package TESS_Java_APIDemo.SecondaryDevCasesMaster;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jidatraffic.tessng.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.jidatraffic.tessng.TESSNG.m2p;
import static com.jidatraffic.tessng.TESSNG.tessngIFace;

public class SecondaryDevelopmentCases {
    private int id;
    public SecondaryDevelopmentCases(int id){
        this.id=id;
    }
    // 信控编辑案例
    public void editSignalController() {
        // 获取TESSNG顶层接口
        TessInterface iface = tessngIFace();
        // 获取仿真子接口
        SimuInterface simuiface = iface.simuInterface();
        // 获取路网子接口
        NetInterface netiface = iface.netInterface();

        // 创建两条新路段和一条连接段作为示例
        Point startPoint1 = new Point(m2p(-300), m2p(-200));
        Point endPoint1 = new Point(m2p(-50), m2p(-200));
        ArrayList<Point> lPoint1 = new ArrayList<>();
        lPoint1.add(startPoint1);
        lPoint1.add(endPoint1);
        ILink link1 = netiface.createLink(lPoint1, 3, "信控编辑路段1");

        Point startPoint2 = new Point(m2p(50), m2p(-200));
        Point endPoint2 = new Point(m2p(300), m2p(-200));
        ArrayList<Point> lPoint2 = new ArrayList<>();
        lPoint2.add(startPoint2);
        lPoint2.add(endPoint2);
        ILink link2 = netiface.createLink( lPoint2, 3, "信控编辑路段2");

        // 连接段车道连接列表
        List<ILaneObject> lLaneObjects = new ArrayList<>();
        if (link1 != null && link2 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
            lFromLaneNumber.add(1);
            lFromLaneNumber.add(2);
            lFromLaneNumber.add(3);

            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
            lToLaneNumber.add(1);
            lToLaneNumber.add(2);
            lToLaneNumber.add(3);

            IConnector connector = netiface.createConnector(
                    link1.id(), link2.id(), lFromLaneNumber, lToLaneNumber, "信控编辑连接段", true);

            if (connector != null) {
                lLaneObjects = connector.laneObjects();
                for (ILaneObject laneObj : lLaneObjects) {
                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
                            " 下游车道ID: " + laneObj.toLaneObject().id());
                }
            }
        }

        // 创建发车点
        if (link1 != null) {
            IDispatchPoint dp = netiface.createDispatchPoint(link1);
            if (dp != null) {
                dp.addDispatchInterval(1, 3600, 3600);
            }
        }
        ISignalController signalController = netiface.createSignalController("信控1");
        //TODO
        // 创建信号灯组
        ISignalPlan signalPlan = netiface.createSignalPlan(signalController,"信号灯组1", 60,80, 1, 3600);

        // 创建相位,40秒绿灯，黄灯3秒，全红3秒
        ArrayList<ColorInterval> colorIntervals = new ArrayList<>();
        colorIntervals.add(new ColorInterval("G", 40));  // 绿灯
        colorIntervals.add(new ColorInterval("Y", 3));   // 黄灯
        colorIntervals.add(new ColorInterval("R", 3));   // 红灯

        ISignalPhase signalPhase = netiface.createSignalPlanSignalPhase(signalPlan, "信号灯组1相位1", colorIntervals);

        // 创建信号灯
        for (int index = 0; index < lLaneObjects.size(); index++) {
            ILaneObject laneObj = lLaneObjects.get(index);
            netiface.createSignalLamp(
                    signalPhase,
                    "信号灯" + (index + 1),
                    laneObj.fromLaneObject().id(),
                    laneObj.toLaneObject().id(),
                    m2p(2.0)
            );
        }
    }

    // 双环信控方案下发
//    public void doubleRingSignalControl(long currentSimuTime) {
//        // 获取TESS NG接口
//        TessInterface iface = tessngIFace();
//        // 获取路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        // 读取方案数据
//        Gson gson = new Gson();
//        try (FileReader reader = new FileReader("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\others\\SecondaryDevCasesMaster\\Data\\Signal_Plan_Data_1109.json")) {
//            // 解析JSON数据
//            Map<String, Map<String, Object>> signalPlansDict =
//                    gson.fromJson(reader, new TypeToken<Map<String, Map<String, Object>>>(){}.getType());
//
//            // 创建信号灯组和相位
//            for (Map.Entry<String, Map<String, Object>> groupEntry : signalPlansDict.entrySet()) {
//                String planName = groupEntry.getKey();
//                Map<String, Object> groupData = groupEntry.getValue();
//
//                // 查找当前灯组
//                ISignalPlan currentSignalPlan = null;
//                ArrayList<ISignalPlan> allSignalPlans = netiface.signalPlans();
//                for (ISignalPlan signalPlan : allSignalPlans) {
//                    if (signalPlan.name().equals(planName)) {
//                        currentSignalPlan = signalPlan;
//                        break;
//                    }
//                }
//
//                if (currentSignalPlan == null) {
//                    System.out.println("FindError: The signalGroup not in current net.");
//                    break;
//                }
//
//                // 获取当前灯组的所有相位
//                List<ISignalPhase> currentSignalGroupPhases = currentSignalPlan.phases();
//
//                // 获取所有灯组的起始时间
//                Set<String> signalPlanstartTimeSet = groupData.keySet();
//                List<String> signalPlanstartTimeList = new ArrayList<>(signalPlanstartTimeSet);
//
//                // 处理每个时间段的信号方案
//                for (int index = 0; index < signalPlanstartTimeList.size(); index++) {
//                    String startTimeStr = signalPlanstartTimeList.get(index);
//                    String endTimeStr = (index != signalPlanstartTimeList.size() - 1)
//                            ? signalPlanstartTimeList.get(index + 1)
//                            : "24:00";
//
//                    // 转换时间为秒
//                    long startTimeSeconds = functions.timeToSeconds(startTimeStr);
//                    long endTimeSeconds = functions.timeToSeconds(endTimeStr);
//
//                    // 检查当前仿真时间是否在该时段内
//                    if (startTimeSeconds <= currentSimuTime && currentSimuTime < endTimeSeconds) {
//                        Map<String, Object> timeSegmentData = (Map<String, Object>) groupData.get(startTimeStr);
//                        int periodTime = ((Number) timeSegmentData.get("cycle_time")).intValue();
//                        List<Map<String, Object>> phases = (List<Map<String, Object>>) timeSegmentData.get("phases");
//
//                        // 修改周期时间
//                        currentSignalPlan.setCycleTime(periodTime);
//
//                        // 处理每个相位
//                        for (Map<String, Object> phase : phases) {
//                            String phaseName = (String) phase.get("phase_name");
//                            int phaseNumber = ((Number) phase.get("phase_number")).intValue();
//
//                            // 创建灯色序列
//                            List<ColorInterval> colorList = new ArrayList<>();
//                            colorList.add(new ColorInterval("红", ((Number) phase.get("start_time")).intValue()));
//                            colorList.add(new ColorInterval("绿", ((Number) phase.get("green_time")).intValue()));
//                            colorList.add(new ColorInterval("黄", 3));
//
//                            // 计算剩余红灯时间
//                            int remainingRedTime = periodTime
//                                    - ((Number) phase.get("start_time")).intValue()
//                                    - ((Number) phase.get("green_time")).intValue()
//                                    - 3;
//
//                            if (remainingRedTime > 0) {
//                                colorList.add(new ColorInterval("红", remainingRedTime));
//                            }
//
//                            // 查找当前相位
//                            ISignalPhase currentPhase = null;
//                            for (ISignalPhase signalPhase : currentSignalGroupPhases) {
//                                if (phaseNumber == Integer.parseInt(signalPhase.number())) {
//                                    currentPhase = signalPhase;
//                                    break;
//                                }
//                            }
//
//                            // 修改或创建相位
//                            if (currentPhase != null) {
//                                currentPhase.setColorList((ArrayList<ColorInterval>) colorList);
//                            } else {
//                                ISignalPhase newPhase = netiface.createSignalPlanSignalPhase(currentSignalPlan,phaseName, (ArrayList<ColorInterval>) colorList) ;
//                                newPhase.setNumber(String.valueOf(phaseNumber));
//                                currentSignalGroupPhases.add(newPhase);
//                            }
//
//                            // 设置相位包含的信号灯
//                            List<Integer> lampIdList = (List<Integer>) phase.get("lamp_lst");
//                            for (int lampId : lampIdList) {
//                                ISignalLamp lamp = netiface.findSignalLamp(lampId);
//                                if (lamp != null) {
//                                    lamp.setPhaseNumber(Integer.parseInt(String.valueOf(phaseNumber)));
//                                } else {
//                                    System.out.println("FindError:未查找到信号灯: " + lampId);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void doubleRingSignalControlTest(int planNumber){
//        // 读取方案数据
//        Gson gson = new Gson();
//        try (FileReader reader = new FileReader("./Data/Signal_Plan_Data_1109.json")) {
//            // 解析JSON数据为Map
//            Map<String, Map<String, Object>> signalGroupsDict =
//                    gson.fromJson(reader, new TypeToken<Map<String, Map<String, Object>>>(){}.getType());
//
//            // 所有灯组的起始时间列表
//            List<Long> signalGroupsStartTimeList = new ArrayList<>();
//            for (Map<String, Object> group : signalGroupsDict.values()) {
//                for (String startTime : group.keySet()) {
//                    // 转换时间为秒并添加到列表
//                    signalGroupsStartTimeList.add(functions.timeToSeconds(startTime));
//                }
//            }
//
//            // 计算当前方案序号（取模防止越界）
//            int currentPlanNumber = planNumber % signalGroupsStartTimeList.size();
//            long currentStartTime = signalGroupsStartTimeList.get(currentPlanNumber);
//            System.out.println(currentStartTime + ":双环信控方案更改。");
//
//            // 执行双环信控方案下发
//            this.doubleRingSignalControl(currentStartTime);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 流量加载方法
     */
    public void trafficLoading() {
        // 获取TESS NG顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        // 获取路网子接口
        NetInterface netiface = iface.netInterface();
        // 获取仿真子接口
        SimuInterface simuiface = iface.simuInterface();

        /* 1. 新建发车点 */
        // 创建两条新路段和一条连接段作为示例
        Point startPoint1 = new Point(m2p(-300), m2p(-180));
        Point endPoint1 = new Point(m2p(-50), m2p(-180));
        ArrayList<Point> lPoint1 = new ArrayList<>();
        lPoint1.add(startPoint1);
        lPoint1.add(endPoint1);
        ILink link1 = netiface.createLink(lPoint1, 3, "流量加载路段1");

        Point startPoint2 = new Point(m2p(50), m2p(-180));
        Point endPoint2 = new Point(m2p(300), m2p(-180));
        ArrayList<Point> lPoint2 = new ArrayList<>();
        lPoint2.add(startPoint2);
        lPoint2.add(endPoint2);
        ILink link2 = netiface.createLink(lPoint2, 3, "流量加载路段2");

        // 连接段车道连接列表
        ArrayList<ILaneObject> lLaneObjects = new ArrayList<>();
        if (link1 != null && link2 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
            lFromLaneNumber.add(1);
            lFromLaneNumber.add(2);
            lFromLaneNumber.add(3);

            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
            lToLaneNumber.add(1);
            lToLaneNumber.add(2);
            lToLaneNumber.add(3);

            IConnector connector = netiface.createConnector(
                    link1.id(), link2.id(), lFromLaneNumber, lToLaneNumber,
                    "流量加载连接段", true
            );

            if (connector != null) {
                lLaneObjects = connector.laneObjects();
                for (ILaneObject laneObj : lLaneObjects) {
                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
                            " 下游车道ID: " + laneObj.toLaneObject().id());
                }
            }

            // 创建车辆组成及指定车辆类型
            ArrayList<VehiComposition> vehiTypeProportionList = new ArrayList<>();
            // 车型组成：小客车0.3，大客车0.2，公交车0.1，货车0.4
            VehiComposition vc1 = new VehiComposition(1,0.3);
            vehiTypeProportionList.add(vc1);

            VehiComposition vc2 = new VehiComposition(2,0.2);
            vehiTypeProportionList.add(vc2);

            VehiComposition vc3 = new VehiComposition(3,0.1);
            vehiTypeProportionList.add(vc3);

            VehiComposition vc4 = new VehiComposition(4,0.4);
            vehiTypeProportionList.add(vc4);

            long vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiTypeProportionList);
            if (vehiCompositionID != -1) {
                System.out.println("车型组成创建成功，id为：" + vehiCompositionID);
                // 新建发车点，车型组成ID为动态创建的，600秒发300辆车
                if (link1 != null) {
                    IDispatchPoint dp = netiface.createDispatchPoint(link1);
                    if (dp != null) {
                        dp.addDispatchInterval(vehiCompositionID, 600, 300);
                    }
                }
            }
        }

        /* 2. 动态发车 */
        // 创建两条新路段和一条连接段作为示例
        Point startPoint3 = new Point(m2p(-300), m2p(-160));
        Point endPoint3 = new Point(m2p(-50), m2p(-160));
        List<Point> lPoint3 = new ArrayList<>();
        lPoint3.add(startPoint3);
        lPoint3.add(endPoint3);
        ILink link3 = netiface.createLink((ArrayList<Point>) lPoint3, 3, "动态加载车辆段");

        Point startPoint4 = new Point(m2p(50), m2p(-160));
        Point endPoint4 = new Point(m2p(300), m2p(-160));
        List<Point> lPoint4 = new ArrayList<>();
        lPoint4.add(startPoint4);
        lPoint4.add(endPoint4);
        ILink link4 = netiface.createLink((ArrayList<Point>) lPoint4, 3, "动态加载车辆段");

        // 连接段车道连接列表（重置为新连接段的车道对象）
        lLaneObjects.clear();
        if (link3 != null && link4 != null) {
            List<Integer> lFromLaneNumber = new ArrayList<>();
            lFromLaneNumber.add(1);
            lFromLaneNumber.add(2);
            lFromLaneNumber.add(3);

            List<Integer> lToLaneNumber = new ArrayList<>();
            lToLaneNumber.add(1);
            lToLaneNumber.add(2);
            lToLaneNumber.add(3);

            IConnector connector = netiface.createConnector(
                    link3.id(), link4.id(), (ArrayList<Integer>) lFromLaneNumber, (ArrayList<Integer>) lToLaneNumber,
                    "动态加载加载连接段", true
            );

            if (connector != null) {
                lLaneObjects = connector.laneObjects();
                for (ILaneObject laneObj : lLaneObjects) {
                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
                            " 下游车道ID: " + laneObj.toLaneObject().id());
                }
            }
        }

        // 在指定车道和位置动态加载车辆（示例：在0,1,2车道不同位置动态加载车辆）
        DynaVehiParam dvpLane0 = new DynaVehiParam();
        DynaVehiParam dvpLane1 = new DynaVehiParam();
        DynaVehiParam dvpLane2 = new DynaVehiParam();

        // 车道0车辆参数
        dvpLane0.setVehiTypeCode(1);
        dvpLane0.setRoadId(link3.id());
        dvpLane0.setLaneNumber(0);
        dvpLane0.setDist(m2p(50));
        dvpLane0.setSpeed(20);
        dvpLane0.setColor("#FF0000");

        // 车道1车辆参数
        dvpLane1.setVehiTypeCode(2);
        dvpLane1.setRoadId(link3.id());
        dvpLane1.setLaneNumber(1);
        dvpLane1.setDist(m2p(100));
        dvpLane1.setSpeed(30);
        dvpLane1.setColor("#008000");

        // 车道2车辆参数
        dvpLane2.setVehiTypeCode(3);
        dvpLane2.setRoadId(link4.id());
        dvpLane2.setLaneNumber(2);
        dvpLane2.setDist(m2p(50));
        dvpLane2.setSpeed(40);
        dvpLane2.setColor("#0000FF");

        // 创建车辆
        IVehicle vehiLane0 = simuiface.createGVehicle(dvpLane0);
        IVehicle vehiLane1 = simuiface.createGVehicle(dvpLane1);
        IVehicle vehiLane2 = simuiface.createGVehicle(dvpLane2);
    }

    /**
     * 路径加载方法
     */
    public void flowLoading() {
        // 获取TESS NG顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        // 获取路网子接口
        NetInterface netiface = iface.netInterface();
        // 获取仿真子接口
        SimuInterface simuiface = iface.simuInterface();

        // 以标准四岔路口为例 (L3-C2-L10)
        ILink link3 = netiface.findLink(3);
        ILink link10 = netiface.findLink(10);
        ILink link6 = netiface.findLink(6);
        ILink link7 = netiface.findLink(7);
        ILink link8 = netiface.findLink(8);

        // 新建发车点
        if (link3 != null) {
            IDispatchPoint dp = netiface.createDispatchPoint(link3);
            if (dp != null) {
                dp.addDispatchInterval(1, 1800, 900);
            }
        }

        // 创建决策点（距离路段起点30米处）
        IDecisionPoint decisionPoint = netiface.createDecisionPoint(link3, m2p(30));

        // 创建路径（左、直、右）
        List<ILink> routingLinks1 = new ArrayList<>();
        routingLinks1.add(link3);
        routingLinks1.add(link10);
        routingLinks1.add(link6);
        IRouting decisionRouting1 = netiface.createDeciRouting(decisionPoint, (ArrayList<ILink>) routingLinks1);

        List<ILink> routingLinks2 = new ArrayList<>();
        routingLinks2.add(link3);
        routingLinks2.add(link10);
        routingLinks2.add(link8);
        IRouting decisionRouting2 = netiface.createDeciRouting(decisionPoint, (ArrayList<ILink>) routingLinks2);

        List<ILink> routingLinks3 = new ArrayList<>();
        routingLinks3.add(link3);
        routingLinks3.add(link10);
        routingLinks3.add(link7);
        IRouting decisionRouting3 = netiface.createDeciRouting(decisionPoint, (ArrayList<ILink>) routingLinks3);

        // 分配左、直、右流量比
        RoutingFlowRatio flowRatioLeft = new RoutingFlowRatio(1, 2.0);
        flowRatioLeft.setRoutingID(decisionRouting1.id());
        flowRatioLeft.setRatio(2.0);

        RoutingFlowRatio flowRatioStraight = new RoutingFlowRatio(2, 3.0);
        flowRatioStraight.setRoutingID(decisionRouting2.id());
        flowRatioStraight.setRatio(3.0);

        RoutingFlowRatio flowRatioRight = new RoutingFlowRatio(3, 1.0);
        flowRatioRight.setRoutingID(decisionRouting3.id());
        flowRatioRight.setRatio(1.0);

        // 构建决策点数据
        _DecisionPoint decisionPointData = new _DecisionPoint();
        decisionPointData.setDeciPointID(decisionPoint.id());
        decisionPointData.setDeciPointName(decisionPoint.name());

        // 获取决策点位置坐标
        Point decisionPointPos = new Point();
        if (decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos)) {
            decisionPointData.setX(decisionPointPos.getX());
            decisionPointData.setY(decisionPointPos.getY());
            decisionPointData.setZ(decisionPoint.link().z());
        }

        // 更新决策点及其各路径不同时间段流量比
        List<RoutingFlowRatio> flowRatios = new ArrayList<>();
        flowRatios.add(flowRatioLeft);
        flowRatios.add(flowRatioStraight);
        flowRatios.add(flowRatioRight);

        IDecisionPoint updatedDecisionPoint = netiface.updateDecipointPoint(decisionPointData);
        if (updatedDecisionPoint != null) {
            System.out.println("决策点创建成功。");
            // 删除右转路径
            boolean isRemoved = netiface.removeDeciRouting(decisionPoint, decisionRouting3);
            if (isRemoved) {
                System.out.println("删除右转路径成功。");
            }
        }
    }

    /**
     * 路径断面流量加载
     * @param currentTime 当前仿真时间
     */
    public void flowLoadingSection(long currentTime) {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netiface = iface.netInterface();
        SimuInterface simuiface = iface.simuInterface();

        // 读取方案数据
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase_mml\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\SecondaryDevCasesMaster\\Data\\flow_ratio_quarter.json")) {
            // 解析JSON数据：key为路段ID字符串，value为季度流量比字典
            Map<String, Map<String, Map<String, Double>>> flowRatioQuarterDict =
                    gson.fromJson(reader, new TypeToken<Map<String, Map<String, Map<String, Double>>>>(){}.getType());

            // 遍历每个路段的流量比配置
            for (Map.Entry<String, Map<String, Map<String, Double>>> entry : flowRatioQuarterDict.entrySet()) {
                String linkIdStr = entry.getKey();
                int linkId = Integer.parseInt(linkIdStr);
                Map<String, Map<String, Double>> quarterRatios = entry.getValue();

                // 查找该路段上的决策点
                IDecisionPoint decisionPoint = null;
                List<IDecisionPoint> decisionPointsList = netiface.decisionPoints();
                for (IDecisionPoint dp : decisionPointsList) {
                    if (dp.link().id() == linkId) {
                        decisionPoint = dp;
                        break;
                    }
                }

                if (decisionPoint != null) {
                    // 获取所有季度起始时间
                    List<String> quarterStartTimeList = new ArrayList<>(quarterRatios.keySet());

                    // 遍历每个季度的流量比配置
                    for (int index = 0; index < quarterStartTimeList.size(); index++) {
                        String startTimeStr = quarterStartTimeList.get(index);
                        Map<String, Double> ratioData = quarterRatios.get(startTimeStr);

                        // 转换时间为秒
                        long quarterTimeSeconds = functions.timeToSeconds(startTimeStr);
                        long quarterTimeSecondsNext = (index != quarterStartTimeList.size() - 1)
                                ? functions.timeToSeconds(quarterStartTimeList.get(index + 1))
                                : quarterTimeSeconds + 1;

                        // 检查当前时间是否在该季度范围内
                        if (quarterTimeSeconds <= currentTime && currentTime < quarterTimeSecondsNext) {
                            // 获取决策点现有路径
                            List<IRouting> decisionRoutingsList = decisionPoint.routings();

                            // 验证路径数量是否为3（左、直、右）
                            if (decisionRoutingsList.size() == 3) {
                                // 配置左转弯流量比
                                RoutingFlowRatio flowRatioLeft = new RoutingFlowRatio(decisionRoutingsList.get(0).id(),ratioData.get("left"));
                                flowRatioLeft.setRoutingID(decisionRoutingsList.get(0).id());
                                flowRatioLeft.setRatio(ratioData.get("left"));

                                // 配置直行流量比
                                RoutingFlowRatio flowRatioStraight = new RoutingFlowRatio(decisionRoutingsList.get(1).id(),ratioData.get("straight"));
                                flowRatioStraight.setRoutingID(decisionRoutingsList.get(1).id());
                                flowRatioStraight.setRatio(ratioData.get("straight"));

                                // 配置右转弯流量比
                                RoutingFlowRatio flowRatioRight = new RoutingFlowRatio(decisionRoutingsList.get(2).id(),ratioData.get("right"));
                                flowRatioRight.setRoutingID(decisionRoutingsList.get(2).id());
                                flowRatioRight.setRatio(ratioData.get("right"));

                                // 构建决策点数据
                                _DecisionPoint decisionPointData = new _DecisionPoint();
                                decisionPointData.setDeciPointID(decisionPoint.id());
                                decisionPointData.setDeciPointName(decisionPoint.name());

                                // 获取决策点位置坐标
                                Point decisionPointPos = new Point();
                                if (decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos)) {
                                    decisionPointData.setX(decisionPointPos.getX());
                                    decisionPointData.setY(decisionPointPos.getY());
                                    decisionPointData.setZ(decisionPoint.link().z());
                                }

                                // 更新决策点流量比配置
                                List<RoutingFlowRatio> flowRatios = new ArrayList<>();
                                flowRatios.add(flowRatioLeft);
                                flowRatios.add(flowRatioStraight);
                                flowRatios.add(flowRatioRight);

                                IDecisionPoint updated = netiface.updateDecipointPoint(decisionPointData);
                                if (updated!=null) {
                                    System.out.println(startTimeStr + "流量更新成功。");
                                }
                            } else {
                                System.out.println("DecisionRoutingsError:决策点" + decisionPoint.id() + "需要包含左、直、右三条路径。");
                            }
                        }
                    }
                } else {
                    System.out.println("FindError:ID为" + linkId + "的路段不存在决策点");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动作控制方法
     * @param planNumber 方案序号
     */
    public void actionControl(int planNumber) {
        // 以动作控制案例-机动车交叉口路网的L5路段为例
        /*
         * 1. 修改发车流量信息，删除发车点
         *  - 修改发车流量信息需在MySimulator中的calcDynaDispatchParameters函数
         *  - 删除发车点位于afterOneStep函数中
         */

        /* 2. 修改决策路径的属性，删除决策路径
         *  - 见路径加载/路径管理模块
         */

        /* 3. 修改减速区，施工区，事故区信息；删除减速区，施工区，事故区
         *  - 减速区见MySimulator中的refReCalcdesirSpeed函数
         */

        /* 4. 车辆位置移动
         *  - 见afterOneStep函数
         */

        /* 5. 修改车辆速度
         *  - 同3减速区
         */

        /* 6. 修改车辆路径
         *  - 以L1路段上的路径为例，见afterOneStep
         */

        /* 7. 强制车辆不变道
         *  - 见MySimulator中的reCalcDismissChangeLane函数
         */

        /* 8. 强制车辆变道
         *  - MySimulator中的reCalcToLeftFreely和reCalcToRightFreely，return true即可
         */

        /* 9. 强制车辆闯红灯
         *  - 见MySimulator的refReSetSpeed函数
         */

        /* 10. 强制车辆停车
         *  - 见MySimulator的refReSetSpeed函数
         */

        /* 11. 强制清除车辆（车辆消失）
         *  - 以L5路段上的路径为例，见afterStep
         */

        /* 12. 修改车辆航向角
         *  - 以L5路段上的路径为例，见afterStep
         */

        /* 13. 修改车辆速度，加速度
         *  - 同5，修改加速度函数为MySimulator的refReSetAcce，用法与设置速度相同
         */

        /* 14. 车道关闭，恢复
         *  - 几种方法都可以实现：1.设置事件区。2.MySimulator中的自由变道，以L5路段50-100m处最右侧封闭30秒为例
         */

        functions.actionControlMethodNumber = planNumber;
        System.out.println(functions.actionControlMethodNumber);
    }

    /**
     * 创建施工区
     * 施工区和事故区的删除有两种方式：
     * 1. duration结束后自动删除
     * 2. 主动删除(removeRoadWorkZone)
     * 此处使用第一种方式
     */
    public void createWorkZone() {
        // 创建施工区参数对象
        DynaRoadWorkZoneParam workZone = new DynaRoadWorkZoneParam();

        // 道路ID（L5路段）
        workZone.setRoadId(5);
        // 施工区名称
        workZone.setName("施工区，限速40,持续20秒");
        // 位置：距离路段起点50米
        workZone.setLocation(50);
        // 施工区长度：50米
        workZone.setLength(50);
        // 限速：40千米/小时
        workZone.setLimitSpeed(40);
        // 持续时间：20秒
        workZone.setDuration(20);

        // 起始车道（最右侧车道，编号0）
        List<Integer> fromLaneNumbers = new ArrayList<>();
        fromLaneNumbers.add(0);
        workZone.setMlFromLaneNumber((ArrayList<Integer>) fromLaneNumbers);

        // 创建施工区
        TESSNG.tessngIFace().netInterface().createRoadWorkZone(workZone);
    }

    /**
     * 管控手段控制
     * @param methodNumber 调用的方法序号
     */
    public void controlMeasures(int methodNumber) {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        /* 1. 修改信号灯灯色
         *  - 见MySimulator的afterOneStep函数，L5路段信号灯第10秒红灯变绿灯，持续20秒。
         */

        /* 2. 修改信号灯组方案
         *  - 见双环管控方案下发。
         */

        /* 3. 修改相位绿灯时间长度
         *  - 除双环管控方案下所包含方法外，还有相位类自带的修改方法
         *  - 以L12路段相位直行信号灯相位为例（ID为7），由红90绿32黄3红25改为红10绿110黄3红28
         */
        if (methodNumber == 3) {
            ISignalPhase signalPhaseL127 = netiface.findSignalPhase(7);
            if (signalPhaseL127 != null) {
                List<ColorInterval> colorList = new ArrayList<>();
                colorList.add(new ColorInterval("红", 10));   // 红灯10秒
                colorList.add(new ColorInterval("绿", 110));  // 绿灯110秒
                colorList.add(new ColorInterval("黄", 3));    // 黄灯3秒
                colorList.add(new ColorInterval("红", 28));   // 红灯28秒
                signalPhaseL127.setColorList((ArrayList<ColorInterval>) colorList);
            }
        }

        /* 5. 修改link, connector 限速
         *  - 以L5路段最高限速由80调整至20，连接段无法修改限速。
         */
        if (methodNumber == 5) {
            ILink link5 = netiface.findLink(5);
            if (link5 != null) {
                link5.setLimitSpeed(20);  // 单位：千米/小时
            }
        }
    }

    public Object judgeVehicleLaneChangeDirection(IVehicle vehi) {
        ILane lane = vehi.lane();
        Point vehiCurrPos = vehi.pos();
        double vehiCurrDistToStart = lane.distToStartPoint(vehiCurrPos);
        List<Point> laneCenterBreakPoints = lane.centerBreakPoints();
        int vehiSegmentIndex = -1;

        // 获取车辆所在的道路分段号
        for (int index = 0; index < laneCenterBreakPoints.size(); index++) {
            Point centerBreakPoint = laneCenterBreakPoints.get(index);
            double breakPointDistToStart = lane.distToStartPoint(centerBreakPoint);
            if (vehiCurrDistToStart < breakPointDistToStart) {
                vehiSegmentIndex = index;
                break;
            }
        }

        if (vehiSegmentIndex > 0 && vehiSegmentIndex < laneCenterBreakPoints.size()) {
            Point startBreakPoint = laneCenterBreakPoints.get(vehiSegmentIndex - 1);
            Point endBreakPoint = laneCenterBreakPoints.get(vehiSegmentIndex);

            // 以点积判断车辆处于中心线左侧还是右侧
            String vehiDirection = functions.carPositionRoad(startBreakPoint, endBreakPoint, vehiCurrPos);

            // 判断车头角度偏度
            double breakLaneAngle = functions.calculateAngle(startBreakPoint, endBreakPoint);

            // 若车辆处于中心线右侧且车头右偏，则判定为右变道意图
            if ("right".equals(vehiDirection) && vehi.angle() > breakLaneAngle) {
                return "right";
            }
            // 若车辆处于中心线左侧且车头左偏，则判定为左变道意图
            else if ("left".equals(vehiDirection) && vehi.angle() < breakLaneAngle) {
                return "left";
            } else {
                return "noChange";
            }
        } else {
            System.out.println("FindError:can't find the segment,relevant info: "
                    + vehiSegmentIndex + ", " + vehiCurrDistToStart + ", " + vehiCurrPos);
            return "error";
        }
    }




        /**
         * 流程控制
         * @param methodNumber 调用的方法序号
         */
        public void processControl(double methodNumber) {
            // 获取TESS NG接口
            TessInterface iface = TESSNG.tessngIFace();
            SimuInterface simuiface = iface.simuInterface();
            NetInterface netiface = iface.netInterface();

            /* 1. 启动、暂停、恢复、停止仿真 */
            if (methodNumber == 1) {
                simuiface.startSimu();
            } else if (methodNumber == 2) {
                simuiface.pauseSimu();
            } else if (methodNumber == 3) {
                simuiface.stopSimu();
            } else if (methodNumber == 4) {
                simuiface.pauseSimuOrNot();
            }

            /* 8. 获取运动信息 */
            // 8.2 根据路段|车道获取车辆列表
            if (methodNumber == 8.2) {
                List<IVehicle> vehiOnRoad5List = simuiface.vehisInLink(5);
                List<IVehicle> vehiOnLane20List = simuiface.vehisInLane(20);

                System.out.println("L5路段车辆id：");
                for (IVehicle vehi : vehiOnRoad5List) {
                    System.out.println(vehi.id());
                }

                System.out.println("lane20车道车辆id：");
                for (IVehicle vehi : vehiOnLane20List) {
                    System.out.println(vehi.id());
                }
            }

            // 8.3 根据车辆id获取具体的车辆信息,以id为300001的车辆为例
            if (methodNumber == 8.3) {
                IVehicle vehi300001 = simuiface.getVehicle(300001);
                if (vehi300001 != null) {
                    System.out.println("300001车辆的具体信息：");
                    System.out.println("所在路段: " + vehi300001.roadId());
                    System.out.println("所在车道: " + vehi300001.lane().id());
                    System.out.println("当前车速: " + vehi300001.currSpeed());
                    System.out.println("当前加速度: " + vehi300001.acce());
                    System.out.println("当前角度: " + vehi300001.angle());
                    System.out.println("当前位置: " + vehi300001.pos());
                    System.out.println("其它: ......");
                }
            }

            /* 10. 设置仿真精度 */
            if (methodNumber == 10) {
                simuiface.setSimuAccuracy(10);
            }

            /* 11. 设置仿真开始结束时间
             * 可以设置仿真时长，无法设置仿真开始的时间，
             * 不过可以由定时器定时启动和结束仿真实现设置仿真开始结束时间，
             * 此处仅展示二次开发的设置仿真时长方法
             */
            if (methodNumber == 11) {
                simuiface.setSimuIntervalScheming(30);
            }

            /* 12. 设置仿真加速比 */
            if (methodNumber == 12) {
                simuiface.setAcceMultiples(10);  // 强制车辆不变道可用
            }
        }

        /**
         * 判断车辆是左变道还是右变道
         * @param vehi 运行车辆
         * @return 变道方向："left"、"right"或"noChange"
         */


}
