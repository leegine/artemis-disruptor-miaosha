// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.marshal.impl:
//            BaseMarshaller, MarshalCircRefsBuf, ArrayInfo

public class NioMarshallerImpl extends BaseMarshaller
    implements NioMarshaller
{

    NioMarshallerImpl(List list)
    {
        super(list);
    }

    public void marshalObj(Marshallable marshallable, ByteBuffer bytebuffer)
        throws MarshalException
    {
        try
        {
            MarshalCircRefsBuf marshalcircrefsbuf = getCircRefsBuf(true);
            marshalcircrefsbuf.add(marshallable);
            encodeInt8((byte)113, bytebuffer);
            writeMrshl(bytebuffer, marshallable, marshalcircrefsbuf);
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public Marshallable demarshalObj(ByteBuffer bytebuffer, Marshallable marshallable)
        throws MarshalException
    {
        try
        {
            byte byte0 = decodeInt8(bytebuffer);
            if(byte0 != 113 && byte0 != 114)
                throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
            else
                return readMrshl(bytebuffer, marshallable, getFactories(), getCircRefsBuf(false));
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public Marshallable demarshalObj(ByteBuffer bytebuffer)
        throws MarshalException
    {
        try
        {
            byte byte0 = decodeInt8(bytebuffer);
            if(byte0 != 113 && byte0 != 114)
                throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
            else
                return readMrshl(bytebuffer, null, getFactories(), getCircRefsBuf(false));
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    private Marshallable readMrshl(ByteBuffer bytebuffer, Marshallable marshallable, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        short word0 = decodeInt16(bytebuffer);
        int i = decodeInt32(bytebuffer);
        HashMap hashmap = new HashMap(Utils.getNonRehashCapacity(i));
        if(marshallable == null)
            marshallable = getMarshObj(list, word0);
        else
        if(marshallable.typeGuid() != word0)
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR14", new Short(word0)));
        marshalcircrefsbuf.add(marshallable);
        for(int j = 0; j < i; j++)
        {
            Object obj = demarshalElem(bytebuffer, list, marshalcircrefsbuf);
            Object obj1 = demarshalElem(bytebuffer, list, marshalcircrefsbuf);
            hashmap.put(obj, obj1);
        }

        marshallable.setObjs(hashmap);
        marshallable.onDemarshal();
        return marshallable;
    }

    private Object demarshalElem(ByteBuffer bytebuffer, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        byte byte0 = decodeInt8(bytebuffer);
        switch(byte0)
        {
        case 52: // '4'
            return null;

        case 51: // '3'
            return marshalcircrefsbuf.lookupRef(decodeInt32(bytebuffer));

        case 100: // 'd'
            return new Byte(decodeInt8(bytebuffer));

        case 101: // 'e'
            return new Short(decodeInt16(bytebuffer));

        case 102: // 'f'
            return new Integer(decodeInt32(bytebuffer));

        case 103: // 'g'
            return new Long(decodeInt64(bytebuffer));

        case 104: // 'h'
            return new Float(decodeFloat32(bytebuffer));

        case 105: // 'i'
            return new Double(decodeFloat64(bytebuffer));

        case 107: // 'k'
            return new Character(decodeChar16(bytebuffer));

        case 106: // 'j'
            return new Boolean(decodeBool(bytebuffer));

        case 109: // 'm'
            return decodeDate(bytebuffer);

        case 110: // 'n'
            return readArr(bytebuffer, list, marshalcircrefsbuf);

        case 108: // 'l'
            return decodeUtf8Str(bytebuffer);

        case 113: // 'q'
            return readMrshl(bytebuffer, null, list, marshalcircrefsbuf);

        case 111: // 'o'
            int i = decodeInt32(bytebuffer);
            ArrayList arraylist = new ArrayList(i);
            marshalcircrefsbuf.add(arraylist);
            for(int k = 0; k < i; k++)
                arraylist.add(demarshalElem(bytebuffer, list, marshalcircrefsbuf));

            return arraylist;

        case 112: // 'p'
            int j = decodeInt32(bytebuffer);
            HashMap hashmap = new HashMap(Utils.getNonRehashCapacity(j));
            marshalcircrefsbuf.add(hashmap);
            for(int l = 0; l < j; l++)
            {
                Object obj = demarshalElem(bytebuffer, list, marshalcircrefsbuf);
                Object obj1 = demarshalElem(bytebuffer, list, marshalcircrefsbuf);
                hashmap.put(obj, obj1);
            }

            return hashmap;
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid object type: " + byte0);
        else
            return null;
    }

    private Object readArr(ByteBuffer bytebuffer, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        int i = decodeInt32(bytebuffer);
        byte byte0 = decodeInt8(bytebuffer);
        switch(byte0)
        {
        case 120: // 'x'
            byte abyte0[] = new byte[i];
            marshalcircrefsbuf.add(abyte0);
            for(int j = 0; j < i; j++)
                abyte0[j] = decodeInt8(bytebuffer);

            return abyte0;

        case 121: // 'y'
            short aword0[] = new short[i];
            marshalcircrefsbuf.add(aword0);
            for(int k = 0; k < i; k++)
                aword0[k] = decodeInt16(bytebuffer);

            return aword0;

        case 122: // 'z'
            int ai[] = new int[i];
            marshalcircrefsbuf.add(ai);
            for(int l = 0; l < i; l++)
                ai[l] = decodeInt32(bytebuffer);

            return ai;

        case 123: // '{'
            long al[] = new long[i];
            marshalcircrefsbuf.add(al);
            for(int i1 = 0; i1 < i; i1++)
                al[i1] = decodeInt64(bytebuffer);

            return al;

        case 126: // '~'
            boolean aflag[] = new boolean[i];
            marshalcircrefsbuf.add(aflag);
            for(int j1 = 0; j1 < i; j1++)
                aflag[j1] = decodeBool(bytebuffer);

            return aflag;

        case 127: // '\177'
            char ac[] = new char[i];
            marshalcircrefsbuf.add(ac);
            for(int k1 = 0; k1 < i; k1++)
                ac[k1] = decodeChar16(bytebuffer);

            return ac;

        case 124: // '|'
            float af[] = new float[i];
            marshalcircrefsbuf.add(af);
            for(int l1 = 0; l1 < i; l1++)
                af[l1] = decodeFloat32(bytebuffer);

            return af;

        case 125: // '}'
            double ad[] = new double[i];
            marshalcircrefsbuf.add(ad);
            for(int i2 = 0; i2 < i; i2++)
                ad[i2] = decodeFloat64(bytebuffer);

            return ad;

        case 108: // 'l'
            String as[] = new String[i];
            marshalcircrefsbuf.add(as);
            for(int j2 = 0; j2 < i; j2++)
                as[j2] = decodeInt8(bytebuffer) != 52 ? decodeUtf8Str(bytebuffer) : null;

            return as;

        case 109: // 'm'
            Date adate[] = new Date[i];
            marshalcircrefsbuf.add(adate);
            for(int k2 = 0; k2 < i; k2++)
                adate[k2] = decodeInt8(bytebuffer) != 52 ? decodeDate(bytebuffer) : null;

            return adate;

        case 107: // 'k'
            Character acharacter[] = new Character[i];
            marshalcircrefsbuf.add(acharacter);
            for(int l2 = 0; l2 < i; l2++)
                acharacter[l2] = decodeInt8(bytebuffer) != 52 ? new Character(decodeChar16(bytebuffer)) : null;

            return acharacter;

        case 105: // 'i'
            Double adouble[] = new Double[i];
            marshalcircrefsbuf.add(adouble);
            for(int i3 = 0; i3 < i; i3++)
                adouble[i3] = decodeInt8(bytebuffer) != 52 ? new Double(decodeFloat64(bytebuffer)) : null;

            return adouble;

        case 104: // 'h'
            Float afloat[] = new Float[i];
            marshalcircrefsbuf.add(afloat);
            for(int j3 = 0; j3 < i; j3++)
                afloat[j3] = decodeInt8(bytebuffer) != 52 ? new Float(decodeFloat32(bytebuffer)) : null;

            return afloat;

        case 103: // 'g'
            Long along[] = new Long[i];
            marshalcircrefsbuf.add(along);
            for(int k3 = 0; k3 < i; k3++)
                along[k3] = decodeInt8(bytebuffer) != 52 ? new Long(decodeInt64(bytebuffer)) : null;

            return along;

        case 102: // 'f'
            Integer ainteger[] = new Integer[i];
            marshalcircrefsbuf.add(ainteger);
            for(int l3 = 0; l3 < i; l3++)
                ainteger[l3] = decodeInt8(bytebuffer) != 52 ? new Integer(decodeInt32(bytebuffer)) : null;

            return ainteger;

        case 101: // 'e'
            Short ashort[] = new Short[i];
            marshalcircrefsbuf.add(ashort);
            for(int i4 = 0; i4 < i; i4++)
                ashort[i4] = decodeInt8(bytebuffer) != 52 ? new Short(decodeInt16(bytebuffer)) : null;

            return ashort;

        case 100: // 'd'
            Byte abyte[] = new Byte[i];
            marshalcircrefsbuf.add(abyte);
            for(int j4 = 0; j4 < i; j4++)
                abyte[j4] = decodeInt8(bytebuffer) != 52 ? new Byte(decodeInt8(bytebuffer)) : null;

            return abyte;

        case 106: // 'j'
            Boolean aboolean[] = new Boolean[i];
            marshalcircrefsbuf.add(aboolean);
            for(int k4 = 0; k4 < i; k4++)
                aboolean[k4] = decodeInt8(bytebuffer) != 52 ? new Boolean(decodeBool(bytebuffer)) : null;

            return aboolean;

        case 110: // 'n'
            Object aobj[] = new Object[i];
            int l4 = marshalcircrefsbuf.add(((Object) (aobj)));
            ArrayInfo arrayinfo = null;
            for(int i6 = 0; i6 < i; i6++)
            {
                aobj[i6] = demarshalElem(bytebuffer, list, marshalcircrefsbuf);
                if(arrayinfo == null && aobj[i6] != null)
                    arrayinfo = ArrayInfo.getInstance(aobj[i6]);
            }

            if(arrayinfo != null)
            {
                int ai1[] = new int[arrayinfo.getLevel()];
                ai1[0] = i;
                Object obj = Array.newInstance(arrayinfo.getComponent(), ai1);
                for(int j6 = 0; j6 < i; j6++)
                    Array.set(obj, j6, aobj[j6]);

                marshalcircrefsbuf.replace(l4, obj);
                return obj;
            } else
            {
                return ((Object) (aobj));
            }

        case 113: // 'q'
            Marshallable amarshallable[] = new Marshallable[i];
            marshalcircrefsbuf.add(amarshallable);
            for(int i5 = 0; i5 < i; i5++)
                amarshallable[i5] = (Marshallable)demarshalElem(bytebuffer, list, marshalcircrefsbuf);

            return amarshallable;

        case 111: // 'o'
            List alist[] = new List[i];
            marshalcircrefsbuf.add(alist);
            for(int j5 = 0; j5 < i; j5++)
                alist[j5] = (List)demarshalElem(bytebuffer, list, marshalcircrefsbuf);

            return alist;

        case 112: // 'p'
            Map amap[] = new Map[i];
            marshalcircrefsbuf.add(amap);
            for(int k5 = 0; k5 < i; k5++)
                amap[k5] = (Map)demarshalElem(bytebuffer, list, marshalcircrefsbuf);

            return amap;

        case 119: // 'w'
            Object aobj1[] = new Object[i];
            marshalcircrefsbuf.add(((Object) (aobj1)));
            for(int l5 = 0; l5 < i; l5++)
                aobj1[l5] = demarshalElem(bytebuffer, list, marshalcircrefsbuf);

            return ((Object) (aobj1));
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid object type: " + byte0);
        else
            return null;
    }

    private void writeMrshl(ByteBuffer bytebuffer, Marshallable marshallable, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        marshallable.onMarshal();
        Map map = marshallable.getObjs();
        encodeInt16(marshallable.typeGuid(), bytebuffer);
        encodeInt32(map.size(), bytebuffer);
        Map.Entry entry;
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); marshalElem(bytebuffer, entry.getValue(), (byte)-1, marshalcircrefsbuf))
        {
            entry = (Map.Entry)iterator.next();
            marshalElem(bytebuffer, entry.getKey(), (byte)-1, marshalcircrefsbuf);
        }

    }

    private void marshalElem(ByteBuffer bytebuffer, Object obj, byte byte0, MarshalCircRefsBuf marshalcircrefsbuf)
        throws MarshalException
    {
        if(obj == null)
        {
            encodeInt8((byte)52, bytebuffer);
        } else
        {
            byte byte1 = byte0 != -1 ? byte0 : getType(obj.getClass());
            switch(byte1)
            {
            case 106: // 'j'
                encodeInt8(byte1, bytebuffer);
                encodeBool(((Boolean)obj).booleanValue(), bytebuffer);
                break;

            case 107: // 'k'
                encodeInt8(byte1, bytebuffer);
                encodeChar16(((Character)obj).charValue(), bytebuffer);
                break;

            case 109: // 'm'
                encodeInt8(byte1, bytebuffer);
                encodeDate((Date)obj, bytebuffer);
                break;

            case 104: // 'h'
                encodeInt8(byte1, bytebuffer);
                encodeFloat32(((Float)obj).floatValue(), bytebuffer);
                break;

            case 105: // 'i'
                encodeInt8(byte1, bytebuffer);
                encodeFloat64(((Double)obj).doubleValue(), bytebuffer);
                break;

            case 100: // 'd'
                encodeInt8(byte1, bytebuffer);
                encodeInt8(((Byte)obj).byteValue(), bytebuffer);
                break;

            case 101: // 'e'
                encodeInt8(byte1, bytebuffer);
                encodeInt16(((Short)obj).shortValue(), bytebuffer);
                break;

            case 102: // 'f'
                encodeInt8(byte1, bytebuffer);
                encodeInt32(((Integer)obj).intValue(), bytebuffer);
                break;

            case 103: // 'g'
                encodeInt8(byte1, bytebuffer);
                encodeInt64(((Long)obj).longValue(), bytebuffer);
                break;

            case 108: // 'l'
                encodeInt8(byte1, bytebuffer);
                encodeUtf8Str((String)obj, bytebuffer);
                break;

            case 110: // 'n'
                int i = marshalcircrefsbuf.lookupObj(obj);
                if(i != -1)
                {
                    encodeInt8((byte)51, bytebuffer);
                    encodeInt32(i, bytebuffer);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    encodeInt8(byte1, bytebuffer);
                    writeArr(bytebuffer, obj, marshalcircrefsbuf);
                }
                break;

            case 113: // 'q'
                int j = marshalcircrefsbuf.lookupObj(obj);
                if(j != -1)
                {
                    encodeInt8((byte)51, bytebuffer);
                    encodeInt32(j, bytebuffer);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    encodeInt8(byte1, bytebuffer);
                    writeMrshl(bytebuffer, (Marshallable)obj, marshalcircrefsbuf);
                }
                break;

            case 111: // 'o'
                int k = marshalcircrefsbuf.lookupObj(obj);
                if(k != -1)
                {
                    encodeInt8((byte)51, bytebuffer);
                    encodeInt32(k, bytebuffer);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                encodeInt8(byte1, bytebuffer);
                List list = (List)obj;
                int i1 = list.size();
                encodeInt32(i1, bytebuffer);
                for(int k1 = 0; k1 < i1; k1++)
                    marshalElem(bytebuffer, list.get(k1), (byte)-1, marshalcircrefsbuf);

                break;

            case 112: // 'p'
                int l = marshalcircrefsbuf.lookupObj(obj);
                if(l != -1)
                {
                    encodeInt8((byte)51, bytebuffer);
                    encodeInt32(l, bytebuffer);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                encodeInt8(byte1, bytebuffer);
                Map map = (Map)obj;
                int j1 = map.size();
                encodeInt32(j1, bytebuffer);
                Map.Entry entry;
                for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); marshalElem(bytebuffer, entry.getValue(), (byte)-1, marshalcircrefsbuf))
                {
                    entry = (Map.Entry)iterator.next();
                    marshalElem(bytebuffer, entry.getKey(), (byte)-1, marshalcircrefsbuf);
                }

                break;

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid object type: " + byte1);
                break;
            }
        }
    }

    private void writeArr(ByteBuffer bytebuffer, Object obj, MarshalCircRefsBuf marshalcircrefsbuf)
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
            encodeInt32(abyte0.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int i = 0; i < abyte0.length; i++)
                encodeInt8(abyte0[i], bytebuffer);

            break;

        case 121: // 'y'
            short aword0[] = (short[])obj;
            encodeInt32(aword0.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int j = 0; j < aword0.length; j++)
                encodeInt16(aword0[j], bytebuffer);

            break;

        case 122: // 'z'
            int ai[] = (int[])obj;
            encodeInt32(ai.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int k = 0; k < ai.length; k++)
                encodeInt32(ai[k], bytebuffer);

            break;

        case 123: // '{'
            long al[] = (long[])obj;
            encodeInt32(al.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int l = 0; l < al.length; l++)
                encodeInt64(al[l], bytebuffer);

            break;

        case 124: // '|'
            float af[] = (float[])obj;
            encodeInt32(af.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int i1 = 0; i1 < af.length; i1++)
                encodeFloat32(af[i1], bytebuffer);

            break;

        case 125: // '}'
            double ad[] = (double[])obj;
            encodeInt32(ad.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int j1 = 0; j1 < ad.length; j1++)
                encodeFloat64(ad[j1], bytebuffer);

            break;

        case 127: // '\177'
            char ac[] = (char[])obj;
            encodeInt32(ac.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int k1 = 0; k1 < ac.length; k1++)
                encodeChar16(ac[k1], bytebuffer);

            break;

        case 126: // '~'
            boolean aflag[] = (boolean[])obj;
            encodeInt32(aflag.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            for(int l1 = 0; l1 < aflag.length; l1++)
                encodeBool(aflag[l1], bytebuffer);

            break;

        case 100: // 'd'
            Byte abyte[] = (Byte[])obj;
            encodeInt32(abyte.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int i2 = 0;
            do
            {
                if(i2 >= abyte.length)
                    break label0;
                Byte byte2 = abyte[i2];
                if(byte2 == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeInt8(byte2.byteValue(), bytebuffer);
                }
                i2++;
            } while(true);

        case 106: // 'j'
            Boolean aboolean[] = (Boolean[])obj;
            encodeInt32(aboolean.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int j2 = 0;
            do
            {
                if(j2 >= aboolean.length)
                    break label0;
                Boolean boolean1 = aboolean[j2];
                if(boolean1 == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeBool(boolean1.booleanValue(), bytebuffer);
                }
                j2++;
            } while(true);

        case 107: // 'k'
            Character acharacter[] = (Character[])obj;
            encodeInt32(acharacter.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int k2 = 0;
            do
            {
                if(k2 >= acharacter.length)
                    break label0;
                Character character = acharacter[k2];
                if(character == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeChar16(character.charValue(), bytebuffer);
                }
                k2++;
            } while(true);

        case 101: // 'e'
            Short ashort[] = (Short[])obj;
            encodeInt32(ashort.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int l2 = 0;
            do
            {
                if(l2 >= ashort.length)
                    break label0;
                Short short1 = ashort[l2];
                if(short1 == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeInt16(short1.shortValue(), bytebuffer);
                }
                l2++;
            } while(true);

        case 102: // 'f'
            Integer ainteger[] = (Integer[])obj;
            encodeInt32(ainteger.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int i3 = 0;
            do
            {
                if(i3 >= ainteger.length)
                    break label0;
                Integer integer = ainteger[i3];
                if(integer == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeInt32(integer.intValue(), bytebuffer);
                }
                i3++;
            } while(true);

        case 103: // 'g'
            Long along[] = (Long[])obj;
            encodeInt32(along.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int j3 = 0;
            do
            {
                if(j3 >= along.length)
                    break label0;
                Long long1 = along[j3];
                if(long1 == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeInt64(long1.longValue(), bytebuffer);
                }
                j3++;
            } while(true);

        case 104: // 'h'
            Float afloat[] = (Float[])obj;
            encodeInt32(afloat.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int k3 = 0;
            do
            {
                if(k3 >= afloat.length)
                    break label0;
                Float float1 = afloat[k3];
                if(float1 == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeFloat32(float1.floatValue(), bytebuffer);
                }
                k3++;
            } while(true);

        case 105: // 'i'
            Double adouble[] = (Double[])obj;
            encodeInt32(adouble.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int l3 = 0;
            do
            {
                if(l3 >= adouble.length)
                    break label0;
                Double double1 = adouble[l3];
                if(double1 == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeFloat64(double1.doubleValue(), bytebuffer);
                }
                l3++;
            } while(true);

        case 108: // 'l'
            String as[] = (String[])obj;
            encodeInt32(as.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int i4 = 0;
            do
            {
                if(i4 >= as.length)
                    break label0;
                String s = as[i4];
                if(s == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeUtf8Str(s, bytebuffer);
                }
                i4++;
            } while(true);

        case 109: // 'm'
            Date adate[] = (Date[])obj;
            encodeInt32(adate.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            int j4 = 0;
            do
            {
                if(j4 >= adate.length)
                    break label0;
                Date date = adate[j4];
                if(date == null)
                {
                    encodeInt8((byte)52, bytebuffer);
                } else
                {
                    encodeInt8((byte)53, bytebuffer);
                    encodeDate(date, bytebuffer);
                }
                j4++;
            } while(true);

        case 110: // 'n'
        case 111: // 'o'
        case 112: // 'p'
        case 113: // 'q'
        case 119: // 'w'
            Object aobj[] = (Object[])obj;
            encodeInt32(aobj.length, bytebuffer);
            encodeInt8(byte0, bytebuffer);
            byte byte1 = byte0 != 119 ? byte0 : -1;
            for(int k4 = 0; k4 < aobj.length; k4++)
                marshalElem(bytebuffer, aobj[k4], byte1, marshalcircrefsbuf);

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

    public void encodeInt8(byte byte0, ByteBuffer bytebuffer)
    {
        bytebuffer.put(byte0);
    }

    public byte decodeInt8(ByteBuffer bytebuffer)
    {
        return bytebuffer.get();
    }

    public void encodeInt16(short word0, ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)(word0 >>> 8));
        bytebuffer.put((byte)word0);
    }

    public short decodeInt16(ByteBuffer bytebuffer)
    {
        return (short)(((bytebuffer.get() & 0xff) << 8) + (bytebuffer.get() & 0xff));
    }

    public void encodeInt32(int i, ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)(i >>> 24));
        bytebuffer.put((byte)(i >>> 16));
        bytebuffer.put((byte)(i >>> 8));
        bytebuffer.put((byte)i);
    }

    public int decodeInt32(ByteBuffer bytebuffer)
    {
        return ((bytebuffer.get() & 0xff) << 24) + ((bytebuffer.get() & 0xff) << 16) + ((bytebuffer.get() & 0xff) << 8) + (bytebuffer.get() & 0xff);
    }

    public void encodeInt64(long l, ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)(int)(l >>> 56));
        bytebuffer.put((byte)(int)(l >>> 48));
        bytebuffer.put((byte)(int)(l >>> 40));
        bytebuffer.put((byte)(int)(l >>> 32));
        bytebuffer.put((byte)(int)(l >>> 24));
        bytebuffer.put((byte)(int)(l >>> 16));
        bytebuffer.put((byte)(int)(l >>> 8));
        bytebuffer.put((byte)(int)l);
    }

    public long decodeInt64(ByteBuffer bytebuffer)
    {
        return (((long)bytebuffer.get() & 255L) << 56) + (((long)bytebuffer.get() & 255L) << 48) + (((long)bytebuffer.get() & 255L) << 40) + (((long)bytebuffer.get() & 255L) << 32) + (((long)bytebuffer.get() & 255L) << 24) + (((long)bytebuffer.get() & 255L) << 16) + (((long)bytebuffer.get() & 255L) << 8) + ((long)bytebuffer.get() & 255L);
    }

    public void encodeFloat32(float f, ByteBuffer bytebuffer)
    {
        int i = Float.floatToIntBits(f);
        bytebuffer.put((byte)(i >>> 24));
        bytebuffer.put((byte)(i >>> 16));
        bytebuffer.put((byte)(i >>> 8));
        bytebuffer.put((byte)i);
    }

    public float decodeFloat32(ByteBuffer bytebuffer)
    {
        return Float.intBitsToFloat(((bytebuffer.get() & 0xff) << 24) + ((bytebuffer.get() & 0xff) << 16) + ((bytebuffer.get() & 0xff) << 8) + (bytebuffer.get() & 0xff));
    }

    public void encodeFloat64(double d, ByteBuffer bytebuffer)
    {
        long l = Double.doubleToLongBits(d);
        bytebuffer.put((byte)(int)(l >>> 56));
        bytebuffer.put((byte)(int)(l >>> 48));
        bytebuffer.put((byte)(int)(l >>> 40));
        bytebuffer.put((byte)(int)(l >>> 32));
        bytebuffer.put((byte)(int)(l >>> 24));
        bytebuffer.put((byte)(int)(l >>> 16));
        bytebuffer.put((byte)(int)(l >>> 8));
        bytebuffer.put((byte)(int)l);
    }

    public double decodeFloat64(ByteBuffer bytebuffer)
    {
        return Double.longBitsToDouble((((long)bytebuffer.get() & 255L) << 56) + (((long)bytebuffer.get() & 255L) << 48) + (((long)bytebuffer.get() & 255L) << 40) + (((long)bytebuffer.get() & 255L) << 32) + (((long)bytebuffer.get() & 255L) << 24) + (((long)bytebuffer.get() & 255L) << 16) + (((long)bytebuffer.get() & 255L) << 8) + ((long)bytebuffer.get() & 255L));
    }

    public void encodeChar16(char c, ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)(c >>> 8));
        bytebuffer.put((byte)c);
    }

    public char decodeChar16(ByteBuffer bytebuffer)
    {
        return (char)(((bytebuffer.get() & 0xff) << 8) + (bytebuffer.get() & 0xff));
    }

    public void encodeUtf8Str(String s, ByteBuffer bytebuffer)
    {
        int i = s.length();
        encodeInt32(i, bytebuffer);
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            if(c >= '\001' && c <= '\177')
            {
                bytebuffer.put((byte)c);
                continue;
            }
            if(c == 0 || '\200' <= c && c <= '\u07FF')
            {
                bytebuffer.put((byte)(c >> 6 | 0xc0));
                bytebuffer.put((byte)(c & 0x3f | 0x80));
            } else
            {
                bytebuffer.put((byte)(c >> 12 | 0xe0));
                bytebuffer.put((byte)(c >> 6 | 0xc0));
                bytebuffer.put((byte)(c & 0x3f | 0x80));
            }
        }

    }

    public String decodeUtf8Str(ByteBuffer bytebuffer)
    {
        int i = decodeInt32(bytebuffer);
        char ac[] = new char[i];
        int j = 0;
        for(int k = 0; j < i; k++)
        {
            byte byte0 = bytebuffer.get();
            if(byte0 >= 1 && byte0 <= 127)
                ac[k] = (char)byte0;
            else
            if((byte0 & 0xe0) == 192)
            {
                byte byte1 = bytebuffer.get();
                ac[k] = (char)((byte0 & 0x1f) << 6 | byte1 & 0x3f);
            } else
            {
                byte byte2 = bytebuffer.get();
                byte byte3 = bytebuffer.get();
                ac[k] = (char)((byte0 & 0xf) << 12 | (byte2 & 0x3f) << 6 | byte3 & 0x3f);
            }
            j++;
        }

        return new String(ac);
    }

    public void encodeBool(boolean flag, ByteBuffer bytebuffer)
    {
        bytebuffer.put(((byte)(!flag ? 0 : 1)));
    }

    public boolean decodeBool(ByteBuffer bytebuffer)
    {
        return bytebuffer.get() != 0;
    }

    public void encodeDate(Date date, ByteBuffer bytebuffer)
    {
        ArgAssert.illegalRange(date.getTime() >= 0L, "value", "value.getTime() >= 0");
        encodeInt8((byte)113, bytebuffer);
        encodeInt64(date.getTime(), bytebuffer);
    }

    public Date decodeDate(ByteBuffer bytebuffer)
    {
        byte byte0 = decodeInt8(bytebuffer);
        if(byte0 == 113)
            return new Date(decodeInt64(bytebuffer));
        if(byte0 == 114)
            return dotNetDate2Java(decodeInt64(bytebuffer));
        else
            throw new IllegalStateException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
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
        $assertionsDisabled = !(NioMarshallerImpl.class).desiredAssertionStatus();
    }
}
