//package Fundamental_Functions.Parking_Lot_Simulation;
//
//import com.jidatraffic.tessng.*;
//import java.util.ArrayList;
//
//public class MySimulator extends JCustomerSimulator {
//    private long lastSimuTime = 0;
//
//    public MySimulator() {
//        super();
//    }
//
//    /**
//     * 每个计算周期结束后调用的方法
//     * 实现动态控制停车场功能
//     */
//    @Override
//    public void afterOneStep() {
//        // 获取TESS NG接口
//        TessInterface iface = TESSNG.tessngIFace();
//        SimuInterface simuiface = iface.simuInterface();
//        NetInterface netiface = iface.netInterface();
//
//        // 当前仿真计算批次
//        long batchNum = simuiface.batchNumber();
//        // 当前已仿真时间，单位：毫秒
//        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
//
//        // 仿真180秒后，关闭路段4左右两侧的停车场
//        // 判断仿真时间
//        if (lastSimuTime < 180 * 1000 && simuTime >= 180 * 1000) {
//            // 更新停车路径分配
//            for (IParkingDecisionPoint parkingDecisionPoint : netiface.parkingDecisionPoints()) {
//                ArrayList<DynaParkDisInfo> updateParkDisInfoList = new ArrayList<>();
//
//                for (DynaParkDisInfo parkDisInfo : parkingDecisionPoint.parkDisInfoList()) {
//                    DynaParkDisInfo updateParkDisInfo = new DynaParkDisInfo();
//                    updateParkDisInfo.setPIRouting(parkDisInfo.pIRouting());
//
//                    ArrayList<DynaRoutingDisVehicleInfo> updateDisVehiclsInfoList = new ArrayList<>();
//
//                    for (DynaRoutingDisVehicleInfo disVehiclsInfo : parkDisInfo.disVehiclsInfoList()) {
//                        DynaRoutingDisVehicleInfo updateDisVehiclsInfo = new DynaRoutingDisVehicleInfo();
//                        updateDisVehiclsInfo.setStartTime(disVehiclsInfo.startTime());
//                        updateDisVehiclsInfo.setEndTime(disVehiclsInfo.endTime());
//
//                        ArrayList<DynaParkRegionVehicleDisDetail> updateVehicleDisDetailList = new ArrayList<>();
//
//                        for (DynaParkRegionVehicleDisDetail vehicleDisDetail : disVehiclsInfo.vehicleDisDetailList()) {
//                            DynaParkRegionVehicleDisDetail updateVehicleDisDetail = new DynaParkRegionVehicleDisDetail();
//                            updateVehicleDisDetail.setParkingRegionID(vehicleDisDetail.parkingRegionID());
//                            updateVehicleDisDetail.setParkingRoutingID(vehicleDisDetail.parkingRoutingID());
//                            updateVehicleDisDetail.setParkingSelection(vehicleDisDetail.parkingSelection());
//                            updateVehicleDisDetail.setParkingTimeDisId(vehicleDisDetail.parkingTimeDisId());
//                            updateVehicleDisDetail.setVehicleType(vehicleDisDetail.vehicleType());
//
//                            IParkingRegion parkingRegion = netiface.findParkingRegion(vehicleDisDetail.parkingRegionID());
//                            long linkId = parkingRegion.dynaParkingRegion().getRoadId();
//
//                            if (linkId == 4) {
//                                updateVehicleDisDetail.setProp(0);
//                            } else {
//                                updateVehicleDisDetail.setProp(vehicleDisDetail.prop());
//                            }
//
//                            updateVehicleDisDetailList.add(updateVehicleDisDetail);
//                        }
//
//                        updateDisVehiclsInfo.setVehicleDisDetailList(updateVehicleDisDetailList);
//                        updateDisVehiclsInfoList.add(updateDisVehiclsInfo);
//                    }
//
//                    updateParkDisInfo.setDisVehiclsInfoList(updateDisVehiclsInfoList);
//                    updateParkDisInfoList.add(updateParkDisInfo);
//                }
//
//                boolean result = parkingDecisionPoint.updateParkDisInfo(updateParkDisInfoList);
//                System.out.println("仿真" + (simuTime / 1000) + "秒，关闭路段4两侧的停车区; result: " + result);
//            }
//        }
//
//        // 更新仿真时间
//        lastSimuTime = simuTime;
//    }
//}
