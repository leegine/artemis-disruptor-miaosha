// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DbTickersManager.java

package co.fin.intellioms.persist.db;

import co.fin.intellioms.ticker.Ticker;
import co.fin.intellioms.ticker.TickersManager;
import co.fin.intellioms.ticker.TickersManagerException;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.com.fin.intellioms.persist.db:
//            AbstractDbStore

public class DbTickersManager extends AbstractDbStore
    implements TickersManager
{

    public DbTickersManager()
    {
    }

    public List getAllTickers()
        throws TickersManagerException
    {
        List tickers;
        PreparedStatement st;
        ResultSet rs;
        tickers = new ArrayList();
        Connection conn = null;
        st = null;
        rs = null;
        try
        {
            TxManager.begin();
             conn = getConnection();
            st = conn.prepareStatement("SELECT prod_id, market_id FROM rls_ticker");
            for(rs = st.executeQuery(); rs.next(); tickers.add(new Ticker(rs.getLong("prod_id"), rs.getLong("market_id"))));
            TxManager.commit();

            return tickers;
        }
        catch(SQLException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                throw new TickersManagerException(e1.getMessage(), e1);
            }
            throw new TickersManagerException(e.getMessage(), e);
        }
        catch(TxManagerException e)
        {
            try
            {
                TxManager.rollback();
            }
            catch(TxManagerException e1)
            {
                throw new TickersManagerException(e1.getMessage(), e1);
            }
            throw new TickersManagerException(e.getMessage(), e);
        }finally {
            close(rs);
            close(st);
        }


    }
}
