package Traffic_Signal_Control.Multitime_Period_And_Multischeme_Signal_Control;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.ColorInterval;
import javax.json.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class MyNet extends JCustomerNet {
    private TessInterface iface;
    private NetInterface netiface;
    private SimuInterface simuiface;
    private Map<Integer, Integer> signalControllerIdMapping = new HashMap<>();
    private Map<Integer, Integer> signalPhaseIdMapping = new HashMap<>();
    private static final String CONFIG_FILE_PATH = "D:\\Yangzhenbei\\Java_demo\\src\\Traffic_Signal_Control\\Multitime_Period_And_Multischeme_Signal_Control\\light_data.json";
    public MyNet() {
        super();
    }

    @Override
    public void afterLoadNet() {
        try {
            iface = TESSNG.tessngIFace();
            netiface = iface.netInterface();
            simuiface = iface.simuInterface();
            // 读取JSON配置文件
            JsonReader jsonReader = Json.createReader(new FileReader(CONFIG_FILE_PATH));
            JsonObject lightData = jsonReader.readObject();
            jsonReader.close();
            System.out.println("读取JSON配置文件成功:" + CONFIG_FILE_PATH);
//            // 创建信号机、信控方案和信号相位
            JsonArray signalControllers = lightData.getJsonArray("signalControllers");
            for (JsonValue controllerVal : signalControllers) {
                JsonObject controllerData = controllerVal.asJsonObject();
                int signalControllerId = controllerData.getInt("id");
                String signalControllerName = controllerData.getString("name");

                // 创建信号机
                ISignalController signalController = netiface.createSignalController(signalControllerName);
                signalControllerIdMapping.put(signalControllerId, (int) signalController.id());
                System.out.printf("创建了信号机，id=%d%n", signalController.id());

                // 处理信控方案
                JsonArray signalPlans = controllerData.getJsonArray("signalPlans");
                for (JsonValue planVal : signalPlans) {
                    JsonObject planData = planVal.asJsonObject();
                    int signalPlanId = planData.getInt("id");
                    String signalPlanName = planData.getString("name");
                    int cycleTime = planData.getInt("cycleTime");
                    int startTime = planData.getInt("startTime");
                    int endTime = planData.getInt("endTime");
                    int offset = planData.getInt("offset");

                    // 创建信控方案
                    ISignalPlan signalPlan = netiface.createSignalPlan(
                            signalController,
                            signalPlanName,
                            cycleTime,
                            offset,
                            startTime,
                            endTime
                    );
                    System.out.printf("创建了信控方案，id=%d%n", signalPlan.id());

                    // 处理信号相位
                    JsonArray signalPhases = planData.getJsonArray("signalPhases");
                    for (JsonValue phaseVal : signalPhases) {
                        JsonObject phaseData = phaseVal.asJsonObject();
                        int signalPhaseId = phaseData.getInt("id");
                        String signalPhaseName = phaseData.getString("name");

                        // 解析信号灯色和时长
                        JsonArray colorsArr = phaseData.getJsonArray("colors");
                        JsonArray durationsArr = phaseData.getJsonArray("durations");
                        ArrayList<ColorInterval> colorIntervals = new ArrayList<>();

                        for (int i = 0; i < colorsArr.size(); i++) {
                            String color = colorsArr.getString(i);
                            int duration = durationsArr.getInt(i);
                            colorIntervals.add(new ColorInterval(color, duration));
                        }

                        // 创建信号相位
                        ISignalPhase signalPhase = netiface.createSignalPlanSignalPhase(
                                signalPlan,
                                signalPhaseName,
                                colorIntervals
                        );
                        signalPhaseIdMapping.put(signalPhaseId, (int) signalPhase.id());
                        System.out.printf("创建了信号相位，id=%d%n", signalPhase.id());
                    }
                }
            }
            System.out.println();

            // 创建信号灯头
            JsonArray signalLamps = lightData.getJsonArray("signalLamps");
            for (JsonValue lampVal : signalLamps) {
                JsonObject lampData = lampVal.asJsonObject();
                String signalLampName = lampData.getString("name");

                // 解析关联的路段ID
                JsonArray linkIdsArr = lampData.getJsonArray("linkIds");
                List<Integer> linkIds = new ArrayList<>();
                for (JsonValue linkVal : linkIdsArr) {
                    linkIds.add(((JsonNumber)linkVal).intValue());
                }

                // 获取信号机ID映射
                int signalControllerId = lampData.getInt("signalControllerId");
                int signalControllerTessId = signalControllerIdMapping.get(signalControllerId);
                ISignalController signalController = netiface.findSignalControllerById(signalControllerTessId);

                // 解析关联的相位ID
                JsonArray phaseIdsArr = lampData.getJsonArray("signalPhaseIds");
                List<Integer> signalPhaseTessIds = new ArrayList<>();
                for (JsonValue phaseVal : phaseIdsArr) {
                    int phaseId = ((JsonNumber) phaseVal).intValue();
                    signalPhaseTessIds.add(signalPhaseIdMapping.get(phaseId));
                }

                // 为每个路段的车道创建信号灯头
                for (int linkId : linkIds) {
                    ILink link = netiface.findLink(linkId);
                    for (ILane lane : link.lanes()) {
                        long laneId = lane.id();
                        double laneLength = TESSNG.p2m(lane.length());
                        double dist = TESSNG.m2p(laneLength - 1);

                        // 创建信号灯头
                        ISignalLamp signalLamp = netiface.createTrafficSignalLamp(
                                signalController,
                                signalLampName,
                                laneId,
                                -1,
                                dist
                        );
                        System.out.printf("创建了信号灯头，id=%d%n", signalLamp.id());

                        // 关联信号相位
                        for (int phaseTessId : signalPhaseTessIds) {
                            netiface.addSignalPhaseToLamp(phaseTessId, signalLamp);
                        }
                    }
                }
            }


        } catch (IOException e) {
            System.err.println("读取配置文件失败: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonException e) {
            System.err.println("解析JSON数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }



}