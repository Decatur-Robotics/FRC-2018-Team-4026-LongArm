package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	static final boolean USE_DRIVE_TIMER = false; // what is the alternative to USE_DRIVE_TIMER?
	int state = 0;
	Timer AutoDriveTimer = new Timer();
	Timer ScoreScaleTimer = new Timer();
	Timer ScoreSwitchTimer = new Timer();
	String gameData;
	SwitchScale teamSwitch;
	SwitchScale scale;
	SwitchScale opponentSwitch;
	
	public void crossLineAuto(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.setLowGear();
		if (!robot.autoDriveRobot(AutoDriveTimer, 0.3, 0.3, 0, 70)) {

		} else {
			robot.stopDrivetrain();
		}

	}

	public void position1Auto(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals(SwitchScale.SCALE_LEFT)) 
		{
			scoreScale(robot, true);
		} 
		else 
		{
			if (teamSwitch.equals(SwitchScale.TEAM_SWITCH_LEFT)) 
			{
				scoreSwitch(robot, true);
			} 
			else 
			{
				crossLineAuto(robot);
			}
		}
	}

	public void position1Scale(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals(SwitchScale.SCALE_LEFT)) 
		{
			scoreScale(robot, false);
		} 
		else 
		{
			scaleCross(robot, true);
		}
	}

	public void position2Auto(Robot robot) {
		position2Switch(robot);
	}

	public void position3Auto(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals(SwitchScale.SCALE_RIGHT)) 
		{
			scoreScale(robot, false);
		} 
		else 
		{
			if (teamSwitch.equals(SwitchScale.TEAM_SWITCH_RIGHT)) {
				scoreSwitch(robot, false);
			} else {
				crossLineAuto(robot);
			}
		}
	}

	public void position3Scale(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals(SwitchScale.SCALE_RIGHT)) {
			scoreScale(robot, false);
		} else {
			scaleCross(robot, false);
		}
	}

	public void scoreScale(Robot robot, boolean leftSide) {

		robot.setHighGear();
		switch (state) {
		case 0:
			if (robot.autoDriveRobot(AutoDriveTimer, .9, .9, 0, 260)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (robot.turnGyro(80, .4)) 
				{
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			} else {
				if (robot.turnGyro( -80, .4)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					ScoreScaleTimer.reset();
					ScoreScaleTimer.start();
					state++;
				}
			}
			break;
		case 2:
			//robot.arm.liftToSwitch();
			if (robot.autoDriveRobot(AutoDriveTimer, -.65, -.65, 0, 60) || ScoreScaleTimer.get() > 2) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				//state++;
			}
			break;
		case 3:
			if (robot.liftToScale()) {
				robot.holdLift();
				System.out.println("Case 2 started");
				state++;
			}
			robot.updateLiftMotor();
			break;
		case 4:
			if (leftSide) 
			{
				if (robot.autoDriveRobot(AutoDriveTimer, 0.4, 0.4, 0, 24)) 
				{
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			} 
			else 
			{
				if (robot.autoDriveRobot(AutoDriveTimer, 0.4, 0.4, 0, 24)) 
				{
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			}
			break;
		case 5:
			robot.holdLift();
			robot.openGrabber();
			robot.updateLiftMotor();
			state++;
			break;
		case 6:
			if (robot.autoDriveRobot(AutoDriveTimer, -0.4, -0.4, 0, 24)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 7:
			if (robot.liftToGround()) {
				robot.holdLift();
				System.out.println("Case 5 started");
				state++;
				robot.updateLiftMotor();
			}
			robot.updateLiftMotor();
			break;
		case 8:
			robot.stopDrivetrain();
			break;
		}
	}
	
	//Scores switch from the side
	public void scoreSwitch(Robot robot, boolean leftSide) {
		robot.setLowGear();
		switch (state) {
		case 0:
			if (robot.autoDriveRobot(AutoDriveTimer,  .5, .5, 0, 90)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				Timer.delay(.5);
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (robot.turnGyro( 77, .25)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			} else {
				if (robot.turnGyro( -79, .2)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			}
			break;
		case 2:
			if (robot.liftToSwitch()) {
				robot.holdLift();
				System.out.println("Case 2 started");
				ScoreSwitchTimer.reset();
				ScoreSwitchTimer.start();
				state++;
			}
			robot.updateLiftMotor();
			break;
		case 3:
			if(ScoreSwitchTimer.get() > 3)
			{
				
			}
			if (robot.autoDriveRobot(AutoDriveTimer, 0.4, 0.4, 0, 8, USE_DRIVE_TIMER || ScoreSwitchTimer.get() > 3 )) 
			{
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 4:
			robot.openGrabber();
			state++;
			break;
		case 5:
			if (robot.autoDriveRobot(AutoDriveTimer,  0.4, 0.4, 0, -24)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 6:
			if (robot.liftToGround()) {
				robot.holdLift();
				System.out.println("Case 5 started");
				state++;
			}
			robot.updateLiftMotor();
			break;
		case 7:
			robot.stopDrivetrain();
			break;
		}
	}

	public void position2Switch(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.setHighGear();
		if (teamSwitch.equals(SwitchScale.TEAM_SWITCH_LEFT)) 
		{
			positionTwoTeamSwitchLeft(robot);

		} 
		else if (teamSwitch.equals("Right"))
		{
			positionTwoTeamSwitchRight(robot);
		}
	}

	private void positionTwoTeamSwitchRight(Robot robot) 
	{
		switch (state) 
		{
		case 0:
			if (robot.autoDriveRobot(AutoDriveTimer,  .5 , .5, 0, 35)) 
			{
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 1:
			if (robot.liftToSwitch()) 
			{
				robot.holdLift();
				System.out.println("Case 2 started");
				state++;
			}
			robot.updateLiftMotor();
			break;

		case 2:

			if (robot.turnGyro( 80, .4)) {
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 3:
			if (robot.autoDriveRobot(AutoDriveTimer,  0.5, 0.5, 0, 40)) {
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 4:
			if (robot.turnGyro( -80, .4)) {
				Timer.delay(.1);
				robot.stopDrivetrain();
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 5:

			if (robot.getDrivetrainAccelerometerXaxis() > 1 || robot.autoDriveRobot(AutoDriveTimer,  .5, .5, 0, 55)) 
			{
				state++;
				robot.stopDrivetrain();
			}
			break;
		case 6:
			robot.openGrabber();
			robot.intakeDown();
			state++;
		}
	}

	private void positionTwoTeamSwitchLeft(Robot robot) 
	{
		switch (state) 
		{
		case 0:
			if (robot.autoDriveRobot(AutoDriveTimer,  .5, .5, 0, 35)) {
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 1:
			if (robot.liftToSwitch()) {
				robot.holdLift();
				System.out.println("Case 2 started");
				state++;
			}
			robot.updateLiftMotor();
			break;

		case 2:

			if (robot.turnGyro( -80, .4)) {
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 3:
			if (robot.autoDriveRobot(AutoDriveTimer,  0.55, 0.55, 0, 65) ) 
			{
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 4:
			if (robot.turnGyro( 85, .4)) {
				Timer.delay(.1);
				robot.stopDrivetrain();
				robot.resetGyroAndLeftEncoder();
				state++;
			}
			break;
		case 5:

			if (robot.autoDriveRobot(AutoDriveTimer,  .5, .5, 0, 55, false)|| robot.getDrivetrainAccelerometerXaxis() > 1 ) 
			{
				state++;
				robot.stopDrivetrain();

			}
			break;
		case 6:
			robot.openGrabber();
			robot.intakeDown();
			state++;

		}
	}

	public void scaleCross(Robot robot, boolean leftSide) {
		robot.setLowGear();
		switch (state) {
		case 0:
			if (robot.autoDriveRobot(AutoDriveTimer,  .3, .3, 0, 229)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (robot.turnGyro( 90, .3)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			} else {
				if (robot.turnGyro( -90, .3)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			}
			break;
		case 2:
			if (robot.autoDriveRobot(AutoDriveTimer,  .3, .3, 0, 191)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 3:
			if (leftSide) {
				if (robot.turnGyro( -90, .3)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			} else {
				if (robot.turnGyro( 90, .3)) {
					robot.resetGyroAndLeftEncoder();
					robot.stopDrivetrain();
					state++;
				}
			}
			break;
		case 4:
			if (robot.autoDriveRobot(AutoDriveTimer,  .3, .3, 0, 30)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 5:
			if (robot.liftToScale()) {
				robot.holdLift();
				System.out.println("Case 2 started");
				// state++;
			}
			robot.updateLiftMotor();
			break;
		case 6:
			if (robot.autoDriveRobot(AutoDriveTimer,  0.4, 0.4, 0, 24)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 7:
			robot.openGrabber();
			state++;
			break;
		case 8:
			if (robot.autoDriveRobot(AutoDriveTimer,  0.4, 0.4, 0, -24)) {
				robot.resetGyroAndLeftEncoder();
				robot.stopDrivetrain();
				state++;
			}
			break;
		case 9:
			if (robot.liftToGround()) {
				robot.holdLift();
				System.out.println("Case 5 started");
				state++;
			}
			robot.updateLiftMotor();
			break;
		case 10:
			robot.stopDrivetrain();
			break;
		}
	}

	public String getGameData() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		return gameData;
	}

	public void decodeGameData() {
		if (gameData.length() > 0) {
			// Check team switch
			if (gameData.charAt(0) == 'L') {
				teamSwitch = SwitchScale.TEAM_SWITCH_LEFT;
			} else {
				teamSwitch = SwitchScale.TEAM_SWITCH_RIGHT;
			}

			// Check Scale
			if (gameData.charAt(1) == 'L') {
				scale = SwitchScale.SCALE_LEFT;
			} else {
				scale = SwitchScale.SCALE_RIGHT;
			}

			// Check opponent switch
			if (gameData.charAt(2) == 'L') {
				opponentSwitch = SwitchScale.OPPONENT_SWITCH_LEFT;
			} else {
				opponentSwitch = SwitchScale.OPPONENT_SWITCH_RIGHT;
			}
		} else {
			teamSwitch = SwitchScale.UNDEFINED;
			scale = SwitchScale.UNDEFINED;
			opponentSwitch = SwitchScale.UNDEFINED;
		}
	}

	public void updateDashboard() {
		SmartDashboard.putString("Team Switch", teamSwitch.getKey());
		SmartDashboard.putString("Scale", scale.getKey());
		SmartDashboard.putString("Opponent Switch", opponentSwitch.getKey());

	}
	
	@SuppressWarnings("unused")
	public void  cheesecakeAuto() {		
		int cheeseState = 0;
		Timer cheeseTimer = new Timer();
		cheeseTimer.reset();
		double cheeseLeft = .5;
		double cheeseRight = -.5;
		switch (cheeseState) {
		case 0:
			while (cheeseTimer.get() < 3 /* && isEnabled() */) {
				//fLeftMotor.set(cheeseLeft);
				//BLeftMotor.set(cheeseLeft);
				//fRightMotor.set(cheeseRight);
				//bRightMotor.set(cheeseRight);
			}
			state++;
			break;
		case 1:
			//fLeftMotor.set(0);
			//BLeftMotor.set(0);
			//fRightMotor.set(0);
			//bRightMotor.set(0);
			break;
		}
}
	
	private enum SwitchScale
	{
		TEAM_SWITCH_LEFT("Team Switch is Left"),
		TEAM_SWITCH_RIGHT("Team Switch is Right"),
		OPPONENT_SWITCH_LEFT("Opponent Switch is Left"),
		OPPONENT_SWITCH_RIGHT("Opponent Switch is Right"),
		SCALE_RIGHT("Scale is Right Side"),
		SCALE_LEFT("Scale is Left Side"),
		UNDEFINED("No Data Received");
		
		private String key;

		private SwitchScale(String key)
		{
			this.key = key;
		}

		public String getKey()
		{
			return key;
		}

	}
}