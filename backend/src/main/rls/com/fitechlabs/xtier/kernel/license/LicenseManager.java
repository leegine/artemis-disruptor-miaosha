// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.license;

import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.kernel.license:
//            License, LicenseException, LicenseFeatureSet, LicenseDescriptor

public class LicenseManager
{

    private LicenseManager()
    {
    }

    private static String getVersionStr()
    {
        return "" + version[0] + '.' + version[1] + '.' + version[2] + '.' + version[3];
    }

    private static void reset()
    {
        actFs = null;
        license = null;
        userName = null;
        companyName = null;
    }

    public static synchronized void init(String s, String s1)
        throws LicenseException
    {
        if(!$assertionsDisabled && (s == null || s1 == null))
            throw new AssertionError();
        reset();
        PublicKey publickey = loadPublicKey(s);
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        license = new License();
        try
        {
            parseLicenseXml(s);
        }
        catch(SAXException saxexception)
        {
            throw new LicenseException(L10n.format("KRNL.LCNS.ERR14", saxexception.getMessage()), saxexception);
        }
        actFs = license.getFs(s1);
        if(actFs == null)
        {
            throw new LicenseException(L10n.format("KRNL.LCNS.ERR1", s1));
        } else
        {
            Locale.setDefault(locale);
            verifyIntegrety(publickey);
            verifyRelease(license.getRelease());
            printLicenseInfo(s1);
            return;
        }
    }

    private static void printLicenseInfo(String s)
    {
        getUserInfo();
        System.out.println("");
        System.out.println("xTier -- Java Middleware Components, ver. " + getVersionStr());
        System.out.println("Copyright (C) Fitech Laboratories, Inc. 2000-2005. All Rights Reserved.");
        System.out.println("");
        System.out.println(L10n.format("KRNL.LCNS.TXT19", userName != null ? ((Object) (userName)) : "<eval>"));
        System.out.println(L10n.format("KRNL.LCNS.TXT20", companyName != null ? ((Object) (companyName)) : "<eval>"));
        System.out.println("");
        System.out.println(L10n.format("KRNL.LCNS.TXT5"));
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT6") + license.getProduct());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT7") + license.getRelease());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT8") + license.getLicensee());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT9") + license.getIssueDateStr());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT10") + license.getLicenser());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT11") + license.getGroupStr());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT12") + license.getTypeStr());
        System.out.println("\t" + L10n.format("KRNL.LCNS.TXT13") + s);
        LicenseDescriptor alicensedescriptor[] = license.getFs(s).getAllDescriptors();
        long l = System.currentTimeMillis();
        for(int i = 0; i < alicensedescriptor.length; i++)
        {
            LicenseDescriptor licensedescriptor = alicensedescriptor[i];
            System.out.println("\t\tService: " + licensedescriptor.getServiceName());
            if(licensedescriptor.getCpus() == 0x7fffffff)
                System.out.println("\t\t\tCPUs: <any>");
            else
                System.out.println("\t\t\tCPUs: " + licensedescriptor.getCpus());
            System.out.print("\t\t\tIPs: ");
            String as[] = licensedescriptor.getIps();
            if(as != null)
            {
                for(int j = 0; j < as.length; j++)
                    System.out.print(as[j].toString() + " ");

                System.out.println();
            } else
            {
                System.out.println("<any>");
            }
            if(licensedescriptor.getNodes() == 0x7fffffff)
                System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT14") + "<any>");
            else
                System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT14") + licensedescriptor.getNodes());
            if(license.getGroup() == 103)
            {
                if(licensedescriptor.getExpDate().getTime() == 0x7fffffffffffffffL)
                {
                    System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT15") + "<never>");
                    continue;
                }
                long l1 = (licensedescriptor.getExpDate().getTime() - l) / 0x5265c00L;
                if(l1 > 0L)
                    System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT15") + l1 + " day(s).");
                else
                    System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT15") + L10n.format("KRNL.LCNS.TXT18"));
                continue;
            }
            if(licensedescriptor.getExpDate().getTime() == 0x7fffffffffffffffL)
            {
                System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT17") + "<never>");
                continue;
            }
            long l2 = (licensedescriptor.getExpDate().getTime() - l) / 0x5265c00L;
            if(l2 > 0L)
                System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT17") + licensedescriptor.getExpDate());
            else
                System.out.println("\t\t\t" + L10n.format("KRNL.LCNS.TXT17") + L10n.format("KRNL.LCNS.TXT18"));
        }

    }

    private static void verifyRelease(String s)
        throws LicenseException
    {
        String as[] = s.replaceAll("\\*", Integer.toString(0x7fffffff)).split("\\.");
        if(!$assertionsDisabled && as.length != 4)
            throw new AssertionError();
        int ai[] = new int[4];
        for(int i = 0; i < as.length; i++)
            try
            {
                ai[i] = Integer.parseInt(as[i]);
            }
            catch(NumberFormatException numberformatexception)
            {
                throw new LicenseException(L10n.format("KRNL.LCNS.ERR2"));
            }

        if(!$assertionsDisabled && ai.length != version.length)
            throw new AssertionError();
        for(int j = 0; j < ai.length; j++)
            if(version[j] > ai[j])
                throw new LicenseException(L10n.format("KRNL.LCNS.ERR26", getVersionStr(), s));

    }

    private static void verifyIntegrety(PublicKey publickey)
        throws LicenseException
    {
        try
        {
            Signature signature = Signature.getInstance("DSA");
            signature.initVerify(publickey);
            signature.update(license.toLicenseArr());
            if(!signature.verify(license.getSignature()))
                throw new LicenseException(L10n.format("KRNL.LCNS.ERR2"));
            LicenseFeatureSet alicensefeatureset[] = license.getAllFss();
            for(int i = 0; i < alicensefeatureset.length; i++)
            {
                signature.update(alicensefeatureset[i].toLicenseArr());
                if(!signature.verify(alicensefeatureset[i].getSignature()))
                    throw new LicenseException(L10n.format("KRNL.LCNS.ERR2"));
                LicenseDescriptor alicensedescriptor[] = alicensefeatureset[i].getAllDescriptors();
                for(int j = 0; j < alicensedescriptor.length; j++)
                {
                    signature.update(alicensedescriptor[j].toLicenseArr());
                    if(!signature.verify(alicensedescriptor[j].getSignature()))
                        throw new LicenseException(L10n.format("KRNL.LCNS.ERR2"));
                }

            }

        }
        catch(InvalidKeyException invalidkeyexception)
        {
            throw new LicenseException(L10n.format("KRNL.LCNS.ERR4", publickey), invalidkeyexception);
        }
        catch(NoSuchAlgorithmException nosuchalgorithmexception)
        {
            throw new LicenseException(L10n.format("KRNL.LCNS.ERR5", "DSA"), nosuchalgorithmexception);
        }
        catch(SignatureException signatureexception)
        {
            throw new LicenseException(L10n.format("KRNL.LCNS.ERR6"), signatureexception);
        }
    }

    private static PublicKey loadPublicKey(String s)
        throws LicenseException
    {
        Object obj = null;
        byte abyte0[];
        Object obj2;
        String s1 = Utils.makeValidPath(Utils.makeValidPath(s, "config"), "java_pub.key");
        File file = new File(s1);
        if(!file.exists())
            break MISSING_BLOCK_LABEL_128;
        abyte0 = new byte[(int)file.length()];
        obj2 = null;
        try
        {
            obj2 = new BufferedInputStream(new FileInputStream(file));
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            throw new LicenseException(L10n.format("KRNL.LCNS.ERR7", s1), filenotfoundexception);
        }
        ((BufferedInputStream) (obj2)).read(abyte0, 0, abyte0.length);
        Utils.close(((InputStream) (obj2)));
        break MISSING_BLOCK_LABEL_108;
        Exception exception;
        exception;
        Utils.close(((InputStream) (obj2)));
        throw exception;
        return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(fromHexArr(abyte0)));
        BufferedInputStream bufferedinputstream;
        bufferedinputstream = new BufferedInputStream((LicenseManager.class).getResourceAsStream("java_pub.key"));
        obj2 = new ByteArrayOutputStream(1024);
        for(int i = 0; (i = bufferedinputstream.read()) != -1;)
            ((ByteArrayOutputStream) (obj2)).write(i);

        Utils.close(bufferedinputstream);
        break MISSING_BLOCK_LABEL_214;
        Exception exception1;
        exception1;
        Utils.close(bufferedinputstream);
        throw exception1;
        return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(fromHexArr(((ByteArrayOutputStream) (obj2)).toByteArray())));
        Object obj1;
        obj1;
        throw new LicenseException(L10n.format("KRNL.LCNS.ERR8"), ((Throwable) (obj1)));
        obj1;
        throw new LicenseException(L10n.format("KRNL.LCNS.ERR9"), ((Throwable) (obj1)));
        obj1;
        throw new LicenseException(L10n.format("KRNL.LCNS.ERR10", "DSA"));
    }

    private static byte[] fromHexArr(byte abyte0[])
    {
        int i = abyte0.length;
        if(!$assertionsDisabled && i % 2 != 0)
            throw new AssertionError("Length of encoded string must be even.");
        byte abyte1[] = new byte[i / 2];
        int j = 0;
        for(int k = 0; j < abyte1.length; k += 2)
        {
            abyte1[j] = (byte)Integer.parseInt(new String(abyte0, k, 2), 16);
            j++;
        }

        return abyte1;
    }

    public static void parseLicenseXml(String s)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        String s1 = "file:///" + Utils.makeValidPath(s, "config/xtier_license.xml");
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(s, "xtier_license.dtd") {

                private byte[] fromHexStr(String s2)
                    throws SAXException
                {
                    int i = s2.length();
                    char ac[] = s2.toCharArray();
                    if(i % 2 != 0)
                        throw createSaxErr(L10n.format("KRNL.LCNS.ERR2"));
                    byte abyte0[] = new byte[i / 2];
                    int j = 0;
                    for(int k = 0; j < abyte0.length; k += 2)
                    {
                        abyte0[j] = (byte)Integer.parseInt(new String(ac, k, 2), 16);
                        j++;
                    }

                    return abyte0;
                }

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("feature-set"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(LicenseManager.license.containsFs(s3))
                            throw createSaxErr(L10n.format("KRNL.LCNS.ERR11"));
                        featureSet = new LicenseFeatureSet(s3);
                    } else
                    if(s2.equals("service"))
                    {
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(featureSet.containsDescriptor(s4))
                            throw createSaxErr(L10n.format("KRNL.LCNS.ERR12", featureSet.getName(), s4));
                        descriptor = new LicenseDescriptor(s4);
                    } else
                    if(s2.equals("java-env"))
                        isJava = true;
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("product"))
                        LicenseManager.license.setProduct(getPcdata());
                    else
                    if(s2.equals("release"))
                        LicenseManager.license.setRelease(getPcdata());
                    else
                    if(s2.equals("licensee"))
                        LicenseManager.license.setLicensee(getPcdata());
                    else
                    if(s2.equals("licenser"))
                        LicenseManager.license.setLicenser(getPcdata());
                    else
                    if(s2.equals("license-class"))
                    {
                        String s3 = getPcdata().toLowerCase();
                        Integer integer = (Integer)LicenseManager.validGrps.get(s3);
                        if(integer == null)
                            throw createSaxErr(L10n.format("KRNL.LCNS.ERR19", s3));
                        LicenseManager.license.setGroup(integer.intValue());
                        LicenseManager.license.setGroupStr(s3);
                    } else
                    if(s2.equals("license-type"))
                    {
                        String s4 = getPcdata().toLowerCase();
                        Integer integer1 = (Integer)LicenseManager.validTypes.get(s4);
                        if(integer1 == null)
                            throw createSaxErr(L10n.format("KRNL.LCNS.ERR21", s4));
                        LicenseManager.license.setType(integer1.intValue());
                        LicenseManager.license.setTypeStr(s4);
                    } else
                    if(s2.equals("issue-date"))
                    {
                        String s5 = getPcdata();
                        LicenseManager.license.setIssueDate(parseDate(s5));
                        LicenseManager.license.setIssueDateStr(s5);
                    } else
                    if(s2.equals("java-sig"))
                    {
                        if(descriptor != null)
                            descriptor.setSignature(fromHexStr(getPcdata()));
                        else
                        if(featureSet != null)
                            featureSet.setSignature(fromHexStr(getPcdata()));
                        else
                            LicenseManager.license.setSignature(fromHexStr(getPcdata()));
                    } else
                    if(s2.equals("clr-sig"))
                        resetPcdata();
                    else
                    if(s2.equals("java-env"))
                        isJava = false;
                    else
                    if(s2.equals("cpus"))
                    {
                        if(isJava)
                        {
                            String s6 = getPcdata();
                            descriptor.setCpusStr(s6);
                            if(s6.equals("any"))
                                descriptor.setCpus(0x7fffffff);
                            else
                                descriptor.setCpus(parseInt(s6));
                        } else
                        {
                            resetPcdata();
                        }
                    } else
                    if(s2.equals("expire-date"))
                    {
                        if(isJava)
                        {
                            String s7 = getPcdata();
                            descriptor.setExpDateStr(s7);
                            if(s7.equals("never"))
                                descriptor.setExpDate(new Date(0x7fffffffffffffffL));
                            else
                            if(Pattern.matches("[0-9]?[0-9]days", s7))
                            {
                                if(LicenseManager.license.getGroup() != 103)
                                    throw createSaxErr(L10n.format("KRNL.LCNS.ERR22"));
                                try
                                {
                                    descriptor.setExpDate(new Date(LicenseManager.getEvalStart() + (long)Integer.parseInt(s7.replaceAll("days", "")) * 0x5265c00L));
                                }
                                catch(IOException ioexception1)
                                {
                                    throw createSaxErr(L10n.format("KRNL.LCNS.ERR25"));
                                }
                            } else
                            {
                                descriptor.setExpDate(parseDate(s7));
                            }
                        } else
                        {
                            resetPcdata();
                        }
                    } else
                    if(s2.equals("ip"))
                    {
                        if(isJava)
                        {
                            String s8 = getPcdata();
                            descriptor.setIpsStr(s8);
                            if(s8.equals("any"))
                            {
                                descriptor.setIps(null);
                            } else
                            {
                                String as[] = s8.split("[;, ]");
                                if(as.length == 0)
                                    throw createSaxErr(L10n.format("KRNL.LCNS.ERR13", s8));
                                descriptor.setIps(as);
                            }
                        } else
                        {
                            resetPcdata();
                        }
                    } else
                    if(s2.equals("nodes"))
                    {
                        if(isJava)
                        {
                            String s9 = getPcdata();
                            descriptor.setNodesStr(s9);
                            if(s9.equals("any"))
                                descriptor.setNodes(0x7fffffff);
                            else
                                descriptor.setNodes(parseInt(s9));
                        } else
                        {
                            resetPcdata();
                        }
                    } else
                    if(s2.equals("service"))
                    {
                        featureSet.addDescriptor(descriptor);
                        descriptor = null;
                    } else
                    if(s2.equals("feature-set"))
                    {
                        LicenseManager.license.addFs(featureSet);
                        featureSet = null;
                    }
                }

                private LicenseFeatureSet featureSet;
                private LicenseDescriptor descriptor;
                private boolean isJava;


                throws SAXException
            {
                featureSet = null;
                descriptor = null;
                isJava = false;
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
            throw new SAXException(L10n.format("KRNL.LCNS.ERR16", s1), ioexception);
        }
    }

    private static String mixVersion(String s)
    {
        if(!$assertionsDisabled && (s == null || s.length() <= 10))
        {
            throw new AssertionError();
        } else
        {
            StringBuffer stringbuffer = new StringBuffer(s);
            stringbuffer.insert(3, version[3]);
            stringbuffer.insert(5, version[0]);
            stringbuffer.insert(8, version[1]);
            stringbuffer.insert(10, version[2]);
            return stringbuffer.toString();
        }
    }

    private static long getEvalStart()
        throws IOException
    {
        CRC32 crc32 = new CRC32();
        crc32.update(license.getSignature());
        File file = new File(Utils.makeValidPath(Utils.USER_HOME, mixVersion(".599999763286062") + Long.toString(crc32.getValue())));
        if(!file.exists())
        {
            File file1 = file.getParentFile();
            if(file1 != null)
                file1.mkdirs();
            file.createNewFile();
        }
        return file.lastModified();
    }

    private static void getUserInfo()
    {
        StringBuffer stringbuffer = new StringBuffer(".805655493624646");
        stringbuffer.insert(3, version[0]);
        stringbuffer.insert(5, version[2]);
        stringbuffer.insert(8, version[1]);
        File file = new File(Utils.makeValidPath(Utils.USER_HOME, stringbuffer.toString()));
        if(!file.exists())
            return;
        int i = 0;
        byte abyte0[] = new byte[200];
        try
        {
            BufferedInputStream bufferedinputstream = new BufferedInputStream(new FileInputStream(file));
            do
            {
                int j = bufferedinputstream.read();
                if(j == -1)
                    break;
                abyte0[i++] = (byte)(j ^ 7);
            } while(true);
            bufferedinputstream.close();
        }
        catch(IOException ioexception)
        {
            return;
        }
        char ac[] = new char[i];
        int k = 0;
        for(int l = 0; l < i;)
        {
            char c = (char)((abyte0[l++] << 8) + abyte0[l++]);
            if(c == '\n')
            {
                if(k == 0)
                    return;
                userName = new String(ac, 0, k);
                k = 0;
            } else
            {
                ac[k++] = c;
            }
        }

        companyName = new String(ac, 0, k);
        if(!$assertionsDisabled && (companyName == null || companyName.length() == 0))
            throw new AssertionError();
        else
            return;
    }

    public static synchronized License getLicense()
    {
        if(!$assertionsDisabled && license == null)
            throw new AssertionError();
        else
            return license;
    }

    public static synchronized LicenseFeatureSet getActiveFeatureSet()
    {
        if(!$assertionsDisabled && actFs == null)
            throw new AssertionError();
        else
            return actFs;
    }

    public static synchronized LicenseDescriptor getLicenseDescriptor(String s)
    {
        if(!$assertionsDisabled && actFs == null)
            throw new AssertionError();
        else
            return actFs.getDescriptor(s);
    }

    private static final int version[] = {
        2, 3, 2, 1599
    };
    private static final Map validGrps;
    private static final Map validTypes;
    private static final String ENCRYPTION = "DSA";
    private static final String LICENSE_PATH = "config/xtier_license.xml";
    private static final String PUB_KEY_PATH = "java_pub.key";
    private static final String EVAL_PREFIX = ".599999763286062";
    private static final String USER_INFO_FILE = ".805655493624646";
    private static final byte XOR_MASK = 7;
    private static final long DAY_MILLIS = 0x5265c00L;
    private static LicenseFeatureSet actFs = null;
    private static License license = null;
    private static String userName = null;
    private static String companyName = null;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LicenseManager.class).desiredAssertionStatus();
        validGrps = new HashMap();
        validTypes = new HashMap();
        validGrps.put("commercial", new Integer(102));
        validGrps.put("eval", new Integer(103));
        validGrps.put("free", new Integer(101));
        validTypes.put("academic", new Integer(4));
        validTypes.put("enterprise", new Integer(2));
        validTypes.put("oem", new Integer(1));
        validTypes.put("retail", new Integer(3));
    }




}
