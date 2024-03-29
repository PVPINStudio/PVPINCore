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

import com.pvpin.pvpincore.api.PVPINListener;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.modules.js.security.JSPluginAccessController;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is used to manage automatic services provided by PVPINCore.
 *
 * @author William_Shi
 */
public class PVPINPluginManager {

    protected final List<String> loaded;

    protected PVPINPluginManager() {
        loaded = new ArrayList<>(16);
        // Used by main class PVPINCore only.
    }

    /**
     * This method is used to initialize some automatic services.<p>
     * By calling this method from another Java Plugin,
     * classes from that plugin are scanned using ClassGraph.<p>
     * Classes are not loaded because they are scanned using ASM.<p>
     * The classes with certain annotation (such as @PVPINListener interface)
     * are processed (such as registered).
     *
     * @throws Exception if the class calling this method does not exist (will never happen)
     */
    public void registerAll() throws Exception {
        if (JSPluginAccessController.isLoadedByJavaScriptEngine()) {
            PVPINLoggerFactory.getCoreLogger().warn("JavaScript 正在试图通过 PVPINCore 加载 Java 插件");
        }
        String pluginName = JavaPlugin.getProvidingPlugin(Class.forName(Thread.currentThread().getStackTrace()[2].getClassName())).getName();
        /*
         * 0 getStackTrace
         * 1 registerAll
         * 2 Some method from other plugins
         */
        PVPINLoggerFactory.getCoreLogger().info("正在加载JavaPlugin: " + pluginName);
        if (loaded.contains(pluginName)) {
            PVPINLoggerFactory.getCoreLogger().warn("重复加载JavaPlugin: " + pluginName);
        } else {
            loaded.add(pluginName);
        }
        ScanResult result = scanClasses();
        ListenerManager.registerListener(result.getAllClasses().filter(
                classInfo -> classInfo.hasAnnotation(PVPINListener.class.getName())
        ).loadClasses());

        result.close();
        // Close manually.
        // Try-with is currently not used.
        PVPINLoggerFactory.getCoreLogger().info("JavaPlugin: " + pluginName + " 加载完毕");
    }

    protected ScanResult scanClasses() throws Exception {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
        /*
         * 0 java.lang.Thread.getStackTrace
         * 1 com.pvpin.pvpincore.modules.utils.ClassScanner.scanClasses
         * 2 com.pvpin.pvpincore.modules.PVPINPluginManager.registerAll
         * 3 Other Plugin
         */
        String path = Class.forName(trace.getClassName()).getProtectionDomain().getCodeSource().getLocation().getFile();
        ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptJars(
                        path.split("/")[path.split("/").length - 1]
                )
                .overrideClassLoaders(
                        //new URLClassLoader(
                        //        new URL[]{
                        //            Class.forName(trace.getClassName()).getProtectionDomain().getCodeSource().getLocation()
                        //        })
                        Class.forName(trace.getClassName()).getClassLoader()
                ).scan();
        scanResult.getAllClasses().forEach(action -> {
            // PVPINLoggerFactory.getCoreLogger().info("已扫描类{}", action.getName());
            // Too much output if there are many classes
        });

        return scanResult;
    }

}
