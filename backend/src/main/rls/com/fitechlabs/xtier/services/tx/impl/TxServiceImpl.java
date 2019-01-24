// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.tx.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jndi.JndiService;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.tx.*;
import com.fitechlabs.xtier.services.tx.adapters.TxXarRecoveryHandlerAdapter;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.tx.impl:
//            TxXmlLogger, TxException, TxRecoveryLogger, TxManagerImpl,
//            TxState, TxUserImpl

public class TxServiceImpl extends ServiceProviderAdapter
    implements TxService
{
    private class XmlAutoRecoveryLog
    {

        String getLogFolder()
        {
            return logFolder;
        }

        void setLogFolder(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                logFolder = s;
                return;
            }
        }

        String getLogFileName()
        {
            return logFileName;
        }

        void setLogFileName(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                logFileName = s;
                return;
            }
        }

        String getLogFileExt()
        {
            return logFileExt;
        }

        void setLogFileExt(String s)
        {
            if(!$assertionsDisabled && logFileName == null)
            {
                throw new AssertionError();
            } else
            {
                logFileExt = s;
                return;
            }
        }

        int getTxPerFile()
        {
            return txPerFile;
        }

        void setTxPerFile(int i)
        {
            if(!$assertionsDisabled && i <= 0)
            {
                throw new AssertionError();
            } else
            {
                txPerFile = i;
                return;
            }
        }

        public String toString()
        {
            return "XML auto-recovery-tlog [tlog-folder=" + logFolder + ", tlog-filename=" + logFileName + ", tlog-ext=" + logFileExt + ", tx-per-file=" + txPerFile + ']';
        }

        private String logFolder;
        private String logFileName;
        private String logFileExt;
        private int txPerFile;
        static final boolean $assertionsDisabled; /* synthetic field */


        private XmlAutoRecoveryLog()
        {
            super();
            logFolder = null;
            logFileName = null;
            logFileExt = null;
            txPerFile = 0;
        }

    }

    private class XmlRecoveryHandler
    {

        String getName()
        {
            return name;
        }

        IocDescriptor getIoc()
        {
            return ioc;
        }

        void setIoc(IocDescriptor iocdescriptor)
        {
            ioc = iocdescriptor;
        }

        public String toString()
        {
            return "XML recovery-handler [ioc=" + ioc + ']';
        }

        private String name;
        private IocDescriptor ioc;

        XmlRecoveryHandler(String s)
        {
            super();
            name = null;
            ioc = null;
            name = s;
        }
    }

    private class XmlXar
    {

        IocDescriptor getIoc()
        {
            return ioc;
        }

        void setIoc(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                ioc = iocdescriptor;
                return;
            }
        }

        public String toString()
        {
            return "XML xar [ioc=" + ioc + ']';
        }

        private IocDescriptor ioc;
        static final boolean $assertionsDisabled; /* synthetic field */


        private XmlXar()
        {
            super();
            ioc = null;
        }

    }

    private class XmlRecoverable
    {

        String getName()
        {
            return name;
        }

        XmlXar getXar()
        {
            return xar;
        }

        void setXar(XmlXar xmlxar)
        {
            if(!$assertionsDisabled && xmlxar == null)
            {
                throw new AssertionError();
            } else
            {
                xar = xmlxar;
                return;
            }
        }

        XmlRecoveryHandler getRecoveryHandler()
        {
            return recoveryHandler;
        }

        void setRecoveryHandler(XmlRecoveryHandler xmlrecoveryhandler)
        {
            if(!$assertionsDisabled && xmlrecoveryhandler == null)
            {
                throw new AssertionError();
            } else
            {
                recoveryHandler = xmlrecoveryhandler;
                return;
            }
        }

        long getTimeout()
        {
            return timeout;
        }

        void setTimeout(long l)
        {
            if(!$assertionsDisabled && l < 0L)
            {
                throw new AssertionError();
            } else
            {
                timeout = l;
                return;
            }
        }

        int getAttempts()
        {
            return attempts;
        }

        void setAttempts(int i)
        {
            if(!$assertionsDisabled && i <= 0)
            {
                throw new AssertionError();
            } else
            {
                attempts = i;
                return;
            }
        }

        boolean isProceedOnFail()
        {
            return proceedOnFail;
        }

        void setProceedOnFail(boolean flag)
        {
            proceedOnFail = flag;
        }

        public String toString()
        {
            return "XML recoverable [name=" + name + ", xar=" + xar + ", recovery-handler=" + recoveryHandler + ", abandon-timeout=" + timeout + ", recover-attempts=" + attempts + ", proceed-if-failed=" + proceedOnFail + ']';
        }

        private String name;
        private XmlXar xar;
        private long timeout;
        private int attempts;
        private boolean proceedOnFail;
        private XmlRecoveryHandler recoveryHandler;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRecoverable(String s)
        {
            super();
            name = null;
            xar = null;
            timeout = 0x337f9800L;
            attempts = 0;
            proceedOnFail = false;
            recoveryHandler = null;
            name = s;
        }
    }

    private class XmlAutoRecovery
    {

        XmlRecoverable getRecoverable(String s)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            else
                return (XmlRecoverable)recoverables.get(s);
        }

        void addRecoverable(XmlRecoverable xmlrecoverable)
        {
            if(!$assertionsDisabled && xmlrecoverable == null)
            {
                throw new AssertionError();
            } else
            {
                recoverables.put(xmlrecoverable.getName(), xmlrecoverable);
                return;
            }
        }

        boolean hasRecoverable(XmlRecoverable xmlrecoverable)
        {
            if(!$assertionsDisabled && xmlrecoverable == null)
                throw new AssertionError();
            else
                return recoverables.containsKey(xmlrecoverable.getName());
        }

        Map getRecoverabes()
        {
            return Collections.unmodifiableMap(recoverables);
        }

        XmlAutoRecoveryLog getAutoRecoveryLog()
        {
            return autoRecoveryLog;
        }

        void setAutoRecoveryLog(XmlAutoRecoveryLog xmlautorecoverylog)
        {
            if(!$assertionsDisabled && xmlautorecoverylog == null)
            {
                throw new AssertionError();
            } else
            {
                autoRecoveryLog = xmlautorecoverylog;
                return;
            }
        }

        boolean isEnabled()
        {
            return enabled;
        }

        void setEnabled(boolean flag)
        {
            enabled = flag;
        }

        public String toString()
        {
            return "XML auto-recovery [enabled=" + enabled + ", recoverables=" + Utils.map2Str(recoverables) + ", auto-recovery-tlog=" + autoRecoveryLog + ']';
        }

        private HashMap recoverables;
        private XmlAutoRecoveryLog autoRecoveryLog;
        private boolean enabled;
        static final boolean $assertionsDisabled; /* synthetic field */


        private XmlAutoRecovery()
        {
            super();
            recoverables = new HashMap();
            enabled = false;
        }

    }

    private class XmlTmJndiBind
    {

        private void setJndiName(String s)
        {
            jndiName = s;
        }

        private String getJndiName()
        {
            return jndiName;
        }

        private void setOverwrite(boolean flag)
        {
            overwrite = flag;
        }

        private boolean isOverwrite()
        {
            return overwrite;
        }

        private boolean overwrite;
        private String jndiName;





        private XmlTmJndiBind()
        {
            super();
        }

    }

    private class XmlRegion
    {

        int getDfltTimeout()
        {
            return dfltTimeout;
        }

        void setDfltTimeout(int i)
        {
            dfltTimeout = i;
        }

        boolean isNativeTm()
        {
            return nativeTmIoc != null || nativeTmJndiName != null;
        }

        String getName()
        {
            return name;
        }

        IocDescriptor getNativeTmIoc()
        {
            return nativeTmIoc;
        }

        void setNativeTmIoc(IocDescriptor iocdescriptor)
        {
            nativeTmIoc = iocdescriptor;
        }

        String getNativeTmJndiName()
        {
            return nativeTmJndiName;
        }

        void setNativeTmJndiName(String s)
        {
            nativeTmJndiName = s;
        }

        String getPoolName()
        {
            return poolName;
        }

        String getPrefix()
        {
            return prefix;
        }

        String getFolder()
        {
            return folder;
        }

        void setFolder(String s)
        {
            folder = s;
        }

        void setPoolName(String s)
        {
            poolName = s;
        }

        void setPrefix(String s)
        {
            prefix = s;
        }

        int getMaxlogSize()
        {
            return maxLogSize;
        }

        void setMaxlogSize(int i)
        {
            maxLogSize = i;
        }

        void addHeuristicExceptionXar(String s)
        {
            exceptionXars.add(s);
        }

        Set getHeuristicExceptionXars()
        {
            return exceptionXars;
        }

        XmlAutoRecovery getAutoRecovery()
        {
            return autoRecovery;
        }

        void setTmJndiBind(XmlTmJndiBind xmltmjndibind)
        {
            if(!$assertionsDisabled && tmBind != null)
            {
                throw new AssertionError();
            } else
            {
                tmBind = xmltmjndibind;
                return;
            }
        }

        XmlTmJndiBind getTmJndiBind()
        {
            return tmBind;
        }

        void setAutoRecovery(XmlAutoRecovery xmlautorecovery)
        {
            if(!$assertionsDisabled && autoRecovery != null)
            {
                throw new AssertionError();
            } else
            {
                autoRecovery = xmlautorecovery;
                return;
            }
        }

        public String toString()
        {
            return "XML region [name=" + name + ", dflt-timeout=" + dfltTimeout + ", native-tm-ioc=" + nativeTmIoc + ", native-jndi-name=" + nativeTmJndiName + ", folder=" + folder + ", prefix=" + prefix + ", pool-name=" + poolName + ", max-log-size=" + maxLogSize + ", auto-recovery=" + autoRecovery + ']';
        }

        void setForgetHeuristic(boolean flag)
        {
            forgetHeuristic = flag;
        }

        boolean isForgetHeuristic()
        {
            return forgetHeuristic;
        }

        private String name;
        private int dfltTimeout;
        private IocDescriptor nativeTmIoc;
        private String nativeTmJndiName;
        private String poolName;
        private String folder;
        private int maxLogSize;
        private String prefix;
        private XmlAutoRecovery autoRecovery;
        private XmlTmJndiBind tmBind;
        private boolean forgetHeuristic;
        private Set exceptionXars;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            folder = null;
            prefix = null;
            autoRecovery = null;
            tmBind = null;
            forgetHeuristic = true;
            exceptionXars = new HashSet();
            name = s;
        }
    }


    public TxServiceImpl()
    {
        isNativeTm = false;
        tXmlLog = null;
        tBinLog = null;
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("tx");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_tx.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.TX.IMPL.ERR10"), saxexception);
        }
        String s = getRegionName();
        XmlRegion xmlregion = (XmlRegion)hashmap.get(s);
        if(xmlregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.TX.IMPL.ERR13", s));
        if(xmlregion.isNativeTm())
        {
            isNativeTm = true;
            String s1 = xmlregion.getNativeTmJndiName();
            if(s1 != null)
            {
                try
                {
                    xaTm = (TransactionManager)XtierKernel.getInstance().jndi().getInitialContext().lookup(s1);
                }
                catch(NamingException namingexception)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.TX.IMPL.ERR22", s1), namingexception);
                }
            } else
            {
                IocDescriptor iocdescriptor = xmlregion.getNativeTmIoc();
                if(!$assertionsDisabled && iocdescriptor == null)
                    throw new AssertionError();
                try
                {
                    xaTm = (TransactionManager)iocdescriptor.createNewObj(javax.transaction.TransactionManager.class);
                }
                catch(IocDescriptorException iocdescriptorexception)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.TX.IMPL.ERR23", iocdescriptor), iocdescriptorexception);
                }
            }
            log.log(L10n.format("SRVC.TX.IMPL.LOG2", xaTm.getClass().getName()));
        } else
        {
            isNativeTm = false;
            String s2 = xmlregion.getFolder();
            if(s2 == null)
                s2 = Utils.makeValidPath(getXtierRoot(), "config/tlog");
            String s3 = xmlregion.getPrefix();
            if(s3 == null)
                s3 = "";
            try
            {
                tXmlLog = new TxXmlLogger(s2, s3, xmlregion.getMaxlogSize(), log);
            }
            catch(TxException txexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.TX.ERR51"), txexception);
            }
            dfltHandler = new TxXarRecoveryHandlerAdapter();
            XmlAutoRecovery xmlautorecovery = xmlregion.getAutoRecovery();
            XmlAutoRecoveryLog xmlautorecoverylog = xmlautorecovery.getAutoRecoveryLog();
            try
            {
                tBinLog = new TxRecoveryLogger(xmlautorecoverylog.getLogFolder(), xmlautorecoverylog.getLogFileName(), xmlautorecoverylog.getLogFileExt(), xmlautorecoverylog.getTxPerFile(), log);
            }
            catch(TxException txexception1)
            {
                throw new ServiceProviderException(L10n.format("SRVC.TX.ERR51"), txexception1);
            }
            if(xmlautorecovery.isEnabled())
                recover(xmlautorecovery);
            try
            {
                xaTm = TxManagerImpl.newInstance(log, xmlregion.getPoolName(), tXmlLog, tBinLog, xmlregion.isForgetHeuristic(), xmlregion.getHeuristicExceptionXars());
            }
            catch(TxException txexception2)
            {
                throw new ServiceProviderException(L10n.format("SRVC.TX.ERR52"), txexception2);
            }
            ((TxManagerImpl)xaTm).setDefaultTimeout(xmlregion.getDfltTimeout());
            XmlTmJndiBind xmltmjndibind = xmlregion.getTmJndiBind();
            if(xmltmjndibind != null)
                try
                {
                    XtierKernel.getInstance().jndi().bindEx(xmltmjndibind.getJndiName(), xaTm, xmltmjndibind.isOverwrite());
                }
                catch(NamingException namingexception1)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.TX.ERR67"), namingexception1);
                }
            log.log(L10n.format("SRVC.TX.IMPL.LOG1", xaTm.getClass().getName()));
        }
        if(!$assertionsDisabled && xaTm == null)
        {
            throw new AssertionError();
        } else
        {
            xaTx = TxUserImpl.newInstance(xaTm);
            return;
        }
    }

    public TransactionManager getTransactionManager()
    {
        return xaTm;
    }

    public UserTransaction userTx()
    {
        return xaTx;
    }

    public String getName()
    {
        return "tx";
    }

    protected void onStop()
        throws ServiceProviderException
    {
        if(!isNativeTm)
            TxManagerImpl.shutdown();
        tXmlLog.closeWriter();
        tBinLog.stop();
        TxUserImpl.shutdown();
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && (s == null || map == null || set == null))
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.TX.IMPL.WRN2", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(map, set) {

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(isNativeTmIoc)
                        region.setNativeTmIoc(iocdescriptor);
                    else
                    if(xar != null)
                        xar.setIoc(iocdescriptor);
                    else
                    if(recoveryHandler != null)
                        recoveryHandler.setIoc(iocdescriptor);
                }

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.TX.IMPL.ERR12", s3));
                        region = new XmlRegion(s3);
                    } else
                    if(s2.equals("native-tm"))
                        isNativeTmIoc = true;
                    else
                    if(s2.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), regions, includes);
                    else
                    if(s2.equals("auto-recovery"))
                    {
                        if(!$assertionsDisabled && autoRecovery != null)
                            throw new AssertionError();
                        autoRecovery = new XmlAutoRecovery();
                        String s4 = xmlattrinterceptor.getValue("enabled");
                        if(s4 != null)
                            autoRecovery.setEnabled(parseBoolean(s4));
                    } else
                    if(s2.equals("recoverable"))
                    {
                        if(!$assertionsDisabled && recoverable != null)
                            throw new AssertionError();
                        String s5 = xmlattrinterceptor.getValue("name");
                        recoverable = new XmlRecoverable(s5);
                    } else
                    if(s2.equals("xar"))
                    {
                        if(!$assertionsDisabled && xar != null)
                            throw new AssertionError();
                        xar = new XmlXar();
                    } else
                    if(s2.equals("recovery-handler"))
                    {
                        if(!$assertionsDisabled && recoveryHandler != null)
                            throw new AssertionError();
                        String s6 = xmlattrinterceptor.getValue("name");
                        recoveryHandler = new XmlRecoveryHandler(s6);
                    } else
                    if(s2.equals("bind-to-jndi"))
                    {
                        tmJndiBind = new XmlTmJndiBind();
                        region.setTmJndiBind(tmJndiBind);
                        String s7 = xmlattrinterceptor.getValue("overwrite");
                        if(s7 != null)
                            tmJndiBind.setOverwrite(parseBoolean(s7));
                    } else
                    if(s2.equals("forget-heuristic"))
                    {
                        String s8 = xmlattrinterceptor.getValue("default");
                        if(s8 != null)
                            region.setForgetHeuristic(parseBoolean(s8));
                    } else
                    if(s2.equals("auto-recovery-tlog"))
                    {
                        if(!$assertionsDisabled && autoRecoveryLog != null)
                            throw new AssertionError();
                        autoRecoveryLog = new XmlAutoRecoveryLog();
                    }
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("native-tm"))
                        isNativeTmIoc = false;
                    else
                    if(s2.equals("jndi-tm-name"))
                        region.setNativeTmJndiName(getPcdata());
                    else
                    if(s2.equals("tm-log-folder"))
                        region.setFolder(getPcdata());
                    else
                    if(s2.equals("tm-log-file-prefix"))
                        region.setPrefix(getPcdata());
                    else
                    if(s2.equals("tm-max-log-file-size"))
                    {
                        int i = parseInt(getPcdata());
                        if(i <= 0)
                            throw createSaxErr(L10n.format("SRVC.TX.IMPL.ERR26", new Integer(i)));
                        region.setMaxlogSize(i);
                    } else
                    if(s2.equals("tx-thread-pool-name"))
                        region.setPoolName(getPcdata());
                    else
                    if(s2.equals("tx-default-timeout-secs"))
                    {
                        int j = parseInt(getPcdata());
                        if(j < 0)
                            throw createSaxErr(L10n.format("SRVC.TX.IMPL.ERR21", new Integer(j)));
                        region.setDfltTimeout(j);
                    } else
                    if(s2.equals("auto-recovery"))
                    {
                        region.setAutoRecovery(autoRecovery);
                        autoRecovery = null;
                    } else
                    if(s2.equals("auto-recovery-tlog"))
                    {
                        autoRecovery.setAutoRecoveryLog(autoRecoveryLog);
                        autoRecoveryLog = null;
                    } else
                    if(s2.equals("tlog-folder"))
                        autoRecoveryLog.setLogFolder(getPcdata());
                    else
                    if(s2.equals("tlog-filename"))
                        autoRecoveryLog.setLogFileName(getPcdata());
                    else
                    if(s2.equals("tlog-ext"))
                        autoRecoveryLog.setLogFileExt(getPcdata());
                    else
                    if(s2.equals("tx-per-file"))
                    {
                        int k = parseInt(getPcdata());
                        autoRecoveryLog.setTxPerFile(k);
                    } else
                    if(s2.equals("recoverable"))
                    {
                        if(autoRecovery.hasRecoverable(recoverable))
                            throw createSaxErr(L10n.format("SRVC.TX.ERR57", recoverable.getName(), region.getName()));
                        autoRecovery.addRecoverable(recoverable);
                        recoverable = null;
                    } else
                    if(s2.equals("xar"))
                    {
                        recoverable.setXar(xar);
                        xar = null;
                    } else
                    if(s2.equals("recovery-handler"))
                    {
                        recoverable.setRecoveryHandler(recoveryHandler);
                        recoveryHandler = null;
                    } else
                    if(s2.equals("bind-to-jndi"))
                    {
                        if(!$assertionsDisabled && tmJndiBind == null)
                            throw new AssertionError();
                        String s3 = getPcdata();
                        tmJndiBind.setJndiName(s3);
                    } else
                    if(s2.equals("abandon-timeout"))
                    {
                        if(!$assertionsDisabled && recoverable == null)
                            throw new AssertionError();
                        String s4 = getPcdata();
                        long l = parseLong(s4);
                        if(l < 0L)
                            throw createSaxErr(L10n.format("SRVC.TX.ERR58", s4, recoverable.getName()));
                        recoverable.setTimeout(l);
                    } else
                    if(s2.equals("recover-attempts"))
                    {
                        if(!$assertionsDisabled && recoverable == null)
                            throw new AssertionError();
                        String s5 = getPcdata();
                        int i1 = parseInt(s5);
                        if(i1 < 1)
                            throw createSaxErr(L10n.format("SRVC.TX.ERR59", s5, recoverable.getName()));
                        recoverable.setAttempts(i1);
                    } else
                    if(s2.equals("proceed-if-failed"))
                        recoverable.setProceedOnFail(parseBoolean(getPcdata()));
                    else
                    if(s2.equals("exclude-xar"))
                        region.addHeuristicExceptionXar(getPcdata());
                }

                private XmlRegion region;
                private XmlAutoRecovery autoRecovery;
                private XmlRecoverable recoverable;
                private XmlXar xar;
                private XmlRecoveryHandler recoveryHandler;
                private XmlAutoRecoveryLog autoRecoveryLog;
                private XmlTmJndiBind tmJndiBind;
                private boolean isNativeTmIoc;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                region = null;
                autoRecovery = null;
                recoverable = null;
                xar = null;
                recoveryHandler = null;
                autoRecoveryLog = null;
                tmJndiBind = null;
                isNativeTmIoc = false;
            }
            }
);
        }
        catch(ParserConfigurationException parserconfigurationexception)
        {
            throw new SAXException(parserconfigurationexception);
        }
        catch(IOException ioexception)
        {
            throw new SAXException(L10n.format("SRVC.TX.IMPL.ERR18", s1), ioexception);
        }
    }

    private void recover(XmlAutoRecovery xmlautorecovery)
        throws ServiceProviderException
    {
        if(!$assertionsDisabled && xmlautorecovery == null)
            throw new AssertionError();
        Map map = null;
        try
        {
            map = tBinLog.getTxStates(true);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
            Utils.debug("Error reading transaction log: " + ioexception.getMessage());
        }
        HashSet hashset = new HashSet(map.size());
        if(map.size() == 0)
            return;
        for(Iterator iterator = xmlautorecovery.getRecoverabes().values().iterator(); iterator.hasNext();)
        {
            XmlRecoverable xmlrecoverable = (XmlRecoverable)iterator.next();
            TxXarRecoverable txxarrecoverable = null;
            IocDescriptor iocdescriptor = xmlrecoverable.getXar().getIoc();
            try
            {
                txxarrecoverable = (TxXarRecoverable)iocdescriptor.createNewObj(com.fitechlabs.xtier.services.tx.TxXarRecoverable.class);
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.TX.ERR55", iocdescriptor), iocdescriptorexception);
            }
            Object obj = null;
            IocDescriptor iocdescriptor1 = xmlrecoverable.getRecoveryHandler().getIoc();
            if(iocdescriptor1 != null)
                try
                {
                    obj = (TxXarRecoveryHandler)iocdescriptor1.createNewObj(com.fitechlabs.xtier.services.tx.TxXarRecoveryHandler.class);
                }
                catch(IocDescriptorException iocdescriptorexception1)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.TX.ERR56", iocdescriptor), iocdescriptorexception1);
                }
            else
                obj = dfltHandler;
            String s = xmlrecoverable.getName();
            long l = xmlrecoverable.getTimeout();
            int i = xmlrecoverable.getAttempts();
            boolean flag = xmlrecoverable.isProceedOnFail();
            if(!$assertionsDisabled && l < 0L)
                throw new AssertionError();
            if(!$assertionsDisabled && i < 0)
                throw new AssertionError();
            if(i == 0)
                log.warning(L10n.format("SRVC.TX.WRN13", xmlrecoverable));
            boolean flag1 = false;
            int j = 0;
            do
            {
                if(j >= i)
                    break;
                XAResource xaresource = null;
                Xid axid[] = null;
                xaresource = txxarrecoverable.getXar(s);
                if(xaresource == null)
                    break;
                try
                {
                    axid = xaresource.recover(0x1800000);
                }
                catch(XAException xaexception)
                {
                    break;
                }
                if(!$assertionsDisabled && axid == null)
                    throw new AssertionError();
                int k = axid.length;
                if(k == 0)
                    break;
                boolean flag2 = false;
                for(int i1 = 0; i1 < k; i1++)
                {
                    Xid xid = axid[i1];
                    TxState txstate = (TxState)map.get(new String(xid.getGlobalTransactionId()));
                    if(txstate == null)
                        continue;
                    long l1 = txstate.getTimestamp();
                    if(l != 0L && System.currentTimeMillis() - l1 > l)
                    {
                        try
                        {
                            xaresource.forget(xid);
                        }
                        catch(XAException xaexception1) { }
                        continue;
                    }
                    int j1 = txstate.getStatus();
                    byte byte0 = 0;
                    if(j1 == 5)
                        byte0 = 2;
                    else
                    if(j1 == 2)
                        byte0 = 1;
                    else
                    if(j1 == 1)
                        byte0 = 3;
                    else
                        throw new ServiceProviderException(L10n.format("SRVC.TX.ERR68", new Integer(j1)));
                    try
                    {
                        ((TxXarRecoveryHandler) (obj)).handleRecovery(xid, xaresource, byte0);
                        hashset.add(new String(xid.getGlobalTransactionId()));
                    }
                    catch(XAException xaexception2)
                    {
                        flag2 = true;
                    }
                }

                if(flag2)
                {
                    Integer integer = new Integer(j + 1);
                    Integer integer1 = new Integer(i - integer.intValue());
                    flag1 = true;
                    log.warning(L10n.format("SRVC.TX.WRN11", xmlrecoverable, integer, integer1));
                } else
                {
                    flag1 = false;
                    break;
                }
                j++;
            } while(true);
            if(flag1)
            {
                if(flag)
                    log.warning(L10n.format("SRVC.TX.WRN12", xmlrecoverable));
                else
                    throw new ServiceProviderException(L10n.format("SRVC.TX.ERR63", xmlrecoverable));
            } else
            {
                log.info(L10n.format("SRVC.TX.IMPL.INFO1", xmlrecoverable));
            }
        }

        tBinLog.cleanLogs(hashset);
    }

    private static final String DFLT_TMLOG_PREFIX = "";
    private static final String DFLT_TMLOG_FOLDER = "config/tlog";
    private Logger log;
    private UserTransaction xaTx;
    private TransactionManager xaTm;
    private boolean isNativeTm;
    private TxXmlLogger tXmlLog;
    private TxRecoveryLogger tBinLog;
    private TxXarRecoveryHandlerAdapter dfltHandler;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(TxServiceImpl.class).desiredAssertionStatus();
    }

}
