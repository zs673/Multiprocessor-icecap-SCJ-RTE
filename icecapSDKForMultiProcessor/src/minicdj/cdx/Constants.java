/**
 *  This file is part of miniCDx benchmark of oSCJ.
 *
 *   miniCDx is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   miniCDx is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with miniCDx.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *   Copyright 2009, 2010 
 *   @authors  Daniel Tang, Ales Plsek
 *
 *   See: http://sss.cs.purdue.edu/projects/oscj/
 */
package minicdj.cdx;

/**
 * All of our globally relevant constants.
 * 
 * @author Filip Pizlo
 */
public final class Constants {
	// I have added this so that we can specify the number of planes at runtime
	public static int NUMBER_OF_PLANES = 6;

	public static final float MIN_X = 0.0f;
	public static final float MIN_Y = 0.0f;
	public static final float MAX_X = 1000.0f;
	public static final float MAX_Y = 1000.0f;
	public static final float MIN_Z = 0.0f;
	public static final float MAX_Z = 10.0f;
	public static final float PROXIMITY_RADIUS = 1.0f;
	public static final float GOOD_VOXEL_SIZE = PROXIMITY_RADIUS * 10.0f;

	public static int SIMULATOR_PRIORITY = 5;
	public static int SIMULATOR_TIME_SCALE = 1;
	public static int SIMULATOR_FPS = 50;
	public static int DETECTOR_STARTUP_PRIORITY = 9;
	public static int DETECTOR_PRIORITY = 9; // DETECTOR_STARTUP_PRIORITY
												// +
												// 1;
	public static long PERSISTENT_DETECTOR_SCOPE_SIZE = 5 * 100 * 1000;
	public static long DETECTOR_PERIOD = 50;
	public static long TRANSIENT_DETECTOR_SCOPE_SIZE = 5 * 100 * 1000;

	public static int MAX_FRAMES = 1; // 1000 standard

	public static int TIME_SCALE = 1;
	public static int FPS = 50;
	public static int BUFFER_FRAMES = 100;
	public static boolean PRESIMULATE = false;
	public static boolean SIMULATE_ONLY = false;

	public static final String DETECTOR_STATS = "detector.rin";
	public static final String SIMULATOR_STATS = "simulator.rin";
	public static final String DETECTOR_RELEASE_STATS = "release.rin";
	public static final boolean PRINT_RESULTS = true;

	// run a SPEC jvm98 benchmark to generate some noise
	public static String SPEC_NOISE_ARGS = "-a -b -g -s100 -m10 -M10 -t _213_javac";
	public static boolean USE_SPEC_NOISE = false;

	public static int DETECTOR_NOISE_REACHABLE_POINTERS = 1000000;
	public static int DETECTOR_NOISE_ALLOCATE_POINTERS = 10000;
	public static int DETECTOR_NOISE_ALLOCATION_SIZE = 64;
	public static boolean DETECTOR_NOISE_VARIABLE_ALLOCATION_SIZE = false;
	public static int DETECTOR_NOISE_ALLOCATION_SIZE_INCREMENT = 13;
	public static int DETECTOR_NOISE_MIN_ALLOCATION_SIZE = 128;
	public static int DETECTOR_NOISE_MAX_ALLOCATION_SIZE = 16384;
	public static int DETECTOR_STARTUP_OFFSET_MILLIS = 3000;
	public static boolean DETECTOR_NOISE = false;

	// write down the FRAMES into the frame.bin file
	public static boolean FRAMES_BINARY_DUMP = false;

	// this is only for debugging of the detector code
	//
	// each frame generated by the simulator is processed exactly once by
	// the detector ; this also turns on some debugging features
	//
	// the results thus should be deterministic
	public static boolean SYNCHRONOUS_DETECTOR = false;

	public static boolean DUMP_RECEIVED_FRAMES = false;
	public static boolean DUMP_SENT_FRAMES = false;
	public static boolean DEBUG_DETECTOR = false;

}
