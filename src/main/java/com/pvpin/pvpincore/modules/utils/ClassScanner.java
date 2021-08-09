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
package com.pvpin.pvpincore.modules.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * @author William_Shi
 */
public class ClassScanner {

    public static ScanResult scanClasses() throws Exception {
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

    /*
     * Deprecated because Class#forName is called.<p>
     * Such action lowers down the program and may cause some classes,
     * which shouldn't have been loaded, to be loaded.

        StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
        URI uri;
        uri = URI.create("jar:file:"
                + Class.forName(trace.getClassName()).getProtectionDomain().getCodeSource().getLocation().getFile()
        );
        // 0 Thread.getStackTrace
        // 1 scanAllClass
        // 2 register
        // 3 Other Plugin

        //首先按照jar:file:/xxx的形式得到一个Jar的URI
        //必须是jar:开始，否则将会报错，要求提供一个/结尾的URI（即必须要目录）

        ArrayList<Class> list = new ArrayList<>(256);
        //将要返回的列表
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
            //通过FileSystem进行遍历
            Path path = fileSystem.getPath("/");
            Stream<Path> walk = Files.walk(path, Integer.MAX_VALUE);
            //深度足够大
            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                Path temp = it.next();
                //Jar内内容遍历
                if (!temp.toString().endsWith(".class")) {
                    //不是类
                    continue;
                }
                StringBuilder className = new StringBuilder("");
                String[] strings = temp.toString().split("/");
                //将形如 /com/pvpin/pvpincore 的元素名分割开来
                for (String s : strings) {
                    if (s.equals("")) {
                        //以"/"开头，因此split后第一个元素是 ""
                        continue;
                    }
                    if (s.endsWith("class")) {
                        className.append(s.substring(0, s.length() - 6));
                        //取到完整类名，去除class
                        continue;
                    }
                    className.append(s);
                    className.append(".");
                    //拼接类名，如com.pvpin
                }
                PVPINLoggerFactory.getCoreLogger().info("Walk class : {}", className.toString());
                list.add(Class.forName(className.toString()));
            }
        }
        return list;
    */

}
