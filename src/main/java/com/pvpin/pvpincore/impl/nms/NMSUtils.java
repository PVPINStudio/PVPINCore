/*
 * The MIT License
 * Copyright © ${year} PVPINStudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pvpin.pvpincore.impl.nms;

import com.pvpin.pvpincore.api.PVPINLogManager;
import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;
import java.lang.reflect.Method;

/**
 *
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class NMSUtils {

    protected static Class<?> nmsNBTBase;
    protected static Class<?> nmsNBTTagCompound;
    protected static Class<?> nmsNBTTagList;

    protected static Class<?> nmsPacket;

    protected static Method nmsNBTTagCompound_hasKey;
    protected static Method nmsNBTTagCompound_get;
    protected static Method nmsNBTTagCompound_set;

    static {
        // Initialize reflections
        try {
            nmsNBTBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
            nmsNBTTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
            nmsNBTTagList = Class.forName("net.minecraft.server." + version + ".NBTTagList");

            nmsPacket = Class.forName("net.minecraft.server." + version + ".Packet");

            nmsNBTTagCompound_hasKey = nmsNBTTagCompound.getMethod("hasKey", String.class);
            nmsNBTTagCompound_get = nmsNBTTagCompound.getMethod("get", String.class);
            nmsNBTTagCompound_set = nmsNBTTagCompound.getMethod("set", String.class, nmsNBTBase);

        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
            PVPINLogManager.log(ex);
        }
    }

}
