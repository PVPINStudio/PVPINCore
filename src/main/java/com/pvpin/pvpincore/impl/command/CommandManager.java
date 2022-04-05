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
package com.pvpin.pvpincore.impl.command;

import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.config.ConfigManager;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.Value;
import sun.misc.Unsafe;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class CommandManager {

    protected static final List<JSCommand> JAVASCRIPT_CMDS = new ArrayList(ConfigManager.PLUGIN_COMMAND_CAPACITY);

    /**
     * This method is used to register a new command.<p>
     * After registration, you can access the command with Bukkit#getPluginCommand.<p>
     * Command Executor needs to be bound manually.
     *
     * @param cmd    name of the command
     * @param plugin instance of the java plugin, under whose namespace will the command be registered
     */
    public static void registerNewCmd(String cmd, JavaPlugin plugin) {
        Constructor<PluginCommand> constructor;
        PluginCommand pluginCmd = null;
        try {
            constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            // Constructor is protected.
            pluginCmd = constructor.newInstance(cmd, plugin);
            Field cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            cmdMapField.setAccessible(true);
            CommandMap map = (CommandMap) cmdMapField.get(Bukkit.getServer().getPluginManager());
            map.register(cmd, pluginCmd);
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException
                | NoSuchFieldException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to register a new command.<p>
     * After registration, you can access the command with Bukkit#getPluginCommand.<p>
     * Command Executor needs to be bound manually.<p>
     * The plugin is registered under the PVPINCore namespace.<p>
     * If you want to register it under your plugin's namespace, add a java plugin parameter.<p>
     * Recommended only if you are registering a command from a JavaScript plugin.
     *
     * @param cmd name of the command
     */
    public static void registerNewCmd(String cmd) {
        Constructor<PluginCommand> constructor;
        PluginCommand pluginCmd = null;
        try {
            constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            pluginCmd = constructor.newInstance(cmd, PVPINCore.getCoreInstance());
            // JavaScript plugins do not have java plugin instances.
            // So plugins can be registered using PVPINCore instance.
            Field cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            cmdMapField.setAccessible(true);
            CommandMap map = (CommandMap) cmdMapField.get(Bukkit.getServer().getPluginManager());
            map.register(cmd, pluginCmd);
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException
                | NoSuchFieldException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to register a command for a Java.
     *
     * @param cmd         the name of the command to be registered, e.g. suicide
     * @param cmdCallback function (sender, cmd, args){}
     * @param tabCallback function (sender, cmd, args){}
     */
    public static void registerJavaScriptCmd(String cmd, Value cmdCallback, Value tabCallback) {
        if (Bukkit.getPluginCommand(cmd) == null) {
            registerNewCmd(cmd);
        }
        JSCommand jsCmd = new JSCommand(cmd, cmdCallback, tabCallback);
        JAVASCRIPT_CMDS.add(jsCmd);
        Bukkit.getPluginCommand(cmd).setExecutor(jsCmd);
        Bukkit.getPluginCommand(cmd).setTabCompleter(jsCmd);
    }

    /**
     * This method is used to unregister a command from the server.
     *
     * @param cmd the name of the command to be unregistered, e.g. suicide
     */
    public static void unregisterJavaScriptCmd(String cmd) {
        try {
            Field cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            cmdMapField.setAccessible(true);
            CommandMap map = (CommandMap) cmdMapField.get(Bukkit.getServer().getPluginManager());
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(map);
            List<String> temp = new ArrayList<>(16);
            knownCommands.forEach((key, value) -> {
                if (value.getName().equalsIgnoreCase(cmd)
                        || value.getAliases().stream().anyMatch(action -> action.equalsIgnoreCase(cmd))) {
                    Bukkit.getPluginCommand(cmd).unregister(map);
                    temp.add(key);
                }
            });
            temp.forEach(knownCommands::remove);
        } catch (NoSuchFieldException
                | IllegalAccessException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to unregister all commands under a JavaScript plugin.
     *
     * @param pluginName the name of the plugin
     */
    public static void unregisterJavaScriptCmds(String pluginName) {
        List<JSCommand> temp = new ArrayList(16);
        JAVASCRIPT_CMDS.forEach(action -> {
            if (action.plugin.getName().equals(pluginName)) {
                unregisterJavaScriptCmd(action.cmdName);
                temp.add(action);
            }
        });
        temp.forEach(JAVASCRIPT_CMDS::remove);
    }

}
