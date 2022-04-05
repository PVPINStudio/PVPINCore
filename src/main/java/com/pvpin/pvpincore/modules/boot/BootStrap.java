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
import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
        PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("init.log"));

        LibraryLoader.loadLibraries();
        // Download libraries.
        PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("init.dependency"));

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
                            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                            theUnsafe.setAccessible(true);
                            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
                            Field module = Class.class.getDeclaredField("module");
                            long offset = unsafe.objectFieldOffset(module);
                            unsafe.putObject(action.loadClass(), offset, Object.class.getModule());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
        }
    }
}
