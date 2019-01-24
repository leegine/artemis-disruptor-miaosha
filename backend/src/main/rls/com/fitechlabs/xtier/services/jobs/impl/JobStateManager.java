// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.jobs.impl;

import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.jobs.JobException;
import com.fitechlabs.xtier.services.log.LogService;
import com.fitechlabs.xtier.services.log.Logger;
import com.fitechlabs.xtier.utils.Utils;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.jobs.impl:
//            JobImpl

public class JobStateManager
{

    public JobStateManager()
    {
    }

    public static void start(Map map)
    {
        if(!$assertionsDisabled && map == null)
        {
            throw new AssertionError();
        } else
        {
            declaredJobs = map;
            log = XtierKernel.getInstance().log().getLogger("jobs");
            return;
        }
    }

    public static void stop()
    {
        declaredJobs = null;
        log = null;
    }

    public static byte[] encodeState(Map map)
    {
        if(!$assertionsDisabled && map == null)
            throw new AssertionError();
        if(!$assertionsDisabled && declaredJobs == null)
            throw new AssertionError();
        int i = 0;
        for(Iterator iterator = map.values().iterator(); iterator.hasNext();)
            i += Utils.getUtf8StrSize(((JobImpl)iterator.next()).getName()) + 29;

        byte abyte0[] = new byte[i];
        int j = 0;
        Iterator iterator1 = map.values().iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            JobImpl jobimpl = (JobImpl)iterator1.next();
            if(!$assertionsDisabled && !jobimpl.isActive())
                throw new AssertionError();
            if(isStorable(jobimpl))
                j = encodeJob(jobimpl, abyte0, j);
        } while(true);
        return abyte0;
    }

    public static Map decodeState(byte abyte0[])
        throws JobException
    {
        HashMap hashmap = new HashMap();
        JobImpl jobimpl = null;
        int i = 0;
        do
        {
            if(i >= abyte0.length)
                break;
            i = decodeJob(abyte0, i, hashmap);
            if(jobimpl != null)
                hashmap.put(jobimpl.getName(), jobimpl);
        } while(true);
        return hashmap;
    }

    private static boolean isStorable(JobImpl jobimpl)
    {
        return declaredJobs.containsKey(jobimpl.getName());
    }

    private static int encodeJob(JobImpl jobimpl, byte abyte0[], int i)
    {
        if(!$assertionsDisabled && jobimpl == null)
            throw new AssertionError();
        if(!$assertionsDisabled && abyte0 == null)
            throw new AssertionError();
        if(!$assertionsDisabled && i < 0)
        {
            throw new AssertionError();
        } else
        {
            i = Utils.encodeUtf8Str(jobimpl.getName(), abyte0, i);
            i = Utils.encodeBool(jobimpl.isDeleteOnFinished(), abyte0, i);
            i = Utils.encodeInt32(jobimpl.getExecCount(), abyte0, i);
            i = Utils.encodeInt64(jobimpl.getLastInvokeTime(), abyte0, i);
            i = Utils.encodeInt64(jobimpl.getLastInvokeDuration(), abyte0, i);
            i = Utils.encodeInt64(jobimpl.getScheduleTime(), abyte0, i);
            return i;
        }
    }

    private static int decodeJob(byte abyte0[], int i, Map map)
        throws JobException
    {
        StringBuffer stringbuffer = new StringBuffer();
        i = Utils.decodeUtf8Str(abyte0, stringbuffer, i);
        String s = stringbuffer.toString();
        if(s == null || s.length() == 0)
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR16"));
        JobImpl jobimpl = (JobImpl)declaredJobs.get(s);
        if(jobimpl == null)
        {
            log.warning(L10n.format("SRVC.JOBS.IMPL.WRN2", s));
            return i + 29;
        }
        if(29 > abyte0.length - i)
        {
            throw new JobException(L10n.format("SRVC.JOBS.IMPL.ERR30"));
        } else
        {
            jobimpl.setDeleteOnFinished(Utils.decodeBool(abyte0, i));
            i++;
            jobimpl.setExecCount(Utils.decodeInt32(abyte0, i));
            i += 4;
            jobimpl.setLastInvokeTime(Utils.decodeInt64(abyte0, i));
            i += 8;
            jobimpl.setLastInvokeDuration(Utils.decodeInt64(abyte0, i));
            i += 8;
            jobimpl.setScheduleTime(Utils.decodeInt64(abyte0, i));
            i += 8;
            map.put(s, jobimpl);
            return i;
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

    private static final int JOB_PARAMS_SIZE = 29;
    private static Map declaredJobs;
    private static Logger log;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(JobStateManager.class).desiredAssertionStatus();
    }
}
