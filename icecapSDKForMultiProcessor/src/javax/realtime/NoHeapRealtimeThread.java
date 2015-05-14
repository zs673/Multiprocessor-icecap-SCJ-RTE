package javax.realtime;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed(Level.LEVEL_2)
public class NoHeapRealtimeThread extends RealtimeThread {

	protected NoHeapRealtimeThread(PriorityParameters priority, Runnable logic) {
		super(priority, logic);
	}

}
