// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.ioc;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.Utils;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.kernel.ioc:
//            IocArg

public class IocCall
{

    public IocCall(String s)
    {
        method = null;
        args = new ArrayList();
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            method = s;
            return;
        }
    }

    public void addArg(IocArg iocarg)
    {
        if(!$assertionsDisabled && iocarg == null)
        {
            throw new AssertionError();
        } else
        {
            args.add(iocarg);
            return;
        }
    }

    public List getArgs()
    {
        return args;
    }

    public String getMethod()
    {
        return method;
    }

    public String toString()
    {
        return L10n.format("KRNL.IOC.TXT6", method, Utils.list2Str(args));
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

    private String method;
    private List args;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(IocCall.class).desiredAssertionStatus();
    }
}
