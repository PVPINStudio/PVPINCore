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

import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.impl.nms.entity.EntityNMSUtils;

import static com.pvpin.pvpincore.impl.translation.TranslationManager.getEN_USName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_CNName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_TWName;
import static com.pvpin.pvpincore.modules.utils.VersionChecker.version;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.modules.utils.VersionChecker;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Random;

/**
 * @author William_Shi
 */
public class TranslationEntityType {

    protected static String getMojangKey(EntityType type) {
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            return "entity.minecraft." + EntityTypeTranslationNMSUtils.getEntityNameOrKey(type);
        } else {
            return "entity." + EntityTypeTranslationNMSUtils.getEntityNameOrKey(type) + ".name";
        }
    }

    /**
     * @param locale locale type
     * @param type   entity type
     * @return localized name
     */
    public static String getLocalizedName(String locale, EntityType type) {
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

    public static TranslatableComponent getTextComponent(EntityType type) {
        return new TranslatableComponent(getMojangKey(type));
    }

}

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
class EntityTypeTranslationNMSUtils extends EntityNMSUtils {

    protected static Class<?> nmsRegistryBlocks;

    protected static Method nmsRegistryBlocks_get;

    protected static Object nmsIRegistry_EntityTypes;

    static {
        try {
            nmsRegistryBlocks = Class.forName("net.minecraft.server." + version + ".RegistryBlocks");

            nmsRegistryBlocks_get = nmsRegistryBlocks.getMethod("get", nmsMinecraftKey);

            Arrays.stream(nmsIRegistry.getDeclaredFields()).forEach(
                    action -> {
                        if (action.getGenericType().getTypeName().contains(nmsEntityTypes.getName())) {
                            if (action.getGenericType() instanceof ParameterizedType) {
                                ParameterizedType type = (ParameterizedType) action.getGenericType();
                                if (type.getRawType().getTypeName().contains("Registry")) {
                                    try {
                                        nmsIRegistry_EntityTypes = action.get(null);
                                    } catch (IllegalAccessException ex) {
                                        PVPINLogManager.log(ex);
                                    }
                                }
                            }
                        }
                    }
            );
        } catch (ClassNotFoundException
                | NoSuchMethodException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * @param type type of the entity
     * @return translation key, such as entity.minecraft.sheep
     */
    public static String getEntityNameOrKey(EntityType type) {
        String ret = null;
        try {
            Constructor cons = nmsMinecraftKey.getConstructor(String.class);
            Object key = cons.newInstance(type.getKey().getKey());
            Object entityTypes = nmsRegistryBlocks_get.invoke(nmsIRegistry_EntityTypes, key);
            Object minecraftKey = nmsIRegistry_getKey.invoke(nmsIRegistry_EntityTypes, entityTypes);
            ret = (String) nmsMinecraftKey_getKey.invoke(minecraftKey);
        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }
}