// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.i18n;

import com.fitechlabs.xtier.kernel.KernelServiceException;
import com.fitechlabs.xtier.l10n.L10n;

public class I18nException extends KernelServiceException
{

    public I18nException(String s)
    {
        super(s);
    }

    public I18nException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.I18N.TXT2", getMessage());
    }
}
