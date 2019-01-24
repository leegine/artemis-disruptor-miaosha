// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.kernel.xml;

import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.xml:
//            XmlSaxHandler

class XmlSaxCallStack
{

    XmlSaxCallStack()
    {
    }

    static XmlSaxHandler pop()
    {
        Map map = threadLocalStack;
        JVM INSTR monitorenter ;
        LinkedList linkedlist;
        linkedlist = (LinkedList)threadLocalStack.get(Thread.currentThread());
        if(linkedlist == null || linkedlist.isEmpty())
            return null;
        XmlSaxHandler xmlsaxhandler = (XmlSaxHandler)linkedlist.removeFirst();
        xmlsaxhandler;
        map;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    static void push(XmlSaxHandler xmlsaxhandler)
    {
        synchronized(threadLocalStack)
        {
            LinkedList linkedlist = (LinkedList)threadLocalStack.get(Thread.currentThread());
            if(linkedlist == null)
                threadLocalStack.put(Thread.currentThread(), linkedlist = new LinkedList());
            linkedlist.addFirst(xmlsaxhandler);
        }
    }

    static XmlSaxHandler peek()
    {
        Map map = threadLocalStack;
        JVM INSTR monitorenter ;
        LinkedList linkedlist = (LinkedList)threadLocalStack.get(Thread.currentThread());
        return linkedlist != null && !linkedlist.isEmpty() ? (XmlSaxHandler)linkedlist.getFirst() : null;
        Exception exception;
        exception;
        throw exception;
    }

    private static Map threadLocalStack = new HashMap();

}
