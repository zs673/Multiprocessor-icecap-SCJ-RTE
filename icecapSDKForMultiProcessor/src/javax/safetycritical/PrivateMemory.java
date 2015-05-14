package javax.safetycritical;

import icecaptools.IcecapCompileMe;

import javax.realtime.MemoryArea;
import javax.safetycritical.annotate.Level;
import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed(Level.INFRASTRUCTURE)
public final class PrivateMemory extends ManagedMemory {
	@IcecapCompileMe
	PrivateMemory(int size, int BackingStoreOfThisMemory, MemoryArea backingStoreProvider,
			String label) {
		super(size, BackingStoreOfThisMemory, backingStoreProvider, label);
	}

}
