//package Fundamental_Functions.Pedestrian_Simulation;
//import com.jidatraffic.tessng.*;
//import java.util.*;
//
//public class TestNet extends JCustomerNet {
//
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
//        // 创建人行道，将路段向右侧平移offset的距离，得到路侧人行道的形状
//        double offset = 3;
//        Map<String, long[]> linkPairs = new HashMap<>();
//        linkPairs.put("西进口", new long[]{1, 6});
//        linkPairs.put("南进口", new long[]{5, 2});
//        linkPairs.put("东进口", new long[]{3, 7});
//        linkPairs.put("北进口", new long[]{8, 4});
//
//        List<IPedestrianPathPoint> pathStartPoints = new ArrayList<>();  // 记录行人发生点，用于创建行人路径
//        List<IPedestrianPathPoint> pathEndPoints = new ArrayList<>();      // 记录行人结束点，用于创建行人路径
//        List<IPedestrianCrossWalkRegion> crossWalks = new ArrayList<>();      // 记录创建的人行横道，用于在人行横道上创建信号灯
//        IPedestrianPathPoint pedestrianStartPoint = null;
//
//        for (Map.Entry<String, long[]> entry : linkPairs.entrySet()) {
//            String direction = entry.getKey();
//            long[] linkPair = entry.getValue();
//            ArrayList<Point> updatePoints = new ArrayList<>();
//
//            for (long linkId : linkPair) {
//                ILink link = netiface.findLink(linkId);
//                ArrayList<Point> rightBreakPoints = link.rightBreakPoints();
//                Point startPoint = rightBreakPoints.get(0);
//                Point endPoint = rightBreakPoints.get(rightBreakPoints.size() - 1);
//                double deltaX = TESSNG.p2m(endPoint.getX()) - TESSNG.p2m(startPoint.getX());
//                double deltaY = TESSNG.p2m(endPoint.getY()) - TESSNG.p2m(startPoint.getY());
//
//                ArrayList<Point> vertexs = new ArrayList<>();
//                for (Point point : link.rightBreakPoints()) {
//                    double x = TESSNG.p2m(point.getX());
//                    double y = TESSNG.p2m(point.getY());
//                    vertexs.add(new Point(TESSNG.m2p(x), TESSNG.m2p(y)));
//                }
//
//                if (Math.abs(deltaX) > Math.abs(deltaY)) {
//                    if (deltaX > 0) {
//                        for (Point p : vertexs) {
//                            updatePoints.add(new Point(p.getX(), p.getY() + TESSNG.m2p(offset)));
//                        }
//                    } else {
//                        for (Point p : vertexs) {
//                            updatePoints.add(new Point(p.getX(), p.getY() - TESSNG.m2p(offset)));
//                        }
//                    }
//                } else {
//                    if (deltaY > 0) {
//                        for (Point p : vertexs) {
//                            updatePoints.add(new Point(p.getX() - TESSNG.m2p(offset), p.getY()));
//                        }
//                    } else {
//                        for (Point p : vertexs) {
//                            updatePoints.add(new Point(p.getX() + TESSNG.m2p(offset), p.getY()));
//                        }
//                    }
//                }
//            }
//
//            IPedestrianSideWalkRegion newPedestrianSideWalkRegion = netiface.createPedestrianSideWalkRegion(updatePoints);
//
//            // 创建行人发生点
//            double pathStartPointX = (updatePoints.get(0).getX() * 2 + updatePoints.get(1).getX()) / 3;
//            double pathStartPointY = (updatePoints.get(0).getY() * 2 + updatePoints.get(1).getY()) / 3;
//            Point pathStartPoint = new Point(pathStartPointX, pathStartPointY);
//            IPedestrianPathPoint newPathStartPoint = netiface.createPedestrianPathStartPoint(pathStartPoint);
//            pathStartPoints.add(newPathStartPoint);
//
//            // 创建行人结束点
//            double pathEndPointX = (updatePoints.get(updatePoints.size() - 1).getX() * 2 +
//                    updatePoints.get(updatePoints.size() - 2).getX()) / 3;
//            double pathEndPointY = (updatePoints.get(updatePoints.size() - 1).getY() * 2 +
//                    updatePoints.get(updatePoints.size() - 2).getY()) / 3;
//            Point pathEndPoint = new Point(pathEndPointX, pathEndPointY);
//            IPedestrianPathPoint newPathEndPoint = netiface.createPedestrianPathEndPoint(pathEndPoint);
//            pathEndPoints.add(newPathEndPoint);
//
//            // 记录西进口行人发生点
//            if ("西进口".equals(direction)) {
//                pedestrianStartPoint = newPathStartPoint;
//            }
//
//            System.out.println("创建" + direction + "人行道" + newPedestrianSideWalkRegion.getId() +
//                    ",行人发生点" + newPathStartPoint.getId() +
//                    ",行人结束点" + newPathEndPoint.getId());
//        }
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // 创建人行横道
//        Map<String, double[][]> crossWalkPoints = new HashMap<>();
//        crossWalkPoints.put("西进口", new double[][]{{-32, -35}, {-32, 2}});
//        crossWalkPoints.put("北进口", new double[][]{{-21, 10}, {35, 10}});
//        crossWalkPoints.put("南进口", new double[][]{{-24, -39}, {33, -39}});
//        crossWalkPoints.put("东进口", new double[][]{{45, -29}, {45, 4}});
//
//        List<Point> pathMiddlePoints = new ArrayList<>();  // 记录人行横道中心点，用于创建行人路径
//
//        for (Map.Entry<String, double[][]> entry : crossWalkPoints.entrySet()) {
//            String direction = entry.getKey();
//            double[][] crossWalkPoint = entry.getValue();
//            double[] startPointCoor = crossWalkPoint[0];
//            Point startPoint = new Point(TESSNG.m2p(startPointCoor[0]), TESSNG.m2p(-startPointCoor[1]));
//            double[] endPointCoor = crossWalkPoint[1];
//            Point endPoint = new Point(TESSNG.m2p(endPointCoor[0]), TESSNG.m2p(-endPointCoor[1]));
//
//            // 取人行横道中心点，用于创建行人路径
//            double pathMiddlePointX = TESSNG.m2p((startPointCoor[0] + endPointCoor[0]) / 2);
//            double pathMiddlePointY = TESSNG.m2p(- (startPointCoor[1] + endPointCoor[1]) / 2);
//            Point pathMiddlePoint = new Point(pathMiddlePointX, pathMiddlePointY);
//            IPedestrianCrossWalkRegion crossWalk = netiface.createPedestrianCrossWalkRegion(startPoint, endPoint);
//            crossWalk.setName(direction + "人行横道");
//            crossWalks.add(crossWalk);
//            pathMiddlePoints.add(pathMiddlePoint);
//
//            System.out.println("创建" + direction + "人行横道" + crossWalk.getId());
//        }
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // 创建过街行人路径
//        IPedestrianPath straightPath = netiface.createPedestrianPath(
//                pathStartPoints.get(0), pathEndPoints.get(1),
//                new ArrayList<Point>(Arrays.asList(pathMiddlePoints.get(2))));  // 西进口直行过街
//
//        System.out.println("创建西进口直行过街路径");
//
//        IPedestrianPath leftPath = netiface.createPedestrianPath(
//                pathStartPoints.get(0), pathEndPoints.get(3),
//                new ArrayList<Point>(Arrays.asList(pathMiddlePoints.get(0))));  // 西进口左转过街
//
//        System.out.println("创建西进口左转过街路径");
//
//        netiface.createPedestrianPath(
//                pathStartPoints.get(1), pathEndPoints.get(2),
//                new ArrayList<Point>(Arrays.asList(pathMiddlePoints.get(3))));  // 南进口直行过街
//
//        System.out.println("创建南进口直行过街路径");
//
//        netiface.createPedestrianPath(
//                pathStartPoints.get(2), pathEndPoints.get(3),
//                new ArrayList<Point>(Arrays.asList(pathMiddlePoints.get(1))));  // 东进口直行过街
//
//        System.out.println("创建东进口直行过街路径");
//
//        netiface.createPedestrianPath(
//                pathStartPoints.get(3), pathEndPoints.get(0),
//                new ArrayList<Point>(Arrays.asList(pathMiddlePoints.get(0))));  // 北进口直行过街
//
//        System.out.println("创建北进口直行过街路径");
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // 创建人行横道信号灯
//        Map<String, Integer> crossWalkPhaseId = new HashMap<>();
//        crossWalkPhaseId.put("西进口", 5);
//        crossWalkPhaseId.put("东进口", 5);
//        crossWalkPhaseId.put("南进口", 4);
//        crossWalkPhaseId.put("北进口", 4);  // 东西进口行人采用南北方向机动车相位，南北进口行人采用东西方向机动车相位
//
//        ISignalController trafficController = netiface.findSignalControllerById(1);
//
//        // 遍历人行横道创建信号灯
//        int index = 0;
//        for (IPedestrianCrossWalkRegion crossWalk : crossWalks) {
//            long crossWalkId = crossWalk.getId();
//            String crossWalkName = crossWalk.getName();
//            String crossWalkDirection = crossWalkName.replace("人行横道", "");
//            double[][] crossWalkPoint = crossWalkPoints.get(crossWalkDirection);
//            double[] crossWalkStartPoint = crossWalkPoint[0];
//            double[] crossWalkEndPoint = crossWalkPoint[1];
//            String name = crossWalkName + "信号灯";
//
//            // 正向信号灯
//            boolean isPositive = true;
//            double positionX = ((crossWalkStartPoint[0] * 9) + crossWalkEndPoint[0]) / 10;
//            double positionY = ((crossWalkStartPoint[1] * 9) + crossWalkEndPoint[1]) / 10;
//            Point position = new Point(TESSNG.m2p(positionX), TESSNG.m2p(-positionY));
//            ICrosswalkSignalLamp signalLampPositive = netiface.createCrossWalkSignalLamp(
//                    trafficController, name, crossWalkId, position, isPositive);
//
//            // 反向信号灯
//            isPositive = false;
//            positionX = (crossWalkStartPoint[0] + (crossWalkEndPoint[0] * 9)) / 10;
//            positionY = (crossWalkStartPoint[1] + (crossWalkEndPoint[1] * 9)) / 10;
//            position = new Point(TESSNG.m2p(positionX), TESSNG.m2p(-positionY));
//            ICrosswalkSignalLamp signalLampNegative = netiface.createCrossWalkSignalLamp(
//                    trafficController, name, crossWalkId, position, isPositive);
//
//            // 为信号灯分配相位
//            int phaseId = crossWalkPhaseId.get(crossWalkDirection);
//            ISignalPhase phase = netiface.findSignalPhase(phaseId);
//            netiface.addCrossWalkSignalPhaseToLamp((int) phase.id(), signalLampPositive);
//            netiface.addCrossWalkSignalPhaseToLamp((int) phase.id(), signalLampNegative);
//
//            System.out.println("为" + crossWalkDirection + "人行横道创建双向信号灯" +
//                    signalLampPositive.id() + "," + signalLampNegative.id() +
//                    "; 并分配相位" + phase.id());
//
//            index++;
//        }
//
//        // 更新行人发生点流量和路径分配比例
//        PedestrianPathStartPointConfigInfo configInfo = netiface.findPedestrianStartPointConfigInfo(pedestrianStartPoint.getId());
//        PedestrianPathStartPointConfigInfo updateConfigInfo = new PedestrianPathStartPointConfigInfo();
//
//        // 更新行人发生点流量
//        List<GenPedestrianInfo> genPedestrianConfigInfoList = new ArrayList<>();
//        GenPedestrianInfo updateGenPedestrianConfigInfo = new GenPedestrianInfo();
//        updateGenPedestrianConfigInfo.setPedestrianCount(6);
//        updateGenPedestrianConfigInfo.setTimeInterval(configInfo.getGenPedestrianConfigInfo().get(0).getTimeInterval());
//        genPedestrianConfigInfoList.add(updateGenPedestrianConfigInfo);
//
//        updateConfigInfo.setGenPedestrianConfigInfo(genPedestrianConfigInfoList);
//        updateConfigInfo.setId(configInfo.getId());
//
//        List<PedestrianTrafficDistributionInfo> distributionInfoList = configInfo.getPedestrianTrafficDistributionConfigInfo();
//        List<PedestrianTrafficDistributionInfo> updateDistributionInfoList = new ArrayList<>();
//        PedestrianTrafficDistributionInfo distributionInfo = distributionInfoList.get(0);
//        PedestrianTrafficDistributionInfo updateDistributionInfo = new PedestrianTrafficDistributionInfo();
//        updateDistributionInfo.setTimeInterval(distributionInfo.getTimeInterval());
//
//        TreeMap<Integer, Integer> trafficRatio = new TreeMap<>();
//        trafficRatio.put(straightPath.getId(), 2.0);
//        trafficRatio.put(leftPath.getId(), 1.0);
//        updateDistributionInfo.setTrafficRatio(trafficRatio);
//
//        updateDistributionInfoList.add(updateDistributionInfo);
//        updateConfigInfo.setPedestrianTrafficDistributionConfigInfo(updateDistributionInfoList);
//
//        boolean result = netiface.updatePedestrianStartPointConfigInfo(updateConfigInfo);
//
//        System.out.println("更新西进口行人发生点流量:" + updateGenPedestrianConfigInfo.getPedestrianCount() +
//                "人/" + updateGenPedestrianConfigInfo.getTimeInterval() + "秒,result:" + result);
//        System.out.println("更新西进口行人发生点路径分配:" + updateDistributionInfo.getTrafficRatio() +
//                ",result:" + result);
//    }
//}
