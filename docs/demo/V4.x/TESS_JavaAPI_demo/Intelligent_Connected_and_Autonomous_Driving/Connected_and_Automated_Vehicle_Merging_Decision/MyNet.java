package TESS_Java_APIDemo.Intelligent_Connected_and_Autonomous_Driving.Connected_and_Automated_Vehicle_Merging_Decision;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.NetItemType;
import com.jidatraffic.tessng.GraphicsItemPropName;
import java.util.ArrayList;
import java.util.List;

import static com.jidatraffic.tessng.TESSNG.m2p;


public class MyNet extends JCustomerNet {

    public MyNet() {
        super();
    }

    /**
     * 创建路网
     */
    public void createNet() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        // 获取路网子接口
        NetInterface netiface = iface.netInterface();

        // 第一条路段 - 曹安公路
        Point startPoint = new Point(m2p(-300), 0);
        Point endPoint = new Point(m2p(300), 0);
        ArrayList<Point> lPoint = new ArrayList<>();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link1 = netiface.createLink(lPoint, 7, "曹安公路");

        if (link1 != null) {
            // 打印该路段所有车道ID列表
            List<ILane> lanes = link1.lanes();
            System.out.print("曹安公路车道ID列表：");
            for (ILane lane : lanes) {
                System.out.print(lane.id() + " ");
            }
            System.out.println();

            // 创建发车点
            IDispatchPoint dp = netiface.createDispatchPoint(link1);
            if (dp != null) {
                // 设置发车间隔，含车型组成、时间间隔、发车数
                dp.addDispatchInterval(1, 2, 28);
            }
        }

        // 第二条路段 - 次干道
        startPoint = new Point(m2p(-300), m2p(-25));
        endPoint = new Point(m2p(300), m2p(-25));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link2 = netiface.createLink(lPoint, 7, "次干道");

        if (link2 != null) {
            IDispatchPoint dp = netiface.createDispatchPoint(link2);
            if (dp != null) {
                dp.addDispatchInterval(1, 3600, 3600);
            }
        }

        // 第三条路段
        startPoint = new Point(m2p(-300), m2p(25));
        endPoint = new Point(m2p(-150), m2p(25));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link3 = netiface.createLink(lPoint, 3);

        if (link3 != null) {
            IDispatchPoint dp = netiface.createDispatchPoint(link3);
            if (dp != null) {
                dp.addDispatchInterval(1, 3600, 3600);
            }
        }

        // 第四条路段
        startPoint = new Point(m2p(-50), m2p(25));
        endPoint = new Point(m2p(50), m2p(25));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link4 = netiface.createLink(lPoint, 3);

        // 第五条路段 - 自定义限速路段
        startPoint = new Point(m2p(150), m2p(25));
        endPoint = new Point(m2p(300), m2p(25));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link5 = netiface.createLink(lPoint, 3, "自定义限速路段");

        if (link5 != null) {
            link5.setLimitSpeed(30);
        }

        // 第六条路段 - 动态发车路段
        startPoint = new Point(m2p(-300), m2p(50));
        endPoint = new Point(m2p(300), m2p(50));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link6 = netiface.createLink(lPoint, 3, "动态发车路段");

        if (link6 != null) {
            link6.setLimitSpeed(80);
        }

        // 第七条路段
        startPoint = new Point(m2p(-300), m2p(75));
        endPoint = new Point(m2p(-250), m2p(75));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link7 = netiface.createLink(lPoint, 3);

        if (link7 != null) {
            link7.setLimitSpeed(80);
        }

        // 第八条路段
        startPoint = new Point(m2p(-50), m2p(75));
        endPoint = new Point(m2p(300), m2p(75));
        lPoint.clear();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        ILink link8 = netiface.createLink(lPoint, 3);

        if (link8 != null) {
            link8.setLimitSpeed(80);
        }

        // 创建第一条连接段
        if (link3 != null && link4 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                lFromLaneNumber.add(i);
                lToLaneNumber.add(i);
            }
            netiface.createConnector(link3.id(), link4.id(), lFromLaneNumber, lToLaneNumber, "连接段1", true);
        }

        // 创建第二条连接段
        if (link4 != null && link5 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                lFromLaneNumber.add(i);
                lToLaneNumber.add(i);
            }
            netiface.createConnector(link4.id(), link5.id(), lFromLaneNumber, lToLaneNumber, "连接段2", true);
        }

        // 创建第三条连接段
        if (link7 != null && link8 != null) {
            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                lFromLaneNumber.add(i);
                lToLaneNumber.add(i);
            }
            netiface.createConnector(link7.id(), link8.id(), lFromLaneNumber, lToLaneNumber, "动态发车连接段", true);

            // 创建路径
            ArrayList<ILink> routingLinks = new ArrayList<>();
            routingLinks.add(link7);
            routingLinks.add(link8);
            IRouting routing = netiface.createRouting(routingLinks);
            System.out.println(routing);
        }
    }

    /**
     * 过载的父类方法，当打开路网后TESS NG调用此方法
     * 实现的逻辑是：路网加载后获取路段数，如果路网数为0则调用方法createNet构建路网，之后再次获取路段数，如果大于0则启动仿真
     */
    @Override
    public void afterLoadNet() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        // 获取路网子接口
        NetInterface netiface = iface.netInterface();
        // 获取路段数
        int count = netiface.linkCount();

        if (count == 0) {
            createNet();
        }

        if (netiface.linkCount() > 0) {
            iface.simuInterface().startSimu();
        }
    }

    /**
     * 是否允许用户对路网元素的绘制进行干预
     * 如选择路段标签类型、确定绘制颜色等，本方法目的在于减少不必要的对python方法调用频次
     */
    @Override
    public boolean isPermitForCustDraw() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netface = iface.netInterface();
        String netFileName = netface.netFilePath();

        return netFileName.contains("Temp");
    }


    /**
     * 过载父类方法，是否绘制车道中心线
     */
    @Override
    public boolean isDrawLaneCenterLine(long laneId) {
        return true;
    }

    /**
     * 过载父类方法，是否绘制路段中心线
     */
    @Override
    public boolean isDrawLinkCenterLine(long linkId) {
        // 路段ID为1时不绘制中心线，其他路段绘制
        return linkId != 1;
    }
}
