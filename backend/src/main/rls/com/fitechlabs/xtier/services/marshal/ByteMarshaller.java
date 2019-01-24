// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.marshal;

import java.util.Date;

// Referenced classes of package com.fitechlabs.xtier.services.marshal:
//            MarshalException, ByteDemarshalResult, Marshallable, ByteUtf8DecodeResult

public interface ByteMarshaller
{

    public abstract ByteDemarshalResult demarshalObj(byte abyte0[], int i)
        throws MarshalException;

    public abstract int demarshalObj(byte abyte0[], int i, Marshallable marshallable)
        throws MarshalException;

    public abstract byte[] marshalObj(Marshallable marshallable)
        throws MarshalException;

    public abstract int marshalObj(Marshallable marshallable, byte abyte0[], int i)
        throws MarshalException;

    public abstract int encodeInt8(byte byte0, byte abyte0[], int i);

    public abstract byte decodeInt8(byte abyte0[], int i);

    public abstract int encodeInt16(short word0, byte abyte0[], int i);

    public abstract short decodeInt16(byte abyte0[], int i);

    public abstract int encodeChar16(char c, byte abyte0[], int i);

    public abstract char decodeChar16(byte abyte0[], int i);

    public abstract int encodeInt32(int i, byte abyte0[], int j);

    public abstract int decodeInt32(byte abyte0[], int i);

    public abstract int encodeInt64(long l, byte abyte0[], int i);

    public abstract long decodeInt64(byte abyte0[], int i);

    public abstract int encodeFloat32(float f, byte abyte0[], int i);

    public abstract float decodeFloat32(byte abyte0[], int i);

    public abstract int encodeFloat64(double d, byte abyte0[], int i);

    public abstract double decodeFloat64(byte abyte0[], int i);

    public abstract int encodeBool(boolean flag, byte abyte0[], int i);

    public abstract boolean decodeBool(byte abyte0[], int i);

    public abstract int encodeUtf8Str(String s, byte abyte0[], int i);

    public abstract ByteUtf8DecodeResult decodeUtf8Str(byte abyte0[], int i);

    public abstract int encodeDate(Date date, byte abyte0[], int i);

    public abstract Date decodeDate(byte abyte0[], int i);
}
