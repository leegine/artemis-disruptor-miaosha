// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SortedListImpl.java

package co.lib.collection.impl;

import co.lib.collection.SortedList;

import java.util.*;

// Referenced classes of package com.com.lib.collection.impl:
//            SortedListSearchResult

public class SortedListImpl
    implements SortedList
{

    public SortedListImpl()
    {
        sortedList = new ArrayList();
    }

    public SortedListImpl(Comparator cmp)
    {
        sortedList = new ArrayList();
        comparator = cmp;
    }

    public SortedListImpl(Comparator cmp, int capacity)
    {
        sortedList = new ArrayList(capacity);
        comparator = cmp;
    }

    public void setComparator(Comparator cmp)
    {
        comparator = cmp;
    }

    public Comparator getComparator()
    {
        return comparator;
    }

    public void add(Object x)
    {
        int idx = findFirst(x);
        if(idx < size())
            sortedList.add(idx, x);
        else
            sortedList.add(x);
    }

    public int indexOf(Object x)
    {
        return sortedList.indexOf(x);
    }

    public Object get(int idx)
    {
        return sortedList.get(idx);
    }

    public Object remove(int idx)
    {
        return sortedList.remove(idx);
    }

    public void clear()
    {
        sortedList.clear();
    }

    public int size()
    {
        return sortedList.size();
    }

    public Iterator gt(Object x)
    {
        return new SortedListSearchResult(this, x, 4);
    }

    public Iterator ge(Object x)
    {
        return new SortedListSearchResult(this, x, 3);
    }

    public Iterator eq(Object x)
    {
        return new SortedListSearchResult(this, x, 2);
    }

    public Iterator le(Object x)
    {
        return new SortedListSearchResult(this, x, 1);
    }

    public Iterator lt(Object x)
    {
        return new SortedListSearchResult(this, x, 0);
    }

    public int findFirst(Object x)
    {
        if(sortedList.size() == 0)
            return 0;
        int left = 0;
        int right;
        for(right = sortedList.size() - 1; left < right;)
        {
            int mid = (left + right) / 2;
            int rlt = comparator.compare(sortedList.get(mid), x);
            if(rlt < 0)
            {
                if(mid > left)
                    left = mid;
                else
                    left = right;
            } else
            if(rlt > 0)
                right = mid;
            else
                right = mid;
        }

        if(comparator.compare(sortedList.get(right), x) >= 0)
            return left;
        else
            return left + 1;
    }

    public List subList(int from, int to)
    {
        return sortedList.subList(from, to);
    }

    public int findLast(Object x)
    {
        if(sortedList.size() == 0)
            return 0;
        int left = 0;
        int right;
        for(right = sortedList.size() - 1; left < right;)
        {
            int mid = (left + right) / 2;
            int rlt = comparator.compare(sortedList.get(mid), x);
            if(rlt < 0)
            {
                if(mid > left)
                    left = mid;
                else
                    left = right;
            } else
            if(rlt > 0)
                right = mid;
            else
            if(left == mid)
                left = right;
            else
                left = mid;
        }

        if(comparator.compare(sortedList.get(right), x) > 0)
            return right;
        else
            return right + 1;
    }

    public List asList()
    {
        return Collections.unmodifiableList(sortedList);
    }

    private ArrayList sortedList;
    private Comparator comparator;
}
