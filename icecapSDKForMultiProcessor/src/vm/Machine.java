package vm;

import icecaptools.IcecapCVar;
import icecaptools.IcecapCompileMe;

public class Machine {
	public static final byte X86_64 = 1;
	public static final byte X86_32 = 2;
	public static final byte CR16_C = 3;
	public static final byte ATMEGA2560 = 4;

	@IcecapCVar
	public static byte architecture;

	private static vm.Scheduler currentScheduler;
	static boolean forCompile = false;

	static vm.Scheduler getCurrentScheduler() {
		return currentScheduler;
	}

	@IcecapCompileMe
	public static void setCurrentScheduler(vm.Scheduler sch) {
		if (forCompile) {
			/* Hack to force inclusion of the following methods.
			 * Lines below will never actually be executed */
			Monitor mon = Monitor.getDefaultMonitor();
			mon.attachMonitor(null);
			Monitor.wait(null);
			Monitor.notify(null);
			Monitor.lock(null);
			Monitor.unlock(null);
		}
		currentScheduler = sch;
	}

	//	@IcecapCompileMe
	//	public static void setMonitor() {
	//		Monitor mon = Monitor.getDefaultMonitor();
	//		mon.attachMonitor(null);
	//		Monitor.wait(null);
	//		Monitor.notify(null);
	//		Monitor.lock(null);
	//		Monitor.unlock(null);
	//	}
}
