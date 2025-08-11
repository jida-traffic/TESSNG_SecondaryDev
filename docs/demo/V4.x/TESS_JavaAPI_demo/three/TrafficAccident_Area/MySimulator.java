package three.TrafficAccident_Area;

import com.jidatraffic.tessng.*;

import java.util.*;



public class MySimulator extends  CustomerSimulator {
        private boolean openEmergencyLaneFlag = false;

        public MySimulator() {
            super();// 初始化逻辑
        }

        // 动态创建事故区
        public void dynaCreateAccidentZone(int roadId, double location, double zoneLength,
                                           ArrayList<Integer> lFromLaneNumber, int duration) {
            DynaAccidentZoneParam accidentZoneObj = new DynaAccidentZoneParam();
            accidentZoneObj.setRoadId(roadId);
            accidentZoneObj.setName(roadId + "路段事故区");
            accidentZoneObj.setLocation(location);
            accidentZoneObj.setLength(zoneLength);
            accidentZoneObj.setMlFromLaneNumber(lFromLaneNumber);
            accidentZoneObj.setDuration(duration);

            // 创建事故区
            TessInterface iface = TESSNG.tessngIFace();
            iface.netInterface().createAccidentZone(accidentZoneObj);
        }

        // 判断车辆变道方向
        public String judgeVehicleLaneChangeDirection(IVehicle vehi) {
            ILane lane = vehi.lane();
            Point vehiCurrPos = vehi.pos();
            double vehiCurrDistToStart = lane.distToStartPoint(vehiCurrPos);
            List<Point> laneCenterBreakPoints = lane.centerBreakPoints();
            int vehiSegmentIndex = -1;

            // 获取车辆所在的道路分段号
            for (int index = 0; index < laneCenterBreakPoints.size(); index++) {
                Point centerBreakPoint = laneCenterBreakPoints.get(index);
                double breakPointDist = lane.distToStartPoint(centerBreakPoint);
                if (vehiCurrDistToStart < breakPointDist) {
                    vehiSegmentIndex = index;
                    break;
                }
            }

            if (vehiSegmentIndex > 0 && vehiSegmentIndex < laneCenterBreakPoints.size()) {
                Point startBreakPoint = laneCenterBreakPoints.get(vehiSegmentIndex - 1);
                Point endBreakPoint = laneCenterBreakPoints.get(vehiSegmentIndex);

                String vehiDirection = functions.carPositionRoad(startBreakPoint, endBreakPoint, vehiCurrPos);
                double breakLaneAngle = functions.calculateAngle(startBreakPoint, endBreakPoint);
                double vehicleAngle = vehi.angle();

                if ("right".equals(vehiDirection) && vehicleAngle > breakLaneAngle) {
                    return "right";
                } else if ("left".equals(vehiDirection) && vehicleAngle < breakLaneAngle) {
                    return "left";
                } else {
                    return "noChange";
                }
            } else {
                System.out.printf("FindError: can't find the segment, relevant info: %d, %f, %s%n",
                        vehiSegmentIndex, vehiCurrDistToStart, vehiCurrPos);
                return "error";
            }
        }

        // 每个计算周期结束后调用
        @Override
        public void afterOneStep() {
            TessInterface iface = TESSNG.tessngIFace();
            SimuInterface simuiface = iface.simuInterface();
            NetInterface netiface = iface.netInterface();

            long batchNum = simuiface.batchNumber();
            long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
            List<IVehicle> allVehi = simuiface.allVehiStarted();
            /* 应急事件1-车辆事故 */
            if (simuTime == 60 * 1000) {
                int accidentRoadId = 337;
                double accidentLocation = 100;
                double accidentLength = 100;
                ArrayList<Integer> accidentLanes = new ArrayList<>();
                accidentLanes.add(2);
                int accidentDuration = 300;

                this.dynaCreateAccidentZone(accidentRoadId, accidentLocation,
                        accidentLength, accidentLanes, accidentDuration);

                String runInfo = "提示：\nL337路段100m处最左侧车道发生事故，请提前变道！";
                System.out.println(runInfo);
            }

            if (simuTime == (60 + 300) * 1000) {
                String runInfo = "提示：\nL337路段100m处最左侧车道事故已处理完毕，请正常通行！";
                System.out.println(runInfo);
            }
        }
    }