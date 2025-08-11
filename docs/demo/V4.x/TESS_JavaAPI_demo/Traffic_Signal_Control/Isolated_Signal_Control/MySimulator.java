package Traffic_Signal_Control.Isolated_Signal_Control;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.ObjReal;
import com.jidatraffic.tessng.TessInterface;
import com.jidatraffic.tessng.SimuInterface;
import com.jidatraffic.tessng.NetInterface;
import com.jidatraffic.tessng.IVehicle;
import com.jidatraffic.tessng.ILink;
import com.jidatraffic.tessng.ISignalPlan;
import com.jidatraffic.tessng.ISignalPhase;
import com.jidatraffic.tessng.ColorInterval;

import java.util.*;
import javax.swing.*;

public class MySimulator extends JCustomerSimulator {
    private Object mNetInf;  // 假设为路网信息对象，具体类型根据SDK调整
    private TessInterface iface;
    private SimuInterface simuiface;
    private NetInterface netiface;
    private JCustomerNet Jnetiface;

    public MySimulator() {
        super();
    }

    @Override
    public void afterOneStep() {
        // 初始化接口
        iface = TESSNG.tessngIFace();
        simuiface = iface.simuInterface();
        netiface = iface.netInterface();
        Jnetiface = new JCustomerNet();

        // 获取当前仿真时间（毫秒）
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        long aggregateTimeMs = Config.aggregate_time * 1000;

        // 检查是否到达集计时间点
        if (simuTime / aggregateTimeMs > 1 && simuTime % aggregateTimeMs == 1000) {
            System.out.println("simuTime: " + simuTime);

            // 收集每个采集器的流量数据
            ArrayList<VehiInfoAggregated> collectors = simuiface.getVehisInfoAggregated();
            Map<Long, Double> collectorFlowDict = new HashMap<>();

            for (VehiInfoAggregated collector : collectors) {
                long collectorId = collector.getCollectorId();
                int vehCount = collector.getVehiCount();
                long aggregateTime = collector.getToTime() - collector.getFromTime();
                double flow = 0;

                if (aggregateTime > 0) {
                    flow = vehCount / (double)aggregateTime * 3600;  // 转换为小时流量
                }
                collectorFlowDict.put(collectorId, flow);
            }

            // 计算每个相位的流量
            Map<Integer, Double> phaseFlowDict = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry : Config.phase2collector_dict.entrySet()) {
                int phaseId = entry.getKey();
                List<Integer> collectorList = entry.getValue();
                double totalFlow = 0;

                for (int collectorId : collectorList) {
                    totalFlow += collectorFlowDict.getOrDefault(collectorId, 0.0);
                }

                phaseFlowDict.put(phaseId, totalFlow / collectorList.size());  // 计算每相位单车道流量
            }

            System.out.println(phaseFlowDict);
            System.out.println();
            System.out.println(collectorFlowDict);

            // 获取当前信号配时方案
            List<ISignalPlan> plans = netiface.signalPlans();
            ISignalPlan plan = netiface.findSignalPlanById(201);
            int cycleTime = plan.cycleTime();

            // 收集每个相位的时间间隔信息
            Map<Integer, Map<String, Integer>> phaseIntervalDict = new HashMap<>();
            for (ISignalPhase phase : plan.phases()) {
                Map<String, Integer> intervalInfo = new HashMap<>();
                int startTime = 0;
                int greenInterval = 0;
                int yellowInterval = 0;

                for (ColorInterval colorInterval : phase.listColor()) {
                    String color = colorInterval.getColor();
                    int interval = colorInterval.getInterval();

                    if ("红".equals(color)) {
                        startTime += interval;
                    } else if ("绿".equals(color)) {
                        greenInterval = interval;
                    } else if ("黄".equals(color)) {
                        yellowInterval = interval;
                    }
                }

                intervalInfo.put("phase_id", (int) phase.id());
                intervalInfo.put("start_time", startTime);
                intervalInfo.put("green_interval", greenInterval);
                intervalInfo.put("yellow_interval", yellowInterval);
                intervalInfo.put("all_red_interval", 2);
                phaseIntervalDict.put((int) phase.id(), intervalInfo);
            }

            // 应用韦伯斯特信号配时计算
            Map<String, Object> websterResult = Function.webster(cycleTime, phaseIntervalDict, phaseFlowDict);
            int newPeriodTime = (int) websterResult.get("new_period_time");
            Map<Integer, Map<String, Integer>> newPhaseIntervalDict =
                    (Map<Integer, Map<String, Integer>>) websterResult.get("new_phase_interval_dict");

            System.out.println(newPeriodTime + " " + newPhaseIntervalDict);

            // 更新信号配时方案
            plan.setCycleTime(newPeriodTime);

            for (ISignalPhase phase : plan.phases()) {
                Map<String, Integer> newPhase = newPhaseIntervalDict.get(phase.id());
                if (newPhase == null) continue;

                ArrayList<ColorInterval> newColorList = new ArrayList<>();
                int startTime = newPhase.get("start_time");
                int greenInterval = newPhase.get("green_interval");
                int yellowInterval = newPhase.get("yellow_interval");

                // 计算全红时间
                int allRedInterval = Math.max(newPeriodTime - startTime - greenInterval - yellowInterval, 0);
                System.out.println("全红时间为：" + allRedInterval);
                newColorList.add(new ColorInterval("红", startTime));
                newColorList.add(new ColorInterval("绿", greenInterval));
                newColorList.add(new ColorInterval("黄", yellowInterval));
                newColorList.add(new ColorInterval("红", allRedInterval));

                phase.setColorList(newColorList);
            }

            System.out.println("set new signal group successfully");
        }
    }
}