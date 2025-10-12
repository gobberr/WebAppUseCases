package com.webapp.backend.utils;

import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class HeapDumpUtil {
   private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
   private static volatile HotSpotDiagnosticMXBean hotspotMBean;

   public static void dumpHeap(String filePath, boolean live) {
       try {
           getHotspotMBean().dumpHeap(filePath, live);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   private static HotSpotDiagnosticMXBean getHotspotMBean() {
       if (hotspotMBean == null) {
           synchronized (HeapDumpUtil.class) {
               if (hotspotMBean == null) {
                   MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                   try {
                       hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(
                               server, HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
           }
       }
       return hotspotMBean;
   }
}