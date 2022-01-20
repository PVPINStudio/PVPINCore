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
package com.pvpin.pvpincore.impl.nms.entity;

import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.impl.nms.NMSUtils;
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;

import static com.pvpin.pvpincore.impl.nms.VersionChecker.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Entity;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class EntityNMSUtils extends NMSUtils {

    protected static Class<?> obcEntity;
    protected static Class<?> nmsEntity;
    protected static Class<?> nmsEntityTypes;

    protected static Method obcEntity_getHandle;
    protected static Method nmsEntity_getEntityType;

    static {

        try {
            obcEntity = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftEntity");
            nmsEntity = Class.forName("net.minecraft.server." + version + ".Entity");
            nmsEntityTypes = Class.forName("net.minecraft.server." + version + ".EntityTypes");

            obcEntity_getHandle = obcEntity.getMethod("getHandle");
        } catch (ClassNotFoundException
                | SecurityException
                | NoSuchMethodException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * @param entity CraftEntity object
     * @return Entity object in NMS
     */
    public static Object getNMSEntity(Entity entity) {
        Object ret = null;
        try {
            ret = obcEntity_getHandle.invoke(entity);
        } catch (SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            PVPINLogManager.log(ex);
        }
        return ret;
    }

}
