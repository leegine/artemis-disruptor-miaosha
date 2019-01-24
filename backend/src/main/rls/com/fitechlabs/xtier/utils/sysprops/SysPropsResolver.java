// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.utils.sysprops;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Referenced classes of package com.fitechlabs.xtier.utils.sysprops:
//            SysPropsException

public class SysPropsResolver
{

    public SysPropsResolver()
    {
    }

    public static String resolve(String s)
        throws SysPropsException
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        resolve(s, stringbuffer);
        return stringbuffer.toString();
    }

    public static void resolve(String s, StringBuffer stringbuffer)
        throws SysPropsException
    {
        ArgAssert.nullArg(s, "text");
        ArgAssert.nullArg(stringbuffer, "out");
        Matcher matcher;
        for(matcher = regex.matcher(s); matcher.find();)
        {
            String s1 = matcher.group();
            if(s1.length() < 4)
                throw new SysPropsException(L10n.format("KRNL.UTILS.SYSPROPS.ERR1", s));
            String s2 = s1.substring(2, s1.length() - 1);
            int i = s2.indexOf(':');
            if(i == -1)
            {
                s2 = s2.trim();
                String s3 = System.getProperty(s2);
                if(s3 == null)
                    throw new SysPropsException(L10n.format("KRNL.UTILS.SYSPROPS.ERR2", s2));
                s3 = s3.replaceAll("\\\\", "\\\\\\\\");
                matcher.appendReplacement(stringbuffer, s3);
            } else
            {
                String s4 = System.getProperty(s2.substring(0, i).trim());
                if(s4 == null)
                {
                    matcher.appendReplacement(stringbuffer, s2.substring(i + 1).trim().replaceAll("\\\\", "\\\\\\\\"));
                } else
                {
                    s4 = s4.replaceAll("\\\\", "\\\\\\\\");
                    matcher.appendReplacement(stringbuffer, s4);
                }
            }
        }

        matcher.appendTail(stringbuffer);
    }

    private static final Pattern regex = Pattern.compile("\\@\\{[^}:]+(:[^}]*)?\\}");

}
