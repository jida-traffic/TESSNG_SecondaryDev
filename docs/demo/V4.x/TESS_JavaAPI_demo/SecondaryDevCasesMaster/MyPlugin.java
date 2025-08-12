package TESS_Java_APIDemo.SecondaryDevCasesMaster;


import com.jidatraffic.tessng.*;

public class MyPlugin extends TessPlugin {
    private CustomerNet mNetInf;
    private CustomerSimulator mSimuInf;

    public MyPlugin() {
        super();
        mNetInf = null;
        mSimuInf = null;
    }

    public void initGui() {
        // 在TESS NG主界面上增加QDockWidget对象
        TessInterface iface = TESSNG.tessngIFace();

    }

    // 重写父类方法，在TESS NG工厂类创建TESS NG对象时调用
    @Override
    public void init() {

        mNetInf = new MyNet();
        TessApiExample example = new  TessApiExample();
        mSimuInf = new MySimulator(new SecondaryDevelopmentCases(2));
        initGui();
        int choice = 6;
        switch (choice) {
            case 1:
                System.out.println("动态修改发车流量测试(L5路段发车点)");
                example.calcDynaDispatchParametersExample();
                break;
            case 2:
                System.out.println("车辆移动测试(L5路段)");
                example.moveVehiExample();
                break;
            case 3:
                System.out.println("车辆速度设置测试(L5路段)");
                example.setVehiSpeedExample();
                break;
            case 4:
                System.out.println("修改车辆路径测试(L1路段)");
                example.setVehiRoutingExample();
                break;
            case 5:
                System.out.println("强制车辆不变道(禁止车辆变道到最右侧车道)");
                example.forceVehiDontChangeLaneExample();
                break;
            case 6:
                System.out.println("强制车辆变道(最右侧车辆左变道)");
                example.forceVehiChangeLaneExample();
                break;
            case 7:
                System.out.println("强制车辆闯红灯(L12路段概率闯红灯)");
                example.runRedLightExample();
                break;
            case 8:
                System.out.println("清除L12车辆");
                example.stopVehiExample();
                break;
            case 9:
                System.out.println("设置L5车辆航向角(45度)");
                example.setVehiAngleExample();
                break;
            case 10:
                System.out.println("设置车辆停车(L5)");
                example.setVehiParkExample();
                break;
            case 11:
                System.out.println("取消车辆控制");
                example.cancelSetVehiExample();
                break;
            case 12:
                System.out.println("修改相位示例");
                example.editPhaseExample();
                break;
            case 13:
                System.out.println("修改路段最高限速示例");
                example.updateLinkLimitSpeed();
                break;
            case 14:
                System.out.println("启动仿真");
                example.startSimulation();
                break;
            case 15:
                System.out.println("暂停仿真");
                example.pauseSimulation();
                break;
            case 16:
                System.out.println("停止仿真");
                example.stopSimulation();
                break;
            case 17:
                System.out.println("恢复仿真");
                example.restoreSimulation();
                break;
            case 18:
                System.out.println("生成仿真快照");
                example.generateSnapshot();
                break;
            case 19:
                System.out.println("查询路段或车道车辆示例");
                example.findVehiInLinkOrLaneInformation();
                break;
            case 20:
                System.out.println("查询车辆信息示例");
                example.findVehicleInformation();
                break;
            case 21:
                System.out.println("设置仿真精度示例");
                example.setSimulationAccuracy();
                break;
            case 22:
                System.out.println("设置仿真时长示例");
                example.setSimulationInterval();
                break;
            case 23:
                System.out.println("设置仿真加速比示例");
                example.setSimulationAcceMultiples();
                break;
            case 0:
                // 退出程序，由主循环处理
                break;
            default:
                System.out.println("无效的操作编号，请重新输入");
        }


        // 信号与槽连接（Java中使用Qt Jambi的信号槽机制）
        // mSimuInf.signalRunInfo.connect(examleWindow, "showRunInfo(String)");

//        TessInterface iface = TESSNG.tessngIFace();

        // 将信号关联到主窗体的槽函数
        // mSimuInf.forStopSimu.connect(win, "doStopSimu()", Qt.ConnectionType.QueuedConnection);
        // mSimuInf.forReStartSimu.connect(win, "doStartSimu()", Qt.ConnectionType.QueuedConnection);
    }

    // 重写父类方法，返回插件路网子接口
    @Override
    public CustomerNet customerNet() {
        return mNetInf;
    }

    // 重写父类方法，返回插件仿真子接口
    @Override
    public CustomerSimulator customerSimulator() {
        return mSimuInf;
    }
}
