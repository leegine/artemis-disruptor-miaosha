// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.utils;

import com.fitechlabs.xtier.l10n.L10n;
import java.util.Arrays;
import java.util.Iterator;

public final class FifoQueue
{

    public FifoQueue(int i)
    {
        growthFactor = 2.0F;
        if(!$assertionsDisabled && i <= 0)
        {
            throw new AssertionError();
        } else
        {
            capacity = i;
            lastIndex = i - 1;
            head = tail = 0;
            cnt = 0;
            data = new Object[i];
            return;
        }
    }

    public FifoQueue(FifoQueue fifoqueue)
    {
        growthFactor = 2.0F;
        copyFrom(fifoqueue);
    }

    public void copyFrom(FifoQueue fifoqueue)
    {
        if(capacity < fifoqueue.capacity)
        {
            head = fifoqueue.head;
            tail = fifoqueue.tail;
            cnt = fifoqueue.cnt;
            capacity = fifoqueue.capacity;
            data = new Object[capacity];
            lastIndex = capacity - 1;
            System.arraycopy(((Object) (fifoqueue.data)), 0, ((Object) (data)), 0, capacity);
        } else
        if(capacity == fifoqueue.capacity)
        {
            head = fifoqueue.head;
            tail = fifoqueue.tail;
            cnt = fifoqueue.cnt;
            System.arraycopy(((Object) (fifoqueue.data)), 0, ((Object) (data)), 0, capacity);
        } else
        {
            tail = fifoqueue.tail;
            cnt = fifoqueue.cnt;
            if(fifoqueue.head <= fifoqueue.tail)
            {
                head = fifoqueue.head;
                System.arraycopy(((Object) (fifoqueue.data)), 0, ((Object) (data)), 0, fifoqueue.capacity);
            } else
            {
                System.arraycopy(((Object) (fifoqueue.data)), 0, ((Object) (data)), 0, tail);
                int i = fifoqueue.capacity - fifoqueue.head;
                head = capacity - i;
                System.arraycopy(((Object) (fifoqueue.data)), fifoqueue.head, ((Object) (data)), head, i);
            }
        }
    }

    public FifoQueue()
    {
        this(16);
    }

    public void setGrowthFactory(float f)
    {
        if((double)f <= 1.0D)
        {
            throw new IllegalArgumentException(L10n.format("ILLEGAL.ARG.ERR", "growthFactor"));
        } else
        {
            growthFactor = f;
            return;
        }
    }

    public float getGrowthFactor()
    {
        return growthFactor;
    }

    public void ensureCapacity(int i)
    {
        if(i > capacity)
        {
            Object aobj[] = new Object[i];
            int j = capacity - head;
            System.arraycopy(((Object) (data)), head, ((Object) (aobj)), 0, j);
            System.arraycopy(((Object) (data)), 0, ((Object) (aobj)), j, tail);
            head = 0;
            tail = capacity;
            capacity = i;
            data = aobj;
            lastIndex = capacity - 1;
        }
    }

    public void add(Object obj)
    {
        if(cnt == capacity)
            ensureCapacity((int)((float)capacity * growthFactor));
        data[tail] = obj;
        tail = tail != lastIndex ? tail + 1 : 0;
        cnt++;
    }

    public Object head()
    {
        if(!$assertionsDisabled && cnt <= 0)
            throw new AssertionError();
        else
            return data[head];
    }

    public Object tail()
    {
        if(!$assertionsDisabled && cnt <= 0)
            throw new AssertionError();
        else
            return data[tail != 0 ? tail - 1 : lastIndex];
    }

    public Object get()
    {
        if(cnt == 0)
            throw new IndexOutOfBoundsException(L10n.format("KRNL.UTILS.FIFO.ERR4"));
        if(!$assertionsDisabled && cnt <= 0)
        {
            throw new AssertionError();
        } else
        {
            Object obj = data[head];
            data[head] = null;
            cnt--;
            head = head != lastIndex ? head + 1 : 0;
            return obj;
        }
    }

    public boolean remove(Object obj)
    {
        int i = head + cnt;
        for(int j = head; j < i; j++)
            if(data[j % data.length].equals(obj))
            {
                if(head < tail)
                {
                    if(j < tail)
                        System.arraycopy(((Object) (data)), j + 1, ((Object) (data)), j, tail - j);
                    if(!$assertionsDisabled && tail == 0)
                        throw new AssertionError();
                    tail--;
                } else
                if(j <= tail)
                {
                    if(j < tail)
                        System.arraycopy(((Object) (data)), j + 1, ((Object) (data)), j, tail - j);
                    tail = tail != 0 ? tail - 1 : lastIndex;
                } else
                {
                    if(j > head)
                        System.arraycopy(((Object) (data)), head, ((Object) (data)), head + 1, j - head);
                    head = head != lastIndex ? head + 1 : 0;
                }
                cnt--;
                return true;
            }

        return false;
    }

    public Object peek()
    {
        return peek(0);
    }

    public Object peek(int i)
    {
        if(i > data.length)
            throw new IndexOutOfBoundsException(L10n.format("KRNL.UTILS.FIFO.ERR1", new Integer(i)));
        else
            return data[(head + i) % data.length];
    }

    public boolean contains(Object obj)
    {
        int i = head + cnt;
        for(int j = head; j < i; j++)
            if(data[j % data.length].equals(obj))
                return true;

        return false;
    }

    public Iterator iterator()
    {
        return new Iterator() {

            public boolean hasNext()
            {
                return i != length;
            }

            public Object next()
            {
                return data[i++ % length];
            }

            public void remove()
            {
                throw new UnsupportedOperationException(L10n.format("KRNL.UTILS.FIFO.ERR2"));
            }

            private int i;
            private final int length;


            {
//                super();
                i = head;
                length = head + cnt;
            }
        }
;
    }

    public boolean isEmpty()
    {
        return cnt == 0;
    }

    public boolean isFilled()
    {
        return cnt == capacity;
    }

    public int getCount()
    {
        return cnt;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void clear()
    {
        Arrays.fill(data, null);
        head = tail = 0;
        cnt = 0;
    }

    public String toString()
    {
        return L10n.format("KRNL.UTILS.FIFO.ERR3", new Integer(cnt), new Integer(head), new Integer(tail), new Integer(capacity), new Float(growthFactor));
    }



    private int capacity;
    private int lastIndex;
    private int head;
    private int tail;
    private int cnt;
    private float growthFactor;
    private Object data[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(FifoQueue.class).desiredAssertionStatus();
    }



}
