package test;

import javax.realtime.AbsoluteTime;
import javax.realtime.AperiodicParameters;
import javax.realtime.Clock;
import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;
import javax.safetycritical.AffinitySet;
import javax.safetycritical.AperiodicEventHandler;
import javax.safetycritical.Launcher;
import javax.safetycritical.ManagedThread;
import javax.safetycritical.Mission;
import javax.safetycritical.MissionSequencer;
import javax.safetycritical.OSProcess;
import javax.safetycritical.OneShotEventHandler;
import javax.safetycritical.PeriodicEventHandler;
import javax.safetycritical.Safelet;
import javax.safetycritical.Services;
import javax.safetycritical.StorageParameters;
import javax.scj.util.Const;


public class TestSCJMP_CustAffinitySet_Level1 implements Safelet<Mission> {
	public static StorageParameters storageParameters_Sequencer;
	public static StorageParameters storageParameters_Handlers;
	public static StorageParameters storageParameters_InnerSequencer;
	public static Mission m;
	MissionSequencer<Mission> ms;
	public static AffinitySet[] sets;
	static int count = 0;
	static OneShotEventHandler one;
	
	private static class MyAperiodicEvh extends AperiodicEventHandler {

		public MyAperiodicEvh(PriorityParameters priority, AperiodicParameters release,
				StorageParameters storage) {
			super(priority, release, storage);
		}

		@Override
		public void handleAsyncEvent() {
			if(OSProcess.getCurrentCPUID() == this.getAffinitySet().getProcessorSet()[0]){
				devices.Console.println("0");
			}
			else{
				devices.Console.println("Aperiodic!");
				throw new IllegalArgumentException();
			}
		}
	}
	
	private static class MyPeriodicEvh extends PeriodicEventHandler {
		AperiodicEventHandler han;
		int count = 0;
		Mission m;

		public MyPeriodicEvh(PriorityParameters priority, PeriodicParameters periodicParameters,
				StorageParameters storage, Mission m, AperiodicEventHandler han) {
			super(priority, periodicParameters, storage);
			this.han = han;
			this.m = m;
		}

		public void handleAsyncEvent() {
			if(OSProcess.getCurrentCPUID() == this.getAffinitySet().getProcessorSet()[0]){
				devices.Console.println("0");
			}
			else{
				devices.Console.println("Periodic!");
				throw new IllegalArgumentException();
			}
			
			han.release();
			one.scheduleNextReleaseTime(new AbsoluteTime());
			
			count++;
			if(count == 10){
				m.requestTermination();
			}
		}
	}
	
	private static class TenThread extends Mission {
		public void initialize() {
			AffinitySet.printAffinitySets();
			MyAperiodicEvh aevh = new MyAperiodicEvh(new PriorityParameters(30),
					new AperiodicParameters(), TestSCJMP_CustAffinitySet_Level1.storageParameters_Handlers);
			AffinitySet.setProcessorAffinity(TestSCJMP_CustAffinitySet_Level1.sets[1], aevh);
			aevh.register();

			PeriodicEventHandler pevh = new MyPeriodicEvh(new PriorityParameters(50),
					new PeriodicParameters(new RelativeTime(0, 0, Clock.getRealtimeClock()),
							new RelativeTime(1, 0, Clock.getRealtimeClock())),
					TestSCJMP_CustAffinitySet_Level1.storageParameters_Handlers, this, aevh);
			AffinitySet.setProcessorAffinity(TestSCJMP_CustAffinitySet_Level1.sets[0], pevh);
			pevh.register();

			one = new OneShotEventHandler(new PriorityParameters(10), new RelativeTime(0, 0),
					new AperiodicParameters(), TestSCJMP_CustAffinitySet_Level1.storageParameters_Handlers) {

				@Override
				public void handleAsyncEvent() {
					if(OSProcess.getCurrentCPUID() == this.getAffinitySet().getProcessorSet()[0]){
						devices.Console.println("0");
					}
					else{
						devices.Console.println("One Shot!");
						throw new IllegalArgumentException();
					}
				}
			};
			AffinitySet.setProcessorAffinity(TestSCJMP_CustAffinitySet_Level1.sets[2], one);
			one.register();
			
			ManagedThread thread = new ManagedThread(new PriorityParameters(50), storageParameters_Handlers){
				@Override
				public void run() {
					devices.Console.println("thread");
					if(OSProcess.getCurrentCPUID() == this.getAffinitySet().getProcessorSet()[0]){
						devices.Console.println("t0");
					}
					else{
						devices.Console.println("Thread!");
						throw new IllegalArgumentException();
					}
				}
				
			};
			AffinitySet.setProcessorAffinity(sets[2], thread);
			thread.register();

		}

		public long missionMemorySize() {
			return Const.MISSION_MEM;
		}
		
	}
	
	public MissionSequencer<Mission> getSequencer() {
		ms = new MySequencer();
		return ms;
	}

	public long immortalMemorySize() {
		return Const.IMMORTAL_MEM;
	}

	public void initializeApplication() {
	}

	private class MySequencer extends MissionSequencer<Mission> {
		
		int count = 0;

		MySequencer() {
			super(new PriorityParameters(12), storageParameters_Sequencer, "outer-ms");
			m = new TenThread();
		}

		public Mission getNextMission() {
			sets = Services.getSchedulingAllocationDomains();
			if (count < 999999) {
				devices.Console.println("new mission");
				count++;
				return m;
			} else {
				return null;
			}
		}
	}
	
	public static void main(String[] args) {
		storageParameters_Sequencer = new StorageParameters(Const.OUTERMOST_SEQ_BACKING_STORE,
				new long[] { Const.HANDLER_STACK_SIZE }, Const.PRIVATE_MEM, Const.IMMORTAL_MEM - 30 * 1000,
				Const.MISSION_MEM);

		storageParameters_Handlers = new StorageParameters(0, new long[] { Const.HANDLER_STACK_SIZE },
				Const.PRIVATE_MEM - 10 * 1000, 0, 0);

		storageParameters_InnerSequencer = new StorageParameters(Const.PRIVATE_BACKING_STORE * 3 + Const.MISSION_MEM
				- 150 * 1000, new long[] { Const.HANDLER_STACK_SIZE }, Const.PRIVATE_MEM, 0,
				Const.MISSION_MEM_DEFAULT - 150 * 1000);

		devices.Console.println("\n***** Two Missions main.begin ************");
		new Launcher(new TestSCJMP_CustAffinitySet_Level1(), 1, true);
		devices.Console.println("***** Two Missions main.end **************");
		args = null;
	}
}




