/*
 * The MIT License
 * Copyright © 2020-2021 PVPINStudio
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
package com.pvpin.pvpincore.impl.nms.nbt;

import com.pvpin.pvpincore.impl.nms.NMSUtils;
import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.api.PVPINLogManager;

import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.GZIPInputStream;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class NBTIO extends NMSUtils {

    protected static Class<?> nmsNBTCompressedStreamTools;
    // NBT Input and Output tools in Minecraft
    protected static Method nmsNBTCompressedStreamTools_read;
    protected static Method nmsNBTCompressedStreamTools_write;

    static {
        try {
            nmsNBTCompressedStreamTools = Class.forName("net.minecraft.server." + version + ".NBTCompressedStreamTools");
            nmsNBTCompressedStreamTools_read = nmsNBTCompressedStreamTools.getMethod("a", InputStream.class);
            nmsNBTCompressedStreamTools_write = nmsNBTCompressedStreamTools.getMethod("a", nmsNBTTagCompound, OutputStream.class);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | SecurityException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to write an NBTTagCompound to a file.<p>
     * The original contents of the file will be covered.
     *
     * @param compound NBTTagCompound to be written
     * @param file     file to be written
     */
    public static void writeNBTToFile(Object compound, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
                // 保证文件存在
            }
            if (compound == null) {
                nmsNBTCompressedStreamTools_write.invoke(null, nmsNBTTagCompound.getConstructor().newInstance(), new FileOutputStream(file));
            } else {
                nmsNBTCompressedStreamTools_write.invoke(null, compound, new FileOutputStream(file));
            }
        } catch (IOException
                | IllegalAccessException
                | IllegalArgumentException
                | InstantiationException
                | InvocationTargetException
                | NoSuchMethodException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to read an NBTTagCompound from a file.<p>
     * If the content of the file is malformed, an empty NBTTagCompound will be returned.
     *
     * @param file file to be written
     * @return NBTTagCompound stored in the file
     */
    public static Object readNBTFromFile(File file) {
        Object compound = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                // 保证文件存在
                try {
                    return nmsNBTTagCompound.newInstance();
                } catch (Exception ex) {
                    PVPINLogManager.log(ex);
                }
            }
            compound = nmsNBTCompressedStreamTools_read.invoke(null, new FileInputStream(file));
        } catch (IOException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            try {
                compound = nmsNBTTagCompound.getConstructor().newInstance();
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                PVPINLogManager.log(e);
            }
            PVPINLogManager.log(ex);
        }
        return compound;
    }

}
