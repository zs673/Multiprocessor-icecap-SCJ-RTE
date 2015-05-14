package javax.safetycritical;

import javax.realtime.AbsoluteTime;
import javax.realtime.AperiodicParameters;
import javax.realtime.Clock;
import javax.realtime.HighResolutionTime;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

@SCJAllowed(Level.LEVEL_1)
public abstract class OneShotEventHandler extends ManagedEventHandler {
	HighResolutionTime releaseTime;
	boolean deschedulePending = false;
	int state = 0;

	@SCJAllowed(Level.LEVEL_1)
	@SCJRestricted(Phase.INITIALIZE)
	public OneShotEventHandler(PriorityParameters priority, HighResolutionTime releaseTime,
			AperiodicParameters release, StorageParameters storage) {
		this(priority, releaseTime, release, storage, null);
	}

	public OneShotEventHandler(PriorityParameters priority, HighResolutionTime releaseTime,
			AperiodicParameters release, StorageParameters storage, String name) {
		super(priority, release, storage, name);

		if (releaseTime instanceof AbsoluteTime)
			this.releaseTime = new RelativeTime(Clock.getRealtimeClock());
		else if (releaseTime.getMilliseconds() < 0
				|| (releaseTime.getMilliseconds() == 0 && releaseTime.getNanoseconds() < 0))
			throw new IllegalArgumentException("release time < 0");
		else
			this.releaseTime = releaseTime;
		Services.setCeiling(this, this.priority.getPriority());
	}

	@SCJAllowed(Level.LEVEL_1)
	synchronized public boolean deschedule() {
		if (this.osProcess.executable.startTimer_c > 0 || state == 0) {
			deschedulePending = true;
			OSProcess.setTimerfd(this.osProcess.executable.startTimer_c, 0);
			return false;
		} else {
			return true;
		}

	}

	public final void cleanUp() {
		super.cleanUp();
	}

	@SCJAllowed(Level.INFRASTRUCTURE)
	@SCJRestricted(Phase.INITIALIZE)
	public final void register() {
		super.register();
	}

	public void scheduleNextReleaseTime(HighResolutionTime time) {
		if (time == null)
			this.deschedule();
		else {
			if (time instanceof AbsoluteTime) {
				releaseTime = new RelativeTime(0, 0);
			} else if (time instanceof RelativeTime) {
				if (releaseTime.getMilliseconds() < 0
						|| (releaseTime.getMilliseconds() == 0 && releaseTime.getNanoseconds() < 0))
					throw new IllegalArgumentException("release time < 0");
				releaseTime = time;
			} else {
				throw new IllegalArgumentException("wrong time form");
			}

			if (state == 0) {
				OSProcess.setTimerfd(this.osProcess.executable.startTimer_c, getStart());
			}
			if (state == 2) {
				OSProcess.setTimerfd(this.osProcess.executable.startTimer_c, getStart());
				fireNextRelease();
			}
		}
	}

	long getStart() {
		return releaseTime.getNanoseconds() + releaseTime.getMilliseconds() * 1000000;
	}

	synchronized void waitForNextRelease() {
		//		if (mission.terminationPending()) {
		//			mission.currMissSeq.decrementActiveCount();
		//			OSProcess.requestTermination_c(osProcess.executable);
		//			OSProcess.testCancel_c();
		//		}

		try {
			if (!this.mission.terminationPending())
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

	int getTimerfd() {
		return this.osProcess.executable.startTimer_c;
	}
}
