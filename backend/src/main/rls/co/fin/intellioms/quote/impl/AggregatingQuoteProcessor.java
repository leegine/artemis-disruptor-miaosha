//// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//// Jad home page: http://www.kpdus.com/jad.html
//// Decompiler options: packimports(3)
//// Source File Name:   AggregatingQuoteProcessor.java
//
//package com.com.fin.intellioms.quote.impl;
//
//import com.com.fin.intellioms.omsclt.Price;
//import com.com.fin.intellioms.quote.*;
//import com.com.fin.intellioms.ticker.Ticker;
//import com.com.fin.intellioms.util.*;
//import com.com.xtier.threads.SysThread;
//import java.util.*;
//
//// Referenced classes of package com.com.fin.intellioms.quote.impl:
////            DefaultQuoteProcessor, QuoteImpl
//
//public class AggregatingQuoteProcessor
//    implements QuoteProcessor, Startable
//{
//    private class Notifier extends SysThread
//    {
//
//        void enqueue(Quote qt)
//        {
//            synchronized(localQueue)
//            {
//                if(!localQueue.contains(qt))
//                    localQueue.add(qt);
//            }
//        }
//
//        protected void body()
//        {
//_L3:
//label0:
//            {
//                checkInterrupted();
//                try
//                {
//                    sleep(interval);
//                }
//                catch(InterruptedException e) { }
//                synchronized(localQueue)
//                {
//                    if(!localQueue.isEmpty())
//                        break label0;
//                }
//                continue; /* Loop/switch isn't completed */
//            }
//            tempQueue.addAll(localQueue);
//            localQueue.clear();
////            set;
////            JVM INSTR monitorexit ;
////              goto _L1
////            exception;
////            throw exception;
////_L1:
//            Quote aggregate;
//            for(Iterator iter = tempQueue.iterator(); iter.hasNext(); nestedProcessor.processQuote(aggregate))
//            {
//                checkInterrupted();
//                aggregate = (Quote)iter.next();
//            }
//
//            tempQueue.clear();
////            if(true) goto _L3; else goto _L2
////_L2:
////        }
//
////        protected void cleanup()
////        {
////            super.cleanup();
////            synchronized(localQueue)
////            {
////                localQueue.clear();
////            }
////        }
//
//        private final Set localQueue = new HashSet();
//        private final Set tempQueue = new HashSet();
//        final AggregatingQuoteProcessor this$0;
//
//        public Notifier()
//        {
//            this$0 = AggregatingQuoteProcessor.this;
//            super("AggregatingQuoteProcessorNotifier");
//        }
//    }
//
//
//    public AggregatingQuoteProcessor(long interval, int notifierThreadsCount)
//    {
//        this(interval, notifierThreadsCount, ((QuoteProcessor) (new DefaultQuoteProcessor())), 16);
//    }
//
//    public AggregatingQuoteProcessor(long interval, int notifierThreadsCount, QuoteProcessor nested)
//    {
//        this(interval, notifierThreadsCount, nested, 16);
//    }
//
//    public AggregatingQuoteProcessor(long interval, int notifierThreadsCount, QuoteProcessor nested, int multiplicity)
//    {
//        state = new ServiceState("AggregatingQuoteProcessor");
//        if(!$assertionsDisabled && multiplicity <= 0)
//            throw new AssertionError("Parallesim must be above zero");
//        if(!$assertionsDisabled && interval <= 0L)
//            throw new AssertionError("Interval must be above zero.");
//        if(!$assertionsDisabled && notifierThreadsCount <= 0)
//            throw new AssertionError("Notifier threads count must be above zero.");
//        if(!$assertionsDisabled && nested == null)
//            throw new AssertionError("Nested processor is null.");
//        this.interval = interval;
//        this.multiplicity = multiplicity;
//        this.notifierThreadsCount = notifierThreadsCount;
//        nestedProcessor = nested;
//        quotes = new Map[multiplicity];
//        for(int i = 0; i < quotes.length; i++)
//            quotes[i] = new HashMap();
//
//    }
//
//    public void onRegister(QuoteFeederAdaptor adaptor)
//    {
//        nestedProcessor.onRegister(adaptor);
//    }
//
//    public void processQuote(Quote qt)
//    {
//        if(!$assertionsDisabled && !state.isStarted())
//            throw new AssertionError("AggregatingQuoteProcessor not started.");
//        if(!$assertionsDisabled && qt == null)
//            throw new AssertionError("Quote is null.");
//        if(!$assertionsDisabled && qt.getLastPrice() == null)
//            throw new AssertionError("Quote.lastPrice is null.");
//        int hash = qt.getTicker().hashCode();
//        Map block = quotes[hash % multiplicity];
//        QuoteImpl aggregate;
//        synchronized(block)
//        {
//            aggregate = (QuoteImpl)block.get(qt.getTicker());
//            if(aggregate == null)
//            {
//                aggregate = new QuoteImpl(qt.getTicker(), qt.getBasePrice());
//                block.put(aggregate.getTicker(), aggregate);
//            }
//            aggregate.setAskPrice(qt.getAskPrice());
//            aggregate.setBidPrice(qt.getBidPrice());
//            aggregate.setLastPrice(qt.getLastPrice());
//            aggregate.setOpenPrice(qt.getOpenPrice());
//            if(aggregate.getLowPrice() == null || aggregate.getLowPrice().isAbove(qt.getLastPrice()))
//                aggregate.setLowPrice(qt.getLowPrice());
//            if(aggregate.getHighPrice() == null || aggregate.getHighPrice().isBelow(qt.getLastPrice()))
//                aggregate.setHighPrice(qt.getLastPrice());
//        }
//        Notifier notifier = notifiers[hash % notifierThreadsCount];
//        notifier.enqueue(aggregate);
//    }
//
//    public void start()
//    {
//        state.checkNotStarted();
//        notifiers = new Notifier[notifierThreadsCount];
//        for(int i = 0; i < notifiers.length; i++)
//        {
//            notifiers[i] = new Notifier();
//            notifiers[i].start();
//        }
//
//        if(log.isInfo())
//            log.info("Started.");
//        state.start();
//    }
//
//    public void stop()
//    {
//        state.checkStarted();
//        if(log.isInfo())
//            log.info("Stopping.");
//        for(int i = 0; i < notifiers.length; i++)
//        {
//            notifiers[i] = new Notifier();
//            notifiers[i].safeStop();
//        }
//
//        notifiers = null;
//        for(int i = 0; i < quotes.length; i++)
//            quotes[i].clear();
//
////        if(log.isInfo())
////            log.info("Stopped.");
//        state.stop();
//    }
//
//    public static final int DEFAULT_MULTIPLICITY = 16;
////    private static final Log log = Log.getLogger(com/com/fin/intellioms/quote/impl/AggregatingQuoteProcessor);
//    private final QuoteProcessor nestedProcessor;
//    private final Map quotes[];
//    private final long interval;
//    private final int multiplicity;
//    private Notifier notifiers[];
//    private final int notifierThreadsCount;
//    private final ServiceState state;
////    static final boolean $assertionsDisabled = !com/com/fin/intellioms/quote/impl/AggregatingQuoteProcessor.desiredAssertionStatus();
//
//
//
//}
