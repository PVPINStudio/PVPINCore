/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pvpin.pvpincore.demo;

import com.pvpin.pvpincore.api.PVPINListener;
import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.api.PVPINPersistence;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * @author William_Shi
 */
@PVPINListener
public class ListenerDemo implements Listener {

    @EventHandler
    public void onEventCalled(ServerCommandEvent event) {
        System.out.println(
                "PVPINCore Java 插件测试: ServerCommandEvent 被触发了"
        );
    }

}
