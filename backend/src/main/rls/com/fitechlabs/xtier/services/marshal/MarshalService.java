// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;

import com.fitechlabs.xtier.kernel.KernelService;

// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            MarshallableFactory, NioMarshaller, IoMarshaller, ByteMarshaller

public interface MarshalService
    extends KernelService
{

    public abstract void registerFactory(MarshallableFactory marshallablefactory);

    public abstract void unregisterFactory(MarshallableFactory marshallablefactory);

    public abstract NioMarshaller getNioMarshaller();

    public abstract IoMarshaller getIoMarshaller();

    public abstract ByteMarshaller getByteMarshaller();
}
