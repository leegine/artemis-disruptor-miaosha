// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms;


// Referenced classes of package com.fitechlabs.xtier.services.jms:
//            JmsReplyContext

public interface JmsReplyObject
{

    public abstract JmsReplyContext getReplyCtx();

    public abstract Object getObj();
}
