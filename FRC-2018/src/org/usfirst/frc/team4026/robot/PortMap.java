package org.usfirst.frc.team4026.robot;

public interface PortMap {
	// CAN
	public static final int ARMLIFT = 4;
	public static final int LEFTINTAKE = 0;
	public static final int RIGHTINTAKE = 2;
	public static final int LEFTGRABBER = 1;
	public static final int RIGHTGRABBER = 3;

	// PWM
	public static final int LEFTDRIVE = 0;
	public static final int RIGHTDRIVE = 1;

	// Joysticks
	public static final int PRIMARYCONTROLLER = 0;
	public static final int SECONDARYCONTROLLER = 1;

	// Pneumatics
	public static final int SHIFTLOWGEAR = 0;
	public static final int SHIFTHIGHGEAR = 3;
	public static final int GRABBERRPISTONIN = 6;
	public static final int GRABBERPISTONOUT = 7;
	public static final int INTAKEPISTONIN = 1;
	public static final int INTAKEPISTONOUT = 2;

	// Analog Sensors
	public static final int GYRO = 1;
	public static final int PRESSURESENSOR = 3;
	public static final int STRINGTHINGY = 2;


	// Digital Sensors

	// Encoders
	public static final int RIGHT_ENCODER_1 = 0;
	public static final int RIGHT_ENCODER_2 = 1;
	public static final int LEFT_ENCODER_1 = 2;
	public static final int LEFT_ENCODER_2 = 3;

	// Limit switch
	public static final int ARM_LOWER_LIMIT = 9;
	public static final int ARM_UPPER_LIMIT = 1342342;
	public static final int CUBE_SENSOR_LIMIT = 6;

}
