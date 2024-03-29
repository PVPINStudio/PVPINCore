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

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.plugin.AbstractJSPlugin;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.graalvm.polyglot.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author William_Shi
 */
public class JSListener {
    protected Class<?> cl;
    protected EventPriority priority;
    protected boolean ignoreCancelled;
    protected Value callback;
    protected AbstractJSPlugin plugin;

    protected JSListener(Class<?> cl, EventPriority priority, boolean ignoreCancelled, Value callback) {
        this.callback = callback;
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.callback = callback;
        this.plugin = PVPINCore.getScriptManagerInstance().getPluginByUUID(
                UUID.fromString(callback.getSourceLocation().getSource().getName())
        );
        plugin.isValid();
    }

    protected void call(Event event) {
        if (event instanceof Cancellable) {
            if (ignoreCancelled && ((Cancellable) event).isCancelled()) {
                // Cancelled, do nothing.
            } else {
                callback.execute(event);
            }
        } else {
            callback.execute(event);
        }
    }

    public void unregister(){
        List<Map<Class<?>, List>> maps = List.of(ListenerManager.LOWEST, ListenerManager.LOW, ListenerManager.NORMAL, ListenerManager.HIGH, ListenerManager.HIGHEST, ListenerManager.MONITOR);
        maps.forEach(map -> {
            map.forEach((clazz, list) -> {
                if(list.contains(this)){
                    list.remove(this);
                }
            });
        });
    }

}
