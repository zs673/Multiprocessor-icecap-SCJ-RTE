package vm;

import icecaptools.IcecapCompileMe;

public abstract class Monitor {

	public abstract void lock();

	public abstract void unlock();

	protected Monitor() {
		//devices.Console.println("monitor created");
	}

	public void attach(Object target) {
		attachMonitor(target);
	}

	public native void attachMonitor(Object target);

	protected native static Object getAttachedMonitor(Object target);

	/* Method below is called by the VM to enter a monitor */
	@IcecapCompileMe
	public static void lock(Monitor monitor) {
		//devices.Console.println("vm.Monitor.lock");
		monitor.lock();
	}

	/* Method below is called by the VM to exit a monitor */
	@IcecapCompileMe
	public static void unlock(Monitor monitor) {
		//devices.Console.println("vm.Monitor.unlock");
		monitor.unlock();
	}

	@IcecapCompileMe
	public static void wait(Object target) {
		//		devices.Console.println("vm.monitor.wait");
		Scheduler sch = Machine.getCurrentScheduler();
		sch.wait(target);
	}

	@IcecapCompileMe
	public static void notify(Object target) {
		Scheduler sch = Machine.getCurrentScheduler();
		sch.notify(target);
	}

	@IcecapCompileMe
	public static void notifyAll(Object target) {
		Scheduler sch = Machine.getCurrentScheduler();
		sch.notifyAll(target);
	}

	@IcecapCompileMe
	public static Monitor getDefaultMonitor() {
		Scheduler sch = Machine.getCurrentScheduler();
		if (sch != null) {
			return sch.getDefaultMonitor();
		} else {
			return null;
		}
	}
}
