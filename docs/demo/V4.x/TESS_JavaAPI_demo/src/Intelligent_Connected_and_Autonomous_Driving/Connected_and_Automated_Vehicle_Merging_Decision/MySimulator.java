package Intelligent_Connected_and_Autonomous_Driving.Connected_and_Automated_Vehicle_Merging_Decision;

import com.jidatraffic.tessng.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.jidatraffic.tessng.TESSNG.m2p;
import static com.jidatraffic.tessng.TESSNG.p2m;

// 用户自定义仿真器类，处理CV车辆汇入逻辑
public class MySimulator extends JCustomerSimulator {


    // 记录获得换道允许的CV车ID
    private Set<Long> getPermission = new HashSet<>();

    public MySimulator() {
        super();
    }

    /**
     * 仿真开始前的设置
     */
    @Override
    public void ref_beforeStart(ObjBool ref_keepOn) {
        // 设置继续仿真
        ref_keepOn.setValue(true);
    }

    /**
     * 计算是否允许左自由变道
     */
    @Override
    public boolean reCalcToLeftFreely(IVehicle vehi) {
        // 对于获得换道允许且当前在0号车道的车辆，允许左变道
        return getPermission.contains(vehi.id()) &&
                vehi.vehicleDriving().laneNumber() == 0;
    }

    /**
     * 计算车辆当前限制车道序号列表
     * 对于进入加速车道的CV，禁止其由TESS的换道决策执行汇入
     */
    @Override
    public ArrayList<Integer> calcLimitedLaneNumber(IVehicle vehi) {
        // 判断是否为指定类型车辆且在指定道路的0号车道
        if (vehi.vehicleTypeCode() == 8 &&
                vehi.roadId() == 5 &&
                vehi.vehicleDriving().laneNumber() == 0) {

            // 对于获得换道允许的车辆或距离加速车道终点不足10m的，解除车道限制
            if (getPermission.contains(vehi.id()) ||
                    (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < m2p(10))) {
                return new ArrayList<>(); // 返回空ArrayList
            }
            // 限制车道：将List转换为ArrayList
            return new ArrayList<>(Arrays.asList(1, 2, 3));
        }
        return new ArrayList<>(); // 返回空ArrayList
    }

    /**
     * 每个计算周期结束后调用的方法
     * 实现自定义换道决策逻辑
     */
    @Override
    public void afterOneStep() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();

        // 当前仿真计算批次
        long batchNum = simuiface.batchNumber();
        // 当前正在运行的所有车辆
        List<IVehicle> allVehicles = simuiface.allVehiStarted();

        /**
         * 自定义换道决策方式——基于与目标车道前后车的车头时距
         * 1. 目标车道无前车/后车——执行左换道
         * 2. 目标车道间隙大于设定值（前向间隙≥0.9s，后向间隙≥1.5s）——执行换道
         */

        // 获取加速车道内侧的主线1号车道上的所有车辆
        List<IVehicle> targetLane = simuiface.vehisInLink(5).stream()
                .filter(vehi -> vehi.vehicleDriving().laneNumber() == 1)
                .collect(Collectors.toList());

        // 获取上游主线对应1号车道上的所有车辆
        List<IVehicle> postLane = simuiface.vehisInLink(1).stream()
                .filter(vehi -> vehi.vehicleDriving().laneNumber() == 1)
                .collect(Collectors.toList());

        // 寻找执行换道决策车辆在目标车道上的前后车
        for (IVehicle vehi : allVehicles) {
            // 判断是否为需要处理的车辆（指定类型、道路和车道）
            if (vehi.vehicleTypeCode() == 10 &&
                    vehi.roadId() == 5 &&
                    vehi.vehicleDriving().laneNumber() == 0) {

                IVehicle leftFront = null;  // 目标车道前车
                IVehicle leftRear = null;   // 目标车道后车

                // 在加速车道内侧的主线1号车道上寻找前后车
                for (IVehicle vehiInTL : targetLane) {
                    double currDist = vehi.vehicleDriving().currDistanceInRoad();
                    double targetDist = vehiInTL.vehicleDriving().currDistanceInRoad();

                    // 寻找前车（位置在当前车辆前方）
                    if (currDist < targetDist) {
                        if (leftFront == null ||
                                leftFront.vehicleDriving().currDistanceInRoad() > targetDist) {
                            leftFront = vehiInTL;
                        }
                    }
                    // 寻找后车（位置在当前车辆后方）
                    else if (currDist > targetDist) {
                        if (leftRear == null ||
                                leftRear.vehicleDriving().currDistanceInRoad() < targetDist) {
                            leftRear = vehiInTL;
                        }
                    }
                }

                // 若未找到后车，扩大范围至上游主线
                if (leftRear == null && !postLane.isEmpty()) {
                    IVehicle temp = postLane.get(0);
                    for (IVehicle vehiInPL : postLane) {
                        if (vehiInPL.vehicleDriving().distToEndpoint() <
                                temp.vehicleDriving().distToEndpoint()) {
                            temp = vehiInPL;
                        }
                    }
                    leftRear = temp;
                }

                // 计算车头时距（THW）
                double leftFrontTHW = Double.POSITIVE_INFINITY;
                double leftRearTHW = Double.POSITIVE_INFINITY;

                // 计算与前车的车头时距
                if (leftFront != null) {
                    double vehiSpeed = p2m(vehi.currSpeed());
                    double frontSpeed = p2m(leftFront.currSpeed());

                    if (vehiSpeed > frontSpeed) {
                        double distanceDiff = p2m(leftFront.vehicleDriving().currDistanceInRoad()) -
                                p2m(vehi.vehicleDriving().currDistanceInRoad());
                        leftFrontTHW = distanceDiff / (vehiSpeed - frontSpeed);
                    }
                }

                // 计算与后车的车头时距
                if (leftRear != null) {
                    double vehiSpeed = p2m(vehi.currSpeed());
                    double rearSpeed = p2m(leftRear.currSpeed());

                    if (vehiSpeed < rearSpeed) {
                        double distanceDiff = p2m(vehi.vehicleDriving().currDistanceInRoad()) -
                                p2m(leftRear.vehicleDriving().currDistanceInRoad());
                        leftRearTHW = distanceDiff / (rearSpeed - vehiSpeed);
                    }
                }

                // 打印调试信息
                if (leftFront != null && leftRear != null) {
                    System.out.printf("%d, %d, %d, %d, %.2f, %.2f%n",
                            batchNum, vehi.id(), leftFront.id(), leftRear.id(),
                            leftFrontTHW, leftRearTHW);
                } else {
                    System.out.printf("%d, %d, null, null, %.2f, %.2f%n",
                            batchNum, vehi.id(), leftFrontTHW, leftRearTHW);
                }

                // 检查是否满足换道条件（可接受间隙）
                if (leftFrontTHW >= 0.9 && leftRearTHW >= 1.5) {
                    System.out.println("允许换道");
                    getPermission.add(vehi.id());
                }
            }
        }
    }
}