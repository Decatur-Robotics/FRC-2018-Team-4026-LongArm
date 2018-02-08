package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Controller implements Subsystem{
	
	Joystick gamepad;
	boolean isInitialized = false;

	public int init(){
		if(!isInitialized){
			gamepad = new Joystick(PortMap.PRIMARYCONTROLLER);
			isInitialized = true;
		}
	//Return 1 if tries to reinit
	return 1;
	}

	public double getLeft(){
		return gamepad.getY();
		
		
	}
	public double getRight(){
		return gamepad.getThrottle();
	}
	public boolean getRawButton(int button) {
		return gamepad.getRawButton(button);
	}
	@Override
	public int shutdown() {
		
		return 1;
	}

}
