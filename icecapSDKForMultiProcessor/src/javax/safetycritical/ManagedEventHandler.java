package javax.safetycritical;

import javax.realtime.AbsoluteTime;
import javax.realtime.BoundAsyncEventHandler;
import javax.realtime.MemoryArea;
import javax.realtime.PriorityParameters;
import javax.realtime.ReleaseParameters;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

import vm.Memory;

@SCJAllowed
public abstract class ManagedEventHandler extends BoundAsyncEventHandler implements
		ManagedSchedulable {
	PriorityParameters priority;
	StorageParameters storage;
	OSProcess osProcess = null;
	Mission mission = null;

	ManagedMemory privateMemory;
	ManagedMemory currentMemory;

	ReleaseParameters release;
	String name;
	AffinitySet set = null;

	public ManagedEventHandler(PriorityParameters priority, ReleaseParameters release,
			StorageParameters storage) {
		this(priority, release, storage, null);

	}

	public ManagedEventHandler(PriorityParameters priority, ReleaseParameters release,
			StorageParameters storage, String name) {
		if (priority == null)
			throw new IllegalArgumentException("priority is null");
		if (release == null)
			throw new IllegalArgumentException("release is null");
		if (storage == null)
			throw new IllegalArgumentException("storage is null");
		this.priority = priority;
		this.release = release;
		this.storage = storage;
		this.name = name;
		this.mission = Mission.getCurrentMission();

		if (mission != null) {
			this.set = mission.currMissSeq.set;
			int backingStoreOfThisMemory = (int) this.storage.totalBackingStore;
			MemoryArea backingStoreProvider = mission.currMissSeq.missionMemory;
			String privateMemoryName = Memory.getNextMemoryName(name + "PvtMem");

			privateMemory = new PrivateMemory((int) this.storage.maxMemoryArea,
					backingStoreOfThisMemory, backingStoreProvider, privateMemoryName);

			this.currentMemory = mission.currMissSeq.missionMemory;
		}
	}

	public abstract void handleAsyncEvent();

	@SCJAllowed(Level.SUPPORT)
	@SCJRestricted(Phase.CLEANUP)
	public void cleanUp() {
		privateMemory.removeArea();
	}

	@SCJAllowed(Level.INFRASTRUCTURE)
	@SCJRestricted(Phase.INITIALIZE)
	public void register() {
		ManagedSchedulableSet hs = mission.msSetForMission;
		hs.addMS(this);
	}

	@SCJAllowed(Level.SUPPORT)
	public void signalTermination() {
	}

	@SCJAllowed(Level.LEVEL_1)
	public AbsoluteTime getLastReleaseTime() {
		return null;
	}

	Mission getMission() {
		return mission;
	}

	PriorityParameters getPriorityParam() {
		return priority;
	}

	ReleaseParameters getReleaseParam() {
		return release;
	}

	public long memorConsumed() {
		return this.privateMemory.memoryConsumed();
	}

	public long MissionMemoryConsumed() {
		return this.mission.currMissSeq.missionMemory.memoryConsumed();
	}

	public void setCurrentMemory(ManagedMemory current) {
		this.currentMemory = current;
	}

	public ManagedMemory getCurrentMemory() {
		return currentMemory;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public AffinitySet getAffinitySet() {
		return set;
	}
}
