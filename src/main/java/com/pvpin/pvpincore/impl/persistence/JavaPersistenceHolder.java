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
package com.pvpin.pvpincore.impl.persistence;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.api.PVPINPersistence;

import java.util.Map;

/**
 * @author William_Shi
 */
class JavaPersistenceHolder extends AbstractHolder {

    @Override
    protected String getDataFileSimpleName() {
        return "java_" + namespace + ".dat";
    }

    protected JavaPersistenceHolder(String namespace) {
        super(namespace);
    }

    /**
     * This method is overridden to check if the data is accessed from the right plugin.
     *
     * @return data map
     * @see PVPINPersistence#getDataMap()
     */
    @Override
    public Map getDataMap() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // java.base/java.lang.Thread.getStackTrace(Thread.java:1606)
        // com.pvpin.pvpincore.impl.persistence.JavaPersistenceHolder.getDataMap,
        // com.pvpin.pvpincore.api.PVPINPersistence.getDataMap
        if (!elements[2].getClassName().equals("com.pvpin.pvpincore.api.PVPINPersistence")) {
            try {
                throw new IllegalAccessException();
            } catch (Exception ex) {
                PVPINLogManager.log(ex);
            }
        }
        return super.getDataMap();
    }

}
