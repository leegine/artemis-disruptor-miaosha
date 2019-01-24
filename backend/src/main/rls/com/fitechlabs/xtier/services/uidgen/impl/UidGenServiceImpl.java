// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.uidgen.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.uidgen.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.uidgen.impl:
//            Uid8Gen, UidSeqImpl, UidImpl

public class UidGenServiceImpl extends ServiceProviderAdapter
    implements UidGenService
{
    private class XmlSeq
    {

        String getName()
        {
            return name;
        }

        void setSink(IocDescriptor iocdescriptor)
        {
            sink = iocdescriptor;
        }

        IocDescriptor getSink()
        {
            return sink;
        }

        long getStart()
        {
            return start;
        }

        long getEnd()
        {
            return end;
        }

        long getStep()
        {
            return step;
        }

        boolean getCycle()
        {
            return cycle;
        }

        int getSaveFreq()
        {
            return saveFreq;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML sequence [name=" + name + ", start=" + start + ", end=" + end + "step=" + step + ", cycle=" + cycle + ", save-freq=" + saveFreq + ']';
        }

        private String name;
        private long start;
        private long end;
        private long step;
        private boolean cycle;
        private int saveFreq;
        private IocDescriptor sink;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlSeq(String s, long l, long l1, long l2, boolean flag, int i)
        {
            super();
            name = s;
            start = l;
            end = l1;
            step = l2;
            cycle = flag;
            saveFreq = i;
        }
    }

    private class XmlRegion
    {

        void addSeq(XmlSeq xmlseq)
        {
            xmlSeqs.put(xmlseq.getName(), xmlseq);
        }

        boolean containsSeq(String s)
        {
            return xmlSeqs.containsKey(s);
        }

        Map getSeqs()
        {
            return xmlSeqs;
        }

        String getName()
        {
            return name;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML region [name=" + name + ", seqs=" + Utils.map2Str(xmlSeqs) + ']';
        }

        private Map xmlSeqs;
        private String name;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            xmlSeqs = new HashMap();
            name = s;
        }
    }


    public UidGenServiceImpl()
    {
        seqs = new HashMap();
        logger = null;
        random = new Random();
        vmId = new byte[4];
        ipAddr = null;
        uid8 = null;
    }

    public String getName()
    {
        return "uidgen";
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            logger.warning(L10n.format("SRVC.UIDGEN.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(map, set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.UIDGEN.ERR12", s3));
                        region = new XmlRegion(s3);
                    } else
                    if(s2.equals("include"))
                    {
                        String s4 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s4, regions, includes);
                    } else
                    if(s2.equals("seq"))
                    {
                        String s5 = xmlattrinterceptor.getValue("name");
                        if(region.containsSeq(s5))
                            throw createSaxErr(L10n.format("SRVC.UIDGEN.ERR1", region.getName(), s5));
                        long l = parseLong(xmlattrinterceptor.getValue("start"));
                        if(l == 0x8000000000000000L)
                            throw createSaxErr(L10n.format("SRVC.UIDGEN.ERR17"));
                        long l1 = parseLong(xmlattrinterceptor.getValue("end"));
                        long l2 = parseLong(xmlattrinterceptor.getValue("step"));
                        boolean flag = parseBoolean(xmlattrinterceptor.getValue("cycle"));
                        String s6 = xmlattrinterceptor.getValue("freq");
                        int i = s6 != null ? parseInt(s6) : 10;
                        long l3 = Math.max(Math.abs(l), Math.abs(l1));
                        long l4 = Math.min(Math.abs(l), Math.abs(l1));
                        long l5 = Math.abs(l2);
                        boolean flag1 = l >= 0L;
                        boolean flag2 = l1 >= 0L;
                        if(l2 == 0L || i <= 0 || l3 - l4 < l5 && (flag1 == flag2 || flag1 != flag2 && l3 < l5 - l4))
                            throw createSaxErr(L10n.format("SRVC.UIDGEN.ERR2", s5));
                        seq = new XmlSeq(s5, l, l1, l2, flag, i);
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(seq != null)
                        seq.setSink(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("seq"))
                    {
                        region.addSeq(seq);
                        seq = null;
                    }
                }

                private XmlSeq seq;
                private XmlRegion region;


                throws SAXException
            {
                super(final_s, final_s1);
                seq = null;
                region = null;
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
            throw new SAXException(L10n.format("SRVC.UIDGEN.ERR3", s1), ioexception);
        }
    }

    protected void onStart()
        throws ServiceProviderException
    {
        logger = XtierKernel.getInstance().log().getLogger("uidgen");
        uid8 = new Uid8Gen();
        setBytesInt(vmId, 0, (new Object()).hashCode());
        if(ipAddr == null)
            try
            {
                byte abyte0[] = InetAddress.getLocalHost().getAddress();
                if(abyte0.length == 4)
                    ipAddr = abyte0;
                else
                if(abyte0.length == 16)
                {
                    ipAddr[0] = (byte)(abyte0[0] ^ abyte0[4] & abyte0[8] ^ abyte0[12]);
                    ipAddr[1] = (byte)(abyte0[1] ^ abyte0[5] & abyte0[9] ^ abyte0[13]);
                    ipAddr[2] = (byte)(abyte0[2] ^ abyte0[6] & abyte0[10] ^ abyte0[14]);
                    ipAddr[3] = (byte)(abyte0[3] ^ abyte0[7] & abyte0[11] ^ abyte0[15]);
                }
            }
            catch(UnknownHostException unknownhostexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.UIDGEN.ERR13", unknownhostexception.getMessage()), unknownhostexception);
            }
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_uidgen.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.UIDGEN.ERR4"), saxexception);
        }
        XmlRegion xmlregion = (XmlRegion)hashmap.get(getRegionName());
        if(xmlregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.UIDGEN.ERR5", getRegionName()));
        UidSeqImpl uidseqimpl;
        for(Iterator iterator = xmlregion.getSeqs().values().iterator(); iterator.hasNext(); seqs.put(uidseqimpl.getName(), uidseqimpl))
        {
            XmlSeq xmlseq = (XmlSeq)iterator.next();
            UidSeqSink uidseqsink = null;
            IocDescriptor iocdescriptor = xmlseq.getSink();
            if(iocdescriptor != null)
                try
                {
                    uidseqsink = (UidSeqSink)xmlseq.getSink().createNewObj(com.fitechlabs.xtier.services.uidgen.UidSeqSink.class);
                }
                catch(IocDescriptorException iocdescriptorexception)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.UIDGEN.ERR6", iocdescriptorexception.getMessage()), iocdescriptorexception);
                }
            uidseqimpl = new UidSeqImpl(xmlseq.getName(), xmlseq.getStart(), xmlseq.getEnd(), xmlseq.getCycle(), xmlseq.getStep(), uidseqsink, xmlseq.getSaveFreq());
            if(uidseqsink == null)
                continue;
            try
            {
                Long long1 = uidseqsink.load(xmlseq.getName());
                if(long1 == null)
                    continue;
                long l = long1.longValue();
                if(!uidseqimpl.isCycle() && (l == 0x7fffffffffffffffL || l == 0x8000000000000000L))
                    throw new UidSeqException(L10n.format("SRVC.UIDGEN.ERR14", uidseqimpl, new Long(l)));
                uidseqimpl.reset(l);
            }
            catch(UidSeqException uidseqexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.UIDGEN.ERR7", uidseqimpl), uidseqexception);
            }
        }

    }

    private void stopSeq(UidSeqImpl uidseqimpl)
        throws UidSeqException
    {
        UidSeqSink uidseqsink = uidseqimpl.getSink();
        if(uidseqsink != null)
        {
            long l = uidseqimpl.getNext();
            uidseqsink.save(uidseqimpl.getName(), l);
            uidseqsink.close();
        }
    }

    protected void onStop()
        throws ServiceProviderException
    {
        Object obj = null;
        Iterator iterator = seqs.values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            try
            {
                stopSeq((UidSeqImpl)iterator.next());
            }
            catch(UidSeqException uidseqexception)
            {
                if(obj == null)
                    obj = uidseqexception;
            }
        } while(true);
        seqs.clear();
        if(obj != null)
            throw new ServiceProviderException(L10n.format("SRVC.UIDGEN.ERR8", ((UidSeqException) (obj)).getMessage()), ((Throwable) (obj)));
        else
            return;
    }

    private int setBytesInt(byte abyte0[], int i, int j)
    {
        abyte0[i++] = (byte)(j >>> 24);
        abyte0[i++] = (byte)(j >>> 16);
        abyte0[i++] = (byte)(j >>> 8);
        abyte0[i++] = (byte)j;
        return i;
    }

    public Uid makeWanUid()
    {
        byte abyte0[] = new byte[32];
        putWanUid(abyte0, 0);
        return new UidImpl(abyte0);
    }

    public int putWanUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 32, "arr", "arr.length >= 32");
        ArgAssert.illegalRange(abyte0.length - i >= 32, "off", "arr.length - off >= 32");
        i = putLanUid(abyte0, i);
        i = setBytesInt(abyte0, i, (new Object()).hashCode());
        i = setBytesInt(abyte0, i, random.nextInt());
        return i;
    }

    public Uid makeLanUid()
    {
        byte abyte0[] = new byte[24];
        putLanUid(abyte0, 0);
        return new UidImpl(abyte0);
    }

    public int putLanUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 24, "arr", "arr.length >= 24");
        ArgAssert.illegalRange(abyte0.length - i >= 24, "off", "arr.length - off >= 24");
        i = putHostUid(abyte0, i);
        System.arraycopy(ipAddr, 0, abyte0, i, ipAddr.length);
        i += ipAddr.length;
        i = setBytesInt(abyte0, i, random.nextInt());
        return i;
    }

    public Uid makeHostUid()
    {
        byte abyte0[] = new byte[16];
        putHostUid(abyte0, 0);
        return new UidImpl(abyte0);
    }

    public int putHostUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 16, "arr", "arr.length >= 16");
        ArgAssert.illegalRange(abyte0.length - i >= 16, "off", "arr.length - off >= 16");
        synchronized(uid8)
        {
            i = uid8.setUid8(abyte0, i);
        }
        System.arraycopy(vmId, 0, abyte0, i, vmId.length);
        i += vmId.length;
        i = setBytesInt(abyte0, i, random.nextInt());
        return i;
    }

    public Uid makeVmUid()
    {
        byte abyte0[] = new byte[8];
        putVmUid(abyte0, 0);
        return new UidImpl(abyte0);
    }

    public int putVmUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 8, "arr", "arr.length >= 8");
        ArgAssert.illegalRange(abyte0.length - i >= 8, "off", "arr.length - off >= 8");
        Uid8Gen uid8gen = uid8;
        JVM INSTR monitorenter ;
        return uid8.setUid8(abyte0, i);
        Exception exception;
        exception;
        throw exception;
    }

    public UidSeq getUidSeq(String s)
    {
        ArgAssert.nullArg(s, "name");
        Map map = seqs;
        JVM INSTR monitorenter ;
        return (UidSeq)seqs.get(s);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean deleteUidSeq(String s)
        throws UidSeqException
    {
        ArgAssert.nullArg(s, "name");
        Map map = seqs;
        JVM INSTR monitorenter ;
        UidSeqImpl uidseqimpl = (UidSeqImpl)seqs.remove(s);
        if(uidseqimpl != null)
        {
            stopSeq(uidseqimpl);
            return true;
        }
        false;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void deleteAllUidSeqs()
        throws UidSeqException
    {
        synchronized(seqs)
        {
            UidSeqException uidseqexception = null;
            Iterator iterator = seqs.values().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                UidSeqImpl uidseqimpl = (UidSeqImpl)iterator.next();
                try
                {
                    stopSeq(uidseqimpl);
                }
                catch(UidSeqException uidseqexception1)
                {
                    if(uidseqexception == null)
                        uidseqexception = uidseqexception1;
                }
            } while(true);
            seqs.clear();
            if(uidseqexception != null)
                throw uidseqexception;
        }
    }

    public UidSeq createUidSeq(String s, long l, long l1, boolean flag, long l2, UidSeqSink uidseqsink, int i)
        throws UidSeqException
    {
        ArgAssert.nullArg(s, "name");
        Map map = seqs;
        JVM INSTR monitorenter ;
        Object obj = (UidSeq)seqs.get(s);
        if(obj == null)
            seqs.put(s, obj = new UidSeqImpl(s, l, l1, flag, l2, uidseqsink, i));
        else
            throw new UidSeqException(L10n.format("SRVC.UIDGEN.ERR11", s));
        return ((UidSeq) (obj));
        Exception exception;
        exception;
        throw exception;
    }

    public UidSeq createUidSeq(String s, long l, long l1, boolean flag, long l2)
        throws UidSeqException
    {
        ArgAssert.nullArg(s, "name");
        return createUidSeq(s, l, l1, flag, l2, null, 10);
    }

    public Uid decodeHostUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 16, "arr", "arr.length >= 16");
        ArgAssert.illegalRange(abyte0.length - i >= 16, "off", "arr.length - off >= 16");
        return new UidImpl(abyte0, i, 16);
    }

    public Uid decodeLanUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 24, "arr", "arr.length >= 24");
        ArgAssert.illegalRange(abyte0.length - i >= 24, "off", "arr.length - off >= 24");
        return new UidImpl(abyte0, i, 24);
    }

    public Uid decodeVmUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 8, "arr", "arr.length >= 8");
        ArgAssert.illegalRange(abyte0.length - i >= 8, "off", "arr.length - off >= 8");
        return new UidImpl(abyte0, i, 8);
    }

    public Uid decodeWanUid(byte abyte0[], int i)
    {
        ArgAssert.nullArg(abyte0, "arr");
        ArgAssert.illegalArg(i >= 0, "off");
        ArgAssert.illegalRange(abyte0.length >= 32, "arr", "arr.length >= 32");
        ArgAssert.illegalRange(abyte0.length - i >= 32, "off", "arr.length - off >= 32");
        return new UidImpl(abyte0, i, 32);
    }

    private static final int DEFAULT_SAVE_FREQ = 10;
    private Map seqs;
    private Logger logger;
    private Random random;
    private byte vmId[];
    private byte ipAddr[];
    private Uid8Gen uid8;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(UidGenServiceImpl.class).desiredAssertionStatus();
    }

}
