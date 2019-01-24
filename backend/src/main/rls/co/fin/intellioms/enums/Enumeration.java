// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Enumeration.java

package co.fin.intellioms.enums;


public abstract class Enumeration
{
    public class Value
    {

        public static final int OTHER = 0;
        final Enumeration this$0;

        public Value()
        {
            this$0 = Enumeration.this;
            super();
        }
    }


    protected Enumeration(int v, String s)
    {
        val = v;
        name = s;
    }

    public final String toString()
    {
        return name;
    }

    public final int toValue()
    {
        return val;
    }

    public boolean equals(Object obj)
    {
        if(obj.getClass().equals(getClass()))
            return val == ((Enumeration)obj).toValue();
        else
            return false;
    }

    protected static Enumeration getEnumError(int v, String className)
    {
        throw new RuntimeException((new StringBuilder()).append(className).append(": no corresponding number for ").append(v).toString());
    }

    private int val;
    private String name;
}
