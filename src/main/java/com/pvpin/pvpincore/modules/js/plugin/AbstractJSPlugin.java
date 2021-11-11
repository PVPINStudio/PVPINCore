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

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.security.JSPluginAccessController;
import org.graalvm.polyglot.*;
import org.slf4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author William_Shi
 */
public abstract class AbstractJSPlugin {
    protected Context context;
    protected Logger logger;

    protected AbstractJSPlugin() {
        ClassLoader appCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(PVPINCore.getCoreInstance().getClass().getClassLoader());
        // Replace the AppClassLoader
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL)
                        .targetTypeMapping(Value.class, Object.class, Value::hasArrayElements,
                                v -> new LinkedList<>(v.as(List.class)))
                        .build())
                // https://github.com/oracle/graaljs/issues/214
                .allowCreateProcess(false)
                .allowCreateThread(false)
                .allowIO(false)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .allowNativeAccess(true)
                .allowHostClassLoading(true)
                .allowHostClassLookup(JSPluginAccessController::checkJSLookUpHostClass)
                .out(new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out))))
                .allowExperimentalOptions(true)
                .option("js.scripting", "true")
                .build();
        Thread.currentThread().setContextClassLoader(appCl);
    }

    public abstract void enable();

    public abstract void disable();

    public abstract boolean isValid();

    public String getName() {
        if (!context.getPolyglotBindings().hasMember("name")) {
            return context.getBindings("js").getMember("name").asString();
        }
        return context.getPolyglotBindings().getMember("name").asString();
    }

    public String getVersion() {
        if (!context.getPolyglotBindings().hasMember("version")) {
            return context.getBindings("js").getMember("version").asString();
        }
        return context.getPolyglotBindings().getMember("version").asString();
    }

    public String getAuthor() {
        if (!context.getPolyglotBindings().hasMember("author")) {
            return context.getBindings("js").getMember("author").asString();
        }
        return context.getPolyglotBindings().getMember("author").asString();
    }

}
