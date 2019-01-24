// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;

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
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.marshal.impl:
//            NioMarshallerImpl, IoMarshallerImpl, ByteMarshallerImpl

public class MarshalServiceImpl extends ServiceProviderAdapter
    implements MarshalService
{

    public MarshalServiceImpl()
    {
        nioImpl = null;
        ioImpl = null;
        byteImpl = null;
    }

    public void registerFactory(MarshallableFactory marshallablefactory)
    {
        ArgAssert.nullArg(marshallablefactory, "factory");
        nioImpl.addFactory(marshallablefactory);
        ioImpl.addFactory(marshallablefactory);
        byteImpl.addFactory(marshallablefactory);
    }

    public void unregisterFactory(MarshallableFactory marshallablefactory)
    {
        ArgAssert.nullArg(marshallablefactory, "factory");
        nioImpl.removeFactory(marshallablefactory);
        ioImpl.removeFactory(marshallablefactory);
        byteImpl.removeFactory(marshallablefactory);
    }

    public NioMarshaller getNioMarshaller()
    {
        return nioImpl;
    }

    public IoMarshaller getIoMarshaller()
    {
        return ioImpl;
    }

    public ByteMarshaller getByteMarshaller()
    {
        return byteImpl;
    }

    public String getName()
    {
        return "marshal";
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("marshal");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_marshal.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.MARSHAL.IMPL.ERR1"), saxexception);
        }
        List list = (List)hashmap.get(getRegionName());
        if(list == null)
            throw new ServiceProviderException(L10n.format("SRVC.MARSHAL.IMPL.ERR2", getRegionName()));
        ArrayList arraylist = new ArrayList();
        int i = list.size();
        for(int j = 0; j < i; j++)
            try
            {
                MarshallableFactory marshallablefactory = (MarshallableFactory)((IocDescriptor)list.get(j)).createNewObj(com.fitechlabs.xtier.services.marshal.MarshallableFactory.class);
                arraylist.add(marshallablefactory);
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.MARSHAL.IMPL.ERR3", iocdescriptorexception.getMessage()), iocdescriptorexception);
            }

        nioImpl = new NioMarshallerImpl(arraylist);
        ioImpl = new IoMarshallerImpl(arraylist);
        byteImpl = new ByteMarshallerImpl(arraylist);
    }

    protected void onStop()
    {
        nioImpl = null;
        ioImpl = null;
        byteImpl = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.MARSHAL.IMPL.WRN1", s));
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
                        name = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(name))
                            throw createSaxErr(L10n.format("SRVC.MARSHAL.IMPL.ERR4", name));
                        region = new ArrayList();
                    } else
                    if(s2.equals("factory"))
                        isFactory = true;
                    else
                    if(s2.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), regions, includes);
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(isFactory)
                        region.add(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                        regions.put(name, region);
                    else
                    if(s2.equals("factory"))
                        isFactory = false;
                }

                private String name;
                private boolean isFactory;
                private List region;


                throws SAXException
            {
                super(final_s, final_s1);
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
            throw new SAXException(L10n.format("SRVC.MARSHAL.IMPL.ERR5", s1), ioexception);
        }
    }

    private NioMarshallerImpl nioImpl;
    private IoMarshallerImpl ioImpl;
    private ByteMarshallerImpl byteImpl;
    private Logger log;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(MarshalServiceImpl.class).desiredAssertionStatus();
    }

}
