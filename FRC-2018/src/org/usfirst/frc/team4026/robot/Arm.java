package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Arm implements Subsystem{

	boolean isInitialized = false;
	double liftSpeed;
	NeutralMode brakeMode;
	WPI_TalonSRX armLiftMotor;
	WPI_TalonSRX leftGrabberMotor;
	WPI_TalonSRX rightGrabberMotor;
	
	public int init() {
		if(!isInitialized){
		
		armLiftMotor = new WPI_TalonSRX (PortMap.ARMLIFT);
		leftGrabberMotor = new WPI_TalonSRX (PortMap.LEFTGRABBER);
		rightGrabberMotor = new WPI_TalonSRX (PortMap.RIGHTGRABBER);
		liftSpeed = 0;
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	public void lift(Controllers gamepad) {
		brakeMode = NeutralMode.Brake;
		armLiftMotor.setNeutralMode(brakeMode);
		liftSpeed = -gamepad.getSecondaryLeft();
		boolean holdLift;
		double grabberSpeed = gamepad.getSecondaryRight();
		
		if(gamepad.getSecondaryRawButton(6))
		{
			holdLift = true;
			liftSpeed = .05;
		}
		else {
			holdLift = false;
		}
		
		if (liftSpeed>0 && !holdLift) {
			liftSpeed *= .5;
		}
		
		if(liftSpeed<0 && !holdLift) {
			liftSpeed *= .3;
		}
		armLiftMotor.set(liftSpeed);
		
		leftGrabberMotor.set(grabberSpeed);
		rightGrabberMotor.set(-grabberSpeed);
	}
	
	private void stopArm(){
		brakeMode = NeutralMode.Coast;
		armLiftMotor.setNeutralMode(brakeMode);
		armLiftMotor.set(0);
		/*leftGrabberMotor.set(0);
		rightGrabberMotor.set(0);*/
	}

	public int shutdown() {
		stopArm();
		return 1;
	}

}
