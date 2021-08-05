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
package com.pvpin.pvpincore.impl.nms.block;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.impl.nms.NMSUtils;
import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.utils.VersionChecker;

import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.block.Block;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class BlockNMSUtils extends NMSUtils {

    protected static Class<?> obcBlock;
    protected static Class<?> nmsBlock;
    protected static Class<?> nmsIBlockData;

    static {
        try {
            obcBlock = Class.forName("org.bukkit.craftbukkit." + version + ".block.CraftBlock");
            nmsBlock = Class.forName("net.minecraft.server." + version + ".Block");
            nmsIBlockData = Class.forName("net.minecraft.server." + version + ".IBlockData");

        } catch (ClassNotFoundException
                | SecurityException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * @param block CraftBlock object
     * @return Block object in NMS
     */
    public static Object getNMSBlock(Block block) {
        Object ret = null;
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            try {
                Method obcBlock_getNMS = obcBlock.getMethod("getNMS");
                Object iBlockData = obcBlock_getNMS.invoke(block);
                Method nmsIBlockData_getBlock = nmsIBlockData.getMethod("getBlock");
                Object nmsBl = nmsIBlockData_getBlock.invoke(iBlockData);
                ret = nmsBl;
                // v1_13_R0 or higher.
                // Use CraftBlock#getNMS to get an IBlockData object.
                // Use IBlockData#getBlock to get a Block object.
            } catch (NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                PVPINLogManager.log(ex);
            }
        } else {
            try {
                Method obcBlock_getNMSBlock = obcBlock.getDeclaredMethod("getNMSBlock");
                obcBlock_getNMSBlock.setAccessible(true);
                ret = obcBlock_getNMSBlock.invoke(block);
                // Directly use private method getNMSBlock.
            } catch (NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                PVPINLogManager.log(ex);
            }
        }
        return ret;
    }

}
