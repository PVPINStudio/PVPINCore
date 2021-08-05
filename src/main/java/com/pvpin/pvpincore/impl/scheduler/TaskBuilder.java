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


import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.JSPlugin;
import org.graalvm.polyglot.Value;

/**
 * @author William_Shi
 */
public class TaskBuilder {

    protected JSPlugin plugin;
    protected Value callback;
    protected Long interval = 0L;
    protected Long delay = 0L;

    protected TaskBuilder(JSPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * This method is used to set the delay of the task.<p>
     * The delay is measured in ticks (1 sec = 20 ticks).<p>
     * The delay represents AFTER how much ticks will the task be executed after calling buildAndRun.
     *
     * @param delay how many ticks to be delayed
     * @return builder itself
     */
    public TaskBuilder delay(Long delay) {
        this.delay = delay;
        return this;
    }

    /**
     * This method is used to set the interval of the task.<p>
     * The interval is measured in ticks (1 sec = 20 ticks).<p>
     * The interval represents between how much ticks will the task be executed.<p>
     * Setting the interval to a value larger than 0L will make the task be executed at set intervals.<p>
     * To stop the task, use BukkitRunnable#cancel().
     *
     * @param interval how many ticks between tasks
     * @return builder itself
     */
    public TaskBuilder interval(Long interval) {
        this.interval = interval;
        return this;
    }

    /**
     * This method is used to set the callback of the task.<p>
     * This function will be executed each time the task is executed.<p>
     * Same as the run() in BukkitRunnables.
     *
     * @param callback function (){ }
     * @return builder itself
     */
    public TaskBuilder callback(Value callback) {
        this.callback = callback;
        return this;
    }

    /**
     * This method is used to start executing the task.<p>
     * A task will be returned.<p>
     * You may use cancel() to stop the task.
     *
     * @return the built task
     */
    public JSScheduledTask buildAndRun() {
        if (callback == null) {
            throw new RuntimeException();
        }
        if (!callback.canExecute()) {
            throw new RuntimeException();
        }
        JSScheduledTask task = new JSScheduledTask(plugin, callback);
        ScheduledTaskManager.TASKS.add(task);
        if (interval != 0L) {
            task.runTaskTimer(PVPINCore.getCoreInstance(), delay, interval);
        } else {
            if (delay != 0L) {
                task.runTaskLater(PVPINCore.getCoreInstance(), delay);
            } else {
                task.runTask(PVPINCore.getCoreInstance());
            }
        }
        return task;
    }
}