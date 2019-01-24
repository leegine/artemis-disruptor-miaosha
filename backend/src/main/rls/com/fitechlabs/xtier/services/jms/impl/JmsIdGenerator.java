// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.jms.impl;

import com.fitechlabs.xtier.utils.Utils;

class JmsIdGenerator
{

    JmsIdGenerator()
    {
    }

    static synchronized long genId()
    {
        if(lastCount == 32767L)
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
            lastCount = -32768L;
        }
        lastCount++;
        return lastTime & 0xffffffL | lastCount << 6;
    }

    private static long lastCount = -32768L;
    private static long lastTime = System.currentTimeMillis();

}
