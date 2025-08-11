package six.IntersectionModelParameters;

import java.io.*;
import java.util.*;

public class resultanalysis {
    String filePathSimu = ".\\SimuResult\\芜湖-初始模型_ver2_3600s.tess\\20250805101721";

    // 真实数据
    private static final Map<String, Double> queueReal = new HashMap<String, Double>() {{
        put("东直", 97.5);
        put("东左", 58.5);
        put("西直", 78.0);
        put("西左", 19.5);
        put("南直", 71.5);
        put("南左", 65.0);
        put("北直", 169.0);
        put("北左", 110.5);
    }};

    // 检测器ID映射
    private static final Map<String, String[]> queueDetector = new HashMap<String, String[]>() {{
        put("东直", new String[]{"排队计数器_集计数据_东直1(ID11).csv", "排队计数器_集计数据_东直2(ID10).csv", "排队计数器_集计数据_东直3(ID9).csv"});
        put("东左", new String[]{"排队计数器_集计数据_东左1(ID29).csv", "排队计数器_集计数据_东左2(ID28).csv"});
        put("西直", new String[]{"排队计数器_集计数据_西直1(ID1).csv", "排队计数器_集计数据_西直2(ID3).csv"});
        put("西左", new String[]{"排队计数器_集计数据_西左1(ID101).csv", "排队计数器_集计数据_西左2(ID2).csv"});
        put("南直", new String[]{"排队计数器_集计数据_南直1(ID21).csv", "排队计数器_集计数据_南直2(ID22).csv", "排队计数器_集计数据_南直3(ID23).csv"});
        put("南左", new String[]{"排队计数器_集计数据_南左(ID20).csv"});
        put("北直", new String[]{"排队计数器_集计数据_北直1(ID16).csv", "排队计数器_集计数据_北直2(ID15).csv", "排队计数器_集计数据_北直3(ID14).csv"});
        put("北左", new String[]{"排队计数器_集计数据_北左1(ID19).csv", "排队计数器_集计数据_北左2(ID18).csv", "排队计数器_集计数据_北左3(ID17).csv"});
    }};

    public static void main(String[] args) {
        String filePathSimu = "C:\\TESSNG_4.0.20\\SecondDev\\TESS_PythonAPI_DEMO\\6.模型参数标定\\交叉口模型参数标定\\SimuResult\\芜湖-初始模型_ver2_3600s.tess\\20250805101721";
        Map<String, Double> re = resultAnalysis(filePathSimu);
        System.out.println(re);
    }

    public static Map<String, Double> resultAnalysis(String filePathSimu) {
        Map<String, Double> re = new HashMap<>();

        for (Map.Entry<String, String[]> entry : queueDetector.entrySet()) {
            String direction = entry.getKey();
            String[] detectorFiles = entry.getValue();

            double sum = 0;
            int count = 0;

            for (String fileName : detectorFiles) {
                String filePath = filePathSimu + File.separator + fileName;
                try {
                    double avgQueueLength = readAndCalculateAverage(filePath, 3); // 第4列(0-based索引为3)
                    sum += avgQueueLength;
                    count++;
                } catch (IOException e) {
                    System.err.println("Error reading file: " + filePath);
                    e.printStackTrace();
                }
            }

            if (count > 0) {
                double simu = sum / count;
                double real = queueReal.get(direction);
                double relativeError = Math.abs(real - simu) / real * 100;
                re.put(direction, relativeError);
            }
        }

        return re;
    }

    private static double readAndCalculateAverage(String filePath, int columnIndex) throws IOException {
        double sum = 0;
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // 跳过第一行(标题行)

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (columnIndex < values.length) {
                    try {
                        double value = Double.parseDouble(values[columnIndex].trim());
                        sum += value;
                        count++;
                    } catch (NumberFormatException e) {
                        // 忽略格式错误的行
                    }
                }
            }
        }

        return count > 0 ? sum / count : 0;
    }
}
