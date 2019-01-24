// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.mbean;

import com.fitechlabs.xtier.l10n.L10n;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import javax.management.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.mbean:
//            DynamicMBeanMetadata

public class DynamicXtierMBean
    implements DynamicMBean
{

    public DynamicXtierMBean()
    {
        metadata = null;
        methods = null;
    }

    DynamicXtierMBean(DynamicMBeanMetadata dynamicmbeanmetadata)
    {
        metadata = null;
        methods = null;
        if(!$assertionsDisabled && dynamicmbeanmetadata == null)
        {
            throw new AssertionError();
        } else
        {
            metadata = dynamicmbeanmetadata;
            methods = dynamicmbeanmetadata.getImpl().getClass().getMethods();
            return;
        }
    }

    private Method getMethodByName(String s)
        throws AttributeNotFoundException
    {
        for(int i = 0; i < methods.length; i++)
            if(methods[i].getName().equals(s))
                return methods[i];

        throw new AttributeNotFoundException(L10n.format("KRNL.MBEAN.ERR2", s));
    }

    public Object getAttribute(String s)
        throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        Set set = metadata.getBoolAttrs();
        String s1 = set == null || !set.contains(s) ? "get" : "is";
        try
        {
            return getMethodByName(s1 + s).invoke(metadata.getImpl(), null);
        }
        catch(Exception exception)
        {
            throw new MBeanException(exception, L10n.format("KRNL.MBEAN.ERR3", s));
        }
    }

    public void setAttribute(Attribute attribute)
        throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
    {
        try
        {
            getMethodByName("set" + attribute.getName()).invoke(metadata.getImpl(), new Object[] {
                attribute.getValue()
            });
        }
        catch(Exception exception)
        {
            throw new MBeanException(exception, L10n.format("KRNL.MBEAN.ERR4", attribute.getName()));
        }
    }

    public AttributeList getAttributes(String as[])
    {
        AttributeList attributelist = new AttributeList(as.length);
        for(int i = 0; i < as.length; i++)
        {
            String s = as[i];
            try
            {
                attributelist.add(new Attribute(s, getAttribute(s)));
            }
            catch(Exception exception) { }
        }

        return attributelist;
    }

    public AttributeList setAttributes(AttributeList attributelist)
    {
        int i = attributelist.size();
        AttributeList attributelist1 = new AttributeList(i);
        for(int j = 0; j < i; j++)
        {
            Attribute attribute = (Attribute)attributelist.get(j);
            try
            {
                setAttribute(attribute);
                String s = attribute.getName();
                attributelist1.add(new Attribute(s, getAttribute(s)));
            }
            catch(Exception exception) { }
        }

        return attributelist1;
    }

    private boolean matchTypes(Class class1, Class class2)
    {
        if(class1.isPrimitive())
        {
            if(class1 == Integer.TYPE)
                return class2 == (Integer.class) || class2 == Integer.TYPE;
            if(class1 == Byte.TYPE)
                return class2 == (Byte.class) || class2 == Byte.TYPE;
            if(class1 == Short.TYPE)
                return class2 == (Short.class) || class2 == Short.TYPE;
            if(class1 == Long.TYPE)
                return class2 == (Long.class) || class2 == Long.TYPE;
            if(class1 == Float.TYPE)
                return class2 == (Float.class) || class2 == Float.TYPE;
            if(class1 == Double.TYPE)
                return class2 == (Double.class) || class2 == Double.TYPE;
            if(class1 == Boolean.TYPE)
                return class2 == (Boolean.class) || class2 == Boolean.TYPE;
            if(class1 == Character.TYPE)
                return class2 == (Character.class) || class2 == Character.TYPE;
            if(!$assertionsDisabled)
                throw new AssertionError("Not a primitive type: " + class1);
            else
                return false;
        } else
        {
            return class1.isAssignableFrom(class2);
        }
    }

    public Object invoke(String s, Object aobj[], String as[])
        throws MBeanException, ReflectionException
    {
        Method method = null;
        for(int i = 0; i < methods.length; i++)
        {
            Method method1 = methods[i];
            if(!method1.getName().equals(s))
                continue;
            if(aobj == null && method1.getParameterTypes().length == 0)
            {
                method = method1;
                break;
            }
            if(aobj == null)
                continue;
            Class aclass[] = method1.getParameterTypes();
            if(aclass.length != aobj.length)
                continue;
            boolean flag = true;
            int j = 0;
            do
            {
                if(j >= aobj.length)
                    break;
                if(!matchTypes(aclass[j], aobj[j].getClass()))
                {
                    flag = false;
                    break;
                }
                j++;
            } while(true);
            if(!flag)
                continue;
            method = method1;
            break;
        }

        if(method == null)
            throw new MBeanException(new OperationsException(L10n.format("KRNL.MBEAN.ERR6", s)));
        try
        {
            return method.invoke(metadata.getImpl(), aobj);
        }
        catch(InvocationTargetException invocationtargetexception)
        {
            Throwable throwable = invocationtargetexception.getTargetException();
            throw new ReflectionException(((Exception) (!(throwable instanceof Exception) ? ((Exception) (invocationtargetexception)) : (Exception)throwable)), L10n.format("KRNL.MBEAN.ERR7", throwable));
        }
        catch(Exception exception)
        {
            throw new MBeanException(exception, L10n.format("KRNL.MBEAN.ERR5", s));
        }
    }

    public MBeanInfo getMBeanInfo()
    {
        return metadata.getInfo();
    }

    public ObjectName getName()
    {
        return metadata.getObjName();
    }

    private DynamicMBeanMetadata metadata;
    private Method methods[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DynamicXtierMBean.class).desiredAssertionStatus();
    }
}
