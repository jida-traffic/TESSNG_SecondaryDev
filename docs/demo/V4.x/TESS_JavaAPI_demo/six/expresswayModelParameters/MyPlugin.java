package six.expresswayModelParameters;


import com.jidatraffic.tessng.CustomerNet;
import com.jidatraffic.tessng.CustomerSimulator;
import com.jidatraffic.tessng.TessPlugin;

public class MyPlugin extends TessPlugin {
    private CustomerNet mNetInf;
    private CustomerSimulator mSimuInf;

    private double[] params;

    public MyPlugin(double[] Params) {
        super();
        mNetInf = null;
        mSimuInf = null;
        this.params = Params;
    }

    public void initGui() {
        // 在TESS NG主界面上增加QDockWidget对象
//        TessInterface iface = TESSNG.tessngIFace();



//        QDockWidget dockWidget = new QDockWidget("自定义与TESS NG交互界面", win);
//        dockWidget.setObjectName("mainDockWidget");
//        dockWidget.setFeatures(QDockWidget.DockWidgetFeature.NoDockWidgetFeatures);
//        dockWidget.setAllowedAreas(Qt.DockWidgetArea.LeftDockWidgetArea);
//        dockWidget.setWidget(examleWindow.centralWidget());
//        iface.guiInterface().addDockWidgetToMainWindow(Qt.DockWidgetArea.valueOf(1), dockWidget);
//
//        // 增加菜单及菜单项
//        QMenu menu = new QMenu(menuBar);
//        menu.setObjectName("menuExample");
//        menuBar.addAction(menu.menuAction());
//        menu.setTitle("范例菜单");

//        QAction actionOk = menu.addAction("范例菜单项");
//        actionOk.setCheckable(true);
//        actionOk.triggered.connect(examleWindow, "isOk()");
    }

    // 重写父类方法，在TESS NG工厂类创建TESS NG对象时调用
    @Override
    public void init() {
        mNetInf = new MyNet();
        mSimuInf = new MySimulator(params);
        initGui();


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
