package javax.safetycritical;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;
import javax.scj.util.Const;

@SCJAllowed(Level.INFRASTRUCTURE)
class ManagedSchedulableSet {
	ManagedSchedulable[] managedSchObjects = new ManagedSchedulable[Const.DEFAULT_HANDLER_NUMBER];
	volatile int noOfRegistered = 0;

	int msCount;
	int activeCount;

	ManagedSchedulableSet() {
	}

	void addMS(ManagedSchedulable ms) {
		if (!contains(ms)) {
			managedSchObjects[noOfRegistered] = ms;
			noOfRegistered++;
			msCount++;
		}
	}

	boolean contains(ManagedSchedulable ms) {
		for (int i = 0; i < noOfRegistered; i++) {
			if (managedSchObjects[i] == ms)
				return true;
		}
		return false;
	}

	int indexOf(ManagedSchedulable ms) {
		for (int i = 0; i < noOfRegistered; i++) {
			if (managedSchObjects[i] == ms)
				return i;
		}
		return -1;
	}

	public String toString() {
		return "Mission: " + noOfRegistered + " handlers";
	}
}