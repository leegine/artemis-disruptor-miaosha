// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.uidgen.impl;

import com.fitechlabs.xtier.utils.Utils;

class Uid8Gen
{

    Uid8Gen()
    {
        time = new byte[6];
        lastTime = System.currentTimeMillis();
        lastCount = -32768;
        resetTimeArr();
    }

    private void resetTimeArr()
    {
        for(int i = 0; i < 6; i++)
            time[i] = (byte)(int)(lastTime >>> (i << 3));

    }

    int setUid8(byte abyte0[], int i)
    {
        if(lastCount == 32767)
        {
            long l;
            do
            {
                l = System.currentTimeMillis();
                if(l != lastTime)
                    break;
                Utils.sleep(10L);
            } while(true);
            lastTime = l;
            lastCount = -32768;
            resetTimeArr();
        }
        lastCount++;
        System.arraycopy(time, 0, abyte0, i, 6);
        i += 6;
        abyte0[i++] = (byte)(lastCount >>> 8);
        abyte0[i++] = (byte)lastCount;
        return i;
    }

    private byte time[];
    private long lastTime;
    private short lastCount;
}
