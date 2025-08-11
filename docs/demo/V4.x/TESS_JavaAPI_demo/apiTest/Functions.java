package Others.apiTest;

import com.jidatraffic.tessng.*;

public class Functions {
    // 动作控制方案号
    public static double actionControlMethodNumber = 0;
    public static double getActionControlMethodNumber() {
        return actionControlMethodNumber;
    }
    public static void setActionControlMethodNumber(double methodNumber) {
        actionControlMethodNumber = methodNumber;
    }
    /**
     * 时钟转秒
     * @param timeStr 时钟字符串（格式如"HH:MM"）
     * @return 转换后的总秒数
     * @throws IllegalArgumentException 当输入格式不正确时抛出
     */
    public static double timeToSeconds(String timeStr) {
        // 使用冒号分割小时和分钟部分
        String[] parts = timeStr.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("无效的时间格式，应为HH:MM");
        }

        // 将小时和分钟部分转换为整数
        double hours = Integer.parseInt(parts[0]);
        double minutes = Integer.parseInt(parts[1]);

        // 计算总秒数
        return hours * 3600 + minutes * 60;
    }

    /**
     * 判断车辆位于直线左侧、右侧还是线上
     * @param laneStartPoint 线段起点
     * @param laneEndPoint 线段终点
     * @param vehiclePoint 车辆坐标点
     * @return 位置描述字符串："left"、"right"或"on"
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
        double crossProduct = carVectorX * normalVectorX + carVectorY * normalVectorY;

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
     * 计算向量与y轴负方向的夹角（顺时针旋转）
     * @param startPoint 向量起点
     * @param endPoint 向量终点
     * @return 角度值（度）
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
        angleDegrees = (angleDegrees + 360) % 360; // 将角度转为正值
        angleDegrees = (angleDegrees - 90) % 360;  // 修正角度，使向上为0度
        angleDegrees = (angleDegrees + 180) % 360; // 修正左右方向

        // 确保结果为非负值
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }

        return angleDegrees;
    }
}