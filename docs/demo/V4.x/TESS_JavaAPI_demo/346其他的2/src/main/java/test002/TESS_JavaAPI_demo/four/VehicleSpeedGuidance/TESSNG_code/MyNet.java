package test002.TESS_JavaAPI_demo.four.VehicleSpeedGuidance.TESSNG_code;

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
    public void afterLoadNet() {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        NetInterface netIface = iface.netInterface();
        //获取路段数
        int count = netIface.linkCount();
        if(count == 0){
            createNet();
        }
    }

    private void createNet() {
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        NetInterface netIface = iface.netInterface();
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


    /**
     * 过载的父类方法，在绘制路网元素时被调用
     * 确定用ID或名称，以及字体大小绘制标签，也可确定不绘制标签
     * @param itemType 路网元素类型
     * @param itemId 路网元素ID
     * @param outPropName 输出参数，标签类型
     * @param outFontSize 输出参数，标签大小（米）
     */
    public void refLabelNameAndFont(int itemType, int itemId,
                                    GraphicsItemPropName[] outPropName, float[] outFontSize) {
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        SimuInterface simuiface = iface.simuInterface();

        // 如果仿真正在进行，不绘制标签
        if (simuiface.isRunning()) {
            outPropName[0] = GraphicsItemPropName.None;
            return;
        }

        // 默认绘制ID，标签大小为6米
        outPropName[0] = GraphicsItemPropName.Id;
        outFontSize[0] = 6;

        // 如果是连接段一律绘制名称
        if (itemType == NetItemType.getGConnectorType()) {
            outPropName[0] = GraphicsItemPropName.Name;
        }
        // 特定路段绘制名称
        else if (itemType == NetItemType.getGLinkType()) {
            if (itemId == 1 || itemId == 5 || itemId == 6) {
                outPropName[0] = GraphicsItemPropName.Name;
            }
        }
    }

    /**
     * 过载父类方法，是否绘制车道中心线
     * @param laneId 车道ID
     * @return 是否绘制
     */
    public boolean isDrawLaneCenterLine(int laneId) {
        return true;
    }

    /**
     * 过载父类方法，是否绘制路段中心线
     * @param linkId 路段ID
     * @return 是否绘制
     */
    public boolean isDrawLinkCenterLine(int linkId) {
        return linkId != 1; // 路段ID为1时不绘制，其他都绘制
    }

    /**
     * 绘制自定义文本说明
     * @param itemType 元素类型
     * @param itemId 元素ID
     * @param painter 绘图对象
     */
//    public void paint(NetItemType itemType, int itemId, Graphics painter) {
//        if (!textAdded) {
//            textAdded = true;
//
//            INetInterface netiface = tessngIFace.netInterface();
//            GraphicsScene scene = netiface.graphicsScene();
//
//            // 创建文本说明项
//            QGraphicsTextItem legendItem = new QGraphicsTextItem(
//                    "橙车：普通车 白车：CV车(非引导状态) 绿车：CV车(降速状态) 红车：CV车(提速状态)");
//
//            // 设置位置、字体和颜色
//            legendItem.setPos(new Point(-1200, 20));
//
//            // 创建黑体20号字体
//            Font font = new Font("SimHei", Font.PLAIN, 20);
//            legendItem.setFont(font);
//
//            // 设置黑色文本
//            legendItem.setDefaultTextColor(Color.BLACK);
//
//            // 添加到场景
//            scene.addItem(legendItem);
//        }
//    }
}

