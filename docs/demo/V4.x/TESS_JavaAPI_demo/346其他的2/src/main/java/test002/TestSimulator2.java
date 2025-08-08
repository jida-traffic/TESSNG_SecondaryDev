package test002;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;

public class TestSimulator2 extends JCustomerSimulator {
    //车辆方阵的车辆数
    int mrSquareVehiCount = 28;
    //飞机速度，飞机后面的车辆速度会被设定为此数据
    double mrSpeedOfPlane = 0;
    //当前正在仿真计算的路网名称
    String mNetPath;
    //相同路网连续仿真次数
    int mSimuCount = 0;

    public TestSimulator2(){
        super();
    }

    public long getCptr(){
        return JCustomerSimulator.getCPtr(this);
    }

    @Override
    public void initVehicle(IVehicle vehi){
        vehi.setIsPermitForVehicleDraw(true);
        vehi.setIsPermitForVehicleDraw(true);
        vehi.setSteps_calcLimitedLaneNumber(10);
        vehi.setSteps_calcChangeLaneSafeDist(10);
        vehi.setSteps_reCalcdesirSpeed(1);
        vehi.setSteps_reSetSpeed(1);

        // 车辆 ID，不含首位数，首位数与车辆来源有关，如发车点、公交线路
        long tmpId = vehi.id() % 100000;
        // 车辆所在路段名或连接段名
        String roadName = vehi.roadName();
        // 车辆所在路段 ID 或连接段 ID
        long roadId = vehi.roadId();
        if ("曹安公路".equals(roadName)) {
            // 飞机
            if (tmpId == 1) {
                vehi.setVehiType(12);
                vehi.initLane(3, TESSNG.m2p(105), 0);
            }
            // 工程车
            else if (tmpId >= 2 && tmpId <= 8) {
                vehi.setVehiType(8);
                vehi.initLane((int)(tmpId - 2) % 7, TESSNG.m2p(80), 0);
            }
            // 消防车
            else if (tmpId >= 9 && tmpId <= 15) {
                vehi.setVehiType(9);
                vehi.initLane((int)(tmpId - 2) % 7, TESSNG.m2p(65), 0);
            }
            // 消防车
            else if (tmpId >= 16 && tmpId <= 22) {
                vehi.setVehiType(10);
                vehi.initLane((int)(tmpId - 2) % 7, TESSNG.m2p(50), 0);
            }
            // 最后两队列小车
            else if (tmpId == 23) {
                vehi.setVehiType(1);
                vehi.initLane(1, TESSNG.m2p(35), 0);
            } else if (tmpId == 24) {
                vehi.setVehiType(1);
                vehi.initLane(5, TESSNG.m2p(35), 0);
            } else if (tmpId == 25) {
                vehi.setVehiType(1);
                vehi.initLane(1, TESSNG.m2p(20), 0);
            } else if (tmpId == 26) {
                vehi.setVehiType(1);
                vehi.initLane(5, TESSNG.m2p(20), 0);
            } else if (tmpId == 27) {
                vehi.setVehiType(1);
                vehi.initLane(1, TESSNG.m2p(5), 0);
            } else if (tmpId == 28) {
                vehi.setVehiType(1);
                vehi.initLane(5, TESSNG.m2p(5), 0);
            }
            // 最后两列小车的长度设为一样长，这个很重要，如果车长不一样长，加上导致的前车距就不一样，会使它们变道轨迹长度不一样，就会乱掉
            if (tmpId >= 23 && tmpId <= 28) {
                vehi.setLength(TESSNG.m2p(4.5), true);
            }
        }
    }

    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netInterface = iface.netInterface();
        SimuInterface simuIFace = iface.simuInterface();
        long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
        if(simuTime % 10000 != 0){
            //return;
        }
        ArrayList<IVehicle> vehis = simuIFace.allVehicle();
        for(int i = 0, size = vehis.size(); i < size; ++i){
            IVehicle vehi = vehis.get(i);
            long id = vehi.id();
            double len = vehi.length();
            String name = vehi.name();
            double speed = vehi.currSpeed();
            double acce = vehi.acce();
            vehi.delete();
        }

        ArrayList<VehicleStatus> statusList = simuIFace.getVehisStatus();
        for(int i = 0, size = statusList.size(); i < size; ++i){
            VehicleStatus status = statusList.get(i);
            long roadId = status.getMrRoadId();
            long id = status.getVehiId();
            double speed = status.getMrSpeed();
            double angle = status.getMrAngle();
            double acce = status.getMrAcce();
            status.delete();
        }
    }

    @Override
    public void afterStep(IVehicle pIVehicle) {
        //System.out.println("vehiid:" + pIVehicle.id());
    }

    @Override
    public boolean isStopDriving(IVehicle pIVehicle) {
        return false;
    }

    @Override
    public boolean ref_reCalcAngle(IVehicle pIVehicle, ObjReal ref_outAngle){
        double angle = ref_outAngle.getValue();
        ref_outAngle.setValue(45);
        return false;
    }

    // 过载的父类方法，重新计算期望速度
// vehi：车辆
// ref_desirSpeed：返回结果,ref_desirSpeed.value是TESS NG计算好的期望速度，可以在此方法改变它
// return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
    public boolean ref_reCalcdesirSpeed(IVehicle vehi, ObjReal ref_desirSpeed) {
        int tmpId = (int) (vehi.id() % 100000);
        String roadName = vehi.roadName();
        if ("曹安公路".equals(roadName)) {
            if (tmpId <= this.mrSquareVehiCount) {
                TessInterface iface = TESSNG.tessngIFace();
                SimuInterface simuIFace = iface.simuInterface();
                long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
                if (simuTime < 5 * 1000) {
                    ref_desirSpeed.setValue(0);
                } else if (simuTime < 10 * 1000) {
                    ref_desirSpeed.setValue(TESSNG.m2p(20 / 3.6));
                } else {
                    ref_desirSpeed.setValue(TESSNG.m2p(40 / 3.6));
                }
                return true;
            }
        }
        return false;
    }

    // 过载的父类方法，重新计算跟驰参数：时距及安全距离
    // vehi:车辆
    // ref_inOutSi，安全时距，ref_inOutSi.value是TESS NG已计算好的值，此方法可以改变它
    // ref_inOutSd，安全距离，ref_inOutSd.value是TESS NG已计算好的值，此方法可以改变它
    // return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
    public boolean ref_reSetFollowingParam(IVehicle vehi, ObjReal ref_inOutSi, ObjReal ref_inOutSd) {
        String roadName = vehi.roadName();
        if ("连接段2".equals(roadName)) {
            ref_inOutSd.setValue(TESSNG.m2p(30));
            return true;
        }
        return false;
    }

    // 过载的父类方法，重新计算加速度
    // vehi：车辆
    // inOutAce：加速度，inOutAcce.value是TESS NG已计算的车辆加速度，此方法可以改变它
    // return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
    public boolean ref_reSetAcce(IVehicle vehi, ObjReal inOutAcce) {
        String roadName = vehi.roadName();
        if ("连接段1".equals(roadName)) {
            if (vehi.currSpeed() > TESSNG.m2p(20 / 3.6)) {
                inOutAcce.setValue(TESSNG.m2p(-5));
                return true;
            } else if (vehi.currSpeed() > TESSNG.m2p(20 / 3.6)) {
                inOutAcce.setValue(TESSNG.m2p(-1));
                return true;
            }
        }
        return false;
    }

    // 过载的父类方法，重新计算当前速度
    // vehi:车辆
    // ref_inOutSpeed，速度ref_inOutSpeed.value，是已计算好的车辆速度，此方法可以改变它
    // return结果：False：TESS NG忽略此方法作的修改，True：TESS NG采用此方法所作修改
    public boolean ref_reSetSpeed(IVehicle vehi, ObjReal ref_inOutSpeed) {
        long tmpId = vehi.id() % 100000;
        String roadName = vehi.roadName();
        if ("曹安公路".equals(roadName)) {
            if (tmpId == 1) {
                this.mrSpeedOfPlane = vehi.currSpeed();
            } else if (tmpId >= 2 && tmpId <= this.mrSquareVehiCount) {
                ref_inOutSpeed.setValue(this.mrSpeedOfPlane);
            }
            return true;
        }
        return false;
    }

    // 过载的父类方法，计算是否要左自由变道
    // vehi:车辆
    // return结果，True：变道、False：不变道
    public boolean reCalcToLeftFreely(IVehicle vehi) {
        // 车辆到路段终点距离小于20米不变道
        if (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < TESSNG.m2p(20)) {
            return false;
        }
        long tmpId = vehi.id() % 100000;
        String roadName = vehi.roadName();
        if ("曹安公路".equals(roadName)) {
            if (tmpId >= 23 && tmpId <= 28) {
                int laneNumber = vehi.vehicleDriving().laneNumber();
                if (laneNumber == 1 || laneNumber == 4) {
                    return true;
                }
            }
        }
        return false;
    }

    // 过载的父类方法，计算是否要右自由变道
    // vehi:车辆
    // return结果，True：变道、False：不变道
    public boolean reCalcToRightFreely(IVehicle vehi) {
        long tmpId = vehi.id() % 100000;
        // 车辆到路段终点距离小于20米不变道
        if (vehi.vehicleDriving().distToEndpoint() - vehi.length() / 2 < TESSNG.m2p(20)) {
            return false;
        }
        String roadName = vehi.roadName();
        if ("曹安公路".equals(roadName)) {
            if (tmpId >= 23 && tmpId <= 28) {
                int laneNumber = vehi.vehicleDriving().laneNumber();
                if (laneNumber == 2 || laneNumber == 5) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void afterStop() {
        System.out.println("after stop");
    }


}
