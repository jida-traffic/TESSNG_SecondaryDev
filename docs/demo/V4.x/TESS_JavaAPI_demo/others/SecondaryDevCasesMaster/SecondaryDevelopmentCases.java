//package test002.TESS_JavaAPI_demo.others.SecondaryDevCasesMaster;
//
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.jidatraffic.tessng.*;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static com.jidatraffic.tessng.TESSNG.m2p;
//import static com.jidatraffic.tessng.TESSNG.tessngIFace;
//
//public class SecondaryDevelopmentCases {
//    // 信控编辑案例
//    public void editSignalController() {
//        // 获取TESSNG顶层接口
//        TessInterface iface = tessngIFace();
//        // 获取仿真子接口
//        SimuInterface simuiface = iface.simuInterface();
//        // 获取路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        // 创建两条新路段和一条连接段作为示例
//        Point startPoint1 = new Point(m2p(-300), m2p(-200));
//        Point endPoint1 = new Point(m2p(-50), m2p(-200));
//        ArrayList<Point> lPoint1 = new ArrayList<>();
//        lPoint1.add(startPoint1);
//        lPoint1.add(endPoint1);
//        ILink link1 = netiface.createLink(lPoint1, 3, "信控编辑路段1");
//
//        Point startPoint2 = new Point(m2p(50), m2p(-200));
//        Point endPoint2 = new Point(m2p(300), m2p(-200));
//        ArrayList<Point> lPoint2 = new ArrayList<>();
//        lPoint2.add(startPoint2);
//        lPoint2.add(endPoint2);
//        ILink link2 = netiface.createLink( lPoint2, 3, "信控编辑路段2");
//
//        // 连接段车道连接列表
//        List<ILaneObject> lLaneObjects = new ArrayList<>();
//        if (link1 != null && link2 != null) {
//            ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
//            lFromLaneNumber.add(1);
//            lFromLaneNumber.add(2);
//            lFromLaneNumber.add(3);
//
//            ArrayList<Integer> lToLaneNumber = new ArrayList<>();
//            lToLaneNumber.add(1);
//            lToLaneNumber.add(2);
//            lToLaneNumber.add(3);
//
//            IConnector connector = netiface.createConnector(
//                    link1.id(), link2.id(), lFromLaneNumber, lToLaneNumber, "信控编辑连接段", true);
//
//            if (connector != null) {
//                lLaneObjects = connector.laneObjects();
//                for (ILaneObject laneObj : lLaneObjects) {
//                    System.out.println("上游车道ID: " + laneObj.fromLaneObject().id() +
//                            " 下游车道ID: " + laneObj.toLaneObject().id());
//                }
//            }
//        }
//
//        // 创建发车点
//        if (link1 != null) {
//            IDispatchPoint dp = netiface.createDispatchPoint(link1);
//            if (dp != null) {
//                dp.addDispatchInterval(1, 3600, 3600);
//            }
//        }
//        ISignalController signalController = netiface.createSignalController("信控1");
//        //TODO
//        // 创建信号灯组
//        ISignalPlan signalPlan = netiface.createSignalPlan(signalController,"信号灯组1", 60,80, 1, 3600);
//
//        // 创建相位,40秒绿灯，黄灯3秒，全红3秒
//        ArrayList<ColorInterval> colorIntervals = new ArrayList<>();
//        colorIntervals.add(new ColorInterval("G", 40));  // 绿灯
//        colorIntervals.add(new ColorInterval("Y", 3));   // 黄灯
//        colorIntervals.add(new ColorInterval("R", 3));   // 红灯
//
//        ISignalPhase signalPhase = netiface.createSignalPlanSignalPhase(signalPlan, "信号灯组1相位1", colorIntervals);
//
//        // 创建信号灯
//        for (int index = 0; index < lLaneObjects.size(); index++) {
//            ILaneObject laneObj = lLaneObjects.get(index);
//            netiface.createSignalLamp(
//                    signalPhase,
//                    "信号灯" + (index + 1),
//                    laneObj.fromLaneObject().id(),
//                    laneObj.toLaneObject().id(),
//                    m2p(2.0)
//            );
//        }
//    }
//
//    // 双环信控方案下发
//    public void doubleRingSignalControl(long currentSimuTime) {
//        // 获取TESS NG接口
//        TessInterface iface = tessngIFace();
//        // 获取路网子接口
//        NetInterface netiface = iface.netInterface();
//
//        // 读取方案数据
//        Gson gson = new Gson();
//        try (FileReader reader = new FileReader("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\others\\SecondaryDevCasesMaster\\Data\\Signal_Plan_Data_1109.json")) {
//            // 解析JSON数据
//            Map<String, Map<String, Object>> signalPlansDict =
//                    gson.fromJson(reader, new TypeToken<Map<String, Map<String, Object>>>(){}.getType());
//
//            // 创建信号灯组和相位
//            for (Map.Entry<String, Map<String, Object>> groupEntry : signalPlansDict.entrySet()) {
//                String planName = groupEntry.getKey();
//                Map<String, Object> groupData = groupEntry.getValue();
//
//                // 查找当前灯组
//                ISignalPlan currentSignalPlan = null;
//                ArrayList<ISignalPlan> allSignalPlans = netiface.signalPlans();
//                for (ISignalPlan signalPlan : allSignalPlans) {
//                    if (signalPlan.name().equals(planName)) {
//                        currentSignalPlan = signalPlan;
//                        break;
//                    }
//                }
//
//                if (currentSignalPlan == null) {
//                    System.out.println("FindError: The signalGroup not in current net.");
//                    break;
//                }
//
//                // 获取当前灯组的所有相位
//                List<ISignalPhase> currentSignalGroupPhases = currentSignalPlan.phases();
//
//                // 获取所有灯组的起始时间
//                Set<String> signalPlanstartTimeSet = groupData.keySet();
//                List<String> signalPlanstartTimeList = new ArrayList<>(signalPlanstartTimeSet);
//
//                // 处理每个时间段的信号方案
//                for (int index = 0; index < signalPlanstartTimeList.size(); index++) {
//                    String startTimeStr = signalPlanstartTimeList.get(index);
//                    String endTimeStr = (index != signalPlanstartTimeList.size() - 1)
//                            ? signalPlanstartTimeList.get(index + 1)
//                            : "24:00";
//
//                    // 转换时间为秒
//                    long startTimeSeconds = functions.timeToSeconds(startTimeStr);
//                    long endTimeSeconds = functions.timeToSeconds(endTimeStr);
//
//                    // 检查当前仿真时间是否在该时段内
//                    if (startTimeSeconds <= currentSimuTime && currentSimuTime < endTimeSeconds) {
//                        Map<String, Object> timeSegmentData = (Map<String, Object>) groupData.get(startTimeStr);
//                        int periodTime = ((Number) timeSegmentData.get("cycle_time")).intValue();
//                        List<Map<String, Object>> phases = (List<Map<String, Object>>) timeSegmentData.get("phases");
//
//                        // 修改周期时间
//                        currentSignalPlan.setCycleTime(periodTime);
//
//                        // 处理每个相位
//                        for (Map<String, Object> phase : phases) {
//                            String phaseName = (String) phase.get("phase_name");
//                            int phaseNumber = ((Number) phase.get("phase_number")).intValue();
//
//                            // 创建灯色序列
//                            List<ColorInterval> colorList = new ArrayList<>();
//                            colorList.add(new ColorInterval("红", ((Number) phase.get("start_time")).intValue()));
//                            colorList.add(new ColorInterval("绿", ((Number) phase.get("green_time")).intValue()));
//                            colorList.add(new ColorInterval("黄", 3));
//
//                            // 计算剩余红灯时间
//                            int remainingRedTime = periodTime
//                                    - ((Number) phase.get("start_time")).intValue()
//                                    - ((Number) phase.get("green_time")).intValue()
//                                    - 3;
//
//                            if (remainingRedTime > 0) {
//                                colorList.add(new ColorInterval("红", remainingRedTime));
//                            }
//
//                            // 查找当前相位
//                            ISignalPhase currentPhase = null;
//                            for (ISignalPhase signalPhase : currentSignalGroupPhases) {
//                                if (phaseNumber == Integer.parseInt(signalPhase.number())) {
//                                    currentPhase = signalPhase;
//                                    break;
//                                }
//                            }
//
//                            // 修改或创建相位
//                            if (currentPhase != null) {
//                                currentPhase.setColorList(colorList);
//                            } else {
//                                ISignalPhase newPhase = netiface.createSignalPhase(currentSignalGroup, phaseName, colorList);
//                                newPhase.setNumber(String.valueOf(phaseNumber));
//                                currentSignalGroupPhases.add(newPhase);
//                            }
//
//                            // 设置相位包含的信号灯
//                            List<Integer> lampIdList = (List<Integer>) phase.get("lamp_lst");
//                            for (int lampId : lampIdList) {
//                                ISignalLamp lamp = netiface.findSignalLamp(lampId);
//                                if (lamp != null) {
//                                    lamp.setPhaseNumber(String.valueOf(phaseNumber));
//                                } else {
//                                    System.out.println("FindError:未查找到信号灯: " + lampId);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException | FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
