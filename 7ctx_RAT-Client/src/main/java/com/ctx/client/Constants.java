/*
 * Copyright (c) 2021, 7ctx <https://github.com/7ctx/> 
 * Email: <7ctx@mail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ctx.client;

import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Constants 
{
    public static String osName = System.getProperty("os.name");
    public static String osType = System.getProperty("os.arch");
    public static String osVersion = System.getProperty("os.version");
    public static String homeDir = System.getProperty("user.home");
    public static String seperator = System.getProperty("file.separator");
    public static String desktopDir = homeDir + seperator + "Desktop";
    public static String tempDir = System.getProperty("java.io.tmpdir");
    public static Locale currentLocale = Locale.getDefault();
    public static String language = currentLocale.getDisplayLanguage();
    public static String country = System.getProperty("user.country");
    public static String country_full = currentLocale.getDisplayCountry();
    public static String username = System.getProperty("user.name");
    public static String javaVersion = System.getProperty("java.version");
    public static String userLanguage = System.getProperty("user.language");
   
    public static int availableProcessors = Runtime.getRuntime().availableProcessors();
    public static long totalPsychicalMemory = (long) (ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getTotalPhysicalMemorySize() / 1000000000.00);
    public static long diskSpace = (long) (new File("/").getTotalSpace() / 1000000000.00); // GigaBytes
    
    public static final ExecutorService executor = Executors.newWorkStealingPool(); // ExecutorService instance
}
