package org.usfirst.frc.team4026.robot;


//Contains commonly used methods for teleop functions
public class TeleopAutoFunction {
	private long timeoutStartTime = 0;
	public int state;
	public void resetTimeoutTimer(){
		timeoutStartTime = System.currentTimeMillis();
	}
	
	protected long getTimeoutTimer(){
		return  System.currentTimeMillis() - timeoutStartTime;
	}
	
	protected boolean timedOut(long timeout){
		if (getTimeoutTimer() > timeout){
			return true;
		}
		else 
			return false;
	}
	protected void autoIntake(Controllers gamepad) {
		int state = 0;
		while(gamepad.getSecondaryRawButton(8)) {
			switch(state) {
			case 0:
				// TODO: write code to start intake
				state++;
				break;
			case 1:
				// TODO: write code to adjust/align grabber
				state++;
				break;
			case 2:
				// TODO: write code to cube activates sensor
				state++;
				break;
			}
		}
	}
	protected void autoOutput(Controllers gamepad) {
		int state = 0;
		while(gamepad.getSecondaryRawButton(7)) {
			switch(state) {
			case 0:
				// TODO: write code to align robot to scale
				state++;
				break;
			case 1:
				// TODO: write code to start outputting cube
				state++;
				break;
			case 2:
				// TODO: write code to stop outputting
				state++;
				break;
			}
		}
	}
}
