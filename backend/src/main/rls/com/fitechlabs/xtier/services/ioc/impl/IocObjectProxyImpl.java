// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.ioc.impl;

import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.ioc.IocObjectProxy;
import com.fitechlabs.xtier.services.ioc.IocServiceException;

class IocObjectProxyImpl
    implements IocObjectProxy
{

    IocObjectProxyImpl(IocDescriptor iocdescriptor)
    {
        ioc = null;
        if(!$assertionsDisabled && iocdescriptor == null)
        {
            throw new AssertionError();
        } else
        {
            ioc = iocdescriptor;
            return;
        }
    }

    public String getUid()
    {
        return ioc.getUid();
    }

    public int getPolicy()
        throws IocServiceException
    {
        try
        {
            return ioc.getPolicy();
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new IocServiceException(iocdescriptorexception.getMessage(), iocdescriptorexception);
        }
    }

    public boolean isReference()
    {
        return ioc.getIocRefUid() != null;
    }

    public String getRefUid()
    {
        return ioc.getIocRefUid();
    }

    public String getJavaImpl()
    {
        return ioc.getJavaImpl();
    }

    public Object makeIocObject()
        throws IocServiceException
    {
        try
        {
            return ioc.createNewObj();
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new IocServiceException(L10n.format("SRVC.IOC.ERR5", iocdescriptorexception.getMessage()), iocdescriptorexception);
        }
    }

    public Object makeIocObject(Object obj)
        throws IocServiceException
    {
        try
        {
            return ioc.createNewObj(obj);
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new IocServiceException(L10n.format("SRVC.IOC.ERR5", iocdescriptorexception.getMessage()), iocdescriptorexception);
        }
    }

    public String toString()
    {
        return L10n.format("KRNL.IOC.TXT7", getUid());
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

    private IocDescriptor ioc;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(IocObjectProxyImpl.class).desiredAssertionStatus();
    }
}
