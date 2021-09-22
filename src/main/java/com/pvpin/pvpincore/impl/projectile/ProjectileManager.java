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

import com.pvpin.pvpincore.modules.PVPINCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * @author William_Shi
 */
public class ProjectileManager {
    /**
     * This method is used to launch a projectile and process its hit event
     * within the same method (using callback) where it is launched.<p>
     * It makes editing custom projectiles easier through
     * managing ProjectileHitEvent listeners for you.
     *
     * @param source   projectile source
     * @param type     projectile type, like Arrow.class
     * @param velocity initial velocity of the projectile
     * @param onHit    a callback to process the hit event, called when this projectile hits something
     * @return the generated projectile
     */
    public static Projectile launch(
            ProjectileSource source,
            Class<? extends Projectile> type,
            Vector velocity,
            Consumer<ProjectileHitEvent> onHit
    ) {
        Projectile projectile = source.launchProjectile(type, velocity);
        ProjectileListener listener = new ProjectileListener(projectile.getUniqueId(), onHit);
        Bukkit.getPluginManager().registerEvents(listener, PVPINCore.getCoreInstance());
        ProjectileValidator.listeners.add(listener);
        return projectile;
    }

}
