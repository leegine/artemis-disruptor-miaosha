// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   IOUtil.java

package co.fin.intellioms.util;

import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;

public class IOUtil
{

    public IOUtil()
    {
    }

    public static byte[] encodeBigDecimal(BigDecimal bd)
    {
        byte unscaled[] = bd.unscaledValue().toByteArray();
        byte buf[] = new byte[8 + unscaled.length];
        int off = Utils.encodeInt32(bd.scale(), buf, 0);
        off = Utils.encodeInt32(unscaled.length, buf, off);
        System.arraycopy(unscaled, 0, buf, off, unscaled.length);
        return buf;
    }

    public static void encodeBigDecimal(BigDecimal bd, OutputStream out)
        throws IOException
    {
        byte unscaled[] = bd.unscaledValue().toByteArray();
        byte buf[] = new byte[8];
        int off = Utils.encodeInt32(bd.scale(), buf, 0);
        Utils.encodeInt32(unscaled.length, buf, off);
        out.write(buf);
        out.write(unscaled);
    }

    public static BigDecimal decodeBigDecimal(byte buf[], int off)
    {
        int scale = Utils.decodeInt32(buf, off);
        int length = Utils.decodeInt32(buf, off + 4);
        byte data[] = new byte[length];
        System.arraycopy(buf, off + 8, data, 0, length);
        return new BigDecimal(new BigInteger(data), scale);
    }

    public static BigDecimal decodeBigDecimal(InputStream is)
        throws IOException
    {
        byte buf[] = new byte[8];
        if(is.read(buf) < buf.length)
        {
            throw new IOException("Unable to read BigDecimal value: Unexpected end of stream.");
        } else
        {
            int scale = Utils.decodeInt32(buf, 0);
            int length = Utils.decodeInt32(buf, 4);
            byte data[] = new byte[length];
            is.read(data);
            return new BigDecimal(new BigInteger(data), scale);
        }
    }
}
