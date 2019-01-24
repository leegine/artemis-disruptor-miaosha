// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   TxAwareOmsAdaptorWrapper.java

package co.fin.intellioms.omsclt.impl;

import co.fin.intellioms.util.Log;
import com.fitechlabs.fin.intellioms.event.*;
import com.fitechlabs.fin.intellioms.omsclt.*;
import com.fitechlabs.fin.intellioms.tx.*;

public class TxAwareOmsAdaptorWrapper
    implements OmsAdaptor
{

    public TxAwareOmsAdaptorWrapper(OmsAdaptor oms)
    {
        this.oms = oms;
    }

    /**
     * @deprecated Method connect is deprecated
     */

    public boolean connect(String url)
        throws OmsAdaptorException
    {
        return oms.connect(url);
    }

    /**
     * @deprecated Method disconnect is deprecated
     */

    public boolean disconnect()
        throws OmsAdaptorException
    {
        return oms.disconnect();
    }

    public boolean submitOrder(final Order ord)
        throws OmsAdaptorException
    {
        try
        {
            TxManager.addCallback(new CommitCallback() {

                public void onCommit()
                {
                    try
                    {
                        oms.submitOrder(ord);
                    }
                    catch(OmsAdaptorException e)
                    {
                        if(log.isError())
                            log.error((new StringBuilder()).append("Unable to submit order to OMS: ").append(ord).toString(), e);
                    }
                }

                final Order val$ord;
                final TxAwareOmsAdaptorWrapper this$0;


            {
                this$0 = TxAwareOmsAdaptorWrapper.this;
                ord = order;
                super();
            }
            }
);
        }
        catch(TxManagerException e)
        {
            throw new OmsAdaptorException((new StringBuilder()).append("Unable to register transaction callback: ").append(e.getMessage()).toString(), e);
        }
        return true;
    }

    public boolean modifyOrder(final Order ord)
        throws OmsAdaptorException
    {
        try
        {
            TxManager.addCallback(new CommitCallback() {

                public void onCommit()
                {
                    try
                    {
                        oms.modifyOrder(ord);
                    }
                    catch(OmsAdaptorException e)
                    {
                        if(log.isError())
                            log.error((new StringBuilder()).append("Unable to modify order in OMS: ").append(ord).toString(), e);
                    }
                }

                final Order val$ord;
                final TxAwareOmsAdaptorWrapper this$0;


            {
                this$0 = TxAwareOmsAdaptorWrapper.this;
                ord = order;
                super();
            }
            }
);
        }
        catch(TxManagerException e)
        {
            throw new OmsAdaptorException((new StringBuilder()).append("Unable to register transaction callback: ").append(e.getMessage()).toString(), e);
        }
        return true;
    }

    public boolean cancelOrder(final Order ord)
        throws OmsAdaptorException
    {
        try
        {
            TxManager.addCallback(new CommitCallback() {

                public void onCommit()
                {
                    try
                    {
                        oms.cancelOrder(ord);
                    }
                    catch(OmsAdaptorException e)
                    {
                        if(log.isError())
                            log.error((new StringBuilder()).append("Unable to modify order in OMS: ").append(ord).toString(), e);
                    }
                }

                final Order val$ord;
                final TxAwareOmsAdaptorWrapper this$0;


            {
                this$0 = TxAwareOmsAdaptorWrapper.this;
                ord = order;
                super();
            }
            }
);
        }
        catch(TxManagerException e)
        {
            throw new OmsAdaptorException((new StringBuilder()).append("Unable to register transaction callback: ").append(e.getMessage()).toString(), e);
        }
        return true;
    }

    public void registerMarketResponseProcessor(MarketResponseProcessor proc)
    {
        oms.registerMarketResponseProcessor(proc);
    }

    public void start()
        throws EventException
    {
        oms.start();
    }

    public void pause()
        throws EventException
    {
        oms.pause();
    }

    public void stop()
        throws EventException
    {
        oms.stop();
    }

    public void onRegister(EventSystem esys)
    {
        oms.onRegister(esys);
    }

    public void raiseEvent(Event evt)
        throws EventException
    {
        oms.raiseEvent(evt);
    }

    public String toString()
    {
        return (new StringBuilder()).append("TxAwareOmsAdaptorWrapper[oms=").append(oms).append("]").toString();
    }

    private final Log log = Log.getLogger(com/ com /fin/intellioms/omsclt/impl/TxAwareOmsAdaptorWrapper);
    private final OmsAdaptor oms;


}
