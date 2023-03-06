
package com.jogamp.opengl.util;

import com.jogamp.opengl.*;

import java.nio.*;

/**
 *
 * The total number of bytes hold by the referenced buffer is:
 * getComponentSize()* getComponentNumber() * getElementNumber()
 *
 */
public interface GLArrayDataEditable extends GLArrayData {

    public boolean sealed();

    public boolean enabled();

    /**
     * Is the buffer written to the VBO ?
     */
    public boolean isVBOWritten();

    /**
     * Marks the buffer written to the VBO
     */
    public void setVBOWritten(boolean written);

    //
    // Data and GL state modification ..
    //

    @Override
    public void destroy(GL gl);

    /**
     * Clears this buffer.
     * <p>
     * Implementation must call {@link #seal(GL, boolean) seal(gl, false)} and {@link #clear()},
     * i.e. turns-off the GL buffer and then clearing it.
     * </p>
     * @see #seal(GL, boolean)
     * @see #clear()
     */
    public void clear(GL gl);

    /**
     * Convenience method calling {@link #seal(boolean)} and {@link #enableBuffer(GL, boolean)}.
     *
     * @see #seal(boolean)
     * @see #enableBuffer(GL, boolean)
     *
     */
    public void seal(GL gl, boolean seal);

    /**
     * Enables the buffer if <code>enable</code> is <code>true</code>,
     * and transfers the data if required.
     * In case {@link #isVBO() VBO is used}, it is bound accordingly for the data transfer and association,
     * i.e. it issued {@link #bindBuffer(GL, boolean)}.
     * The VBO buffer is unbound when the method returns.
     * <p>
     * Disables the buffer if <code>enable</code> is <code>false</code>.
     * </p>
     *
     * <p>The action will only be executed,
     * if the internal enable state differs,
     * or 'setEnableAlways' was called with 'true'.</b>
     *
     * <p>It is up to the user to enable/disable the array properly,
     * ie in case of multiple data sets for the same vertex attribute (VA).
     * Meaning in such case usage of one set while expecting another one
     * to be used for the same VA implies decorating each usage with enable/disable.</p>
     *
     * @see #setEnableAlways(boolean)
     */
    public void enableBuffer(GL gl, boolean enable);

    /**
     * if <code>bind</code> is true and the data uses {@link #isVBO() VBO},
     * the latter will be bound and data written to the GPU if required.
     * <p>
     * If  <code>bind</code> is false and the data uses {@link #isVBO() VBO},
     * the latter will be unbound.
     * </p>
     * <p>
     * This method is exposed to allow data VBO arrays, i.e. {@link GL#GL_ELEMENT_ARRAY_BUFFER},
     * to be bounded and written while keeping the VBO bound. The latter is in contrast to {@link #enableBuffer(GL, boolean)},
     * which leaves the VBO unbound, since it's not required for vertex attributes or pointers.
     * </p>
     *
     * @param gl current GL object
     * @param bind true if VBO shall be bound and data written,
     *        otherwise clear VBO binding.
     * @return true if data uses VBO and action was performed, otherwise false
     */
    public boolean bindBuffer(GL gl, boolean bind);

    /**
     * Affects the behavior of 'enableBuffer'.
     *
     * The default is 'false'
     *
     * This is useful when you mix up
     * GLArrayData usage with conventional GL array calls
     * or in case of a buggy GL VBO implementation.
     *
     * @see #enableBuffer(GL, boolean)
     */
    public void setEnableAlways(boolean always);

    //
    // Data modification ..
    //

    /**
     * Clears this buffer and resets states accordingly.
     * @see #clear(GL)
     */
    public void clear();

    /**
     * <p>If <i>seal</i> is true, it
     * disables write operations to the buffer.
     * Calls flip, ie limit:=position and position:=0.</p>
     *
     * <p>If <i>seal</i> is false, it
     * enable write operations continuing
     * at the buffer position, where you left off at seal(true),
     * ie position:=limit and limit:=capacity.</p>
     *
     * @see #seal(boolean)
     */
    public void seal(boolean seal);

    public void rewind();
    public void padding(int doneInByteSize);
    public void put(Buffer v);

    public void putb(byte v);
    public void put3b(byte v1, byte v2, byte v3);
    public void put4b(byte v1, byte v2, byte v3, byte v4);
    public void putb(byte[] src, int offset, int length);

    public void puts(short v);
    public void put3s(short v1, short v2, short v3);
    public void put4s(short v1, short v2, short v3, short v4);
    public void puts(short[] src, int offset, int length);

    public void puti(int v);
    public void put3i(int v1, int v2, int v3);
    public void put4i(int v1, int v2, int v3, int v4);
    public void puti(int[] src, int offset, int length);

    public void putx(int v);

    public void putf(float v);
    public void put3f(float v1, float v2, float v3);
    public void put4f(float v1, float v2, float v3, float v4);
    public void putf(float[] src, int offset, int length);
}

