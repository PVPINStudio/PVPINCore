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
package com.pvpin.pvpincore.modules.js.security;

import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.plugin.AbstractJSPlugin;
import com.pvpin.pvpincore.modules.js.plugin.LocalFileJSPlugin;
import com.pvpin.pvpincore.modules.js.plugin.StringJSPlugin;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.graalvm.polyglot.Context;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class JSPluginAccessController {

    protected static final ClassGraph GRAPH;
    protected static final ScanResult RESULT;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().registerEvents(new ReloadListener(), PVPINCore.getCoreInstance());
                // Tasks start running after all plugins have been loaded.
                // So it can make sure reload listener is the last to be executed according to registry order.
            }
        }.runTaskLater(PVPINCore.getCoreInstance(), 1L);

        File[] plugins = PVPINCore.getCoreInstance().getDataFolder().getParentFile().listFiles(((dir, name) -> name.endsWith(".jar")));
        List<URL> urls = Arrays.stream(plugins).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException ex) {
            }
            return null;
        }).collect(Collectors.toList());
        urls.add(Bukkit.class.getProtectionDomain().getCodeSource().getLocation());
        URL[] urlArray = new URL[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlArray[i] = urls.get(i);
        }
        GRAPH = new ClassGraph().enableAllInfo().overrideClassLoaders(
                new URLClassLoader(urlArray)
        );
        RESULT = GRAPH.scan();
    }

    public static boolean checkJSLookUpHostClass(String className) {
        boolean[] bool = new boolean[1];
        bool[0] = false;
        RESULT.getAllClasses().forEach(classInfo -> {
            if (className.equals(classInfo.getName())) {
                if (classInfo.extendsSuperclass(org.bukkit.event.Event.class.getName())) {
                    bool[0] = true;
                }
            }
        });
        return className.startsWith("com.pvpin.pvpincore") ||
                className.startsWith("net.pvpin.graaljsbox") ||
                className.startsWith("org.bukkit") ||
                className.startsWith("net.minecraft") ||
                className.startsWith("java") ||
                className.startsWith("javax") ||
                className.startsWith("jdk") ||
                className.startsWith("com.sun") ||
                className.startsWith("io.github.plugindustry.wheelcore") ||
                className.startsWith("moe.orangemc.plugincommons") || bool[0];
        // William_Shi 夹带私货
    }

    /**
     * This method is used when a JavaScript plugin illegally accesses some methods,<p>
     * such as accessing java.io or java.nio or something else.
     *
     * @param cxt current context
     */
    public static void denyAccess(Context cxt) {
        if(cxt.getPolyglotBindings().hasMember("internal")){
            return;
            // Contexts used internally can bypass any restriction.
            // Currently, only contexts from js parsers (to call JSHINT or to parse expressions) have that member.
        }
        PVPINLoggerFactory.getCoreLogger().error("已阻止未授权的 JavaScript 操作");
        if (cxt.getPolyglotBindings().getMember("name") != null) {
            PVPINLoggerFactory.getCoreLogger().error("来源:" + cxt.getPolyglotBindings().getMember("name"));
            AbstractJSPlugin plugin = PVPINCore.getScriptManagerInstance().getPluginByName(cxt.getPolyglotBindings().getMember("name").asString());
            if (plugin instanceof LocalFileJSPlugin) {
                PVPINLoggerFactory.getCoreLogger().error("源文件:" + ((LocalFileJSPlugin) plugin).getSourceFile().getName());
            } else if (plugin instanceof StringJSPlugin) {
                PVPINLoggerFactory.getCoreLogger().error("执行插件的玩家:" + Bukkit.getOfflinePlayer(((StringJSPlugin) plugin).getPlayer()).getName());
            }
        }
        cxt.getPolyglotBindings().putMember("close", true);
        throw new RuntimeException("Access Denied.");
    }

    /**
     * This method is used to check if some method is called from JavaScript using Java.type
     *
     * @return true if loaded by JavaScript engine
     */
    public static boolean isLoadedByJavaScriptEngine() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : elements) {
            String name = element.toString();
            if (name.contains("jdk.nashorn")
                    || name.contains("org.graalvm")
                    || name.contains("com.oracle.truffle.polyglot")
                    || name.contains("org.mozilla.javascript")
                    || name.contains("com.eclipsesource.v8")) {
                return true;
            }
        }
        return false;
    }
}

class ReloadListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onReload_Console(ServerCommandEvent event) {
        if (event.getCommand().startsWith("reload") || event.getCommand().startsWith("rl")) {
            PVPINLoggerFactory.getCoreLogger().error("PVPINCore 不支持重载, 请重启服务器");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReload_Player(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/reload") || event.getMessage().startsWith("/rl")) {
            event.getPlayer().sendMessage(ChatColor.RED + "PVPINCore 不支持重载, 请重启服务器");
            PVPINLoggerFactory.getCoreLogger().error("PVPINCore 不支持重载, 请重启服务器");
            event.setCancelled(true);
        }
    }
}