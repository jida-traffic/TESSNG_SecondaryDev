package Traffic_Signal_Control.Multitime_Period_And_Multischeme_Signal_Control;
import com.jidatraffic.tessng.CustomerNet;
import com.jidatraffic.tessng.CustomerSimulator;
import com.jidatraffic.tessng.TessPlugin;

public class MyPlugin extends TessPlugin
{
    private MyNet mNet;


    public MyPlugin()
    {
        super();
    }

    @Override
    public void init() {
        mNet = new MyNet();

    }

    @Override
    public CustomerNet customerNet() {
        return mNet;
    }
}
