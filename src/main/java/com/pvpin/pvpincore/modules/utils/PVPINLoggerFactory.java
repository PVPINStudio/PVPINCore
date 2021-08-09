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
package com.pvpin.pvpincore.modules.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.OptionHelper;
import com.pvpin.pvpincore.impl.nms.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.PVPINCore;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.LoggerFactory;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class PVPINLoggerFactory {

    protected static final Logger CORE_LOGGER;
    protected static final HashMap<String, Logger> MAP = new HashMap<>(32);
    protected static final DateFormat FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE);

    static {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ((Logger)LoggerFactory.getLogger("org.apache")).setLevel(Level.OFF);
        ((Logger)LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.OFF);
        ((Logger)LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.OFF);
        ((Logger)LoggerFactory.getLogger("org.eclipse.aether")).setLevel(Level.OFF);

        CORE_LOGGER = (Logger) LoggerFactory.getLogger("CORE_LOGGER");
        MAP.put("CORE_LOGGER", CORE_LOGGER);
        CORE_LOGGER.setAdditive(false);

        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(context);
        fileAppender.setName("CORE_LOGGER_FILE_APPENDER");
        fileAppender.setFile(
                OptionHelper.substVars(
                        PVPINCore.getCoreInstance().getDataFolder().getAbsolutePath()
                                + "/logs/PVPINCore/debug.log",
                        context
                )
        );
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);
        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        String pattern = OptionHelper.substVars(
                PVPINCore.getCoreInstance().getDataFolder().getAbsolutePath()
                        + "/logs/PVPINCore/debug.%d.log.zip",
                context
        );
        policy.setFileNamePattern(pattern);
        policy.setMaxHistory(1024);
        policy.setParent(fileAppender);
        policy.setContext(context);
        policy.start();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("[%d{HH:mm:ss:SSS} (%file:%line %p\\)] %m%n");
        encoder.start();
        fileAppender.setRollingPolicy(policy);
        fileAppender.setEncoder(encoder);
        fileAppender.start();
        CORE_LOGGER.addAppender(fileAppender);

        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setPattern("%file:%line > %m%n");
        consoleEncoder.start();
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();
        CORE_LOGGER.addAppender(consoleAppender);

    }

    public static org.slf4j.Logger getCoreLogger() {
        return CORE_LOGGER;
    }

    public static org.slf4j.Logger getLoggerByName(String name) {
        if (MAP.containsKey(name)) {
            return MAP.get(name);
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        MAP.put(name, logger);
        logger.setAdditive(false);

        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(context);
        fileAppender.setName(name + "_FILE_APPENDER");
        fileAppender.setFile(
                OptionHelper.substVars(
                        PVPINCore.getCoreInstance().getDataFolder().getAbsolutePath()
                                + "/logs/" + name + "/debug.log",
                        context
                )
        );
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);
        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        String pattern = OptionHelper.substVars(
                PVPINCore.getCoreInstance().getDataFolder().getAbsolutePath()
                        + "/logs/" + name + "/debug.%d.log.zip",
                context
        );
        policy.setFileNamePattern(pattern);
        policy.setMaxHistory(1024);
        policy.setParent(fileAppender);
        policy.setContext(context);
        policy.start();
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setContext(context);
        fileEncoder.setPattern("[%d{HH:mm:ss:SSS} (%file:%line %p\\)] %m%n");
        fileEncoder.start();
        fileAppender.setRollingPolicy(policy);
        fileAppender.setEncoder(fileEncoder);
        fileAppender.start();
        logger.addAppender(fileAppender);

        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setPattern("PVPIN > %m%n");
        consoleEncoder.start();
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();
        logger.addAppender(consoleAppender);
        return logger;
    }
}
