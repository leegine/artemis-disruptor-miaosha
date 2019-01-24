// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.fitechlabs.xtier.services.fs;

import java.io.File;

public interface FsTrackingCallback
{

    public abstract void onGC(File file);
}
