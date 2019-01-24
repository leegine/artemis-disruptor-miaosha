// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DbStore.java

package co.fin.intellioms.persist.db;

import co.fin.intellioms.exclusive.ExclusiveCondOrder;
import co.fin.intellioms.rulesys.CondOrder;
import co.fin.intellioms.rulesys.CondOrderOperation;
import co.fin.intellioms.rulesys.Ratio;
import co.fin.intellioms.time.TimeCondOrder;
import co.fin.intellioms.tx.TxManager;
import co.fin.intellioms.tx.TxManagerException;
import co.fin.intellioms.util.IDGenerator;
import co.fin.intellioms.chain.ChainCondOrder;
import co.fin.intellioms.chain.ChainCondition;
import co.fin.intellioms.enums.CondOrderOpType;
import co.fin.intellioms.enums.CondOrderState;
import co.fin.intellioms.enums.CondOrderType;
import co.fin.intellioms.enums.EventType;
import co.fin.intellioms.enums.OrderExecType;
import co.fin.intellioms.enums.OrderType;
import co.fin.intellioms.enums.PriceDirection;
import co.fin.intellioms.enums.Side;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.omsclt.MarketResponse;
import co.fin.intellioms.omsclt.Order;
import co.fin.intellioms.omsclt.OrderId;
import co.fin.intellioms.omsclt.Price;
import co.fin.intellioms.omsclt.Quantity;
import co.fin.intellioms.omsclt.impl.OrderImpl;
import co.fin.intellioms.persist.CondOrderDuplicateException;
import co.fin.intellioms.persist.PersistAdaptor;
import co.fin.intellioms.persist.PersistException;
import co.fin.intellioms.price.PriceCondOrder;
import co.fin.intellioms.price.PriceCondition;
import co.fin.intellioms.simple.SimpleCondOrder;
import co.fin.intellioms.ticker.Ticker;
import co.fin.intellioms.util.IDGeneratorException;
import co.fin.intellioms.util.SequenceIDGenerator;
import com.fitechlabs.xtier.services.objpool.ObjectPoolException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// Referenced classes of package com.com.fin.intellioms.persist.db:
//            AbstractDbStore

public class DbStore extends AbstractDbStore
    implements PersistAdaptor
{

    public DbStore()
        throws PersistException
    {
        try
        {
            operationsIdGen = new SequenceIDGenerator("rls_cond_order_op_seq", 1000L);
        }
        catch(ObjectPoolException e)
        {
            throw new PersistException(e.getMessage(), e);
        }
        catch(SQLException e)
        {
            throw new PersistException(e.getMessage(), e);
        }
    }

    public void save(Event event)
        throws PersistException
    {
        if(event.getEventType() == EventType.EXECUTION || event.getEventType() == EventType.MARKET_RESP)
        {
            MarketResponse act = (MarketResponse)event.getEventData();
            saveMarketResponse(act);
        }
    }

    public void save(CondOrderOperation op)
        throws PersistException
    {
        Connection conn = null;
        try
        {
            TxManager.begin();
            conn = getConnection();
            switch(op.getOpType().toValue())
            {
            default:
                break;

            case 1: // '\001'
            {
                CondOrder ord = op.getCondOrder();
                if(!$assertionsDisabled && ord == null)
                    throw new AssertionError("Conditional order is null.");
                doSave(conn, ord);
                break;
            }

            case 2: // '\002'
            {
                CondOrder oldOrd = op.getOldCondOrder();
                CondOrder newOrd = op.getCondOrder();
                if(!$assertionsDisabled && oldOrd == null)
                    throw new AssertionError("Old order is null.");
                if(!$assertionsDisabled && newOrd == null)
                    throw new AssertionError("New order is null.");
                long opId = operationsIdGen.next();
                doModify(conn, oldOrd, newOrd, opId);
                break;
            }

            case 3: // '\003'
            {
                CondOrder ord = op.getOldCondOrder();
                if(!$assertionsDisabled && ord == null)
                    throw new AssertionError("Old order is null.");
                cancel(ord);
                break;
            }
            }
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to store conditional order operation ").append(op).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to store conditional order operation ").append(op).toString(), e);
        }
        catch(IDGeneratorException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to store conditional order operation ").append(op).toString(), e);
        }
        catch(PersistException e)
        {
            rollback();
            throw e;
        }
    }

    public void cancel(CondOrder ord)
        throws PersistException
    {
        PreparedStatement cancelStmt;
        PreparedStatement histStmt;
        cancelStmt = null;
        histStmt = null;
        try
        {
            TxManager.begin();
            Connection conn = TxManager.getConnection();
            cancelStmt = conn.prepareStatement("UPDATE rls_cond_order SET status = 3 WHERE id = ?");
            histStmt = prepareOpHistoryStmt(conn);
            long opId = operationsIdGen.next();
            doCancel(cancelStmt, ord, opId, histStmt);
            cancelStmt.executeBatch();
            histStmt.executeBatch();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to cancel conditional order ").append(ord).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to cancel conditional order ").append(ord).toString(), e);
        }
        catch(IDGeneratorException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to cancel conditional order ").append(ord).toString(), e);
        }
        close(cancelStmt);
        close(histStmt);
        break MISSING_BLOCK_LABEL_199;
        Exception exception;
        exception;
        close(cancelStmt);
        close(histStmt);
        throw exception;
    }

    public void markCompleted(CondOrder ord)
        throws PersistException
    {
        PreparedStatement st;
        Connection conn = null;
        st = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            st = conn.prepareStatement("UPDATE rls_cond_order SET status = 4 WHERE id = ?");
            st.setLong(1, ord.getId());
            st.executeUpdate();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to mark conditional order as completed:").append(ord).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to mark conditional order as completed:").append(ord).toString(), e);
        }
        close(st);
        break MISSING_BLOCK_LABEL_132;
        Exception exception;
        exception;
        close(st);
        throw exception;
    }

    public void markActivated(CondOrder ord)
        throws PersistException
    {
        PreparedStatement st;
        Connection conn = null;
        st = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            st = conn.prepareStatement("UPDATE rls_cond_order SET status = 2 WHERE id = ?");
            st.setLong(1, ord.getId());
            st.executeUpdate();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to mark conditional order as activated:").append(ord).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to mark conditional order as activated:").append(ord).toString(), e);
        }
        close(st);
        break MISSING_BLOCK_LABEL_132;
        Exception exception;
        exception;
        close(st);
        throw exception;
    }

    public void updateActivationRatio(CondOrder ord)
        throws PersistException
    {
        PreparedStatement st;
        Connection conn = null;
        st = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            st = conn.prepareStatement("UPDATE rls_cond_order SET act_ratio = ? WHERE id = ?");
            st.setBigDecimal(1, ord.getActivatedRatio().getBigDecimal());
            st.setLong(2, ord.getId());
            st.executeUpdate();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to update activated ratio:").append(ord).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable to update activated ratio:").append(ord).toString(), e);
        }
        close(st);
        break MISSING_BLOCK_LABEL_148;
        Exception exception;
        exception;
        close(st);
        throw exception;
    }

    public void updateQuantity(Order ord)
        throws PersistException
    {
        PreparedStatement st;
        Connection conn = null;
        st = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            st = conn.prepareStatement("UPDATE rls_oms_order_gen SET orig_qty = ? WHERE id = ? AND ord_type = ?");
            st.setBigDecimal(1, ord.getOriginalQuantity().getBigDecimal());
            st.setLong(2, ord.getOrderId().getId());
            st.setInt(3, ord.getOrderId().getType().toValue());
            st.executeUpdate();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable update quantity for OMS order:").append(ord).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable update quantity for OMS order:").append(ord).toString(), e);
        }
        close(st);
        break MISSING_BLOCK_LABEL_170;
        Exception exception;
        exception;
        close(st);
        throw exception;
    }

    public void save(Order ord)
        throws PersistException
    {
        PreparedStatement st;
        Connection conn = null;
        st = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            st = conn.prepareStatement("INSERT INTO rls_oms_order_gen (id, init_ord_id, ord_type, exec_type, orig_qty, price, seq_nmbr, memo) VALUES (?, ?, ?, ?, ?, ?, rls_oms_order_seq.NEXTVAL, ?)");
            st.setLong(1, ord.getOrderId().getId());
            st.setLong(2, ord.getInitialOrderId().getId());
            st.setLong(3, ord.getInitialOrderId().getType().toValue());
            st.setInt(4, ord.getExecType().toValue());
            st.setBigDecimal(5, ord.getOriginalQuantity().getBigDecimal());
            if(ord.getPrice() != null)
                st.setBigDecimal(6, ord.getPrice().getBigDecimal());
            else
                st.setNull(6, 3);
            if(ord.getMemo() != null)
                st.setString(7, ord.getMemo());
            else
                st.setNull(7, 12);
            st.executeUpdate();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable save order:").append(ord).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable save order:").append(ord).toString(), e);
        }
        close(st);
        break MISSING_BLOCK_LABEL_277;
        Exception exception;
        exception;
        close(st);
        throw exception;
    }

    public Collection loadOrders(long start, long end)
        throws PersistException
    {
        Map orders;
        List rootOrders;
        PreparedStatement st;
        ResultSet rs;
        orders = new HashMap();
        rootOrders = new ArrayList();
        st = null;
        rs = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            String qry = "SELECT c.id, c.parent_id, c.type, c.status, c.account_id, c.act_ratio,                                     p.price as p_price, p.direction as p_direction, p.prod_id as p_prod_id, p.market_id as p_market_id, ch.incremental as ch_incr,                                                                          t.exec_time as t_exec_time,                                                                         o.ord_id as o_id, o.prod_id as o_prod_id, o.market_id as o_market_id, o.ord_type as o_ord_type, o.exec_type as o_exec_type, o.side as o_side, o.orig_qty as o_orig_qty, o.price as o_price, o.memo as o_memo, og.id as o_go_id, og.exec_type as o_go_exec_type, og.orig_qty as o_go_orig_qty, og.price as o_go_price, og.memo as o_go_memo, ex.exec_qty as o_go_exec_qty FROM rls_cond_order c                                                                               LEFT JOIN rls_price_cond p ON c.id = p.cond_ord_id                                                  LEFT JOIN rls_chain_cond ch ON c.id = ch.cond_ord_id                                                LEFT JOIN rls_time_cond t ON c.id = t.cond_ord_id                                                    LEFT JOIN rls_oms_order o ON c.id = o.cond_ord_id LEFT JOIN rls_oms_order_gen og ON og.init_ord_id = o.ord_id AND og.ord_type = o.ord_type LEFT JOIN rls_oms_order_exec_qty ex ON og.id = ex.ord_id AND og.ord_type = ex.ord_type WHERE c.account_id >= ? AND c.account_id  <= ? ORDER BY c.seq_num, og.id";
            st = conn.prepareStatement(qry.toString());
            st.setLong(1, start);
            st.setLong(2, end);
            rs = st.executeQuery();
            do
            {
                if(!rs.next())
                    break;
                long id = rs.getLong("id");
                long parentId = rs.getLong("parent_id");
                CondOrderType type = (CondOrderType)CondOrderType.getEnum(rs.getInt("type"));
                CondOrderState state = (CondOrderState)CondOrderState.getEnum(rs.getInt("status"));
                long accountId = rs.getLong("account_id");
                CondOrder ord = null;
                switch(type.toValue())
                {
                case 1: // '\001'
                {
                    OrderType ordType = OrderType.getEnum(rs.getInt("o_ord_type"));
                    Ticker ticker = new Ticker(rs.getLong("o_prod_id"), rs.getLong("o_market_id"));
                    Side side = Side.getEnum(rs.getInt("o_side"));
                    SimpleCondOrder simple = (SimpleCondOrder)orders.get(new Long(id));
                    long ordId;
                    if(simple == null)
                    {
                        ordId = rs.getLong("o_id");
                        String memo = rs.getString("o_memo");
                        OrderExecType execType = OrderExecType.getEnum(rs.getInt("o_exec_type"));
                        BigDecimal bdPrice = rs.getBigDecimal("o_price");
                        Price price = bdPrice == null ? null : new Price(bdPrice);
                        Quantity origQty = new Quantity(rs.getBigDecimal("o_orig_qty"));
                        OrderImpl omsOrd = new OrderImpl(new OrderId(ordId, ordType), accountId, side, price, new Quantity(origQty), ticker, execType, new Quantity(0.0D), origQty);
                        omsOrd.setMemo(memo);
                        Ratio ratio = new Ratio(rs.getBigDecimal("act_ratio"));
                        simple = new SimpleCondOrder(id, accountId, omsOrd, ratio);
                        ord = simple;
                    }
                    ordId = rs.getLong("o_go_id");
                    if(!rs.wasNull())
                    {
                        OrderExecType execType = OrderExecType.getEnum(rs.getInt("o_go_exec_type"));
                        String memo = rs.getString("o_go_memo");
                        BigDecimal bdExecQty = rs.getBigDecimal("o_go_exec_qty");
                        Quantity execQty = bdExecQty == null ? new Quantity(0.0D) : new Quantity(bdExecQty);
                        BigDecimal bdPrice = rs.getBigDecimal("o_go_price");
                        Price price = bdPrice == null ? null : new Price(bdPrice);
                        Quantity origQty = new Quantity(rs.getBigDecimal("o_go_orig_qty"));
                        if(origQty.isBelow(execQty))
                            origQty = new Quantity(execQty);
                        Quantity qty = new Quantity(origQty);
                        qty.subtruct(execQty);
                        OrderImpl omsOrd = new OrderImpl(new OrderId(ordId, ordType), accountId, side, price, qty, ticker, execType, execQty, origQty);
                        omsOrd.setMemo(memo);
                        simple.addSubmittedOrder(omsOrd);
                        OrderImpl templ = (OrderImpl)simple.getOmsOrderTemplate();
                        Quantity templExecQty = new Quantity(templ.getExecutedQuantity());
                        templExecQty.add(omsOrd.getExecutedQuantity());
                        templ.setExecutedQuantity(templExecQty);
                        Ratio execratio = simple.getExecutedRatio();
                        if(simple.getActivatedRatio().isBelow(execratio))
                        {
                            simple.getActivatedRatio().subtruct(simple.getActivatedRatio());
                            simple.getActivatedRatio().add(execratio);
                        }
                    }
                    break;
                }

                case 2: // '\002'
                {
                    Price price = new Price(rs.getBigDecimal("p_price"));
                    PriceDirection dir = rs.getInt("p_direction") != 1 ? PriceDirection.DOWN : PriceDirection.UP;
                    Ticker ticker = new Ticker(rs.getLong("p_prod_id"), rs.getLong("p_market_id"));
                    ord = new PriceCondOrder(id, accountId, new PriceCondition(price, dir, ticker));
                    break;
                }

                case 3: // '\003'
                {
                    Date time = new Date(rs.getTimestamp("t_exec_time").getTime());
                    ord = new TimeCondOrder(id, accountId, time);
                    break;
                }

                case 4: // '\004'
                {
                    Ratio ratio = new Ratio(rs.getBigDecimal("act_ratio"));
                    ord = new ExclusiveCondOrder(id, accountId, ratio);
                    break;
                }

                case 5: // '\005'
                {
                    boolean incr = rs.getInt("ch_incr") == 1;
                    ord = new ChainCondOrder(id, accountId, new ChainCondition(incr));
                    break;
                }

                default:
                {
                    if(!$assertionsDisabled)
                        throw new AssertionError((new StringBuilder()).append("Unknown conditional order type: ").append(type.toValue()).toString());
                    break;
                }
                }
                if(ord != null)
                {
                    ord.setState(state);
                    if(parentId > 0L)
                    {
                        CondOrder parent = (CondOrder)orders.get(new Long(parentId));
                        if(!$assertionsDisabled && parent == null)
                            throw new AssertionError((new StringBuilder()).append("Unable to find parent order with id=").append(parentId).toString());
                        parent.addSubOrder(ord);
                    } else
                    {
                        rootOrders.add(ord);
                    }
                    orders.put(new Long(ord.getId()), ord);
                }
            } while(true);
            TxManager.commit();
        }
        catch(SQLException e)
        {
            if(log.isError())
                log.error((new StringBuilder()).append("Unable to load conditional orders: ").append(e.getMessage()).toString(), e);
            rollback();
            throw new PersistException(e.getMessage(), e);
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException(e.getMessage(), e);
        }
        close(rs);
        close(st);
        break MISSING_BLOCK_LABEL_1281;
        Exception exception;
        exception;
        close(rs);
        close(st);
        throw exception;
        Iterator iter = rootOrders.iterator();
        do
        {
            if(!iter.hasNext())
                break;
            CondOrder ord = (CondOrder)iter.next();
            if(isCompleted(ord))
                iter.remove();
        } while(true);
        return rootOrders;
    }

    private void saveMarketResponse(MarketResponse rsp)
        throws PersistException
    {
        PreparedStatement st;
        Connection conn = null;
        st = null;
        try
        {
            TxManager.begin();
            Connection conn = getConnection();
            st = conn.prepareStatement("INSERT INTO rls_market_rsp (act_id, ord_id, ord_type, rsp_type, qty, failed_qty, rejected, event_seq_num) VALUES (?, ?, ?, ?, ?, ?, ?, rls_event_seq.NEXTVAL)");
            st.setLong(1, rsp.getOrderActionId());
            st.setLong(2, rsp.getOrderId().getId());
            st.setInt(3, rsp.getOrderId().getType().toValue());
            st.setInt(4, rsp.getMarketResponseType().toValue());
            st.setBigDecimal(5, rsp.getQuantity().getBigDecimal());
            if(rsp.getFailedQuantity() != null)
                st.setBigDecimal(6, rsp.getFailedQuantity().getBigDecimal());
            else
                st.setNull(6, 4);
            st.setInt(7, !rsp.getRejectFlag() ? 0 : 1);
            st.executeUpdate();
            TxManager.commit();
        }
        catch(TxManagerException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable save order action:").append(rsp).toString(), e);
        }
        catch(SQLException e)
        {
            rollback();
            throw new PersistException((new StringBuilder()).append("Unable save order action:").append(rsp).toString(), e);
        }
        close(st);
        break MISSING_BLOCK_LABEL_260;
        Exception exception;
        exception;
        close(st);
        throw exception;
    }

    private boolean isCompleted(CondOrder ord)
    {
        if(ord.hasSubOrders())
        {
            for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext();)
            {
                CondOrder child = (CondOrder)iter.next();
                if(!isCompleted(child))
                    return false;
            }

            return true;
        } else
        if(ord.getType() != CondOrderType.SIMPLE)
            return true;
        else
            return ord.getState() == CondOrderState.COMPLETED || ord.getState() == CondOrderState.CANCELED;
    }

    private void doSave(Connection conn, CondOrder ord)
        throws SQLException, PersistException, IDGeneratorException
    {
        PreparedStatement insertStmt;
        PreparedStatement histStmt;
        Map paramStatements;
        insertStmt = null;
        histStmt = null;
        paramStatements = new HashMap();
        insertStmt = prepareOpHistoryStmt(conn);
        long opId = operationsIdGen.next();
        insertStmt = conn.prepareStatement("INSERT INTO rls_cond_order (id, type, parent_id, account_id, seq_num) VALUES (?, ?, ?, ?, rls_cond_order_seq.NEXTVAL)");
        histStmt = prepareOpHistoryStmt(conn);
        doSaveCondOrder(ord, conn, insertStmt, paramStatements, opId, histStmt);
        try
        {
            insertStmt.executeBatch();
        }
        catch(SQLException e)
        {
            if(e.getErrorCode() == 1)
                throw new CondOrderDuplicateException("Duplicated conditional order.");
            else
                throw e;
        }
        PreparedStatement paramStmt;
        for(Iterator iter = paramStatements.values().iterator(); iter.hasNext(); paramStmt.executeBatch())
            paramStmt = (PreparedStatement)iter.next();

        histStmt.executeBatch();
        PreparedStatement paramStmt;
        for(Iterator iter = paramStatements.values().iterator(); iter.hasNext(); close(paramStmt))
            paramStmt = (PreparedStatement)iter.next();

        close(insertStmt);
        close(histStmt);
        break MISSING_BLOCK_LABEL_269;
        Exception exception;
        exception;
        PreparedStatement paramStmt;
        for(Iterator iter = paramStatements.values().iterator(); iter.hasNext(); close(paramStmt))
            paramStmt = (PreparedStatement)iter.next();

        close(insertStmt);
        close(histStmt);
        throw exception;
    }

    private PreparedStatement prepareOpHistoryStmt(Connection conn)
        throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO RLS_COND_ORDER_HIST (id, cond_ord_id, op_type, op_id, p_price, p_direction, t_exec_time, oms_exec_type, oms_orig_qty, oms_price, event_seq_num) VALUES (rls_cond_order_history_id_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, rls_event_seq.NEXTVAL)");
        return stmt;
    }

    private void doSaveCondOrder(CondOrder ord, Connection conn, PreparedStatement orderStmt, Map prepStmtCache, long opId, PreparedStatement histStmt)
        throws PersistException, SQLException
    {
        if(!$assertionsDisabled && ord == null)
            throw new AssertionError("Order is null.");
        if(!$assertionsDisabled && conn == null)
            throw new AssertionError("Connection is null.");
        orderStmt.setLong(1, ord.getId());
        orderStmt.setInt(2, ord.getType().toValue());
        if(ord.getParent() != null)
            orderStmt.setLong(3, ord.getParent().getId());
        else
            orderStmt.setNull(3, -5);
        orderStmt.setLong(4, ord.getAccountId());
        orderStmt.addBatch();
        PreparedStatement paramStmt = null;
        switch(ord.getType().toValue())
        {
        case 2: // '\002'
        {
            PriceCondition cond = (PriceCondition)ord.getParam();
            if(!$assertionsDisabled && cond == null)
                throw new AssertionError("Price condition is null.");
            paramStmt = (PreparedStatement)prepStmtCache.get(ord.getType());
            if(paramStmt == null)
            {
                paramStmt = conn.prepareStatement("INSERT INTO rls_price_cond (cond_ord_id, price, direction, prod_id, market_id) VALUES (?, ?, ?, ?, ?)");
                prepStmtCache.put(ord.getType(), paramStmt);
            }
            paramStmt.setLong(1, ord.getId());
            paramStmt.setBigDecimal(2, cond.getTargetPrice().getBigDecimal());
            paramStmt.setInt(3, cond.getDirection().toValue());
            paramStmt.setLong(4, cond.getTicker().getProductId());
            paramStmt.setLong(5, cond.getTicker().getMarketId());
            break;
        }

        case 4: // '\004'
            break;

        case 1: // '\001'
        {
            Order oms = (Order)ord.getParam();
            if(!$assertionsDisabled && oms == null)
                throw new AssertionError("OMS order is null.");
            paramStmt = (PreparedStatement)prepStmtCache.get(ord.getType());
            if(paramStmt == null)
            {
                paramStmt = conn.prepareStatement("INSERT INTO rls_oms_order (cond_ord_id, account_id, ord_id, prod_id, market_id, ord_type, exec_type, side, orig_qty, price, memo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                prepStmtCache.put(ord.getType(), paramStmt);
            }
            paramStmt.setLong(1, ord.getId());
            paramStmt.setLong(2, oms.getAccountId());
            paramStmt.setLong(3, oms.getOrderId().getId());
            paramStmt.setLong(4, oms.getTicker().getProductId());
            paramStmt.setLong(5, oms.getTicker().getMarketId());
            paramStmt.setInt(6, oms.getOrderType().toValue());
            paramStmt.setInt(7, oms.getExecType().toValue());
            paramStmt.setInt(8, oms.getSide().toValue());
            paramStmt.setBigDecimal(9, oms.getOriginalQuantity().getBigDecimal());
            if(oms.getPrice() != null)
                paramStmt.setBigDecimal(10, oms.getPrice().getBigDecimal());
            else
                paramStmt.setNull(10, 8);
            if(oms.getMemo() != null)
                paramStmt.setString(11, oms.getMemo());
            else
                paramStmt.setNull(11, 12);
            break;
        }

        case 5: // '\005'
        {
            ChainCondition cond = (ChainCondition)ord.getParam();
            paramStmt = (PreparedStatement)prepStmtCache.get(ord.getType());
            if(paramStmt == null)
            {
                paramStmt = conn.prepareStatement("INSERT INTO rls_chain_cond (cond_ord_id, incremental) VALUES (?, ?)");
                prepStmtCache.put(ord.getType(), paramStmt);
            }
            paramStmt.setLong(1, ord.getId());
            paramStmt.setInt(2, cond.isIncremental() ? 1 : 0);
            break;
        }

        case 3: // '\003'
        {
            java.sql.Date execTime = new java.sql.Date(((Date)ord.getParam()).getTime());
            if(!$assertionsDisabled && execTime == null)
                throw new AssertionError("Exec time is null.");
            paramStmt = (PreparedStatement)prepStmtCache.get(ord.getType());
            if(paramStmt == null)
            {
                paramStmt = conn.prepareStatement("INSERT INTO rls_time_cond (cond_ord_id, exec_time) VALUES (?, ?)");
                prepStmtCache.put(ord.getType(), paramStmt);
            }
            paramStmt.setLong(1, ord.getId());
            paramStmt.setTimestamp(2, new Timestamp(execTime.getTime()));
            break;
        }

        default:
        {
            throw new PersistException((new StringBuilder()).append("Unknown order type: ").append(ord).toString());
        }
        }
        addToHistory(ord, CondOrderOpType.NEW, opId, histStmt);
        if(paramStmt != null)
            paramStmt.addBatch();
        if(ord.hasSubOrders())
        {
            CondOrder subOrd;
            for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext(); doSaveCondOrder(subOrd, conn, orderStmt, prepStmtCache, opId, histStmt))
                subOrd = (CondOrder)iter.next();

        }
    }

    private void addToHistory(CondOrder ord, CondOrderOpType opType, long opId, PreparedStatement histStmt)
        throws SQLException
    {
        switch(ord.getType().toValue())
        {
        default:
            break;

        case 2: // '\002'
            PriceCondition cond = (PriceCondition)ord.getParam();
            histStmt.setLong(1, ord.getId());
            histStmt.setInt(2, opType.toValue());
            histStmt.setLong(3, opId);
            histStmt.setBigDecimal(4, cond.getTargetPrice().getBigDecimal());
            histStmt.setInt(5, cond.getDirection().toValue());
            histStmt.setNull(6, 93);
            histStmt.setNull(7, 2);
            histStmt.setNull(8, 2);
            histStmt.setNull(9, 2);
            histStmt.addBatch();
            break;

        case 1: // '\001'
            Order oms = (Order)ord.getParam();
            if(!$assertionsDisabled && oms == null)
                throw new AssertionError("OMS order is null.");
            histStmt.setLong(1, ord.getId());
            histStmt.setInt(2, opType.toValue());
            histStmt.setLong(3, opId);
            histStmt.setNull(4, 2);
            histStmt.setNull(5, 2);
            histStmt.setNull(6, 93);
            histStmt.setInt(7, oms.getExecType().toValue());
            histStmt.setBigDecimal(8, oms.getOriginalQuantity().getBigDecimal());
            if(oms.getPrice() != null)
                histStmt.setBigDecimal(9, oms.getPrice().getBigDecimal());
            else
                histStmt.setNull(9, 2);
            histStmt.addBatch();
            break;

        case 3: // '\003'
            java.sql.Date execTime = new java.sql.Date(((Date)ord.getParam()).getTime());
            if(!$assertionsDisabled && execTime == null)
                throw new AssertionError("Exec time is null.");
            histStmt.setLong(1, ord.getId());
            histStmt.setInt(2, opType.toValue());
            histStmt.setLong(3, opId);
            histStmt.setNull(4, 2);
            histStmt.setNull(5, 2);
            histStmt.setTimestamp(6, new Timestamp(execTime.getTime()));
            histStmt.setNull(7, 2);
            histStmt.setNull(8, 2);
            histStmt.setNull(9, 2);
            histStmt.addBatch();
            break;

        case 4: // '\004'
        case 5: // '\005'
            histStmt.setLong(1, ord.getId());
            histStmt.setInt(2, opType.toValue());
            histStmt.setLong(3, opId);
            histStmt.setNull(4, 2);
            histStmt.setNull(5, 2);
            histStmt.setNull(6, 93);
            histStmt.setNull(7, 2);
            histStmt.setNull(8, 2);
            histStmt.setNull(9, 2);
            histStmt.addBatch();
            break;
        }
    }

    private void doCancel(PreparedStatement cancelStmt, CondOrder ord, long opId, PreparedStatement histStmt)
        throws SQLException
    {
        cancelStmt.setLong(1, ord.getId());
        cancelStmt.addBatch();
        addToHistory(ord, CondOrderOpType.CANCEL, opId, histStmt);
        if(ord.hasSubOrders())
        {
            CondOrder child;
            for(Iterator iter = ord.getSubOrders().iterator(); iter.hasNext(); doCancel(cancelStmt, child, opId, histStmt))
                child = (CondOrder)iter.next();

        }
    }

    private void doModify(Connection conn, CondOrder oldOrder, CondOrder newOrder, long opId)
        throws PersistException, SQLException, IDGeneratorException
    {
        PreparedStatement updateStmt;
        PreparedStatement histStmt;
        if(!$assertionsDisabled && oldOrder == null)
            throw new AssertionError("Old order is null.");
        if(!$assertionsDisabled && newOrder == null)
            throw new AssertionError("New order is null.");
        updateStmt = null;
        histStmt = null;
        switch(newOrder.getType().toValue())
        {
        case 1: // '\001'
            SimpleCondOrder ord = (SimpleCondOrder)newOrder;
            Order omsOrd = ord.getOmsOrderTemplate();
            updateStmt = conn.prepareStatement("UPDATE rls_oms_order SET exec_type = ?, orig_qty = ?, price = ?, memo = ? WHERE ord_id = ? AND ord_type = ?");
            updateStmt.setInt(1, omsOrd.getExecType().toValue());
            updateStmt.setBigDecimal(2, omsOrd.getOriginalQuantity().getBigDecimal());
            if(omsOrd.getPrice() != null)
                updateStmt.setBigDecimal(3, omsOrd.getPrice().getBigDecimal());
            else
                updateStmt.setNull(3, 3);
            if(omsOrd.getMemo() == null)
                updateStmt.setString(4, omsOrd.getMemo());
            else
                updateStmt.setNull(4, 12);
            updateStmt.setLong(5, omsOrd.getOrderId().getId());
            updateStmt.setInt(6, omsOrd.getOrderId().getType().toValue());
            updateStmt.executeUpdate();
            break;

        case 4: // '\004'
        case 5: // '\005'
            break;

        case 2: // '\002'
            PriceCondOrder price = (PriceCondOrder)newOrder;
            updateStmt = conn.prepareStatement("UPDATE rls_price_cond SET price = ?, direction = ? WHERE cond_ord_id = ?");
            updateStmt.setBigDecimal(1, price.getPriceCondition().getTargetPrice().getBigDecimal());
            updateStmt.setLong(2, price.getPriceCondition().getDirection().toValue());
            updateStmt.setLong(3, price.getId());
            updateStmt.executeUpdate();
            break;

        case 3: // '\003'
            TimeCondOrder time = (TimeCondOrder)newOrder;
            updateStmt = conn.prepareStatement("UPDATE rls_time_cond SET exec_time = ? WHERE cond_ord_id = ?");
            Timestamp fireTime = new Timestamp(time.getFireTime().getTime());
            updateStmt.setTimestamp(1, fireTime);
            updateStmt.setLong(2, time.getId());
            updateStmt.executeUpdate();
            break;

        default:
            if(!$assertionsDisabled)
                throw new AssertionError((new StringBuilder()).append("Unexpected conditional order type: ").append(newOrder.getType()).toString());
            break;
        }
        histStmt = prepareOpHistoryStmt(conn);
        addToHistory(newOrder, CondOrderOpType.MODIFY, opId, histStmt);
        close(updateStmt);
        close(histStmt);
        break MISSING_BLOCK_LABEL_521;
        Exception exception;
        exception;
        close(updateStmt);
        close(histStmt);
        throw exception;
        List oldSubords = oldOrder.getSubOrders();
        if(newOrder.hasSubOrders())
        {
            for(Iterator iter = newOrder.getSubOrders().iterator(); iter.hasNext();)
            {
                CondOrder subOrd = (CondOrder)iter.next();
                int idx = oldSubords.indexOf(subOrd);
                if(idx == -1)
                {
                    doSave(conn, subOrd);
                } else
                {
                    CondOrder oldSubOrd = (CondOrder)oldSubords.get(idx);
                    doModify(conn, oldSubOrd, subOrd, opId);
                }
            }

        }
        return;
    }

    private final IDGenerator operationsIdGen;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/persist/db/DbStore.desiredAssertionStatus();

}
