package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pneumatics implements Subsystem {

	DoubleSolenoid shifter;
	public DoubleSolenoid grabberPistons;
	public DoubleSolenoid intakePistons;
	//RevRoboticsAirPressureSensor airPressureSensor = new RevRoboticsAirPressureSensor(PortMap.PRESSURESENSOR);
	Compressor compressor;
	boolean isInitialized = false;

	@Override
	public int init() {
		if (!isInitialized) {
			shifter = new DoubleSolenoid(PortMap.SHIFTLOWGEAR, PortMap.SHIFTHIGHGEAR);
			grabberPistons = new DoubleSolenoid(PortMap.GRABBERRPISTONIN, PortMap.GRABBERPISTONOUT);
			intakePistons = new DoubleSolenoid(PortMap.INTAKEPISTONIN, PortMap.INTAKEPISTONOUT);
			compressor = new Compressor();
			compressor.setClosedLoopControl(true);
			shifter.set(Value.kForward);
			grabberPistons.set(Value.kReverse);
			intakePistons.set(Value.kReverse);
			isInitialized = true;
			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	void shift(int lowGearButton, int highGearButton, Controllers driveGamepad) 
	{
		if (driveGamepad.getPrimaryRawButton(highGearButton)) 
		{
			shifter.set(DoubleSolenoid.Value.kForward);

		} else if (driveGamepad.getPrimaryRawButton(lowGearButton)) 
		{
			shifter.set(DoubleSolenoid.Value.kReverse);
		}
	}

	void actuateGrabber(int inButton, int outButton, Controllers gamepad) 
	{
		if (gamepad.getSecondaryRawButton(inButton)) 
		{
			grabberPistons.set(DoubleSolenoid.Value.kForward);

		} else if (gamepad.getSecondaryRawButton(outButton)) 
		{
			grabberPistons.set(DoubleSolenoid.Value.kReverse);
		}
	}

	void actuateIntake(int inButton, int outButton, Controllers gamepad) 
	{
		if (gamepad.getSecondaryRawButton(inButton)) {
			intakePistons.set(DoubleSolenoid.Value.kForward);

		} else if (gamepad.getSecondaryRawButton(outButton)) {
			intakePistons.set(DoubleSolenoid.Value.kReverse);
		}
	}

	String gearState() 
	{
		if (shifter.get() == DoubleSolenoid.Value.kForward) {
			return "High Gear";
		} else {
			return "Low Gear";
		}
	}
	
	String intakeState() 
	{
		if (intakePistons.get() == DoubleSolenoid.Value.kForward) {
			return "In";
		} else {
			return "Out";
		}
	}
	
	String grabberState() 
	{
		if (grabberPistons.get() == DoubleSolenoid.Value.kForward) {
			return "In";
		} else {
			return "Out";
		}
	}

	@Override
	public int shutdown() {
		shifter.set(Value.kForward);
		grabberPistons.set(Value.kReverse);
		intakePistons.set(Value.kReverse);
		return 1;
	}
	
	public void updateDashboard()
	{	
		//SmartDashboard.putNumber("Air Pressure", airPressureSensor.getAirPressurePsi());
		SmartDashboard.putString("Gear State", gearState());
		SmartDashboard.putString("Intake Piston State", intakeState());
		SmartDashboard.putString("Grabber Piston State", grabberState());
	}

}
