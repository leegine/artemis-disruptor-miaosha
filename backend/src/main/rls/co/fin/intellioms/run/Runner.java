// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Runner.java

package co.fin.intellioms.run;

import co.fin.intellioms.util.ServicesManager;
import com.fitechlabs.xtier.kernel.KernelLifeCycleListener;
import com.fitechlabs.xtier.kernel.XtierKernel;
import com.fitechlabs.xtier.microkernel.std.StandardMicroKernel;
import com.fitechlabs.xtier.microkernel.std.StandardMicroKernelParams;
import com.fitechlabs.xtier.services.ioc.IocService;
import java.io.File;
import java.util.Locale;

public class Runner
{

    public Runner()
    {
    }

    private static void showUsage()
    {
        System.out.println("Usage: -base <path-to-oms-directory> -rmi <rmi-port-number> -html <html-adpater-port-number>");
        System.exit(-1);
    }

    public static void main(String args[])
    {
        String baseDir = null;
        int rmiPort = 10991;
        int htmlPort = 8081;
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].equalsIgnoreCase("-base") && i < args.length - 1)
            {
                baseDir = args[++i];
                continue;
            }
            if(args[i].equalsIgnoreCase("-rmi") && i < args.length - 1)
            {
                try
                {
                    rmiPort = Integer.parseInt(args[++i]);
                }
                catch(NumberFormatException e)
                {
                    showUsage();
                }
                continue;
            }
            if(args[i].equalsIgnoreCase("-html") && i < args.length - 1)
                try
                {
                    htmlPort = Integer.parseInt(args[++i]);
                }
                catch(NumberFormatException e)
                {
                    showUsage();
                }
            else
                showUsage();
        }

        if(baseDir == null)
            showUsage();
        File base = new File(baseDir);
        if(!base.exists() || !base.isDirectory())
        {
            System.out.println((new StringBuilder()).append("Path '").append(baseDir).append("' does not exist or is not a directory.").toString());
            showUsage();
        }
        System.out.println("Starting IntelliOMS rule engine, version intelli-oms 1.8.6.J6 (build 800 created on 20110427)....");
        try
        {
            startServer(base, rmiPort, htmlPort);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void startServer(File base, int rmiPort, int htmlPort)
        throws Exception
    {
        StandardMicroKernelParams params = new StandardMicroKernelParams();
        params.setRoot(base.getAbsolutePath());
        params.setAppRegion("oms");
        params.setLocale(Locale.US);
        params.setRmiRegPort(rmiPort);
        params.setJmxHtmlAdaptorPort(htmlPort);
        StandardMicroKernel.start(params);
        XtierKernel.getInstance().addKernelListener(new KernelLifeCycleListener() {

            public void stateChanged(int state)
            {
                if(state == 2)
                    (new Thread() {

                        public void run()
                        {
                            try
                            {
                                sleep(500L);
                            }
                            catch(InterruptedException e) { }
                            System.exit(0);
                        }

                        final _cls1 this$0;


                    {
                        this$0 = _cls1.this;
                        super();
                    }
                    }
).start();
            }

        }
);
        ServicesManager services = (ServicesManager)XtierKernel.getInstance().ioc().makeIocObject("services.manager");
        services.start();
    }
}
