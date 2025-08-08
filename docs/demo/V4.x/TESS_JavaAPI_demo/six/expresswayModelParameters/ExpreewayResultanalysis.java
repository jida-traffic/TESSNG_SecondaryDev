package test002.TESS_JavaAPI_demo.six.expresswayModelParameters;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ExpreewayResultanalysis {
//    String filePathSimu = ".\\SimuResult\\芜湖-初始模型_ver2_3600s.tess\\20250805101721";

//    public static void main(String[] args) {
//        String filePathSimu = "C:\\TESSNG_4.0.20\\SecondDev\\TESS_PythonAPI_DEMO\\6.模型参数标定\\交叉口模型参数标定\\SimuResult\\芜湖-初始模型_ver2_3600s.tess\\20250805101721";
//        Map<String, Double> re = resultAnalysis(filePathSimu);
//        System.out.println(re);
//    }

    private static final int NUM_COILS = 5;
    private static final int NUM_TIME_SLOTS = 48;

    public static List<Double> resultAnalysis(String filePathSimu, String filePathReal) throws IOException {
        // 读取现实路况数据
        RealMatrix realData = readRealData(filePathReal);
        RealMatrix SCM_r = realData.getSubMatrix(0, 4, 5, 9);  // 速度数据
        RealMatrix V = realData.getSubMatrix(0, 4, 0, 4);      // 流量数据

        // 初始化仿真矩阵
        RealMatrix SCM_s = MatrixUtils.createRealMatrix(NUM_COILS, NUM_TIME_SLOTS);
        RealMatrix E = MatrixUtils.createRealMatrix(NUM_COILS, NUM_TIME_SLOTS);

        // 线圈配置
        int[][] noCluster = {{0, 3, 4}, {5, 6}, {7, 8}, {9, 10}, {1, 2}};

        // 处理每个线圈
        for (int i = 0; i < NUM_COILS; i++) {
            RealMatrix temp = MatrixUtils.createRealMatrix(NUM_TIME_SLOTS, 3);

            for (int collector : noCluster[i]) {
                String filePath = Paths.get(filePathSimu, getSimulationFileName(collector)).toString();
                RealMatrix data = readSimulationData(filePath);

                // 累加流量和速度*流量
                for (int j = 0; j < NUM_TIME_SLOTS; j++) {
                    temp.setEntry(j, 1, temp.getEntry(j, 1) + data.getEntry(j, 1)); // flow
                    temp.setEntry(j, 2, temp.getEntry(j, 2) +
                            data.getEntry(j, 0) * data.getEntry(j, 1)); // speed * flow
                }
            }

            // 计算平均速度
            for (int j = 0; j < NUM_TIME_SLOTS; j++) {
                double avgSpeed = temp.getEntry(j, 2) / temp.getEntry(j, 1);
                SCM_s.setEntry(i, j, avgSpeed);
                E.setEntry(i, j, temp.getEntry(j, 1));
            }
        }

        // 计算指标
        RealMatrix BSCM_s = thresholdMatrix(SCM_s, 45);
        RealMatrix BSCM_r = thresholdMatrix(SCM_r, 45);

        double C1 = calculateC1(BSCM_s, BSCM_r);
        double C2 = calculateC2(BSCM_s, BSCM_r, SCM_s, SCM_r);
        double GEH = calculateGEH(E, V);
        double DevS = calculateDevS(SCM_s, SCM_r);

        System.out.println("C1: " + C1);
        System.out.println("C2: " + C2);
        System.out.println("GEH: " + GEH);
        System.out.println("DevS: " + DevS);

        return Arrays.asList(C1, C2, GEH, DevS);
    }

    private static RealMatrix readRealData(String filePath) throws IOException {
        List<double[]> data = Files.lines(Paths.get(filePath))
                .skip(1) // 跳过标题
                .map(line -> Arrays.stream(line.split(","))
                        .mapToDouble(Double::parseDouble)
                        .toArray())
                .collect(Collectors.toList());

        return MatrixUtils.createRealMatrix(data.toArray(new double[0][]));
    }

    private static RealMatrix readSimulationData(String filePath) throws IOException {
        List<double[]> data = Files.lines(Paths.get(filePath))
                .skip(3) // 跳过前两行预热数据
                .map(line -> {
                    String[] parts = line.split(",");
                    return new double[]{
                            Double.parseDouble(parts[0]), // 平均车速
                            Double.parseDouble(parts[1])  // 车辆数
                    };
                })
                .limit(NUM_TIME_SLOTS)
                .collect(Collectors.toList());

        return MatrixUtils.createRealMatrix(data.toArray(new double[0][]));
    }

    private static String getSimulationFileName(int collector) {
        // 根据实际情况调整文件名获取逻辑
        return "collector_" + collector + ".csv";
    }

    private static RealMatrix thresholdMatrix(RealMatrix matrix, double threshold) {
        RealMatrix result = matrix.copy();
        for (int i = 0; i < result.getRowDimension(); i++) {
            for (int j = 0; j < result.getColumnDimension(); j++) {
                result.setEntry(i, j, result.getEntry(i, j) <= threshold ? 1 : 0);
            }
        }
        return result;
    }

    private static double calculateC1(RealMatrix BSCM_s, RealMatrix BSCM_r) {
        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i < BSCM_s.getRowDimension(); i++) {
            for (int j = 0; j < BSCM_s.getColumnDimension(); j++) {
                double sum = BSCM_s.getEntry(i, j) + BSCM_r.getEntry(i, j);
                if (sum == 2) {
                    numerator += 1;
                }
                denominator += sum / 2;
            }
        }

        return numerator / denominator;
    }

    private static double calculateC2(RealMatrix BSCM_s, RealMatrix BSCM_r,
                               RealMatrix SCM_s, RealMatrix SCM_r) {
        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i < BSCM_s.getRowDimension(); i++) {
            for (int j = 0; j < BSCM_s.getColumnDimension(); j++) {
                double sum = BSCM_s.getEntry(i, j) + BSCM_r.getEntry(i, j);
                if (sum > 0) {
                    numerator += Math.abs(SCM_s.getEntry(i, j) - SCM_r.getEntry(i, j));
                    denominator += (SCM_s.getEntry(i, j) + SCM_r.getEntry(i, j)) / 2;
                }
            }
        }

        return numerator / denominator;
    }

    private static double calculateGEH(RealMatrix E, RealMatrix V) {
        RealMatrix diff = E.subtract(V);
        RealMatrix squared = diff.multiply(diff);
        RealMatrix sum = E.add(V);
        RealMatrix gehMatrix = matrixDivide(squared.scalarMultiply(2),sum) ;

        // 计算85百分位数 - 简化版，实际需要更精确的实现
        double[] values = Arrays.stream(gehMatrix.getData())
                .flatMapToDouble(Arrays::stream)
                .toArray();
        Arrays.sort(values);
        int index = (int) Math.ceil(0.85 * values.length);
        return values[Math.min(index, values.length - 1)];
    }

    private static double calculateDevS(RealMatrix SCM_s, RealMatrix SCM_r) {
        RealMatrix diff = absMatrix(SCM_s.subtract(SCM_r));
        RealMatrix relativeDiff = matrixDivide(diff,SCM_r);
        return Arrays.stream(relativeDiff.getData())
                .flatMapToDouble(Arrays::stream)
                .average()
                .orElse(0);
    }

    private static RealMatrix absMatrix(RealMatrix matrix) {
        double[][] data = matrix.getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = Math.abs(data[i][j]);
            }
        }
        return MatrixUtils.createRealMatrix(data);
    }


    static RealMatrix matrixDivide(RealMatrix A, RealMatrix B) {
        LUDecomposition lu = new LUDecomposition(B);
        RealMatrix B_inverse = lu.getSolver().getInverse();
        return A.multiply(B_inverse);
    }
}
