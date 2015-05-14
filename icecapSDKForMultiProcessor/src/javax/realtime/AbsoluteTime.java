package javax.realtime;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public class AbsoluteTime extends HighResolutionTime {

	public AbsoluteTime() {
		this(0L, 0);
	}

	public AbsoluteTime(long millis, int nanos) {
		super(millis, nanos, Clock.getRealtimeClock());
	}

	public AbsoluteTime(AbsoluteTime time) {
		this();
		if (time == null)
			throw new IllegalArgumentException("null parameter");
		set(time);
	}

	public AbsoluteTime(long millis, int nanos, Clock clock) {
		super(millis, nanos, clock);
	}

	public AbsoluteTime(Clock clock) {
		this(0, 0, clock);
	}

	public AbsoluteTime add(long millis, int nanos) {
		return new AbsoluteTime(this.millis + millis, this.nanos + nanos, this.clock);
	}

	public AbsoluteTime add(long millis, int nanos, AbsoluteTime dest) {
		if (dest == null)
			dest = new AbsoluteTime(this.clock);
		dest.set(this.millis + millis, this.nanos + nanos);

		return dest;
	}

	public AbsoluteTime add(RelativeTime time) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (time.getClock() != getClock())
			throw new IllegalArgumentException("clock mismatch");

		return new AbsoluteTime(this.millis + time.getMilliseconds(), this.nanos
				+ time.getNanoseconds(), this.clock);
	}

	public AbsoluteTime add(RelativeTime time, AbsoluteTime dest) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (time.getClock() != getClock())
			throw new IllegalArgumentException("clock mismatch");

		return add(time.getMilliseconds(), time.getNanoseconds(), dest);
	}

	public RelativeTime subtract(AbsoluteTime time) {
		if (time == null) {
			throw new IllegalArgumentException("time is null");
		}
		if (this.clock != time.clock) {
			throw new IllegalArgumentException("clock mismatch");
		}
		return new RelativeTime(this.millis - time.getMilliseconds(), this.nanos
				- time.getNanoseconds(), this.clock);
	}

	public RelativeTime subtract(AbsoluteTime time, RelativeTime dest) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (this.clock != time.clock)
			throw new IllegalArgumentException("clock mismatch");

		if (dest != null) {
			dest.set(this.millis - time.getMilliseconds(), this.nanos - time.getNanoseconds());
		} else {
			dest = this.subtract(time);
		}

		return dest;
	}

	public AbsoluteTime subtract(RelativeTime time) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (time.getClock() != getClock())
			throw new IllegalArgumentException("clock mismatch");

		return new AbsoluteTime(this.millis - time.getMilliseconds(), this.nanos
				- time.getNanoseconds(), this.clock);
	}

	public AbsoluteTime subtract(RelativeTime time, AbsoluteTime dest) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (this.clock != time.clock)
			throw new IllegalArgumentException("clock mismatch");

		if (dest == null) {
			dest = new AbsoluteTime(this.millis - time.getMilliseconds(), this.nanos
					- time.getNanoseconds(), this.clock);
		} else {
			dest.set(this.millis - time.getMilliseconds(), this.nanos - time.getNanoseconds());
		}
		return dest;
	}

}
