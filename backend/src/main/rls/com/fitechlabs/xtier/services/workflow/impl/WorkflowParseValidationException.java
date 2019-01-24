// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.l10n.L10n;

class WorkflowParseValidationException extends Exception
{

    WorkflowParseValidationException(String s)
    {
        super(s);
    }

    WorkflowParseValidationException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public String toString()
    {
        return L10n.format("SRVC.WF.TXT2", getMessage());
    }
}
