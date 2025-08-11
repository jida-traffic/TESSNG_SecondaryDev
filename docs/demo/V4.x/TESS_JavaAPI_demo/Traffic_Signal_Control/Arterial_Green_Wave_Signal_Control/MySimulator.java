package Traffic_Signal_Control.Arterial_Green_Wave_Signal_Control;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.ColorInterval;

import java.io.FileReader;
import java.util.*;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MySimulator extends JCustomerSimulator {

    private NetInterface mNetInf;

    public MySimulator() {
        super();
    }

    /**
     * 重载父类方法，TESS NG在每个计算周期结束后调用此方法
     */
    @Override
    public void afterOneStep() {
        // TESSNG 顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        // TESSNG 仿真子接口
        SimuInterface simuiface = iface.simuInterface();
        // TESSNG 路网子接口
        NetInterface netiface = iface.netInterface();

        // 当前仿真计算批次
        long batchNum = simuiface.batchNumber();
        // 当前已仿真时间，单位：毫秒
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();

        // 修改触发条件，确保只在仿真时间达到180秒时执行一次
        if ((int)(simuTime / (180 * 1000)) == 1 && simuTime % (180 * 1000) == 0) {
            System.out.println("当前仿真时间:" + (simuTime/1000) + "s");

            try {
                JSONParser parser = new JSONParser();
                JSONObject params = (JSONObject) parser.parse(new FileReader("D:\\Yangzhenbei\\Java_demo\\src\\Traffic_Signal_Control\\Arterial_Green_Wave_Signal_Control\\param.json"));
                List<Long> cor_groups = (List<Long>) params.get("groupIdLst");

                Map<Integer, Map<String, Object>> origin_groups = new HashMap<>();

                for (Long group_id : cor_groups) {
                    ISignalPlan group = netiface.findSignalPlanById(group_id);
                    if (group == null) {
                        System.err.println("警告：找不到ID为 " + group_id + " 的信号方案");
                        continue;
                    }

                    int period_time = group.cycleTime();
                    // 修复数据结构类型：使用Integer作为key而不是Long
                    Map<Integer, Map<String, Object>> phase_interval_dict = new HashMap<>();

                    List<ISignalPhase> phases = group.phases();
                    for (ISignalPhase phase : phases) {
                        int start_time = 0;
                        int green_interval = 0;
                        int yellow_interval = 0;

                        List<ColorInterval> color_intervals = phase.listColor();
                        for (ColorInterval color_interval : color_intervals) {
                            if ("红".equals(color_interval.getColor())) {
                                start_time += color_interval.getInterval();
                            } else if ("绿".equals(color_interval.getColor())) {
                                green_interval = color_interval.getInterval();
                            } else if ("黄".equals(color_interval.getColor())) {
                                yellow_interval = color_interval.getInterval();
                                break;
                            }
                        }

                        Map<String, Object> phase_info = new HashMap<>();
                        phase_info.put("phase_id", phase.id());
                        phase_info.put("start_time", start_time);
                        phase_info.put("green_interval", green_interval);
                        phase_info.put("yellow_interval", yellow_interval);
                        phase_info.put("all_red_interval", 0);

                        // 修复数据结构类型：使用Integer作为key而不是Long
                        phase_interval_dict.put((int)phase.id(), phase_info);
                    }

                    Map<String, Object> group_info = new HashMap<>();
                    group_info.put("period_time", period_time);
                    group_info.put("phases", phase_interval_dict);

                    origin_groups.put(group_id.intValue(), group_info);
                }

                // maxband计算干线周期和协调相位差
                System.out.println("maxband计算干道绿波方案");
                Object[] result = Function.maxband();
                if (result == null) {
                    System.err.println("maxband计算失败");
                    return;
                }

                double C_double = (double) result[0];
                int C = (int) Math.round(C_double);
                List<Double> o_lst = (List<Double>) result[1];
                Map<Integer, Map<String, Object>> new_groups = Function.calGroups(origin_groups, C, o_lst);

                if (new_groups == null) {
                    System.err.println("calGroups计算失败");
                    return;
                }

                for (Map.Entry<Integer, Map<String, Object>> entry : new_groups.entrySet()) {
                    Integer group_id = entry.getKey();
                    Map<String, Object> new_group = entry.getValue();

                    ISignalPlan group = netiface.findSignalPlanById(group_id.longValue());
                    if (group == null) {
                        System.err.println("警告：找不到ID为 " + group_id + " 的信号方案");
                        continue;
                    }

                    // 使用Number进行安全的类型转换
                    int new_period_time = ((Number) new_group.get("period_time")).intValue();
                    group.setCycleTime(new_period_time);

                    // 修复数据结构类型：使用Integer作为key而不是Long
                    Map<Integer, Map<String, Object>> phases_map = (Map<Integer, Map<String, Object>>) new_group.get("phases");
                    for (Map.Entry<Integer, Map<String, Object>> phase_entry : phases_map.entrySet()) {
                        Integer phase_id = phase_entry.getKey();
                        Map<String, Object> new_phase = phase_entry.getValue();

                        System.out.println("phase_id=" + phase_id + ", new_phase=" + new_phase);

                        List<ISignalPhase> phases = group.phases();
                        ISignalPhase phase = null;
                        for (ISignalPhase p : phases) {
                            if (p.id() == phase_id.longValue()) {
                                phase = p;
                                break;
                            }
                        }

                        if (phase == null) {
                            System.err.println("警告：找不到ID为 " + phase_id + " 的信号相位");
                            continue;
                        }

                        // 使用Number进行安全的类型转换
                        int start_time = ((Number) new_phase.get("start_time")).intValue();
                        int green_interval = ((Number) new_phase.get("green_interval")).intValue();
                        int yellow_interval = ((Number) new_phase.get("yellow_interval")).intValue();

                        ArrayList<ColorInterval> new_color_lst = new ArrayList<>();

                        if (start_time + green_interval > new_period_time) {
                            // 该相位绿灯时间将被截断
                            new_color_lst.add(new ColorInterval("红", Math.max(start_time + green_interval - new_period_time - green_interval, 0)));
                            new_color_lst.add(new ColorInterval("绿", Math.min(start_time + green_interval - new_period_time, green_interval)));
                            new_color_lst.add(new ColorInterval("黄", yellow_interval));
                            new_color_lst.add(new ColorInterval("红", start_time - (start_time + green_interval - new_period_time) - yellow_interval));
                            new_color_lst.add(new ColorInterval("绿", new_period_time - start_time));
                        } else if (start_time + green_interval + yellow_interval > new_period_time) {
                            // 该相位黄灯时间将被截断
                            new_color_lst.add(new ColorInterval("黄", start_time + green_interval + yellow_interval - new_period_time));
                            new_color_lst.add(new ColorInterval("红", start_time - (start_time + green_interval + yellow_interval - new_period_time)));
                            new_color_lst.add(new ColorInterval("绿", green_interval));
                            new_color_lst.add(new ColorInterval("黄", yellow_interval - (start_time + green_interval + yellow_interval - new_period_time)));
                        } else {
                            new_color_lst.add(new ColorInterval("红", start_time));
                            new_color_lst.add(new ColorInterval("绿", green_interval));
                            new_color_lst.add(new ColorInterval("黄", yellow_interval));
                            new_color_lst.add(new ColorInterval("红", Math.max((new_period_time - start_time - green_interval - yellow_interval), 0)));
                        }

                        phase.setColorList(new_color_lst);
                    }
                }
                System.out.println("已修改信控方案");

            } catch (Exception e) {
                System.err.println("处理过程中发生错误:");
                e.printStackTrace();
            }
        }
    }
}
