package TESS_Java_APIDemo.four.PriorityLaneManagement;

import com.jidatraffic.tessng.SWIGTYPE_p_QMainWindow;
import com.jidatraffic.tessng.TessPlugin;
import com.jidatraffic.tessng.TessngFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
public class main {

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
    }
}


