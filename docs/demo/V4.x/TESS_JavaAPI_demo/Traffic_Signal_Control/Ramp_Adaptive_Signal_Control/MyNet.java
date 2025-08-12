package TESS_Java_APIDemo.Traffic_Signal_Control.Ramp_Adaptive_Signal_Control;

import com.jidatraffic.tessng.*;

import java.util.ArrayList;
import java.util.List;

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