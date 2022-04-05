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

import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.js.parser.ParserManager;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.impl.scheduler.ScheduledTaskManager;
import com.pvpin.pvpincore.modules.PVPINCore;
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
            List<String> info = ParserManager.parseInfo(src);
            this.name = info.get(0);
            this.version = info.get(1);
            this.author = info.get(2);
            super.value = PVPINCore.getScriptManagerInstance().getContext()
                    .eval(Source.newBuilder("js", this.src, super.uuid.toString())
                            .mimeType(JavaScriptLanguage.MODULE_MIME_TYPE).buildLiteral());

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
            if (!value.hasMember("main")) {
                throw new RuntimeException(I18N.translateByDefault("js.parser.main"));
            }
            if (!value.getMember("main").canExecute()) {
                throw new RuntimeException(I18N.translateByDefault("js.parser.main"));
            }
            Value func = value.getMember("main");
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
        // PersistenceManager.getCurrentHolder().saveToFile();
    }

    public boolean isValid() {
        return name != null && version != null && author != null;
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
}
