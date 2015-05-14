package javax.realtime;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;
import javax.scj.util.Priorities;

@SCJAllowed(Level.LEVEL_1)
public class PriorityScheduler extends Scheduler {

	@SCJAllowed(Level.LEVEL_1)
	public int getMaxPriority() {
		return Priorities.MAX_PRIORITY;
	}

	@SCJAllowed(Level.LEVEL_1)
	public int getMinPriority() {
		return Priorities.MIN_PRIORITY;
	}

}
