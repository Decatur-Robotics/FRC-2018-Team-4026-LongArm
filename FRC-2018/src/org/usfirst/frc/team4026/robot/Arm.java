package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm implements Subsystem {

	private static final double ARM_SWITCH_POSITION = 14;
	private static final double ARM_SCALE_POSITION = 28;
	private static final double ARM_GROUND_POSITION = 5;
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
	AnalogInput stringThingy;

	@Override
	public int init() {
		if (!isInitialized) {

			armLiftMotor = new WPI_TalonSRX(PortMap.ARMLIFT);
			leftGrabberMotor = new WPI_TalonSRX(PortMap.LEFTGRABBER);
			rightGrabberMotor = new WPI_TalonSRX(PortMap.RIGHTGRABBER);
			leftIntakeMotor = new WPI_TalonSRX(PortMap.LEFTINTAKE);
			rightIntakeMotor = new WPI_TalonSRX(PortMap.RIGHTINTAKE);
			armLowerLimit = new DigitalInput(PortMap.ARM_LOWER_LIMIT);
			stringThingy = new AnalogInput(PortMap.STRINGTHINGY);
			// armUpperLimit = new DigitalInput(PortMap.ARM_UPPER_LIMIT);
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

	public void lift(Controllers gamepad, Robot robot) {
		brakeMode = NeutralMode.Brake;
		armLiftMotor.setNeutralMode(brakeMode);
		liftSpeed = -gamepad.getSecondaryLeft();
		boolean holdLift;

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
		if (gamepad.getSecondaryRawButton(1)) {
			liftToGround();
		}
		if (gamepad.getSecondaryRawButton(2)) {
			liftToSwitch();
		}
		if (gamepad.getSecondaryRawButton(3)) {
			liftToScale();
		}
		updateLiftMotor();

	}

	// Ask Walden - this will need to be tweaked this afternoon. Controls intake
	// and grabber in conjunction. Allows left and right to be controlled with
	// one analog stick
	void arcadeIntake(double x, double y, double deadzone, Robot robot) {
		y = -y;
		double right = x + y;
		double left = x - y;
		if (Math.abs(left) < deadzone && Math.abs(right) < deadzone) {
			left = 0;
			right = 0;
		} else {
			robot.pneumatics.openGrabber();
		}
		right = trim(right);
		left = trim(left);

		leftIntakeMotor.set(left);
		rightIntakeMotor.set(right);

	}

	public void updateLiftMotor() {
		if (getArmPosition() > 45 && liftSpeed > 0) {
			liftSpeed = 0;
		}

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

	public boolean liftToAngle(double targetPosition) {
		double armPos = getArmPosition();
		if (Math.abs(targetPosition - armPos) < 2) {
			holdLift();

			return true;
		} else {
			if (armPos < (targetPosition)) {
				if (Math.abs(targetPosition - armPos) > 4.5) {
					liftSpeed = .65;
				} else {
					liftSpeed = .4;
				}
			} else {
				if (Math.abs(targetPosition - Math.abs(armPos)) > 4.5) {
					liftSpeed = -.3;
				} else {
					liftSpeed = -.15;
				}
			}
			return false;
		}
	}

	public boolean liftToSwitch() {
		return liftToAngle(ARM_SWITCH_POSITION);
	}

	public boolean liftToScale() {
		return liftToAngle(ARM_SCALE_POSITION);
	}

	public boolean liftToGround() {
		return liftToAngle(ARM_GROUND_POSITION);
	}

	public void holdLift() {
		liftSpeed = .06;
	}

	public void run(Robot robot) {
		
		lift(robot.controllers, robot);
		pivotIntake(robot);
		robot.pneumatics.actuateGrabber(5, 7, robot.controllers);
		intakeLift(robot.controllers);
		arcadeIntake(robot.controllers.getSecondaryRightX(), robot.controllers.getSecondaryRightY(), .05, robot);
	}

	public double getArmPosition() {
		return (stringThingy.getVoltage() * 11) - 1;
	}

	
	public void pivotIntake(Robot robot) {
		if (getArmPosition() < ARM_SWITCH_POSITION) {
			if (robot.controllers.getSecondaryRawButton(8)) {
				robot.pneumatics.intakeLiftPistons.set(Value.kForward);
			} else {
				robot.pneumatics.intakeLiftPistons.set(Value.kReverse);
			}
		}else {
			robot.pneumatics.intakeLiftPistons.set(Value.kReverse);
		}
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
		SmartDashboard.putNumber("String Thingy Extension:", getArmPosition());

	}

}
