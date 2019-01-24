// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.cache;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.Collection;

// Referenced classes of package com.fitechlabs.xtier.services.cache:
//            CacheException

public class CacheTxReplyFailureException extends CacheException
{

    public CacheTxReplyFailureException(String s)
    {
        super(s);
    }

    public CacheTxReplyFailureException(String s, Collection collection)
    {
        super(s);
        notRepliedNodes = collection;
    }

    public CacheTxReplyFailureException(String s, Throwable throwable, Collection collection)
    {
        super(s, throwable);
        notRepliedNodes = collection;
    }

    public Collection getNotRepliedNodes()
    {
        return notRepliedNodes;
    }

    public void setNotRepliedNodes(Collection collection)
    {
        notRepliedNodes = collection;
    }

    public String toString()
    {
        return L10n.format("SRVC.CACHE.TXT3", getMessage());
    }

    private Collection notRepliedNodes;
}
