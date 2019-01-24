// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.security.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.security.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.security.impl:
//            SecurityIdentityImpl, SecurityResourceImpl, SecurityRoleImpl, SecurityAclImpl,
//            SecurityRealm

public class SecurityServiceImpl extends ServiceProviderAdapter
    implements SecurityService
{
    private class XmlAcl
    {

        String getAction()
        {
            return action;
        }

        String getIdentity()
        {
            return id;
        }

        String getResource()
        {
            return rs;
        }

        int getType()
        {
            return type;
        }

        public int hashCode()
        {
            return id.hashCode() ^ rs.hashCode() ^ action.hashCode();
        }

        public boolean equals(Object obj)
        {
            if(obj == this)
                return true;
            if(obj == null)
            {
                return false;
            } else
            {
                XmlAcl xmlacl = (XmlAcl)obj;
                return xmlacl.action.equals(action) && xmlacl.id.equals(id) && xmlacl.rs.equals(rs) && xmlacl.type == type;
            }
        }

        public String toString()
        {
            return L10n.format("SRVC.SCRT.TXT12", new Integer(type), rs, id, action);
        }

        private int type;
        private String rs;
        private String id;
        private String action;

        XmlAcl(int i, String s, String s1, String s2)
        {
            super();
            type = i;
            rs = s;
            id = s1;
            action = s2;
        }
    }

    private class XmlRole extends XmlNamedElement
    {

        void grant(String s, String s1)
        {
            Object obj = (Set)grants.get(s);
            if(obj == null)
                grants.put(s, obj = new HashSet());
            ((Set) (obj)).add(s1);
        }

        Map getGrants()
        {
            return grants;
        }

        void addIdRef(XmlIdRef xmlidref)
        {
            idRefs.add(xmlidref);
        }

        boolean containsIdRef(XmlIdRef xmlidref)
        {
            return idRefs.contains(xmlidref);
        }

        Set getIdRefs()
        {
            return idRefs;
        }

        public String toString()
        {
            return L10n.format("SRVC.SCRT.TXT9", getName(), getXmlLocation());
        }

        private Set idRefs;
        private Map grants;

        XmlRole(String s, Locator locator)
        {
            super(s, locator);
            idRefs = new HashSet();
            grants = new HashMap();
        }
    }

    private class XmlIdentity extends XmlNamedElement
    {

        boolean isGroup()
        {
            return isGroup;
        }

        void addIdRef(XmlIdRef xmlidref)
        {
            idRefs.add(xmlidref);
        }

        Set getIdRefs()
        {
            return idRefs;
        }

        boolean containsIdRef(XmlIdRef xmlidref)
        {
            return idRefs.contains(xmlidref);
        }

        public String toString()
        {
            return L10n.format("SRVC.SCRT.TXT8", getName(), getXmlLocation(), Boolean.toString(isGroup));
        }

        private Set idRefs;
        private boolean isGroup;

        XmlIdentity(String s, boolean flag, Locator locator)
        {
            super(s, locator);
            idRefs = new HashSet();
            isGroup = flag;
        }
    }

    private class XmlIdRef extends XmlNamedElement
    {

        boolean isGroup()
        {
            return isGroup;
        }

        public String toString()
        {
            return L10n.format("SRVC.SCRT.TXT7", getName(), Boolean.toString(isGroup), getXmlLocation());
        }

        private boolean isGroup;

        XmlIdRef(String s, boolean flag, Locator locator)
        {
            super(s, locator);
            isGroup = flag;
        }
    }

    private class XmlResource extends XmlNamedElement
    {

        void addAction(String s)
        {
            actions.add(s);
        }

        boolean containsAction(String s)
        {
            return actions.contains(s);
        }

        Set getActions()
        {
            return actions;
        }

        public String toString()
        {
            return L10n.format("SRVC.SCRT.TXT6", getName(), getXmlLocation());
        }

        private Set actions;

        XmlResource(String s, Locator locator)
        {
            super(s, locator);
            actions = new HashSet();
        }
    }

    private class XmlRegion extends XmlNamedElement
    {

        Set getIdentities()
        {
            return ids;
        }

        Set getResources()
        {
            return resources;
        }

        Set getRoles()
        {
            return roles;
        }

        Set getAcls()
        {
            return acls;
        }

        void addRole(XmlRole xmlrole)
        {
            roles.add(xmlrole);
        }

        boolean containsRole(XmlRole xmlrole)
        {
            return roles.contains(xmlrole);
        }

        void addIdentity(XmlIdentity xmlidentity)
        {
            ids.add(xmlidentity);
        }

        boolean containsIdentity(XmlIdentity xmlidentity)
        {
            return ids.contains(xmlidentity);
        }

        void addResource(XmlResource xmlresource)
        {
            resources.add(xmlresource);
        }

        boolean containsResource(XmlResource xmlresource)
        {
            return resources.contains(xmlresource);
        }

        void addAcl(XmlAcl xmlacl)
        {
            acls.add(xmlacl);
        }

        boolean containsAcl(XmlAcl xmlacl)
        {
            return acls.contains(xmlacl);
        }

        public String toString()
        {
            return L10n.format("SRVC.SCRT.TXT5", getName(), getXmlLocation());
        }

        private Set roles;
        private Set resources;
        private Set ids;
        private Set acls;

        XmlRegion(String s, Locator locator)
        {
            super(s, locator);
            roles = new HashSet();
            resources = new HashSet();
            ids = new HashSet();
            acls = new HashSet();
        }
    }

    private abstract class XmlNamedElement
    {

        String getName()
        {
            return name;
        }

        XmlLocation getXmlLocation()
        {
            return xmlLoc;
        }

        public int hashCode()
        {
            return name.hashCode();
        }

        public boolean equals(Object obj)
        {
            if(obj == this)
                return true;
            if(obj == null)
                return false;
            else
                return ((XmlNamedElement)obj).getName().equals(name);
        }

        private String name;
        private XmlLocation xmlLoc;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlNamedElement(String s, Locator locator)
        {
            super();
            if(!$assertionsDisabled && (s == null || locator == null))
            {
                throw new AssertionError();
            } else
            {
                name = s;
                xmlLoc = new XmlLocation(locator);
                return;
            }
        }
    }


    public SecurityServiceImpl()
    {
        listeners = new ArrayList();
        realm = null;
        log = null;
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.SCRT.WRN1", s));
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
                            throw createSaxErr(L10n.format("SRVC.SCRT.ERR2", s3));
                        region = new XmlRegion(s3, getLocator());
                    } else
                    if(s2.equals("acl"))
                    {
                        String s4 = xmlattrinterceptor.getValue("identity");
                        String s8 = xmlattrinterceptor.getValue("type");
                        String s10 = xmlattrinterceptor.getValue("resource");
                        String s11 = xmlattrinterceptor.getValue("action");
                        byte byte0 = ((byte)(!s8.equals("grant") ? 2 : 1));
                        XmlAcl xmlacl = new XmlAcl(byte0, s10, s4, s11);
                        if(region.containsAcl(xmlacl))
                            throw createSaxErr(L10n.format("SRVC.SCRT.ERR23", region, xmlacl));
                        region.addAcl(xmlacl);
                    } else
                    if(s2.equals("identity"))
                    {
                        String s5 = xmlattrinterceptor.getValue("user");
                        String s9 = xmlattrinterceptor.getValue("group");
                        if(s5 == null && s9 == null || s5 != null && s9 != null)
                            throw createSaxErr(L10n.format("SRVC.SCRT.ERR4", region, s5, s9));
                        XmlIdRef xmlidref = new XmlIdRef(s5 != null ? s5 : s9, s9 != null, getLocator());
                        if(id != null)
                        {
                            if(!$assertionsDisabled && !id.isGroup())
                                throw new AssertionError();
                            if(id.containsIdRef(xmlidref))
                                throw createSaxErr(L10n.format("SRVC.SCRT.ERR5", region, id, xmlidref));
                            id.addIdRef(xmlidref);
                        } else
                        {
                            if(!$assertionsDisabled && role == null)
                                throw new AssertionError();
                            if(role.containsIdRef(xmlidref))
                                throw createSaxErr(L10n.format("SRVC.SCRT.ERR6", region, role, xmlidref));
                            role.addIdRef(xmlidref);
                        }
                    } else
                    if(s2.equals("action"))
                    {
                        String s6 = xmlattrinterceptor.getValue("name");
                        if(rs != null)
                        {
                            if(rs.containsAction(s6))
                                throw createSaxErr(L10n.format("SRVC.SCRT.ERR7", region, rs, s6));
                            rs.addAction(s6);
                        } else
                        {
                            if(!$assertionsDisabled && role == null)
                                throw new AssertionError();
                            if(!$assertionsDisabled && roleRsName == null)
                                throw new AssertionError();
                            role.grant(roleRsName, s6);
                        }
                    } else
                    if(s2.equals("grant"))
                        roleRsName = xmlattrinterceptor.getValue("resource");
                    else
                    if(s2.equals("role"))
                    {
                        role = new XmlRole(xmlattrinterceptor.getValue("name"), getLocator());
                        if(region.containsRole(role))
                            throw createSaxErr(L10n.format("SRVC.SCRT.ERR8", region, role));
                    } else
                    if(s2.equals("resource"))
                    {
                        rs = new XmlResource(xmlattrinterceptor.getValue("name"), getLocator());
                        if(region.containsResource(rs))
                            throw createSaxErr(L10n.format("SRVC.SCRT.ERR9", region, rs));
                    } else
                    if(s2.equals("user") || s2.equals("group"))
                    {
                        boolean flag = s2.equals("group");
                        id = new XmlIdentity(xmlattrinterceptor.getValue("name"), flag, getLocator());
                        if(region.containsIdentity(id))
                            throw createSaxErr(L10n.format("SRVC.SCRT.ERR10", region, id));
                    } else
                    if(s2.equals("include"))
                    {
                        String s7 = xmlattrinterceptor.getValue("path");
                        parseXmlConfig(s7, regions, includes);
                    }
                }

                protected void onTagEnd(String s2)
                {
                    if(s2.equals("region"))
                    {
                        regions.put(region.getName(), region);
                        region = null;
                    } else
                    if(s2.equals("user") || s2.equals("group"))
                    {
                        region.addIdentity(id);
                        id = null;
                    } else
                    if(s2.equals("resource"))
                    {
                        region.addResource(rs);
                        rs = null;
                    } else
                    if(s2.equals("role"))
                    {
                        region.addRole(role);
                        role = null;
                    }
                }

                private XmlRegion region;
                private XmlIdentity id;
                private XmlRole role;
                private XmlResource rs;
                private String roleRsName;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                region = null;
                id = null;
                role = null;
                rs = null;
                roleRsName = null;
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
            throw new SAXException(L10n.format("SRVC.SCRT.ERR12", s1), ioexception);
        }
    }

    protected void onStop()
    {
        listeners.clear();
        realm = null;
        log = null;
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("security");
        try
        {
            load();
        }
        catch(SecurityConfigException securityconfigexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.SCRT.ERR13", securityconfigexception.getMessage()), securityconfigexception);
        }
    }

    public void reload()
        throws SecurityConfigException
    {
        SecurityChangeListener asecuritychangelistener[] = null;
        synchronized(mux)
        {
            load();
            listeners.toArray(asecuritychangelistener = new SecurityChangeListener[listeners.size()]);
        }
        for(int i = 0; i < asecuritychangelistener.length; i++)
            asecuritychangelistener[i].configChanged();

    }

    public void addListener(SecurityChangeListener securitychangelistener)
    {
        ArgAssert.nullArg(securitychangelistener, "listener");
        synchronized(mux)
        {
            listeners.add(securitychangelistener);
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

    public boolean removeListener(SecurityChangeListener securitychangelistener)
    {
        ArgAssert.nullArg(securitychangelistener, "listener");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return listeners.remove(securitychangelistener);
        Exception exception;
        exception;
        throw exception;
    }

    private void load()
        throws SecurityConfigException
    {
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_security.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR14", saxexception.getMessage()), saxexception);
        }
        String s = getRegionName();
        XmlRegion xmlregion = (XmlRegion)hashmap.get(s);
        if(xmlregion == null)
        {
            throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR15", s));
        } else
        {
            realm = processXmlRegion(xmlregion);
            return;
        }
    }

    private void addIdentities(XmlRegion xmlregion, SecurityRealm securityrealm)
    {
        Set set = xmlregion.getIdentities();
        XmlIdentity xmlidentity;
        for(Iterator iterator = set.iterator(); iterator.hasNext(); securityrealm.addIdentity(new SecurityIdentityImpl(xmlidentity.getName(), xmlidentity.isGroup())))
            xmlidentity = (XmlIdentity)iterator.next();

    }

    private void resolveGroupIdRefs(XmlRegion xmlregion, SecurityRealm securityrealm)
        throws SecurityConfigException
    {
        Set set = xmlregion.getIdentities();
        Iterator iterator = set.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            XmlIdentity xmlidentity = (XmlIdentity)iterator.next();
            if(xmlidentity.isGroup())
            {
                SecurityIdentityImpl securityidentityimpl = securityrealm.getIdentity(xmlidentity.getName());
                if(!$assertionsDisabled && securityidentityimpl == null)
                    throw new AssertionError();
                Set set1 = xmlidentity.getIdRefs();
                if(!$assertionsDisabled && set1 == null)
                    throw new AssertionError();
                Iterator iterator1 = set1.iterator();
                while(iterator1.hasNext())
                {
                    XmlIdRef xmlidref = (XmlIdRef)iterator1.next();
                    SecurityIdentityImpl securityidentityimpl1 = securityrealm.getIdentity(xmlidref.getName());
                    if(securityidentityimpl1 == null)
                        throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR16", xmlidentity, xmlidref));
                    if(xmlidref.isGroup() != securityidentityimpl1.isGroup())
                        throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR17", xmlidentity, xmlidref));
                    securityidentityimpl.addIdentity(securityidentityimpl1);
                }
            }
        } while(true);
    }

    private void checkRecursiveGroups(SecurityRealm securityrealm)
        throws SecurityConfigException
    {
        Iterator iterator = securityrealm.getIdentities().values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            SecurityIdentityImpl securityidentityimpl = (SecurityIdentityImpl)iterator.next();
            if(securityidentityimpl.isGroup())
                testRecursion(securityidentityimpl, new HashSet());
        } while(true);
    }

    private void testRecursion(SecurityIdentityImpl securityidentityimpl, Set set)
        throws SecurityConfigException
    {
        String s = securityidentityimpl.getName();
        if(set.contains(s))
            throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR22", securityidentityimpl));
        set.add(s);
        Iterator iterator = securityidentityimpl.getIdentities().values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            SecurityIdentityImpl securityidentityimpl1 = (SecurityIdentityImpl)iterator.next();
            if(securityidentityimpl1.isGroup())
                testRecursion(securityidentityimpl1, set);
        } while(true);
    }

    private void addResources(XmlRegion xmlregion, SecurityRealm securityrealm)
    {
        Set set = xmlregion.getResources();
        XmlResource xmlresource;
        for(Iterator iterator = set.iterator(); iterator.hasNext(); securityrealm.addResource(new SecurityResourceImpl(xmlresource.getName(), xmlresource.getActions())))
            xmlresource = (XmlResource)iterator.next();

    }

    private void addRoles(XmlRegion xmlregion, SecurityRealm securityrealm)
        throws SecurityConfigException
    {
        Set set = xmlregion.getRoles();
        Iterator iterator = set.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            XmlRole xmlrole = (XmlRole)iterator.next();
            SecurityRoleImpl securityroleimpl = new SecurityRoleImpl(xmlrole.getName());
            Map map = xmlrole.getGrants();
            Iterator iterator1 = map.entrySet().iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                Map.Entry entry = (Map.Entry)iterator1.next();
                SecurityResourceImpl securityresourceimpl = securityrealm.getResource((String)entry.getKey());
                if(securityresourceimpl == null)
                    throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR20", xmlrole, entry.getKey()));
                Set set1 = (Set)entry.getValue();
                Set set2 = securityresourceimpl.getActions();
                for(Iterator iterator3 = set1.iterator(); iterator3.hasNext();)
                {
                    String s1 = (String)iterator3.next();
                    if(!set2.contains(s1))
                        throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR31", securityroleimpl, securityresourceimpl, s1));
                }

                securityroleimpl.grant(securityresourceimpl, set1);
            } while(true);
            SecurityIdentityImpl securityidentityimpl;
            for(Iterator iterator2 = xmlrole.getIdRefs().iterator(); iterator2.hasNext(); securityroleimpl.addIdentity(securityidentityimpl))
            {
                String s = ((XmlIdRef)iterator2.next()).getName();
                securityidentityimpl = securityrealm.getIdentity(s);
                if(securityidentityimpl == null)
                    throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR27", xmlrole, s));
            }

            securityrealm.addRole(securityroleimpl);
        } while(true);
    }

    private void addAcls(XmlRegion xmlregion, SecurityRealm securityrealm)
        throws SecurityConfigException
    {
        Set set = xmlregion.getAcls();
        XmlAcl xmlacl;
        SecurityResourceImpl securityresourceimpl;
        SecurityIdentityImpl securityidentityimpl;
label0:
        for(Iterator iterator = set.iterator(); iterator.hasNext(); securityrealm.addAcl(new SecurityAclImpl(xmlacl.getType(), securityidentityimpl, securityresourceimpl, xmlacl.getAction())))
        {
            xmlacl = (XmlAcl)iterator.next();
            securityresourceimpl = securityrealm.getResource(xmlacl.getResource());
            if(securityresourceimpl == null)
                throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR24", xmlregion, xmlacl));
            securityidentityimpl = securityrealm.getIdentity(xmlacl.getIdentity());
            if(securityidentityimpl == null)
                throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR25", xmlregion, xmlacl));
            if(!securityresourceimpl.getActions().contains(xmlacl.getAction()))
                throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR32", xmlacl));
            if(xmlacl.getType() != 1)
                continue;
            Map map = securityrealm.getRoles();
            Iterator iterator1 = map.values().iterator();
            do
            {
                if(!iterator1.hasNext())
                    continue label0;
                SecurityRoleImpl securityroleimpl = (SecurityRoleImpl)iterator1.next();
                Iterator iterator2 = securityroleimpl.getIds().values().iterator();
                while(iterator2.hasNext())
                {
                    SecurityIdentityImpl securityidentityimpl1 = (SecurityIdentityImpl)iterator2.next();
                    if(securityidentityimpl1.contains(securityidentityimpl) && securityroleimpl.hasAccess(securityresourceimpl.getName(), xmlacl.getAction()))
                        log.warning(L10n.format("SRVC.SCRT.WRN4", xmlacl, securityroleimpl));
                }
            } while(true);
        }

    }

    private void warnUnusedIdentities(SecurityRealm securityrealm)
    {
        Iterator iterator = securityrealm.getIdentities().keySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            boolean flag = false;
            Iterator iterator1 = securityrealm.getRoles().values().iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                if(!securityrealm.isInRole(s, (SecurityRoleImpl)iterator1.next()))
                    continue;
                flag = true;
                break;
            } while(true);
            if(flag)
                continue;
            iterator1 = securityrealm.getAcls().iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                if(!((SecurityAcl)iterator1.next()).getIdentity().getName().equals(s))
                    continue;
                flag = true;
                break;
            } while(true);
            if(!flag)
                log.warning(L10n.format("SRVC.SCRT.WRN2", s));
        } while(true);
    }

    private void warnUnusedActions(SecurityRealm securityrealm)
    {
        for(Iterator iterator = securityrealm.getResources().values().iterator(); iterator.hasNext();)
        {
            SecurityResourceImpl securityresourceimpl = (SecurityResourceImpl)iterator.next();
            String s = securityresourceimpl.getName();
            Iterator iterator1 = securityresourceimpl.getActions().iterator();
            while(iterator1.hasNext())
            {
                String s1 = (String)iterator1.next();
                boolean flag = false;
                Iterator iterator2 = securityrealm.getRoles().values().iterator();
                do
                {
                    if(!iterator2.hasNext())
                        break;
                    if(!((SecurityRoleImpl)iterator2.next()).containsAction(s, s1))
                        continue;
                    flag = true;
                    break;
                } while(true);
                if(!flag)
                {
                    Iterator iterator3 = securityrealm.getAcls().iterator();
                    do
                    {
                        if(!iterator3.hasNext())
                            break;
                        SecurityAcl securityacl = (SecurityAcl)iterator3.next();
                        if(!securityacl.getResource().getName().equals(s) || !securityacl.getAction().equals(s1))
                            continue;
                        flag = true;
                        break;
                    } while(true);
                    if(!flag)
                        log.warning(L10n.format("SRVC.SCRT.WRN3", s, s1));
                }
            }
        }

    }

    private void checkIntersectingAcls(SecurityRealm securityrealm)
        throws SecurityConfigException
    {
        Iterator iterator = securityrealm.getAcls().iterator();
_L2:
        SecurityAclImpl securityaclimpl;
        Iterator iterator1;
        if(!iterator.hasNext())
            break MISSING_BLOCK_LABEL_168;
        securityaclimpl = (SecurityAclImpl)iterator.next();
        iterator1 = securityrealm.getAcls().iterator();
_L4:
        if(!iterator1.hasNext()) goto _L2; else goto _L1
_L1:
        SecurityAclImpl securityaclimpl1 = (SecurityAclImpl)iterator1.next();
        if(securityaclimpl == securityaclimpl1) goto _L4; else goto _L3
_L3:
        SecurityIdentityImpl securityidentityimpl;
        SecurityIdentityImpl securityidentityimpl1;
        securityidentityimpl = securityaclimpl1.getId();
        securityidentityimpl1 = securityaclimpl.getId();
        if(!securityidentityimpl1.getName().equals(securityidentityimpl.getName()) && !securityidentityimpl1.contains(securityidentityimpl) && !securityidentityimpl.contains(securityidentityimpl1) || !securityaclimpl.getResource().equals(securityaclimpl1.getResource()) || !securityaclimpl.getAction().equals(securityaclimpl1.getAction())) goto _L4; else goto _L5
_L5:
        throw new SecurityConfigException(L10n.format("SRVC.SCRT.ERR30", securityaclimpl, securityaclimpl1));
    }

    private SecurityRealm processXmlRegion(XmlRegion xmlregion)
        throws SecurityConfigException
    {
        SecurityRealm securityrealm = new SecurityRealm(xmlregion.getName());
        addIdentities(xmlregion, securityrealm);
        resolveGroupIdRefs(xmlregion, securityrealm);
        checkRecursiveGroups(securityrealm);
        addResources(xmlregion, securityrealm);
        addRoles(xmlregion, securityrealm);
        addAcls(xmlregion, securityrealm);
        warnUnusedIdentities(securityrealm);
        warnUnusedActions(securityrealm);
        checkIntersectingAcls(securityrealm);
        return securityrealm;
    }

    public Map getAllResources()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableMap(realm.getResources());
        Exception exception;
        exception;
        throw exception;
    }

    public Map getAllIdentities()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableMap(realm.getIdentities());
        Exception exception;
        exception;
        throw exception;
    }

    public Map getAllRoles()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableMap(realm.getRoles());
        Exception exception;
        exception;
        throw exception;
    }

    public Set getAllAcls()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableSet(realm.getAcls());
        Exception exception;
        exception;
        throw exception;
    }

    public boolean checkAccess(String s, String s1, String s2)
    {
        ArgAssert.nullArg(s, "id");
        ArgAssert.nullArg(s1, "rs");
        ArgAssert.nullArg(s2, "action");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return realm.isAllowed(s, s1, s2);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean checkAccess(String s, String s1, String as[])
    {
        ArgAssert.nullArg(s, "id");
        ArgAssert.nullArg(s1, "rs");
        ArgAssert.nullArg(as, "actions");
        Object obj = mux;
        JVM INSTR monitorenter ;
        return realm.isAllowed(s, s1, as);
        Exception exception;
        exception;
        throw exception;
    }

    public String getName()
    {
        return "security";
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

    private List listeners;
    private SecurityRealm realm;
    private Logger log;
    private final Object mux = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(SecurityServiceImpl.class).desiredAssertionStatus();
    }

}
