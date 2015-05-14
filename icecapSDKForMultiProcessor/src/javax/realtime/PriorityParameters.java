package javax.realtime;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public class PriorityParameters extends SchedulingParameters {
	private int priority;

	public PriorityParameters(int priority) {
		;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int value) {
		priority = value;
	}
}
