// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.email;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.mail.*;

public interface EmailSmtpSession
{

    public abstract void setSmtpProvider(Provider provider);

    public abstract void send()
        throws MessagingException;

    public abstract void setUsername(String s);

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract void setPassword(String s);

    public abstract String getProviderProperty(String s);

    public abstract Map getProviderProperties();

    public abstract String setProviderProperty(String s, String s1);

    public abstract void setFromAddress(String s);

    public abstract String getFromAddress();

    public abstract void addToAddress(String s);

    public abstract void addToAddresses(String as[]);

    public abstract void addCcAddress(String s);

    public abstract void addCcAddresses(String as[]);

    public abstract void addBccAddress(String s);

    public abstract void addBccAddresses(String as[]);

    public abstract List getToAddresses();

    public abstract List getCcAddresses();

    public abstract List getBccAddresses();

    public abstract void setSubject(String s);

    public abstract void setSubject(String s, String s1);

    public abstract String getSubject();

    public abstract void setContent(Object obj);

    public abstract Object getContent();

    public abstract void setSmtpHost(String s);

    public abstract String getSmtpHost();

    public abstract void setSmtpPort(int i);

    public abstract int getSmtpPort();

    public abstract boolean removeToAddress(String s);

    public abstract void removeAllToAddresses();

    public abstract boolean removeCcAddress(String s);

    public abstract void removeAllCcAddresses();

    public abstract boolean removeBccAddress(String s);

    public abstract void removeAllBccAddresses();

    public abstract void setMime(String s);

    public abstract String getMime();

    public abstract void addAttachemnt(BodyPart bodypart);

    public abstract void attachFile(String s)
        throws MessagingException;

    public abstract void attachFile(File file)
        throws MessagingException;

    public abstract List getAttachments();

    public abstract void setEncoding(String s);

    public abstract String getEncoding();
}
