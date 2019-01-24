// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import com.fitechlabs.xtier.utils.EmptyIterator;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheTxObject, CacheTxGroup, CacheTxEntry, CacheTxBucket,
//            CacheGroup

public class CacheTxSet
{

    CacheTxSet(boolean flag)
    {
        txObjs = new HashMap();
        txGrps = new HashMap();
        objsOrder = new ArrayList();
        isRemoteTx = flag;
    }

    public boolean isRemoteTx()
    {
        return isRemoteTx;
    }

    public boolean isEmpty()
    {
        return txObjs.isEmpty();
    }

    public int getTxObjsCount()
    {
        return txObjs.size();
    }

    public Map getTxObjs()
    {
        return txObjs;
    }

    public Map getTxGroups()
    {
        return txGrps;
    }

    public CacheTxObject add(CacheTxObject cachetxobject)
    {
        CacheTxGroup cachetxgroup = findTxGroup(cachetxobject.getGroup());
        cachetxgroup.add(cachetxobject);
        objsOrder.add(cachetxobject);
        return (CacheTxObject)txObjs.put(cachetxobject.getTxKey(), cachetxobject);
    }

    public CacheTxGroup enlist(CacheGroup cachegroup, Object obj)
    {
        CacheTxGroup cachetxgroup = findTxGroup(cachegroup);
        cachetxgroup.enlist();
        cachetxgroup.setUserArgs(obj);
        return cachetxgroup;
    }

    private CacheTxGroup findTxGroup(CacheGroup cachegroup)
    {
        Long long1 = cachegroup.getGroupId();
        CacheTxGroup cachetxgroup = (CacheTxGroup)txGrps.get(long1);
        if(cachetxgroup == null)
            txGrps.put(long1, cachetxgroup = new CacheTxGroup(cachegroup, isRemoteTx));
        return cachetxgroup;
    }

    public CacheTxObject remove(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        CacheTxObject cachetxobject = (CacheTxObject)txObjs.remove(obj);
        if(cachetxobject != null)
        {
            CacheTxGroup cachetxgroup = (CacheTxGroup)txGrps.get(cachetxobject.getGroup().getGroupId());
            if(!$assertionsDisabled && cachetxgroup == null)
                throw new AssertionError();
            cachetxgroup.remove(cachetxobject);
            if(!cachetxgroup.hasDepended() && !cachetxgroup.hasNotDepended() && !cachetxgroup.isEnlisted())
                txGrps.remove(cachetxgroup.getGroup().getGroupId());
            objsOrder.remove(cachetxobject);
        }
        return cachetxobject;
    }

    public CacheTxEntry getTxEntry(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachekeyattrs == null)
            throw new AssertionError();
        if(!$assertionsDisabled && isRemoteTx)
            throw new AssertionError();
        else
            return (CacheTxEntry)txObjs.get(new CacheTxEntry.EntryKey(obj, cachekeyattrs));
    }

    public CacheTxBucket getTxBucket(int i, CacheKeyAttrs cachekeyattrs)
    {
        if(!$assertionsDisabled && cachekeyattrs == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !isRemoteTx)
            throw new AssertionError();
        else
            return (CacheTxBucket)txObjs.get(new CacheTxBucket.BucketKey(i, cachekeyattrs));
    }

    public CacheTxObject getTxObj(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return (CacheTxObject)txObjs.get(obj);
    }

    public CacheTxGroup getTxGroup(Long long1)
    {
        if(!$assertionsDisabled && long1 == null)
            throw new AssertionError();
        else
            return (CacheTxGroup)txGrps.get(long1);
    }

    public boolean hasTxObject(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        else
            return txObjs.containsKey(obj);
    }

    public boolean hasTxGroup(Long long1)
    {
        if(!$assertionsDisabled && long1 == null)
            throw new AssertionError();
        else
            return txGrps.containsKey(long1);
    }

    public boolean hasEnlistedGroup(Long long1)
    {
        CacheTxGroup cachetxgroup = getTxGroup(long1);
        return cachetxgroup != null && cachetxgroup.isEnlisted();
    }

    public boolean intersects(CacheTxSet cachetxset)
    {
        if(!$assertionsDisabled && cachetxset == null)
            throw new AssertionError();
        Iterator iterator1 = cachetxset.txGrps.values().iterator();
label0:
        do
            if(iterator1.hasNext())
            {
                CacheTxGroup cachetxgroup = (CacheTxGroup)iterator1.next();
                CacheTxGroup cachetxgroup1 = (CacheTxGroup)txGrps.get(cachetxgroup.getGroup().getGroupId());
                if(cachetxgroup1 == null)
                    continue;
                if(cachetxgroup1.isEnlisted() || cachetxgroup.isEnlisted() || cachetxgroup1.hasDepended() || cachetxgroup.hasDepended())
                    return true;
                if(!$assertionsDisabled && (!cachetxgroup1.hasNotDepended() || !cachetxgroup.hasNotDepended()))
                    throw new AssertionError();
                if(cachetxset.isRemoteTx() == isRemoteTx())
                {
                    if(Utils.intersects(cachetxgroup.getNotDepended().keySet(), cachetxgroup1.getNotDepended().keySet()))
                        return true;
                    continue;
                }
                CacheTxGroup cachetxgroup2 = !cachetxgroup.isRemoteTx() ? cachetxgroup1 : cachetxgroup;
                CacheTxGroup cachetxgroup3 = cachetxgroup2 != cachetxgroup ? cachetxgroup : cachetxgroup1;
                Map map = cachetxgroup2.getNotDepended();
                CacheTxBucket.BucketKey bucketkey = new CacheTxBucket.BucketKey();
                Iterator iterator2 = cachetxgroup3.getNotDepended().values().iterator();
                do
                {
                    if(!iterator2.hasNext())
                        continue label0;
                    CacheTxEntry cachetxentry = (CacheTxEntry)iterator2.next();
                    bucketkey.set(cachetxentry.getKey().hashCode(), cachetxentry.getKeyAttrs());
                } while(!map.containsKey(bucketkey));
                break;
            } else
            {
                return false;
            }
        while(true);
        return true;
    }

    public Iterator iterator()
    {
        if(txObjs.isEmpty())
            return EMPTY_ITERATOR;
        else
            return txObjs.values().iterator();
    }

    public ListIterator listIterator()
    {
        return objsOrder.listIterator();
    }

    public List getTxObjList()
    {
        return objsOrder;
    }

    private int getTxObjOrderNr(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        Object obj1 = txObjs.get(obj);
        if(obj1 != null)
            return objsOrder.indexOf(obj1);
        else
            return -1;
    }

    private CacheTxObject getLastTxObj()
    {
        if(objsOrder.size() == 0)
            return null;
        else
            return (CacheTxObject)objsOrder.get(objsOrder.size() - 1);
    }

    public boolean hasReverseOrder(CacheTxSet cachetxset)
    {
        if(!$assertionsDisabled && cachetxset == null)
            throw new AssertionError();
        if(!$assertionsDisabled && objsOrder.size() <= 0)
            throw new AssertionError();
        if(!$assertionsDisabled && cachetxset.isRemoteTx())
            throw new AssertionError();
        if(cachetxset.isEmpty())
            return false;
        CacheTxObject cachetxobject = (CacheTxObject)objsOrder.get(objsOrder.size() - 1);
        int i = cachetxset.getTxObjOrderNr(cachetxobject.getTxKey());
        if(i == -1 || i == cachetxset.getTxObjsCount() - 1)
            return false;
        CacheKeyAttrs cachekeyattrs = cachetxset.getLastTxObj().getKeyAttrs();
        for(ListIterator listiterator = objsOrder.listIterator(); listiterator.hasNext();)
        {
            CacheKeyAttrs cachekeyattrs1 = ((CacheTxObject)listiterator.next()).getKeyAttrs();
            if(equalKeyAttrs(cachekeyattrs, cachekeyattrs1) && listiterator.hasNext())
                return true;
        }

        return false;
    }

    public boolean hasReverseOrder(List list)
    {
        if(!$assertionsDisabled && list == null)
            throw new AssertionError();
        if(!$assertionsDisabled && objsOrder.size() <= 0)
            throw new AssertionError();
        if(list.isEmpty())
            return false;
        CacheKeyAttrs cachekeyattrs = (CacheKeyAttrs)list.get(list.size() - 1);
        boolean flag = false;
        Object obj = objsOrder.listIterator();
        do
        {
            if(!((ListIterator) (obj)).hasNext())
                break;
            CacheKeyAttrs cachekeyattrs1 = ((CacheTxObject)((ListIterator) (obj)).next()).getKeyAttrs();
            if(!equalKeyAttrs(cachekeyattrs, cachekeyattrs1))
                continue;
            if(((ListIterator) (obj)).hasNext())
                flag = true;
            break;
        } while(true);
        if(!flag)
            return false;
        obj = ((CacheTxObject)objsOrder.get(objsOrder.size() - 1)).getKeyAttrs();
        ListIterator listiterator = list.listIterator();
        do
        {
            if(!listiterator.hasNext())
                break;
            CacheKeyAttrs cachekeyattrs2 = (CacheKeyAttrs)listiterator.next();
            if(!equalKeyAttrs(((CacheKeyAttrs) (obj)), cachekeyattrs2))
                continue;
            if(listiterator.hasNext())
                return true;
            break;
        } while(true);
        return false;
    }

    private boolean equalKeyAttrs(CacheKeyAttrs cachekeyattrs, CacheKeyAttrs cachekeyattrs1)
    {
        return cachekeyattrs.getTypeId() == cachekeyattrs1.getTypeId() && cachekeyattrs.getGroupId() == cachekeyattrs1.getGroupId() && cachekeyattrs.isDepended() == cachekeyattrs1.isDepended();
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

    private static final Iterator EMPTY_ITERATOR = new EmptyIterator();
    private final boolean isRemoteTx;
    private Map txObjs;
    private Map txGrps;
    private List objsOrder;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheTxSet.class).desiredAssertionStatus();
    }
}
