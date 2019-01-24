// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.marshal.impl;

import java.util.*;

class MarshalCircRefsBuf
{

    MarshalCircRefsBuf()
    {
        marshal = false;
        list = new ArrayList();
        map = new IdentityHashMap();
        cnt = 0;
    }

    int add(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(marshal)
        {
            if(cnt < 15)
                list.add(obj);
            else
            if(cnt == 15)
            {
                if(!$assertionsDisabled && !map.isEmpty())
                    throw new AssertionError();
                for(int i = 0; i < cnt; i++)
                    map.put(list.get(i), new Integer(i));

                map.put(obj, new Integer(cnt));
            } else
            {
                map.put(obj, new Integer(cnt));
            }
        } else
        {
            list.add(obj);
        }
        return cnt++;
    }

    int lookupObj(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !marshal)
            throw new AssertionError();
        if(cnt < 15)
        {
            for(int i = 0; i < cnt; i++)
                if(list.get(i) == obj)
                    return i;

            return -1;
        } else
        {
            Integer integer = (Integer)map.get(obj);
            return integer != null ? integer.intValue() : -1;
        }
    }

    void replace(int i, Object obj)
    {
        if(!$assertionsDisabled && marshal)
            throw new AssertionError();
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && i >= cnt)
        {
            throw new AssertionError();
        } else
        {
            list.set(i, obj);
            return;
        }
    }

    Object lookupRef(int i)
    {
        if(!$assertionsDisabled && marshal)
            throw new AssertionError();
        if(!$assertionsDisabled && i >= cnt)
            throw new AssertionError();
        else
            return list.get(i);
    }

    void reset(boolean flag)
    {
        marshal = flag;
        cnt = 0;
        list.clear();
        map.clear();
    }

    public String toString()
    {
        if(!$assertionsDisabled)
            throw new AssertionError();
        else
            return "Marshal circular reference buffer [count=" + cnt + ", marshal=" + marshal + ", list-size=" + list.size() + ", map-size=" + map.size() + ']';
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

    private static final int THRESHOLD = 15;
    private boolean marshal;
    private ArrayList list;
    private Map map;
    private int cnt;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(MarshalCircRefsBuf.class).desiredAssertionStatus();
    }
}
