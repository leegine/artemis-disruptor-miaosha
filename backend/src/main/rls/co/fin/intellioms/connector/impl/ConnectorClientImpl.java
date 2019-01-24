// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConnectorClientImpl.java

package co.fin.intellioms.connector.impl;

import co.fin.intellioms.connector.ConnectorClient;
import co.fin.intellioms.connector.MessageListener;
import co.fin.intellioms.util.Log;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.threads.SysThread;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

// Referenced classes of package com.com.fin.intellioms.connector.impl:
//            MsgHelper

public class ConnectorClientImpl
    implements ConnectorClient
{

    public ConnectorClientImpl(String host, int port)
    {
        shutdownErr = null;
        this.host = host;
        this.port = port;
    }

    public void connect()
        throws IOException
    {
        doConnect(0);
        connectorThread.start();
        if(log.isInfo())
            log.info((new StringBuilder()).append("Connected to ").append(sock.getRemoteSocketAddress()).toString());
    }

    public void connect(int timeout)
        throws IOException
    {
        doConnect(timeout);
        connectorThread.start();
        if(log.isInfo())
            log.info((new StringBuilder()).append("Connected to ").append(sock.getRemoteSocketAddress()).toString());
    }

    public void disconnect()
        throws IOException
    {
        if(connectorThread == null)
            throw new IOException("Not connected.");
        if(log.isInfo())
            log.info("Disconnecting");
        connectorThread.safeStop();
        if(shutdownErr != null)
            throw shutdownErr;
        else
            return;
    }

    public boolean isConnected()
    {
        return connectorThread != null;
    }

    public void setListener(MessageListener lstnr)
    {
        if(!$assertionsDisabled && this.lstnr != null)
        {
            throw new AssertionError("Listener already assigned.");
        } else
        {
            this.lstnr = lstnr;
            return;
        }
    }

    public void send(MarshalObject msg)
        throws MarshalException, IOException
    {
        synchronized(mux)
        {
            marshal.marshalObj(msg, sock.getOutputStream());
        }
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Submited ").append(msg).append(" to ").append(sock.getRemoteSocketAddress()).toString());
    }

    public MarshalObject sendSync(MarshalObject msg)
        throws MarshalException, IOException, InterruptedException
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        marshal.marshalObj(msg, sock.getOutputStream());
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Submited ").append(msg).append(" to ").append(sock.getRemoteSocketAddress()).toString());
        for(; reply == null && isConnected(); mux.wait(1000L));
        if(reply == null && !isConnected())
            throw new IOException((new StringBuilder()).append("Unable to send message: Not connected. ").append(msg).toString());
        MarshalObject tmpReply = reply;
        reply = null;
        mux.notifyAll();
        return tmpReply;
        Exception exception;
        exception;
        throw exception;
    }

    private void doConnect(int timeout)
        throws IOException
    {
        if(connectorThread != null)
            throw new IOException("Already connected.");
        sock = new Socket();
        if(timeout > 0)
            sock.connect(new InetSocketAddress(host, port), timeout);
        else
            sock.connect(new InetSocketAddress(host, port));
        connectorThread = new SysThread((new StringBuilder()).append("Connection-").append(host).append(":").append(port).toString()) {

            protected void body()
            {
                try
                {
                    while(true)
                    {
                        checkInterrupted();
                        try
                        {
                            InputStream in = sock.getInputStream();
                            MarshalObject msg = (MarshalObject)marshal.demarshalObj(in);
                            if(MsgHelper.isReply(msg))
                                synchronized(mux)
                                {
                                    if(!$assertionsDisabled && reply != null)
                                        throw new AssertionError((new StringBuilder()).append("Reply object must be null: ").append(reply).toString());
                                    reply = msg;
                                    mux.notifyAll();
                                    try
                                    {
                                        mux.wait();
                                    }
                                    catch(InterruptedException e) { }
                                }
                            else
                            if(lstnr != null)
                                lstnr.onMessage(msg);
                        }
                        catch(MarshalException e)
                        {
                            if(log.isError() && !isInterrupted())
                                log.error("Error reading message from socket.", e);
                        }
                    }
                }
                catch(IOException e) { }
                if(!isInterrupted())
                    interrupt();
                break MISSING_BLOCK_LABEL_243;
                Exception exception1;
                exception1;
                if(!isInterrupted())
                    interrupt();
                throw exception1;
            }

            public void interrupt()
            {
                if(!isInterrupted())
                {
                    if(lstnr != null)
                        lstnr.onClose();
                    super.interrupt();
                    if(sock != null)
                    {
                        try
                        {
                            sock.shutdownInput();
                        }
                        catch(IOException e)
                        {
                            shutdownErr = e;
                        }
                        try
                        {
                            sock.shutdownOutput();
                        }
                        catch(IOException e)
                        {
                            if(shutdownErr == null)
                                shutdownErr = e;
                        }
                        try
                        {
                            sock.close();
                        }
                        catch(IOException e)
                        {
                            if(shutdownErr == null)
                                shutdownErr = e;
                        }
                        if(log.isInfo())
                            log.info((new StringBuilder()).append("Closed connection ").append(sock.getRemoteSocketAddress()).toString());
                    }
                    connectorThread = null;
                    sock = null;
                }
            }

            static final boolean $assertionsDisabled = !com/ com /fin/intellioms/connector/impl/ConnectorClientImpl.desiredAssertionStatus();
            final ConnectorClientImpl this$0;



            {
                this$0 = ConnectorClientImpl.this;
                super(x0);
            }
        }
;
    }

    public String toString()
    {
        return (new StringBuilder()).append("ConnectorClientImpl[host=").append(host).append(", port=").append(port).append(", connected=").append(isConnected()).append(']').toString();
    }

    private Socket sock;
    private final Log log = Log.getLogger(com/ com /fin/intellioms/connector/ConnectorClient);
    private IOException shutdownErr;
    private MessageListener lstnr;
    private final String host;
    private final int port;
    private final IoMarshaller marshal = XtierKernel.getInstance().marshal().getIoMarshaller();
    private SysThread connectorThread;
    private MarshalObject reply;
    private final Object mux = new Object();
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/connector/impl/ConnectorClientImpl.desiredAssertionStatus();












}
