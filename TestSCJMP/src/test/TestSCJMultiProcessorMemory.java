package test;

import javax.realtime.Clock;
import javax.realtime.PeriodicParameters;
import javax.realtime.PriorityParameters;
import javax.realtime.RelativeTime;
import javax.safetycritical.Launcher;
import javax.safetycritical.Mission;
import javax.safetycritical.MissionSequencer;
import javax.safetycritical.PeriodicEventHandler;
import javax.safetycritical.Safelet;
import javax.safetycritical.Services;
import javax.safetycritical.StorageParameters;
import javax.scj.util.Const;

public class TestSCJMultiProcessorMemory {
	public static MissionSequencer<MyMission0> ms;

	private static class MyPeriodicEvh extends PeriodicEventHandler {
		int i = 0;
		Mission m;

		public MyPeriodicEvh(PriorityParameters priority, PeriodicParameters periodicParameters,
				StorageParameters storage, Mission m, String name) {
			super(priority, periodicParameters, storage, name);
			this.m = m;
		}

		public void handleAsyncEvent() {
			if (Services.getNameOfCurrentMemoryArea().equals("handler 1: PvtMem")) {
				devices.Console.println("1");
			} else {
				devices.Console.println("5 " + Services.getNameOfCurrentMemoryArea());
			}

			if (i == 3) {
				i++;
				m.requestTermination();
			}
		}
	}

	private static class MyPeriodicEvh1 extends PeriodicEventHandler {

		public MyPeriodicEvh1(PriorityParameters priority, PeriodicParameters periodicParameters,
				StorageParameters storage, String name) {
			super(priority, periodicParameters, storage, name);
		}

		public void handleAsyncEvent() {
			if (Services.getNameOfCurrentMemoryArea().equals("handler 2: PvtMem")) {
				devices.Console.println("0");
			} else {
				devices.Console.println("	6 " + Services.getNameOfCurrentMemoryArea());
			}
		}
	}

	private static class MyMission0 extends Mission {
		public void initialize() {
			PeriodicEventHandler pevh = new MyPeriodicEvh(new PriorityParameters(20),
					new PeriodicParameters(new RelativeTime(0, 0, Clock.getRealtimeClock()),
							new RelativeTime(1, 0, Clock.getRealtimeClock())),
					storageParameters_Handlers, this, "handler 1: ");
			pevh.register();

			PeriodicEventHandler pevh1 = new MyPeriodicEvh1(new PriorityParameters(20),
					new PeriodicParameters(new RelativeTime(0, 0, Clock.getRealtimeClock()),
							new RelativeTime(1, 0, Clock.getRealtimeClock())),
					storageParameters_Handlers, "handler 2: ");
			pevh1.register();

		}

		public long missionMemorySize() {
			return Const.MISSION_MEM;
		}
	}

	private static class MyApp implements Safelet<MyMission0> {
		public MissionSequencer<MyMission0> getSequencer() {
			ms = new MySequencer();
			return ms;
		}

		public long immortalMemorySize() {
			return Const.IMMORTAL_MEM;
		}

		public void initializeApplication() {
		}

		private class MySequencer extends MissionSequencer<MyMission0> {
			MyMission0 m;
			int count = 0;

			MySequencer() {
				super(new PriorityParameters(12), storageParameters_Sequencer, "outer-ms");
				m = new MyMission0();
			}

			public MyMission0 getNextMission() {
				if (count < 999) {
					count++;
					//
					return m;
				} else {
					return null;
				}
			}
		}
	}

	public static StorageParameters storageParameters_Sequencer;
	public static StorageParameters storageParameters_Handlers;
	public static StorageParameters storageParameters_InnerSequencer;

	public static void main(String[] args) {
		storageParameters_Sequencer = new StorageParameters(Const.OUTERMOST_SEQ_BACKING_STORE,
				new long[] { Const.HANDLER_STACK_SIZE }, Const.PRIVATE_MEM, Const.IMMORTAL_MEM,
				Const.MISSION_MEM);

		storageParameters_Handlers = new StorageParameters(Const.PRIVATE_BACKING_STORE,
				new long[] { Const.HANDLER_STACK_SIZE }, Const.PRIVATE_MEM, 0, 0);

		storageParameters_InnerSequencer = new StorageParameters(Const.PRIVATE_BACKING_STORE * 3
				+ Const.MISSION_MEM, new long[] { Const.HANDLER_STACK_SIZE }, Const.PRIVATE_MEM, 0,
				Const.MISSION_MEM_DEFAULT);

		devices.Console.println("\n***** Two Missions main.begin ************");
		new Launcher(new MyApp(), 1, true);
		devices.Console.println("***** Two Missions main.end **************");
		args = null;
	}

}
