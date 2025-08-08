//package test002.TESS_JavaAPI_demo.others.SecondaryDevCasesMaster;
//
//import com.jidatraffic.tessng.*;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import javafx.beans.property.SimpleObjectProperty;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Random;
//
//import static com.jidatraffic.tessng.TESSNG.*;
//
//
//public class MySimulator extends CustomerSimulator {
//    // 信号定义，使用JavaFX的Property模拟Qt的Signal
//    private SimpleObjectProperty<String> signalRunInfo = new SimpleObjectProperty<>("");
//    private SimpleObjectProperty<Boolean> forStopSimu = new SimpleObjectProperty<>(false);
//    private SimpleObjectProperty<Boolean> forReStartSimu = new SimpleObjectProperty<>(false);
//
//    // 车辆方阵的车辆数
//    private int mrSquareVehiCount = 28;
//    // 飞机速度，飞机后面的车辆速度会被设定为此数据
//    private double mrSpeedOfPlane = 0;
//    // 当前正在仿真计算的路网名称
//    private String mNetPath = null;
//    // 相同路网连续仿真次数
//    private int mSimuCount = 0;
//    // 初始化二次开发对象
//    private Object secondaryDev;
//    // 所有灯组的起始时间
//    private List<Long> signalGroupsStartTimeLst = new ArrayList<>();
//    // 设置跟驰模型函数标志
//    private boolean reSetFollowingParamsFlag = false;
//    // 设置换道模型函数标志
//    private boolean refReSetChangeLaneFreelyParamFlag = false;
//    // 设置信号灯颜色标志位
//    private boolean calcLampColorFlag = false;
//
//    public MySimulator(Object secondaryDev) {
//        this.secondaryDev = secondaryDev;
//        // 初始化信号灯方案起始时间
//        initSignalGroupsStartTime();
//    }
//
//    // 初始化信号灯方案起始时间
//    private void initSignalGroupsStartTime() {
//        // 读取方案数据
//        Gson gson = new Gson();
//        try (FileReader reader = new FileReader("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\three\\TrafficAccident_Vehicle\\Data")) {
//            // 解析JSON为Map
//            Map<String, Map<String, Object>> signalGroupsDict =
//                    gson.fromJson(reader, new TypeToken<Map<String, Map<String, Object>>>() {
//                    }.getType());
//
//            // 提取所有灯组的起始时间
//            for (Map<String, Object> group : signalGroupsDict.values()) {
//                for (String startTime : group.keySet()) {
//                    // 转换为毫秒
//                    long seconds = functions.timeToSeconds(startTime);
//                    signalGroupsStartTimeLst.add(seconds * 1000);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 设置本类实现的过载方法被调用频次，即多少个计算周期调用一次
////    @Override
////    public void setStepsPerCall(IVehicle vehi) {
////        // 设置当前车辆及其驾驶行为过载方法被TESSNG调用频次
////        TessInterface iface = TESSNG.tessngIFace();
////        SimuInterface simuiface = iface.simuInterface();
////        NetInterface netiface = iface.netInterface();
////        String netFileName = netiface.netFilePath();
////
////        // 范例打开临时路段会创建车辆方阵，需要进行一些仿真过程控制
////        if (netFileName.contains("Temp")) {
////            // 允许对车辆重绘方法的调用
////            vehi.setIsPermitForVehicleDraw(true);
////            // 计算限制车道方法每10个计算周期被调用一次
////            vehi.setStepsCalcLimitedLaneNumber(10);
////            // 计算安全变道距离方法每10个计算周期被调用一次
////            vehi.setStepsCalcChangeLaneSafeDist(10);
////            // 重新计算车辆期望速度方法每一个计算周期被调用一次
////            vehi.setStepsReCalcdesirSpeed(1);
////            // 重新设置车速方法每一个计算周期被调用一次
////            vehi.setStepsReSetSpeed(1);
////        } else {
////            SimuInterface simuface = iface.simuInterface();
////            // 仿真精度，即每秒计算次数
////            int steps = simuface.simuAccuracy();
////            // 这里可以根据需要添加更多逻辑
////        }
////    }
//    @Override
//    public boolean calcAcce(IVehicle pIVehicle, ObjReal acce){
//        return false;
//    }
//
//        // 随机数生成器（用于变道概率等场景）
//        private static final Random random = new Random ();
//
//        // 过载父类方法：重新计算期望速度
//        @Override
//        public boolean reCalcdesirSpeed(IVehicle vehi, ObjReal inOutDesirSpeed) {
//            long roadId = vehi.roadId ();
//
//// 以 L5 路段为例设置减速区
//            if (roadId == 5) {
//                IVehicleDriving driving = vehi.vehicleDriving ();
//                double distToStart = driving.distToStartPoint ();
//
//// 仅当动作控制方案为 3 时生效
//                if (functions.actionControlMethodNumber == 3) {
//// 检查是否在 50-100 米减速区（单位转换：m2p 将米转为仿真单位）
//                    if (m2p (50) < distToStart && distToStart < m2p (100)) {
//                        long vehicleType = vehi.vehicleTypeCode ();
//                        if (vehicleType == 1) { // 小客车
//                            inOutDesirSpeed.setValue(m2p (10)); // 减速至 10m/s
//                            System.out.println ("车辆 ID:" + vehi.id () + "进入减速区，减速为 10m/s，当前速度:" + vehi.currSpeed ());
//                            return true; // 应用修改
//                        } else if (vehicleType == 2) { // 大客车
//                            inOutDesirSpeed.setValue(m2p (5)); // 减速至 5m/s
//                            System.out.println ("车辆 ID:" + vehi.id () + "进入减速区，减速为 5m/s，当前速度:" + vehi.currSpeed ());
//                            return true; // 应用修改
//                        }
//                    }
//                }
//            }
//            return false; // 不应用修改，使用 TESS NG 默认计算值
//        }
//
//        // 过载父类方法：重新计算跟驰参数（时距及安全距离）
//        @Override
//        public boolean reSetFollowingParam (IVehicle pIVehicle, ObjReal inOutSafeInterval, ObjReal inOutSafeDistance) {
//// 不修改跟驰参数，使用默认值
//            return false;
//        }
//
//        // 过载父类方法：重新计算加速度
//        @Override
//        public boolean reSetAcce(IVehicle pIVehicle, ObjReal inOutAcce) {
//// 不修改加速度，使用默认值
//            return false;
//        }
//
//        // 过载父类方法：重新计算当前速度
//        @Override
//        public boolean reSetSpeed(IVehicle vehi, ObjReal inOutSpeed) {
//// 动作控制方案 7：强制 L12 路段车辆闯红灯
//            if (functions.actionControlMethodNumber == 7) {
//                if (vehi.roadId () == 12) {
//                    double currentSpeed = vehi.currSpeed ();
//                    IVehicleDriving driving = vehi.vehicleDriving ();
//                    double distToEnd = driving.distToEndpoint (true);
//
//// 速度低于 20m/s 且距离终点小于 3 米时，80% 概率加速通过
//                    if (p2m (currentSpeed) < 20 && p2m (distToEnd) < 3) {
//                        if (random.nextDouble () < 0.8) {
//                            inOutSpeed.setValue(m2p (15)); // 设置速度为 15m/s
//                            System.out.println ("车辆 ID:" + vehi.id () + "当前速度:" + currentSpeed);
//                            return true;
//                        }
//                    }
//                }
//            }
//
//// 动作控制方案 10：强制 L5 路段车辆停车
//            if (vehi.roadId () == 5 && functions.actionControlMethodNumber == 10) {
//                inOutSpeed.setValue(m2p (0)); // 速度设为 0
//                return true;
//            }
//
//            return false; // 使用默认速度
//        }
//
//        // 过载父类方法：计算是否左自由变道
//        @Override
//        public boolean reCalcToLeftFreely (IVehicle vehi) {
//// 动作控制方案 6：最右侧车道（序号 0）的车辆强制左变道
//            if (functions.actionControlMethodNumber == 6) {
//                if (vehi.lane ().number () == 0) {
//                    return true; // 允许左变道
//                }
//            }
//            return false; // 不允许左变道
//        }
//
//        // 过载父类方法：计算是否右自由变道
//        @Override
//        public boolean reCalcToRightFreely (IVehicle vehi) {
//// 禁止所有右自由变道
//            return false;
//        }
//
//        // 过载父类方法：设置信号灯颜色
//        @Override
//        public boolean calcLampColor (ISignalLamp signalLamp) {
//// 仅当标志位开启且信号灯 ID 为 5 时，强制设为红色
//            if (this.calcLampColorFlag && signalLamp.id () == 5) {
//                signalLamp.setLampColor ("红");
//                return true; // 应用修改
//            }
//            return false; // 使用默认颜色
//        }
//
//        // 过载父类方法：计算车辆当前限制车道序号列表
//        @Override
//        public ArrayList<Integer> calcLimitedLaneNumber (IVehicle vehi) {
//// 无限制车道，返回空列表
//            return new ArrayList<>();
//        }
//
//    // 过载父类方法：对发车点增加发车时间段
//    @Override
//    public ArrayList<DispatchInterval> calcDynaDispatchParameters() {
//        if (functions.actionControlMethodNumber == 1) {
//            // 获取TESSNG顶层接口
//            TessInterface iface = TESSNG.tessngIFace();
//            // 获取ID为5的路段上的车辆
//            ArrayList<IVehicle> vehiclesInLink = iface.simuInterface().vehisInLink(5);
//
//            // 获取当前时间并转换为秒
//            LocalDateTime now = LocalDateTime.now();
//            int currSecs = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
//
//            // 创建发车参数对象
//            DispatchInterval di = new DispatchInterval();
//            // 动作控制案例-机动车交叉口L5路段发车点ID为11
//            di.setDispatchId(11);
//            di.setFromTime(currSecs);
//            di.setToTime(di.getFromTime() + 300 - 1); // 持续300秒
//            di.setVehiCount(300); // 总发车数
//
//            // 设置车辆组成（小客车60%，大客车40%）
//            ArrayList<VehiComposition> compositionList = new ArrayList<>();
//            VehiComposition vc1 = new VehiComposition(1,60);
//            compositionList.add(vc1);
//
//            VehiComposition vc2 = new VehiComposition(2,40);
//            compositionList.add(vc2);
//
//            di.setVehicleConsDetails(compositionList);
//
//            // 重置动作控制方案号，避免重复执行
//            functions.actionControlMethodNumber=0;
//
//            ArrayList<DispatchInterval> result = new ArrayList<>();
//            result.add(di);
//            return result;
//        }
//        return new ArrayList<>();
//    }
//
//    // 过载父类方法：动态修改决策点不同路径流量比
//    @Override
//    public ArrayList<DecipointFlowRatioByInterval> calcDynaFlowRatioParameters() {
//        // 获取TESSNG顶层接口
//        TessInterface iface = TESSNG.tessngIFace();
//        // 获取当前仿真计算批次
//        long batchNum = iface.simuInterface().batchNumber();
//
//        // 在计算第20批次时修改某决策点各路径流量比
//        if (batchNum == 20) {
//            DecipointFlowRatioByInterval dfi = new DecipointFlowRatioByInterval();
//            // 决策点编号
//            dfi.setDeciPointID(5);
//            // 起始时间（单位：秒）
//            dfi.setStartDateTime(1);
//            // 结束时间（单位：秒）
//            dfi.setEndDateTime(84000);
//
//            // 设置各路径流量比
//            ArrayList<RoutingFlowRatio> flowRatios = new ArrayList<>();
//
//            RoutingFlowRatio rfr1 = new RoutingFlowRatio(10,1);
//            flowRatios.add(rfr1);
//
//            RoutingFlowRatio rfr2 = new RoutingFlowRatio(11,2);
//            flowRatios.add(rfr2);
//
//            RoutingFlowRatio rfr3 = new RoutingFlowRatio(12,3);
//            flowRatios.add(rfr3);
//
//            dfi.setRoutingFlowRatios(flowRatios);
//
//            ArrayList<DecipointFlowRatioByInterval> result = new ArrayList<>();
//            result.add(dfi);
//            return result;
//        }
//        return new ArrayList<>();
//    }
//
//    // 过载父类方法：动态修改信号控制参数
//    @Override
//    public ArrayList<SignalContralParam> calcDynaSignalContralParameters() {
//        return new ArrayList<>();
//    }
//
//    // 过载父类方法：判断是否停止指定车辆运行
//    // 范例：车辆进入ID等于2的路段或连接段，距离终点小于100米，则驶出路网
//    @Override
//    public boolean isStopDriving(IVehicle vehi) {
//        return false;
//    }
//
//    // 修改跟驰模型参数
//    public ArrayList<FollowingModelParam> reSetFollowingParams() {
//
//        // 默认设置函数为关闭状态，如开启需将MySimulator中的函数标志位设置为True
//        if (this.reSetFollowingParamsFlag) {
//            // 机动车跟驰参数
//            FollowingModelParam motorParam = new FollowingModelParam();
//            motorParam.setVtype(MotorOrNonmotor.Motor);
//            motorParam.setAlfa(5);
//            motorParam.setBeit(3);
//            motorParam.setSafeDistance(15);
//            motorParam.setSafeInterval(10);
//
//            // 非机动车跟驰参数
//            FollowingModelParam nonmotorParam = new FollowingModelParam();
//            nonmotorParam.setVtype(MotorOrNonmotor.Nonmotor);
//            nonmotorParam.setAlfa(3);
//            nonmotorParam.setBeit(1);
//            nonmotorParam.setSafeDistance(5);
//            nonmotorParam.setSafeInterval(6);
//
//            ArrayList<FollowingModelParam> paramList = new ArrayList<>();
//            paramList.add(motorParam);
//            paramList.add(nonmotorParam);
//            return paramList;
//        }
//        return new ArrayList<>();
//    }
//
//    // 重新设置自由变道参数
//    @Override
//    public boolean reSetChangeLaneFreelyParam(IVehicle pIVehicle, ObjInt safeTime, ObjReal ultimateDist, ObjReal targetRParam) {
//        // 默认设置函数为关闭状态，如开启需将MySimulator中的函数标志位设置为True
//        if (this.refReSetChangeLaneFreelyParamFlag) {
//            // 安全操作时间，从驾驶员反应到实施变道(完成变道前半段)所需时间，默认4秒
//            safeTime.setValue(100);
//            // 安全变道(完成变道前半段)后距前车距离，小于此距离压迫感增强，触发驾驶员寻求变道
//            ultimateDist.setValue(50.0);
//            // 目标车道后车影响系数，大于等于0小于等于1，此值越大目标车道后车距影响越大，反之则越小
//            targetRParam.setValue(0.9);
//            return true;
//        }
//        return false;
//    }
//
//    // 撤销变道，可用于强制车辆不变道
//    @Override
//    public boolean reCalcDismissChangeLane(IVehicle vehi) {
//        if (functions.actionControlMethodNumber == 5) {
//            // 禁止车辆变道到最右侧车道
//            ILane lane = vehi.lane();
//            if (lane.number() == 1) {
//                // 判断车辆变道方向是否为右
//                if (this.secondaryDev.judgeVehicleLaneChangeDirection(vehi).equals("right")) {
//                    return true; // 撤销变道
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void afterStep(IVehicle vehi) {
//        // 获取TESSNG顶层接口
//        TessInterface iface = tessngIFace();
//        // 获取仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//        // 获取当前已仿真时间，单位：毫秒
//        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
//
//        // 动作控制方案8：L12路段车辆行驶至离路段终点50m处被移出路网
//        if (functions.actionControlMethodNumber == 8) {
//            if (vehi.roadId() == 12) {
//                simuiface.stopVehicleDriving(vehi);
//            }
//        }
//
//        // 动作控制方案9：L5路段车辆行驶至离路段终点50m处修改航向角
//        if (functions.actionControlMethodNumber == 9) {
//            if (vehi.roadId() == 5) {
//                IVehicleDriving driving = vehi.vehicleDriving();
//                driving.setAngle(vehi.angle() + 45.0);
//            }
//        }
//    }
//
//    @Override
//    public void afterOneStep() {
//        // 获取TESSNG顶层接口
//        TessInterface iface = tessngIFace();
//        // 获取仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//        // 获取路网子接口
//        NetInterface netiface = iface.netInterface();
//        // 当前仿真计算批次
//        long batchNum = simuiface.batchNumber();
//        // 当前已仿真时间，单位：毫秒
//        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
//        // 正在运行的所有车辆
//        List<IVehicle> allVehiStartedList = simuiface.allVehiStarted();
//
//        // 动作控制方案2：车辆位置移动（以L5路段上的车辆为例，在3秒时直接将该车移动过路口）
//        if (functions.actionControlMethodNumber == 2) {
//            for (int index = 0; index < allVehiStartedList.size(); index++) {
//                IVehicle vehi = allVehiStartedList.get(index);
//                System.out.println(index + " " + vehi.id());
//
//                if (vehi.roadId() == 5) {
//                    ILink nextLink = netiface.findLink(9);
//                    ArrayList<ILaneObject> laneObjsNextLinkList = nextLink.laneObjects();
//
//                    if (!laneObjsNextLinkList.isEmpty()) {
//                        int laneIndex = index % laneObjsNextLinkList.size();
//                        ILaneObject targetLane = laneObjsNextLinkList.get(laneIndex);
//                        float position = (float)(index % 100);
//
//                        // 移动车辆到目标车道和位置
//                        if (vehi.vehicleDriving().move(targetLane, position)) {
//                            System.out.println("车辆" + vehi.id() + "移动成功。");
//                            functions.actionControlMethodNumber = 0;
//                        }
//                    }
//                }
//            }
//        }
//
//        // 动作控制方案4：修改路径（L1所有车辆均修改为右转路径）
//        if (functions.actionControlMethodNumber == 4) {
//            for (IVehicle vehi : allVehiStartedList) {
//                if (vehi.roadId() == 1) {
//                    // 查找L1路段的决策点
//                    List<IDecisionPoint> decisionPointsList = netiface.decisionPoints();
//                    IDecisionPoint decisionPointLink1 = null;
//
//                    for (IDecisionPoint decisionPoint : decisionPointsList) {
//                        if (decisionPoint.link().id() == 1) {
//                            decisionPointLink1 = decisionPoint;
//                            break;
//                        }
//                    }
//
//                    // 获取该决策点的所有路径
//                    if (decisionPointLink1 != null) {
//                        List<IRouting> routingsList = decisionPointLink1.routings();
//                        if (!routingsList.isEmpty()) {
//                            IRouting targetRouting = routingsList.get(routingsList.size() - 1);
//
//                            // 如果当前路径不是目标路径，则修改路径
//                            if (vehi.routing() != targetRouting) {
//                                if (vehi.vehicleDriving().setRouting(targetRouting)) {
//                                    System.out.println("车辆" + vehi.id() + "修改路径成功。");
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//
//}
//
//
//
//
//
