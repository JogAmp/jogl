
REM set TEMP=\\jordan\data\Incoming\windows\temp
REM set TMP=\\jordan\data\Incoming\windows\temp
REM set TEMP=C:\Documents and Settings\jogamp\temp
REM set TMP=C:\Documents and Settings\jogamp\temp

REM set LIBGL_DEBUG=verbose
REM set MESA_DEBUG=true
set LIBGL_ALWAYS_SOFTWARE=true
REM set INTEL_DEBUG="buf bat"
REM set INTEL_STRICT_CONFORMANCE=1

set BLD_SUB=build-win64
set J2RE_HOME=c:\jdk-21
set JAVA_HOME=c:\jdk-21
set ANT_PATH=C:\apache-ant-1.10.5

set PROJECT_ROOT=D:\projects\jogamp\jogl
set BLD_DIR=..\%BLD_SUB%

set MESA3D_LIB=C:\Mesa3D\x64
REM set FFMPEG_LIB=C:\ffmpeg-5.1.2-full_build-shared\bin
set FFMPEG_LIB=C:\ffmpeg-n6.1-latest-win64-gpl-shared-6.1\bin

REM set PATH=%JAVA_HOME%\bin;%ANT_PATH%\bin;c:\mingw\bin;%PATH%
REM set PATH=%JAVA_HOME%\bin;%ANT_PATH%\bin;%PROJECT_ROOT%\make\lib\external\PVRVFrame\OGLES-2.0\Windows_x86_64;%PATH%
set PATH=%J2RE_HOME%\bin;%JAVA_HOME%\bin;%ANT_PATH%\bin;C:\temp;%MESA3D_LIB%;%FFMPEG_LIB%;%PATH%
echo PATH %PATH%

set CP_ALL=.;%BLD_DIR%\jar\jogl-all.jar;%BLD_DIR%\jar\atomic\oculusvr.jar;%BLD_DIR%\jar\jogl-test.jar;%BLD_DIR%\jar\jogl-demos.jar;..\..\joal\%BLD_SUB%\jar\joal.jar;..\..\gluegen\%BLD_SUB%\gluegen-rt.jar;..\..\gluegen\%BLD_SUB%\gluegen-test-util.jar;..\..\gluegen\make\lib\junit.jar;%ANT_PATH%\lib\ant.jar;%ANT_PATH%\lib\ant-junit.jar;%BLD_DIR%\..\make\lib\swt\win32-win32-x86_64\swt.jar

echo CP_ALL %CP_ALL%

REM set D_ARGS="-Djogamp.debug=all"
REM set D_ARGS="-Djogl.debug=all" "-Dnativewindow.debug=all"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogamp.debug=all" "-Djogl.debug.EGLDrawableFactory.DontQuery"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogl.debug.windows.cpu_affinity_mode=0" "-Djogl.debug.GLContext.TraceSwitch" "-Djogl.debug.GLContext"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogl.debug.GLContext.TraceSwitch" "-Djogl.debug.GLContext"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogl.debug.GLContext"
REM set D_ARGS="-Djogl.disable.opengles"
REM set D_ARGS="-Djogl.disable.openglcore"
REM set D_ARGS="-Djogl.disable.openglarbcontext"
REM set D_ARGS="-Dnativewindow.debug.GDIUtil" "-Dnativewindow.debug.RegisteredClass"
REM set D_ARGS="-Djogl.debug.GLContext"
REM set D_ARGS="-Djogl.debug.GLContext" "-Djogl.debug.FBObject"
REM set D_ARGS="-Djogl.debug.GLDrawable" "-Djogl.debug.EGLDrawableFactory.DontQuery"
REM set D_ARGS="-Djogl.debug.GLDrawable" "-Djogl.debug.EGLDrawableFactory.QueryNativeTK"
REM set D_ARGS="-Djogl.debug.GLDrawable" "-Djogl.debug.GLContext" "-Djogl.debug.GLCanvas"
REM set D_ARGS="-Dnativewindow.debug.GraphicsConfiguration -Djogl.debug.CapabilitiesChooser -Djogl.debug.GLProfile"
REM set D_ARGS="-Djogamp.debug.IOUtil"
REM set D_ARGS="-Djogl.debug.GLSLCode"
REM set D_ARGS="-Djogl.debug.GLSLCode" "-Djogl.debug.GLMediaPlayer"
REM set D_ARGS="-Djogl.debug.GLMediaPlayer"
REM set D_ARGS="-Djogl.debug.GLMediaPlayer" "-Djogl.debug.AudioSink"
REM set D_ARGS="-Djogl.debug.GLMediaPlayer" "-Djogl.debug.GLMediaPlayer.Native"
REM set D_ARGS="-Djogl.debug.GLMediaPlayer" "-Djogl.debug.GLMediaPlayer.Native" "-Djogamp.debug.NativeLibrary=true"
REM set D_ARGS="-Djogl.debug.GLMediaPlayer.StreamWorker.delay=25" "-Djogl.debug.GLMediaPlayer"
REM set D_ARGS="-Djogl.debug.GLMediaPlayer.Native"
REM set D_ARGS="-Djogl.debug.AudioSink"
set D_ARGS="-Djogamp.debug.NativeLibrary=true" "-Dnativewindow.debug.GraphicsConfiguration"
REM set D_ARGS="-Djogamp.debug.NativeLibrary=true" "-Djogamp.debug.NativeLibrary.Lookup=true" "-Djogl.debug.GLSLCode"
REM set D_ARGS="-Djogl.debug=all" "-Dnativewindow.debug=all" "-Djogamp.debug.ProcAddressHelper" "-Djogamp.debug.NativeLibrary" "-Djogamp.debug.NativeLibrary.Lookup" "-Djogamp.debug.JNILibLoader" "-Djogamp.debug.TempJarCache" "-Djogamp.debug.JarUtil"
REM set D_ARGS="-Djogl.debug.ExtensionAvailabilityCache" "-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogamp.debug.ProcAddressHelper=true" "-Djogamp.debug.NativeLibrary=true" "-Djogamp.debug.NativeLibrary.Lookup=true"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogamp.debug.NativeLibrary=true"
REM set D_ARGS="-Djogl.debug.GLContext" "-Djogl.debug.ExtensionAvailabilityCache" "-Djogamp.debug.ProcAddressHelper=true"
REM set D_ARGS="-Dnativewindow.debug.GraphicsConfiguration"
REM set D_ARGS="-Djogl.debug.GLDrawable" "-Dnativewindow.debug.GraphicsConfiguration" "-Djogl.debug.CapabilitiesChooser"
REM set D_ARGS="-Djogamp.debug.JNILibLoader=true" "-Djogamp.debug.NativeLibrary=true" "-Djogamp.debug.NativeLibrary.Lookup=true" "-Djogl.debug.GLProfile=true"
REM set D_ARGS="-Djogl.debug=all" "-Dnewt.debug=all" "-Dnativewindow.debug=all" "-Djogamp.debug.Lock" "-Djogamp.debug.Lock.TraceLock"
REM set D_ARGS="-Djogl.debug=all" "-Dnativewindow.debug=all"
REM set D_ARGS="-Djogl.debug=all"
REM set D_ARGS="-Djogl.debug.GLCanvas" "-Djogl.debug.Animator" "-Djogl.debug.GLContext" "-Djogl.debug.GLContext.TraceSwitch" "-Djogl.debug.DebugGL" "-Djogl.debug.TraceGL"
REM set D_ARGS="-Djogl.debug.GLCanvas" "-Djogl.debug.Animator" "-Djogl.debug.GLContext" "-Djogl.debug.GLContext.TraceSwitch" "-Djogl.windows.useWGLVersionOf5WGLGDIFuncSet"
REM set D_ARGS="-Djogl.debug.GLCanvas" "-Djogl.debug.Animator" "-Djogl.debug.GLContext" "-Djogl.debug.GLContext.TraceSwitch"
REM set D_ARGS="-Djogl.debug.GLCanvas" "-Djogl.debug.GLJPanel" "-Djogl.debug.TileRenderer" "-Djogl.debug.TileRenderer.PNG"
REM set D_ARGS="-Djogl.debug.GLCanvas" "-Djogl.debug.GLJPanel" "-Djogl.debug.TileRenderer"
REM set D_ARGS="-Djogl.debug.GLContext" "-Djogl.debug.GLJPanel"
REM set D_ARGS="-Djogl.gljpanel.noverticalflip"
REM set D_ARGS="-Dnewt.debug=all"
REM set D_ARGS="-Dnewt.debug.Window"
REM set D_ARGS="-Dnewt.debug.Window" "-Dnativewindow.debug.JAWT"
REM set D_ARGS="-Djogl.debug.GLJPanel" "-Dnativewindow.debug.JAWT"
REM set D_ARGS="-Dnativewindow.debug.SWT" "-Dnewt.debug.Window" "-Djogl.debug.GLCanvas"
REM set D_ARGS="-Dnativewindow.debug.JFX" "-Dnewt.debug.Window"
REM set D_ARGS="-Dnewt.debug.Window.KeyEvent"
REM set D_ARGS="-Dnewt.debug.Window" "-Dnewt.debug.Window.KeyEvent" "-Dnewt.debug.EDT"
REM set D_ARGS="-Dnewt.debug.Window.MouseEvent"
REM set D_ARGS="-Dnewt.debug.Window.MouseEvent" "-Dnewt.debug.Window.KeyEvent"
REM set D_ARGS="-Dnewt.debug.Window" "-Dnewt.debug.Display"
REM set D_ARGS="-Djogl.debug.GLDebugMessageHandler" "-Djogl.debug.DebugGL" "-Djogl.debug.TraceGL"
REM set D_ARGS="-Djogl.debug.DebugGL" "-Djogl.debug.GLDebugMessageHandler" "-Djogl.debug.GLSLCode"
REM set D_ARGS="-Djogl.debug.GLContext" "-Dnewt.debug=all"
REM set D_ARGS="-Dnewt.debug.Window" "-Dnativewindow.debug.TraceLock"
REM set D_ARGS="-Dnativewindow.debug.TraceLock"
REM set D_ARGS="-Dnewt.debug.Display" "-Dnewt.debug.EDT" "-Dnewt.debug.Window"
REM set D_ARGS="-Dnewt.debug.Window" "-Dnewt.debug.Display" "-Dnewt.debug.EDT" "-Djogl.debug.GLContext"
REM set D_ARGS="-Dnewt.debug.Screen" "-Dnewt.debug.EDT" "-Dnativewindow.debug=all"
REM set D_ARGS="-Dnewt.debug.Screen"
REM set D_ARGS="-Dnewt.debug.Screen" "-Dnewt.debug.Window"
REM set D_ARGS="-Dnewt.debug.Window" "-Dnewt.debug.Display" "-Dnewt.test.Window.reparent.incompatible=true"

REM set D_ARGS="-Djogamp.debug.ReflectionUtil" "-Djogamp.debug.ReflectionUtil.forNameStats"
REM set D_ARGS="-Djogamp.debug.ReflectionUtil.forNameStats"

REM set X_ARGS="-Dsun.java2d.noddraw=true" "-Dsun.java2d.opengl=true" "-Dsun.awt.noerasebackground=true"
REM set X_ARGS="-Dsun.java2d.noddraw=true" "-Dsun.java2d.d3d=false" "-Dsun.java2d.ddoffscreen=false" "-Dsun.java2d.gdiblit=false" "-Dsun.java2d.opengl=false" "-Dsun.awt.noerasebackground=true" "-Xms512m" "-Xmx1024m"
REM set X_ARGS="-Dsun.java2d.noddraw=true" "-Dsun.java2d.d3d=false" "-Dsun.java2d.ddoffscreen=false" "-Dsun.java2d.gdiblit=false" "-Dsun.java2d.opengl=true" "-Dsun.awt.noerasebackground=true" "-Xms512m" "-Xmx1024m"

REM set MODULE_ARGS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.java2d=ALL-UNNAMED
set MODULE_ARGS=--add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.awt.windows=ALL-UNNAMED --add-opens java.desktop/sun.java2d=ALL-UNNAMED

REM set X_ARGS="-Dsun.java2d.noddraw=true" "-Dsun.awt.noerasebackground=true" %MODULE_ARGS%
REM set X_ARGS="-Dsun.java2d.noddraw=true" %MODULE_ARGS%
REM set X_ARGS="-Xcheck:jni" "-Dsun.java2d.noddraw=true" "-Djava.awt.headless=true" %MODULE_ARGS%

REM If using a custom opengl32.dll (see MESA3D_LIB), java2d can't load system32's opengl32.dll
REM set X_ARGS="-Dsun.java2d.noddraw=true" %MODULE_ARGS%
set X_ARGS="-Dsun.java2d.noddraw=true" "-Dsun.java2d.opengl=false" %MODULE_ARGS%

scripts\tests-win.bat %*

