package TESS_Java_APIDemo.six.IntersectionModelParameters;

import com.jidatraffic.tessng.TessngFactory;

public class tessRunner {
    static {
        try {
            System.loadLibrary("C:\\TESSNG_4.0.20\\TESS_WIN.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
            System.exit(1);
        }
    }

    public static void tessRun(double[] Params){
        System.out.println("Adding and calling a normal C++ callback");
        MyPlugin myPlugin = new MyPlugin(Params);
        myPlugin.init();
        TessngFactory tessngFactory = new TessngFactory();
        tessngFactory.build(myPlugin, "C:\\TESSNG_4.0.20");
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("需要4个参数: alpha beit safeDistance safeInterval");
            System.exit(1);
        }

        try {
            double alpha = Double.parseDouble(args[0]);
            double beit = Double.parseDouble(args[1]);
            double safeDistance = Double.parseDouble(args[2]);
            double safeInterval = Double.parseDouble(args[3]);

            // 调用实际的仿真方法
            tessRun(new double[]{alpha, beit, safeDistance, safeInterval});

        } catch (NumberFormatException e) {
            System.err.println("参数格式错误: " + e.getMessage());
            System.exit(2);
        }
    }
}
