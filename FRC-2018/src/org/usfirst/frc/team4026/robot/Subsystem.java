package org.usfirst.frc.team4026.robot;

public interface Subsystem {
	public abstract int init();
	public abstract void run(Robot robot);
	public abstract int shutdown();
	public abstract void updateDashboard();
	
}