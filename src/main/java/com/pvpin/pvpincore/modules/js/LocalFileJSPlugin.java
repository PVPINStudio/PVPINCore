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
public class LocalFileJSPlugin extends AbstractJSPlugin {
    private File file;

    public LocalFileJSPlugin(File file) {
        super();
        try {
            this.file = file;
            context.eval(Source.newBuilder(Source.findLanguage(file), file).build());
            this.logger = PVPINLogManager.getLogger(this.getName());
            context.getPolyglotBindings().putMember("name", getName());
            context.getPolyglotBindings().putMember("version", getVersion());
            context.getPolyglotBindings().putMember("author", getAuthor());
            this.logger.info(getName() + " has been loaded.");
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    @Override
    public void enable() {
        try {
            if(!context.getBindings("js").hasMember("main")){
                throw new RuntimeException();
            }
            if(!context.getBindings("js").getMember("main").canExecute()){
                throw new RuntimeException();
            }
            Value func = context.getBindings("js").getMember("main");
            func.executeVoid();
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    @Override
    public void disable() {
        ListenerManager.unregisterListener(this.getName());
        CommandManager.unregisterJavaScriptCmds(this.getName());
        ScheduledTaskManager.cancelTasks(this.getName());
        PersistenceManager.getCurrentHolder().saveToFile();
        context.close(true);
    }

    public boolean isValid() {
        if (context.getPolyglotBindings().hasMember("close")) {
            return false;
        }
        if (!context.getPolyglotBindings().getMember("name").asString().equals(context.getBindings("js").getMember("name").asString())) {
            JSPluginAccessController.denyAccess(context);
            return false;
        }
        if (!context.getPolyglotBindings().getMember("version").asString().equals(context.getBindings("js").getMember("version").asString())) {
            JSPluginAccessController.denyAccess(context);
            return false;
        }
        if (!context.getPolyglotBindings().getMember("author").asString().equals(context.getBindings("js").getMember("author").asString())) {
            JSPluginAccessController.denyAccess(context);
            return false;
        }
        return true;
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
        if (another instanceof LocalFileJSPlugin) {
            LocalFileJSPlugin plugin = (LocalFileJSPlugin) another;
            return (this.getName().equals(plugin.getName()))
                    && (this.getVersion().equals(plugin.getVersion()))
                    && (this.getAuthor().equals(plugin.getAuthor()))
                    && (this.getSourceFile().equals(plugin.getSourceFile()));
        }
        return false;
    }
}
