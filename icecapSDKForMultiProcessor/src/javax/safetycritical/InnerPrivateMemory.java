package javax.safetycritical;

public class InnerPrivateMemory extends ManagedMemory {

	ManagedMemory prev;

	public InnerPrivateMemory(int size, int BackingStoreOfThisMemory,
			ManagedMemory backingStoreProvider, String label) {
		super(size, BackingStoreOfThisMemory, backingStoreProvider, label);
	}
}
