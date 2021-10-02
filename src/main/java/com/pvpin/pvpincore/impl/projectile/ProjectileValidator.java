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
package com.pvpin.pvpincore.impl.projectile;

import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.PVPINCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is used to validate all listeners.<p>
 * If the corresponding entity of a listener does not exist anymore,
 * the listener is then removed.
 *
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class ProjectileValidator {
    protected static final List<ProjectileListener> listeners = new ArrayList<>(16);

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                listeners.stream()
                        .filter(action -> {
                            UUID uid = action.uid;
                            if (Bukkit.getEntity(uid) == null) {
                                return true;
                            }
                            if (!(Bukkit.getEntity(uid) instanceof Projectile)) {
                                return true;
                            }
                            return (!Bukkit.getEntity(uid).isValid()) || (Bukkit.getEntity(uid).isDead());
                        }).collect(Collectors.toUnmodifiableList())
                        .forEach(action -> {
                            listeners.remove(action);
                            HandlerList.unregisterAll(action);
                        });
            }
        }.runTaskTimer(PVPINCore.getCoreInstance(), 0L, 100L);
    }
}
