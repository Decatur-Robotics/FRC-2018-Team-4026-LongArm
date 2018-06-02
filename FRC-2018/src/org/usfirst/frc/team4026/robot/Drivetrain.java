package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.interfaces.Accelerometer.Range;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain implements Subsystem {

	// Constants
	static final double MAX_BATTERY = 12.3;
	static final double TIPPING_POINT_DEGS = 20;

	// Motor Controllers
	Talon rightDriveMotor;
	Talon leftDriveMotor;

	// Sensors
	AnalogGyro gyro;

	Encoder LeftEncoder;
	BuiltInAccelerometer Accel;
	AnalogInput Ultrasonic;

	// Power vars
	double right = 0;
	double left = 0;

	// Other Class Variables
	boolean isGyroresetTelop = false;
	boolean isInitialized = false;

	@Override
	public int init() {
		if (!isInitialized) {
			// todo - best practice - indent this code so it is clearly part of
			// the if block

			LeftEncoder = new Encoder(PortMap.LEFT_ENCODER_1, PortMap.LEFT_ENCODER_2, true);
			// RightEncoder.setDistancePerPulse();
			// LeftEncoder.setDistancePerPulse();

			leftDriveMotor = new Talon(PortMap.LEFTDRIVE);
			rightDriveMotor = new Talon(PortMap.RIGHTDRIVE);
			Accel = new BuiltInAccelerometer(Range.k4G);
			gyro = new AnalogGyro(PortMap.GYRO);
			gyro.calibrate();

			Ultrasonic = new AnalogInput(PortMap.ULTRASONIC);
			isInitialized = true;
			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	void tankDrive(Controllers driveGamepad) {
		left = -driveGamepad.getPrimaryLeft();
		right = -driveGamepad.getPrimaryRight();

		/*
		 * left = smoothJoyStick(left); right = smoothJoyStick(right);
		 */

		// Cut speed in half
		if (driveGamepad.getPrimaryRawButton(7)) {
			right /= 2.0;
			left /= 2.0;
		}

		double avgStick = (right + left) / 2.0;

		if (!driveGamepad.getPrimaryRawButton(8) && !shouldIHelpDriverDriveStraight()) {

			setDriveMotors(left, right);
			isGyroresetTelop = false;
		} else {
			if (isGyroresetTelop == false) {
				gyro.reset();
				isGyroresetTelop = true;
			}
			keepDriveStraight(avgStick, avgStick, 0);
		}

	}

	void setDriveMotors(double leftPower2, double rightPower2) {
		leftDriveMotor.set(-leftPower2);
		rightDriveMotor.set(rightPower2);
	}

	public boolean shouldIHelpDriverDriveStraight() {
		return false;
	}

	double batteryCompensationPct() {
		return MAX_BATTERY / RobotController.getBatteryVoltage();
	}

	public void keepDriveStraight(double leftDriveVel, double rightDriveVel, double targetAngle) {

		double error = 0, correctionFactor;
		error = targetAngle + gyro.getAngle();
		correctionFactor = (error / 75.0);

		// todo - best practice - conditions on a separate line should be
		// wrapped in brackets
		if (leftDriveVel > 0.9)
			leftDriveVel = 0.9;
		else if (leftDriveVel < -0.9)
			leftDriveVel = -0.9;

		// todo - best practice - conditions on a separate line should be
		// wrapped in brackets
		if (rightDriveVel > 0.9)
			rightDriveVel = 0.9;
		else if (rightDriveVel < -0.9)
			rightDriveVel = -0.9;

		if (targetAngle > (gyro.getAngle() - 0.5) || targetAngle < (gyro.getAngle() + 0.5)) {
			leftDriveMotor.set(((-leftDriveVel) + correctionFactor) * batteryCompensationPct());
			rightDriveMotor.set((rightDriveVel + correctionFactor) * batteryCompensationPct());
		} else {
			leftDriveMotor.set(leftDriveVel * batteryCompensationPct());
			rightDriveMotor.set(-rightDriveVel * batteryCompensationPct());
		}
	}

	/*
	 * Smooth joystick input for driving
	 */
	double smoothJoyStick(double joyInput) {
		return Math.pow(joyInput, 2);
	}

	public double calculateWallDistance(boolean averaged) {
		double rawVoltage;
		double wallDistance;

		if (averaged)
			rawVoltage = (Ultrasonic.getAverageVoltage());
		else
			rawVoltage = (Ultrasonic.getVoltage());

		// MB1030
		double VFiveMM = 0.009671875;
		wallDistance = rawVoltage / VFiveMM;

		return wallDistance;
	}

	public void reset() {
		LeftEncoder.reset();
	}

	void stopDrive() {
		leftDriveMotor.set(0);
		rightDriveMotor.set(0);
	}

	@Override
	public void run(Robot robot) {
		tankDrive(robot.controllers);
	}

	@Override
	public int shutdown() {
		stopDrive();
		return 1;
	}

	@Override
	public void updateDashboard() {
		// SmartDashboard.putNumber("Right Encoder Ticks", RightEncoder.get());
		SmartDashboard.putNumber("Left Encoder Ticks", LeftEncoder.get());
		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
		// SmartDashboard.putNumber("Ultrasonic Distance",
		// calculateWallDistance(false));
		// System.out.println(gyro.getAngle());
	}

}
