package test002.TESS_JavaAPI_demo.four.VariableLaneManagement;

import com.jidatraffic.tessng.*;
import io.qt.core.QPointF;

import java.util.ArrayList;
import java.util.List;

public class SecondaryDevCases {
    private int id;

    public SecondaryDevCases(int id) {
        this.id = id;
    }

    /**
     * 优化交通渠化（修改交叉口进口道属性）
     */
    public void optTrafficCanalization( ) {
        // 获取TESS NG顶层接口

        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        // 获取路网接口
        NetInterface netInterface = iface.netInterface();

        // 查找关键连接段（东进口、西出口、北出口）
        ILink link1 = netInterface.findLink(1);  // 东进口link
        ILink link2 = netInterface.findLink(2);  // 西出口link
        ILink link7 = netInterface.findLink(7);  // 北出口link

        // 步骤1：创建新的连接段（lane connector）
        if (link1 != null && link2 != null) {
            // 定义车道连接关系（从车道 -> 到车道）
            ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
            fromLaneNumbers.add(3);
            fromLaneNumbers.add(4);

            ArrayList<Integer> toLaneNumbers = new ArrayList<>();
            toLaneNumbers.add(3);
            toLaneNumbers.add(4);

            // 移除旧的左转连接器
            IConnector oldLeftConnector = netInterface.findConnector(3);
            if (oldLeftConnector != null) {
                netInterface.removeConnector(oldLeftConnector);
            }

            // 创建新的左转连接器
            IConnector newConnLeft = netInterface.createConnector(
                    link1.id(),
                    link7.id(),
                    fromLaneNumbers,
                    toLaneNumbers,
                    "优化后左转连接器",
                    true
            );
            System.out.println("新左转连接器: " + newConnLeft);

            // 步骤2：修改决策点和路径（保证路径与渠化一致）
            IDecisionPoint decisionPoint = netInterface.findDecisionPoint(1);
            if (decisionPoint == null) {
                System.out.println("未找到决策点ID=1");
                return;
            }

            // 移除旧的左转路径
            IRouting decisionRoutingLeft = netInterface.findRouting(3);
            if (decisionRoutingLeft != null) {
                netInterface.removeDeciRouting(decisionPoint, decisionRoutingLeft);
            }

            // 创建新的左转路径（经过link1 -> link7）
            ArrayList<ILink> newRoutingLinks = new ArrayList<>();
            newRoutingLinks.add(link1);
            newRoutingLinks.add(link7);
            IRouting decisionRoutingLeftNew = netInterface.createDeciRouting(decisionPoint, newRoutingLinks);
            if (decisionRoutingLeftNew == null) {
                System.out.println("创建新左转路径失败");
                return;
            }

            // 获取直行和右转路径
            IRouting decisionRoutingRight = netInterface.findRouting(1);
            IRouting decisionRoutingStraight = netInterface.findRouting(2);

            // 定义左、直、右流量比
            _RoutingFLowRatio flowRatioLeft = new _RoutingFLowRatio();
            flowRatioLeft.setRoutingFLowRatioID(3);
            flowRatioLeft.setRoutingID(decisionRoutingLeftNew.id());
            flowRatioLeft.setStartDateTime(0);
            flowRatioLeft.setEndDateTime(999999);
            flowRatioLeft.setRatio(2.0);


            // 直行流量比设置
            _RoutingFLowRatio flowRatioStraight = new _RoutingFLowRatio();
            flowRatioStraight.setRoutingFLowRatioID(2);   // 设置ID
            flowRatioStraight.setRoutingID(decisionRoutingStraight.id());  // 关联直行路径
            flowRatioStraight.setStartDateTime(0);       // 起始时间
            flowRatioStraight.setEndDateTime(999999);    // 结束时间
            flowRatioStraight.setRatio(3.0);             // 流量比

            // 右转流量比设置
            _RoutingFLowRatio flowRatioRight = new _RoutingFLowRatio();
            flowRatioRight.setRoutingFLowRatioID(1);     // 设置ID
            flowRatioRight.setRoutingID(decisionRoutingRight.id());  // 关联右转路径
            flowRatioRight.setStartDateTime(0);          // 起始时间
            flowRatioRight.setEndDateTime(999999);       // 结束时间
            flowRatioRight.setRatio(1.0);                // 流量比

            // 准备决策点数据（位置等信息）
            _DecisionPoint decisionPointData = new _DecisionPoint();
            decisionPointData.setDeciPointID(decisionPoint.id());
            decisionPointData.setDeciPointName(decisionPoint.name());

            // 获取决策点位置坐标
            Point decisionPointPos = new Point();
            if (decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos)) {
                decisionPointData.setX(decisionPointPos.getX());
                decisionPointData.setY(decisionPointPos.getY());
                decisionPointData.setZ(decisionPoint.link().z());
            }

            // 更新决策点及其流量比
            ArrayList<_RoutingFLowRatio> lFlowRatio = new ArrayList<>();
            lFlowRatio.add(flowRatioLeft);
            lFlowRatio.add(flowRatioStraight);
            lFlowRatio.add(flowRatioRight);

            IDecisionPoint updatedDecisionPoint = netInterface.updateDecipointPoint(decisionPointData, lFlowRatio);
            System.out.println("更新后的决策点: " + updatedDecisionPoint);
        }
    }

    public void reverseoptTrafficCanalization( ) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        // 获取路网接口
        NetInterface netInterface = iface.netInterface();
        // 获取TESS NG顶层接口
        if (iface == null) {
            System.out.println("TESS NG接口初始化失败");
            return;
        }

        // 查找关键连接段（东进口、西出口、北出口，与原方法一致）
        ILink link1 = netInterface.findLink(1);  // 东进口link
        ILink link2 = netInterface.findLink(2);  // 西出口link（原直行方向）
        ILink link7 = netInterface.findLink(7);  // 北出口link（原左转方向）

        // 步骤1：移除原左转连接器，恢复为直行连接器
        if (link1 != null && link2 != null) {
            // 定义车道连接关系（从车道 -> 到车道，改为直行方向）
            ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
            fromLaneNumbers.add(2);
            fromLaneNumbers.add(3);
            

            ArrayList<Integer> toLaneNumbers = new ArrayList<>();
            toLaneNumbers.add(2);
            toLaneNumbers.add(3);    // 调整为直行车道

            // 移除之前创建的直行连接器
            IConnector oldStriaghtConnector = netInterface.findConnector(2);
            if (oldStriaghtConnector != null) {
                netInterface.removeConnector(oldStriaghtConnector);
                System.out.println("已移除原直行连接器");
            }

            // 创建新的直行连接器（从东进口link1到西出口link2）
            IConnector newStraightConnector = netInterface.createConnector(
                    link1.id(),
                    link2.id(),  // 目标改为西出口（直行方向）
                    fromLaneNumbers,
                    toLaneNumbers,
                    "优化后直行连接器",
                    true
            );
            System.out.println("新直行连接器: " + newStraightConnector);

            // 步骤2：修改决策点和路径（将原左转路径改为直行路径）
            IDecisionPoint decisionPoint = netInterface.findDecisionPoint(1);
            if (decisionPoint == null) {
                System.out.println("未找到决策点ID=1");
                return;
            }

            // 移除原直行路径（如果存在）
            IRouting oldStriaghtRouting = netInterface.findRouting(2);
            if (oldStriaghtRouting != null) {
                netInterface.removeDeciRouting(decisionPoint, oldStriaghtRouting);
                System.out.println("已移除原左转路径");
            }

            // 创建新的直行路径（经过link1 -> link2）
            ArrayList<ILink> newStraightLinks = new ArrayList<>();
            newStraightLinks.add(link1);
            newStraightLinks.add(link2);  // 路径改为直行方向
            IRouting newStraightRouting = netInterface.createDeciRouting(decisionPoint, newStraightLinks);
            if (newStraightRouting == null) {
                System.out.println("创建新直行路径失败");
                return;
            }

            // 获取原有左转和右转路径（保持右转不变，调整直行）
            IRouting originalLeftRouting = netInterface.findRouting(3);
            IRouting rightRouting = netInterface.findRouting(1);

            // 定义新的流量比
            _RoutingFLowRatio flowRatioLeft = new _RoutingFLowRatio();
            flowRatioLeft.setRoutingFLowRatioID(1);
            flowRatioLeft.setRoutingID(originalLeftRouting.id());
            flowRatioLeft.setStartDateTime(0);
            flowRatioLeft.setEndDateTime(999999);
            flowRatioLeft.setRatio(2.0);

            _RoutingFLowRatio flowRatioStraight = new _RoutingFLowRatio();
            flowRatioStraight.setRoutingFLowRatioID(1);
            flowRatioStraight.setRoutingID(newStraightRouting.id());
            flowRatioStraight.setStartDateTime(0);
            flowRatioStraight.setEndDateTime(999999);
            flowRatioStraight.setRatio(4.0);

            _RoutingFLowRatio flowRatioRight = new _RoutingFLowRatio();
            flowRatioRight.setRoutingFLowRatioID(3);
            flowRatioRight.setRoutingID(rightRouting.id());
            flowRatioRight.setStartDateTime(0);
            flowRatioRight.setEndDateTime(999999);
            flowRatioRight.setRatio(1.0);

            // 准备决策点数据（位置等信息）
            _DecisionPoint decisionPointData = new _DecisionPoint();
            decisionPointData.setDeciPointID(decisionPoint.id());
            decisionPointData.setDeciPointName(decisionPoint.name());

            // 获取决策点位置坐标
            Point decisionPointPos = new Point();
            if (decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos)) {
                decisionPointData.setX(decisionPointPos.getX());
                decisionPointData.setY(decisionPointPos.getY());
                decisionPointData.setZ(decisionPoint.link().z());
            }


            ArrayList<_RoutingFLowRatio> lFlowRatio = new ArrayList<>();
            lFlowRatio.add(flowRatioLeft);
            lFlowRatio.add(flowRatioStraight);
            lFlowRatio.add(flowRatioRight);

            IDecisionPoint updatedDecisionPoint = netInterface.updateDecipointPoint(decisionPointData, lFlowRatio);
            System.out.println("更新后的决策点（左转改直行）: " + updatedDecisionPoint);
        }
    }

}

