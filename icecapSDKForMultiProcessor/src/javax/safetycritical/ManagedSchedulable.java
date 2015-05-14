package javax.safetycritical;

import javax.realtime.Schedulable;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

@SCJAllowed
public interface ManagedSchedulable extends Schedulable {

	@SCJRestricted(Phase.INITIALIZE)
	public void register();

	@SCJAllowed(Level.SUPPORT)
	public void cleanUp();

	@SCJAllowed(Level.SUPPORT)
	public void signalTermination();
}
