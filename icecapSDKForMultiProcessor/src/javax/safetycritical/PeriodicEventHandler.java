package javax.safetycritical;

import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public abstract class PeriodicEventHandler extends ManagedEventHandler {
	PeriodicParameters releaseP;

	public PeriodicEventHandler(PriorityParameters priority, PeriodicParameters release,
			StorageParameters storage) {
		this(priority, release, storage, null);
	}

	public PeriodicEventHandler(PriorityParameters priority, PeriodicParameters release,
			StorageParameters storage, String name) {
		super(priority, release, storage, name);
		releaseP = release;
	}

	public final void register() {
		super.register();
	}

	@Override
	public void run() {
		handleAsyncEvent();
	}

	long getStart() {
		return releaseP.getStart().getNanoseconds() + releaseP.getStart().getMilliseconds()
				* 1000000;
	}

	long getPeriod() {
		return releaseP.getPeriod().getNanoseconds() + releaseP.getPeriod().getMilliseconds()
				* 1000000;
	}
}
