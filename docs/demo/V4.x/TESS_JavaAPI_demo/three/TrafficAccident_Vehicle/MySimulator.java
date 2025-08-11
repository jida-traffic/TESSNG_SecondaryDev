package three.TrafficAccident_Vehicle;

import com.jidatraffic.tessng.*;
import three.TrafficAccident_Area.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jidatraffic.tessng.TESSNG.m2p;


public class MySimulator extends  JCustomerSimulator {
    private final Random random = new Random();

    public MySimulator(){
        super();
    }

    @Override
    public void initVehicle(IVehicle vehi) {
        // 车辆ID处理（取后5位）
        long tmpId = vehi.id() % 100000;
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuIFace = iface.simuInterface();

        // 获取路段名称
        String roadName = vehi.roadName();

        if ("路段1".equals(roadName)) {
            // 初始化速度（m2p转换）
            vehi.initSpeed(m2p(150));

            // 根据车辆ID设置车辆类型
            if (tmpId == 1) {
                vehi.setVehiType(2);
            } else if (tmpId >= 2 && tmpId <= 4) {
                vehi.setVehiType(8);
            } else if (tmpId >= 5 && tmpId <= 6) {
                vehi.setVehiType(9);
            } else if (tmpId >= 7 && tmpId <= 9) {
                vehi.setVehiType(10);
            } else if (tmpId >= 9 && tmpId <= 12) {
                vehi.setVehiType(8);
            } else if (tmpId >= 12 && tmpId <= 22) {
                vehi.setVehiType(1);
            } else if (tmpId >= 22 && tmpId <= 24) {
                vehi.setVehiType(9);
            } else if (tmpId >= 25 && tmpId <= 50) {
                vehi.setVehiType(1);
            } else if (tmpId >= 51 && tmpId <= 54) {
                vehi.setVehiType(10);
            } else if (tmpId >= 55 && tmpId <= 56) {
                vehi.setVehiType(8);
            } else {
                vehi.setVehiType(1);
            }
        }
    }

    @Override
    public boolean reSetFollowingParam(IVehicle vehi, ObjReal refInOutSd, ObjReal inOutSafeDistance) {
        String roadName = vehi.roadName();
        if ("连接段1".equals(roadName)) {
            refInOutSd.setValue(m2p(5));
            return true;
        }
        return false;
    }
    @Override
    public boolean reSetSpeed(IVehicle vehi, ObjReal inOutSpeed) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();
        long tmpId = vehi.id() % 100000;
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        // 转换路段指针为ILink对象
        if (tmpId == 11 && simuTime >= 78000 && simuTime <= 308000) {
            System.out.println(tmpId + ", " + vehi.vehicleTypeCode());
            if (vehi.vehicleTypeCode() != 12) {
                inOutSpeed.setValue(m2p(0));
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Integer> calcLimitedLaneNumber(IVehicle vehi) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        // 小车（类型1）在特定路段的车道限制
        if (vehi.vehicleTypeCode() == 1 && vehi.roadIsLink()) {
            ILink link = vehi.section().castToLink();
            if (link != null && link.id() == 3 && simuTime >= 25000) {
                // 返回空列表表示无限制
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isStopDriving(IVehicle vehi) {
        if (vehi.roadId() == 2) {
            // 获取车头到终点的距离
            double dist = vehi.vehicleDriving().distToEndpoint(true);
            // 距离小于5米时停止运行
            if (dist < m2p(5)) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        long batchNum = simuiface.batchNumber();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        long startRealtime = simuiface.startMSecsSinceEpoch();
        List<IVehicle> allVehi = simuiface.allVehiStarted();
        List<IVehicle> vehisInLink1 = simuiface.vehisInLink(1);
        System.out.println(allVehi);

        // 动态发车（每50个批次一次）
        if (batchNum % 50 == 1) {
            System.out.println(batchNum);
            // 生成随机颜色
            String color = generateRandomColor();
            DynaVehiParam dvp = new DynaVehiParam();
            dvp.setVehiTypeCode(random.nextInt(5) + 1); // 1-5之间的随机类型
            dvp.setRoadId(6);
            dvp.setLaneNumber(random.nextInt(4)); // 0-3车道
            dvp.setDist(50);
            dvp.setSpeed(20);
            dvp.setColor(color);

            IVehicle vehi1 = simuiface.createGVehicle(dvp);
            System.out.println(vehi1);
            if(vehi1==null){
                System.out.println("车辆空");
            }
        }
    }

    // 生成随机颜色（类似Python的hex处理）
    private String generateRandomColor() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        System.out.println(String.format("#%02X%02X%02X", r, g, b));
        return String.format("#%02X%02X%02X", r, g, b);
    }

}