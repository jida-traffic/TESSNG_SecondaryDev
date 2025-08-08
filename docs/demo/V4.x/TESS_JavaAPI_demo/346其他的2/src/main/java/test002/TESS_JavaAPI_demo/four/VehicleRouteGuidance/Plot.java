package test002.TESS_JavaAPI_demo.four.VehicleRouteGuidance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Plot {

    /**
     * 绘制时间-流量比柱状图（对应Python的plot_traffic_ratios）
     * @param times 时间列表
     * @param ratiosList 流量比列表（每个元素为[右转比例, 左转比例, 直行比例]）
     */
    public static void plotTrafficRatios(List<Double> times, List<List<Double>> ratiosList) {
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] directions = {"Right", "Left", "Straight"};

        for (int i = 0; i < times.size(); i++) {
            double time = times.get(i);
            List<Double> ratios = ratiosList.get(i);
            for (int dirIdx = 0; dirIdx < 3; dirIdx++) {
                dataset.addValue(ratios.get(dirIdx), directions[dirIdx], String.valueOf(time));
            }
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                "Traffic Ratios Over Time",  // 标题
                "Time",                      // X轴标签
                "Traffic Ratios",            // Y轴标签
                dataset,                     // 数据集
                PlotOrientation.VERTICAL,    // 方向
                true,                        // 显示图例
                true,                        // 工具提示
                false                        // URL链接
        );

        // 设置柱子宽度
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.05);  // 调整柱子间距

        // 保存图表
        saveChart(chart, "300s_interval_Time2TrafficRatios.png");
    }

    /**
     * 绘制时间-平均排队长度柱状图（对应Python的plot_queue_lengths）
     * @param times 时间列表
     * @param queueLengthsDict 排队长度字典（key:计数器ID，value:长度列表）
     */
    public static void plotQueueLengths(List<Double> times, Map<Integer, List<Double>> queueLengthsDict) {
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] laneNames = {
                "Left Turn",
                "Straight lane 1",
                "Straight lane 2",
                "Straight lane 3"
        };
        int[] counterIds = {4501, 4502, 4503, 4504};

        for (int i = 0; i < times.size(); i++) {
            double time = times.get(i);
            for (int laneIdx = 0; laneIdx < 4; laneIdx++) {
                int counterId = counterIds[laneIdx];
                List<Double> lengths = queueLengthsDict.getOrDefault(counterId, Collections.emptyList());
                double length = (i < lengths.size()) ? lengths.get(i) : 0.0;
                dataset.addValue(length, laneNames[laneIdx], String.valueOf(time));
            }
        }

        // 创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(
                "Average Queue Lengths Over Time",  // 标题
                "Time",                             // X轴标签
                "Average Queue Length",             // Y轴标签
                dataset,                            // 数据集
                PlotOrientation.VERTICAL,           // 方向
                true,                               // 显示图例
                true,                               // 工具提示
                false                               // URL链接
        );

        // 设置柱子宽度
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.05);  // 调整柱子间距

        // 保存图表
        saveChart(chart, "300s_interval_Time2QueueLength.png");
    }

    /**
     * 保存图表到文件
     */
    private static void saveChart(JFreeChart chart, String fileName) {
        try {
            // 创建保存目录
            File dir = new File("C:\\MaoMl\\project\\TESSNG_JavaSecondaryCase\\src\\main\\java\\test002\\TESS_JavaAPI_demo\\four\\VehicleRouteGuidance\\VehiQueueResult");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            File file = new File(dir, timestamp + "_" + fileName);

            // 保存图表（宽度800，高度600）
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("图表保存成功：" + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("图表保存失败：" + e.getMessage());
        }
    }
}
