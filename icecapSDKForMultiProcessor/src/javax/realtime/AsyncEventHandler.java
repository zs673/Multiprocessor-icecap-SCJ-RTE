package javax.realtime;

import javax.safetycritical.annotate.SCJAllowed;

@SCJAllowed(javax.safetycritical.annotate.Level.LEVEL_0)
public class AsyncEventHandler extends AbstractAsyncEventHandler {

	public void run() {
		handleAsyncEvent();
	}

	@SCJAllowed(javax.safetycritical.annotate.Level.SUPPORT)
	public void handleAsyncEvent() {
	}

}
