package TESS_Java_APIDemo.four.VariableLaneManagement;



import com.jidatraffic.tessng.*;


public class MyPlugin extends TessPlugin {
    private MyNet mNetInf;
    private MySimulator mSimuInf;
    private TESS_API_EXAMPLE examleWindow;

    public MyPlugin() {
        super();
        this.mNetInf = null;
        this.mSimuInf = null;
    }

    /**
     * 初始化图形界面相关功能（移除QT部分后简化）
     */
    public void initGui() {
        // 创建示例窗口（非QT版本）
        this.examleWindow = new TESS_API_EXAMPLE();

        // 获取TESS NG接口
        TessInterface iface = TESSNG.tessngIFace();
        if (iface == null) return;

        // 注：原QT相关的DockWidget代码已移除
        // 菜单创建逻辑在非QT环境下需要根据实际UI框架重新实现

        // 此处仅保留核心逻辑示意
        setupMenuActions(iface);
    }

    /**
     * 设置菜单动作（简化版）
     */
    private void setupMenuActions(TessInterface iface) {
        // 在实际实现中，需要根据TESS NG提供的Java API创建菜单
        // 这里仅保留动作关联逻辑
        if (examleWindow != null) {
            // 模拟菜单动作触发
            // 实际应通过TESS NG的菜单API注册动作
        }
    }

    /**
     * 初始化插件
     */
    @Override
    public void init() {
        initGui();

        // 初始化网络和仿真接口
        this.mNetInf = new MyNet();
        this.mSimuInf = new MySimulator();

        // 注册运行信息回调
//        if (this.mSimuInf != null && this.examleWindow != null) {
//            this.mSimuInf.setRunInfoListener(message ->
//                    examleWindow.showRunInfo(message)
//            );
//        }

        // 获取主窗口并设置仿真控制回调
//        TessInterface iface = TESSNG.tessngIFace();
//        if (iface != null) {
//            IGuiInterface guiInterface = iface.guiInterface();
//            if (guiInterface != null) {
//                Object mainWindow = guiInterface.mainWindow();
//                if (mainWindow != null && mSimuInf != null) {
//                    // 设置仿真控制回调（模拟QT信号槽机制）
//                    mSimuInf.setStopSimuListener(() -> {
//                        // 调用主窗口的停止仿真方法
//                        if (mainWindow instanceof MainWindow) {
//                            ((MainWindow) mainWindow).doStopSimu();
//                        }
//                    });
//
//                    mSimuInf.setRestartSimuListener(() -> {
//                        // 调用主窗口的重启仿真方法
//                        if (mainWindow instanceof MainWindow) {
//                            ((MainWindow) mainWindow).doStartSimu();
//                        }
//                    });
//                }
//            }
//        }
    }

    /**
     * 返回自定义路网接口
     */
    @Override
    public CustomerNet customerNet() {
        return mNetInf;
    }

    /**
     * 返回自定义仿真接口
     */
    @Override
    public CustomerSimulator customerSimulator() {
        return mSimuInf;
    }


}

