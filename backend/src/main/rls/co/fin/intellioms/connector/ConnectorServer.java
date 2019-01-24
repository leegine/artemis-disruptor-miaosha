// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConnectorServer.java

package co.fin.intellioms.connector;

import java.util.Set;

// Referenced classes of package com.com.fin.intellioms.connector:
//            ServerMessagesListener

public interface ConnectorServer
{

    public abstract void start();

    public abstract void stop();

    public abstract void setListener(ServerMessagesListener servermessageslistener);

    public abstract Set getConnections();
}
