package TESS_Java_APIDemo.four.VehicleSpeedGuidance.TESSNG_code;


import TESS_Java_APIDemo.four.VehicleSpeedGuidance.my_code.config;
import TESS_Java_APIDemo.four.VehicleSpeedGuidance.my_code.functions;
import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.ObjBool;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

import static com.jidatraffic.tessng.TESSNG.m2p;
import static com.jidatraffic.tessng.TESSNG.p2m;


public class MySimulator extends CustomerSimulator {
    // 监听器列表，替代QT信号

    // 存储速度引导信息
    private Map<Integer, Double> speedGuidance = new HashMap<>();
    // 存储仿真数据
    private List<Object[]> datas = new ArrayList<>();

    // 配置参数（对应config中的定义）
    private static final double SIM_TIME = config.SIM_TIME;
    private static final boolean IS_GUIDANCE = config.IS_GUIDANCE;
    private static final List<Integer> LINKS_ID = config.LINKS_ID;
    private static final List<Double> SIGNAL_HEADS_POS = config.SIGNAL_HEADS_POS;
    private static final List<List<Double>> GUIDANCE_POINTS = config.GUIDANCE_POINTS;
    private static final double DEFAULT_DES_SPEED = config.DEFAULT_DES_SPEED;
    private static final DecimalFormat df = new DecimalFormat("#.0");

    public MySimulator() {
        super();// 初始化逻辑
    }


    // 仿真开始前
    @Override
    public void beforeStart(ObjBool keepOn) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        simuiface.setAcceMultiples(5);

    }

    @Override
    public void afterOneStep() {
        // TESSNG 顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        // TESSNG 仿真子接口
        SimuInterface simuiface = iface.simuInterface();
        // TESSNG 路网子接口
        NetInterface netiface = iface.netInterface();

        // 当前已仿真时间，单位：毫秒
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();

        // 如果仿真时间大于等于最长时间，通知停止仿真
//        if (simuTime >= SIM_TIME * 1000) {
//            for (StopSimuListener listener : stopSimuListeners) {
//                listener.onStopSimu();
//            }
//        }

        // 速度引导--------------------------------------------------------------

        // 获取当前仿真时刻
        double currentTime = Math.round((simuTime / 1000.0) * 10) / 10.0;

        // 当前正在运行车辆列表
        ArrayList<IVehicle> lAllVehi = simuiface.allVehiStarted();

        if (lAllVehi.isEmpty()) {
            return;
        }

        // 获取目标路段排队信息
        List<Double> queueLengthList = functions.getQueueLength(lAllVehi, LINKS_ID);

        // 遍历每一辆车
        for (IVehicle vehi : lAllVehi) {
            int vehID = (int)(vehi.id() % 100000); // 获取车辆编号
            int vehType = (int) vehi.vehicleTypeCode(); // 获取车辆类型
            double vehPos = p2m(vehi.vehicleDriving().currDistanceInRoad()); // 获取车辆位置
            double vehSpeed = p2m(vehi.currSpeed());  // 获取车辆速度(m/s)
            boolean isLink = vehi.roadIsLink();
            int vehLink = (int)vehi.roadId();
            if (!isLink) {
                vehLink += 100;
            }
            double vehDesSpeed = p2m(vehi.vehicleDriving().desirSpeed()) * 3.6; // 获取车辆期望速度(km/h)

            // 添加数据到列表中，仿真结束后统计
            double realVehPos = functions.getRealVehPos(vehLink, vehPos); // 获取车辆在路网的累计行驶位移
            datas.add(new Object[]{
                    currentTime, vehID, vehType, vehSpeed,
                    vehLink, vehPos, vehDesSpeed, realVehPos
            });

            // 设置车辆初始颜色
            if (vehPos < 5 && vehLink == 1) {
                if (vehType == 2) {
                    vehi.setColor("#FFA500"); // 橙色：普通车
                } else {
                    vehi.setColor("#FFFFFF"); // 白色：CV车(非引导状态)
                }
            }

            // 如果不启用引导，跳过后续逻辑
            if (!IS_GUIDANCE) {
                continue;
            }

            // 不引导普通车
            if (vehType == 2) {
                continue;
            }

            // 检查该车辆是否在目标路段上
            if (!LINKS_ID.contains(vehLink)) {
                continue;
            }

            double signalHeadPos = 0;
            List<Double> guidancePoint = new ArrayList<>();
            int guidancePointCount = 0;
            double ql = 0; // 当前排队长度

            // 获取所在路段的引导配置信息
            for (int j = 0; j < LINKS_ID.size(); j++) {
                if (vehLink == LINKS_ID.get(j)) {
                    signalHeadPos = SIGNAL_HEADS_POS.get(j);
                    guidancePoint = GUIDANCE_POINTS.get(j);
                    guidancePointCount = guidancePoint.size();
                    ql = queueLengthList.get(j);
                    break;
                }
            }

            // 判断车辆是否已经驶过信号灯
            if (vehPos > signalHeadPos) {
                // 恢复颜色和默认速度
                vehi.setColor("#FFFFFF");
                speedGuidance.put(vehID, DEFAULT_DES_SPEED / 3.6); // (m/s)
                continue;
            }

            // 判断该车是否在引导点附近1米处
            for (int j = 0; j < guidancePointCount; j++) {
                double gp = guidancePoint.get(j); // 获取引导点位置
                // 如果车辆进入引导点附近1米内
                if (Math.abs(vehPos - gp) < 1) {
                    // 计算还剩几个引导点
                    int restGpCount = j + 1;
                    // 进行速度引导，寻找最佳速度
                    double guidanceSpeed = functions.findOptimalSpeed(
                            vehSpeed, currentTime, restGpCount, vehLink, ql); // (m/s)

                    // 设置车辆的期望速度
                    speedGuidance.put(vehID, guidanceSpeed);

                    // 更改车辆的颜色
                    if (Math.abs(guidanceSpeed * 3.6 - vehDesSpeed) < 0.1) {
                        continue; // 速度不变，保持当前颜色
                    }

                    if (guidanceSpeed >= vehSpeed || guidanceSpeed * 3.6 >= 80.0) {
                        vehi.setColor("#D92626"); // 红色：CV车(提速状态)
                    } else {
                        vehi.setColor("#00FF07"); // 绿色：CV车(降速状态)
                    }
                    break;
                }
            }
        }

        // 每1秒发送一次运行信息
        if (currentTime % 1 == 0) {
            String strLinkCount = String.valueOf(netiface.linkCount());
            String strVehiCount = String.valueOf(lAllVehi.size());
            String strSimuTime = df.format(simuTime / 1000.0);
            String runInfo = String.format("运行车辆数：%s\n仿真时间：%s(秒)\n\n%s",
                    strVehiCount, strSimuTime, queueLengthList);


        }
    }

    // 重新计算期望速度
    @Override
    public boolean reCalcdesirSpeed(IVehicle vehi, ObjReal inOutDesirSpeed) {
        int vehID = (int)(vehi.id() % 100000);
        if (speedGuidance.containsKey(vehID)) {
            inOutDesirSpeed.setValue(m2p(speedGuidance.get(vehID)));
            return true;
        }
        return false;
    }

    // 仿真结束后
    @Override
    public void afterStop() {
        try {
            // 保存数据到CSV文件
            saveDatasToCsv("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\four\\VehicleSpeedGuidance\\Data\\Data.csv");
            // 处理数据
            functions.cookDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 保存仿真数据到CSV
    private void saveDatasToCsv(String filePath) throws IOException {
        File directory = new File(filePath).getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        System.out.println(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入表头
            writer.write("currentTime,vehID,vehType,vehSpeed,vehLink,vehPos,desSpeed,realVehPos");
            writer.newLine();

            // 写入数据
            DecimalFormat numFormat = new DecimalFormat("#.000000");
            for (Object[] row : datas) {
                String line = String.format("%s,%d,%d,%s,%d,%s,%s,%s",
                        numFormat.format(row[0]),  // currentTime
                        (int)row[1],                // vehID
                        (int)row[2],                // vehType
                        numFormat.format(row[3]),   // vehSpeed
                        (int)row[4],                // vehLink
                        numFormat.format(row[5]),   // vehPos
                        numFormat.format(row[6]),   // desSpeed
                        numFormat.format(row[7])    // realVehPos
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // 计算车辆当前限制车道序号列表
    @Override
    public ArrayList<Integer> calcLimitedLaneNumber(IVehicle vehi) {
        int laneNumber = vehi.vehicleDriving().laneNumber();
        Set<Integer> allLanes = new HashSet<>(Arrays.asList(0, 1, 2, 3));
        allLanes.remove(laneNumber);
        return new ArrayList<>(allLanes);
    }

}
    