// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   RuleEngineConnectorManagerMBean.java

package co.fin.intellioms.jmx;

import co.fin.intellioms.util.InitializationException;
import java.net.SocketAddress;

public interface RuleEngineConnectorManagerMBean
{

    public abstract int getPort();

    public abstract SocketAddress[] getConnectedClientAddresses();

    public abstract boolean isStarted();

    public abstract void start()
        throws InitializationException;

    public abstract void stop();
}
