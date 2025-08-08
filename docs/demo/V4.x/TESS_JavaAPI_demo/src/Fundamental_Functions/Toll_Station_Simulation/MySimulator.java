package Fundamental_Functions.Toll_Station_Simulation;

import com.jidatraffic.tessng.*;
import java.util.*;

public class MySimulator extends JCustomerSimulator {
    // 车辆方阵的车辆数
    private int mrSquareVehiCount = 28;
    // 飞机速度，飞机后面的车辆速度会被设定为此数据
    private double mrSpeedOfPlane = 0;
    // 相位改变量
    private int phaseChange = 0;
    private int __mTg2r = 30;
    private int __mTr2g = 0;
    private int __mrT1 = 0;
    private int __mrT2BusArrival = 0;
    // 公交车车速信息，用于判断是否有公交车经过
    private List<VehiInfoCollected> __mlOutVehiInfo = new ArrayList<>();

    public MySimulator() {
        super();
    }

    /**
     * 计算信号灯颜色
     * @param signalLamp 信号灯对象
     * @return 是否成功计算
     */
    public boolean calcLampColor(ISignalLamp signalLamp) {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        long batchNum = simuiface.batchNumber();
        // 当前已仿真时间，单位：毫秒
        long simuTime = iface.simuInterface().simuTimeIntervalWithAcceMutiples();

        // 当前的仿真时间，为t
        int t = (int)(simuTime / 1000);
        // 获取信号灯相位号
        long signalePhaseId = signalLamp.signalPhase().id();
        // System.out.println("signalePhaseId=" + signalePhaseId);
        List<VehiInfoCollected> empty = new ArrayList<>();

        // 判断是否有公交车到达
        if (this.__mrT1 != t) {
            this.__mrT1 = t;
            // mrT1用来储存当前的仿真秒数，如果储存的仿真秒数不等于当前，则证明仿真时间已经往前走了，可以处理数据
            if (!this.__mlOutVehiInfo.isEmpty()) {
                // System.out.println(this.__mlOutVehiInfo.get(0).getAvgSpeed());
                if (this.__mlOutVehiInfo.get(0).getAvgSpeed() < 200) {
                    this.__mrT2BusArrival = t;
                }
            }
            // 清除数据，如果不做清除，会导致变卡数据会一直存储，后面调用的时候也会有问题
            this.__mlOutVehiInfo.clear();
        }

        // 判断仿真时间是否在可延长的绿色相位内
        // 根据情况的不同要计算的量有，红变绿的时刻、绿变红的时刻、还有可能要算的是相位改变量
        if ((this.__mrT2BusArrival > (this.__mTg2r - 8) && this.__mrT2BusArrival < this.__mTg2r)) {
            this.__mTg2r += 8;
            this.phaseChange += 8;
            this.__mTr2g = 90 + this.__mTg2r;
        }
        // 改下个相位的时刻
        else if (this.__mrT1 >= this.__mTr2g && this.__mTg2r >= this.__mrT1) {
            this.__mTg2r = this.__mTg2r;
            this.__mTr2g = 90 + this.__mTg2r;
        }

        // 判断仿真时间是否在可缩短的红色相位内
        if (this.__mrT2BusArrival > this.__mTr2g - 35 && this.__mrT2BusArrival < this.__mTr2g - 8) {
            this.__mTr2g = this.__mrT1 + 8;
            this.phaseChange = this.__mrT1 + 8;  // 这里相当于重置改变量
            this.__mTg2r = 30 + this.__mTr2g;
        }
        else if ((this.__mTg2r < this.__mrT1 && this.__mrT1 < this.__mTr2g - 35) ||
                (this.__mrT1 >= this.__mTr2g - 8 && this.__mrT1 < this.__mTr2g)) {
            this.__mTr2g = this.__mTr2g;
            this.__mTg2r = 30 + this.__mTr2g;
        }

        // 在相位时刻为0累积相位改变量的时刻，西向信号灯（也既公交车进入的信号灯）转为绿色，北向信号灯转为红色
        int w2GandN2R = (0 + this.phaseChange) % 120;
        // 在相位时刻为0累积相位改变量的时刻，南向信号灯转为绿色，西向信号灯转为红色
        int s2GandW2R = (30 + this.phaseChange) % 120;
        // 在相位时刻为0累积相位改变量的时刻，东向信号灯转为绿色，南向信号灯转为红色
        int e2GandS2R = (60 + this.phaseChange) % 120;
        // 在相位时刻为0累积相位改变量的时刻，北向信号灯转为绿色，东向信号灯转为红色
        int n2GandE2R = (90 + this.phaseChange) % 120;


        // 这里是分界，从这里往上的分部是用于计算最关键的几个值，w2GandN2R等，就是算出这些值用这些值和当前时刻进行对比，如果符合这些值就进行改变
        // 从这里往下的部分就是单纯的根据时刻与上面计算的各个进口需要的值的对比，然后进行颜色的改变
        // 南向和东向的初始变化
        if (t == 0) {
            // 这里需要加入判断是否为南向向信号灯的语句，如果是则改变红色
            if (signalePhaseId == 418) { // 这种是相位id去看路网信息里面有
                signalLamp.setLampColor("红");
            }
            // 同上需加入东向判断
            if (signalePhaseId == 415) {
                signalLamp.setLampColor("红");
            }
        }
        // 下面这些和上面一样，判断不同进口方向的变化
        if (t % 120 == w2GandN2R - 3) {
            if (signalePhaseId == 417) {
                signalLamp.setLampColor("黄");
            }
        }
        else if (t % 120 == s2GandW2R - 3) {
            if (signalePhaseId == 416) {
                signalLamp.setLampColor("黄");
            }
        }
        else if (t % 120 == e2GandS2R - 3) {
            if (signalePhaseId == 418) {
                signalLamp.setLampColor("黄");
            }
        }
        else if (t % 120 == n2GandE2R - 3) {
            if (signalePhaseId == 415) {
                signalLamp.setLampColor("黄");
            }
        }

        // 判断当前模拟时间与某个信号灯的转变时刻相符，并改变那个信号灯颜色
        if (t % 120 == w2GandN2R) {
            if (signalePhaseId == 416) {
                signalLamp.setLampColor("绿");
            }
            if (signalePhaseId == 417) {
                signalLamp.setLampColor("红");
            }
        }
        else if (t % 120 == s2GandW2R) {
            if (signalePhaseId == 418) {
                signalLamp.setLampColor("绿");
            }
            if (signalePhaseId == 416) {
                signalLamp.setLampColor("红");
            }
        }
        else if (t % 120 == e2GandS2R) {
            if (signalePhaseId == 415) {
                signalLamp.setLampColor("绿");
            }
            if (signalePhaseId == 418) {
                signalLamp.setLampColor("红");
            }
        }
        else if (t % 120 == n2GandE2R) {
            if (signalePhaseId == 417) {
                signalLamp.setLampColor("绿");
            }
            if (signalePhaseId == 415) {
                signalLamp.setLampColor("红");
            }
        }

        return true;
    }

    /**
     * 过载的父类方法，TESS NG 在每个计算周期结束后调用此方法，
     * 大量用户逻辑在此实现，注意耗时大的计算要尽可能优化，否则影响运行效率
     */
    @Override
    public void afterOneStep() {
        // 以下是获取一些仿真过程数据的方法
        // TESSNG 顶层接口
        TessInterface iface = TESSNG.tessngIFace();
        // TESSNG 仿真子接口
        SimuInterface simuiface = iface.simuInterface();
        // TESSNG 路网子接口
        NetInterface netiface = iface.netInterface();
        // 当前仿真计算批次
        long batchNum = simuiface.batchNumber();
        // 当前已仿真时间，单位：毫秒
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
        // 开始仿真的现实时间
        long startRealtime = simuiface.startMSecsSinceEpoch();

        System.out.println("信号灯组相位颜色");
        // 信号灯组相位颜色
        ArrayList<SignalPhaseColor> lPhoneColor = simuiface.getSignalPhasesColor();
        System.out.print("信号灯组相位颜色: ");
        for (SignalPhaseColor pcolor : lPhoneColor) {
            System.out.print("(" + pcolor.getSignalGroupId() + ", " +
                    pcolor.getPhaseNumber() + ", " +
                    pcolor.getColor() + ", " +
                    pcolor.getMrIntervalSetted() + ", " +
                    pcolor.getMrIntervalByNow() + ") ");
        }
        System.out.println();

        // 获取当前仿真时间完成穿越采集器的所有车辆信息
        ArrayList<VehiInfoCollected> lVehiInfo = simuiface.getVehisInfoCollected();

        if (lVehiInfo.size() > 0 && lVehiInfo.get(0).getVehiType() == 2) {
            this.__mlOutVehiInfo.add(lVehiInfo.get(0));
            System.out.print("车辆信息采集器采集信息：");
            for (VehiInfoCollected vinfo : lVehiInfo) {
                System.out.print("(" + vinfo.getCollectorId() + ", " + vinfo.getVehiId() + ") ");
            }
            System.out.println();
        }
    }
}
