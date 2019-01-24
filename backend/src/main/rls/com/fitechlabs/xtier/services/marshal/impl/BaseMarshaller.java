// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.marshal.impl:
//            MarshalByteBuffer, MarshalCircRefsBuf

abstract class BaseMarshaller
{

    BaseMarshaller(List list)
    {
        byteBufsMap = new HashMap(Utils.getNonRehashCapacity(256));
        circRefsMap = new HashMap(Utils.getNonRehashCapacity(256));
        if(!$assertionsDisabled && list == null)
        {
            throw new AssertionError();
        } else
        {
            factories = list;
            return;
        }
    }

    public void addFactory(MarshallableFactory marshallablefactory)
    {
        ArgAssert.nullArg(marshallablefactory, "factory");
        synchronized(mutex)
        {
            ArrayList arraylist = new ArrayList(factories);
            arraylist.add(marshallablefactory);
            factories = arraylist;
        }
    }

    public boolean removeFactory(MarshallableFactory marshallablefactory)
    {
        ArgAssert.nullArg(marshallablefactory, "factory");
        Object obj = mutex;
        JVM INSTR monitorenter ;
        ArrayList arraylist = new ArrayList(factories);
        boolean flag = arraylist.remove(marshallablefactory);
        factories = arraylist;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    protected List getFactories()
    {
        Object obj = mutex;
        JVM INSTR monitorenter ;
        return factories;
        Exception exception;
        exception;
        throw exception;
    }

    protected byte getType(Class class1)
        throws MarshalException
    {
        if(class1 == (Byte.class))
            return 100;
        if(class1 == (Short.class))
            return 101;
        if(class1 == (Integer.class))
            return 102;
        if(class1 == (Long.class))
            return 103;
        if(class1 == (String.class))
            return 108;
        if(class1 == (Date.class))
            return 109;
        if(class1 == (Float.class))
            return 104;
        if(class1 == (Double.class))
            return 105;
        if(class1 == (Boolean.class))
            return 106;
        if(class1 == (Character.class))
            return 107;
        if(class1 == (ArrayList.class))
            return 111;
        if(class1 == (HashMap.class))
            return 112;
        if(class1 == Byte.TYPE)
            return 120;
        if(class1 == Short.TYPE)
            return 121;
        if(class1 == Integer.TYPE)
            return 122;
        if(class1 == Long.TYPE)
            return 123;
        if(class1 == Float.TYPE)
            return 124;
        if(class1 == Double.TYPE)
            return 125;
        if(class1 == Boolean.TYPE)
            return 126;
        if(class1 == Character.TYPE)
            return 127;
        if(class1 == (Object.class))
            return 119;
        if(class1.isArray())
            return 110;
        if((com.fitechlabs.xtier.services.marshal.Marshallable.class).isAssignableFrom(class1))
            return 113;
        else
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR12", class1.getName()));
    }

    protected MarshalByteBuffer getByteBuf()
    {
        Map map = byteBufsMap;
        JVM INSTR monitorenter ;
        Thread thread = Thread.currentThread();
        MarshalByteBuffer marshalbytebuffer = (MarshalByteBuffer)byteBufsMap.get(thread);
        if(marshalbytebuffer == null)
            byteBufsMap.put(thread, marshalbytebuffer = new MarshalByteBuffer());
        marshalbytebuffer.reset();
        return marshalbytebuffer;
        Exception exception;
        exception;
        throw exception;
    }

    protected MarshalCircRefsBuf getCircRefsBuf(boolean flag)
    {
        Map map = circRefsMap;
        JVM INSTR monitorenter ;
        Thread thread = Thread.currentThread();
        MarshalCircRefsBuf marshalcircrefsbuf = (MarshalCircRefsBuf)circRefsMap.get(thread);
        if(marshalcircrefsbuf == null)
            circRefsMap.put(thread, marshalcircrefsbuf = new MarshalCircRefsBuf());
        marshalcircrefsbuf.reset(flag);
        return marshalcircrefsbuf;
        Exception exception;
        exception;
        throw exception;
    }

    protected Marshallable getMarshObj(List list, short word0)
        throws MarshalException
    {
        if(word0 == 32767)
            return new MarshalObject();
        Object obj = mutex;
        JVM INSTR monitorenter ;
        int i;
        int j;
        i = list.size();
        j = 0;
_L1:
        if(j >= i)
            break MISSING_BLOCK_LABEL_75;
        Marshallable marshallable = ((MarshallableFactory)list.get(j)).tryNewInstance(word0);
        if(marshallable != null)
            return marshallable;
        j++;
          goto _L1
        obj;
        JVM INSTR monitorexit ;
          goto _L2
        Exception exception;
        exception;
        throw exception;
_L2:
        throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR9", new Short(word0)));
    }

    protected Date dotNetDate2Java(long l)
    {
        if(l < 0x89f7ff5f7b58000L)
            throw new IllegalArgumentException(L10n.format("SRVC.MARSHAL.ERR17", new Long(l)));
        else
            return new Date(l / 10000L - 0x3883122cd800L);
    }

    static final byte MRSHL_XTIER_PACKET_JAVA = 113;
    static final byte MRSHL_XTIER_PACKET_NET = 114;
    static final byte MRSHL_REF_OBJ = 51;
    static final byte MRSHL_NULL_OBJ = 52;
    static final byte MRSHL_NOT_NULL_OBJ = 53;
    static final byte MRSHL_TYPE_INT8 = 100;
    static final byte MRSHL_TYPE_INT16 = 101;
    static final byte MRSHL_TYPE_INT32 = 102;
    static final byte MRSHL_TYPE_INT64 = 103;
    static final byte MRSHL_TYPE_FLOAT32 = 104;
    static final byte MRSHL_TYPE_FLOAT64 = 105;
    static final byte MRSHL_TYPE_BOOL = 106;
    static final byte MRSHL_TYPE_CHAR16 = 107;
    static final byte MRSHL_TYPE_UTF8 = 108;
    static final byte MRSHL_TYPE_DATE = 109;
    static final byte MRSHL_TYPE_ARR = 110;
    static final byte MRSHL_TYPE_LIST = 111;
    static final byte MRSHL_TYPE_MAP = 112;
    static final byte MRSHL_TYPE_MRSHL = 113;
    static final byte MRSHL_TYPE_OBJECT = 119;
    static final byte MRSHL_TYPE_INT8_PRIM = 120;
    static final byte MRSHL_TYPE_INT16_PRIM = 121;
    static final byte MRSHL_TYPE_INT32_PRIM = 122;
    static final byte MRSHL_TYPE_INT64_PRIM = 123;
    static final byte MRSHL_TYPE_FLOAT32_PRIM = 124;
    static final byte MRSHL_TYPE_FLOAT64_PRIM = 125;
    static final byte MRSHL_TYPE_BOOL_PRIM = 126;
    static final byte MRSHL_TYPE_CHAR16_PRIM = 127;
    private static final long MIDNIGHT_JAN1_0001_MS = 0x3883122cd800L;
    private static final long MIDNIGHT_JAN1_0001_100NS = 0x89f7ff5f7b58000L;
    private List factories;
    private final Object mutex = new Object();
    private Map byteBufsMap;
    private Map circRefsMap;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(BaseMarshaller.class).desiredAssertionStatus();
    }
}
