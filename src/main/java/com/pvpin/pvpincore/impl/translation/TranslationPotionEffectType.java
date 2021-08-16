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

import static com.pvpin.pvpincore.impl.translation.TranslationManager.getEN_USName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_CNName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_TWName;

import com.pvpin.pvpincore.impl.nms.NMSUtils;
import com.pvpin.pvpincore.api.PVPINLogManager;

import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.utils.VersionChecker;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;

/**
 * @author William_Shi
 */
public class TranslationPotionEffectType {

    protected static String getMojangKey(PotionEffectType type) {
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            return "effect.minecraft." + PotionTypeTranslationNMSUtils.getPotionEffectTypeKey(type);
        } else {
            return "effect." + PotionTypeTranslationNMSUtils.getPotionEffectTypeKey(type);
        }
    }

    /**
     * @param locale locale type
     * @param type   potion effect type
     * @return localized name
     */
    public static String getLocalizedName(String locale, PotionEffectType type) {
        String str = null;
        switch (locale) {
            case "zh_cn": {
                str = getZH_CNName(getMojangKey(type));
                break;
            }
            case "zh_tw": {
                str = getZH_TWName(getMojangKey(type));
                break;
            }
            case "en_us": {
                str = getEN_USName(getMojangKey(type));
                break;
            }
        }
        ;
        return str;
    }

    public static TranslatableComponent getTextComponent(PotionEffectType type) {
        return new TranslatableComponent(getMojangKey(type));
    }

}

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
class PotionTypeTranslationNMSUtils extends NMSUtils {

    protected static Class<?> nmsMobEffectList;
    protected static Class<?> obcPotionEffectType;

    protected static Method obcPotionEffectType_getHandle;
    protected static Object nmsIRegistry_MobEffectList;

    static {
        try {
            nmsMobEffectList = Class.forName("net.minecraft.server." + version + ".MobEffectList");
            obcPotionEffectType = Class.forName("org.bukkit.craftbukkit." + version + ".potion.CraftPotionEffectType");
            obcPotionEffectType_getHandle = obcPotionEffectType.getMethod("getHandle");

            Arrays.stream(nmsIRegistry.getDeclaredFields()).forEach(
                    action -> {
                        if (action.getGenericType().getTypeName().contains(nmsMobEffectList.getName())) {
                            if (action.getGenericType() instanceof ParameterizedType) {
                                ParameterizedType type = (ParameterizedType) action.getGenericType();
                                if (type.getRawType().getTypeName().contains(nmsIRegistry.getName())) {
                                    try {
                                        nmsIRegistry_MobEffectList = action.get(null);
                                    } catch (IllegalAccessException ex) {
                                        PVPINLogManager.log(ex);
                                    }
                                }
                            }
                        }
                    }
            );
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | SecurityException ex) {
            PVPINLogManager.log(ex);
        }
    }

    public static String getPotionEffectTypeKey(PotionEffectType type) {
        String ret = null;
        try {
            Object type0 = type;
            if (type instanceof PotionEffectTypeWrapper) {
                type0 = ((PotionEffectTypeWrapper) type).getType();
            }
            Object mobEffectList = obcPotionEffectType_getHandle.invoke(type0);
            Object minecraftKey = nmsIRegistry_getKey.invoke(nmsIRegistry_MobEffectList, mobEffectList);
            ret = (String) nmsMinecraftKey_getKey.invoke(minecraftKey);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }
}
