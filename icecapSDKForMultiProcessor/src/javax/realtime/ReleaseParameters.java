package javax.realtime;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public abstract class ReleaseParameters implements Cloneable {

	RelativeTime deadline;
	AsyncEventHandler missHandler;

	protected ReleaseParameters() {
	}

	@SCJAllowed(Level.LEVEL_1)
	protected ReleaseParameters(RelativeTime deadline, AsyncEventHandler missHandler) {
		this.deadline = (deadline == null ? null : new RelativeTime(deadline));
		this.missHandler = missHandler;
	}

	public Object clone() throws CloneNotSupportedException {
		ReleaseParameters clone = (ReleaseParameters) super.clone();
		return clone;
	}

	public RelativeTime getDeadline() {
		return deadline;
	}

	public AsyncEventHandler getMissHandler() {
		return missHandler;
	}
}
