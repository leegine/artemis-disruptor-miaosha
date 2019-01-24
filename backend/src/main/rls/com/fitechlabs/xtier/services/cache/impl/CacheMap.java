// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cache.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.cache.CacheEntry;
import com.fitechlabs.xtier.services.cache.CacheKeyAttrs;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.boxed.sync.BoxedInt32Sync;
import java.lang.reflect.Array;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cache.impl:
//            CacheBucket, CacheUtils, CacheEntryImpl

public class CacheMap
{
    private class EntryIterator
        implements Iterator
    {

        public boolean hasNext()
        {
            findNext();
            return nextEntry != null;
        }

        public Object next()
        {
            if(nextEntry == null)
                throw new NoSuchElementException();
            else
                return nextEntry;
        }

        public void remove()
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "Iterator.remove()"));
        }

        protected void findNext()
        {
label0:
            {
                nextEntry = null;
                if(bucket == null)
                    break MISSING_BLOCK_LABEL_94;
                synchronized(bucket.getMutex())
                {
                    List list = bucket.getEntries();
                    entryIndex--;
                    if(list == null || entryIndex < 0 || entryIndex >= list.size())
                        break label0;
                    nextEntry = (CacheEntry)list.get(entryIndex);
                }
                return;
            }
            obj;
            JVM INSTR monitorexit ;
            break MISSING_BLOCK_LABEL_94;
            exception;
            throw exception;
            bucket = null;
_L2:
label1:
            {
                do
                {
                    if(!bucketIter.hasNext())
                        break MISSING_BLOCK_LABEL_206;
                    bucket = (CacheBucket)bucketIter.next();
                } while(bucket == null);
                synchronized(bucket.getMutex())
                {
                    List list1 = bucket.getEntries();
                    if(list1.isEmpty())
                        break label1;
                    entryIndex = list1.size() - 1;
                    nextEntry = (CacheEntry)list1.get(entryIndex);
                }
                return;
            }
            obj1;
            JVM INSTR monitorexit ;
            if(true) goto _L2; else goto _L1
_L1:
            exception1;
            throw exception1;
        }

        private Iterator bucketIter;
        private CacheBucket bucket;
        private int entryIndex;
        private CacheEntry nextEntry;

        private EntryIterator()
        {
            super();
            bucketIter = buckets();
            bucket = null;
            entryIndex = 0;
            nextEntry = null;
        }

    }

    private class EntrySet extends AbstractCollection
        implements Set
    {

        public boolean contains(Object obj)
        {
            if(!(obj instanceof CacheEntry))
            {
                return false;
            } else
            {
                CacheEntryImpl cacheentryimpl = peek(((CacheEntry)obj).getKey(), ((CacheEntry)obj).getKeyAttrs());
                return cacheentryimpl != null && cacheentryimpl.equals(obj);
            }
        }

        public Iterator iterator()
        {
            return new EntryIterator();
        }

        public Object[] toArray()
        {
            return toArray(((Object []) (new CacheEntry[size()])));
        }

        private EntrySet(BoxedInt32Sync boxedint32sync)
        {
            super(boxedint32sync);
        }

    }

    private class ValueCollection extends AbstractCollection
    {

        public boolean contains(Object obj)
        {
            return containsValue(obj);
        }

        public Iterator iterator()
        {
            return new Iterator() {

                public void remove()
                {
                    entries.remove();
                }

                public boolean hasNext()
                {
                    return entries.hasNext();
                }

                public Object next()
                {
                    return ((CacheEntry)entries.next()).getValue();
                }

                private EntryIterator entries;


                {
                    super();
                    entries = new EntryIterator();
                }
            }
;
        }


        private ValueCollection(BoxedInt32Sync boxedint32sync)
        {
            super(boxedint32sync);
        }

    }

    private class KeySet extends AbstractCollection
        implements Set
    {

        public boolean contains(Object obj)
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "contains(Object)"));
        }

        public Iterator iterator()
        {
            return new Iterator() {

                public void remove()
                {
                    entries.remove();
                }

                public boolean hasNext()
                {
                    return entries.hasNext();
                }

                public Object next()
                {
                    return ((CacheEntry)entries.next()).getKey();
                }

                private EntryIterator entries;


            {
                super();
                entries = new EntryIterator();
            }
            }
;
        }


        private KeySet(BoxedInt32Sync boxedint32sync)
        {
            super(boxedint32sync);
        }

    }

    private abstract class AbstractCollection
        implements Collection
    {

        public boolean add(Object obj)
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "add(Object)"));
        }

        public boolean addAll(Collection collection)
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "addAll(Collection)"));
        }

        public void clear()
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "clear()"));
        }

        public boolean remove(Object obj)
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "remove(Object)"));
        }

        public boolean removeAll(Collection collection)
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "removeAll(Collection)"));
        }

        public boolean retainAll(Collection collection)
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "retainAll(Collection)"));
        }

        public boolean containsAll(Collection collection)
        {
            ArgAssert.nullArg(collection, "c");
            for(Iterator iterator = collection.iterator(); iterator.hasNext();)
                if(!contains(iterator.next()))
                    return false;

            return true;
        }

        public boolean isEmpty()
        {
            return size.get() == 0;
        }

        public int size()
        {
            return size.get();
        }

        public Object[] toArray()
        {
            return toArray(new Object[size()]);
        }

        public Object[] toArray(Object aobj[])
        {
            CacheMap cachemap = CacheMap.this;
            JVM INSTR monitorenter ;
            int i = size();
            if(aobj.length < i)
                aobj = (Object[])Array.newInstance(((Object) (aobj)).getClass().getComponentType(), i);
            Iterator iterator = iterator();
            for(int j = 0; j < i && iterator.hasNext(); j++)
                aobj[j] = iterator.next();

            if(aobj.length > i)
                aobj[i] = null;
            return aobj;
            Exception exception;
            exception;
            throw exception;
        }

        private BoxedInt32Sync size;

        protected AbstractCollection(BoxedInt32Sync boxedint32sync)
        {
            super();
            size = boxedint32sync;
        }
    }

    private class BucketIterator
        implements Iterator
    {

        public boolean hasNext()
        {
            return !isStopped && nextBucket != null;
        }

        public Object next()
        {
            if(nextBucket == null)
            {
                throw new NoSuchElementException();
            } else
            {
                CacheBucket cachebucket = nextBucket;
                findNext();
                return cachebucket;
            }
        }

        public void remove()
        {
            throw new UnsupportedOperationException(L10n.format("SRVC.CACHE.ERR30", "Iterator.remove()"));
        }

        private List findNext(int i)
        {
            for(; i < table.length; i++)
                if(table[i] != null && !table[i].isEmpty())
                    return table[i];

            return null;
        }

        protected void findNext()
        {
            List list;
            int i;
label0:
            {
                synchronized(CacheMap.this)
                {
                    list = null;
                    if(isStopped)
                        break MISSING_BLOCK_LABEL_157;
                    if(nextBucket == null)
                    {
                        list = findNext(0);
                        break MISSING_BLOCK_LABEL_157;
                    }
                    i = indexFor(nextBucket.hashCode());
                    list = table[i];
                    if(list == null || list.isEmpty())
                    {
                        list = findNext(++i);
                        break MISSING_BLOCK_LABEL_157;
                    }
                    int j = list.size();
                    CacheBucket cachebucket;
                    do
                    {
                        if(j-- <= 0)
                            break label0;
                        cachebucket = (CacheBucket)list.get(j);
                    } while(cachebucket != nextBucket || j <= 0);
                    nextBucket = (CacheBucket)list.get(j - 1);
                }
                return;
            }
            list = findNext(++i);
            if(list == null)
                break MISSING_BLOCK_LABEL_208;
            if(!$assertionsDisabled && list.size() <= 0)
                throw new AssertionError();
            nextBucket = (CacheBucket)list.get(list.size() - 1);
            cachemap;
            JVM INSTR monitorexit ;
            return;
            isStopped = true;
            cachemap;
            JVM INSTR monitorexit ;
              goto _L1
            exception;
            throw exception;
_L1:
        }

        private CacheBucket nextBucket;
        private boolean isStopped;
        static final boolean $assertionsDisabled; /* synthetic field */


        BucketIterator()
        {
            super();
            isStopped = false;
            findNext();
        }
    }


    CacheMap()
    {
        this(16);
    }

    CacheMap(int i)
    {
        this(i, 0.75F);
    }

    public CacheMap(int i, float f)
    {
        if(!$assertionsDisabled && i < 0)
            throw new AssertionError("Invalid initial capacity: " + i);
        if(!$assertionsDisabled && (f <= 0.0F || Float.isNaN(f)))
            throw new AssertionError("Invalid load factor: " + f);
        if(i > 0x40000000)
            i = 0x40000000;
        int j;
        for(j = 1; j < i; j <<= 1);
        loadFactor = f;
        threshold = (int)((float)j * f);
        table = new List[j];
    }

    public synchronized int size()
    {
        return mapSize;
    }

    public synchronized void clear()
    {
        for(int i = 0; i < table.length; i++)
            table[i] = null;

        mapSize = 0;
    }

    public synchronized boolean isEmpty()
    {
        return mapSize == 0;
    }

    public synchronized boolean containsKey(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachekeyattrs == null)
        {
            throw new AssertionError();
        } else
        {
            CacheBucket cachebucket = get(obj.hashCode(), cachekeyattrs);
            return cachebucket != null && cachebucket.containsKey(obj);
        }
    }

    public synchronized boolean containsValue(Object obj)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
label0:
        for(int i = 0; i < table.length; i++)
        {
            if(table[i] == null)
                continue;
            List list = table[i];
            int j = list.size();
            do
                if(j-- <= 0)
                    continue label0;
            while(!((CacheBucket)list.get(j)).containsValue(obj));
            return true;
        }

        return false;
    }

    public synchronized void put(CacheBucket cachebucket)
    {
        if(!$assertionsDisabled && cachebucket == null)
            throw new AssertionError();
        if(mapSize >= threshold)
            resize(2 * table.length);
        int i = indexFor(cachebucket.getHashCode());
        Object obj = table[i];
        if(obj == null)
            table[i] = ((List) (obj = new ArrayList(1)));
        ((List) (obj)).add(cachebucket);
        mapSize++;
    }

    public synchronized CacheBucket get(int i, CacheKeyAttrs cachekeyattrs)
    {
label0:
        {
            if(!$assertionsDisabled && cachekeyattrs == null)
                throw new AssertionError();
            List list = table[indexFor(i)];
            if(list == null)
                break label0;
            CacheBucket cachebucket;
label1:
            do
            {
                for(int j = list.size(); j-- > 0;)
                {
                    cachebucket = (CacheBucket)list.get(j);
                    if(!cachebucket.isRemoved())
                        continue label1;
                    if(!$assertionsDisabled && !cachebucket.isEmpty())
                        throw new AssertionError();
                    list.remove(j);
                    mapSize--;
                }

                break label0;
            } while(cachebucket.getHashCode() != i || !CacheUtils.equals(cachebucket.getKeyAttrs(), cachekeyattrs));
            return cachebucket;
        }
        return null;
    }

    public synchronized CacheEntryImpl peek(Object obj, CacheKeyAttrs cachekeyattrs)
    {
        if(!$assertionsDisabled && obj == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachekeyattrs == null)
        {
            throw new AssertionError();
        } else
        {
            CacheBucket cachebucket = get(obj.hashCode(), cachekeyattrs);
            return cachebucket != null ? cachebucket.get(obj) : null;
        }
    }

    public synchronized void remove(CacheBucket cachebucket)
    {
label0:
        {
            List list = table[indexFor(cachebucket.getHashCode())];
            if(list == null)
                break label0;
            int i = list.size();
            CacheBucket cachebucket1;
            do
            {
                if(i-- <= 0)
                    break label0;
                cachebucket1 = (CacheBucket)list.get(i);
                if(!$assertionsDisabled && cachebucket1 != cachebucket && cachebucket1.equals(cachebucket))
                    throw new AssertionError();
                if(cachebucket1.isRemoved())
                {
                    if(!$assertionsDisabled && !cachebucket1.isEmpty())
                        throw new AssertionError();
                    list.remove(i);
                    mapSize--;
                }
            } while(cachebucket1 != cachebucket);
            if(!$assertionsDisabled && !cachebucket1.isRemoved())
                throw new AssertionError();
        }
    }

    public Iterator buckets()
    {
        return new BucketIterator();
    }

    public synchronized Set keySet(BoxedInt32Sync boxedint32sync)
    {
        if(keySet == null)
            keySet = new KeySet(boxedint32sync);
        return keySet;
    }

    public synchronized Collection values(BoxedInt32Sync boxedint32sync)
    {
        if(values == null)
            values = new ValueCollection(boxedint32sync);
        return values;
    }

    public synchronized Set entrySet(BoxedInt32Sync boxedint32sync)
    {
        if(entrySet == null)
            entrySet = new EntrySet(boxedint32sync);
        return entrySet;
    }

    private void resize(int i)
    {
        if(!$assertionsDisabled && !Thread.holdsLock(this))
            throw new AssertionError();
        if(table.length == 0x40000000)
        {
            threshold = 0x7fffffff;
            return;
        }
        List alist[] = new List[i];
        for(int j = 0; j < table.length; j++)
        {
            if(table[j] == null)
                continue;
            List list = table[j];
            for(int k = list.size(); k-- > 0;)
                put(alist, (CacheBucket)list.get(k));

        }

        table = alist;
        threshold = (int)((float)i * loadFactor);
    }

    private void put(List alist[], CacheBucket cachebucket)
    {
        if(!$assertionsDisabled && alist == null)
            throw new AssertionError();
        if(!$assertionsDisabled && cachebucket == null)
            throw new AssertionError();
        int i = indexFor(cachebucket.getHashCode(), alist.length);
        Object obj = alist[i];
        if(obj == null)
            alist[i] = ((List) (obj = new ArrayList(1)));
        if(!$assertionsDisabled && ((List) (obj)).contains(cachebucket))
        {
            throw new AssertionError();
        } else
        {
            ((List) (obj)).add(cachebucket);
            return;
        }
    }

    private int indexFor(int i)
    {
        return indexFor(i, table.length);
    }

    private int indexFor(int i, int j)
    {
        return hash(i) & j - 1;
    }

    private int hash(int i)
    {
        i += ~(i << 9);
        i ^= i >>> 14;
        i += i << 4;
        i ^= i >>> 10;
        return i;
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

    static final int MAXIMUM_CAPACITY = 0x40000000;
    protected List table[];
    protected int mapSize;
    protected int threshold;
    protected final float loadFactor;
    private EntrySet entrySet;
    private KeySet keySet;
    private ValueCollection values;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(CacheMap.class).desiredAssertionStatus();
    }

}
