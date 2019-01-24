// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache.keyattrs;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import java.io.Serializable;

public class CacheKeyAttrsAdapter
    implements CacheKeyAttrs, Serializable
{

    public CacheKeyAttrsAdapter()
    {
        typeId = 0x8000000000000000L;
        grpId = 0x8000000000000000L;
        isDepended = false;
    }

    public CacheKeyAttrsAdapter(long l, long l1, boolean flag)
    {
        typeId = 0x8000000000000000L;
        grpId = 0x8000000000000000L;
        isDepended = false;
        typeId = l;
        grpId = l1;
        isDepended = flag;
    }

    public long getTypeId()
    {
        return typeId;
    }

    public long getGroupId()
    {
        return grpId;
    }

    public boolean isDepended()
    {
        return isDepended;
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(!(obj instanceof CacheKeyAttrs))
        {
            return false;
        } else
        {
            CacheKeyAttrs cachekeyattrs = (CacheKeyAttrs)obj;
            return cachekeyattrs.getTypeId() == typeId && cachekeyattrs.getGroupId() == grpId && cachekeyattrs.isDepended() == isDepended;
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT15", new Long(typeId), new Long(grpId), Boolean.valueOf(isDepended));
    }

    private long typeId;
    private long grpId;
    private boolean isDepended;
}
