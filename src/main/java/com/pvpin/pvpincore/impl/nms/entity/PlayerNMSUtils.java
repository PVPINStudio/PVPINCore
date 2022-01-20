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
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.impl.nms.VersionChecker;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pvpin.pvpincore.impl.nms.VersionChecker.version;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class PlayerNMSUtils extends EntityNMSUtils {

    protected static Class<?> nmsPlayer;
    protected static Class<?> nmsPlayerConnection;
    protected static Class<?> nmsPacketPlayOutChat;
    protected static Class<?> nmsChatComponentText;
    protected static Class<?> nmsIChatBaseComponent;
    protected static Class<?> nmsChatMessageType;

    protected static Method nmsPlayerConnection_sendPacket;

    static {
        try {
            nmsPlayer = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            nmsPlayerConnection = Class.forName("net.minecraft.server." + version + ".PlayerConnection");
            nmsPacketPlayOutChat = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
            nmsChatComponentText = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
            nmsIChatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
            nmsChatMessageType = Class.forName("net.minecraft.server." + version + ".ChatMessageType");

            nmsPlayerConnection_sendPacket = nmsPlayerConnection.getMethod("sendPacket", nmsPacket);
        } catch (ClassNotFoundException
                | NoSuchMethodException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to send an action bar message.<p>
     * An action bar message is the message displayed above the hot bar.<p>
     * Also referred to as GAME_INFO.
     *
     * @param msg    the text to be sent
     * @param player target player
     */
    public static void sendActionBar(String msg, Player player) {
        try {
            Object nmsPlayerObj = getNMSEntity(player);
            Object nmsPlayerConnObj = nmsPlayer.getField("playerConnection").get(nmsPlayerObj);

            Constructor<?> textCons = nmsChatComponentText.getConstructor(String.class);
            Object text = textCons.newInstance(msg);
            Object type = Arrays.stream((Enum[]) nmsChatMessageType.getEnumConstants()).filter(action ->
                    action.name().equals("GAME_INFO")).collect(Collectors.toList()).get(0);
            UUID uid = new UUID(0L, 0L);

            Object packet = null;
            if (VersionChecker.isCurrentHigherOrEquals("v1_16_R0")) {
                Constructor<?> cons = nmsPacketPlayOutChat.getConstructor(
                        nmsIChatBaseComponent, nmsChatMessageType, UUID.class
                );
                packet = cons.newInstance(text, type, uid);
                // Constructor is changed in 1.16.
            } else {
                Constructor<?> cons = nmsPacketPlayOutChat.getConstructor(
                        nmsIChatBaseComponent, nmsChatMessageType
                );
                packet = cons.newInstance(text, type);
            }
            nmsPlayerConnection_sendPacket.invoke(nmsPlayerConnObj, packet);
        } catch (NoSuchFieldException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException
                | InstantiationException ex) {
            PVPINLogManager.log(ex);
        }
    }

}
