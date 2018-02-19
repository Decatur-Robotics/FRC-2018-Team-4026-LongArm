package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm implements Subsystem {

	private static final double ARM_SWITCH_ANGLE = 30;
	boolean isInitialized = false;
	boolean intake = false;
	boolean intakeForward = false;
	boolean intakeReverse = false;
	double liftSpeed;
	NeutralMode brakeMode = NeutralMode.Coast;
	WPI_TalonSRX armLiftMotor;
	WPI_TalonSRX leftGrabberMotor;
	WPI_TalonSRX rightGrabberMotor;
	WPI_TalonSRX leftIntakeMotor;
	WPI_TalonSRX rightIntakeMotor;
	DigitalInput armLowerLimit;
	DigitalInput armUpperLimit;
	AnalogGyro armGyro;

	@Override
	public int init() {
		if (!isInitialized) {

			armLiftMotor = new WPI_TalonSRX(PortMap.ARMLIFT);
			leftGrabberMotor = new WPI_TalonSRX(PortMap.LEFTGRABBER);
			rightGrabberMotor = new WPI_TalonSRX(PortMap.RIGHTGRABBER);
			leftIntakeMotor = new WPI_TalonSRX(PortMap.LEFTINTAKE);
			rightIntakeMotor = new WPI_TalonSRX(PortMap.RIGHTINTAKE);
			armLowerLimit = new DigitalInput(PortMap.ARM_LOWER_LIMIT);
			// armLowerLimit = new DigitalInput(PortMap.ARM_UPPER_LIMIT);
			liftSpeed = 0;
			isInitialized = true;
			armGyro = new AnalogGyro(0);
			armGyro.calibrate();
			armGyro.reset();

			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	public void lift(Controllers gamepad, Pneumatics pneumatics) {
		intakeLift(gamepad);
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

		arcadeIntake(gamepad.getSecondaryRightX(), gamepad.getSecondaryRightY(), .05, pneumatics);

		if (gamepad.getSecondaryRawButton(6)) {
			holdLift = true;
			liftSpeed = .06;
		} else {
			holdLift = false;
		}

		if (liftSpeed > 0 && !holdLift) {
			liftSpeed *= .5;
		}

		if (liftSpeed < 0 && !holdLift) {
			liftSpeed *= .3;
		}
		if (liftSpeed < 0 && !armLowerLimit.get()) {
			liftSpeed = 0;
		}
		if (gamepad.getSecondaryRawButton(2)) {
			liftToSwitch();
		}
		updateLiftMotor();

	}

	// Ask Walden - this will need to be tweaked this afternoon. Controls intake
	// and grabber in conjunction. Allows left and right to be controlled with
	// one analog stick
	void arcadeIntake(double x, double y, double deadzone, Pneumatics pneumatics) {
		y = -y;
		double right = x + y;
		double left = x - y;
		if (Math.abs(left) < deadzone && Math.abs(right) < deadzone) {
			left = 0;
			right = 0;
		} else {
			pneumatics.openGrabber();
		}
		right = trim(right);
		left = trim(left);

		leftIntakeMotor.set(left);
		rightIntakeMotor.set(right);

	}

	public void updateLiftMotor() {
		armLiftMotor.set(liftSpeed);
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

	private void intakeLift(Controllers gamepad) {
		if (gamepad.getSecondaryRawButton(1)) {
			leftGrabberMotor.set(1.0);
			rightGrabberMotor.set(-1.0);
		} else if (gamepad.getSecondaryRawButton(2)) {
			leftGrabberMotor.set(-1);
			rightGrabberMotor.set(1);
		} else {
			leftGrabberMotor.set(0);
			rightGrabberMotor.set(0);
		}

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

	public boolean liftToSwitch() {
		if (Math.abs(ARM_SWITCH_ANGLE - armGyro.getAngle()) < 5) {
			holdLift();

			return true;
		} else {
			if (armGyro.getAngle() < (ARM_SWITCH_ANGLE)) {
				if (Math.abs(ARM_SWITCH_ANGLE - armGyro.getAngle()) > 15) {
					liftSpeed = .5;
				} else {
					liftSpeed = .5;
				}
			} else {
				if (Math.abs(ARM_SWITCH_ANGLE - Math.abs(armGyro.getAngle())) > 0) {
					liftSpeed = -.3;
				} else {
					liftSpeed = -.15;
				}
			}
			return false;
		}
	}

	public void holdLift() {
		armLiftMotor.set(.06);
	}

	@Override
	public int shutdown() {
		stopArm();
		return 1;
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber("Lift Speed", liftSpeed);
		SmartDashboard.putNumber("Arm Motor Output Voltage", armLiftMotor.getMotorOutputVoltage());
		SmartDashboard.putNumber("Arm Output Current", armLiftMotor.getOutputCurrent());
		SmartDashboard.putBoolean("Arm Lower Limit Switch", armLowerLimit.get());
		SmartDashboard.putString("Brake Mode", brakeMode.toString());
		SmartDashboard.putNumber("Arm Gyro", armGyro.getAngle());
	}

}
