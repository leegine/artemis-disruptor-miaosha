// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;

import java.io.*;
import java.util.Date;

// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            MarshalException, Marshallable

public interface IoMarshaller
{

    public abstract Marshallable demarshalObj(InputStream inputstream)
        throws MarshalException, IOException;

    public abstract Marshallable demarshalObj(InputStream inputstream, Marshallable marshallable)
        throws MarshalException, IOException;

    public abstract void marshalObj(Marshallable marshallable, OutputStream outputstream)
        throws MarshalException, IOException;

    public abstract void encodeInt8(byte byte0, OutputStream outputstream)
        throws IOException;

    public abstract byte decodeInt8(InputStream inputstream)
        throws IOException;

    public abstract void encodeInt16(short word0, OutputStream outputstream)
        throws IOException;

    public abstract short decodeInt16(InputStream inputstream)
        throws IOException;

    public abstract void encodeInt32(int i, OutputStream outputstream)
        throws IOException;

    public abstract int decodeInt32(InputStream inputstream)
        throws IOException;

    public abstract void encodeInt64(long l, OutputStream outputstream)
        throws IOException;

    public abstract long decodeInt64(InputStream inputstream)
        throws IOException;

    public abstract void encodeChar16(char c, OutputStream outputstream)
        throws IOException;

    public abstract char decodeChar16(InputStream inputstream)
        throws IOException;

    public abstract void encodeBool(boolean flag, OutputStream outputstream)
        throws IOException;

    public abstract boolean decodeBool(InputStream inputstream)
        throws IOException;

    public abstract void encodeFloat32(float f, OutputStream outputstream)
        throws IOException;

    public abstract float decodeFloat32(InputStream inputstream)
        throws IOException;

    public abstract void encodeFloat64(double d, OutputStream outputstream)
        throws IOException;

    public abstract double decodeFloat64(InputStream inputstream)
        throws IOException;

    public abstract void encodeUtf8Str(String s, OutputStream outputstream)
        throws IOException;

    public abstract String decodeUtf8Str(InputStream inputstream)
        throws IOException;

    public abstract void encodeDate(Date date, OutputStream outputstream)
        throws IOException;

    public abstract Date decodeDate(InputStream inputstream)
        throws IOException;
}
