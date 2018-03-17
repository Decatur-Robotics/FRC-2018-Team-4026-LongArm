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
	private static final double ARM_SCALE_POSITION = 27;
	private static final double ARM_GROUND_POSITION = 5.5;
	boolean isInitialized = false;
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

			armLiftMotor = new WPI_TalonSRX(PortMap.ARMLIFT);;
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

	public void updateLiftMotor() {
		if (getArmPosition() > 45 && liftSpeed > 0) {
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

	public boolean liftToScale() {
		return liftToPosition(ARM_SCALE_POSITION);
	}

	public boolean liftToGround() {
		return liftToPosition(ARM_GROUND_POSITION);
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
		}else {
			robot.pneumatics.intakeLiftPistons.set(Value.kReverse);
		}
	}
	
	
	private void smartPivot(Robot robot) {
		if (getArmPosition() < ARM_SWITCH_POSITION - 3) {
			switch (state) {
			case -1:
				robot.pneumatics.intakeUp();
				state++;
			case 0:
				if (robot.controllers.getSecondaryRawButton(8))
				{
					robot.pneumatics.openGrabber();
					GrabberLiftTimer.reset();
					GrabberLiftTimer.start();
					state++;
				
				}
				break;
			case 2:
				if (GrabberLiftTimer.get() > .5) {
					robot.pneumatics.intakeDown();
					state++;
					
				}else if (!robot.controllers.getSecondaryRawButton(8)){
					state = -1;
					
				}
				break;
			case 3:
				if  (!robot.controllers.getSecondaryRawButton(8))
				{
					robot.pneumatics.closeGrabber();
					GrabberLiftTimer.reset();
					GrabberLiftTimer.start();
					state++;
				} 
			case 4:
				if (GrabberLiftTimer.get() > 1) {
					robot.pneumatics.intakeUp();
					state = -1;
				} else if (robot.controllers.getSecondaryRawButton(8)) {
					robot.pneumatics.openGrabber();
					state = 3;
				}
			
			}
		} else {
			robot.pneumatics.intakeUp();
		}
	}
	//****
	public void run(Robot robot) {
		
		lift(robot.controllers, robot);
		manualPivotIntake(robot);
		//smartPivot(robot);
		robot.pneumatics.actuateGrabber(5, 7, robot.controllers);
	}
	//******

	@Override
	public int shutdown() {
		stopArm();
		return 1;
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber("Lift Speed", liftSpeed);
		SmartDashboard.putBoolean("Arm Lower Limit Switch", armLowerLimit.get());
		SmartDashboard.putString("Brake Mode", brakeMode.toString());
		SmartDashboard.putNumber("String Thingy Extension:", getArmPosition());

	}

}
