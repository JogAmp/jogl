set JRE_HOME=c:\jdk-21
set J2RE_HOME=c:\jdk-21
set JAVA_HOME=c:\jdk-21
REM set JRE_HOME=c:\jre1.8.0_121_x64
set LOG_PATH=%USERPROFILE%\AppData\LocalLow\Sun\Java\Deployment\log

%JRE_PATH%\javaws -uninstall
del /F /Q %LOG_PATH%\*.*

set JNLP=Gears.jnlp
REM set JNLP=TextCube.jnlp
REM set JNLP=JRefractNoOGL.jnlp

set D_FLAGS="-J-Djnlp.nativewindow.debug=all" "-J-Djnlp.jogl.debug=all" "-J-Djnlp.newt.debug=all"

REM set X_FLAGS="-J-Dsun.java2d.noddraw=true" "-J-Dsun.awt.noerasebackground=true" "-J-Dsun.java2d.opengl=false"
REM set X_FLAGS="-J-verbose:jni"
set X_FLAGS=

%JRE_HOME%\bin\javaws %X_FLAGS% %D_FLAGS% http://risa/deployment/test/jau01s/jogl-demos/%JNLP% > tests-javaws.log 2>&1

