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
package com.pvpin.pvpincore.impl.translation;

import static com.pvpin.pvpincore.impl.translation.TranslationManager.getEN_USName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_CNName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_TWName;

import com.pvpin.pvpincore.impl.nms.NMSUtils;
import com.pvpin.pvpincore.api.PVPINLogManager;

import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.potion.PotionEffectType;

/**
 * @author William_Shi
 */
public class TranslationPotionEffectType {

    protected static String getMojangKey(PotionEffectType type) {
        return PotionTypeTranslationNBTUtils.getPotionEffectTypeKey(type);
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
class PotionTypeTranslationNBTUtils extends NMSUtils {

    protected static Class<?> nmsRegistryMaterials;
    protected static Class<?> nmsMobEffectList;

    protected static Method nmsMobEffectList_fromId;
    protected static Method nmsMobEffectList_getName;

    static {
        try {
            nmsRegistryMaterials = Class.forName("net.minecraft.server." + version + ".RegistryMaterials");
            nmsMobEffectList = Class.forName("net.minecraft.server." + version + ".MobEffectList");
            nmsMobEffectList_fromId = nmsMobEffectList.getMethod("fromId", int.class);
            for (Method method : nmsMobEffectList.getMethods()) {
                if (method.getAnnotatedReturnType().getType().equals(String.class)) {
                    if (method.getName().equals("toString")) {
                        continue;
                    }
                    nmsMobEffectList_getName = method;
                }
            }
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | SecurityException ex) {
            PVPINLogManager.log(ex);
        }
    }

    public static String getPotionEffectTypeKey(PotionEffectType type) {
        String ret = null;
        try {
            Object mobEffectList = nmsMobEffectList_fromId.invoke(null, type.getId());
            // Get the object directly from ID.
            ret = (String) nmsMobEffectList_getName.invoke(mobEffectList);
        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }
}
