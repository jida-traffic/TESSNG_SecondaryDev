package Intelligent_Connected_and_Autonomous_Driving.Connected_and_Autonomous_Vehicle_Platooning;

import com.jidatraffic.tessng.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static com.jidatraffic.tessng.TESSNG.m2p;

public class MySimulator extends JCustomerSimulator {

    // 记录CV是否已经加入编队，键为车辆ID，值为是否在编队中(1表示在编队中，0表示不在)
    private Map<Long, Integer> platoon = new HashMap<>();
    private Random random = new Random();

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
     * 重新计算跟驰参数：时距及安全距离
     */
    @Override
    public boolean ref_reSetFollowingParam(IVehicle vehi, ObjReal ref_inOutSi, ObjReal ref_inOutSd) {
        IVehicle front = vehi.vehicleFront();
        // 若当前车与其前车均为CV，则修改其安全时距至0.8s
        if (vehi.vehicleTypeCode() == 13 && front != null && front.vehicleTypeCode() == 13) {
            ref_inOutSi.setValue(0.8);
            return true;
        }
        return false;
    }

    /**
     * 自定义CV驾驶行为，重新计算期望速度
     */
    @Override
    public boolean ref_reCalcdesirSpeed(IVehicle vehi, ObjReal ref_desirSpeed) {
        if (vehi.vehicleTypeCode() == 13) { // 仅对CV车辆(类型13)生效
            IVehicle front = vehi.vehicleFront();

            if (front == null) {
                // 前方无车，期望速度提升至120km/h(转换为m/s后再转为仿真单位)
                ref_desirSpeed.setValue(m2p(120.0 / 3.6));
                return true;
            }
            // 前方为CV
            else if (front.vehicleTypeCode() == 13) {
                if (vehi.vehiHeadwayFront() > 1) {
                    // 车头时距>1s，期望速度提升至前车+20km/h
                    ref_desirSpeed.setValue(front.currSpeed() + m2p(20.0 / 3.6));
                    return true;
                } else {
                    // 车头时距<1s，期望速度与前车保持一致
                    ref_desirSpeed.setValue(front.currSpeed());
                    return true;
                }
            }
            // 前方为非CV
            else {
                if (vehi.vehiHeadwayFront() > 2) {
                    // 车头时距>2s，期望速度提升至前车+20km/h
                    ref_desirSpeed.setValue(front.currSpeed() + m2p(20.0 / 3.6));
                    return true;
                } else {
                    // 车头时距<2s，期望速度与前车保持一致
                    ref_desirSpeed.setValue(front.currSpeed());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算是否要左自由变道
     */
    @Override
    public boolean reCalcToLeftFreely(IVehicle vehi) {
        // 车辆到路段终点距离小于20米不变道
        if (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < m2p(20)) {
            return false;
        }

        IVehicle leftFront = vehi.vehicleLFront();
        IVehicle leftRear = vehi.vehicleLRear();

        // 若当前车为CV，且左前车/左后车为CV，则执行左变道
        if (vehi.vehicleTypeCode() == 13 &&
                ((leftFront != null && leftFront.vehicleTypeCode() == 13) ||
                        (leftRear != null && leftRear.vehicleTypeCode() == 13))) {

            // 更新platoon字典，表示该车已经处于编队中
            platoon.put(vehi.id(), 1);

            // 更新前车和后车的编队状态
            if (leftFront != null && leftFront.vehicleTypeCode() == 13) {
                platoon.put(leftFront.id(), 1);
            }
            if (leftRear != null && leftRear.vehicleTypeCode() == 13) {
                platoon.put(leftRear.id(), 1);
            }

            return true;
        }

        return false;
    }

    /**
     * 计算车辆当前限制车道序号列表
     * 限制已经向左换道完成编队的CV执行右换道驶离编队
     */
    @Override
    public ArrayList<Integer> calcLimitedLaneNumber(IVehicle vehi) {
        // 检查车辆是否为CV且已在编队中
        if (vehi.vehicleTypeCode() == 13 && platoon.containsKey(vehi.id()) && platoon.get(vehi.id()) == 1) {
            int currentLane = vehi.vehicleDriving().laneNumber();
            ArrayList<Integer> limitedLanes = new ArrayList<>();

            if (currentLane == 1) {
                // 1号车道的编队，限制其右变道至0号车道
                limitedLanes.add(0);
            } else if (currentLane == 2) {
                // 2号车道的编队，限制其右变道至0号和1号车道
                limitedLanes.add(0);
                limitedLanes.add(1);
            }

            return limitedLanes;
        }

        return new ArrayList<>();
    }

    /**
     * 每个计算周期结束后调用的方法
     * 实现动态发车，控制CV和普通车辆的渗透率
     */
    @Override
    public void afterOneStep() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();

        // 当前仿真计算批次
        long batchNum = simuiface.batchNumber();

        // 每3个批次发一辆车，控制发车频率
        if (batchNum % 3 == 0) {
            long second = batchNum / 3; // 归一化，用于控制渗透率

            // 控制CV渗透率(约50%)
            if (second % 10 < 5) {
                // 生成CV车辆(类型13)，颜色为蓝色系
                String color = "#FF69B4"; // 粉色系CV车辆
                DynaVehiParam dvp = new DynaVehiParam();
                dvp.setVehiTypeCode(13);
                dvp.setRoadId(1);
                dvp.setLaneNumber(random.nextInt(4));
                dvp.setDist(TESSNG.m2p(0.01));
                dvp.setSpeed(TESSNG.m2p(80.0 / 3.6));
                dvp.setColor(color);
                IVehicle vehi = simuiface.createGVehicle(dvp);
                if (vehi != null) {
                    platoon.put(vehi.id(), 0); // 初始状态：未加入编队
                }
            } else {
                // 生成普通车辆(类型1)，颜色为天蓝色
                String color = "#87CEEB";
                DynaVehiParam dvp = new DynaVehiParam();
                dvp.setVehiTypeCode(1);
                dvp.setRoadId(1);
                dvp.setLaneNumber(random.nextInt(4));
                dvp.setDist(TESSNG.m2p(0.01));
                dvp.setSpeed(TESSNG.m2p(60.0 / 3.6));
                dvp.setColor(color);
                IVehicle vehi = simuiface.createGVehicle(dvp);

            }
        }
    }
}
