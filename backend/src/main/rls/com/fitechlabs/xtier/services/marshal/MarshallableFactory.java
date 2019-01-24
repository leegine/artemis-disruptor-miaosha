// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;


// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            Marshallable

public interface MarshallableFactory
{

    public abstract Marshallable tryNewInstance(short word0);
}
