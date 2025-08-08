package Fundamental_Functions.basic_example;
import com.jidatraffic.tessng.CustomerNet;
import com.jidatraffic.tessng.CustomerSimulator;
import com.jidatraffic.tessng.TessPlugin;

public class MyPlugin extends TessPlugin
{
    private MyNet mNet;
    private MySimulator mSimu;

    public MyPlugin()
    {
        super();
    }

    @Override
    public void init() {
        mNet = new MyNet();
        mSimu = new MySimulator();
    }

    @Override
    public CustomerSimulator customerSimulator() {
        return mSimu;
    }

    @Override
    public CustomerNet customerNet() {
        return mNet;
    }
}
