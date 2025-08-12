package TESS_Java_APIDemo.four.PriorityLaneManagement;

import com.jidatraffic.tessng.CustomerSimulator;
import java.util.*;
import java.time.*;

// 假设这些是TESS NG相关的Java接口和类
import com.sun.xml.internal.bind.v2.TODO;
import io.qt.QtSignalBlockerInterface;
import io.qt.QtSignalEmitterInterface;
import io.qt.QtThreadAffineInterface;
import io.qt.core.QObject;
import com.jidatraffic.tessng.*;
import io.qt.core.QThread;
import io.qt.core.QVariant;

public class MySimulator extends CustomerSimulator {
    // 标记当前是否需要进行动态车道管理
    private static boolean needToApply = true;
    // 设定ML与GL车道的目标速度差
    private static double speedDiffRate = 0.1;

    // GL拟合曲线的函数需要的4个参数（Van Aerde模型）
    private static double Kj_GL = 56;       // 普通车道的阻塞密度（veh/km）
    private static double Qc_GL = 1680;     // 普通车道的通行能力
    private static double Vf_GL = 71.14;    // 普通车道的自由流速度
    private static double Vm_GL = 39;       // 普通车道的最大流量对应的速度

    // ML拟合曲线的函数需要的4个参数（Van Aerde模型）
    private static double Kj_ML = 50;       // 管控车道的阻塞密度（veh/km）
    private static double Qc_ML = 1680;     // 管控车道的通行能力
    private static double Vf_ML = 72;       // 管控车道的自由流速度
    private static double Vm_ML = 40;       // 管控车道的最大流量对应的速度

    // 成员变量
    private double Q_GL = 0;
    private double V_GL = 0;       // 普通车道的速度，是三根车道的平均值

    private double Q_ML = 0;
    private double V_ML = 0;

    private double Q6 = 0;
    private double Q7 = 0;
    private double Q8 = 0;

    private double V6 = 0;
    private double V7 = 0;
    private double V8 = 0;

    private double MLSpeed = 0;
    private double GLSpeed = 0;

    private double MLFlow = 0;
    private double GLFlow = 0;

    private double solutionMLFlow = 0;
    private double solutionGLFlow = 0;

    private double solutionMLSpeed = 0;
    private double solutionGLSpeed = 0;

    private double tempSpeedDiffRate = 0;
    private double solutionSpeedDiffRate = 0;
    private double solutionProductivity = 0;
    private double AllowVehsFlow = 0;

    private double speedDiffRateVal = 0;

    private double V_GL1 = 0;
    private double V_GL2 = 0;
    private double V_GL3 = 0;

    private double V_ML_out = 0;

    // 变道开关
    private int GL2ML = 0;  // 普通车道需要变到管控车道
    private int ML2GL = 0;  // 管控车道需要变到普通车道

    // 变道累积
    private int count = 0;
    private ArrayList<Long> managedcars = new ArrayList<>();
    // 记录需要强制换道的车辆ID
    private ArrayList<Long> Cars = new ArrayList<>();

    public MySimulator() {
        super();
    }

    /**
     * Van Aerde模型拟合函数 X是速度 Y是流量
     * 表达式：q = v/(C1 + C2/(Vf-v) + C3*v)
     * @param x 速度
     * @param kj 阻塞密度Kj
     * @param qc 通行能力Qc
     * @param vf 自由流速度Vf
     * @param vm 最佳车速Vm
     * @return 流量
     */
    private static double speedFlowFunction(double x, double kj, double qc, double vf, double vm) {
        double C1 = vf / (kj * vm * vm) * (2.0 * vm - vf);
        double C2 = vf / (kj * vm * vm) * (vf - vm) * (vf - vm);
        double C3 = (1.0 / qc) - vf / (kj * vm * vm);
        return x / (C1 + C2 / (Math.abs(vf - x) + 0.1) + C3 * x);
    }

    /**
     * 根据拟合出来的表达式由流量反推出当前速度（反解方程）
     * @param Y 流量
     * @param isGL 是否为普通车道
     * @param getBigX 是否取较大的速度值
     * @return 速度
     */
    private static int calcSpeedbyFlow(double Y, boolean isGL, boolean getBigX) {
        double Kj = isGL ? Kj_GL : Kj_ML;
        double Qc = isGL ? Qc_GL : Qc_ML;
        double Vf = isGL ? Vf_GL : Vf_ML;
        double Vm = isGL ? Vm_GL : Vm_ML;

        ArrayList<Integer> xList = new ArrayList<>();
        for (int i = 55; i < 75; i++) {
            xList.add(i);
        }

        if (getBigX) {
            // 从大到小遍历
            Collections.reverse(xList);
            for (int x : xList) {
                double y = speedFlowFunction(x, Kj, Qc, Vf, Vm);
                if (y >= Y) {
                    return x;
                }
            }
        } else {
            // 从小到大遍历
            for (int x : xList) {
                double y = speedFlowFunction(x * 1.0, Kj, Qc, Vf, Vm);
                if (y >= Y) {
                    return x;
                }
            }
        }

        // 找不到合适值时返回最佳速度点
        int topX = 0;
        for (int x : xList) {
            if (speedFlowFunction(x * 1.0, Kj, Qc, Vf, Vm) >=
                    speedFlowFunction(topX * 1.0, Kj, Qc, Vf, Vm)) {
                topX = x;
            }
        }
        return topX;
    }

    /**
     * 初始化车辆，在车辆启动上路时被TESS NG调用一次
     */
    @Override
    public void initVehicle(IVehicle vehi) {
        TessInterface iface = TESSNG.tessngIFace();

        SimuInterface simuIFace = iface.simuInterface();
        long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();  // simuTime = 1000→1s
        long tmpId = vehi.id() % 100000;

        if (vehi.vehicleTypeCode() == 13) {
            vehi.setColor("Green");
        }
    }

    /**
     * 重写调用频次，每分钟修改一次车辆的限制车道
     */
//    @Override
//    public void setStepsPerCall(IVehicle vehi) {
//        TessInterface iface = TESSNG.tessngIFace();
//        // 原Python代码中注释掉的逻辑保留为注释
//        /*
//        //计算限制车道方法被调用频次
//        vehi.setSteps_calcLimitedLaneNumber(20 * 60);
//        //重新计算是否可以左强制变道方法被调用频次
//        vehi.setSteps_reCalcToLeftLane(20);
//        //重新计算是否可以右强制变道方法被调用频次
//        vehi.setsteps_reCalcToRightLane(20);
//        */
//    }

    /**
     * 计算车辆的限制车道
     */
    @Override
    public ArrayList<Integer> calcLimitedLaneNumber(IVehicle pIVehicle) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        long batchNum = simuiface.batchNumber();

        ArrayList<Integer> GL_list = new ArrayList<>(Arrays.asList(0, 1, 2));  // GL车道编号
        ArrayList<Integer> ML_list = new ArrayList<>(Collections.singletonList(3));  // ML车道编号

        // 车辆已经行驶距离（米）
        IVehicleDriving driving = pIVehicle.vehicleDriving();
        double dist = driving.getVehiDrivDistance();

//        System.out.printf("vehicle=%d dist=%.2f%n", pIVehicle.id(), dist);

        // ---进入管控区域前---
        // 针对普通车
        if (dist <= 800 && pIVehicle.vehicleTypeCode() != 13 && driving.laneNumber() == 3) {
            driving.toRightLane();
            return ML_list;
        }

        // 针对EV车
        if (pIVehicle.vehicleTypeCode() == 13) {
            if (driving.laneNumber() != 3) {
                driving.toLeftLane();
            }
            return GL_list;
        }

        // ---管控区域---
        // 针对非EV车
        if (ML2GL == 1) {
            // 针对管控车道的车，选择一定数量换道至普通车道
            if (driving.laneNumber() == 3) {
                if (count * 60 < AllowVehsFlow) {
                    pIVehicle.setColor("Red");
                    count++;
                    System.out.printf("count: %d%n", count);
                    return ML_list;
                } else {
                    return new ArrayList<>();
                }
            }
            // 针对普通车道的车，禁止进入管控车道
            else {
                return ML_list;
            }
        } else if (GL2ML == 1) {
            if (count * 60 <= AllowVehsFlow && driving.laneNumber() == 2 && dist <= 1227) {
                pIVehicle.setColor("Red");
                Cars.add(pIVehicle.id());
                driving.toLeftLane();
                managedcars.add(pIVehicle.id());
                count++;
                System.out.printf("count: %d%n", count);
                return GL_list;
            } else if (managedcars.contains(pIVehicle.id())) {
                return GL_list;
            } else {
                return ML_list;
            }
        } else {
            return ML_list;
        }
    }

    /**
     * TESS NG 在每个计算周期结束后调用此方法
     */
    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        long batchNum = simuiface.batchNumber();

        // 获取最近集计时间段内采集器采集的所有车辆集计信息
        ArrayList<VehiInfoAggregated> lVehisInfoAggr = simuiface.getVehisInfoAggregated();

        if (lVehisInfoAggr.size() > 0) {
            for (VehiInfoAggregated vinfo : lVehisInfoAggr) {
                long collectorId = vinfo.getCollectorId();

                // ML车道起点处采集器编号为17
                if (collectorId == 17) {
                    // 集计时间(180s)内过ML检测器的车流量，单位：veh/h
                    Q_ML = vinfo.getVehiCount() * 60;
                } else if (collectorId == 18) {
                    Q6 = vinfo.getVehiCount() * 60;
                    V6 = vinfo.getAvgSpeed();
                } else if (collectorId == 19) {
                    Q7 = vinfo.getVehiCount() * 60;
                    V7 = vinfo.getAvgSpeed();
                } else if (collectorId == 20) {
                    Q8 = vinfo.getVehiCount() * 60;
                    V8 = vinfo.getAvgSpeed();
                } else if (collectorId == 25) {
                    V_ML = vinfo.getAvgSpeed();
                    System.out.printf("speed_ML: %.2f%n", V_ML);
                }
                // 断面2
                else if (collectorId == 24) {
                    V_GL1 = vinfo.getAvgSpeed();
                } else if (collectorId == 23) {
                    V_GL2 = vinfo.getAvgSpeed();
                } else if (collectorId == 22) {
                    V_GL3 = vinfo.getAvgSpeed();
                } else if (collectorId == 21) {
                    V_ML_out = vinfo.getAvgSpeed();
                }
            }

            Q_GL = (Q6 + Q7 + Q8) / 3;
            V_GL = (V6 + V7 + V8) / 3;  // 检测器来的
            System.out.printf("speed_GL: %.2f%n", V_GL);
            System.out.printf("speed_ML_out: %.2f%n", V_ML_out);
            System.out.printf("speed_GL_out: %.2f%n", (V_GL1 + V_GL2 + V_GL3) / 3);
        }

        // 流量计算取2分钟时检测到的流量数据，这里是在预热（让车跑满路网）
        if (batchNum == (20 * 60 * 2)) {
            MLFlow = Q_ML;
            GLFlow = Q_GL;
        }

        // 每分钟刷新一次流量数据
        if (batchNum % (20 * 60) == 0) {
            MLFlow = Q_ML;
            GLFlow = Q_GL;
            count = 0;
            System.out.printf("管控车道的流量: %.2f%n", MLFlow);
            System.out.printf("普通车道的流量: %.2f%n", GLFlow);
        }

        // 每3分钟计算一次变道信号
        if (batchNum % (20 * 60 * 3) == 0) {
            // 初始化变道信号
            ML2GL = 0;
            GL2ML = 0;

            if (V_GL != 0 && MLFlow != 0 && GLFlow != 0) {
                speedDiffRateVal = (V_ML - V_GL) / V_GL;

                // 管控车道状态良好，允许其他普通车辆进入
                if ((V_ML - V_GL) / V_GL > speedDiffRate) {
                    double tempMLFlow = MLFlow;
                    double tempGLFlow = GLFlow;

                    while (tempMLFlow <= 3600 && tempGLFlow >= 0) {
                        tempMLFlow += 90;
                        tempGLFlow -= 30;

                        System.out.printf("MLFlow: %.2f%n", tempMLFlow);
                        MLSpeed = calcSpeedbyFlow(tempMLFlow, false, true);
                        GLSpeed = calcSpeedbyFlow(tempGLFlow, true, true);

                        System.out.printf("calculating_MLSpeed: %.2f calculating_GLSpeed: %.2f%n",
                                MLSpeed, GLSpeed);

                        tempSpeedDiffRate = (MLSpeed - GLSpeed) / GLSpeed;

                        if (tempSpeedDiffRate <= speedDiffRate) {
                            solutionMLFlow = (int)tempMLFlow - 90;
                            solutionGLFlow = (int)tempGLFlow + 30;

                            solutionMLSpeed = calcSpeedbyFlow(solutionMLFlow, false, true);
                            solutionGLSpeed = calcSpeedbyFlow(solutionGLFlow, true, true);
                            solutionSpeedDiffRate = (solutionMLSpeed - solutionGLSpeed) / solutionGLSpeed;

                            solutionProductivity = solutionMLFlow * solutionMLSpeed +
                                    solutionGLFlow * solutionGLSpeed * 3;
                            AllowVehsFlow = Math.abs(solutionMLFlow - Q_ML);

                            GL2ML = 1;
                            System.out.printf("需要有%.0f辆车从普通车道进入到管控车道%n", AllowVehsFlow);
                            break;
                        } else {
                            GL2ML = 0;
                        }
                    }

                    if (!(tempMLFlow <= 3600 && tempGLFlow >= 0)) {
                        System.out.println("no result");
                    }
                }
                // 速度差比值小于0.1，管控车道过载，需要将普通车辆移出来
                else {
                    double tempMLFlow = MLFlow;
                    double tempGLFlow = GLFlow;

                    while (tempMLFlow >= 0 && tempGLFlow <= 3600) {
                        tempMLFlow -= 30;
                        System.out.printf("MLFlow: %.2f%n", tempMLFlow);
                        tempGLFlow += 10;

                        MLSpeed = calcSpeedbyFlow(tempMLFlow, false, true);
                        GLSpeed = calcSpeedbyFlow(tempGLFlow, true, true);

                        System.out.printf("calculating_MLSpeed: %.2f calculating_GLSpeed: %.2f%n",
                                MLSpeed, GLSpeed);

                        tempSpeedDiffRate = (MLSpeed - GLSpeed) / GLSpeed;

                        if (tempSpeedDiffRate >= speedDiffRate) {
                            solutionMLFlow = (int)tempMLFlow;
                            solutionGLFlow = (int)tempGLFlow;

                            solutionMLSpeed = calcSpeedbyFlow(solutionMLFlow, false, true);
                            solutionGLSpeed = calcSpeedbyFlow(solutionGLFlow, true, true);
                            solutionSpeedDiffRate = (solutionMLSpeed - solutionGLSpeed) / solutionGLSpeed;

                            solutionProductivity = solutionMLFlow * solutionMLSpeed +
                                    solutionGLFlow * solutionGLSpeed * 3;
                            AllowVehsFlow = Math.abs(solutionMLFlow - Q_ML);

                            ML2GL = 1;
                            System.out.printf("需要有%.0f辆车从管控车道进入到普通车道%n", AllowVehsFlow);
                            break;
                        } else {
                            ML2GL = 0;
                        }
                    }

                    if (!(tempMLFlow <= 3600 && tempGLFlow >= 0)) {
                        System.out.println("no result");
                    }
                }
            }
        }
    }


    /**
     * 计算是否有权利进行左自由变道,降低变道频率
     */
    @Override
    public void beforeToLeftFreely(IVehicle pIVehicle, ObjBool bKeepOn) {
        // 降低变道频率,随机值大于0.3则禁止变道计算
        if (Math.random() > 0.3) {
            bKeepOn.setValue(false);
        }

        int laneNumber = pIVehicle.vehicleDriving().laneNumber();
        if (laneNumber == 2 || laneNumber == 3) {
            bKeepOn.setValue(true);
        }
    }

    /**
     * 计算是否有权利进行右自由变道,降低变道频率
     */
    @Override
    public void beforeToRightFreely(IVehicle pIVehicle, ObjBool bKeepOn) {
        // 降低变道频率,随机值大于0.3则禁止变道计算
        if (Math.random() > 0.3) {
            bKeepOn.setValue(false);
        }

    }

    /**
     * 车辆强制换道时，提高车速
     */
    @Override
    public boolean reSetSpeed(IVehicle pIVehicle, ObjReal inOutSpeed){
        long tmpId = pIVehicle.id();
        if (Cars.contains(tmpId)) {
            inOutSpeed.setValue(pIVehicle.vehicleDriving().desirSpeed());
            Cars.remove(tmpId);
            return true;
        }
        return false;
    }


    /**
     * 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法
     * 大量用户逻辑在此实现，注意耗时大的计算要尽可能优化，否则影响运行效率
     */

}



