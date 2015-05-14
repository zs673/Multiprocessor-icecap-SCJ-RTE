package javax.realtime;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public class RelativeTime extends HighResolutionTime {

	public RelativeTime() {
		this(0, 0);
	}

	public RelativeTime(long millis, int nanos) {
		this(millis, nanos, Clock.getRealtimeClock());
	}

	public RelativeTime(Clock clock) {
		this(0, 0, clock == null ? Clock.getRealtimeClock() : clock);
	}

	public RelativeTime(long millis, int nanos, Clock clock) {
		super(millis, nanos, clock == null ? Clock.getRealtimeClock() : clock);
	}

	public RelativeTime(RelativeTime time) {
		this();
		if (time != null) {
			millis = time.millis;
			nanos = time.nanos;
			clock = time.clock;
		} else
			throw new IllegalArgumentException();
	}

	public RelativeTime add(long millis, int nanos) {
		return new RelativeTime(this.millis + millis, this.nanos + nanos, this.clock);
	}

	public RelativeTime add(RelativeTime time) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (this.clock != time.clock)
			throw new IllegalArgumentException("clock mismatch");

		return new RelativeTime(this.millis + time.getMilliseconds(), this.nanos
				+ time.getNanoseconds(), time.getClock());
	}

	public RelativeTime add(long millis, int nanos, RelativeTime dest) {
		if (dest == null) {
			dest = new RelativeTime(this.millis + millis, this.nanos + nanos);
		} else {
			dest.set(this.millis + millis, this.nanos + nanos);
		}
		return dest;
	}

	public RelativeTime add(RelativeTime time, RelativeTime dest) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (this.clock != time.clock)
			throw new IllegalArgumentException("clock mismatch");

		return this.add(time.getMilliseconds(), time.getNanoseconds(), dest);
	}

	public RelativeTime subtract(RelativeTime time) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (this.clock != time.clock)
			throw new IllegalArgumentException("clock mismatch");

		return new RelativeTime(this.millis - time.getMilliseconds(), this.nanos
				- time.getNanoseconds(), this.clock);
	}

	public RelativeTime subtract(RelativeTime time, RelativeTime dest) {
		if (time == null)
			throw new IllegalArgumentException("time is null");
		if (this.clock != time.clock)
			throw new IllegalArgumentException("clock mismatch");

		if (dest == null) {
			dest = new RelativeTime(this.millis - time.getMilliseconds(), this.nanos
					- time.getNanoseconds(), this.clock);
		} else {
			dest.set(this.millis - time.getMilliseconds(), this.nanos - time.getNanoseconds());
		}
		return dest;
	}
}
