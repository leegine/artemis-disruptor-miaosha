// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DbAccountsManager.java

package co.fin.intellioms.account.impl;

import co.fin.intellioms.account.AccountException;
import co.fin.intellioms.account.AccountIDRange;
import co.fin.intellioms.account.AccountsManager;
import co.fin.intellioms.persist.db.AbstractDbStore;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbAccountsManager extends AbstractDbStore
    implements AccountsManager
{

    public DbAccountsManager()
    {
    }

    public List getAllAccountIds()
        throws AccountException
    {
        List result;
        PreparedStatement st;
        ResultSet rs;
        result = new ArrayList();
        Connection conn = null;
        st = null;
        rs = null;
        try
        {
            TxManager.begin();
             conn = getConnection();
            st = conn.prepareStatement("SELECT id FROM rls_account ORDER BY id");
            for(rs = st.executeQuery(); rs.next(); result.add(new Long(rs.getLong("id"))));
            TxManager.commit();
            return result;
        }
        catch(SQLException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                throw new AccountException(e1.getMessage(), e1);
            }
            throw new AccountException(e.getMessage(), e);
        }
        catch(TxManagerException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                throw new AccountException(e1.getMessage(), e1);
            }
            throw new AccountException(e.getMessage(), e);
        }finally {
            close(rs);
            close(st);

        }


    }

    public AccountIDRange getAccountsCount()
        throws AccountException
    {
        AccountIDRange result;
        PreparedStatement st;
        ResultSet rs;
        result = null;
        Connection conn = null;
        st = null;
        rs = null;
        try
        {
            TxManager.begin();
             conn = getConnection();
            st = conn.prepareStatement("SELECT min(id) min_acc_id, max(id) max_acc_id FROM rls_account");
            rs = st.executeQuery();
            if(rs.next())
                result = new AccountIDRange(rs.getLong("min_acc_id"), rs.getLong("max_acc_id"));
            TxManager.commit();
            if(result == null)
                throw new AccountException("Unable to retrieve accounts information: 'RLS_ACCOUNT' table is empty.");
            else
                return result;
        }
        catch(SQLException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                throw new AccountException(e1.getMessage(), e1);
            }
            throw new AccountException(e.getMessage(), e);
        }
        catch(TxManagerException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                throw new AccountException(e1.getMessage(), e1);
            }
            throw new AccountException(e.getMessage(), e);
        }finally {
            close(rs);
            close(st);
        }

    }
}
