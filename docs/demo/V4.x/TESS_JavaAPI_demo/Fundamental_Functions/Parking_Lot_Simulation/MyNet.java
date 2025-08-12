package TESS_Java_APIDemo.Fundamental_Functions.Parking_Lot_Simulation;

import com.jidatraffic.tessng.*;

import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.util.*;

public class MyNet extends JCustomerNet {
    public MyNet(){
        super();
    }

    @Override
    public void afterLoadNet(){
        //代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        //代表TESS NG的路网子接口
        NetInterface netiface = iface.netInterface();

        createNet();
    }
    @Override
    public void ref_labelNameAndFont(int itemType, long itemId, ObjInt ref_outPropName, ObjReal ref_outFontSize) {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG仿真子接口
        SimuInterface simuiface = iface.simuInterface();
        // 如果仿真正在进行，设置 ref_outPropName 为 None，路段和车道均不绘制标签
        if (simuiface.isRunning()) {
            ref_outPropName.setValue(GraphicsItemPropName.None.swigValue());
        }
        // 默认绘制 ID
        ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
        // 标签大小为 6 米
        ref_outFontSize.setValue(6);
        // 如果是连接段，一律绘制名称
        if (itemType == NetItemType.getGConnectorType()) {
            ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
        } else if (itemType == NetItemType.getGLinkType()) {
            if (itemId == 26977 || itemId == 5 || itemId == 6) {
                ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
            }
        }
    }
    private void test_IRoadNet(){
        System.out.println("--------test_IRoadNet 开始-----------");
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netInterface = iface.netInterface();
        System.out.println(LocalDateTime.now() +"路网加载完成");
        IRoadNet iRoadNet = netInterface.roadNet();
        long id = iRoadNet.id();
        System.out.println("路网ID="+id);
        String name = iRoadNet.netName();
        System.out.println(name);
        String url = iRoadNet.url();
        System.out.println("路网地址:"+url);
        String bkgUrl = iRoadNet.bkgUrl();
        System.out.println("背景路径:"+bkgUrl);
        String type = iRoadNet.type();
        System.out.println("来源分类:"+type);
        JsonObject jsonObject = iRoadNet.otherAttrs();
        System.out.println(" 其它属性json:"+jsonObject);
        Point point = iRoadNet.centerPoint();
        System.out.println("中心点:"+point);
        System.out.println("--------test_IRoadNet 结束-----------");
    }

    private void createNet(){

        //代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        //代表TESS NG的路网子接口
        NetInterface netiface = iface.netInterface();
        SimuInterface simuInterface = iface.simuInterface();
        netiface.pedestrianCrossWalkRegions();
//        simuInterface.createGVehicle();
//        netiface.openNetFle("D:\\TESSNG_4.0.20\\Example\\1-机动车交叉口案例\\单车道的主辅路交叉口（影响范围0+安全时距0s+停车间距1.2m+忍耐因子1）.tess");
        System.out.println("路网加载完成");
        // 定义路网ID（从数据库中获取的路网唯一标识）
        int netId = 0; // 示例路网ID，实际应根据数据库设置

        // 从数据库加载路网
//        netiface.createLinkWithLaneWidth();
        System.out.println(netiface +"路网加载完成");

        // 第一条路段
        Point startPoint = new Point(TESSNG.m2p(-300), 0);
        Point endPoint = new Point(TESSNG.m2p(300), 0);
        ArrayList<Point> lPoint = new ArrayList<>();
        lPoint.add(startPoint);
        lPoint.add(endPoint);

        ILink link1 = netiface.createLink(lPoint, 7, "曹安公路", true);
        if (link1 != null) {
            // 车道列表
            ArrayList<ILane> lanes = link1.lanes();
            // 打印该路段所有车道 ID 列表
            List<Long> laneIds = new ArrayList<>();
            for (ILane lane : lanes) {
                laneIds.add((long)lane.id());
            }
            System.out.println("曹安公路车道 ID 列表：" + laneIds);

            // 在当前路段创建发车点
            IDispatchPoint dp = netiface.createDispatchPoint(link1);
            if (dp != null) {
                // 设置发车间隔，含车型组成、时间间隔、发车数
                dp.addDispatchInterval(1, 3600, 3600);
            }
        }

        // 第二条路段
        startPoint = new Point(TESSNG.m2p(-300), TESSNG.m2p(-25));
        endPoint = new Point(TESSNG.m2p(300), TESSNG.m2p(-25));
        ArrayList<Point> lPoint2 = new ArrayList<>();
        lPoint2.add(startPoint);
        lPoint2.add(endPoint);

        ILink link2 = netiface.createLink(lPoint2, 7, "次干道", true);
        if (link2 != null) {
            IDispatchPoint dp2 = netiface.createDispatchPoint(link2);
            if (dp2 != null) {
                dp2.addDispatchInterval(1, 3600, 3600);
            }
            // 将外侧车道设为”公交专用道"
            List<ILane> lanes2 = link2.lanes();
            if (!lanes2.isEmpty()) {
                lanes2.get(0).setLaneType("公交专用道");
            }
        }
//
        // 第三条路段
//        Point startPoint3 = new Point(m2p(-300), m2p(25));
//        Point endPoint3 = new Point(m2p(-150), m2p(25));
//        ArrayList<Point> lPoint3 = new ArrayList<>();
//        lPoint3.add(startPoint3);
//        lPoint3.add(endPoint3);
//
//        ILink link3 = netiface.createLink(lPoint3, 3);
//        if (link3 != null) {
//            IDispatchPoint dp3 = netiface.createDispatchPoint(link3);
//            if (dp3 != null) {
//                dp3.addDispatchInterval(1, 3600, 3600);
//            }
//        }
//
//        // 创建第四条路段
//        Point startPoint4 = new Point(TESSNG.m2p(-50), TESSNG.m2p(25));
//        Point endPoint4 = new Point(TESSNG.m2p(50), TESSNG.m2p(25));
//        ArrayList<Point> lPoint4 = new ArrayList<>();
//        lPoint4.add(startPoint4);
//        lPoint4.add(endPoint4);
//
//        ILink link4 = netiface.createLink(lPoint4, 3);
//
        // 创建第五条路段
//        Point startPoint5 = new Point(TESSNG.m2p(150), TESSNG.m2p(25));
//        Point endPoint5 = new Point(TESSNG.m2p(300), TESSNG.m2p(25));
//        ArrayList<Point> lPoint5 = new ArrayList<>();
//        lPoint5.add(startPoint5);
//        lPoint5.add(endPoint5);
//
//        ILink link5 = netiface.createLink(lPoint5, 3, "自定义限速路段");
//        if (link5 != null) {
//            link5.setLimitSpeed(30);
//        }
//
//        // 创建第六条路段
//        Point startPoint6 = new Point(TESSNG.m2p(-300), TESSNG.m2p(50));
//        Point endPoint6 = new Point(TESSNG.m2p(300), TESSNG.m2p(50));
//        ArrayList<Point> lPoint6 = new ArrayList<>();
//        lPoint6.add(startPoint6);
//        lPoint6.add(endPoint6);
//
//        ILink link6 = netiface.createLink(lPoint6, 3, "动态发车路段");
//        if (link6 != null) {
//            link6.setLimitSpeed(80);
//        }
//
//        // 创建第七条路段
//        Point startPoint7 = new Point(TESSNG.m2p(-300), TESSNG.m2p(75));
//        Point endPoint7 = new Point(TESSNG.m2p(-250), TESSNG.m2p(75));
//        ArrayList<Point> lPoint7 = new ArrayList<>();
//        lPoint7.add(startPoint7);
//        lPoint7.add(endPoint7);
//
//        ILink link7 = netiface.createLink(lPoint7, 3);
//        if (link7 != null) {
//            link7.setLimitSpeed(80);
//        }
//
//        // 创建第八条路段
//        Point startPoint8 = new Point(TESSNG.m2p(-50), TESSNG.m2p(75));
//        Point endPoint8 = new Point(TESSNG.m2p(300), TESSNG.m2p(75));
//        ArrayList<Point> lPoint8 = new ArrayList<>();
//        lPoint8.add(startPoint8);
//        lPoint8.add(endPoint8);
//
//        ILink link8 = netiface.createLink(lPoint8, 3);
//        if (link8 != null) {
//            link8.setLimitSpeed(80);
//        }
//        ArrayList<Point3D> centerPoints1 = new ArrayList<>();
//        Point3D startPoint1 = new Point3D(300, -6, 0); // 起点
//        Point3D endPoint1 = new Point3D(25, -6, 5);     // 终点
//        centerPoints1.add(startPoint1);
//        centerPoints1.add(endPoint1);

//        int laneCount = 3;                          // 车道数量
//        String linkName = "东进口creatlink3d";       // 路段名称
//        boolean bAddToScene = true;                 // 是否加入场景
//
//
//        // 创建3D道路链接
//        ILink e_approach = netiface.createLink3D(centerPoints1, laneCount, linkName, bAddToScene);
//
//        if (e_approach != null) {
//            System.out.println("3D路段创建成功，ID: " + e_approach.id());
//        } else {
//            System.out.println("3D路段创建失败");
//        }
        // 创建第一条连接段

//        // 创建中心线点集（二维坐标）
//        ArrayList<Point> centerPoints1 = new ArrayList<>();
//        Point startPoint1 = new Point(6, 300); // 起点
//        Point endPoint1 = new Point(6, 25);     // 终点
//        centerPoints1.add(startPoint1);
//        centerPoints1.add(endPoint1);
//
//        // 设置每条车道的宽度（从左到右）
//        ArrayList<Double> laneWidths = new ArrayList<>();
//        laneWidths.add(3.5);
//        laneWidths.add(3.0);
//        laneWidths.add(3.0);
//
//        // 其他参数
//        String linkName = "南进口creatlinkWithLaneWidth"; // 路段名称
//        boolean bAddToScene = true;                       // 是否添加到场景
//        ILink s_approach = netiface.createLinkWithLaneWidth(centerPoints1, laneWidths, linkName, bAddToScene);
//
//        if (s_approach != null) {
//            System.out.println("路段创建成功，ID: " + s_approach.id());
//        } else {
//            System.out.println("路段创建失败");
//        }
        // 创建中心线点集（三维坐标）
//        ArrayList<Point3D> centerPoints3D = new ArrayList<>();
//        Point3D startPoint3D = new Point3D(6, 300, 0); // 起点 (x, y, z)
//        Point3D endPoint3D = new Point3D(6, 25, 5);    // 终点 (x, y, z)
//        centerPoints3D.add(startPoint3D);
//        centerPoints3D.add(endPoint3D);
//
//        // 设置每条车道的宽度（从左到右）
//        ArrayList<Double> laneWidths = new ArrayList<>();
//        laneWidths.add(3.5);
//        laneWidths.add(3.0);
//        laneWidths.add(3.0);
//        System.out.println("3D路段创建成功，ID: " + laneWidths);
//        // 其他参数
//        String linkName = "南进口creatlink3dWithLaneWidth"; // 路段名称
//        boolean bAddToScene = true;                       // 是否添加到场景
//
//        // 创建带有自定义车道宽度的3D路段
//        ILink s_approach = netiface.createLink3DWithLaneWidth(centerPoints3D, laneWidths, linkName, bAddToScene, UnitOfMeasure.Metric);
//
//        if (s_approach != null) {
//            System.out.println("3D路段创建成功，ID: " + s_approach.id());
//        } else {
//            System.out.println("3D路段创建失败");
//        }

//// 定义起点和终点
//        Point3D startPoint = new Point3D(-6, -300, 0);
//        Point3D endPoint = new Point3D(-6, -25, 5);
//        ArrayList<Point3D> lPoint = new ArrayList<>();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//
//// 定义每条车道的左右和中心线点集
//        ArrayList<Point3D> lane3_left = new ArrayList<>();
//        lane3_left.add(new Point3D(-3, -300, 0));
//        lane3_left.add(new Point3D(-3, -25, 0));
//
//        ArrayList<Point3D> lane3_mid = new ArrayList<>();
//        lane3_mid.add(new Point3D(-1.5, -300, 0));
//        lane3_mid.add(new Point3D(-1.5, -25, 0));
//
//        ArrayList<Point3D> lane3_right = new ArrayList<>();
//        lane3_right.add(new Point3D(0, -300, 0));
//        lane3_right.add(new Point3D(0, -25, 0));
//
//        ArrayList<Point3D> lane2_left = new ArrayList<>();
//        lane2_left.add(new Point3D(-6, -300, 0));
//        lane2_left.add(new Point3D(-6, -25, 0));
//
//        ArrayList<Point3D> lane2_mid = new ArrayList<>();
//        lane2_mid.add(new Point3D(-4.5, -300, 0));
//        lane2_mid.add(new Point3D(-4.5, -25, 0));
//
//        ArrayList<Point3D> lane2_right = new ArrayList<>();
//        lane2_right.add(new Point3D(-3, -300, 0));
//        lane2_right.add(new Point3D(-3, -25, 0));
//
//        ArrayList<Point3D> lane1_left = new ArrayList<>();
//        lane1_left.add(new Point3D(-9, -300, 0));
//        lane1_left.add(new Point3D(-9, -25, 0));
//
//        ArrayList<Point3D> lane1_mid = new ArrayList<>();
//        lane1_mid.add(new Point3D(-7.5, -300, 0));
//        lane1_mid.add(new Point3D(-7.5, -25, 0));
//
//        ArrayList<Point3D> lane1_right = new ArrayList<>();
//        lane1_right.add(new Point3D(-6, -300, 0));
//        lane1_right.add(new Point3D(-6, -25, 0));
//        ArrayList<TreeMap<String, ArrayList<Point3D>>> lanes = new ArrayList<>();
//// 车道1
//        TreeMap<String, ArrayList<Point3D>> lane1 = new TreeMap<>();
//        lane1.put("left", lane1_left);
//        lane1.put("center", lane1_mid);
//        lane1.put("right", lane1_right);
//// 车道2
//        TreeMap<String, ArrayList<Point3D>> lane2 = new TreeMap<>();
//        lane2.put("left", lane2_left);
//        lane2.put("center", lane2_mid);
//        lane2.put("right", lane2_right);
//// 车道3
//        TreeMap<String, ArrayList<Point3D>> lane3 = new TreeMap<>();
//        lane3.put("left", lane3_left);
//        lane3.put("center", lane3_mid);
//        lane3.put("right", lane3_right);
//// 添加到列表
//        lanes.add(lane1);
//        lanes.add(lane2);
//        lanes.add(lane3);
//// 创建带有自定义车道点集的3D路段
//        String linkName = "北进口createLink3DWithLanePoints";
//        boolean bAddToScene = true;
//        ILink n_approach = netiface.createLink3DWithLanePoints(lPoint, lanes, linkName, bAddToScene, UnitOfMeasure.Metric);

// 创建西进口路段
// 定义起点和终点
//        Point startPoint = new Point(-300, 6);
//        Point endPoint = new Point(-25, 6);
//        ArrayList<Point> lPoint = new ArrayList<>();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        int laneCount = 3;
//        String linkName1 = "西进口";
//        boolean bAddToScene1 = true;
//        ILink w_approach = netiface.createLink(lPoint, laneCount, linkName1, bAddToScene1);
//// 创建东进口 3D 路段
//// 定义 3D 起点和终点
//        Point3D startPoint3D = new Point3D(300, -6, 0);
//        Point3D endPoint3D = new Point3D(25, -6, 5);
//        ArrayList<Point3D> lPoint3D = new ArrayList<>();
//        lPoint3D.add(startPoint3D);
//        lPoint3D.add(endPoint3D);
//        String linkName2 = "东进口creatlink3d";
//        boolean bAddToScene2 = true;
//        ILink e_approach = netiface.createLink3D(lPoint3D, laneCount, linkName2, bAddToScene2);
//// 定义车道编号列表
//        ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
//        lFromLaneNumber.add(2);
//        ArrayList<Integer> lToLaneNumber = new ArrayList<>();
//        lToLaneNumber.add(2);
//// 创建连接段
//        String connectorName = "东西直行";
//        boolean bAddToScene3 = true;
//        IConnector w_e_straight_connector = netiface.createConnector(w_approach.id(), e_approach.id(), lFromLaneNumber, lToLaneNumber, connectorName, bAddToScene3);


// 创建西进口路段
// 定义起点和终点
//        Point startPoint = new Point(-300, 6);
//        Point endPoint = new Point(-25, 6);
//        ArrayList<Point> lPoint = new ArrayList<>();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        int laneCount = 3;
//        String linkName1 = "西进口";
//        boolean bAddToScene1 = true;
//        ILink w_approach = netiface.createLink(lPoint, laneCount, linkName1, bAddToScene1);
//// 创建东进口 3D 路段
//// 定义 3D 起点和终点
//        Point3D startPoint3D = new Point3D(300, -6, 0);
//        Point3D endPoint3D = new Point3D(25, -6, 5);
//        ArrayList<Point3D> lPoint3D = new ArrayList<>();
//        lPoint3D.add(startPoint3D);
//        lPoint3D.add(endPoint3D);
//        String linkName2 = "东进口creatlink3d";
//        boolean bAddToScene2 = true;
//        ILink e_approach = netiface.createLink3D(lPoint3D, laneCount, linkName2, bAddToScene2);
//// 定义车道编号列表
//        ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
//        lFromLaneNumber.add(2);
//        ArrayList<Integer> lToLaneNumber = new ArrayList<>();
//        lToLaneNumber.add(2);
//// 定义车道点集，这里假设每个车道有一个点集
//        ArrayList<Point3D> lane1Points = new ArrayList<>();
//        lane1Points.add(new Point3D(0, 0, 0));
//        lane1Points.add(new Point3D(1, 1, 1));
//// 创建车道结构
//        TreeMap<String, ArrayList<Point3D>> laneMap = new TreeMap<>();
//        laneMap.put("center", lane1Points);
//// 创建车道列表
//        ArrayList<TreeMap<String, ArrayList<Point3D>>> laneConnectorWithPoints = new ArrayList<>();
//        laneConnectorWithPoints.add(laneMap);
//        String connectorName = "直行";
//        boolean bAddToScene3 = true;
//        IConnector connector = netiface.createConnector3DWithPoints(w_approach.id(), e_approach.id(), lFromLaneNumber, lToLaneNumber, laneConnectorWithPoints, connectorName, bAddToScene3, UnitOfMeasure.Metric);

// 定义起点和终点
//        Point startPoint1 = new Point(TESSNG.m2p(-300), TESSNG.m2p(-200));
//        Point endPoint1 = new Point(TESSNG.m2p(-50), TESSNG.m2p(-200));
//        ArrayList<Point> lPoint1 = new ArrayList<>();
//        lPoint1.add(startPoint1);
//        lPoint1.add(endPoint1);
//        int laneCount = 3;
//        String linkName = "信控编辑路段1";
//// 创建路段
//        ILink link1 = netiface.createLink(lPoint1, laneCount, linkName);
//        if (link1 != null) {
//            System.out.println("路段创建成功，ID: " + link1.id());
//            // 创建发车点
//            IDispatchPoint dp = netiface.createDispatchPoint(link1);
//            if (dp != null) {
//                System.out.println("发车点创建成功");
//                // 设置发车间隔，含车型组成、时间间隔、发车数
//                dp.addDispatchInterval(1, 3600, 3600);
//            } else {
//                System.out.println("发车点创建失败");
//            }
//        } else {
//            System.out.println("路段创建失败");
//        }


// 创建车辆组成及指定车辆类型
//        ArrayList<VehiComposition> vehiType_proportion_lst = new ArrayList<>();
//// 车型组成: 小客车 0.3, 大客车 0.2, 公交车 0.1, 货车 0.4
//        vehiType_proportion_lst.add(new VehiComposition(1, 0.3));
//        vehiType_proportion_lst.add(new VehiComposition(2, 0.2));
//        vehiType_proportion_lst.add(new VehiComposition(3, 0.1));
//        vehiType_proportion_lst.add(new VehiComposition(4, 0.4));
//        String compositionName = "动态创建车型组成";
//        long vehiCompositionID = netiface.createVehicleComposition(compositionName, vehiType_proportion_lst);
//        System.out.println("车辆组成 ID: " + vehiCompositionID);


//        ILink w_approach = netiface.findLink(16226);
//        GuideArrowType arrowType = GuideArrowType.StraightRight;
//
//// 获取路段 w_approach 的第一条车道
//        ILane firstLane = w_approach.lanes().get(0);
//// 创建导向箭头
//
//        IGuidArrow straitghtRightArrow = netiface.createGuidArrow(firstLane, 4, 10, arrowType);


//        IDecisionPoint decisionPoint = netiface.createDecisionPoint(link3, 30, "Decesion Point", UnitOfMeasure.Metric);
//        System.out.println("创建决策点成功");


//// 创建决策点（参数：路段，距离路段起点的位置）
//        IDecisionPoint decisionPoint = netiface.createDecisionPoint(link3, m2p(30));
//// 创建路径(左, 直, 右)
//        ArrayList<ILink> route1Links = new ArrayList<>(Arrays.asList(link3, link5, link6));
//        IRouting decisionRouting1 = netiface.createDeciRouting(decisionPoint, route1Links);
//        ArrayList<ILink> route2Links = new ArrayList<>(Arrays.asList(link3, link5, link8));
//        IRouting decisionRouting2 = netiface.createDeciRouting(decisionPoint, route2Links);
//        ArrayList<ILink> route3Links = new ArrayList<>(Arrays.asList(link3, link5, link7));
//        IRouting decisionRouting3 = netiface.createDeciRouting(decisionPoint, route3Links);
//        System.out.println("创建决策路径成功");


//// 查找路段9
//        ILink link = netiface.findLink(16940);
//// 获取最左侧车道（Java中列表索引从0开始，最后一个元素为 lanes().size()-1）
//        ILane leftLane = link.lanes().get(link.lanes().size() - 1);
//// 初始位置（可能不需要，后续会设置到400米处）
//        double initialDist = m2p(100);
//// 创建车辆采集器
//        IVehicleDrivInfoCollector collector = netiface.createVehiCollectorOnLink(leftLane, initialDist);
//// 将采集器设置到距路段起点400米处
//        collector.setDistToStart(m2p(400));
//        System.out.println("成功创建车辆采集器，ID: " + collector.id());

//        // 获取TESSNG接口并查找路段9
//        ILink link = TESSNG.tessngIFace().netInterface().findLink(16940);
//// 获取最左侧车道（假设路段存在且至少有1个车道）
//        ILane leftLane = link.lanes().get(link.lanes().size() - 1);
//// 创建排队计数器（假设方法存在且参数正确）
//        IVehicleQueueCounter counter = TESSNG.tessngIFace().netInterface().createVehiQueueCounterOnLink(leftLane, m2p(100));
//// 输出计数器位置（假设counter不为null）
//        Point point = counter.point();
//        System.out.printf("计数器所在点坐标为: (%.2f, %.2f)%n", point.getX(), point.getY());

//        // 获取TESSNG接口并查找路段9
//        ILink link = TESSNG.tessngIFace().netInterface().findLink(16940);
//// 创建行程检测器
//        ArrayList<IVehicleTravelDetector> detectors =
//                TESSNG.tessngIFace().netInterface().createVehicleTravelDetector_link2link(link, link, m2p(50), m2p(550));
//// 设置检测时间范围
//        for (IVehicleTravelDetector detector : detectors) {
//            detector.setFromTime(10);
//            detector.setToTime(60);
//        }
//        System.out.println("成功创建行程检测器");

        // 获取TESSNG核心接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//// 1. 创建路段（路段南进口）
//        ArrayList<Point> points = new ArrayList<>();
//        points.add(new Point(m2p(-100), m2p(25)));  // 起点
//        points.add(new Point(m2p(100), m2p(25)));   // 终点
//        ILink s_approach = netInterface.createLink(points, 3, "南进口");
//        System.out.println("路段创建成功，ID: " + s_approach.id());
//// 2. 定义信号控制器
//        ISignalController signalController = netInterface.createSignalController("主信号控制器");
//// 3. 创建信号控制方案（周期60秒，绿信比50%，相位差0，开始时间0）
//        ISignalPlan signalPlan = netInterface.createSignalPlan(signalController, "基础方案", 60, 50, 0, 0);
//// 4. 创建信号相位（直行相位）
//        ArrayList<ColorInterval> phaseColors = new ArrayList<>();
//        phaseColors.add(new ColorInterval("绿", 30));  // 绿灯30秒
//        phaseColors.add(new ColorInterval("黄", 3));    // 黄灯3秒
//        phaseColors.add(new ColorInterval("红", 27));  // 红灯27秒
//        ISignalPhase signalPhase = netInterface.createSignalPlanSignalPhase(signalPlan, "直行相位", phaseColors);
//        System.out.println("创建信号相位成功");
//// 5. 从路段中获取车道对象列表
//        List<ILaneObject> lLaneObjects = s_approach.laneObjects();
//        System.out.println("车道对象列表获取成功");
//        System.out.println("车道对象列表大小: " + lLaneObjects.size());
//// 6. 循环创建信号灯
//        for (int index = 0; index < lLaneObjects.size(); index++) {
//            ILaneObject laneObj = lLaneObjects.get(index);
//            // 创建信号灯
//            ISignalLamp signalLamp = netInterface.createSignalLamp(
//                    signalPhase,                     // 关联信号相位
//                    "信号灯" + (index + 1),         // 名称
//                    laneObj.id(),        // 来源车道ID
//                    -1,          // 目标车道ID
//                    m2p(2.0)                      // 位置
//            );
//            ISignalLamp signalLamp2 =netiface.createTrafficSignalLamp(signalController, "信号灯" + (index + 1), laneObj.id(), -1, m2p(2.0));
//            if (signalLamp2 != null){
//                System.out.println("信号灯创建成功: " + signalLamp.name());
//            }
//            if (signalLamp != null) {
//                System.out.println("信号灯创建成功: " + signalLamp.name());
//            } else {
//                System.out.println("信号灯创建失败，index = " + index);
//            }
//        }

        // 获取TESSNG核心接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//// 创建两个路段
//        ArrayList<Point> points1 = new ArrayList<>();
//        points1.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
//        points1.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        ILink link10 = netInterface.createLink(points1, 3, "路段10");
//        ArrayList<Point> points2 = new ArrayList<>();
//        points2.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        points2.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
//        ILink link11 = netInterface.createLink(points2, 3, "路段11");
//        if (link10 != null && link11 != null) {
//            ArrayList<ILink> linksForBusLine = new ArrayList<>();
//            linksForBusLine.add(link10);
//            linksForBusLine.add(link11);
//            IBusLine busLine = netInterface.createBusLine(linksForBusLine);
//            System.out.println("公交线路创建成功" + busLine.id());
//            if (busLine != null) {
//                busLine.setDesirSpeed(TESSNG.m2p(60));
//                System.out.println("公交线路创建成功，期望速度设置为: " + busLine.desirSpeed());
//            }
//        }

        // 获取 TESS NG 接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//// 创建两个路段
//        ArrayList<Point> points1 = new ArrayList<>();
//        points1.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
//        points1.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        ILink link10 = netInterface.createLink(points1, 3, "路段10");
//        ArrayList<Point> points2 = new ArrayList<>();
//        points2.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        points2.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
//        ILink link11 = netInterface.createLink(points2, 3, "路段11");
//        if (link10 == null || link11 == null) {
//            System.out.println("路段 link10 或 link11 创建失败");
//            return;
//        }
//// 创建公交线路
//        ArrayList<ILink> linksForBusLine = new ArrayList<>();
//        linksForBusLine.add(link10);
//        linksForBusLine.add(link11);
//        IBusLine busLine = netInterface.createBusLine(linksForBusLine);
//        if (busLine != null) {
//            // 设置公交线路期望速度为 60 km/h，转换为 TESS NG 内部单位
//            busLine.setDesirSpeed(TESSNG.m2p(60));
//            System.out.println("公交线路创建成功，期望速度设置为: " + busLine.desirSpeed());
//            // 获取路段的车道
//            List<ILane> lanes10 = link10.lanes();
//            List<ILane> lanes11 = link11.lanes();
//            if (lanes10 != null && !lanes10.isEmpty() &&
//                    lanes11 != null && !lanes11.isEmpty()) {
//                ILane lane10 = lanes10.get(0);  // 第一个车道
//                ILane lane11 = lanes11.get(0);  // 第一个车道
//                // 创建公交站点
//                IBusStation busStation1 = netInterface.createBusStation(
//                        lane10,
//                        TESSNG.m2p(30),  // 站点位置在车道上 30 米处
//                        TESSNG.m2p(50),  // 停靠点距离车道中心线 50 米
//                        "公交站1"
//                );
//                IBusStation busStation2 = netInterface.createBusStation(
//                        lane11,
//                        TESSNG.m2p(15),  // 站点位置在车道上 15 米处
//                        TESSNG.m2p(50),  // 停靠点距离车道中心线 50 米
//                        "公交站2"
//                );
//                if (busStation1 != null && busStation2 != null) {
//                    System.out.println("公交站点创建成功");
//                } else {
//                    System.out.println("公交站点创建失败");
//                }
//            } else {
//                System.out.println("路段车道为空");
//            }
//        } else {
//            System.out.println("公交线路创建失败");
//        }

//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//        ArrayList<Point> points1 = new ArrayList<>();
//        points1.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
//        points1.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        ILink link10 = netInterface.createLink(points1, 3, "路段10");
//        ArrayList<Point> points2 = new ArrayList<>();
//        points2.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        points2.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
//        ILink link11 = netInterface.createLink(points2, 3, "路段11");
//        if (link10 == null || link11 == null) {
//            System.out.println("路段 link10 或 link11 创建失败");
//            return;
//        }
//        ArrayList<ILink> linksForBusLine = new ArrayList<>();
//        linksForBusLine.add(link10);
//        linksForBusLine.add(link11);
//        IBusLine busLine = netInterface.createBusLine(linksForBusLine);
//        if (busLine != null) {
//            busLine.setDesirSpeed(TESSNG.m2p(60));
//            System.out.println("公交线路创建成功，期望速度设置为: " + busLine.desirSpeed());
//            List<ILane> lanes10 = link10.lanes();
//            List<ILane> lanes11 = link11.lanes();
//            if (lanes10 != null && !lanes10.isEmpty() &&
//                    lanes11 != null && !lanes11.isEmpty()) {
//                ILane lane10 = lanes10.get(0);
//                ILane lane11 = lanes11.get(0);
//                IBusStation busStation1 = netInterface.createBusStation(
//                        lane10,
//                        TESSNG.m2p(30),
//                        TESSNG.m2p(50),
//                        "公交站1"
//                );
//                IBusStation busStation2 = netInterface.createBusStation(
//                        lane11,
//                        TESSNG.m2p(15),
//                        TESSNG.m2p(50),
//                        "公交站2"
//                );
//                if (busStation1 != null && busStation2 != null) {
//                    System.out.println("公交站点创建成功");
//                    if (netInterface.addBusStationToLine(busLine, busStation1)) {
//                        busStation1.setType(2);  // 设置为上客站
//                        System.out.println("公交站1已关联到公交线路");
//                    }
//                    if (netInterface.addBusStationToLine(busLine, busStation2)) {
//                        System.out.println("公交站2已关联到公交线路");
//                    }
//
//                } else {
//                    System.out.println("公交站点创建失败");
//                }
//            } else {
//                System.out.println("路段车道为空");
//            }
//        }



// 创建四个方向的路段
//        ArrayList<Point> westPoints = new ArrayList<>();
//        westPoints.add(new Point(m2p(-300), m2p(0)));
//        westPoints.add(new Point(m2p(0), m2p(0)));
//        ILink w_approach = netInterface.createLink(westPoints, 3, "西进口");
//
//        ArrayList<Point> eastPoints = new ArrayList<>();
//        eastPoints.add(new Point(m2p(0), m2p(0)));
//        eastPoints.add(new Point(m2p(300), m2p(0)));
//        ILink e_approach = netInterface.createLink(eastPoints, 3, "东进口");
//
//        ArrayList<Point> northPoints = new ArrayList<>();
//        northPoints.add(new Point(m2p(0), m2p(300)));
//        northPoints.add(new Point(m2p(0), m2p(0)));
//        ILink n_approach = netInterface.createLink(northPoints, 3, "北进口");
//
//        ArrayList<Point> southPoints = new ArrayList<>();
//        southPoints.add(new Point(m2p(0), m2p(0)));
//        southPoints.add(new Point(m2p(0), m2p(-300)));
//        ILink s_approach = netInterface.createLink(southPoints, 3, "南进口");
//
//// 创建斑马线区域
//        Point crosswalkPoint1 = new Point(m2p(0), m2p(0));
//        Point crosswalkPoint2 = new Point(m2p(10), m2p(0));
//        IPedestrianCrossWalkRegion n_crosswalk = netInterface.createPedestrianCrossWalkRegion(
//                crosswalkPoint1, crosswalkPoint2);
//        IPedestrianCrossWalkRegion s_crosswalk = netInterface.createPedestrianCrossWalkRegion(
//                new Point(m2p(0), m2p(0)), new Point(m2p(10), m2p(0)));
//        IPedestrianCrossWalkRegion w_crosswalk = netInterface.createPedestrianCrossWalkRegion(
//                new Point(m2p(0), m2p(0)), new Point(m2p(0), m2p(10)));
//        IPedestrianCrossWalkRegion e_crosswalk = netInterface.createPedestrianCrossWalkRegion(
//                new Point(m2p(0), m2p(0)), new Point(m2p(0), m2p(10)));
//
//// 创建信号控制系统
//        createSignalControl(netInterface, w_approach, e_approach, n_approach, s_approach,
//                n_crosswalk, s_crosswalk, w_crosswalk, e_crosswalk);

//// 获取TESSNG核心接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//// 1. 获取已有路段（假设路段ID为12345）
//        ILink existingLink = netInterface.findLink(21177);  // 替换为实际存在的路段ID
//        if (existingLink == null) {
//            System.out.println("路段不存在，ID=12345");
//            return;
//        }
//// 2. 定义信号控制器
//        ISignalController signalController = netInterface.createSignalController("主信号控制器");
//// 3. 创建信号控制方案（周期60秒，绿信比50%，相位差0，开始时间0）
//        ISignalPlan signalPlan = netInterface.createSignalPlan(signalController, "基础方案", 60, 50, 0, 0);
//// 4. 创建信号相位（直行相位）
//        ArrayList<ColorInterval> phaseColors = new ArrayList<>();
//        phaseColors.add(new ColorInterval("绿", 30));  // 绿灯30秒
//        phaseColors.add(new ColorInterval("黄", 3));    // 黄灯3秒
//        phaseColors.add(new ColorInterval("红", 27));  // 红灯27秒
//        ISignalPhase signalPhase = netInterface.createSignalPlanSignalPhase(signalPlan, "直行相位", phaseColors);
//// 5. 从已有路段中获取车道对象列表
//        List<ILaneObject> lLaneObjects = existingLink.laneObjects();
//        if (lLaneObjects == null || lLaneObjects.isEmpty()) {
//            System.out.println("该路段没有车道对象");
//            return;
//        }
//// 6. 循环创建信号灯
//        for (int index = 0; index < lLaneObjects.size(); index++) {
//            ILaneObject laneObj = lLaneObjects.get(index);
//            // 创建信号灯
//            System.out.println("正在创建信号灯: " + laneObj.id());
//            ISignalLamp signalLamp = netInterface.createSignalLamp(
//                    signalPhase,                     // 关联信号相位
//                    "信号灯" + (index + 1),         // 名称
//                    laneObj.fromLaneObject().id(),   // 来源车道ID
//                    laneObj.toLaneObject().id(),     // 目标车道ID
//                    m2p(2.0)                      // 位置（单位转换）
//            );
//            if (signalLamp != null) {
//                System.out.println("信号灯创建成功: " + signalLamp.name());
//            } else {
//                System.out.println("信号灯创建失败，index = " + index);
//            }
//        }

        // 获取 TESSNG 核心接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//
//        // 构造 link3
//        Point startPoint3 = new Point(TESSNG.m2p(-300), TESSNG.m2p(25));
//        Point endPoint3 = new Point(TESSNG.m2p(-150), TESSNG.m2p(25));
//        ArrayList<Point> lPoint3 = new ArrayList<>();
//        lPoint3.add(startPoint3);
//        lPoint3.add(endPoint3);
//        ILink link3 = netiface.createLink(lPoint3, 3, "路段3");
//
//        // 构造 link5
//        Point startPoint5 = new Point(TESSNG.m2p(150), TESSNG.m2p(25));
//        Point endPoint5 = new Point(TESSNG.m2p(300), TESSNG.m2p(25));
//        ArrayList<Point> lPoint5 = new ArrayList<>();
//        lPoint5.add(startPoint5);
//        lPoint5.add(endPoint5);
//        ILink link5 = netiface.createLink(lPoint5, 3, "路段5");
//
//        // 构造 link6
//        Point startPoint6 = new Point(TESSNG.m2p(-300), TESSNG.m2p(50));
//        Point endPoint6 = new Point(TESSNG.m2p(300), TESSNG.m2p(50));
//        ArrayList<Point> lPoint6 = new ArrayList<>();
//        lPoint6.add(startPoint6);
//        lPoint6.add(endPoint6);
//        ILink link6 = netiface.createLink(lPoint6, 3, "路段6");

//        if (link3 != null && link5 != null && link6 != null) {
//            // 创建决策点（在 link3 上距离起点 30 米处）
//            IDecisionPoint decisionPoint = netInterface.createDecisionPoint(link3, m2p(30), "决策点1");
//            if (decisionPoint != null) {
//                // 创建三条路径
//                ArrayList<ILink> route1Links = new ArrayList<>();
//                route1Links.add(link3);
//                route1Links.add(link5);
//                route1Links.add(link6);
//                IRouting decisionRouting1 = netInterface.createDeciRouting(decisionPoint, route1Links);
//
//                ArrayList<ILink> route2Links = new ArrayList<>();
//                route2Links.add(link3);
//                route2Links.add(link5);
//                route2Links.add(link6); // 可替换为 link8 或其他路段
//                IRouting decisionRouting2 = netInterface.createDeciRouting(decisionPoint, route2Links);
//
//                ArrayList<ILink> route3Links = new ArrayList<>();
//                route3Links.add(link3);
//                route3Links.add(link5);
//                route3Links.add(link6); // 可替换为 link7 或其他路段
//                IRouting decisionRouting3 = netInterface.createDeciRouting(decisionPoint, route3Links);
//
//                if (decisionRouting1 != null && decisionRouting2 != null && decisionRouting3 != null) {
//                    // 调用 updateDecisionPointWithFlowRatio 方法
//                    IDecisionPoint updatedDecisionPoint = updateDecisionPointWithFlowRatio(
//                            netInterface,
//                            decisionPoint,
//                            decisionRouting1,
//                            decisionRouting2,
//                            decisionRouting3
//                    );
//
//                    if (updatedDecisionPoint != null) {
//                        System.out.println("决策点已成功更新，名称: " + updatedDecisionPoint.name());
//                    } else {
//                        System.out.println("决策点更新失败");
//                    }
//                } else {
//                    System.out.println("路径创建失败");
//                }
//            } else {
//                System.out.println("决策点创建失败");
//            }
//        } else {
//            System.out.println("路段 link3、link5 或 link6 创建失败");
//        }

//        IDecisionPoint decisionPoint = netiface.createDecisionPoint(link3, m2p(30), "测试决策点");
//        ArrayList<ILink> route1Links = new ArrayList<>();
//        route1Links.add(link3);
//        route1Links.add(link5);
//        IRouting decisionRouting1 = netiface.createDeciRouting(decisionPoint, route1Links);
//
//        ArrayList<ILink> route2Links = new ArrayList<>();
//        route2Links.add(link3);
//        route2Links.add(link6);
//        IRouting decisionRouting2 = netiface.createDeciRouting(decisionPoint, route2Links);
//
//        ArrayList<ILink> route3Links = new ArrayList<>();
//        route3Links.add(link3);
//        route3Links.add(link5);
//        route3Links.add(link6);
//        IRouting decisionRouting3 = netiface.createDeciRouting(decisionPoint, route3Links);
//        if (decisionRouting3 != null) {
//            if (netiface.removeDeciRouting(decisionPoint, decisionRouting3)) {
//                System.out.println("删除右转路径成功。");
//            } else {
//                System.out.println("删除右转路径失败。");
//            }
//        } else {
//            System.out.println("decisionRouting3 创建失败，无法测试删除功能。");
//        }

//        netiface.buildNetGrid(10, UnitOfMeasure.Metric);


        // 获取 TESS NG 接口
//        TessInterface tessng = TESSNG.tessngIFace();
//// 获取所有分段
//        ArrayList<ISection> sections = netInterface.sections();
//// 创建一个点对象
//        Point point = new Point(0, 0);
//// 在所有分段上定位该点，获取位置信息
//        ArrayList<Location> locations = netInterface.locateOnSections(point, sections);
//// 遍历结果并输出信息
//        if (locations != null && !locations.isEmpty()) {
//            for (Location location : locations) {
//                // 输出相关车道或车道连接的 ID
//                System.out.println("相关车道或车道连接为: " + location.getPLaneObject().id());
//                // 输出最近点的坐标
//                Point nearestPoint = location.getPoint();
//                System.out.println("最近点坐标: (" + nearestPoint.getX() + ", " + nearestPoint.getY() + ")");
//                // 输出到最近点的距离
//                System.out.println("到最近点的最短距离: " + location.getLeastDist());
//                // 输出最近点到起点的里程
//                System.out.println("最近点到起点的里程: " + location.getDistToStart());
//                // 输出最近点所在分段序号
//                System.out.println("最近点所在分段序号: " + location.getSegmIndex());
//                System.out.println(); // 换行
//            }
//        } else {
//            System.out.println("未找到任何匹配的车道或车道连接。");
//        }

//        // 获取 TESS NG 接口
//        TessInterface tessng = TESSNG.tessngIFace();
//// 创建起点和终点坐标
//        Point startPoint = new Point(-300, 6);
//        Point endPoint = new Point(-25, 6);
//// 创建点列表
//        ArrayList<Point> lPoint = new ArrayList<>();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//// 创建路段
//        ILink w_approach = netiface.createLink(lPoint, 3, "西进口", true, UnitOfMeasure.Metric);
//        if (w_approach != null) {
//            System.out.println("路段创建成功: " + w_approach.name());
//            // 创建要移动的路段列表
//            ArrayList<ILink> linksToMove = new ArrayList<>();
//            linksToMove.add(w_approach);
//            // 移动路段
//            Point offset = new Point(0, 300);
//            netiface.moveLinks(linksToMove, offset, UnitOfMeasure.Metric);
//        }

//        IRoadWorkZone workZone = createRoadWorkZone(netiface, link6);

//        // 创建西进口路段
//        Point startPoint = new Point(TESSNG.m2p(-300), TESSNG.m2p(0));
//        Point endPoint = new Point(TESSNG.m2p(300), TESSNG.m2p(0));
//        ArrayList<Point> lPoint = new ArrayList<>();
//        lPoint.add(startPoint);
//        lPoint.add(endPoint);
//        ILink w_approach = netiface.createLink(lPoint, 3, "西进口", true);
//
//// 创建东出口路段
//        Point startPoint2 = new Point(TESSNG.m2p(300), TESSNG.m2p(0));
//        Point endPoint2 = new Point(TESSNG.m2p(600), TESSNG.m2p(0));
//        ArrayList<Point> lPoint2 = new ArrayList<>();
//        lPoint2.add(startPoint2);
//        lPoint2.add(endPoint2);
//        ILink e_outgoing = netiface.createLink(lPoint2, 3, "东出口", true);
//
//// 创建南出口路段
//        Point startPoint3 = new Point(TESSNG.m2p(0), TESSNG.m2p(-300));
//        Point endPoint3 = new Point(TESSNG.m2p(0), TESSNG.m2p(300));
//        ArrayList<Point> lPoint3 = new ArrayList<>();
//        lPoint3.add(startPoint3);
//        lPoint3.add(endPoint3);
//        ILink s_outgoing = netiface.createLink(lPoint3, 3, "南出口", true);
//        createAndManageBusLine(netiface, w_approach, e_outgoing, s_outgoing);
//        List<IBusLine> busLines = netiface.buslines();

// 创建路段
//        ArrayList<Point> points = new ArrayList<>();
//        points.add(new Point(TESSNG.m2p(-300), TESSNG.m2p(200)));
//        points.add(new Point(TESSNG.m2p(300), TESSNG.m2p(200)));
//        ILink link = netiface.createLink(points, 4, "主干道");
//// 创建事故区参数对象
//        DynaAccidentZoneParam accidentZoneParam = new DynaAccidentZoneParam();
//// 设置事故区参数
//        accidentZoneParam.setRoadId(link.id());               // 使用创建的 link 的 ID
//        accidentZoneParam.setName("最左侧车道发生事故");
//        accidentZoneParam.setLocation(TESSNG.m2p(700));       // 位置
//        accidentZoneParam.setLength(TESSNG.m2p(50));         // 事故区长度
//        accidentZoneParam.setStartTime(0);                   // 开始时间（秒）
//        accidentZoneParam.setDuration(500);                  // 持续时间（秒）
//        accidentZoneParam.setNeedStayed(true);               // 是否需要停留
//        accidentZoneParam.setLimitSpeed(TESSNG.m2p(55));      // 限速
//        accidentZoneParam.setControlLength(TESSNG.m2p(100));   // 控制区长度
//        ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
//        fromLaneNumbers.add(2);
//        accidentZoneParam.setMlFromLaneNumber(fromLaneNumbers);
//// 创建事故区
//        IAccidentZone accidentZone = netiface.createAccidentZone(accidentZoneParam);
//        System.out.println("事故区创建成功");
//// 创建事故区时间段参数
//        DynaAccidentZoneIntervalParam intervalParam = new DynaAccidentZoneIntervalParam();
//        intervalParam.setAccidentZoneId(accidentZone.id());
//        intervalParam.setStartTime(501);
//        intervalParam.setEndTime(1000);
//        intervalParam.setLocation(TESSNG.m2p(200));
//        intervalParam.setLength(TESSNG.m2p(50));
//        intervalParam.setLimitedSpeed(TESSNG.m2p(10));
//        intervalParam.setControlLength(TESSNG.m2p(100));
//        ArrayList<Integer> intervalLaneNumbers = new ArrayList<>();
//        intervalLaneNumbers.add(0);
//        intervalLaneNumbers.add(1);
//        intervalLaneNumbers.add(3);
//        System.out.println("intervalLaneNumbers的值：" + intervalLaneNumbers);
//        intervalParam.setMlFromLaneNumber(intervalLaneNumbers);
//        IAccidentZoneInterval added = accidentZone.addAccidentZoneInterval(intervalParam);
//        if (added != null) {
//            System.out.println("事故区时间段信息添加成功");
//        } else {
//            System.out.println("事故区时间段信息添加失败");
//        }

        // 获取 TESS NG 接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//// 创建限行区参数对象
//        DynaLimitedZoneParam param = new DynaLimitedZoneParam();
//// 设置限行区参数
//        param.setName("限行区测试");                // 名称
//        param.setRoadId(28679);                         // 道路ID
//        param.setLocation(m2p(50));                 // 限行区起始位置
//        param.setLength(m2p(100));                  // 限行区长度
//        param.setLimitSpeed(m2p(40));               // 限速
//        param.setDuration(3600);                    // 持续时间
//// 设置限行车道编号
//        ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
//        fromLaneNumbers.add(0);  // 本例限行右侧第一车道
//        param.setMlFromLaneNumber(fromLaneNumbers);
//// 创建限行区
//        ILimitedZone limitedZone = netInterface.createLimitedZone(param);
//        if (limitedZone != null) {
//            System.out.println("限行区创建成功: " + limitedZone.name());
//        } else {
//            System.out.println("限行区创建失败");
//        }

        // 获取 TESS NG 接口
        TessInterface tessInterface = TESSNG.tessngIFace();
        NetInterface netInterface = tessInterface.netInterface();
//// 创建上游路段（upstreamLink）
        ArrayList<Point> upstreamPoints = new ArrayList<>();
        upstreamPoints.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
        upstreamPoints.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
        ILink upstreamLink = netInterface.createLink(upstreamPoints, 3, "上游路段");
//// 创建下游路段（downstreamLink）
//        ArrayList<Point> downstreamPoints = new ArrayList<>();
//        downstreamPoints.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
//        downstreamPoints.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
//        ILink downstreamLink = netInterface.createLink(downstreamPoints, 3, "下游路段");

//// 创建施工区参数对象
        DynaRoadWorkZoneParam param = new DynaRoadWorkZoneParam();
// 设置施工区参数
        param.setName("施工区");
        param.setRoadId(link1.id());
        param.setLocation(TESSNG.m2p(200));           // 位置
        param.setLength(TESSNG.m2p(100));             // 长度
        param.setUpCautionLength(TESSNG.m2p(10));     // 提前警示长度
        param.setUpTransitionLength(TESSNG.m2p(10));  // 过渡区长度
        param.setUpBufferLength(TESSNG.m2p(10));      // 缓冲区长度
        param.setDownTransitionLength(TESSNG.m2p(10));// 下游过渡区长度
        param.setDownTerminationLength(TESSNG.m2p(10));// 终止区长度
// 设置车道编号（注意：Java 中使用 List<Integer>）
        ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
        fromLaneNumbers.add(1);
        param.setMlFromLaneNumber(fromLaneNumbers);  // 设置施工影响的车道编号
        param.setStartTime(0);                       // 开始时间（秒）
        param.setDuration(3600);                    // 持续时间（秒）
        param.setLimitSpeed(TESSNG.m2p(42));         // 限速，转换为 TESS NG 内部单位
// 创建施工区
        IRoadWorkZone workZone = netInterface.createRoadWorkZone(param, UnitOfMeasure.Metric); // Metric 单位制
        if (workZone != null) {
            System.out.println("施工区创建成功: " + workZone.name());
        } else {
            System.out.println("施工区创建失败");
        }
//// 获取 TESSNG 接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//
//// 创建一个路段（假设路段长度为 1000 米）
//        ArrayList<Point> points = new ArrayList<>();
//        points.add(new Point(m2p(-500), m2p(0)));
//        points.add(new Point(m2p(500), m2p(0)));
//        ILink tollLink = netInterface.createLink(points, 3, "收费路段");
//
//// 在路段上距离起点 700 米处创建一个收费决策点
//        double decisionPointDistance = m2p(700);  // 转换为 TESS NG 内部单位
//        ITollDecisionPoint tollDecisionPoint = netInterface.createTollDecisionPoint(tollLink, decisionPointDistance, "收费决策点1");
//
//// 判断是否创建成功
//        if (tollDecisionPoint != null) {
//            System.out.println("收费决策点创建成功，ID: " + tollDecisionPoint.id());
//        } else {
//            System.out.println("收费决策点创建失败");
//        }
// 获取 TESSNG 接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//
//// 创建一个路段（假设路段长度为 1000 米）
//        ArrayList<Point> points = new ArrayList<>();
//        points.add(new Point(m2p(-500), m2p(0)));
//        points.add(new Point(m2p(500), m2p(0)));
//        ILink tollLink = netInterface.createLink(points, 3, "收费路段");
//
//// 在路段上距离起点 700 米处创建一个收费决策点
//        double decisionPointDistance = m2p(700);  // 转换为 TESS NG 内部单位
//        ITollDecisionPoint tollDecisionPoint = netInterface.createTollDecisionPoint(tollLink, decisionPointDistance, "收费决策点1");
//
//// 创建路径所经过的其他路段
//        ArrayList<Point> points2 = new ArrayList<>();
//        points2.add(new Point(m2p(500), m2p(0)));
//        points2.add(new Point(m2p(1000), m2p(0)));
//        ILink tollLink2 = netInterface.createLink(points2, 3, "收费路段2");
//
//// 创建收费车道参数
//        DynaTollLane tollLaneParam = new DynaTollLane();
//        tollLaneParam.setRoadId(tollLink.id());     // 设置收费路段 ID
//        tollLaneParam.setLocation(m2p(700));       // 收费车道起始位置
//        tollLaneParam.setLength(m2p(100));         // 收费车道长度
//        tollLaneParam.setLaneNumber(0);            // 设置车道编号（从0开始）
//        tollLaneParam.setStartTime(0);             // 开始时间
//        tollLaneParam.setEndTime(999999);         // 结束时间
//
//// 创建收费车道
//        ITollLane tollLane = netInterface.createTollLane(tollLaneParam);
//        if (tollLane == null) {
//            System.out.println("收费车道创建失败");
//            return;
//        }
//
//// 创建收费路径（绑定到收费车道）
//        ITollRouting tollRouting = netInterface.createTollRouting(tollDecisionPoint, tollLane);
//        if (tollRouting != null) {
//            System.out.println("收费路径创建成功，ID: " + tollRouting.id());
//        } else {
//            System.out.println("收费路径创建失败");
//        }

//        LayerInfo pedLayer = netiface.addLayerInfo("行人图层", 0.0, true, false);
//        System.out.println("图层创建成功: " + pedLayer.getId());

//        // 创建第一个行人矩形面域
//        Point point1 = new Point(-300, -300);
//        Point point2 = new Point(-400, -400);
//        IPedestrianRectRegion leftupArea = netiface.createPedestrianRectRegion(point1, point2);
//// 创建第二个行人矩形面域
//        Point point3 = new Point(-400, -400);
//        Point point4 = new Point(-500, -500);
//        IPedestrianRectRegion leftupArea1 = netiface.createPedestrianRectRegion(point3, point4);
//// 删除第二个面域
//        netiface.removePedestrianRectRegion(leftupArea1);

//// 获取 TESS NG 接口
//        TessInterface tessInterface = TESSNG.tessngIFace();
//        NetInterface netInterface = tessInterface.netInterface();
//
//// 创建信号控制器
//        ISignalController signalController = netInterface.createSignalController("主信号控制器");
//        if (signalController == null) {
//            System.out.println("信号控制器创建失败");
//            return;
//        }
//
//// 创建斑马线区域（s_crosswalk）
//        Point crosswalkPoint1 = new Point(m2p(0), m2p(0));
//        Point crosswalkPoint2 = new Point(m2p(10), m2p(0));
//        IPedestrianCrossWalkRegion s_crosswalk = netInterface.createPedestrianCrossWalkRegion(
//                crosswalkPoint1, crosswalkPoint2);
//
//        if (s_crosswalk == null) {
//            System.out.println("南斑马线区域创建失败");
//            return;
//        }
//
//// 创建斑马线信号灯
//        ICrosswalkSignalLamp signalLamp1_positive = netInterface.createCrossWalkSignalLamp(
//                signalController,
//                "南斑马线信号灯",
//                s_crosswalk.getId(),
//                new Point(2, 0),
//                true
//        );
//
//        if (signalLamp1_positive != null) {
//            System.out.println("南斑马线信号灯创建成功: " + signalLamp1_positive.name());
//        } else {
//            System.out.println("南斑马线信号灯创建失败");
//        }




//
//        if (link3 != null && link4 != null) {
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
//            lFromLaneNumber.add(Integer.valueOf(1));
//            lFromLaneNumber.add(Integer.valueOf(2));
//            lFromLaneNumber.add(Integer.valueOf(3));
//            ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
//            lToLaneNumber.add(Integer.valueOf(1));
//            lToLaneNumber.add(Integer.valueOf(2));
//            lToLaneNumber.add(Integer.valueOf(3));
//            IConnector conn1 = netiface.createConnector(link3.id(), link4.id(), lFromLaneNumber, lToLaneNumber, "连接段1", true);
//
//        }
//
//        // 创建第二条连接段
//        if (link4 != null && link5 != null) {
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
//            lFromLaneNumber.add(Integer.valueOf(1));
//            lFromLaneNumber.add(Integer.valueOf(2));
//            lFromLaneNumber.add(Integer.valueOf(3));
//            ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
//            lToLaneNumber.add(Integer.valueOf(1));
//            lToLaneNumber.add(Integer.valueOf(2));
//            lToLaneNumber.add(Integer.valueOf(3));
//            IConnector conn2 = netiface.createConnector(link4.id(), link5.id(), lFromLaneNumber, lToLaneNumber, "连接段2", true);
//        }
//
//        // 创建第三条连接段
//        if (link7 != null && link8 != null) {
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
//            lFromLaneNumber.add(Integer.valueOf(1));
//            lFromLaneNumber.add(Integer.valueOf(2));
//            lFromLaneNumber.add(Integer.valueOf(3));
//            ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
//            lToLaneNumber.add(Integer.valueOf(1));
//            lToLaneNumber.add(Integer.valueOf(2));
//            lToLaneNumber.add(Integer.valueOf(3));
//            IConnector conn3 = netiface.createConnector(link7.id(), link8.id(), lFromLaneNumber, lToLaneNumber, "动态发车连接段", true);
//        }
//        ArrayList<ILink> routingLinks = new ArrayList<ILink>();
//        routingLinks.add(link7);
//        routingLinks.add(link8);
//        IRouting routing = netiface.createRouting(routingLinks);
//
//
//        ArrayList<Point> centerPoints = link1.centerBreakPoints();
//        for(int i = 0, size = centerPoints.size(); i < size; ++i){
//            Point p = centerPoints.get(i);
//            System.out.println("p" + i + ".x" + p.getX());
//            System.out.println("p" + i + ".y" + p.getY());
//        }
//        System.out.println("link id:" + link1.id());
//        System.out.println("link name:" + link1.name());
//        System.out.println("link length:" + TESSNG.p2m(link1.length()));



//        simuInterface.startSimu();
//        System.out.println("模拟开始");

        TessPlugin tessPlugin = TESSNG.tessngPlugin();




    }
//    public void createSignalControl(NetInterface netInterface, ILink w_approach, ILink e_approach,
//                                    ILink n_approach, ILink s_approach, IPedestrianCrossWalkRegion n_crosswalk,
//                                    IPedestrianCrossWalkRegion s_crosswalk, IPedestrianCrossWalkRegion w_crosswalk, IPedestrianCrossWalkRegion e_crosswalk) {
//
//        // 创建信号机
//        ISignalController signalController = netInterface.createSignalController("交叉口1");
//        System.out.println("信号机创建成功，ID: " + signalController.id());
//        // 创建信控方案（周期150秒，绿信比50%，相位差0，开始时间1800秒）
//        ISignalPlan signalPlan = netInterface.createSignalPlan(
//                signalController, "早高峰", 150, 50, 0, 1800);
//
//        // 创建东西直行相位
//        ArrayList<ColorInterval> w_e_straight_phasecolor = new ArrayList<>();
//        w_e_straight_phasecolor.add(new ColorInterval("绿", 50));
//        w_e_straight_phasecolor.add(new ColorInterval("黄", 3));
//        w_e_straight_phasecolor.add(new ColorInterval("红", 97));
//        ISignalPhase w_e_straight_phase = netInterface.createSignalPlanSignalPhase(
//                signalPlan, "东西直行", w_e_straight_phasecolor);
//        System.out.println("东西直行相位创建成功，ID: " + w_e_straight_phase.id());
//        // 创建东西直行行人相位
//        ISignalPhase we_ped_phase = netInterface.createSignalPlanSignalPhase(
//                signalPlan, "东西直行行人", w_e_straight_phasecolor);
//
//        // 创建东西左转相位
//        ArrayList<ColorInterval> w_e_left_phasecolor = new ArrayList<>();
//        w_e_left_phasecolor.add(new ColorInterval("红", 53));
//        w_e_left_phasecolor.add(new ColorInterval("绿", 30));
//        w_e_left_phasecolor.add(new ColorInterval("黄", 3));
//        w_e_left_phasecolor.add(new ColorInterval("红", 64));
//        ISignalPhase w_e_left_phase = netInterface.createSignalPlanSignalPhase(
//                signalPlan, "东西左转", w_e_left_phasecolor);
//
//        // 创建南北直行相位
//        ArrayList<ColorInterval> s_n_straight_phasecolor = new ArrayList<>();
//        s_n_straight_phasecolor.add(new ColorInterval("红", 86));
//        s_n_straight_phasecolor.add(new ColorInterval("绿", 30));
//        s_n_straight_phasecolor.add(new ColorInterval("黄", 3));
//        s_n_straight_phasecolor.add(new ColorInterval("红", 31));
//        ISignalPhase s_n_straight_phase = netInterface.createSignalPlanSignalPhase(
//                signalPlan, "南北直行", s_n_straight_phasecolor);
//
//        // 创建南北直行行人相位
//        ISignalPhase ns_ped_phase = netInterface.createSignalPlanSignalPhase(
//                signalPlan, "南北直行行人", s_n_straight_phasecolor);
//        System.out.println("s_n_straight_phase:" + s_n_straight_phase.id());
//        // 创建南北左转相位
//        ArrayList<ColorInterval> s_n_left_phasecolor = new ArrayList<>();
//        s_n_left_phasecolor.add(new ColorInterval("红", 119));
//        s_n_left_phasecolor.add(new ColorInterval("绿", 29));
//        s_n_left_phasecolor.add(new ColorInterval("黄", 3));
//        ISignalPhase s_n_left_phase = netInterface.createSignalPlanSignalPhase(
//                signalPlan, "南北左转", s_n_left_phasecolor);
//        System.out.println("s_n_left_phase:" + s_n_left_phase.id());
//        // 创建东西直行机动车信号灯
//        ArrayList<ISignalLamp> w_e_straight_lamps = new ArrayList<>();
//        for (ILane lane : w_approach.lanes()) {
//            if (lane.number() < w_approach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length() - 0.5);
//                w_e_straight_lamps.add(signalLamp);
//            }
//        }
//        for (ILane lane : e_approach.lanes()) {
//            if (lane.number() < e_approach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length() - 0.5);
//                w_e_straight_lamps.add(signalLamp);
//            }
//        }
//
//        // 创建东西左转机动车信号灯
//        ArrayList<ISignalLamp> w_e_left_lamps = new ArrayList<>();
//        for (ILane lane : w_approach.lanes()) {
//            if (lane.number() == w_approach.laneCount() - 1) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        w_e_left_phase, "东西左转信号灯", lane.id(), -1, lane.length() - 0.5);
//                w_e_left_lamps.add(signalLamp);
//            }
//        }
//        for (ILane lane : e_approach.lanes()) {
//            if (lane.number() == e_approach.laneCount() - 1) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        w_e_left_phase, "东西左转信号灯", lane.id(), -1, lane.length() - 0.5);
//                w_e_left_lamps.add(signalLamp);
//            }
//        }
//
//        // 创建南北直行机动车信号灯
//        ArrayList<ISignalLamp> n_s_straight_lamps = new ArrayList<>();
//        for (ILane lane : n_approach.lanes()) {
//            if (lane.number() < n_approach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        s_n_straight_phase, "南北直行信号灯", lane.id(), -1, lane.length() - 0.5);
//                n_s_straight_lamps.add(signalLamp);
//            }
//        }
//        for (ILane lane : s_approach.lanes()) {
//            if (lane.number() < s_approach.laneCount() - 1 && lane.number() > 0) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        s_n_straight_phase, "南北直行信号灯", lane.id(), -1, lane.length() - 0.5);
//                n_s_straight_lamps.add(signalLamp);
//            }
//        }
//
//        // 创建南北左转机动车信号灯
//        ArrayList<ISignalLamp> n_s_left_lamps = new ArrayList<>();
//        for (ILane lane : n_approach.lanes()) {
//            if (lane.number() == n_approach.laneCount() - 1) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        s_n_left_phase, "南北左转信号灯", lane.id(), -1, lane.length() - 0.5);
//                n_s_left_lamps.add(signalLamp);
//            }
//        }
//        for (ILane lane : s_approach.lanes()) {
//            if (lane.number() == s_approach.laneCount() - 1) {
//                ISignalLamp signalLamp = netInterface.createSignalLamp(
//                        s_n_left_phase, "南北左转信号灯", lane.id(), -1, lane.length() - 0.5);
//                n_s_left_lamps.add(signalLamp);
//            }
//        }
//        System.out.println("w_e_straight_phase:" + w_e_straight_phase.id());
//        // 创建行人信号灯并关联相位
//        // 南斑马线信号灯
//        ICrosswalkSignalLamp signalLamp1_positive = netInterface.createCrossWalkSignalLamp(
//                signalController, "南斑马线信号灯", s_crosswalk.getId(), new Point(m2p(0), m2p(0)), true);
//        ICrosswalkSignalLamp signalLamp1_negetive = netInterface.createCrossWalkSignalLamp(
//                signalController, "南斑马线信号灯", s_crosswalk.getId(), new Point(m2p(0), m2p(0)), false);
//        signalLamp1_positive.setSignalPhase(we_ped_phase);
//        signalLamp1_negetive.setSignalPhase(we_ped_phase);
//        System.out.println("we_ped_phase:" + we_ped_phase.id());
//        // 北斑马线信号灯
//        ICrosswalkSignalLamp signalLamp2_positive = netInterface.createCrossWalkSignalLamp(
//                signalController, "北斑马线信号灯", n_crosswalk.getId(), new Point(0, 0), true);
//        ICrosswalkSignalLamp signalLamp2_negetive = netInterface.createCrossWalkSignalLamp(
//                signalController, "北斑马线信号灯", n_crosswalk.getId(), new Point(0, 0), false);
//        signalLamp2_positive.setSignalPhase(we_ped_phase);
//        signalLamp2_negetive.setSignalPhase(we_ped_phase);
//
//        // 东斑马线信号灯
//        ICrosswalkSignalLamp signalLamp3_positive = netInterface.createCrossWalkSignalLamp(
//                signalController, "东斑马线信号灯", e_crosswalk.getId(), new Point(0, 0), true);
//        ICrosswalkSignalLamp signalLamp3_negetive = netInterface.createCrossWalkSignalLamp(
//                signalController, "东斑马线信号灯", e_crosswalk.getId(), new Point(0, 0), false);
//        signalLamp3_positive.setSignalPhase(ns_ped_phase);
//        signalLamp3_negetive.setSignalPhase(ns_ped_phase);
//
//        // 西斑马线信号灯
//        ICrosswalkSignalLamp signalLamp4_positive = netInterface.createCrossWalkSignalLamp(
//                signalController, "西斑马线信号灯", w_crosswalk.getId(), new Point(0, 0), true);
//        ICrosswalkSignalLamp signalLamp4_negetive = netInterface.createCrossWalkSignalLamp(
//                signalController, "西斑马线信号灯", w_crosswalk.getId(), new Point(0, 0), false);
//        signalLamp4_positive.setSignalPhase(ns_ped_phase);
//        signalLamp4_negetive.setSignalPhase(ns_ped_phase);}

//    public boolean ref_curvatureMinDist(int itemType, int itemId, ObjReal ref_minDist){
//        double v = ref_minDist.getValue();
//        ref_minDist.setValue(v * 2);
//        return true;
//    }


    //public IRoadWorkZone createRoadWorkZone(NetInterface netInterface, ILink upstreamLink) {
//    // 创建施工区参数对象
//    DynaRoadWorkZoneParam param = new DynaRoadWorkZoneParam();
//    // 设置施工区参数
//    param.setName("施工区");
//    param.setRoadId(upstreamLink.id());
//    param.setLocation(TESSNG.m2p(0));           // 位置
//    param.setLength(TESSNG.m2p(100));             // 长度
//    param.setUpCautionLength(TESSNG.m2p(70));     // 提前警示长度
//    param.setUpTransitionLength(TESSNG.m2p(50));  // 过渡区长度
//    param.setUpBufferLength(TESSNG.m2p(50));      // 缓冲区长度
//    param.setDownTransitionLength(TESSNG.m2p(40));// 下游过渡区长度
//    param.setDownTerminationLength(TESSNG.m2p(40));// 终止区长度
//    // 设置车道编号（注意：Java 中使用 List<Integer>）
//    ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
//    fromLaneNumbers.add(1);
//    param.setMlFromLaneNumber(fromLaneNumbers);  // 设置施工影响的车道编号
//    param.setStartTime(0);                       // 开始时间（秒）
//    param.setDuration(3600);                    // 持续时间（秒）
//    param.setLimitSpeed(TESSNG.m2p(42));         // 限速，转换为 TESS NG 内部单位
//    // 创建施工区
//    IRoadWorkZone workZone = netInterface.createRoadWorkZone(param, UnitOfMeasure.Metric); // Metric 单位制
//    if (workZone != null) {
//        System.out.println("施工区创建成功: " + workZone.name());
//    } else {
//        System.out.println("施工区创建失败");
//    }
//    return workZone;
//}
//public ITollDecisionPoint createTollDecisionPoint(
//        NetInterface netInterface,
//        int linkId,
//        double location,
//        String name) {
//
//    // 查找路段
//    ILink link = netInterface.findLink(linkId);
//    if (link == null) {
//        System.out.println("路段未找到，ID: " + linkId);
//        return null;
//    }
//
//    // 创建收费路径决策点
//    ITollDecisionPoint tollDecisionPoint = netInterface.createTollDecisionPoint(link, m2p(location), name);
//
//    if (tollDecisionPoint != null) {
//        System.out.println("收费路径决策点创建成功: " + tollDecisionPoint.name());
//    } else {
//        System.out.println("收费路径决策点创建失败");
//    }
//
//    return tollDecisionPoint;
//}
    public ITollRouting createTollRouting(
            NetInterface netInterface,
            ITollDecisionPoint tollDecisionPoint,
            ITollLane tollLane) {

        // 创建收费路径
        ITollRouting tollRouting = netInterface.createTollRouting(tollDecisionPoint, tollLane);
        if (tollRouting != null) {
            System.out.println("收费路径创建成功");
        } else {
            System.out.println("收费路径创建失败");
        }
        return tollRouting;
    }
    //    public void createJunctionNode(NetInterface netiface) {
//        // Step1: 创建节点
//        double x1 = TESSNG.m2p(-500);
//        double y1 = TESSNG.m2p(500);
//        double x2 = TESSNG.m2p(500);
//        double y2 = TESSNG.m2p(-500);
//        String junctionName = "newJunction";
//        netiface.createJunction(new Point(x1, y1), new Point(x2, y2), junctionName);
//
//        // Step2: 创建静态路径
//        netiface.buildAndApplyPaths(3);  // 设置每个 OD 最多搜索 3 条路径
//        netiface.reSetDeciPoint();      // 优化决策点位置
//
//        for (IDecisionPoint dp : netiface.decisionPoints()) {
//            for (IRouting routing : dp.routings()) {
//                netiface.reSetLaneConnector(routing);  // 优化路径中的车道连接
//            }
//        }
//
//        // Step3: 为节点中每个转向设置流量
//        // 由于没有已知值，这里针对不同转向类型为其赋流量初值
//        Map<String, Integer> turnVolumeReduct = new HashMap<>();
//        turnVolumeReduct.put("左转", 400);
//        turnVolumeReduct.put("直行", 1200);
//        turnVolumeReduct.put("右转", 200);
//        turnVolumeReduct.put("掉头", 0);
//
//        // Step3-1: 添加流量时间段
//        FlowTimeInterval timeInterval = netiface.addFlowTimeInterval();
//        long timeId = timeInterval.getTimeId();
//        int startTime = 0;
//        int endTime = 3600;
//        netiface.updateFlowTimeInterval(timeId, startTime, endTime);
//
//        // Step3-2: 遍历转向，为其设置流量
//        List<IJunction> junctions = netiface.getAllJunctions();
//        for (IJunction junction : junctions) {
//            int junctionId = junction.getId();
//            List<ITurningInfo> turningInfos = junction.getAllTurnningInfo();
//
//            for (ITurningInfo turning : turningInfos) {
//                int turningId = turning.getTurningId();
//                String turnType = turning.getStrTurnType();
//                int inputVolume = turnVolumeReduct.getOrDefault(turnType, 0);
//
//                // 为该转向设置输入流量
//                netiface.updateFlow(timeId, junctionId, turningId, inputVolume);
//            }
//        }
//
//        // Step4: 进行流量分配计算
//        // 设置 BPR 路阻函数参数，流量分配算法参数
//        double theta = 0.1;
//        double bpra = 0.15;
//        double bprb = 4;
//        int maxIterateNum = 300;
//        netiface.updateFlowAlgorithmParams(theta, bpra, bprb, maxIterateNum);  // 更新计算参数
//
//        // 计算路径流量分配并应用，返回分配结果
//        Map<Integer, List<FlowAllocationResult>> result = netiface.calculateFlows();
//
//        // 取流量分配结果
//        Map<String, List<Map<String, Object>>> resultJson = new HashMap<>();
//
//        for (Map.Entry<Integer, List<FlowAllocationResult>> entry : result.entrySet()) {
//            int timeIdResult = entry.getKey();
//            List<FlowAllocationResult> turningFlows = entry.getValue();
//
//            for (FlowAllocationResult i : turningFlows) {
//                int junction = i.getJunction().getId();
//                String turning = i.getTurningBaseInfo().getStrDirection() + "-" + i.getTurningBaseInfo().getStrTurnType();
//                double inputVolume = i.getInputFlowValue();        // 该转向输入流量
//                double realVolume = i.getRealFlow();               // 该转向实际分配到的流量
//                double relativeError = i.getRelativeError();        // 分配的相对误差
//                IFlowTimeInterval interval = i.getFlowTimeInterval();
//                int startTime = interval.getStartTime();
//                int endTimeResult = interval.getEndTime();
//
//                String timeRange = startTime + "-" + endTimeResult;
//                Map<String, Object> resultEntry = new HashMap<>();
//                resultEntry.put("junction", junction);
//                resultEntry.put("turning", turning);
//                resultEntry.put("inputVolume", inputVolume);
//                resultEntry.put("realVolume", realVolume);
//                resultEntry.put("relativeError", relativeError);
//
//                resultJson.computeIfAbsent(timeRange, k -> new ArrayList<>()).add(resultEntry);
//            }
//        }
//
//        System.out.println("result: " + new Gson().toJson(resultJson));
//    }
    public IDecisionPoint updateDecisionPointWithFlowRatio(
            NetInterface netInterface,
            IDecisionPoint decisionPoint,
            IRouting decisionRouting1,
            IRouting decisionRouting2,
            IRouting decisionRouting3) {

        // 分配左、直、右流量比
        ArrayList<_RoutingFLowRatio> flowRatios = new ArrayList<>();

        _RoutingFLowRatio flowRatioLeft = new _RoutingFLowRatio();
        flowRatioLeft.setRoutingFLowRatioID(1);
        flowRatioLeft.setRoutingID(decisionRouting1.id());
        flowRatioLeft.setStartDateTime(0);
        flowRatioLeft.setEndDateTime(999999);
        flowRatioLeft.setRatio(2.0);
        flowRatios.add(flowRatioLeft);

        _RoutingFLowRatio flowRatioStraight = new _RoutingFLowRatio();
        flowRatioStraight.setRoutingFLowRatioID(2);
        flowRatioStraight.setRoutingID(decisionRouting2.id());
        flowRatioStraight.setStartDateTime(0);
        flowRatioStraight.setEndDateTime(999999);
        flowRatioStraight.setRatio(3.0);
        flowRatios.add(flowRatioStraight);

        _RoutingFLowRatio flowRatioRight = new _RoutingFLowRatio();
        flowRatioRight.setRoutingFLowRatioID(3);
        flowRatioRight.setRoutingID(decisionRouting3.id());
        flowRatioRight.setStartDateTime(0);
        flowRatioRight.setEndDateTime(999999);
        flowRatioRight.setRatio(1.0);
        flowRatios.add(flowRatioRight);

        // 构建决策点数据
        _DecisionPoint decisionPointData = new _DecisionPoint();
        decisionPointData.setDeciPointID(decisionPoint.id());
        decisionPointData.setDeciPointName(decisionPoint.name());

        // 获取决策点坐标
        Point decisionPointPos = new Point();
        boolean gotPoint = decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos);

        if (gotPoint) {
            decisionPointData.setX(decisionPointPos.getX());
            decisionPointData.setY(decisionPointPos.getY());
            decisionPointData.setZ(decisionPoint.link().z());
        } else {
            System.out.println("无法获取决策点坐标");
            return null;
        }

        // 更新决策点及其路径流量比
        IDecisionPoint updatedDecisionPoint = netInterface.updateDecipointPoint(decisionPointData, flowRatios);

        if (updatedDecisionPoint != null) {
            System.out.println("决策点更新成功: " + updatedDecisionPoint.name());
        } else {
            System.out.println("决策点更新失败");
        }

        return updatedDecisionPoint;
    }
    public void createAndManageBusLine(NetInterface netiface, ILink w_approach, ILink e_outgoing, ILink s_outgoing) {
        // 创建公交线路
        ArrayList<ILink> busLineLinks1 = new ArrayList<>();
        busLineLinks1.add(w_approach);
        busLineLinks1.add(e_outgoing);
        IBusLine busline = netiface.createBusLine(busLineLinks1);

        ArrayList<ILink> busLineLinks2 = new ArrayList<>();
        busLineLinks2.add(w_approach);
        busLineLinks2.add(s_outgoing);
        IBusLine busline1 = netiface.createBusLine(busLineLinks2);

        // 删除公交线路 busline1
        netiface.removeBusLine(busline1);

        // 设置公交线路期望速度
        if (busline != null) {
            busline.setDesirSpeed(TESSNG.m2p(60));
        }

        // 创建公交站点
        ILane lane_w_approach = w_approach.lanes().get(0);
        IBusStation busstation1 = netiface.createBusStation(lane_w_approach, TESSNG.m2p(30), TESSNG.m2p(100), "西进口公交站点1", UnitOfMeasure.Metric);
        IBusStation busstation2 = netiface.createBusStation(lane_w_approach, TESSNG.m2p(30), TESSNG.m2p(200), "西进口公交站点2", UnitOfMeasure.Metric);

        ILane lane_e_outgoing = e_outgoing.lanes().get(0);
        IBusStation busstation3 = netiface.createBusStation(lane_e_outgoing, TESSNG.m2p(30), TESSNG.m2p(200), "东出口公交站点1", UnitOfMeasure.Metric);

        // 将公交站点关联到公交线路上
        if (busline != null && busstation1 != null) {
            netiface.addBusStationToLine(busline, busstation1);
        }
        if (busline != null && busstation2 != null) {
            netiface.addBusStationToLine(busline, busstation2);
        }
        if (busline != null && busstation3 != null) {
            netiface.addBusStationToLine(busline, busstation3);
        }

        // 删除公交线路中的一个站点
        if (busline != null && busstation2 != null) {
            netiface.removeBusStationFromLine(busline, busstation2);
        }
    }

}
