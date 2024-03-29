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
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.config.ConfigManager;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;

import java.util.*;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.PVPINPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.Value;

/**
 * This class is used to register listeners for JavaScript plugins.<p>
 * The maps LOWEST, LOW, NORMAL, HIGH, HIGHEST, and MONITOR are used to store the JSListeners and the event classes they listen.<p>
 * The key is the event class to be listened.<p>
 * The value is a list containing the JSListeners that listen the certain event(the key).<p>
 * When registering a listener, first check whether the event to be listened exists in the map.<p>
 * If exists, add the JSListener to the list of that event class.<p>
 * If not, then register a new ListenerImpl to listen that event.<p>
 * A ListenerImpl is used to listen a certain event and when the event happens, it calls ListenerManager#call.<p>
 * The call method walks all the JSListeners of a certain event and calls them.
 *
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class ListenerManager {

    protected static final Map<Class<?>, List> LOWEST = new HashMap<>(ConfigManager.PLUGIN_LISTENER_CAPACITY_LOWEST);
    // LOWEST Priority Listeners
    protected static final Map<Class<?>, List> LOW = new HashMap<>(ConfigManager.PLUGIN_LISTENER_CAPACITY_LOW);
    // LOW Priority Listeners
    protected static final Map<Class<?>, List> NORMAL = new HashMap<>(ConfigManager.PLUGIN_LISTENER_CAPACITY_NORMAL);
    // NORMAL Priority Listeners
    protected static final Map<Class<?>, List> HIGH = new HashMap<>(ConfigManager.PLUGIN_LISTENER_CAPACITY_HIGH);
    // HIGH Priority Listeners
    protected static final Map<Class<?>, List> HIGHEST = new HashMap<>(ConfigManager.PLUGIN_LISTENER_CAPACITY_HIGHEST);
    // HIGHEST Priority Listeners
    protected static final Map<Class<?>, List> MONITOR = new HashMap<>(ConfigManager.PLUGIN_LISTENER_CAPACITY_MONITOR);
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
    public static JSListener registerListener(String eventClass, EventPriority priority, boolean ignoreCancelled, Value callback) {
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
            JSListener newListener = new JSListener(cl, priority, ignoreCancelled, callback);
            if (map.containsKey(cl)) {
                List list = map.get(cl);
                list.add(newListener);
            } else {
                List list = new ArrayList();
                list.add(newListener);
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
            return newListener;
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
        return null;
    }

    /**
     * This method is used to unregister all listeners of a JavaScript plugin.
     *
     * @param pluginName name of the plugin
     */
    public static void unregisterListener(String pluginName) {
        List<Map<Class<?>, List<JSListener>>> maps = (List<Map<Class<?>, List<JSListener>>>) (Object) List.of(LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR);
        maps.forEach(map -> {
            map.forEach((clazz, list) -> {
                List<JSListener> temp = new ArrayList<>(16);
                // Stores all the elements to be deleted.
                list.forEach(listener -> {
                    listener.plugin.isValid();
                    if (UUID.fromString(
                            listener.callback.getSourceLocation().getSource().getName()
                    ).equals(PVPINCore.getScriptManagerInstance().getPluginByName(pluginName).getId())) {
                        temp.add(listener);
                    }
                });
                if (!temp.isEmpty()) {
                    temp.forEach(list::remove);
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
