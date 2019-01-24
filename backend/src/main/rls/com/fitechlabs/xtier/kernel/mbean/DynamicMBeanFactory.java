// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.kernel.mbean;

import com.fitechlabs.xtier.l10n.L10n;
import java.lang.reflect.Method;
import java.util.*;
import javax.management.*;

// Referenced classes of package com.fitechlabs.xtier.kernel.mbean:
//            DynamicXtierMBean, DynamicMBeanMetadata

public class DynamicMBeanFactory
{

    public DynamicMBeanFactory()
    {
    }

    public static DynamicXtierMBean createDynMBean(Object obj, Class class1, String s, String s1)
        throws JMException
    {
        if(!$assertionsDisabled && (obj == null || class1 == null || s == null || s1 == null))
            throw new AssertionError();
        else
            return new DynamicXtierMBean(makeMBeanMetadata(obj, class1, new String[] {
                s, s1
            }));
    }

    public static DynamicXtierMBean createDynMBean(Object obj, Class class1)
        throws JMException
    {
        if(!$assertionsDisabled && (obj == null || class1 == null))
            throw new AssertionError();
        else
            return new DynamicXtierMBean(makeMBeanMetadata(obj, class1, null));
    }

    private static MBeanAttributeInfo createAttrInfo(String s, String s1, String s2)
    {
        return new MBeanAttributeInfo(s, s1, s2, true, false, false);
    }

    private static boolean isExcluded(Method method)
    {
        for(int i = 0; i < EXCLUDES.length; i++)
            if(method.equals(EXCLUDES[i]))
                return true;

        return false;
    }

    private static DynamicMBeanMetadata makeMBeanMetadata(Object obj, Class class1, String as[])
        throws JMException
    {
        if(!$assertionsDisabled && (obj == null || class1 == null || as != null && as.length % 2 != 0))
            throw new AssertionError();
        Hashtable hashtable = new Hashtable();
        boolean flag = (com.fitechlabs.xtier.kernel.KernelService.class).isAssignableFrom(class1);
        hashtable.put("name", class1.getName());
        hashtable.put("service", !flag ? "false" : "true");
        if(as != null)
        {
            for(int i = 0; i < as.length;)
            {
                String s = as[i++];
                String s2 = as[i++];
                hashtable.put(s, s2);
            }

        }
        Method amethod[] = class1.getMethods();
        String s1 = class1.getName();
        if(flag)
        {
            MBeanOperationInfo ambeanoperationinfo[] = new MBeanOperationInfo[amethod.length - EXCLUDES.length];
            int j = 0;
            for(int k = 0; k < amethod.length; k++)
            {
                Method method = amethod[k];
                if(!isExcluded(method))
                    ambeanoperationinfo[j++] = new MBeanOperationInfo(L10n.format("KRNL.MBEAN.TXT1", s1), method);
            }

            MBeanAttributeInfo ambeanattributeinfo[] = {
                createAttrInfo("StartupTime", Long.TYPE.getName(), L10n.format("KRNL.MBEAN.TXT3")), createAttrInfo("UpTime", Long.TYPE.getName(), L10n.format("KRNL.MBEAN.TXT16")), createAttrInfo("State", Integer.TYPE.getName(), L10n.format("KRNL.MBEAN.TXT4")), createAttrInfo("Version", (int[].class).getName(), L10n.format("KRNL.MBEAN.TXT5")), createAttrInfo("Name", "java.lang.String", L10n.format("KRNL.MBEAN.TXT6")), createAttrInfo("RegionName", "java.lang.String", L10n.format("KRNL.MBEAN.TXT7")), createAttrInfo("ConfigPath", "java.lang.String", L10n.format("KRNL.MBEAN.TXT8")), createAttrInfo("FeatureSet", "java.lang.String", L10n.format("KRNL.MBEAN.TXT9")), createAttrInfo("UpTimeStr", "java.lang.String", L10n.format("KRNL.MBEAN.TXT10")), createAttrInfo("VersionStr", "java.lang.String", L10n.format("KRNL.MBEAN.TXT11")),
                createAttrInfo("StateStr", "java.lang.String", L10n.format("KRNL.MBEAN.TXT12")), createAttrInfo("StartupTimeStr", "java.lang.String", L10n.format("KRNL.MBEAN.TXT13"))
            };
            return new DynamicMBeanMetadata(new MBeanInfo(s1, L10n.format("KRNL.MBEAN.TXT2", s1), ambeanattributeinfo, NO_CTOR_INFO, ambeanoperationinfo, NO_NOTIFY_INFO), obj, null, new ObjectName("xtier", hashtable));
        }
        HashSet hashset = new HashSet();
        HashMap hashmap = new HashMap();
        HashMap hashmap1 = new HashMap();
        HashMap hashmap2 = new HashMap();
        for(int l = 0; l < amethod.length; l++)
        {
            Method method1 = amethod[l];
            int j1 = method1.getParameterTypes().length;
            String s3 = method1.getName();
            if(s3.startsWith("is") && j1 == 0)
            {
                String s4 = s3.substring(2);
                if(hashmap1.containsKey(s4) || hashset.contains(s4))
                    throw new JMException(L10n.format("KRNL.MBEAN.ERR1", s4));
                hashmap1.put(s4, method1);
                hashset.add(s4);
                continue;
            }
            if(s3.startsWith("get") && j1 == 0)
            {
                String s5 = s3.substring(3);
                if(hashmap1.containsKey(s5) || hashset.contains(s5))
                    throw new JMException(L10n.format("KRNL.MBEAN.ERR1", s5));
                hashmap1.put(s5, method1);
                continue;
            }
            if(s3.startsWith("set") && j1 == 1)
            {
                String s6 = s3.substring(3);
                hashmap.put(s6, method1);
            } else
            {
                hashmap2.put(s3, method1);
            }
        }

        MBeanOperationInfo ambeanoperationinfo1[] = new MBeanOperationInfo[hashmap2.size()];
        int i1 = 0;
        for(Iterator iterator = hashmap2.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            ambeanoperationinfo1[i1++] = new MBeanOperationInfo((String)entry.getKey(), (Method)entry.getValue());
        }

        HashMap hashmap3 = new HashMap();
        String s7;
        for(Iterator iterator1 = hashmap1.keySet().iterator(); iterator1.hasNext(); hashmap3.put(s7, new MBeanAttributeInfo(s7, L10n.format("KRNL.MBEAN.TXT14", s7), (Method)hashmap1.get(s7), (Method)hashmap.get(s7))))
            s7 = (String)iterator1.next();

        Iterator iterator2 = hashmap.keySet().iterator();
        do
        {
            if(!iterator2.hasNext())
                break;
            String s8 = (String)iterator2.next();
            if(!hashmap3.containsKey(s8))
                hashmap3.put(s8, new MBeanAttributeInfo(s8, L10n.format("KRNL.MBEAN.TXT14", s8), null, (Method)hashmap.get(s8)));
        } while(true);
        MBeanAttributeInfo ambeanattributeinfo1[] = new MBeanAttributeInfo[hashmap3.size()];
        i1 = 0;
        for(Iterator iterator3 = hashmap3.values().iterator(); iterator3.hasNext();)
            ambeanattributeinfo1[i1++] = (MBeanAttributeInfo)iterator3.next();

        return new DynamicMBeanMetadata(new MBeanInfo(s1, L10n.format("KRNL.MBEAN.TXT15", s1), ambeanattributeinfo1, NO_CTOR_INFO, ambeanoperationinfo1, NO_NOTIFY_INFO), obj, hashset, new ObjectName("xtier", hashtable));
    }

    private static final MBeanConstructorInfo NO_CTOR_INFO[] = new MBeanConstructorInfo[0];
    private static final MBeanNotificationInfo NO_NOTIFY_INFO[] = new MBeanNotificationInfo[0];
    private static final Method EXCLUDES[];
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DynamicMBeanFactory.class).desiredAssertionStatus();
        EXCLUDES = (com.fitechlabs.xtier.kernel.KernelService.class).getMethods();
    }
}
