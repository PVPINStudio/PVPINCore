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

import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.impl.nms.nbt.NBTIO;
import com.pvpin.pvpincore.impl.nms.nbt.NBTUtils;
import com.pvpin.pvpincore.modules.PVPINCore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a data holder.<p>
 * Each JavaScript plugin or Java plugin can have only one data holder.<p>
 * The data is stored in the format of NBT.<p>
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
 * @author William_Shi
 */
public abstract class AbstractHolder {

    protected String namespace;
    protected Map data = new HashMap();
    protected File datafile_nbt;

    protected abstract String getDataFileSimpleName();

    protected AbstractHolder(String namespace) {
        try {
            this.namespace = namespace;
            File folder = new File(PVPINCore.getCoreInstance().getDataFolder(), "data");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File dataFile_nbt = new File(folder, getDataFileSimpleName());
            if (!dataFile_nbt.exists()) {
                dataFile_nbt.createNewFile();
                NBTIO.writeNBTToFile(null, dataFile_nbt);
            }
            this.datafile_nbt = dataFile_nbt;
            readFromFile();
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }

    public void readFromFile() {
        this.data.clear();
        Map map = NBTUtils.convertNBTToMap(NBTIO.readNBTFromFile(this.datafile_nbt));
        this.data.putAll(map);
    }

    public void saveToFile() {
        NBTIO.writeNBTToFile(NBTUtils.convertMapToNBT(this.data), this.datafile_nbt);
    }

    public Map getDataMap() {
        return this.data;
    }

}
