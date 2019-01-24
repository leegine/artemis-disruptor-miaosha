// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   EventException.java

package co.fin.intellioms.event;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EventException extends Exception
{

    public EventException()
    {
        error_code = 0;
    }

    public EventException(String message)
    {
        super(message);
        error_code = 0;
    }

    public EventException(Throwable cause)
    {
        super(cause);
        error_code = 0;
        addCause(cause);
    }

    public EventException(String message, Throwable cause)
    {
        super(message, cause);
        error_code = 0;
        addCause(cause);
    }

    public void addCause(Throwable err)
    {
        if(causes == null)
            causes = new ArrayList();
        causes.add(err);
    }

    public Throwable[] getCauses()
    {
        return causes == null ? null : (Throwable[])(Throwable[])causes.toArray(new Throwable[causes.size()]);
    }

    public String toString()
    {
        String str = super.toString();
        if(causes != null)
        {
            for(int i = 0; i < causes.size(); i++)
            {
                Throwable tr = (Throwable)causes.get(i);
                str = (new StringBuilder()).append(str).append('\n').append(tr.toString()).toString();
            }

        }
        return str;
    }

    public void printStackTrace()
    {
        super.printStackTrace();
        if(causes != null)
        {
            for(int i = 0; i < causes.size(); i++)
            {
                Throwable tr = (Throwable)causes.get(i);
                tr.printStackTrace();
            }

        }
    }

    public void printStackTrace(PrintStream s)
    {
        super.printStackTrace(s);
        if(causes != null)
        {
            for(int i = 0; i < causes.size(); i++)
            {
                Throwable tr = (Throwable)causes.get(i);
                tr.printStackTrace(s);
            }

        }
    }

    public void printStackTrace(PrintWriter s)
    {
        super.printStackTrace(s);
        if(causes != null)
        {
            for(int i = 0; i < causes.size(); i++)
            {
                Throwable tr = (Throwable)causes.get(i);
                tr.printStackTrace(s);
            }

        }
    }

    public static final int OTHER_ERR = 0;
    public static final int NETWORK_ERR = 1;
    public static final int OS_ERR = 2;
    public static final int DB_ERR = 3;
    public int error_code;
    private List causes;
}
