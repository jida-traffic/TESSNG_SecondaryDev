package six.expresswayModelParameters;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;

public class MySimulator extends  CustomerSimulator {
    private double[] params;

    // 跟驰模型参数
    private double followingModelAlpha = 5;
    private double followingModelBeit = 3;
    private double followingModelSafeDistance = 15;
    private double followingModelSafeInterval = 1.5;

    public MySimulator() {
        // 默认构造函数
    }

    public MySimulator(double[] params) {
        super();
        this.params = params;
    }

    @Override
    public void beforeStart(ObjBool keepOn) {
        // 设置是否继续启动仿真
        keepOn.setValue(true);

        // 启发式算法参数设置
        if (params != null && params.length >= 4) {
            this.followingModelAlpha = params[0];
            this.followingModelBeit = params[1];
            this.followingModelSafeDistance = params[2];
            this.followingModelSafeInterval = params[3];
        }

        // 打印当前跟驰参数
        System.out.println("仿真跟驰参数列表：");
        System.out.println("[" + followingModelAlpha + ", "
                + followingModelBeit + ", "
                + followingModelSafeDistance + ", "
                + followingModelSafeInterval + "]");
    }

    @Override
    public ArrayList<FollowingModelParam> reSetFollowingParams() {
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuInterface = iface.simuInterface();
        long batchNum = simuInterface.batchNumber();

        // 仅在初始批次修改跟驰参数
        if (batchNum < 2) {
            ArrayList<FollowingModelParam> paramsList = new ArrayList<>();

            // 机动车跟驰参数
            FollowingModelParam motorParam = new FollowingModelParam();
            motorParam.setVtype(MotorOrNonmotor.Motor); // 设置车辆类型为机动车
            motorParam.setAlfa(followingModelAlpha); // 设置alpha参数
            motorParam.setBeit(followingModelBeit); // 设置beit参数
            motorParam.setSafeDistance(followingModelSafeDistance); // 设置安全距离
            motorParam.setSafeInterval(followingModelSafeInterval); // 设置安全时距
            paramsList.add(motorParam);

// 非机动车跟驰参数
            FollowingModelParam nonmotorParam = new FollowingModelParam();
            nonmotorParam.setVtype(MotorOrNonmotor.Nonmotor); // 设置车辆类型为非机动车
            nonmotorParam.setAlfa(3); // 设置alpha参数
            nonmotorParam.setBeit(1); // 设置beit参数
            nonmotorParam.setSafeDistance(5); // 设置安全距离
            nonmotorParam.setSafeInterval(6); // 设置安全时距
            paramsList.add(nonmotorParam);

            return paramsList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void afterStop() {
        // 仿真结束后退出
        System.exit(0);
    }
}