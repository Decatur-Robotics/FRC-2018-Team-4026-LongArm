package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Arm implements Subsystem{

	boolean isInitialized = false;
	WPI_TalonSRX armLiftMotor;
	WPI_TalonSRX leftGrabberMotor;
	WPI_TalonSRX rightGrabberMotor;
	
	public int init() {
		if(!isInitialized){
		
		armLiftMotor = new WPI_TalonSRX (PortMap.ARMLIFT);
		leftGrabberMotor = new WPI_TalonSRX (PortMap.LEFTGRABBER);
		rightGrabberMotor = new WPI_TalonSRX (PortMap.RIGHTGRABBER);
		
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	public void lift(Controllers gamepad) {
		double liftSpeed = -gamepad.getSecondaryLeft();
		double intakeSpeed = gamepad.getSecondaryRight();
		if (liftSpeed>0) {
			liftSpeed *= .3;
		}
		if(liftSpeed<0) {
			liftSpeed *= .1;
		}
		armLiftMotor.set(liftSpeed);
		leftGrabberMotor.set(intakeSpeed);
		rightGrabberMotor.set(intakeSpeed);
	}
	
	private void stopDrive(){
		armLiftMotor.set(0);
		leftGrabberMotor.set(0);
		rightGrabberMotor.set(0);
	}

	public int shutdown() {
		stopDrive();
		return 1;
	}

}
