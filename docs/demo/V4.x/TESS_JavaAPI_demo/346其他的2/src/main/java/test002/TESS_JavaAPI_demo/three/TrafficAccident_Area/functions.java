package test002.TESS_JavaAPI_demo.three.TrafficAccident_Area;

import com.jidatraffic.tessng.Point;

public class functions {
    /**
     * Determines whether a vehicle is on the left, right, or on a straight line
     * @param laneStartPoint Start point of the line segment
     * @param laneEndPoint End point of the line segment
     * @param vehiclePoint Vehicle coordinates
     * @return String indicating position ("left", "right", or "on")
     */
    public static String carPositionRoad(Point laneStartPoint, Point laneEndPoint, Point vehiclePoint) {
        double x1 = laneStartPoint.getX();
        double y1 = laneStartPoint.getY();
        double x2 = laneEndPoint.getX();
        double y2 = laneEndPoint.getY();
        double x = vehiclePoint.getX();
        double y = vehiclePoint.getY();

        double lineVectorX = x2 - x1;
        double lineVectorY = y2 - y1;

        double normalVectorX = -lineVectorY;
        double normalVectorY = lineVectorX;

        double carVectorX = x - x1;
        double carVectorY = y - y1;

        double crossProduct = (carVectorX * normalVectorX) + (carVectorY * normalVectorY);

        if (crossProduct > 0) {
            return "right";
        } else if (crossProduct < 0) {
            return "left";
        } else {
            return "on";
        }
    }

    /**
     * Calculates the angle between the vector and the negative y-axis, rotating clockwise
     * @param startPoint Start point of the vector
     * @param endPoint End point of the vector
     * @return Calculated angle in degrees
     */
    public static double calculateAngle(Point startPoint, Point endPoint) {
        double x1 = startPoint.getX();
        double y1 = startPoint.getY();
        double x2 = endPoint.getX();
        double y2 = endPoint.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;

        double angleRadians = Math.atan2(dy, dx);

        double angleDegrees = Math.toDegrees(angleRadians);

        angleDegrees = (angleDegrees + 360) % 360;
        angleDegrees = (angleDegrees - 90) % 360;
        angleDegrees = (angleDegrees + 180) % 360;

        return angleDegrees;
    }
}
