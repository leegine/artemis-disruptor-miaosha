// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.threads.ThreadPool;
import com.fitechlabs.xtier.services.workflow.*;
import com.fitechlabs.xtier.threads.SysThread;
import com.fitechlabs.xtier.threads.SysThreadGroup;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowResultImpl, WorkflowSessionImpl, WorkflowExecSetImpl, WorkflowRuleImpl,
//            WorkflowParam, WorkflowEvent, WorkflowExecutable, WorkflowCondition,
//            WorkflowAction, WorkflowRuleOutput

class WorkflowExecManager
{
    private static class ExecContext
    {

        void addOutput(String s)
        {
            outputs.add(s);
        }

        void addInput(String s)
        {
            inputs.add(s);
        }

        Set getOutputs()
        {
            return outputs;
        }

        Set getInputs()
        {
            return inputs;
        }

        private Set outputs;
        private Set inputs;

        private ExecContext()
        {
            outputs = new HashSet();
            inputs = new HashSet();
        }

    }


    WorkflowExecManager(Logger logger1, boolean flag)
    {
        listeners = new ArrayList();
        ctxMap = new HashMap();
        errorsMap = new HashMap();
        sysGrp = SysThreadGroup.getNewGroup("workflow");
        logger = logger1;
        isRunVal = flag;
    }

    void stop()
    {
        sysGrp.stopAndDestroy();
    }

    public boolean addListener(WorkflowListener workflowlistener)
    {
        List list = listeners;
        JVM INSTR monitorenter ;
        return listeners.add(workflowlistener);
        Exception exception;
        exception;
        throw exception;
    }

    public boolean removeListener(WorkflowListener workflowlistener)
    {
        List list = listeners;
        JVM INSTR monitorenter ;
        return listeners.remove(workflowlistener);
        Exception exception;
        exception;
        throw exception;
    }

    public List getAllListeners()
    {
        List list = listeners;
        JVM INSTR monitorenter ;
        return Collections.unmodifiableList(new ArrayList(listeners));
        Exception exception;
        exception;
        throw exception;
    }

    WorkflowResult exec(WorkflowExecSetImpl workflowexecsetimpl, Map map)
        throws WorkflowException
    {
        if(!$assertionsDisabled && workflowexecsetimpl == null)
            throw new AssertionError();
        else
            return exec(workflowexecsetimpl, createSession(map));
    }

    void exec(WorkflowExecSetImpl workflowexecsetimpl, Map map, WorkflowResultListener workflowresultlistener)
    {
        if(!$assertionsDisabled && workflowexecsetimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && workflowresultlistener == null)
        {
            throw new AssertionError();
        } else
        {
            (new SysThread(map, workflowresultlistener, workflowexecsetimpl) {

                protected void body()
                {
                    WorkflowSessionImpl workflowsessionimpl = createSession(inputs);
                    try
                    {
                        listener.onResult(exec(execSet, workflowsessionimpl));
                    }
                    catch(WorkflowException workflowexception)
                    {
                        listener.onError(execSet, workflowsessionimpl, workflowexception);
                    }
                }


            {
                super(final_s, final_i, final_threadgroup);
            }
            }
).start();
            return;
        }
    }

    void exec(final WorkflowExecSetImpl execSet, final Map inputs, final WorkflowResultListener listener, ThreadPool threadpool)
    {
        threadpool.addTask(new Runnable() {

            public void run()
            {
                WorkflowSessionImpl workflowsessionimpl = createSession(inputs);
                try
                {
                    listener.onResult(exec(execSet, workflowsessionimpl));
                }
                catch(WorkflowException workflowexception)
                {
                    listener.onError(execSet, workflowsessionimpl, workflowexception);
                }
            }


            {
                super();
            }
        }
);
    }

    WorkflowResult exec(WorkflowExecSetImpl workflowexecsetimpl, WorkflowSessionImpl workflowsessionimpl)
        throws WorkflowException
    {
        WorkflowResultImpl workflowresultimpl = new WorkflowResultImpl(workflowexecsetimpl, workflowsessionimpl);
        invokeExecSet(workflowexecsetimpl, workflowsessionimpl, workflowresultimpl);
        if(isRunVal)
            handleRunValErrors();
        return workflowresultimpl;
    }

    private WorkflowSessionImpl createSession(Map map)
    {
        if(!isRunVal)
            return new WorkflowSessionImpl(map);
        else
            return new WorkflowSessionImpl(map) {

                void addValue(String s, Object obj)
                {
                    ExecContext execcontext = (ExecContext)ctxMap.get(Thread.currentThread());
                    execcontext.addOutput(s);
                    super.addValue(s, obj);
                }

                Object getValue(String s)
                {
                    ExecContext execcontext = (ExecContext)ctxMap.get(Thread.currentThread());
                    execcontext.addInput(s);
                    return super.getValue(s);
                }


            {
                super(map);
            }
            }
;
    }

    private void beforeExecSet(WorkflowExecSetImpl workflowexecsetimpl, WorkflowSession workflowsession)
    {
        synchronized(listeners)
        {
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((WorkflowListener)listeners.get(i)).onExecSetStart(workflowexecsetimpl, workflowsession);

        }
    }

    private void afterExecSet(WorkflowExecSetImpl workflowexecsetimpl, WorkflowSession workflowsession, String s)
    {
        synchronized(listeners)
        {
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((WorkflowListener)listeners.get(i)).onExecSetEnd(workflowexecsetimpl, workflowsession, s);

        }
    }

    private void beforeRule(WorkflowExecSetImpl workflowexecsetimpl, WorkflowRuleImpl workflowruleimpl, WorkflowSession workflowsession)
    {
        synchronized(listeners)
        {
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((WorkflowListener)listeners.get(i)).onRuleStart(workflowexecsetimpl, workflowruleimpl, workflowsession);

        }
    }

    private void afterRule(WorkflowExecSetImpl workflowexecsetimpl, WorkflowRuleImpl workflowruleimpl, WorkflowSession workflowsession, String s, boolean flag)
    {
        synchronized(listeners)
        {
            int i = 0;
            for(int j = listeners.size(); i < j; i++)
                ((WorkflowListener)listeners.get(i)).onRuleEnd(workflowexecsetimpl, workflowruleimpl, workflowsession, s, flag);

        }
    }

    private void invokeExecSet(WorkflowExecSetImpl workflowexecsetimpl, WorkflowSession workflowsession, WorkflowResultImpl workflowresultimpl)
        throws WorkflowException
    {
        long l;
label0:
        {
            beforeExecSet(workflowexecsetimpl, workflowsession);
            l = System.currentTimeMillis();
            WorkflowEvent workflowevent = workflowexecsetimpl.getStartEvent();
            WorkflowAction workflowaction1;
            do
            {
                if(workflowevent.getCause().isExecSet())
                {
                    invokeExecSet((WorkflowExecSetImpl)workflowevent.getCause(), workflowsession, workflowresultimpl);
                    WorkflowAction workflowaction = workflowevent.getCondition(workflowresultimpl.getExitCode()).getAction();
                    if(!workflowaction.isTerminal())
                    {
                        workflowevent = workflowaction.getNextEvent();
                        continue;
                    }
                    workflowresultimpl.setExitCode(workflowaction.getExitCode());
                    break label0;
                }
                WorkflowRuleImpl workflowruleimpl = (WorkflowRuleImpl)workflowevent.getCause();
                beforeRule(workflowexecsetimpl, workflowruleimpl, workflowsession);
                String s = invokeRule(workflowruleimpl, workflowsession);
                boolean flag = workflowruleimpl.getOutput(s).isSuccessful();
                if(!flag)
                    workflowresultimpl.setIsSuccessful(false);
                afterRule(workflowexecsetimpl, workflowruleimpl, workflowsession, s, flag);
                workflowaction1 = workflowevent.getCondition(s).getAction();
                if(workflowaction1.isTerminal())
                    break;
                workflowevent = workflowaction1.getNextEvent();
            } while(true);
            workflowresultimpl.setExitCode(workflowaction1.getExitCode());
        }
        workflowexecsetimpl.onExec(System.currentTimeMillis() - l);
        afterExecSet(workflowexecsetimpl, workflowsession, workflowresultimpl.getExitCode());
    }

    private String invokeRule(WorkflowRuleImpl workflowruleimpl, WorkflowSession workflowsession)
        throws WorkflowException
    {
        String s;
        long l;
        ExecContext execcontext;
        l = System.currentTimeMillis();
        if(!isRunVal)
        {
            s = workflowruleimpl.getBody().invoke(workflowsession);
            break MISSING_BLOCK_LABEL_104;
        }
        execcontext = new ExecContext();
        ctxMap.put(Thread.currentThread(), execcontext);
        s = workflowruleimpl.getBody().invoke(workflowsession);
        ctxMap.remove(Thread.currentThread());
        break MISSING_BLOCK_LABEL_96;
        Exception exception;
        exception;
        ctxMap.remove(Thread.currentThread());
        throw exception;
        validateRuleCall(execcontext, workflowruleimpl, s);
        workflowruleimpl.onExec(System.currentTimeMillis() - l);
        return s;
    }

    private void validateRuleCall(ExecContext execcontext, WorkflowRuleImpl workflowruleimpl, String s)
    {
        Set set = execcontext.getInputs();
        Set set1 = execcontext.getOutputs();
        Iterator iterator = set.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s1 = (String)iterator.next();
            if(!workflowruleimpl.hasInput(s1))
                addError(workflowruleimpl.getXmlLoc(), L10n.format("SRVC.WF.RUNVAL.ERR1", s1));
        } while(true);
        iterator = workflowruleimpl.getInputs().values().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            WorkflowParam workflowparam = (WorkflowParam)iterator.next();
            if(!workflowparam.isOptional() && !set.contains(workflowparam.getName()))
                addError(workflowparam.getXmlLoc(), L10n.format("SRVC.WF.RUNVAL.ERR2", workflowparam.getName()));
        } while(true);
        boolean flag = false;
        if(s == null)
        {
            addError(workflowruleimpl.getXmlLoc(), L10n.format("SRVC.WF.RUNVAL.ERR3", workflowruleimpl.getGroup(), workflowruleimpl.getName(), workflowruleimpl.getBody()));
            flag = true;
        } else
        {
            WorkflowRuleOutput workflowruleoutput = workflowruleimpl.getOutput(s);
            if(workflowruleoutput == null)
            {
                addError(workflowruleimpl.getXmlLoc(), L10n.format("SRVC.WF.RUNVAL.ERR4", s));
                flag = true;
            } else
            {
                Iterator iterator1 = set1.iterator();
                do
                {
                    if(!iterator1.hasNext())
                        break;
                    String s2 = (String)iterator1.next();
                    if(!workflowruleoutput.hasParam(s2))
                        addError(workflowruleimpl.getXmlLoc(), L10n.format("SRVC.WF.RUNVAL.ERR5", s2));
                } while(true);
                iterator1 = workflowruleoutput.getParams().values().iterator();
                do
                {
                    if(!iterator1.hasNext())
                        break;
                    WorkflowParam workflowparam1 = (WorkflowParam)iterator1.next();
                    if(!workflowparam1.isOptional() && !set1.contains(workflowparam1.getName()))
                        addError(workflowparam1.getXmlLoc(), L10n.format("SRVC.WF.RUNVAL.ERR6", workflowparam1.getName()));
                } while(true);
            }
        }
        Set set2 = (Set)errorsMap.get(Thread.currentThread());
        int i = set2 != null ? set2.size() : 0;
        if(flag || i >= 30)
            handleRunValErrors();
    }

    private void handleRunValErrors()
    {
        Set set = (Set)errorsMap.remove(Thread.currentThread());
        int i = set != null ? set.size() : 0;
        if(i > 0)
        {
            logger.error("***");
            logger.error("*** " + L10n.format("SRVC.WF.RUNVAL.ERR7"));
            logger.error("*** " + L10n.format("SRVC.WF.RUNVAL.ERR8", new Integer(i)));
            int j = 1;
            for(Iterator iterator = set.iterator(); iterator.hasNext();)
            {
                String s = (String)iterator.next();
                logger.error("*** " + j + ") " + s);
                j++;
            }

            logger.error("***");
            throw new WorkflowRunValidationException(L10n.format("SRVC.WF.RUNVAL.ERR9"));
        } else
        {
            return;
        }
    }

    private void addError(XmlLocation xmllocation, String s)
    {
        Object obj = (Set)errorsMap.get(Thread.currentThread());
        if(obj == null)
        {
            obj = new HashSet();
            errorsMap.put(Thread.currentThread(), obj);
        }
        ((Set) (obj)).add(xmllocation.toStrLocation() + ": " + s);
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

    private static final int ERROR_THRESHOLD = 30;
    protected Logger logger;
    private List listeners;
    private boolean isRunVal;
    private Map ctxMap;
    private Map errorsMap;
    private SysThreadGroup sysGrp;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowExecManager.class).desiredAssertionStatus();
    }


}
