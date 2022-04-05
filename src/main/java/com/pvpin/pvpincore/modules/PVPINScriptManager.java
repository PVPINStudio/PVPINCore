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


import com.pvpin.pvpincore.modules.config.ConfigManager;
import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.js.security.PVPINFileSystem;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.listener.JSListener;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.impl.scheduler.ScheduledTaskManager;
import com.pvpin.pvpincore.impl.scheduler.TaskBuilder;
import com.pvpin.pvpincore.modules.js.parser.ParserManager;
import com.pvpin.pvpincore.modules.js.plugin.*;
import com.pvpin.pvpincore.modules.js.security.JSPluginAccessController;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.management.ExecutionListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is used to manage all methods designed for JavaScript plugins.
 *
 * @author William_Shi
 */
public class PVPINScriptManager {

    protected final Map<String, AbstractJSPlugin> MAP = new HashMap<>(ConfigManager.PLUGIN_CAPACITY);
    protected BukkitTask task = null;
    protected final Context SCRIPT_CONTEXT;

    protected PVPINScriptManager() throws Exception {
        // Used by main class PVPINCore only.
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        // Replace the AppClassLoader
        SCRIPT_CONTEXT = Context.newBuilder("js")
                .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL)
                        .targetTypeMapping(Value.class, Object.class, Value::hasArrayElements,
                                v -> new LinkedList<>(v.as(List.class)))
                        .build())
                // https://github.com/oracle/graaljs/issues/214
                .allowCreateProcess(false)
                .allowCreateThread(false)
                .allowIO(true)
                .fileSystem(new PVPINFileSystem())
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .allowNativeAccess(true)
                .allowHostClassLookup(name -> JSPluginAccessController.checkJSLookUpHostClass(name))
                .allowHostClassLoading(true)
                .out(new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out))))
                .allowExperimentalOptions(true)
                .option("js.scripting", "true")
                .option("js.esm-eval-returns-exports", "true")
                .build();
        ExecutionListener listener = ExecutionListener.newBuilder()
                .onEnter(event -> {
                    if (String.valueOf(event.getLocation().getCharacters()).startsWith("eval")) {
                        throw new RuntimeException(I18N.translateByDefault("js.access"));
                    }
                })
                .statements(true)
                .attach(SCRIPT_CONTEXT.getEngine());
        Thread.currentThread().setContextClassLoader(appCl);
    }

    public synchronized Context getContext() {
        if (JSPluginAccessController.isLoadedByJavaScriptEngine()) {
            throw new RuntimeException(I18N.translateByDefault("js.access"));
        }
        return SCRIPT_CONTEXT;
    }

    /**
     * This method is used to load all JavaScript plugins under /js folder.
     */
    public synchronized void onEnable() {
        File js = new File(PVPINCore.getCoreInstance().getDataFolder(), "js");
        if (!js.exists()) {
            js.mkdirs();
            return;
        }
        try {
            for (File file : Objects.requireNonNull(js.listFiles(
                    ((dir, name) -> name.endsWith(".js") && !name.equals("api.js"))
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
    public synchronized void onDisable() {
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
    public synchronized void onReload() {
        onDisable();
        onEnable();
    }

    /**
     * This method is used to stop executing a JavaScript plugin by its name.
     *
     * @param pluginName name of the plugin
     */
    public synchronized void disablePlugin(String pluginName) {
        if (!MAP.containsKey(pluginName)) {
            throw new RuntimeException(I18N.translateByDefault("js.disable.fail") + pluginName);
        }
        AbstractJSPlugin plugin = MAP.get(pluginName);
        if (plugin instanceof LocalFileJSPlugin) {
            PVPINLoggerFactory.getCoreLogger().info(
                    I18N.translateByDefault("js.disable.local"),
                    plugin.getName(),
                    plugin.getVersion(),
                    ((LocalFileJSPlugin) plugin).getSourceFile()
            );
        } else if (plugin instanceof StringJSPlugin) {
            PVPINLoggerFactory.getCoreLogger().info(
                    I18N.translateByDefault("js.disable.string"),
                    plugin.getName(),
                    plugin.getVersion(),
                    Bukkit.getOfflinePlayer(((StringJSPlugin) plugin).getPlayer()).getName()
            );
        }
        MAP.get(pluginName).disable();
        MAP.remove(pluginName);
    }

    /**
     * This method is used to stop executing a JavaScript plugin by its UUID.
     *
     * @param pluginId UUID of the plugin
     */
    public synchronized void disablePlugin(UUID pluginId) {
        MAP.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getId().equals(pluginId))
                .forEach(entry -> disablePlugin(entry.getKey()));
    }

    /**
     * This method is used to load a JavaScript plugin by its file.
     *
     * @param file the javascript file
     */
    public synchronized void enablePlugin(File file) {
        try {
            if (!file.exists()) {
                throw new RuntimeException(I18N.translateByDefault("js.enable.fail") + file.getAbsolutePath());
            }
            ClassLoader appCl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
            String src0 = ParserManager.parse(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            Thread.currentThread().setContextClassLoader(appCl);
            Bukkit.getScheduler().runTask(PVPINCore.getCoreInstance(), () -> {
                LocalFileJSPlugin plugin = new LocalFileJSPlugin(file, src0);
                if (MAP.containsKey(plugin.getName())) {
                    plugin.disable();
                } else {
                    MAP.put(plugin.getName(), plugin);
                    plugin.enable();
                    PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("js.enable.finish"), plugin.getName());
                }
            });
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
    public synchronized void enablePlugin(UUID player, String src) {
        try {
            if (src.isBlank() || src.isEmpty()) {
                throw new RuntimeException(I18N.translateByDefault("js.parser.blank"));
            }
            String src0 = ParserManager.parse("function main(){\n" + src + "}\n");
            Bukkit.getScheduler().runTask(PVPINCore.getCoreInstance(), () -> {
                StringJSPlugin plugin = new StringJSPlugin(player, src0);
                PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("js.enable.string"), plugin.getName(), Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
                if (MAP.containsKey(plugin.getName())) {
                    AbstractJSPlugin old = MAP.get(plugin.getName());
                    plugin.disable();
                } else {
                    MAP.put(plugin.getName(), plugin);
                    plugin.enable();
                    PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("js.enable.finish"), plugin.getName());
                }
            });
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
    public synchronized void enablePlugin(UUID player, BookMeta src) {
        try {
            String src0 = ParserManager.parse("function main(){\n" + BookJSPlugin.getContents(src) + "}\n");
            Bukkit.getScheduler().runTask(PVPINCore.getCoreInstance(), () -> {
                BookJSPlugin plugin = new BookJSPlugin(player, src, src0);
                PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("js.enable.string"), plugin.getName(), Bukkit.getOfflinePlayer(plugin.getPlayer()).getName());
                if (MAP.containsKey(plugin.getName())) {
                    AbstractJSPlugin old = MAP.get(plugin.getName());
                    plugin.disable();
                } else {
                    MAP.put(plugin.getName(), plugin);
                    plugin.enable();
                    PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("js.enable.finish"), plugin.getName());
                }
            });
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

    public AbstractJSPlugin getPluginByUUID(UUID uuid) {
        AtomicReference<AbstractJSPlugin> reference = new AtomicReference<>();
        MAP.forEach((str, plugin) -> {
            if (reference.get() != null) {
                return;
            }
            if (plugin.getId().equals(uuid)) {
                reference.set(plugin);
            }
        });
        return reference.get();
    }

    /**
     * This method is used to get all the JavaScript plugins that have been loaded.
     *
     * @return all JavaScript plugins loaded
     */
    public Collection<AbstractJSPlugin> getAllPlugins() {
        return MAP.values();
    }

    /**
     * This method is used to log a msg using the corresponding logger of the JavaScript plugin.
     *
     * @param msg the log message
     */
    public void log(String msg) {
        try {
            if (!JSPluginAccessController.isLoadedByJavaScriptEngine()) {
                throw new IllegalAccessException(I18N.translateByDefault("js.log"));
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

}
