// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.ioc;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import org.xml.sax.Locator;

// Referenced classes of package com.fitechlabs.xtier.kernel.ioc:
//            IocDescriptorException, IocArg, IocCall

public class IocDescriptor
    implements Serializable
{
    private class CallContext
    {

        void add(Class class1, Object obj)
        {
            types.add(class1);
            values.add(obj);
        }

        Class[] getTypes()
        {
            return (Class[])types.toArray(new Class[types.size()]);
        }

        Object[] getValues()
        {
            return values.toArray();
        }

        private List types;
        private List values;

        CallContext(int i)
        {
            super();
            types = new ArrayList(i);
            values = new ArrayList(i);
        }
    }


    public IocDescriptor()
    {
        policy = -1;
        ctorArgs = new ArrayList();
        calls = new ArrayList();
        javaImpl = null;
        factoryUid = null;
        factoryMethod = null;
        iocRefUid = null;
        uid = null;
        objCache = null;
        iocRefCache = null;
        keyedCache = new HashMap();
        seenThisIoc = false;
        refMap = null;
        xmlLoc = null;
    }

    public void setUid(String s)
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            uid = s;
            return;
        }
    }

    public void setIocRefUid(String s)
    {
        if(!$assertionsDisabled && s == null)
        {
            throw new AssertionError();
        } else
        {
            iocRefUid = s;
            return;
        }
    }

    public String getIocRefUid()
    {
        return iocRefUid;
    }

    public void setPolicy(int i)
    {
        if(!$assertionsDisabled && i != 3 && i != 2 && i != 1)
        {
            throw new AssertionError();
        } else
        {
            policy = i;
            return;
        }
    }

    public void setLocator(Locator locator)
    {
        if(!$assertionsDisabled && xmlLoc != null)
        {
            throw new AssertionError();
        } else
        {
            xmlLoc = new XmlLocation(locator);
            return;
        }
    }

    public void addCtorArg(IocArg iocarg)
    {
        if(!$assertionsDisabled && iocarg == null)
            throw new AssertionError();
        if(!$assertionsDisabled && iocRefUid != null)
        {
            throw new AssertionError();
        } else
        {
            ctorArgs.add(iocarg);
            return;
        }
    }

    public void addCall(IocCall ioccall)
    {
        if(!$assertionsDisabled && ioccall == null)
            throw new AssertionError();
        if(!$assertionsDisabled && iocRefUid != null)
        {
            throw new AssertionError();
        } else
        {
            calls.add(ioccall);
            return;
        }
    }

    public void setJavaImpl(String s)
    {
        javaImpl = s;
    }

    public void setFactoryUid(String s)
    {
        factoryUid = s;
    }

    public void setFactoryMethod(String s)
    {
        factoryMethod = s;
    }

    public String getUid()
    {
        return uid;
    }

    public int getPolicy()
        throws IocDescriptorException
    {
        if(iocRefUid == null)
            return policy;
        IocDescriptor iocdescriptor = findIocRef(iocRefUid);
        if(iocdescriptor == null)
            throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR17", iocRefUid)));
        else
            return iocdescriptor.policy;
    }

    public void setRefMap(Map map)
    {
        if(!$assertionsDisabled && map == null)
        {
            throw new AssertionError();
        } else
        {
            refMap = map;
            return;
        }
    }

    public String getJavaImpl()
    {
        return javaImpl;
    }

    public String getFactoryUid()
    {
        return factoryUid;
    }

    public String getFactoryMethod()
    {
        return factoryMethod;
    }

    private IocDescriptor findIocRef(String s)
        throws IocDescriptorException
    {
        HashSet hashset = new HashSet();
        if(uid != null)
            hashset.add(uid);
        IocDescriptor iocdescriptor = scanIocRefs(s, hashset);
        if(iocdescriptor != null && !$assertionsDisabled && iocdescriptor.iocRefUid != null)
            throw new AssertionError();
        else
            return iocdescriptor;
    }

    private IocDescriptor scanIocRefs(String s, Set set)
        throws IocDescriptorException
    {
        if(set.contains(s))
            throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR18", s)));
        IocDescriptor iocdescriptor = (IocDescriptor)refMap.get(s);
        if(iocdescriptor != null && iocdescriptor.iocRefUid != null)
        {
            set.add(s);
            return scanIocRefs(iocdescriptor.iocRefUid, set);
        } else
        {
            return iocdescriptor;
        }
    }

    private CallContext getCallCtx(List list, Object obj)
        throws IocDescriptorException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException
    {
        if(list != null)
        {
            int i = list.size();
            CallContext callcontext = new CallContext(i);
            for(int j = 0; j < i; j++)
            {
                IocArg iocarg = (IocArg)list.get(j);
                String s = iocarg.getRefUid();
                if(s != null)
                {
                    if(refMap == null)
                        throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR7", s)));
                    IocDescriptor iocdescriptor = findIocRef(s);
                    if(iocdescriptor == null)
                        throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR7", s)));
                    if(iocdescriptor.javaImpl == null)
                    {
                        IocDescriptor iocdescriptor1 = findIocRef(iocdescriptor.factoryUid);
                        if(iocdescriptor1 == null)
                            throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR24", iocdescriptor.factoryUid)));
                        Object obj1 = iocdescriptor1.newObj(obj);
                        Object obj2 = obj1.getClass().getMethod(iocdescriptor.factoryMethod, null).invoke(obj1, null);
                        callcontext.add(obj2.getClass(), obj2);
                    } else
                    {
                        callcontext.add(Class.forName(iocdescriptor.javaImpl), iocdescriptor.newObj(obj));
                    }
                } else
                {
                    callcontext.add(iocarg.getType(), iocarg.getValue());
                }
            }

            return callcontext;
        } else
        {
            return new CallContext(0);
        }
    }

    private boolean matchTypes(Class aclass[], Class aclass1[])
    {
        if(aclass.length != aclass1.length)
            return false;
        for(int i = 0; i < aclass.length; i++)
        {
            if(aclass[i] == null)
            {
                if(aclass1[i].isPrimitive())
                    return false;
                continue;
            }
            if(!aclass1[i].isAssignableFrom(aclass[i]))
                return false;
        }

        return true;
    }

    private Object newObj(Object obj)
        throws IocDescriptorException
    {
        if(iocRefUid != null)
        {
            if(!$assertionsDisabled && policy != -1)
                throw new AssertionError("Policy cannot be specified for IoC reference.");
            if(iocRefCache == null)
            {
                iocRefCache = findIocRef(iocRefUid);
                if(iocRefCache == null)
                    throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR17", iocRefUid)));
            }
            Object obj1 = iocRefCache.newObj(obj);
            return obj1;
        }
        if(!$assertionsDisabled && iocRefUid != null)
            throw new AssertionError();
        if(!$assertionsDisabled && (obj != null || policy != 2 && policy != 1) && (obj == null || policy != 3))
            throw new AssertionError();
        Object obj2 = null;
        if(policy == 3)
            obj2 = keyedCache.get(obj);
        else
        if(policy == 1)
            obj2 = objCache;
        if(obj2 != null)
            return obj2;
        if(seenThisIoc)
            throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR6", uid)));
        seenThisIoc = true;
        Object obj4 = null;
        if(!$assertionsDisabled && obj2 != null)
            throw new AssertionError();
        Object obj3;
        try
        {
            Class class1;
            if(javaImpl != null)
            {
                class1 = Class.forName(javaImpl);
                CallContext callcontext = getCallCtx(ctorArgs, obj);
                Constructor aconstructor[] = class1.getDeclaredConstructors();
                Constructor constructor = null;
                for(int k = 0; k < aconstructor.length; k++)
                {
                    Constructor constructor1 = aconstructor[k];
                    if(!Modifier.isPublic(constructor1.getModifiers()))
                        makeAccessable(constructor1);
                    if(!matchTypes(callcontext.getTypes(), constructor1.getParameterTypes()))
                        continue;
                    if(constructor != null)
                        throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR8", uid)));
                    constructor = constructor1;
                }

                if(constructor == null)
                    throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR9", uid, class1)));
                obj3 = constructor.newInstance(callcontext.getValues());
            } else
            {
                IocDescriptor iocdescriptor = findIocRef(factoryUid);
                if(iocdescriptor == null)
                    throw new IocDescriptorException(makeErrMsg(L10n.format("KRNL.IOC.ERR24", factoryUid)));
                Object obj5 = iocdescriptor.newObj(obj);
                obj3 = obj5.getClass().getMethod(factoryMethod, null).invoke(obj5, null);
                class1 = obj3.getClass();
            }
            if(!$assertionsDisabled && obj3 == null)
                throw new AssertionError();
            if(!$assertionsDisabled && class1 == null)
                throw new AssertionError();
            int i = calls.size();
            for(int j = 0; j < i; j++)
            {
                IocCall ioccall = (IocCall)calls.get(j);
                CallContext callcontext1 = getCallCtx(ioccall.getArgs(), obj);
                Method method = findDeclMethod(class1, ioccall.getMethod(), callcontext1.getTypes());
                if(!Modifier.isPublic(method.getModifiers()))
                    makeAccessable(method);
                method.invoke(obj3, callcontext1.getValues());
            }

        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw iocdescriptorexception;
        }
        catch(Exception exception)
        {
            throw new IocDescriptorException(makeErrMsg(uid == null ? L10n.format("KRNL.IOC.ERR15") : L10n.format("KRNL.IOC.ERR10", uid)), exception);
        }
        seenThisIoc = false;
        break MISSING_BLOCK_LABEL_687;
        Exception exception1;
        exception1;
        seenThisIoc = false;
        throw exception1;
        if(!$assertionsDisabled && obj3 == null)
            throw new AssertionError();
        if(policy == 1)
            objCache = obj3;
        else
        if(policy == 3)
        {
            if(!$assertionsDisabled && obj == null)
                throw new AssertionError();
            keyedCache.put(obj, obj3);
        }
        return obj3;
    }

    private Method findDeclMethod(Class class1, String s, Class aclass[])
        throws NoSuchMethodException
    {
        for(; class1 != null; class1 = class1.getSuperclass())
        {
            Method amethod[] = class1.getDeclaredMethods();
            for(int i = 0; i < amethod.length; i++)
            {
                Method method = amethod[i];
                if(method.getName().equals(s) && matchTypes(aclass, method.getParameterTypes()))
                    return method;
            }

        }

        throw new NoSuchMethodException(L10n.format("KRNL.IOC.ERR22", class1, s, Utils.arr2Str(aclass)));
    }

    public Object createNewObj(Object obj)
        throws IocDescriptorException
    {
        ArgAssert.nullArg(obj, "key");
        if(policy != 3)
            throw new IocDescriptorException(L10n.format("KRNL.IOC.ERR19", getPolicyStr()));
        else
            return newObj(obj);
    }

    public Object createNewObj(Object obj, Class class1)
        throws IocDescriptorException
    {
        ArgAssert.nullArg(obj, "key");
        ArgAssert.nullArg(class1, "targetClass");
        if(policy != 3)
            throw new IocDescriptorException(L10n.format("KRNL.IOC.ERR19", getPolicyStr()));
        Object obj1 = newObj(obj);
        if(class1.isInstance(obj1))
            return obj1;
        else
            throw new IocDescriptorException(L10n.format("KRNL.IOC.ERR31", uid, class1.toString(), obj1.getClass()));
    }

    public Object createNewObj(Class class1)
        throws IocDescriptorException
    {
        ArgAssert.nullArg(class1, "targetClass");
        if(policy == 3)
            throw new IocDescriptorException(L10n.format("KRNL.IOC.ERR19", getPolicyStr()));
        Object obj = newObj(null);
        if(class1.isInstance(obj))
            return obj;
        else
            throw new IocDescriptorException(L10n.format("KRNL.IOC.ERR31", uid, class1.toString(), obj.getClass()));
    }

    public Object createNewObj()
        throws IocDescriptorException
    {
        if(policy == 3)
            throw new IocDescriptorException(L10n.format("KRNL.IOC.ERR19", getPolicyStr()));
        else
            return newObj(null);
    }

    private void makeAccessable(AccessibleObject accessibleobject)
    {
        try
        {
            accessibleobject.setAccessible(true);
        }
        catch(SecurityException securityexception) { }
    }

    private String makeErrMsg(String s)
    {
        String s1 = xmlLoc.toStrLocation();
        return s1 != null ? s1 + ": " + s : s;
    }

    private String getPolicyStr()
    {
        switch(policy)
        {
        case 3: // '\003'
            return "POLICY_KEYED";

        case 2: // '\002'
            return "POLICY_NEW";

        case 1: // '\001'
            return "POLICY_SINGLETON";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid policy: " + policy);
        else
            return null;
    }

    public String toString()
    {
        if(iocRefUid == null)
            return L10n.format("KRNL.IOC.TXT1", uid, getPolicyStr(), javaImpl);
        else
            return L10n.format("KRNL.IOC.TXT5", uid, iocRefUid);
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

    public static final int POLICY_SINGLETON = 1;
    public static final int POLICY_NEW = 2;
    public static final int POLICY_KEYED = 3;
    private int policy;
    private List ctorArgs;
    private List calls;
    private String javaImpl;
    private String factoryUid;
    private String factoryMethod;
    private String iocRefUid;
    private String uid;
    private Object objCache;
    private IocDescriptor iocRefCache;
    private Map keyedCache;
    private boolean seenThisIoc;
    private Map refMap;
    private XmlLocation xmlLoc;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(IocDescriptor.class).desiredAssertionStatus();
    }
}
