/**
 * Copyright 2024 JogAmp Community. All rights reserved.
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
package com.jogamp.opengl.util.av;

/**
 * ASS/SAA Event Line of subtitles
 * <p>
 * See http://www.tcax.org/docs/ass-specs.htm
 * </p>
 */
public class ASSEventLine {
    public enum Format {
        UNKNOWN,
        /** FFMpeg output w/o start, end:
         * <pre>
           0    1      2      3     4        5        6        7       8
           Seq, Layer, Style, Name, MarginL, MarginR, MarginV, Effect, TEXT
         * </pre>
         */
        FFMPEG,
        /** Just the plain text part */
        TEXT
    };
    public final Format source;
    public final int seqnr;
    public final int pts_start;
    public final int pts_end;
    public final int layer;
    public final String style;
    public final String name;
    /** Actual subtitle text */
    public final String text;
    /** Number of lines of {@link #text}, i.e. occurrence of {@code \n} + 1. */
    public final int lines;

    /**
     * ASS/SAA Event Line ctor
     * @param fmt input format of {@code ass}, currently only {@link ASSEventLine.Format#FFMPEG} and {@link ASSEventLine.Format#TEXT} is supported
     * @param ass ASS/SAA compatible event line according to {@code fmt}
     * @param pts_start pts start in ms, provided for {@link ASSEventLine.Format#FFMPEG} and {@link ASSEventLine.Format#TEXT}
     * @param pts_end pts end in ms, provided for {@link ASSEventLine.Format#FFMPEG} and {@link ASSEventLine.Format#TEXT}
     */
    public ASSEventLine(final Format fmt, final String ass, final int pts_start, final int pts_end) {
        this.source = fmt;
        int seqnr = 0;
        int layer = 0;
        String style = "Default";
        String name = "";
        String text = "";
        if( Format.FFMPEG == fmt ) {
            final int len = null != ass ? ass.length() : 0;
            int part = 0;
            for(int i=0; 9 > part && len > i; ) {
                if( 8 == part ) {
                    text = ass.substring(i);
                } else {
                    final int j = ass.indexOf(',', i);
                    if( 0 > j ) {
                        break;
                    }
                    final String v = ass.substring(i, j);
                    switch(part) {
                        case 0:
                            seqnr = Integer.valueOf(v);
                            break;
                        case 1:
                            layer = Integer.valueOf(v);
                            break;
                        case 2:
                            style = v;
                            break;
                        case 3:
                            name = v;
                            break;
                    }
                    i = j + 1;
                }
                ++part;
            }
        } else if( Format.TEXT == fmt ) {
            text = ass;
        }
        this.seqnr = seqnr;
        this.pts_start = pts_start;
        this.pts_end = pts_end;
        this.layer = layer;
        this.style = style;
        this.name = name;
        this.text = text.replace("\\N", "\n");
        {
            final int len = this.text.length();
            int lc = 1;
            for(int i=0; len > i; ) {
                final int j = this.text.indexOf("\n", i);
                if( 0 > j ) {
                    break;
                }
                ++lc;
                i = j + 1;
            }
            this.lines = lc;
        }
    }
    public int getDuration() { return pts_end - pts_start + 1; }

    @Override
    public String toString() {
        return "ASS["+source+", #"+seqnr+", l_"+layer+", ["+pts_start+".."+pts_end+"] "+getDuration()+" ms, style "+style+", name '"+name+"': '"+text+"' ("+lines+")]";
    }
}