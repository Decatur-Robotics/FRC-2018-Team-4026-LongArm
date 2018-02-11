package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Pneumatics implements Subsystem{
	
	DoubleSolenoid shifter;
	DoubleSolenoid grabberPistons;
	DoubleSolenoid intakePistons;
	Compressor compressor;
	boolean isInitialized = false;

	
	public int init() {
		if(!isInitialized){
		shifter = new DoubleSolenoid(PortMap.SHIFTLOWGEAR,PortMap.SHIFTHIGHGEAR);
		grabberPistons = new DoubleSolenoid(PortMap.GRABBERPISTONIN,PortMap.GRABBERPISTONOUT);
		//intakePistons = new DoubleSolenoid(PortMap.INTAKEPISTONIN,PortMap.INTAKEPISTONOUT);
		compressor = new Compressor();
		compressor.setClosedLoopControl(true);
		shifter.set(Value.kForward);
		grabberPistons.set(Value.kForward);
		//intakePistons.set(Value.kForward);
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	
	void shift(int lowGearButton, int highGearButton,Controllers driveGamepad)
	{
		if (driveGamepad.getPrimaryRawButton(highGearButton))
		{
			shifter.set(DoubleSolenoid.Value.kForward);
			
		}
		else if (driveGamepad.getPrimaryRawButton(lowGearButton))
		{
			shifter.set(DoubleSolenoid.Value.kReverse);
		}
	}
	void actuateGrabber(int inButton, int outButton,Controllers gamepad)
	{
		if (gamepad.getSecondaryRawButton(inButton))
		{
			grabberPistons.set(DoubleSolenoid.Value.kForward);
			
		}
		else if (gamepad.getSecondaryRawButton(outButton))
		{
			grabberPistons.set(DoubleSolenoid.Value.kReverse);
		}
	}
	void actuateIntake(int inButton, int outButton,Controllers gamepad)
	{
		if (gamepad.getSecondaryRawButton(inButton))
		{
			intakePistons.set(DoubleSolenoid.Value.kForward);
			
		}
		else if (gamepad.getSecondaryRawButton(outButton))
		{
			intakePistons.set(DoubleSolenoid.Value.kReverse);
		}
	}
	String gearState()
	{
		if (shifter.get() == DoubleSolenoid.Value.kForward)
		{
			return "High Gear";
		}
		else
		{
			return "Low Gear";
		}
	}
	@Override
	public int shutdown() {
		shifter.set(Value.kForward);
		return 1;
	}
	

}

