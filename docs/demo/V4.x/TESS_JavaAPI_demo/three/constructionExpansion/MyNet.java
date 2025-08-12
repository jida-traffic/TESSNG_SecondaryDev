package TESS_Java_APIDemo.three.constructionExpansion;

import com.jidatraffic.tessng.CustomerNet;
import com.jidatraffic.tessng.NetInterface;
import com.jidatraffic.tessng.*;
import com.jidatraffic.tessng.TessInterface;

import java.util.ArrayList;
import java.util.Arrays;

import static com.jidatraffic.tessng.TESSNG.m2p;
import static com.jidatraffic.tessng.TESSNG.p2m;


public class MyNet extends CustomerNet {
    public MyNet() {
        super();
    }

    @Override
    public void afterLoadNet() {
        // 创建施工区
        IRoadWorkZone rwz = this.createRoadWorkZone();
        if (rwz != null) {
            System.out.println("施工区创建成功");

            // 获取施工区信息
            long rwzId = rwz.id();
            // 施工区位置，单位：m
            double location = p2m(rwz.location());
            // 施工区长度，单位：m
            double zoneLength = p2m(rwz.zoneLength());
            // 施工区上游警示区长度，单位：m
            double upCautionLength = p2m(rwz.upCautionLength());
            // 施工区上游过渡区长度，单位：m
            double upTransitionLength = p2m(rwz.upTransitionLength());
            // 施工区上游缓冲区长度，单位：m
            double upBufferLength = p2m(rwz.upBufferLength());
            // 施工区下游过渡区长度，单位：m
            double downTransitionLength = p2m(rwz.downTransitionLength());
            // 施工区下游终止区长度，单位：m
            double downTerminationLength = p2m(rwz.downTerminationLength());
            // 施工区持续时间，单位：s
            double duration = rwz.duration();
            // 施工区限速，单位：m/s
            double limitSpeed = rwz.limitSpeed();

            // 打印施工区信息
            System.out.println("施工区信息：");
            System.out.printf("\t施工区ID：%d%n", rwzId);
            System.out.printf("\t施工区位置：%.2fm%n", location);
            System.out.printf("\t施工区长度：%.2fm%n", zoneLength);
            System.out.printf("\t施工区上游警示区长度：%.2fm%n", upCautionLength);
            System.out.printf("\t施工区上游过渡区长度：%.2fm%n", upTransitionLength);
            System.out.printf("\t施工区上游缓冲区长度：%.2fm%n", upBufferLength);
            System.out.printf("\t施工区下游过渡区长度：%.2fm%n", downTransitionLength);
            System.out.printf("\t施工区下游终止区长度：%.2fm%n", downTerminationLength);
            System.out.printf("\t施工区持续时间：%.2fs%n", duration);
            System.out.printf("\t施工区限速：%.2fkm/h%n", limitSpeed * 3.6);

            // 创建借道
            IReconstruction reconstruction = this.createReconstruction(rwzId);
            if (reconstruction != null) {
                System.out.println("借道创建成功");

                // 获取借道信息
                long reconstructionId = reconstruction.id();
                // 借用的车道数
                int borrowedNum = reconstruction.borrowedNum();
                // 保通开口长度，单位：m
                double passagewayLength = p2m(reconstruction.passagewayLength());
                // 保通开口限速，单位：m/s
                double passagewayLimitedSpeed = reconstruction.passagewayLimitedSpeed();

                // 打印借道信息
                System.out.println("借道信息：");
                System.out.printf("\t借道ID：%d%n", reconstructionId);
                System.out.printf("\t借用的车道数：%d%n", borrowedNum);
                System.out.printf("\t保通开口长度：%.2fm%n", passagewayLength);
                System.out.printf("\t保通开口限速：%.2fkm/h%n", passagewayLimitedSpeed * 3.6);
            }
        }
    }

    private IRoadWorkZone createRoadWorkZone() {
        TessInterface iface  = TESSNG.tessngIFace();
        NetInterface netiface = iface.netInterface();
        DynaRoadWorkZoneParam param = new DynaRoadWorkZoneParam();
        // 施工区名称
        param.setName("施工区");
        // 施工区所在路段ID
        param.setRoadId(1);
        // 施工区开始位置
        param.setLocation(m2p(2500));
        // 施工区长度，单位：m
        param.setLength(m2p(50));
        // 施工区上游警示区长度，单位：m
        param.setUpCautionLength(m2p(70));
        // 施工区上游过渡区长度，单位：m
        param.setUpTransitionLength(m2p(60));
        // 施工区缓冲区长度，单位：m
        param.setUpBufferLength(m2p(50));
        // 施工区下游过渡区长度，单位：m
        param.setDownTransitionLength(m2p(40));
        // 施工区下游终止区长度，单位：m
        param.setDownTerminationLength(m2p(30));
        // 施工区占用车道序号，从右向左，从0开始
        param.setMlFromLaneNumber(new ArrayList<>(Arrays.asList(3)));
        // 开始时间，单位：s
        param.setStartTime(0);
        // 持续时间，单位：s
        param.setDuration(400);
        // 限速，单位：km/h
        param.setLimitSpeed(72);
        // 创建施工区
        return netiface.createRoadWorkZone(param);
    }

    private IReconstruction createReconstruction(long rwzId) {
        TessInterface iface  = TESSNG.tessngIFace();
        NetInterface netiface = iface.netInterface();
        DynaReconstructionParam param = new DynaReconstructionParam();
        // 施工区ID
        param.setRoadWorkZoneId(rwzId);
        // 被借道的路段ID
        param.setBeBorrowedLinkId(2);
        // 被借道的车道数量
        param.setBorrowedNum(1);
        // 保通开口长度，单位：m
        param.setPassagewayLength(80);
        // 保通开口限速，单位：m/s
        param.setPassagewayLimitedSpeed(40 / 3.6);

        // 创建借道
        return netiface.createReconstruction(param);
    }
}

