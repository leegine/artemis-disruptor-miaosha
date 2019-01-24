// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.db.impl;

import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.db.DbException;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.db.impl:
//            DbManager

class DbPredefinedStatement
{

    DbPredefinedStatement(String s, String s1, int i)
    {
        jdbcSql = null;
        params = null;
        dbMgr = DbManager.getInstance();
        if(!$assertionsDisabled && (s == null || s1 == null || s1.length() == 0))
            throw new AssertionError();
        if(!$assertionsDisabled && i != 1 && i != 2 && i != 3 && i != 4 && i != 5)
        {
            throw new AssertionError();
        } else
        {
            name = s;
            userSql = s1;
            type = i;
            return;
        }
    }

    String getName()
    {
        return name;
    }

    String getUserSql()
    {
        return userSql;
    }

    void setSelectOut(String s, IocDescriptor iocdescriptor)
    {
        if(!$assertionsDisabled && type != 1)
            throw new AssertionError();
        if(!$assertionsDisabled && s != null && iocdescriptor != null)
        {
            throw new AssertionError();
        } else
        {
            resClass = s;
            resIoc = iocdescriptor;
            return;
        }
    }

    String getResClass()
    {
        if(!$assertionsDisabled && type != 1)
            throw new AssertionError();
        else
            return resClass;
    }

    IocDescriptor getResIoc()
    {
        if(!$assertionsDisabled && type != 1)
            throw new AssertionError();
        else
            return resIoc;
    }

    void setAutogenKeys(String as[], int ai[])
    {
        if(!$assertionsDisabled && type != 2)
            throw new AssertionError();
        if(!$assertionsDisabled && as != null && ai != null)
        {
            throw new AssertionError();
        } else
        {
            autogenKeys = as;
            autogenKeysIndexes = ai;
            return;
        }
    }

    int[] getAutogenKeysIndexes()
    {
        if(!$assertionsDisabled && type != 2)
            throw new AssertionError();
        else
            return autogenKeysIndexes;
    }

    String[] getAutogenKeys()
    {
        if(!$assertionsDisabled && type != 2)
            throw new AssertionError();
        else
            return autogenKeys;
    }

    int getType()
    {
        return type;
    }

    void init()
        throws DbException
    {
        synchronized(mutex)
        {
            if(jdbcSql == null)
            {
                StringBuffer stringbuffer = new StringBuffer();
                int i = userSql.length();
                byte byte0 = 100;
                StringBuffer stringbuffer1 = null;
                for(int j = 0; j < i; j++)
                {
                    char c = userSql.charAt(j);
                    switch(byte0)
                    {
                    default:
                        break;

                    case 100: // 'd'
                        if(c == '?')
                            byte0 = 101;
                        else
                            stringbuffer.append(c);
                        break;

                    case 101: // 'e'
                        if(c == '{')
                        {
                            if(stringbuffer1 == null)
                                stringbuffer1 = new StringBuffer();
                            else
                                stringbuffer1.setLength(0);
                            byte0 = 102;
                            break;
                        }
                        if(c == '?')
                        {
                            stringbuffer.append(c);
                            byte0 = 100;
                        } else
                        {
                            stringbuffer.append('?');
                            stringbuffer.append(c);
                            byte0 = 100;
                        }
                        break;

                    case 102: // 'f'
                        if(c == '}')
                        {
                            byte0 = 100;
                            if(stringbuffer1.length() == 0)
                                throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR53", userSql));
                            if(params == null)
                                params = new ArrayList();
                            params.add(stringbuffer1.toString());
                            stringbuffer1.setLength(0);
                            stringbuffer.append('?');
                            break;
                        }
                        if(!Character.isJavaIdentifierPart(c))
                            throw dbMgr.newDbException(L10n.format("SRVC.DB.ERR53", userSql));
                        stringbuffer1.append(c);
                        break;
                    }
                }

                jdbcSql = stringbuffer.toString();
            }
        }
    }

    String getJdbcSql()
        throws DbException
    {
        init();
        return jdbcSql;
    }

    List getParams()
        throws DbException
    {
        init();
        return params;
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

    static final int SELECT_STATEMENT = 1;
    static final int INSERT_STATEMENT = 2;
    static final int UPDATE_STATEMENT = 3;
    static final int DELETE_STATEMENT = 4;
    static final int CALL_STATEMENT = 5;
    private static final int PARSE_STATE_STMT = 100;
    private static final int PARSE_STATE_QS = 101;
    private static final int PARSE_STATE_PARAM = 102;
    private String name;
    private String userSql;
    private String resClass;
    private IocDescriptor resIoc;
    private String autogenKeys[];
    private int autogenKeysIndexes[];
    private int type;
    private String jdbcSql;
    private List params;
    private DbManager dbMgr;
    private final Object mutex = new Object();
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(DbPredefinedStatement.class).desiredAssertionStatus();
    }
}
