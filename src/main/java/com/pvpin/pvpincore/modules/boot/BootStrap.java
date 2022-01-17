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
import com.pvpin.pvpincore.modules.js.security.JSSecurityManager;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Policy;
import java.util.Set;

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

        PVPINCore.getCoreInstance().saveResource("com/pvpin/pvpincore/modules/js/security/pvpin.policy", true);
        File policyFileOrigin = new File(PVPINCore.getCoreInstance().getDataFolder(), "com/pvpin/pvpincore/modules/js/security/pvpin.policy");
        File policyFileNew = new File(PVPINCore.getCoreInstance().getDataFolder(), "security/pvpin.policy");
        // Save the policy file to a local folder, and move it to an appropriate location.
        // Delete the com/ directory after use.
        boolean bool = policyFileNew.exists() ? policyFileNew.delete() : policyFileNew.getParentFile().mkdirs();
        policyFileOrigin.renameTo(policyFileNew);
        Files.walkFileTree(new File(PVPINCore.getCoreInstance().getDataFolder(), "com").toPath(), Set.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        System.setProperty("java.security.policy", policyFileNew.toURI().toURL().toString());
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
