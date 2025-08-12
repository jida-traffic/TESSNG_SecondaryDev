package TESS_Java_APIDemo.SecondaryDevCasesMaster;

import com.jidatraffic.tessng.*;


public class MyNet extends CustomerNet {

    public MyNet() {
        super();
    }

    /**
     * 重写父类方法，当打开路网后TESS NG调用此方法
     * 实现的逻辑是：路网加载后获取路段数，如果路网数为0则调用方法createNet构建路网，
     * 之后再次获取路段数，如果大于0则启动仿真
     */
    @Override
    public void afterLoadNet()  {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        NetInterface netIface = iface.netInterface();
        //获取路段数
    }

    /**
     * 是否允许用户对路网元素的绘制进行干预，如选择路段标签类型、确定绘制颜色等
     * 本方法目的在于减少不必要的对Java方法调用频次
     */
    @Override
    public boolean isPermitForCustDraw() {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        NetInterface netIface = iface.netInterface();
        String netFileName = netIface.netFilePath();

        // 检查路网文件路径中是否包含"Temp"
        return netFileName.contains("Temp");
    }

    @Override
    public void labelNameAndFont(int itemType, long itemId, ObjInt outPropName, ObjReal outFontSize)
    {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        SimuInterface simuiface = iface.simuInterface();
        if(simuiface.isRunning()){
            outPropName.setValue(GraphicsItemPropName.None.swigValue());
            return;
        }
        outPropName.setValue(GraphicsItemPropName.Id.swigValue());
        outFontSize.setValue(6);
        outPropName.setValue(GraphicsItemPropName.Name.swigValue());
    }

    @Override
    public boolean isDrawLaneCenterLine(long laneId){
        return true;
    }



    @Override
    public boolean isDrawLinkCenterLine(long linkId)
    {
        if(linkId == 1){
            return false;
        }else{
            return true;
        }
    }
}

