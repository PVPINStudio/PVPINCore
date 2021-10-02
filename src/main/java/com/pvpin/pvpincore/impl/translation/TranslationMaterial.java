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
package com.pvpin.pvpincore.impl.translation;

import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.impl.nms.block.BlockNMSUtils;
import com.pvpin.pvpincore.impl.nms.itemstack.ItemStackNMSUtils;

import static com.pvpin.pvpincore.impl.translation.TranslationManager.getEN_USName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_CNName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_TWName;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.impl.nms.VersionChecker;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author William_Shi
 */
public class TranslationMaterial {

    protected static String getMojangKey(Material material) {
        if (material.isItem()) {
            ItemStack stack = new ItemStack(material);
            Object nmsItem = ItemStackNMSUtils.getNMSItemStackCopy(stack);
            if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
                return ItemStackTranslationNMSUtils.getItemStackNameOrKey(nmsItem);
            } else {
                return ItemStackTranslationNMSUtils.getItemStackNameOrKey(nmsItem) + ".name";
            }
        } else {
            if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
                return BlockTranslationNMSUtils.getBlockNameOrKey(material);
            } else {
                return BlockTranslationNMSUtils.getBlockNameOrKey(material) + ".name";
            }
        }
    }

    /**
     * @param locale   locale type
     * @param material material to be translated
     * @return localized name
     */
    public static String getLocalizedName(String locale, Material material) {
        String str = null;
        switch (locale) {
            case "zh_cn": {
                str = getZH_CNName(getMojangKey(material));
                break;
            }
            case "zh_tw": {
                str = getZH_TWName(getMojangKey(material));
                break;
            }
            case "en_us": {
                str = getEN_USName(getMojangKey(material));
                break;
            }
        }
        ;
        return str;
    }

    public static TranslatableComponent getTextComponent(Material material) {
        return new TranslatableComponent(getMojangKey(material));
    }

}

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
class ItemStackTranslationNMSUtils extends ItemStackNMSUtils {

    protected static Method nmsItem_getName;

    static {
        try {
            nmsItem_getName = nmsItem.getMethod("getName");
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to get the translation key of an item.<p>
     * In 1.12, DiamondSword's key is swordDiamond, but in 1.13 its key is the same as its material.
     *
     * @param nmsItem ItemStack object in NMS.
     * @return name of the material
     */
    public static String getItemStackNameOrKey(Object nmsItem) {
        try {
            return (String) nmsItem_getName.invoke(nmsItemStack_getItem.invoke(nmsItem));
        } catch (SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return "";
    }

}

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
class BlockTranslationNMSUtils extends BlockNMSUtils {
    protected static Method nmsBlock_getName;
    protected static final Random ran;

    static {
        ran = new Random();
        for (Method method : nmsBlock.getMethods()) {
            if (method.getAnnotatedReturnType().getType().equals(String.class)) {
                if (method.getName().equals("toString")) {
                    continue;
                }
                nmsBlock_getName = method;
                // Method to get the translation key in NMS.
                // a() in 1.12, and k() in 1.13.
            }
        }
    }

    /**
     * This method is used to get the translation key of a block.<p>
     * In 1.12 IronOre's key is oreIron, but in 1.13 its key is the same as its material.
     *
     * @param material material for the block
     * @return name of the material
     */
    public static String getBlockNameOrKey(Material material) {
        String[] ret = new String[1];
        Block bl = Bukkit.getWorlds().get(0).getBlockAt(ran.nextInt(16), 2, ran.nextInt(16));
        bl.getChunk().load();
        while ((bl.getType() != Material.AIR) && (bl.getType() != Material.BEDROCK)) {
            bl = Bukkit.getWorlds().get(0).getBlockAt(ran.nextInt(16), 2, ran.nextInt(16));
        }
        Material former = bl.getType();
        // Save the former one.
        bl.setType(material);
        Object nmsBl = getNMSBlock(bl);
        try {
            ret[0] = (String) nmsBlock_getName.invoke(nmsBl);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            ex.printStackTrace();
        } finally {
            bl.setType(former);
            // Set to the former one to avoid problems.
        }
        return ret[0];
    }
}