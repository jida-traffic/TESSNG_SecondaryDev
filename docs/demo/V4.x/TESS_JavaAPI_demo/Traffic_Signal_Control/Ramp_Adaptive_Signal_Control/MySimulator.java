package Traffic_Signal_Control.Ramp_Adaptive_Signal_Control;

import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.ObjReal;
import com.jidatraffic.tessng.TessInterface;
import com.jidatraffic.tessng.SimuInterface;
import com.jidatraffic.tessng.NetInterface;
import com.jidatraffic.tessng.IVehicle;
import com.jidatraffic.tessng.ILink;
import com.jidatraffic.tessng.ISignalLamp;
import com.jidatraffic.tessng.ISignalPhase;
import com.jidatraffic.tessng.ColorInterval;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MySimulator extends JCustomerSimulator {
    private int mrSquareVehiCount = 28;
    private double mrSpeedOfPlane = 0;
    private String mNetPath = null;
    private int mSimuCount = 0;
    private int greenTime = 60;
    private int vehiCount = 0;

    public MySimulator() {
        super();
    }


    public void setStepsPerCall(IVehicle vehi) {
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netface = iface.netInterface();
        String netFileName = netface.netFilePath();

        if (netFileName.contains("Temp")) {
            vehi.setIsPermitForVehicleDraw(true);
            vehi.setSteps_calcLimitedLaneNumber(10);
            vehi.setSteps_calcChangeLaneSafeDist(10);
            vehi.setSteps_reCalcdesirSpeed(1);
            vehi.setSteps_reSetSpeed(1);
        } else {
            SimuInterface simuface = iface.simuInterface();
            int steps = simuface.simuAccuracy();
            // 可根据需要添加其他处理逻辑
        }
    }

    @Override
    public void afterOneStep() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuiface = iface.simuInterface();
        NetInterface netiface = iface.netInterface();
        long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();

        List<VehiInfoAggregated> lVehiInfo = simuiface.getVehisInfoAggregated();
        List<Double> lVehiInfoFromPartly = new ArrayList<>();
        double O_bar = 0;
        List<Double> lVehiInfoToPartly = new ArrayList<>();
        double O_k_1 = 0;
        final int K_r = 70;

        if (!lVehiInfo.isEmpty()) {
            if (simuTime > 90 * 1000 && simuTime < 110 * 1000) {
                for (VehiInfoAggregated vehiInfo : lVehiInfo) {
                    long vehiCollectorId = vehiInfo.getCollectorId();
                    if (vehiCollectorId == 10 || vehiCollectorId == 11 || vehiCollectorId == 12) {
                        lVehiInfoFromPartly.add(vehiInfo.getOccupancy());
                        System.out.println("上游初始饱和占有率：" + vehiCollectorId + " " + vehiInfo.getOccupancy());
                    }
                }
                if (!lVehiInfoFromPartly.isEmpty()) {
                    double sum = 0;
                    for (double occ : lVehiInfoFromPartly) {
                        sum += occ;
                    }
                    O_bar = sum / lVehiInfoFromPartly.size();
                    System.out.println("上游初始饱和平均占有率 " + O_bar);
                }
            } else if (simuTime > 110 * 1000) {
                for (VehiInfoAggregated vinfo : lVehiInfo) {
                    long vehiCollectorId = vinfo.getCollectorId();
                    if (vehiCollectorId == 7 || vehiCollectorId == 8 || vehiCollectorId == 9) {
                        lVehiInfoToPartly.add(vinfo.getOccupancy());
                        System.out.println("下游初始饱和占有率：" + vehiCollectorId + " " + vinfo.getOccupancy());
                    }
                    if (vehiCollectorId == 1) {
                        this.vehiCount = vinfo.getVehiCount();
                        System.out.println("采集器1过车数：" + vinfo.getVehiCount());
                    }
                }
                if (!lVehiInfoToPartly.isEmpty()) {
                    double sum = 0;
                    for (double occ : lVehiInfoToPartly) {
                        sum += occ;
                    }
                    O_k_1 = sum / lVehiInfoToPartly.size();
                    System.out.println("下游初始饱和平均占有率 " + O_k_1);
                    lVehiInfoToPartly.clear();
                    System.out.println("vehiCount " + this.vehiCount);

                    double r_k = this.vehiCount * 60 + K_r * ((O_bar - O_k_1) / 100);
                    double T = 3600.0 / 1800; // 饱和车头时距
                    this.greenTime = (int) Math.round((r_k / 60) * T);
                    if (this.greenTime < 0) {
                        this.greenTime = 0;
                    }
                    System.out.println("greenTime " + this.greenTime);
                    System.out.println("r_k " + r_k);

                    // 设置信号灯绿灯时间
                    ISignalLamp signalLamp = netiface.findSignalLamp(1);
                    System.out.println(signalLamp);

                    if (signalLamp != null) {
                        ISignalPhase signalPhase = signalLamp.signalPhase();
                        System.out.println(signalPhase);

                        if (signalPhase != null) {
                            ArrayList<ColorInterval> colorList = new ArrayList<>();
                            colorList.add(new ColorInterval("红", 60 - this.greenTime));
                            colorList.add(new ColorInterval("绿", this.greenTime));
                            signalPhase.setColorList(colorList);
                        }
                    }
                }
            }
        }
    }
}