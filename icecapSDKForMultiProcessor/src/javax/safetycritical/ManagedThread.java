package javax.safetycritical;

import javax.realtime.MemoryArea;
import javax.realtime.NoHeapRealtimeThread;
import javax.realtime.PriorityParameters;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

import vm.Memory;

@SCJAllowed(Level.LEVEL_2)
public class ManagedThread extends NoHeapRealtimeThread implements ManagedSchedulable {

	PriorityParameters priority;
	StorageParameters storage;
	OSProcess osProcess = null;
	Mission mission = null;

	ManagedMemory privateMemory;
	ManagedMemory currentMemory;
	String name;
	AffinitySet set = null;

	public ManagedThread(PriorityParameters priority, StorageParameters storage) {
		this(priority, storage, null);
	}

	public ManagedThread(PriorityParameters priority, StorageParameters storage, Runnable logic) {
		this(priority, storage, logic, null);
	}

	public ManagedThread(PriorityParameters priority, StorageParameters storage, Runnable logic,
			String name) {
		super(priority, logic);
		if (storage == null)
			throw new IllegalArgumentException("storage is null");

		this.priority = priority;
		this.storage = storage;
		this.name = name;
		this.mission = Mission.getCurrentMission();
		if (mission == null)
			throw new IllegalArgumentException("mission is null");

		this.set = mission.currMissSeq.set;
		int backingStoreOfThisMemory = (int) this.storage.totalBackingStore;
		MemoryArea backingStoreProvider = mission.currMissSeq.missionMemory;
		String privateMemoryName = Memory.getNextMemoryName("PvtMem");
		privateMemory = new PrivateMemory((int) storage.maxMemoryArea, backingStoreOfThisMemory,
				backingStoreProvider, privateMemoryName);

		this.currentMemory = privateMemory;
	}

	Mission getMission() {
		return mission;
	}

	@SCJAllowed(Level.INFRASTRUCTURE)
	@SCJRestricted(Phase.INITIALIZE)
	public final void register() {
		ManagedSchedulableSet msSet = mission.msSetForMission;
		msSet.addMS(this);
	}

	@SCJAllowed(Level.SUPPORT)
	@SCJRestricted(Phase.CLEANUP)
	public void cleanUp() {
		privateMemory.removeArea();
	}

	public void signalTermination() {
	}

	PriorityParameters getPriorityParam() {
		return priority;
	}

	public void setCurrentMemory(ManagedMemory current) {
		this.currentMemory = current;
	}

	public ManagedMemory getCurrentMemory() {
		return currentMemory;
	}
	
	public AffinitySet getAffinitySet(){
		return this.set;
	}

}
