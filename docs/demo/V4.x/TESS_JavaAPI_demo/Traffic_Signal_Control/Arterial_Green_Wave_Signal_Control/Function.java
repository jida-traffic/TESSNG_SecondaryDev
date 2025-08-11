package Traffic_Signal_Control.Arterial_Green_Wave_Signal_Control;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.MaxIter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Function {

    /**
     * 求解最大绿波带问题（使用Apache Commons Math，仅支持连续变量）
     * @return 包含协调周期和相位差的结果数组 [周期时长, 相位差列表]
     */
    public static Object[] maxband() {
        try {
            // 读取参数配置
            JsonObject params = JsonParser.parseReader(new FileReader("D:\\Yangzhenbei\\Java_demo\\src\\Traffic_Signal_Control\\Arterial_Green_Wave_Signal_Control\\param.json")).getAsJsonObject();

            int N = params.get("N").getAsInt();
            double CMax = params.get("Cmax").getAsDouble();
            double CMin = params.get("Cmin").getAsDouble();

            JsonArray LArray = params.get("outbound_Li").getAsJsonArray();
            double[] L = new double[N-1];
            for (int i = 0; i < N-1; i++) {
                L[i] = LArray.get(i).getAsDouble();
            }

            JsonArray LBarArray = params.get("inbound_Li_bar").getAsJsonArray();
            double[] LBar = new double[N-1];
            for (int i = 0; i < N-1; i++) {
                LBar[i] = LBarArray.get(i).getAsDouble();
            }

            JsonArray RArray = params.get("outbound_ri").getAsJsonArray();
            double[] R = new double[N];
            for (int i = 0; i < N; i++) {
                R[i] = RArray.get(i).getAsDouble();
            }

            JsonArray RBarArray = params.get("inbound_ri_bar").getAsJsonArray();
            double[] RBar = new double[N];
            for (int i = 0; i < N; i++) {
                RBar[i] = RBarArray.get(i).getAsDouble();
            }

            JsonArray DeltaArray = params.get("inbound_outband_Deltai").getAsJsonArray();
            double[] Delta = new double[N];
            for (int i = 0; i < N; i++) {
                Delta[i] = DeltaArray.get(i).getAsDouble();
            }

            JsonArray VArray = params.get("outbound_vi").getAsJsonArray();
            double[] V = new double[N-1];
            for (int i = 0; i < N-1; i++) {
                V[i] = VArray.get(i).getAsDouble();
            }

            JsonArray VBarArray = params.get("inbound_vi_bar").getAsJsonArray();
            double[] VBar = new double[N-1];
            for (int i = 0; i < N-1; i++) {
                VBar[i] = VBarArray.get(i).getAsDouble();
            }

            double K = params.get("K").getAsDouble();

            // 计算变量总数（Apache Commons Math只支持连续变量，将整数变量改为连续变量）
            int varCount = N + N + N + (N-1) + (N-1) + (N-1) + (N-1) + 3;
            List<LinearConstraint> constraints = new ArrayList<>();
            double[] objectiveCoefficients = new double[varCount];

            // 变量索引映射
            int idx = 0;
            int[] oIndices = new int[N];
            int[] wIndices = new int[N];
            int[] wBarIndices = new int[N];
            int[] tIndices = new int[N-1];
            int[] tBarIndices = new int[N-1];
            int[] deltaIndices = new int[N-1];  // 改为连续变量
            int[] deltaBarIndices = new int[N-1];  // 改为连续变量
            int bIndex, bBarIndex, zIndex;

            // 分配变量索引
            for (int i = 0; i < N; i++) oIndices[i] = idx++;
            for (int i = 0; i < N; i++) wIndices[i] = idx++;
            for (int i = 0; i < N; i++) wBarIndices[i] = idx++;
            for (int i = 0; i < N-1; i++) tIndices[i] = idx++;
            for (int i = 0; i < N-1; i++) tBarIndices[i] = idx++;
            for (int i = 0; i < N-1; i++) deltaIndices[i] = idx++;
            for (int i = 0; i < N-1; i++) deltaBarIndices[i] = idx++;
            bIndex = idx++;
            bBarIndex = idx++;
            zIndex = idx++;

            // 设置目标函数: 最大化 b + K*b_bar
            objectiveCoefficients[bIndex] = 1;
            objectiveCoefficients[bBarIndex] = K;

            // 创建变量边界
            double[] lowerBounds = new double[varCount];
            double[] upperBounds = new double[varCount];
            Arrays.fill(lowerBounds, 0.0);
            Arrays.fill(upperBounds, Double.POSITIVE_INFINITY);

            // 设置变量边界
            for (int i = 0; i < N; i++) {
                lowerBounds[oIndices[i]] = 0.0;
                upperBounds[oIndices[i]] = 1.0;
            }

            for (int i = 0; i < N-1; i++) {
                lowerBounds[tBarIndices[i]] = -10;
                upperBounds[tBarIndices[i]] = 10;

                lowerBounds[deltaIndices[i]] = -10;  // 原整数变量改为连续变量
                upperBounds[deltaIndices[i]] = 10;
            }

            lowerBounds[bIndex] = 0;
            lowerBounds[bBarIndex] = 0;
            lowerBounds[zIndex] = 1 / CMax;
            upperBounds[zIndex] = 1 / CMin;

            // 添加约束条件
            // 1. (1 - K) * b_bar >= (1 - K) * K * b
            double[] coeffs = new double[varCount];
            coeffs[bBarIndex] = (1 - K);
            coeffs[bIndex] = -(1 - K) * K;
            constraints.add(new LinearConstraint(coeffs, Relationship.GEQ, 0));

            // 2. 绿灯时间约束
            for (int i = 0; i < N; i++) {
                // b/2 <= w[i]
                coeffs = new double[varCount];
                coeffs[wIndices[i]] = 1;
                coeffs[bIndex] = -0.5;
                constraints.add(new LinearConstraint(coeffs, Relationship.GEQ, 0));

                // w[i] <= (1 - R[i] - b/2)
                coeffs = new double[varCount];
                coeffs[wIndices[i]] = 1;
                coeffs[bIndex] = 0.5;
                constraints.add(new LinearConstraint(coeffs, Relationship.LEQ, 1 - R[i]));

                // b_bar/2 <= w_bar[i]
                coeffs = new double[varCount];
                coeffs[wBarIndices[i]] = 1;
                coeffs[bBarIndex] = -0.5;
                constraints.add(new LinearConstraint(coeffs, Relationship.GEQ, 0));

                // w_bar[i] <= (1 - RBar[i] - b_bar/2)
                coeffs = new double[varCount];
                coeffs[wBarIndices[i]] = 1;
                coeffs[bBarIndex] = 0.5;
                constraints.add(new LinearConstraint(coeffs, Relationship.LEQ, 1 - RBar[i]));
            }

            // 3. 相位差约束
            for (int i = 0; i < N-1; i++) {
                // o[i] + w[i] + t[i] == o[i+1] + w[i+1] + delta[i]
                coeffs = new double[varCount];
                coeffs[oIndices[i]] = 1;
                coeffs[wIndices[i]] = 1;
                coeffs[tIndices[i]] = 1;
                coeffs[oIndices[i+1]] = -1;
                coeffs[wIndices[i+1]] = -1;
                coeffs[deltaIndices[i]] = -1;
                constraints.add(new LinearConstraint(coeffs, Relationship.EQ, 0));
            }

            // 4. 上下行协调约束
            for (int i = 0; i < N-1; i++) {
                // o[i] + Delta[i] + w_bar[i] + delta_bar[i] == o[i+1] + Delta[i+1] + w_bar[i+1] + t_bar[i]
                coeffs = new double[varCount];
                coeffs[oIndices[i]] = 1;
                coeffs[wBarIndices[i]] = 1;
                coeffs[deltaBarIndices[i]] = 1;
                coeffs[oIndices[i+1]] = -1;
                coeffs[wBarIndices[i+1]] = -1;
                coeffs[tBarIndices[i]] = -1;
                constraints.add(new LinearConstraint(coeffs, Relationship.EQ, Delta[i+1] - Delta[i]));
            }

            // 5. 行驶时间约束
            for (int i = 0; i < N-1; i++) {
                // t[i] == L[i] / V[i] * z
                coeffs = new double[varCount];
                coeffs[tIndices[i]] = 1;
                coeffs[zIndex] = -L[i] / V[i];
                constraints.add(new LinearConstraint(coeffs, Relationship.EQ, 0));

                // t_bar[i] == LBar[i] / VBar[i] * z
                coeffs = new double[varCount];
                coeffs[tBarIndices[i]] = 1;
                coeffs[zIndex] = -LBar[i] / VBar[i];
                constraints.add(new LinearConstraint(coeffs, Relationship.EQ, 0));
            }

            // 6. 初始相位差约束
            coeffs = new double[varCount];
            coeffs[oIndices[0]] = 1;
            constraints.add(new LinearConstraint(coeffs, Relationship.EQ, 0));

            // 创建线性规划问题
            LinearObjectiveFunction objective = new LinearObjectiveFunction(objectiveCoefficients, 0);
// 创建求解器时通过构造函数设置精度（可选），并在优化时传入迭代次数参数
            SimplexSolver solver = new SimplexSolver(1e-6); // 1e-6 是默认精度，可调整

// 定义最大迭代次数（通过 MaxIter 优化数据对象）
            OptimizationData maxIterData = new MaxIter(100000); // 设置最大迭代次数为 100000

            // 设置变量边界
            List<LinearConstraint> boundsConstraints = new ArrayList<>();
            for (int i = 0; i < varCount; i++) {
                if (lowerBounds[i] > Double.NEGATIVE_INFINITY) {
                    coeffs = new double[varCount];
                    coeffs[i] = 1;
                    boundsConstraints.add(new LinearConstraint(coeffs, Relationship.GEQ, lowerBounds[i]));
                }
                if (upperBounds[i] < Double.POSITIVE_INFINITY) {
                    coeffs = new double[varCount];
                    coeffs[i] = 1;
                    boundsConstraints.add(new LinearConstraint(coeffs, Relationship.LEQ, upperBounds[i]));
                }
            }
            constraints.addAll(boundsConstraints);

            PointValuePair solution = solver.optimize(
                    objective,
                    new LinearConstraintSet(constraints),
                    GoalType.MAXIMIZE,
                    new NonNegativeConstraint(false), // 允许变量为负
                    maxIterData // 传入最大迭代次数参数
            );

            if (solution == null) {
                System.err.println("问题没有最优解!");
                return null;
            }

            // 获取求解结果
            double[] solutionValues = solution.getPoint();
            double bValue = solutionValues[bIndex];
            System.out.println("西向东方向绿波带带宽：" + bValue + "个周期");

            // 收集相位差结果
            List<Double> oList = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                oList.add(solutionValues[oIndices[i]]);
            }

            // 计算周期时长
            double zValue = solutionValues[zIndex];
            double C = 1 / zValue;

            return new Object[]{C, oList};

        } catch (IOException e) {
            System.err.println("读取参数文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("求解过程出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算新的信号配时方案（与LPSolve版本完全相同）
     */
    public static Map<Integer, Map<String, Object>> calGroups(
            Map<Integer, Map<String, Object>> originGroups,
            double C,
            List<Double> oList) {

        try {
            JsonObject params = JsonParser.parseReader(new FileReader("D:\\Yangzhenbei\\Java_demo\\src\\Traffic_Signal_Control\\Arterial_Green_Wave_Signal_Control\\param.json")).getAsJsonObject();

            int N = params.get("N").getAsInt();

            JsonArray groupIdArray = params.get("groupIdLst").getAsJsonArray();
            List<Integer> corGroups = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                corGroups.add(groupIdArray.get(i).getAsInt());
            }

            JsonArray RArray = params.get("outbound_ri").getAsJsonArray();
            double[] R = new double[N];
            for (int i = 0; i < N; i++) {
                R[i] = RArray.get(i).getAsDouble();
            }

            JsonArray phaseArray = params.get("inbound_phase").getAsJsonArray();
            List<Integer> corPhases = new ArrayList<>();
            for (int i = 0; i < phaseArray.size(); i++) {
                corPhases.add(phaseArray.get(i).getAsInt());
            }

            Map<Integer, Map<String, Object>> newGroups = new HashMap<>();

            for (int i = 0; i < N; i++) {
                int groupId = corGroups.get(i);
                Map<String, Object> group = originGroups.get(groupId);

                if (group == null) {
                    System.err.println("未找到信号组: " + groupId);
                    continue;
                }

                // 修复类型转换：将Long改为Integer
                int originPeriodTime = ((Integer) group.get("period_time")).intValue();
                int newPeriodTime = (int) Math.round(C);

                @SuppressWarnings("unchecked")
                Map<Integer, Map<String, Object>> phases =
                        (Map<Integer, Map<String, Object>>) group.get("phases");

                Map<Integer, Map<String, Object>> newPhaseDict = new HashMap<>();
                int sumGreenTime = 0;

                for (Map.Entry<Integer, Map<String, Object>> entry : phases.entrySet()) {
                    int phaseId = entry.getKey();
                    Map<String, Object> phase = entry.getValue();

                    int startTime;
                    int newGreenInterval;
                    // 修复类型转换：将Long改为Integer
                    int yellowInterval = ((Integer) phase.get("yellow_interval")).intValue();
                    int allRedInterval = ((Integer) phase.get("all_red_interval")).intValue();

                    if (corPhases.contains(phaseId)) {
                        // 协调相位
                        startTime = (int) Math.round(newPeriodTime * oList.get(i));
                        newGreenInterval = (int) Math.round(newPeriodTime * (1 - R[i])) - yellowInterval;
                    } else {
                        // 非协调相位
                        if (newPhaseDict.isEmpty()) {
                            startTime = 0;
                        } else {
                            // 找上一个相位
                            Map.Entry<Integer, Map<String, Object>> lastEntry =
                                    new ArrayList<>(newPhaseDict.entrySet()).get(newPhaseDict.size() - 1);
                            Map<String, Object> lastPhase = lastEntry.getValue();

                            // 修复类型转换：将Long改为Integer
                            startTime = ((Integer) lastPhase.get("start_time")).intValue() +
                                    ((Integer) lastPhase.get("green_interval")).intValue() +
                                    ((Integer) lastPhase.get("yellow_interval")).intValue();

                            if (startTime > newPeriodTime) {
                                startTime -= newPeriodTime;
                            }
                        }

                        // 按比例分配非协调相位绿灯时间
                        // 修复类型转换：将Long改为Integer
                        double ratio = (double) (((Integer) phase.get("green_interval")).intValue() +
                                ((Integer) phase.get("yellow_interval")).intValue()) /
                                (originPeriodTime * R[i]);
                        newGreenInterval = (int) Math.round(newPeriodTime * R[i] * ratio) - yellowInterval;
                    }

                    // 确保绿灯时间不为负
                    newGreenInterval = Math.max(newGreenInterval, 1);

                    sumGreenTime += newGreenInterval + yellowInterval;

                    Map<String, Object> newPhase = new HashMap<>();
                    newPhase.put("phase_id", phaseId);
                    newPhase.put("start_time", startTime);
                    newPhase.put("green_interval", newGreenInterval);
                    newPhase.put("yellow_interval", yellowInterval);
                    newPhase.put("all_red_interval", allRedInterval);

                    newPhaseDict.put(phaseId, newPhase);
                }

                // 校核周期时长，调整最后一个相位
                if (sumGreenTime != newPeriodTime && !newPhaseDict.isEmpty()) {
                    Map.Entry<Integer, Map<String, Object>> lastEntry =
                            new ArrayList<>(newPhaseDict.entrySet()).get(newPhaseDict.size() - 1);
                    int lastPhaseId = lastEntry.getKey();
                    Map<String, Object> lastPhase = lastEntry.getValue();

                    int adjust = newPeriodTime - sumGreenTime;
                    // 修复类型转换：将Long改为Integer
                    lastPhase.put("green_interval",
                            ((Integer) lastPhase.get("green_interval")).intValue() + adjust);
                    newPhaseDict.put(lastPhaseId, lastPhase);
                }

                // 保存新的信号组配置
                Map<String, Object> newGroup = new HashMap<>();
                newGroup.put("period_time", newPeriodTime);
                newGroup.put("phases", newPhaseDict);
                newGroups.put(groupId, newGroup);
            }

            return newGroups;

        } catch (IOException e) {
            System.err.println("读取参数文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}
