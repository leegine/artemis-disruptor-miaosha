// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.grid.routers;

import com.fitechlabs.xtier.services.cluster.ClusterNode;
import com.fitechlabs.xtier.services.grid.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import java.util.*;

public class GridTaskWeightRouter
    implements GridTaskRouter
{
    private static class Rank
    {

        int getCpuRank()
        {
            return cpuRank;
        }

        int getIoRank()
        {
            return ioRank;
        }

        int getMemRank()
        {
            return memRank;
        }

        Object getObj()
        {
            return obj;
        }

        void setCpuRank(int i)
        {
            cpuRank = i;
        }

        void setIoRank(int i)
        {
            ioRank = i;
        }

        void setMemRank(int i)
        {
            memRank = i;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Rank [cpu-rank=" + cpuRank + ", mem-rank=" + memRank + ", io-rank=" + ioRank + ", obj=" + obj + ']';
        }

        private int cpuRank;
        private int memRank;
        private int ioRank;
        private Object obj;
        static final boolean $assertionsDisabled; /* synthetic field */


        Rank(Object obj1)
        {
            if(!$assertionsDisabled && obj1 == null)
            {
                throw new AssertionError();
            } else
            {
                obj = obj1;
                return;
            }
        }
    }


    public GridTaskWeightRouter()
    {
    }

    private void rankRefsCpuWeights(List list)
    {
        Collections.sort(list, new Comparator() {

            public int compare(Object obj, Object obj1)
            {
                double d1 = ((GridTaskSplitRef)((Rank)obj).getObj()).getCpuWeight();
                double d2 = ((GridTaskSplitRef)((Rank)obj1).getObj()).getCpuWeight();
                return d1 != d2 ? d1 != -1D ? d2 != -1D ? ((byte)(d1 >= d2 ? 1 : -1)) : 1 : -1 : 0;
            }


            {
                super();
            }
        }
);
        int i = 0;
        for(int j = list.size() - 1; j >= 0; j--)
        {
            Rank rank = (Rank)list.get(j);
            double d = ((GridTaskSplitRef)rank.getObj()).getCpuWeight();
            rank.setCpuRank(i);
            if(d != -1D)
                i++;
        }

    }

    private void rankRefsMemWeights(List list)
    {
        Collections.sort(list, new Comparator() {

            public int compare(Object obj, Object obj1)
            {
                double d1 = ((GridTaskSplitRef)((Rank)obj).getObj()).getMemoryWeight();
                double d2 = ((GridTaskSplitRef)((Rank)obj1).getObj()).getMemoryWeight();
                return d1 != d2 ? d1 != -1D ? d2 != -1D ? ((byte)(d1 >= d2 ? 1 : -1)) : 1 : -1 : 0;
            }


            {
                super();
            }
        }
);
        int i = 0;
        for(int j = list.size() - 1; j >= 0; j--)
        {
            Rank rank = (Rank)list.get(j);
            double d = ((GridTaskSplitRef)rank.getObj()).getMemoryWeight();
            rank.setMemRank(i);
            if(d != -1D)
                i++;
        }

    }

    private void rankRefsIoWeights(List list)
    {
        Collections.sort(list, new Comparator() {

            public int compare(Object obj, Object obj1)
            {
                double d1 = ((GridTaskSplitRef)((Rank)obj).getObj()).getIoWeight();
                double d2 = ((GridTaskSplitRef)((Rank)obj1).getObj()).getIoWeight();
                return d1 != d2 ? d1 != -1D ? d2 != -1D ? ((byte)(d1 >= d2 ? 1 : -1)) : 1 : -1 : 0;
            }


            {
                super();
            }
        }
);
        int i = 0;
        for(int j = list.size() - 1; j >= 0; j--)
        {
            Rank rank = (Rank)list.get(j);
            double d = ((GridTaskSplitRef)rank.getObj()).getIoWeight();
            rank.setIoRank(i);
            if(d != -1D)
                i++;
        }

    }

    private void rankNodesIoWeights(List list, final GridTaxonomy tax)
    {
        Collections.sort(list, new Comparator() {

            public int compare(Object obj, Object obj1)
            {
                double d1 = tax.getIoWeight((ClusterNode)((Rank)obj).getObj());
                double d2 = tax.getIoWeight((ClusterNode)((Rank)obj1).getObj());
                return d1 != d2 ? d1 != -1D ? d2 != -1D ? ((byte)(d1 >= d2 ? 1 : -1)) : 1 : -1 : 0;
            }


            {
                super();
            }
        }
);
        int i = 0;
        for(int j = list.size() - 1; j >= 0; j--)
        {
            Rank rank = (Rank)list.get(j);
            double d = tax.getIoWeight((ClusterNode)rank.getObj());
            rank.setIoRank(i);
            if(d != -1D)
                i++;
        }

    }

    private void rankNodesCpuWeights(List list, final GridTaxonomy tax)
    {
        Collections.sort(list, new Comparator() {

            public int compare(Object obj, Object obj1)
            {
                double d1 = tax.getCpuWeight((ClusterNode)((Rank)obj).getObj());
                double d2 = tax.getCpuWeight((ClusterNode)((Rank)obj1).getObj());
                return d1 != d2 ? d1 != -1D ? d2 != -1D ? ((byte)(d1 >= d2 ? 1 : -1)) : 1 : -1 : 0;
            }


            {
                super();
            }
        }
);
        int i = 0;
        for(int j = list.size() - 1; j >= 0; j--)
        {
            Rank rank = (Rank)list.get(j);
            double d = tax.getCpuWeight((ClusterNode)rank.getObj());
            rank.setCpuRank(i);
            if(d != -1D)
                i++;
        }

    }

    private void rankNodesMemWeights(List list, final GridTaxonomy tax)
    {
        Collections.sort(list, new Comparator() {

            public int compare(Object obj, Object obj1)
            {
                double d1 = tax.getMemoryWeight((ClusterNode)((Rank)obj).getObj());
                double d2 = tax.getMemoryWeight((ClusterNode)((Rank)obj1).getObj());
                return d1 != d2 ? d1 != -1D ? d2 != -1D ? ((byte)(d1 >= d2 ? 1 : -1)) : 1 : -1 : 0;
            }


            {
                super();
            }
        }
);
        int i = 0;
        for(int j = list.size() - 1; j >= 0; j--)
        {
            Rank rank = (Rank)list.get(j);
            double d = tax.getMemoryWeight((ClusterNode)rank.getObj());
            rank.setMemRank(i);
            if(d != -1D)
                i++;
        }

    }

    public ClusterNode route(GridTaskSplitRef gridtasksplitref, Set set, Set set1, GridTaxonomy gridtaxonomy)
    {
        if(!$assertionsDisabled && (gridtasksplitref == null || set == null || set1 == null || set1.isEmpty()))
            throw new AssertionError();
        ArgAssert.nullArg(gridtaxonomy, "tax");
        ArrayList arraylist = new ArrayList(set.size() + 1);
        for(Iterator iterator = set.iterator(); iterator.hasNext(); arraylist.add(new Rank(iterator.next())));
        Rank rank = new Rank(gridtasksplitref);
        arraylist.add(rank);
        rankRefsCpuWeights(arraylist);
        rankRefsMemWeights(arraylist);
        rankRefsIoWeights(arraylist);
        ArrayList arraylist1 = new ArrayList(set1.size());
        for(Iterator iterator1 = set1.iterator(); iterator1.hasNext(); arraylist1.add(new Rank(iterator1.next())));
        rankNodesCpuWeights(arraylist1, gridtaxonomy);
        rankNodesMemWeights(arraylist1, gridtaxonomy);
        rankNodesIoWeights(arraylist1, gridtaxonomy);
        ClusterNode clusternode = null;
        int i = 0x7fffffff;
        int j = arraylist1.size();
        for(int k = 0; k < j; k++)
        {
            Rank rank1 = (Rank)arraylist1.get(k);
            int l = Math.abs(rank.getCpuRank() - rank1.getCpuRank()) + Math.abs(rank.getIoRank() - rank1.getIoRank()) + Math.abs(rank.getMemRank() - rank1.getMemRank());
            if(l < i)
            {
                i = l;
                clusternode = (ClusterNode)rank1.getObj();
            }
        }

        if(!$assertionsDisabled && clusternode == null)
            throw new AssertionError();
        else
            return clusternode;
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
        $assertionsDisabled = !(GridTaskWeightRouter.class).desiredAssertionStatus();
    }
}
