package ru.er_log.utils;

import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Log
{
    private static final String debugPrefix     = "[DEBUG]";
    private static final String infoPrefix      = " [INFO]";
    private static final String warningPrefix   = " [WARN]";
    private static final String errorPrefix     = "[ERROR]";
    private static final String fatalPrefix     = "[FATAL]";
    private static final String timeFormat = "HH:mm:ss";

    private static Log singleton;
    private static ArrayList<ILogObserver> outObserversList = new ArrayList<>();
    private static ArrayList<ILogObserver> errObserversList = new ArrayList<>();

    public static Log getInstance()
    {
        if (singleton == null)
            singleton = new Log();

        return singleton;
    }

    public static void debug(Object message)
    {
        debug((message != null) ? message.toString() : null, "");
    }

    public static void debug(String separator, Object... objects)
    {
        debug(null, separator, objects);
    }

    public static void debug(String message, String objSeparator, Object... objects)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(getTime("HH:mm:ss")).append("] ").append(debugPrefix).append(" ");

        if (message != null)
            stringBuilder.append(message).append(!message.isEmpty() ? " " : "");

        if (objects != null)
        {
            if (objSeparator == null) objSeparator = "";
            for (Object object : objects)
                stringBuilder.append((object != null) ? object.toString() : "null").append(objSeparator);
            stringBuilder.setLength(stringBuilder.length() - objSeparator.length());
        }

        System.out.println(stringBuilder.toString());
        fireLogOut(stringBuilder.toString());
    }

    public static void out(Object message)
    {
        out((message != null) ? message.toString() : null, "");
    }

    public static void out(String separator, Object... objects)
    {
        out(null, separator, objects);
    }

    public static void out(String message, String objSeparator, Object... objects)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(getTime("HH:mm:ss")).append("] ").append(infoPrefix).append(" ");

        if (message != null)
            stringBuilder.append(message).append(!message.isEmpty() ? " " : "");

        if (objects != null)
        {
            if (objSeparator == null) objSeparator = "";
            for (Object object : objects)
                stringBuilder.append((object != null) ? object.toString() : "null").append(objSeparator);
            stringBuilder.setLength(stringBuilder.length() - objSeparator.length());
        }

        System.out.println(stringBuilder.toString());
        fireLogOut(stringBuilder.toString());
    }

    public static void warn(Object message)
    {
        warn((message != null) ? message.toString() : null, "");
    }

    public static void warn(String separator, Object... objects)
    {
        warn(null, separator, objects);
    }

    public static void warn(String message, String objSeparator, Object... objects)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(getTime("HH:mm:ss")).append("] ").append(warningPrefix).append(" ");

        if (message != null)
            stringBuilder.append(message).append(!message.isEmpty() ? " " : "");

        if (objects != null)
        {
            if (objSeparator == null) objSeparator = "";
            for (Object object : objects)
                stringBuilder.append((object != null) ? object.toString() : "null").append(objSeparator);
            stringBuilder.setLength(stringBuilder.length() - objSeparator.length());
        }

        System.out.println(stringBuilder.toString());
        fireLogOut(stringBuilder.toString());
    }

    public static void err(Object message)
    {
        err((message != null) ? message.toString() : null, "");
    }

    public static void err(String separator, Object... objects)
    {
        err(null, separator, objects);
    }

    public static void err(String message, String objSeparator, Object... objects)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(getTime("HH:mm:ss")).append("] ").append(errorPrefix).append(" ");

        if (message != null)
            stringBuilder.append(message).append(!message.isEmpty() ? " " : "");

        if (objects != null)
        {
            if (objSeparator == null) objSeparator = "";
            for (Object object : objects)
                stringBuilder.append((object != null) ? object.toString() : "null").append(objSeparator);
            stringBuilder.setLength(stringBuilder.length() - objSeparator.length());
        }

        System.err.println(stringBuilder.toString());
        fireLogErr(stringBuilder.toString());
    }

    public static void fatal(Object message)
    {
        fatal((message != null) ? message.toString() : null, "");
    }

    public static void fatal(String separator, Object... objects)
    {
        fatal(null, separator, objects);
    }

    public static void fatal(String message, String objSeparator, Object... objects)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(getTime("HH:mm:ss")).append("] ").append(fatalPrefix).append(" ");

        if (message != null)
            stringBuilder.append(message).append(!message.isEmpty() ? " " : "");

        if (objects != null)
        {
            if (objSeparator == null) objSeparator = "";
            for (Object object : objects)
                stringBuilder.append((object != null) ? object.toString() : "null").append(objSeparator);
            stringBuilder.setLength(stringBuilder.length() - objSeparator.length());
        }

        System.err.println(stringBuilder.toString());
        fireLogErr(stringBuilder.toString());
    }

    private static String getTime(String format)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(cal.getTime());
    }

    public void attachLogOutObserver(ILogObserver observer)
    {
        outObserversList.add(observer);
    }

    public void attachLogErrObserver(ILogObserver observer)
    {
        errObserversList.add(observer);
    }

    public void removeLogObserver(ILogObserver observer)
    {
        outObserversList.remove(observer);
        errObserversList.remove(observer);
    }

    private static void fireLogOut(String message)
    {
        for (ILogObserver iLogObserver : outObserversList)
            EventQueue.invokeLater(new HandleLogRunner(iLogObserver, message));
    }

    private static void fireLogErr(String message)
    {
        for (ILogObserver iLogObserver : errObserversList)
            EventQueue.invokeLater(new HandleLogRunner(iLogObserver, message));
    }

    public interface ILogObserver
    {
        void handleLog(String param1String);
    }

    private static class HandleLogRunner implements Runnable
    {
        private String message;
        private Log.ILogObserver target;

        private HandleLogRunner(Log.ILogObserver target, String message)
        {
            this.message = message;
            this.target = target;
        }

        @Override
        public void run()
        {
            this.target.handleLog(this.message);
        }
    }
}