package javax.realtime;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public class PeriodicParameters extends ReleaseParameters {

	RelativeTime start;
	RelativeTime period;

	public PeriodicParameters(RelativeTime start, RelativeTime period) {
		this(start, period, null, null);
	}

	@SCJAllowed(Level.LEVEL_1)
	public PeriodicParameters(RelativeTime start, RelativeTime period, RelativeTime deadline,
			AsyncEventHandler missHandler) {
		super(deadline == null ? period : deadline, missHandler);

		if (start == null)
			this.start = new RelativeTime();
		else
			this.start = new RelativeTime(start);

		if (period == null || period.millis < 0 || (period.millis == 0 && period.nanos == 0)
				|| start.clock != period.clock)
			throw new IllegalArgumentException("period is null or not vaild");
		if (deadline != null
				&& (deadline.millis < 0 || (deadline.millis == 0 && deadline.nanos == 0) || period.clock != deadline.clock))
			throw new IllegalArgumentException("deadline is null or not vaild");

		this.period = new RelativeTime(period);
	}

	public RelativeTime getPeriod() {
		return period;
	}

	public RelativeTime getStart() {
		return start;
	}
}
