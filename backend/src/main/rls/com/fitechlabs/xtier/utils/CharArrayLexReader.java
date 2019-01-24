// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils;


public class CharArrayLexReader
{

    public CharArrayLexReader(String s)
    {
        chars = null;
        index = 0;
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            chars = s.toCharArray();
            return;
        }
    }

    public int getLength()
    {
        return chars.length;
    }

    public char read()
    {
        char c = index < chars.length ? chars[index] : '\uFFFF';
        index++;
        return c;
    }

    public char[] read(int i)
    {
        char ac[] = new char[i];
        for(int j = 0; j < i; j++)
            ac[j] = read();

        return ac;
    }

    public char[] read(int i, char ac[], int j)
    {
        for(int k = 0; k < i; k++)
            ac[j + k] = read();

        return ac;
    }

    public char peek()
    {
        return index < chars.length ? chars[index] : '\uFFFF';
    }

    public void skip()
    {
        index++;
    }

    public void skip(int i)
    {
        index += i;
    }

    public void back()
    {
        if(!$assertionsDisabled && index <= 0)
        {
            throw new AssertionError("Cannot back before start.");
        } else
        {
            index--;
            return;
        }
    }

    public void back(int i)
    {
        if(!$assertionsDisabled && index - i < 0)
        {
            throw new AssertionError("Cannot back before start.");
        } else
        {
            index -= i;
            return;
        }
    }

    public boolean hasNext()
    {
        return index < chars.length;
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    public static final char EOF = 65535;
    private char chars[];
    private int index;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CharArrayLexReader.class).desiredAssertionStatus();
    }
}
