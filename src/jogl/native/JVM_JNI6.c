/**
 * Copyright 2019 JogAmp Community. All rights reserved.
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

#include <stdio.h> //required by android to identify NULL
#include <jni.h>

#if defined (JNI_VERSION_1_6)

JNIEXPORT jint JNICALL JNI_OnLoad_jogl_mobile(JavaVM *vm, void *reserved) { return JNI_VERSION_1_6; }
JNIEXPORT jint JNICALL JNI_OnLoad_jogl_desktop(JavaVM *vm, void *reserved) { return JNI_VERSION_1_6; }
JNIEXPORT jint JNICALL JNI_OnLoad_jogl_cg(JavaVM *vm, void *reserved) { return JNI_VERSION_1_6; }

JNIEXPORT void JNICALL JNI_OnUnload_jogl_mobile(JavaVM *vm, void *reserved) { }
JNIEXPORT void JNICALL JNI_OnUnload_jogl_desktop(JavaVM *vm, void *reserved) { }
JNIEXPORT void JNICALL JNI_OnUnload_jogl_cg(JavaVM *vm, void *reserved) { }

#else
    #error build toolchain does not supply JNI_VERSION_1_6
#endif /* defined (JNI_VERSION_1_6) */

