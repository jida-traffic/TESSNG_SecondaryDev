//package Fundamental_Functions.Toll_Station_Simulation;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import com.jidatraffic.tessng.*;
//
//import static com.jidatraffic.tessng.TESSNG.m2p;
//
///**
// * 自定义路网类，实现收费站点相关配置
// */
//public class MyNet extends JCustomerNet {
//    // TESS NG核心接口
//    private TessInterface iface;
//    private NetInterface netiface;
//    private SimuInterface simuiface;
//
//    /**
//     * 构造方法，初始化接口
//     */
//    public MyNet() {
//        super();
//        iface = TESSNG.tessngIFace();
//        netiface = iface.netInterface();
//        simuiface = iface.simuInterface();
//    }
//
//    /**
//     * 路网加载后调用的方法，创建收费相关设施
//     */
//    @Override
//    public void afterLoadNet() {
//        // 创建停车分布
//        List<DynaMtcTimeDetail> timeDisList = new ArrayList<>();
//        timeDisList.add(createMtcTimeDetail(3, 1));
//        timeDisList.add(createMtcTimeDetail(5, 1));
//
//        List<DynaEtcSpeedDetail> speedDisList = new ArrayList<>();
//        speedDisList.add(createEtcSpeedDetail(15, 1));
//        speedDisList.add(createEtcSpeedDetail(20, 1));
//
//        // 车型ID为1的停车时间分布
//        DynaTollParkingTime tollParkingTime = createTollParkingTime(1, timeDisList, speedDisList);
//        List<DynaTollParkingTime> parkingTimeList = new ArrayList<>();
//        parkingTimeList.add(tollParkingTime);
//
//        // 创建收费停车分布
//        DynaTollParkingTimeDis tollParkingTimeDis = createTollParkingTimeDis(parkingTimeList, "新建停车分布");
//        System.out.printf("创建了停车分布，id=%d%n%n", tollParkingTimeDis.getId());
//
//        // 创建收费车道
//        List<ITollLane> tollLanes = new ArrayList<>();
//        for (int laneNumber = 0; laneNumber < 12; laneNumber++) {
//            // 创建收费区域
//            DynaTollPoint tollPoint = createTollPoint(m2p(40), 1);
//
//            int linkId = 1;
//            double location = m2p(30);
//            double length = m2p(40);
//
//            // 使用Arrays.asList()替代List.of()
//            ITollLane tollLane = createTollLane(linkId, location, length, laneNumber, 0, 3600,
//                    new ArrayList<>(Arrays.asList(tollPoint)));
//            tollLanes.add(tollLane);
//            System.out.printf("在序号为%d的车道上创建了收费车道，id=%d%n", laneNumber, tollLane.id());
//        }
//        System.out.println();
//
//        // 创建收费路径决策点
//        int linkId = 2;
//        double location = m2p(30);
//        ITollDecisionPoint tollDeciPoint = createTollDecisionPoint(linkId, location);
//        System.out.printf("创建了收费路径决策点，id=%d%n%n", tollDeciPoint.id());
//
//        // 创建收费路径
//        for (ITollLane tollLane : tollLanes) {
//            ITollRouting tollRouting = createTollRouting(tollDeciPoint, tollLane);
//            System.out.printf("在收费路径决策点%d创建了收费路径，id=%d%n", tollDeciPoint.id(), tollRouting.id());
//        }
//        System.out.println();
//
//        // 更新收费决策点的车型对车道选择的分布比重
//        if (!tollLanes.isEmpty()) {
//            ITollLane lastTollLane = tollLanes.get(tollLanes.size() - 1);
//            List<ITollRouting> tollRoutings = netiface.tol();
//            if (!tollRoutings.isEmpty()) {
//                ITollRouting lastTollRouting = tollRoutings.get(tollRoutings.size() - 1);
//
//                List<ITollDecisionPoint> tollDeciPoints = netiface.tollDecisionPoints();
//                if (!tollDeciPoints.isEmpty()) {
//                    ITollDecisionPoint firstTollDeciPoint = tollDeciPoints.get(0);
//
//                    DynaEtcTollInfo ectTollInfo = createEctTollInfo((int)lastTollRouting.id(), (int)lastTollLane.id(), 0.5);
//                    DynaVehicleTollDisDetail vehicleDisDetail = createVehicleTollDisDetail((int)lastTollRouting.id(), (int)lastTollLane.id(), 0.5);
//
//                    // 使用Arrays.asList()替代List.of()
//                    DynaVehicleTollDisInfo vehicleDisInfo = createVehicleTollDisInfo(1, new ArrayList<>(Arrays.asList(vehicleDisDetail)));
//                    DynaRoutingDisTollInfo routingDisInfo = createRoutingDisInfo(new ArrayList<>(Arrays.asList(ectTollInfo)),
//                            new ArrayList<>(Arrays.asList(vehicleDisInfo)));
//                    DynaTollDisInfo tollDisInfo = createTollDisInfo(null, new ArrayList<>(Arrays.asList(routingDisInfo)));
//
//                    firstTollDeciPoint.updateTollDisInfoList(new ArrayList<>(Arrays.asList(tollDisInfo)));
//                    System.out.println("更新了收费决策点的车型对车道选择的分布比重\n");
//                }
//            }
//        }
//
//        // 查看所有收费相关设施数量
//        List<IDynaTollParkingTimeDis> allTollParkingTimeDis = netiface.tollParkingTimeDis();
//        System.out.printf("收费停车分布的数量是：%d%n", allTollParkingTimeDis.size());
//
//        List<ITollLane> allTollLanes = netiface.tollLanes();
//        System.out.printf("收费车道的数量是：%d%n", allTollLanes.size());
//
//        List<ITollDecisionPoint> allTollDeciPoints = netiface.tollDecisionPoints();
//        System.out.printf("收费路径决策点的数量是：%d%n", allTollDeciPoints.size());
//
//        // 查看收费分布信息列表
//        System.out.println("收费分布信息列表");
//        if (!allTollDeciPoints.isEmpty()) {
//            ITollDecisionPoint firstTollDeciPoint = allTollDeciPoints.get(0);
//            List<DynaTollDisInfo> tollDisInfoList = firstTollDeciPoint.tollDisInfoList();
//
//            for (DynaTollDisInfo tollDisInfo : tollDisInfoList) {
//                // 静态决策路径
//                IRouting route = tollDisInfo.pIRouting();
//                System.out.printf("\t静态决策路径：%s%n", route);
//
//                // 收费需求设置
//                List<DynaRoutingDisTollInfo> disTollInfoList = tollDisInfo.disTollInfoList();
//                for (DynaRoutingDisTollInfo disTollInfo : disTollInfoList) {
//                    // 开始时间和结束时间（单位：秒）
//                    System.out.printf("\t\t开始时间：%ds%n", disTollInfo.startTime());
//                    System.out.printf("\t\t结束时间：%ds%n", disTollInfo.endTime());
//
//                    // ETC车辆比率
//                    List<DynaEtcTollInfo> ectTollInfoList = disTollInfo.ectTollInfoList();
//                    for (DynaEtcTollInfo ectTollInfo : ectTollInfoList) {
//                        System.out.printf("\t\t\t收费路径ID：%d%n", ectTollInfo.tollRoutingID());
//                        System.out.printf("\t\t\t\t收费车道ID：%d%n", ectTollInfo.tollLaneID());
//                        System.out.printf("\t\t\t\tETC占比：%f%n", ectTollInfo.etcRatio());
//                    }
//
//                    // 车型对车道选择的分布比重
//                    List<DynaVehicleTollDisInfo> vehicleDisInfoList = disTollInfo.vehicleDisInfoList();
//                    for (DynaVehicleTollDisInfo vehicleDisInfo : vehicleDisInfoList) {
//                        System.out.printf("\t\t\t车型ID：%d%n", vehicleDisInfo.vehicleType());
//
//                        List<DynaVehicleTollDisDetail> choiceDisList = vehicleDisInfo.list();
//                        for (DynaVehicleTollDisDetail choiceDis : choiceDisList) {
//                            System.out.printf("\t\t\t\t收费路径ID：%d%n", choiceDis.tollRoutingID());
//                            System.out.printf("\t\t\t\t收费车道ID：%d%n", choiceDis.tollLaneID());
//                            System.out.printf("\t\t\t\t分布比重：%f%n", choiceDis.prop());
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println();
//    }
//
//    /**
//     * 创建MTC时间分布
//     */
//    private DynaMtcTimeDetail createMtcTimeDetail(float time, float prop) {
//        DynaMtcTimeDetail mtcTimeDetail = new DynaMtcTimeDetail();
//        mtcTimeDetail.setTime(time);  // 停车收费时长（单位：分钟）
//        mtcTimeDetail.setProp(prop);  // 占比
//        return mtcTimeDetail;
//    }
//
//    /**
//     * 创建ETC速度分布
//     */
//    private DynaEtcSpeedDetail createEtcSpeedDetail(float speed, float prop) {
//        DynaEtcSpeedDetail etcSpeedDetail = new DynaEtcSpeedDetail();
//        etcSpeedDetail.setLimitSpeed(speed);  // 通过速度（单位：km/h）
//        etcSpeedDetail.setProp(prop);  // 占比
//        return etcSpeedDetail;
//    }
//
//    /**
//     * 为一种车型创建MTC停车时间分布和ETC速度分布
//     */
//    private DynaTollParkingTime createTollParkingTime(int vehicleTypeId,
//                                                      List<DynaMtcTimeDetail> timeDisList,
//                                                      List<DynaEtcSpeedDetail> speedDisList) {
//        DynaTollParkingTime tollParkingTime = new DynaTollParkingTime();
//        tollParkingTime.setVehicleTypeId(vehicleTypeId);  // 车型ID
//        tollParkingTime.setTimeDisList(timeDisList);      // MTC时间分布
//        tollParkingTime.setSpeedDisList(speedDisList);    // ETC速度分布
//        return tollParkingTime;
//    }
//
//    /**
//     * 创建收费时间分布
//     */
//    private DynaTollParkingTimeDis createTollParkingTimeDis(List<DynaTollParkingTime> parkingTimeList, String name) {
//        DynaTollParkingTimeDis tollParkingTimeDis = new DynaTollParkingTimeDis();
//        tollParkingTimeDis.setName(name);                // 收费时间分布名称
//        tollParkingTimeDis.setParkingTimeList(parkingTimeList);  // 各车型的分布
//        return netiface.createTollParkingTimeDis(tollParkingTimeDis);
//    }
//
//    /**
//     * 创建收费区域
//     */
//    private DynaTollPoint createTollPoint(double distance, int tollType) {
//        return createTollPoint(distance, tollType, 1);
//    }
//
//    private DynaTollPoint createTollPoint(double distance, int tollType, int timeDisId) {
//        DynaTollPoint tollPoint = new DynaTollPoint();
//        tollPoint.setLocation(distance);  // 距离路段起点的距离（单位：米）
//        tollPoint.setTollType(tollType);  // 收费类型：1=MTC，2=ETC，3=ETC&MTC
//        tollPoint.setTimeDisId(timeDisId);  // 停车时间分布的ID
//        tollPoint.setEnable(true);        // 启用收费区域
//        return tollPoint;
//    }
//
//    /**
//     * 创建收费车道
//     */
//    private ITollLane createTollLane(int linkId, double location, double length, int laneNumber,
//                                     float startTime, float endTime, List<DynaTollPoint> tollPointList) {
//        return createTollLane(linkId, location, length, laneNumber, startTime, endTime, tollPointList, 8);
//    }
//
//    private ITollLane createTollLane(int linkId, double location, double length, int laneNumber,
//                                     float startTime, float endTime, List<DynaTollPoint> tollPointList,
//                                     float tollPointLength) {
//        DynaTollLane tollLaneParam = new DynaTollLane();
//        tollLaneParam.setRoadId(linkId);         // 路段ID
//        tollLaneParam.setLocation(location);     // 距离路段起点的距离（单位：米）
//        tollLaneParam.setLength(length);         // 收费车道长度（单位：米）
//        tollLaneParam.setLaneNumber(laneNumber); // 车道序号（从右向左，从0开始）
//        tollLaneParam.setStartTime(startTime);   // 开始时间（单位：秒）
//        tollLaneParam.setEndTime(endTime);       // 结束时间（单位：秒）
//        tollLaneParam.setTollPoint(tollPointList); // 收费区域列表
//        tollLaneParam.setTollPointLen(tollPointLength); // 收费区域长度
//        return netiface.createTollLane(tollLaneParam);
//    }
//
//    /**
//     * 创建收费路径决策点
//     */
//    private ITollDecisionPoint createTollDecisionPoint(int linkId, double location) {
//        return createTollDecisionPoint(linkId, location, "");
//    }
//
//    private ITollDecisionPoint createTollDecisionPoint(int linkId, double location, String name) {
//        ILink link = netiface.findLink(linkId);
//        if (link == null) return null;
//        return netiface.createTollDecisionPoint(link, location, name);
//    }
//
//    /**
//     * 创建收费路径
//     */
//    private ITollRouting createTollRouting(ITollDecisionPoint tollDeciPoint, ITollLane tollLane) {
//        return netiface.createTollRouting(tollDeciPoint, tollLane);
//    }
//
//    /**
//     * 创建ETC收费信息
//     */
//    private DynaEtcTollInfo createEctTollInfo(int tollRoutingId, int tollLaneId, double etcRatio) {
//        DynaEtcTollInfo ectTollInfo = new DynaEtcTollInfo();
//        ectTollInfo.setTollRoutingID(tollRoutingId);  // 收费路径编号
//        ectTollInfo.setTollLaneID(tollLaneId);        // 收费车道编号
//        ectTollInfo.setEtcRatio(etcRatio);            // 车道内ETC占比
//        return ectTollInfo;
//    }
//
//    /**
//     * 创建某一车型对某一车道选择的分布比重
//     */
//    private DynaVehicleTollDisDetail createVehicleTollDisDetail(int tollRoutingId, int tollLaneId, double prop) {
//        DynaVehicleTollDisDetail vehicleDisDetail = new DynaVehicleTollDisDetail();
//        vehicleDisDetail.setTollRoutingID(tollRoutingId);  // 收费路径编号
//        vehicleDisDetail.setTollLaneID(tollLaneId);        // 收费车道编号
//        vehicleDisDetail.setProp(prop);                    // 分布比重
//        return vehicleDisDetail;
//    }
//
//    /**
//     * 创建某一车型对所有车道选择的分布比重
//     */
//    private DynaVehicleTollDisInfo createVehicleTollDisInfo(int vehicleTypeId,
//                                                            List<DynaVehicleTollDisDetail> vehicleTollDisDetailList) {
//        DynaVehicleTollDisInfo vehicleDisInfo = new DynaVehicleTollDisInfo();
//        vehicleDisInfo.setVehicleType(vehicleTypeId);  // 车型ID
//        vehicleDisInfo.setList(vehicleTollDisDetailList);  // 选择分布列表
//        return vehicleDisInfo;
//    }
//
//    /**
//     * 创建路径分布信息
//     */
//    private DynaRoutingDisTollInfo createRoutingDisInfo(List<DynaEtcTollInfo> ectTollInfoList,
//                                                        List<DynaVehicleTollDisInfo> vehicleDisInfoList) {
//        DynaRoutingDisTollInfo routingDisInfo = new DynaRoutingDisTollInfo();
//        routingDisInfo.setEctTollInfoList(ectTollInfoList);      // ETC收费信息列表
//        routingDisInfo.setVehicleDisInfoList(vehicleDisInfoList);  // 车型分布信息列表
//        return routingDisInfo;
//    }
//
//    /**
//     * 创建收费信息
//     */
//    private DynaTollDisInfo createTollDisInfo(IRouting route, List<DynaRoutingDisTollInfo> routingDisInfoList) {
//        DynaTollDisInfo tollDisInfo = new DynaTollDisInfo();
//        tollDisInfo.setPIRouting(route);                // 静态决策路径
//        tollDisInfo.setDisTollInfoList(routingDisInfoList);  // 路径分布信息列表
//        return tollDisInfo;
//    }
//
//
//}