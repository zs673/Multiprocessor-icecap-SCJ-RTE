package javax.safetycritical;

import javax.realtime.AperiodicParameters;
import javax.realtime.MemoryArea;
import javax.realtime.PriorityParameters;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.safetycritical.annotate.SCJRestricted;

import vm.Memory;

@SCJAllowed
public abstract class MissionSequencer<SpecificMission extends Mission> extends ManagedEventHandler {
	MissionMemory missionMemory;
	SpecificMission currMission;

	interface State {
		public final static int START = 1;
		public final static int INITIALIZE = 2;
		public final static int EXECUTE = 3;
		public final static int CLEANUP = 4;
		public final static int TERMINATE = 5;
		public final static int END = 6;
	}

	int currState;
	boolean terminateSeq = false;

	static volatile boolean isOuterMostSeq = true;
	static MissionSequencer<?> outerMostSeq = null;

	@SCJAllowed
	@SCJRestricted(Phase.INITIALIZE)
	public MissionSequencer(PriorityParameters priority, StorageParameters storage, String name) {
		super(priority, new AperiodicParameters(), storage);
		this.name = name;

		if (isOuterMostSeq) {
			int backingStoreOfThisMemory = MemoryArea.getRemainingMemorySize();
			MemoryArea backingStoreProvider = MemoryArea.backingStore;
			String privateMemoryName = Memory.getNextMemoryName("outer most PvtMem");
			privateMemory = new PrivateMemory((int) this.storage.maxMemoryArea,
					backingStoreOfThisMemory, backingStoreProvider, privateMemoryName);

			missionMemory = new MissionMemory((int) storage.maxMissionMemory, privateMemory, name
					+ "mission mem");
			currentMemory = ManagedMemory.ImmortalMemory.instance();

			outerMostSeq = this;
			isOuterMostSeq = false;

			OSProcess.setOuterMostMissionSequencer(this.priority.getPriority());
			this.set = Launcher.level == 1 ? findAffinitySetForLevel1()
					: AffinitySet.AFFINITY_SET[0];
		}

		currState = State.START;
		Services.setCeiling(this, this.priority.getPriority());
	}

	@SCJAllowed
	@SCJRestricted(Phase.INITIALIZE)
	public MissionSequencer(PriorityParameters priority, final StorageParameters storage) {
		this(priority, storage, "MisMem");
	}

	synchronized void seqWait() {
		while (currMission.msSetForMission.activeCount > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	synchronized void decrementActiveCount() {
		currMission.msSetForMission.activeCount--;
		if (currMission.msSetForMission.activeCount == 0) {
			notify();
		}
	}

	@SCJAllowed(Level.INFRASTRUCTURE)
	public final void handleAsyncEvent() {
		do {
			switch (currState) {
			case State.START:
				currMission = getNextMission();

				if (currMission == null || terminateSeq) {
					terminateSeq = true;
					currState = State.TERMINATE;
				} else {
					currMission.missionTerminate = false;
					currState = State.INITIALIZE;
				}
				break;

			case State.INITIALIZE:
				currMission.setMissionSeq(this);
				missionMemory.enterToInitialize(currMission);
				currState = State.EXECUTE;
				break;

			case State.EXECUTE:
				missionMemory.enterToExecute(currMission);
				currState = State.CLEANUP;
				break;

			case State.CLEANUP:
				missionMemory.enterToCleanup(currMission);
				missionMemory.resizeArea(storage.maxMissionMemory);
				currState = State.START;
				break;

			case State.TERMINATE:
				currMission = null;
				currState = State.END;
			default:
			}
		} while (currState < State.END);

	}

	@SCJAllowed(Level.SUPPORT)
	protected abstract SpecificMission getNextMission();

	public final void register() {
		super.register();
		missionMemory = new MissionMemory((int) storage.maxMissionMemory, privateMemory, name);
	}

	@SCJAllowed
	public final void requestSequenceTermination() {
		terminateSeq = true;
		currMission.requestTermination();
	}

	@SCJAllowed
	public final boolean sequenceTerminationPending() {
		return terminateSeq;
	}

	@Override
	public void signalTermination() {
		terminateSeq = true;
		currMission.requestTermination();
	}

	public final void cleanUp() {
		super.cleanUp();
		missionMemory.removeArea();
	}

	MissionMemory getMissionMemory() {
		return missionMemory;
	}

	OSProcess getOSOsProcess() {
		return osProcess;
	}

	static MissionSequencer<?> getOuterMostSequencer() {
		return outerMostSeq;
	}

	private AffinitySet findAffinitySetForLevel1() {
		int processor = OSProcess.getCurrentCPUID();
		for (int i = 0; i < AffinitySet.AFFINITY_SET.length; i++) {
			if (AffinitySet.AFFINITY_SET[i].processorSet[0] == processor) {
				return AffinitySet.AFFINITY_SET[i];
			}
		}
		throw new NullPointerException();
	}
}
