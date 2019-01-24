// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SortedList.java

package co.lib.collection;

import java.util.*;

public interface SortedList
{

    public abstract void setComparator(Comparator comparator);

    public abstract Comparator getComparator();

    public abstract void add(Object obj);

    public abstract int indexOf(Object obj);

    public abstract Object get(int i);

    public abstract Object remove(int i);

    public abstract void clear();

    public abstract int size();

    public abstract Iterator gt(Object obj);

    public abstract Iterator ge(Object obj);

    public abstract Iterator eq(Object obj);

    public abstract Iterator le(Object obj);

    public abstract Iterator lt(Object obj);

    public abstract List subList(int i, int j);

    public abstract List asList();

    public abstract int findFirst(Object obj);

    public abstract int findLast(Object obj);

    public static final int LT = 0;
    public static final int LE = 1;
    public static final int EQ = 2;
    public static final int GE = 3;
    public static final int GT = 4;
}
