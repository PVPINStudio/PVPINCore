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
package com.pvpin.pvpincore.impl.persistence;

/**
 * @author William_Shi
 */
class JSPersistenceHolder extends AbstractHolder {

    @Override
    protected String getDataFileSimpleName() {
        return "js_" + namespace + ".dat";
    }

    protected JSPersistenceHolder(String namespace) {
        super(namespace);
    }

    @Override
    public void saveToFile() {
        // The [] list in JavaScript is wrapped into a PolyglotMap.
        // As you can see, the PolyglotMap is not a list, but a map.
        // The map's keySet, values and entrySet are all empty.

        // https://github.com/oracle/graaljs/issues/214
        // This problem is still not solved.
        // It existed in 2019 and in 2021 a developer replied, saying it was solved in graaljs 21.1.
        // But I met the same problem in graaljs 21.2.

        // Note in 2021.7.27 by William_Shi
        // HostAccess access = HostAccess.newBuilder(HostAccess.ALL).targetTypeMapping(Value.class, Object.class, Value::hasArrayElements, v -> new LinkedList<>(v.as(List.class))).build();
        // This line of code works.
        super.saveToFile();
    }
}
