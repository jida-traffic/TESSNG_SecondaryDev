package test002.TESS_JavaAPI_demo.four.VariableLaneManagement;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



// 主仿真类（移除QT依赖）
public class MySimulator extends CustomerSimulator {
    // 车辆方阵的车辆数
    private int mrSquareVehiCount = 28;
    // 飞机速度，飞机后面的车辆速度会被设定为此数据
    private double mrSpeedOfPlane = 0;
    // 当前正在仿真计算的路网名称
    private String mNetPath = null;
    // 相同路网连续仿真次数
    private int mSimuCount = 0;
    // 初始化二次开发对象
    private SecondaryDevCases secondaryDev = new SecondaryDevCases(2);

    public MySimulator() {


    }

    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();

        long batchNum = simuiface.batchNumber();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        List<IVehicle> allVehi = simuiface.allVehiStarted();

        long time = simuTime % (900 * 1000);
        if (time > 100 * 1000 && time <450 *1000 ) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            iface.simuInterface().stopSimu();
            System.out.println("停止仿真运算");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            secondaryDev.optTrafficCanalization( );
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            iface.simuInterface().startSimu();
            System.out.println("开启仿真运算");
        }
        else{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            iface.simuInterface().stopSimu();
            System.out.println("停止仿真运算");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            secondaryDev.reverseoptTrafficCanalization();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            iface.simuInterface().startSimu();
            System.out.println("开启仿真运算");
        }
    }

    /**
     * 动态修改决策点不同路径流量比
     */
    @Override
    public ArrayList<DecipointFlowRatioByInterval>  calcDynaFlowRatioParameters() {
        // 获取TESS NG顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        if (iface == null) {
            System.out.println("TESS NG接口初始化失败");
            return new ArrayList<>();
        }

        // 获取当前仿真计算批次
        long batchNum = iface.simuInterface().batchNumber();

        // 在计算第20批次时修改某决策点各路径流量比
        if (batchNum == 20) {
            DecipointFlowRatioByInterval dfi = new DecipointFlowRatioByInterval();
            // 设置决策点编号
            dfi.setDeciPointID(1);
            // 起始时间（单位：秒）
            dfi.setStartDateTime(1);
            // 结束时间（单位：秒）
            dfi.setEndDateTime(84000);

            // 添加各路径流量比


            RoutingFlowRatio rfr1 = new RoutingFlowRatio(1, 3);
            RoutingFlowRatio rfr2 = new RoutingFlowRatio(2, 4);
            RoutingFlowRatio rfr3 = new RoutingFlowRatio(3, 3);

            dfi.setRoutingFlowRatios(new ArrayList<>(Arrays.asList(rfr1,rfr2,rfr3)));
            ArrayList<DecipointFlowRatioByInterval> result = new ArrayList<>();
            result.add(dfi);
            return result;
        }

        return new ArrayList<>();
    }
}

