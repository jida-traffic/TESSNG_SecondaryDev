package TESS_Java_APIDemo.Intelligent_Connected_and_Autonomous_Driving.Connected_and_Autonomous_Vehicle_Platooning;

//import com.jidatraffic.tessng.*;

import com.jidatraffic.tessng.TessngFactory;

import java.io.IOException;

public class Main {
    static {
        try {
            System.loadLibrary("TESS_WIN");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Adding and calling a normal C++ callback");

        MyPlugin myPlugin = new MyPlugin();
        myPlugin.init();

        TessngFactory tessngFactory = new TessngFactory();
        tessngFactory.build(myPlugin, "D:\\TESSNG_4.0.20");

        System.out.println("java exit");
    }
}
