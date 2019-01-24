// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.info.impl;

import com.fitechlabs.xtier.utils.Utils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.fitechlabs.xtier.services.info.impl:
//            XmlConfigViewerMBean

public class XmlConfigViewer
    implements XmlConfigViewerMBean
{

    public XmlConfigViewer()
    {
    }

    XmlConfigViewer(String s, String s1)
    {
        xTierRoot = s;
        configPath = s1;
    }

    private void addRow(StringBuffer stringbuffer, String s)
    {
        stringbuffer.append("<tr>");
        stringbuffer.append("    <td><a href=\"file:///" + s + "\">" + s + "</a></td>");
        stringbuffer.append("</tr>");
    }

    public String viewXtierXmlConfig()
    {
        if(!$assertionsDisabled && (xTierRoot == null || configPath == null))
            throw new AssertionError();
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<table class=dotted aling=center cellspacing=1 cellpadding=3 border=0>");
        addRow(stringbuffer, Utils.makeValidPath(xTierRoot, "config/xtier_kernel.xml"));
        addRow(stringbuffer, Utils.makeValidPath(xTierRoot, "config/xtier_license.xml"));
        ArrayList arraylist = new ArrayList();
        scanXmlFiles(Utils.makeValidPath(xTierRoot, configPath), arraylist);
        int i = arraylist.size();
        for(int j = 0; j < i; j++)
            addRow(stringbuffer, (String)arraylist.get(j));

        return stringbuffer.append("</table>").toString();
    }

    private void scanXmlFiles(String s, List list)
    {
        File file = new File(s);
        if(file.exists())
        {
            File afile[] = file.listFiles(new FilenameFilter() {

                public boolean accept(File file1, String s1)
                {
                    return s1.endsWith(".xml");
                }


            {
                super();
            }
            }
);
            for(int i = 0; i < afile.length; i++)
                list.add(afile[i].getAbsolutePath());

            File afile1[] = file.listFiles(new FileFilter() {

                public boolean accept(File file1)
                {
                    return file1.isDirectory();
                }


            {
                super();
            }
            }
);
            for(int j = 0; j < afile1.length; j++)
                scanXmlFiles(afile1[j].getAbsolutePath(), list);

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

    private String xTierRoot;
    private String configPath;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(XmlConfigViewer.class).desiredAssertionStatus();
    }
}
