/*
 * The MIT License
 * Copyright © ${year} PVPINStudio
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
package com.pvpin.pvpincore.modules.js;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.impl.persistence.PersistenceManager;
import com.pvpin.pvpincore.impl.scheduler.ScheduledTaskManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import org.graalvm.polyglot.*;
import org.slf4j.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author William_Shi
 */
public class JSPlugin {
    private Context context;
    private File file;
    private Logger logger;

    public JSPlugin(File file) {
        try {
            ClassLoader appCl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
            // Replace the AppClassLoader
            this.file = file;
            this.context = Context.newBuilder("js")
                    .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL)
                            .targetTypeMapping(Value.class, Object.class, Value::hasArrayElements,
                                    v -> new LinkedList<>(v.as(List.class)))
                            .build())
                    // https://github.com/oracle/graaljs/issues/214
                    .allowAllAccess(true).build();
            context.eval(Source.newBuilder("js", PVPINCore.class.getResource("/api.js")).build());
            context.eval(Source.newBuilder(Source.findLanguage(file), file).build());
            this.logger = PVPINLogManager.getLogger(this.getName());
            Thread.currentThread().setContextClassLoader(appCl);
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    public void enable() {
        try {
            Value func = context.getBindings("js").getMember("main");
            func.executeVoid();
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    public void disable() {
        ListenerManager.unregisterListener(this.getName());
        CommandManager.unregisterJavaScriptCmds(this.getName());
        ScheduledTaskManager.cancelTasks(this.getName());
        PersistenceManager.getCurrentHolder().saveToFile();
        context.close(false);
    }

    public String getName() {
        return context.getBindings("js").getMember("name").asString();
    }

    public String getVersion() {
        return context.getBindings("js").getMember("version").asString();
    }

    public String getAuthor() {
        return context.getBindings("js").getMember("author").asString();
    }

    public File getSourceFile() {
        return file;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JavaScript Plugin: ");
        sb.append(getName());
        sb.append(" Version: ");
        sb.append(getVersion());
        sb.append(" Author(s):");
        sb.append(getAuthor());
        sb.append(" (");
        sb.append(getSourceFile().getName());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getName().hashCode();
        result = 31 * result + this.getVersion().hashCode();
        result = 31 * result + this.getAuthor().hashCode();
        result = 31 * result + this.getSourceFile().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }
        if (another instanceof JSPlugin) {
            JSPlugin plugin = (JSPlugin) another;
            return (this.getName().equals(plugin.getName()))
                    && (this.getVersion().equals(plugin.getVersion()))
                    && (this.getAuthor().equals(plugin.getAuthor()))
                    && (this.getSourceFile().equals(plugin.getSourceFile()));
        }
        return false;
    }
}
