package Intelligent_Connected_and_Autonomous_Driving.Autonomous_Driving_Simulation;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.DynaVehiParam;
import com.jidatraffic.tessng.IVehicle;
import com.jidatraffic.tessng.IVehicle.*;
import com.jidatraffic.tessng.Point;
import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.qt.RectF;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MySimulator extends JCustomerSimulator {
    private final AtomicLong simuTime = new AtomicLong(0);
    private final int mrSquareVehiCount = 28;
    private final Random random = new Random();

    @Override
    public void initVehicle(IVehicle vehi) {
        // 允许自定义绘制车辆
        vehi.setIsPermitForVehicleDraw(true);

        long tmpId = vehi.id() % 100000;
        String roadName = vehi.roadName();

        if ("公路1".equals(roadName)) {
            switch ((int) tmpId) {
                case 4:
                    vehi.setVehiType(9);
                    vehi.initLane(1, TESSNG.m2p(47), 4);
                    vehi.initSpeed(0);
                    break;
                case 5:
                    vehi.setVehiType(1);
                    vehi.initLane(1, TESSNG.m2p(10), 9);
                    break;
                case 1:
                    vehi.setVehiType(2);
                    vehi.initLane(1, TESSNG.m2p(10), 7);
                    break;
                case 2:
                    vehi.setVehiType(3);
                    vehi.initLane(0, TESSNG.m2p(10), 8);
                    break;
                case 3:
                    vehi.setVehiType(2);
                    vehi.initLane(1, TESSNG.m2p(30), 6);
                    break;
                case 6:
                case 8:
                    vehi.setVehiType(2);
                    vehi.initLane(1, TESSNG.m2p(10), 5);
                    break;
                case 7:
                case 9:
                    vehi.setVehiType(2);
                    vehi.initLane(0, TESSNG.m2p(10), 5);
                    break;
            }
        }
    }

    @Override
    public boolean ref_calcAcce(IVehicle vehi, ObjReal acce) {
        return false;
    }

    @Override
    public boolean ref_reCalcdesirSpeed(IVehicle vehi, ObjReal ref_desirSpeed) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuIFace = iface.simuInterface();
        simuTime.set(simuIFace.simuTimeIntervalWithAcceMutiples());

        long tmpId = vehi.id() % 100000;
        String roadName = vehi.roadName();

        if ("公路1".equals(roadName) && simuTime.get() >= 6000) {
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

    @Override
    public boolean ref_reSetAcce(IVehicle vehi, ObjReal inOutAcce) {
        String roadName = vehi.roadName();

        if ("公路1".equals(roadName) && simuTime.get() >= 6000) {
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

    @Override
    public boolean ref_reSetSpeed(IVehicle vehi, ObjReal ref_inOutSpeed) {
        String roadName = vehi.roadName();

        if ("公路1".equals(roadName) && simuTime.get() >= 6000) {
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

    @Override
    public boolean reCalcToRightFreely(IVehicle vehi) {
        long tmpId = vehi.id() % 100000;
        IVehicleDriving driving = vehi.vehicleDriving();

        // 车辆到路段终点距离小于20米不变道
        if (driving.distToEndpoint() - vehi.length() / 2 < TESSNG.m2p(20)) {
            return false;
        }

        String roadName = vehi.roadName();
        if ("公路1".equals(roadName) && simuTime.get() >= 6000) {
            System.out.println("15");
            if (vehi.vehicleTypeCode() == 1 || tmpId == 5) {
                System.out.println("25");
                System.out.println(TESSNG.p2m(vehi.vehiDistFront()));
                if (vehi.vehiDistFront() <= TESSNG.m2p(50)) {
                    System.out.println("35");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isStopDriving(IVehicle vehi) {
        if (vehi.roadId() == 2) {
            // 车头到当前路段或连接段终点距离
            double dist = vehi.vehicleDriving().distToEndpoint(true);
            // 如果距终点距离小于10米，车辆停止运行退出路网
            if (dist < TESSNG.m2p(10)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean boundingRect(IVehicle vehi, RectF outRect) {
        if (vehi.vehicleTypeCode() == 1) {
            double length = vehi.length() + 200;
            double w = length * 2;
            outRect.setLeft(TESSNG.m2p(-w / 2));
            outRect.setTop(TESSNG.m2p(-w / 2));
            outRect.setWidth(TESSNG.m2p(w));
            outRect.setHeight(TESSNG.m2p(w));
            return true;
        }
        return false;
    }

    public boolean paintVehicle(IVehicle vehi, Graphics2D painter) {
        if (vehi.vehicleTypeCode() == 1) {
            painter.setColor(new Color(254, 174, 165, 200));
            painter.fillArc(-70, -70, 140, 143, 70 * 16, 40 * 16);

            painter.setColor(new Color(180, 180, 180, 200));
            painter.fill(new RoundRectangle2D.Double(-2, 2, 4, 8, 30, 30));

            painter.setColor(new Color(225, 225, 225, 200));
            painter.fill(new RoundRectangle2D.Double(-2, 4, 4, 5, 30, 30));

            return true;
        }
        return false;
    }

    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        long batchNum = simuiface.batchNumber();
        simuTime.set(simuiface.simuTimeIntervalWithAcceMutiples());
        long startRealtime = simuiface.startMSecsSinceEpoch();
        List<IVehicle> lAllVehi = simuiface.allVehiStarted();
        List<IVehicle> lVehis = simuiface.vehisInLink(1);

        // 每20个计算批次发送一次运行信息
        if (batchNum % 20 == 0) {
            String strLinkCount = String.valueOf(netiface.linkCount());
            String strVehiCount = String.valueOf(lAllVehi.size());
            String strSimuTime = String.valueOf(simuTime.get());
            String runInfo = String.format("路段数：%s\n运行车辆数：%s\n仿真时间：%s(毫秒)",
                    strLinkCount, strVehiCount, strSimuTime);
            // 这里可以通过事件或回调将信息传递出去
            System.out.println(runInfo);
        }

        // 动态发车，每50个计算批次发送一次
        if (batchNum % 50 == 1) {
            String color = String.format("#%02X%02X%02X",
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256));

            // 路段上发车
            DynaVehiParam dvp = new DynaVehiParam();
            dvp.setVehiTypeCode(random.nextInt(5) + 1);
            dvp.setRoadId(1);
            dvp.setLaneNumber(random.nextInt(4));
            dvp.setDist(TESSNG.m2p(50));
            dvp.setSpeed(TESSNG.m2p(20));
            dvp.setColor(color);
            IVehicle vehi1 = simuiface.createGVehicle(dvp);
            if (vehi1 != null) {
                System.out.println("车道1车辆创建成功，ID: " + vehi1.id());
            } else {
                System.out.println("车道1车辆创建失败");
            }
            // 连接段上发车
            DynaVehiParam dvp2 = new DynaVehiParam();
            dvp2.setVehiTypeCode(random.nextInt(5) + 1);
            dvp2.setRoadId(3);
            dvp2.setLaneNumber(random.nextInt(4));
            dvp2.setToLaneNumber(dvp2.getLaneNumber());// 默认为 - 1，如果大于等于0, 在连接段上发车
            dvp2.setDist(TESSNG.m2p(50));
            dvp2.setSpeed(TESSNG.m2p(20));
            dvp2.setColor(color);
            IVehicle vehi2 = simuiface.createGVehicle(dvp2);
            if (vehi2 != null) {
                System.out.println("车道2车辆创建成功，ID: " + vehi1.id());
            } else {
                System.out.println("车道2车辆创建失败");
            }

            // 获取车辆状态信息
            List<VehicleStatus> lVehiStatus = simuiface.getVehisStatus();
            List<SignalPhaseColor> lPhoneColor = simuiface.getSignalPhasesColor();
            List<VehiInfoCollected> lVehiInfo = simuiface.getVehisInfoCollected();
            List<VehiInfoAggregated> lVehisInfoAggr = simuiface.getVehisInfoAggregated();
            List<VehiQueueCounted> lVehiQueue = simuiface.getVehisQueueCounted();
            List<VehiQueueAggregated> lVehiQueueAggr = simuiface.getVehisQueueAggregated();
            List<VehiTravelDetected> lVehiTravel = simuiface.getVehisTravelDetected();
            List<VehiTravelAggregated> lVehiTravAggr = simuiface.getVehisTravelAggregated();
        }
    }
}