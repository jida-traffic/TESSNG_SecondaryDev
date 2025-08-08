package Intelligent_Connected_and_Autonomous_Driving.Connected_and_Intelligent_Vehicle_Accident_Avoidance;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.DynaVehiParam;
import com.jidatraffic.tessng.qt.RectF;

import java.util.Random;

public class MySimulator extends JCustomerSimulator {

    // 车辆方阵的车辆数
    private int mrSquareVehiCount = 28;
    private long simuTime = 0;
    private Random random = new Random();

    public MySimulator() {
        super();
    }

    /**
     * 过载的父类方法， 初始化车辆，在车辆启动上路时被TESS NG调用一次
     */
    @Override
    public void initVehicle(IVehicle vehi) {
        // 车辆ID，不含首位数，首位数与车辆来源有关，如发车点、公交线路
        long tmpId = vehi.id() % 100000;
        // 车辆所在路段名或连接段名
        String roadName = vehi.roadName();
        // 车辆所在路段ID或连接段ID
        long roadId = vehi.roadId();
        if ("公路1".equals(roadName)) {
            if (tmpId == 1) {
                vehi.setVehiType(9);
                vehi.initLane(1, TESSNG.m2p(60), 5);
                vehi.initSpeed(0);
            } else if (tmpId == 2) {
                vehi.setVehiType(1);
                vehi.initLane(1, TESSNG.m2p(10), 8);
            } else if (tmpId == 3) {
                vehi.setVehiType(2);
                vehi.initLane(0, TESSNG.m2p(10), 5);
            }
        }
    }

    /**
     * 过载的父类方法重新计算加速度
     */
    @Override
    public boolean ref_calcAcce(IVehicle vehi, ObjReal acce) {
        return false;
    }

    /**
     * 过载的父类方法，重新计算期望速度
     * @param vehi 车辆
     * @param ref_desirSpeed 返回结果,ref_desirSpeed.value是TESS NG计算好的期望速度，可以在此方法改变它
     * @return 结果：false：TESS NG忽略此方法作的修改，true：TESS NG采用此方法所作修改
     */
    @Override
    public boolean ref_reCalcdesirSpeed(IVehicle vehi, ObjReal ref_desirSpeed) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuIFace = iface.simuInterface();
        simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
        long tmpId = vehi.id() % 100000;
        String roadName = vehi.roadName();

        if ("公路1".equals(roadName) && simuTime >= 6000) {
            if (vehi.vehicleTypeCode() == 9) {
                ref_desirSpeed.setValue(TESSNG.m2p(50));
                return true;
            } else {
                ref_desirSpeed.setValue(TESSNG.m2p(60));
                return true;
            }
        }
        return false;
    }

    /**
     * 过载的父类方法，重新计算加速度
     * @param vehi 车辆
     * @param inOutAcce 加速度，inOutAcce.value是TESS NG已计算的车辆加速度，此方法可以改变它
     * @return 结果：false：TESS NG忽略此方法作的修改，true：TESS NG采用此方法所作修改
     */
    @Override
    public boolean ref_reSetAcce(IVehicle vehi, ObjReal inOutAcce) {
        String roadName = vehi.roadName();
        if ("公路1".equals(roadName) && simuTime >= 6000) {
            // 重设加速度实现超车
            if (vehi.vehicleTypeCode() == 1) {
                inOutAcce.setValue(TESSNG.m2p(8));
                return true;
            } else {
                inOutAcce.setValue(TESSNG.m2p(3));
                return true;
            }
        }
        return false;
    }
    /**
     * 过载的父类方法，重新计算当前速度
     * @param vehi 车辆
     * @param ref_inOutSpeed 速度ref_inOutSpeed.value，是已计算好的车辆速度，此方法可以改变它
     * @return 结果：false：TESS NG忽略此方法作的修改，true：TESS NG采用此方法所作修改
     */
    @Override
    public boolean ref_reSetSpeed(IVehicle vehi, ObjReal ref_inOutSpeed) {
        long tmpId = vehi.id() % 100000;
        String roadName = vehi.roadName();
        // 重设期望速度实现超车
        if ("公路1".equals(roadName) && simuTime >= 6000) {
            if (vehi.vehicleTypeCode() == 1) {
                ref_inOutSpeed.setValue(TESSNG.m2p(32));
                return true;
            } else {
                ref_inOutSpeed.setValue(TESSNG.m2p(30));
                return true;
            }
        }
        return false;
    }

    /**
     * 过载的父类方法，计算是否要右自由变道
     * @param vehi 车辆
     * @return 结果，true：变道、false：不变道
     */
    @Override
    public boolean reCalcToRightFreely(IVehicle vehi) {
        long tmpId = vehi.id() % 100000;
        // 车辆到路段终点距离小于20米不变道
        if (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < TESSNG.m2p(20)) {
            return false;
        }
        String roadName = vehi.roadName();
        if (simuTime >= 6000) {
            // 小于雷达探测距离，变道，开始超车
            if (vehi.vehicleTypeCode() == 10) {
                if (vehi.vehiDistFront() <= TESSNG.m2p(65)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        long carType = vehi.vehicleTypeCode();
        if (carType == 9) {
            return true;
        }
        return false;
    }

    /**
     * 过载父类方法，停止指定车辆运行，退出路网，但不会从内存删除，会参数各种统计
     * 范例车辆进入ID等于2的路段或连接段，路离终点小于100米，则驰出路网
     */
    @Override
    public boolean isStopDriving(IVehicle vehi) {
        if (vehi.roadId() == 2) {
            // 车头到当前路段或连接段终点距离
            double dist = vehi.vehicleDriving().distToEndpoint(true);
            // 如果距终点距离小于10米，车辆停止运行退出路网
            if (dist < TESSNG.m2p(10)) {
                return false;
            }
        }
        return false;
    }

    /**
     * 调整车辆角度
     */
    @Override
    public boolean boundingRect(IVehicle vehi, RectF outRect) {
        if (vehi.vehicleTypeCode() == 1) {
            if (vehi != null) {
                double length = vehi.length() + 200;
                double w = length * 2;
                outRect.setLeft(TESSNG.m2p(-w/2));
                outRect.setTop(TESSNG.m2p(-w/2));
                outRect.setWidth(TESSNG.m2p(w));
                outRect.setHeight(TESSNG.m2p(w));
                return true;
            }
        }
        return false;
    }

    /**
     * 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法，大量用户逻辑在此实现，
     * 注意耗时大的计算要尽可能优化，否则影响运行效率
     */
    @Override
    public void afterOneStep() {
        // TESSNG 顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        // TESSNG 仿真子接口
        SimuInterface simuiface = iface.simuInterface();
        // TESSNG 路网子接口
        NetInterface netiface = iface.netInterface();
        // 当前仿真计算批次
        long batchNum = simuiface.batchNumber();
        // 当前已仿真时间，单位：毫秒
        simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        // 开始仿真的现实时间
        long startRealtime = simuiface.startMSecsSinceEpoch();
        // 当前正在运行车辆列表
        java.util.List<IVehicle> lAllVehi = simuiface.allVehiStarted();
        // 当前在ID为1的路段上车辆
        java.util.List<IVehicle> lVehis = simuiface.vehisInLink(1);

        // 每20个计算批次做一次小计
        if (batchNum % 20 == 0) {
            String strLinkCount = String.valueOf(netiface.linkCount());
            String strVehiCount = String.valueOf(lAllVehi.size());
            String strSimuTime = String.valueOf(simuTime);
            String runInfo = "路段数：" + strLinkCount + "\n运行车辆数：" + strVehiCount + "\n仿真时间：" + strSimuTime + "(毫秒)";
            // 信号发送需要根据具体实现来处理
            System.out.println(runInfo);
        }

        // 动态发车，不通过发车点发送，直接在路段和连接段中间某位置创建并发送，每50个计算批次发送一次
        if (batchNum % 50 == 0) {
            String r = Integer.toHexString(256 + random.nextInt(256)).substring(1).toUpperCase();
            String g = Integer.toHexString(256 + random.nextInt(256)).substring(1).toUpperCase();
            String b = Integer.toHexString(256 + random.nextInt(256)).substring(1).toUpperCase();
            String color = "#" + r + g + b;
            for (long i = 1; i <= 10; i++) {
                ILink link = netiface.findLink(i);
                if (link != null) {
                    int laneCount = link.laneCount(); // 获取该路段的车道数
                    System.out.printf("存在的路段ID：%d，车道数：%d%n", i, laneCount);
                }
            }
            // 路段上发车
            DynaVehiParam dvp = new DynaVehiParam();
            dvp.setVehiTypeCode(random.nextInt(4) + 1);
            System.out.println("车辆Code：" + dvp.getVehiTypeCode());
            dvp.setRoadId(6);
            dvp.setLaneNumber(random.nextInt(4));
            dvp.setDist(TESSNG.m2p(50));
            dvp.setSpeed(TESSNG.m2p(20));
            dvp.setColor(color);
            IVehicle vehi1 = simuiface.createGVehicle(dvp);
            if (vehi1 != null) {
                System.out.println("车道0车辆创建成功，ID: " + vehi1.id());
            } else {
                System.out.println("车道0车辆创建失败");
            }
            // 连接段上发车
            DynaVehiParam dvp2 = new DynaVehiParam();
            dvp2.setVehiTypeCode(random.nextInt(4) + 1);
            dvp2.setRoadId(3);
            dvp2.setLaneNumber(random.nextInt(1));
            dvp2.setToLaneNumber(dvp2.getLaneNumber());
            dvp2.setDist(TESSNG.m2p(50));
            dvp2.setSpeed(TESSNG.m2p(20));
            dvp2.setColor(color);
            IVehicle vehi2 = simuiface.createGVehicle(dvp2);
            if (vehi2 != null) {
                System.out.println("车道1车辆创建成功，ID: " + vehi2.id());
            } else {
                System.out.println("车道1车辆创建失败");
            }
        }
    }
}
