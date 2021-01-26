package com.hehongdan;

import android.util.Log;

import com.rengwuxian.materialedittext.BuildConfig;

import java.util.Hashtable;

/**
 * 打印日志
 * <p>
 * The class for print log
 *
 * @author kesenhoo
 * <p>
 * logger工具类，单例设计模式
 * <p>
 * 定义MyLogger过程：
 * hhdLog.v("括号中间的内容");//测试代码处编写
 * <p>
 * 实际显示效果：
 * [HHD]: @HHD@ [ scan_bluetooth: HomeMainActivity.java:88 onCreate ] - 括号中间的内容
 * <p>
 * Log.e(TAG,"级别5，错误信息");//Error:FF5555//Assert:FF0000
 * Log.w(TAG,"级别4，警告信息");//Warning:BBBB55
 * Log.i(TAG,"级别3，一般信息");//Info:55BB55
 * Log.d(TAG,"级别2，调试信息");//Debug:6666FF
 * Log.v(TAG,"级别1，无用信息");//Verbose:BBBBBB
 */
public class LogUtils {

    /**
     * 单例模式：持有外部类的私有静态内部类（保证实例单一）。
     */
    private static class Holder {
        /** 持有单一外部实例。 */
        private static final LogUtils INSTANCE = new LogUtils();
    }

    /** 标记是否打印日志 */
    private final static boolean LOG_FLAG = BuildConfig.DEBUG;
    /** 日志的标记（一般为类名） */
    public final static String TAG = "【HHD】";
    /** 日志的级别 */
    private final static int LOG_LEVEL = Log.VERBOSE;
    /** 存放不同用户Log实例的容器 */
    @SuppressWarnings({"FieldMayBeFinal", "Convert2Diamond"})
    private static Hashtable<String, LogUtils> sLoggerTable = new Hashtable<String, LogUtils>();
    /** 拼接符号（方法名与内容之间） */
    private static final String APPEND_SYMBOL = " - ";

    /**
     * 私有的构造方法
     */
    private LogUtils() {
    }

    /**
     * 单例：获取实例。
     *
     * @return 实例。
     */
    public static LogUtils instance() {
        return Holder.INSTANCE;
    }

    /**
     * 获取当前（堆栈跟踪元素）的方法名。
     * Get The Current Function Name
     *
     * @return 方法名（包括：@HHD@、线程名、类名、行数、方法名）。
     */
    @SuppressWarnings("ConstantConditions")
    private String getFunctionName() {
        //堆栈跟踪元素数组（当前线程）
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            //堆栈跟踪元素方法是（本机方法）
            if (st.isNativeMethod()) {
                //结束单次循环
                continue;
            }
            //堆栈跟踪元素类名是（该线程的名称）
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            //堆栈跟踪元素类名是（本Log的名称）
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            //@HHD@ [ 线程:scan_bluetooth  文件:类名.java  行数:100  方法:方法名 ]
            return "[线程:" + Thread.currentThread().getName() + " 文件:" + st.getFileName() + " 行数:" + st.getLineNumber() + " 方法:" + st.getMethodName() + "]";
        }
        return null;
    }


    /**
     * Verbose级别的日志。
     * The Log Level:V
     *
     * @param str 打印的内容。
     */
    @SuppressWarnings("ConstantConditions")
    public static void v(Object str) {
        if (LOG_FLAG) {
            if (LOG_LEVEL <= Log.VERBOSE) {
                String name = instance().getFunctionName();
                if (name != null) {
                    Log.v(TAG, name + APPEND_SYMBOL + str);
                } else {
                    Log.v(TAG, str.toString());
                }
            }
        }
    }

    /**
     * Debug级别的日志。
     * The Log Level:d
     *
     * @param str 打印的内容。
     */
    @SuppressWarnings("ConstantConditions")
    public static void d(Object str) {
        if (false) {
            Log.d("HHD 检查是否同个过Log实例", String.valueOf(instance().hashCode()));
        }


        if (LOG_FLAG) {
            if (LOG_LEVEL <= Log.DEBUG) {
                String name = instance().getFunctionName();
                if (name != null) {
                    Log.d(TAG, name + APPEND_SYMBOL + str);
                } else {
                    Log.d(TAG, str.toString());
                }
            }
        }
    }


    /**
     * Info级别的日志。
     * The Log Level:i
     *
     * @param str 打印的内容。
     */
    @SuppressWarnings("ConstantConditions")
    public static void i(Object str) {
        if (LOG_FLAG) {
            if (LOG_LEVEL <= Log.INFO) {
                String name = instance().getFunctionName();
                if (name != null) {
                    Log.i(TAG, name + APPEND_SYMBOL + str);
                } else {
                    Log.i(TAG, str.toString());
                }
            }
        }
    }

    /**
     * Warn级别的日志。
     * The Log Level:w
     *
     * @param str 打印的内容。
     */
    @SuppressWarnings("ConstantConditions")
    public static void w(Object str) {
        if (LOG_FLAG) {
            if (LOG_LEVEL <= Log.WARN) {
                String name = instance().getFunctionName();
                if (name != null) {
                    Log.w(TAG, name + APPEND_SYMBOL + str);
                } else {
                    Log.w(TAG, str.toString());
                }
            }
        }
    }

    /**
     * Error级别的日志。
     * The Log Level:e
     *
     * @param str 打印的内容。
     */
    @SuppressWarnings("ConstantConditions")
    public static void e(Object str) {
        if (LOG_FLAG) {
            if (LOG_LEVEL <= Log.ERROR) {
                String name = instance().getFunctionName();
                if (name != null) {
                    Log.e(TAG, name + APPEND_SYMBOL + str);
                } else {
                    Log.e(TAG, str.toString());
                }
            }
        }
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    /** 自定义Log：日志构造方法参数。 */
    @SuppressWarnings("FieldMayBeFinal")
    private String mClassName;

    /**
     * 自定义Log：自定义实例。
     *
     * <h3>自定义Log时给{@link LogUtils#getLogger(String)}调用。</h3>
     *
     * @param className 调用类的名称。
     */
    private LogUtils(String className) {
        mClassName = className;
    }

    /**
     * 自定义Log：单例模式创建Log。
     *
     * @param className 调用类的名称。
     * @return 日志实例。
     */
    @SuppressWarnings({"unused", "RedundantCast"})
    private static LogUtils getLogger(String className) {
        LogUtils classLogger = (LogUtils) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new LogUtils(className);
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    /**
     * 自定义Log：自定义打印异常Error级别的日志。
     * The Log Level:e
     *
     * @param ex 异常。
     */
    @SuppressWarnings("ConstantConditions")
    public void e(Exception ex) {
        if (LOG_FLAG) {
            if (LOG_LEVEL <= Log.ERROR) {
                Log.e(TAG, "错误", ex);
            }
        }
    }

    /**
     * 自定义Log：自定义打印抛错误Error级别的日志。
     * The Log Level:e
     *
     * @param log 日志内容。
     * @param tr  抛错误。
     */
    public void e(String log, Throwable tr) {
        if (LOG_FLAG) {
            String line = getFunctionName();
            Log.e(TAG, "{线程:" + Thread.currentThread().getName() + "}" + "[文件:" + mClassName + "，行数：" + line + "] " + log + "\n", tr);
        }
    }
}