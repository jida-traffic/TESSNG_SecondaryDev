package Traffic_Signal_Control.Isolated_Signal_Control;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    // 每相位对应的采集器ID列表（对应Python的 phase2collector_dict）
    public static final Map<Integer, List<Integer>> phase2collector_dict;
    static {
        // 初始化Map并添加键值对
        phase2collector_dict = new HashMap<>();

        // 南北左转：相位8对应采集器[2,3,14,15]
        List<Integer> phase8Collectors = new ArrayList<>();
        phase8Collectors.add(2);
        phase8Collectors.add(3);
        phase8Collectors.add(14);
        phase8Collectors.add(15);
        phase2collector_dict.put(8, phase8Collectors);

        // 南北直行：相位7对应采集器[4,5,6,16,17,18]
        List<Integer> phase7Collectors = new ArrayList<>();
        phase7Collectors.add(4);
        phase7Collectors.add(5);
        phase7Collectors.add(6);
        phase7Collectors.add(16);
        phase7Collectors.add(17);
        phase7Collectors.add(18);
        phase2collector_dict.put(7, phase7Collectors);

        // 东西左转：相位6对应采集器[7,8,19,20,21]
        List<Integer> phase6Collectors = new ArrayList<>();
        phase6Collectors.add(7);
        phase6Collectors.add(8);
        phase6Collectors.add(19);
        phase6Collectors.add(20);
        phase6Collectors.add(21);
        phase2collector_dict.put(6, phase6Collectors);

        // 东西直行：相位5对应采集器[9,10,11,12,22,23,24,25]
        List<Integer> phase5Collectors = new ArrayList<>();
        phase5Collectors.add(9);
        phase5Collectors.add(10);
        phase5Collectors.add(11);
        phase5Collectors.add(12);
        phase5Collectors.add(22);
        phase5Collectors.add(23);
        phase5Collectors.add(24);
        phase5Collectors.add(25);
        phase2collector_dict.put(5, phase5Collectors);
    }

    // 采集器集计周期（单位：秒），与路网保持一致
    public static final int aggregate_time = 180;

    // 单车道通行能力
    public static final int capacity = 1400;

    // 最大周期时长（单位：秒）
    public static final int max_period_time = 200;

    // 最小周期时长（单位：秒）
    public static final int min_period_time = 100;
}