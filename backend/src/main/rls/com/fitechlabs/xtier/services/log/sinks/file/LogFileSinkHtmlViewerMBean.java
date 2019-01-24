// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.log.sinks.file;


public interface LogFileSinkHtmlViewerMBean
{

    public abstract String viewLogFiles();

    public abstract String getDestinationFolder();

    public abstract void setDestinationFolder(String s);
}
