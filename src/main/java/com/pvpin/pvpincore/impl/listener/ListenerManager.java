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

import com.pvpin.pvpincore.api.PVPINListener;
import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.api.PVPINLogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.PVPINPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.Value;

/**
 * This class is used to register all listeners.<p>
 * Listeners in com.pvpin.pvpincore.listener.listenerimpl listen all the events in 1.16.5.<p>
 * If the server version is lower, there will be fewer events.<p>
 * So the server jar needs to be scanned to make certain all available events are listened.<p>
 * while those events that aren't available are NOT listened.<p>
 * Then the available events' listeners are registered.
 *
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class ListenerManager {

    private static final Map<Class<?>, List> LOWEST = new HashMap<>(64);
    // LOWEST Priority Listeners
    private static final Map<Class<?>, List> LOW = new HashMap<>(32);
    // LOW Priority Listeners
    private static final Map<Class<?>, List> NORMAL = new HashMap<>(256);
    // NORMAL Priority Listeners
    private static final Map<Class<?>, List> HIGH = new HashMap<>(32);
    // HIGH Priority Listeners
    private static final Map<Class<?>, List> HIGHEST = new HashMap<>(32);
    // HIGHEST Priority Listeners
    private static final Map<Class<?>, List> MONITOR = new HashMap<>(64);
    // MONITOR Priority Listeners

    private static final ListenerImpl LISTENER = new ListenerImpl();

    /**
     * This method is used to register listeners.
     *
     * @param classList All classes with the PVPINListener annotation
     * @see PVPINPluginManager#registerAll()
     */
    public static void registerListener(List<Class<?>> classList) {

        classList.stream().filter(
                (clazz) -> (clazz.isAnnotationPresent(PVPINListener.class) && Listener.class.isAssignableFrom(clazz))
        ).forEachOrdered((clazz) -> {
            try {
                Bukkit.getPluginManager().registerEvents((Listener) clazz.newInstance(), JavaPlugin.getProvidingPlugin(clazz));
            } catch (InstantiationException | IllegalAccessException ex) {
                PVPINLogManager.log(ex);
            }

        });

    }

    /**
     * Register an event listener for a JavaScript plugin.
     *
     * @param eventClass      name of the event, like "org.bukkit.server.ServerCommandEvent"
     * @param priority        priority of the event
     * @param ignoreCancelled true if ignore cancelled events
     * @param callback        function to be executed when an event is called
     */
    public static void registerListener(String eventClass, EventPriority priority, boolean ignoreCancelled, Value callback) {
        Map<Class<?>, List> map = null;
        switch (priority) {
            case LOWEST: {
                map = LOWEST;
                break;
            }
            case LOW: {
                map = LOW;
                break;
            }
            case NORMAL: {
                map = NORMAL;
                break;
            }
            case HIGH: {
                map = HIGH;
                break;
            }
            case HIGHEST: {
                map = HIGHEST;
                break;
            }
            case MONITOR: {
                map = MONITOR;
                break;
            }
            default: {
                // The program will never get here.
            }
        }
        try {
            Class cl = Class.forName(eventClass);
            if (map.containsKey(cl)) {
                List list = map.get(cl);
                list.add(new JSListener(cl, priority, ignoreCancelled, callback));
            } else {
                List list = new ArrayList();
                list.add(new JSListener(cl, priority, ignoreCancelled, callback));
                map.put(cl, list);

                switch (priority) {
                    case LOWEST: {
                        Bukkit.getPluginManager().registerEvent(
                                cl,
                                LISTENER,
                                EventPriority.LOWEST,
                                LISTENER.new LOWEST(),
                                PVPINCore.getCoreInstance(),
                                ignoreCancelled
                        );
                        break;
                    }
                    case LOW: {
                        Bukkit.getPluginManager().registerEvent(
                                cl,
                                LISTENER,
                                EventPriority.LOW,
                                LISTENER.new LOW(),
                                PVPINCore.getCoreInstance(),
                                ignoreCancelled
                        );
                        break;
                    }
                    case NORMAL: {
                        Bukkit.getPluginManager().registerEvent(
                                cl,
                                LISTENER,
                                EventPriority.NORMAL,
                                LISTENER.new NORMAL(),
                                PVPINCore.getCoreInstance(),
                                ignoreCancelled
                        );
                        break;
                    }
                    case HIGH: {
                        Bukkit.getPluginManager().registerEvent(
                                cl,
                                LISTENER,
                                EventPriority.HIGH,
                                LISTENER.new HIGH(),
                                PVPINCore.getCoreInstance(),
                                ignoreCancelled
                        );
                        break;
                    }
                    case HIGHEST: {
                        Bukkit.getPluginManager().registerEvent(
                                cl,
                                LISTENER,
                                EventPriority.HIGHEST,
                                LISTENER.new HIGHEST(),
                                PVPINCore.getCoreInstance(),
                                ignoreCancelled
                        );
                        break;
                    }
                    case MONITOR: {
                        Bukkit.getPluginManager().registerEvent(
                                cl,
                                LISTENER,
                                EventPriority.MONITOR,
                                LISTENER.new MONITOR(),
                                PVPINCore.getCoreInstance(),
                                ignoreCancelled
                        );
                        break;
                    }
                    default: {
                        // The program will never get here.
                    }
                }
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is used to unregister all listeners of a JavaScript plugin.
     *
     * @param pluginName name of the plugin
     */
    public static void unregisterListener(String pluginName) {
        List<Map<Class<?>, List>> maps = List.of(LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR);
        maps.forEach(map -> {
            map.forEach((clazz, list) -> {
                List temp = new ArrayList();
                // Stores all the elements to be deleted.
                list.forEach(listener -> {
                    JSListener lis = (JSListener) listener;
                    lis.plugin.isValid();
                    if (lis.callback.getContext().getPolyglotBindings().getMember("name").asString().equals(pluginName)) {
                        temp.add(lis);
                    }
                });
                if (!temp.isEmpty()) {
                    temp.forEach(listener -> {
                        list.remove(listener);
                    });
                }
            });
        });
    }

    /**
     * Deprecated because no priority is specified.
     *
     * @param event event
     */
    @Deprecated
    public static void call(Event event) {
        call(event, EventPriority.NORMAL);
    }

    /**
     * @param event    event
     * @param priority priority of the event
     */
    public static void call(Event event, EventPriority priority) {
        Map<Class<?>, List> map = null;
        switch (priority) {
            case LOWEST: {
                map = LOWEST;
                break;
            }
            case LOW: {
                map = LOW;
                break;
            }
            case NORMAL: {
                map = NORMAL;
                break;
            }
            case HIGH: {
                map = HIGH;
                break;
            }
            case HIGHEST: {
                map = HIGHEST;
                break;
            }
            case MONITOR: {
                map = MONITOR;
                break;
            }
            default: {
                // The program will never get here.
            }
        }
        if (map.containsKey(event.getClass())) {
            List<JSListener> list = map.get(event.getClass());
            list.forEach((listener) -> {
                listener.call(event);
            });
        }
    }
}
