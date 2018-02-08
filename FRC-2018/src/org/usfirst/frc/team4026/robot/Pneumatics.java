package org.usfirst.frc.team4026.robot;

//import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Pneumatics implements Subsystem{
	
	DoubleSolenoid shifter;
	//Compressor compressor;
	
	boolean isInitialized = false;

	
	public int init() {
		if(!isInitialized){
		shifter = new DoubleSolenoid(PortMap.SHIFTLOWGEAR,PortMap.SHIFTHIGHGEAR);
		/*compressor = new Compressor();
		compressor.setClosedLoopControl(true);*/
		shifter.set(Value.kForward);
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	
	void shift(int highGearButton, int lowGearButton,Controllers driveGamepad)
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
	@Override
	public int shutdown() {
		shifter.set(Value.kForward);
		return 1;
	}
	

}

