// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.cluster.filters;

import com.fitechlabs.xtier.services.cluster.*;
import java.util.*;

// Referenced classes of package com.fitechlabs.xtier.services.cluster.filters:
//            ClusterFilterAdapter

public class ClusterGroupFilter extends ClusterFilterAdapter
{

    public ClusterGroupFilter()
    {
        grpName = null;
    }

    public ClusterGroupFilter(String s)
    {
        grpName = null;
        grpName = s;
    }

    public ClusterGroupFilter(String s, Map map)
    {
        grpName = null;
        grpName = s;
        grpProps = map;
    }

    public ClusterGroupFilter(String s, ClusterFilter clusterfilter)
    {
        this(s, null, clusterfilter);
    }

    public ClusterGroupFilter(String s, Map map, ClusterFilter clusterfilter)
    {
        super(clusterfilter);
        grpName = null;
        grpName = s;
        grpProps = map;
    }

    public String getGroupName()
    {
        return grpName;
    }

    public Map getGroupProps()
    {
        return grpProps;
    }

    public void setGroupName(String s)
    {
        grpName = s;
    }

    public void setGroupProps(Map map)
    {
        grpProps = map;
    }

    public boolean accept(ClusterNode clusternode)
    {
label0:
        {
label1:
            {
                if(grpName == null)
                    return true;
                ClusterFilter clusterfilter = getNested();
                if(clusterfilter != null && !clusterfilter.accept(clusternode))
                    return false;
                if(!clusternode.isGroupMember(grpName))
                    break label0;
                if(grpProps == null || grpProps.isEmpty())
                    break label1;
                Map map = clusternode.getGroupMembership(grpName).getGroupProps();
                if(map != null)
                {
                    Iterator iterator = grpProps.entrySet().iterator();
                    Map.Entry entry;
                    String s;
                    do
                    {
                        if(!iterator.hasNext())
                            break label1;
                        entry = (Map.Entry)iterator.next();
                        s = (String)map.get(entry.getKey());
                    } while(s != null && s.equals(entry.getValue()));
                    return false;
                } else
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private String grpName;
    private Map grpProps;
}
