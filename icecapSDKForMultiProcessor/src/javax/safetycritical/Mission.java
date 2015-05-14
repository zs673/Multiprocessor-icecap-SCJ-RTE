package javax.safetycritical;

import icecaptools.IcecapCompileMe;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.Phase;
import javax.safetycritical.annotate.SCJAllowed;
import javax.scj.util.Const;

@SCJAllowed
public abstract class Mission {
	MissionSequencer<?> currMissSeq;
	boolean missionTerminate = false;
	ManagedSchedulableSet msSetForMission;
	Phase missionPhase;

	protected int missionIndex = -1;
	boolean isMissionSetInitByThis = false;

	@SCJAllowed
	public Mission() {
	}

	@SCJAllowed(Level.SUPPORT)
	protected boolean cleanUp() {
		return true;
	}

	@SCJAllowed
	@IcecapCompileMe
	public static Mission getCurrentMission() {
		Mission m = null;
		ManagedSchedulable ms = Services.getSchedulableObject();
		if (ms != null) {
			if (ms instanceof ManagedEventHandler) {
				if (ms instanceof MissionSequencer<?>) {
					m = ((MissionSequencer<?>) ms).currMission;
				} else {
					m = ((ManagedEventHandler) ms).mission;
				}
			} else {
				m = ((ManagedThread) ms).mission;
			}
		}
		return m;
	}

	@SCJAllowed
	public MissionSequencer<?> getSequencer() {
		return currMissSeq;
	}

	@SCJAllowed(Level.SUPPORT)
	protected abstract void initialize();

	void setMissionSeq(MissionSequencer<?> missSeq) {
		currMissSeq = missSeq;
	}

	@SCJAllowed(Level.SUPPORT)
	public abstract long missionMemorySize();

	@SCJAllowed
	@IcecapCompileMe
	public final void requestTermination() {
		missionTerminate = true;
		for (int i = 0; i < msSetForMission.noOfRegistered; i++) {
			if (msSetForMission.managedSchObjects[i] != null) {
				if (msSetForMission.managedSchObjects[i] instanceof AperiodicEventHandler) {
					((AperiodicEventHandler) msSetForMission.managedSchObjects[i])
							.fireNextRelease();
				}
				if (msSetForMission.managedSchObjects[i] instanceof OneShotEventHandler) {
					((OneShotEventHandler) msSetForMission.managedSchObjects[i]).deschedule();
					((OneShotEventHandler) msSetForMission.managedSchObjects[i]).fireNextRelease();
				}
				msSetForMission.managedSchObjects[i].signalTermination();
			}
		}
	}

	public final boolean terminationPending() {
		return missionTerminate;
	}

	void runInitialize() {
		missionIndex = Launcher.helper.addNewMission(this);
		msSetForMission = new ManagedSchedulableSet();
		initialize();
	}

	void runExecute() {
		missionPhase = Phase.EXECUTE;
		ManagedSchedulableSet msSet = msSetForMission;

		int index = missionIndex * Const.DEFAULT_HANDLER_NUMBER;

		for (int i = 0; i < msSet.noOfRegistered; i++) {
			ManagedSchedulable ms = msSet.managedSchObjects[i];
			OSProcess process = new OSProcess(ms);
			process.executable.id = index;
			index++;
			msSetForMission.activeCount++;
			process.executable.start();
		}

		currMissSeq.seqWait();
		for (int i = 0; i < msSet.noOfRegistered; i++) {
			try {
				if (msSet.managedSchObjects[i] instanceof ManagedThread)
					((ManagedThread) msSet.managedSchObjects[i]).osProcess.executable.join();
				else
					((ManagedEventHandler) msSet.managedSchObjects[i]).osProcess.executable.join();
			} catch (InterruptedException e) {

			}
		}
	}

	void runCleanup(MissionMemory missMem) {
		missionPhase = Phase.CLEANUP;

		if (msSetForMission.activeCount > 0) {
			devices.Console.println("still have SOs");
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < msSetForMission.noOfRegistered; i++) {
			msSetForMission.managedSchObjects[i].cleanUp();
			msSetForMission.managedSchObjects[i] = null;
			msSetForMission.msCount--;
		}

		Launcher.helper.missionSet[missionIndex] = null;
		if (isMissionSetInitByThis == true) {
			Launcher.helper.isMissionSetInit = false;
		}

		cleanUp();
		missMem.resetArea();
	}

	boolean isRegistered(ManagedSchedulable evh) {
		return true;
	}

	boolean inMissionScope(ManagedSchedulable evh) {
		return true;
	}

	Phase getPhase() {
		return missionPhase;
	}

	public void getMSSetRegistered() {
		devices.Console.println("No of registered: " + this.msSetForMission.noOfRegistered);
	}

}
