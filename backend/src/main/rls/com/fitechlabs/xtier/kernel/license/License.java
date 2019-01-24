// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.license;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.license:
//            LicenseFeatureSet

public class License
{

    License()
    {
        product = null;
        release = null;
        licensee = null;
        licenser = null;
        grpStr = null;
        typeStr = null;
        issueDate = null;
        issueDateStr = null;
        featureSets = new ArrayList();
    }

    boolean containsFs(String s)
    {
        int i = featureSets.size();
        for(int j = 0; j < i; j++)
        {
            LicenseFeatureSet licensefeatureset = (LicenseFeatureSet)featureSets.get(j);
            if(licensefeatureset.getName().equals(s))
                return true;
        }

        return false;
    }

    byte[] getSignature()
    {
        return sig;
    }

    void setSignature(byte abyte0[])
    {
        sig = abyte0;
    }

    void addFs(LicenseFeatureSet licensefeatureset)
    {
        if(!$assertionsDisabled && licensefeatureset == null)
        {
            throw new AssertionError();
        } else
        {
            featureSets.add(licensefeatureset);
            return;
        }
    }

    String getProduct()
    {
        return product;
    }

    String getRelease()
    {
        return release;
    }

    String getLicensee()
    {
        return licensee;
    }

    String getLicenser()
    {
        return licenser;
    }

    int getGroup()
    {
        return grp;
    }

    String getGroupStr()
    {
        return grpStr;
    }

    int getType()
    {
        return type;
    }

    String getTypeStr()
    {
        return typeStr;
    }

    Date getIssueDate()
    {
        return issueDate;
    }

    String getIssueDateStr()
    {
        return issueDateStr;
    }

    LicenseFeatureSet[] getAllFss()
    {
        return (LicenseFeatureSet[])featureSets.toArray(new LicenseFeatureSet[featureSets.size()]);
    }

    LicenseFeatureSet getFs(String s)
    {
        int i = featureSets.size();
        for(int j = 0; j < i; j++)
        {
            LicenseFeatureSet licensefeatureset = (LicenseFeatureSet)featureSets.get(j);
            if(licensefeatureset.getName().equals(s))
                return licensefeatureset;
        }

        return null;
    }

    void setIssueDate(Date date)
    {
        issueDate = date;
    }

    void setGroup(int i)
    {
        grp = i;
    }

    void setLicensee(String s)
    {
        licensee = s;
    }

    void setLicenser(String s)
    {
        licenser = s;
    }

    void setType(int i)
    {
        type = i;
    }

    void setProduct(String s)
    {
        product = s;
    }

    void setRelease(String s)
    {
        release = s;
    }

    void setIssueDateStr(String s)
    {
        issueDateStr = s;
    }

    void setGroupStr(String s)
    {
        grpStr = s;
    }

    void setTypeStr(String s)
    {
        typeStr = s;
    }

    byte[] toLicenseArr()
    {
        byte abyte0[] = new byte[32];
        int i = 0;
        i = Utils.encodeInt32(product.hashCode(), abyte0, i);
        i = Utils.encodeInt32(release.hashCode(), abyte0, i);
        i = Utils.encodeInt32(licensee.hashCode(), abyte0, i);
        i = Utils.encodeInt32(licenser.hashCode(), abyte0, i);
        i = Utils.encodeInt32(grpStr.hashCode(), abyte0, i);
        i = Utils.encodeInt32(typeStr.hashCode(), abyte0, i);
        i = Utils.encodeInt32(issueDateStr.hashCode(), abyte0, i);
        StringBuffer stringbuffer = new StringBuffer();
        int j = featureSets.size();
        for(int k = 0; k < j; k++)
            stringbuffer.append(((LicenseFeatureSet)featureSets.get(k)).getName()).append('|');

        Utils.encodeInt32(stringbuffer.toString().hashCode(), abyte0, i);
        return abyte0;
    }

    public String toString()
    {
        return L10n.format("KRNL.LCNS.TXT4", licensee, licenser);
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

    public static final int LICENSE_TYPE_OEM = 1;
    public static final int LICENSE_TYPE_ENTERPRISE = 2;
    public static final int LICENSE_TYPE_RETAIL = 3;
    public static final int LICENSE_TYPE_ACADEMIC = 4;
    public static final int LICENSE_GROUP_FREE = 101;
    public static final int LICENSE_GROUP_COMMERCIAL = 102;
    public static final int LICENSE_GROUP_EVAL = 103;
    private String product;
    private String release;
    private String licensee;
    private String licenser;
    private int grp;
    private String grpStr;
    private int type;
    private String typeStr;
    private Date issueDate;
    private String issueDateStr;
    private List featureSets;
    private byte sig[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(License.class).desiredAssertionStatus();
    }
}
