// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.i18n;

import com.fitechlabs.xtier.kernel.KernelServiceRuntimeException;
import com.fitechlabs.xtier.l10n.L10n;

public class I18nMissingResourceException extends KernelServiceRuntimeException
{

    public I18nMissingResourceException(String s)
    {
        super(s);
    }

    public I18nMissingResourceException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.I18N.TXT1", getMessage());
    }
}
