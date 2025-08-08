package test002;

import com.jidatraffic.tessng.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestNet2 extends JCustomerNet {
    public TestNet2(){
        super();
    }

    @Override
    public void afterLoadNet(){
        //代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        //代表TESS NG的路网子接口
        NetInterface netiface = iface.netInterface();
        test_IRoadNet();
        createNet();

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
//        JsonObject jsonObject = iRoadNet.otherAttrs();
//        System.out.println(" 其它属性json:"+jsonObject);
        Point point = iRoadNet.centerPoint();
        System.out.println("中心点:"+point);
        System.out.println("--------test_IRoadNet 结束-----------");
    }

    private void createNet(){
        //代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        //代表TESS NG的路网子接口
        NetInterface netiface = iface.netInterface();


        // 第一条路段
        Point startPoint = new Point(TESSNG.m2p(-300), 0);
        Point endPoint = new Point(TESSNG.m2p(300), 0);
        ArrayList<Point> lPoint = new ArrayList<>();
        lPoint.add(startPoint);
        lPoint.add(endPoint);

        ILink link1 = netiface.createLink(lPoint, 7, "曹安公路");
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
                dp.addDispatchInterval(1, 2, 28);
            }
        }

        // 第二条路段
        startPoint = new Point(TESSNG.m2p(-300), TESSNG.m2p(-25));
        endPoint = new Point(TESSNG.m2p(300), TESSNG.m2p(-25));
        ArrayList<Point> lPoint2 = new ArrayList<>();
        lPoint2.add(startPoint);
        lPoint2.add(endPoint);

        ILink link2 = netiface.createLink(lPoint2, 7, "次干道");
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

        // 第三条路段
        Point startPoint3 = new Point(TESSNG.m2p(-300), TESSNG.m2p(25));
        Point endPoint3 = new Point(TESSNG.m2p(-150), TESSNG.m2p(25));
        ArrayList<Point> lPoint3 = new ArrayList<>();
        lPoint3.add(startPoint3);
        lPoint3.add(endPoint3);

        ILink link3 = netiface.createLink(lPoint3, 3);
        if (link3 != null) {
            IDispatchPoint dp3 = netiface.createDispatchPoint(link3);
            if (dp3 != null) {
                dp3.addDispatchInterval(1, 3600, 3600);
            }
        }

        // 创建第四条路段
        Point startPoint4 = new Point(TESSNG.m2p(-50), TESSNG.m2p(25));
        Point endPoint4 = new Point(TESSNG.m2p(50), TESSNG.m2p(25));
        ArrayList<Point> lPoint4 = new ArrayList<>();
        lPoint4.add(startPoint4);
        lPoint4.add(endPoint4);

        ILink link4 = netiface.createLink(lPoint4, 3);

        // 创建第五条路段
        Point startPoint5 = new Point(TESSNG.m2p(150), TESSNG.m2p(25));
        Point endPoint5 = new Point(TESSNG.m2p(300), TESSNG.m2p(25));
        ArrayList<Point> lPoint5 = new ArrayList<>();
        lPoint5.add(startPoint5);
        lPoint5.add(endPoint5);

        ILink link5 = netiface.createLink(lPoint5, 3, "自定义限速路段");
        if (link5 != null) {
            link5.setLimitSpeed(30);
        }

        // 创建第六条路段
        Point startPoint6 = new Point(TESSNG.m2p(-300), TESSNG.m2p(50));
        Point endPoint6 = new Point(TESSNG.m2p(300), TESSNG.m2p(50));
        ArrayList<Point> lPoint6 = new ArrayList<>();
        lPoint6.add(startPoint6);
        lPoint6.add(endPoint6);

        ILink link6 = netiface.createLink(lPoint6, 3, "动态发车路段");
        if (link6 != null) {
            link6.setLimitSpeed(80);
        }

        // 创建第七条路段
        Point startPoint7 = new Point(TESSNG.m2p(-300), TESSNG.m2p(75));
        Point endPoint7 = new Point(TESSNG.m2p(-250), TESSNG.m2p(75));
        ArrayList<Point> lPoint7 = new ArrayList<>();
        lPoint7.add(startPoint7);
        lPoint7.add(endPoint7);

        ILink link7 = netiface.createLink(lPoint7, 3);
        if (link7 != null) {
            link7.setLimitSpeed(80);
        }

        // 创建第八条路段
        Point startPoint8 = new Point(TESSNG.m2p(-50), TESSNG.m2p(75));
        Point endPoint8 = new Point(TESSNG.m2p(300), TESSNG.m2p(75));
        ArrayList<Point> lPoint8 = new ArrayList<>();
        lPoint8.add(startPoint8);
        lPoint8.add(endPoint8);

        ILink link8 = netiface.createLink(lPoint8, 3);
        if (link8 != null) {
            link8.setLimitSpeed(80);
        }

        // 创建第一条连接段
        if (link3 != null && link4 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
            lFromLaneNumber.add(Integer.valueOf(1));
            lFromLaneNumber.add(Integer.valueOf(2));
            lFromLaneNumber.add(Integer.valueOf(3));
            ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
            lToLaneNumber.add(Integer.valueOf(1));
            lToLaneNumber.add(Integer.valueOf(2));
            lToLaneNumber.add(Integer.valueOf(3));
            IConnector conn1 = netiface.createConnector(link3.id(), link4.id(), lFromLaneNumber, lToLaneNumber, "连接段1", true);
        }

        // 创建第二条连接段
        if (link4 != null && link5 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
            lFromLaneNumber.add(Integer.valueOf(1));
            lFromLaneNumber.add(Integer.valueOf(2));
            lFromLaneNumber.add(Integer.valueOf(3));
            ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
            lToLaneNumber.add(Integer.valueOf(1));
            lToLaneNumber.add(Integer.valueOf(2));
            lToLaneNumber.add(Integer.valueOf(3));
            IConnector conn2 = netiface.createConnector(link4.id(), link5.id(), lFromLaneNumber, lToLaneNumber, "连接段2", true);
        }

        // 创建第三条连接段
        if (link7 != null && link8 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<Integer>();
            lFromLaneNumber.add(Integer.valueOf(1));
            lFromLaneNumber.add(Integer.valueOf(2));
            lFromLaneNumber.add(Integer.valueOf(3));
            ArrayList<Integer> lToLaneNumber = new ArrayList<Integer>();
            lToLaneNumber.add(Integer.valueOf(1));
            lToLaneNumber.add(Integer.valueOf(2));
            lToLaneNumber.add(Integer.valueOf(3));
            IConnector conn3 = netiface.createConnector(link7.id(), link8.id(), lFromLaneNumber, lToLaneNumber, "动态发车连接段", true);
        }
        ArrayList<ILink> routingLinks = new ArrayList<ILink>();
        routingLinks.add(link7);
        routingLinks.add(link8);
        IRouting routing = netiface.createRouting(routingLinks);


        ArrayList<Point> centerPoints = link1.centerBreakPoints();
        for(int i = 0, size = centerPoints.size(); i < size; ++i){
            Point p = centerPoints.get(i);
            System.out.println("p" + i + ".x" + p.getX());
            System.out.println("p" + i + ".y" + p.getY());
        }
        System.out.println("link id:" + link1.id());
        System.out.println("link name:" + link1.name());
        System.out.println("link length:" + TESSNG.p2m(link1.length()));
    }

//    @Override
//    public boolean ref_curvatureMinDist(int itemType, int itemId, ObjReal ref_minDist){
//        double v = ref_minDist.getValue();
//        ref_minDist.setValue(v * 2);
//        return true;
//    }
}
