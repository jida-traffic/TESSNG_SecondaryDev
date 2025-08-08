package Fundamental_Functions.basic_example;

import java.util.ArrayList;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.GraphicsItemPropName;
import com.jidatraffic.tessng.NetItemType;

import static com.jidatraffic.tessng.TESSNG.m2p;

public class MyNet extends JCustomerNet {
    // TESS NG接口
    private TessInterface iface;
    private NetInterface netiface;
    private SimuInterface simuiface;

    /**
     * 构造方法
     */
    public MyNet() {
        super();
    }

    /**
     * 自定义方法：创建路网上的路段和连接段
     */
    public void createNetwork() {
        // 初始化TESS NG接口
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();
        // 创建第一条路段：曹安公路
        Point startPoint1 = new Point(m2p(-300), 0);
        Point endPoint1 = new Point(m2p(300), 0);
        ArrayList<Point> linkPoints1 = new ArrayList<>();
        linkPoints1.add(startPoint1);
        linkPoints1.add(endPoint1);
        ILink link1 = netiface.createLink(linkPoints1, 7, "曹安公路");

        if (link1 != null) {
            // 打印该路段所有车道ID
            ArrayList<ILane> lanes1 = link1.lanes();
            System.out.print("111");
            System.out.print("曹安公路车道ID列表：");
            for (ILane lane : lanes1) {
                System.out.print(lane.id() + " ");
            }
            System.out.println();

            // 创建发车点
            IDispatchPoint dp1 = netiface.createDispatchPoint(link1);
            if (dp1 != null) {
                // 设置发车间隔：车型1，间隔2，发车数28
                dp1.addDispatchInterval(1, 2, 28);
            }
        }

        // 创建第二条路段：次干道
        Point startPoint2 = new Point(m2p(-300), m2p(-25));
        Point endPoint2 = new Point(m2p(300), m2p(-25));
        ArrayList<Point> linkPoints2 = new ArrayList<>();
        linkPoints2.add(startPoint2);
        linkPoints2.add(endPoint2);
        ILink link2 = netiface.createLink(linkPoints2, 7, "次干道");

        if (link2 != null) {
            // 创建发车点
            IDispatchPoint dp2 = netiface.createDispatchPoint(link2);
            if (dp2 != null) {
                dp2.addDispatchInterval(1, 3600, 3600);
            }

            // 将外侧车道设为公交专用道
            ArrayList<ILane> lanes2 = link2.lanes();
            if (!lanes2.isEmpty()) {
                ILane lane = lanes2.get(0);
                lane.setLaneType("公交专用道");
            }
        }

        // 创建第三条路段
        Point startPoint3 = new Point(m2p(-300), m2p(25));
        Point endPoint3 = new Point(m2p(-150), m2p(25));
        ArrayList<Point> linkPoints3 = new ArrayList<>();
        linkPoints3.add(startPoint3);
        linkPoints3.add(endPoint3);
        ILink link3 = netiface.createLink(linkPoints3, 3);

        if (link3 != null) {
            IDispatchPoint dp3 = netiface.createDispatchPoint(link3);
            if (dp3 != null) {
                dp3.addDispatchInterval(1, 3600, 3600);
            }
        }

        // 创建第四条路段
        Point startPoint4 = new Point(m2p(-50), m2p(25));
        Point endPoint4 = new Point(m2p(50), m2p(25));
        ArrayList<Point> linkPoints4 = new ArrayList<>();
        linkPoints4.add(startPoint4);
        linkPoints4.add(endPoint4);
        ILink link4 = netiface.createLink(linkPoints4, 3);

        // 创建第五条路段：自定义限速路段
        Point startPoint5 = new Point(m2p(150), m2p(25));
        Point endPoint5 = new Point(m2p(300), m2p(25));
        ArrayList<Point> linkPoints5 = new ArrayList<>();
        linkPoints5.add(startPoint5);
        linkPoints5.add(endPoint5);
        ILink link5 = netiface.createLink(linkPoints5, 3, "自定义限速路段");

        if (link5 != null) {
            // 设置路段限速30km/h
            link5.setLimitSpeed(30);
        }

        // 创建第六条路段：动态发车路段
        Point startPoint6 = new Point(m2p(-300), m2p(50));
        Point endPoint6 = new Point(m2p(300), m2p(50));
        ArrayList<Point> linkPoints6 = new ArrayList<>();
        linkPoints6.add(startPoint6);
        linkPoints6.add(endPoint6);
        ILink link6 = netiface.createLink(linkPoints6, 3, "动态发车路段");

        if (link6 != null) {
            link6.setLimitSpeed(80);
        }

        // 创建第七条路段
        Point startPoint7 = new Point(m2p(-300), m2p(75));
        Point endPoint7 = new Point(m2p(-250), m2p(75));
        ArrayList<Point> linkPoints7 = new ArrayList<>();
        linkPoints7.add(startPoint7);
        linkPoints7.add(endPoint7);
        ILink link7 = netiface.createLink(linkPoints7, 3);

        if (link7 != null) {
            link7.setLimitSpeed(80);
        }

        // 创建第八条路段
        Point startPoint8 = new Point(m2p(-50), m2p(75));
        Point endPoint8 = new Point(m2p(300), m2p(75));
        ArrayList<Point> linkPoints8 = new ArrayList<>();
        linkPoints8.add(startPoint8);
        linkPoints8.add(endPoint8);
        ILink link8 = netiface.createLink(linkPoints8, 3);

        if (link8 != null) {
            link8.setLimitSpeed(80);
        }

        // 创建第一条连接段：连接link3和link4
        if (link3 != null && link4 != null) {
            ArrayList<Integer> fromLaneNumbers1 = new ArrayList<>();
            fromLaneNumbers1.add(1);
            fromLaneNumbers1.add(2);
            fromLaneNumbers1.add(3);

            ArrayList<Integer> toLaneNumbers1 = new ArrayList<>();
            toLaneNumbers1.add(1);
            toLaneNumbers1.add(2);
            toLaneNumbers1.add(3);

            netiface.createConnector(link3.id(), link4.id(), fromLaneNumbers1, toLaneNumbers1, "连接段1", true);
        }

        // 创建第二条连接段：连接link4和link5
        if (link4 != null && link5 != null) {
            ArrayList<Integer> fromLaneNumbers2 = new ArrayList<>();
            fromLaneNumbers2.add(1);
            fromLaneNumbers2.add(2);
            fromLaneNumbers2.add(3);

            ArrayList<Integer> toLaneNumbers2 = new ArrayList<>();
            toLaneNumbers2.add(1);
            toLaneNumbers2.add(2);
            toLaneNumbers2.add(3);

            netiface.createConnector(link4.id(), link5.id(), fromLaneNumbers2, toLaneNumbers2, "连接段2", true);
        }

        // 创建第三条连接段：连接link7和link8
        if (link7 != null && link8 != null) {
            ArrayList<Integer> fromLaneNumbers3 = new ArrayList<>();
            fromLaneNumbers3.add(1);
            fromLaneNumbers3.add(2);
            fromLaneNumbers3.add(3);

            ArrayList<Integer> toLaneNumbers3 = new ArrayList<>();
            toLaneNumbers3.add(1);
            toLaneNumbers3.add(2);
            toLaneNumbers3.add(3);

            netiface.createConnector(link7.id(), link8.id(), fromLaneNumbers3, toLaneNumbers3, "动态发车连接段", true);
        }
    }

    /**
     * 重写父类方法：加载路网后调用
     */
    @Override
    public void afterLoadNet() {
        // 初始化TESS NG接口
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();
        // 获取路段数量
        int linkCount = netiface.linkCount();

        // 如果没有路段，则创建自定义路网
        if (linkCount == 0) {
            createNetwork();
        }

        if (netiface.linkCount() > 0) {
            // 获取所有路段
            ArrayList<ILink> links = netiface.links();

            // 获取ID为1的路段
            ILink link = netiface.findLink(1);
            if (link != null) {
                // 获取路段中心线断点集
                ArrayList<Point> linkPoints = link.centerBreakPoints();

                // 获取路段的车道列表
                ArrayList<ILane> lanes = link.lanes();
                if (lanes != null && !lanes.isEmpty()) {
                    // 获取第一条车道的中心线断点
                    ArrayList<Point> lanePoints = lanes.get(0).centerBreakPoints();
                }
            }

            // 获取所有连接段
            ArrayList<IConnector> connectors = netiface.connectors();
            if (!connectors.isEmpty()) {
                // 获取第一条连接段的所有车道连接
                ArrayList<ILaneConnector> laneConnectors = connectors.get(0).laneConnectors();
                if (!laneConnectors.isEmpty()) {
                    // 获取第一条车道连接
                    ILaneConnector laneConnector = laneConnectors.get(0);
                    // 获取车道连接的断点集
                    ArrayList<Point> laneConnectorPoints = laneConnector.centerBreakPoints();
                }
            }
        }
    }

    /**
     * 重写父类方法：是否允许用户干预路网元素绘制
     */
    @Override
    public boolean isPermitForCustDraw() {
        // 初始化TESS NG接口
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();
        // 当路网文件名包含"Temp"时允许自定义绘制
        String netFileName = netiface.netFilePath();
        return netFileName.contains("Temp");
    }

    /**
     * 重写父类方法：确定路网元素标签的绘制方式
     */
    @Override
    public void ref_labelNameAndFont(int itemType, long itemId, ObjInt ref_outPropName, ObjReal ref_outFontSize) {
        // 初始化TESS NG接口
        iface = TESSNG.tessngIFace();
        netiface = iface.netInterface();
        simuiface = iface.simuInterface();

        // 如果仿真正在进行，设置ref_outPropName.value等于GraphicsItemPropName.None，
        // 路段和车道都不绘制标签
        if (simuiface.isRunning()) {
            ref_outPropName.setValue(GraphicsItemPropName.None.swigValue());
            return;
        }

        // 默认绘制ID
        ref_outPropName.setValue(GraphicsItemPropName.Id.swigValue());
        // 标签大小为6米
        ref_outFontSize.setValue(6);

        // 如果是连接段一律绘制名称
        if (itemType == NetItemType.getGConnectorType()) {
            ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
        } else if (itemType == NetItemType.getGLinkType()) {
            if (itemId == 1 || itemId == 5 || itemId == 6) {
                ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
            }
        }
    }

    /**
     * 重写父类方法：是否绘制车道中心线
     */
    @Override
    public boolean isDrawLaneCenterLine(long laneId) {
        return true; // 始终绘制车道中心线
    }

    /**
     * 重写父类方法：是否绘制路段中心线
     */
    @Override
    public boolean isDrawLinkCenterLine(long linkId) {
        return linkId != 1; // ID为1的路段不绘制中心线
    }

}
