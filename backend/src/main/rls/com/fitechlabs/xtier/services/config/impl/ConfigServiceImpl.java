// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.config.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.config.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.*;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.config.impl:
//            ConfigGroup

public class ConfigServiceImpl extends ServiceProviderAdapter
    implements ConfigService
{
    private class XmlIocHierarchyQualif extends XmlIocQualif
    {

        void setParent(String s)
        {
            parentName = s;
        }

        String getParent()
        {
            return parentName;
        }

        void addImpl(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                implNames.add(s);
                return;
            }
        }

        List getImpls()
        {
            return implNames;
        }

        public void init()
            throws ConfigException
        {
            Object obj = null;
            if(parentName != null)
            {
                Class class1;
                try
                {
                    class1 = Class.forName(parentName);
                }
                catch(ClassNotFoundException classnotfoundexception)
                {
                    throw new ConfigException(L10n.format("SRVC.CNFG.ERR43", getName(), parentName), classnotfoundexception);
                }
                if(class1.isInterface())
                    throw new ConfigException(L10n.format("SRVC.CNFG.ERR45", getName(), parentName));
                parent = class1;
            }
            for(int i = 0; i < implNames.size(); i++)
            {
                String s = (String)implNames.get(i);
                Class class2;
                try
                {
                    class2 = Class.forName(s);
                }
                catch(ClassNotFoundException classnotfoundexception1)
                {
                    throw new ConfigException(L10n.format("SRVC.CNFG.ERR43", getName(), s));
                }
                if(!class2.isInterface())
                    throw new ConfigException(L10n.format("SRVC.CNFG.ERR46", getName(), s));
                impls.add(class2);
            }

        }

        public boolean qualifIocValue(Object obj)
            throws ConfigException
        {
            if(!$assertionsDisabled && obj == null)
                throw new AssertionError();
            if(parent != null && !parent.isInstance(obj))
                return false;
            for(int i = 0; i < impls.size(); i++)
            {
                Class class1 = (Class)impls.get(i);
                if(!class1.isInstance(obj))
                    return false;
            }

            return true;
        }

        private String parentName;
        private ArrayList implNames;
        private Class parent;
        private ArrayList impls;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlIocHierarchyQualif(String s, Locator locator)
        {
            super(s, locator);
            parentName = null;
            implNames = new ArrayList();
            parent = null;
            impls = new ArrayList();
        }
    }

    private class XmlIocCustomQualif extends XmlIocQualif
    {

        IocDescriptor getUnparsedQuilif()
        {
            return unparsedQlf;
        }

        void setUnparsedQualif(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                unparsedQlf = iocdescriptor;
                return;
            }
        }

        ConfigCustomQualifier getParsedQualif()
        {
            return parsedQlf;
        }

        void setParsedQualif(ConfigCustomQualifier configcustomqualifier)
        {
            if(!$assertionsDisabled && configcustomqualifier == null)
            {
                throw new AssertionError();
            } else
            {
                parsedQlf = configcustomqualifier;
                return;
            }
        }

        public void init()
            throws ConfigException
        {
            if(!$assertionsDisabled && unparsedQlf == null)
                throw new AssertionError();
            try
            {
                if(unparsedQlf.getPolicy() == 3)
                    throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR35", getName()), this));
                parsedQlf = (ConfigCustomQualifier)unparsedQlf.createNewObj(ConfigServiceImpl.class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier != null ? ConfigServiceImpl.class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier : (ConfigServiceImpl.class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier = ConfigServiceImpl._mthclass$("com.fitechlabs.xtier.services.config.ConfigCustomQualifier")));
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR36", getName()), this), iocdescriptorexception);
            }
        }

        public boolean qualifIocValue(Object obj)
            throws ConfigException
        {
            return parsedQlf.isValueValid(obj);
        }

        private IocDescriptor unparsedQlf;
        private ConfigCustomQualifier parsedQlf;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlIocCustomQualif(String s, Locator locator)
        {
            super(s, locator);
            unparsedQlf = null;
            parsedQlf = null;
        }
    }

    private abstract class XmlIocQualif extends XmlNamedElem
    {

        abstract boolean qualifIocValue(Object obj)
            throws ConfigException;

        void init()
            throws ConfigException
        {
        }

        protected XmlIocQualif(String s, Locator locator)
        {
            super(s, locator);
        }
    }

    private class XmlCustomQualif extends XmlQualif
    {

        IocDescriptor getUnparsedQuilif()
        {
            return unparsedQualif;
        }

        void setUnparsedQualif(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                unparsedQualif = iocdescriptor;
                return;
            }
        }

        ConfigCustomQualifier getParsedQualif()
        {
            return parsedQualif;
        }

        void setParsedQualif(ConfigCustomQualifier configcustomqualifier)
        {
            if(!$assertionsDisabled && configcustomqualifier == null)
            {
                throw new AssertionError();
            } else
            {
                parsedQualif = configcustomqualifier;
                return;
            }
        }

        public void instantiateQualif()
            throws ConfigException
        {
            if(!$assertionsDisabled && unparsedQualif == null)
                throw new AssertionError();
            try
            {
                if(unparsedQualif.getPolicy() == 3)
                    throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR35", getName()), this));
                parsedQualif = (ConfigCustomQualifier)unparsedQualif.createNewObj(ConfigServiceImpl.class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier != null ? ConfigServiceImpl.class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier : (ConfigServiceImpl.class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier = ConfigServiceImpl._mthclass$("com.fitechlabs.xtier.services.config.ConfigCustomQualifier")));
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR36", getName()), this), iocdescriptorexception);
            }
        }

        boolean qualifValue(XmlProp xmlprop)
            throws ConfigException
        {
label0:
            {
                if(!$assertionsDisabled && xmlprop == null)
                    throw new AssertionError();
                if(!$assertionsDisabled && xmlprop.getType() == 14)
                    throw new AssertionError();
                switch(xmlprop.getAggr())
                {
                case 2: // '\002'
                    Iterator iterator = ((List)xmlprop.getParsedValue()).iterator();
                    do
                        if(!iterator.hasNext())
                            break label0;
                    while(parsedQualif.isValueValid(iterator.next()));
                    return false;

                case 3: // '\003'
                    Iterator iterator1 = ((Map)xmlprop.getParsedValue()).values().iterator();
                    do
                        if(!iterator1.hasNext())
                            break label0;
                    while(parsedQualif.isValueValid(iterator1.next()));
                    return false;

                case 1: // '\001'
                    return parsedQualif.isValueValid(xmlprop.getParsedValue());

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property type in: " + xmlprop);
                    break;
                }
            }
            return true;
        }

        private IocDescriptor unparsedQualif;
        private ConfigCustomQualifier parsedQualif;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlCustomQualif(String s, Locator locator)
        {
            super(s, locator);
            unparsedQualif = null;
            parsedQualif = null;
        }
    }

    private class XmlRangeQualif extends XmlQualif
    {

        void setUnparsedMax(String s)
        {
            max = s;
        }

        String getUnparsedMax()
        {
            return max;
        }

        void setUnparsedMin(String s)
        {
            min = s;
        }

        String getUnparsedMin()
        {
            return min;
        }

        void setParsedMax(Object obj)
        {
            maxObj = obj;
        }

        void setParsedMin(Object obj)
        {
            minObj = obj;
        }

        private boolean isValid(XmlProp xmlprop, Object obj)
        {
            if(!$assertionsDisabled && obj == null)
                throw new AssertionError();
            switch(xmlprop.getType())
            {
            case 8: // '\b'
                char c = ((Character)obj).charValue();
                if(minObj != null)
                {
                    char c1 = ((Character)minObj).charValue();
                    if(c < c1)
                        return !valid;
                    if(c == c1 && !inclusive)
                        return !valid;
                }
                if(maxObj != null)
                {
                    char c2 = ((Character)maxObj).charValue();
                    if(c > c2)
                        return !valid;
                    if(c == c2 && !inclusive)
                        return !valid;
                }
                break;

            case 9: // '\t'
                float f = ((Float)obj).floatValue();
                if(minObj != null)
                {
                    float f1 = ((Float)minObj).floatValue();
                    if(f < f1)
                        return !valid;
                    if(f == f1 && !inclusive)
                        return !valid;
                }
                if(maxObj == null)
                    break;
                float f2 = ((Float)maxObj).floatValue();
                if(f > f2)
                    return !valid;
                if(f == f2 && !inclusive)
                    return !valid;
                break;

            case 10: // '\n'
                double d = ((Double)obj).doubleValue();
                if(minObj != null)
                {
                    double d1 = ((Double)minObj).doubleValue();
                    if(d < d1)
                        return !valid;
                    if(d == d1 && !inclusive)
                        return !valid;
                }
                if(maxObj == null)
                    break;
                double d2 = ((Double)maxObj).doubleValue();
                if(d > d2)
                    return !valid;
                if(d == d2 && !inclusive)
                    return !valid;
                break;

            case 4: // '\004'
                byte byte0 = ((Byte)obj).byteValue();
                if(minObj != null)
                {
                    byte byte1 = ((Byte)minObj).byteValue();
                    if(byte0 < byte1)
                        return !valid;
                    if(byte0 == byte1 && !inclusive)
                        return !valid;
                }
                if(maxObj == null)
                    break;
                byte byte2 = ((Byte)maxObj).byteValue();
                if(byte0 > byte2)
                    return !valid;
                if(byte0 == byte2 && !inclusive)
                    return !valid;
                break;

            case 5: // '\005'
                short word0 = ((Short)obj).shortValue();
                if(minObj != null)
                {
                    short word1 = ((Short)minObj).shortValue();
                    if(word0 < word1)
                        return !valid;
                    if(word0 == word1 && !inclusive)
                        return !valid;
                }
                if(maxObj == null)
                    break;
                short word2 = ((Short)maxObj).shortValue();
                if(word0 > word2)
                    return !valid;
                if(word0 == word2 && !inclusive)
                    return !valid;
                break;

            case 6: // '\006'
                int i = ((Integer)obj).intValue();
                if(minObj != null)
                {
                    int j = ((Integer)minObj).intValue();
                    if(i < j)
                        return !valid;
                    if(i == j && !inclusive)
                        return !valid;
                }
                if(maxObj == null)
                    break;
                int k = ((Integer)maxObj).intValue();
                if(i > k)
                    return !valid;
                if(i == k && !inclusive)
                    return !valid;
                break;

            case 7: // '\007'
                long l = ((Long)obj).longValue();
                if(minObj != null)
                {
                    long l1 = ((Long)minObj).longValue();
                    if(l < l1)
                        return !valid;
                    if(l == l1 && !inclusive)
                        return !valid;
                }
                if(maxObj == null)
                    break;
                long l2 = ((Long)maxObj).longValue();
                if(l > l2)
                    return !valid;
                if(l == l2 && !inclusive)
                    return !valid;
                break;

            case 13: // '\r'
                Date date = (Date)obj;
                if(minObj != null)
                {
                    Date date1 = (Date)minObj;
                    if(!date.after(date1))
                        return !valid;
                    if(!inclusive && date.equals(date1))
                        return !valid;
                }
                if(maxObj == null)
                    break;
                Date date2 = (Date)maxObj;
                if(date.after(date2))
                    return !valid;
                if(!inclusive && date.equals(date2))
                    return !valid;
                break;

            case 11: // '\013'
            case 12: // '\f'
            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid config property value type: " + xmlprop.getType());
                break;
            }
            return valid;
        }

        boolean qualifValue(XmlProp xmlprop)
            throws ConfigException
        {
label0:
            {
                if(!$assertionsDisabled && xmlprop == null)
                    throw new AssertionError();
                switch(xmlprop.getAggr())
                {
                case 2: // '\002'
                    Iterator iterator = ((List)xmlprop.getParsedValue()).iterator();
                    do
                        if(!iterator.hasNext())
                            break label0;
                    while(isValid(xmlprop, iterator.next()));
                    return false;

                case 3: // '\003'
                    Iterator iterator1 = ((Map)xmlprop.getParsedValue()).values().iterator();
                    do
                        if(!iterator1.hasNext())
                            break label0;
                    while(isValid(xmlprop, iterator1.next()));
                    return false;

                case 1: // '\001'
                    return isValid(xmlprop, xmlprop.getParsedValue());

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property type in: " + xmlprop);
                    break;
                }
            }
            return true;
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT7", getName(), min, max, new Boolean(inclusive));
        }

        private String min;
        private String max;
        private boolean inclusive;
        private boolean valid;
        private Object minObj;
        private Object maxObj;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRangeQualif(String s, boolean flag, boolean flag1, Locator locator)
        {
            super(s, locator);
            minObj = null;
            maxObj = null;
            inclusive = flag;
            valid = flag1;
        }
    }

    private class XmlRegexQualif extends XmlQualif
    {

        void setPattern(String s)
        {
            pattern = s;
        }

        void compilePattern()
            throws ConfigException
        {
            try
            {
                regex = Pattern.compile(pattern);
            }
            catch(PatternSyntaxException patternsyntaxexception)
            {
                throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR26", getName()), this), patternsyntaxexception);
            }
        }

        boolean qualifValue(XmlProp xmlprop)
        {
label0:
            {
                if(!$assertionsDisabled && xmlprop == null)
                    throw new AssertionError();
                switch(xmlprop.getAggr())
                {
                case 1: // '\001'
                    if(xmlprop.getType() != 14)
                        return regex.matcher(xmlprop.getStrUnparsedValue()).matches() == valid;
                    // fall through

                case 3: // '\003'
                    Iterator iterator = ((Map)xmlprop.getUnparsedValue()).values().iterator();
                    do
                        if(!iterator.hasNext())
                            break label0;
                    while(regex.matcher((String)iterator.next()).matches() == valid);
                    return false;

                case 2: // '\002'
                    Iterator iterator1 = ((List)xmlprop.getUnparsedValue()).iterator();
                    do
                        if(!iterator1.hasNext())
                            break label0;
                    while(regex.matcher((String)iterator1.next()).matches() == valid);
                    return false;

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property type in: " + xmlprop);
                    break;
                }
            }
            return true;
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT6", getName(), pattern);
        }

        private String pattern;
        private Pattern regex;
        private boolean valid;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegexQualif(String s, boolean flag, Locator locator)
        {
            super(s, locator);
            valid = flag;
        }
    }

    private class XmlSetQualif extends XmlQualif
    {

        void addUnparsedValue(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                unparsedValues.add(s);
                return;
            }
        }

        ArrayList getAllUnparsedValues()
        {
            return unparsedValues;
        }

        void addParsedValue(Object obj)
        {
            if(!$assertionsDisabled && obj == null)
            {
                throw new AssertionError();
            } else
            {
                parsedValues.add(obj);
                return;
            }
        }

        private boolean isValid(Object obj)
        {
            if(!$assertionsDisabled && obj == null)
                throw new AssertionError();
            else
                return parsedValues.contains(obj) == valid;
        }

        boolean qualifValue(XmlProp xmlprop)
            throws ConfigException
        {
label0:
            {
                if(!$assertionsDisabled && xmlprop == null)
                    throw new AssertionError();
                switch(xmlprop.getAggr())
                {
                case 2: // '\002'
                    Iterator iterator = ((List)xmlprop.getParsedValue()).iterator();
                    do
                        if(!iterator.hasNext())
                            break label0;
                    while(isValid(iterator.next()));
                    return false;

                case 3: // '\003'
                    Iterator iterator1 = ((Map)xmlprop.getParsedValue()).values().iterator();
                    do
                        if(!iterator1.hasNext())
                            break label0;
                    while(isValid(iterator1.next()));
                    return false;

                case 1: // '\001'
                    return isValid(xmlprop.getParsedValue());

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property type in: " + xmlprop);
                    break;
                }
            }
            return true;
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT5", getName(), new Boolean(valid), Utils.coll2Str(unparsedValues));
        }

        private boolean valid;
        private ArrayList unparsedValues;
        private ArrayList parsedValues;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlSetQualif(String s, Locator locator, boolean flag)
        {
            super(s, locator);
            unparsedValues = new ArrayList();
            parsedValues = new ArrayList();
            valid = flag;
        }
    }

    private abstract class XmlQualif extends XmlNamedElem
    {

        abstract boolean qualifValue(XmlProp xmlprop)
            throws ConfigException;

        XmlQualif(String s, Locator locator)
        {
            super(s, locator);
        }
    }

    private class XmlProp extends XmlNamedElem
    {

        int getType()
        {
            return type;
        }

        void setAggr(int i)
        {
            aggr = i;
        }

        int getAggr()
        {
            return aggr;
        }

        void setKeyType(int i)
        {
            keyType = i;
        }

        int getKeyType()
        {
            return keyType;
        }

        void setUnparsedValue(Object obj)
        {
            unparsedValue = obj;
        }

        Object getUnparsedValue()
        {
            return unparsedValue;
        }

        String getStrUnparsedValue()
        {
            if(!$assertionsDisabled && aggr != 1)
                throw new AssertionError();
            if(!$assertionsDisabled && type == 14)
                throw new AssertionError();
            else
                return (String)unparsedValue;
        }

        void setParsedValue(Object obj)
        {
            if(!$assertionsDisabled && parsedValue != null)
            {
                throw new AssertionError();
            } else
            {
                parsedValue = obj;
                return;
            }
        }

        Object getParsedValue()
        {
            return parsedValue;
        }

        void setArrValue(Object obj)
        {
            if(!$assertionsDisabled && arrValue != null)
            {
                throw new AssertionError();
            } else
            {
                arrValue = obj;
                return;
            }
        }

        Object getArrValue()
        {
            return arrValue;
        }

        void addQualif(XmlQualif xmlqualif)
        {
            qualifs.add(xmlqualif);
        }

        boolean containsQualifName(String s)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            for(Iterator iterator = qualifs.iterator(); iterator.hasNext();)
            {
                String s1 = ((XmlQualif)iterator.next()).getName();
                if(s1 != null && s1.equals(s))
                    return true;
            }

            return false;
        }

        Iterator getAllQualifs()
        {
            return qualifs.iterator();
        }

        XmlGroup getGroup()
        {
            return parentGroup;
        }

        private String getTypeStr()
        {
            return getStrValueType(type);
        }

        private String getAggrStr()
        {
            switch(aggr)
            {
            case 2: // '\002'
                return "AGGR_ARRAY";

            case 3: // '\003'
                return "AGGR_MAP";

            case 1: // '\001'
                return "AGGR_SINGLE";
            }
            if(!$assertionsDisabled)
                throw new AssertionError("Invalid config property aggregation type: " + aggr);
            if(!$assertionsDisabled)
                throw new AssertionError("Unknown aggregation type: " + aggr);
            else
                return null;
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT4", getName(), getTypeStr(), getAggrStr());
        }

        static final int AGGR_SINGLE = 1;
        static final int AGGR_ARRAY = 2;
        static final int AGGR_MAP = 3;
        static final int TYPE_INT8 = 4;
        static final int TYPE_INT16 = 5;
        static final int TYPE_INT32 = 6;
        static final int TYPE_INT64 = 7;
        static final int TYPE_CHAR16 = 8;
        static final int TYPE_FLOAT32 = 9;
        static final int TYPE_FLOAT64 = 10;
        static final int TYPE_BOOLEAN = 11;
        static final int TYPE_STRING = 12;
        static final int TYPE_DATE = 13;
        static final int TYPE_IOC_OBJ = 14;
        private int type;
        private int aggr;
        private int keyType;
        private Object unparsedValue;
        private Object parsedValue;
        private Object arrValue;
        private Set qualifs;
        private XmlGroup parentGroup;
        static final boolean $assertionsDisabled; /* synthetic field */



        XmlProp(String s, Locator locator, int i, XmlGroup xmlgroup)
        {
            super(s, locator);
            aggr = -1;
            keyType = -1;
            unparsedValue = null;
            qualifs = new HashSet();
            parentGroup = null;
            type = i;
            parentGroup = xmlgroup;
        }
    }

    private class XmlGroup extends XmlNamedElem
    {

        boolean containsProp(String s)
        {
            return props.containsKey(s);
        }

        void addProp(XmlProp xmlprop)
        {
            props.put(xmlprop.getName(), xmlprop);
        }

        XmlProp getProp(String s)
        {
            return (XmlProp)props.get(s);
        }

        Iterator getAllProps()
        {
            return props.values().iterator();
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT3", getName());
        }

        private Map props;

        XmlGroup(String s, Locator locator)
        {
            super(s, locator);
            props = new HashMap();
        }
    }

    private class XmlRegion extends XmlNamedElem
    {

        boolean isAbstract()
        {
            return isAbstract;
        }

        void setAbstract(boolean flag)
        {
            isAbstract = flag;
        }

        Iterator getAllParents()
        {
            return parents.iterator();
        }

        void addParent(String s)
        {
            parents.add(s);
        }

        boolean hasParent(String s)
        {
            return parents.contains(s);
        }

        void addGroup(XmlGroup xmlgroup)
        {
            xmlGroups.put(xmlgroup.getName(), xmlgroup);
        }

        boolean containsGroup(String s)
        {
            return xmlGroups.containsKey(s);
        }

        XmlGroup getGroup(String s)
        {
            return (XmlGroup)xmlGroups.get(s);
        }

        Iterator getAllGroups()
        {
            return xmlGroups.values().iterator();
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT1", getName(), new Boolean(isAbstract), new Integer(parents.size()));
        }

        private boolean isAbstract;
        private List parents;
        private Map xmlGroups;

        XmlRegion(String s, Locator locator)
        {
            super(s, locator);
            parents = new ArrayList();
            xmlGroups = new HashMap();
        }
    }

    private abstract class XmlNamedElem
    {

        String getName()
        {
            return name;
        }

        XmlLocation getLocation()
        {
            return loc;
        }

        private String name;
        private XmlLocation loc;

        protected XmlNamedElem(String s, Locator locator)
        {
            super();
            loc = null;
            name = s;
            loc = new XmlLocation(locator);
        }
    }

    private class RefName
    {

        String getGroup()
        {
            return grp;
        }

        String getProp()
        {
            return prop;
        }

        void setGroup(String s)
        {
            grp = s;
        }

        void setProp(String s)
        {
            prop = s;
        }

        public int hashCode()
        {
            if(hash == -1)
                hash = grp.hashCode() ^ prop.hashCode();
            return hash;
        }

        public boolean equals(Object obj)
        {
            if(obj == this)
                return true;
            if(!(obj instanceof RefName))
            {
                return false;
            } else
            {
                RefName refname = (RefName)obj;
                return refname.prop.equals(prop) && refname.grp.equals(grp);
            }
        }

        public String toString()
        {
            return L10n.format("SRVC.CNFG.TXT10", grp, prop);
        }

        private String grp;
        private String prop;
        private int hash;

        RefName(String s, String s1)
        {
            super();
            hash = -1;
            grp = s;
            prop = s1;
        }
    }

    private static interface NodeVisitor
    {

        public abstract void action(XmlRegion xmlregion, XmlGroup xmlgroup, XmlProp xmlprop)
            throws ConfigException;
    }


    public ConfigServiceImpl()
    {
        groups = null;
        log = null;
        mainRegion = null;
        listeners = new HashSet();
        mux = new Object();
        iocQualifs = new HashMap();
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("config");
        groups = new HashMap();
        try
        {
            load();
        }
        catch(ConfigException configexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.CNFG.ERR15", configexception.getMessage()), configexception);
        }
    }

    protected void onStop()
        throws ServiceProviderException
    {
        groups = null;
        log = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.CNFG.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(map, set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR16", s3));
                        region = new XmlRegion(s3, getLocator());
                        region.setAbstract(parseBoolean(xmlattrinterceptor.getValue("abstract")));
                    } else
                    if(s2.equals("parent"))
                    {
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(s4 != null)
                        {
                            if(region.hasParent(s4))
                                throw createSaxErr(L10n.format("SRVC.CNFG.ERR17", s4));
                            region.addParent(s4);
                        }
                    } else
                    if(s2.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), regions, includes);
                    else
                    if(s2.equals("group"))
                    {
                        String s5 = xmlattrinterceptor.getValue("name");
                        if(region.containsGroup(s5))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR18", s5));
                        group = new XmlGroup(s5, getLocator());
                    } else
                    if(s2.equals("config-prop"))
                    {
                        String s6 = xmlattrinterceptor.getValue("name");
                        if(group.containsProp(s6))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR19", s6));
                        prop = new XmlProp(s6, getLocator(), getTypeId(xmlattrinterceptor.getValue("type")), group);
                    } else
                    if(s2.equals("regex"))
                    {
                        String s7 = xmlattrinterceptor.getValue("name");
                        boolean flag = true;
                        String s12 = xmlattrinterceptor.getValue("valid");
                        if(s12.equalsIgnoreCase("false"))
                            flag = false;
                        if(prop.getType() == 14)
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR42", s2, s7, prop));
                        if(s7 != null && prop.containsQualifName(s7))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR20", s7));
                        regexQlf = new XmlRegexQualif(s7, flag, getLocator());
                    } else
                    if(s2.equals("range"))
                    {
                        String s8 = xmlattrinterceptor.getValue("name");
                        if(s8 != null && prop.containsQualifName(s8))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR21", s8));
                        boolean flag1 = true;
                        String s13 = xmlattrinterceptor.getValue("inclusive");
                        if(s13.equalsIgnoreCase("false"))
                            flag1 = false;
                        boolean flag2 = true;
                        String s14 = xmlattrinterceptor.getValue("valid");
                        if(s14.equalsIgnoreCase("false"))
                            flag2 = false;
                        if(prop.getType() == 11 || prop.getType() == 14)
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR27", s8, prop));
                        rangeQlf = new XmlRangeQualif(s8, flag1, flag2, getLocator());
                    } else
                    if(s2.equals("invalid-set") || s2.equals("valid-set"))
                    {
                        String s9 = xmlattrinterceptor.getValue("name");
                        if(prop.getType() == 14)
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR42", s2, s9, prop));
                        if(s9 != null && prop.containsQualifName(s9))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR22", s9));
                        setQlf = new XmlSetQualif(s9, getLocator(), s2.equals("valid-set"));
                    } else
                    if(s2.equals("custom-qualif"))
                    {
                        String s10 = xmlattrinterceptor.getValue("name");
                        if(s10 != null && prop.containsQualifName(s10))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR38", s10));
                        if(prop.getType() == 14)
                            iocCustomQlf = new XmlIocCustomQualif(s10, getLocator());
                        else
                            customQlf = new XmlCustomQualif(s10, getLocator());
                    } else
                    if(s2.equals("hierarchy"))
                    {
                        String s11 = xmlattrinterceptor.getValue("name");
                        if(prop.getType() != 14)
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR44", s11, prop));
                        if(s11 != null && prop.containsQualifName(s11))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR38", s11));
                        hierarchyQlf = new XmlIocHierarchyQualif(s11, getLocator());
                    } else
                    if(s2.equals("extends"))
                        hierarchyQlf.setParent(xmlattrinterceptor.getValue("class"));
                    else
                    if(s2.equals("implements"))
                        hierarchyQlf.addImpl(xmlattrinterceptor.getValue("class"));
                    else
                    if(s2.equals("array"))
                        value = new ArrayList();
                    else
                    if(s2.equals("map"))
                    {
                        value = new HashMap();
                        prop.setKeyType(getTypeId(xmlattrinterceptor.getValue("key-type")));
                    } else
                    if(s2.equals("entry"))
                    {
                        mapKey = xmlattrinterceptor.getValue("key");
                        if(((Map)value).containsKey(mapKey))
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR34", mapKey, prop.getName()));
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                    throws SAXException
                {
                    if(prop != null)
                    {
                        if(customQlf != null)
                        {
                            customQlf.setUnparsedQualif(iocdescriptor);
                            return;
                        }
                        if(iocCustomQlf != null)
                        {
                            iocCustomQlf.setUnparsedQualif(iocdescriptor);
                            return;
                        }
                        if(prop.getType() != 14)
                            throw createSaxErr(L10n.format("SRVC.CNFG.ERR23", prop.getName()));
                        prop.setUnparsedValue(iocdescriptor);
                        prop.setAggr(1);
                    }
                }

                private int getTypeId(String s2)
                {
                    if(s2.equals("int8"))
                        return 4;
                    if(s2.equals("int16"))
                        return 5;
                    if(s2.equals("int32"))
                        return 6;
                    if(s2.equals("int64"))
                        return 7;
                    if(s2.equals("float32"))
                        return 9;
                    if(s2.equals("float64"))
                        return 10;
                    if(s2.equals("char16"))
                        return 8;
                    if(s2.equals("string"))
                        return 12;
                    if(s2.equals("boolean"))
                        return 11;
                    if(s2.equals("date"))
                        return 13;
                    if(s2.equals("obj"))
                        return 14;
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property type: " + s2);
                    else
                        return -1;
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("config-prop"))
                    {
                        group.addProp(prop);
                        prop = null;
                    } else
                    if(s2.equals("group"))
                    {
                        region.addGroup(group);
                        group = null;
                    } else
                    if(s2.equals("regex"))
                    {
                        regexQlf.setPattern(getPcdata());
                        prop.addQualif(regexQlf);
                        regexQlf = null;
                    } else
                    if(s2.equals("range"))
                    {
                        prop.addQualif(rangeQlf);
                        rangeQlf = null;
                    } else
                    if(s2.equals("single"))
                    {
                        prop.setUnparsedValue(getPcdata());
                        prop.setAggr(1);
                    } else
                    if(s2.equals("array"))
                    {
                        prop.setUnparsedValue(value);
                        prop.setAggr(2);
                    } else
                    if(s2.equals("map"))
                    {
                        prop.setUnparsedValue(value);
                        prop.setAggr(3);
                    } else
                    if(s2.equals("valid") || s2.equals("invalid"))
                        setQlf.addUnparsedValue(getPcdata());
                    else
                    if(s2.equals("invalid-set") || s2.equals("valid-set"))
                    {
                        prop.addQualif(setQlf);
                        setQlf = null;
                    } else
                    if(s2.equals("item"))
                        ((List)value).add(getPcdata());
                    else
                    if(s2.equals("entry"))
                        ((Map)value).put(mapKey, getPcdata());
                    else
                    if(s2.equals("min"))
                        rangeQlf.setUnparsedMin(getPcdata());
                    else
                    if(s2.equals("max"))
                        rangeQlf.setUnparsedMax(getPcdata());
                    else
                    if(s2.equals("custom-qualif"))
                    {
                        if(prop.getType() == 14)
                        {
                            addIocQualif(prop, iocCustomQlf);
                            iocCustomQlf = null;
                        } else
                        {
                            prop.addQualif(customQlf);
                            customQlf = null;
                        }
                    } else
                    if(s2.equals("hierarchy"))
                    {
                        addIocQualif(prop, hierarchyQlf);
                        hierarchyQlf = null;
                    }
                }

                private XmlRegion region;
                private XmlGroup group;
                private XmlProp prop;
                private XmlRegexQualif regexQlf;
                private XmlRangeQualif rangeQlf;
                private XmlSetQualif setQlf;
                private XmlCustomQualif customQlf;
                private XmlIocCustomQualif iocCustomQlf;
                private XmlIocHierarchyQualif hierarchyQlf;
                private Object value;
                private String mapKey;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                region = null;
                group = null;
                prop = null;
                regexQlf = null;
                rangeQlf = null;
                setQlf = null;
                customQlf = null;
                iocCustomQlf = null;
                hierarchyQlf = null;
                value = null;
                mapKey = null;
            }
            }
);
        }
        catch(ParserConfigurationException parserconfigurationexception)
        {
            throw new SAXException(parserconfigurationexception);
        }
        catch(IOException ioexception)
        {
            throw new SAXException(ioexception);
        }
    }

    public void reload()
        throws ConfigException
    {
        synchronized(mux)
        {
            groups.clear();
            load();
            for(Iterator iterator = listeners.iterator(); iterator.hasNext(); ((ConfigChangeListener)iterator.next()).configChanged());
        }
    }

    private void load()
        throws ConfigException
    {
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_config.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ConfigException(L10n.format("SRVC.CNFG.ERR24", saxexception.getMessage()), saxexception);
        }
        mainRegion = (XmlRegion)hashmap.get(getRegionName());
        if(mainRegion == null)
            throw new ConfigException(L10n.format("SRVC.CNFG.ERR1", getRegionName()));
        if(mainRegion.isAbstract())
        {
            throw new ConfigException(L10n.format("SRVC.CNFG.ERR31", getRegionName()));
        } else
        {
            flattenRegion(mainRegion, hashmap);
            doVarExpansion();
            parsePropValues();
            initIocQualifs();
            checkValueQualifiers();
            createConfigGroups();
            return;
        }
    }

    private void createConfigGroups()
    {
        XmlGroup xmlgroup;
        ConfigGroup configgroup;
label0:
        for(Iterator iterator = mainRegion.getAllGroups(); iterator.hasNext(); groups.put(xmlgroup.getName(), configgroup))
        {
            xmlgroup = (XmlGroup)iterator.next();
            configgroup = new ConfigGroup(xmlgroup.getName());
            Iterator iterator1 = xmlgroup.getAllProps();
            XmlProp xmlprop;
label1:
            do
            {
                do
                {
                    if(!iterator1.hasNext())
                        continue label0;
                    xmlprop = (XmlProp)iterator1.next();
                    switch(xmlprop.getAggr())
                    {
                    default:
                        continue label1;

                    case 2: // '\002'
                        configgroup.addProp(xmlprop.getName(), xmlprop.getArrValue());
                        configgroup.addList(xmlprop.getName(), (List)xmlprop.getParsedValue());
                        break;

                    case 3: // '\003'
                        configgroup.addProp(xmlprop.getName(), xmlprop.getParsedValue());
                        break;

                    case 1: // '\001'
                        configgroup.addProp(xmlprop.getName(), xmlprop.getType() != 14 ? xmlprop.getParsedValue() : xmlprop.getUnparsedValue());
                        break;
                    }
                } while(true);
                continue label0;
            } while($assertionsDisabled);
            throw new AssertionError("Invalid property aggregation type in: " + xmlprop);
        }

    }

    private void flattenRegion(XmlRegion xmlregion, Map map)
        throws ConfigException
    {
        Iterator iterator = xmlregion.getAllParents();
_L6:
        Iterator iterator1;
        if(!iterator.hasNext())
            break MISSING_BLOCK_LABEL_245;
        String s = (String)iterator.next();
        XmlRegion xmlregion1 = (XmlRegion)map.get(s);
        if(xmlregion1 == null)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR2", s), xmlregion));
        flattenRegion(xmlregion1, map);
        iterator1 = xmlregion1.getAllGroups();
_L2:
        XmlGroup xmlgroup1;
        Iterator iterator2;
        if(!iterator1.hasNext())
            continue; /* Loop/switch isn't completed */
        XmlGroup xmlgroup = (XmlGroup)iterator1.next();
        if(!xmlregion.containsGroup(xmlgroup.getName()))
        {
            xmlregion.addGroup(xmlgroup);
            continue; /* Loop/switch isn't completed */
        }
        xmlgroup1 = xmlregion.getGroup(xmlgroup.getName());
        iterator2 = xmlgroup.getAllProps();
_L4:
        XmlProp xmlprop;
        while(iterator2.hasNext())
        {
label0:
            {
                xmlprop = (XmlProp)iterator2.next();
                if(xmlgroup1.containsProp(xmlprop.getName()))
                    break label0;
                xmlgroup1.addProp(xmlprop);
            }
        }
        if(true) goto _L2; else goto _L1
_L1:
        continue; /* Loop/switch isn't completed */
        XmlProp xmlprop1 = xmlgroup1.getProp(xmlprop.getName());
        if(xmlprop1.getType() == xmlprop.getType() && xmlprop1.getAggr() == xmlprop.getAggr()) goto _L4; else goto _L3
_L3:
        throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR3", xmlprop1), xmlprop1));
        return;
        if(true) goto _L6; else goto _L5
_L5:
    }

    private void parsePropValues()
        throws ConfigException
    {
        dfs(new NodeVisitor() {

            public void action(XmlRegion xmlregion, XmlGroup xmlgroup, XmlProp xmlprop)
                throws ConfigException
            {
                if(xmlprop.getType() == 14)
                    return;
label0:
                switch(xmlprop.getAggr())
                {
                case 2: // '\002'
                    List list = (List)xmlprop.getUnparsedValue();
                    int i = list.size();
                    ArrayList arraylist = new ArrayList(i);
                    for(int j = 0; j < i; j++)
                        arraylist.add(decodeValue((String)list.get(j), xmlprop.getType(), xmlprop));

                    xmlprop.setParsedValue(arraylist);
                    switch(xmlprop.getType())
                    {
                    case 11: // '\013'
                        boolean aflag[] = new boolean[i];
                        for(int k = 0; k < aflag.length; k++)
                            aflag[k] = ((Boolean)arraylist.get(k)).booleanValue();

                        xmlprop.setArrValue(aflag);
                        break label0;

                    case 4: // '\004'
                        byte abyte0[] = new byte[i];
                        for(int l = 0; l < abyte0.length; l++)
                            abyte0[l] = ((Byte)arraylist.get(l)).byteValue();

                        xmlprop.setArrValue(abyte0);
                        break label0;

                    case 5: // '\005'
                        short aword0[] = new short[i];
                        for(int i1 = 0; i1 < aword0.length; i1++)
                            aword0[i1] = ((Short)arraylist.get(i1)).shortValue();

                        xmlprop.setArrValue(aword0);
                        break label0;

                    case 6: // '\006'
                        int ai[] = new int[i];
                        for(int j1 = 0; j1 < ai.length; j1++)
                            ai[j1] = ((Integer)arraylist.get(j1)).intValue();

                        xmlprop.setArrValue(ai);
                        break label0;

                    case 7: // '\007'
                        long al[] = new long[i];
                        for(int k1 = 0; k1 < al.length; k1++)
                            al[k1] = ((Long)arraylist.get(k1)).longValue();

                        xmlprop.setArrValue(al);
                        break label0;

                    case 9: // '\t'
                        float af[] = new float[i];
                        for(int l1 = 0; l1 < af.length; l1++)
                            af[l1] = ((Float)arraylist.get(l1)).floatValue();

                        xmlprop.setArrValue(af);
                        break label0;

                    case 10: // '\n'
                        double ad[] = new double[i];
                        for(int i2 = 0; i2 < ad.length; i2++)
                            ad[i2] = ((Double)arraylist.get(i2)).doubleValue();

                        xmlprop.setArrValue(ad);
                        break label0;

                    case 12: // '\f'
                        String as[] = new String[i];
                        for(int j2 = 0; j2 < as.length; j2++)
                            as[j2] = (String)arraylist.get(j2);

                        xmlprop.setArrValue(as);
                        break label0;

                    case 8: // '\b'
                        char ac[] = new char[i];
                        for(int k2 = 0; k2 < ac.length; k2++)
                            ac[k2] = ((Character)arraylist.get(k2)).charValue();

                        xmlprop.setArrValue(ac);
                        break label0;

                    case 13: // '\r'
                        Date adate[] = new Date[i];
                        for(int l2 = 0; l2 < adate.length; l2++)
                            adate[l2] = (Date)arraylist.get(l2);

                        xmlprop.setArrValue(adate);
                        break label0;
                    }
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property value type in: " + xmlprop);
                    break;

                case 3: // '\003'
                    HashMap hashmap = new HashMap();
                    Map.Entry entry;
                    for(Iterator iterator1 = ((Map)xmlprop.getUnparsedValue()).entrySet().iterator(); iterator1.hasNext(); hashmap.put(decodeValue((String)entry.getKey(), xmlprop.getKeyType(), xmlprop), decodeValue((String)entry.getValue(), xmlprop.getType(), xmlprop)))
                        entry = (Map.Entry)iterator1.next();

                    xmlprop.setParsedValue(hashmap);
                    break;

                case 1: // '\001'
                    xmlprop.setParsedValue(decodeValue(xmlprop.getStrUnparsedValue(), xmlprop.getType(), xmlprop));
                    break;

                default:
                    if(!$assertionsDisabled)
                        throw new AssertionError("Invalid property type in: " + xmlprop);
                    break;
                }
                Iterator iterator = xmlprop.getAllQualifs();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    XmlQualif xmlqualif = (XmlQualif)iterator.next();
                    if((xmlqualif instanceof XmlSetQualif))
                    {
                        XmlSetQualif xmlsetqualif = (XmlSetQualif)xmlqualif;
                        ArrayList arraylist1 = xmlsetqualif.getAllUnparsedValues();
                        int i3 = arraylist1.size();
                        int j3 = 0;
                        while(j3 < i3)
                        {
                            xmlsetqualif.addParsedValue(decodeValue((String)arraylist1.get(j3), xmlprop.getType(), xmlsetqualif));
                            j3++;
                        }
                    } else
                    if((xmlqualif instanceof XmlRangeQualif))
                    {
                        XmlRangeQualif xmlrangequalif = (XmlRangeQualif)xmlqualif;
                        Object obj = null;
                        Object obj1 = null;
                        if(xmlrangequalif.getUnparsedMin() != null)
                        {
                            obj = decodeValue(xmlrangequalif.getUnparsedMin(), xmlprop.getType(), xmlrangequalif);
                            xmlrangequalif.setParsedMin(obj);
                            if(!$assertionsDisabled && !(obj instanceof Comparable))
                                throw new AssertionError();
                        }
                        if(xmlrangequalif.getUnparsedMax() != null)
                        {
                            obj1 = decodeValue(xmlrangequalif.getUnparsedMax(), xmlprop.getType(), xmlrangequalif);
                            xmlrangequalif.setParsedMax(obj1);
                            if(!$assertionsDisabled && !(obj1 instanceof Comparable))
                                throw new AssertionError();
                        }
                        if(obj != null && obj1 != null && ((Comparable)obj).compareTo(obj1) > 0)
                            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR28", xmlrangequalif), xmlrangequalif));
                    } else
                    if((xmlqualif instanceof XmlRegexQualif))
                    {
                        XmlRegexQualif xmlregexqualif = (XmlRegexQualif)xmlqualif;
                        xmlregexqualif.compilePattern();
                    } else
                    if((xmlqualif instanceof XmlCustomQualif))
                    {
                        XmlCustomQualif xmlcustomqualif = (XmlCustomQualif)xmlqualif;
                        xmlcustomqualif.instantiateQualif();
                    }
                } while(true);
            }

            static final boolean $assertionsDisabled; /* synthetic field */



                throws ConfigException
            {
                super();
            }
        }
);
    }

    private Object decodeValue(String s, int i, XmlNamedElem xmlnamedelem)
        throws ConfigException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        i;
        JVM INSTR tableswitch 4 7: default 85
    //                   4 48
    //                   5 48
    //                   6 48
    //                   7 48;
           goto _L1 _L2 _L2 _L2 _L2
_L2:
        if(!intValue.matcher(s).matches())
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR4", getStrValueType(i), s), xmlnamedelem));
_L1:
        i;
        JVM INSTR tableswitch 4 13: default 271
    //                   4 152
    //                   5 164
    //                   6 176
    //                   7 188
    //                   8 231
    //                   9 200
    //                   10 212
    //                   11 140
    //                   12 224
    //                   13 226;
           goto _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13
_L11:
        return new Boolean(Utils.parseBoolean(s));
_L4:
        try
        {
            return new Byte(Utils.parseByte(s));
        }
        catch(UtilsException utilsexception)
        {
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR4", getStrValueType(i), s), xmlnamedelem), utilsexception);
        }
_L5:
        return new Short(Utils.parseShort(s));
_L6:
        return new Integer(Utils.parseInt(s));
_L7:
        return new Long(Utils.parseLong(s));
_L9:
        return new Float(Utils.parseFloat(s));
_L10:
        return new Double(Utils.parseDouble(s));
_L12:
        return s;
_L13:
        return Utils.parseDateOrTime(s);
_L8:
        if(s.length() == 1)
            return new Character(s.charAt(0));
        throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR5", s), xmlnamedelem));
_L3:
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid config property value: " + i);
        if(!$assertionsDisabled)
            throw new AssertionError("Never reached.");
        else
            return null;
    }

    private String getStrValueType(int i)
    {
        switch(i)
        {
        case 11: // '\013'
            return "TYPE_BOOLEAN";

        case 4: // '\004'
            return "TYPE_INT8";

        case 5: // '\005'
            return "TYPE_INT16";

        case 6: // '\006'
            return "TYPE_INT32";

        case 7: // '\007'
            return "TYPE_INT64";

        case 9: // '\t'
            return "TYPE_FLOAT32";

        case 10: // '\n'
            return "TYPE_FLOAT64";

        case 12: // '\f'
            return "TYPE_STRING";

        case 8: // '\b'
            return "TYPE_CHAR16";

        case 13: // '\r'
            return "TYPE_DATE";

        case 14: // '\016'
            return "TYPE_IOC_OBJ";
        }
        if(!$assertionsDisabled)
            throw new AssertionError("Invalid config property value type: " + i);
        if(!$assertionsDisabled)
            throw new AssertionError("Never reached.");
        else
            return null;
    }

    private void doVarExpansion()
        throws ConfigException
    {
        if(!$assertionsDisabled && mainRegion == null)
        {
            throw new AssertionError();
        } else
        {
            dfs(new NodeVisitor() {

                public void action(XmlRegion xmlregion, XmlGroup xmlgroup, XmlProp xmlprop)
                    throws ConfigException
                {
label0:
                    switch(xmlprop.getAggr())
                    {
                    case 2: // '\002'
                        List list = (List)xmlprop.getUnparsedValue();
                        int i = list.size();
                        for(int j = 0; j < i; j++)
                        {
                            String s1 = doVarSubst((String)list.get(j), mainRegion, xmlgroup, new HashSet(), xmlprop);
                            if(s1 != null)
                                list.set(j, s1);
                        }

                        break;

                    case 3: // '\003'
                        Map map = (Map)xmlprop.getUnparsedValue();
                        Iterator iterator1 = map.entrySet().iterator();
                        do
                        {
                            Map.Entry entry;
                            String s2;
                            do
                            {
                                if(!iterator1.hasNext())
                                    break label0;
                                entry = (Map.Entry)iterator1.next();
                                s2 = doVarSubst((String)entry.getValue(), mainRegion, xmlgroup, new HashSet(), xmlprop);
                            } while(s2 == null);
                            entry.setValue(s2);
                        } while(true);

                    case 1: // '\001'
                        if(xmlprop.getType() == 14)
                            break;
                        String s = doVarSubst(xmlprop.getStrUnparsedValue(), mainRegion, xmlgroup, new HashSet(), xmlprop);
                        if(s != null)
                            xmlprop.setUnparsedValue(s);
                        break;

                    default:
                        if(!$assertionsDisabled)
                            throw new AssertionError("Invalid property type in: " + xmlprop);
                        break;
                    }
                    Iterator iterator = xmlprop.getAllQualifs();
                    do
                    {
                        if(!iterator.hasNext())
                            break;
                        XmlQualif xmlqualif = (XmlQualif)iterator.next();
                        if((xmlqualif instanceof XmlSetQualif))
                        {
                            XmlSetQualif xmlsetqualif = (XmlSetQualif)xmlqualif;
                            ArrayList arraylist = xmlsetqualif.getAllUnparsedValues();
                            int k = arraylist.size();
                            int l = 0;
                            while(l < k)
                            {
                                String s5 = doVarSubst((String)arraylist.get(l), mainRegion, xmlprop.getGroup(), new HashSet(), xmlsetqualif);
                                if(s5 != null)
                                    arraylist.set(l, s5);
                                l++;
                            }
                        } else
                        if((xmlqualif instanceof XmlRangeQualif))
                        {
                            XmlRangeQualif xmlrangequalif = (XmlRangeQualif)xmlqualif;
                            if(xmlrangequalif.getUnparsedMin() != null)
                            {
                                String s3 = doVarSubst(xmlrangequalif.getUnparsedMin(), mainRegion, xmlprop.getGroup(), new HashSet(), xmlrangequalif);
                                if(s3 != null)
                                    xmlrangequalif.setUnparsedMin(s3);
                            }
                            if(xmlrangequalif.getUnparsedMax() != null)
                            {
                                String s4 = doVarSubst(xmlrangequalif.getUnparsedMax(), mainRegion, xmlprop.getGroup(), new HashSet(), xmlrangequalif);
                                if(s4 != null)
                                    xmlrangequalif.setUnparsedMax(s4);
                            }
                        }
                    } while(true);
                }

                static final boolean $assertionsDisabled; /* synthetic field */



                throws ConfigException
            {
                super();
            }
            }
);
            return;
        }
    }

    private String doVarSubst(String s, XmlRegion xmlregion, XmlGroup xmlgroup, Set set, XmlNamedElem xmlnamedelem)
        throws ConfigException
    {
        Matcher matcher;
        StringBuffer stringbuffer;
        if(!$assertionsDisabled && (s == null || xmlregion == null || xmlgroup == null || set == null))
            throw new AssertionError();
        matcher = varRegex.matcher(s);
        stringbuffer = new StringBuffer();
_L3:
        if(!matcher.find()) goto _L2; else goto _L1
_L1:
        XmlProp xmlprop;
        String s3;
        String s5;
        String s1 = matcher.group();
        if(s1.length() < 4)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR8", s, varRegex.pattern()), xmlnamedelem));
        xmlprop = findPropByRef(xmlregion, parseRef(s1.substring(2, s1.length() - 1), xmlgroup.getName(), xmlnamedelem), xmlnamedelem);
        if(xmlprop.getAggr() != 1)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR9", xmlprop.getAggrStr()), xmlprop));
        if(set.contains(xmlprop.getName()))
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR29", xmlprop), xmlprop));
        s3 = xmlprop.getName();
        Object obj = null;
        set.add(s3);
        s5 = null;
        if(xmlprop.getType() == 14)
        {
            IocDescriptor iocdescriptor = (IocDescriptor)xmlprop.getUnparsedValue();
            try
            {
                if(iocdescriptor.getPolicy() == 3)
                    throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR32", xmlprop), xmlprop));
                s5 = iocdescriptor.createNewObj().toString();
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR33", xmlprop), xmlprop), iocdescriptorexception);
            }
        } else
        {
            s5 = xmlprop.getStrUnparsedValue();
        }
        if(!$assertionsDisabled && s5 == null)
            throw new AssertionError();
        String s4 = doVarSubst(s5, xmlregion, xmlgroup, set, ((XmlNamedElem) (xmlprop)));
        set.remove(s3);
        break MISSING_BLOCK_LABEL_381;
        Exception exception;
        exception;
        set.remove(s3);
        throw exception;
        if(s4 == null)
            s4 = s5;
        matcher.appendReplacement(stringbuffer, s4.replaceAll("\\\\", "\\\\\\\\"));
          goto _L3
_L2:
        matcher.appendTail(stringbuffer);
        String s2 = stringbuffer.toString();
        return s.equals(s2) ? null : s2;
    }

    private RefName parseRef(String s, String s1, XmlNamedElem xmlnamedelem)
        throws ConfigException
    {
        String as[] = s.split(":");
        if(as.length < 1 || as.length > 2)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR10", s), xmlnamedelem));
        if(as[0].length() == 0)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR10", s), xmlnamedelem));
        else
            return as.length != 1 ? new RefName(as[0], as[1]) : new RefName(s1, as[0]);
    }

    private XmlProp findPropByRef(XmlRegion xmlregion, RefName refname, XmlNamedElem xmlnamedelem)
        throws ConfigException
    {
        if(!$assertionsDisabled && refname == null)
            throw new AssertionError();
        XmlGroup xmlgroup = xmlregion.getGroup(refname.getGroup());
        if(xmlgroup == null)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR11", refname), xmlnamedelem));
        XmlProp xmlprop = xmlgroup.getProp(refname.getProp());
        if(xmlprop == null)
            throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR12", refname), xmlnamedelem));
        else
            return xmlprop;
    }

    private void checkValueQualifiers()
        throws ConfigException
    {
        dfs(new NodeVisitor() {

            public void action(XmlRegion xmlregion, XmlGroup xmlgroup, XmlProp xmlprop)
                throws ConfigException
            {
                for(Iterator iterator = xmlprop.getAllQualifs(); iterator.hasNext();)
                {
                    XmlQualif xmlqualif = (XmlQualif)iterator.next();
                    if(!xmlqualif.qualifValue(xmlprop))
                        throw new ConfigException(addLocInfo(L10n.format("SRVC.CNFG.ERR13", xmlregion, xmlgroup, xmlprop, xmlqualif.getName()), xmlprop));
                }

            }


                throws ConfigException
            {
                super();
            }
        }
);
    }

    private void dfs(NodeVisitor nodevisitor)
        throws ConfigException
    {
        for(Iterator iterator = mainRegion.getAllGroups(); iterator.hasNext();)
        {
            XmlGroup xmlgroup = (XmlGroup)iterator.next();
            Iterator iterator1 = xmlgroup.getAllProps();
            while(iterator1.hasNext())
                nodevisitor.action(mainRegion, xmlgroup, (XmlProp)iterator1.next());
        }

    }

    private ConfigGroup getGroup(String s)
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && !Thread.holdsLock(mux))
            throw new AssertionError();
        ConfigGroup configgroup = (ConfigGroup)groups.get(s);
        if(configgroup == null)
            throw new ConfigMissingPropException(L10n.format("SRVC.CNFG.ERR14", s));
        else
            return configgroup;
    }

    private String addLocInfo(String s, XmlNamedElem xmlnamedelem)
    {
        String s1 = xmlnamedelem.getLocation().toStrLocation();
        return (s1 != null ? s1 + ": " : "") + s;
    }

    private void addIocQualif(XmlProp xmlprop, XmlIocQualif xmliocqualif)
    {
        if(!$assertionsDisabled && xmlprop == null)
            throw new AssertionError();
        if(!$assertionsDisabled && xmliocqualif == null)
            throw new AssertionError();
        if(!$assertionsDisabled && xmlprop.getType() != 14)
            throw new AssertionError();
        String s = xmlprop.getGroup().getName() + ':' + xmlprop.getName();
        ArrayList arraylist = (ArrayList)iocQualifs.get(s);
        if(arraylist == null)
            arraylist = new ArrayList();
        arraylist.add(xmliocqualif);
        iocQualifs.put(s, arraylist);
    }

    private void initIocQualifs()
        throws ConfigException
    {
        for(Iterator iterator = iocQualifs.values().iterator(); iterator.hasNext();)
        {
            ArrayList arraylist = (ArrayList)iterator.next();
            int i = 0;
            while(i < arraylist.size())
            {
                ((XmlIocQualif)arraylist.get(i)).init();
                i++;
            }
        }

    }

    private void qualifIocValue(String s, String s1, Object obj)
        throws ConfigException
    {
        String s2 = s + ':' + s1;
        ArrayList arraylist = (ArrayList)iocQualifs.get(s2);
        if(arraylist != null)
        {
            for(int i = 0; i < arraylist.size(); i++)
            {
                XmlIocQualif xmliocqualif = (XmlIocQualif)arraylist.get(i);
                if(!xmliocqualif.qualifIocValue(obj))
                    throw new ConfigException(L10n.format("SRVC.CNFG.ERR13", mainRegion, s, s1, xmliocqualif.getName()));
            }

        }
    }

    public int getInt(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getInt(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public int[] getIntArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getIntArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public char getChar(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getChar(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public char[] getCharArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getCharArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public short getShort(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getShort(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public short[] getShortArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getShortArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public byte getByte(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getByte(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public byte[] getByteArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getByteArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public double getDouble(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getDouble(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public double[] getDoubleArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getDoubleArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public float getFloat(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getFloat(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public float[] getFloatArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getFloatArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public long getLong(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getLong(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public long[] getLongArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getLongArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public String getString(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getString(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public String[] getStringArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getStringArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean getBoolean(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getBoolean(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean[] getBooleanArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getBooleanArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public Date getDate(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getDate(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public Date[] getDateArr(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getDateArr(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public Map getMap(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getMap(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public List getList(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return getGroup(s).getList(s1);
        Exception exception;
        exception;
        throw exception;
    }

    public Object getIocObj(String s, String s1, Object obj)
        throws ConfigException
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        ArgAssert.nullArg(obj, "key");
        Object obj1 = mux;
        JVM INSTR monitorenter ;
        Object obj2 = getGroup(s).getIocObj(s1, obj);
        qualifIocValue(s, s1, obj2);
        return obj2;
        Exception exception;
        exception;
        throw exception;
    }

    public Object getIocObj(String s, String s1)
        throws ConfigException
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        Object obj1 = getGroup(s).getIocObj(s1);
        qualifIocValue(s, s1, obj1);
        return obj1;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean containsProp(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "prop");
        Object obj = mux;
        JVM INSTR monitorenter ;
        ConfigGroup configgroup = (ConfigGroup)groups.get(s);
        if(configgroup != null)
            return configgroup.containsProp(s1);
        false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean containsGroup(String s)
    {
        ArgAssert.nullArg(s, "grp");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return groups.get(s) != null;
        Exception exception;
        exception;
        throw exception;
    }

    public String getName()
    {
        return "config";
    }

    public void addListener(ConfigChangeListener configchangelistener)
    {
        ArgAssert.nullArg(configchangelistener, "listener");
        synchronized(mux)
        {
            listeners.add(configchangelistener);
        }
    }

    public List getAllListeners()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(listeners));
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeListener(ConfigChangeListener configchangelistener)
    {
        ArgAssert.nullArg(configchangelistener, "listener");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return listeners.remove(configchangelistener);
        Exception exception;
        exception;
        throw exception;
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

    private Map groups;
    private Logger log;
    private XmlRegion mainRegion;
    private Set listeners;
    private Object mux;
    private Map iocQualifs;
    private static final Pattern varRegex = Pattern.compile("\\$\\{[^:}]+(:[^}]+)?\\}");
    private static final Pattern intValue = Pattern.compile("^\\s*0|([-+]?((0x)|(0X)|[1-9a-fA-F]).*)\\s*$");
    static Class class$com$fitechlabs$xtier$services$config$ConfigCustomQualifier; /* synthetic field */
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(ConfigServiceImpl.class).desiredAssertionStatus();
    }







}
