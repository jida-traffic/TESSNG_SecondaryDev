package TESS_Java_APIDemo.four.EmergencyLaneOpeningControl;

import com.jidatraffic.tessng.Point;

public class functions {
    /**
     * 判断车辆位于直线左侧还是右侧
     * @param laneStartPoint 线段向量起点
     * @param laneEndPoint 线段向量终点
     * @param vehiclePoint 车辆坐标
     * @return 车辆位于哪侧的字符串 ("left", "right", "on")
     */
    public static String carPositionRoad(Point laneStartPoint, Point laneEndPoint, Point vehiclePoint) {
        double x1 = laneStartPoint.getX();
        double y1 = laneStartPoint.getY();
        double x2 = laneEndPoint.getX();
        double y2 = laneEndPoint.getY();
        double x = vehiclePoint.getX();
        double y = vehiclePoint.getY();

        // 计算直线上两点的向量
        double lineVectorX = x2 - x1;
        double lineVectorY = y2 - y1;

        // 计算直线的法向量（垂直于直线的向量）
        double normalVectorX = -lineVectorY;
        double normalVectorY = lineVectorX;

        // 计算直线上一个点到车辆的向量
        double carVectorX = x - x1;
        double carVectorY = y - y1;

        // 计算叉积
        double crossProduct = (carVectorX * normalVectorX) + (carVectorY * normalVectorY);

        // 判断车辆位置
        if (crossProduct > 0) {
            return "right";
        } else if (crossProduct < 0) {
            return "left";
        } else {
            return "on";
        }
    }

    /**
     * 计算向量与y轴负方向夹角，顺时针旋转
     * @param startPoint 起点
     * @param endPoint 终点
     * @return 计算得到的角度
     */
    public static double calculateAngle(Point startPoint, Point endPoint) {
        double x1 = startPoint.getX();
        double y1 = startPoint.getY();
        double x2 = endPoint.getX();
        double y2 = endPoint.getY();

        // 计算两点之间的差值
        double dx = x2 - x1;
        double dy = y2 - y1;

        // 使用atan2计算夹角（弧度）
        double angleRadians = Math.atan2(dy, dx);

        // 将弧度转换为角度
        double angleDegrees = Math.toDegrees(angleRadians);

        // 修正角度，使其符合软件的定义方式
        angleDegrees = (angleDegrees + 360) % 360;  // 将角度转为正值
        angleDegrees = (angleDegrees - 90) % 360;   // 修正角度，使向上为0度

        // 修正左右方向
        angleDegrees = (angleDegrees + 180) % 360;  // 修正左右方向

        return angleDegrees;
    }
}
