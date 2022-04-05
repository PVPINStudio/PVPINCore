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
package com.pvpin.pvpincore.modules;

import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.modules.boot.BootStrap;
import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.jsloader.command.MainCommand;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.bukkit.Bukkit;
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
        try {
            coreInstance = this;
            BootStrap.boot();
            pluginManagerInstance = new PVPINPluginManager();
            scriptManagerInstance = new PVPINScriptManager();
        } catch (Exception ex) {
            ex.printStackTrace();
            // Logger is not initialized yet.
        }

        CommandManager.registerNewCmd("pvpincore");
        var mainCmd = new MainCommand();
        Bukkit.getPluginCommand("pvpincore").setExecutor(mainCmd);
        Bukkit.getPluginCommand("pvpincore").setTabCompleter(mainCmd);
        // Register command /pvpincore.
        PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("init.cmd"));

        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        // Ignore the warning that the polyglot context is using an implementation
        // that does not support runtime compilation.
        System.setProperty("js.esm-eval-returns-exports", "true");
        // Expose the ES module namespace exported object to a Polyglot Context.

        PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("init.js"));
        scriptManagerInstance.onEnable();
        PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("init.ok"));
        // Salute PVPIN !
        // This is the output of PVPIN JavaScript Runtime since 2017.
    }

    @Override
    public void onDisable() {
        scriptManagerInstance.onDisable();
        PVPINLoggerFactory.getCoreLogger().info("全部 JavaScript 插件卸载完毕");
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
