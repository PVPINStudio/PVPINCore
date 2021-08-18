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
import com.pvpin.pvpincore.modules.utils.PVPINLoggerFactory;
import org.graalvm.polyglot.Context;

import java.util.Arrays;

/**
 * @author William_Shi
 */
public class JSSecurityManager extends SecurityManager {
    String PERSISTENCE = "com.pvpin.pvpincore.impl.persistence";

    @Override
    public void checkWrite(String file) {
        if (ClassChecker.isLoadedByJavaScriptEngine()) {
            boolean[] bool = new boolean[2];
            bool[0] = false;
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            Arrays.stream(elements).forEach(stackTraceElement -> {
                if (stackTraceElement.toString().contains(PERSISTENCE)) {
                    bool[1] = true;
                }
                if (stackTraceElement.toString().contains("org.graalvm.polyglot.Value.execute")) {
                    bool[0] = true;
                }
            });
            if (bool[1]) {
                return;
            }
            if (bool[0]) {
                JSPluginAccessController.denyAccess(Context.getCurrent());
            }
        }
    }

    @Override
    public void checkRead(String file) {
        super.checkRead(file);
        if (ClassChecker.isLoadedByJavaScriptEngine()) {
            boolean[] bool = new boolean[2];
            bool[0] = false;
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            Arrays.stream(elements).forEach(stackTraceElement -> {
                if (stackTraceElement.toString().contains(PERSISTENCE)) {
                    bool[1] = true;
                }
                if (stackTraceElement.toString().contains("org.graalvm.polyglot.Value.execute")) {
                    bool[0] = true;
                }
            });
            if (bool[1]) {
                return;
            }
            if (bool[0]) {
                JSPluginAccessController.denyAccess(Context.getCurrent());
            }
        }
    }
}
