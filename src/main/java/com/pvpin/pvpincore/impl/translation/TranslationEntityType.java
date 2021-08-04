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

import com.pvpin.pvpincore.impl.nms.entity.EntityNMSUtils;

import static com.pvpin.pvpincore.impl.translation.TranslationManager.getEN_USName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_CNName;
import static com.pvpin.pvpincore.impl.translation.TranslationManager.getZH_TWName;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.modules.utils.VersionChecker;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author William_Shi
 */
public class TranslationEntityType {

    protected static String getMojangKey(EntityType type) {
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            return EntityTypeTranslationNMSUtils.getEntityNameOrKey(type);
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
class EntityTypeTranslationNMSUtils extends EntityNMSUtils {

    protected static final Random ran;

    protected static Method nmsEntity_getName;
    protected static Method nmsEntityTypes_getName;

    static {
        ran = new Random();
        try {
            if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
                nmsEntity_getEntityType = nmsEntity.getMethod("getEntityType");
                // 1.13 or above.
                for (Method method : nmsEntityTypes.getMethods()) {
                    if (method.getAnnotatedReturnType().getType().getTypeName().toString().toLowerCase().contains("component")) {
                        nmsEntityTypes_getName = method;
                        // 1.13 or above.
                        break;
                    }
                }
            } else {
                nmsEntity_getName = nmsEntity.getMethod("getName");
                // 1.12.
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * @param type type of the entity
     * @return translation key, such as entity.minecraft.sheep
     */
    public static String getEntityNameOrKey(EntityType type) {
        String[] ret = new String[1];
        Location ranLoc = new Location(Bukkit.getWorlds().get(0), ran.nextInt(16), 2, ran.nextInt(16));
        ranLoc.getChunk().load();
        Entity en = Bukkit.getWorlds().get(0).spawnEntity(ranLoc, type);
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            ranLoc.getChunk().setForceLoaded(true);
            try {
                Object nmsEn = getNMSEntity(en);
                Object nmsType = nmsEntity_getEntityType.invoke(nmsEn);
                String component = nmsEntityTypes_getName.invoke(nmsType).toString();
                ret[0] = component.split("'")[1];
                // Above 1.13, use EntityTypes to get.
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                PVPINLogManager.log(ex);
            } finally {
                en.remove();
                // Kill after use.
            }
        } else {
            try {
                Object nmsEn = getNMSEntity(en);
                ret[0] = (String) nmsEntity_getName.invoke(nmsEn);
            } catch (IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                PVPINLogManager.log(ex);
            } finally {
                en.remove();
                // Kill the entity after use.
            }
        }
        return ret[0];
    }
}