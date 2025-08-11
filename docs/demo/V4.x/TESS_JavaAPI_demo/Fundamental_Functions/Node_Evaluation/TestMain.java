//package Fundamental_Functions.Node_Evaluation;
//
////import com.jidatraffic.tessng.*;
//
//import com.jidatraffic.tessng.TessPlugin;
//import com.jidatraffic.tessng.TessngFactory;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class TestMain {
//    static {
//        try {
//            System.loadLibrary("TESS_WIN");
//        } catch (UnsatisfiedLinkError e) {
//            System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
//            System.exit(1);
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        System.out.println("Adding and calling a normal C++ callback");
//
//        String workingDirectory = System.getProperty("user.dir");
//        System.out.println("工作目录(一般是tessng安装目录):"+workingDirectory);
//        String configPath = workingDirectory+"\\config.json";
//        String configJson =new String(Files.readAllBytes(Paths.get(configPath)));
//
//        TestPlugin myPlugin = new TestPlugin();
//        myPlugin.init();
//
//        TessngFactory tessngFactory = new TessngFactory();
//        tessngFactory.build((TessPlugin)myPlugin, "D:\\TESSNG_4.0.20");
//
//        System.out.println("java exit");
//    }
//}
