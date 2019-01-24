// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MultiThreadQuoteProcessor.java

package co.fin.intellioms.quote.impl;

import co.fin.intellioms.quote.Quote;
import co.fin.intellioms.quote.QuoteFeederAdaptor;
import co.fin.intellioms.quote.QuoteProcessor;
import co.fin.intellioms.util.ServiceState;
import co.fin.intellioms.util.Startable;


// Referenced classes of package com.com.fin.intellioms.quote.impl:
//            DefaultQuoteProcessor

public class MultiThreadQuoteProcessor
    implements QuoteProcessor, Startable
{
    private class Worker extends SysThread
    {

        void addQuote(Quote qt)
        {
            synchronized(queue)
            {
                while(queue.getCount() >= maxQueueSize)
                    try
                    {
                        queue.wait();
                    }
                    catch(InterruptedException e) { }
                queue.add(qt);
                queue.notifyAll();
            }
        }

        protected void body()
        {
            do
            {
                Quote qt;
                do
                {
                    checkInterrupted();
                    qt = null;
                    synchronized(queue)
                    {
                        if(!queue.isEmpty())
                            qt = (Quote)queue.get();
                        else
                            try
                            {
                                queue.wait();
                            }
                            catch(InterruptedException e) { }
                    }
                } while(qt == null);
                if(!$assertionsDisabled && nestedProcessor == null)
                    throw new AssertionError("Nested quote processor is null.");
                nestedProcessor.processQuote(qt);
                synchronized(queue)
                {
                    queue.notifyAll();
                }
            } while(true);
        }

        protected void cleanup()
        {
            super.cleanup();
            synchronized(queue)
            {
                queue.clear();
            }
        }

        private FifoQueue queue;
//        static final boolean $assertionsDisabled = !com/ com /fin/intellioms/quote/impl/MultiThreadQuoteProcessor.desiredAssertionStatus();
        final MultiThreadQuoteProcessor this$0;


        Worker()
        {
            this$0 = MultiThreadQuoteProcessor.this;
//            super("QuoteProcessorWorker");
            queue = new FifoQueue();
        }
    }


    public MultiThreadQuoteProcessor(int threadsCount, int maxQueueSize)
    {
        this(threadsCount, maxQueueSize, ((QuoteProcessor) (new DefaultQuoteProcessor())));
    }

    public MultiThreadQuoteProcessor(int threadsCount, int maxQueueSize, QuoteProcessor nestedProcessor)
    {
        state = new ServiceState("MultiThreadQuoteProcessor");
        if(!$assertionsDisabled && threadsCount <= 0)
            throw new AssertionError("Threads number must be above zero.");
        if(!$assertionsDisabled && nestedProcessor == null)
        {
            throw new AssertionError("Nested proccessor is null.");
        } else
        {
            this.threadsCount = threadsCount;
            this.nestedProcessor = nestedProcessor;
            this.maxQueueSize = maxQueueSize;
            return;
        }
    }

    public void onRegister(QuoteFeederAdaptor adaptor)
    {
        nestedProcessor.onRegister(adaptor);
    }

    public void processQuote(Quote qt)
    {
        if(!$assertionsDisabled && !state.isStarted())
        {
            throw new AssertionError("Not started");
        } else
        {
            worker[qt.getTicker().hashCode() % threadsCount].addQuote(qt);
            return;
        }
    }

    public QuoteProcessor getNestedProcessor()
    {
        return nestedProcessor;
    }

    public int getThreadsCount()
    {
        return threadsCount;
    }

    public void start()
    {
        state.checkNotStarted();
        worker = new Worker[threadsCount];
        for(int i = 0; i < worker.length; i++)
        {
            worker[i] = new Worker();
            worker[i].start();
        }

        state.start();
    }

    public void stop()
    {
        state.checkStarted();
        for(int i = 0; i < worker.length; i++)
            worker[i].safeStop();

        worker = null;
        state.stop();
    }

    QuoteFeederAdaptor adaptor;
    private final int threadsCount;
    private QuoteProcessor nestedProcessor;
    private Worker worker[];
    private ServiceState state;
    private final int maxQueueSize;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/quote/impl/MultiThreadQuoteProcessor.desiredAssertionStatus();



}
