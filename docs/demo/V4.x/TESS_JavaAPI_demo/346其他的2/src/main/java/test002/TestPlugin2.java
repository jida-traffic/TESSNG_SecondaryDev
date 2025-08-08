package test002;
import com.jidatraffic.tessng.CustomerNet;
import com.jidatraffic.tessng.CustomerSimulator;
import com.jidatraffic.tessng.TessPlugin;

public class TestPlugin2 extends TessPlugin
{
    private TestNet2 mNet;
//    private TestSimulator2 mSimu;

    public TestPlugin2()
    {
        super();
    }

    @Override
    public void init() {
        mNet = new TestNet2();
//        mSimu = new TestSimulator2();
    }

//    @Override
//    public CustomerSimulator customerSimulator() {
//        return mSimu;
//    }

    @Override
    public CustomerNet customerNet() {
        return mNet;
    }
}
