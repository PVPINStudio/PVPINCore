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
package com.pvpin.pvpincore.impl.persistence;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.ClassChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to get the data holders.<p>
 * A plugin shouldn't access other plugins' data.<p>
 * So some security checks are needed.
 *
 * @author William_Shi
 */
public class PersistenceManager {

    protected static List<AbstractHolder> loadedHolders = new ArrayList<>(32);

    public static AbstractHolder getCurrentHolder() {
        AbstractHolder holder = null;
        try {
            if (ClassChecker.isLoadedByJavaScriptEngine()) {
                Context cxt = Context.getCurrent();
                for (AbstractHolder loaded : loadedHolders) {
                    if (cxt.getPolyglotBindings().getMember("name").asString().equals(loaded.namespace)
                            && loaded instanceof JSPersistenceHolder) {
                        PVPINCore.getScriptManagerInstance().getPluginByName(
                                cxt.getPolyglotBindings().getMember("name").asString()
                        ).isValid();
                        return loaded;
                    }
                }
                // No loaded holders.
                holder = new JSPersistenceHolder(
                        cxt.getPolyglotBindings().getMember("name").asString()
                );
                PVPINCore.getScriptManagerInstance().getPluginByName(
                        cxt.getPolyglotBindings().getMember("name").asString()
                ).isValid();
                loadedHolders.add(holder);
            } else {
                StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                if (!elements[2].getClassName().startsWith("com.pvpin")) {
                    throw new IllegalAccessException();
                }
                // 0 java.base/java.lang.Thread.getStackTrace,
                // 1 com.pvpin.pvpincore.impl.persistence.PersistenceManager,
                // 2 com.pvpin.pvpincore.api.PVPINPersistence,
                // 3 method in other plugins
                JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Class.forName(elements[3].getClassName()));
                for (AbstractHolder loaded : loadedHolders) {
                    if (plugin.getName().equals(loaded.namespace)
                            && loaded instanceof JavaPersistenceHolder) {
                        return loaded;
                    }
                }
                // No loaded holders.
                holder = new JavaPersistenceHolder(plugin.getName());
                loadedHolders.add(holder);
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
        return holder;
    }

    public static void saveAll() {
        loadedHolders.forEach(AbstractHolder::saveToFile);
    }

}
