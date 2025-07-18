package com.jogamp.opengl.demos.android;

import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.demos.android.LauncherUtil.BaseActivityLauncher;
import com.jogamp.opengl.demos.android.LauncherUtil.OrderedProperties;

public class NEWTGraphUISceneDemo20Activity1pLauncher extends LauncherUtil.BaseActivityLauncher {
    static String demo = "com.jogamp.opengl.demos.android.NEWTGraphUISceneDemo20Activity1p";
    static String[] sys_pkgs = new String[] { "com.jogamp.common", "com.jogamp.openal", "com.jogamp.opengl" };
    static String[] usr_pkgs = new String[] { "com.jogamp.opengl.demos" };

    @Override
    public void init() {
       final OrderedProperties props = getProperties();
       // props.setProperty("jogamp.debug.JNILibLoader", "true");
       // props.setProperty("jogamp.debug.NativeLibrary", "true");
       // props.setProperty("jogamp.debug.NativeLibrary.Lookup", "true");
       // props.setProperty("jogamp.debug.IOUtil", "true");
       // props.setProperty("nativewindow.debug", "all");
       props.setProperty("nativewindow.debug.GraphicsConfiguration", "true");
       // props.setProperty("jogl.debug", "all");
       // props.setProperty("jogl.debug.GLProfile", "true");
       props.setProperty("jogl.debug.GLDrawable", "true");
       props.setProperty("jogl.debug.GLContext", "true");
       props.setProperty("jogl.debug.GLSLCode", "true");
       props.setProperty("jogl.debug.CapabilitiesChooser", "true");
       // props.setProperty("jogl.debug.GLSLState", "true");
       // props.setProperty("jogl.debug.DebugGL", "true");
       // props.setProperty("jogl.debug.TraceGL", "true");
       // props.setProperty("newt.debug", "all");
       props.setProperty("newt.debug.Window", "true");
       // props.setProperty("newt.debug.Window.MouseEvent", "true");
       // props.setProperty("newt.debug.Window.KeyEvent", "true");
    }

    @Override
    public String getActivityName() {
        return demo;
    }

    @Override
    public List<String> getSysPackages() {
        return Arrays.asList(sys_pkgs);
    }

    @Override
    public List<String> getUsrPackages() {
        return Arrays.asList(usr_pkgs);
    }
}
