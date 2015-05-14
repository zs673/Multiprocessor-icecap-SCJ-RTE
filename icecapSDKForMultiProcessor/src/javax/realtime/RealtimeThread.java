package javax.realtime;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed(Level.LEVEL_1)
public class RealtimeThread implements Schedulable {

	PriorityParameters priority;
	Runnable logic;

	protected RealtimeThread(PriorityParameters priority, Runnable logic) {
		if (priority == null)
			throw new IllegalArgumentException("priority is null");

		this.priority = priority;
		this.logic = logic;
	}

	public void run() {
		if (logic != null) {
			logic.run();
		}
	}
}
