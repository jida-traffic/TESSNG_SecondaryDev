//package Others.apiTest;//package Traffic_Signal_Control.Pedestrian_Intersection_Signal_Control;
//
//import com.jidatraffic.tessng.*;
//import com.jidatraffic.tessng.UnitOfMeasure;
//import java.util.*;
//import java.io.*;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.JSONArray;
//
//import static com.jidatraffic.tessng.TESSNG.m2p;
//
//public class FunctionTest {
//    private int id;
//
//    public FunctionTest(int id) {
//        this.id = id;
//    }
//
//    // 信控编辑案例
//    public void editSignalController() {
//        TessInterface iface = TESSNG.tessngIFace();
//        NetInterface netiface = iface.netInterface();
//        // 创建路段
//        Point startPoint1 = new Point(m2p(-300), m2p(-200));
//        Point endPoint1 = new Point(m2p(-50), m2p(-200));
//        ArrayList<Point> lPoint1 = new ArrayList<>();
//        lPoint1.add(startPoint1);
//        lPoint1.add(endPoint1);
//        ILink link1 = netiface.createLink(lPoint1, 3, "信控编辑路段1");
//
//        Point startPoint2 = new Point(m2p(50), m2p(-200));
//        Point endPoint2 = new Point(m2p(300), m2p(-200));
//        ArrayList<Point> lPoint2 = new ArrayList<>();
//        lPoint1.add(startPoint1);
//        lPoint1.add(endPoint1);
//        ILink link2 = netiface.createLink(lPoint2, 3, "信控编辑路段2");
//
//        ArrayList<ILaneObject> lLaneObjects = new ArrayList<>();
//        if (link1 != null && link2 != null) {
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<>(Arrays.asList(1, 2, 3));
//            ArrayList<Integer> lToLaneNumber = new ArrayList<>(Arrays.asList(1, 2, 3));
//            IConnector connector = netiface.createConnector(link1.id(), link2.id(),
//                    lFromLaneNumber, lToLaneNumber, "信控编辑连接段", true);
//
//            if (connector != null) {
//                lLaneObjects = connector.laneObjects();
//                for (ILaneObject laneObj : lLaneObjects) {
//                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
//                            " 下游车道ID: " + laneObj.toLaneObject().id());
//                }
//            }
//        }
//
//        // 创建发车点
//        if (link1 != null) {
//            IDispatchPoint dp = netiface.createDispatchPoint(link1);
//            if (dp != null) {
//                dp.addDispatchInterval(1, 3600, 3600);
//            }
//        }
//
//        // 1. 创建信号控制器（相当于信号组的容器）
//        ISignalController signalController = netiface.createSignalController("信号灯控制器1");
//
//        // 2. 创建信号方案（设置周期、起始时间等参数）
//        // 参数说明：信号控制器、方案名称、周期时长、偏移量、开始时间、结束时间
//        ISignalPlan signalPlan = netiface.createSignalPlan(
//                signalController,
//                "信号灯组1",
//                60,  // 周期时间（秒）
//                0,   // 偏移量
//                1,   // 开始时间
//                3600 // 结束时间
//        );
//
//        // 3. 创建相位（40秒绿灯，3秒黄灯，3秒红灯）
//        ColorInterval green = new ColorInterval("G", 40);  // 绿灯
//        ColorInterval yellow = new ColorInterval("Y", 3); // 黄灯
//        ColorInterval red = new ColorInterval("R", 3);    // 红灯
//
//        ArrayList<ColorInterval> colorIntervals = new ArrayList<>();
//        colorIntervals.add(green);
//        colorIntervals.add(yellow);
//        colorIntervals.add(red);
//
//        // 4. 在信号方案下创建信号相位
//        ISignalPhase signalPhase = netiface.createSignalPlanSignalPhase(
//                signalPlan,
//                "信号灯组1相位1",
//                colorIntervals
//        );
//
//        // 5. 创建信号灯并关联到相位
//        for (int index = 0; index < lLaneObjects.size(); index++) {
//            ILaneObject laneObj = lLaneObjects.get(index);
//            // 创建信号灯头
//            ISignalLamp signalLamp = netiface.createTrafficSignalLamp(
//                    signalController,  // 关联的信号控制器
//                    "信号灯" + (index + 1),  // 信号灯名称
//                    laneObj.fromLaneObject().id(),  // 关联的上游车道ID
//                    laneObj.toLaneObject().id(),    // 关联的下游车道ID
//                    m2p(2.0)                  // 距离（米转像素）
//            );
//
//            // 将信号灯与相位关联
//            netiface.addSignalPhaseToLamp((int) signalPhase.id(), signalLamp);
//        }
//    }
//
//    // 双环信控方案下发
//    public void doubleRingSignalControl(double currentSimuTime) {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        NetInterface netiface = iface.netInterface();
//
//        // 读取JSON方案数据
//        try (FileReader fileReader = new FileReader("./Data/Signal_Plan_Data_1109.json")) {
//            // 读取文件内容为字符串
//            BufferedReader br = new BufferedReader(fileReader);
//            StringBuilder jsonContent = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonContent.append(line);
//            }
//            JSONObject signalGroupsDict = JSON.parseObject(jsonContent.toString());
//
//            // 遍历所有信号控制器配置
//            for (String groupName : signalGroupsDict.keySet()) {
//                JSONObject group = signalGroupsDict.getJSONObject(groupName);
//
//                // 查找对应的信号控制器（原Python中的signalGroup）
//                ISignalController currentSignalController = null;
//                ArrayList<ISignalController> allControllers = netiface.signalControllers();
//                for (ISignalController controller : allControllers) {
//                    if (controller.name().equals(groupName)) {
//                        currentSignalController = controller;
//                        break;
//                    }
//                }
//
//                if (currentSignalController == null) {
//                    System.out.println("FindError: The signalController not in current net.");
//                    break;
//                }
//
//                // 获取信号控制器下的所有信号方案及相位
//                ArrayList<ISignalPlan> signalPlans = currentSignalController.plans();
//                ArrayList<ISignalPhase> allPhases = new ArrayList<>();
//                for (ISignalPlan plan : signalPlans) {
//                    allPhases.addAll(plan.phases());
//                }
//
//                // 处理时间区间配置
//                ArrayList<String> startTimeList = new ArrayList<>(group.keySet());
//                for (int index = 0; index < startTimeList.size(); index++) {
//                    String startTimeStr = startTimeList.get(index);
//                    String endTimeStr = (index != startTimeList.size() - 1)
//                            ? startTimeList.get(index + 1)
//                            : "24:00";
//
//                    // 转换时间为秒数
//                    double startTimeSeconds = Functions.timeToSeconds(startTimeStr);
//                    double endTimeSeconds = Functions.timeToSeconds(endTimeStr);
//
//                    // 判断当前仿真时间是否在该区间内
//                    if (startTimeSeconds <= currentSimuTime && currentSimuTime < endTimeSeconds) {
//                        JSONObject groupData = group.getJSONObject(startTimeStr);
//                        int periodTime = groupData.getIntValue("cycle_time");
//                        JSONArray phases = groupData.getJSONArray("phases");
//
//                        // 更新信号方案周期
//                        for (ISignalPlan plan : signalPlans) {
//                            plan.setCycleTime(periodTime);
//                        }
//
//                        // 处理每个相位配置
//                        for (Object phaseObj : phases) {
//                            JSONObject phase = (JSONObject) phaseObj;
//                            String phaseName = phase.getString("phase_name");
//                            int phaseNumber = phase.getIntValue("phase_number");
//
//                            // 构建灯色序列
//                            ArrayList<ColorInterval> colorList = new ArrayList<>();
//                            colorList.add(new ColorInterval("红", phase.getIntValue("start_time")));
//                            colorList.add(new ColorInterval("绿", phase.getIntValue("green_time")));
//                            colorList.add(new ColorInterval("黄", 3));
//
//                            // 计算剩余红灯时间
//                            int remainingRed = periodTime
//                                    - phase.getIntValue("start_time")
//                                    - phase.getIntValue("green_time")
//                                    - 3;
//                            if (remainingRed > 0) {
//                                colorList.add(new ColorInterval("红", remainingRed));
//                            }
//
//                            // 查找是否存在该相位
//                            ISignalPhase currentPhase = null;
//                            for (ISignalPhase p : allPhases) {
//                                if (Integer.parseInt(p.number()) == phaseNumber) {
//                                    currentPhase = p;
//                                    break;
//                                }
//                            }
//
//                            // 更新或创建相位
//                            if (currentPhase != null) {
//                                currentPhase.setColorList(colorList);
//                            } else {
//                                // 若没有信号方案则创建默认方案
//                                ISignalPlan targetPlan = signalPlans.isEmpty()
//                                        ? netiface.createSignalPlan(currentSignalController, "默认方案", periodTime, 0, 0, 3600)
//                                        : signalPlans.get(0);
//
//                                // 创建新相位
//                                ISignalPhase newPhase = netiface.createSignalPlanSignalPhase(
//                                        targetPlan, phaseName, colorList
//                                );
//                                newPhase.setNumber(String.valueOf(phaseNumber));
//                                allPhases.add(newPhase); // 加入相位列表便于后续查找
//                            }
//
//                            // 关联信号灯与相位
//                            JSONArray lampIds = phase.getJSONArray("lamp_lst");
//                            for (Object lampIdObj : lampIds) {
//                                int lampId = lampIdObj instanceof Integer
//                                        ? (Integer) lampIdObj
//                                        : Integer.parseInt(lampIdObj.toString());
//
//                                ISignalLamp lamp = netiface.findSignalLamp(lampId);
//                                if (lamp != null) {
//                                    lamp.setPhaseNumber(phaseNumber);
//                                } else {
//                                    System.out.println("FindError:未查找到信号灯:" + lampId);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("方案文件读取失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    // 双环信控方案下发测试
//// 双环信控方案下发测试
//    public void doubleRingSignalControlTest(int planNumber) {
//        // 读取方案数据
//        try (FileReader fileReader = new FileReader("./Data/Signal_Plan_Data_1109.json")) {
//            // 读取文件内容为字符串
//            BufferedReader br = new BufferedReader(fileReader);
//            StringBuilder jsonContent = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonContent.append(line);
//            }
//            JSONObject signalGroupsDict = JSON.parseObject(jsonContent.toString());
//
//            // 收集所有灯组的起始时间（转换为秒）
//            List<Double> signalGroupsStartTimeList = new ArrayList<>();
//            for (Object groupObj : signalGroupsDict.values()) {
//                JSONObject group = (JSONObject) groupObj;
//                for (String startTimeStr : group.keySet()) {
//                    // 转换时间字符串为秒数
//                    double startTimeSeconds = Functions.timeToSeconds(startTimeStr);
//                    signalGroupsStartTimeList.add(startTimeSeconds);
//                }
//            }
//
//            // 计算当前方案序号（取模避免越界）
//            int currentPlanNumber = planNumber % signalGroupsStartTimeList.size();
//            double currentTime = signalGroupsStartTimeList.get(currentPlanNumber);
//
//            // 打印信息并调用双环信控方案下发方法
//            System.out.println(currentTime + ":双环信控方案更改。");
//            this.doubleRingSignalControl(currentTime);
//
//        } catch (IOException e) {
//            System.err.println("方案文件读取失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // 流量加载
//    // 流量加载方法
//    public void trafficLoading() {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        NetInterface netiface = iface.netInterface();
//        SimuInterface simuiface = iface.simuInterface();
//
//        /* 1. 新建发车点 */
//        // 创建第一条路段
//        Point startPoint1 = new Point(m2p(-300), m2p(-180));
//        Point endPoint1 = new Point(m2p(-50), m2p(-180));
//        ArrayList<Point> lPoint1 = new ArrayList<>();
//        lPoint1.add(startPoint1);
//        lPoint1.add(endPoint1);
//        ILink link1 = netiface.createLink(lPoint1, 3, "流量加载路段1");
//
//        // 创建第二条路段
//        Point startPoint2 = new Point(m2p(50), m2p(-180));
//        Point endPoint2 = new Point(m2p(300), m2p(-180));
//        ArrayList<Point> lPoint2 = new ArrayList<>();
//        lPoint2.add(startPoint2);
//        lPoint2.add(endPoint2);
//        ILink link2 = netiface.createLink(lPoint2, 3, "流量加载路段2");
//
//        // 创建连接段
//        ArrayList<ILaneObject> lLaneObjects = new ArrayList<>();
//        if (link1 != null && link2 != null) {
//            // 车道连接配置
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
//            lFromLaneNumber.add(1);
//            lFromLaneNumber.add(2);
//            lFromLaneNumber.add(3);
//
//            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
//            lToLaneNumber.add(1);
//            lToLaneNumber.add(2);
//            lToLaneNumber.add(3);
//
//            IConnector connector = netiface.createConnector(
//                    link1.id(),
//                    link2.id(),
//                    lFromLaneNumber,
//                    lToLaneNumber,
//                    "流量加载连接段",
//                    true
//            );
//
//            if (connector != null) {
//                lLaneObjects = connector.laneObjects();
//                for (ILaneObject laneObj : lLaneObjects) {
//                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
//                            " 下游车道ID: " + laneObj.toLaneObject().id());
//                }
//
//                // 创建车型组成
//                ArrayList<VehiComposition> vehiTypeProportionList = new ArrayList<>();
//                // 车型组成：小客车0.3，大客车0.2，公交车0.1，货车0.4
//                vehiTypeProportionList.add(new VehiComposition(1, 0.3));
//                vehiTypeProportionList.add(new VehiComposition(2, 0.2));
//                vehiTypeProportionList.add(new VehiComposition(3, 0.1));
//                vehiTypeProportionList.add(new VehiComposition(4, 0.4));
//
//                long vehiCompositionID = netiface.createVehicleComposition(
//                        "动态创建车型组成",
//                        vehiTypeProportionList
//                );
//
//                if (vehiCompositionID != -1) {
//                    System.out.println("车型组成创建成功，id为：" + vehiCompositionID);
//                    // 新建发车点
//                    if (link1 != null) {
//                        IDispatchPoint dp = netiface.createDispatchPoint(link1);
//                        if (dp != null) {
//                            // 600秒发300辆车
//                            dp.addDispatchInterval(vehiCompositionID, 600, 300);
//                        }
//                    }
//                }
//            }
//        }
//
//        /* 2. 动态发车 */
//        // 创建动态发车路段3
//        Point startPoint3 = new Point(m2p(-300), m2p(-160));
//        Point endPoint3 = new Point(m2p(-50), m2p(-160));
//        ArrayList<Point> lPoint3 = new ArrayList<>();
//        lPoint3.add(startPoint3);
//        lPoint3.add(endPoint3);
//        ILink link3 = netiface.createLink(lPoint3, 3, "动态加载车辆段");
//
//        // 创建动态发车路段4
//        Point startPoint4 = new Point(m2p(50), m2p(-160));
//        Point endPoint4 = new Point(m2p(300), m2p(-160));
//        ArrayList<Point> lPoint4 = new ArrayList<>();
//        lPoint4.add(startPoint4);
//        lPoint4.add(endPoint4);
//        ILink link4 = netiface.createLink(lPoint4, 3, "动态加载车辆段");
//
//        // 创建动态发车路段的连接段
//        if (link3 != null && link4 != null) {
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
//            lFromLaneNumber.add(1);
//            lFromLaneNumber.add(2);
//            lFromLaneNumber.add(3);
//
//            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
//            lToLaneNumber.add(1);
//            lToLaneNumber.add(2);
//            lToLaneNumber.add(3);
//
//            IConnector connector = netiface.createConnector(
//                    link3.id(),
//                    link4.id(),
//                    lFromLaneNumber,
//                    lToLaneNumber,
//                    "动态加载加载连接段",
//                    true
//            );
//
//            if (connector != null) {
//                lLaneObjects = connector.laneObjects();
//                for (ILaneObject laneObj : lLaneObjects) {
//                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
//                            " 下游车道ID: " + laneObj.toLaneObject().id());
//                }
//            }
//        }
//
//        // 配置动态车辆参数
//        if (link3 != null && link4 != null) {
//            DynaVehiParam dvpLane0 = new DynaVehiParam();
//            DynaVehiParam dvpLane1 = new DynaVehiParam();
//            DynaVehiParam dvpLane2 = new DynaVehiParam();
//
//            // 设置车辆类型
//            dvpLane0.setVehiTypeCode(1);
//            dvpLane1.setVehiTypeCode(2);
//            dvpLane2.setVehiTypeCode(3);
//
//            // 设置所在路段ID
//            dvpLane0.setRoadId(link3.id());
//            dvpLane1.setRoadId(link3.id());
//            dvpLane2.setRoadId(link4.id());
//
//            // 设置车道号
//            dvpLane0.setLaneNumber(0);
//            dvpLane1.setLaneNumber(1);
//            dvpLane2.setLaneNumber(2);
//
//            // 设置距离起点距离
//            dvpLane0.setDist(m2p(50));
//            dvpLane1.setDist(m2p(100));
//            dvpLane2.setDist(m2p(50));
//
//            // 设置速度
//            dvpLane0.setSpeed(20);
//            dvpLane1.setSpeed(30);
//            dvpLane2.setSpeed(40);
//
//            // 设置颜色
//            dvpLane0.setColor("#FF0000");  // 红色
//            dvpLane1.setColor("#008000");  // 绿色
//            dvpLane2.setColor("#0000FF");  // 蓝色
//
//            // 创建动态车辆
//            simuiface.createGVehicle(dvpLane0);
//            simuiface.createGVehicle(dvpLane1);
//            simuiface.createGVehicle(dvpLane2);
//        }
//    }
//
//
//    // 路径加载主函数（单一函数实现全部逻辑）
//    public void flowLoading() {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        NetInterface netiface = iface.netInterface();
//        // 仿真接口（按需保留）
//        SimuInterface simuiface = iface.simuInterface();
//
//        // 1. 查找四岔路口相关路段 (L3-C2-L10)
//        ILink link3 = netiface.findLink(3);
//        ILink link10 = netiface.findLink(10);
//        ILink link6 = netiface.findLink(6);
//        ILink link7 = netiface.findLink(7);
//        ILink link8 = netiface.findLink(8);
//
//        // 2. 新建发车点
//        if (link3 != null) {
//            IDispatchPoint dp = netiface.createDispatchPoint(link3);
//            if (dp != null) {
//                // 配置发车参数：车型组成ID=1，1800秒内发900辆车
//                dp.addDispatchInterval(1, 1800, 900);
//            }
//        }
//
//        // 3. 创建决策点（位于link3上，距离起点30米处）
//        IDecisionPoint decisionPoint = netiface.createDecisionPoint(link3, m2p(30));
//
//        // 4. 创建左、直、右三条路径（使用ArrayList存储路段）
//        // 左转路径：link3 → link10 → link6
//        ArrayList<ILink> routeLeft = new ArrayList<>();
//        routeLeft.add(link3);
//        routeLeft.add(link10);
//        routeLeft.add(link6);
//        IRouting decisionRouting1 = netiface.createDeciRouting(decisionPoint, routeLeft);
//
//        // 直线路径：link3 → link10 → link8
//        ArrayList<ILink> routeStraight = new ArrayList<>();
//        routeStraight.add(link3);
//        routeStraight.add(link10);
//        routeStraight.add(link8);
//        IRouting decisionRouting2 = netiface.createDeciRouting(decisionPoint, routeStraight);
//
//        // 右转路径：link3 → link10 → link7
//        ArrayList<ILink> routeRight = new ArrayList<>();
//        routeRight.add(link3);
//        routeRight.add(link10);
//        routeRight.add(link7);
//        IRouting decisionRouting3 = netiface.createDeciRouting(decisionPoint, routeRight);
//
//        // 5. 配置各路径流量比（使用ArrayList存储流量比对象）
//        ArrayList<_RoutingFLowRatio> flowRatios = new ArrayList<>();
//
//        // 左转流量比
//        _RoutingFLowRatio flowRatioLeft = new _RoutingFLowRatio();
//        flowRatioLeft.setRoutingFLowRatioID(1);
//        flowRatioLeft.setRoutingID(decisionRouting1.id());
//        flowRatioLeft.setStartDateTime(0);
//        flowRatioLeft.setEndDateTime(999999);
//        flowRatioLeft.setRatio(2.0);
//        flowRatios.add(flowRatioLeft);
//
//        // 直行流量比
//        _RoutingFLowRatio flowRatioStraight = new _RoutingFLowRatio();
//        flowRatioStraight.setRoutingFLowRatioID(2);
//        flowRatioStraight.setRoutingID(decisionRouting2.id());
//        flowRatioStraight.setStartDateTime(0);
//        flowRatioStraight.setEndDateTime(999999);
//        flowRatioStraight.setRatio(3.0);
//        flowRatios.add(flowRatioStraight);
//
//        // 右转流量比
//        _RoutingFLowRatio flowRatioRight = new _RoutingFLowRatio();
//        flowRatioRight.setRoutingFLowRatioID(3);
//        flowRatioRight.setRoutingID(decisionRouting3.id());
//        flowRatioRight.setStartDateTime(0);
//        flowRatioRight.setEndDateTime(999999);
//        flowRatioRight.setRatio(1.0);
//        flowRatios.add(flowRatioRight);
//
//        // 6. 构建决策点数据（包含ID、名称和坐标）
//        _DecisionPoint decisionPointData = new _DecisionPoint();
//        decisionPointData.setDeciPointID(decisionPoint.id());
//        decisionPointData.setDeciPointName(decisionPoint.name());
//
//        // 获取决策点坐标
//        Point decisionPointPos = new Point();
//        boolean isPointObtained = decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos);
//        if (isPointObtained) {
//            decisionPointData.setX(decisionPointPos.getX());
//            decisionPointData.setY(decisionPointPos.getY());
//            decisionPointData.setZ(decisionPoint.link().z());
//        } else {
//            System.out.println("警告：无法获取决策点坐标，更新可能失败");
//        }
//
//        // 7. 更新决策点及其流量比配置
//        IDecisionPoint updatedDecisionPoint = netiface.updateDecipointPoint(decisionPointData, flowRatios);
//        if (updatedDecisionPoint != null) {
//            System.out.println("决策点创建成功：" + updatedDecisionPoint.name());
//
//            // 8. 删除右转路径
//            boolean isRightRouteRemoved = netiface.removeDeciRouting(decisionPoint, decisionRouting3);
//            if (isRightRouteRemoved) {
//                System.out.println("右转路径已成功删除");
//            } else {
//                System.out.println("警告：右转路径删除失败");
//            }
//        } else {
//            System.out.println("错误：决策点更新失败");
//        }
//    }
//
//    // 路径断面流量加载
//    public void flowLoadingSection(double currentTime) {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        NetInterface netiface = iface.netInterface();
//        SimuInterface simuiface = iface.simuInterface();
//
//        // 读取流量比配置文件
//        try (FileReader fileReader = new FileReader("./Data/flow_ratio_quarter.json")) {
//            // 读取文件内容为字符串
//            BufferedReader br = new BufferedReader(fileReader);
//            StringBuilder jsonContent = new StringBuilder();
//            String line;
//            while ((line = br.readLine()) != null) {
//                jsonContent.append(line);
//            }
//            JSONObject flowRatioQuarterDict = JSON.parseObject(jsonContent.toString());
//
//            // 遍历所有路段的流量配置
//            Set<String> linkIds = flowRatioQuarterDict.keySet();
//            for (String linkIdStr : linkIds) {
//                int linkId = Integer.parseInt(linkIdStr);
//                JSONObject quarterRatios = flowRatioQuarterDict.getJSONObject(linkIdStr);
//
//                // 查找该路段上的决策点
//                IDecisionPoint decisionPoint = null;
//                ArrayList<IDecisionPoint> decisionPointsList = netiface.decisionPoints();
//                for (IDecisionPoint dp : decisionPointsList) {
//                    if (dp.link() != null && dp.link().id() == linkId) {
//                        decisionPoint = dp;
//                        break;
//                    }
//                }
//
//                if (decisionPoint != null) {
//                    // 获取时间段列表并排序
//                    ArrayList<String> quarterStartTimeList = new ArrayList<>(quarterRatios.keySet());
//
//                    // 遍历每个时间段的流量比配置
//                    for (int index = 0; index < quarterStartTimeList.size(); index++) {
//                        String startTimeStr = quarterStartTimeList.get(index);
//                        JSONObject quarterRatio = quarterRatios.getJSONObject(startTimeStr);
//
//                        // 转换时间为秒数
//                        double quarterTimeSeconds = Functions.timeToSeconds(startTimeStr);
//                        double quarterTimeSecondsNext;
//
//                        if (index != quarterStartTimeList.size() - 1) {
//                            quarterTimeSecondsNext = Functions.timeToSeconds(quarterStartTimeList.get(index + 1));
//                        } else {
//                            quarterTimeSecondsNext = quarterTimeSeconds + 1; // 最后一个时段的结束时间
//                        }
//
//                        // 判断当前时间是否在该时段内
//                        if (quarterTimeSeconds <= currentTime && currentTime < quarterTimeSecondsNext) {
//                            // 获取决策点的现有路径
//                            ArrayList<IRouting> decisionRoutingsList = decisionPoint.routings();
//
//                            // 检查是否有3条路径（左、直、右）
//                            if (decisionRoutingsList.size() == 3) {
//                                // 配置左转流量比
//                                _RoutingFLowRatio flowRatioLeft = new _RoutingFLowRatio();
//                                flowRatioLeft.setRoutingFLowRatioID(decisionRoutingsList.get(0).id());
//                                flowRatioLeft.setRoutingID(decisionRoutingsList.get(0).id());
//                                flowRatioLeft.setStartDateTime(0);
//                                flowRatioLeft.setEndDateTime(999999);
//                                flowRatioLeft.setRatio(quarterRatio.getDoubleValue("left"));
//
//                                // 配置直行流量比
//                                _RoutingFLowRatio flowRatioStraight = new _RoutingFLowRatio();
//                                flowRatioStraight.setRoutingFLowRatioID(decisionRoutingsList.get(1).id());
//                                flowRatioStraight.setRoutingID(decisionRoutingsList.get(1).id());
//                                flowRatioStraight.setStartDateTime(0);
//                                flowRatioStraight.setEndDateTime(999999);
//                                flowRatioStraight.setRatio(quarterRatio.getDoubleValue("straight"));
//
//                                // 配置右转流量比
//                                _RoutingFLowRatio flowRatioRight = new _RoutingFLowRatio();
//                                flowRatioRight.setRoutingFLowRatioID(decisionRoutingsList.get(2).id());
//                                flowRatioRight.setRoutingID(decisionRoutingsList.get(2).id());
//                                flowRatioRight.setStartDateTime(0);
//                                flowRatioRight.setEndDateTime(999999);
//                                flowRatioRight.setRatio(quarterRatio.getDoubleValue("right"));
//
//                                // 构建决策点数据
//                                _DecisionPoint decisionPointData = new _DecisionPoint();
//                                decisionPointData.setDeciPointID(decisionPoint.id());
//                                decisionPointData.setDeciPointName(decisionPoint.name());
//
//                                // 获取决策点坐标
//                                Point decisionPointPos = new Point();
//                                if (decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos)) {
//                                    decisionPointData.setX(decisionPointPos.getX());
//                                    decisionPointData.setY(decisionPointPos.getY());
//                                    decisionPointData.setZ(decisionPoint.link().z());
//                                }
//
//                                // 封装流量比列表
//                                ArrayList<_RoutingFLowRatio> flowRatios = new ArrayList<>();
//                                flowRatios.add(flowRatioLeft);
//                                flowRatios.add(flowRatioStraight);
//                                flowRatios.add(flowRatioRight);
//
//                                // 更新决策点流量比
//                                IDecisionPoint updatedDecisionPoint = netiface.updateDecipointPoint(decisionPointData, flowRatios);
//                                if (updatedDecisionPoint != null) {
//                                    System.out.println(startTimeStr + "流量更新成功。");
//                                }
//                            } else {
//                                System.out.println("DecisionRoutingsError:决策点" + decisionPoint.id() + "需要包含左、直、右三条路径。");
//                            }
//                        }
//                    }
//                } else {
//                    System.out.println("FindError:ID为" + linkId + "的路段不存在决策点");
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("流量配置文件读取失败: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // 动作控制
//    /**
//     * 动作控制方法
//     * @param planNumber 方案序号
//     */
//    public void actionControl(int planNumber) {
//        // 以动作控制案例-机动车交叉口路网的L5路段为例
//
//        /* 1. 修改发车流量信息，删除发车点
//           - 修改发车流量信息需在MySimulator中的calcDynaDispatchParameters函数实现
//           - 删除发车点位于afterOneStep函数中
//         */
//
//        /* 2. 修改决策路径的属性，删除决策路径
//           - 详见路径加载/路径管理模块
//         */
//
//        /* 3. 修改减速区，施工区，事故区信息；删除减速区，施工区，事故区
//           - 减速区相关逻辑见MySimulator中的ref_reCalcdesirSpeed函数
//         */
//
//        /* 4. 车辆位置移动
//           - 详见afterOneStep函数
//         */
//
//        /* 5. 修改车辆速度
//           - 同3减速区相关逻辑
//         */
//
//        /* 6. 修改车辆路径
//           - 以L1路段上的路径为例，详见afterOneStep函数
//         */
//
//        /* 7. 强制车辆不变道
//           - 详见MySimulator中的reCalcDismissChangeLane函数
//         */
//
//        /* 8. 强制车辆变道
//           - 详见MySimulator中的reCalcToLeftFreely和reCalcToRightFreely函数
//           - 只需返回true即可实现强制变道
//         */
//
//        /* 9. 强制车辆闯红灯
//           - 详见MySimulator的ref_reSetSpeed函数
//         */
//
//        /* 10. 强制车辆停车
//            - 详见MySimulator的ref_reSetSpeed函数
//         */
//
//        /* 11. 强制清除车辆（车辆消失）
//            - 以L5路段上的路径为例，详见afterStep函数
//         */
//
//        /* 12. 修改车辆航向角
//            - 以L5路段上的路径为例，详见afterStep函数
//         */
//
//        /* 13. 修改车辆速度，加速度
//            - 同5（修改速度）
//            - 修改加速度函数为MySimulator的ref_reSetAcce，用法与设置速度相同
//         */
//
//        /* 14. 车道关闭与恢复
//            - 实现方式：
//              1. 设置事件区
//              2. 通过MySimulator中的自由变道控制
//              （示例：L5路段50-100m处最右侧车道封闭30秒）
//         */
//
//        // 设置动作控制方案序号
//        Functions.setActionControlMethodNumber(planNumber);
//        System.out.println(Functions.getActionControlMethodNumber());
//    }
//
//    /**
//     * 创建施工区
//     * @return 创建的施工区对象（IRoadWorkZone）
//     */
//    public IRoadWorkZone createWorkZone() {
//        // 创建施工区参数对象
//        DynaRoadWorkZoneParam workZoneParam = new DynaRoadWorkZoneParam();
//
//        // 设置道路ID（对应Python中的roadId = 5）
//        workZoneParam.setRoadId(5);
//
//        // 设置施工区名称
//        workZoneParam.setName("施工区，限速40,持续20秒");
//
//        // 位置：距离路段起点50米（单位米）
//        workZoneParam.setLocation(50);
//
//        // 施工区长度：50米
//        workZoneParam.setLength(50);
//
//        // 限速：40千米/小时
//        workZoneParam.setLimitSpeed(40);
//
//        // 施工时长：20秒（duration结束后自动删除）
//        workZoneParam.setDuration(20);
//
//        // 设置施工影响的起始车道（Python中的[0]对应Java的ArrayList）
//        ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
//        fromLaneNumbers.add(0);  // 车道编号为0
//        workZoneParam.setMlFromLaneNumber(fromLaneNumbers);
//
//        // 获取TESS NG路网接口并创建施工区
//        NetInterface netInterface = TESSNG.tessngIFace().netInterface();
//        IRoadWorkZone zone = netInterface.createRoadWorkZone(workZoneParam);
//
//        // 可根据需要添加创建结果日志
//        if (zone != null) {
//            System.out.println("施工区创建成功：" + zone.name());
//        } else {
//            System.out.println("施工区创建失败");
//        }
//
//        return zone;
//    }
//
//    /**
//     * 管控手段控制
//     * @param methodNumber 调用的方法序号
//     */
//    public void controlMeasures(int methodNumber) {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        // 仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//        // 路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        /* 1. 修改信号灯灯色
//           - 见MySimulator的afterOneStep函数，L5路段信号灯第10秒红灯变绿灯，持续20秒
//         */
//
//        /* 2. 修改信号灯组方案
//           - 见双环管控方案下发
//         */
//
//        /* 3. 修改相位绿灯时间长度
//           - 除双环管控方案下所包含方法外，还有相位类自带的修改方法
//           - 以L12路段相位直行信号灯相位为例（ID为7），由红90绿32黄3红25改为红10绿110黄3红28
//         */
//        if (methodNumber == 3) {
//            ISignalPhase signalPhaseL127 = netiface.findSignalPhase(7);
//            if (signalPhaseL127 != null) {
//                // 按照红灯、绿灯、黄灯、红灯顺序设置
//                ArrayList<ColorInterval> colorList = new ArrayList<>();
//                colorList.add(new ColorInterval("红", 10));
//                colorList.add(new ColorInterval("绿", 110));
//                colorList.add(new ColorInterval("黄", 3));
//                colorList.add(new ColorInterval("红", 28));
//
//                signalPhaseL127.setColorList(colorList);
//            } else {
//                System.out.println("未找到ID为7的信号灯相位");
//            }
//        }
//
//        /* 5. 修改link, connector 限速
//           - 以L5路段最高限速由80调整至20，连接段无法修改限速
//         */
//        if (methodNumber == 5) {
//            ILink link5 = netiface.findLink(5);
//            if (link5 != null) {
//                link5.setLimitSpeed(20);
//                System.out.println("L5路段限速已调整为20");
//            } else {
//                System.out.println("未找到ID为5的路段");
//            }
//        }
//    }
//
//    /**
//     * 换道模型
//     * @param methodNumber 调用的方法序号
//     */
//    public void laneChangingModel(int methodNumber) {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        // 仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//        // 路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        /* 1. 选择变道类型：强制变道，压迫变道，自由变道 */
//
//    /* 2. 设置强制变道，压迫变道参数
//       - 目前通过MySimulator中的ref_reSetChangeLaneFreelyParam函数设置以下参数：
//         安全操作时间、安全变道(完成变道前半段)后距前车距离、目标车道后车影响系数
//       - 以L5路段两侧车道往中间变道为例
//     */
//    }
//    /**
//     * 流程控制
//     * @param methodNumber 调用的方法序号
//     */
//    public void processControl(double methodNumber) {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        // 仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//        // 路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        /* 1. 启动、暂停、恢复、停止仿真 */
//        if (methodNumber == 1) {
//            simuiface.startSimu();
//        } else if (methodNumber == 2) {
//            simuiface.pauseSimu();
//        } else if (methodNumber == 3) {
//            simuiface.stopSimu();
//        } else if (methodNumber == 4) {
//            simuiface.pauseSimuOrNot();
//        }
//
//        /* 8. 获取运动信息 */
//        // 8.1 获取路网在途车辆，见MySimulator中afterOneStep的simuiface.allVehiStarted()
//
//        // 8.2 根据路段|车道获取车辆列表
//        if (methodNumber == 8.2) {
//            // 获取L5路段的车辆列表
//            ArrayList<IVehicle> vehiOnRoad5List = simuiface.vehisInLink(5);
//            // 获取车道ID为20的车辆列表
//            ArrayList<IVehicle> vehiOnLane20List = simuiface.vehisInLane(20);
//
//            System.out.println("L5路段车辆id：");
//            for (IVehicle vehicle : vehiOnRoad5List) {
//                System.out.println(vehicle.id());
//            }
//
//            System.out.println("lane20车道车辆id：");
//            for (IVehicle vehicle : vehiOnLane20List) {
//                System.out.println(vehicle.id());
//            }
//        }
//
//        // 8.3 根据车辆id获取具体的车辆信息,以id为300001的车辆为例
//        if (methodNumber == 8.3) {
//            IVehicle vehi300001 = simuiface.getVehicle(300001);
//            if (vehi300001 != null) {
//                System.out.println("300001车辆的具体信息：");
//                System.out.println("所在路段: " + vehi300001.roadId());
//                System.out.println("所在车道: " + vehi300001.lane().id());
//                System.out.println("当前车速: " + vehi300001.currSpeed());
//                System.out.println("当前加速度: " + vehi300001.acce());
//                System.out.println("当前角度: " + vehi300001.angle());
//                System.out.println("当前位置: " + vehi300001.pos());
//                System.out.println("其它: ......");
//            } else {
//                System.out.println("未找到ID为300001的车辆");
//            }
//        }
//
//        /* 10. 设置仿真精度 */
//        if (methodNumber == 10) {
//            simuiface.setSimuAccuracy(10);
//        }
//
//        /* 11. 设置仿真开始结束时间
//           - 可以设置仿真时长，无法设置仿真开始的时间
//           - 可通过定时器定时启动和结束仿真实现设置仿真开始结束时间
//           - 此处展示二次开发的设置仿真时长方法
//         */
//        if (methodNumber == 11) {
//            simuiface.setSimuIntervalScheming(30);
//        }
//
//        /* 12. 设置仿真加速比 */
//        if (methodNumber == 12) {
//            simuiface.setAcceMultiples(10);
//        }
//    }
//
//    /**
//     * 判断车辆是左变道还是右变道
//     * @param vehi 运行车辆对象
//     * @return 变道方向："left"（左变道）、"right"（右变道）或"noChange"（不变道）
//     */
//    public String judgeVehicleLaneChangeDirection(IVehicle vehi) {
//        // 获取车辆当前所在车道
//        ILane lane = vehi.lane();
//        if (lane == null) {
//            System.out.println("车辆未在任何车道上");
//            return "noChange";
//        }
//
//        // 获取车辆当前位置
//        Point vehiCurrPos = vehi.pos();
//        // 计算车辆到车道起点的距离
//        double vehiCurrDistToStart = lane.distToStartPoint(vehiCurrPos);
//
//        // 获取车道中心线的断点列表
//        ArrayList<Point> laneCenterBreakPoints = lane.centerBreakPoints();
//        int vehiSegmentIndex = -1;
//
//        // 确定车辆所在的道路分段号
//        for (int index = 0; index < laneCenterBreakPoints.size(); index++) {
//            Point centerBreakPoint = laneCenterBreakPoints.get(index);
//            double breakPointDistToStart = lane.distToStartPoint(centerBreakPoint);
//
//            if (vehiCurrDistToStart < breakPointDistToStart) {
//                vehiSegmentIndex = index;
//                break;
//            }
//        }
//
//        // 检查分段索引是否有效
//        if (vehiSegmentIndex > 0 && vehiSegmentIndex < laneCenterBreakPoints.size()) {
//            // 获取当前分段的起点和终点
//            Point startBreakPoint = laneCenterBreakPoints.get(vehiSegmentIndex - 1);
//            Point endBreakPoint = laneCenterBreakPoints.get(vehiSegmentIndex);
//
//            // 判断车辆位于中心线的左侧还是右侧
//            String vehiDirection = Functions.carPositionRoad(startBreakPoint, endBreakPoint, vehiCurrPos);
//
//            // 计算车道分段的角度
//            double breakLaneAngle = Functions.calculateAngle(startBreakPoint, endBreakPoint);
//
//            // 若车辆处于中心线右侧且车头右偏，则判定为右变道意图
//            if ("right".equals(vehiDirection) && vehi.angle() > breakLaneAngle) {
//                return "right";
//            }
//            // 若车辆处于中心线左侧且车头左偏，则判定为左变道意图
//            else if ("left".equals(vehiDirection) && vehi.angle() < breakLaneAngle) {
//                return "left";
//            }
//            // 其他情况判定为不变道
//            else {
//                return "noChange";
//            }
//        } else {
//            // 无法找到有效的分段
//            System.out.printf("FindError:无法找到车辆所在分段，相关信息: 分段索引=%d, 距离起点=%f, 位置=%s%n",
//                    vehiSegmentIndex, vehiCurrDistToStart, vehiCurrPos.toString());
//            return "noChange";
//        }
//    }
//
//    /**
//     * 创建交通事件：包括路段、发车点、事故区、限速区、收费站和施工区等
//     * @param netiface 路网接口
//     */
//    public void createTrafficIncident(NetInterface netiface) {
//        // 1. 创建上行路段（含多个转折点的长路段）
//        ArrayList<Point> upstreamPoints = new ArrayList<>();
//        upstreamPoints.add(new Point(-900, 900 + 1500));
//        upstreamPoints.add(new Point(900, 900 + 1500));
//        upstreamPoints.add(new Point(900, 700 + 1500));
//        upstreamPoints.add(new Point(-900, 700 + 1500));
//        upstreamPoints.add(new Point(-900, 500 + 1500));
//        upstreamPoints.add(new Point(900, 500 + 1500));
//        upstreamPoints.add(new Point(900, 300 + 1500));
//        upstreamPoints.add(new Point(-900, 300 + 1500));
//        upstreamPoints.add(new Point(-900, 100 + 1500));
//        upstreamPoints.add(new Point(900, 100 + 1500));
//        upstreamPoints.add(new Point(900, -100 + 1500));
//        upstreamPoints.add(new Point(-900, -100 + 1500));
//        upstreamPoints.add(new Point(-900, -300 + 1500));
//        upstreamPoints.add(new Point(900, -300 + 1500));
//        upstreamPoints.add(new Point(900, -500 + 1500));
//        upstreamPoints.add(new Point(-900, -500 + 1500));
//        upstreamPoints.add(new Point(-900, -700 + 1500));
//        upstreamPoints.add(new Point(900, -700 + 1500));
//        upstreamPoints.add(new Point(900, -900 + 1500));
//        upstreamPoints.add(new Point(-900, -900 + 1500));
//
//        // 反转点列表
//        ArrayList<Point> reversedUpstreamPoints = new ArrayList<>();
//        for (int i = upstreamPoints.size() - 1; i >= 0; i--) {
//            reversedUpstreamPoints.add(upstreamPoints.get(i));
//        }
//
//        ILink upstream = netiface.createLink(reversedUpstreamPoints, 4, "上行", UnitOfMeasure.Metric);
//        upstream.setLimitSpeed(120, UnitOfMeasure.Metric);
//
//        // 2. 创建下行路段
//        ArrayList<Point> downstreamPoints = new ArrayList<>();
//        downstreamPoints.add(new Point(-880, 920 + 1500));
//        downstreamPoints.add(new Point(920, 920 + 1500));
//        downstreamPoints.add(new Point(920, 680 + 1500));
//        downstreamPoints.add(new Point(-880, 680 + 1500));
//        downstreamPoints.add(new Point(-880, 520 + 1500));
//        downstreamPoints.add(new Point(920, 520 + 1500));
//        downstreamPoints.add(new Point(920, 280 + 1500));
//        downstreamPoints.add(new Point(-880, 280 + 1500));
//        downstreamPoints.add(new Point(-880, 120 + 1500));
//        downstreamPoints.add(new Point(920, 120 + 1500));
//        downstreamPoints.add(new Point(920, -120 + 1500));
//        downstreamPoints.add(new Point(-880, -120 + 1500));
//        downstreamPoints.add(new Point(-880, -280 + 1500));
//        downstreamPoints.add(new Point(920, -280 + 1500));
//        downstreamPoints.add(new Point(920, -520 + 1500));
//        downstreamPoints.add(new Point(-880, -520 + 1500));
//        downstreamPoints.add(new Point(-880, -680 + 1500));
//        downstreamPoints.add(new Point(920, -680 + 1500));
//        downstreamPoints.add(new Point(920, -920 + 1500));
//        downstreamPoints.add(new Point(-880, -920 + 1500));
//
//        ILink downstream = netiface.createLink(downstreamPoints, 4, "下行", true, UnitOfMeasure.Metric);
//        downstream.setLimitSpeed(120, UnitOfMeasure.Metric);
//
//        // 3. 创建发车点
//        IDispatchPoint upstreamDispatchPoint = netiface.createDispatchPoint(upstream, "上行发车");
//        IDispatchPoint downstreamDispatchPoint = netiface.createDispatchPoint(downstream, "上行发车");
//
//        // 4. 设置车型组成
//        ArrayList<VehiComposition> vehiTypeProportionList = new ArrayList<>();
//        vehiTypeProportionList.add(new VehiComposition(1, 0.8));  // 小客车
//        vehiTypeProportionList.add(new VehiComposition(2, 0.1));  // 大客车
//        vehiTypeProportionList.add(new VehiComposition(4, 0.1));  // 货车
//        long vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiTypeProportionList);
//
//        // 设置发车间隔
//        upstreamDispatchPoint.addDispatchInterval(vehiCompositionID, 3600, 3600);
//        downstreamDispatchPoint.addDispatchInterval(vehiCompositionID, 3600, 3600);
//
//        // 5. 创建事故区
//        DynaAccidentZoneParam accidentZoneParam = new DynaAccidentZoneParam();
//        accidentZoneParam.setRoadId(upstream.id());
//        accidentZoneParam.setName("最左侧车道发生事故");
//        accidentZoneParam.setLocation(700);
//        accidentZoneParam.setLength(50);
//        accidentZoneParam.setStartTime(0);
//        accidentZoneParam.setDuration(500);
//        accidentZoneParam.setNeedStayed(true);
//        accidentZoneParam.setLimitSpeed(55);
//        accidentZoneParam.setControlLength(100);
//
//        ArrayList<Integer> accidentLanes = new ArrayList<>();
//        accidentLanes.add(2);
//        accidentZoneParam.setMlFromLaneNumber(accidentLanes);
//
//        IAccidentZone accidentZone = netiface.createAccidentZone(accidentZoneParam);
//
//        // 添加事故区时间段信息
//        DynaAccidentZoneIntervalParam intervalParam = new DynaAccidentZoneIntervalParam();
//        intervalParam.setAccidentZoneId(accidentZone.id());
//        intervalParam.setStartTime(90);
//        intervalParam.setEndTime(120);
//        intervalParam.setLocation(200);
//        intervalParam.setLength(50);
//        intervalParam.setLimitedSpeed(10);
//        intervalParam.setControlLength(100);
//
//        ArrayList<Integer> intervalLanes = new ArrayList<>();
//        intervalLanes.add(0);
//        intervalLanes.add(1);
//        intervalLanes.add(3);
//        intervalParam.setMlFromLaneNumber(intervalLanes);
//
//        accidentZone.addAccidentZoneInterval(intervalParam);
//
//        System.out.println("创建限速区");
//
//// 定义不同车型的限速参数
//        DynaReduceSpeedVehiTypeParam type1 = new DynaReduceSpeedVehiTypeParam();
//        type1.setVehicleTypeCode(1); // 小客车
//        type1.setAvgSpeed(10);       // 平均速度
//        type1.setSpeedSD(5);         // 速度标准差
//
//        DynaReduceSpeedVehiTypeParam type2 = new DynaReduceSpeedVehiTypeParam();
//        type2.setVehicleTypeCode(2); // 大客车
//        type2.setAvgSpeed(20);       // 平均速度
//        type2.setSpeedSD(0);         // 速度标准差
//
//// 创建车型限速参数列表
//        ArrayList<DynaReduceSpeedVehiTypeParam> vehicleTypeParams = new ArrayList<>();
//        vehicleTypeParams.add(type1);
//        vehicleTypeParams.add(type2);
//
//// 创建限速时间段参数（并添加车型限速配置）
//        DynaReduceSpeedIntervalParam speedInterval = new DynaReduceSpeedIntervalParam();
//        speedInterval.setStartTime(0);   // 开始时间
//        speedInterval.setEndTime(50);    // 结束时间
//// 使用正确的方法设置车辆类型参数列表
//        speedInterval.setReduceSpeedVehicleTypeParams(vehicleTypeParams);
//
//// 创建限速时间段参数列表
//        ArrayList<DynaReduceSpeedIntervalParam> intervalParams = new ArrayList<>();
//        intervalParams.add(speedInterval);
//
//// 创建限速区参数（并添加时间段配置）
//        DynaReduceSpeedAreaParam param1 = new DynaReduceSpeedAreaParam();
//        param1.setName("限速区");
//        param1.setLocation(2500);       // 距离路段起点的位置（米）
//        param1.setAreaLength(100);      // 限速区长度（米）
//        param1.setRoadId(upstream.id());// 关联的道路ID
//        param1.setLaneNumber(0);        // 起始车道序号
//        param1.setToLaneNumber(-1);     // 结束车道序号（-1表示所有车道）
//// 假设DynaReduceSpeedAreaParam有类似的方法来设置时间段参数列表
//        param1.setReduceSpeedIntervalParams(intervalParams);
//
//// 创建限速区
//        IReduceSpeedArea reduceSpeedArea = netiface.createReduceSpeedArea(param1);
//        System.out.println(reduceSpeedArea);
//
//        // 7. 创建收费站相关
//        // 7.1 创建停车分布
//        ArrayList<TollTimeDetail> timeDisList = new ArrayList<>();
//        timeDisList.add(createMtcTimeDetail(3, 1));
//        timeDisList.add(createMtcTimeDetail(5, 1));
//
//        ArrayList<TollSpeedDetail> speedDisList = new ArrayList<>();
//        speedDisList.add(createEtcSpeedDetail(15, 1));
//        speedDisList.add(createEtcSpeedDetail(20, 1));
//
//        int vehicleTypeId = 1;
//        TollParkingTime tollParkingTime = createTollParkingTime(vehicleTypeId, timeDisList, speedDisList);
//        DynaTollParkingTimeDis tollParkingTimeDis = netiface.createTollParkingTimeDis("新建停车分布", new ArrayList<>() {{
//            add(tollParkingTime);
//        }});
//        System.out.println("创建了停车分布，id=" + tollParkingTimeDis.getId());
//
//        // 7.2 创建收费车道
//        ArrayList<ITollLane> tollLanes = new ArrayList<>();
//        for (int laneNumber = 0; laneNumber < upstream.lanes().size(); laneNumber++) {
//            TollPoint tollPoint = createTollPoint(40, 1);
//            int linkId = upstream.id();
//            float location = 5500;
//            float length = 40;
//            ITollLane tollLane = netiface.createTollLane(linkId, location, length, laneNumber, 0, 3600, new ArrayList<>() {{
//                add(tollPoint);
//            }});
//            tollLanes.add(tollLane);
//            System.out.println("在序号为" + laneNumber + "的车道上创建了收费车道，id=" + tollLane.id());
//        }
//
//        // 7.3 创建收费路径决策点
//        int linkId = upstream.id();
//        float location = 5000;
//        ITollDecisionPoint tollDeciPoint = netiface.createTollDecisionPoint(linkId, location);
//        System.out.println("创建了收费路径决策点，id=" + tollDeciPoint.id());
//
//        // 7.4 创建收费路径及分配信息
//        ArrayList<TollStation.DynaTollDisInfo> tollDisInfoList = new ArrayList<>();
//        for (ITollLane tollLane : tollLanes) {
//            ITollRouting tollRouting = netiface.createTollRouting(tollDeciPoint, tollLane);
//            System.out.println("在收费路径决策点" + tollDeciPoint.id() + "创建了收费路径，id=" + tollRouting.id());
//
//            TollStation.DynaTollDisInfo tollDisInfo = new TollStation.DynaTollDisInfo();
//            tollDisInfo.pIRouting = null;
//
//            TollStation.DynaRoutingDisTollInfo routingDisTollInfo = new TollStation.DynaRoutingDisTollInfo();
//            routingDisTollInfo.startTime = 0;
//            routingDisTollInfo.endTime = 3600;
//
//            // 添加ETC收费信息
//            TollStation.DynaEtcTollInfo ectTollInfo = new TollStation.DynaEtcTollInfo();
//            ectTollInfo.etcRatio = 0.2;
//            ectTollInfo.tollLaneID = tollLane.id();
//            ectTollInfo.tollRoutingID = tollRouting.id();
//            routingDisTollInfo.ectTollInfoList.add(ectTollInfo);
//
//            // 添加车辆类型分布信息
//            TollStation.DynaVehicleTollDisInfo vehicleTollDisInfo = new TollStation.DynaVehicleTollDisInfo();
//            vehicleTollDisInfo.vehicleType = 3;
//
//            TollStation.DynaVehicleTollDisDetail detail = new TollStation.DynaVehicleTollDisDetail();
//            detail.prop = 10;
//            detail.tollLaneID = tollLane.id();
//            detail.tollRoutingID = tollRouting.id();
//            vehicleTollDisInfo.list.add(detail);
//
//            routingDisTollInfo.vehicleDisInfoList.add(vehicleTollDisInfo);
//            tollDisInfo.disTollInfoList.add(routingDisTollInfo);
//            tollDisInfoList.add(tollDisInfo);
//        }
//
//        // 更新决策点信息
//        tollDeciPoint.updateTollDisInfoList(tollDisInfoList);
//
//        // 输出收费站统计信息
//        System.out.println("收费停车分布的数量是：" + netiface.tollParkingTimeDis().size());
//        System.out.println("收费车道的数量是：" + netiface.tollLanes().size());
//        System.out.println("收费路径决策点的数量是：" + netiface.tollDecisionPoints().size());
//
//        // 8. 创建路边停车场
//        createParking(netiface, downstream);
//
//        // 9. 创建施工区
//        createRoadWorkZone(netiface, upstream, downstream);
//    }
//
//    // 创建路口
//    public void createJunction(INetInterface netiface) {
//        // 实现路口创建逻辑（路段、连接段、发车点等）
//    }
//    /**
//     * 创建MTC时间分布
//     * @param time 时间值
//     * @param prop 比例值
//     * @return DynaMtcTimeDetail对象
//     */
//    private DynaMtcTimeDetail createMtcTimeDetail(float time, float prop) {
//        DynaMtcTimeDetail mtcTimeDetail = new DynaMtcTimeDetail();
//        mtcTimeDetail.setTime(time);
//        mtcTimeDetail.setProp(prop);
//        return mtcTimeDetail;
//    }
//    /**
//     * 创建ETC速度分布
//     * @param speed 速度值（单位：米/秒或千米/小时，根据实际业务场景确定）
//     * @param prop 比例值
//     * @return DynaEtcSpeedDetail对象
//     */
//    private DynaEtcSpeedDetail createEtcSpeedDetail(float speed, float prop) {
//        DynaEtcSpeedDetail etcSpeedDetail = new DynaEtcSpeedDetail();
//        etcSpeedDetail.setLimitSpeed(speed);
//        etcSpeedDetail.setProp(prop);
//        return etcSpeedDetail;
//    }
//    /**
//     * 为一种车型创建MTC停车时间分布和ETC速度分布
//     * @param vehicleTypeId 车辆类型ID
//     * @param timeDisList MTC时间分布列表
//     * @param speedDisList ETC速度分布列表
//     * @return DynaTollParkingTime对象
//     */
//    private DynaTollParkingTime createTollParkingTime(int vehicleTypeId,
//                                                      List<DynaMtcTimeDetail> timeDisList,
//                                                      List<DynaEtcSpeedDetail> speedDisList) {
//        DynaTollParkingTime tollParkingTime = new DynaTollParkingTime();
//        tollParkingTime.setVehicleTypeId(vehicleTypeId);
//        tollParkingTime.setTimeDisList(timeDisList);
//        tollParkingTime.setSpeedDisList(speedDisList);
//        return tollParkingTime;
//    }
//
//
//}