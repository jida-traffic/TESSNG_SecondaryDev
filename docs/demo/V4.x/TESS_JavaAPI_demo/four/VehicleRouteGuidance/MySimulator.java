package test002.TESS_JavaAPI_demo.four.VehicleRouteGuidance;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;

import java.util.*;
import java.io.*;
import com.google.gson.Gson;

import static test002.TESS_JavaAPI_demo.four.VehicleRouteGuidance.Plot.plotQueueLengths;
import static test002.TESS_JavaAPI_demo.four.VehicleRouteGuidance.Plot.plotTrafficRatios;

public class MySimulator extends  CustomerSimulator {
    // 示例路段排队计数器列表
    private Map<Integer, List<Double>> vehiQueueAggregationDict = new HashMap<>();
    // 示例决策点当前流量
    private Map<String, Map<String, String>> decisionPointCurrentFlowRation = new HashMap<>();
    // 示例决策点右左直流量比例
    private List<List<Double>> decisionPointFlowRatio = new ArrayList<>();
    // 排队计数器统计标志
    private boolean vehiQueueAggregateFlag = false;
    // 排队计数器集计时间间隔列表
    private List<Double> lVehiQueueAggrInterval = new ArrayList<>();


    public MySimulator() {
        // 初始化父类（如果需要）
        // 加载示例决策点初始流量
        super();
        loadDecisionPointFlowRation();

    }

    private void loadDecisionPointFlowRation() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\four\\VehicleRouteGuidance\\JsonData\\DecisionPoint3801_FlowRation.json")) {
            // 解析JSON文件到决策点流量比映射
            decisionPointCurrentFlowRation = gson.fromJson(reader,
                    new com.google.gson.reflect.TypeToken<Map<String, Map<String, String>>>(){}.getType());
        } catch (IOException e) {
            System.err.println("加载决策点流量比JSON失败: " + e.getMessage());
        }
    }

    /**
     * 动态修改决策点不同路径流量比
     */
    @Override
    public ArrayList<DecipointFlowRatioByInterval> calcDynaFlowRatioParameters() {
        if(!vehiQueueAggregateFlag){
            return new ArrayList<>();
        }
        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        System.out.println("L21起点路段路口直行车道排队过长，降低直行流量比！" + simuTime);

        // 构建流量比调整参数
        ArrayList<DecipointFlowRatioByInterval> result = new ArrayList<>();
        if (decisionPointCurrentFlowRation.isEmpty()) {
            return result;
        }

        // 获取第一个时间段的流量比配置
        String decipointInterval = decisionPointCurrentFlowRation.keySet().iterator().next();
        Map<String, String> routingFlowMap = decisionPointCurrentFlowRation.get(decipointInterval);
        if (routingFlowMap == null) {
            return result;
        }

        // 构建决策点流量比时间段对象
        DecipointFlowRatioByInterval decipointFlowRatio = new DecipointFlowRatioByInterval();
        decipointFlowRatio.setDeciPointID(3801);

        // 解析时间区间
        String[] timeRange = decipointInterval.split("-");
        if (timeRange.length == 2) {
            decipointFlowRatio.setStartDateTime(Integer.parseInt(timeRange[0]));
            decipointFlowRatio.setEndDateTime(Integer.parseInt(timeRange[1]));
        }

        // 调整各路径流量比
        List<RoutingFlowRatio> routingFlowRatios = new ArrayList<>();
        List<Integer> updatedRatios = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, String> entry : routingFlowMap.entrySet()) {
            String routingIdStr = entry.getKey();
            int routingId = Integer.parseInt(routingIdStr);
            int currentRatio = Integer.parseInt(entry.getValue());
            int updatedRatio = currentRatio;

            // 根据路径类型调整比例
            if (index <= 0) { // 右转
                updatedRatio = currentRatio + 3;
            } else if (index <= 4) { // 左转
                updatedRatio = currentRatio;
            } else if (index < 8) { // 直行（3905,3907,3908）
                updatedRatio = currentRatio + 3;
            } else { // 直行（6109,6110）
                updatedRatio = (currentRatio < 6) ? currentRatio : currentRatio - 6;
            }

            // 添加调整后的流量比
            routingFlowRatios.add(new  RoutingFlowRatio(routingId, (double) updatedRatio));
            updatedRatios.add(updatedRatio);
            // 更新映射中的值
            routingFlowMap.put(routingIdStr, String.valueOf(updatedRatio));

            // 计算并发送比例信息（最后一个索引时）
            if (index == 9) {
                int sum = updatedRatios.stream().mapToInt(Integer::intValue).sum();
                double rightRatio = Math.round(updatedRatios.get(0) * 100.0 / sum * 10) / 10.0;
                double leftRatio = Math.round(updatedRatios.subList(1, 5).stream().mapToInt(Integer::intValue).sum() * 100.0 / sum * 10) / 10.0;
                double straightRatio = Math.round(updatedRatios.subList(6, 10).stream().mapToInt(Integer::intValue).sum() * 100.0 / sum * 10) / 10.0;

                String runInfo = String.format(
                        "L21起点路段路口直行车道排队过长，动态调整左转、右转和直行的流量比！\n\n修改后右转比例为: %.1f\n左转比例为: %.1f\n直行比例为: %.1f\n",
                        rightRatio, leftRatio, straightRatio
                );
//                sendRunInfo(runInfo);
                decisionPointFlowRatio.add(Arrays.asList(rightRatio, leftRatio, straightRatio));
            }

            index++;
        }
        decipointFlowRatio.setRoutingFlowRatios((ArrayList<RoutingFlowRatio>) routingFlowRatios);
        result.add(decipointFlowRatio);
        vehiQueueAggregateFlag = false; // 重置标志
        return result;
    }

    /**
     * 每个计算周期结束后调用
     */
    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        long batchNum = simuiface.batchNumber();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();

        // 获取当前运行车辆列表（未使用，保留原逻辑）
        ArrayList<IVehicle> allVehicles = simuiface.allVehiStarted();

        // 处理排队计数器数据
        List<VehiQueueAggregated> lVehiQueueAggr = simuiface.getVehisQueueAggregated();
        if (!lVehiQueueAggr.isEmpty() && simuTime > (300 + 20) * 1000) {
            System.out.println(simuTime);
            lVehiQueueAggrInterval.add((simuTime / 1000.0) - 300);

            for (VehiQueueAggregated vqAggr : lVehiQueueAggr) {
                long vehiCounterId = vqAggr.getCounterId();
                // 处理目标排队计数器
                if (Arrays.asList(4501, 4502, 4503, 4504).contains((int)vehiCounterId)) {
                    vehiQueueAggregationDict.computeIfAbsent((int) vehiCounterId, k -> new ArrayList<>())
                            .add(vqAggr.getAvgQueueLength());
                    System.out.printf("车辆排队集计数据：%d, %.2f, %.2f%n",
                            vehiCounterId, vqAggr.getAvgQueueLength(), vqAggr.getMaxQueueLength());
                    vehiQueueAggregateFlag = true;
                }
            }
        }
    }

    /**
     * 仿真停止后调用
     */
    @Override
    public void afterStop() {
        System.out.println(decisionPointFlowRatio);
        System.out.println(vehiQueueAggregationDict);
        System.out.println(lVehiQueueAggrInterval);

        // 绘制排队长度和流量比图表
        plotQueueLengths(lVehiQueueAggrInterval, vehiQueueAggregationDict);
        plotTrafficRatios(lVehiQueueAggrInterval, decisionPointFlowRatio);
    }
}