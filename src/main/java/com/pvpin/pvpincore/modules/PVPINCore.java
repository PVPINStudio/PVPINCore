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
package com.pvpin.pvpincore.modules;

import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.impl.persistence.PersistenceManager;
import com.pvpin.pvpincore.modules.command.MainCommand;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import com.pvpin.pvpincore.modules.swing.JFrameManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author William_Shi
 */
public class PVPINCore extends JavaPlugin {

    protected static PVPINCore coreInstance;
    protected static PVPINPluginManager pluginManagerInstance;
    protected static PVPINScriptManager scriptManagerInstance;

    @Override
    public void onEnable() {
        coreInstance = this;
        pluginManagerInstance = new PVPINPluginManager();
        scriptManagerInstance = new PVPINScriptManager();

        try {
            Class.forName("com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory");
            // Logging is initialized first.
            Class.forName("com.pvpin.pvpincore.modules.utils.VersionChecker");
            // VersionChecker is used in many NMS related classes.
            // So load it before any NMSUtils subclass is loaded.

            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .acceptPackages("com.pvpin.pvpincore")
                    .scan()) {
                scanResult.getClassesWithAnnotation(PVPINLoadOnEnable.class.getName())
                        .forEach(action -> {
                            try {
                                Class.forName(action.getName());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            // Logger is not initialized yet.
        }

        CommandManager.registerNewCmd("pvpincore");
        var mainCmd = new MainCommand();
        Bukkit.getPluginCommand("pvpincore").setExecutor(mainCmd);
        Bukkit.getPluginCommand("pvpincore").setTabCompleter(mainCmd);

        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        // Ignore the warning that the polyglot context is using an implementation
        // that does not support runtime compilation.

        scriptManagerInstance.onEnable();
        PVPINLoggerFactory.getCoreLogger().info("PVPIN OK.");
        // Salute PVPIN !
        // This is the output of PVPIN JavaScript Runtime since 2017.
    }

    @Override
    public void onDisable() {
        scriptManagerInstance.onDisable();
        PersistenceManager.saveAll();
    }

    public static Plugin getCoreInstance() {
        return coreInstance;
    }

    public static PVPINPluginManager getPluginManagerInstance() {
        return pluginManagerInstance;
    }

    public static PVPINScriptManager getScriptManagerInstance() {
        return scriptManagerInstance;
    }

}
