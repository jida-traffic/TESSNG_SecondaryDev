package TESS_Java_APIDemo.six.IntersectionModelParameters;

import com.jidatraffic.tessng.TessngFactory;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

import static TESS_Java_APIDemo.six.IntersectionModelParameters.resultanalysis.resultAnalysis;


public class main {

    // 仿真计数器
    static AtomicInteger count = new AtomicInteger(0);
//    private static int count = 0;


    public static double  evaluation(){
        String dirPathSimu = "C:\\TESSNG_4.0.20\\SecondDev\\TESS_PythonAPI_DEMO\\6.模型参数标定\\交叉口模型参数标定\\SimuResult\\芜湖-初始模型_ver2_3600s.tess";
        try {
            // 获取目录下所有文件/子目录
            File simuDir = new File(dirPathSimu);
            File[] dirs = simuDir.listFiles();

            if (dirs == null || dirs.length == 0) {
                throw new RuntimeException("仿真结果目录为空");
            }

            // 按最后修改时间排序，获取最新的目录
            Arrays.sort(dirs, Comparator.comparingLong(File::lastModified).reversed());
            File latestDir = dirs[0];

            // 结果评价，返回四个评价指标C1，C2，GEH，DevS
            Map<String, Double> results = resultAnalysis(latestDir.getAbsolutePath());

            // 返回所有进口道平均最大排队长度相对误差
            double sum = 0;
            int count = 0;
            for (Double value : results.values()) {
                sum += value;
                count++;
            }

            return sum / count;

        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    public static double optFunc(double[] p) throws InterruptedException {
        try{
            // 遗传算法参数
            double alpha = p[0];
            double beit = Math.round(p[1]);
            double safeDistance = p[2];
            double safeInterval = p[3];
            // 更新计数器
            count.getAndIncrement();

            System.out.println("第" + count.get() + "次仿真已开始");

            String javaHome = System.getProperty("java.home");
            String javaCmd = javaHome + File.separator + "bin" + File.separator + "java";

            List<String> command = new ArrayList<>();
            command.add(javaCmd);
            command.add("-Djava.library.path=C:\\TESSNG_4.0.20");
            command.add("-cp");
            command.add(System.getProperty("java.class.path"));
            command.add("test002.TESS_JavaAPI_demo.six.IntersectionModelParameters.tessRunner");

            // 使用ProcessBuilder创建子进程
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.directory(new File("C:\\TESSNG_4.0.20"));
            Map<String, String> env = pb.environment();
            Map<String,String> envVariables  = new HashMap<>();
            envVariables.put("QT_PLUGIN_PATH","C:\\TESSNG_4.0.20\\plugins");
            env.putAll(envVariables);

            // 重定向子进程的输入输出
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 等待子进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("子进程执行失败，退出码: " + exitCode);
                return Double.MAX_VALUE;
            }

            double result = evaluation();
            System.out.println("RE："+result);
            return result;
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return Double.MAX_VALUE;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)  throws InterruptedException{
            // 遗传算法参数配置
            final Engine<DoubleGene, Double> engine = Engine
                    .builder(p-> {
                        try{
                            return optFunc(p);
                            }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return Double.MAX_VALUE;
                        }},
                            // 定义4个参数及其范围
                            Codecs.ofVector(
                                    DoubleRange.of(2.0, 5.0),   // alpha [2.0, 5.0]
                                    DoubleRange.of(1.0, 3.0),   // beit [1.0, 3.0]
                                    DoubleRange.of(1.0, 2.0),   // safedistance [1.0, 2.0]
                                    DoubleRange.of(1.0, 2.0)    // safeinterval [1.0, 2.0]
                            ))
                    .populationSize(10)      // 种群大小 size_pop=10
                    .alterers(
                            new Mutator<>(0.05), // 变异概率 prob_mut=0.05
                            new MeanAlterer<>(0.65)) // 交叉概率
                    .optimize(Optimize.MINIMUM) // 最小化目标函数
                    .build();

            // 运行遗传算法 最多max_iter=10代
            final EvolutionResult<DoubleGene, Double> result = engine.stream()
                    .limit(10)
                    .collect(EvolutionResult.toBestEvolutionResult());

            // 输出最佳结果
            final DoubleChromosome bestChromosome = (DoubleChromosome)result.bestPhenotype()
                    .genotype()
                    .chromosome();

            System.out.println("best_Params: " + bestChromosome +
                    " best_RE: " + result.bestFitness());

            // TODO: 这里可以添加结果可视化部分
            // Java需要借助其他库如JFreeChart进行绘图

            System.out.println("结束测试");
//        tessRun(new double[]{2.29032258,1.90322581, 1.46666667, 1.06666667});
    }
}


