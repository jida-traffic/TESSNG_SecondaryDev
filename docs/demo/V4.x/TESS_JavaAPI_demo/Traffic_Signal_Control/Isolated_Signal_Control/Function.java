package TESS_Java_APIDemo.Traffic_Signal_Control.Isolated_Signal_Control;

import java.util.*;
import java.lang.Math;

public class Function {

    /**
     * 韦伯斯特信号配时计算方法
     * @param periodTime 初始周期时长
     * @param phaseIntervalDict 相位时间间隔字典（key:相位ID，value:包含各时间参数的Map）
     * @param phaseFlowDict 相位流量字典（key:相位ID，value:流量）
     * @return 包含新周期时长和新相位间隔字典的Map
     */
    public static Map<String, Object> webster(int periodTime,
                                              Map<Integer, Map<String, Integer>> phaseIntervalDict,
                                              Map<Integer, Double> phaseFlowDict) {
        // 1. 初始化参数
        int k = phaseIntervalDict.size(); // 相位数
        List<Integer> allRedIntervalList = new ArrayList<>(); // 全红时间列表
        List<Integer> yellowIntervalList = new ArrayList<>(); // 黄灯时间列表
        List<Double> flowList = new ArrayList<>(); // 流量列表
        List<Integer> phaseIds = new ArrayList<>(phaseIntervalDict.keySet()); // 相位ID列表（保持顺序）

        // 2. 提取各相位时间参数和流量
        for (int phaseId : phaseIds) {
            Map<String, Integer> phaseInfo = phaseIntervalDict.get(phaseId);
            allRedIntervalList.add(phaseInfo.get("all_red_interval"));
            yellowIntervalList.add(phaseInfo.get("yellow_interval"));
            flowList.add(phaseFlowDict.get(phaseId));
        }

        // 3. 计算总损失时间L（全红+黄灯）
        int L = 0;
        for (int allRed : allRedIntervalList) {
            L += allRed;
        }
        for (int yellow : yellowIntervalList) {
            L += yellow;
        }

        // 4. 计算流量比y
        List<Double> yList = new ArrayList<>();
        double totalY = 0.0;
        for (double flow : flowList) {
            double y = flow / Config.capacity; // 流量比=流量/通行能力
            yList.add(y);
            totalY += y;
        }

        // 5. 计算新周期时长C
        double c = Math.ceil(L / (1 - totalY)); // 韦伯斯特周期公式
        // 周期范围限制：[初始周期*0.5, 初始周期*2]，且不超出配置的最大/最小周期
        c = Math.max(c, periodTime * 0.5);
        c = Math.min(c, periodTime * 2);
        c = Math.max(c, Config.min_period_time);
        c = Math.min(c, Config.max_period_time);
        int newPeriodTime = (int) c;

        // 6. 计算总有效绿灯时间G_e
        int geTotal = newPeriodTime - (int) Math.floor(L);

        // 7. 分配各相位有效绿灯时间
        List<Integer> greenIntervalList = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            double ratio = yList.get(i) / totalY; // 按流量比分配
            int ge = (int) Math.floor(ratio * geTotal);
            greenIntervalList.add(ge);
        }

        // 8. 调整最后一个相位的绿灯时间，确保总和等于总有效绿灯时间
        int sumGreen = 0;
        for (int ge : greenIntervalList) {
            sumGreen += ge;
        }
        int adjust = geTotal - sumGreen;
        greenIntervalList.set(k - 1, greenIntervalList.get(k - 1) + adjust);

        // 9. 构建新的相位间隔字典
        Map<Integer, Map<String, Integer>> newPhaseIntervalDict = new HashMap<>();
        for (int i = 0; i < phaseIds.size(); i++) {
            int phaseId = phaseIds.get(i);
            Map<String, Integer> oldPhaseInfo = phaseIntervalDict.get(phaseId);

            // 计算起始时间（前序相位的绿灯+黄灯时间总和）
            int startTime = 0;
            for (int j = 0; j < i; j++) {
                startTime += greenIntervalList.get(j);
                startTime += yellowIntervalList.get(j);
            }

            // 封装新的相位信息
            Map<String, Integer> newPhaseInfo = new HashMap<>();
            newPhaseInfo.put("phase_id", phaseId);
            newPhaseInfo.put("start_time", startTime);
            newPhaseInfo.put("green_interval", greenIntervalList.get(i));
            newPhaseInfo.put("yellow_interval", yellowIntervalList.get(i));
            newPhaseInfo.put("all_red_interval", allRedIntervalList.get(i));

            newPhaseIntervalDict.put(phaseId, newPhaseInfo);
        }

        // 10. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("new_period_time", newPeriodTime);
        result.put("new_phase_interval_dict", newPhaseIntervalDict);
        return result;
    }
}