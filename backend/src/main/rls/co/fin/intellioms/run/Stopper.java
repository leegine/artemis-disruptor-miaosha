// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Stopper.java

package co.fin.intellioms.run;

import com.fitechlabs.xtier.jmxrmi.JmxRmiClient;
import com.fitechlabs.xtier.jmxrmi.JmxRmiClientException;
import java.io.PrintStream;

public class Stopper
{

    public Stopper()
    {
    }

    public static void main(String args[])
    {
        int rmiPort = 10991;
        for(int i = 0; i < args.length; i++)
            if(args[i].equalsIgnoreCase("-rmi") && i < args.length - 1)
                try
                {
                    rmiPort = Integer.parseInt(args[++i]);
                }
                catch(NumberFormatException e)
                {
                    showUsage();
                }
            else
                showUsage();

        JmxRmiClient jmx;
        try
        {
            jmx = new JmxRmiClient(rmiPort);
        }
        catch(JmxRmiClientException e)
        {
            System.err.println((new StringBuilder()).append("Unable to connect ot xTier JMX server: ").append(e.getMessage()).toString());
            System.exit(-1);
            return;
        }
        String objName = "IntelliOMS:name=RuleEngineServicesManager";
        try
        {
            jmx.invoke(objName, "terminate", null, null);
        }
        catch(JmxRmiClientException e) { }
        System.out.println("Done.");
    }

    private static void showUsage()
    {
        System.out.println("Usage: -rmi <rule-engine-rmi-port-number> ");
        System.exit(-1);
    }
}
