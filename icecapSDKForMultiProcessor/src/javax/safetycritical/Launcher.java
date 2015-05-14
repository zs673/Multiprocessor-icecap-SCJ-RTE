package javax.safetycritical;

import javax.scj.util.Configuration;
import javax.scj.util.Const;

import thread.JavaLangThreadScheduler;
import vm.Machine;

public class Launcher implements Runnable {
	Safelet<?> app;
	static int level;
	static boolean useOSScheduler;
	static MissionHelper helper = null;

	public Launcher(Safelet<?> app, int level) {
		this(app, level, false);
	}

	public Launcher(Safelet<?> app, int level, boolean useOSScheduler) {
		if (level < 0 || level > 2 || app == null) {
			throw new IllegalArgumentException();
		}

		this.app = app;
		Launcher.level = level;
		Launcher.useOSScheduler = useOSScheduler;

		ManagedMemory.allocateBackingStore(Const.OVERALL_BACKING_STORE);

		ManagedMemory immortalMem = new ManagedMemory.ImmortalMemory((int) app.immortalMemorySize());
		immortalMem.executeInArea(this);
		immortalMem.removeArea();
	}

	public void run() {
		app.initializeApplication();
		initAffinfitySets();
		startwithOS();
	}

	protected void startwithOS() {
		Machine.setCurrentScheduler(new JavaLangThreadScheduler());
		OSProcess.initSpecificID();
		helper = new MissionHelper();
		MissionSequencer<?> outerMostMS = app.getSequencer();
		outerMostMS.privateMemory.enter(outerMostMS);
		outerMostMS.cleanUp();
	}

	private void initAffinfitySets() {
		if (Configuration.processors != null) {
			AffinitySet.checkAndInitAffinityByCustomized(Configuration.processors);
		} else {
			switch (Launcher.level) {
			case 0:
				AffinitySet.initAffinitySet_Level0();
				break;
			case 1:
				AffinitySet.initAffinitySet_Level1();
				break;
			case 2:
				AffinitySet.initAffinitySet_Level2();
				break;
			}
		}
	}

}
