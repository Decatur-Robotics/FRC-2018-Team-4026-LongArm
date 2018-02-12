package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Arm implements Subsystem {

	boolean isInitialized = false;
	boolean intake = false;
	boolean intakeForward = false;
	boolean intakeReverse = false;
	double liftSpeed;
	NeutralMode brakeMode;
	WPI_TalonSRX armLiftMotor;
	WPI_TalonSRX leftGrabberMotor;
	WPI_TalonSRX rightGrabberMotor;
	WPI_TalonSRX leftIntakeMotor;
	WPI_TalonSRX rightIntakeMotor;

	@Override
	public int init() {
		if (!isInitialized) {

			armLiftMotor = new WPI_TalonSRX(PortMap.ARMLIFT);
			leftGrabberMotor = new WPI_TalonSRX(PortMap.LEFTGRABBER);
			rightGrabberMotor = new WPI_TalonSRX(PortMap.RIGHTGRABBER);
			leftIntakeMotor = new WPI_TalonSRX(PortMap.LEFTINTAKE);
			rightIntakeMotor = new WPI_TalonSRX(PortMap.RIGHTINTAKE);
			liftSpeed = 0;
			isInitialized = true;

			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	public void lift(Controllers gamepad, Pneumatics pneumatics) {
		brakeMode = NeutralMode.Brake;
		armLiftMotor.setNeutralMode(brakeMode);
		liftSpeed = -gamepad.getSecondaryLeft();
		boolean holdLift;

		if (gamepad.getSecondaryRawButton(8)) {
			pneumatics.intakePistons.set(Value.kForward);
		} else {
			pneumatics.intakePistons.set(Value.kReverse);
		}

		pneumatics.actuateGrabber(5, 7, gamepad);

		arcadeIntake(gamepad.getSecondaryRightX(), gamepad.getSecondaryRightY(), .05);

		if (gamepad.getSecondaryRawButton(6)) {
			holdLift = true;
			liftSpeed = .05;
		} else {
			holdLift = false;
		}

		if (liftSpeed > 0 && !holdLift) {
			liftSpeed *= .5;
		}

		if (liftSpeed < 0 && !holdLift) {
			liftSpeed *= .3;
		}

		armLiftMotor.set(liftSpeed);
		pneumatics.actuateGrabber(1, 2, gamepad);

	}

	// Ask Walden - this will need to be tweaked this afternoon. Controls intake
	// and grabber in conjunction. Allows left and right to be controlled with
	// one analog stick
	void arcadeIntake(double x, double y, double deadzone) {
		double right = x + y;
		double left = x - y;
		right = trim(right);
		left = trim(left);

		leftIntakeMotor.set(left);
		rightIntakeMotor.set(right);
		leftGrabberMotor.set(left);
		rightGrabberMotor.set(right);
	}

	private static double trim(double v) {
		if (v > 1) {
			v = 1;
		}
		if (v < -1) {
			v = -1;
		}
		return v;
	}

	private void stopArm() {
		brakeMode = NeutralMode.Coast;
		armLiftMotor.setNeutralMode(brakeMode);
		armLiftMotor.set(0);
		leftGrabberMotor.set(0);
		rightGrabberMotor.set(0);
		leftIntakeMotor.set(0);
		rightIntakeMotor.set(0);
	}

	@Override
	public int shutdown() {
		stopArm();
		return 1;
	}

}
