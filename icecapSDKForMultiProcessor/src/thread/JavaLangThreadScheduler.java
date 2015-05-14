package thread;

import vm.Monitor;
import vm.Process;
import vm.Scheduler;

public class JavaLangThreadScheduler implements Scheduler {

	public static class JavaLangThreadMonitor extends vm.Monitor {
		private int mutex;
		private int conditionVariable;

		public JavaLangThreadMonitor(int ceiling) {
			initializeMutex(ceiling);
		}

		private native void initializeMutex(int ceiling);

		@Override
		public void lock() {
			//devices.Console.println("JavaLangThreadMonitor.lock");
			if (mutex != 0) {
				//				devices.Console.println("JavaLangThreadMonitor.lock.acquireMutex");
				acquireMutex();
			}
		}

		private native void acquireMutex();

		@Override
		public void unlock() {
			//devices.Console.println("JavaLangThreadMonitor.unlock");
			if (mutex != 0) {
				//				devices.Console.println("JavaLangThreadMonitor.unlock.releaseMutex");
				releaseMutex();
			}
		}

		private native void releaseMutex();

	}

	@Override
	public Process getNextProcess() {
		return null;
	}

	@Override
	public void wait(Object target) {
		//		devices.Console.println("thread.javalangthreads.wait");
		waitOnCondition(target);
	}

	private static native void waitOnCondition(Object target);

	@Override
	public void notify(Object target) {
		notifyOnCondition(target);
	}

	@Override
	public void notifyAll(Object target) {

	}

	private static native void notifyOnCondition(Object target);

	@Override
	public Monitor getDefaultMonitor() {
		//		return new JavaLangThreadMonitor(20);
		return null;
	}
}
