package javax.safetycritical;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed
public final class StorageParameters {
	long totalBackingStore;
	long[] configurationSizes;
	int messageLength;
	int stackTraceLength;
	long maxMemoryArea;
	long maxImmortal;
	long maxMissionMemory;

	public static final long NO_MAX = -1L;

	@SCJAllowed
	public StorageParameters(long totalBackingStore, long[] sizes, int messageLength,
			int stackTraceLength, long maxMemoryArea, long maxImmortal, long maxMissionMemory) {
		if (maxMemoryArea < -1L)
			throw new IllegalArgumentException();
		if (maxImmortal < -1L)
			throw new IllegalArgumentException();

		this.totalBackingStore = totalBackingStore;
		this.configurationSizes = sizes;
		this.messageLength = messageLength;
		this.stackTraceLength = stackTraceLength;
		this.maxMemoryArea = maxMemoryArea;
		this.maxImmortal = maxImmortal;
		this.maxMissionMemory = maxMissionMemory;
	}

	@SCJAllowed
	public StorageParameters(long totalBackingStore, long[] sizes, long maxMemoryArea,
			long maxImmortal, long maxMissionMemory) {
		if (maxMemoryArea < -1L)
			throw new IllegalArgumentException();
		if (maxImmortal < -1L)
			throw new IllegalArgumentException();

		this.totalBackingStore = totalBackingStore;
		this.configurationSizes = sizes;
		this.messageLength = 0;
		this.stackTraceLength = 0;
		this.maxMemoryArea = maxMemoryArea;
		this.maxImmortal = maxImmortal;
		this.maxMissionMemory = maxMissionMemory;
	}

	long getBackingStoreSize() {
		return totalBackingStore;
	}
}
