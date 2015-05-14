package javax.realtime;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public abstract class Clock {
	public static Clock getRealtimeClock() {
		return RealtimeClock.instance();
	}

}
