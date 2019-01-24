// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SortedListSearchResult.java

package co.lib.collection.impl;

import co.lib.collection.SortedList;

import java.util.Comparator;
import java.util.Iterator;

public class SortedListSearchResult
    implements Iterator
{

    public SortedListSearchResult()
    {
        found_first = 0;
        found_last = 0;
        current_idx = 0;
    }

    public SortedListSearchResult(SortedList lst, Object x, int compareCond)
    {
        found_first = 0;
        found_last = 0;
        current_idx = 0;
        compare_condition = compareCond;
        sorted_list = lst;
        comparator = sorted_list.getComparator();
        found_first = lst.findFirst(x);
        found_last = lst.findLast(x);
        if(comparator.compare(lst.get(found_first), x) == 0)
            found_exact = true;
        else
            found_exact = false;
        if(found_exact)
            switch(compareCond)
            {
            case 0: // '\0'
            case 1: // '\001'
                current_idx = 0;
                break;

            case 2: // '\002'
            case 3: // '\003'
                current_idx = found_first;
                break;

            case 4: // '\004'
                current_idx = found_last;
                break;

            default:
                throw new RuntimeException((new StringBuilder()).append("Error: unknown comaparison type:").append(compareCond).toString());
            }
        else
            switch(compareCond)
            {
            case 0: // '\0'
            case 1: // '\001'
                if(found_first == 0)
                    current_idx = lst.size();
                else
                    current_idx = 0;
                break;

            case 2: // '\002'
                current_idx = lst.size();
                // fall through

            case 3: // '\003'
            case 4: // '\004'
                current_idx = found_last;
                break;

            default:
                throw new RuntimeException((new StringBuilder()).append("Error: unknown comaparison type:").append(compareCond).toString());
            }
    }

    public void remove()
    {
        if(hasNext())
            sorted_list.remove(current_idx);
    }

    public boolean hasNext()
    {
        if(current_idx >= sorted_list.size())
            return false;
        switch(compare_condition)
        {
        case 0: // '\0'
            return current_idx < found_first;

        case 1: // '\001'
            return current_idx < found_last;

        case 2: // '\002'
            return current_idx >= found_first && current_idx < found_last;

        case 3: // '\003'
            return current_idx >= found_first;

        case 4: // '\004'
            return current_idx >= found_last;
        }
        throw new RuntimeException((new StringBuilder()).append("Unkown Comparison Condition:").append(compare_condition).toString());
    }

    public Object next()
    {
        if(hasNext())
            return sorted_list.get(current_idx++);
        else
            return null;
    }

    private boolean found_exact;
    private int found_first;
    private int found_last;
    private int current_idx;
    private SortedList sorted_list;
    private Comparator comparator;
    private int compare_condition;
}
