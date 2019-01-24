// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.email.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.email.EmailSmtpSession;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.io.File;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

class EmailSmtpSessionImpl
    implements EmailSmtpSession
{

    EmailSmtpSessionImpl()
    {
        providerProps = new HashMap();
        toRecipients = new ArrayList();
        ccRecipients = new ArrayList();
        bccRecipients = new ArrayList();
        attachments = new ArrayList();
        provider = null;
    }

    private void setRecipients(Message message, List list, javax.mail.Message.RecipientType recipienttype)
        throws MessagingException
    {
        int i = list.size();
        if(i != 0)
        {
            InternetAddress ainternetaddress[] = new InternetAddress[i];
            for(int j = 0; j < ainternetaddress.length; j++)
                ainternetaddress[j] = new InternetAddress((String)list.get(j));

            message.setRecipients(recipienttype, ainternetaddress);
        }
    }

    public void send()
        throws MessagingException
    {
        if(from == null)
            throw new MessagingException(L10n.format("SRVC.EMAIL.IMPL.ERR8"));
        if(toRecipients.size() == 0)
            throw new MessagingException(L10n.format("SRVC.EMAIL.IMPL.ERR9"));
        if(content == null)
            throw new MessagingException(L10n.format("SRVC.EMAIL.IMPL.ERR10"));
        if(password != null && username == null || password == null && username != null)
            throw new MessagingException(L10n.format("SRVC.EMAIL.IMPL.ERR12"));
        Properties properties = new Properties();
        properties.putAll(providerProps);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", Integer.toString(port));
        Session session = Session.getInstance(properties, null);
        if(provider != null)
            session.setProvider(provider);
        MimeMessage mimemessage = new MimeMessage(session);
        mimemessage.setFrom(new InternetAddress(from));
        if(subject != null)
            if(subjectCharset == null)
                mimemessage.setSubject(subject);
            else
                mimemessage.setSubject(subject, subjectCharset);
        mimemessage.setSentDate(new Date());
        setRecipients(mimemessage, toRecipients, javax.mail.Message.RecipientType.TO);
        setRecipients(mimemessage, ccRecipients, javax.mail.Message.RecipientType.CC);
        setRecipients(mimemessage, bccRecipients, javax.mail.Message.RecipientType.BCC);
        MimeBodyPart mimebodypart = new MimeBodyPart();
        mimebodypart.setDataHandler(new DataHandler(content, mime != null ? mime : "text/plain"));
        if(encoding != null)
            mimebodypart.setHeader("Content-Transfer-Encoding", encoding);
        MimeMultipart mimemultipart = new MimeMultipart();
        mimemultipart.addBodyPart(mimebodypart);
        int i = attachments.size();
        for(int j = 0; j < i; j++)
            mimemultipart.addBodyPart((BodyPart)attachments.get(j));

        mimemessage.setContent(mimemultipart);
        if(username == null)
        {
            Transport.send(mimemessage);
        } else
        {
            mimemessage.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            transport.sendMessage(mimemessage, mimemessage.getAllRecipients());
            transport.close();
        }
    }

    public void setFromAddress(String s)
    {
        ArgAssert.nullArg(s, "from");
        from = s;
    }

    private void addRecipient(List list, String s)
    {
        if(!list.contains(s))
            list.add(s);
    }

    public void addToAddress(String s)
    {
        ArgAssert.nullArg(s, "to");
        addRecipient(toRecipients, s);
    }

    public void addToAddresses(String as[])
    {
        ArgAssert.nullArg(as, "to");
        for(int i = 0; i < as.length; i++)
            addRecipient(toRecipients, as[i]);

    }

    public void addCcAddress(String s)
    {
        ArgAssert.nullArg(s, "cc");
        addRecipient(ccRecipients, s);
    }

    public void addCcAddresses(String as[])
    {
        ArgAssert.nullArg(as, "cc");
        for(int i = 0; i < as.length; i++)
            addRecipient(ccRecipients, as[i]);

    }

    public void addBccAddress(String s)
    {
        ArgAssert.nullArg(s, "bcc");
        addRecipient(bccRecipients, s);
    }

    public void addBccAddresses(String as[])
    {
        ArgAssert.nullArg(as, "bcc");
        for(int i = 0; i < as.length; i++)
            addRecipient(bccRecipients, as[i]);

    }

    public void setSubject(String s)
    {
        ArgAssert.nullArg(s, "subject");
        subject = s;
    }

    public void setSubject(String s, String s1)
    {
        ArgAssert.nullArg(s, "subject");
        ArgAssert.nullArg(s1, "charset");
        subject = s;
        subjectCharset = s1;
    }

    public void setContent(Object obj)
    {
        ArgAssert.nullArg(obj, "content");
        content = obj;
    }

    public void setSmtpHost(String s)
    {
        ArgAssert.nullArg(s, "host");
        host = s;
    }

    public void setSmtpPort(int i)
    {
        ArgAssert.illegalArg(i > 0, "port");
        port = i;
    }

    public boolean removeToAddress(String s)
    {
        ArgAssert.nullArg(s, "to");
        return toRecipients.remove(s);
    }

    public void removeAllToAddresses()
    {
        toRecipients.clear();
    }

    public boolean removeCcAddress(String s)
    {
        ArgAssert.nullArg(s, "cc");
        return ccRecipients.remove(s);
    }

    public void removeAllCcAddresses()
    {
        ccRecipients.clear();
    }

    public boolean removeBccAddress(String s)
    {
        ArgAssert.nullArg(s, "bcc");
        return bccRecipients.remove(s);
    }

    public void removeAllBccAddresses()
    {
        bccRecipients.clear();
    }

    public void setMime(String s)
    {
        ArgAssert.nullArg(s, "mime");
        mime = s;
    }

    public void addAttachemnt(BodyPart bodypart)
    {
        ArgAssert.nullArg(bodypart, "attachment");
        attachments.add(bodypart);
    }

    public void attachFile(File file)
        throws MessagingException
    {
        ArgAssert.nullArg(file, "file");
        MimeBodyPart mimebodypart = new MimeBodyPart();
        mimebodypart.setDataHandler(new DataHandler(new FileDataSource(file)));
        mimebodypart.setFileName(file.getName());
        addAttachemnt(mimebodypart);
    }

    public void attachFile(String s)
        throws MessagingException
    {
        ArgAssert.nullArg(s, "path");
        MimeBodyPart mimebodypart = new MimeBodyPart();
        mimebodypart.setDataHandler(new DataHandler(new FileDataSource(s)));
        mimebodypart.setFileName((new File(s)).getName());
        addAttachemnt(mimebodypart);
    }

    public void setEncoding(String s)
    {
        ArgAssert.nullArg(s, "encoding");
        ArgAssert.illegalArg(s.equals("7bit") || s.equals("8bit") || s.equals("binary") || s.equals("base64") || s.equals("quoted-printable"), "encoding");
        encoding = s;
    }

    public List getAttachments()
    {
        return attachments;
    }

    public Object getContent()
    {
        return content;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public String getFromAddress()
    {
        return from;
    }

    public String getMime()
    {
        return mime;
    }

    public String getPassword()
    {
        return password;
    }

    public String getProviderProperty(String s)
    {
        return (String)providerProps.get(s);
    }

    public Map getProviderProperties()
    {
        return providerProps;
    }

    public String getSmtpHost()
    {
        return host;
    }

    public int getSmtpPort()
    {
        return port;
    }

    public String getSubject()
    {
        return subject;
    }

    public List getToAddresses()
    {
        return toRecipients;
    }

    public List getCcAddresses()
    {
        return ccRecipients;
    }

    public List getBccAddresses()
    {
        return bccRecipients;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String s)
    {
        ArgAssert.nullArg(s, "password");
        password = s;
    }

    public String setProviderProperty(String s, String s1)
    {
        ArgAssert.nullArg(s, "name");
        return (String)providerProps.put(s, s1);
    }

    public void setUsername(String s)
    {
        ArgAssert.nullArg(s, "username");
        username = s;
    }

    public void setSmtpProvider(Provider provider1)
    {
        ArgAssert.nullArg(provider1, "provider");
        if(!provider1.getProtocol().equals("smtp"))
        {
            throw new IllegalArgumentException(L10n.format("SRVC.EMAIL.IMPL.ERR13", provider1));
        } else
        {
            provider = provider1;
            return;
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.EMAIL.IMPL.TXT1", new Object[] {
            host, new Integer(port), from, subject, subjectCharset, mime, username
        });
    }

    private String host;
    private int port;
    private String from;
    private String subject;
    private String subjectCharset;
    private String encoding;
    private Object content;
    private String mime;
    private String username;
    private String password;
    private Map providerProps;
    private List toRecipients;
    private List ccRecipients;
    private List bccRecipients;
    private List attachments;
    private Provider provider;
}
