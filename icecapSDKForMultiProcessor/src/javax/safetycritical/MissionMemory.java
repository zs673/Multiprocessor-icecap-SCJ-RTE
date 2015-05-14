package javax.safetycritical;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed(Level.INFRASTRUCTURE)
public final class MissionMemory extends ManagedMemory {
	Runnable runInitialize;
	Runnable runExecute;
	Runnable runCleanup;
	Mission m;

	MissionMemory(int size, ManagedMemory backingStoreProvider, String label) {
		super(size, backingStoreProvider.getRemainingBackingstoreSize(), backingStoreProvider,
				label);

		runInitialize = new Runnable() {
			public void run() {
				m.runInitialize();
			}
		};
		runExecute = new Runnable() {
			public void run() {
				m.runExecute();
			}
		};
		runCleanup = new Runnable() {
			public void run() {
				m.runCleanup(MissionMemory.this);
			}
		};
	}

	void enterToInitialize(final Mission mission) {
		m = mission;
		executeInArea(runInitialize);
	}

	void enterToExecute(final Mission mission) {
		m = mission;
		executeInArea(runExecute);
	}

	void enterToCleanup(final Mission mission) {
		m = mission;
		executeInArea(runCleanup);
		resetArea();
	}
}
