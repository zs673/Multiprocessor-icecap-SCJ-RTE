package javax.safetycritical;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

@SCJAllowed(Level.SUPPORT)
@SCJRestricted(Phase.INITIALIZE)
public interface Safelet<MissionType extends Mission> {

	@SCJAllowed(Level.SUPPORT)
	@SCJRestricted(Phase.INITIALIZE)
	public MissionSequencer<MissionType> getSequencer();

	@SCJAllowed(Level.SUPPORT)
	public long immortalMemorySize();

	@SCJAllowed(Level.SUPPORT)
	@SCJRestricted(Phase.INITIALIZE)
	public void initializeApplication();

}
