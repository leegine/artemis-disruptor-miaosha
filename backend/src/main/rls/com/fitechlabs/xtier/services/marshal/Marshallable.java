// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;

import java.util.Map;

// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            MarshalException

public interface Marshallable
{

    public abstract short typeGuid();

    public abstract Map getObjs()
        throws MarshalException;

    public abstract void setObjs(Map map)
        throws MarshalException;

    public abstract void onMarshal()
        throws MarshalException;

    public abstract void onDemarshal()
        throws MarshalException;
}
