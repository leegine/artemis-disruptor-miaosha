// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   LicenseManager.java

package co.fin.intellioms.license;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.services.info.InfoService;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Locale;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

// Referenced classes of package com.com.fin.intellioms.license:
//            License, LicenseException, LicenseFeatureSet, LicenseDescriptor

public class LicenseManager
{

    private LicenseManager()
    {
    }

    private static void reset()
    {
        actFs = null;
        license = null;
    }

    public static synchronized void init()
        throws LicenseException
    {
        if(actFs == null)
        {
            String root = XtierKernel.getInstance().info().getXtierInstallRoot();
            reset();
            PublicKey pubKey = loadPublicKey(root);
            Locale currLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            license = new License();
            try
            {
                parseLicenseXml(root);
            }
            catch(SAXException e)
            {
                throw new LicenseException("Unable to parse license file.", e);
            }
            actFs = license.getFs();
            Locale.setDefault(currLocale);
            verifyIntegrety(pubKey);
        }
    }

    private static void verifyIntegrety(PublicKey pubKey)
        throws LicenseException
    {
        try
        {
            Signature sig = Signature.getInstance("DSA");
            sig.initVerify(pubKey);
            sig.update(license.toLicenseArr());
            if(!sig.verify(license.getSignature()))
                throw new LicenseException("Wrong license signature.");
            LicenseDescriptor descriptors[] = license.getFs().getAllDescriptors();
            for(int j = 0; j < descriptors.length; j++)
            {
                sig.update(descriptors[j].toLicenseArr());
                if(!sig.verify(descriptors[j].getSignature()))
                    throw new LicenseException("Wrong license signature.");
            }

        }
        catch(InvalidKeyException e)
        {
            throw new LicenseException(e.getMessage(), e);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new LicenseException(e.getMessage(), e);
        }
        catch(SignatureException e)
        {
            throw new LicenseException(e.getMessage(), e);
        }
    }

    private static PublicKey loadPublicKey(String root)
        throws LicenseException
    {
        String path = null;
        byte buf[];
        BufferedInputStream in;
        path = Utils.makeValidPath(Utils.makeValidPath(root, "config"), "public.key");
        File file = new File(path);
        if(!file.exists())
            break MISSING_BLOCK_LABEL_124;
        buf = new byte[(int)file.length()];
        in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(file));
        }
        catch(FileNotFoundException e)
        {
            throw new LicenseException("Unable to load public key.", e);
        }
        in.read(buf, 0, buf.length);
        Utils.close(in);
        break MISSING_BLOCK_LABEL_104;
        Exception exception;
        exception;
        Utils.close(in);
        throw exception;
        return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(fromHexArr(buf)));
        BufferedInputStream in;
        ByteArrayOutputStream out;
        in = new BufferedInputStream(com/ com /fin/intellioms/license/LicenseManager.getResourceAsStream("public.key"));
        out = new ByteArrayOutputStream(1024);
        for(int ch = 0; (ch = in.read()) != -1;)
            out.write(ch);

        Utils.close(in);
        break MISSING_BLOCK_LABEL_192;
        Exception exception1;
        exception1;
        Utils.close(in);
        throw exception1;
        return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(fromHexArr(out.toByteArray())));
        IOException e;
        e;
        throw new LicenseException(e.getMessage(), e);
        e;
        throw new LicenseException(e.getMessage(), e);
        e;
        throw new LicenseException(e.getMessage(), e);
    }

    private static byte[] fromHexArr(byte hexArr[])
    {
        int len = hexArr.length;
        if(!$assertionsDisabled && len % 2 != 0)
            throw new AssertionError("Length of encoded string must be even.");
        byte bytes[] = new byte[len / 2];
        int i = 0;
        for(int j = 0; i < bytes.length; j += 2)
        {
            bytes[i] = (byte)Integer.parseInt(new String(hexArr, j, 2), 16);
            i++;
        }

        return bytes;
    }

    public static void parseLicenseXml(String root)
        throws SAXException
    {
        if(!$assertionsDisabled && root == null)
            throw new AssertionError("Root is null.");
        String url = (new StringBuilder()).append("file:///").append(Utils.makeValidPath(root, "config/rls_license.xml")).toString();
        try
        {
            XmlUtils.makeSaxParser().parse(url, new XmlSaxHandler(root, "rls_license.dtd") {

                private byte[] fromHexStr(String hexStr)
                    throws SAXException
                {
                    int len = hexStr.length();
                    char chars[] = hexStr.toCharArray();
                    if(len % 2 != 0)
                        throw createSaxErr("Hex to string conversion failure.");
                    byte bytes[] = new byte[len / 2];
                    int i = 0;
                    for(int j = 0; i < bytes.length; j += 2)
                    {
                        bytes[i] = (byte)Integer.parseInt(new String(chars, j, 2), 16);
                        i++;
                    }

                    return bytes;
                }

                protected void onTagStart(String tagName, XmlAttrInterceptor attrs)
                    throws SAXException
                {
                    if(tagName.equals("feature-set"))
                        featureSet = new LicenseFeatureSet();
                    else
                    if(tagName.equals("service"))
                    {
                        String name = attrs.getValue("name");
                        if(featureSet.containsDescriptor(name))
                            throw createSaxErr((new StringBuilder()).append("Duplicated service name '").append(name).append("'.").toString());
                        descriptor = new LicenseDescriptor(name);
                    }
                }

                protected void onTagEnd(String tagName)
                    throws SAXException
                {
                    if(tagName.equals("product"))
                        LicenseManager.license.setProduct(getPcdata());
                    else
                    if(tagName.equals("licensee"))
                        LicenseManager.license.setLicensee(getPcdata());
                    else
                    if(tagName.equals("licenser"))
                        LicenseManager.license.setLicenser(getPcdata());
                    else
                    if(tagName.equals("issue-date"))
                    {
                        String str = getPcdata();
                        LicenseManager.license.setIssueDate(parseDate(str));
                        LicenseManager.license.setIssueDateStr(str);
                    } else
                    if(tagName.equals("sig"))
                    {
                        if(descriptor != null)
                        {
                            String str = getPcdata();
                            descriptor.setSignature(fromHexStr(str));
                        } else
                        {
                            String str = getPcdata();
                            LicenseManager.license.setSignature(fromHexStr(str));
                        }
                    } else
                    if(tagName.equals("cpus"))
                    {
                        String str = getPcdata();
                        descriptor.setCpusStr(str);
                        if(str.equals("any"))
                            descriptor.setCpus(0x7fffffff);
                        else
                            descriptor.setCpus(parseInt(str));
                    } else
                    if(tagName.equals("expire-date"))
                    {
                        String str = getPcdata();
                        descriptor.setExpDateStr(str);
                        if(str.equals("never"))
                            descriptor.setExpDate(new Date(0x7fffffffffffffffL));
                        else
                            descriptor.setExpDate(parseDate(str));
                    } else
                    if(tagName.equals("ip"))
                    {
                        String str = getPcdata();
                        descriptor.setIpsStr(str);
                        if(str.equals("any"))
                        {
                            descriptor.setIps(null);
                        } else
                        {
                            String ips[] = str.split("[;, ]");
                            if(ips.length == 0)
                                throw createSaxErr("Missing IP adresses.");
                            descriptor.setIps(ips);
                        }
                    } else
                    if(tagName.equals("nodes"))
                    {
                        String str = getPcdata();
                        descriptor.setNodesStr(str);
                        if(str.equals("any"))
                            descriptor.setNodes(0x7fffffff);
                        else
                            descriptor.setNodes(parseInt(str));
                    } else
                    if(tagName.equals("service"))
                    {
                        featureSet.addDescriptor(descriptor);
                        descriptor = null;
                    } else
                    if(tagName.equals("feature-set"))
                    {
                        LicenseManager.license.setFs(featureSet);
                        featureSet = null;
                    }
                }

                private LicenseFeatureSet featureSet;
                private LicenseDescriptor descriptor;


            {
                featureSet = null;
                descriptor = null;
            }
            }
);
        }
        catch(ParserConfigurationException e)
        {
            throw new SAXException(e);
        }
        catch(IOException e)
        {
            throw new SAXException(e);
        }
    }

    public static synchronized License getLicense()
    {
        if(!$assertionsDisabled && license == null)
            throw new AssertionError("License is null.");
        else
            return license;
    }

    public static synchronized LicenseFeatureSet getActiveFeatureSet()
    {
        if(!$assertionsDisabled && actFs == null)
            throw new AssertionError("Active feature set is null.");
        else
            return actFs;
    }

    public static synchronized LicenseDescriptor getLicenseDescriptor(String name)
    {
        if(!$assertionsDisabled && actFs == null)
            throw new AssertionError("Active feature set is null.");
        else
            return actFs.getDescriptor(name);
    }

    private static final String ENCRYPTION = "DSA";
    private static final String LICENSE_PATH = "config/rls_license.xml";
    private static final String PUB_KEY_PATH = "public.key";
    private static LicenseFeatureSet actFs = null;
    private static License license = null;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/license/LicenseManager.desiredAssertionStatus();


}
