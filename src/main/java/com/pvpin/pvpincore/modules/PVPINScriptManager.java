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


import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.api.PVPINPersistence;
import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.listener.JSListener;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.impl.persistence.PersistenceManager;
import com.pvpin.pvpincore.impl.scheduler.ScheduledTaskManager;
import com.pvpin.pvpincore.impl.scheduler.TaskBuilder;
import com.pvpin.pvpincore.modules.js.*;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.util.*;

/**
 * This class is used to manage all methods designed for JavaScript plugins.
 *
 * @author William_Shi
 */
public class PVPINScriptManager {

    protected final Map<String, AbstractJSPlugin> MAP = new HashMap<>(32);
    protected BukkitTask task = null;

    protected PVPINScriptManager() {
        // Used by main class PVPINCore only.
    }

    /**
     * This method is used to load all JavaScript plugins under /js folder.
     */
    public void onEnable() {
        File js = new File(PVPINCore.getCoreInstance().getDataFolder(), "js");
        if (!js.exists()) {
            js.mkdirs();
            return;
        }
        try {
            for (File file : Objects.requireNonNull(js.listFiles(
                    ((dir, name) -> name.endsWith(".js"))
            ))) {
                this.enablePlugin(file);
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                List<String> temp = new ArrayList<>(16);
                MAP.entrySet().forEach(entry -> {
                    if (!entry.getValue().isValid()) {
                        temp.add(entry.getKey());
                        // When the plugin is marked 'close' in JSPluginAccessController,
                        // Or it is not valid (e.g. the executor player of a BookJSPlugin is offline).
                    }
                });
                temp.forEach(action -> PVPINCore.getScriptManagerInstance().disablePlugin(action));
            }
        }.runTaskTimer(PVPINCore.getCoreInstance(), 0, 60L);
    }

    /**
     * This method is used to stop all JavaScript plugins.
     */
    public void onDisable() {
        MAP.forEach((name, plugin) -> {
            plugin.disable();
        });
        MAP.clear();
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * This method is used to stop all loaded plugins, re-scan the /js folder and load all plugins.
     */
    public void onReload() {
        onDisable();
        onEnable();
    }

    /**
     * This method is used to stop executing a JavaScript plugin by its name.
     *
     * @param pluginName name of the plugin
     */
    public void disablePlugin(String pluginName) {
        if (!MAP.containsKey(pluginName)) {
            throw new RuntimeException("No such plugin: " + pluginName);
        }
        MAP.get(pluginName).disable();
        MAP.remove(pluginName);
    }

    /**
     * This method is used to load a JavaScript plugin by its file.
     *
     * @param file the javascript file
     */
    public void enablePlugin(File file) {
        try {
            if (!file.exists()) {
                throw new RuntimeException("No such file: " + file.getAbsolutePath());
            }
            LocalFileJSPlugin plugin = new LocalFileJSPlugin(file);
            PVPINLoggerFactory.getCoreLogger().info("正在加载JavaScriptPlugin: " + plugin.getName() + " 源文件" + plugin.getSourceFile().getName());
            if (plugin.getName().isBlank() || plugin.getName().isEmpty() || plugin.getName().endsWith(" ") || plugin.getName().startsWith(" ")) {
                PVPINLoggerFactory.getCoreLogger().warn("加载" + plugin.getSourceFile().getName() + "失败，无法识别名称");
                plugin.disable();
            }
            if (plugin.getVersion().isBlank() || plugin.getVersion().isEmpty() || plugin.getVersion().endsWith(" ") || plugin.getVersion().startsWith(" ")) {
                PVPINLoggerFactory.getCoreLogger().warn("加载" + plugin.getSourceFile().getName() + "失败，无法识别版本");
                plugin.disable();
            }
            if (plugin.getAuthor().isBlank() || plugin.getAuthor().isEmpty() || plugin.getAuthor().endsWith(" ") || plugin.getAuthor().startsWith(" ")) {
                PVPINLoggerFactory.getCoreLogger().warn("加载" + plugin.getSourceFile().getName() + "失败，无法识别作者");
                plugin.disable();
            }
            if (MAP.containsKey(plugin.getName())) {
                AbstractJSPlugin old = MAP.get(plugin.getName());
                if (old instanceof LocalFileJSPlugin) {
                    PVPINLoggerFactory.getCoreLogger().warn("重复自文件加载JavaScriptPlugin");
                    PVPINLoggerFactory.getCoreLogger().warn("加载 " + old.getName() + " 版本" + old.getVersion() + " 源文件" + ((LocalFileJSPlugin) old).getSourceFile());
                    PVPINLoggerFactory.getCoreLogger().warn("忽略 " + plugin.getName() + " 版本" + plugin.getVersion() + " 源文件" + plugin.getSourceFile());
                    PVPINLoggerFactory.getCoreLogger().warn("名称重复的JavaScriptPlugin同时仅能加载一个！");
                } else if (old instanceof StringJSPlugin) {
                    PVPINLoggerFactory.getCoreLogger().warn("重复自文件加载JavaScriptPlugin ( " + plugin.getSourceFile().getName() + " )");
                    PVPINLoggerFactory.getCoreLogger().warn("加载 " + old.getName() + " 版本" + old.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(((StringJSPlugin) old).getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("忽略 " + plugin.getName() + " 版本" + plugin.getVersion() + " 源文件" + plugin.getSourceFile());
                    PVPINLoggerFactory.getCoreLogger().warn("名称重复的JavaScriptPlugin同时仅能加载一个！");
                }
                plugin.disable();
            } else {
                MAP.put(plugin.getName(), plugin);
                plugin.enable();
                PVPINLoggerFactory.getCoreLogger().info("JavaScriptPlugin: " + plugin.getName() + " 加载完毕");
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to load a String JavaScript plugin.
     *
     * @param player the executor of the plugin
     * @param src    the javascript code
     */
    public void enablePlugin(UUID player, String src) {
        try {
            if (src.isBlank() || src.isEmpty()) {
                throw new RuntimeException("Invalid JavaScript Code.");
            }
            StringJSPlugin plugin = new StringJSPlugin(player, src);
            PVPINLoggerFactory.getCoreLogger().info("正在加载JavaScriptPlugin: " + plugin.getName() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
            if (MAP.containsKey(plugin.getName())) {
                AbstractJSPlugin old = MAP.get(plugin.getName());
                if (old instanceof LocalFileJSPlugin) {
                    PVPINLoggerFactory.getCoreLogger().warn("重复加载JavaScriptPlugin");
                    PVPINLoggerFactory.getCoreLogger().warn("加载 " + old.getName() + " 版本" + old.getVersion() + " 源文件" + ((LocalFileJSPlugin) old).getSourceFile());
                    PVPINLoggerFactory.getCoreLogger().warn("忽略 " + plugin.getName() + " 版本" + plugin.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("名称重复的JavaScriptPlugin同时仅能加载一个！");
                } else if (old instanceof StringJSPlugin) {
                    PVPINLoggerFactory.getCoreLogger().warn("重复加载JavaScriptPlugin");
                    PVPINLoggerFactory.getCoreLogger().warn("加载 " + old.getName() + " 版本" + old.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(((StringJSPlugin) old).getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("忽略 " + plugin.getName() + " 版本" + plugin.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("名称重复的JavaScriptPlugin同时仅能加载一个！");
                }
                plugin.disable();
            } else {
                MAP.put(plugin.getName(), plugin);
                plugin.enable();
                PVPINLoggerFactory.getCoreLogger().info("JavaScriptPlugin: " + plugin.getName() + " 加载完毕");
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to load a String JavaScript plugin.
     *
     * @param player the executor of the plugin
     * @param src    the javascript code
     */
    public void enablePlugin(UUID player, BookMeta src) {
        try {
            BookJSPlugin plugin = new BookJSPlugin(player, src);
            PVPINLoggerFactory.getCoreLogger().info("正在加载JavaScriptPlugin: " + plugin.getName() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
            if (MAP.containsKey(plugin.getName())) {
                AbstractJSPlugin old = MAP.get(plugin.getName());
                if (old instanceof LocalFileJSPlugin) {
                    PVPINLoggerFactory.getCoreLogger().warn("重复加载JavaScriptPlugin");
                    PVPINLoggerFactory.getCoreLogger().warn("加载 " + old.getName() + " 版本" + old.getVersion() + " 源文件" + ((LocalFileJSPlugin) old).getSourceFile());
                    PVPINLoggerFactory.getCoreLogger().warn("忽略 " + plugin.getName() + " 版本" + plugin.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("名称重复的JavaScriptPlugin同时仅能加载一个！");
                } else if (old instanceof StringJSPlugin) {
                    PVPINLoggerFactory.getCoreLogger().warn("重复加载JavaScriptPlugin");
                    PVPINLoggerFactory.getCoreLogger().warn("加载 " + old.getName() + " 版本" + old.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(((StringJSPlugin) old).getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("忽略 " + plugin.getName() + " 版本" + plugin.getVersion() + " 执行插件的玩家" + Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
                    PVPINLoggerFactory.getCoreLogger().warn("名称重复的JavaScriptPlugin同时仅能加载一个！");
                }
                plugin.disable();
            } else {
                MAP.put(plugin.getName(), plugin);
                plugin.enable();
                PVPINLoggerFactory.getCoreLogger().info("JavaScriptPlugin: " + plugin.getName() + " 加载完毕");
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to get a JavaScript plugin by its name.
     *
     * @param name name of the plugin
     * @return js plugin of this name, or null if not found
     */
    public AbstractJSPlugin getPluginByName(String name) {
        return MAP.getOrDefault(name, null);
    }

    /**
     * This method is used to get all the JavaScript plugins that have been loaded.
     *
     * @return all JavaScript plugins loaded
     */
    public List getAllPlugins() {
        return Arrays.asList(MAP.values().toArray());
    }

    /**
     * This method is used to log a msg using the corresponding logger of the JavaScript plugin.
     *
     * @param msg the log message
     */
    public void log(String msg) {
        try {
            if (!JSPluginAccessController.isLoadedByJavaScriptEngine()) {
                throw new IllegalAccessException();
            }
            Context cxt = Context.getCurrent();
            String name = cxt.getPolyglotBindings().getMember("name").asString();
            PVPINCore.getScriptManagerInstance().getPluginByName(name).isValid();
            PVPINLogManager.getLogger(name).info(msg);
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * Register an event listener for a JavaScript plugin.<p>
     * e.g.<p>
     * scriptManager.registerListener("org.bukkit.event.server.ServerCommandEvent", Java.type("org.bukkit.event.EventPriority").NORMAL, false, function (event) { });
     *
     * @param eventClass      name of the event, like "org.bukkit.server.ServerCommandEvent"
     * @param priority        priority of the event
     * @param ignoreCancelled true if ignore cancelled events
     * @param callback        function to be executed when an event is called
     * @return the listener registered (to unregister in the future)
     * @see ListenerManager#registerListener(String, EventPriority, boolean, Value)
     */
    public JSListener registerListener(String eventClass, EventPriority priority, boolean ignoreCancelled, Value callback) {
        return ListenerManager.registerListener(eventClass, priority, ignoreCancelled, callback);
    }

    /**
     * This method is used to register a command for a Java.<p>
     * e.g.<p>
     * scriptManager.registerCommand("test", function (sender, cmd, args) { sender.sendMessage("JSCommandTest"); }, function (sender, cmd, args) { return ["tab"]; });
     *
     * @param cmd         the name of the command to be registered, e.g. suicide
     * @param cmdCallback function (sender, cmd, args){}
     * @param tabCallback function (sender, cmd, args){}
     */
    public void registerCommand(String cmd, Value cmdCallback, Value tabCallback) {
        CommandManager.registerJavaScriptCmd(cmd, cmdCallback, tabCallback);
    }

    /**
     * This method is used to unregister a command from the server.
     *
     * @param cmd the name of the command to be unregistered, e.g. suicide
     * @see com.pvpin.pvpincore.impl.command.CommandManager#unregisterJavaScriptCmd(String)
     */
    public void unregisterCommand(String cmd) {
        CommandManager.unregisterJavaScriptCmd(cmd);
    }

    /**
     * This method is used to generate a new task builder.
     *
     * @return a new task builder
     * @see TaskBuilder#buildAndRun()
     */
    public TaskBuilder registerTask() {
        return ScheduledTaskManager.newTaskBuilder();
    }

    /**
     * This method is used to get a map to store data permanently.
     *
     * @return the data map of the plugin calling this method
     * @see PVPINPersistence#getDataMap()
     */
    public Map getDataMap() {
        if (JSPluginAccessController.isLoadedByJavaScriptEngine()) {
            return PersistenceManager.getCurrentHolder().getDataMap();
        }
        return null;
    }

    /**
     * This method reads the contents in the file and disposes the current data in memory.
     */
    public void readPersistentDataFromFile() {
        PersistenceManager.getCurrentHolder().readFromFile();
    }

    /**
     * This method writes the data in memory to a file to store permanently.
     */
    public void savePersistentDataToFile() {
        PersistenceManager.getCurrentHolder().saveToFile();
    }

}
