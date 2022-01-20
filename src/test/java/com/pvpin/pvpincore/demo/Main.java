/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pvpin.pvpincore.demo;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author William_Shi
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            PVPINCore.getPluginManagerInstance().registerAll();
            Class.forName(CommandDemo.class.getName());
            Class.forName(PersistenceDemo.class.getName());
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

}
