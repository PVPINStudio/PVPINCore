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
package com.pvpin.pvpincore.modules.logging;

import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.slf4j.Logger;


/**
 * This class is used to manage methods related to logging.
 *
 * @author William_Shi
 */
public class PVPINLogManager {

    /**
     * This method is used to log exceptions uniformly.
     *
     * @param ex Exception caught
     */
    public static void log(Exception ex) {
        Logger logger = PVPINLoggerFactory.getCoreLogger();
        logger.error(ex.getLocalizedMessage(), ex);
        logger.error("如果您认为该问题不是您正在使用的插件的问题，尝试联系PVPINCore开发者取得帮助");
        logger.error("请先向您使用的插件的开发者进行反馈以确认是PVPINCore的问题后再行联系");
        logger.error("您可以在https://github.com/PVPINStudio/PVPINCore 找到我们");
    }

    /**
     * This method is used to get a logger by name.
     *
     * @param name name of the logger
     * @return the logger of this name (may generate a new one if not found)
     */
    public static org.slf4j.Logger getLogger(String name) {
        return PVPINLoggerFactory.getLoggerByName(name);
    }
}
