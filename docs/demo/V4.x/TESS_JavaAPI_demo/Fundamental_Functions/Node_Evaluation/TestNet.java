//package Fundamental_Functions.Node_Evaluation;
//
//import com.jidatraffic.tessng.*;
//import java.util.*;
//
//public class TestNet extends JCustomerNet {
//    public TestNet() {
//        super();
//    }
//
//    @Override
//    public void afterLoadNet() {
//        // 代表TESS NG的接口
//        TessInterface iface = TESSNG.tessngIFace();
//        // 代表TESS NG的路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        if (netiface.links().size() == 0) {
//            return;
//        }
//
//        // step1：创建节点
//        double x1 = -1000;
//        double y1 = -40;
//        double x2 = -800;
//        double y2 = 180;
//        String junctionName = "newJunction";
//        netiface.createJunction(new Point(TESSNG.m2p(x1), TESSNG.m2p(y1)),
//                new Point(TESSNG.m2p(x2), TESSNG.m2p(y2)), junctionName);
//        System.out.println("createJunction: " + junctionName + " done!");
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // step2：创建静态路径
//        netiface.buildAndApplyPaths(3);  // 设置每个OD最多搜索3条路径
//        netiface.reSetDeciPoint();       // 优化决策点位置
//
//        for (IDecisionPoint dp : netiface.decisionPoints()) {
//            for (IRouting routing : dp.routings()) {
//                netiface.reSetLaneConnector(routing);  // 优化路径中的车道连接
//            }
//        }
//        System.out.println("buildAndApplyPaths done!");
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // step3：为节点中每个转向设置流量
//        // 由于没有已知值，这里针对不同转向类型为其赋流量初值
//        Map<String, Integer> turnVolumeReduct = new HashMap<>();
//        turnVolumeReduct.put("左转", 400);
//        turnVolumeReduct.put("直行", 1200);
//        turnVolumeReduct.put("右转", 200);
//        turnVolumeReduct.put("掉头", 0);
//
//        // step3-1：添加流量时间段
//        FlowTimeInterval timeInterval = netiface.addFlowTimeInterval();
//        long timeId = timeInterval.getTimeId();
//        int startTime = 0;
//        int endTime = 3600;
//        netiface.updateFlowTimeInterval(timeId, startTime, endTime);
//
//        // step3-2：遍历转向，为其设置流量
//        List<IJunction> junctions = netiface.getAllJunctions();
//        for (IJunction junction : junctions) {
//            long junctionId = junction.getId();
//            ArrayList<TurnningBaseInfo> turningInfos = junction.getAllTurningInfo();
//
//            for (TurnningBaseInfo turning : turningInfos) {
//                long turningId = turning.getTurningId();
//                String turnType = turning.getStrTurnType();
//                int inputVolume = turnVolumeReduct.getOrDefault(turnType, 0);
//                // 为该转向设置输入流量
//                netiface.updateFlow(timeId, junctionId, turningId, inputVolume);
//            }
//        }
//        System.out.println("updateFlow done!");
//
//        // step4：进行流量分配计算
//        // 设置BPR路阻函数参数，流量分配算法参数
//        double theta = 0.1;
//        double bpra = 0.15;
//        double bprb = 4;
//        int maxIterateNum = 300;
//        netiface.updateFlowAlgorithmParams(theta, bpra, bprb, maxIterateNum);  // 更新计算参数
//
//        // 计算路径流量分配并应用，返回分配结果
//        Map<Integer, List<FlowAllocationResult>> result = netiface.calculateFlows();
//        System.out.println("calculateFlows done!");
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // 解析流量分配结果
//        for (Map.Entry<Integer, List<FlowAllocationResult>> entry : result.entrySet()) {
//            int timeIdResult = entry.getKey();
//            List<FlowAllocationResult> turningFlows = entry.getValue();
//
//            for (FlowAllocationResult i : turningFlows) {
//                int junction = i.getJunction().getId();
//                String turning = i.getTurningBaseInfo().getStrDirection() + "-" +
//                        i.getTurningBaseInfo().getStrTurnType();
//                double inputVolume = i.getInputFlowValue();        // 该转向输入流量
//                double realVolume = i.getRealFlow();               // 该转向实际分配到的流量
//                double relativeError = i.getRelativeError();       // 分配的相对误差
//                IFlowTimeInterval interval = i.getFlowTimeInterval();
//                int startTimeResult = interval.getStartTime();
//                int endTimeResult = interval.getEndTime();
//
//                System.out.println(startTimeResult + "-" + endTimeResult + ": " +
//                        "{节点=" + junction + ", 转向=" + turning +
//                        ", 输入流量=" + inputVolume +
//                        ", 分配流量=" + realVolume +
//                        ", 相对误差=" + relativeError + "}");
//            }
//        }
//    }
//}
