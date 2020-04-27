/*
 * Class: org.bklab.util.Debug
 * Modify date: 2020/3/20 上午10:25
 * Copyright (c) 2008 - 2020. - Broderick Labs.
 */

package org.bklab.quark.util;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Debug {

    private static final Logger logger = Logger.getAnonymousLogger();

    private static String className;
    private static String methodName;
    private static int lineNumber;

    public static void line() {
        System.err.println(getMsg(""));
    }

    public static void warn(Object e) {
        System.err.println("Log.warn:\n  " + getMsg(e));
    }

    public static void logWarn(Object e) {
        logger.logp(Level.WARNING, className, methodName, getMsg(e));
    }

    public static void info(Object e) {
        System.err.println("Log.info:\n  " + getMsg(e));
    }

    public static void logInfo(Object e) {
        logger.logp(Level.INFO, className, methodName, getMsg(e));
    }

    public static void println(Object e) {
        System.err.println(getMsg(e));
    }

    public static void printValueLine(Object e) {
        System.err.println(e);
    }

    public static void print(Object e) {
        System.err.print(getMsg(e));
    }

    public static void logger(Object e, Level level) {
        logger.logp(level, className, methodName, getMsg(e));
    }


    private static String getMsg(Object e) {
        StackTraceElement stack = (new Throwable()).getStackTrace()[2];
        className = stack.getClassName();
        methodName = stack.getMethodName();
        lineNumber = stack.getLineNumber();
        return className + ':' +
                lineNumber + '\t' +
                methodName + "()" + ':' + System.lineSeparator() + '\t' +
                e + System.lineSeparator();
    }
}
