package com.tisawesomeness.minecord.util;

import javax.management.MBeanServer;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;

import com.sun.management.HotSpotDiagnosticMXBean;

//This class was taken from https://blogs.oracle.com/sundararajan/entry/programmatically_dumping_heap_from_java
@SuppressWarnings("restriction")
public class HeapUtils {
	// This is the name of the HotSpot Diagnostic MBean
	private static final String HOTSPOT_BEAN_NAME =
		 "com.sun.management:type=HotSpotDiagnostic";

	// field to store the hotspot diagnostic MBean 
	private static volatile HotSpotDiagnosticMXBean hotspotMBean;

	/**
	 * Call this method from your application whenever you 
	 * want to dump the heap snapshot into a file.
	 *
	 * @param filename name of the heap dump file
	 * @param live flag that tells whether to dump only the live objects
	 */
	public static void dumpHeap(String filename, boolean live) {
		try {
			Files.deleteIfExists(new File(filename).toPath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// initialize hotspot diagnostic MBean
		initHotspotMBean();
		try {
			hotspotMBean.dumpHeap(filename, live);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// initialize the hotspot diagnostic MBean field
	private static void initHotspotMBean() {
		if (hotspotMBean == null) {
			synchronized (HeapUtils.class) {
				if (hotspotMBean == null) {
					hotspotMBean = getHotspotMBean();
				}
			}
		}
	}

	// get the hotspot diagnostic MBean from the
	// platform MBean server
	private static HotSpotDiagnosticMXBean getHotspotMBean() {
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean bean = 
				ManagementFactory.newPlatformMXBeanProxy(server,
				HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);
			return bean;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception exp) {
			throw new RuntimeException(exp);
		}
	}
}