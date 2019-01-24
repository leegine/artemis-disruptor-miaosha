// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ConnectorServerImpl.java

package co.fin.intellioms.connector.impl;

import co.fin.intellioms.connector.ClientConnection;
import co.fin.intellioms.connector.ConnectorServer;
import co.fin.intellioms.connector.ServerMessagesListener;
import co.fin.intellioms.util.Log;
import co.fin.intellioms.util.Startable;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.marshal.IoMarshaller;
import com.fitechlabs.xtier.services.marshal.MarshalException;
import com.fitechlabs.xtier.services.marshal.MarshalObject;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedBooleanSync;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConnectorServerImpl
    implements ConnectorServer, Startable
{
    private class Connection extends SysThread
        implements ClientConnection
    {

        protected void body()
        {
            try
            {
                if(msgLstn != null)
                    msgLstn.onConnect(this);
                else
                if(log.isWarn())
                    log.warn("Unable to invoke ServerMessagesListener.onConnect() method: Message listener is not assigned to connector server.");
                do
                {
                    checkInterrupted();
                    try
                    {
                        MarshalObject msg = (MarshalObject)marshal.demarshalObj(sock.getInputStream());
                        if(log.isDebug())
                            log.debug((new StringBuilder()).append("Received ").append(msg).toString());
                        if(msgLstn != null)
                            msgLstn.onMessage(this, msg);
                        else
                        if(log.isWarn())
                            log.warn((new StringBuilder()).append("Message listener is not assigned to connector server. Lost message ").append(msg).toString());
                    }
                    catch(MarshalException e)
                    {
                        if(log.isError() && !isInterrupted())
                            log.error((new StringBuilder()).append("Error reading message from socket ").append(sock.getRemoteSocketAddress()).toString(), e);
                    }
                } while(true);
            }
            catch(IOException e)
            {
                interrupt();
            }
//            break MISSING_BLOCK_LABEL_271;
//            Exception exception;
//            exception;
//            interrupt();
//            throw exception;
        }

        public void interrupt()
        {
            if(!isInterrupted())
            {
                if(log.isInfo())
                    log.info((new StringBuilder()).append("Closing client socket connection ").append(sock.getRemoteSocketAddress()).toString());
                super.interrupt();
                if(msgLstn != null)
                    msgLstn.onClose(this);
                close(sock);
                dead.set(true);
                if(log.isDebug())
                    log.debug((new StringBuilder()).append("Closed client socket connection ").append(sock.getRemoteSocketAddress()).toString());
            }
        }

        public SocketAddress getRemoteSocketAddress()
        {
            return sock.getRemoteSocketAddress();
        }

        public void send(MarshalObject msg)
            throws MarshalException, IOException
        {
            marshal.marshalObj(msg, sock.getOutputStream());
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Submited ").append(msg).append(" to ").append(sock.getRemoteSocketAddress()).toString());
        }

        private final Socket sock;
        private BoxedBooleanSync dead;
        final ConnectorServerImpl this$0;


        public Connection(Socket sock)
        {
            this$0 = ConnectorServerImpl.this;
//            super((new StringBuilder()).append("Connection-").append(sock.getRemoteSocketAddress()).toString());
            dead = new BoxedBooleanSync(false);
            this.sock = sock;
        }
    }

    private class Listener extends SysThread
    {

        protected void body()
        {
            if(log.isInfo())
                log.info((new StringBuilder()).append("Started listener on port ").append(port).append(".").toString());
            do
            {
                checkInterrupted();
                try
                {
                    synchronized(mux)
                    {
                        if(serv == null)
                            serv = new ServerSocket(port);
                    }
                    Socket sock = serv.accept();
                    if(log.isInfo())
                        log.info((new StringBuilder()).append("Got incoming connection from ").append(sock.getRemoteSocketAddress()).toString());
                    synchronized(mux)
                    {
                        if(!started)
                        {
                            if(log.isWarn())
                                log.warn((new StringBuilder()).append("Unable to accept client connection from ").append(sock.getRemoteSocketAddress()).append(": Server is stopped.").toString());
                            close(sock);
                        } else
                        {
                            Connection conn = new Connection(sock);
                            conn.start();
                            activeConnections.add(conn);
                            if(log.isDebug())
                                log.debug((new StringBuilder()).append("Added connection ").append(this).append(" to active connections list.").toString());
                        }
                        clearDeadClients();
                    }
                }
                catch(IOException e)
                {
                    if(log.isError() && !isInterrupted())
                        log.error("Error while accepting socket connection.", e);
                }
            } while(true);
        }

        private void clearDeadClients()
        {
            if(!Thread.holdsLock(mux))
                throw new AssertionError();
            Iterator iter = activeConnections.iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                Connection conn = (Connection)iter.next();
                if(conn.dead.get())
                    iter.remove();
            } while(true);
        }

        public void interrupt()
        {
            if(log.isInfo())
                log.info("Stopping listener.");
            super.interrupt();
            synchronized(mux)
            {
                if(serv != null)
                    try
                    {
                        serv.close();
                        if(log.isDebug())
                            log.debug("Closed server socket.");
                    }
                    catch(IOException e)
                    {
                        if(log.isWarn())
                            log.warn("Error while closing server socket.", e);
                    }
                for(Iterator iter = activeConnections.iterator(); iter.hasNext(); iter.remove())
                {
                    Connection conn = (Connection)iter.next();
                    if(log.isDebug())
                        log.debug((new StringBuilder()).append("Stopping client thread ").append(conn).toString());
                    conn.safeStop();
                }

            }
            if(log.isInfo())
                log.info("Stopped.");
        }

        private ServerSocket serv;
        private final int port;
//        static final boolean $assertionsDisabled = !com/ com /fin/intellioms/connector/impl/ConnectorServerImpl.desiredAssertionStatus();
        final ConnectorServerImpl this$0;


        public Listener(int port)
        {
            this$0 = ConnectorServerImpl.this;
//            super("ConnectorListener");
            this.port = port;
        }
    }


    public ConnectorServerImpl(int port)
    {
        log = Log.getLogger(ConnectorServer.class);
        activeConnections = new HashSet();
        lstn = new Listener(port);
    }

    public void start()
    {
        synchronized(mux)
        {
            if(started)
                throw new IllegalStateException("Already started.");
            started = true;
        }
        lstn.start();
    }

    public void stop()
    {
        synchronized(mux)
        {
            if(!started)
                throw new IllegalStateException("Already stopped.");
            started = false;
        }
        lstn.safeStop();
    }

    private void close(Socket sock)
    {
        try
        {
            if(!sock.isClosed())
                sock.shutdownInput();
        }
        catch(IOException e)
        {
            if(log.isWarn())
                log.warn((new StringBuilder()).append("Error while shuting down socket input ").append(sock.getRemoteSocketAddress()).toString(), e);
        }
        try
        {
            if(!sock.isClosed())
                sock.shutdownOutput();
        }
        catch(IOException e)
        {
            if(log.isWarn())
                log.warn((new StringBuilder()).append("Error while shuting down socket output ").append(sock.getRemoteSocketAddress()).toString(), e);
        }
        try
        {
            if(!sock.isClosed())
                sock.close();
        }
        catch(IOException e)
        {
            if(log.isWarn())
                log.warn((new StringBuilder()).append("Error while closing socket ").append(sock.getRemoteSocketAddress()).toString(), e);
        }
    }

    public void setListener(ServerMessagesListener lstnr)
    {
        if(msgLstn != null)
        {
            throw new AssertionError("Listener already assigned.");
        } else
        {
            msgLstn = lstnr;
            return;
        }
    }

    public Set getConnections()
    {
        return new HashSet(activeConnections);
    }

    private Log log;
    private Set activeConnections;
    private Listener lstn;
    private final IoMarshaller marshal = XtierKernel.getInstance().marshal().getIoMarshaller();
    private ServerMessagesListener msgLstn;
    private final Object mux = new Object();
    private boolean started;
    //static final boolean $assertionsDisabled = !com/ com /fin/intellioms/connector/impl/ConnectorServerImpl.desiredAssertionStatus();








}
