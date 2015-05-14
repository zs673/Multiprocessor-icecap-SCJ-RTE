package javax.safetycritical;

import javax.realtime.BoundAsyncLongEventHandler;
import javax.realtime.PriorityParameters;
import javax.realtime.ReleaseParameters;

public abstract class ManagedLongEventHandler extends BoundAsyncLongEventHandler implements
		ManagedSchedulable {

	PriorityParameters priority;
	ReleaseParameters release;
	String name;

	StorageParameters storage;
	Mission mission = null;

	ManagedMemory privateMemory;
	OSProcess OSprocess;

	public void run() {
		// TODO Auto-generated method stub

	}

	public void register() {
		// TODO Auto-generated method stub

	}

	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	public void signalTermination() {
		// TODO Auto-generated method stub

	}

	void setCurrentMemory(ManagedMemory current) {
		this.privateMemory = current;
	}

	ManagedMemory getCurrentMemory() {
		return privateMemory;
	}

	public PriorityParameters getPriorityParam() {
		return priority;
	}
}
