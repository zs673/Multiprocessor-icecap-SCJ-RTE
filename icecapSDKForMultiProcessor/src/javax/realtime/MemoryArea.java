package javax.realtime;

import icecaptools.IcecapCompileMe;

import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

import reflect.ObjectInfo;
import vm.Memory;

@SCJAllowed(Level.INFRASTRUCTURE)
public abstract class MemoryArea extends Object {

	public static MemoryArea backingStore;

	protected MemoryArea backingStoreProvider;

	private MemoryArea containedMemories;

	private MemoryArea nextContainedMemory;

	protected Memory delegate;

	private int reservedEnd;

	private int maxUsage;

	protected MemoryArea() {
	}

	protected MemoryArea(Memory delegate) {
		this.delegate = delegate;
		reservedEnd = delegate.getBase() + delegate.getSize();
		delegate.resize(0);
	}

	@IcecapCompileMe
	protected MemoryArea(int initialSize, int reservedSize, MemoryArea backingStoreProvider,
			String label) {
		int base = backingStoreProvider.reservedEnd
				- backingStoreProvider.getRemainingBackingstoreSize();

		int endOfAvailableSpace = backingStoreProvider.reservedEnd;

		if (base + reservedSize <= endOfAvailableSpace) {
			this.backingStoreProvider = backingStoreProvider;
			delegate = new Memory(base, initialSize, label);
			reservedEnd = base + reservedSize;
			backingStoreProvider.addContainedMemory(this);
		} else {
			devices.Console.println("   MemoryArea: throw");
			throw new OutOfMemoryError(
					"thrown from MemoryArea :: constructor : Out of backingstore exception: size: "
							+ initialSize + " backingStoreSize: " + reservedSize + " base: " + base
							+ " backingStoreEnd: " + endOfAvailableSpace);
		}
	}

	private static void print(MemoryArea backingStoreProvider, int indent) {
		MemoryArea current = backingStoreProvider.containedMemories;

		int count = indent;
		while (count > 0) {
			devices.Console.print("   ");
			count--;
		}

		int bsstart = backingStoreProvider.delegate.getBase()
				+ backingStoreProvider.delegate.getSize();

		devices.Console.print(backingStoreProvider.delegate.getName() + "[used "
				+ backingStoreProvider.delegate.consumedMemory() + " of "
				+ backingStoreProvider.delegate.getSize());

		int bssize = backingStoreProvider.reservedEnd - bsstart;

		if (bssize > 0) {
			int consumedBackingStore;
			if (backingStoreProvider.maxUsage > 0) {
				consumedBackingStore = backingStoreProvider.maxUsage - bsstart;
			} else {
				consumedBackingStore = 0;
			}

			devices.Console.println(", used " + consumedBackingStore + " of " + bssize + "]");
		} else {
			devices.Console.println("]");
		}

		while (current != null) {
			print(current, indent + 1);
			current = current.nextContainedMemory;
		}
	}

	private void addContainedMemory(MemoryArea memoryArea) {
		memoryArea.nextContainedMemory = containedMemories;
		containedMemories = memoryArea;
		if (memoryArea.reservedEnd > maxUsage) {
			maxUsage = memoryArea.reservedEnd;
		}
	}

	private void removeContainedMemory(MemoryArea memoryArea) {
		if (containedMemories == memoryArea) {
			containedMemories = containedMemories.nextContainedMemory;
			//			devices.Console.println("*");
		} else {
			MemoryArea current = containedMemories;

			while (current.nextContainedMemory != null) {
				if (current.nextContainedMemory == memoryArea) {
					current.nextContainedMemory = current.nextContainedMemory.nextContainedMemory;
					//					devices.Console.println("*");
					return;
				}
				current = current.nextContainedMemory;
			}
			//			devices.Console.println("!");
		}
	}

	protected void removeMemArea() {
		if (this != backingStore) {
			backingStoreProvider.removeContainedMemory(this);
		}
	}

	@SCJAllowed
	public long memoryConsumed() {
		return (long) delegate.consumedMemory();
	}

	@SCJAllowed
	public long memoryRemaining() {
		return size() - memoryConsumed();
	}

	public long size() {
		return this.delegate.getSize();
	}

	protected void resizeMemArea(long newSize) {

		if (memoryConsumed() < newSize) {
			if (this.delegate.getBase() + newSize < reservedEnd) {
				if (containedMemories == null) {
					delegate.resize((int) newSize);
					return;
				} else {
					devices.Console.println("still contians memories: "
							+ containedMemories.delegate.getName());
				}
			} else {
				devices.Console.println("base + newsize >= reservedEnd");
			}
		} else {
			devices.Console.println("consumed: " + memoryConsumed() + " new size: " + newSize);
		}
		throw new OutOfMemoryError(
				"thrown from MemoryArea :: resizeMem : Out of backingstore exception ");
	}

	public static int getRemainingMemorySize() {
		return backingStore.getRemainingBackingstoreSize();
	}

	public int getRemainingBackingstoreSize() {
		int maxEnd = delegate.getBase() + delegate.getSize();
		MemoryArea current = containedMemories;
		while (current != null) {
			maxEnd = maxEnd > current.reservedEnd ? maxEnd : current.reservedEnd;
			current = current.nextContainedMemory;
		}
		return reservedEnd - maxEnd;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public static void printMemoryAreas() {
		print(backingStore, 0);
	}

	protected static MemoryArea getNamedMemoryArea(String name) {
		return getNamedMemoryArea(backingStore, name);
	}

	private static MemoryArea getNamedMemoryArea(MemoryArea provider, String name) {
		if (provider.delegate.getName().equals(name)) {
			return provider;
		}
		MemoryArea current = provider.containedMemories;
		while (current != null) {
			MemoryArea result = getNamedMemoryArea(current, name);
			if (result != null) {
				return result;
			}
			current = current.nextContainedMemory;
		}
		return null;
	}

	@SCJAllowed
	public static MemoryArea getMemoryArea(Object object) {
		int ref = ObjectInfo.getAddress(object);

		return getMemoryArea(backingStore, ref);
	}

	private static MemoryArea getMemoryArea(MemoryArea provider, int ref) {

		if ((provider.delegate.getBase() <= ref)
				&& (ref < provider.delegate.getBase() + provider.delegate.getSize())) {
			return provider;
		}
		MemoryArea current = provider.containedMemories;
		while (current != null) {
			MemoryArea result = getMemoryArea(current, ref);
			if (result != null) {
				return result;
			}
			current = current.nextContainedMemory;
		}
		return null;
	}
}
