// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            Marshallable, MarshalException, MarshalService, ByteMarshaller

public class MarshalObject
    implements Marshallable, Externalizable
{

    public void copy(Marshallable marshallable)
        throws MarshalException
    {
        data.putAll(marshallable.getObjs());
    }

    public void onMarshal()
        throws MarshalException
    {
    }

    public void onDemarshal()
        throws MarshalException
    {
    }

    protected MarshalObject(short word0)
    {
        data = null;
        typeGuid = 32767;
        typeGuid = word0;
        data = new HashMap();
    }

    protected MarshalObject(short word0, int i, float f)
    {
        data = null;
        typeGuid = 32767;
        ArgAssert.illegalRange(i >= 0, "capacity", "capacity >= 0");
        ArgAssert.illegalRange(f >= 0.0F, "loadFactor", "loadFactor >= 0");
        typeGuid = word0;
        data = new HashMap(i, f);
    }

    protected MarshalObject(short word0, int i)
    {
        data = null;
        typeGuid = 32767;
        ArgAssert.illegalRange(i >= 0, "capacity", "capacity >= 0");
        typeGuid = word0;
        data = new HashMap(i);
    }

    public MarshalObject()
    {
        data = null;
        typeGuid = 32767;
        data = new HashMap();
    }

    public MarshalObject(Marshallable marshallable)
        throws MarshalException
    {
        data = null;
        typeGuid = 32767;
        data = new HashMap();
        copy(marshallable);
    }

    public MarshalObject(Map map)
    {
        data = null;
        typeGuid = 32767;
        ArgAssert.nullArg(map, "data");
        data = map;
    }

    public MarshalObject(int i, float f)
    {
        data = null;
        typeGuid = 32767;
        ArgAssert.illegalRange(i >= 0, "capacity", "capacity >= 0");
        ArgAssert.illegalRange(f >= 0.0F, "loadFactor", "loadFactor >= 0");
        data = new HashMap(i, f);
    }

    public MarshalObject(int i)
    {
        this(Utils.getNonRehashCapacity(i), 0.75F);
    }

    public short typeGuid()
    {
        return typeGuid;
    }

    public final Map getObjs()
    {
        return data;
    }

    public final void setObjs(Map map)
    {
        data.clear();
        data.putAll(map);
    }

    public final Object remove(Object obj)
    {
        return data.remove(obj);
    }

    public final void clear()
    {
        data.clear();
    }

    public final int size()
    {
        return data.size();
    }

    public final boolean containsName(Object obj)
    {
        return data.containsKey(obj);
    }

    public final boolean containsObj(Object obj)
    {
        return data.containsValue(obj);
    }

    public final MarshalObject putInt8(Object obj, byte byte0)
    {
        data.put(obj, new Byte(byte0));
        return this;
    }

    public final MarshalObject putInt8(Object obj, Byte byte1)
    {
        data.put(obj, byte1);
        return this;
    }

    public final MarshalObject putInt16(Object obj, short word0)
    {
        data.put(obj, new Short(word0));
        return this;
    }

    public final MarshalObject putInt16(Object obj, Short short1)
    {
        data.put(obj, short1);
        return this;
    }

    public final MarshalObject putInt32(Object obj, int i)
    {
        data.put(obj, new Integer(i));
        return this;
    }

    public final MarshalObject putInt32(Object obj, Integer integer)
    {
        data.put(obj, integer);
        return this;
    }

    public final MarshalObject putInt64(Object obj, long l)
    {
        data.put(obj, new Long(l));
        return this;
    }

    public final MarshalObject putInt64(Object obj, Long long1)
    {
        data.put(obj, long1);
        return this;
    }

    public final MarshalObject putChar16(Object obj, char c)
    {
        data.put(obj, new Character(c));
        return this;
    }

    public final MarshalObject putChar16(Object obj, Character character)
    {
        data.put(obj, character);
        return this;
    }

    public final MarshalObject putFloat32(Object obj, float f)
    {
        data.put(obj, new Float(f));
        return this;
    }

    public final MarshalObject putFloat32(Object obj, Float float1)
    {
        data.put(obj, float1);
        return this;
    }

    public final MarshalObject putFloat64(Object obj, double d)
    {
        data.put(obj, new Double(d));
        return this;
    }

    public final MarshalObject putFloat64(Object obj, Double double1)
    {
        data.put(obj, double1);
        return this;
    }

    public final MarshalObject putBool(Object obj, boolean flag)
    {
        data.put(obj, new Boolean(flag));
        return this;
    }

    public final MarshalObject putBool(Object obj, Boolean boolean1)
    {
        data.put(obj, boolean1);
        return this;
    }

    public final MarshalObject putUtf8Str(Object obj, String s)
    {
        data.put(obj, s);
        return this;
    }

    public final MarshalObject putDate(Object obj, Date date)
    {
        data.put(obj, date);
        return this;
    }

    public final MarshalObject putList(Object obj, List list)
    {
        data.put(obj, list);
        return this;
    }

    public final MarshalObject putMarshalObj(Object obj, Marshallable marshallable)
    {
        data.put(obj, marshallable);
        return this;
    }

    public final MarshalObject putMap(Object obj, Map map)
    {
        data.put(obj, map);
        return this;
    }

    public final MarshalObject putArr(Object obj, Object obj1)
    {
        data.put(obj, obj1);
        return this;
    }

    public final byte getInt8(Object obj)
    {
        return ((Byte)data.get(obj)).byteValue();
    }

    public final short getInt16(Object obj)
    {
        return ((Short)data.get(obj)).shortValue();
    }

    public final int getInt32(Object obj)
    {
        return ((Integer)data.get(obj)).intValue();
    }

    public final long getInt64(Object obj)
    {
        return ((Long)data.get(obj)).longValue();
    }

    public final char getChar16(Object obj)
    {
        return ((Character)data.get(obj)).charValue();
    }

    public final boolean getBool(Object obj)
    {
        return ((Boolean)data.get(obj)).booleanValue();
    }

    public final String getUtf8Str(Object obj)
    {
        return (String)data.get(obj);
    }

    public final List getList(Object obj)
    {
        return (List)data.get(obj);
    }

    public final Map getMap(Object obj)
    {
        return (Map)data.get(obj);
    }

    public final Object getArr(Object obj)
    {
        return data.get(obj);
    }

    public final float getFloat32(Object obj)
    {
        return ((Float)data.get(obj)).floatValue();
    }

    public final double getFloat64(Object obj)
    {
        return ((Double)data.get(obj)).doubleValue();
    }

    public final Object getBoxed(Object obj)
    {
        return data.get(obj);
    }

    public final Object getBoxedNotNull(Object obj)
    {
        Object obj1 = data.get(obj);
        if(obj1 == null)
            throw new NullPointerException(obj.toString());
        else
            return obj1;
    }

    public final Marshallable getMarshalObj(Object obj)
    {
        return (Marshallable)data.get(obj);
    }

    public final Marshallable getMarshalObjNotNull(Object obj)
    {
        Marshallable marshallable = (Marshallable)data.get(obj);
        if(marshallable == null)
            throw new NullPointerException(obj.toString());
        else
            return marshallable;
    }

    public void readExternal(ObjectInput objectinput)
        throws IOException, ClassNotFoundException
    {
        ByteMarshaller bytemarshaller = XtierKernel.getInstance().marshal().getByteMarshaller();
        clear();
        try
        {
            byte abyte0[] = new byte[objectinput.readInt()];
            objectinput.readFully(abyte0);
            bytemarshaller.demarshalObj(abyte0, 0, this);
        }
        catch(MarshalException marshalexception)
        {
            IOException ioexception = new IOException(L10n.format("SRVC.MARSHAL.ERR15"));
            ioexception.initCause(marshalexception);
            throw ioexception;
        }
    }

    public void writeExternal(ObjectOutput objectoutput)
        throws IOException
    {
        ByteMarshaller bytemarshaller = XtierKernel.getInstance().marshal().getByteMarshaller();
        try
        {
            byte abyte0[] = bytemarshaller.marshalObj(this);
            objectoutput.writeInt(abyte0.length);
            objectoutput.write(abyte0);
        }
        catch(MarshalException marshalexception)
        {
            IOException ioexception = new IOException(L10n.format("SRVC.MARSHAL.ERR15"));
            ioexception.initCause(marshalexception);
            throw ioexception;
        }
    }

    public String toString()
    {
        return L10n.format("SRVC.MARSHAL.TXT3", Utils.map2Str(data));
    }

    public static final short TYPE_GUID = 32767;
    private Map data;
    private short typeGuid;
}
