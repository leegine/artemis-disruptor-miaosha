// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jndi;

import com.fitechlabs.xtier.kernel.KernelService;
import java.util.Map;
import javax.naming.*;

public interface JndiService
    extends KernelService
{

    public abstract InitialContext getInitialContext()
        throws NamingException;

    public abstract InitialContext getInitialContext(Map map)
        throws NamingException;

    public abstract Object lookupNvl(Context context, String s, Object obj)
        throws NamingException;

    public abstract Object lookupNvl(String s, Object obj)
        throws NamingException;

    public abstract Object lookupEx(Context context, String s, boolean flag)
        throws NamingException;

    public abstract Object lookupEx(String s, boolean flag)
        throws NamingException;

    public abstract boolean contains(Context context, String s)
        throws NamingException;

    public abstract boolean contains(String s)
        throws NamingException;

    public abstract void bindEx(Context context, String s, Object obj, boolean flag)
        throws NamingException;

    public abstract void bindEx(String s, Object obj, boolean flag)
        throws NamingException;

    public abstract void unbindEx(Context context, String s)
        throws NamingException;

    public abstract void unbindEx(String s)
        throws NamingException;

    public abstract void renameEx(Context context, String s, String s1)
        throws NamingException;

    public abstract void renameEx(String s, String s1)
        throws NamingException;

    public abstract Object lookupNvl(Context context, Name name, Object obj)
        throws NamingException;

    public abstract Object lookupNvl(Name name, Object obj)
        throws NamingException;

    public abstract Object lookupEx(Context context, Name name, boolean flag)
        throws NamingException;

    public abstract Object lookupEx(Name name, boolean flag)
        throws NamingException;

    public abstract boolean contains(Context context, Name name)
        throws NamingException;

    public abstract boolean contains(Name name)
        throws NamingException;

    public abstract void bindEx(Context context, Name name, Object obj, boolean flag)
        throws NamingException;

    public abstract void bindEx(Name name, Object obj, boolean flag)
        throws NamingException;

    public abstract void unbindEx(Context context, Name name)
        throws NamingException;

    public abstract void unbindEx(Name name)
        throws NamingException;

    public abstract void renameEx(Context context, Name name, Name name1)
        throws NamingException;

    public abstract void renameEx(Name name, Name name1)
        throws NamingException;
}
