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
import com.pvpin.pvpincore.modules.js.ClassChecker;
import org.graalvm.polyglot.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create and submit tasks for JavaScriptPlugins.<p>
 * JavaScript plugins only.
 *
 * @author William_Shi
 */
public class ScheduledTaskManager {
    protected static final List<JSScheduledTask> TASKS = new ArrayList<>(64);

    /**
     * This method is used to get a builder for JSScheduledTasks.
     *
     * @return a builder to build tasks
     * @see TaskBuilder
     */
    public static TaskBuilder newTaskBuilder() {
        if (!ClassChecker.isLoadedByJavaScriptEngine()) {
            throw new RuntimeException();
        }
        Context context = Context.getCurrent();
        String pluginName = context.getPolyglotBindings().getMember("name").asString();
        PVPINCore.getScriptManagerInstance().getPluginByName(pluginName).isValid();
        return new TaskBuilder(PVPINCore.getScriptManagerInstance().getPluginByName(pluginName));
    }

    public static void cancelTasks(String pluginName) {
        List<JSScheduledTask> temp = new ArrayList(16);
        TASKS.forEach(action -> {
            if (action.plugin.getName().equals(pluginName)) {
                temp.add(action);
            }
        });
        temp.forEach(action -> {
            action.cancel();
            TASKS.remove(action);
        });
    }
}
