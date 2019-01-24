// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.email.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.email.EmailService;
import com.fitechlabs.xtier.services.email.EmailSmtpSession;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.email.impl:
//            EmailSmtpSessionImpl

public class EmailServiceImpl extends ServiceProviderAdapter
    implements EmailService
{

    public EmailServiceImpl()
    {
        includes = new HashSet();
        regions = new HashSet();
        regionFound = false;
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("email");
        regions.clear();
        try
        {
            parseXmlConfig("xtier_email.xml", getRegionName());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.EMAIL.IMPL.ERR1"), saxexception);
        }
    }

    protected void onStop()
    {
        includes.clear();
    }

    public String getName()
    {
        return "email";
    }

    public EmailSmtpSession getSmtpSession()
    {
        EmailSmtpSessionImpl emailsmtpsessionimpl = new EmailSmtpSessionImpl();
        emailsmtpsessionimpl.setSmtpHost(smtpHost);
        emailsmtpsessionimpl.setSmtpPort(smtpPort);
        if(dfltMime != null)
            emailsmtpsessionimpl.setMime(dfltMime);
        if(dfltFrom != null)
            emailsmtpsessionimpl.setFromAddress(dfltFrom);
        if(dfltEncoding != null)
            emailsmtpsessionimpl.setEncoding(dfltEncoding);
        return emailsmtpsessionimpl;
    }

    public String getDfltFromAddress()
    {
        return dfltFrom;
    }

    public String getDfltMime()
    {
        return dfltMime;
    }

    public String getDfltEncoding()
    {
        return dfltEncoding;
    }

    public String getDfltSmtpHost()
    {
        return smtpHost;
    }

    public int getDfltSmtpPort()
    {
        return smtpPort;
    }

    private void parseXmlConfig(String s, String s1)
        throws SAXException
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        if(includes.contains(s))
        {
            log.warning(L10n.format("SRVC.EMAIL.IMPL.WRN1", s));
            return;
        }
        includes.add(s);
        String s2 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s2, new XmlSaxHandler("xtier_email.dtd", s1) {

                protected void onTagStart(String s3, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s3.equals("region"))
                    {
                        currRegion = xmlattrinterceptor.getValue("name");
                        if(regions.contains(currRegion))
                            throw createSaxErr(L10n.format("SRVC.EMAIL.IMPL.ERR11", currRegion));
                        regions.add(currRegion);
                    } else
                    if(s3.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), targetRegion);
                }

                protected void onTagEnd(String s3)
                    throws SAXException
                {
                    if(s3.equals("xtier-email"))
                    {
                        if(!regionFound)
                            throw createSaxErr(L10n.format("SRVC.EMAIL.IMPL.ERR2", targetRegion));
                    } else
                    if(s3.equals("region"))
                    {
                        if(currRegion.equals(targetRegion))
                        {
                            regionFound = true;
                            smtpHost = host;
                            smtpPort = port;
                            dfltMime = mime;
                            dfltEncoding = javaEncoding;
                            dfltFrom = from;
                        }
                    } else
                    if(s3.equals("smtp-host"))
                    {
                        host = getPcdata();
                        if(host.length() == 0)
                            throw createSaxErr(L10n.format("SRVC.EMAIL.IMPL.ERR3", currRegion));
                    } else
                    if(s3.equals("smtp-port"))
                    {
                        String s4 = getPcdata();
                        port = parseIpPort(s4);
                    } else
                    if(s3.equals("default-from"))
                        from = getPcdata();
                    else
                    if(s3.equals("java-default-encoding"))
                    {
                        javaEncoding = getPcdata();
                        if(javaEncoding.length() != 0 && !EmailServiceImpl.javaEncodings.contains(javaEncoding))
                            throw createSaxErr(L10n.format("SRVC.EMAIL.IMPL.ERR6", currRegion));
                    } else
                    if(s3.equals("clr-default-encoding"))
                        resetPcdata();
                    else
                    if(s3.equals("default-mime"))
                        mime = getPcdata();
                }

                private String currRegion;
                private String host;
                private int port;
                private String from;
                private String javaEncoding;
                private String mime;


                throws SAXException
            {
                super(final_s, s1);
                currRegion = null;
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
            throw new SAXException(L10n.format("SRVC.EMAIL.IMPL.ERR7", s2), ioexception);
        }
    }

    public void send(String s, String s1, String s2, Object obj, String s3, File file)
        throws MessagingException
    {
        ArgAssert.nullArg(s, "to");
        ArgAssert.nullArg(obj, "content");
        EmailSmtpSession emailsmtpsession = getSmtpSession();
        emailsmtpsession.addToAddress(s);
        if(s1 != null)
            emailsmtpsession.addCcAddress(s1);
        if(s2 != null)
            emailsmtpsession.addBccAddress(s2);
        emailsmtpsession.setContent(obj);
        if(s3 != null)
            emailsmtpsession.setSubject(s3);
        if(file != null)
            emailsmtpsession.attachFile(file);
        emailsmtpsession.send();
    }

    public void send(String as[], String as1[], String as2[], Object obj, String s, File afile[])
        throws MessagingException
    {
        ArgAssert.nullArg(as, "to");
        ArgAssert.nullArg(obj, "content");
        EmailSmtpSession emailsmtpsession = getSmtpSession();
        emailsmtpsession.addToAddresses(as);
        if(as1 != null)
            emailsmtpsession.addCcAddresses(as1);
        if(as2 != null)
            emailsmtpsession.addBccAddresses(as2);
        emailsmtpsession.setContent(obj);
        if(s != null)
            emailsmtpsession.setSubject(s);
        if(afile != null)
        {
            for(int i = 0; i < afile.length; i++)
                emailsmtpsession.attachFile(afile[i]);

        }
        emailsmtpsession.send();
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private Set includes;
    private String smtpHost;
    private int smtpPort;
    private String dfltMime;
    private String dfltEncoding;
    private String dfltFrom;
    private Logger log;
    private Set regions;
    private boolean regionFound;
    private static Set javaEncodings;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(EmailServiceImpl.class).desiredAssertionStatus();
        javaEncodings = new HashSet();
        javaEncodings.add("7bit");
        javaEncodings.add("8bit");
        javaEncodings.add("binary");
        javaEncodings.add("quoted-printable");
        javaEncodings.add("base64");
    }










}
