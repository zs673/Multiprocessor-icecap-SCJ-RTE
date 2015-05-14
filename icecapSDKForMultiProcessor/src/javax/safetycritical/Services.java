package javax.safetycritical;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

import thread.JavaLangThreadScheduler.JavaLangThreadMonitor;

@SCJAllowed
public class Services {

	@SCJAllowed(Level.LEVEL_1)
	public static void setCeiling(Object target, int ceiling) {
		JavaLangThreadMonitor monitor = new JavaLangThreadMonitor(ceiling);
		monitor.attach(target);
	}

	public static ManagedSchedulable getSchedulableObject() {
		return Launcher.helper.getManagedSchedulableByID(OSProcess.getThreadID());
	}

	public static AffinitySet[] getSchedulingAllocationDomains() {
		return AffinitySet.AFFINITY_SET;
	}

	public static String getNameOfCurrentMemoryArea() {
		return OSProcess.getCurrentMemoryArea().getName();
	}
}
