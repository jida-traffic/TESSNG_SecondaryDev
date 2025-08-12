package TESS_Java_APIDemo.Intelligent_Connected_and_Autonomous_Driving.Connected_and_Intelligent_Vehicle_Accident_Avoidance;

import com.jidatraffic.tessng.*;

public class MyNet extends JCustomerNet {

    public MyNet() {
        super();
    }

    /**
     * 过载的父类方法，当打开网后TESS NG调用此方法
     * 实现的逻辑是：路网加载后获取路段数，如果路网数为0则调用方法createNet构建路网，
     * 之后再次获取路段数，如果大于0则启动仿真
     */
    @Override
    public void afterLoadNet() {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        NetInterface netiface = iface.netInterface();
        // 获取路段数
        int count = netiface.linkCount();
    }

    /**
     * 是否允许用户对路网元素的绘制进行干预，如选择路段标签类型、确定绘制颜色等
     * 本方法目的在于减少不必要的对python方法调用频次
     */
    @Override
    public boolean isPermitForCustDraw() {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        NetInterface netface = iface.netInterface();
        String netFileName = netface.netFilePath();
        if (netFileName != null && netFileName.contains("Temp")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 过载的父类方法，在绘制路网元素时被调用，确定用ID或名称，以及字体大小绘制标签，也可确定不绘制标签
     * @param itemType NetItemType常量，代表不同类型路网元素
     * @param itemId 路网元素的ID
     * @param ref_outPropName 返回值，GraphicsItemPropName枚举类型，影响路段和连接段的标签是否被绘制
     *                        GraphicsItemPropName.None表示不绘制，
     *                        GraphicsItemPropName.Id：表示绘制ID，
     *                        GraphicsItemPropName.Name:表示绘制名称
     * @param ref_outFontSize 返回值，标签大小，单位：米。
     *                        假设车道宽度是3.5米，如果ref_outFontSize.value等于7，绘制的标签大小占两个车道宽度
     */
    @Override
    public void ref_labelNameAndFont(int itemType, long itemId, ObjInt ref_outPropName, ObjReal ref_outFontSize) {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG仿真子接口
        SimuInterface simuiface = iface.simuInterface();

        // 如果仿真正在进行，设置ref_outPropName.value等于GraphicsItemPropName.None，
        // 路段和车道都不绘制标签
        if (simuiface.isRunning()) {
            ref_outPropName.setValue(GraphicsItemPropName.None.swigValue());
            return;
        }

        // 默认绘制ID
        ref_outPropName.setValue(GraphicsItemPropName.Id.swigValue());
        // 标签大小为6米
        ref_outFontSize.setValue(6);

        // 如果是连接段一律绘制名称
        if (itemType == NetItemType.getGConnectorType()) {
            ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
        } else if (itemType == NetItemType.getGLinkType()) {
            if (itemId == 1 || itemId == 5 || itemId == 6) {
                ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
            }
        }
    }

    /**
     * 过载父类方法，是否绘制车道中心线
     */
    @Override
    public boolean isDrawLaneCenterLine(long laneId) {
        return true;
    }

    /**
     * 过载父类方法，是否绘制路段中心线
     */
    @Override
    public boolean isDrawLinkCenterLine(long linkId) {
        if (linkId == 1) {
            return false;
        } else {
            return true;
        }
    }
}
