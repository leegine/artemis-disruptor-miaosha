// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.lang.reflect.Array;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.marshal.impl:
//            BaseMarshaller, ByteDemarshalResultImpl, ByteArrResult, ByteUtf8DecodeResultImpl,
//            MarshalCircRefsBuf, MarshalByteBuffer, ArrayInfo

public class ByteMarshallerImpl extends BaseMarshaller
    implements ByteMarshaller
{

    ByteMarshallerImpl(List list)
    {
        super(list);
    }

    public ByteDemarshalResult demarshalObj(byte abyte0[], int i)
        throws MarshalException
    {
        try
        {
            byte byte0 = decodeInt8(abyte0, i);
            i++;
            if(byte0 != 113 && byte0 != 114)
                throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
            else
                return readMrshl(abyte0, i, null, getFactories(), getCircRefsBuf(false));
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public int demarshalObj(byte abyte0[], int i, Marshallable marshallable)
        throws MarshalException
    {
        try
        {
            byte byte0 = decodeInt8(abyte0, i);
            i++;
            if(byte0 != 113 && byte0 != 114)
                throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
            ByteDemarshalResult bytedemarshalresult = readMrshl(abyte0, i, marshallable, getFactories(), getCircRefsBuf(false));
            if(!$assertionsDisabled && bytedemarshalresult.getObject() != marshallable)
                throw new AssertionError();
            else
                return bytedemarshalresult.getOffset();
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public byte[] marshalObj(Marshallable marshallable)
        throws MarshalException
    {
        try
        {
            MarshalCircRefsBuf marshalcircrefsbuf = getCircRefsBuf(true);
            MarshalByteBuffer marshalbytebuffer = getByteBuf();
            marshalcircrefsbuf.add(marshallable);
            encodeInt8(marshalbytebuffer, (byte)113);
            writeMrshl(marshalbytebuffer, marshallable, marshalcircrefsbuf);
            return marshalbytebuffer.toArray();
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public int marshalObj(Marshallable marshallable, byte abyte0[], int i)
        throws MarshalException
    {
        try
        {
            MarshalCircRefsBuf marshalcircrefsbuf = getCircRefsBuf(true);
            marshalcircrefsbuf.add(marshallable);
            i = encodeInt8((byte)113, abyte0, i);
            i = writeMrshl(abyte0, i, marshallable, marshalcircrefsbuf);
            return i;
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    private int writeMrshl(byte abyte0[], int i, Marshallable marshallable, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        Map map = marshallable.getObjs();
        i = encodeInt16(marshallable.typeGuid(), abyte0, i);
        i = encodeInt32(map.size(), abyte0, i);
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            i = marshalElem(abyte0, i, entry.getKey(), (byte)-1, marshalcircrefsbuf);
            i = marshalElem(abyte0, i, entry.getValue(), (byte)-1, marshalcircrefsbuf);
        }

        return i;
    }

    private void writeMrshl(MarshalByteBuffer marshalbytebuffer, Marshallable marshallable, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        marshallable.onMarshal();
        Map map = marshallable.getObjs();
        encodeInt16(marshalbytebuffer, marshallable.typeGuid());
        encodeInt32(marshalbytebuffer, map.size());
        Map.Entry entry;
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); marshalElem(marshalbytebuffer, entry.getValue(), (byte)-1, marshalcircrefsbuf))
        {
            entry = (Map.Entry)iterator.next();
            marshalElem(marshalbytebuffer, entry.getKey(), (byte)-1, marshalcircrefsbuf);
        }

    }

    private int writeArr(byte abyte0[], int i, Object obj, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        if(!$assertionsDisabled && !obj.getClass().isArray())
            throw new AssertionError();
        byte byte0 = getType(obj.getClass().getComponentType());
label0:
        switch(byte0)
        {
        case 120: // 'x'
            byte abyte1[] = (byte[])obj;
            i = encodeInt32(abyte1.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int j = 0; j < abyte1.length; j++)
                i = encodeInt8(abyte1[j], abyte0, i);

            break;

        case 121: // 'y'
            short aword0[] = (short[])obj;
            i = encodeInt32(aword0.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int k = 0; k < aword0.length; k++)
                i = encodeInt16(aword0[k], abyte0, i);

            break;

        case 122: // 'z'
            int ai[] = (int[])obj;
            i = encodeInt32(ai.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int l = 0; l < ai.length; l++)
                i = encodeInt32(ai[l], abyte0, i);

            break;

        case 123: // '{'
            long al[] = (long[])obj;
            i = encodeInt32(al.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int i1 = 0; i1 < al.length; i1++)
                i = encodeInt64(al[i1], abyte0, i);

            break;

        case 124: // '|'
            float af[] = (float[])obj;
            i = encodeInt32(af.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int j1 = 0; j1 < af.length; j1++)
                i = encodeFloat32(af[j1], abyte0, i);

            break;

        case 125: // '}'
            double ad[] = (double[])obj;
            i = encodeInt32(ad.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int k1 = 0; k1 < ad.length; k1++)
                i = encodeFloat64(ad[k1], abyte0, i);

            break;

        case 127: // '\177'
            char ac[] = (char[])obj;
            i = encodeInt32(ac.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int l1 = 0; l1 < ac.length; l1++)
                i = encodeChar16(ac[l1], abyte0, i);

            break;

        case 126: // '~'
            boolean aflag[] = (boolean[])obj;
            i = encodeInt32(aflag.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            for(int i2 = 0; i2 < aflag.length; i2++)
                i = encodeBool(aflag[i2], abyte0, i);

            break;

        case 100: // 'd'
            Byte abyte[] = (Byte[])obj;
            i = encodeInt32(abyte.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int j2 = 0;
            do
            {
                if(j2 >= abyte.length)
                    break label0;
                Byte byte2 = abyte[j2];
                if(byte2 == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeInt8(byte2.byteValue(), abyte0, i);
                }
                j2++;
            } while(true);

        case 106: // 'j'
            Boolean aboolean[] = (Boolean[])obj;
            i = encodeInt32(aboolean.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int k2 = 0;
            do
            {
                if(k2 >= aboolean.length)
                    break label0;
                Boolean boolean1 = aboolean[k2];
                if(boolean1 == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeBool(boolean1.booleanValue(), abyte0, i);
                }
                k2++;
            } while(true);

        case 107: // 'k'
            Character acharacter[] = (Character[])obj;
            i = encodeInt32(acharacter.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int l2 = 0;
            do
            {
                if(l2 >= acharacter.length)
                    break label0;
                Character character = acharacter[l2];
                if(character == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeChar16(character.charValue(), abyte0, i);
                }
                l2++;
            } while(true);

        case 101: // 'e'
            Short ashort[] = (Short[])obj;
            i = encodeInt32(ashort.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int i3 = 0;
            do
            {
                if(i3 >= ashort.length)
                    break label0;
                Short short1 = ashort[i3];
                if(short1 == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeInt16(short1.shortValue(), abyte0, i);
                }
                i3++;
            } while(true);

        case 102: // 'f'
            Integer ainteger[] = (Integer[])obj;
            i = encodeInt32(ainteger.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int j3 = 0;
            do
            {
                if(j3 >= ainteger.length)
                    break label0;
                Integer integer = ainteger[j3];
                if(integer == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeInt32(integer.intValue(), abyte0, i);
                }
                j3++;
            } while(true);

        case 103: // 'g'
            Long along[] = (Long[])obj;
            i = encodeInt32(along.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int k3 = 0;
            do
            {
                if(k3 >= along.length)
                    break label0;
                Long long1 = along[k3];
                if(long1 == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeInt64(long1.longValue(), abyte0, i);
                }
                k3++;
            } while(true);

        case 104: // 'h'
            Float afloat[] = (Float[])obj;
            i = encodeInt32(afloat.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int l3 = 0;
            do
            {
                if(l3 >= afloat.length)
                    break label0;
                Float float1 = afloat[l3];
                if(float1 == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeFloat32(float1.floatValue(), abyte0, i);
                }
                l3++;
            } while(true);

        case 105: // 'i'
            Double adouble[] = (Double[])obj;
            i = encodeInt32(adouble.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int i4 = 0;
            do
            {
                if(i4 >= adouble.length)
                    break label0;
                Double double1 = adouble[i4];
                if(double1 == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeFloat64(double1.doubleValue(), abyte0, i);
                }
                i4++;
            } while(true);

        case 108: // 'l'
            String as[] = (String[])obj;
            i = encodeInt32(as.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int j4 = 0;
            do
            {
                if(j4 >= as.length)
                    break label0;
                String s = as[j4];
                if(s == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeUtf8Str(s, abyte0, i);
                }
                j4++;
            } while(true);

        case 109: // 'm'
            Date adate[] = (Date[])obj;
            i = encodeInt32(adate.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            int k4 = 0;
            do
            {
                if(k4 >= adate.length)
                    break label0;
                Date date = adate[k4];
                if(date == null)
                {
                    i = encodeInt8((byte)52, abyte0, i);
                } else
                {
                    i = encodeInt8((byte)53, abyte0, i);
                    i = encodeDate(date, abyte0, i);
                }
                k4++;
            } while(true);

        case 110: // 'n'
        case 111: // 'o'
        case 112: // 'p'
        case 113: // 'q'
        case 119: // 'w'
            Object aobj[] = (Object[])obj;
            i = encodeInt32(aobj.length, abyte0, i);
            i = encodeInt8(byte0, abyte0, i);
            byte byte1 = byte0 != 119 ? byte0 : -1;
            for(int l4 = 0; l4 < aobj.length; l4++)
                i = marshalElem(abyte0, i, aobj[l4], byte1, marshalcircrefsbuf);

            break;

        case 114: // 'r'
        case 115: // 's'
        case 116: // 't'
        case 117: // 'u'
        case 118: // 'v'
        default:
            if(!$assertionsDisabled)
                throw new AssertionError("Invalid object type: " + byte0);
            break;
        }
        return i;
    }

    private void writeArr(MarshalByteBuffer marshalbytebuffer, Object obj, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        if(!$assertionsDisabled && !obj.getClass().isArray())
            throw new AssertionError();
        byte byte0 = getType(obj.getClass().getComponentType());
label0:
        switch(byte0)
        {
        case 120: // 'x'
            byte abyte0[] = (byte[])obj;
            encodeInt32(marshalbytebuffer, abyte0.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int i = 0; i < abyte0.length; i++)
                encodeInt8(marshalbytebuffer, abyte0[i]);

            break;

        case 121: // 'y'
            short aword0[] = (short[])obj;
            encodeInt32(marshalbytebuffer, aword0.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int j = 0; j < aword0.length; j++)
                encodeInt16(marshalbytebuffer, aword0[j]);

            break;

        case 122: // 'z'
            int ai[] = (int[])obj;
            encodeInt32(marshalbytebuffer, ai.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int k = 0; k < ai.length; k++)
                encodeInt32(marshalbytebuffer, ai[k]);

            break;

        case 123: // '{'
            long al[] = (long[])obj;
            encodeInt32(marshalbytebuffer, al.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int l = 0; l < al.length; l++)
                encodeInt64(marshalbytebuffer, al[l]);

            break;

        case 124: // '|'
            float af[] = (float[])obj;
            encodeInt32(marshalbytebuffer, af.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int i1 = 0; i1 < af.length; i1++)
                encodeFloat32(marshalbytebuffer, af[i1]);

            break;

        case 125: // '}'
            double ad[] = (double[])obj;
            encodeInt32(marshalbytebuffer, ad.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int j1 = 0; j1 < ad.length; j1++)
                encodeFloat64(marshalbytebuffer, ad[j1]);

            break;

        case 127: // '\177'
            char ac[] = (char[])obj;
            encodeInt32(marshalbytebuffer, ac.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int k1 = 0; k1 < ac.length; k1++)
                encodeChar16(marshalbytebuffer, ac[k1]);

            break;

        case 126: // '~'
            boolean aflag[] = (boolean[])obj;
            encodeInt32(marshalbytebuffer, aflag.length);
            encodeInt8(marshalbytebuffer, byte0);
            for(int l1 = 0; l1 < aflag.length; l1++)
                encodeBool(marshalbytebuffer, aflag[l1]);

            break;

        case 100: // 'd'
            Byte abyte[] = (Byte[])obj;
            encodeInt32(marshalbytebuffer, abyte.length);
            encodeInt8(marshalbytebuffer, byte0);
            int i2 = 0;
            do
            {
                if(i2 >= abyte.length)
                    break label0;
                Byte byte2 = abyte[i2];
                if(byte2 == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeInt8(marshalbytebuffer, byte2.byteValue());
                }
                i2++;
            } while(true);

        case 107: // 'k'
            Character acharacter[] = (Character[])obj;
            encodeInt32(marshalbytebuffer, acharacter.length);
            encodeInt8(marshalbytebuffer, byte0);
            int j2 = 0;
            do
            {
                if(j2 >= acharacter.length)
                    break label0;
                Character character = acharacter[j2];
                if(character == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeChar16(marshalbytebuffer, character.charValue());
                }
                j2++;
            } while(true);

        case 106: // 'j'
            Boolean aboolean[] = (Boolean[])obj;
            encodeInt32(marshalbytebuffer, aboolean.length);
            encodeInt8(marshalbytebuffer, byte0);
            int k2 = 0;
            do
            {
                if(k2 >= aboolean.length)
                    break label0;
                Boolean boolean1 = aboolean[k2];
                if(boolean1 == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeBool(marshalbytebuffer, boolean1.booleanValue());
                }
                k2++;
            } while(true);

        case 101: // 'e'
            Short ashort[] = (Short[])obj;
            encodeInt32(marshalbytebuffer, ashort.length);
            encodeInt8(marshalbytebuffer, byte0);
            int l2 = 0;
            do
            {
                if(l2 >= ashort.length)
                    break label0;
                Short short1 = ashort[l2];
                if(short1 == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeInt16(marshalbytebuffer, short1.shortValue());
                }
                l2++;
            } while(true);

        case 102: // 'f'
            Integer ainteger[] = (Integer[])obj;
            encodeInt32(marshalbytebuffer, ainteger.length);
            encodeInt8(marshalbytebuffer, byte0);
            int i3 = 0;
            do
            {
                if(i3 >= ainteger.length)
                    break label0;
                Integer integer = ainteger[i3];
                if(integer == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeInt32(marshalbytebuffer, integer.intValue());
                }
                i3++;
            } while(true);

        case 103: // 'g'
            Long along[] = (Long[])obj;
            encodeInt32(marshalbytebuffer, along.length);
            encodeInt8(marshalbytebuffer, byte0);
            int j3 = 0;
            do
            {
                if(j3 >= along.length)
                    break label0;
                Long long1 = along[j3];
                if(long1 == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeInt64(marshalbytebuffer, long1.longValue());
                }
                j3++;
            } while(true);

        case 104: // 'h'
            Float afloat[] = (Float[])obj;
            encodeInt32(marshalbytebuffer, afloat.length);
            encodeInt8(marshalbytebuffer, byte0);
            int k3 = 0;
            do
            {
                if(k3 >= afloat.length)
                    break label0;
                Float float1 = afloat[k3];
                if(float1 == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeFloat32(marshalbytebuffer, float1.floatValue());
                }
                k3++;
            } while(true);

        case 105: // 'i'
            Double adouble[] = (Double[])obj;
            encodeInt32(marshalbytebuffer, adouble.length);
            encodeInt8(marshalbytebuffer, byte0);
            int l3 = 0;
            do
            {
                if(l3 >= adouble.length)
                    break label0;
                Double double1 = adouble[l3];
                if(double1 == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeFloat64(marshalbytebuffer, double1.doubleValue());
                }
                l3++;
            } while(true);

        case 108: // 'l'
            String as[] = (String[])obj;
            encodeInt32(marshalbytebuffer, as.length);
            encodeInt8(marshalbytebuffer, byte0);
            int i4 = 0;
            do
            {
                if(i4 >= as.length)
                    break label0;
                String s = as[i4];
                if(s == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeUtf8(marshalbytebuffer, s);
                }
                i4++;
            } while(true);

        case 109: // 'm'
            Date adate[] = (Date[])obj;
            encodeInt32(marshalbytebuffer, adate.length);
            encodeInt8(marshalbytebuffer, byte0);
            int j4 = 0;
            do
            {
                if(j4 >= adate.length)
                    break label0;
                Date date = adate[j4];
                if(date == null)
                {
                    encodeInt8(marshalbytebuffer, (byte)52);
                } else
                {
                    encodeInt8(marshalbytebuffer, (byte)53);
                    encodeDate(marshalbytebuffer, date);
                }
                j4++;
            } while(true);

        case 110: // 'n'
        case 111: // 'o'
        case 112: // 'p'
        case 113: // 'q'
        case 119: // 'w'
            Object aobj[] = (Object[])obj;
            encodeInt32(marshalbytebuffer, aobj.length);
            encodeInt8(marshalbytebuffer, byte0);
            byte byte1 = byte0 != 119 ? byte0 : -1;
            for(int k4 = 0; k4 < aobj.length; k4++)
                marshalElem(marshalbytebuffer, aobj[k4], byte1, marshalcircrefsbuf);

            break;

        case 114: // 'r'
        case 115: // 's'
        case 116: // 't'
        case 117: // 'u'
        case 118: // 'v'
        default:
            if(!$assertionsDisabled)
                throw new AssertionError("Invalid object type: " + byte0);
            break;
        }
    }

    private void marshalElem(MarshalByteBuffer marshalbytebuffer, Object obj, byte byte0, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        if(obj == null)
        {
            encodeInt8(marshalbytebuffer, (byte)52);
        } else
        {
            byte byte1 = byte0 != -1 ? byte0 : getType(obj.getClass());
            switch(byte1)
            {
            case 106: // 'j'
                encodeInt8(marshalbytebuffer, byte1);
                encodeBool(marshalbytebuffer, ((Boolean)obj).booleanValue());
                break;

            case 107: // 'k'
                encodeInt8(marshalbytebuffer, byte1);
                encodeChar16(marshalbytebuffer, ((Character)obj).charValue());
                break;

            case 109: // 'm'
                encodeInt8(marshalbytebuffer, byte1);
                encodeDate(marshalbytebuffer, (Date)obj);
                break;

            case 104: // 'h'
                encodeInt8(marshalbytebuffer, byte1);
                encodeFloat32(marshalbytebuffer, ((Float)obj).floatValue());
                break;

            case 105: // 'i'
                encodeInt8(marshalbytebuffer, byte1);
                encodeFloat64(marshalbytebuffer, ((Double)obj).doubleValue());
                break;

            case 100: // 'd'
                encodeInt8(marshalbytebuffer, byte1);
                encodeInt8(marshalbytebuffer, ((Byte)obj).byteValue());
                break;

            case 101: // 'e'
                encodeInt8(marshalbytebuffer, byte1);
                encodeInt16(marshalbytebuffer, ((Short)obj).shortValue());
                break;

            case 102: // 'f'
                encodeInt8(marshalbytebuffer, byte1);
                encodeInt32(marshalbytebuffer, ((Integer)obj).intValue());
                break;

            case 103: // 'g'
                encodeInt8(marshalbytebuffer, byte1);
                encodeInt64(marshalbytebuffer, ((Long)obj).longValue());
                break;

            case 108: // 'l'
                encodeInt8(marshalbytebuffer, byte1);
                encodeUtf8(marshalbytebuffer, (String)obj);
                break;

            case 110: // 'n'
                int i = marshalcircrefsbuf.lookupObj(obj);
                if(i != -1)
                {
                    encodeInt8(marshalbytebuffer, (byte)51);
                    encodeInt32(marshalbytebuffer, i);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    encodeInt8(marshalbytebuffer, byte1);
                    writeArr(marshalbytebuffer, obj, marshalcircrefsbuf);
                }
                break;

            case 113: // 'q'
                int j = marshalcircrefsbuf.lookupObj(obj);
                if(j != -1)
                {
                    encodeInt8(marshalbytebuffer, (byte)51);
                    encodeInt32(marshalbytebuffer, j);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    encodeInt8(marshalbytebuffer, byte1);
                    writeMrshl(marshalbytebuffer, (Marshallable)obj, marshalcircrefsbuf);
                }
                break;

            case 111: // 'o'
                int k = marshalcircrefsbuf.lookupObj(obj);
                if(k != -1)
                {
                    encodeInt8(marshalbytebuffer, (byte)51);
                    encodeInt32(marshalbytebuffer, k);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                encodeInt8(marshalbytebuffer, byte1);
                List list = (List)obj;
                int i1 = list.size();
                encodeInt32(marshalbytebuffer, i1);
                for(int k1 = 0; k1 < i1; k1++)
                    marshalElem(marshalbytebuffer, list.get(k1), (byte)-1, marshalcircrefsbuf);

                break;

            case 112: // 'p'
                int l = marshalcircrefsbuf.lookupObj(obj);
                if(l != -1)
                {
                    encodeInt8(marshalbytebuffer, (byte)51);
                    encodeInt32(marshalbytebuffer, l);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                encodeInt8(marshalbytebuffer, byte1);
                Map map = (Map)obj;
                int j1 = map.size();
                encodeInt32(marshalbytebuffer, j1);
                Map.Entry entry;
                for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); marshalElem(marshalbytebuffer, entry.getValue(), (byte)-1, marshalcircrefsbuf))
                {
                    entry = (Map.Entry)iterator.next();
                    marshalElem(marshalbytebuffer, entry.getKey(), (byte)-1, marshalcircrefsbuf);
                }

                break;

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid object type: " + byte1);
                break;
            }
        }
    }

    private int marshalElem(byte abyte0[], int i, Object obj, byte byte0, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        if(obj == null)
        {
            i = encodeInt8((byte)52, abyte0, i);
        } else
        {
            byte byte1 = byte0 != -1 ? byte0 : getType(obj.getClass());
            switch(byte1)
            {
            case 106: // 'j'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeBool(((Boolean)obj).booleanValue(), abyte0, i);
                break;

            case 107: // 'k'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeChar16(((Character)obj).charValue(), abyte0, i);
                break;

            case 109: // 'm'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeDate((Date)obj, abyte0, i);
                break;

            case 104: // 'h'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeFloat32(((Float)obj).floatValue(), abyte0, i);
                break;

            case 105: // 'i'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeFloat64(((Double)obj).doubleValue(), abyte0, i);
                break;

            case 100: // 'd'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeInt8(((Byte)obj).byteValue(), abyte0, i);
                break;

            case 101: // 'e'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeInt16(((Short)obj).shortValue(), abyte0, i);
                break;

            case 102: // 'f'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeInt32(((Integer)obj).intValue(), abyte0, i);
                break;

            case 103: // 'g'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeInt64(((Long)obj).longValue(), abyte0, i);
                break;

            case 108: // 'l'
                i = encodeInt8(byte1, abyte0, i);
                i = encodeUtf8Str((String)obj, abyte0, i);
                break;

            case 110: // 'n'
                int j = marshalcircrefsbuf.lookupObj(obj);
                if(j != -1)
                {
                    i = encodeInt8((byte)51, abyte0, i);
                    i = encodeInt32(j, abyte0, i);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    i = encodeInt8(byte1, abyte0, i);
                    i = writeArr(abyte0, i, obj, marshalcircrefsbuf);
                }
                break;

            case 113: // 'q'
                int k = marshalcircrefsbuf.lookupObj(obj);
                if(k != -1)
                {
                    i = encodeInt8((byte)51, abyte0, i);
                    i = encodeInt32(k, abyte0, i);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    i = encodeInt8(byte1, abyte0, i);
                    i = writeMrshl(abyte0, i, (Marshallable)obj, marshalcircrefsbuf);
                }
                break;

            case 111: // 'o'
                int l = marshalcircrefsbuf.lookupObj(obj);
                if(l != -1)
                {
                    i = encodeInt8((byte)51, abyte0, i);
                    i = encodeInt32(l, abyte0, i);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                i = encodeInt8(byte1, abyte0, i);
                List list = (List)obj;
                int j1 = list.size();
                i = encodeInt32(j1, abyte0, i);
                for(int l1 = 0; l1 < j1; l1++)
                    i = marshalElem(abyte0, i, list.get(l1), (byte)-1, marshalcircrefsbuf);

                break;

            case 112: // 'p'
                int i1 = marshalcircrefsbuf.lookupObj(obj);
                if(i1 != -1)
                {
                    i = encodeInt8((byte)51, abyte0, i);
                    i = encodeInt32(i1, abyte0, i);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                i = encodeInt8(byte1, abyte0, i);
                Map map = (Map)obj;
                int k1 = map.size();
                i = encodeInt32(k1, abyte0, i);
                for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
                {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    i = marshalElem(abyte0, i, entry.getKey(), (byte)-1, marshalcircrefsbuf);
                    i = marshalElem(abyte0, i, entry.getValue(), (byte)-1, marshalcircrefsbuf);
                }

                break;

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid object type: " + byte1);
                break;
            }
        }
        return i;
    }

    private ByteDemarshalResult readMrshl(byte abyte0[], int i, Marshallable marshallable, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        short word0 = decodeInt16(abyte0, i);
        i += 2;
        int j = decodeInt32(abyte0, i);
        i += 4;
        HashMap hashmap = new HashMap(Utils.getNonRehashCapacity(j));
        if(marshallable == null)
            marshallable = getMarshObj(list, word0);
        else
        if(marshallable.typeGuid() != word0)
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR14", new Short(word0)));
        marshalcircrefsbuf.add(marshallable);
        for(int k = 0; k < j; k++)
        {
            ByteArrResult bytearrresult = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
            i = bytearrresult.getOffset();
            ByteArrResult bytearrresult1 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
            i = bytearrresult1.getOffset();
            hashmap.put(bytearrresult.getObject(), bytearrresult1.getObject());
        }

        marshallable.setObjs(hashmap);
        marshallable.onDemarshal();
        return new ByteDemarshalResultImpl(i, marshallable);
    }

    private ByteArrResult readArr(byte abyte0[], int i, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        int j = decodeInt32(abyte0, i);
        i += 4;
        byte byte0 = decodeInt8(abyte0, i);
        i++;
        switch(byte0)
        {
        case 120: // 'x'
            byte abyte1[] = new byte[j];
            marshalcircrefsbuf.add(abyte1);
            for(int k = 0; k < j; k++)
            {
                abyte1[k] = decodeInt8(abyte0, i);
                i++;
            }

            return new ByteArrResult(i, abyte1);

        case 121: // 'y'
            short aword0[] = new short[j];
            marshalcircrefsbuf.add(aword0);
            for(int l = 0; l < j; l++)
            {
                aword0[l] = decodeInt16(abyte0, i);
                i += 2;
            }

            return new ByteArrResult(i, aword0);

        case 122: // 'z'
            int ai[] = new int[j];
            marshalcircrefsbuf.add(ai);
            for(int i1 = 0; i1 < j; i1++)
            {
                ai[i1] = decodeInt32(abyte0, i);
                i += 4;
            }

            return new ByteArrResult(i, ai);

        case 123: // '{'
            long al[] = new long[j];
            marshalcircrefsbuf.add(al);
            for(int j1 = 0; j1 < j; j1++)
            {
                al[j1] = decodeInt64(abyte0, i);
                i += 8;
            }

            return new ByteArrResult(i, al);

        case 126: // '~'
            boolean aflag[] = new boolean[j];
            marshalcircrefsbuf.add(aflag);
            for(int k1 = 0; k1 < j; k1++)
            {
                aflag[k1] = decodeBool(abyte0, i);
                i++;
            }

            return new ByteArrResult(i, aflag);

        case 127: // '\177'
            char ac[] = new char[j];
            marshalcircrefsbuf.add(ac);
            for(int l1 = 0; l1 < j; l1++)
            {
                ac[l1] = decodeChar16(abyte0, i);
                i += 2;
            }

            return new ByteArrResult(i, ac);

        case 124: // '|'
            float af[] = new float[j];
            marshalcircrefsbuf.add(af);
            for(int i2 = 0; i2 < j; i2++)
            {
                af[i2] = decodeFloat32(abyte0, i);
                i += 4;
            }

            return new ByteArrResult(i, af);

        case 125: // '}'
            double ad[] = new double[j];
            marshalcircrefsbuf.add(ad);
            for(int j2 = 0; j2 < j; j2++)
            {
                ad[j2] = decodeFloat64(abyte0, i);
                i += 8;
            }

            return new ByteArrResult(i, ad);

        case 108: // 'l'
            String as[] = new String[j];
            marshalcircrefsbuf.add(as);
            for(int k2 = 0; k2 < j; k2++)
            {
                byte byte1 = decodeInt8(abyte0, i);
                i++;
                if(byte1 == 52)
                {
                    as[k2] = null;
                } else
                {
                    ByteUtf8DecodeResult byteutf8decoderesult = decodeUtf8Str(abyte0, i);
                    i = byteutf8decoderesult.getOffset();
                    as[k2] = byteutf8decoderesult.getObject();
                }
            }

            return new ByteArrResult(i, as);

        case 109: // 'm'
            Date adate[] = new Date[j];
            marshalcircrefsbuf.add(adate);
            for(int l2 = 0; l2 < j; l2++)
            {
                byte byte2 = decodeInt8(abyte0, i);
                i++;
                if(byte2 == 52)
                {
                    adate[l2] = null;
                } else
                {
                    adate[l2] = decodeDate(abyte0, i);
                    i += 9;
                }
            }

            return new ByteArrResult(i, adate);

        case 107: // 'k'
            Character acharacter[] = new Character[j];
            marshalcircrefsbuf.add(acharacter);
            for(int i3 = 0; i3 < j; i3++)
            {
                byte byte3 = decodeInt8(abyte0, i);
                i++;
                if(byte3 == 52)
                {
                    acharacter[i3] = null;
                } else
                {
                    acharacter[i3] = new Character(decodeChar16(abyte0, i));
                    i += 2;
                }
            }

            return new ByteArrResult(i, acharacter);

        case 105: // 'i'
            Double adouble[] = new Double[j];
            marshalcircrefsbuf.add(adouble);
            for(int j3 = 0; j3 < j; j3++)
            {
                byte byte4 = decodeInt8(abyte0, i);
                i++;
                if(byte4 == 52)
                {
                    adouble[j3] = null;
                } else
                {
                    adouble[j3] = new Double(decodeFloat64(abyte0, i));
                    i += 8;
                }
            }

            return new ByteArrResult(i, adouble);

        case 104: // 'h'
            Float afloat[] = new Float[j];
            marshalcircrefsbuf.add(afloat);
            for(int k3 = 0; k3 < j; k3++)
            {
                byte byte5 = decodeInt8(abyte0, i);
                i++;
                if(byte5 == 52)
                {
                    afloat[k3] = null;
                } else
                {
                    afloat[k3] = new Float(decodeFloat32(abyte0, i));
                    i += 4;
                }
            }

            return new ByteArrResult(i, afloat);

        case 103: // 'g'
            Long along[] = new Long[j];
            marshalcircrefsbuf.add(along);
            for(int l3 = 0; l3 < j; l3++)
            {
                byte byte6 = decodeInt8(abyte0, i);
                i++;
                if(byte6 == 52)
                {
                    along[l3] = null;
                } else
                {
                    along[l3] = new Long(decodeInt64(abyte0, i));
                    i += 8;
                }
            }

            return new ByteArrResult(i, along);

        case 102: // 'f'
            Integer ainteger[] = new Integer[j];
            marshalcircrefsbuf.add(ainteger);
            for(int i4 = 0; i4 < j; i4++)
            {
                byte byte7 = decodeInt8(abyte0, i);
                i++;
                if(byte7 == 52)
                {
                    ainteger[i4] = null;
                } else
                {
                    ainteger[i4] = new Integer(decodeInt32(abyte0, i));
                    i += 4;
                }
            }

            return new ByteArrResult(i, ainteger);

        case 101: // 'e'
            Short ashort[] = new Short[j];
            marshalcircrefsbuf.add(ashort);
            for(int j4 = 0; j4 < j; j4++)
            {
                byte byte8 = decodeInt8(abyte0, i);
                i++;
                if(byte8 == 52)
                {
                    ashort[j4] = null;
                } else
                {
                    ashort[j4] = new Short(decodeInt16(abyte0, i));
                    i += 2;
                }
            }

            return new ByteArrResult(i, ashort);

        case 100: // 'd'
            Byte abyte[] = new Byte[j];
            marshalcircrefsbuf.add(abyte);
            for(int k4 = 0; k4 < j; k4++)
            {
                byte byte9 = decodeInt8(abyte0, i);
                i++;
                if(byte9 == 52)
                {
                    abyte[k4] = null;
                } else
                {
                    abyte[k4] = new Byte(decodeInt8(abyte0, i));
                    i++;
                }
            }

            return new ByteArrResult(i, abyte);

        case 106: // 'j'
            Boolean aboolean[] = new Boolean[j];
            marshalcircrefsbuf.add(aboolean);
            for(int l4 = 0; l4 < j; l4++)
            {
                byte byte10 = decodeInt8(abyte0, i);
                i++;
                if(byte10 == 52)
                {
                    aboolean[l4] = null;
                } else
                {
                    aboolean[l4] = new Boolean(decodeBool(abyte0, i));
                    i++;
                }
            }

            return new ByteArrResult(i, aboolean);

        case 110: // 'n'
            Object aobj[] = new Object[j];
            int i5 = marshalcircrefsbuf.add(((Object) (aobj)));
            ArrayInfo arrayinfo = null;
            for(int j6 = 0; j6 < j; j6++)
            {
                ByteArrResult bytearrresult4 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                aobj[j6] = bytearrresult4.getObject();
                if(arrayinfo == null && aobj[j6] != null)
                    arrayinfo = ArrayInfo.getInstance(aobj[j6]);
                i = bytearrresult4.getOffset();
            }

            if(arrayinfo != null)
            {
                int ai1[] = new int[arrayinfo.getLevel()];
                ai1[0] = j;
                Object obj = Array.newInstance(arrayinfo.getComponent(), ai1);
                for(int k6 = 0; k6 < j; k6++)
                    Array.set(obj, k6, aobj[k6]);

                marshalcircrefsbuf.replace(i5, obj);
                return new ByteArrResult(i, obj);
            } else
            {
                return new ByteArrResult(i, ((Object) (aobj)));
            }

        case 113: // 'q'
            Marshallable amarshallable[] = new Marshallable[j];
            marshalcircrefsbuf.add(amarshallable);
            for(int j5 = 0; j5 < j; j5++)
            {
                ByteArrResult bytearrresult = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                amarshallable[j5] = (Marshallable)bytearrresult.getObject();
                i = bytearrresult.getOffset();
            }

            return new ByteArrResult(i, amarshallable);

        case 111: // 'o'
            List alist[] = new List[j];
            marshalcircrefsbuf.add(alist);
            for(int k5 = 0; k5 < j; k5++)
            {
                ByteArrResult bytearrresult1 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                alist[k5] = (List)bytearrresult1.getObject();
                i = bytearrresult1.getOffset();
            }

            return new ByteArrResult(i, alist);

        case 112: // 'p'
            Map amap[] = new Map[j];
            marshalcircrefsbuf.add(amap);
            for(int l5 = 0; l5 < j; l5++)
            {
                ByteArrResult bytearrresult2 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                amap[l5] = (Map)bytearrresult2.getObject();
                i = bytearrresult2.getOffset();
            }

            return new ByteArrResult(i, amap);

        case 119: // 'w'
            Object aobj1[] = new Object[j];
            marshalcircrefsbuf.add(((Object) (aobj1)));
            for(int i6 = 0; i6 < j; i6++)
            {
                ByteArrResult bytearrresult3 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                aobj1[i6] = bytearrresult3.getObject();
                i = bytearrresult3.getOffset();
            }

            return new ByteArrResult(i, ((Object) (aobj1)));
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid object type: " + byte0);
        else
            return null;
    }

    private ByteArrResult demarshalElem(byte abyte0[], int i, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        byte byte0 = decodeInt8(abyte0, i);
        i++;
        switch(byte0)
        {
        case 52: // '4'
            return new ByteArrResult(i, null);

        case 51: // '3'
            int j = decodeInt32(abyte0, i);
            i += 4;
            return new ByteArrResult(i, marshalcircrefsbuf.lookupRef(j));

        case 100: // 'd'
            return new ByteArrResult(i + 1, new Byte(decodeInt8(abyte0, i)));

        case 101: // 'e'
            return new ByteArrResult(i + 2, new Short(decodeInt16(abyte0, i)));

        case 102: // 'f'
            return new ByteArrResult(i + 4, new Integer(decodeInt32(abyte0, i)));

        case 103: // 'g'
            return new ByteArrResult(i + 8, new Long(decodeInt64(abyte0, i)));

        case 104: // 'h'
            return new ByteArrResult(i + 4, new Float(decodeFloat32(abyte0, i)));

        case 105: // 'i'
            return new ByteArrResult(i + 8, new Double(decodeFloat64(abyte0, i)));

        case 107: // 'k'
            return new ByteArrResult(i + 2, new Character(decodeChar16(abyte0, i)));

        case 106: // 'j'
            return new ByteArrResult(i + 1, new Boolean(decodeBool(abyte0, i)));

        case 109: // 'm'
            return new ByteArrResult(i + 9, decodeDate(abyte0, i));

        case 110: // 'n'
            return readArr(abyte0, i, list, marshalcircrefsbuf);

        case 108: // 'l'
            ByteUtf8DecodeResult byteutf8decoderesult = decodeUtf8Str(abyte0, i);
            return new ByteArrResult(byteutf8decoderesult.getOffset(), byteutf8decoderesult.getObject());

        case 113: // 'q'
            ByteDemarshalResult bytedemarshalresult = readMrshl(abyte0, i, null, list, marshalcircrefsbuf);
            return new ByteArrResult(bytedemarshalresult.getOffset(), bytedemarshalresult.getObject());

        case 111: // 'o'
            int k = decodeInt32(abyte0, i);
            i += 4;
            ArrayList arraylist = new ArrayList(k);
            marshalcircrefsbuf.add(arraylist);
            for(int i1 = 0; i1 < k; i1++)
            {
                ByteArrResult bytearrresult = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                i = bytearrresult.getOffset();
                arraylist.add(bytearrresult.getObject());
            }

            return new ByteArrResult(i, arraylist);

        case 112: // 'p'
            int l = decodeInt32(abyte0, i);
            i += 4;
            HashMap hashmap = new HashMap(Utils.getNonRehashCapacity(l));
            marshalcircrefsbuf.add(hashmap);
            for(int j1 = 0; j1 < l; j1++)
            {
                ByteArrResult bytearrresult1 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                i = bytearrresult1.getOffset();
                ByteArrResult bytearrresult2 = demarshalElem(abyte0, i, list, marshalcircrefsbuf);
                i = bytearrresult2.getOffset();
                hashmap.put(bytearrresult1.getObject(), bytearrresult2.getObject());
            }

            return new ByteArrResult(i, hashmap);
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid object type: " + byte0);
        else
            return null;
    }

    public int encodeInt8(byte byte0, byte abyte0[], int i)
    {
        abyte0[i++] = byte0;
        return i;
    }

    public byte decodeInt8(byte abyte0[], int i)
    {
        return abyte0[i];
    }

    public int encodeInt16(short word0, byte abyte0[], int i)
    {
        abyte0[i++] = (byte)(word0 >>> 8);
        abyte0[i++] = (byte)word0;
        return i;
    }

    public short decodeInt16(byte abyte0[], int i)
    {
        return (short)(((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff));
    }

    public int encodeChar16(char c, byte abyte0[], int i)
    {
        abyte0[i++] = (byte)(c >>> 8);
        abyte0[i++] = (byte)c;
        return i;
    }

    public char decodeChar16(byte abyte0[], int i)
    {
        return (char)(((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff));
    }

    public int encodeInt32(int i, byte abyte0[], int j)
    {
        abyte0[j++] = (byte)(i >>> 24);
        abyte0[j++] = (byte)(i >>> 16);
        abyte0[j++] = (byte)(i >>> 8);
        abyte0[j++] = (byte)i;
        return j;
    }

    public int decodeInt32(byte abyte0[], int i)
    {
        return ((abyte0[i] & 0xff) << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8) + (abyte0[i + 3] & 0xff);
    }

    public int encodeInt64(long l, byte abyte0[], int i)
    {
        abyte0[i++] = (byte)(int)(l >>> 56);
        abyte0[i++] = (byte)(int)(l >>> 48);
        abyte0[i++] = (byte)(int)(l >>> 40);
        abyte0[i++] = (byte)(int)(l >>> 32);
        abyte0[i++] = (byte)(int)(l >>> 24);
        abyte0[i++] = (byte)(int)(l >>> 16);
        abyte0[i++] = (byte)(int)(l >>> 8);
        abyte0[i++] = (byte)(int)l;
        return i;
    }

    public long decodeInt64(byte abyte0[], int i)
    {
        return (((long)abyte0[i] & 255L) << 56) + (((long)abyte0[i + 1] & 255L) << 48) + (((long)abyte0[i + 2] & 255L) << 40) + (((long)abyte0[i + 3] & 255L) << 32) + (((long)abyte0[i + 4] & 255L) << 24) + (((long)abyte0[i + 5] & 255L) << 16) + (((long)abyte0[i + 6] & 255L) << 8) + ((long)abyte0[i + 7] & 255L);
    }

    public int encodeFloat32(float f, byte abyte0[], int i)
    {
        int j = Float.floatToIntBits(f);
        abyte0[i++] = (byte)(j >>> 24);
        abyte0[i++] = (byte)(j >>> 16);
        abyte0[i++] = (byte)(j >>> 8);
        abyte0[i++] = (byte)j;
        return i;
    }

    public float decodeFloat32(byte abyte0[], int i)
    {
        return Float.intBitsToFloat(((abyte0[i] & 0xff) << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8) + (abyte0[i + 3] & 0xff));
    }

    public int encodeFloat64(double d, byte abyte0[], int i)
    {
        long l = Double.doubleToLongBits(d);
        abyte0[i++] = (byte)(int)(l >>> 56);
        abyte0[i++] = (byte)(int)(l >>> 48);
        abyte0[i++] = (byte)(int)(l >>> 40);
        abyte0[i++] = (byte)(int)(l >>> 32);
        abyte0[i++] = (byte)(int)(l >>> 24);
        abyte0[i++] = (byte)(int)(l >>> 16);
        abyte0[i++] = (byte)(int)(l >>> 8);
        abyte0[i++] = (byte)(int)l;
        return i;
    }

    public double decodeFloat64(byte abyte0[], int i)
    {
        return Double.longBitsToDouble((((long)abyte0[i] & 255L) << 56) + (((long)abyte0[i + 1] & 255L) << 48) + (((long)abyte0[i + 2] & 255L) << 40) + (((long)abyte0[i + 3] & 255L) << 32) + (((long)abyte0[i + 4] & 255L) << 24) + (((long)abyte0[i + 5] & 255L) << 16) + (((long)abyte0[i + 6] & 255L) << 8) + ((long)abyte0[i + 7] & 255L));
    }

    public int encodeBool(boolean flag, byte abyte0[], int i)
    {
        abyte0[i] = ((byte)(!flag ? 0 : 1));
        return i + 1;
    }

    public boolean decodeBool(byte abyte0[], int i)
    {
        return abyte0[i] != 0;
    }

    public int encodeUtf8Str(String s, byte abyte0[], int i)
    {
        int j = s.length();
        i = encodeInt32(j, abyte0, i);
        for(int k = 0; k < j; k++)
        {
            char c = s.charAt(k);
            if(c >= '\001' && c <= '\177')
            {
                abyte0[i++] = (byte)c;
                continue;
            }
            if(c == 0 || '\200' <= c && c <= '\u07FF')
            {
                abyte0[i++] = (byte)(c >> 6 | 0xc0);
                abyte0[i++] = (byte)(c & 0x3f | 0x80);
            } else
            {
                abyte0[i++] = (byte)(c >> 12 | 0xe0);
                abyte0[i++] = (byte)(c >> 6 | 0xc0);
                abyte0[i++] = (byte)(c & 0x3f | 0x80);
            }
        }

        return i;
    }

    public ByteUtf8DecodeResult decodeUtf8Str(byte abyte0[], int i)
    {
        int j = decodeInt32(abyte0, i);
        i += 4;
        char ac[] = new char[j];
        int k = 0;
        for(int l = 0; k < j; l++)
        {
            byte byte0 = abyte0[i++];
            if(byte0 >= 1 && byte0 <= 127)
                ac[l] = (char)byte0;
            else
            if((byte0 & 0xe0) == 192)
            {
                byte byte1 = abyte0[i++];
                ac[l] = (char)((byte0 & 0x1f) << 6 | byte1 & 0x3f);
            } else
            {
                byte byte2 = abyte0[i++];
                byte byte3 = abyte0[i++];
                ac[l] = (char)((byte0 & 0xf) << 12 | (byte2 & 0x3f) << 6 | byte3 & 0x3f);
            }
            k++;
        }

        return new ByteUtf8DecodeResultImpl(i, new String(ac));
    }

    public int encodeDate(Date date, byte abyte0[], int i)
    {
        ArgAssert.illegalRange(date.getTime() >= 0L, "value", "value.getTime() >= 0");
        i = encodeInt8((byte)113, abyte0, i);
        return encodeInt64(date.getTime(), abyte0, i);
    }

    public Date decodeDate(byte abyte0[], int i)
    {
        byte byte0 = decodeInt8(abyte0, i);
        i++;
        if(byte0 == 113)
            return new Date(decodeInt64(abyte0, i));
        if(byte0 == 114)
            return dotNetDate2Java(decodeInt64(abyte0, i));
        else
            throw new IllegalStateException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
    }

    private void encodeInt8(MarshalByteBuffer marshalbytebuffer, byte byte0)
    {
        marshalbytebuffer.put(byte0);
    }

    private void encodeInt16(MarshalByteBuffer marshalbytebuffer, short word0)
    {
        marshalbytebuffer.put((byte)(word0 >>> 8));
        marshalbytebuffer.put((byte)word0);
    }

    private void encodeInt32(MarshalByteBuffer marshalbytebuffer, int i)
    {
        marshalbytebuffer.put((byte)(i >>> 24));
        marshalbytebuffer.put((byte)(i >>> 16));
        marshalbytebuffer.put((byte)(i >>> 8));
        marshalbytebuffer.put((byte)i);
    }

    private void encodeInt64(MarshalByteBuffer marshalbytebuffer, long l)
    {
        marshalbytebuffer.put((byte)(int)(l >>> 56));
        marshalbytebuffer.put((byte)(int)(l >>> 48));
        marshalbytebuffer.put((byte)(int)(l >>> 40));
        marshalbytebuffer.put((byte)(int)(l >>> 32));
        marshalbytebuffer.put((byte)(int)(l >>> 24));
        marshalbytebuffer.put((byte)(int)(l >>> 16));
        marshalbytebuffer.put((byte)(int)(l >>> 8));
        marshalbytebuffer.put((byte)(int)l);
    }

    private void encodeFloat32(MarshalByteBuffer marshalbytebuffer, float f)
    {
        int i = Float.floatToIntBits(f);
        marshalbytebuffer.put((byte)(i >>> 24));
        marshalbytebuffer.put((byte)(i >>> 16));
        marshalbytebuffer.put((byte)(i >>> 8));
        marshalbytebuffer.put((byte)i);
    }

    private void encodeFloat64(MarshalByteBuffer marshalbytebuffer, double d)
    {
        long l = Double.doubleToLongBits(d);
        marshalbytebuffer.put((byte)(int)(l >>> 56));
        marshalbytebuffer.put((byte)(int)(l >>> 48));
        marshalbytebuffer.put((byte)(int)(l >>> 40));
        marshalbytebuffer.put((byte)(int)(l >>> 32));
        marshalbytebuffer.put((byte)(int)(l >>> 24));
        marshalbytebuffer.put((byte)(int)(l >>> 16));
        marshalbytebuffer.put((byte)(int)(l >>> 8));
        marshalbytebuffer.put((byte)(int)l);
    }

    private void encodeChar16(MarshalByteBuffer marshalbytebuffer, char c)
    {
        marshalbytebuffer.put((byte)(c >>> 8));
        marshalbytebuffer.put((byte)c);
    }

    private void encodeBool(MarshalByteBuffer marshalbytebuffer, boolean flag)
    {
        marshalbytebuffer.put(((byte)(!flag ? 0 : 1)));
    }

    private void encodeDate(MarshalByteBuffer marshalbytebuffer, Date date)
    {
        ArgAssert.illegalRange(date.getTime() >= 0L, "value", "value.getTime() >= 0");
        encodeInt8(marshalbytebuffer, (byte)113);
        encodeInt64(marshalbytebuffer, date.getTime());
    }

    private void encodeUtf8(MarshalByteBuffer marshalbytebuffer, String s)
    {
        int i = s.length();
        encodeInt32(marshalbytebuffer, (short)i);
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            if(c >= '\001' && c <= '\177')
            {
                marshalbytebuffer.put((byte)c);
                continue;
            }
            if(c == 0 || '\200' <= c && c <= '\u07FF')
            {
                marshalbytebuffer.put((byte)(c >> 6 | 0xc0));
                marshalbytebuffer.put((byte)(c & 0x3f | 0x80));
            } else
            {
                marshalbytebuffer.put((byte)(c >> 12 | 0xe0));
                marshalbytebuffer.put((byte)(c >> 6 | 0xc0));
                marshalbytebuffer.put((byte)(c & 0x3f | 0x80));
            }
        }

    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ByteMarshallerImpl.class).desiredAssertionStatus();
    }
}
