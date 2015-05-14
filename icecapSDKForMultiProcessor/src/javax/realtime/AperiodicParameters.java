package javax.realtime;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed(Level.LEVEL_1)
public class AperiodicParameters extends ReleaseParameters {

	public AperiodicParameters() {
		super();
	}

	public AperiodicParameters(RelativeTime deadline, AsyncEventHandler missHandler) {
		super(deadline, missHandler);
	}
}
