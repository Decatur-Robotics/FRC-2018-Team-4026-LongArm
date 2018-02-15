package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {
	
	static final boolean USE_DRIVE_TIMER = false;
	static final double DRIVE_TICKSPERREV = 64;	
	
	Timer autoDriveTimer = new Timer();
	
	String gameData;
	String teamSwitch;
	String scale;
	String opponentSwitch;

	public void crossLineAuto(Drivetrain drivetrain) 
	{
		decodeGameData();
		updateDashboard();
		autoDriveRobot(drivetrain, 0.3, 0.3, 0, 36, USE_DRIVE_TIMER);
		
	}

	public void position1Scale(Drivetrain drivetrain) 
	{
		decodeGameData();
		updateDashboard();
		
	}

	public void position1Switch(Drivetrain drivetrain) 
	{
		decodeGameData();
		updateDashboard();
		
	}

	public void position2Switch(Drivetrain drivetrain) 
	{
		decodeGameData();
		updateDashboard();
		
	}

	public void position3Scale(Drivetrain drivetrain) 
	{
		decodeGameData();
		updateDashboard();
		
	}

	public void position3Switch(Drivetrain drivetrain) 
	{
		decodeGameData();
		updateDashboard();
		
	}
	
	void resetDrive(Drivetrain drivetrain, boolean isTimerBased)
	{
		if(isTimerBased)
		{
			autoDriveTimer.reset();
			autoDriveTimer.start();
			drivetrain.gyro.reset();;
		}
		else
		{
			drivetrain.LeftEncoder.reset();
			drivetrain.RightEncoder.reset();
			drivetrain.gyro.reset();
		}
	}
	
	boolean autoDriveRobot(Drivetrain drivetrain, double velocityLeft, double velocityRight, double timeSec, double targetDistanceInch, boolean isTimerBased)
	{
		double err = 0.0;
		double driveDistInch = 0.0;
		double percentPower = 0.0;
		if(isTimerBased)
		{
			if(autoDriveTimer.get() <= timeSec)
			{
				//leftDriveMotor.set(-velocityLeft);
				//rightDriveMotor.set(velocityRight);
				drivetrain.keepDriveStraight(velocityLeft, velocityRight, 0);
			}
			else
			{
				drivetrain.stopDrive();
				return true;
			}
		}
		else
		{
			driveDistInch = Math.abs(convertDriveTicksToInches(drivetrain.RightEncoder.get()));
			if(driveDistInch < Math.abs(targetDistanceInch))
			{
				//leftDriveMotor.set(-velocityLeft);
				//rightDriveMotor.set(velocityRight);
				err = Math.abs(targetDistanceInch) - driveDistInch;
				percentPower = (err / Math.abs(targetDistanceInch));

				if(err <= 48.0)	//If within 24" start slowing down
				{
					velocityLeft *= percentPower;
					velocityRight *= percentPower;

					if(velocityLeft < 0.0 && velocityLeft > -0.2)
						velocityLeft = -0.2;
					else if(velocityLeft > 0.0 && velocityLeft < 0.2)
						velocityLeft = 0.2;
					if(velocityRight < 0.0 && velocityRight > -0.2)
						velocityRight = -0.2;
					else if(velocityRight > 0.0 && velocityRight < 0.2)
						velocityRight = 0.2;
				}
				drivetrain.keepDriveStraight(velocityLeft, velocityRight, 0);
			}
			else
			{
				drivetrain.stopDrive();
				return true;
			}
		}
		return false;
	}

	double convertDriveTicksToInches(int encTicks)
	{
		return (encTicks / DRIVE_TICKSPERREV) * 3.14 * 5.0;
	}
	
	public String getGameData()
	{
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		return gameData;
	}
	
	public void decodeGameData()
	{
		if(gameData.length() > 0)
        {
			//Check team switch
			if(gameData.charAt(0) == 'L')
			{
				teamSwitch = "Left";
			} 
			else 
			{
				teamSwitch = "Right";
			}
			
			//Check Scale
			if(gameData.charAt(1) == 'L')
			{
				scale = "Left";
			} 
			else 
			{
				scale = "Right";
			}
			
			//Check opponent switch
			if(gameData.charAt(2) == 'L')
			{
				opponentSwitch = "Left";
			} 
			else 
			{
				opponentSwitch = "Right";
			}
        }
		else
		{
			teamSwitch = "Not Recieved Data Yet";
			scale = "Not Recieved Data Yet";
			opponentSwitch = "Not Recieved Data Yet";
		}
	}
	public void updateDashboard()
	{
		SmartDashboard.putString("Team Switch", teamSwitch);
		SmartDashboard.putString("Scale", scale);
		SmartDashboard.putString("Opponent Switch", opponentSwitch);
		
	}
}
