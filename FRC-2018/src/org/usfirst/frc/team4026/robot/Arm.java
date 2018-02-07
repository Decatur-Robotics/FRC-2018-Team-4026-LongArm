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
		leftGrabberMotor = new WPI_TalonSRX (PortMap.LEFTINTAKE);
		rightGrabberMotor = new WPI_TalonSRX (PortMap.LEFTINTAKE);
		
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	public void lift(Controller gamepad) {
		double liftSpeed = gamepad.getLeft();
		double intakeSpeed = gamepad.getRight();
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
