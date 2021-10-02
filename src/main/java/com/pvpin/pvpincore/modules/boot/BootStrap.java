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
package com.pvpin.pvpincore.modules.boot;

import com.pvpin.pvpincore.impl.nms.VersionChecker;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.JSSecurityManager;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.io.File;
import java.security.Policy;

/**
 * @author William_Shi
 */
public class BootStrap {
    public static void boot() throws Exception {
        PVPINLoggerFactory.loadLoggers();
        // Logging is initialized first.
        PVPINLoggerFactory.getCoreLogger().info("日志系统初始化完毕");

        LibraryLoader.loadLibraries();
        // Download libraries.
        PVPINLoggerFactory.getCoreLogger().info("依赖加载完毕");

        PVPINCore.getCoreInstance().saveResource("pvpin.policy", true);
        System.setProperty("java.security.policy", new File(PVPINCore.getCoreInstance().getDataFolder(), "pvpin.policy").toURI().toURL().toString());
        Policy.getPolicy().refresh();
        System.setSecurityManager(new JSSecurityManager());
        // Replace the old security manager.
        PVPINLoggerFactory.getCoreLogger().info("安全管理系统加载完毕");

        Class.forName(VersionChecker.class.getName());
        // VersionChecker is used in many NMS related classes.
        // So load it before any NMSUtils subclass is loaded.
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("com.pvpin.pvpincore")
                .scan()) {
            scanResult.getClassesWithAnnotation(PVPINLoadOnEnable.class.getName())
                    .forEach(action -> {
                        try {
                            action.loadClass();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
        }
        PVPINLoggerFactory.getCoreLogger().info("各启动时运行模块加载完毕");
    }
}
