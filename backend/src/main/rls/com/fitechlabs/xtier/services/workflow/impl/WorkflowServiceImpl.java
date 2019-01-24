// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.services.workflow.*;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowExecSetImpl, WorkflowParseValidator, WorkflowParseValidationException, WorkflowExecManager,
//            WorkflowRegion, WorkflowRef, WorkflowRuleImpl, WorkflowEvent,
//            WorkflowAction, WorkflowGroup, WorkflowRuleOutput, WorkflowParam,
//            WorkflowCondition

public class WorkflowServiceImpl extends ServiceProviderAdapter
    implements WorkflowService
{
    private class RefResolver
    {

        void addIocRef(WorkflowRef workflowref, IocDescriptor iocdescriptor)
        {
            iocRefs.put(iocdescriptor, workflowref);
        }

        void addEventRef(WorkflowRef workflowref, WorkflowEvent workflowevent)
        {
            eventRefs.put(workflowevent, workflowref);
        }

        void addActionRef(WorkflowRef workflowref, WorkflowAction workflowaction)
        {
            actionRefs.put(workflowaction, workflowref);
        }

        void addEntryRef(WorkflowRef workflowref, WorkflowExecSet workflowexecset)
        {
            entryRefs.put(workflowexecset, workflowref);
        }

        void resolve()
            throws IocDescriptorException
        {
            resolveEventRefs();
            resolveEntryRefs();
            resolveActionRefs();
            resolveIocRefs();
        }

        private void resolveIocRefs()
            throws IocDescriptorException
        {
            IocDescriptor iocdescriptor;
            WorkflowRuleImpl workflowruleimpl;
            for(Iterator iterator = iocRefs.entrySet().iterator(); iterator.hasNext(); workflowruleimpl.setBody((WorkflowRuleBody)iocdescriptor.createNewObj(WorkflowServiceImpl.class$com$fitechlabs$xtier$services$workflow$WorkflowRuleBody != null ? WorkflowServiceImpl.class$com$fitechlabs$xtier$services$workflow$WorkflowRuleBody : (WorkflowServiceImpl.class$com$fitechlabs$xtier$services$workflow$WorkflowRuleBody = WorkflowServiceImpl._mthclass$("com.fitechlabs.xtier.services.workflow.WorkflowRuleBody")))))
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                iocdescriptor = (IocDescriptor)entry.getKey();
                WorkflowRef workflowref = (WorkflowRef)entry.getValue();
                workflowruleimpl = (WorkflowRuleImpl)region.getExecutable(workflowref);
                if(!$assertionsDisabled && workflowruleimpl == null)
                    throw new AssertionError("Could not find rule for workflow reference: " + workflowref);
            }

        }

        private void resolveEventRefs()
        {
            WorkflowEvent workflowevent;
            WorkflowRef workflowref;
            for(Iterator iterator = eventRefs.entrySet().iterator(); iterator.hasNext(); workflowevent.setCause(region.getExecutable(workflowref)))
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                workflowevent = (WorkflowEvent)entry.getKey();
                workflowref = (WorkflowRef)entry.getValue();
            }

        }

        private void resolveEntryRefs()
        {
            WorkflowExecSetImpl workflowexecsetimpl;
            WorkflowRef workflowref;
            for(Iterator iterator = entryRefs.entrySet().iterator(); iterator.hasNext(); workflowexecsetimpl.setStartEvent(workflowexecsetimpl.getEvent(workflowref)))
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                workflowexecsetimpl = (WorkflowExecSetImpl)entry.getKey();
                workflowref = (WorkflowRef)entry.getValue();
            }

        }

        private void resolveActionRefs()
        {
            WorkflowAction workflowaction;
            WorkflowRef workflowref;
            for(Iterator iterator = actionRefs.entrySet().iterator(); iterator.hasNext(); workflowaction.setNextEvent(workflowaction.getExecSet().getEvent(workflowref)))
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                workflowaction = (WorkflowAction)entry.getKey();
                workflowref = (WorkflowRef)entry.getValue();
            }

        }

        private Map entryRefs;
        private Map eventRefs;
        private Map actionRefs;
        private Map iocRefs;
        static final boolean $assertionsDisabled; /* synthetic field */


        private RefResolver()
        {
            super();
            entryRefs = new HashMap();
            eventRefs = new HashMap();
            actionRefs = new HashMap();
            iocRefs = new HashMap();
        }

    }


    public WorkflowServiceImpl()
    {
    }

    public String getName()
    {
        return "workflow";
    }

    public List getAllRules()
    {
        return region.getAllRules();
    }

    public List getAllExecSets()
    {
        return region.getAllExecSets();
    }

    public WorkflowExecSet getExecSet(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "name");
        return region.getExecSet(s, s1);
    }

    public WorkflowRule getRule(String s, String s1)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "name");
        return region.getRule(s, s1);
    }

    public boolean addListener(WorkflowListener workflowlistener)
    {
        ArgAssert.nullArg(workflowlistener, "listener");
        return execMgr.addListener(workflowlistener);
    }

    public boolean removeListener(WorkflowListener workflowlistener)
    {
        ArgAssert.nullArg(workflowlistener, "listener");
        return execMgr.removeListener(workflowlistener);
    }

    public List getAllListeners()
    {
        return execMgr.getAllListeners();
    }

    public WorkflowResult exec(String s, String s1, Map map)
        throws WorkflowException
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "name");
        WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)getExecSet(s, s1);
        if(workflowexecsetimpl == null)
            throw new IllegalArgumentException(L10n.format("SRVC.WF.ERR13", s, s1));
        else
            return execMgr.exec(workflowexecsetimpl, map);
    }

    public void exec(String s, String s1, Map map, WorkflowResultListener workflowresultlistener)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "name");
        ArgAssert.nullArg(workflowresultlistener, "listener");
        WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)getExecSet(s, s1);
        if(workflowexecsetimpl == null)
        {
            throw new IllegalArgumentException(L10n.format("SRVC.WF.ERR13", s, s1));
        } else
        {
            execMgr.exec(workflowexecsetimpl, map, workflowresultlistener);
            return;
        }
    }

    public void exec(String s, String s1, Map map, WorkflowResultListener workflowresultlistener, ThreadPool threadpool)
    {
        ArgAssert.nullArg(s, "grp");
        ArgAssert.nullArg(s1, "name");
        ArgAssert.nullArg(workflowresultlistener, "listener");
        ArgAssert.nullArg(threadpool, "threadPool");
        WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)getExecSet(s, s1);
        if(workflowexecsetimpl == null)
        {
            throw new IllegalArgumentException(L10n.format("SRVC.WF.ERR13", s, s1));
        } else
        {
            execMgr.exec(workflowexecsetimpl, map, workflowresultlistener, threadpool);
            return;
        }
    }

    public WorkflowResult exec(WorkflowExecSet workflowexecset, Map map)
        throws WorkflowException
    {
        ArgAssert.nullArg(workflowexecset, "execSet");
        return execMgr.exec((WorkflowExecSetImpl)workflowexecset, map);
    }

    public void exec(WorkflowExecSet workflowexecset, Map map, WorkflowResultListener workflowresultlistener)
    {
        ArgAssert.nullArg(workflowexecset, "execSet");
        ArgAssert.nullArg(workflowresultlistener, "listener");
        execMgr.exec((WorkflowExecSetImpl)workflowexecset, map, workflowresultlistener);
    }

    public void exec(WorkflowExecSet workflowexecset, Map map, WorkflowResultListener workflowresultlistener, ThreadPool threadpool)
    {
        ArgAssert.nullArg(workflowexecset, "execSet");
        ArgAssert.nullArg(workflowresultlistener, "listener");
        ArgAssert.nullArg(threadpool, "threadPool");
        execMgr.exec((WorkflowExecSetImpl)workflowexecset, map, workflowresultlistener, threadpool);
    }

    protected void onStart()
        throws ServiceProviderException
    {
        logger = XtierKernel.getInstance().log().getLogger("workflow");
        try
        {
            parseXmlConfig("xtier_workflow.xml", new HashSet(), new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.WF.ERR1"), saxexception);
        }
        if(region == null)
            throw new ServiceProviderException(L10n.format("SRVC.WF.ERR2", getRegionName()));
        try
        {
            refResolver.resolve();
        }
        catch(IocDescriptorException iocdescriptorexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.WF.ERR3"), iocdescriptorexception);
        }
        refResolver = null;
        if(region.isParseValidation())
            try
            {
                (new WorkflowParseValidator(region, logger)).validate();
            }
            catch(WorkflowParseValidationException workflowparsevalidationexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.WF.PARSEVAL.ERR1"), workflowparsevalidationexception);
            }
        execMgr = new WorkflowExecManager(logger, region.isRunValidation());
    }

    protected void onStop()
        throws ServiceProviderException
    {
        execMgr.stop();
    }

    private void parseXmlConfig(String s, Set set, Set set1)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(!$assertionsDisabled && set == null)
            throw new AssertionError();
        if(!$assertionsDisabled && set1 == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            logger.warning(L10n.format("SRVC.WF.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        String s2 = getRegionName();
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(set1, s2) {

                private XmlLocation getXmlLoc()
                {
                    return new XmlLocation(getLocator());
                }

                private WorkflowRef createRef(String s3, String s4, String s5, WorkflowExecSetImpl workflowexecsetimpl)
                    throws SAXException
                {
                    if(s3 == null)
                        s3 = group.getName();
                    String s6 = s4;
                    boolean flag = false;
                    if(s6 == null)
                    {
                        s6 = s5;
                        if(s6 == null)
                            throw createSaxErr(L10n.format("SRVC.WF.ERR11"));
                        flag = true;
                    } else
                    if(s5 != null)
                        throw createSaxErr(L10n.format("SRVC.WF.ERR11"));
                    return new WorkflowRef(s3, s6, workflowexecsetimpl, flag);
                }

                protected void onTagStart(String s3, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s3.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), includes, regions);
                    else
                    if(s3.equals("region"))
                    {
                        xmlRegion = new WorkflowRegion(getXmlLoc(), xmlattrinterceptor.getValue("name"));
                        if(regions.contains(xmlRegion))
                            throw createSaxErr(L10n.format("SRVC.WF.ERR4", xmlRegion.getName()));
                        regions.add(xmlRegion);
                        xmlRefResolver = new RefResolver();
                    } else
                    if(s3.equals("validation"))
                    {
                        xmlRegion.setRunValidation(parseBoolean(xmlattrinterceptor.getValue("run-time")));
                        xmlRegion.setParseValidation(parseBoolean(xmlattrinterceptor.getValue("parse-time")));
                    } else
                    if(s3.equals("group"))
                    {
                        group = new WorkflowGroup(getXmlLoc(), xmlattrinterceptor.getValue("name"));
                        if(xmlRegion.hasGroup(group.getName()))
                            createSaxErr(L10n.format("SRVC.WF.ERR12", group.getName()));
                        xmlRegion.addGroup(group);
                    } else
                    if(s3.equals("rule"))
                    {
                        rule = new WorkflowRuleImpl(getXmlLoc(), group.getName(), xmlattrinterceptor.getValue("name"));
                        if(execSet == null)
                        {
                            if(group.hasExecutable(rule))
                                throw createSaxErr(L10n.format("SRVC.WF.ERR5", rule.getGroup(), rule.getName()));
                            group.addExecutable(rule);
                        } else
                        {
                            if(execSet.hasPrivateRule(rule))
                                throw createSaxErr(L10n.format("SRVC.WF.ERR5", rule.getGroup(), rule.getName()));
                            execSet.addPrivateRule(rule);
                        }
                    } else
                    if(s3.equals("execset"))
                    {
                        execSet = new WorkflowExecSetImpl(getXmlLoc(), group.getName(), xmlattrinterceptor.getValue("name"));
                        WorkflowRef workflowref = createRef(xmlattrinterceptor.getValue("entry-group"), xmlattrinterceptor.getValue("entry-rule"), xmlattrinterceptor.getValue("entry-execset"), execSet);
                        if(group.hasExecutable(execSet))
                            throw createSaxErr(L10n.format("SRVC.WF.ERR7", execSet.getGroup(), execSet.getName()));
                        group.addExecutable(execSet);
                        xmlRefResolver.addEntryRef(workflowref, execSet);
                    } else
                    if(s3.equals("success") || s3.equals("failure"))
                    {
                        boolean flag = s3.equals("success");
                        output = new WorkflowRuleOutput(getXmlLoc(), xmlattrinterceptor.getValue("code"), flag);
                        if(rule.hasOutput(output.getCode()))
                            throw createSaxErr(L10n.format("SRVC.WF.ERR8", output.getCode()));
                        rule.addOutput(output);
                    } else
                    if(s3.equals("param"))
                    {
                        WorkflowParam workflowparam = new WorkflowParam(getXmlLoc(), xmlattrinterceptor.getValue("name"), parseBoolean(xmlattrinterceptor.getValue("optional")));
                        if(output != null)
                        {
                            if(output.hasParam(workflowparam.getName()))
                                throw createSaxErr(L10n.format("SRVC.WF.ERR9", workflowparam.getName()));
                            output.addParam(workflowparam);
                        } else
                        {
                            if(rule.hasOutput(workflowparam.getName()))
                                throw createSaxErr(L10n.format("SRVC.WF.ERR9", workflowparam.getName()));
                            rule.addInput(workflowparam);
                        }
                    } else
                    if(s3.equals("event"))
                    {
                        event = new WorkflowEvent(getXmlLoc());
                        event.setExecSet(execSet);
                        if(execSet.hasEvent(event))
                            throw createSaxErr(L10n.format("SRVC.WF.ERR10", execSet.getGroup(), execSet.getName()));
                        WorkflowRef workflowref1 = createRef(xmlattrinterceptor.getValue("group"), xmlattrinterceptor.getValue("rule"), xmlattrinterceptor.getValue("execset"), execSet);
                        execSet.addEvent(workflowref1, event);
                        xmlRefResolver.addEventRef(workflowref1, event);
                    } else
                    if(s3.equals("if"))
                    {
                        condition = new WorkflowCondition(getXmlLoc(), xmlattrinterceptor.getValue("code"));
                        condition.setExecSet(execSet);
                        if(event.hasCondition(condition.getCode()))
                            throw createSaxErr(L10n.format("SRVC.WF.ERR6", condition.getCode()));
                        event.addCondition(condition);
                    } else
                    if(s3.equals("action"))
                    {
                        String s4 = xmlattrinterceptor.getValue("type");
                        boolean flag1 = s4.equals("exit");
                        WorkflowAction workflowaction = new WorkflowAction(getXmlLoc(), flag1);
                        workflowaction.setExecSet(execSet);
                        if(flag1)
                        {
                            String s5 = xmlattrinterceptor.getValue("code");
                            workflowaction.setExitCode(s5);
                            execSet.addExitCode(s5);
                        } else
                        {
                            WorkflowRef workflowref2 = createRef(xmlattrinterceptor.getValue("group"), xmlattrinterceptor.getValue("rule"), xmlattrinterceptor.getValue("execset"), execSet);
                            xmlRefResolver.addActionRef(workflowref2, workflowaction);
                        }
                        condition.setAction(workflowaction);
                    }
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(rule != null)
                        xmlRefResolver.addIocRef(new WorkflowRef(rule.getGroup(), rule.getName(), execSet, false), iocdescriptor);
                }

                protected void onTagEnd(String s3)
                {
                    if(s3.equals("region"))
                    {
                        if(xmlRegion.getName().equals(initRegion))
                        {
                            region = xmlRegion;
                            refResolver = xmlRefResolver;
                        }
                        xmlRegion = null;
                        xmlRefResolver = null;
                    } else
                    if(s3.equals("group"))
                        group = null;
                    else
                    if(s3.equals("rule"))
                        rule = null;
                    else
                    if(s3.equals("execset"))
                        execSet = null;
                    else
                    if(s3.equals("success") || s3.equals("failure"))
                        output = null;
                    else
                    if(s3.equals("event"))
                        event = null;
                    else
                    if(s3.equals("if"))
                        condition = null;
                }

                private WorkflowRegion xmlRegion;
                private WorkflowGroup group;
                private WorkflowRuleImpl rule;
                private WorkflowExecSetImpl execSet;
                private WorkflowRuleOutput output;
                private WorkflowEvent event;
                private WorkflowCondition condition;
                private RefResolver xmlRefResolver;


                throws SAXException
            {
                super(final_s, final_s1);
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
            throw new SAXException(L10n.format("SRVC.WF.ERR14", s1), ioexception);
        }
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

    private Logger logger;
    private WorkflowRegion region;
    private WorkflowExecManager execMgr;
    private RefResolver refResolver;
    static final boolean $assertionsDisabled; /* synthetic field */
    static Class class$com$fitechlabs$xtier$services$workflow$WorkflowRuleBody; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowServiceImpl.class).desiredAssertionStatus();
    }




}
