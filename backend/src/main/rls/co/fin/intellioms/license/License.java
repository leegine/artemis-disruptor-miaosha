// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   License.java

package co.fin.intellioms.license;

import com.fitechlabs.xtier.utils.Utils;
import java.util.Date;

// Referenced classes of package com.com.fin.intellioms.license:
//            LicenseFeatureSet

public class License
{

    License()
    {
        product = null;
        licensee = null;
        licenser = null;
        issueDate = null;
        issueDateStr = null;
    }

    byte[] getSignature()
    {
        return sig;
    }

    void setSignature(byte sig[])
    {
        this.sig = sig;
    }

    void setFs(LicenseFeatureSet fs)
    {
        if(!$assertionsDisabled && fs == null)
        {
            throw new AssertionError("Feauture set is null.");
        } else
        {
            featureSet = fs;
            return;
        }
    }

    String getProduct()
    {
        return product;
    }

    String getLicensee()
    {
        return licensee;
    }

    String getLicenser()
    {
        return licenser;
    }

    Date getIssueDate()
    {
        return issueDate;
    }

    String getIssueDateStr()
    {
        return issueDateStr;
    }

    LicenseFeatureSet getFs()
    {
        return featureSet;
    }

    void setIssueDate(Date issueDate)
    {
        this.issueDate = issueDate;
    }

    void setLicensee(String licensee)
    {
        this.licensee = licensee;
    }

    void setLicenser(String licenser)
    {
        this.licenser = licenser;
    }

    void setProduct(String product)
    {
        this.product = product;
    }

    void setIssueDateStr(String issueDateStr)
    {
        this.issueDateStr = issueDateStr;
    }

    byte[] toLicenseArr()
    {
        byte sigArr[] = new byte[16];
        int off = 0;
        off = Utils.encodeInt32(product.hashCode(), sigArr, off);
        off = Utils.encodeInt32(licensee.hashCode(), sigArr, off);
        off = Utils.encodeInt32(licenser.hashCode(), sigArr, off);
        off = Utils.encodeInt32(issueDateStr.hashCode(), sigArr, off);
        return sigArr;
    }

    public String toString()
    {
        return (new StringBuilder()).append("License[linsee=").append(licensee).append(", licenser=").append(licenser).append(']').toString();
    }

    private String product;
    private String licensee;
    private String licenser;
    private Date issueDate;
    private String issueDateStr;
    private LicenseFeatureSet featureSet;
    private byte sig[];
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/license/License.desiredAssertionStatus();

}
