package javax.safetycritical;

import javax.realtime.AperiodicParameters;
import javax.realtime.PriorityParameters;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

@SCJAllowed(Level.LEVEL_1)
public abstract class AperiodicEventHandler extends ManagedEventHandler {

	private boolean isRelased = false;

	@SCJAllowed(Level.LEVEL_1)
	@SCJRestricted(Phase.INITIALIZE)
	public AperiodicEventHandler(PriorityParameters priority, AperiodicParameters release,
			StorageParameters storage) {
		this(priority, release, storage, null);
	}

	public AperiodicEventHandler(PriorityParameters priority, AperiodicParameters release,
			StorageParameters storage, String name) {
		super(priority, release, storage, name);
		if (priority == null || release == null)
			throw new IllegalArgumentException("null argument");
		Services.setCeiling(this, this.priority.getPriority());
	}

	@SCJAllowed
	public final void release() {
		fireNextRelease();
		isRelased = true;
	}

	boolean isReleased() {
		return isRelased;
	}

	synchronized void waitForNextRelease() {
		//		if (mission.terminationPending()) {
		//			mission.currMissSeq.decrementActiveCount();
		//			OSProcess.requestTermination_c(osProcess.executable);
		//			OSProcess.testCancel_c();
		//		}

		try {
			if (!mission.terminationPending())
				wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (mission.terminationPending()) {
			mission.currMissSeq.decrementActiveCount();
			OSProcess.requestTermination_c(osProcess.executable);
			//			OSProcess.testCancel_c();
		}
	}

	synchronized void fireNextRelease() {
		notify();
	}

}
