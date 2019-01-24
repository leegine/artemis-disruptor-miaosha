// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   GenericRuleModule.java

package co.fin.intellioms.rulesys.impl;

import com.fitechlabs.fin.intellioms.enums.*;
import co.fin.intellioms.event.Event;
import co.fin.intellioms.event.EventException;
import com.fitechlabs.fin.intellioms.rulesys.*;
import com.fitechlabs.fin.intellioms.util.*;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

public class GenericRuleModule
    implements RuleModule, Startable
{

    public GenericRuleModule(String name, EventType evtTypes[], CondOrderType ordTypes[])
    {
        rules = new ArrayList();
        this.evtTypes = evtTypes == null ? new EventType[0] : evtTypes;
        this.name = name;
        this.ordTypes = ordTypes;
        log = Log.getLogger((new StringBuilder()).append(com/ com /fin/intellioms/rulesys/impl/GenericRuleModule.getName()).append("#").append(name).toString());
        Arrays.sort(this.evtTypes, new Comparator() {

            public int compare(Object o1, Object o2)
            {
                Enumeration e1 = (Enumeration)o1;
                Enumeration e2 = (Enumeration)o2;
                return e1.toValue() - e2.toValue();
            }

            final GenericRuleModule this$0;


            {
                this$0 = GenericRuleModule.this;
                super();
            }
        }
);
    }

    public GenericRuleModule(String name, String evtTypes, String ordTypes)
    {
        rules = new ArrayList();
        EventType events[];
        String strs[];
        if(evtTypes != null)
        {
            strs = evtTypes == null ? new String[0] : evtTypes.split(",");
            events = new EventType[strs.length];
            for(int i = 0; i < strs.length; i++)
                events[i] = (EventType)EventType.getEnum(Integer.parseInt(strs[i]));

        } else
        {
            events = new EventType[0];
        }
        strs = ordTypes.split(",");
        CondOrderType ords[] = new CondOrderType[strs.length];
        for(int i = 0; i < strs.length; i++)
            ords[i] = (CondOrderType)CondOrderType.getEnum(Integer.parseInt(strs[i]));

        this.evtTypes = events;
        this.name = name;
        this.ordTypes = ords;
        log = Log.getLogger((new StringBuilder()).append(com/ com /fin/intellioms/rulesys/impl/GenericRuleModule.getName()).append("#").append(name).toString());
        Arrays.sort(this.evtTypes, new Comparator() {

            public int compare(Object o1, Object o2)
            {
                Enumeration e1 = (Enumeration)o1;
                Enumeration e2 = (Enumeration)o2;
                return e1.toValue() - e2.toValue();
            }

            final GenericRuleModule this$0;


            {
                this$0 = GenericRuleModule.this;
                super();
            }
        }
);
    }

    public String name()
    {
        return name;
    }

    public CondOrderType[] getApplicableCondOrderTypes()
    {
        return ordTypes;
    }

    public EventType[] getApplicableEventTypes()
    {
        return evtTypes;
    }

    public boolean applicableEvent(Event event)
    {
        return Arrays.binarySearch(evtTypes, event) >= 0;
    }

    public List getAllRules()
    {
        return rules;
    }

    public List getApplicableRules(Event event)
    {
        return null;
    }

    public List getMatchingOrders(Event event)
        throws EventException
    {
        List result = new ArrayList();
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            List ords = rl.findMatchingOrders(event);
            if(ords != null)
                result.addAll(ords);
        }

        return result;
    }

    public void matchAndExecute(Event event)
        throws EventException
    {
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            rl.matchAndExecute(event);
        }

    }

    public boolean registerRule(OrderRule rl)
    {
        for(int i = 0; i < rules.size(); i++)
            if(rules.get(i).equals(rl))
                return false;

        rules.add(rl);
        if(ruleEngine != null)
            rl.onRegister(ruleEngine.createRuleContext(rl, this));
        if(log.isInfo())
            log.info((new StringBuilder()).append("Registered rule: ").append(rl).toString());
        return true;
    }

    public void registerOrder(CondOrder ord)
        throws CondOrderException
    {
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            rl.registerOrder(ord);
            if(log.isInfo())
                log.info((new StringBuilder()).append("Registered order ").append(ord).append(" in rule ").append(rl).toString());
        }

    }

    public void unregisterOrder(CondOrder ord)
        throws CondOrderException
    {
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            rl.unregisterOrder(ord);
            if(log.isDebug())
                log.debug((new StringBuilder()).append("Unregistered order ").append(ord).append(" from rule ").append(rl).toString());
        }

    }

    public void onRegister(OmsRuleEngine ruleEng)
    {
        ruleEngine = ruleEng;
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rule = (OrderRule)rules.get(i);
            rule.onRegister(ruleEngine.createRuleContext(rule, this));
        }

    }

    public void onUnregister(OmsRuleEngine ruleEng)
    {
        ruleEngine = null;
    }

    public void modifyCondOrder(CondOrder old, CondOrder newOrd)
        throws CondOrderException
    {
        if(!$assertionsDisabled && old == null)
            throw new AssertionError("Old order is null");
        if(!$assertionsDisabled && newOrd == null)
            throw new AssertionError("New order is null");
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            rl.modifyOrder(old, newOrd);
        }

        if(log.isInfo())
            log.info((new StringBuilder()).append("Modified order from ").append(old).append(" to ").append(newOrd).append(".").toString());
    }

    public OmsRuleEngine getRuleEngine()
    {
        if(!$assertionsDisabled && ruleEngine == null)
            throw new AssertionError((new StringBuilder()).append("Module not registered in rule engine: ").append(this).toString());
        else
            return ruleEngine;
    }

    public String toString()
    {
        return (new StringBuilder()).append("GenericRuleModule[name=").append(name).append(", orderTypes=").append(Utils.arr2Str(ordTypes)).append(", eventTypes=").append(Utils.arr2Str(evtTypes)).append("]").toString();
    }

    public void start()
        throws InitializationException
    {
        for(int i = 0; i < rules.size(); i++)
            ((OrderRule)rules.get(i)).start();

    }

    public void stop()
    {
        for(int i = rules.size() - 1; i >= 0; i--)
            ((OrderRule)rules.get(i)).stop();

    }

    public void validate(CondOrder ord)
        throws CondOrderValidationException
    {
        for(int i = rules.size() - 1; i >= 0; i--)
            ((OrderRule)rules.get(i)).validate(ord);

    }

    public void load(CondOrder ord)
        throws CondOrderException
    {
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            rl.load(ord);
        }

    }

    public void unload(CondOrder ord)
    {
        for(int i = 0; i < rules.size(); i++)
        {
            OrderRule rl = (OrderRule)rules.get(i);
            rl.unload(ord);
        }

    }

    private final Log log;
    private final String name;
    private CondOrderType ordTypes[];
    private EventType evtTypes[];
    private final List rules;
    private OmsRuleEngine ruleEngine;
    static final boolean $assertionsDisabled = !com/ com /fin/intellioms/rulesys/impl/GenericRuleModule.desiredAssertionStatus();

}
