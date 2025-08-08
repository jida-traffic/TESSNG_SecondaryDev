package test002.TESS_JavaAPI_demo.four.VehicleSpeedGuidance.my_code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class config {
    // 表示是否进行速度引导
    public static final boolean IS_GUIDANCE = true;

    // 仿真时长
    public static final int SIM_TIME = 3600;

    // 车辆性能相关的参数：加减速度(m/s²)
    public static final double ACCELERATION = 3.5;
    public static final double DECELERATION = 3.0;

    // 惩罚系数，用来惩罚停车
    public static final int STOP_WEIGHT = 5;

    // 每个引导段长度
    public static final int GUIDANCE_LENGTH = 200;

    // 默认的期望速度(km/h)
    public static final int DEFAULT_DES_SPEED = 60;
    // 建议速度遍历从30km/h开始
    public static final int FROM_SPEED = 30;
    // 建议速度遍历到80km/h结束
    public static final int TO_SPEED = 80;
    // 速度建议的精度(km/h)
    public static final int DES_GAP = 5;

    // 存储目标路段的ID
    public static final List<Integer> LINKS_ID = new ArrayList<Integer>() {{
        add(1);
        add(2);
        add(3);
    }};

    // 存储全部路段的ID
    public static final List<Integer> ALL_LINKS_ID = new ArrayList<Integer>() {{
        add(1);
        add(101);
        add(2);
        add(102);
        add(3);
        add(103);
        add(4);
    }};

    // 储存每个目标路段的长度
    public static final List<Double> LINKS_LENGTH = new ArrayList<Double>() {{
        add(921.4);
        add(1912.7);
        add(987.3);
    }};

    // 存储着信号灯的位置信息
    public static final List<Double> SIGNAL_HEADS_POS = new ArrayList<Double>() {{
        add(858.7);
        add(1838.7);
        add(982.7);
    }};

    // 信号灯周期
    public static final int SIGNAL_CYCLE = 72;
    // 红灯时长
    public static final int SIGNAL_RED = 54;

    // 每个目标路段对应的信号灯offset
    public static final Map<Integer, Integer> SIGNAL_HEAD_OFFSET = new HashMap<Integer, Integer>() {{
        put(1, 0);
        put(2, 0);
        put(3, 0);
    }};

    // 先构建一个路段长度表
    public static final List<LinkLength> LINKS_LENGTH_LIST = new ArrayList<LinkLength>() {{
        add(new LinkLength(1, 921.4));
        add(new LinkLength(101, 28.2));
        add(new LinkLength(2, 1912.7));
        add(new LinkLength(102, 30.8));
        add(new LinkLength(3, 987.3));
        add(new LinkLength(103, 85.4));
        add(new LinkLength(4, 386.7));
    }};

    // 路段累计长度字典
    public static final Map<Integer, Double> LINKS_LENGTH_ADDED;
    static {
        LINKS_LENGTH_ADDED = new HashMap<>();
        double length = 0;
        for (LinkLength item : LINKS_LENGTH_LIST) {
            int linkId = item.linkId;
            LINKS_LENGTH_ADDED.put(linkId, round(length, 2));
            length += round(item.length, 2);
        }
    }

    // 储存每个目标路段的引导点位置
    public static final List<List<Double>> GUIDANCE_POINTS;
    static {
        GUIDANCE_POINTS = new ArrayList<>();
        // 处理每个路段的引导点位置
        for (int i = 0; i < LINKS_ID.size(); i++) {
            double signalHeadPos = SIGNAL_HEADS_POS.get(i);
            List<Double> guidancePoint = new ArrayList<>();
            double l = signalHeadPos - GUIDANCE_LENGTH - 2.0; // 预留2米作为停车线

            while (l > 0) {
                guidancePoint.add(round(l, 2));
                l = l - GUIDANCE_LENGTH;
            }
            GUIDANCE_POINTS.add(guidancePoint);
        }
    }

    // 储存全部车辆数据
    public static final List<Object> DATAS = new ArrayList<>();

    // 实时路段排队信息
    public static final Map<Object, Object> QUEUE_LENGTH_LIST = new HashMap<>();

    // 存储引导速度信息
    public static final Map<Object, Object> SPEED_GUIDANCE = new HashMap<>();

    // 辅助类：存储路段ID和长度
    static class LinkLength {
        int linkId;
        double length;

        LinkLength(int linkId, double length) {
            this.linkId = linkId;
            this.length = length;
        }
    }

    // 辅助方法：保留指定位数的小数
    private static double round(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.round(value * scale) / scale;
    }
}

