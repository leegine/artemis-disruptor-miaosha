// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.email;

import com.fitechlabs.xtier.kernel.KernelService;
import java.io.File;
import javax.mail.MessagingException;

// Referenced classes of package com.fitechlabs.xtier.services.email:
//            EmailSmtpSession

public interface EmailService
    extends KernelService
{

    public abstract EmailSmtpSession getSmtpSession();

    public abstract String getDfltFromAddress();

    public abstract String getDfltSmtpHost();

    public abstract int getDfltSmtpPort();

    public abstract String getDfltEncoding();

    public abstract String getDfltMime();

    public abstract void send(String s, String s1, String s2, Object obj, String s3, File file)
        throws MessagingException;

    public abstract void send(String as[], String as1[], String as2[], Object obj, String s, File afile[])
        throws MessagingException;
}
