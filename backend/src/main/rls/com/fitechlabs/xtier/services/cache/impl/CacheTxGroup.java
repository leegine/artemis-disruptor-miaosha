// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxEntry, CacheTxObject, CacheGroup, CacheObject

public class CacheTxGroup
    implements CacheTxObject
{

    CacheTxGroup(CacheGroup cachegroup, boolean flag)
    {
        isEnlisted = false;
        if(!$assertionsDisabled && cachegroup == null)
        {
            throw new AssertionError();
        } else
        {
            group = cachegroup;
            isRemoteTx = flag;
            return;
        }
    }

    public boolean isRemoteTx()
    {
        return isRemoteTx;
    }

    public boolean isEnlisted()
    {
        return isEnlisted;
    }

    public void enlist()
    {
        isEnlisted = true;
    }

    public CacheGroup getGroup()
    {
        return group;
    }

    public CacheObject getObj()
    {
        return group;
    }

    public Object getTxKey()
    {
        return group.getGroupId();
    }

    public boolean hasDepended()
    {
        return depended != null && !depended.isEmpty();
    }

    public boolean hasNotDepended()
    {
        return notDepended != null && !notDepended.isEmpty();
    }

    public Map getDepended()
    {
        return depended;
    }

    public Map getNotDepended()
    {
        return notDepended;
    }

    public Object getUserArgs()
    {
        return userArgs;
    }

    public void setUserArgs(Object obj)
    {
        userArgs = obj;
    }

    public CacheKeyAttrs getKeyAttrs()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return null;
    }

    public byte getOp()
    {
        return 4;
    }

    public Object add(CacheTxObject cachetxobject)
    {
        if(!$assertionsDisabled && cachetxobject == null)
            throw new AssertionError();
        else
            return fetchMap(cachetxobject.getKeyAttrs().isDepended(), true).put(cachetxobject.getTxKey(), cachetxobject);
    }

    public Object remove(CacheTxObject cachetxobject)
    {
        if(!$assertionsDisabled && cachetxobject == null)
        {
            throw new AssertionError();
        } else
        {
            Map map = fetchMap(cachetxobject.getKeyAttrs().isDepended(), false);
            return map != null ? map.get(cachetxobject.getTxKey()) : null;
        }
    }

    private Map fetchMap(boolean flag, boolean flag1)
    {
        if(flag)
        {
            if(depended == null && flag1)
                depended = new HashMap();
            return depended;
        }
        if(notDepended == null && flag1)
            notDepended = new HashMap();
        return notDepended;
    }

    public void invalidate()
    {
        if(!$assertionsDisabled && isRemoteTx)
        {
            throw new AssertionError();
        } else
        {
            invalidateDepended();
            invalidateNotDepended();
            return;
        }
    }

    public void invalidateDepended()
    {
        if(!$assertionsDisabled && isRemoteTx)
            throw new AssertionError();
        if(depended != null)
        {
            CacheTxEntry cachetxentry;
            for(Iterator iterator = depended.values().iterator(); iterator.hasNext(); cachetxentry.invalidate())
                cachetxentry = (CacheTxEntry)iterator.next();

        }
    }

    public void invalidateNotDepended()
    {
        if(!$assertionsDisabled && isRemoteTx)
            throw new AssertionError();
        if(notDepended != null)
        {
            CacheTxEntry cachetxentry;
            for(Iterator iterator = notDepended.values().iterator(); iterator.hasNext(); cachetxentry.invalidate())
                cachetxentry = (CacheTxEntry)iterator.next();

        }
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

    private final boolean isRemoteTx;
    private final CacheGroup group;
    private Map depended;
    private Map notDepended;
    private boolean isEnlisted;
    private Object userArgs;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheTxGroup.class).desiredAssertionStatus();
    }
}
