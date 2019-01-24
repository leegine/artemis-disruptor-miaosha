// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SequenceIDGenerator.java

package co.fin.intellioms.util;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.services.db.DbService;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import com.fitechlabs.xtier.utils.Utils;
import java.sql.*;
import javax.sql.DataSource;

// Referenced classes of package com.com.fin.intellioms.util:
//            IDGeneratorException, IDGenerator, Log

public class SequenceIDGenerator
    implements IDGenerator
{

    public SequenceIDGenerator(String sequenceName, String datasourceName, long step)
        throws ObjectPoolException, SQLException
    {
        curValue = 0L;
        topValue = 0L;
        mux = new Object();
        this.sequenceName = sequenceName;
        ds = XtierKernel.getInstance().db().getDs(datasourceName);
        this.step = step;
        log = Log.getLogger((new StringBuilder()).append(com/ com /fin/intellioms/util/SequenceIDGenerator.getName()).append("#").append(sequenceName).toString());
        updateValue();
        if(log.isDebug())
            log.debug((new StringBuilder()).append("Initialized with current value = ").append(curValue).append(" and top value = ").append(topValue).append(".").toString());
    }

    public SequenceIDGenerator(String sequenceName, long step)
        throws ObjectPoolException, SQLException
    {
        this(sequenceName, "rulesys-ds", step);
    }

    public long next()
        throws IDGeneratorException
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        if(curValue == topValue)
        {
            try
            {
                updateValue();
            }
            catch(IllegalStateException e)
            {
                throw new IDGeneratorException(e.getMessage(), e);
            }
            catch(SQLException e)
            {
                throw new IDGeneratorException(e.getMessage(), e);
            }
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Updated to current value = ").append(curValue).append(" and top value = ").append(topValue).append(".").toString());
        }
        return curValue++;
        Exception exception;
        exception;
        throw exception;
    }

    public Long nextLong()
        throws IDGeneratorException
    {
        return new Long(next());
    }

    public long current()
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        return curValue;
        Exception exception;
        exception;
        throw exception;
    }

    private void updateValue()
        throws SQLException
    {
        Object obj = mux;
        JVM INSTR monitorenter ;
        Connection conn;
        Statement st;
        ResultSet rs;
        conn = null;
        st = null;
        rs = null;
        conn = ds.getConnection();
        st = conn.createStatement();
        rs = st.executeQuery((new StringBuilder()).append("SELECT ").append(sequenceName).append(".NEXTVAL FROM DUAL").toString());
        if(rs.next())
        {
            topValue = rs.getLong(1);
            curValue = topValue - step;
        }
        Utils.close(rs);
        Utils.close(st);
        Utils.close(conn);
        break MISSING_BLOCK_LABEL_135;
        Exception exception;
        exception;
        Utils.close(rs);
        Utils.close(st);
        Utils.close(conn);
        throw exception;
        Exception exception1;
        exception1;
        throw exception1;
    }

    private final Log log;
    private final String sequenceName;
    private DataSource ds;
    private long curValue;
    private long topValue;
    private final long step;
    private final Object mux;
}
