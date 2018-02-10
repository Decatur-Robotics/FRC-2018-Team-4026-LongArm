package org.usfirst.frc.team4026.robot;


//Contains commonly use methods for teleop functions
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
	
	
}
