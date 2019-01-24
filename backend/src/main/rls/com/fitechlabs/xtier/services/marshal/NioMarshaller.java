// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;

import java.nio.ByteBuffer;
import java.util.Date;

// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            MarshalException, Marshallable

public interface NioMarshaller
{

    public abstract void marshalObj(Marshallable marshallable, ByteBuffer bytebuffer)
        throws MarshalException;

    public abstract Marshallable demarshalObj(ByteBuffer bytebuffer)
        throws MarshalException;

    public abstract Marshallable demarshalObj(ByteBuffer bytebuffer, Marshallable marshallable)
        throws MarshalException;

    public abstract void encodeInt8(byte byte0, ByteBuffer bytebuffer);

    public abstract byte decodeInt8(ByteBuffer bytebuffer);

    public abstract void encodeInt16(short word0, ByteBuffer bytebuffer);

    public abstract short decodeInt16(ByteBuffer bytebuffer);

    public abstract void encodeInt32(int i, ByteBuffer bytebuffer);

    public abstract int decodeInt32(ByteBuffer bytebuffer);

    public abstract void encodeInt64(long l, ByteBuffer bytebuffer);

    public abstract long decodeInt64(ByteBuffer bytebuffer);

    public abstract void encodeFloat32(float f, ByteBuffer bytebuffer);

    public abstract float decodeFloat32(ByteBuffer bytebuffer);

    public abstract void encodeFloat64(double d, ByteBuffer bytebuffer);

    public abstract double decodeFloat64(ByteBuffer bytebuffer);

    public abstract void encodeChar16(char c, ByteBuffer bytebuffer);

    public abstract char decodeChar16(ByteBuffer bytebuffer);

    public abstract void encodeUtf8Str(String s, ByteBuffer bytebuffer);

    public abstract String decodeUtf8Str(ByteBuffer bytebuffer);

    public abstract void encodeBool(boolean flag, ByteBuffer bytebuffer);

    public abstract boolean decodeBool(ByteBuffer bytebuffer);

    public abstract void encodeDate(Date date, ByteBuffer bytebuffer);

    public abstract Date decodeDate(ByteBuffer bytebuffer);
}
