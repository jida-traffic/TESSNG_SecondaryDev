package TESS_Java_APIDemo.four.EmergencyLaneOpeningControl;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MySimulator extends CustomerSimulator {
    private boolean openEmergencyLaneFlag = false;

    public MySimulator() {
        super();// 初始化逻辑
    }

    /**
     * 动态创建事故区
     * @param roadId 事故区所在的道路ID
     * @param location 事故区位置，事故区起点距路段起点的距离
     * @param zoneLength 事故区长度
     * @param fromLaneNumbers 事故区所在车道列表
     * @param duration 事故区持续时间，单位秒
     */
    public void dynaCreateAccidentZone(int roadId, double location, double zoneLength,
                                       ArrayList<Integer> fromLaneNumbers, int duration) {
        DynaAccidentZoneParam accidentZoneObj = new DynaAccidentZoneParam();

        // 设置事故区属性
        accidentZoneObj.setRoadId(roadId);
        accidentZoneObj.setName(roadId + "路段事故区");
        accidentZoneObj.setLocation(location);
        accidentZoneObj.setLength(zoneLength);
        accidentZoneObj.setMlFromLaneNumber(fromLaneNumbers);
        accidentZoneObj.setDuration(duration);

        // 创建事故区
        TESSNG.tessngIFace().netInterface().createAccidentZone(accidentZoneObj);
        System.out.println(accidentZoneObj.getName());
    }

    /**
     * 判断车辆是左变道还是右变道
     * @param vehi 运行车辆
     * @return 变道方向："left"、"right"或"noChange"
     */
    public String judgeVehicleLaneChangeDirection(IVehicle vehi) {
        ILane lane = vehi.lane();
        Point vehiCurrPos = vehi.pos();
        double vehiCurrDistToStart = lane.distToStartPoint(vehiCurrPos);
        ArrayList<Point> laneCenterBreakPoints = lane.centerBreakPoints();
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
            System.out.println("FindError: can't find the segment, relevant info: " +
                    vehiSegmentIndex + ", " + vehiCurrDistToStart + ", " + vehiCurrPos);
            return "noChange";
        }
    }

    /**
     * 过载父类方法，撤销变道，可用于强制车辆不变道
     * @param vehi 车辆对象
     * @return 是否撤销变道
     */
    @Override
    public boolean reCalcDismissChangeLane(IVehicle vehi) {
        if (!openEmergencyLaneFlag) {
            // 封闭应急车道
            if (vehi.roadId() == 1044) {
                ILane lane = vehi.lane();
                if (lane.number() == 1) {
                    return "right".equals(judgeVehicleLaneChangeDirection(vehi));
                }
            }
        }
        return false;
    }

    /**
     * 过载的父类方法，TESS NG在每个计算周期结束后调用此方法
     */
    @Override
    public void afterOneStep() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        // 当前仿真计算批次
        long batchNum = simuiface.batchNumber();
        // 当前已仿真时间，单位：毫秒
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        // 当前正在运行车辆列表
        ArrayList<IVehicle> allVehicles = simuiface.allVehiStarted();

        /** 应急事件2-应急车道开放 */
        // 应急车道开放时设置为true，并删除路段决策点
        if (simuTime > 500 * 1000 && simuTime <= 1000 * 1000) {
            this.openEmergencyLaneFlag = true;
        }

        if (this.openEmergencyLaneFlag) {
            String runInfo = "提示：\nL1033路段发生事故，车辆拥堵加剧，应急车道开放！\nL1044路段发生事故，车辆拥堵加剧，应急车道开放！";
            System.out.println(runInfo);
            // 获取所有决策点并删除其路径
            ArrayList<IDecisionPoint> decisionPoints = netiface.decisionPoints();
            for (IDecisionPoint decisionPoint : decisionPoints) {
                ArrayList<IRouting> routings = decisionPoint.routings();
                for (IRouting routing : routings) {
                    if (netiface.removeDeciRouting(decisionPoint, routing)) {
                        System.out.println("应急车道开放，删除路段决策点！");
                    }
                }
            }
        }
    }

    private void sendRunInfo(String info) {
        // 实现运行信息通知逻辑（替代原QT信号）
        System.out.println(info);
    }
}
