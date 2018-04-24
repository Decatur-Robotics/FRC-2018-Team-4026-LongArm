package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm implements Subsystem {

	private static final double ARM_SWITCH_POSITION = 14;
	private static final double ARM_LOWSCALE_POSITION = 27;
	private static final double ARM_HIGHSCALE_POSITION = 30;
	private static final double ARM_GROUND_POSITION = 5.5;

	boolean isInitialized = false;
	boolean smartGrabState = true;
	double liftSpeed;
	NeutralMode brakeMode = NeutralMode.Coast;
	Timer GrabberLiftTimer;
	WPI_TalonSRX armLiftMotor;
	DigitalInput armLowerLimit;
	DigitalInput armUpperLimit;
	AnalogInput stringThingy;
	int state = -1;

	@Override
	public int init() {
		if (!isInitialized) {

			armLiftMotor = new WPI_TalonSRX(PortMap.ARMLIFT);
			armLowerLimit = new DigitalInput(PortMap.ARM_LOWER_LIMIT);
			stringThingy = new AnalogInput(PortMap.STRINGTHINGY);
			GrabberLiftTimer = new Timer();
			// armUpperLimit = new DigitalInput(PortMap.ARM_UPPER_LIMIT);
			liftSpeed = 0;
			isInitialized = true;

			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	public void lift(Controllers gamepad, Robot robot) {
		brakeMode = NeutralMode.Brake;
		armLiftMotor.setNeutralMode(brakeMode);
		liftSpeed = -gamepad.getSecondaryRightY();
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
			liftToLowScale();
		}
		if (gamepad.getSecondaryRawButton(4)) {
			liftToHighScale();
		}
		updateLiftMotor();

	}

	public void updateLiftMotor() {
		if (getArmPosition() > ARM_HIGHSCALE_POSITION && liftSpeed > 0) {
			liftSpeed = 0;
		}

		armLiftMotor.set(liftSpeed);
	}

	private void stopArm() {
		brakeMode = NeutralMode.Coast;
		armLiftMotor.setNeutralMode(brakeMode);
		armLiftMotor.set(0);
	}

	public boolean liftToPosition(double targetPosition) {
		double armPos = getArmPosition();

		if (armPos < ARM_GROUND_POSITION - 2) // Checks if the string sensor is
												// broken/unplugged
		{
			liftSpeed = 0;
			return false;
		}
		if (Math.abs(targetPosition - armPos) < .75) {
			holdLift();

			return true;
		} else {
			if (targetPosition == ARM_SWITCH_POSITION) {
				if (armPos < (targetPosition)) {
					if (Math.abs(targetPosition - armPos) > 5) {
						liftSpeed = .65;
					} else {
						liftSpeed = .4;
					}
				} else {
					if (Math.abs(targetPosition - Math.abs(armPos)) > 4.5) {
						liftSpeed = -.4;
					} else {
						liftSpeed = -.2;
					}
				}
			} else {
				if (armPos < (targetPosition)) {
					if (Math.abs(targetPosition - armPos) > 5) {
						liftSpeed = .9;
					} else {
						liftSpeed = .4;
					}
				} else {
					if (Math.abs(targetPosition - Math.abs(armPos)) > 4.5) {
						liftSpeed = -.4;
					} else {
						liftSpeed = -.2;
					}
				}
			}

			return false;
		}
	}

	public boolean liftToPositionAuto(double targetPosition) {
		double armPos = getArmPosition();

		if (armPos < ARM_GROUND_POSITION - 2) // Checks if string thing is
												// broken/unplugged
		{
			liftSpeed = 0;
			return false;
		}
		if (Math.abs(targetPosition - armPos) < .75) {
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
		return liftToPosition(ARM_SWITCH_POSITION);
	}

	public boolean liftToHighScale() {
		return liftToPosition(ARM_HIGHSCALE_POSITION);
	}

	public boolean liftToLowScale() {
		return liftToPosition(ARM_LOWSCALE_POSITION);
	}

	public boolean liftToGround() {
		return liftToPosition(ARM_GROUND_POSITION);
	}

	// Slower liftToPosition for Auto
	public boolean liftToSwitchAuto() {
		return liftToPositionAuto(ARM_SWITCH_POSITION);
	}

	public boolean liftToScaleAuto() {
		return liftToPositionAuto(ARM_LOWSCALE_POSITION);
	}

	public boolean liftToGroundAuto() {
		return liftToPositionAuto(ARM_GROUND_POSITION);
	}

	public void holdLift() {
		liftSpeed = .06;
	}

	public double getArmPosition() {
		return (stringThingy.getVoltage() * 11) - 1;
	}

	public void manualPivotIntake(Robot robot) {
		if (getArmPosition() < ARM_SWITCH_POSITION - 3) {
			if (robot.controllers.getSecondaryRawButton(8)) {
				robot.pneumatics.intakeLiftPistons.set(Value.kForward);
			} else {
				robot.pneumatics.intakeLiftPistons.set(Value.kReverse);
			}
		} else {
			robot.pneumatics.intakeLiftPistons.set(Value.kReverse);
		}
	}

	public void intakeDown(Robot robot) {
		robot.pneumatics.intakeLiftPistons.set(Value.kForward);
	}

	public void intakeUp(Robot robot) {
		robot.pneumatics.intakeLiftPistons.set(Value.kReverse);
	}

	/*
	 * private void smartPivot(Robot robot) { if (getArmPosition() <
	 * ARM_SWITCH_POSITION - 3) { if
	 * (robot.controllers.getSecondaryRawButton(8)) {
	 * robot.pneumatics.intakeLiftPistons.set(Value.kForward); } else { if
	 * (robot.pneumatics.grabberIsClosed()) { if
	 * (robot.pneumatics.airPressureSensor.getAirPressurePsi() >
	 * MINIMUM_GRIP_PRESSURE) {
	 * robot.pneumatics.intakeLiftPistons.set(Value.kReverse); } } else {
	 * robot.pneumatics.intakeLiftPistons.set(Value.kReverse); } } } } private
	 * void shouldISmartGrab(Robot robot) { if
	 * (robot.controllers.getSecondaryRawButton(10)) { smartGrabState = true; }
	 * if (robot.controllers.getSecondaryRawButton(9)) { smartGrabState = false;
	 * } }
	 */
	// ****
	@Override
	public void run(Robot robot) {

		lift(robot.controllers, robot);
		/*
		 * shouldISmartGrab(robot); if(smartGrabState) { smartPivot(robot);
		 * 
		 * } else {
		 */
		manualPivotIntake(robot);
		// }
		robot.pneumatics.actuateGrabber(5, 7, robot.controllers);
	}
	// ******

	@Override
	public int shutdown() {
		stopArm();
		return 1;
	}

	@Override
	public void updateDashboard() {
		// SmartDashboard.putNumber("Lift Speed", liftSpeed);
		// SmartDashboard.putBoolean("SmartGrab State", smartGrabState);
		// SmartDashboard.putString("Brake Mode", brakeMode.toString());
		SmartDashboard.putNumber("String Thingy Extension:", getArmPosition());

	}

}
