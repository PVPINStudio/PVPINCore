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
package com.pvpin.pvpincore.impl.scheduler;

import com.pvpin.pvpincore.modules.js.AbstractJSPlugin;
import com.pvpin.pvpincore.modules.js.LocalFileJSPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.graalvm.polyglot.Value;

/**
 * This class represents a task.<p>
 * JavaScript plugins only.
 *
 * @author William_Shi
 */
public class JSScheduledTask extends BukkitRunnable {
    protected AbstractJSPlugin plugin;
    protected Value callback;

    protected JSScheduledTask(AbstractJSPlugin plugin, Value callback) {
        this.plugin = plugin;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (callback.canExecute()) {
            callback.executeVoid();
        }
    }
}
