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
package com.pvpin.pvpincore.impl.nms;

import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class VersionChecker {

    public static final String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName()
            .replace(".", ",").split(",")[3];

    /**
     * @return Server version, such as v1_15_R1.
     */
    public static String getServerVersion() {
        return version;
    }

    /**
     * This method is used to compare versions.<p>
     * It is useful when NMS or OBC operations are needed.<p>
     * e.g. You may need to determine whether the server is running on 1.17,
     * to choose the right format of NMS class names.
     *
     * @param compare version to be compared, such as v1_13_R1.
     * @return true if the current server version is higher than the param version.
     */
    public static boolean isCurrentHigherOrEquals(String compare) {
        if (compare.startsWith("v")) {
            compare = compare.substring(1);
            // Remove the 'v' of 'v1_1x_Rx'.
        }
        String[] sp = compare.split("_");
        StringBuilder builder = new StringBuilder("");
        builder.append(sp[0]);
        // Append 1.
        builder.append(sp[1]);
        // Append 1x.
        if (sp.length == 3) {
            if (sp[2].startsWith("R")) {
                builder.append(sp[2].substring(1));
                // Remove the 'R' of 'Rx'
            } else {
                builder.append(sp[2]);
                // Directly append if there is no 'R' at the beginning.
            }
        } else {
            builder.append("0");
            // v1_1x is considered as v1_1x_R0.
        }
        String current = version.substring(1);
        StringBuilder currBuilder = new StringBuilder("");
        currBuilder.append(current.split("_")[0]);
        currBuilder.append(current.split("_")[1]);
        currBuilder.append(current.split("_")[2].substring(1));
        // Current server version
        Integer vCurr = Integer.parseInt(currBuilder.toString());
        Integer vCompare = Integer.parseInt(builder.toString());
        // A number such as 1121 (v1_12_R1).
        if (vCurr >= vCompare) {
            return true;
        } else {
            return false;
        }
    }

}
