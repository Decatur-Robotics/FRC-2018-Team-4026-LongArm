package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Pneumatics implements Subsystem {

	DoubleSolenoid shifter;
	public DoubleSolenoid grabberPistons;
	public DoubleSolenoid intakeLiftPistons;
	RevRoboticsAirPressureSensor airPressureSensor;
	Compressor compressor;
	boolean isInitialized = false;

	@Override
	public int init() {
		if (!isInitialized) {
			shifter = new DoubleSolenoid(PortMap.SHIFTLOWGEAR, PortMap.SHIFTHIGHGEAR);
			grabberPistons = new DoubleSolenoid(PortMap.GRABBERRPISTONIN, PortMap.GRABBERPISTONOUT);
			intakeLiftPistons = new DoubleSolenoid(PortMap.INTAKEPISTONIN, PortMap.INTAKEPISTONOUT);
			airPressureSensor = new RevRoboticsAirPressureSensor(PortMap.PRESSURESENSOR);
			compressor = new Compressor();
			compressor.setClosedLoopControl(true);
			shifter.set(Value.kReverse);
			intakeLiftPistons.set(Value.kReverse);
			isInitialized = true;
			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	void shift(int lowGearButton, int highGearButton, Controllers driveGamepad) {
		if (driveGamepad.getPrimaryRawButton(highGearButton)) {
			shifter.set(DoubleSolenoid.Value.kForward);

		} else if (driveGamepad.getPrimaryRawButton(lowGearButton)) {
			shifter.set(DoubleSolenoid.Value.kReverse);
		}
	}

	public void setHighGear() {
		shifter.set(DoubleSolenoid.Value.kForward);

	}

	public void setLowGear() {
		shifter.set(DoubleSolenoid.Value.kReverse);
	}

	void actuateGrabber(int inButton, int outButton, Controllers gamepad) {
		if (gamepad.getSecondaryRawButton(inButton)) {
			grabberPistons.set(DoubleSolenoid.Value.kForward);

		} else if (gamepad.getSecondaryRawButton(outButton)) {
			grabberPistons.set(DoubleSolenoid.Value.kReverse);
		}
	}

	public void openGrabber() {
		grabberPistons.set(DoubleSolenoid.Value.kReverse);
	}

	public void closeGrabber() {
		grabberPistons.set(DoubleSolenoid.Value.kForward);
	}

	void liftIntake(int downButton, int upButton, Controllers gamepad) {
		if (gamepad.getSecondaryRawButton(downButton)) {
			intakeLiftPistons.set(DoubleSolenoid.Value.kForward);

		} else if (gamepad.getSecondaryRawButton(upButton)) {
			intakeLiftPistons.set(DoubleSolenoid.Value.kReverse);
		}
	}
	
	public void intakeUp() {
		intakeLiftPistons.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void intakeDown() {
		intakeLiftPistons.set(DoubleSolenoid.Value.kForward);
	}
	
	String gearState() {
		if (shifter.get() == DoubleSolenoid.Value.kForward) {
			return "High Gear";
		} else {
			return "Low Gear";
		}
	}

	String intakeLiftState() {
		if (intakeLiftPistons.get() == DoubleSolenoid.Value.kForward) {
			return "Up";
		} else {
			return "Down";
		}
	}

	String grabberState() {
		if (grabberPistons.get() == DoubleSolenoid.Value.kForward) {
			return "In";
		} else {
			return "Out";
		}
	}
	
	public void run(Robot robot) {
		shift(1, 3, robot.controllers);
	}

	@Override
	public int shutdown() {
		shifter.set(Value.kReverse);
		intakeLiftPistons.set(Value.kReverse);
		return 1;
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putNumber("Air Pressure", airPressureSensor.getAirPressurePsi());
		SmartDashboard.putString("Gear State", gearState());
		SmartDashboard.putString("Intake Piston State", intakeLiftState());
		SmartDashboard.putString("Grabber Piston State", grabberState());
	}

}
