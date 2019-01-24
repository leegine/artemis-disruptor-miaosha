// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.marshal.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.marshal.impl:
//            BaseMarshaller, MarshalCircRefsBuf, ArrayInfo

public class IoMarshallerImpl extends BaseMarshaller
    implements IoMarshaller
{

    IoMarshallerImpl(List list)
    {
        super(list);
    }

    public Marshallable demarshalObj(InputStream inputstream, Marshallable marshallable)
        throws MarshalException, IOException
    {
        try
        {
            byte byte0 = decodeInt8(inputstream);
            if(byte0 != 113 && byte0 != 114)
                throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
            else
                return readMrshl(inputstream, marshallable, getFactories(), getCircRefsBuf(false));
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public Marshallable demarshalObj(InputStream inputstream)
        throws MarshalException, IOException
    {
        try
        {
            byte byte0 = decodeInt8(inputstream);
            if(byte0 != 113 && byte0 != 114)
                throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
            else
                return readMrshl(inputstream, null, getFactories(), getCircRefsBuf(false));
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    public void marshalObj(Marshallable marshallable, OutputStream outputstream)
        throws MarshalException, IOException
    {
        try
        {
            marshallable.onMarshal();
            MarshalCircRefsBuf marshalcircrefsbuf = getCircRefsBuf(true);
            marshalcircrefsbuf.add(marshallable);
            encodeInt8((byte)113, outputstream);
            writeMrshl(outputstream, marshallable, marshalcircrefsbuf);
        }
        catch(RuntimeException runtimeexception)
        {
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR11"), runtimeexception);
        }
    }

    private Marshallable readMrshl(InputStream inputstream, Marshallable marshallable, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws IOException, MarshalException
    {
        short word0 = decodeInt16(inputstream);
        int i = decodeInt32(inputstream);
        HashMap hashmap = new HashMap(Utils.getNonRehashCapacity(i));
        if(marshallable == null)
            marshallable = getMarshObj(list, word0);
        else
        if(marshallable.typeGuid() != word0)
            throw new MarshalException(L10n.format("SRVC.MARSHAL.ERR14", new Short(word0)));
        marshalcircrefsbuf.add(marshallable);
        for(int j = 0; j < i; j++)
        {
            Object obj = demarshalElem(inputstream, list, marshalcircrefsbuf);
            Object obj1 = demarshalElem(inputstream, list, marshalcircrefsbuf);
            hashmap.put(obj, obj1);
        }

        marshallable.setObjs(hashmap);
        marshallable.onDemarshal();
        return marshallable;
    }

    private Object demarshalElem(InputStream inputstream, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws IOException, MarshalException
    {
        byte byte0 = decodeInt8(inputstream);
        switch(byte0)
        {
        case 52: // '4'
            return null;

        case 51: // '3'
            return marshalcircrefsbuf.lookupRef(decodeInt32(inputstream));

        case 100: // 'd'
            return new Byte(decodeInt8(inputstream));

        case 101: // 'e'
            return new Short(decodeInt16(inputstream));

        case 102: // 'f'
            return new Integer(decodeInt32(inputstream));

        case 103: // 'g'
            return new Long(decodeInt64(inputstream));

        case 104: // 'h'
            return new Float(decodeFloat32(inputstream));

        case 105: // 'i'
            return new Double(decodeFloat64(inputstream));

        case 107: // 'k'
            return new Character(decodeChar16(inputstream));

        case 106: // 'j'
            return new Boolean(decodeBool(inputstream));

        case 109: // 'm'
            return decodeDate(inputstream);

        case 110: // 'n'
            return readArr(inputstream, list, marshalcircrefsbuf);

        case 108: // 'l'
            return decodeUtf8Str(inputstream);

        case 113: // 'q'
            return readMrshl(inputstream, null, list, marshalcircrefsbuf);

        case 111: // 'o'
            int i = decodeInt32(inputstream);
            ArrayList arraylist = new ArrayList(i);
            marshalcircrefsbuf.add(arraylist);
            for(int k = 0; k < i; k++)
                arraylist.add(demarshalElem(inputstream, list, marshalcircrefsbuf));

            return arraylist;

        case 112: // 'p'
            int j = decodeInt32(inputstream);
            HashMap hashmap = new HashMap(Utils.getNonRehashCapacity(j));
            marshalcircrefsbuf.add(hashmap);
            for(int l = 0; l < j; l++)
            {
                Object obj = demarshalElem(inputstream, list, marshalcircrefsbuf);
                Object obj1 = demarshalElem(inputstream, list, marshalcircrefsbuf);
                hashmap.put(obj, obj1);
            }

            return hashmap;
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid object type: " + byte0);
        else
            return null;
    }

    private Object readArr(InputStream inputstream, List list, MarshalCircRefsBuf marshalcircrefsbuf)
        throws IOException, MarshalException
    {
        int i = decodeInt32(inputstream);
        byte byte0 = decodeInt8(inputstream);
        switch(byte0)
        {
        case 120: // 'x'
            byte abyte0[] = new byte[i];
            marshalcircrefsbuf.add(abyte0);
            for(int j = 0; j < i; j++)
                abyte0[j] = decodeInt8(inputstream);

            return abyte0;

        case 121: // 'y'
            short aword0[] = new short[i];
            marshalcircrefsbuf.add(aword0);
            for(int k = 0; k < i; k++)
                aword0[k] = decodeInt16(inputstream);

            return aword0;

        case 122: // 'z'
            int ai[] = new int[i];
            marshalcircrefsbuf.add(ai);
            for(int l = 0; l < i; l++)
                ai[l] = decodeInt32(inputstream);

            return ai;

        case 123: // '{'
            long al[] = new long[i];
            marshalcircrefsbuf.add(al);
            for(int i1 = 0; i1 < i; i1++)
                al[i1] = decodeInt64(inputstream);

            return al;

        case 126: // '~'
            boolean aflag[] = new boolean[i];
            marshalcircrefsbuf.add(aflag);
            for(int j1 = 0; j1 < i; j1++)
                aflag[j1] = decodeBool(inputstream);

            return aflag;

        case 127: // '\177'
            char ac[] = new char[i];
            marshalcircrefsbuf.add(ac);
            for(int k1 = 0; k1 < i; k1++)
                ac[k1] = decodeChar16(inputstream);

            return ac;

        case 124: // '|'
            float af[] = new float[i];
            marshalcircrefsbuf.add(af);
            for(int l1 = 0; l1 < i; l1++)
                af[l1] = decodeFloat32(inputstream);

            return af;

        case 125: // '}'
            double ad[] = new double[i];
            marshalcircrefsbuf.add(ad);
            for(int i2 = 0; i2 < i; i2++)
                ad[i2] = decodeFloat64(inputstream);

            return ad;

        case 108: // 'l'
            String as[] = new String[i];
            marshalcircrefsbuf.add(as);
            for(int j2 = 0; j2 < i; j2++)
                as[j2] = decodeInt8(inputstream) != 52 ? decodeUtf8Str(inputstream) : null;

            return as;

        case 109: // 'm'
            Date adate[] = new Date[i];
            marshalcircrefsbuf.add(adate);
            for(int k2 = 0; k2 < i; k2++)
                adate[k2] = decodeInt8(inputstream) != 52 ? decodeDate(inputstream) : null;

            return adate;

        case 107: // 'k'
            Character acharacter[] = new Character[i];
            marshalcircrefsbuf.add(acharacter);
            for(int l2 = 0; l2 < i; l2++)
                acharacter[l2] = decodeInt8(inputstream) != 52 ? new Character(decodeChar16(inputstream)) : null;

            return acharacter;

        case 105: // 'i'
            Double adouble[] = new Double[i];
            marshalcircrefsbuf.add(adouble);
            for(int i3 = 0; i3 < i; i3++)
                adouble[i3] = decodeInt8(inputstream) != 52 ? new Double(decodeFloat64(inputstream)) : null;

            return adouble;

        case 104: // 'h'
            Float afloat[] = new Float[i];
            marshalcircrefsbuf.add(afloat);
            for(int j3 = 0; j3 < i; j3++)
                afloat[j3] = decodeInt8(inputstream) != 52 ? new Float(decodeFloat32(inputstream)) : null;

            return afloat;

        case 103: // 'g'
            Long along[] = new Long[i];
            marshalcircrefsbuf.add(along);
            for(int k3 = 0; k3 < i; k3++)
                along[k3] = decodeInt8(inputstream) != 52 ? new Long(decodeInt64(inputstream)) : null;

            return along;

        case 102: // 'f'
            Integer ainteger[] = new Integer[i];
            marshalcircrefsbuf.add(ainteger);
            for(int l3 = 0; l3 < i; l3++)
                ainteger[l3] = decodeInt8(inputstream) != 52 ? new Integer(decodeInt32(inputstream)) : null;

            return ainteger;

        case 101: // 'e'
            Short ashort[] = new Short[i];
            marshalcircrefsbuf.add(ashort);
            for(int i4 = 0; i4 < i; i4++)
                ashort[i4] = decodeInt8(inputstream) != 52 ? new Short(decodeInt16(inputstream)) : null;

            return ashort;

        case 100: // 'd'
            Byte abyte[] = new Byte[i];
            marshalcircrefsbuf.add(abyte);
            for(int j4 = 0; j4 < i; j4++)
                abyte[j4] = decodeInt8(inputstream) != 52 ? new Byte(decodeInt8(inputstream)) : null;

            return abyte;

        case 106: // 'j'
            Boolean aboolean[] = new Boolean[i];
            marshalcircrefsbuf.add(aboolean);
            for(int k4 = 0; k4 < i; k4++)
                aboolean[k4] = decodeInt8(inputstream) != 52 ? new Boolean(decodeBool(inputstream)) : null;

            return aboolean;

        case 110: // 'n'
            Object aobj[] = new Object[i];
            int l4 = marshalcircrefsbuf.add(((Object) (aobj)));
            ArrayInfo arrayinfo = null;
            for(int i6 = 0; i6 < i; i6++)
            {
                aobj[i6] = demarshalElem(inputstream, list, marshalcircrefsbuf);
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
                amarshallable[i5] = (Marshallable)demarshalElem(inputstream, list, marshalcircrefsbuf);

            return amarshallable;

        case 111: // 'o'
            List alist[] = new List[i];
            marshalcircrefsbuf.add(alist);
            for(int j5 = 0; j5 < i; j5++)
                alist[j5] = (List)demarshalElem(inputstream, list, marshalcircrefsbuf);

            return alist;

        case 112: // 'p'
            Map amap[] = new Map[i];
            marshalcircrefsbuf.add(amap);
            for(int k5 = 0; k5 < i; k5++)
                amap[k5] = (Map)demarshalElem(inputstream, list, marshalcircrefsbuf);

            return amap;

        case 119: // 'w'
            Object aobj1[] = new Object[i];
            marshalcircrefsbuf.add(((Object) (aobj1)));
            for(int l5 = 0; l5 < i; l5++)
                aobj1[l5] = demarshalElem(inputstream, list, marshalcircrefsbuf);

            return ((Object) (aobj1));
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid object type: " + byte0);
        else
            return null;
    }

    private void writeMrshl(OutputStream outputstream, Marshallable marshallable, MarshalCircRefsBuf marshalcircrefsbuf)
        throws IOException, MarshalException
    {
        marshallable.onMarshal();
        Map map = marshallable.getObjs();
        encodeInt16(marshallable.typeGuid(), outputstream);
        encodeInt32(map.size(), outputstream);
        Map.Entry entry;
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); marshalElem(outputstream, entry.getValue(), (byte)-1, marshalcircrefsbuf))
        {
            entry = (Map.Entry)iterator.next();
            marshalElem(outputstream, entry.getKey(), (byte)-1, marshalcircrefsbuf);
        }

    }

    private void marshalElem(OutputStream outputstream, Object obj, byte byte0, MarshalCircRefsBuf marshalcircrefsbuf)
        throws IOException, MarshalException
    {
        if(obj == null)
        {
            encodeInt8((byte)52, outputstream);
        } else
        {
            byte byte1 = byte0 != -1 ? byte0 : getType(obj.getClass());
            switch(byte1)
            {
            case 106: // 'j'
                encodeInt8(byte1, outputstream);
                encodeBool(((Boolean)obj).booleanValue(), outputstream);
                break;

            case 107: // 'k'
                encodeInt8(byte1, outputstream);
                encodeChar16(((Character)obj).charValue(), outputstream);
                break;

            case 109: // 'm'
                encodeInt8(byte1, outputstream);
                encodeDate((Date)obj, outputstream);
                break;

            case 104: // 'h'
                encodeInt8(byte1, outputstream);
                encodeFloat32(((Float)obj).floatValue(), outputstream);
                break;

            case 105: // 'i'
                encodeInt8(byte1, outputstream);
                encodeFloat64(((Double)obj).doubleValue(), outputstream);
                break;

            case 100: // 'd'
                encodeInt8(byte1, outputstream);
                encodeInt8(((Byte)obj).byteValue(), outputstream);
                break;

            case 101: // 'e'
                encodeInt8(byte1, outputstream);
                encodeInt16(((Short)obj).shortValue(), outputstream);
                break;

            case 102: // 'f'
                encodeInt8(byte1, outputstream);
                encodeInt32(((Integer)obj).intValue(), outputstream);
                break;

            case 103: // 'g'
                encodeInt8(byte1, outputstream);
                encodeInt64(((Long)obj).longValue(), outputstream);
                break;

            case 108: // 'l'
                encodeInt8(byte1, outputstream);
                encodeUtf8Str((String)obj, outputstream);
                break;

            case 110: // 'n'
                int i = marshalcircrefsbuf.lookupObj(obj);
                if(i != -1)
                {
                    encodeInt8((byte)51, outputstream);
                    encodeInt32(i, outputstream);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    encodeInt8(byte1, outputstream);
                    writeArr(outputstream, obj, marshalcircrefsbuf);
                }
                break;

            case 113: // 'q'
                int j = marshalcircrefsbuf.lookupObj(obj);
                if(j != -1)
                {
                    encodeInt8((byte)51, outputstream);
                    encodeInt32(j, outputstream);
                } else
                {
                    marshalcircrefsbuf.add(obj);
                    encodeInt8(byte1, outputstream);
                    writeMrshl(outputstream, (Marshallable)obj, marshalcircrefsbuf);
                }
                break;

            case 111: // 'o'
                int k = marshalcircrefsbuf.lookupObj(obj);
                if(k != -1)
                {
                    encodeInt8((byte)51, outputstream);
                    encodeInt32(k, outputstream);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                encodeInt8(byte1, outputstream);
                List list = (List)obj;
                int i1 = list.size();
                encodeInt32(i1, outputstream);
                for(int k1 = 0; k1 < i1; k1++)
                    marshalElem(outputstream, list.get(k1), (byte)-1, marshalcircrefsbuf);

                break;

            case 112: // 'p'
                int l = marshalcircrefsbuf.lookupObj(obj);
                if(l != -1)
                {
                    encodeInt8((byte)51, outputstream);
                    encodeInt32(l, outputstream);
                    break;
                }
                marshalcircrefsbuf.add(obj);
                encodeInt8(byte1, outputstream);
                Map map = (Map)obj;
                int j1 = map.size();
                encodeInt32(j1, outputstream);
                Map.Entry entry;
                for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); marshalElem(outputstream, entry.getValue(), (byte)-1, marshalcircrefsbuf))
                {
                    entry = (Map.Entry)iterator.next();
                    marshalElem(outputstream, entry.getKey(), (byte)-1, marshalcircrefsbuf);
                }

                break;

            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid object type: " + byte1);
                break;
            }
        }
    }

    private void writeArr(OutputStream outputstream, Object obj, MarshalCircRefsBuf marshalcircrefsbuf)
        throws IOException, MarshalException
    {
        if(!$assertionsDisabled && !obj.getClass().isArray())
            throw new AssertionError();
        byte byte0 = getType(obj.getClass().getComponentType());
label0:
        switch(byte0)
        {
        case 120: // 'x'
            byte abyte0[] = (byte[])obj;
            encodeInt32(abyte0.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int i = 0; i < abyte0.length; i++)
                encodeInt8(abyte0[i], outputstream);

            break;

        case 121: // 'y'
            short aword0[] = (short[])obj;
            encodeInt32(aword0.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int j = 0; j < aword0.length; j++)
                encodeInt16(aword0[j], outputstream);

            break;

        case 122: // 'z'
            int ai[] = (int[])obj;
            encodeInt32(ai.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int k = 0; k < ai.length; k++)
                encodeInt32(ai[k], outputstream);

            break;

        case 123: // '{'
            long al[] = (long[])obj;
            encodeInt32(al.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int l = 0; l < al.length; l++)
                encodeInt64(al[l], outputstream);

            break;

        case 124: // '|'
            float af[] = (float[])obj;
            encodeInt32(af.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int i1 = 0; i1 < af.length; i1++)
                encodeFloat32(af[i1], outputstream);

            break;

        case 125: // '}'
            double ad[] = (double[])obj;
            encodeInt32(ad.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int j1 = 0; j1 < ad.length; j1++)
                encodeFloat64(ad[j1], outputstream);

            break;

        case 127: // '\177'
            char ac[] = (char[])obj;
            encodeInt32(ac.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int k1 = 0; k1 < ac.length; k1++)
                encodeChar16(ac[k1], outputstream);

            break;

        case 126: // '~'
            boolean aflag[] = (boolean[])obj;
            encodeInt32(aflag.length, outputstream);
            encodeInt8(byte0, outputstream);
            for(int l1 = 0; l1 < aflag.length; l1++)
                encodeBool(aflag[l1], outputstream);

            break;

        case 100: // 'd'
            Byte abyte[] = (Byte[])obj;
            encodeInt32(abyte.length, outputstream);
            encodeInt8(byte0, outputstream);
            int i2 = 0;
            do
            {
                if(i2 >= abyte.length)
                    break label0;
                Byte byte2 = abyte[i2];
                if(byte2 == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeInt8(byte2.byteValue(), outputstream);
                }
                i2++;
            } while(true);

        case 107: // 'k'
            Character acharacter[] = (Character[])obj;
            encodeInt32(acharacter.length, outputstream);
            encodeInt8(byte0, outputstream);
            int j2 = 0;
            do
            {
                if(j2 >= acharacter.length)
                    break label0;
                Character character = acharacter[j2];
                if(character == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeChar16(character.charValue(), outputstream);
                }
                j2++;
            } while(true);

        case 106: // 'j'
            Boolean aboolean[] = (Boolean[])obj;
            encodeInt32(aboolean.length, outputstream);
            encodeInt8(byte0, outputstream);
            int k2 = 0;
            do
            {
                if(k2 >= aboolean.length)
                    break label0;
                Boolean boolean1 = aboolean[k2];
                if(boolean1 == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeBool(boolean1.booleanValue(), outputstream);
                }
                k2++;
            } while(true);

        case 101: // 'e'
            Short ashort[] = (Short[])obj;
            encodeInt32(ashort.length, outputstream);
            encodeInt8(byte0, outputstream);
            int l2 = 0;
            do
            {
                if(l2 >= ashort.length)
                    break label0;
                Short short1 = ashort[l2];
                if(short1 == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeInt16(short1.shortValue(), outputstream);
                }
                l2++;
            } while(true);

        case 102: // 'f'
            Integer ainteger[] = (Integer[])obj;
            encodeInt32(ainteger.length, outputstream);
            encodeInt8(byte0, outputstream);
            int i3 = 0;
            do
            {
                if(i3 >= ainteger.length)
                    break label0;
                Integer integer = ainteger[i3];
                if(integer == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeInt32(integer.intValue(), outputstream);
                }
                i3++;
            } while(true);

        case 103: // 'g'
            Long along[] = (Long[])obj;
            encodeInt32(along.length, outputstream);
            encodeInt8(byte0, outputstream);
            int j3 = 0;
            do
            {
                if(j3 >= along.length)
                    break label0;
                Long long1 = along[j3];
                if(long1 == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeInt64(long1.longValue(), outputstream);
                }
                j3++;
            } while(true);

        case 104: // 'h'
            Float afloat[] = (Float[])obj;
            encodeInt32(afloat.length, outputstream);
            encodeInt8(byte0, outputstream);
            int k3 = 0;
            do
            {
                if(k3 >= afloat.length)
                    break label0;
                Float float1 = afloat[k3];
                if(float1 == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeFloat32(float1.floatValue(), outputstream);
                }
                k3++;
            } while(true);

        case 105: // 'i'
            Double adouble[] = (Double[])obj;
            encodeInt32(adouble.length, outputstream);
            encodeInt8(byte0, outputstream);
            int l3 = 0;
            do
            {
                if(l3 >= adouble.length)
                    break label0;
                Double double1 = adouble[l3];
                if(double1 == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeFloat64(double1.doubleValue(), outputstream);
                }
                l3++;
            } while(true);

        case 108: // 'l'
            String as[] = (String[])obj;
            encodeInt32(as.length, outputstream);
            encodeInt8(byte0, outputstream);
            int i4 = 0;
            do
            {
                if(i4 >= as.length)
                    break label0;
                String s = as[i4];
                if(s == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeUtf8Str(s, outputstream);
                }
                i4++;
            } while(true);

        case 109: // 'm'
            Date adate[] = (Date[])obj;
            encodeInt32(adate.length, outputstream);
            encodeInt8(byte0, outputstream);
            int j4 = 0;
            do
            {
                if(j4 >= adate.length)
                    break label0;
                Date date = adate[j4];
                if(date == null)
                {
                    encodeInt8((byte)52, outputstream);
                } else
                {
                    encodeInt8((byte)53, outputstream);
                    encodeDate(date, outputstream);
                }
                j4++;
            } while(true);

        case 110: // 'n'
        case 111: // 'o'
        case 112: // 'p'
        case 113: // 'q'
        case 119: // 'w'
            Object aobj[] = (Object[])obj;
            encodeInt32(aobj.length, outputstream);
            encodeInt8(byte0, outputstream);
            byte byte1 = byte0 != 119 ? byte0 : -1;
            for(int k4 = 0; k4 < aobj.length; k4++)
                marshalElem(outputstream, aobj[k4], byte1, marshalcircrefsbuf);

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

    public void encodeInt8(byte byte0, OutputStream outputstream)
        throws IOException
    {
        outputstream.write(byte0);
    }

    public byte decodeInt8(InputStream inputstream)
        throws IOException
    {
        return readByte(inputstream);
    }

    public void encodeInt16(short word0, OutputStream outputstream)
        throws IOException
    {
        outputstream.write((byte)(word0 >>> 8));
        outputstream.write((byte)word0);
    }

    public short decodeInt16(InputStream inputstream)
        throws IOException
    {
        return (short)(((readByte(inputstream) & 0xff) << 8) + (readByte(inputstream) & 0xff));
    }

    public void encodeInt32(int i, OutputStream outputstream)
        throws IOException
    {
        outputstream.write((byte)(i >>> 24));
        outputstream.write((byte)(i >>> 16));
        outputstream.write((byte)(i >>> 8));
        outputstream.write((byte)i);
    }

    public int decodeInt32(InputStream inputstream)
        throws IOException
    {
        return ((readByte(inputstream) & 0xff) << 24) + ((readByte(inputstream) & 0xff) << 16) + ((readByte(inputstream) & 0xff) << 8) + (readByte(inputstream) & 0xff);
    }

    public void encodeInt64(long l, OutputStream outputstream)
        throws IOException
    {
        outputstream.write((byte)(int)(l >>> 56));
        outputstream.write((byte)(int)(l >>> 48));
        outputstream.write((byte)(int)(l >>> 40));
        outputstream.write((byte)(int)(l >>> 32));
        outputstream.write((byte)(int)(l >>> 24));
        outputstream.write((byte)(int)(l >>> 16));
        outputstream.write((byte)(int)(l >>> 8));
        outputstream.write((byte)(int)l);
    }

    public long decodeInt64(InputStream inputstream)
        throws IOException
    {
        return (((long)readByte(inputstream) & 255L) << 56) + (((long)readByte(inputstream) & 255L) << 48) + (((long)readByte(inputstream) & 255L) << 40) + (((long)readByte(inputstream) & 255L) << 32) + (((long)readByte(inputstream) & 255L) << 24) + (((long)readByte(inputstream) & 255L) << 16) + (((long)readByte(inputstream) & 255L) << 8) + ((long)readByte(inputstream) & 255L);
    }

    public void encodeChar16(char c, OutputStream outputstream)
        throws IOException
    {
        outputstream.write((byte)(c >>> 8));
        outputstream.write((byte)c);
    }

    public char decodeChar16(InputStream inputstream)
        throws IOException
    {
        return (char)(((readByte(inputstream) & 0xff) << 8) + (readByte(inputstream) & 0xff));
    }

    public void encodeBool(boolean flag, OutputStream outputstream)
        throws IOException
    {
        outputstream.write(!flag ? 0 : 1);
    }

    public boolean decodeBool(InputStream inputstream)
        throws IOException
    {
        return readByte(inputstream) != 0;
    }

    public void encodeFloat32(float f, OutputStream outputstream)
        throws IOException
    {
        int i = Float.floatToIntBits(f);
        outputstream.write((byte)(i >>> 24));
        outputstream.write((byte)(i >>> 16));
        outputstream.write((byte)(i >>> 8));
        outputstream.write((byte)i);
    }

    public float decodeFloat32(InputStream inputstream)
        throws IOException
    {
        return Float.intBitsToFloat(((readByte(inputstream) & 0xff) << 24) + ((readByte(inputstream) & 0xff) << 16) + ((readByte(inputstream) & 0xff) << 8) + (readByte(inputstream) & 0xff));
    }

    public void encodeFloat64(double d, OutputStream outputstream)
        throws IOException
    {
        long l = Double.doubleToLongBits(d);
        outputstream.write((byte)(int)(l >>> 56));
        outputstream.write((byte)(int)(l >>> 48));
        outputstream.write((byte)(int)(l >>> 40));
        outputstream.write((byte)(int)(l >>> 32));
        outputstream.write((byte)(int)(l >>> 24));
        outputstream.write((byte)(int)(l >>> 16));
        outputstream.write((byte)(int)(l >>> 8));
        outputstream.write((byte)(int)l);
    }

    public double decodeFloat64(InputStream inputstream)
        throws IOException
    {
        return Double.longBitsToDouble((((long)readByte(inputstream) & 255L) << 56) + (((long)readByte(inputstream) & 255L) << 48) + (((long)readByte(inputstream) & 255L) << 40) + (((long)readByte(inputstream) & 255L) << 32) + (((long)readByte(inputstream) & 255L) << 24) + (((long)readByte(inputstream) & 255L) << 16) + (((long)readByte(inputstream) & 255L) << 8) + ((long)readByte(inputstream) & 255L));
    }

    public void encodeUtf8Str(String s, OutputStream outputstream)
        throws IOException
    {
        int i = s.length();
        encodeInt32(i, outputstream);
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            if(c >= '\001' && c <= '\177')
            {
                outputstream.write((byte)c);
                continue;
            }
            if(c == 0 || '\200' <= c && c <= '\u07FF')
            {
                outputstream.write((byte)(c >> 6 | 0xc0));
                outputstream.write((byte)(c & 0x3f | 0x80));
            } else
            {
                outputstream.write((byte)(c >> 12 | 0xe0));
                outputstream.write((byte)(c >> 6 | 0xc0));
                outputstream.write((byte)(c & 0x3f | 0x80));
            }
        }

    }

    public String decodeUtf8Str(InputStream inputstream)
        throws IOException
    {
        int i = decodeInt32(inputstream);
        char ac[] = new char[i];
        int j = 0;
        for(int k = 0; j < i; k++)
        {
            byte byte0 = readByte(inputstream);
            if(byte0 >= 1 && byte0 <= 127)
                ac[k] = (char)byte0;
            else
            if((byte0 & 0xe0) == 192)
            {
                byte byte1 = readByte(inputstream);
                ac[k] = (char)((byte0 & 0x1f) << 6 | byte1 & 0x3f);
            } else
            {
                byte byte2 = readByte(inputstream);
                byte byte3 = readByte(inputstream);
                ac[k] = (char)((byte0 & 0xf) << 12 | (byte2 & 0x3f) << 6 | byte3 & 0x3f);
            }
            j++;
        }

        return new String(ac);
    }

    public void encodeDate(Date date, OutputStream outputstream)
        throws IOException
    {
        ArgAssert.illegalRange(date.getTime() >= 0L, "value", "value.getTime() >= 0");
        encodeInt8((byte)113, outputstream);
        encodeInt64(date.getTime(), outputstream);
    }

    public Date decodeDate(InputStream inputstream)
        throws IOException
    {
        byte byte0 = decodeInt8(inputstream);
        if(byte0 == 113)
            return new Date(decodeInt64(inputstream));
        if(byte0 == 114)
            return dotNetDate2Java(decodeInt64(inputstream));
        else
            throw new IllegalStateException(L10n.format("SRVC.MARSHAL.ERR10", new Byte(byte0)));
    }

    private byte readByte(InputStream inputstream)
        throws IOException
    {
        int i = inputstream.read();
        if(i == -1)
            throw new IOException(L10n.format("SRVC.MARSHAL.ERR6"));
        else
            return (byte)i;
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
        $assertionsDisabled = !(IoMarshallerImpl.class).desiredAssertionStatus();
    }
}
