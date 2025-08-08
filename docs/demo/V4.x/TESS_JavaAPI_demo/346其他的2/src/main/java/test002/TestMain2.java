package test002;

//import com.jidatraffic.tessng.*;

import com.jidatraffic.tessng.*;
import io.qt.core.QPointF;
import test002.net.*;
import test002.TestPlugin2;

import javax.json.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.jidatraffic.tessng.TESSNG.m2p;

public class TestMain2 {
    static {
        try {
            System.loadLibrary("TESS_WIN");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
            System.exit(1);
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Adding and calling a normal C++ callback");

        String configPath = "C:\\TESSNG_4.0.20\\config.json";
        String configJson =new String(Files.readAllBytes(Paths.get(configPath)));

        TestPlugin2 myPlugin = new TestPlugin2();
        myPlugin.init();

        TessngFactory tessngFactory = new TessngFactory();
//        tessngFactory.build(myPlugin, configJson);
        tessngFactory.build(myPlugin, "C:\\TESSNG_4.0.20");


        //#######################测试#######################

        int[] indexs = new int[17];
        for (int i = 0; i < indexs.length; i++) {
            indexs[i] = i + 1; // 索引从0开始，值从1开始
        }
        for(int index:indexs){
//            TestILink.testLink(index);
//            TestILaneConnector.testLink(index);
//            TestIRouting.testLink(index);
//            TestILane.testLane(index);
//            TestIAccidentZone.testAccidentZone(index);
//            TestIBusStationLine.testBusStationLine(index);
//            TestIGuidArrow.testGuidArrow(index);
//            TestILane.testLane(index);
//            TestILaneConnector.testLink(index);
//            TestILimitedZone.testLimitedZone(index);
//            TestILink.testLink(index);
//            TestIParkingDecisionPoint.testParkingDecisionPoint(index);
//            TestIParkingRegion.testParkingRegion(index);
//            TestIPedestrian.testPedestrian(index);
//            TestIPedestrianCrossWalkRegion.testPedestrianCrossWalkRegion(index);
//            TestIPedestrianEllipseRegion.testPedestrianEllipseRegion(index);
//            TestIPedestrianFanShapeRegion.testPedestrianFanShapeRegion(index);
//            TestIPedestrianSideWalkRegion.testPedestrianSideWalkRegion(index);
//            TestIPedestrianStairRegion.testPedestrianStairRegion(index);
//            TestIReconstruction.testReconstruction(index);
//            TestIReduceSpeedArea.testIReduceSpeedArea(index);
//            TestIReduceSpeedVehiType.testIReduceSpeedVehiType(index);
//            TestIRoadWorkZone.testIRoadWorkZone(index);
//            TestIRouting.testLink(index);
//            TestISignalLamp.testISignalLamp(index);
//            TestISignalPhase.testISignalPhase(index);
//            TestISignalPlan.testISignalPlan(index);
//            TestITollDecisionPoint.testTollDecisionPoint(index);
//            TestITollLane.testTollLane(index);
//            TestITollRouting.testTollRouting(index);
//            TestIVehicleDrivInfoCollector.testVehicleDrivInfoCollector(index);
//            TestIVehicleQueueCounter.testVehicleQueueCounter(index);
            TestIVehicleTravelDetector.testVehicleTravelDetector(index);
            TestIReconstruction.testReconstruction(index);

        }
        //##########################
            System.out.println("java exit");
    }
}

