package test002.TESS_JavaAPI_demo.four.VariableLaneManagement;

import com.jidatraffic.tessng.NetInterface;
import com.jidatraffic.tessng.SimuInterface;
import com.jidatraffic.tessng.TESSNG;
import com.jidatraffic.tessng.TessInterface;

import javax.swing.*;
import java.io.File;

public class TESS_API_EXAMPLE {
    // 模拟TESSNG接口实例
    private static TessInterface iface;

    public TESS_API_EXAMPLE(){
        // 初始化接口
        iface = TESSNG.tessngIFace();
        // 这里可以添加测试代码
//        openNet();
//        startSimu();

//        pauseSimu();
//        stopSimu();
    }


    /**
     * 打开路网文件
     */
    public void openNet() {
        if (iface == null) {
            showWarning("TESSNG接口初始化失败");
            return;
        }

        SimuInterface simuInterface = iface.simuInterface();
        if (simuInterface != null && simuInterface.isRunning()) {
            showWarning("请先停止仿真，再打开路网");
            return;
        }

        String dbDir = getDefaultDataDir();

        String netFilePath = chooseNetFile(dbDir);

        if (netFilePath != null && !netFilePath.isEmpty()) {
            NetInterface netInterface = iface.netInterface();
            if (netInterface != null) {
                netInterface.openNetFle(netFilePath);
            }
        }
    }

    /**
     * 开始仿真
     */
    public void startSimu() {
//        if (iface == null) {
//            System.out.println("空");
//            return;
//        }
        SimuInterface simuInterface = iface.simuInterface();
        if (simuInterface != null) {
            if (!simuInterface.isRunning() || simuInterface.isPausing()) {
                simuInterface.startSimu();
            }
        }
    }

    /**
     * 暂停仿真
     */
    public void pauseSimu() {
        if (iface == null) return;

        SimuInterface simuInterface = iface.simuInterface();
        if (simuInterface != null && simuInterface.isRunning()) {
            simuInterface.pauseSimu();
        }
    }

    /**
     * 停止仿真
     */
    public void stopSimu() {
        if (iface == null) return;

        SimuInterface simuInterface = iface.simuInterface();
        if (simuInterface != null && simuInterface.isRunning()) {
            simuInterface.stopSimu();
        }
    }

    /**
     * 显示运行信息
     */
    public void showRunInfo(String runInfo) {
        // 原QT文本框显示逻辑，这里改为控制台输出
        System.out.println("运行信息: " + runInfo);
    }

    /**
     * 显示确认信息
     */
    public void isOk() {
        showInfo("操作成功", "提示信息");
    }

    // 辅助方法：获取默认数据目录
    private String getDefaultDataDir() {
        // 获取当前类所在目录
        String currentDir = new File(TESS_API_EXAMPLE.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getParent();
        // 构建数据目录路径
        return currentDir + File.separator + "Data";
    }

    // 辅助方法：选择路网文件（简化实现）
    private String chooseNetFile(String defaultDir) {
        // 实际应用中应使用文件选择器
        // 这里仅作示例返回
        File dataDir = new File(defaultDir);
        if (dataDir.exists() && dataDir.isDirectory()) {
            // 查找目录下的.tess文件
            File[] tessFiles = dataDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".tess") || name.toLowerCase().endsWith(".backup"));

            if (tessFiles != null && tessFiles.length > 0) {
                return tessFiles[0].getAbsolutePath();
            }
        }
        return null;
    }

    // 辅助方法：显示警告信息
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message, "提示信息", JOptionPane.WARNING_MESSAGE);
    }

    // 辅助方法：显示信息
    private void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }


}

