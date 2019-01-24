// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptor;
import com.fitechlabs.xtier.kernel.ioc.IocDescriptorException;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderAdapter;
import com.fitechlabs.xtier.kernel.spi.ServiceProviderException;
import com.fitechlabs.xtier.kernel.xml.XmlAttrInterceptor;
import com.fitechlabs.xtier.kernel.xml.XmlSaxHandler;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.*;
import com.fitechlabs.xtier.services.jobs.scalers.JobsLinearTimeScaler;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.services.objpool.ObjectPoolService;
import com.fitechlabs.xtier.utils.ArgAssert;
import com.fitechlabs.xtier.utils.Utils;
import com.fitechlabs.xtier.utils.xml.XmlUtils;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;

// Referenced classes of package com.fitechlabs.xtier.services.jobs.impl:
//            JobDefaultCalendar, JobImpl, JobsGroupImpl, JobManager

public class JobsServiceImpl extends ServiceProviderAdapter
    implements JobsService
{
    private class XmlRegion
    {

        String getName()
        {
            return name;
        }

        void addJob(XmlJob xmljob)
        {
            if(!$assertionsDisabled && xmljob == null)
                throw new AssertionError();
            if(!$assertionsDisabled && jobs.containsKey(xmljob.getName()))
            {
                throw new AssertionError();
            } else
            {
                jobs.put(xmljob.getName(), xmljob);
                return;
            }
        }

        boolean hasJob(String s)
        {
            return jobs.containsKey(s);
        }

        Map getJobs()
        {
            return jobs;
        }

        void addGroup(XmlGroup xmlgroup)
        {
            if(!$assertionsDisabled && xmlgroup == null)
                throw new AssertionError();
            if(!$assertionsDisabled && groups.containsKey(xmlgroup.getName()))
            {
                throw new AssertionError();
            } else
            {
                groups.put(xmlgroup.getName(), xmlgroup);
                return;
            }
        }

        boolean hasGroup(String s)
        {
            return groups.containsKey(s);
        }

        Map getGroups()
        {
            return groups;
        }

        void setThreadPoolName(String s)
        {
            threadPoolName = s;
        }

        public void setScalerIoc(IocDescriptor iocdescriptor)
        {
            scalerIoc = iocdescriptor;
        }

        public IocDescriptor getScalerIoc()
        {
            return scalerIoc;
        }

        public void setStoreIoc(IocDescriptor iocdescriptor)
        {
            storeIoc = iocdescriptor;
        }

        public IocDescriptor getStoreIoc()
        {
            return storeIoc;
        }

        String getThreadPoolName()
        {
            return threadPoolName;
        }

        boolean isRestoreOnStart()
        {
            return restoreOnStart;
        }

        boolean isStoreOnShutdown()
        {
            return storeOnShutdown;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML region [name=" + name + ", thread-pool-name=" + threadPoolName + ']';
        }

        private String name;
        private Map jobs;
        private Map groups;
        private String threadPoolName;
        private IocDescriptor scalerIoc;
        private IocDescriptor storeIoc;
        private boolean restoreOnStart;
        private boolean storeOnShutdown;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlRegion(String s, boolean flag, boolean flag1)
        {
            super();
            jobs = new HashMap();
            groups = new HashMap();
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                name = s;
                restoreOnStart = flag;
                storeOnShutdown = flag1;
                return;
            }
        }
    }

    private class XmlGroup
    {

        String getName()
        {
            return name;
        }

        void setName(String s)
        {
            name = s;
        }

        void addJob(String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                jobs.add(s);
                return;
            }
        }

        boolean hasJob(String s)
        {
            if(!$assertionsDisabled && s == null)
                throw new AssertionError();
            else
                return jobs.contains(s);
        }

        List getJobs()
        {
            return jobs;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "XML jobs group [name=" + name + ", jobs=" + jobs + ']';
        }

        private String name;
        private ArrayList jobs;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlGroup(String s)
        {
            super();
            jobs = new ArrayList();
            name = s;
        }
    }

    private class XmlJob
    {

        public IocDescriptor getBodyIoc()
        {
            return bodyIoc;
        }

        public void setBodyIoc(IocDescriptor iocdescriptor)
        {
            bodyIoc = iocdescriptor;
        }

        public IocDescriptor getCalendarIoc()
        {
            return calIoc;
        }

        public void setCalendarIoc(IocDescriptor iocdescriptor)
        {
            calIoc = iocdescriptor;
        }

        public IocDescriptor getSchedulerIoc()
        {
            return schedulerIoc;
        }

        public void setSchedulerIoc(IocDescriptor iocdescriptor)
        {
            schedulerIoc = iocdescriptor;
        }

        public String getName()
        {
            return name;
        }

        public boolean isAutostart()
        {
            return autostart;
        }

        public boolean isDeleteOnFinished()
        {
            return deleteOnFinished;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Xml job [name=" + name + ", autostart=" + autostart + ']';
        }

        private String name;
        private boolean autostart;
        private boolean deleteOnFinished;
        private IocDescriptor bodyIoc;
        private IocDescriptor schedulerIoc;
        private IocDescriptor calIoc;
        static final boolean $assertionsDisabled; /* synthetic field */


        XmlJob(String s, boolean flag, boolean flag1)
        {
            super();
            name = s;
            autostart = flag;
            deleteOnFinished = flag1;
        }
    }


    public JobsServiceImpl()
    {
    }

    protected void onStart()
        throws ServiceProviderException
    {
        log = XtierKernel.getInstance().log().getLogger("jobs");
        HashMap hashmap = new HashMap();
        try
        {
            parseXmlConfig("xtier_jobs.xml", hashmap, new HashSet());
        }
        catch(SAXException saxexception)
        {
            throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR1"), saxexception);
        }
        XmlRegion xmlregion = (XmlRegion)hashmap.get(getRegionName());
        if(xmlregion == null)
            throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR2", getRegionName()));
        Map map = xmlregion.getJobs();
        Map map1 = xmlregion.getGroups();
        HashMap hashmap1 = new HashMap(map.size());
        HashMap hashmap2 = new HashMap(map1.size());
        IocDescriptor iocdescriptor = xmlregion.getScalerIoc();
        Object obj = null;
        if(iocdescriptor == null)
            obj = new JobsLinearTimeScaler();
        else
            try
            {
                obj = (JobsTimeScaler)iocdescriptor.createNewObj(com.fitechlabs.xtier.services.jobs.JobsTimeScaler.class);
            }
            catch(IocDescriptorException iocdescriptorexception)
            {
                throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR15"), iocdescriptorexception);
            }
        IocDescriptor iocdescriptor1 = xmlregion.getStoreIoc();
        JobsStore jobsstore = null;
        if(iocdescriptor1 != null)
            try
            {
                jobsstore = (JobsStore)iocdescriptor1.createNewObj(com.fitechlabs.xtier.services.jobs.JobsStore.class);
            }
            catch(IocDescriptorException iocdescriptorexception1)
            {
                throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR17"), iocdescriptorexception1);
            }
        XmlJob xmljob;
        JobImpl jobimpl;
        for(Iterator iterator = map.values().iterator(); iterator.hasNext(); jobimpl.setDeleteOnFinished(xmljob.isDeleteOnFinished()))
        {
            xmljob = (XmlJob)iterator.next();
            String s = xmljob.getName();
            JobBody jobbody = null;
            try
            {
                jobbody = (JobBody)xmljob.getBodyIoc().createNewObj(com.fitechlabs.xtier.services.jobs.JobBody.class);
            }
            catch(IocDescriptorException iocdescriptorexception2)
            {
                throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR3", s), iocdescriptorexception2);
            }
            JobScheduler jobscheduler = null;
            try
            {
                jobscheduler = (JobScheduler)xmljob.getSchedulerIoc().createNewObj(com.fitechlabs.xtier.services.jobs.JobScheduler.class);
            }
            catch(IocDescriptorException iocdescriptorexception3)
            {
                throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR4", s), iocdescriptorexception3);
            }
            IocDescriptor iocdescriptor2 = xmljob.getCalendarIoc();
            Object obj1 = null;
            if(iocdescriptor2 == null)
                obj1 = new JobDefaultCalendar();
            else
                try
                {
                    obj1 = (JobCalendar)iocdescriptor2.createNewObj(com.fitechlabs.xtier.services.jobs.JobCalendar.class);
                }
                catch(IocDescriptorException iocdescriptorexception4)
                {
                    throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR5", s), iocdescriptorexception4);
                }
            jobimpl = new JobImpl(s, jobbody, jobscheduler, ((JobCalendar) (obj1)), ((JobsTimeScaler) (obj)), xmljob.isAutostart());
            hashmap1.put(s, jobimpl);
        }

        JobsGroupImpl jobsgroupimpl;
        for(Iterator iterator1 = map1.values().iterator(); iterator1.hasNext(); hashmap2.put(jobsgroupimpl.getName(), jobsgroupimpl))
        {
            XmlGroup xmlgroup = (XmlGroup)iterator1.next();
            String s1 = xmlgroup.getName();
            jobsgroupimpl = new JobsGroupImpl(s1);
            List list = xmlgroup.getJobs();
            for(int i = 0; i < list.size(); i++)
            {
                String s2 = (String)list.get(i);
                Job job = (Job)hashmap1.get(s2);
                if(job == null)
                    throw new ServiceProviderException(L10n.format("SRVC.JOBS.IMPL.ERR27", s2, s1));
                jobsgroupimpl.addJob(job);
            }

        }

        try
        {
            JobManager.start(xmlregion.getThreadPoolName(), hashmap1, hashmap2, ((JobsTimeScaler) (obj)), jobsstore, xmlregion.isRestoreOnStart(), xmlregion.isStoreOnShutdown());
        }
        catch(JobException jobexception)
        {
            throw new ServiceProviderException(jobexception.getMessage(), jobexception);
        }
    }

    protected void onStop()
        throws ServiceProviderException
    {
        JobManager.stop();
    }

    public String getName()
    {
        return "jobs";
    }

    private void parseXmlConfig(String s, Map map, Set set)
        throws SAXException
    {
        if(!$assertionsDisabled && s == null)
            throw new AssertionError();
        if(set.contains(s))
        {
            log.warning(L10n.format("SRVC.JOBS.IMPL.WRN1", s));
            return;
        }
        set.add(s);
        String s1 = "file:///" + Utils.makeValidPath(Utils.makeValidPath(getXtierRoot(), getConfigPath()), s);
        try
        {
            XmlUtils.makeSaxParser().parse(s1, new XmlSaxHandler(map, set) {

                protected void onTagStart(String s2, XmlAttrInterceptor xmlattrinterceptor)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        String s3 = xmlattrinterceptor.getValue("name");
                        if(regions.containsKey(s3))
                            throw createSaxErr(L10n.format("SRVC.JOBS.IMPL.ERR6", s3));
                        String s7 = xmlattrinterceptor.getValue("restore-on-start");
                        boolean flag = s7 != null ? parseBoolean(s7) : false;
                        String s9 = xmlattrinterceptor.getValue("store-on-shutdown");
                        boolean flag2 = s9 != null ? parseBoolean(s9) : false;
                        xmlRegion = new XmlRegion(s3, flag, flag2);
                    } else
                    if(s2.equals("time-scaler"))
                        isScaler = true;
                    else
                    if(s2.equals("store"))
                        isStore = true;
                    else
                    if(s2.equals("job"))
                    {
                        String s4 = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasJob(s4))
                            throw createSaxErr(L10n.format("SRVC.JOBS.IMPL.ERR7", xmlRegion.getName(), s4));
                        String s8 = xmlattrinterceptor.getValue("autostart");
                        boolean flag1 = s8 != null ? parseBoolean(s8) : false;
                        String s10 = xmlattrinterceptor.getValue("delete-on-finished");
                        boolean flag3 = s10 != null ? parseBoolean(s10) : false;
                        xmlJob = new XmlJob(s4, flag1, flag3);
                        xmlRegion.addJob(xmlJob);
                    } else
                    if(s2.equals("group"))
                    {
                        String s5 = xmlattrinterceptor.getValue("name");
                        if(xmlRegion.hasGroup(s5))
                            throw createSaxErr(L10n.format("SRVC.JOBS.IMPL.ERR29", xmlRegion.getName(), s5));
                        xmlGroup = new XmlGroup(s5);
                        xmlRegion.addGroup(xmlGroup);
                    } else
                    if(s2.equals("scheduler"))
                        isScheduler = true;
                    else
                    if(s2.equals("calendar"))
                        isCalendar = true;
                    else
                    if(s2.equals("body"))
                        isBody = true;
                    else
                    if(s2.equals("include-job"))
                    {
                        String s6 = xmlattrinterceptor.getValue("name");
                        if(xmlGroup.hasJob(s6))
                            throw createSaxErr(L10n.format("SRVC.JOBS.IMPL.ERR28", xmlGroup.getName(), s6));
                        xmlGroup.addJob(s6);
                    } else
                    if(s2.equals("include"))
                        parseXmlConfig(xmlattrinterceptor.getValue("path"), regions, includes);
                }

                protected void onIocDescriptor(IocDescriptor iocdescriptor)
                {
                    if(isCalendar)
                        xmlJob.setCalendarIoc(iocdescriptor);
                    else
                    if(isScheduler)
                        xmlJob.setSchedulerIoc(iocdescriptor);
                    else
                    if(isBody)
                        xmlJob.setBodyIoc(iocdescriptor);
                    else
                    if(isScaler)
                        xmlRegion.setScalerIoc(iocdescriptor);
                    else
                    if(isStore)
                        xmlRegion.setStoreIoc(iocdescriptor);
                }

                protected void onTagEnd(String s2)
                    throws SAXException
                {
                    if(s2.equals("region"))
                    {
                        regions.put(xmlRegion.getName(), xmlRegion);
                        xmlRegion = null;
                    } else
                    if(s2.equals("time-scaler"))
                        isScaler = false;
                    else
                    if(s2.equals("store"))
                        isStore = false;
                    else
                    if(s2.equals("thread-pool-name"))
                    {
                        String s3 = getPcdata().trim();
                        if(XtierKernel.getInstance().objpool().getThreadPool(s3) == null)
                            throw createSaxErr(L10n.format("SRVC.JOBS.IMPL.ERR14", s3));
                        xmlRegion.setThreadPoolName(s3);
                    } else
                    if(s2.equals("scheduler"))
                        isScheduler = false;
                    else
                    if(s2.equals("calendar"))
                        isCalendar = false;
                    else
                    if(s2.equals("body"))
                        isBody = false;
                    else
                    if(s2.equals("group"))
                        xmlGroup = null;
                }

                private XmlRegion xmlRegion;
                private XmlJob xmlJob;
                private XmlGroup xmlGroup;
                private boolean isBody;
                private boolean isCalendar;
                private boolean isScheduler;
                private boolean isScaler;
                private boolean isStore;


                throws SAXException
            {
                super(final_s, final_s1);
                xmlRegion = null;
                xmlJob = null;
                xmlGroup = null;
                isBody = false;
                isCalendar = false;
                isScheduler = false;
                isScaler = false;
                isStore = false;
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
            throw new SAXException(L10n.format("SRVC.JOBS.IMPL.ERR8", s1), ioexception);
        }
    }

    public Map getActiveJobs()
    {
        return JobManager.getActiveJobs();
    }

    public Job getJob(String s)
    {
        ArgAssert.nullArg(s, "name");
        return JobManager.getJob(s);
    }

    public Map getAllJobs()
    {
        return JobManager.getAllJobs();
    }

    public void schedule(String s, boolean flag)
        throws JobException
    {
        ArgAssert.nullArg(s, "jobName");
        JobManager.schedule(s, flag);
    }

    public void schedule(String s)
        throws JobException
    {
        ArgAssert.nullArg(s, "jobName");
        JobManager.schedule(s);
    }

    public Job addJob(String s, JobBody jobbody, JobScheduler jobscheduler, JobCalendar jobcalendar)
        throws JobException
    {
        ArgAssert.nullArg(s, "jobName");
        ArgAssert.nullArg(jobbody, "body");
        ArgAssert.nullArg(jobscheduler, "scheduler");
        if(jobcalendar == null)
            jobcalendar = new JobDefaultCalendar();
        return JobManager.addJob(s, jobbody, jobscheduler, jobcalendar);
    }

    public boolean cancel(String s)
    {
        ArgAssert.nullArg(s, "jobName");
        return JobManager.cancel(s);
    }

    public void cancelAll()
    {
        JobManager.cancelAll();
    }

    public boolean delete(String s)
    {
        ArgAssert.nullArg(s, "jobName");
        return JobManager.delete(s);
    }

    public void deleteAll()
    {
        JobManager.deleteAll();
    }

    public JobsTimeScaler getTimeScaler()
    {
        return JobManager.getTimeScaler();
    }

    public void setTimeScaler(JobsTimeScaler jobstimescaler)
    {
        ArgAssert.nullArg(jobstimescaler, "scaler");
        JobManager.setTimeScaler(jobstimescaler);
    }

    public JobsGroup getGroup(String s)
    {
        ArgAssert.nullArg(s, "grpName");
        return JobManager.getGroup(s);
    }

    public Map getAllGroups()
    {
        return JobManager.getAllGroups();
    }

    public void scheduleGroup(String s)
        throws JobException
    {
        ArgAssert.nullArg(s, "grpName");
        JobManager.scheduleGroup(s);
    }

    public void scheduleGroup(String s, boolean flag)
        throws JobException
    {
        ArgAssert.nullArg(s, "grpName");
        JobManager.scheduleGroup(s, flag);
    }

    public JobsGroup createGroup(String s)
        throws JobException
    {
        ArgAssert.nullArg(s, "grpName");
        return JobManager.createGroup(s);
    }

    public void cancellGroup(String s)
    {
        ArgAssert.nullArg(s, "grpName");
        JobManager.cancelGroup(s);
    }

    public void deleteGroup(String s, boolean flag)
    {
        ArgAssert.nullArg(s, "grpName");
        JobManager.deleteGroup(s, flag);
    }

    public JobsStore getStore()
    {
        return JobManager.getStore();
    }

    public void setStore(JobsStore jobsstore)
    {
        JobManager.setStore(jobsstore);
    }

    public void storeState()
        throws JobException
    {
        JobManager.storeState();
    }

    private Logger log;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobsServiceImpl.class).desiredAssertionStatus();
    }

}
