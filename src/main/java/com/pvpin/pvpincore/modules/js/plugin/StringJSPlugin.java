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

import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.impl.command.CommandManager;
import com.pvpin.pvpincore.impl.listener.ListenerManager;
import com.pvpin.pvpincore.impl.persistence.PersistenceManager;
import com.pvpin.pvpincore.impl.scheduler.ScheduledTaskManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.security.JSPluginAccessController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author William_Shi
 */
public class StringJSPlugin extends AbstractJSPlugin {
    protected UUID player;
    protected String src;

    public StringJSPlugin(UUID player, String src) {
        super();
        this.player = player;
        this.src = src;
        String name = Bukkit.getOfflinePlayer(player).getName() + "_" + UUID.randomUUID();
        String version = "0.0.1";
        String author = Bukkit.getOfflinePlayer(player).getName();
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        try {
            context.eval(Source.newBuilder("js", PVPINCore.class.getResource("/api.js")).build());
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
        context.getBindings("js").putMember("name", name);
        context.getBindings("js").putMember("version", version);
        context.getBindings("js").putMember("author", author);
        context.getPolyglotBindings().putMember("name", getName());
        context.getPolyglotBindings().putMember("version", getVersion());
        context.getPolyglotBindings().putMember("author", getAuthor());

        context.eval(Source.newBuilder("js", this.src, name).buildLiteral());

        this.logger = PVPINLogManager.getLogger(this.getName());
        Thread.currentThread().setContextClassLoader(appCl);
    }

    protected StringJSPlugin() {
        super();
    }

    @Override
    public void enable() {
        if (context.getBindings("js").hasMember("main")) {
            if (context.getBindings("js").getMember("main").canExecute()) {
                Value func = context.getBindings("js").getMember("main");
                func.executeVoid();
            }
        }
        Player me = Bukkit.getOfflinePlayer(player).isOnline() ? Bukkit.getPlayer(player) : null;
        context.getBindings("js").putMember("me", me);
    }

    @Override
    public void disable() {
        ListenerManager.unregisterListener(this.getName());
        CommandManager.unregisterJavaScriptCmds(this.getName());
        ScheduledTaskManager.cancelTasks(this.getName());
        PersistenceManager.getCurrentHolder().saveToFile();
        context.close(true);
    }

    @Override
    public boolean isValid() {
        if (context.getPolyglotBindings().hasMember("close")) {
            return false;
        }
        if (!Bukkit.getOfflinePlayer(player).isOnline()) {
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

    public UUID getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Temp JavaScript Plugin: ");
        sb.append(getName());
        sb.append(" Version: ");
        sb.append(getVersion());
        sb.append(" Author(s):");
        sb.append(getAuthor());
        sb.append(" Executor:");
        sb.append(Bukkit.getOfflinePlayer(player).getName());
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getName().hashCode();
        result = 31 * result + this.getVersion().hashCode();
        result = 31 * result + this.getAuthor().hashCode();
        result = 31 * result + this.src.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }
        if (another instanceof StringJSPlugin) {
            StringJSPlugin plugin = (StringJSPlugin) another;
            return (this.getName().equals(plugin.getName()))
                    && (this.getVersion().equals(plugin.getVersion()))
                    && (this.getAuthor().equals(plugin.getAuthor()))
                    && (this.src.equals(plugin.src));
        }
        return false;
    }
}
