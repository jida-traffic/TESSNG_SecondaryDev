package Intelligent_Connected_and_Autonomous_Driving.Autonomous_Driving_Simulation;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;
import java.util.List;

import static com.jidatraffic.tessng.TESSNG.m2p;

public class MyNet extends JCustomerNet {

    public MyNet() {
        super();
    }

    /**
     * 创建自定义路网
     */
    public void createNet() {
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        // 获取路网子接口
        NetInterface netiface = iface.netInterface();

        // 定义路段起点和终点（转换为仿真单位）
        Point startPoint = new Point(m2p(-300), 0);
        Point endPoint = new Point(m2p(300), 0);
        ArrayList<Point> lPoint = new ArrayList<>();
        lPoint.add(startPoint);
        lPoint.add(endPoint);
        // 创建路段：包含两个端点、2条车道、名称为"公路1"
        ILink link1 = netiface.createLink(lPoint, 2, "公路1");

        if (link1 != null) {
            // 获取该路段的所有车道
            List<ILane> lanes = link1.lanes();
            // 打印车道ID列表
            System.out.print("公路1车道ID列表：");
            for (ILane lane : lanes) {
                System.out.print(lane.id() + " ");
            }
            System.out.println();

            // 在该路段创建发车点
            IDispatchPoint dp = netiface.createDispatchPoint(link1);
            if (dp != null) {
                // 设置发车间隔参数（车型组成、时间间隔、发车数）
                dp.addDispatchInterval(1, 14, 9);
            }
        }
    }

    /**
     * 路网加载后调用的方法
     * 若路网为空则创建路网，否则初始化并启动仿真
     */
    @Override
    public void afterLoadNet() {
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netiface = iface.netInterface();

        // 检查路段数量
        int count = netiface.linkCount();
        if (count == 0) {
            // 若没有路段则创建路网
            createNet();
        }

        // 再次检查路段数量，若大于0则初始化并启动仿真
        if (netiface.linkCount() > 0) {
            // 获取所有路段
            List<ILink> allLinks = netiface.links();

            // 获取ID为1的路段
            ILink link = netiface.findLink(1);
            if (link != null) {
                // 打印路段中心线断点
                List<Point> linkPoints = link.centerBreakPoints();
                System.out.print("一条路段中心线断点：");
                for (Point p : linkPoints) {
                    System.out.print("(" + p.getX() + ", " + p.getY() + ") ");
                }
                System.out.println();

                // 获取路段的所有车道
                List<ILane> lanes = link.lanes();
                if (lanes != null && !lanes.isEmpty()) {
                    // 打印第一条车道的中心线断点
                    List<Point> lanePoints = lanes.get(0).centerBreakPoints();
                    System.out.print("一条车道中心线断点：");
                    for (Point p : lanePoints) {
                        System.out.print("(" + p.getX() + ", " + p.getY() + ") ");
                    }
                    System.out.println();
                }
            }

            // 获取所有连接段
            List<IConnector> connectors = netiface.connectors();
            if (connectors != null && !connectors.isEmpty()) {
                // 获取第一条连接段的所有车道连接
                List<ILaneConnector> laneConnectors = connectors.get(0).laneConnectors();
                if (!laneConnectors.isEmpty()) {
                    // 获取第一条车道连接
                    ILaneConnector laneConnector = laneConnectors.get(0);
                    // 打印车道连接的中心线断点
                    List<Point> connectorPoints = laneConnector.centerBreakPoints();
                    System.out.print("一条'车道连接'中心线断点：");
                    for (Point p : connectorPoints) {
                        System.out.print("(" + p.getX() + ", " + p.getY() + ") ");
                    }
                    System.out.println();
                }
            }

            // 启动仿真
            iface.simuInterface().startSimu();
        }
    }

    /**
     * 控制是否绘制车道中心线
     */
    @Override
    public boolean isDrawLaneCenterLine(long laneId) {
        // 始终绘制车道中心线
        return true;
    }

    /**
     * 控制是否绘制路段中心线
     */
    @Override
    public boolean isDrawLinkCenterLine(long linkId) {
        // ID为1的路段不绘制中心线，其他路段绘制
        return linkId != 1;
    }
}