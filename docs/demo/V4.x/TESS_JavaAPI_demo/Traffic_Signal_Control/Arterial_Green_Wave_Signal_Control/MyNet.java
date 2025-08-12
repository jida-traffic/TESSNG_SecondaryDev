package TESS_Java_APIDemo.Traffic_Signal_Control.Arterial_Green_Wave_Signal_Control;

import com.jidatraffic.tessng.JCustomerNet;
import com.jidatraffic.tessng.NetInterface;
import com.jidatraffic.tessng.TESSNG;
import com.jidatraffic.tessng.TessInterface;

public class MyNet extends JCustomerNet {

    public MyNet() {
        super();
    }

    /**
     * 创建路网
     */
    public void createNet() {
        // 代表TESS NG的接口
        TessInterface iface = TESSNG.tessngIFace();
        // 代表TESS NG的路网子接口
        NetInterface netiface = iface.netInterface();
    }
}