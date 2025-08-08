package test002.TESS_JavaAPI_demo.four.VehicleSpeedGuidance.my_code;

import com.jidatraffic.tessng.*;

import java.io.*;
import java.util.*;

public class functions {
    // 从配置类获取参数（对应Python的from config import *）
    private static final boolean IS_GUIDANCE = config.IS_GUIDANCE;
    private static final double ACCELERATION = config.ACCELERATION;
    private static final double DECELERATION = config.DECELERATION;
    private static final int STOP_WEIGHT = config.STOP_WEIGHT;
    private static final int GUIDANCE_LENGTH = config.GUIDANCE_LENGTH;
    private static final int DEFAULT_DES_SPEED = config.DEFAULT_DES_SPEED;
    private static final int FROM_SPEED = config.FROM_SPEED;
    private static final int TO_SPEED = config.TO_SPEED;
    private static final int DES_GAP = config.DES_GAP;
    private static final List<Integer> LINKS_ID = config.LINKS_ID;
    private static final List<Integer> ALL_LINKS_ID = config.ALL_LINKS_ID;
    private static final Map<Integer, Double> LINKS_LENGTH_ADDED = config.LINKS_LENGTH_ADDED;
    private static final int SIGNAL_CYCLE = config.SIGNAL_CYCLE;
    private static final int SIGNAL_RED = config.SIGNAL_RED;
    private static final Map<Integer, Integer> SIGNAL_HEAD_OFFSET = config.SIGNAL_HEAD_OFFSET;

    /**
     * 计算匀速阶段的油耗
     *
     * @param v 速度(m/s)
     * @param d 位移(m)
     * @return 油耗
     */
    public static double calcFU(double v, double d) {
        if (v > 0) {
            return d * (0.0411 * (0.132 * v + 0.000302 * Math.pow(v, 3)) + 0.4629) / v;
        } else {
            return 0;
        }
    }

    /**
     * 计算变速阶段的油耗
     *
     * @param v0 初速度(m/s)
     * @param vt 末速度(m/s)
     * @return 油耗
     */
    public static double calcAccFU(double v0, double vt) {
        double fu = 0;
        double tt = 0;

        if (v0 < vt) {
            // 加速运动
            double a = ACCELERATION;
            double t = (vt - v0) / a;

            while (tt < t) {
                tt += 0.1;
                double v = v0 + a * tt;
                double d = (v + v0) / 2.0 * 0.1;
                fu += calcFU((v + v0) / 2.0, d);
            }
        } else {
            // 减速运动
            double a = DECELERATION;
            double t = (v0 - vt) / a;

            while (tt < t) {
                tt += 0.1;
                double v = v0 - a * tt;
                double d = (v + v0) / 2.0 * 0.1;
                fu += calcFU((v + v0) / 2.0, d);
            }
        }
        return fu;
    }

    /**
     * 计算停车油耗（含惩罚系数）
     *
     * @param finalSpeed 停车后恢复的目标速度(m/s)
     * @return 停车油耗
     */
    public static double calcStopFU(double finalSpeed) {
        return calcAccFU(0, finalSpeed) * STOP_WEIGHT;
    }

    /**
     * 计算车辆全程油耗
     *
     * @param speedList 每0.1s记录的速度列表(m/s)
     * @return 全程油耗
     */
    public static double calcWholeFU(List<Double> speedList) {
        double fu = 0.0;
        for (double v : speedList) {
            double d = v * 0.1;
            fu += calcFU(v, d);
        }
        return fu;
    }

    /**
     * 计算引导区间内的油耗、最终速度和所需时间
     *
     * @param currentSpeed 当前速度(m/s)
     * @param targetSpeed  目标速度(m/s)
     * @param ql           前方排队长度(m)
     * @return 包含油耗、最终速度、时间的数组
     */
    public static double[] calcSegmentFU(double currentSpeed, double targetSpeed, double ql) {
        double gl = GUIDANCE_LENGTH - ql;
        double finalSpeed = targetSpeed;
        double fu = 999.9;
        double travelTime = 999.9;

        if (currentSpeed > targetSpeed) {
            // 减速情形
            double t = (currentSpeed - targetSpeed) / DECELERATION;
            double s = (currentSpeed + targetSpeed) * t / 2;

            if (s <= gl) {
                travelTime = t + (gl - s) / finalSpeed;
                double fu1 = calcAccFU(currentSpeed, finalSpeed);
                double fu2 = calcFU(finalSpeed, gl - s);
                fu = fu1 + fu2;
            } else {
                finalSpeed = Math.sqrt(Math.pow(currentSpeed, 2) - 2 * DECELERATION * gl);
                travelTime = (currentSpeed - finalSpeed) / DECELERATION;
                fu = calcAccFU(currentSpeed, finalSpeed);
            }
        } else if (currentSpeed < targetSpeed) {
            // 加速情形
            double t = (targetSpeed - currentSpeed) / ACCELERATION;
            double s = (currentSpeed + targetSpeed) * t / 2;

            if (s <= gl) {
                travelTime = t + (gl - s) / finalSpeed;
                double fu1 = calcAccFU(currentSpeed, finalSpeed);
                double fu2 = calcFU(finalSpeed, gl - s);
                fu = fu1 + fu2;
            } else {
                finalSpeed = Math.sqrt(Math.pow(currentSpeed, 2) + 2 * ACCELERATION * gl);
                travelTime = (finalSpeed - currentSpeed) / ACCELERATION;
                fu = calcAccFU(currentSpeed, finalSpeed);
            }
        } else {
            // 匀速情形
            travelTime = gl / finalSpeed;
            fu = calcFU(finalSpeed, gl);
        }

        return new double[]{fu, finalSpeed, travelTime};
    }

    /**
     * 判断是否为红灯
     *
     * @param currentTime 当前时间(s)
     * @param t           行程时间(s)
     * @param linkId      路段ID
     * @return 是否红灯
     */
    public static boolean checkSignalHeadRed(double currentTime, double t, int linkId) {
        int offset = SIGNAL_HEAD_OFFSET.getOrDefault(linkId, 0);
        double cyclePos = (currentTime + t + SIGNAL_CYCLE - offset) % SIGNAL_CYCLE;
        return cyclePos <= SIGNAL_RED;
    }

    /**
     * 获取车辆在路网中的累计位移
     *
     * @param linkId 路段ID
     * @param vehPos 车辆在路段中的位置(m)
     * @return 累计位移(m)
     */
    public static double getRealVehPos(int linkId, double vehPos) {
        if (ALL_LINKS_ID.contains(linkId)) {
            return LINKS_LENGTH_ADDED.getOrDefault(linkId, 0.0) + vehPos;
        }
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netiface = iface.netInterface();
        SimuInterface simuInterface = iface.simuInterface();
        ArrayList<IVehicle> iVehicles = simuInterface.allVehiStarted();
        return 0;
    }

    /**
     * 获取路段排队长度
     *
     * @param vehsInfo    车辆信息列表
     * @param targetLinks 目标路段ID列表
     * @return 排队长度列表
     */
    public static List<Double> getQueueLength(ArrayList<IVehicle> vehsInfo, List<Integer> targetLinks) {
        List<Double> qlList = new ArrayList<>();
        for (int link : targetLinks) {
            int count = 0;
            for (IVehicle veh : vehsInfo) {
                double vehSpeed = veh.currSpeed() * 3.6; // 转换为km/h
                int vehLink = (int) veh.roadId();
                if (veh.roadIsLink()) {
                    vehLink += 100;
                }
                if (vehSpeed < 7.2 && vehLink == link) {
                    count++;
                }
            }
            // 计算排队长度（车长4.5m + 间隙1m，4车道，放大系数2）
            qlList.add(count * (4.5 + 1.0) / 4 * 2);
        }
        return qlList;
    }

    public static Object[] calcDatas(List<VehicleRecord> dataList, String target) {
        // 定义统计目标类型
        List<Integer> targetTypes = new ArrayList<>();
        if ("CV".equals(target)) {
            targetTypes.add(1);
        } else if ("ALL".equals(target)) {
            targetTypes.add(1);
            targetTypes.add(2);
        }

        List<Object[]> datasOutput = new ArrayList<>();
        double totalFu = 0.0;
        double totalFuWithStop = 0.0;
        double totalTravelTime = 0.0;
        int totalStopTimes = 0;

        // 获取唯一车辆ID
        Set<Integer> vehIds = new HashSet<>();
        for (VehicleRecord record : dataList) {
            vehIds.add(record.vehId);
        }
        int vehCount = vehIds.size();
        System.out.println("车辆总数: " + vehCount);

        // 遍历每辆车的数据
        for (int vehId : vehIds) {
            // 筛选当前车辆的所有记录
            List<VehicleRecord> vehRecords = new ArrayList<>();
            for (VehicleRecord record : dataList) {
                if (record.vehId == vehId) {
                    vehRecords.add(record);
                }
            }
            if (vehRecords.isEmpty()) continue;

            // 获取车辆类型（取最大值，与Python代码一致）
            int vehType = vehRecords.stream()
                    .mapToInt(r -> r.vehType)
                    .max()
                    .orElse(-1);
            if (!targetTypes.contains(vehType)) continue;

            // 判断是否走完全程（是否经过路段4）
            boolean completed = vehRecords.stream()
                    .anyMatch(r -> r.vehLink == 4);
            if (!completed) continue;

            // 过滤初始路段（只保留vehLink < 5的记录）
            List<VehicleRecord> filteredRecords = new ArrayList<>();
            for (VehicleRecord record : vehRecords) {
                if (record.vehLink < 5) {
                    filteredRecords.add(record);
                }
            }
            if (filteredRecords.isEmpty()) continue;

            // 计算行程时间
            double minTime = filteredRecords.stream()
                    .mapToDouble(r -> r.currentTime)
                    .min()
                    .orElse(0);
            double maxTime = filteredRecords.stream()
                    .mapToDouble(r -> r.currentTime)
                    .max()
                    .orElse(0);
            double travelTime = maxTime - minTime;
            totalTravelTime += travelTime;

            // 计算油耗
            List<Double> speedList = new ArrayList<>();
            for (VehicleRecord record : filteredRecords) {
                speedList.add(record.vehSpeed);
            }
            double fu = calcWholeFU(speedList);
            totalFu += fu;

            // 计算停车次数
            int stopTimes = 0;
            List<Integer> targetLinks = Arrays.asList(1, 2, 3);
            for (int link : targetLinks) {
                // 筛选当前路段的记录
                List<VehicleRecord> linkRecords = new ArrayList<>();
                for (VehicleRecord record : filteredRecords) {
                    if (record.vehLink == link) {
                        linkRecords.add(record);
                    }
                }
                if (linkRecords.isEmpty()) continue;

                // 计算最小速度
                double minSpeed = linkRecords.stream()
                        .mapToDouble(r -> r.vehSpeed)
                        .min()
                        .orElse(Double.MAX_VALUE);
                if (minSpeed < 0.1) {
                    stopTimes++;
                }
            }
            totalStopTimes += stopTimes;

            // 计算含停车惩罚的油耗
            double fuWithStop = fu + stopTimes * (STOP_WEIGHT - 1.0) * calcStopFU(DEFAULT_DES_SPEED / 3.6);
            totalFuWithStop += fuWithStop;

            // 存储结果
            datasOutput.add(new Object[]{vehId, fu, fuWithStop, travelTime, stopTimes});
        }

        return new Object[]{datasOutput, totalFu, totalFuWithStop, totalTravelTime, totalStopTimes};
    }

    private static void printStatistics(Object[] result, String label) {
        List<Object[]> dataList = (List<Object[]>) result[0];
        double totalFu = (double) result[1];
        double totalFuWithStop = (double) result[2];
        double totalTravelTime = (double) result[3];
        int totalStopTimes = (int) result[4];
        int count = dataList.size();

        if (count == 0) {
            System.out.println(label + "统计车辆数：0");
            return;
        }

        System.out.println(label + "统计车辆数：" + count);
        System.out.printf("%s车均油耗：%.2f ml%n", label, totalFu / count);
        System.out.printf("%s车均油耗（含停车惩罚）：%.2f ml%n", label, totalFuWithStop / count);
        System.out.printf("%s车均行程时间：%.2f s%n", label, totalTravelTime / count);
        System.out.printf("%s车均停车次数：%.2f%n", label, (double) totalStopTimes / count);
        System.out.println();
    }

    /**
     * 处理数据并保存结果（对应Python的CookDatas函数）
     */
    public static void cookDatas() {
        try {
            // 读取CSV数据并去重
            List<VehicleRecord> dataList = readAndDeduplicateCsv("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\four\\VehicleSpeedGuidance\\Data\\Data.csv");

            // 统计数据
            Object[] allResult = calcDatas(dataList, "ALL");
            Object[] cvResult = calcDatas(dataList, "CV");

            // 保存结果到CSV
            saveResult((List<Object[]>) allResult[0], "C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\four\\VehicleSpeedGuidance\\Data\\result_ALL.csv");
            saveResult((List<Object[]>) cvResult[0], "C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\four\\VehicleSpeedGuidance\\Data\\result_CV.csv");

            // 打印统计信息
            printStatistics(allResult, "所有车辆");
            printStatistics(cvResult, "CV车辆");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取CSV文件并按currentTime和vehID去重
     */
    private static List<VehicleRecord> readAndDeduplicateCsv(String csvFilePath) throws IOException {
        Map<String, VehicleRecord> uniqueRecords = new HashMap<>();
        BufferedReader reader = null;
        String line = null;
        int lineNumber = 0;

        try {
            reader = new BufferedReader(new FileReader(csvFilePath));
            // 读取表头行
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new ArrayList<>(); // 空文件直接返回
            }
            lineNumber++;

            // 解析表头，建立列名到索引的映射
            Map<String, Integer> columnMap = new HashMap<>();
            String[] headers = headerLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                columnMap.put(headers[i].trim(), i);
            }

            // 验证必要的列是否存在
            List<String> requiredColumns = Arrays.asList("vehID", "vehType", "vehLink", "currentTime", "vehSpeed");
            for (String col : requiredColumns) {
                if (!columnMap.containsKey(col)) {
                    throw new IllegalArgumentException("CSV文件缺少必要的列: " + col);
                }
            }

            // 读取数据行
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // 跳过空行
                }

                // 分割CSV行（处理可能包含空格的字段）
                String[] parts = line.split(",");
                if (parts.length < headers.length) {
                    System.err.println("警告: 第" + lineNumber + "行字段数量不足，已跳过");
                    continue;
                }

                // 解析字段
                VehicleRecord record = new VehicleRecord();
                try {
                    record.vehId = Integer.parseInt(parts[columnMap.get("vehID")].trim());
                    record.vehType = Integer.parseInt(parts[columnMap.get("vehType")].trim());
                    record.vehLink = Integer.parseInt(parts[columnMap.get("vehLink")].trim());
                    record.currentTime = Double.parseDouble(parts[columnMap.get("currentTime")].trim());
                    record.vehSpeed = Double.parseDouble(parts[columnMap.get("vehSpeed")].trim());
                } catch (NumberFormatException e) {
                    System.err.println("警告: 第" + lineNumber + "行数据格式错误，已跳过: " + e.getMessage());
                    continue;
                }

                // 构建去重键（保留时间小数点后3位）
                String key = String.format("%.3f_%d", record.currentTime, record.vehId);
                uniqueRecords.putIfAbsent(key, record);
            }
        } catch (IOException e) {
            throw new IOException("读取CSV文件失败: " + e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                }
            }
        }

        return new ArrayList<>(uniqueRecords.values());
    }


    /**
     * 保存统计结果到CSV文件
     */
    private static void saveResult(List<Object[]> data, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 写入表头
            writer.write("vehID,FU,FUwithStop,travelTime,stopTimes");
            writer.newLine();

            // 写入数据行
            for (Object[] row : data) {
                // 安全地获取数值，使用包装类的方法避免类型转换错误
                int vehID = ((Number) row[0]).intValue();
                double FU = ((Number) row[1]).doubleValue();
                double FUwithStop = ((Number) row[2]).doubleValue();
                double travelTime = ((Number) row[3]).doubleValue();
                double stopTimes = ((Number) row[4]).doubleValue();

                String line = String.format(
                        "%d,%.6f,%.6f,%.6f,%.1f",
                        vehID,
                        FU,
                        FUwithStop,
                        travelTime,
                        stopTimes
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // 车辆记录数据类
    private static class VehicleRecord {
        int vehId;
        int vehType;
        int vehLink;
        double currentTime;
        double vehSpeed;
    }

    /**
     * 寻找最优速度
     *
     * @param currentSpeed       当前速度(m/s)
     * @param currentTime        当前时间(s)
     * @param guidancePointCount 剩余引导点数量
     * @param linkId             路段ID
     * @param ql                 排队长度(m)
     * @return 最优速度(m / s)
     */
    public static double findOptimalSpeed(double currentSpeed, double currentTime, int guidancePointCount, int linkId, double ql) {
        // 超过3次引导返回默认速度
        if (guidancePointCount > 3) {
            return DEFAULT_DES_SPEED / 3.6;
        }

        int ranges = (TO_SPEED - FROM_SPEED) / DES_GAP + 1;
        double optimalSpeed = DEFAULT_DES_SPEED / 3.6;
        double minFU = 9999999.99;

        switch (guidancePointCount) {
            case 1:
                for (int i = 0; i < ranges; i++) {
                    double targetSpeed = (FROM_SPEED + i * DES_GAP) / 3.6;
                    double[] segResult = calcSegmentFU(currentSpeed, targetSpeed, ql);
                    double fu = segResult[0];
                    double t = segResult[2];

                    if (checkSignalHeadRed(currentTime, t, linkId)) {
                        fu += calcStopFU(DEFAULT_DES_SPEED / 3.6);
                    }

                    if (fu < minFU) {
                        minFU = fu;
                        optimalSpeed = targetSpeed;
                    }
                }
                break;

            case 2:
                for (int i = 0; i < ranges; i++) {
                    double target1 = (FROM_SPEED + i * DES_GAP) / 3.6;
                    double[] seg1 = calcSegmentFU(currentSpeed, target1, 0);
                    double fu1 = seg1[0];
                    double finalSpeed1 = seg1[1];
                    double t1 = seg1[2];

                    for (int j = 0; j < ranges; j++) {
                        double target2 = (FROM_SPEED + j * DES_GAP) / 3.6;
                        double[] seg2 = calcSegmentFU(finalSpeed1, target2, 0);
                        double totalFu = fu1 + seg2[0];
                        double totalTime = t1 + seg2[2];

                        if (checkSignalHeadRed(currentTime, totalTime, linkId)) {
                            totalFu += calcStopFU(DEFAULT_DES_SPEED / 3.6);
                        }

                        if (totalFu < minFU) {
                            minFU = totalFu;
                            optimalSpeed = target1;
                        }
                    }
                }
                break;

            // 处理引导次数3-6次的逻辑实现
            case 3:
                // 计算3次引导的最优速度
                for (int i = 0; i < ranges; i++) {
                    double targetSpeed1 = (FROM_SPEED + i * DES_GAP) / 3.6;
                    double[] seg1 = calcSegmentFU(currentSpeed, targetSpeed1, 0);
                    double fu1 = seg1[0];
                    double finalSpeed1 = seg1[1];
                    double t1 = seg1[2];

                    // 第二段计算
                    for (int j = 0; j < ranges; j++) {
                        double targetSpeed2 = (FROM_SPEED + j * DES_GAP) / 3.6;
                        double[] seg2 = calcSegmentFU(finalSpeed1, targetSpeed2, 0);
                        double fu2 = seg2[0];
                        double finalSpeed2 = seg2[1];
                        double t2 = seg2[2];

                        // 第三段计算
                        for (int k = 0; k < ranges; k++) {
                            double targetSpeed3 = (FROM_SPEED + k * DES_GAP) / 3.6;
                            double[] seg3 = calcSegmentFU(finalSpeed2, targetSpeed3, 0);
                            double totalFu = fu1 + fu2 + seg3[0];
                            double totalTime = t1 + t2 + seg3[2];

                            // 检查是否遇到红灯
                            if (checkSignalHeadRed(currentTime, totalTime, linkId)) {
                                totalFu += calcStopFU(DEFAULT_DES_SPEED / 3.6);
                            }

                            // 更新最优解
                            if (totalFu < minFU) {
                                minFU = totalFu;
                                optimalSpeed = targetSpeed1;
                            }
                        }
                    }
                }
                break;

            case 4:
                // 计算4次引导的最优速度
                for (int i = 0; i < ranges; i++) {
                    double targetSpeed1 = (FROM_SPEED + i * DES_GAP) / 3.6;
                    double[] seg1 = calcSegmentFU(currentSpeed, targetSpeed1, 0);
                    double fu1 = seg1[0];
                    double finalSpeed1 = seg1[1];
                    double t1 = seg1[2];

                    // 第二段计算
                    for (int j = 0; j < ranges; j++) {
                        double targetSpeed2 = (FROM_SPEED + j * DES_GAP) / 3.6;
                        double[] seg2 = calcSegmentFU(finalSpeed1, targetSpeed2, 0);
                        double fu2 = seg2[0];
                        double finalSpeed2 = seg2[1];
                        double t2 = seg2[2];

                        // 第三段计算
                        for (int k = 0; k < ranges; k++) {
                            double targetSpeed3 = (FROM_SPEED + k * DES_GAP) / 3.6;
                            double[] seg3 = calcSegmentFU(finalSpeed2, targetSpeed3, 0);
                            double fu3 = seg3[0];
                            double finalSpeed3 = seg3[1];
                            double t3 = seg3[2];

                            // 第四段计算
                            for (int l = 0; l < ranges; l++) {
                                double targetSpeed4 = (FROM_SPEED + l * DES_GAP) / 3.6;
                                double[] seg4 = calcSegmentFU(finalSpeed3, targetSpeed4, 0);
                                double totalFu = fu1 + fu2 + fu3 + seg4[0];
                                double totalTime = t1 + t2 + t3 + seg4[2];

                                // 检查是否遇到红灯
                                if (checkSignalHeadRed(currentTime, totalTime, linkId)) {
                                    totalFu += calcStopFU(DEFAULT_DES_SPEED / 3.6);
                                }

                                // 更新最优解
                                if (totalFu < minFU) {
                                    minFU = totalFu;
                                    optimalSpeed = targetSpeed1;
                                }
                            }
                        }
                    }
                }
                break;

            case 5:
                // 计算5次引导的最优速度
                for (int i = 0; i < ranges; i++) {
                    double targetSpeed1 = (FROM_SPEED + i * DES_GAP) / 3.6;
                    double[] seg1 = calcSegmentFU(currentSpeed, targetSpeed1, 0);
                    double fu1 = seg1[0];
                    double finalSpeed1 = seg1[1];
                    double t1 = seg1[2];

                    // 第二段计算
                    for (int j = 0; j < ranges; j++) {
                        double targetSpeed2 = (FROM_SPEED + j * DES_GAP) / 3.6;
                        double[] seg2 = calcSegmentFU(finalSpeed1, targetSpeed2, 0);
                        double fu2 = seg2[0];
                        double finalSpeed2 = seg2[1];
                        double t2 = seg2[2];

                        // 第三段计算
                        for (int k = 0; k < ranges; k++) {
                            double targetSpeed3 = (FROM_SPEED + k * DES_GAP) / 3.6;
                            double[] seg3 = calcSegmentFU(finalSpeed2, targetSpeed3, 0);
                            double fu3 = seg3[0];
                            double finalSpeed3 = seg3[1];
                            double t3 = seg3[2];

                            // 第四段计算
                            for (int l = 0; l < ranges; l++) {
                                double targetSpeed4 = (FROM_SPEED + l * DES_GAP) / 3.6;
                                double[] seg4 = calcSegmentFU(finalSpeed3, targetSpeed4, 0);
                                double fu4 = seg4[0];
                                double finalSpeed4 = seg4[1];
                                double t4 = seg4[2];

                                // 第五段计算
                                for (int n = 0; n < ranges; n++) {
                                    double targetSpeed5 = (FROM_SPEED + n * DES_GAP) / 3.6;
                                    double[] seg5 = calcSegmentFU(finalSpeed4, targetSpeed5, 0);
                                    double totalFu = fu1 + fu2 + fu3 + fu4 + seg5[0];
                                    double totalTime = t1 + t2 + t3 + t4 + seg5[2];

                                    // 检查是否遇到红灯
                                    if (checkSignalHeadRed(currentTime, totalTime, linkId)) {
                                        totalFu += calcStopFU(DEFAULT_DES_SPEED / 3.6);
                                    }

                                    // 更新最优解
                                    if (totalFu < minFU) {
                                        minFU = totalFu;
                                        optimalSpeed = targetSpeed1;
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            case 6:
                // 计算6次引导的最优速度
                for (int i = 0; i < ranges; i++) {
                    double targetSpeed1 = (FROM_SPEED + i * DES_GAP) / 3.6;
                    double[] seg1 = calcSegmentFU(currentSpeed, targetSpeed1, 0);
                    double fu1 = seg1[0];
                    double finalSpeed1 = seg1[1];
                    double t1 = seg1[2];

                    // 第二段计算
                    for (int j = 0; j < ranges; j++) {
                        double targetSpeed2 = (FROM_SPEED + j * DES_GAP) / 3.6;
                        double[] seg2 = calcSegmentFU(finalSpeed1, targetSpeed2, 0);
                        double fu2 = seg2[0];
                        double finalSpeed2 = seg2[1];
                        double t2 = seg2[2];

                        // 第三段计算
                        for (int k = 0; k < ranges; k++) {
                            double targetSpeed3 = (FROM_SPEED + k * DES_GAP) / 3.6;
                            double[] seg3 = calcSegmentFU(finalSpeed2, targetSpeed3, 0);
                            double fu3 = seg3[0];
                            double finalSpeed3 = seg3[1];
                            double t3 = seg3[2];

                            // 第四段计算
                            for (int l = 0; l < ranges; l++) {
                                double targetSpeed4 = (FROM_SPEED + l * DES_GAP) / 3.6;
                                double[] seg4 = calcSegmentFU(finalSpeed3, targetSpeed4, 0);
                                double fu4 = seg4[0];
                                double finalSpeed4 = seg4[1];
                                double t4 = seg4[2];

                                // 第五段计算
                                for (int n = 0; n < ranges; n++) {
                                    double targetSpeed5 = (FROM_SPEED + n * DES_GAP) / 3.6;
                                    double[] seg5 = calcSegmentFU(finalSpeed4, targetSpeed5, 0);
                                    double fu5 = seg5[0];
                                    double finalSpeed5 = seg5[1];
                                    double t5 = seg5[2];

                                    // 第六段计算
                                    for (int o = 0; o < ranges; o++) {
                                        double targetSpeed6 = (FROM_SPEED + o * DES_GAP) / 3.6;
                                        double[] seg6 = calcSegmentFU(finalSpeed5, targetSpeed6, 0);
                                        double totalFu = fu1 + fu2 + fu3 + fu4 + fu5 + seg6[0];
                                        double totalTime = t1 + t2 + t3 + t4 + t5 + seg6[2];

                                        // 检查是否遇到红灯
                                        if (checkSignalHeadRed(currentTime, totalTime, linkId)) {
                                            totalFu += calcStopFU(DEFAULT_DES_SPEED / 3.6);
                                        }

                                        // 更新最优解
                                        if (totalFu < minFU) {
                                            minFU = totalFu;
                                            optimalSpeed = targetSpeed1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;

            default:
                optimalSpeed = DEFAULT_DES_SPEED / 3.6;
        }

        return optimalSpeed;
    }

}