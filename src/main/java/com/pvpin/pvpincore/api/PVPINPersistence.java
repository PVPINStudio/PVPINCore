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
package com.pvpin.pvpincore.api;

import com.pvpin.pvpincore.impl.persistence.PersistenceManager;

import java.util.Map;

/**
 * @author William_Shi
 */
public class PVPINPersistence {

    /**
     * This method is used to get a map to store data permanently.<p>
     * The data map is stored in memory and you can use saveToFile to write it into the file.<p>
     * You may also read the data from the file by readFromFile, and the map in memory will be disposed.<p>
     * (The path of the file is automatically generated, in plugins/PVPINCore/data)<p>
     * Each plugin(no matter it's a Java plugin or a JavaScript plugin)
     * can have ONLY ONE data map.<p>
     * The plugin CANNOT access other plugins' data maps.<p>
     * IMPORTANT: Type of the map's keys MUST be String.<p>
     * Type of the map's values MUST be one of the following:<p>
     * 1.Number(Byte, Short, Integer, Long and Double)<p>
     * 2.String<p>
     * 3.Map (whose keys MUST BE strings and values are within the four types)<p>
     * 4.List (values are within the four types)<p>
     * Note that any other number types, like float, etc. will be converted to Double automatically.<p>
     * Note that all elements in one list MUST be of the same type.<p>
     * e.g:<p>
     * [1,2] is valid.<p>
     * [1, "abc"] is invalid.
     *
     * @return the data map of the plugin calling this method
     */
    public static Map getDataMap() {
        return PersistenceManager.getCurrentHolder().getDataMap();
    }

    /**
     * This method writes the data in memory to a file to store permanently.
     */
    public static void saveToFile() {
        PersistenceManager.getCurrentHolder().saveToFile();
    }

    /**
     * This method reads the contents in the file and disposes the current data in memory.
     */
    public static void readFromFile() {
        PersistenceManager.getCurrentHolder().readFromFile();
    }

}
