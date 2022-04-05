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
import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.js.plugin.AbstractJSPlugin;
import com.pvpin.pvpincore.modules.js.plugin.LocalFileJSPlugin;
import com.pvpin.pvpincore.modules.js.plugin.StringJSPlugin;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class JSPluginAccessController {

    private static final List<String> EVENT_CLASSES = new ArrayList<>(16);
    private static final List<String> BLACKLIST;
    private static final List<String> WHITELIST;

    static {
        File[] plugins = PVPINCore.getCoreInstance().getDataFolder().getParentFile().listFiles(((dir, name) -> name.endsWith(".jar")));
        List<URL> urls = Arrays.stream(plugins).map(file -> {
            try {
                return file.toURI().toURL();
            } catch (MalformedURLException ignored) {
            }
            return null;
        }).collect(Collectors.toList());
        urls.add(Bukkit.class.getProtectionDomain().getCodeSource().getLocation());
        URL[] urlArray = new URL[urls.size()];
        for (int i = 0; i < urls.size(); i++) {
            urlArray[i] = urls.get(i);
        }
        ClassGraph graph = new ClassGraph().enableAllInfo().overrideClassLoaders(
                new URLClassLoader(urlArray)
        );
        graph.scan().getAllClasses().forEach(classInfo -> {
            if (classInfo.extendsSuperclass("org.bukkit.event.Event")) {
                EVENT_CLASSES.add(classInfo.getName());
            }
        });

        BLACKLIST = List.of("sun.misc.Unsafe", "java.net", "java.lang.reflect", "java.security",
                "java.lang.Runtime", "java.lang.System", "java.lang.Class", "java.lang.ClassLoader",
                "java.lang.Thread", "java.lang.ThreadGroup", "java.lang.ProcessBuilder");

        WHITELIST = List.of("com.pvpin", "net.pvpin",
                "java", "jdk", "javax",
                "org.bukkit", "net.md_5", "net.minecraft", "com.mojang", "org.spigotmc",
                "io.github.plugindustry", "rarityeg.mc.plugins");
    }

    public static boolean checkJSLookUpHostClass(String className) {
        if (BLACKLIST.stream().anyMatch(className::startsWith)) {
            return false;
        }
        if (WHITELIST.stream().anyMatch(className::startsWith)) {
            return true;
        }
        return EVENT_CLASSES.stream().anyMatch(className::startsWith);
    }

    /**
     * This method is used when a JavaScript plugin illegally accesses some methods,<p>
     * such as accessing java.io or java.nio or something else.
     *
     * @param cxt current context
     */
    @Deprecated
    public static void denyAccess(Context cxt) {
        if (cxt.getPolyglotBindings().hasMember("internal")) {
            return;
            // Contexts used internally can bypass any restriction.
            // Currently, only contexts from js parsers (to call ESPREE or to parse expressions) have that member.
        }
        throw new RuntimeException(I18N.translateByDefault("js.access"));
    }

    /**
     * This method is used to check if some method is called from JavaScript using Java.type
     *
     * @return true if loaded by JavaScript engine
     */
    public static boolean isLoadedByJavaScriptEngine() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .anyMatch(name -> name.contains("jdk.nashorn")
                        || name.contains("org.graalvm")
                        || name.contains("com.oracle.truffle.polyglot")
                        || name.contains("org.mozilla.javascript")
                        || name.contains("com.eclipsesource.v8"));
    }
}