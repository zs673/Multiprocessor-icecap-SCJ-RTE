package javax.realtime;

public class AsyncLongEventHandler extends AbstractAsyncEventHandler {

	protected long data = 0L;

	public void run() {
		handleAsyncEvent(data);
	}

	public void handleAsyncEvent(long data) {
	}
}
