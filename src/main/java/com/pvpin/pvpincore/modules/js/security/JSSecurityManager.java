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
package com.pvpin.pvpincore.modules.js.security;

import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import org.graalvm.polyglot.Context;

import java.io.FileDescriptor;
import java.util.Arrays;

/**
 * @author William_Shi
 */
public class JSSecurityManager extends SecurityManager {
    static {
        try {
            Class.forName(JSPluginAccessController.class.getName());
        } catch (ClassNotFoundException ex) {
            PVPINLogManager.log(ex);
        }
    }

    private static final String PERSISTENCE = "com.pvpin.pvpincore.impl.persistence";

    private void check() {
        if (!JSPluginAccessController.isLoadedByJavaScriptEngine()) {
            return;
        }
        boolean[] bool = new boolean[1];
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        Arrays.stream(elements).forEach(stackTraceElement -> {
            if (stackTraceElement.toString().contains(PERSISTENCE) ||
                    stackTraceElement.toString().contains("org.graalvm.polyglot.Engine.<clinit>") ||
                    stackTraceElement.toString().contains("org.graalvm.polyglot.Engine$ImplHolder.<clinit>") ||
                    stackTraceElement.toString().contains("com.oracle.truffle.js.lang.JavaScriptLanguage.<clinit>") ||
                    stackTraceElement.toString().contains("com.oracle.truffle.js.runtime.JSEngine.<clinit>") ||
                    stackTraceElement.toString().contains("com.oracle.truffle.js.runtime.JSContext.<init>") ||
                    stackTraceElement.toString().contains("com.oracle.truffle.js.runtime.JSRealm.<clinit>") ||
                    stackTraceElement.toString().contains("org.graalvm.polyglot.Source$Builder.build") ||
                    stackTraceElement.toString().contains("org.graalvm.polyglot.Context$Builder.build") ||
                    stackTraceElement.toString().contains("org.graalvm.polyglot.Source.findLanguage") ||
                    stackTraceElement.toString().contains("ch.qos.logback.core.CoreConstants.<clinit>")) {
                bool[0] = true;
            }

        });
        if (bool[0]) {
            return;
        }
        JSPluginAccessController.denyAccess(Context.getCurrent());
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        // super.checkRead(fd);
        check();
    }

    @Override
    public void checkRead(String file) {
        // super.checkRead(file);
        check();
    }

    @Override
    public void checkRead(String file, Object context) {
        super.checkRead(file, context);
        check();
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        super.checkWrite(fd);
        check();
    }

    @Override
    public void checkWrite(String file) {
        super.checkWrite(file);
        check();
    }

    @Override
    public void checkDelete(String file) {
        super.checkDelete(file);
        check();
    }
}
