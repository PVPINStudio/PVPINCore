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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.OptionHelper;
import com.pvpin.pvpincore.modules.PVPINCore;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

/**
 * @author William_Shi
 */
public class PVPINLoggerFactory {

    protected static Logger CORE_LOGGER = null;
    protected static HashMap<String, Logger> MAP = new HashMap<>(32);
    protected static DateFormat FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE);
    private static final String LOG_FILE_PATTERN = "[%d{HH:mm:ss:SSS} (%file:%line %p\\)] %m%n";
    private static final String LOG_CONSOLE_PATTERN = "\r∞ {NAME} (%d{HH:mm}\\) >>> %m%n";
    private static final String LOG_CONSOLE_PATTERN_CORE = "\r∞ PVPIN (%d{HH:mm}\\) >>> %m%n";

    public static void loadLoggers() {
        if (CORE_LOGGER != null) {
            return;
        }
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ((Logger) LoggerFactory.getLogger("org.apache")).setLevel(Level.OFF);
        ((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.OFF);
        ((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.OFF);
        ((Logger) LoggerFactory.getLogger("org.eclipse.aether")).setLevel(Level.OFF);

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
        encoder.setPattern(LOG_FILE_PATTERN);
        encoder.start();
        fileAppender.setRollingPolicy(policy);
        fileAppender.setEncoder(encoder);
        fileAppender.start();
        CORE_LOGGER.addAppender(fileAppender);

        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setPattern(LOG_CONSOLE_PATTERN_CORE);
        consoleEncoder.start();
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();
        CORE_LOGGER.addAppender(consoleAppender);
        hackConsoleAppender(consoleAppender);

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
        fileEncoder.setPattern(LOG_FILE_PATTERN);
        fileEncoder.start();
        fileAppender.setRollingPolicy(policy);
        fileAppender.setEncoder(fileEncoder);
        fileAppender.start();
        logger.addAppender(fileAppender);

        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setPattern(LOG_CONSOLE_PATTERN.replace("{NAME}", name));
        consoleEncoder.start();
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();
        logger.addAppender(consoleAppender);
        hackConsoleAppender(consoleAppender);
        return logger;
    }

    /**
     * This method is used to replace the System.out stream for an appender with FileDescriptor.out<p>
     * Because the standard output in a spigot based server begins with [HH:mm:ss]:<p>
     * Using this method can print a line directly to the screen and bypass the spigot limits.
     *
     * @param appender the appender to be hacked
     */
    private static void hackConsoleAppender(ConsoleAppender appender) {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Field module = Class.class.getDeclaredField("module");
            long offset = unsafe.objectFieldOffset(module);
            unsafe.putObject(PVPINLoggerFactory.class, offset, Object.class.getModule());

            Field stream = OutputStreamAppender.class.getDeclaredField("outputStream");
            stream.setAccessible(true);
            stream.set(appender, new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out))));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
