// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.workflow.impl;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.xml.XmlLocation;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.workflow.impl:
//            WorkflowGroup, WorkflowExecSetImpl, WorkflowEvent, WorkflowRuleImpl,
//            WorkflowRuleOutput, WorkflowParseValidationException, WorkflowRegion, WorkflowExecutable,
//            WorkflowCondition, WorkflowAction

class WorkflowParseValidator
{
    private static abstract class Visitor
    {

        void onExecSetEnter(WorkflowExecSetImpl workflowexecsetimpl)
            throws WorkflowParseValidationException
        {
        }

        void onEventEnter(WorkflowEvent workflowevent)
            throws WorkflowParseValidationException
        {
        }

        void onEventExit(WorkflowEvent workflowevent)
            throws WorkflowParseValidationException
        {
        }

        void onExecSetExit(WorkflowExecSetImpl workflowexecsetimpl)
            throws WorkflowParseValidationException
        {
        }

        private Visitor()
        {
        }

    }


    WorkflowParseValidator(WorkflowRegion workflowregion, Logger logger1)
    {
        errSet = new HashSet();
        wrnSet = new HashSet();
        if(!$assertionsDisabled && workflowregion == null)
            throw new AssertionError();
        if(!$assertionsDisabled && logger1 == null)
        {
            throw new AssertionError();
        } else
        {
            region = workflowregion;
            logger = logger1;
            return;
        }
    }

    void validate()
        throws WorkflowParseValidationException
    {
        validateRefs();
        validateAtLeastOneTerminalState();
        validateAllReturnCodesHandled();
        handleErrors();
        warnNonExistentReturnCodes();
        warnCyclesInExecPath();
        warnUnreachableEvents();
        handleWarnings();
    }

    private void validateRefs()
        throws WorkflowParseValidationException
    {
        breadthFirstSearch(new Visitor() {

            void onExecSetEnter(WorkflowExecSetImpl workflowexecsetimpl)
                throws WorkflowParseValidationException
            {
                if(workflowexecsetimpl.getStartEvent() == null)
                    addError(workflowexecsetimpl.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.ERR2"));
            }

            void onEventEnter(WorkflowEvent workflowevent)
                throws WorkflowParseValidationException
            {
                if(workflowevent.getCause() == null)
                    addError(workflowevent.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.ERR3"));
                Iterator iterator = workflowevent.getConditions().values().iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    WorkflowCondition workflowcondition = (WorkflowCondition)iterator.next();
                    WorkflowAction workflowaction = workflowcondition.getAction();
                    if(!workflowaction.isTerminal() && workflowaction.getNextEvent() == null)
                        addError(workflowaction.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.ERR4"));
                } while(true);
            }


                throws WorkflowParseValidationException
            {
                super();
            }
        }
);
    }

    private void validateAtLeastOneTerminalState()
        throws WorkflowParseValidationException
    {
        breadthFirstSearch(new Visitor() {

            void onExecSetEnter(WorkflowExecSetImpl workflowexecsetimpl)
                throws WorkflowParseValidationException
            {
                if(workflowexecsetimpl.getExitCodes().isEmpty())
                    addError(workflowexecsetimpl.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.ERR5", workflowexecsetimpl.getGroup(), workflowexecsetimpl.getName()));
            }


                throws WorkflowParseValidationException
            {
                super();
            }
        }
);
    }

    private void validateAllReturnCodesHandled()
        throws WorkflowParseValidationException
    {
        breadthFirstSearch(new Visitor() {

            void onEventEnter(WorkflowEvent workflowevent)
                throws WorkflowParseValidationException
            {
                WorkflowExecutable workflowexecutable = workflowevent.getCause();
                if(workflowexecutable != null)
                    if(workflowexecutable.isExecSet())
                    {
                        WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)workflowexecutable;
                        Iterator iterator = workflowexecsetimpl.getExitCodes().iterator();
                        do
                        {
                            if(!iterator.hasNext())
                                break;
                            String s = (String)iterator.next();
                            if(!workflowevent.hasCondition(s))
                                addError(workflowevent.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.ERR6", s));
                        } while(true);
                    } else
                    {
                        WorkflowRuleImpl workflowruleimpl = (WorkflowRuleImpl)workflowexecutable;
                        Iterator iterator1 = workflowruleimpl.getOutputs().values().iterator();
                        do
                        {
                            if(!iterator1.hasNext())
                                break;
                            WorkflowRuleOutput workflowruleoutput = (WorkflowRuleOutput)iterator1.next();
                            String s1 = workflowruleoutput.getCode();
                            if(!workflowevent.hasCondition(s1))
                                addError(workflowevent.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.ERR7", s1));
                        } while(true);
                    }
            }


                throws WorkflowParseValidationException
            {
                super();
            }
        }
);
    }

    private void warnNonExistentReturnCodes()
        throws WorkflowParseValidationException
    {
        breadthFirstSearch(new Visitor() {

            public void onEventEnter(WorkflowEvent workflowevent)
                throws WorkflowParseValidationException
            {
                WorkflowExecutable workflowexecutable = workflowevent.getCause();
                if(workflowexecutable != null)
                    if(workflowexecutable.isExecSet())
                    {
                        WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)workflowexecutable;
                        Set set = workflowexecsetimpl.getExitCodes();
                        Iterator iterator1 = workflowevent.getConditions().values().iterator();
                        do
                        {
                            if(!iterator1.hasNext())
                                break;
                            WorkflowCondition workflowcondition1 = (WorkflowCondition)iterator1.next();
                            String s1 = workflowcondition1.getCode();
                            if(!set.contains(s1))
                                addWarning(workflowcondition1.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.WRN1", s1));
                        } while(true);
                    } else
                    {
                        WorkflowRuleImpl workflowruleimpl = (WorkflowRuleImpl)workflowexecutable;
                        Iterator iterator = workflowevent.getConditions().values().iterator();
                        do
                        {
                            if(!iterator.hasNext())
                                break;
                            WorkflowCondition workflowcondition = (WorkflowCondition)iterator.next();
                            String s = workflowcondition.getCode();
                            if(!workflowruleimpl.hasOutput(s))
                                addWarning(workflowcondition.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.WRN2", s));
                        } while(true);
                    }
            }


            {
                super();
            }
        }
);
    }

    private void warnCyclesInExecPath()
        throws WorkflowParseValidationException
    {
        depthFirstSearch(new Visitor() {

            void onExecSetEnter(WorkflowExecSetImpl workflowexecsetimpl)
                throws WorkflowParseValidationException
            {
                visited = new HashSet();
            }

            void onEventEnter(WorkflowEvent workflowevent)
                throws WorkflowParseValidationException
            {
                if(visited.contains(workflowevent))
                {
                    WorkflowExecSetImpl workflowexecsetimpl = workflowevent.getExecSet();
                    addWarning(workflowevent.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.WRN3", workflowexecsetimpl.getGroup(), workflowexecsetimpl.getName()));
                } else
                {
                    visited.add(workflowevent);
                }
            }

            void onEventExit(WorkflowEvent workflowevent)
                throws WorkflowParseValidationException
            {
                visited.remove(workflowevent);
            }

            void onExecSetExit(WorkflowExecSetImpl workflowexecsetimpl)
                throws WorkflowParseValidationException
            {
                visited = null;
            }

            private Set visited;


            {
                super();
            }
        }
);
    }

    private void warnUnreachableEvents()
        throws WorkflowParseValidationException
    {
        depthFirstSearch(new Visitor() {

            void onExecSetEnter(WorkflowExecSetImpl workflowexecsetimpl)
                throws WorkflowParseValidationException
            {
                visited = new HashSet();
            }

            void onEventEnter(WorkflowEvent workflowevent)
                throws WorkflowParseValidationException
            {
                visited.add(workflowevent);
            }

            void onExecSetExit(WorkflowExecSetImpl workflowexecsetimpl)
                throws WorkflowParseValidationException
            {
                Map map = workflowexecsetimpl.getEvents();
                Iterator iterator = map.values().iterator();
                do
                {
                    if(!iterator.hasNext())
                        break;
                    WorkflowEvent workflowevent = (WorkflowEvent)iterator.next();
                    if(!visited.contains(workflowevent))
                        addWarning(workflowevent.getXmlLoc(), L10n.format("SRVC.WF.PARSEVAL.WRN4", workflowexecsetimpl.getGroup(), workflowexecsetimpl.getName()));
                } while(true);
                visited = null;
            }

            private Set visited;


            {
                super();
            }
        }
);
    }

    private void breadthFirstSearch(Visitor visitor)
        throws WorkflowParseValidationException
    {
        for(Iterator iterator = region.getGroups().values().iterator(); iterator.hasNext();)
        {
            WorkflowGroup workflowgroup = (WorkflowGroup)iterator.next();
            Iterator iterator1 = workflowgroup.getExecSets().values().iterator();
            while(iterator1.hasNext())
            {
                WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)iterator1.next();
                visitor.onExecSetEnter(workflowexecsetimpl);
                WorkflowEvent workflowevent;
                for(Iterator iterator2 = workflowexecsetimpl.getEvents().values().iterator(); iterator2.hasNext(); visitor.onEventExit(workflowevent))
                {
                    workflowevent = (WorkflowEvent)iterator2.next();
                    visitor.onEventEnter(workflowevent);
                }

                visitor.onExecSetExit(workflowexecsetimpl);
            }
        }

    }

    private void depthFirstSearch(Visitor visitor)
        throws WorkflowParseValidationException
    {
        for(Iterator iterator = region.getGroups().values().iterator(); iterator.hasNext();)
        {
            WorkflowGroup workflowgroup = (WorkflowGroup)iterator.next();
            Iterator iterator1 = workflowgroup.getExecSets().values().iterator();
            while(iterator1.hasNext())
            {
                WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)iterator1.next();
                visitor.onExecSetEnter(workflowexecsetimpl);
                dfsRun(visitor, new HashSet(), workflowexecsetimpl.getStartEvent());
                visitor.onExecSetExit(workflowexecsetimpl);
            }
        }

    }

    private void dfsRun(Visitor visitor, Set set, WorkflowEvent workflowevent)
        throws WorkflowParseValidationException
    {
        visitor.onEventEnter(workflowevent);
        if(set.contains(workflowevent))
            return;
        set.add(workflowevent);
        if(workflowevent.getCause().isExecSet())
        {
            WorkflowExecSetImpl workflowexecsetimpl = (WorkflowExecSetImpl)workflowevent.getCause();
            dfsRun(visitor, set, workflowexecsetimpl.getStartEvent());
            Set set1 = workflowexecsetimpl.getExitCodes();
            Iterator iterator = set1.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                WorkflowAction workflowaction = workflowevent.getCondition((String)iterator.next()).getAction();
                if(workflowaction != null && !workflowaction.isTerminal())
                    dfsRun(visitor, set, workflowaction.getNextEvent());
            } while(true);
        } else
        {
            WorkflowRuleImpl workflowruleimpl = (WorkflowRuleImpl)workflowevent.getCause();
            Map map = workflowruleimpl.getOutputs();
            Iterator iterator1 = map.values().iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                WorkflowRuleOutput workflowruleoutput = (WorkflowRuleOutput)iterator1.next();
                WorkflowAction workflowaction1 = workflowevent.getCondition(workflowruleoutput.getCode()).getAction();
                if(workflowaction1 != null && !workflowaction1.isTerminal())
                    dfsRun(visitor, set, workflowaction1.getNextEvent());
            } while(true);
        }
        set.remove(workflowevent);
        visitor.onEventExit(workflowevent);
    }

    private void addError(XmlLocation xmllocation, String s)
        throws WorkflowParseValidationException
    {
        errSet.add(xmllocation.toStrLocation() + ": " + s);
        if(errSet.size() >= 30)
            handleErrors();
    }

    private void addWarning(XmlLocation xmllocation, String s)
    {
        wrnSet.add(xmllocation.toStrLocation() + ": " + s);
    }

    private void handleErrors()
        throws WorkflowParseValidationException
    {
        int i = errSet.size();
        if(i > 0)
        {
            logger.error("***");
            logger.error("*** " + L10n.format("SRVC.WF.PARSEVAL.ERR8"));
            logger.error("*** " + L10n.format("SRVC.WF.PARSEVAL.ERR9", new Integer(i)));
            int j = 1;
            for(Iterator iterator = errSet.iterator(); iterator.hasNext();)
            {
                String s = (String)iterator.next();
                logger.error("*** " + j + ")" + s);
                j++;
            }

            logger.error("***");
            throw new WorkflowParseValidationException(L10n.format("SRVC.WF.PARSEVAL.ERR10"));
        } else
        {
            return;
        }
    }

    private void handleWarnings()
    {
        int i = wrnSet.size();
        if(i > 0)
        {
            logger.warning("***");
            logger.warning("*** " + L10n.format("SRVC.WF.PARSEVAL.WRN5", new Integer(i)));
            int j = 1;
            for(Iterator iterator = wrnSet.iterator(); iterator.hasNext();)
            {
                String s = (String)iterator.next();
                logger.warning("*** " + j + ") " + s);
                j++;
            }

            logger.warning("***");
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

    private static final int ERROR_THRESHOLD = 30;
    private Set errSet;
    private Set wrnSet;
    private Logger logger;
    private WorkflowRegion region;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(WorkflowParseValidator.class).desiredAssertionStatus();
    }


}
