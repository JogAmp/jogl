    // Pass-2: Dump Texture
#ifdef USE_AABBOX_CLIPPING
    if( is_inside(gcv_ClipBBoxCoord, gcu_ClipBBox[0], gcu_ClipBBox[1]) < 0.5 ) {
        #if USE_DISCARD
            discard; // discard freezes NV tegra2 compiler
        #else
            mgl_FragColor = vec4(0);
        #endif
    } else
#endif
    {     
        vec4 t = texture2D(gcu_FboTexUnit, gcv_FboTexCoord.st);
        #if USE_DISCARD
            if( 0.0 == t.a ) {
                discard; // discard freezes NV tegra2 compiler
            } else {
                mgl_FragColor = t;
            }
        #else
            mgl_FragColor = t;
        #endif
    }     

