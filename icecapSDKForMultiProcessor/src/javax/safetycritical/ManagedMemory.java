package javax.safetycritical;

import icecaptools.IcecapCompileMe;

import javax.realtime.MemoryArea;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

import vm.Memory;

@SCJAllowed
public abstract class ManagedMemory extends MemoryArea {

	static boolean flag = true;

	@SuppressWarnings("unused")
	private static MemoryArea backingStore;

	private static final IllegalArgumentException exception = new IllegalArgumentException();

	private static class BackingStore extends MemoryArea {

		public BackingStore(Memory delegate) {
			super(delegate);
		}
	}

	static void allocateBackingStore(int size) {
		MemoryArea.backingStore = new BackingStore(Memory.allocateInHeap(size));
	}

	public static class ImmortalMemory extends ManagedMemory // HSO: not public
	{
		ImmortalMemory(int sizeOfArea) {
			super(sizeOfArea, sizeOfArea, MemoryArea.backingStore, "Imm");
		}

		static ImmortalMemory instance() {
			MemoryArea result = MemoryArea.getNamedMemoryArea("Imm");
			if (result != null) {
				return (ImmortalMemory) result;
			}
			return null;
		}
	}

	@IcecapCompileMe
	ManagedMemory(int size, int BackingStoreOfThisMemory, MemoryArea backingStoreProvider,
			String label) {
		super(size, BackingStoreOfThisMemory, backingStoreProvider, label);
	}

	@SCJAllowed(Level.INFRASTRUCTURE)
	@IcecapCompileMe
	void enter(Runnable logic) throws IllegalArgumentException {
		if (logic == null || !(logic instanceof ManagedSchedulable))
			throw new IllegalArgumentException();
		ManagedSchedulable ms = (ManagedSchedulable) logic;

		ManagedMemory outer;

		if (ms instanceof ManagedEventHandler) {
			outer = ((ManagedEventHandler) ms).currentMemory;
			((ManagedEventHandler) ms).currentMemory = this;
		} else {
			outer = ((ManagedThread) ms).currentMemory;
			((ManagedThread) ms).currentMemory = this;
		}

		OSProcess.setMemoryArea(this.delegate);
		logic.run();
		OSProcess.setMemoryArea(outer.delegate);
		this.delegate.reset(0);

		if (ms instanceof ManagedEventHandler) {
			((ManagedEventHandler) ms).currentMemory = outer;
		} else {
			((ManagedThread) ms).currentMemory = outer;
		}
	}

	@SCJAllowed
	void executeInArea(Runnable logic) throws IllegalArgumentException {
		if (logic == null)
			throw new IllegalArgumentException("executeInArea: logic is null");

		if (flag) {
			flag = false;
			Memory currentMem = vm.Memory.getHeapArea();

			OSProcess.setMemoryArea(this.delegate);
			logic.run();
			OSProcess.setMemoryArea(currentMem);

		} else {
			ManagedMemory outer;

			ManagedSchedulable ms = Services.getSchedulableObject();
			if (ms instanceof ManagedEventHandler) {
				outer = ((ManagedEventHandler) ms).getCurrentMemory();
				((ManagedEventHandler) ms).setCurrentMemory(this);
			} else {
				outer = ((ManagedThread) ms).getCurrentMemory();
				((ManagedThread) ms).setCurrentMemory(this);
			}

			OSProcess.setMemoryArea(this.delegate);
			logic.run();
			OSProcess.setMemoryArea(outer.delegate);

			if (ms instanceof ManagedEventHandler) {
				((ManagedEventHandler) ms).setCurrentMemory(outer);
			} else {
				((ManagedThread) ms).setCurrentMemory(outer);
			}

		}
	}

	@SCJAllowed
	public static void enterPrivateMemory(int size, Runnable logic) throws IllegalStateException {
		if (logic == null)
			throw exception;

		ManagedSchedulable ms = Services.getSchedulableObject();
		devices.Console.println("enterPrivateMemory");
		runEnterPrivateMemory(ms, size, logic);

	}

	private static void runEnterPrivateMemory(ManagedSchedulable ms, int size, Runnable logic) {
		ManagedMemory prev = getMemory(ms);
		devices.Console.println("enterPrivateMemory: prev " + prev);
		long prevFree = prev.memoryConsumed();

		InnerPrivateMemory inner = new InnerPrivateMemory(size,
				prev.getRemainingBackingstoreSize(), prev, "InnerPrvMem");
		inner.prev = prev;

		ManagedMemory outer;

		if (ms instanceof ManagedEventHandler) {
			outer = ((ManagedEventHandler) ms).getCurrentMemory();
			((ManagedEventHandler) ms).setCurrentMemory(inner);
		} else {
			outer = ((ManagedThread) ms).getCurrentMemory();
			((ManagedThread) ms).setCurrentMemory(inner);
		}

		OSProcess.setMemoryArea(inner.delegate);
		logic.run();
		OSProcess.setMemoryArea(outer.delegate);

		if (prev.memoryConsumed() != prevFree)
			prev.resetArea(prevFree);

		inner.removeArea();

		if (ms instanceof ManagedEventHandler) {
			((ManagedEventHandler) ms).setCurrentMemory(outer);
		} else {
			((ManagedThread) ms).setCurrentMemory(outer);
		}
	}

	private static ManagedMemory getMemory(ManagedSchedulable ms) {
		if (ms instanceof ManagedEventHandler) {
			ManagedEventHandler mevh = (ManagedEventHandler) ms;
			return mevh.privateMemory;
		} else {
			ManagedThread mth = (ManagedThread) ms;
			return mth.privateMemory;
		}
	}

	@SCJAllowed
	public static void executeInAreaOf(Object obj, Runnable logic) {
		if (obj == null || logic == null)
			throw exception;

		ManagedMemory outer;
		ManagedMemory memAreaOfObject = (ManagedMemory) MemoryArea.getMemoryArea(obj);

		ManagedSchedulable ms = Services.getSchedulableObject();
		if (ms instanceof ManagedEventHandler) {
			outer = ((ManagedEventHandler) ms).getCurrentMemory();
			((ManagedEventHandler) ms).setCurrentMemory(memAreaOfObject);
		} else {
			outer = ((ManagedThread) ms).getCurrentMemory();
			((ManagedThread) ms).setCurrentMemory(memAreaOfObject);
		}

		OSProcess.setMemoryArea(memAreaOfObject.delegate);
		logic.run();
		OSProcess.setMemoryArea(outer.delegate);

		if (ms instanceof ManagedEventHandler) {
			((ManagedEventHandler) ms).setCurrentMemory(outer);
		} else {
			((ManagedThread) ms).setCurrentMemory(outer);
		}

	}

	static final ManagedMemory getOuterMemory(ManagedMemory mem) {
		if (mem instanceof InnerPrivateMemory)
			return ((InnerPrivateMemory) mem).prev;

		else if (mem instanceof PrivateMemory) {
			return Mission.getCurrentMission().getSequencer().getMissionMemory();
		}

		else if (mem instanceof MissionMemory) {
			// return nearest outermost memory
			MissionSequencer<?> missSeq = Mission.getCurrentMission().getSequencer();
			if (missSeq.mission == null)
				return ImmortalMemory.instance();
			else
				return missSeq.mission.currMissSeq.missionMemory;
		} else
			return null;
	}

	@SCJAllowed
	public static void executeInOuterArea(Runnable logic) {
		if (logic == null)
			throw exception;

		ManagedSchedulable ms = Services.getSchedulableObject();

		ManagedMemory currentMem;
		if (ms instanceof ManagedEventHandler) {
			ManagedEventHandler handler = ((ManagedEventHandler) ms);
			currentMem = handler.getCurrentMemory();
		} else {
			ManagedThread handler = ((ManagedThread) ms);
			currentMem = handler.getCurrentMemory();
		}
		devices.Console.println("executeInOuterArea: currentMem: " + currentMem);

		if (currentMem instanceof ManagedMemory.ImmortalMemory) {
			devices.Console.println("executeInOuterArea: already in ImmortalMemory");
			throw new IllegalStateException("executeInOuterArea: already in ImmortalMemory");
		}

		ManagedMemory outerMemory = getOuterMemory(currentMem);

		if (ms instanceof ManagedEventHandler) {
			((ManagedEventHandler) ms).setCurrentMemory(outerMemory);
		} else {
			((ManagedThread) ms).setCurrentMemory(outerMemory);
		}

		OSProcess.setMemoryArea(outerMemory.delegate);
		logic.run();
		OSProcess.setMemoryArea(currentMem.delegate);

		if (ms instanceof ManagedEventHandler) {
			((ManagedEventHandler) ms).setCurrentMemory(currentMem);
		} else {
			((ManagedThread) ms).setCurrentMemory(currentMem);
		}
	}

	@SCJAllowed
	public long getRemainingBackingStore() {
		return memoryRemaining();
	}

	private void resetArea(long newFree) {
		this.delegate.reset((int) newFree);
	}

	void resetArea() {
		this.delegate.reset(0);
	}

	void removeArea() {
		this.removeMemArea();
	}

	void resizeArea(long newSize) {
		this.resizeMemArea(newSize);

	}

	Memory getDelegate() {
		return delegate;
	}
}
