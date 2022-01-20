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
package com.pvpin.pvpincore.modules.js.plugin;

import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.impl.persistence.PersistenceManager;
import com.pvpin.pvpincore.impl.scheduler.ScheduledTaskManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.parser.InfoParser;
import com.pvpin.pvpincore.modules.js.parser.LoopParser;
import com.pvpin.pvpincore.modules.js.security.JSPluginAccessController;
import org.graalvm.polyglot.*;

import java.io.*;
import java.util.List;

/**
 * @author William_Shi
 */
public class LocalFileJSPlugin extends AbstractJSPlugin {
    protected File file;
    protected String src;

    public LocalFileJSPlugin(File file, String src) {
        super();
        try {
            ClassLoader appCl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
            // Replace the AppClassLoader
            this.file = file;
            this.src = src;
            InfoParser parser = new InfoParser(src);
            try {
                context.eval(Source.newBuilder("js", PVPINCore.class.getResource("/api.js")).build());
            } catch (IOException ex) {
                PVPINLogManager.log(ex);
            }
            context.getBindings("js").putMember("name", parser.parseName());
            context.getBindings("js").putMember("version", parser.parseVersion());
            context.getBindings("js").putMember("author", parser.parseAuthor());
            context.getPolyglotBindings().putMember("name", parser.parseName());
            context.getPolyglotBindings().putMember("version", parser.parseVersion());
            context.getPolyglotBindings().putMember("author", parser.parseAuthor());

            context.eval(Source.newBuilder("js", this.src, "🐎").buildLiteral());

            this.logger = PVPINLogManager.getLogger(this.getName());
            this.logger.info(getName() + " has been loaded.");
            Thread.currentThread().setContextClassLoader(appCl);
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    @Deprecated
    public LocalFileJSPlugin(File file) {
        super();
        try {
            ClassLoader appCl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
            // Replace the AppClassLoader
            this.file = file;
            this.src = new LoopParser(
                    Source.newBuilder(Source.findLanguage(file), file).build().getCharacters().toString()
            ).parse();
            // System.out.println(this.src);
            InfoParser parser = new InfoParser(src);
            context.getBindings("js").putMember("name", parser.parseName());
            context.getBindings("js").putMember("version", parser.parseVersion());
            context.getBindings("js").putMember("author", parser.parseAuthor());
            context.getPolyglotBindings().putMember("name", parser.parseName());
            context.getPolyglotBindings().putMember("version", parser.parseVersion());
            context.getPolyglotBindings().putMember("author", parser.parseAuthor());
            try {
                context.eval(Source.newBuilder("js", PVPINCore.class.getResource("/api.js")).build());
                context.eval(Source.newBuilder("js", this.src, "🐎").buildLiteral());
            } catch (IOException ex) {
                PVPINLogManager.log(ex);
            }
            this.logger = PVPINLogManager.getLogger(this.getName());
            this.logger.info(getName() + " has been loaded.");
            Thread.currentThread().setContextClassLoader(appCl);
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    @Override
    public void enable() {
        try {
            if (!context.getBindings("js").hasMember("main")) {
                throw new RuntimeException(I18N.translateByDefault("js.parser.main"));
            }
            if (!context.getBindings("js").getMember("main").canExecute()) {
                throw new RuntimeException(I18N.translateByDefault("js.parser.main"));
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
        List<String> list = List.of("name", "version", "author");
        for (String action : list) {
            if (!context.getPolyglotBindings().hasMember(action)) {
                JSPluginAccessController.denyAccess(context);
                return false;
            }
            if (!context.getPolyglotBindings().getMember(action).asString()
                    .equals(context.getBindings("js").getMember(action).asString())) {
                JSPluginAccessController.denyAccess(context);
                return false;
            }
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
