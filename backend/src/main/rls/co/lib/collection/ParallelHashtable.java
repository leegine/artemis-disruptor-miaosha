// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ParallelHashtable.java

package co.lib.collection;

import java.util.Hashtable;

public class ParallelHashtable
{

    public ParallelHashtable(int size, int multi)
    {
        init(size, multi);
    }

    public ParallelHashtable(int size)
    {
        init(size, 16);
    }

    private void init(int size, int multi)
    {
        multiplicity = multi;
        tables = new Hashtable[multiplicity];
        for(int i = 0; i < multiplicity; i++)
            tables[i] = new Hashtable(size);

    }

    public ParallelHashtable()
    {
        multiplicity = 16;
        tables = new Hashtable[multiplicity];
        for(int i = 0; i < multiplicity; i++)
            tables[i] = new Hashtable();

    }

    public Object put(Object key, Object val)
    {
        int pos = key.hashCode() % multiplicity;
        Object old = tables[pos].get(key);
        tables[pos].put(key, val);
        return old;
    }

    public Object get(Object key)
    {
        int pos = key.hashCode() % multiplicity;
        return tables[pos].get(key);
    }

    static final int DEFAULT_MULTIPLICITY = 16;
    Hashtable tables[];
    int multiplicity;
}
