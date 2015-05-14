package javax.realtime;

import icecaptools.IcecapCompileMe;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public abstract class HighResolutionTime implements Comparable<HighResolutionTime> {
	Clock clock;
	long millis;
	int nanos;

	HighResolutionTime(long millis, int nanos, Clock clock) {
		if (!isNormalized(millis, nanos)) {
			setNormalized(millis, nanos);
		} else {
			this.millis = millis;
			this.nanos = nanos;
		}
		this.clock = clock;
	}

	private boolean isNormalized(long millis, int nanos) {
		return (millis >= 0L && (0 <= nanos && nanos < 1000000))
				|| (millis <= 0L && (-1000000 < nanos && nanos <= 0));
	}

	public final Clock getClock() {
		return this.clock;
	}

	public final long getMilliseconds() {
		return this.millis;
	}

	public final int getNanoseconds() {
		return this.nanos;
	}

	public void set(HighResolutionTime time) {
		if (time == null)
			throw new IllegalArgumentException("null parameter");
		if (this.getClass() != time.getClass())
			throw new ClassCastException("from HighResolutionTime :: set");

		this.millis = time.millis;
		this.nanos = time.nanos;
		this.clock = time.clock;
	}

	public void set(long millis) {
		this.millis = millis;
		this.nanos = 0;
	}

	public void set(long millis, int nanos) {
		if (!isNormalized(millis, nanos))
			setNormalized(millis, nanos);
		else {
			this.millis = millis;
			this.nanos = nanos;
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (millis ^ (millis >>> 32));
		result = prime * result + nanos;
		return result;
	}

	public boolean equals(HighResolutionTime time) {
		if (time == null)
			return false;

		return (this.getClass() == time.getClass()) && (this.millis == time.getMilliseconds())
				&& (this.nanos == time.getNanoseconds()) && (this.clock == time.getClock());
	}

	public boolean equals(Object object) {
		HighResolutionTime time = null;
		if (object instanceof HighResolutionTime)
			time = (HighResolutionTime) object;
		if (object instanceof AbsoluteTime)
			time = (AbsoluteTime) object;
		if (object instanceof RelativeTime)
			time = (RelativeTime) object;
		else
			time = null;
		if (time == null || object == null)
			return false;

		return (this.getClass() == time.getClass()) && (this.millis == time.getMilliseconds())
				&& (this.nanos == time.getNanoseconds()) && (this.clock == time.getClock());
	}

	public int compareTo(HighResolutionTime time) {
		if (time == null)
			throw new IllegalArgumentException();
		if (this.getClass() != time.getClass())
			throw new ClassCastException();
		if (this.clock != time.getClock())
			throw new IllegalArgumentException();

		if (this.millis < time.getMilliseconds())
			return -1;
		else if (this.millis > time.getMilliseconds())
			return 1;
		else if (this.nanos < time.getNanoseconds())
			return -1;
		else if (this.nanos > time.getNanoseconds())
			return 1;
		else
			return 0;
	}

	public String toString() {
		return ("(ms,ns) = (" + millis + ", " + nanos + ")");
	}

	static final int NANOS_PER_MILLI = 1000000;

	@IcecapCompileMe
	final void setNormalized(final long ms, final int ns) {

		millis = ms + ns / NANOS_PER_MILLI;
		nanos = ns % NANOS_PER_MILLI;
		if (millis > 0 && nanos < 0) {
			millis--; // millis >= 0
			nanos += NANOS_PER_MILLI;
		} else if (millis < 0 && nanos > 0) {
			millis++; // millis <= 0
			nanos -= NANOS_PER_MILLI;
		}
	}

}
