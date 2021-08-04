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
package com.pvpin.pvpincore.impl.nms.itemstack;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.impl.nms.NMSUtils;
import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.utils.VersionChecker;

import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class ItemStackNMSUtils extends NMSUtils {

    protected static Class<?> obcItemStack;
    // CraftItemStack, useful for its methods asNMSCopy and asBukkitCopy.
    protected static Class<?> nmsItemStack;
    // ItemStack in NMS.
    protected static Class<?> nmsItem;
    // Item in NMS (Representing a kind of item corresponding to a material).

    protected static Method obcItemStack_asNMSCopy;
    protected static Method obcItemStack_asBukkitCopy;
    protected static Method nmsItemStack_setTag;
    protected static Method nmsItemStack_getItem;

    static {
        try {
            obcItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            nmsItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
            nmsItem = Class.forName("net.minecraft.server." + version + ".Item");

            obcItemStack_asNMSCopy = obcItemStack.getMethod("asNMSCopy", ItemStack.class);
            obcItemStack_asBukkitCopy = obcItemStack.getMethod("asBukkitCopy", nmsItemStack);
            nmsItemStack_setTag = nmsItemStack.getMethod("setTag", nmsNBTTagCompound);
            nmsItemStack_getItem = nmsItemStack.getMethod("getItem");
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * @param stack ItemStack object
     * @return ItemStack object in NMS
     */
    public static Object getNMSItemStackCopy(ItemStack stack) {
        Object ret = null;
        try {
            ret = obcItemStack_asNMSCopy.invoke(null, stack);
            //直接调用静态方法
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }

    /**
     * This method is used as an extension of the APIs in NMS.<p>
     * Because ItemStack#getOrCreateTag is available in versions higher than 1.12.
     *
     * @param nmsItem ItemStack object in NMS
     * @return the NBTTagCompound of the stack
     */
    public static Object getOrCreateTag(Object nmsItem) {
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            try {
                Method nmsItemStack_getOrCreateTag;
                nmsItemStack_getOrCreateTag = nmsItemStack.getMethod("getOrCreateTag");
                return nmsItemStack_getOrCreateTag.invoke(nmsItem);
                // Above 1.13, call directly.
            } catch (NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                PVPINLogManager.log(ex);
            }
        } else {
            try {
                Method nmsItemStack_hasTag;
                nmsItemStack_hasTag = nmsItemStack.getMethod("hasTag");
                if ((boolean) nmsItemStack_hasTag.invoke(nmsItem)) {
                    Method nmsItemStack_getTag;
                    nmsItemStack_getTag = nmsItemStack.getMethod("getTag");
                    return nmsItemStack_getTag.invoke(nmsItem);
                    // Get if there is a compound.
                } else {
                    Constructor con = NMSUtils.nmsNBTTagCompound.getConstructor();
                    return con.newInstance();
                    // Create one.
                }
            } catch (NoSuchMethodException
                    | SecurityException
                    | InstantiationException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                PVPINLogManager.log(ex);
            }
        }
        return null;
    }

}
