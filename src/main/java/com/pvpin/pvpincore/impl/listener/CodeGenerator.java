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
package com.pvpin.pvpincore.impl.listener;

import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class is used to generate .java files for listenimpl package.
 * No longer needs it.
 *
 * @author William_Shi
 */
@Deprecated
public class CodeGenerator {
    // Template.txt
    private static final String TEMPLATE =
            "package com.pvpin.pvpincore.impl.listener.listenerimpl;\n" +
                    "\n" +
                    "import org.bukkit.event.Listener;\n" +
                    "import org.bukkit.event.EventHandler;\n" +
                    "import org.bukkit.event.EventPriority;\n" +
                    "\n" +
                    "/**\n" +
                    "*\n" +
                    "* @author William_Shi\n" +
                    "* 监听器模板类\n" +
                    "* 为每一个1.16.5的事件单独创建一个类并注册监听\n" +
                    "*/\n" +
                    "\n" +
                    "public class %listener_name% implements Listener{\n" +
                    "\n" +
                    "    @EventHandler(priority = EventPriority.LOWEST)\n" +
                    "    public void onEventCalled_LOWEST(%event_fullname% event){\n" +
                    "        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.LOWEST);\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler(priority = EventPriority.LOW)\n" +
                    "    public void onEventCalled_LOW(%event_fullname% event){\n" +
                    "        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.LOW);\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler(priority = EventPriority.NORMAL)\n" +
                    "    public void onEventCalled_NORMAL(%event_fullname% event){\n" +
                    "        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.NORMAL);\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler(priority = EventPriority.HIGH)\n" +
                    "    public void onEventCalled_HIGH(%event_fullname% event){\n" +
                    "        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.HIGH);\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler(priority = EventPriority.HIGHEST)\n" +
                    "    public void onEventCalled_HIGHEST(%event_fullname% event){\n" +
                    "        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.HIGHEST);\n" +
                    "    }\n" +
                    "\n" +
                    "    @EventHandler(priority = EventPriority.MONITOR)\n" +
                    "    public void onEventCalled_MONITOR(%event_fullname% event){\n" +
                    "        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.MONITOR);\n" +
                    "    }\n" +
                    "\n" +
                    "}\n";

    public static void main(String args[]) throws Exception {
        File folder = new File(
                "C:/Users/williamshi/Documents/Code/PVPINCore/src/main/java/com/pvpin/pvpincore/impl/listener/listenerimpl"
        );
        // The listener/listenerimpl source code folder
        String spigotFileName = "C:/Users/williamshi/Documents/Code/ToolBox/1.16/spigot-1.16.5.jar";
        // The server core jar

        ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptJars(
                        spigotFileName.split("/")[spigotFileName.split("/").length - 1]
                )
                .overrideClassLoaders(new URLClassLoader(new URL[]{
                        (new File(spigotFileName)).toURI().toURL()
                }))
                .acceptPackages("org.bukkit.event")
                .scan();
        scanResult.getAllClasses()
                .filter(action -> action.hasMethod("getHandlerList"))
                .forEach(action -> {
                    try {
                        System.out.println("扫描类: " + action.getName());
                        String java = new String(TEMPLATE)
                                .replace("%event_fullname%", action.getPackageName() + "." + action.getSimpleName())
                                .replace("%listener_name%", action.getSimpleName() + "Listener");
                        File file = new File(folder, action.getSimpleName() + "Listener" + ".java");
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                        BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write(java);
                        out.flush();
                        out.close();
                    } catch (Exception ex) {
                        PVPINLogManager.log(ex);
                    }

                });
    }

    /*
      Deprecated.
      Use ClassGraph instead.

        for (Enumeration<JarEntry> e = spigot.entries(); e.hasMoreElements(); ) {
            JarEntry entry = e.nextElement();
            if (entry.getName().contains("org/bukkit/event")
                    && entry.getName().contains("class")
                    && entry.getName().contains("Event")
                    && (!entry.getName().contains("$"))
                    && (!entry.getName().contains("package-info"))) {
                String stripped = entry.getName().substring(0, entry.getName().length() - 6);
                String full = stripped.replace("/", ".");
                String simple = stripped.split("/")[stripped.split("/").length - 1];
                if (simple.equals("Event") || simple.equals("EventException") || simple.equals("EventHandler") || simple.equals("EventPriority")) {
                    continue;
                    //过滤掉另外的不是事件的类
                }
                String java = builder.toString().replace("%event_fullname%", full).replace("%listener_name%", simple + "Listener");
                File file = new File(folder, simple + "Listener" + ".java");
                if (file.exists()) {
                    continue;
                }
                file.createNewFile();
                System.out.println(file.getName());
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(java);
                out.flush();
                out.close();
            }
        }
     */
}
