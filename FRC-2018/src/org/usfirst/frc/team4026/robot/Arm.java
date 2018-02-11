package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Arm implements Subsystem{

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
	
	
	public int init() {
		if(!isInitialized){
		
		armLiftMotor = new WPI_TalonSRX (PortMap.ARMLIFT);
		leftGrabberMotor = new WPI_TalonSRX (PortMap.LEFTGRABBER);
		rightGrabberMotor = new WPI_TalonSRX (PortMap.RIGHTGRABBER);
		leftIntakeMotor = new WPI_TalonSRX (PortMap.LEFTINTAKE);
		rightIntakeMotor = new WPI_TalonSRX (PortMap.RIGHTINTAKE);
		liftSpeed = 0;
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	public void lift(Controllers gamepad, Pneumatics pneumatics) {
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
		pneumatics.actuateGrabber(1, 2, gamepad);
		leftGrabberMotor.set(grabberSpeed);
		rightGrabberMotor.set(-grabberSpeed);
	}
	public void intake(Controllers gamepad, Pneumatics pneumatics, double intakeSpeed)
	{	
		pneumatics.actuateIntake(3, 4, gamepad);
		if (!intake) 
		{
			leftIntakeMotor.set(0);
			rightIntakeMotor.set(0);
		}
		if (intake && intakeForward)
		{
			leftIntakeMotor.set(intakeSpeed);
			rightIntakeMotor.set(-intakeSpeed);
		}
		if (intake && intakeReverse)
		{
			leftIntakeMotor.set(-intakeSpeed);
			rightIntakeMotor.set(intakeSpeed);
		}
	}

	private void shouldIIntake(Controllers gamepad) 
	{
		if ((gamepad.getPrimaryRawButton(8) || gamepad.getSecondaryRawButton(8)) && !(gamepad.getPrimaryRawButton(7) || gamepad.getSecondaryRawButton(7)))
		{
			intake = true;
			intakeForward = true;
		}
		else if ((gamepad.getPrimaryRawButton(7) || gamepad.getSecondaryRawButton(7)) && !(gamepad.getPrimaryRawButton(8) || gamepad.getSecondaryRawButton(8)))
		{
			intake = true;
			intakeReverse = true;
		}
		else
		{
			intake= false;
			intakeForward = false;
			intakeReverse = false;
		}	
	}
	private void stopArm(){
		brakeMode = NeutralMode.Coast;
		armLiftMotor.setNeutralMode(brakeMode);
		armLiftMotor.set(0);
		leftGrabberMotor.set(0);
		rightGrabberMotor.set(0);
		leftIntakeMotor.set(0);
		rightIntakeMotor.set(0);
	}

	public int shutdown() {
		stopArm();
		return 1;
	}

}
