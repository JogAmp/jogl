/**
 * Copyright 2023-2025 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package com.jogamp.opengl.demos.graph.ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

import com.jogamp.common.net.Uri;
import com.jogamp.common.os.Clock;
import com.jogamp.common.os.Platform;
import com.jogamp.common.util.IOUtil;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.common.util.VersionUtil;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.ui.GraphShape;
import com.jogamp.graph.ui.Group;
import com.jogamp.graph.ui.Scene;
import com.jogamp.graph.ui.Shape;
import com.jogamp.graph.ui.AnimGroup;
import com.jogamp.graph.ui.layout.Alignment;
import com.jogamp.graph.ui.layout.Gap;
import com.jogamp.graph.ui.layout.GridLayout;
import com.jogamp.graph.ui.shapes.Button;
import com.jogamp.graph.ui.shapes.Label;
import com.jogamp.graph.ui.shapes.Rectangle;
import com.jogamp.math.FloatUtil;
import com.jogamp.math.Quaternion;
import com.jogamp.math.Vec2f;
import com.jogamp.math.Vec3f;
import com.jogamp.math.Vec4f;
import com.jogamp.math.geom.AABBox;
import com.jogamp.newt.Display;
import com.jogamp.newt.MonitorDevice;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLPipelineFactory;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.JoglVersion;
import com.jogamp.opengl.demos.graph.FontSetDemos;
import com.jogamp.opengl.demos.graph.MSAATool;
import com.jogamp.opengl.demos.util.CommandlineOptions;
import com.jogamp.opengl.demos.util.MiscUtils;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.av.GLMediaPlayer;
import com.jogamp.opengl.util.av.GLMediaPlayerFactory;
import com.jogamp.opengl.util.av.GLMediaPlayer.GLMediaEventListener;
import com.jogamp.opengl.util.caps.NonFSAAGLCapsChooser;

import jogamp.graph.ui.TreeTool;

/**
 * Res independent Shape, Scene attached to GLWindow showing multiple animated shape movements.
 *
 * This is a GLEventListener based version of UISceneDemo03, capable to be used with any GLAutoDrawable.
 *
 * This variation of {@link UISceneDemo00} shows
 * - Two repetitive steady scrolling text lines. One text shorter than the line-width and one longer.
 * - One line of animated rectangles, rotating around their z-axis and accelerating towards their target.
 * - A text animation assembling one line of text,
 *   each glyph coming from from a random 3D point moving to its destination all at once including rotation.
 * - One line of text with sine wave animation flattening and accelerating towards its target.
 *
 * Blog entry: https://jausoft.com/blog/2023/08/27/graphui_animation_animgroup/
 *
 * Commandline options
 * - Pass '-keep' to main-function to keep running.
 * - Pass '-aspeed' to vary velocity
 * - Pass '-rspeed <float>' angular velocity in radians/s
 * - Pass '-no_anim_box' to not show a visible and shrunken box around the AnimGroup
 * - Pass '-audio <uri or file-path>' to play audio (only)
 */
public class UISceneDemo03b implements GLEventListener  {
    static final boolean DEBUG = false;

    static final String[] originalTexts = {
            " JOGL, Java™ Binding for the OpenGL® API ",
            " GraphUI, Resolution Independent Curves ",
            " JogAmp, Java™ libraries for 3D & Media ",
            " Linux, Android, Windows, MacOS; iOS, embedded, etc on demand"
    };

    public static void main(final String[] args) throws IOException {
        final UISceneDemo03b demo = new UISceneDemo03b(0);

        if (0 != args.length) {
            final int[] idx = { 0 };
            for (idx[0] = 0; idx[0] < args.length; ++idx[0]) {
                if( UISceneDemo03b.options.parse(args, idx) ) {
                    continue;
                } else if (args[idx[0]].equals("-v")) {
                    ++idx[0];
                    demo.setVelocity(MiscUtils.atoi(args[idx[0]], (int) demo.velocity * 1000) / 1000f);
                } else if(args[idx[0]].equals("-aspeed")) {
                    demo.setVelocity(80/1000f);
                    demo.autoSpeed = -1;
                } else if(args[idx[0]].equals("-rspeed")) {
                    ++idx[0];
                    demo.ang_velo = MiscUtils.atof(args[idx[0]], demo.ang_velo);
                } else if(args[idx[0]].equals("-no_anim_box")) {
                    demo.showAnimBox = false;
                } else if(args[idx[0]].equals("-audio")) {
                    ++idx[0];
                    try {
                        demo.audioUri = Uri.cast( args[idx[0]] );
                    } catch (final URISyntaxException e1) {
                        System.err.println(e1);
                        demo.audioUri = null;
                    }
                }
            }
        }
        System.err.println(JoglVersion.getInstance().toString());
        // renderModes |= Region.COLORCHANNEL_RENDERING_BIT;
        System.err.println(UISceneDemo03b.options);

        final Display dpy = NewtFactory.createDisplay(null);
        final Screen screen = NewtFactory.createScreen(dpy, 0);
        System.err.println(VersionUtil.getPlatformInfo());
        // System.err.println(JoglVersion.getAllAvailableCapabilitiesInfo(dpy.getGraphicsDevice(), null).toString());

        final GLCapabilities caps = UISceneDemo03b.options.getGLCaps();
        System.out.println("Requested: " + caps);

        final GLWindow window = GLWindow.create(screen, caps);
        if( 0 == UISceneDemo03b.options.sceneMSAASamples ) {
            window.setCapabilitiesChooser(new NonFSAAGLCapsChooser(false));
        }
        window.setSize(UISceneDemo03b.options.surface_width, UISceneDemo03b.options.surface_height);
        window.setTitle(UISceneDemo03b.class.getSimpleName() + ": " + window.getSurfaceWidth() + " x " + window.getSurfaceHeight());

        window.addGLEventListener(demo);

        final Animator animator = new Animator(0 /* w/o AWT */);
        animator.setUpdateFPSFrames(5*60, null);
        animator.add(window);
        animator.setExclusiveContext(UISceneDemo03b.options.exclusiveContext);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(final WindowEvent e) {
                animator.stop();
            }
        });

        if( UISceneDemo03b.options.wait_to_start ) {
            System.err.println("Press enter to continue");
            MiscUtils.waitForKey("Start");
        }

        window.setVisible(true);
        System.out.println("Chosen: " + window.getChosenGLCapabilities());

        animator.start();
    }

    public static CommandlineOptions options = new CommandlineOptions(1280, 720, Region.VBAA_RENDERING_BIT);
    // public static CommandlineOptions options = new CommandlineOptions(1280, 720, Region.NORM_RENDERING_BIT, Region.DEFAULT_AA_QUALITY, 0, 4);

    boolean showAnimBox = true;
    float frame_velocity = 5f / 1e3f; // [m]/[s]
    float velocity = 30 / 1e3f; // [m]/[s]
    float ang_velo = velocity * 60f; // [radians]/[s]
    int autoSpeed = -1;

    Uri audioUri = null;
    GLMediaPlayer mPlayer = null;

    final int[] manualScreenShotCount = { 0 };

    private boolean debug = false;
    private boolean trace = false;

    private final Font fontButtons, fontSymbols, font, fontStatus;
    private float dpiV = 96;
    private float pixPerMM;

    private final Scene scene;
    private final AABBox sceneBox;
    private float top_ypos = 0;
    private final AnimGroup animGroup;
    private final AABBox animBox;
    private final Animator animator;
    private final AnimGroup.Set[] dynAnimSet = { null, null, null };
    private boolean firstReshape = true;
    private boolean z_only = true;
    private int txt_idx = 0;
    private int m_state = -1;
    private long t0_us = 0;
    private long t1_pause_us = 0;

    /**
     * @param renderModes
     */
    public UISceneDemo03b(final int renderModes) {
        this(null, renderModes, false, false);
    }

    private UISceneDemo03b(final String fontfilename, final int renderModes, final boolean debug, final boolean trace) {
        setVelocity(80/1000f);
        autoSpeed = -1;

        this.debug = debug;
        this.trace = trace;

        UISceneDemo03b.options.renderModes = renderModes;

        try {
            fontButtons = FontFactory.get(FontFactory.UBUNTU).getDefault();
            fontSymbols = FontFactory.get(FontFactory.SYMBOLS).getDefault();
            font = FontFactory.get(IOUtil.getResource("fonts/freefont/FreeSerif.ttf",FontSetDemos.class.getClassLoader(), FontSetDemos.class).getInputStream(), true);
            // final Font font = FontFactory.get(IOUtil.getResource("jogamp/graph/font/fonts/ubuntu/Ubuntu-R.ttf",FontSetDemos.class.getClassLoader(), FontSetDemos.class).getInputStream(), true);
            System.err.println("Font FreeSerif: " + font.getFullFamilyName());
            fontStatus = FontFactory.get(IOUtil.getResource("fonts/freefont/FreeMono.ttf", FontSetDemos.class.getClassLoader(), FontSetDemos.class).getInputStream(), true);
            System.err.println("Font Status: " + fontStatus.getFullFamilyName());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }

        scene = new Scene(options.graphAASamples);
        sceneBox = new AABBox();
        scene.setClearParams(new float[] { 1f, 1f, 1f, 1f }, GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        scene.getRenderer().setHintBits(RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED);

        animGroup = new AnimGroup(null);
        animBox = new AABBox();
        scene.addShape(animGroup);

        scene.setPMvCullingEnabled(true);
        animGroup.setPMvCullingEnabled(true);

        animator = new Animator(0 /* w/o AWT */);
        animator.setUpdateFPSFrames(1 * 60, null); // System.err);
    }

    void setVelocity(final float v) {
        velocity = v; // Math.max(1/1e3f, v);
        ang_velo = velocity * 60f;
        autoSpeed = 0;
    }


    /**
     * Rotate the shape while avoiding 90 degree position
     * @param shape the shape to rotate
     * @param angle the angle in radians
     * @param axis 0 for X-, 1 for Y- and 2 for Z-axis
     */
    public static void rotateShape(final Shape shape, float angle, final int axis) {
        final Quaternion rot = shape.getRotation().copy();
        final Vec3f euler = rot.toEuler(new Vec3f());
        final Vec3f eulerOld = euler.copy();

        final float eps = FloatUtil.adegToRad(5f);
        final float sign = angle >= 0f ? 1f : -1f;
        final float v;
        switch( axis ) {
            case 0: v = euler.x(); break;
            case 1: v = euler.y(); break;
            case 2: v = euler.z(); break;
            default: return;
        }
        final float av = Math.abs(v);
        if( 1f*FloatUtil.HALF_PI - eps <= av && av <= 1f*FloatUtil.HALF_PI + eps ||
            3f*FloatUtil.HALF_PI - eps <= av && av <= 3f*FloatUtil.HALF_PI + eps) {
            angle = 2f * eps * sign;
        }
        switch( axis ) {
            case 0: euler.add(angle, 0, 0); break;
            case 1: euler.add(0, angle, 0); break;
            case 2: euler.add(0, 0, angle); break;
        }
        System.err.println("Rot: angleDelta "+angle+" (eps "+eps+"): "+eulerOld+" -> "+euler);
        shape.setRotation( rot.setFromEuler(euler) );
    }

    @Override
    public void init(final GLAutoDrawable glad) {
        // Resolution independent, no screen size
        //
        final Object upObj = glad.getUpstreamWidget();
        if( upObj instanceof Window ) {
            final Window upWin = (Window)upObj;
            final MonitorDevice monitor = upWin.getMainMonitor();
            final float[] monitorDPI = MonitorDevice.mmToInch( monitor.getPixelsPerMM(new float[2]) );
            final float[] pixPerX = upWin.getPixelsPerMM(new float[2]);
            pixPerMM = pixPerX[1]; // [px]/[mm]
            MonitorDevice.mmToInch( pixPerX );
            dpiV = pixPerX[1];
            System.err.println("Monitor detected: "+monitor);
            System.err.println("Monitor dpi: "+monitorDPI[0]+" x "+monitorDPI[1]);
            System.err.println("Surface scale: native "+Arrays.toString(upWin.getMaximumSurfaceScale(new float[2]))+", current "+Arrays.toString(upWin.getCurrentSurfaceScale(new float[2])));
            System.err.println("Surface dpi "+pixPerX[0]+" x "+pixPerX[1]);
        } else {
            pixPerMM = MonitorDevice.inchToMM(dpiV); // [px]/[mm]
            System.err.println("Using default DPI of "+dpiV);
        }
        {
            final int o = options.fixDefaultAARenderModeWithDPIThreshold(dpiV);
            System.err.println("AUTO RenderMode: dpi "+dpiV+", threshold "+options.noAADPIThreshold+
                               ", mode "+Region.getRenderModeString(o)+" -> "+
                               Region.getRenderModeString(options.renderModes));
        }
        if(glad instanceof GLWindow) {
            System.err.println("UISceneDemo20: init (1.1)");
            final GLWindow glw = (GLWindow) glad;
            scene.attachInputListenerTo(glw);
        } else {
            System.err.println("UISceneDemo20: init (1.0)");
        }

        GL2ES2 gl = glad.getGL().getGL2ES2();
        if(debug) {
            gl = gl.getContext().setGL( GLPipelineFactory.create("com.jogamp.opengl.Debug", null, gl, null) ).getGL2ES2();
        }
        if(trace) {
            gl = gl.getContext().setGL( GLPipelineFactory.create("com.jogamp.opengl.Trace", null, gl, new Object[] { System.err } ) ).getGL2ES2();
        }
        System.err.println(JoglVersion.getGLInfo(gl, null, false /* withCapsAndExts */).toString());
        System.err.println("VSync Swap Interval: "+gl.getSwapInterval());
        System.err.println("Chosen: "+glad.getChosenGLCapabilities());
        MSAATool.dump(glad);

        gl.setSwapInterval(1);
        gl.glEnable(GL.GL_DEPTH_TEST);
        // gl.glDepthFunc(GL.GL_LEQUAL);
        // gl.glEnable(GL.GL_BLEND);

        scene.setAAQuality(options.graphAAQuality);
        scene.init(glad);

        //
        // Optional Audio
        //
        if( null != audioUri ) {
            mPlayer = GLMediaPlayerFactory.createDefault();
            mPlayer.addEventListener( new MyGLMediaEventListener() );
            mPlayer.playStream(audioUri, GLMediaPlayer.STREAM_ID_NONE, GLMediaPlayer.STREAM_ID_AUTO, GLMediaPlayer.STREAM_ID_NONE, GLMediaPlayer.TEXTURE_COUNT_DEFAULT);
        } else {
            mPlayer = null;
        }

        firstReshape = true;
    }

    @Override
    public void dispose(final GLAutoDrawable drawable) {
        animGroup.destroy(drawable.getGL().getGL2ES2(), scene.getRenderer());
        scene.dispose(drawable);
        if(null != mPlayer) {
            mPlayer.destroy(drawable.getGL());
            mPlayer = null;
        }
    }

    @Override
    public void reshape(final GLAutoDrawable glad, final int x, final int y, final int width, final int height) {
        scene.reshape(glad, x, y, width, height);
        if( !firstReshape ) {
            return; // done
        }

        sceneBox.set( scene.getBounds() );
        final float sceneBoxFrameWidth = sceneBox.getWidth() * 0.0025f;
        final GraphShape r = new Rectangle(options.renderModes, sceneBox, sceneBoxFrameWidth);
        if( showAnimBox ) {
            r.setColor(0.45f, 0.45f, 0.45f, 0.9f);
        } else {
            r.setColor(0f, 0f, 0f, 0f);
        }
        r.setInteractive(false);
        animGroup.addShape( r );

        animGroup.setRotationPivot(0, 0, 0);
        if( showAnimBox ) {
            animGroup.scale(0.85f, 0.85f, 1f);
            animGroup.move(-sceneBox.getWidth()/2f*0.075f, 0f, 0f);
            animGroup.setRotation( animGroup.getRotation().rotateByAngleY(0.1325f) );
        } else {
            animGroup.scale(1.0f, 1.0f, 1f);
        }
        final GLProfile hasGLP = glad.getChosenGLCapabilities().getGLProfile();
        animGroup.validate(hasGLP);
        animGroup.setInteractive(false);
        animGroup.setToggleable(true);
        animGroup.setResizable(false);
        animGroup.setToggle( false );
        System.err.println("SceneBox " + sceneBox);
        System.err.println("Frustum " + scene.getMatrix().getFrustum());
        System.err.println("AnimGroup.0: "+animGroup);

        final Label statusLabel;
        {
            final AABBox fbox = fontStatus.getGlyphBounds("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            final float statusLabelScale = sceneBox.getWidth() / fbox.getWidth();
            System.err.println("StatusLabelScale: " + statusLabelScale + " = " + sceneBox.getWidth() + " / " + fbox.getWidth() + ", " + fbox);

            statusLabel = new Label(options.renderModes, fontStatus, "Nothing there yet");
            statusLabel.setScale(statusLabelScale, statusLabelScale, 1f);
            statusLabel.setColor(0.1f, 0.1f, 0.1f, 1.0f);
            statusLabel.moveTo(sceneBox.getMinX(), sceneBox.getMinY() + statusLabelScale * (fontStatus.getMetrics().getLineGap() - fontStatus.getMetrics().getDescent()), 0f);
            scene.addShape(statusLabel);
        }
        if(glad instanceof GLWindow) {
            System.err.println("UISceneDemo20: init (1.1)");
            final GLWindow glw = (GLWindow) glad;
            sub01SetupWindowListener(glw, scene, animGroup, statusLabel, dpiV);
        }
        // HUD UI
        sub02AddUItoScene(scene, animGroup, 2, glad);

        //
        // Setup the moving glyphs
        //
        animBox.set( animGroup.getBounds() );
        System.err.println("AnimBox " + animBox);
        System.err.println("AnimGroup.1 " + animGroup);

        top_ypos = 0;
        final float fontScale2;
        {
            final String vs = "Welcome to Göthel Software ***  Jausoft  ***  https://jausoft.com *** We do software ...  Check out Gamp. XXXXXXXXXXXXXXXXXXXXXXXXXXX";
            final AABBox fbox = font.getGlyphBounds(vs);
            fontScale2 = animBox.getWidth() / fbox.getWidth();
            System.err.println("FontScale2: " + fontScale2 + " = " + animBox.getWidth() + " / " + fbox.getWidth());
        }
        final AABBox clippedBox = new AABBox(animBox).resizeWidth(-sceneBoxFrameWidth, -sceneBoxFrameWidth);
        top_ypos = clippedBox.getMaxY();
        // AnimGroup.Set 1:
        // Circular short scrolling text (right to left) without rotation, no acceleration
        {
            final String vs = "Welcome to Göthel Software ***  Jausoft  ***  https://jausoft.com *** We do software ...  Check out Gamp.";
            top_ypos -= fontScale2 * 1.5f;
            animGroup.addGlyphSetHorizScroll01(pixPerMM, hasGLP, scene.getMatrix(), scene.getViewport(), options.renderModes,
                    font, vs, fontScale2, new Vec4f(0.1f, 0.1f, 0.1f, 0.9f),
                    50 / 1e3f /* velocity */, clippedBox, top_ypos);
        }
        // AnimGroup.Set 2:
        // Circular long scrolling text (right to left) without rotation, no acceleration
        {
            final String vs = VersionUtil.getPlatformInfo().replace(Platform.getNewline(), "; ").replace(VersionUtil.SEPERATOR, "  ***  ").replaceAll("\\s+", " ");
            top_ypos -= fontScale2 * 1.2f;
            animGroup.addGlyphSetHorizScroll01(pixPerMM, hasGLP, scene.getMatrix(), scene.getViewport(), options.renderModes,
                    font, vs, fontScale2, new Vec4f(0.1f, 0.1f, 0.1f, 0.9f),
                    30 / 1e3f /* velocity */, clippedBox, top_ypos);
        }

        firstReshape = false;
    }

    @Override
    public void display(final GLAutoDrawable drawable) {
        final boolean hadActiveAnimSet = null != dynAnimSet[0] && dynAnimSet[0].isAnimationActive();
        if ( 0 == m_state && !hadActiveAnimSet && !animGroup.getTickPaused() ) {
            //
            // Setup new animation sequence
            // - Flush all AnimGroup.Set entries
            // - Add newly created AnimGroup.Set entries
            //
            if( null != dynAnimSet[0] ) {
                // Print stats and set pause 1.5s
                final long t1_us = Clock.currentNanos() / 1000;
                final float has_dur_s = (t1_us - t0_us) / 1e6f; // [us]
                final String curText = originalTexts[txt_idx];
                System.err.printf("Text travel-duration %.3f s, %d chars%n", has_dur_s, curText.length());
                if( scene.getScreenshotCount() - manualScreenShotCount[0] < 1 + originalTexts.length ) {
                    scene.screenshot(true, scene.nextScreenshotFile(null, UISceneDemo03b.class.getSimpleName(), options.renderModes, drawable.getChosenGLCapabilities(), null));
                }
                m_state = 1;
                t1_pause_us = t1_us + 1500 * 1000;
            }
        } else if( 1 == m_state && t1_pause_us <= Clock.currentNanos() / 1000 ) {
            // Flush all AnimGroup.Set entries and prep param for next cycle
            animGroup.removeAnimSets(drawable.getGL().getGL2ES2(), scene.getRenderer(), Arrays.asList(dynAnimSet));
            if( autoSpeed > 0 ) {
                if( velocity < 60/1000f ) {
                    setVelocity(velocity + 9/1000f);
                } else {
                    setVelocity(velocity - 9/1000f);
                    autoSpeed = -1;
                }
            } else  if( autoSpeed < 0 ) {
                if( velocity > 11/1000f ) {
                    setVelocity(velocity - 9/1000f);
                } else {
                    setVelocity(velocity + 9/1000f);
                    autoSpeed = 1;
                }
            }
            txt_idx = ( txt_idx + 1 ) % originalTexts.length;
            z_only = !z_only;
            m_state = -1;
            System.err.println("Next animation loop ...");
        } else if( -1 == m_state ) {
            // Add newly created AnimGroup.Set entries
            createAnimSets(drawable.getGL());
            t0_us = Clock.currentNanos();
            m_state = 0;
        }
        scene.display(drawable);
    }

    private void createAnimSets(final GL gl) {
        final GLProfile hasGLP = gl.getGLProfile();
        final String curText = originalTexts[txt_idx];
        final float fontScale;
        {
            final AABBox fbox = font.getGlyphBounds(curText);
            fontScale = animBox.getWidth() / fbox.getWidth();
            System.err.println("FontScale: " + fontScale + " = " + animBox.getWidth() + " / " + fbox.getWidth());
        }

        // AnimGroup.Set 3: This `mainAnimSet[0]` is controlling overall animation duration
        // Rotating animated text moving to target (right to left) + slight acceleration on rotation
        dynAnimSet[0] = animGroup.addGlyphSetRandom01(pixPerMM, hasGLP, scene.getMatrix(), scene.getViewport(),
                options.renderModes, font, curText, fontScale, new Vec4f(0.1f, 0.1f, 0.1f, 1f),
                0f /* accel */, velocity, FloatUtil.PI/20f /* ang_accel */, ang_velo,
                animBox, z_only, new Random(), new AnimGroup.TargetLerp(Vec3f.UNIT_Y));

        // AnimGroup.Set 4:
        // Sine animated text moving to target (right to left) with sine amplitude alternating on Z- and Y-axis + acceleration
        {
            final GLContext ctx = gl.getContext();
            final String vs = "JogAmp Version "+JoglVersion.getInstance().getImplementationVersion()+
                              ", GL "+ctx.getGLVersionNumber().getVersionString()+
                              ", GLSL "+ctx.getGLSLVersionNumber().getVersionString() +
                              " by "+gl.glGetString(GL.GL_VENDOR);
            final float fontScale2;
            {
                final AABBox fbox = font.getGlyphBounds(vs);
                fontScale2 = animBox.getWidth() / fbox.getWidth() * 0.6f;
            }
            // Translation : We use velocity as acceleration (good match) and pass only velocity/10 as starting velocity
            dynAnimSet[1] = animGroup.addGlyphSet(pixPerMM, hasGLP, scene.getMatrix(), scene.getViewport(),
                    options.renderModes, font, 'X', vs, fontScale2,
                    velocity /* accel */, velocity/10f, 0f /* ang_accel */, 2*FloatUtil.PI /* 1-rotation/s */,
                    new AnimGroup.SineLerp(z_only ? Vec3f.UNIT_Z : Vec3f.UNIT_Y, 1.618f, 1.618f),
                    (final AnimGroup.Set as, final int idx, final AnimGroup.ShapeData sd) -> {
                        sd.shape.setColor(0.1f, 0.1f, 0.1f, 0.9f);

                        sd.targetPos.add(
                                animBox.getMinX() + as.refShape.getScaledWidth() * 1.0f,
                                animBox.getMinY() + as.refShape.getScaledHeight() * 2.0f, 0f);

                        sd.startPos.set( sd.targetPos.x() + animBox.getWidth(),
                                         sd.targetPos.y(), sd.targetPos.z());
                        sd.shape.moveTo( sd.startPos );
                    } );
        }
        // AnimGroup.Set 5:
        // 3 animated Shapes moving to target (right to left) while rotating around z-axis + acceleration on translation
        {
            final float size2 = fontScale/2;
            final float yscale = 1.1f;
            final GraphShape refShape = new Rectangle(options.renderModes, size2, size2*yscale, sceneBox.getWidth() * 0.0025f );
            dynAnimSet[2] = animGroup.addAnimSet(
                        pixPerMM, hasGLP, scene.getMatrix(), scene.getViewport(),
                        velocity /* accel */, velocity/10f, 0f /* ang_accel */, 2*FloatUtil.PI /* 1-rotation/s */,
                        new AnimGroup.TargetLerp(Vec3f.UNIT_Z), refShape);
            final AnimGroup.ShapeSetup shapeSetup = (final AnimGroup.Set as, final int idx, final AnimGroup.ShapeData sd) -> {
                    sd.targetPos.add(animBox.getMinX() + as.refShape.getScaledWidth() * 1.0f,
                                     top_ypos - as.refShape.getScaledHeight() * 1.5f, 0f);

                    sd.startPos.set( sd.targetPos.x() + animBox.getWidth(),
                                     sd.targetPos.y(), sd.targetPos.z());
                    sd.shape.moveTo( sd.startPos );
                };
            refShape.setColor(1.0f, 0.0f, 0.0f, 0.9f);
            refShape.setRotation( refShape.getRotation().rotateByAngleZ(FloatUtil.QUARTER_PI) );
            dynAnimSet[2].addShape(animGroup, refShape, shapeSetup);
            {
                final Shape s = new Rectangle(options.renderModes, size2, size2*yscale, sceneBox.getWidth() * 0.0025f ).validate(hasGLP);
                s.setColor(0.0f, 1.0f, 0.0f, 0.9f);
                s.move(refShape.getScaledWidth() * 1.5f * 1, 0, 0);
                dynAnimSet[2].addShape(animGroup, s, shapeSetup);
            }
            {
                final Shape s = new Rectangle(options.renderModes, size2, size2*yscale, sceneBox.getWidth() * 0.0025f ).validate(hasGLP);
                s.setColor(0.0f, 0.0f, 1.0f, 0.9f);
                s.move(refShape.getScaledWidth() * 1.5f * 2, 0, 0);
                s.getRotation().rotateByAngleZ(FloatUtil.QUARTER_PI);
                dynAnimSet[2].addShape(animGroup, s, shapeSetup);
            }
        }
    }

    /**
     * Setup Window listener for I/O
     * @param window
     * @param animGroup
     */
    void sub01SetupWindowListener(final GLWindow window, final Scene scene, final AnimGroup animGroup, final Label statusLabel, final float dpiV) {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowResized(final WindowEvent e) {
                window.setTitle(UISceneDemo03b.class.getSimpleName() + ": " + window.getSurfaceWidth() + " x " + window.getSurfaceHeight());
            }

            @Override
            public void windowDestroyNotify(final WindowEvent e) {
                final GLAnimatorControl animator = window.getAnimator();
                if( null != animator ) {
                    animator.stop();
                }
            }
        });
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final short keySym = e.getKeySymbol();
                if (keySym == KeyEvent.VK_PLUS ||
                    keySym == KeyEvent.VK_ADD)
                {
                    if (e.isShiftDown()) {
                        setVelocity(velocity + 10 / 1000f);
                    } else {
                        setVelocity(velocity + 1 / 1000f);
                    }
                } else if (keySym == KeyEvent.VK_MINUS ||
                           keySym == KeyEvent.VK_SUBTRACT)
                {
                    if (e.isShiftDown()) {
                        setVelocity(velocity - 10 / 1000f);
                    } else {
                        setVelocity(velocity - 1 / 1000f);
                    }
                } else if( keySym == KeyEvent.VK_F4 || keySym == KeyEvent.VK_ESCAPE || keySym == KeyEvent.VK_Q ) {
                    MiscUtils.destroyWindow(window);
                } else if( keySym == KeyEvent.VK_SPACE ) {
                    animGroup.setTickPaused ( !animGroup.getTickPaused() );
                } else if( keySym == KeyEvent.VK_ENTER ) {
                    animGroup.stopAnimation();
                }
            }
        });
        window.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseWheelMoved(final MouseEvent e) {
                int axis = 1;
                if( e.isControlDown() ) {
                    axis = 0;
                } else if( e.isAltDown() ) {
                    axis = 2;
                }
                final float angle = e.getRotation()[1] < 0f ? FloatUtil.adegToRad(-1f) : FloatUtil.adegToRad(1f);
                rotateShape(animGroup, angle, axis);
            }
        });
        window.addGLEventListener(new GLEventListener() {
            float dir = 1f;
            @Override
            public void init(final GLAutoDrawable drawable) {
                System.err.println(JoglVersion.getGLInfo(drawable.getGL(), null));
            }
            @Override
            public void dispose(final GLAutoDrawable drawable) {}
            @Override
            public void display(final GLAutoDrawable drawable) {
                if( animGroup.isToggleOn() ) {
                    final Quaternion rot = animGroup.getRotation();
                    final Vec3f euler = rot.toEuler(new Vec3f());
                    if( FloatUtil.HALF_PI <= euler.y() ) {
                        dir = -1f;
                    } else if( euler.y() <= -FloatUtil.HALF_PI ) {
                        dir = 1f;
                    }
                    rot.rotateByAngleY( frame_velocity * dir );
                    animGroup.setRotation(rot);
                }
                final String text = String.format("%s, v %.1f mm/s, r %.3f rad/s",
                        scene.getStatusText(drawable, options.renderModes, dpiV), velocity * 1e3f, ang_velo);
                statusLabel.setText(text);
            }
            @Override
            public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {}
        });
    }

    /**
     * Add a HUD UI to the scene
     * @param scene
     * @param animGroup
     * @param glad
     */
    void sub02AddUItoScene(final Scene scene, final AnimGroup animGroup, final int mainAnimSetIdx, final GLAutoDrawable glad) {
        final Group buttonsRight = new Group();

        final float buttonWidth = sceneBox.getWidth() * 0.09f;
        final float buttonHeight = buttonWidth / 3.0f;
        final float buttonZOffset = scene.getZEpsilon(16);
        final Vec2f fixedSymSize = new Vec2f(0.0f, 1.0f);
        final Vec2f symSpacing = new Vec2f(0f, 0.2f);

        buttonsRight.setLayout(new GridLayout(buttonWidth, buttonHeight, Alignment.Fill, new Gap(buttonHeight*0.50f, buttonWidth*0.10f), 7));
        {
            final Button button = new Button(options.renderModes, fontSymbols,
                        fontSymbols.getUTF16String("play_arrow"),  fontSymbols.getUTF16String("pause"),
                        buttonWidth, buttonHeight, buttonZOffset);
            button.setSpacing(symSpacing, fixedSymSize);
            button.onToggle((final Shape s) -> {
                System.err.println("Play/Pause "+s);
                animGroup.setTickPaused ( s.isToggleOn() );
                if( s.isToggleOn() ) {
                    animGroup.setTickPaused ( false );
                    if( null != mPlayer ) {
                        mPlayer.resume();
                    }
                } else {
                    animGroup.setTickPaused ( true );
                    if( null != mPlayer ) {
                        mPlayer.pause(false);
                    }
                }
            });
            button.setToggle(true); // on == play
            buttonsRight.addShape(button);
        }
        {
            final Button button = new Button(options.renderModes, fontSymbols, fontSymbols.getUTF16String("fast_forward"), buttonWidth, buttonHeight, buttonZOffset); // next (ffwd)
            button.setSpacing(symSpacing, fixedSymSize);
            button.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    final AnimGroup.Set as = animGroup.getAnimSet(mainAnimSetIdx);
                    if( null != as ) {
                        as.setAnimationActive(false);
                    }
                } } );
            buttonsRight.addShape(button);
        }
        {
            final Button button = new Button(options.renderModes, fontSymbols,
                    fontSymbols.getUTF16String("rotate_right"), fontSymbols.getUTF16String("stop_circle"),
                    buttonWidth, buttonHeight, buttonZOffset); // rotate (replay)
            button.setSpacing(symSpacing, fixedSymSize);
            button.setToggleable(true);
            button.onToggle((final Shape s) -> {
                animGroup.toggle();
            });
            buttonsRight.addShape(button);
        }
        {
            final Button button = new Button(options.renderModes, fontButtons, " < Rot > ", buttonWidth, buttonHeight, buttonZOffset);
            button.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    final Shape.EventInfo shapeEvent = (Shape.EventInfo) e.getAttachment();
                    int axis = 1;
                    if( e.isControlDown() ) {
                        axis = 0;
                    } else if( e.isAltDown() ) {
                        axis = 2;
                    }
                    if( shapeEvent.objPos.x() < shapeEvent.shape.getBounds().getCenter().x() ) {
                        rotateShape(animGroup, FloatUtil.adegToRad(1f), axis);
                    } else {
                        rotateShape(animGroup, FloatUtil.adegToRad(-1f), axis);
                    }
                } } );
            buttonsRight.addShape(button);
        }
        {
            final Button button = new Button(options.renderModes, fontButtons, " < Velo > ", buttonWidth, buttonHeight, buttonZOffset);
            button.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    final Shape.EventInfo shapeEvent = (Shape.EventInfo) e.getAttachment();
                    final float scale = e.isShiftDown() ? 1f : 10f;
                    if( shapeEvent.objPos.x() < shapeEvent.shape.getBounds().getCenter().x() ) {
                        setVelocity(velocity - scale / 1000f);
                    } else {
                        setVelocity(velocity + scale / 1000f);
                    }
                    final AnimGroup.Set as = animGroup.getAnimSet(mainAnimSetIdx);
                    if( null != as ) {
                        as.setAnimationActive(false);
                    }
                } } );
            buttonsRight.addShape(button);
        }
        {
            final Button button = new Button(options.renderModes, fontSymbols, fontSymbols.getUTF16String("camera"), buttonWidth, buttonHeight, buttonZOffset); // snapshot (camera)
            button.setSpacing(symSpacing, fixedSymSize);
            button.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    scene.screenshot(false, scene.nextScreenshotFile(null, UISceneDemo03b.class.getSimpleName(), options.renderModes, glad.getChosenGLCapabilities(), null));
                    manualScreenShotCount[0]++;
                } } );
            buttonsRight.addShape(button);
        }
        {
            final Button button = new Button(options.renderModes, fontSymbols, fontSymbols.getUTF16String("power_settings_new"), buttonWidth, buttonHeight, buttonZOffset); // exit (power_settings_new)
            button.setSpacing(symSpacing, fixedSymSize);
            button.setColor(0.7f, 0.3f, 0.3f, 1.0f);
            button.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    MiscUtils.destroyWindow(glad);
                } } );
            buttonsRight.addShape(button);
        }
        TreeTool.forAll(buttonsRight, (final Shape s) -> { s.setDragAndResizable(false); return false; });
        buttonsRight.validate(glad.getChosenGLCapabilities().getGLProfile());
        buttonsRight.moveTo(sceneBox.getWidth()/2f  - buttonsRight.getScaledWidth()*1.02f,
                            sceneBox.getHeight()/2f - buttonsRight.getScaledHeight()*1.02f, 0f);
        scene.addShape(buttonsRight);
        if( DEBUG ) {
            System.err.println("Buttons-Right: Button-1 "+buttonsRight.getShapes().get(0));
            System.err.println("Buttons-Right: SceneBox "+sceneBox);
            System.err.println("Buttons-Right: scaled   "+buttonsRight.getScaledWidth()+" x "+buttonsRight.getScaledHeight());
            System.err.println("Buttons-Right: Box      "+buttonsRight.getBounds());
            System.err.println("Buttons-Right: "+buttonsRight);
        }
    }

    class MyGLMediaEventListener implements GLMediaEventListener {
            @Override
            public void attributesChanged(final GLMediaPlayer mp, final GLMediaPlayer.EventMask eventMask, final long when) {
                System.err.println("MediaPlayer.1 AttributesChanges: "+eventMask+", when "+when);
                System.err.println("MediaPlayer.1 State: "+mp);
                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.Init) ) {
                    new InterruptSource.Thread() {
                        @Override
                        public void run() {
                            try {
                                mp.initGL(null);
                                if ( GLMediaPlayer.State.Paused == mp.getState() ) { // init OK
                                    mp.resume();
                                }
                                System.out.println("MediaPlayer.1 "+mp);
                            } catch (final Exception e) {
                                e.printStackTrace();
                                mp.destroy(null);
                                mPlayer = null;
                                return;
                            }
                        }
                    }.start();
                }
                boolean destroy = false;
                Throwable err = null;

                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.EOS) ) {
                    err = mp.getStreamException();
                    if( null != err ) {
                        System.err.println("MovieSimple State: Exception");
                        destroy = true;
                    } else {
                        new InterruptSource.Thread() {
                            @Override
                            public void run() {
                                mp.setPlaySpeed(1f);
                                mp.seek(0);
                                mp.resume();
                            }
                        }.start();
                    }
                }
                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.Error) ) {
                    err = mp.getStreamException();
                    destroy = true;;
                }
                if( destroy ) {
                    if( null != err ) {
                        System.err.println("MovieSimple State: Exception");
                        err.printStackTrace();
                    }
                    mp.destroy(null);
                    mPlayer = null;
                }
            }
        }

}
