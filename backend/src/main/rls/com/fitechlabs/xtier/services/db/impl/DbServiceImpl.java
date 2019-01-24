// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.*;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbDataSourceProps, DbPredefinedStatement, DbManager, DbSqlManager

public class DbServiceImpl extends ServiceProviderAdapter
    implements DbService
{
    private class XmlDatabase
    {

        String getName()
        {
            return name;
        }

        void setBadSqlErrCodes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                badSqlErrCodes = ai;
                return;
            }
        }

        int[] getBadSqlErrCodes()
        {
            return badSqlErrCodes;
        }

        void setIntegrityErrCodes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                integrityErrCodes = ai;
                return;
            }
        }

        int[] getIntegrityErrCodes()
        {
            return integrityErrCodes;
        }

        void setDbNotAvailErrCodes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                dbNotAvailErrCodes = ai;
                return;
            }
        }

        int[] getDbNotAvailErrCodes()
        {
            return dbNotAvailErrCodes;
        }

        void setOptLockFailErrCodes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                optLockFailErrCodes = ai;
                return;
            }
        }

        int[] getOptLockFailErrCodes()
        {
            return optLockFailErrCodes;
        }

        void setNoDataFoundErrCodes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                noDataFoundErrCodes = ai;
                return;
            }
        }

        int[] getNoDataFoundErrCodes()
        {
            return noDataFoundErrCodes;
        }

        void setTooManyRowsErrCodes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                tooManyRowsErrCodes = ai;
                return;
            }
        }

        int[] getTooManyRowsErrCodes()
        {
            return tooManyRowsErrCodes;
        }

        private String name;
        private int badSqlErrCodes[];
        private int integrityErrCodes[];
        private int dbNotAvailErrCodes[];
        private int optLockFailErrCodes[];
        private int noDataFoundErrCodes[];
        private int tooManyRowsErrCodes[];
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlDatabase(String s)
        {
            super();
            badSqlErrCodes = null;
            integrityErrCodes = null;
            dbNotAvailErrCodes = null;
            optLockFailErrCodes = null;
            noDataFoundErrCodes = null;
            tooManyRowsErrCodes = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                return;
            }
        }
    }

    private class XmlInsert
    {

        void setAutogenKeys(String as[])
        {
            if(!$assertionsDisabled && as == null)
            {
                throw new AssertionError();
            } else
            {
                autogenKeys = as;
                return;
            }
        }

        void setAutogenKeysIndexes(int ai[])
        {
            if(!$assertionsDisabled && ai == null)
            {
                throw new AssertionError();
            } else
            {
                autogenKeysIndexes = ai;
                return;
            }
        }

        String[] getAutogenKeys()
        {
            return autogenKeys;
        }

        int[] getAutogenKeysIndexes()
        {
            return autogenKeysIndexes;
        }

        String getName()
        {
            return name;
        }

        void setSql(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                sql = s;
                return;
            }
        }

        String getSql()
        {
            if(!$assertionsDisabled && sql == null)
                throw new AssertionError();
            else
                return sql;
        }

        private String name;
        private String sql;
        private String autogenKeys[];
        private int autogenKeysIndexes[];
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlInsert(String s)
        {
            super();
            autogenKeys = null;
            autogenKeysIndexes = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                return;
            }
        }
    }

    private class XmlSelect
    {

        void setResultClass(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                resClass = s;
                return;
            }
        }

        String getResultClass()
        {
            return resClass;
        }

        void setResultIoc(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                resIoc = iocdescriptor;
                return;
            }
        }

        IocDescriptor getResultIoc()
        {
            return resIoc;
        }

        String getName()
        {
            return name;
        }

        void setSql(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                sql = s;
                return;
            }
        }

        String getSql()
        {
            if(!$assertionsDisabled && sql == null)
                throw new AssertionError();
            else
                return sql;
        }

        private String name;
        private String sql;
        private String resClass;
        private IocDescriptor resIoc;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlSelect(String s)
        {
            super();
            resClass = null;
            resIoc = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                return;
            }
        }
    }

    private class XmlDataSource
    {

        String getName()
        {
            return name;
        }

        String getDbName()
        {
            return dbName;
        }

        void setUser(String s)
        {
            user = s;
        }

        String getUser()
        {
            return user;
        }

        void setPassword(String s)
        {
            passwd = s;
        }

        String getPassword()
        {
            return passwd;
        }

        void setLogWarns(boolean flag)
        {
            logWarns = flag;
        }

        boolean isLogWarns()
        {
            return logWarns;
        }

        void setLogStmts(boolean flag)
        {
            logStmts = flag;
        }

        boolean isLogStmts()
        {
            return logStmts;
        }

        void setJndiDsName(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                jndiDsName = s;
                return;
            }
        }

        String getJndiDsName()
        {
            return jndiDsName;
        }

        void setIocDs(IocDescriptor iocdescriptor)
        {
            if(!$assertionsDisabled && iocdescriptor == null)
            {
                throw new AssertionError();
            } else
            {
                iocDs = iocdescriptor;
                return;
            }
        }

        IocDescriptor getIocDs()
        {
            return iocDs;
        }

        void setRaiseWarns(boolean flag)
        {
            raiseWarns = flag;
        }

        boolean isRaiseWarns()
        {
            return raiseWarns;
        }

        private String name;
        private String dbName;
        private String user;
        private String passwd;
        private boolean logWarns;
        private boolean logStmts;
        private boolean raiseWarns;
        private String jndiDsName;
        private IocDescriptor iocDs;
        static final boolean $assertionsDisabled; /* synthetic field */


        public XmlDataSource(String s, String s1)
        {
            super();
            user = null;
            passwd = null;
            jndiDsName = null;
            iocDs = null;
            if(!$assertionsDisabled && (s == null || s1 == null))
            {
                throw new AssertionError();
            } else
            {
                name = s;
                dbName = s1;
                return;
            }
        }
    }

    private class XmlRegion
    {

        String getName()
        {
            return name;
        }

        void addDs(XmlDataSource xmldatasource)
        {
            if(!$assertionsDisabled && xmldatasource == null)
            {
                throw new AssertionError();
            } else
            {
                dss.put(xmldatasource.getName(), xmldatasource);
                return;
            }
        }

        boolean hasDs(String s)
        {
            return dss.containsKey(s);
        }

        Iterator getDss()
        {
            return dss.values().iterator();
        }

        void addSelect(XmlSelect xmlselect)
        {
            if(!$assertionsDisabled && xmlselect == null)
            {
                throw new AssertionError();
            } else
            {
                selects.put(xmlselect.getName(), xmlselect);
                return;
            }
        }

        Map getSelects()
        {
            return selects;
        }

        void addInsert(XmlInsert xmlinsert)
        {
            if(!$assertionsDisabled && xmlinsert == null)
            {
                throw new AssertionError();
            } else
            {
                inserts.put(xmlinsert.getName(), xmlinsert);
                return;
            }
        }

        Map getInserts()
        {
            return inserts;
        }

        void addDelete(String s, String s1)
        {
            if(!$assertionsDisabled && (s == null || s1 == null))
            {
                throw new AssertionError();
            } else
            {
                deletes.put(s, s1);
                return;
            }
        }

        Map getDeletes()
        {
            return deletes;
        }

        void addUpdate(String s, String s1)
        {
            if(!$assertionsDisabled && (s == null || s1 == null))
            {
                throw new AssertionError();
            } else
            {
                updates.put(s, s1);
                return;
            }
        }

        Map getUpdates()
        {
            return updates;
        }

        void addCall(String s, String s1)
        {
            if(!$assertionsDisabled && (s == null || s1 == null))
            {
                throw new AssertionError();
            } else
            {
                calls.put(s, s1);
                return;
            }
        }

        Map getCalls()
        {
            return calls;
        }

        boolean hasStmt(String s)
        {
            return selects.containsKey(s) || inserts.containsKey(s) || updates.containsKey(s) || deletes.containsKey(s) || calls.containsKey(s);
        }

        private String name;
        private Map dss;
        private Map selects;
        private Map deletes;
        private Map updates;
        private Map inserts;
        private Map calls;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s)
        {
            super();
            dss = new HashMap();
            selects = new HashMap();
            deletes = new HashMap();
            updates = new HashMap();
            inserts = new HashMap();
            calls = new HashMap();
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                return;
            }
        }
    }


    public DbServiceImpl()
    {
        dbMgr = null;
        sqlMgr = null;
    }

    public String getName()
    {
        return "db";
    }

    protected void onStart()
        throws ServiceProviderException
    {
        logger = XtierKernel.getInstance().log().getLogger("db");
        dbMgr = DbManager.getInstance();
        HashMap hashmap = new HashMap();
        HashMap hashmap1 = new HashMap();
        try
        {
            parseXmlConfig("xtier_db.xml", hashmap, new HashSet(), hashmap1);
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.DB.ERR1", saxexception.getMessage()), saxexception);
        }
        XmlRegion xmlregion = (XmlRegion)hashmap.get(getRegionName());
        if(xmlregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.DB.ERR14", getRegionName()));
        XmlDatabase xmldatabase;
        for(Iterator iterator = hashmap1.keySet().iterator(); iterator.hasNext(); dbMgr.addPredefinedDatabase(xmldatabase.getName(), xmldatabase.getBadSqlErrCodes(), xmldatabase.getIntegrityErrCodes(), xmldatabase.getDbNotAvailErrCodes(), xmldatabase.getOptLockFailErrCodes(), xmldatabase.getNoDataFoundErrCodes(), xmldatabase.getTooManyRowsErrCodes()))
            xmldatabase = (XmlDatabase)hashmap1.get(iterator.next());

        XmlDataSource xmldatasource;
        DbDataSourceProps dbdatasourceprops;
        for(Iterator iterator1 = xmlregion.getDss(); iterator1.hasNext(); dbMgr.addDsHolder(xmldatasource.getName(), xmldatasource.getIocDs(), xmldatasource.getJndiDsName(), dbdatasourceprops))
        {
            xmldatasource = (XmlDataSource)iterator1.next();
            dbdatasourceprops = new DbDataSourceProps(xmldatasource.getName(), xmldatasource.getDbName());
            dbdatasourceprops.setUser(xmldatasource.getUser());
            dbdatasourceprops.setPassword(xmldatasource.getPassword());
            dbdatasourceprops.setLogStmts(xmldatasource.isLogStmts());
            dbdatasourceprops.setLogWarns(xmldatasource.isLogWarns());
            dbdatasourceprops.setRaiseWarns(xmldatasource.isRaiseWarns());
        }

        sqlMgr = DbSqlManager.getInstance();
        DbPredefinedStatement dbpredefinedstatement;
        for(Iterator iterator2 = xmlregion.getSelects().values().iterator(); iterator2.hasNext(); sqlMgr.addDbPredefinedStmt(dbpredefinedstatement))
        {
            XmlSelect xmlselect = (XmlSelect)iterator2.next();
            dbpredefinedstatement = new DbPredefinedStatement(xmlselect.getName(), xmlselect.getSql(), 1);
            dbpredefinedstatement.setSelectOut(xmlselect.getResultClass(), xmlselect.getResultIoc());
        }

        DbPredefinedStatement dbpredefinedstatement1;
        for(Iterator iterator3 = xmlregion.getInserts().values().iterator(); iterator3.hasNext(); sqlMgr.addDbPredefinedStmt(dbpredefinedstatement1))
        {
            XmlInsert xmlinsert = (XmlInsert)iterator3.next();
            dbpredefinedstatement1 = new DbPredefinedStatement(xmlinsert.getName(), xmlinsert.getSql(), 2);
            dbpredefinedstatement1.setAutogenKeys(xmlinsert.getAutogenKeys(), xmlinsert.getAutogenKeysIndexes());
        }

        setStmts(xmlregion.getUpdates(), 3);
        setStmts(xmlregion.getDeletes(), 4);
        setStmts(xmlregion.getCalls(), 5);
    }

    private void setStmts(Map map, int i)
    {
        if(!$assertionsDisabled && sqlMgr == null)
            throw new AssertionError();
        if(!$assertionsDisabled && i != 3 && i != 4 && i != 5)
            throw new AssertionError();
        String s;
        for(Iterator iterator = map.keySet().iterator(); iterator.hasNext(); sqlMgr.addDbPredefinedStmt(new DbPredefinedStatement(s, (String)map.get(s), i)))
            s = (String)iterator.next();

    }

    protected void onStop()
        throws ServiceProviderException
    {
        dbMgr.close();
        sqlMgr.close();
    }

    private void parseXmlConfig(String s, Map map, Set set, Map map1)
        throws SAXException
    {
        if(!$assertionsDisabled && (s == null || map == null || set == null))
            throw new AssertionError();
        if(set.contains(s))
        {
            logger.warning(L10n.format("SRVC.DB.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(set, map1) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), regions, includes, dbs);
                    else
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR5", s3));
                        xmlRegion = new XmlRegion(s3);
                        regions.put(s3, xmlRegion);
                    } else
                    if(s2.equals("ds"))
                        dsProp = new XmlDataSource(xmlattrinterceptor.getValue("name"), xmlattrinterceptor.getValue("db-type"));
                    else
                    if(s2.equals("db"))
                    {
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(dbs.containsKey(s4))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR6", s4));
                        db = new XmlDatabase(xmlattrinterceptor.getValue("name"));
                    } else
                    if(s2.equals("jndi-ds"))
                        dsProp.setJndiDsName(xmlattrinterceptor.getValue("name"));
                    else
                    if(s2.equals("ioc-ds"))
                        isIocDs = true;
                    else
                    if(s2.equals("config"))
                    {
                        dsProp.setUser(xmlattrinterceptor.getValue("username"));
                        dsProp.setPassword(xmlattrinterceptor.getValue("password"));
                        dsProp.setLogStmts(parseBoolean(xmlattrinterceptor.getValue("log-stmts")));
                        dsProp.setLogWarns(parseBoolean(xmlattrinterceptor.getValue("log-warns")));
                        dsProp.setRaiseWarns(parseBoolean(xmlattrinterceptor.getValue("raise-warns")));
                    } else
                    if(s2.equals("ioc-result"))
                        isIocResult = true;
                    else
                    if(s2.equals("select"))
                    {
                        if(!$assertionsDisabled && (xmlRegion == null || stmtName != null))
                            throw new AssertionError();
                        stmtName = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasStmt(stmtName))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR7", xmlRegion.getName(), stmtName, "select"));
                        select = new XmlSelect(stmtName);
                        String s5 = xmlattrinterceptor.getValue("result-class");
                        if(s5 != null && s5.length() != 0)
                            select.setResultClass(s5.trim());
                    } else
                    if(s2.equals("delete"))
                    {
                        if(!$assertionsDisabled && (xmlRegion == null || stmtName != null))
                            throw new AssertionError();
                        stmtName = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasStmt(stmtName))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR7", xmlRegion.getName(), stmtName, "delete"));
                    } else
                    if(s2.equals("update"))
                    {
                        if(!$assertionsDisabled && (xmlRegion == null || stmtName != null))
                            throw new AssertionError();
                        stmtName = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasStmt(stmtName))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR7", xmlRegion.getName(), stmtName, "update"));
                    } else
                    if(s2.equals("insert"))
                    {
                        if(!$assertionsDisabled && (xmlRegion == null || stmtName != null))
                            throw new AssertionError();
                        stmtName = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasStmt(stmtName))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR7", xmlRegion.getName(), stmtName, "insert"));
                        insert = new XmlInsert(stmtName);
                        String s6 = xmlattrinterceptor.getValue("autogen-keys-columns");
                        if(s6 != null)
                            if(s6.trim().length() != 0)
                                insert.setAutogenKeys(parseStringList(s6));
                            else
                                throw createSaxErr(L10n.format("SRVC.DB.ERR8", xmlRegion.getName(), stmtName));
                        String s7 = xmlattrinterceptor.getValue("autogen-keys-indexes");
                        if(s6 != null && s7 != null)
                            throw createSaxErr(L10n.format("SRVC.DB.ERR9", xmlRegion.getName(), stmtName));
                        if(s7 != null)
                            if(s6.trim().length() != 0)
                                insert.setAutogenKeysIndexes(parseIntList(s7));
                            else
                                throw createSaxErr(L10n.format("SRVC.DB.ERR10", xmlRegion.getName(), stmtName));
                    } else
                    if(s2.equals("sp-call"))
                    {
                        if(!$assertionsDisabled && (xmlRegion == null || stmtName != null))
                            throw new AssertionError();
                        stmtName = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasStmt(stmtName))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR7", xmlRegion.getName(), stmtName, "sp-call"));
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                    throws SAXException
                {
                    if(isIocDs)
                    {
                        if(!$assertionsDisabled && dsProp == null)
                            throw new AssertionError();
                        dsProp.setIocDs(iocdescriptor);
                        isIocDs = false;
                    } else
                    if(isIocResult)
                    {
                        if(!$assertionsDisabled && select == null)
                            throw new AssertionError();
                        select.setResultIoc(iocdescriptor);
                        isIocResult = false;
                    }
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                        xmlRegion = null;
                    else
                    if(s2.equals("bad-sql"))
                    {
                        if(!$assertionsDisabled && db == null)
                            throw new AssertionError();
                        String s3 = getPcdata().trim();
                        if(s3 != null && s3.length() != 0)
                            db.setBadSqlErrCodes(parseIntList(s3));
                    } else
                    if(s2.equals("data-integrity"))
                    {
                        if(!$assertionsDisabled && db == null)
                            throw new AssertionError();
                        String s4 = getPcdata().trim();
                        if(s4 != null && s4.length() != 0)
                            db.setIntegrityErrCodes(parseIntList(s4));
                    } else
                    if(s2.equals("opt-lock-failure"))
                    {
                        if(!$assertionsDisabled && db == null)
                            throw new AssertionError();
                        String s5 = getPcdata().trim();
                        if(s5 != null && s5.length() != 0)
                            db.setOptLockFailErrCodes(parseIntList(s5));
                    } else
                    if(s2.equals("too-many-rows"))
                    {
                        if(!$assertionsDisabled && db == null)
                            throw new AssertionError();
                        String s6 = getPcdata().trim();
                        if(s6 != null && s6.length() != 0)
                            db.setTooManyRowsErrCodes(parseIntList(s6));
                    } else
                    if(s2.equals("no-data-found"))
                    {
                        if(!$assertionsDisabled && db == null)
                            throw new AssertionError();
                        String s7 = getPcdata().trim();
                        if(s7 != null && s7.length() != 0)
                            db.setNoDataFoundErrCodes(parseIntList(s7));
                    } else
                    if(s2.equals("db-not-avail"))
                    {
                        if(!$assertionsDisabled && db == null)
                            throw new AssertionError();
                        String s8 = getPcdata().trim();
                        if(s8 != null && s8.length() != 0)
                            db.setDbNotAvailErrCodes(parseIntList(s8));
                    } else
                    if(s2.equals("db"))
                    {
                        if(!$assertionsDisabled && (dbs == null || db == null))
                            throw new AssertionError();
                        dbs.put(db.getName(), db);
                        db = null;
                    } else
                    if(s2.equals("ds"))
                    {
                        if(!$assertionsDisabled && (xmlRegion == null || dsProp == null))
                            throw new AssertionError();
                        if(xmlRegion.hasDs(dsProp.getName()))
                            throw createSaxErr(L10n.format("SRVC.DB.ERR11", xmlRegion.getName(), dsProp.getName()));
                        xmlRegion.addDs(dsProp);
                        dsProp = null;
                    } else
                    if(s2.equals("select"))
                    {
                        if(!$assertionsDisabled && (select == null || xmlRegion == null || sqlStr == null))
                            throw new AssertionError();
                        select.setSql(sqlStr);
                        xmlRegion.addSelect(select);
                        sqlStr = null;
                        select = null;
                        stmtName = null;
                    } else
                    if(s2.equals("insert"))
                    {
                        if(!$assertionsDisabled && (insert == null || xmlRegion == null || sqlStr == null))
                            throw new AssertionError();
                        insert.setSql(sqlStr);
                        xmlRegion.addInsert(insert);
                        sqlStr = null;
                        insert = null;
                        stmtName = null;
                    } else
                    if(s2.equals("update"))
                    {
                        if(!$assertionsDisabled && (stmtName == null || xmlRegion == null || sqlStr == null))
                            throw new AssertionError();
                        xmlRegion.addUpdate(stmtName, sqlStr);
                        sqlStr = null;
                        stmtName = null;
                    } else
                    if(s2.equals("delete"))
                    {
                        if(!$assertionsDisabled && (stmtName == null || xmlRegion == null || sqlStr == null))
                            throw new AssertionError();
                        xmlRegion.addDelete(stmtName, sqlStr);
                        sqlStr = null;
                        stmtName = null;
                    } else
                    if(s2.equals("sp-call"))
                    {
                        if(!$assertionsDisabled && (stmtName == null || xmlRegion == null || sqlStr == null))
                            throw new AssertionError();
                        xmlRegion.addCall(stmtName, sqlStr);
                        sqlStr = null;
                        stmtName = null;
                    } else
                    if(s2.equals("sql"))
                    {
                        if(!$assertionsDisabled && (stmtName == null || xmlRegion == null))
                            throw new AssertionError();
                        sqlStr = getPcdata().trim();
                        if(sqlStr.length() == 0)
                            throw createSaxErr(L10n.format("SRVC.DB.ERR12", xmlRegion.getName(), stmtName));
                    }
                }

                private String[] parseStringList(String s2)
                {
                    if(!$assertionsDisabled && s2 == null)
                        throw new AssertionError();
                    String as[] = s2.split(",");
                    for(int i = 0; i < as.length; i++)
                        as[i] = as[i].trim();

                    return as;
                }

                private int[] parseIntList(String s2)
                    throws SAXParseException
                {
                    if(!$assertionsDisabled && s2 == null)
                        throw new AssertionError();
                    String as[] = s2.split(",");
                    int ai[] = new int[as.length];
                    for(int i = 0; i < as.length; i++)
                        ai[i] = parseInt(as[i].trim());

                    return ai;
                }

                private XmlRegion xmlRegion;
                private XmlDataSource dsProp;
                private boolean isIocDs;
                private String stmtName;
                private String sqlStr;
                private XmlSelect select;
                private XmlInsert insert;
                private boolean isIocResult;
                private XmlDatabase db;
                static final boolean $assertionsDisabled; /* synthetic field */



                throws SAXException
            {
                super(final_s, final_s1);
                xmlRegion = null;
                dsProp = null;
                isIocDs = false;
                stmtName = null;
                sqlStr = null;
                select = null;
                insert = null;
                isIocResult = false;
                db = null;
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
            throw new SAXException(L10n.format("SRVC.DB.ERR13", s1), ioexception);
        }
    }

    public void addDs(String s, DataSource datasource, String s1)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(datasource, "ds");
        ArgAssert.nullArg(s1, "dbType");
        dbMgr.addDs(s, datasource, new DbDataSourceProps(s, s1));
    }

    public void addDs(String s, XADataSource xadatasource, String s1)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(xadatasource, "ds");
        ArgAssert.nullArg(s1, "dbType");
        dbMgr.addXaDs(s, xadatasource, new DbDataSourceProps(s, s1));
    }

    public void addDs(String s, DataSource datasource, String s1, String s2, String s3, boolean flag, boolean flag1,
            boolean flag2)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(datasource, "ds");
        ArgAssert.nullArg(s1, "dbType");
        ArgAssert.illegalArg(s2 != null || s3 == null, "user");
        DbDataSourceProps dbdatasourceprops = new DbDataSourceProps(s, s1);
        dbdatasourceprops.setUser(s2);
        dbdatasourceprops.setPassword(s3);
        dbdatasourceprops.setLogWarns(flag);
        dbdatasourceprops.setLogStmts(flag1);
        dbdatasourceprops.setRaiseWarns(flag2);
        dbMgr.addDs(s, datasource, dbdatasourceprops);
    }

    public void addDs(String s, XADataSource xadatasource, String s1, String s2, String s3, boolean flag, boolean flag1,
            boolean flag2)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(xadatasource, "ds");
        ArgAssert.nullArg(s1, "dbType");
        ArgAssert.illegalArg(s2 != null || s3 == null, "user");
        DbDataSourceProps dbdatasourceprops = new DbDataSourceProps(s, s1);
        dbdatasourceprops.setUser(s2);
        dbdatasourceprops.setPassword(s3);
        dbdatasourceprops.setLogWarns(flag);
        dbdatasourceprops.setLogStmts(flag1);
        dbdatasourceprops.setRaiseWarns(flag2);
        dbMgr.addXaDs(s, xadatasource, dbdatasourceprops);
    }

    public void changeDsProps(String s, String s1, String s2, boolean flag, boolean flag1, boolean flag2)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.illegalArg(s1 != null || s2 == null, "user");
        dbMgr.changeDsProps(s, s1, s2, flag, flag1, flag2);
    }

    public DataSource getDs(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.getDs(s);
    }

    public XADataSource getXaDs(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "xaDsName");
        return dbMgr.getXaDs(s);
    }

    public boolean removeDs(String s)
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.removeDs(s);
    }

    public void removeAllDss()
    {
        dbMgr.removeAllDss();
    }

    public Set getAllDss()
        throws DbException
    {
        return dbMgr.getAllDss();
    }

    public DbSession createSession(String s, String s1, int i, int j, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.nullArg(s1, "dsName");
        ArgAssert.illegalArg(i == 1 || i == 2 || i == 3 || i == 4 || i == 5, "txIsoLevel");
        ArgAssert.illegalArg(j == 101 || j == 102, "txCmtType");
        return dbMgr.createSession(s, s1, i, j, flag);
    }

    public DbSession createSession(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.createSession(s);
    }

    public DbJdbc3Session createJdbc3Session(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.createJdbc3Session(s);
    }

    public DbJdbc3Session createJdbc3Session(String s, String s1, int i, int j, boolean flag)
        throws DbException
    {
        ArgAssert.nullArg(s1, "dsName");
        return dbMgr.createJdbc3Session(s, s1, i, j, flag);
    }

    public DbXaSession createXaSession(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.createXaSession(s);
    }

    public DbJdbc3XaSession createJdbc3XaSession(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.createJdbc3XaSession(s);
    }

    public DbSession getSession(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.getSession(s);
    }

    public DbJdbc3Session getJdbc3Session(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.getJdbc3Session(s);
    }

    public DbXaSession getXaSession(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.getXaSession(s);
    }

    public DbJdbc3XaSession getJdbc3XaSession(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "dsName");
        return dbMgr.getJdbc3XaSession(s);
    }

    public Set getAllSessions()
    {
        return dbMgr.getAllSessions();
    }

    public Set getAllXaSessions()
    {
        return dbMgr.getAllXaSessions();
    }

    public void close(ResultSet resultset)
    {
        Utils.close(resultset);
    }

    public void close(Connection connection)
    {
        Utils.close(connection);
    }

    public void close(PooledConnection pooledconnection)
    {
        Utils.close(pooledconnection);
    }

    public void close(Statement statement)
    {
        Utils.close(statement);
    }

    public String getSqlCall(String s, int i, boolean flag)
    {
        ArgAssert.nullArg(s, "name");
        ArgAssert.illegalRange(i >= 0, "params", "params >= 0");
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(!flag ? "{ ? =" : "{").append(" call ").append(s).append('(');
        for(int j = 0; j < i; j++)
            stringbuffer.append(j != i - 1 ? "?," : "?");

        return stringbuffer.append(")}").toString();
    }

    public void setNvl(PreparedStatement preparedstatement, int i, Object obj)
        throws DbException
    {
        ArgAssert.nullArg(preparedstatement, "stmt");
        ArgAssert.illegalRange(i >= 0, "index", "index >= 0");
        dbMgr.setNvl(preparedstatement, i, obj);
    }

    public String escapeSql(String s)
    {
        ArgAssert.nullArg(s, "sql");
        char ac[] = s.toCharArray();
        char ac1[] = new char[ac.length * 2];
        int i = 0;
        for(int j = 0; j < ac.length; j++)
            if((ac1[i++] = ac[j]) == '\'')
                ac1[i++] = '\'';

        return new String(ac1, 0, i);
    }

    public Set getSupportedDbs()
    {
        return dbMgr.getSupportedDbs();
    }

    public String getDsDbType(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.getDsDbType(s);
    }

    public String getDsUser(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.getDsUser(s);
    }

    public boolean isDsLogWarns(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.isDsLogWarns(s);
    }

    public boolean isDsLogStmts(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.isDsLogStmts(s);
    }

    public boolean isDsRaiseWarns(String s)
        throws DbException
    {
        ArgAssert.nullArg(s, "name");
        return dbMgr.isDsRaiseWarns(s);
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

    private DbManager dbMgr;
    private DbSqlManager sqlMgr;
    private Logger logger;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbServiceImpl.class).desiredAssertionStatus();
    }

}
