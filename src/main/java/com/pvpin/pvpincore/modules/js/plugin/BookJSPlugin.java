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

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.parser.LoopParser;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.BookMeta;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.util.UUID;

/**
 * @author William_Shi
 */
public class BookJSPlugin extends StringJSPlugin {
    private BookMeta meta;
    private String src;

    public BookJSPlugin(UUID player, BookMeta meta, String src) {
        super();
        this.player = player;
        this.meta = meta;
        this.src = src;
        String name = Bukkit.getOfflinePlayer(player).getName() + "_" + (meta.hasTitle() ? meta.getTitle() : UUID.randomUUID().toString());
        if (PVPINCore.getScriptManagerInstance().getPluginByName(name) == null) {
            name = Bukkit.getOfflinePlayer(player).getName() + "_" + UUID.randomUUID().toString();
        }
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

    @Deprecated
    public BookJSPlugin(UUID player, BookMeta meta) {
        super();
        this.player = player;
        this.meta = meta;
        this.src = new LoopParser("function main(){\n" + getContents(meta) + "}\n").parse();
        String name = Bukkit.getOfflinePlayer(player).getName() + "_" + (meta.hasTitle() ? meta.getTitle() : UUID.randomUUID().toString());
        String version = "0.0.1";
        String author = Bukkit.getOfflinePlayer(player).getName();
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        context.getBindings("js").putMember("name", name);
        context.getBindings("js").putMember("version", version);
        context.getBindings("js").putMember("author", author);
        context.getPolyglotBindings().putMember("name", getName());
        context.getPolyglotBindings().putMember("version", getVersion());
        context.getPolyglotBindings().putMember("author", getAuthor());

        try {
            context.eval(Source.newBuilder("js", PVPINCore.class.getResource("/api.js")).build());
            context.eval(Source.newBuilder("js", this.src, name).buildLiteral());
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
        this.logger = PVPINLogManager.getLogger(this.getName());
        Thread.currentThread().setContextClassLoader(appCl);
    }

    public static String getContents(BookMeta meta) {
        StringBuilder sb = new StringBuilder();
        meta.getPages().forEach(sb::append);
        String str = sb.toString();
        if (str.isBlank() || str.isEmpty()) {
            throw new RuntimeException("Invalid JavaScript Code.");
        }
        return str;
    }
}
