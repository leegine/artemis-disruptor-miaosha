// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.microkernel;


public class MicroKernelException extends Exception
{

    public MicroKernelException(String s)
    {
        super(s);
    }

    public MicroKernelException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return "Micro kernel exception: " + getMessage();
    }
}
