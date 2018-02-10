package org.usfirst.frc.team4026.robot;

import com.kauailabs.navx.frc.AHRS;
//Written by Walden on Feb 9th 2018
//Last modified
public class Tipper extends TeleopAutoFunction {
	//Set the point in which the robot will try ad right its self
	private static final int TIPPING_POINT_DEGS = 15;
	private static double ROBOT_REACTION_SPEED = -.3;
	
	//In milliseconds
	private static final long TIMEOUT_TIME = 500; 
	private  AHRS Navx;
	private Drivetrain Drive;
	
	
	public Tipper(AHRS navxVar, Drivetrain driveVar){
		Navx = navxVar;
		Drive = driveVar;
		resetTimeoutTimer();
	}
	
	public boolean tippingControl()
	{
		if(!robotIsTipping()){
			return false;
		}
		
		switch (state)
		{
		case 0:
			if (robotIsTipping())
			{
				resetTimeoutTimer();
				state++;
			}
			else
			{
				state = 0;
				return false;
			}
			break;
		case 1:
			if (robotIsTipping() && !timedOut(TIMEOUT_TIME))
			{
				//Log this bad boi
				System.out.println("Bot is tipping with pitch of " + Navx.getPitch());
				//Throw that bot into reverse baby
				Drive.setDriveMotors(ROBOT_REACTION_SPEED, ROBOT_REACTION_SPEED);
			}
			else
			{
				state = 0;
				return false;
			}
			
			
		}
		return true;
		
	}
	//Returns TRUE if the bot is angled back past TIPPING_POINT_DEGS
	private boolean robotIsTipping()
	{
		if (Navx.getPitch() > TIPPING_POINT_DEGS)
		{
			return true;
		}else{
			return false;
		}
	}
	
}
