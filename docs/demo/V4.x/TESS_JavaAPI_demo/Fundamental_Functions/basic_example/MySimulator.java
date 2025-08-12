package TESS_Java_APIDemo.Fundamental_Functions.basic_example;

import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import com.jidatraffic.tessng.*;

import static com.jidatraffic.tessng.TESSNG.m2p;

public class MySimulator extends JCustomerSimulator {

    // TESS NG接口
    private TessInterface iface;
    private NetInterface netiface;
    private SimuInterface simuiface;

    // 仿真参数
    private int squareVehiCount = 28;
    private double planeSpeed = 0;
    private String currentNetName = "";
    private int maxSimuCount = 0;

    /**
     * 构造方法
     */
    public MySimulator() {

        super();


    }

    /**
     * 仿真开始前调用
     */
    @Override
    public void beforeStart(ObjBool keepOn) {
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        String currentNetName = netiface.netFilePath();

        if (!currentNetName.equals(this.currentNetName)) {
            this.currentNetName = currentNetName;
            this.maxSimuCount = 1;
        } else {
            this.maxSimuCount++;
        }
    }

    /**
     * 仿真开始后调用
     */
    @Override
    public void afterStart() {
        // 空实现
    }

    /**
     * 仿真结束后调用
     */
    @Override
    public void afterStop() {
    }

    /**
     * 每个计算周期结束后调用，核心逻辑实现
     */
    @Override
    public void afterOneStep() {
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();
        // 获取仿真参数
        int simuAccuracy = simuiface.simuAccuracy();
        int simuMultiples = simuiface.acceMultiples();
        long batchNumber = simuiface.batchNumber();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        long startRealtime = simuiface.startMSecsSinceEpoch();

        // 仿真时间超过600秒则停止
        if (simuTime >= 600 * 1000) {
            simuiface.stopSimu();
        }

        // 动态发车（每50个批次）
        if (batchNumber % 50 == 1) {
            // 生成随机颜色
            String color = generateRandomColor();

            // 路段上发车
            DynaVehiParam dvp = new DynaVehiParam();
            dvp.setVehiTypeCode(randomInt(0, 4) + 1);
            dvp.setRoadId(6);
            dvp.setLaneNumber(randomInt(0, 3));
            dvp.setDist(50);
            dvp.setSpeed(20);
            dvp.setColor(color);
            IVehicle vehicle1 = simuiface.createGVehicle(dvp);

            // 连接段上发车
            DynaVehiParam dvp2 = new DynaVehiParam();
            dvp2.setVehiTypeCode(randomInt(0, 4) + 1);
            dvp2.setRoadId(3);
            dvp2.setLaneNumber(randomInt(0, 3));
            dvp2.setToLaneNumber(dvp2.getLaneNumber());
            dvp2.setDist(50);
            dvp2.setSpeed(20);
            dvp2.setColor(color);
            IVehicle vehicle2 = simuiface.createGVehicle(dvp2);
        }

        // 获取各种检测器数据（注释掉打印语句以提高性能）
        List<SignalPhaseColor> lPhoneColor = simuiface.getSignalPhasesColor();
        List<VehiInfoCollected> lVehiInfo = simuiface.getVehisInfoCollected();
        List<VehiInfoAggregated> lVehisInfoAggr = simuiface.getVehisInfoAggregated();
        List<VehiQueueCounted> lVehiQueue = simuiface.getVehisQueueCounted();
        List<VehiQueueAggregated> lVehiQueueAggr = simuiface.getVehisQueueAggregated();
        List<VehiTravelDetected> lVehiTravel = simuiface.getVehisTravelDetected();
        List<VehiTravelAggregated> lVehiTravAggr = simuiface.getVehisTravelAggregated();
    }

    /**
     * 车辆启动上路时调用
     */
    @Override
    public void initVehicle(IVehicle vehicle) {
        System.out.println("车辆启动上路");
        vehicle.setIsPermitForVehicleDraw(true);
        vehicle.setIsPermitForVehicleDraw(true);
        vehicle.setSteps_calcLimitedLaneNumber(10);
        vehicle.setSteps_calcChangeLaneSafeDist(10);
        vehicle.setSteps_reCalcdesirSpeed(1);
        vehicle.setSteps_reSetSpeed(1);
        setStepsPerCall(vehicle);
        // 车辆ID处理（取后5位）
        int tmpVehicleId = (int) (vehicle.id() % 100000);
        String roadName = vehicle.roadName();
        int roadId = (int) (vehicle.roadId());
        System.out.println("车辆ID：" + vehicle.id() + "，车辆名称：" + vehicle.name()  + "，车辆颜色：" + vehicle.color() + "，车辆所在道路：" + roadName + "，车辆所在道路ID：" + roadId + "，车辆所在车道：" + vehicle.vehicleDriving().laneNumber() );
        // 曹安公路上的车辆特殊处理
        if ("曹安公路".equals(roadName)) {
            // 飞机
            if (tmpVehicleId == 1) {
                vehicle.setVehiType(12);
                vehicle.initLane(3, m2p(105), 0);
            }
            // 工程车
            else if (tmpVehicleId >= 2 && tmpVehicleId <= 8) {
                vehicle.setVehiType(8);
                vehicle.initLane((int)((tmpVehicleId - 2) % 7), m2p(80), 0);
            }
            // 消防车
            else if (tmpVehicleId >= 9 && tmpVehicleId <= 15) {
                vehicle.setVehiType(9);
                vehicle.initLane((int)((tmpVehicleId - 2) % 7), m2p(65), 0);
            }
            // 消防车
            else if (tmpVehicleId >= 16 && tmpVehicleId <= 22) {
                vehicle.setVehiType(10);
                vehicle.initLane((int)((tmpVehicleId - 2) % 7), m2p(50), 0);
            }
            // 最后两队列小车
            else if (tmpVehicleId == 23) {
                vehicle.setVehiType(1);
                vehicle.initLane(1, m2p(35), 0);
            }
            else if (tmpVehicleId == 24) {
                vehicle.setVehiType(1);
                vehicle.initLane(5, m2p(35), 0);
            }
            else if (tmpVehicleId == 25) {
                vehicle.setVehiType(1);
                vehicle.initLane(1, m2p(20), 0);
            }
            else if (tmpVehicleId == 26) {
                vehicle.setVehiType(1);
                vehicle.initLane(5, m2p(20), 0);
            }
            else if (tmpVehicleId == 27) {
                vehicle.setVehiType(1);
                vehicle.initLane(1, m2p(5), 0);
            }
            else if (tmpVehicleId == 28) {
                vehicle.setVehiType(1);
                vehicle.initLane(5, m2p(5), 0);
            }

            // 设置最后两列小车的长度
            if (tmpVehicleId >= 23 && tmpVehicleId <= 28) {
                vehicle.setLength(m2p(4.5), true);
            }
        }
    }

    /**
     * 设置车辆方法调用频次
     */
    private void setStepsPerCall(IVehicle vehicle) {
        String netFileName = netiface.netFilePath();

        // 临时路段特殊处理
        if (netFileName.contains("Temp")) {
            vehicle.setIsPermitForVehicleDraw(true);
            vehicle.setSteps_calcLimitedLaneNumber(10);
            vehicle.setSteps_calcChangeLaneSafeDist(10);
            vehicle.setSteps_reCalcdesirSpeed(1);
            vehicle.setSteps_reSetSpeed(1);
        } else {
            int steps = simuiface.simuAccuracy();

            // 车辆相关方法调用频次
            vehicle.setIsPermitForVehicleDraw(false);
            vehicle.setSteps_beforeNextPoint(steps * 300);
            vehicle.setSteps_nextPoint(steps * 300);
            vehicle.setSteps_afterStep(steps * 300);
            vehicle.setSteps_isStopDriving(steps * 300);

            // 驾驶行为相关方法调用频次
            vehicle.setSteps_reCalcdesirSpeed(steps * 300);
            vehicle.setSteps_calcMaxLimitedSpeed(steps * 300);
            vehicle.setSteps_calcLimitedLaneNumber(steps);
            vehicle.setSteps_calcSpeedLimitByLane(steps);
            vehicle.setSteps_calcChangeLaneSafeDist(steps);
            vehicle.setSteps_reCalcToLeftLane(steps);
            vehicle.setSteps_reCalcToRightLane(steps);
            vehicle.setSteps_reCalcToLeftFreely(steps);
            vehicle.setSteps_reCalcToRightFreely(steps);
            vehicle.setSteps_afterCalcTracingType(steps * 300);
            vehicle.setSteps_beforeMergingToLane(steps * 300);
            vehicle.setSteps_reSetFollowingType(steps * 300);
            vehicle.setSteps_calcAcce(steps * 300);
            vehicle.setSteps_reSetAcce(steps * 300);
            vehicle.setSteps_reSetSpeed(steps * 300);
            vehicle.setSteps_reCalcAngle(steps * 300);
            vehicle.setSteps_recentTimeOfSpeedAndPos(steps * 300);
            vehicle.setSteps_travelOnChangingTrace(steps * 300);
            vehicle.setSteps_leaveOffChangingTrace(steps * 300);
            vehicle.setSteps_beforeNextRoad(steps * 300);
        }
    }

    /**
     * 判断是否停止车辆运行
     */
    @Override
    public boolean isStopDriving(IVehicle vehicle) {
        // ID等于2的路段，距离终点小于100米则驶出路网
        if (vehicle.roadId() == 2) {
            double dist = vehicle.vehicleDriving().distToEndpoint(true);
            if (dist < m2p(100)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 车辆被移除时调用
     */
    @Override
    public void afterStopVehicle(IVehicle vehicle) {
        // 空实现
    }

    /**
     * 重新计算车辆加速度
     */
    @Override
    public boolean ref_reSetAcce(IVehicle vehicle, ObjReal inOutAcce) {
        String roadName = vehicle.roadName();
        if ("连接段1".equals(roadName)) {
            if (vehicle.currSpeed() > m2p(20 / 3.6)) {
                inOutAcce.setValue(m2p(-5));
                return true;
            } else if (vehicle.currSpeed() > m2p(20 / 3.6)) {
                inOutAcce.setValue(m2p(-1));
                return true;
            }
        }
        return false;
    }

    /**
     * 重新计算车辆期望速度
     */
    @Override
    public boolean ref_reCalcdesirSpeed(IVehicle vehicle, ObjReal refDesirSpeed) {
        long tmpVehicleId = vehicle.id() % 100000;
        String roadName = vehicle.roadName();

        if ("曹安公路".equals(roadName)) {
            if (tmpVehicleId <= squareVehiCount) {
                long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
                if (simuTime < 5 * 1000) {
                    refDesirSpeed.setValue(0);
                } else if (simuTime < 10 * 1000) {
                    refDesirSpeed.setValue(m2p(20 / 3.6));
                } else {
                    refDesirSpeed.setValue(m2p(40 / 3.6));
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 重新设置车辆当前速度
     */
    @Override
    public boolean ref_reSetSpeed(IVehicle vehicle, ObjReal refInOutSpeed) {
        long tmpVehicleId = vehicle.id() % 100000;
        String roadName = vehicle.roadName();

        if ("曹安公路".equals(roadName)) {
            if (tmpVehicleId == 1) {
                planeSpeed = vehicle.currSpeed();
            } else if (tmpVehicleId >= 2 && tmpVehicleId <= squareVehiCount) {
                refInOutSpeed.setValue(planeSpeed);
            }
            return true;
        }
        return false;
    }

    /**
     * 重新计算车辆跟驰参数
     */
    @Override
    public boolean ref_reSetFollowingParam(IVehicle vehicle, ObjReal refInOutSi, ObjReal refInOutSd) {
        String roadName = vehicle.roadName();
        if ("连接段2".equals(roadName)) {
            refInOutSd.setValue(m2p(30));
            return true;
        }
        return false;
    }

    /**
     * 计算是否向左自由变道
     */
    @Override
    public boolean reCalcToLeftFreely(IVehicle vehicle) {
        // 距离路段终点小于20米不变道
        if (vehicle.vehicleDriving().distToEndpoint() - vehicle.length() / 2 < m2p(20)) {
            return false;
        }

        long tmpVehicleId = vehicle.id() % 100000;
        String roadName = vehicle.roadName();

        if ("曹安公路".equals(roadName)) {
            if (tmpVehicleId >= 23 && tmpVehicleId <= 28) {
                int laneNumber = vehicle.vehicleDriving().laneNumber();
                if (laneNumber == 1 || laneNumber == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算是否向右自由变道
     */
    @Override
    public boolean reCalcToRightFreely(IVehicle vehicle) {
        // 距离路段终点小于20米不变道
        if (vehicle.vehicleDriving().distToEndpoint() - vehicle.length() / 2 < m2p(20)) {
            return false;
        }

        long tmpVehicleId = vehicle.id() % 100000;
        String roadName = vehicle.roadName();

        if ("曹安公路".equals(roadName)) {
            if (tmpVehicleId >= 23 && tmpVehicleId <= 28) {
                int laneNumber = vehicle.vehicleDriving().laneNumber();
                if (laneNumber == 2 || laneNumber == 5) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算禁行车道列表
     */
    @Override
    public ArrayList<Integer> calcLimitedLaneNumber(IVehicle vehicle) {
        ArrayList<Integer> limitedLanes = new ArrayList<>();

        // 路段ID等于2时，小车走内侧，大车走外侧
        if (vehicle.roadIsLink()) {
            ILink link = vehicle.lane().link();
            if (link != null && link.id() == 2) {
                int laneCount = link.laneCount();
                // 长度小于8米为小车
                if (vehicle.length() < m2p(8)) {
                    for (int i = 0; i < laneCount / 2 - 1; i++) {
                        limitedLanes.add(i);
                    }
                } else {
                    for (int i = laneCount / 2 - 1; i < laneCount; i++) {
                        limitedLanes.add(i);
                    }
                }
            }
        }
        return limitedLanes;
    }

    /**
     * 设置信号灯灯色
     */
    @Override
    public boolean calcLampColor(ISignalLamp signalLamp) {
        if (signalLamp.id() == 5) {
            signalLamp.setLampColor("红");
            return true;
        }
        return false;
    }

    /**
     * 计算车道限速
     */
    @Override
    public boolean ref_calcSpeedLimitByLane(ILink link, int laneNumber, ObjReal refOutSpeed) {
        // ID为2的路段，车道0和1限速30km/h
        if (link.id() == 2 && laneNumber <= 1) {
            refOutSpeed.setValue(30);
            return true;
        }
        return false;
    }

    /**
     * 动态修改发车参数
     */
    @Override
    public ArrayList<DispatchInterval> calcDynaDispatchParameters() {
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();
        ArrayList<DispatchInterval> result = new ArrayList<>();
        long currentSimuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        ArrayList<IVehicle> vehicles = simuiface.vehisInLink(1);

        // 仿真10秒后且路段1上无车辆时设置发车参数
        if (currentSimuTime >= 1000 * 10 && vehicles.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            int currentSecond = now.get(ChronoField.HOUR_OF_DAY) * 3600 +
                    now.get(ChronoField.MINUTE_OF_HOUR) * 60 +
                    now.get(ChronoField.SECOND_OF_MINUTE);

            DispatchInterval di = new DispatchInterval();
            di.setDispatchId(1);
            di.setFromTime(currentSecond);
            di.setToTime(di.getFromTime() + 300 - 1);
            di.setVehiCount(300);

            ArrayList<VehiComposition> composition = new ArrayList<>();
            composition.add(new VehiComposition(1, 60));
            composition.add(new VehiComposition(2, 40));
            di.setVehicleConsDetails(composition);

            result.add(di);
        }
        return result;
    }

    /**
     * 动态修改决策点路径流量比
     */
    @Override
    public ArrayList<DecipointFlowRatioByInterval> calcDynaFlowRatioParameters() {
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();
        ArrayList<DecipointFlowRatioByInterval> result = new ArrayList<>();
        long batchNumber = simuiface.batchNumber();

        // 第20批次时修改决策点5的路径流量比
        if (batchNumber == 20) {
            DecipointFlowRatioByInterval dfi = new DecipointFlowRatioByInterval();
            dfi.setDeciPointID(5);
            dfi.setStartDateTime(1);
            dfi.setEndDateTime(999999);

            ArrayList<RoutingFlowRatio> ratios = new ArrayList<>();
            ratios.add(new RoutingFlowRatio(10, 3));
            ratios.add(new RoutingFlowRatio(11, 4));
            ratios.add(new RoutingFlowRatio(12, 3));
            dfi.setRoutingFlowRatios(ratios);

            result.add(dfi);
        }
        return result;
    }

    /**
     * 生成随机颜色
     */
    private String generateRandomColor() {
        Random random = new Random();
        String r = String.format("%02X", random.nextInt(256));
        String g = String.format("%02X", random.nextInt(256));
        String b = String.format("%02X", random.nextInt(256));
        return "#" + r + g + b;
    }

    /**
     * 生成指定范围的随机整数
     */
    private int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
