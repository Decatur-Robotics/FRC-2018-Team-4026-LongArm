package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	static final boolean USE_DRIVE_TIMER = false; // what is the alternative to USE_DRIVE_TIMER?
	static final double DRIVE_TICKSPERREV = 392;
	int state = 0;
	double gyroKi = 0;
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
		robot.pneumatics.setLowGear();
		if (!autoDriveRobot(robot.drivetrain, 0.3, 0.3, 0, 70, USE_DRIVE_TIMER)) {

		} else {
			robot.drivetrain.stopDrive();
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

		robot.pneumatics.setHighGear();
		switch (state) {
		case 0:
			if (autoDriveRobot(robot.drivetrain, .9, .9, 0, 260, USE_DRIVE_TIMER)) {
				resetGyroAndLeftEncoder(robot);
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, 80, .4)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, -80, .4)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					ScoreScaleTimer.reset();
					ScoreScaleTimer.start();
					state++;
				}
			}
			break;
		case 2:
			//robot.arm.liftToSwitch();
			if (autoDriveRobot(robot.drivetrain, -.65, -.65, 0, 60, USE_DRIVE_TIMER) || ScoreScaleTimer.get() > 2) {
				resetGyroAndLeftEncoder(robot);
				robot.drivetrain.stopDrive();
				//state++;
			}
			break;
		case 3:
			if (robot.arm.liftToScale()) {
				robot.arm.holdLift();
				System.out.println("Case 2 started");
				state++;
			}
			robot.arm.updateLiftMotor();
			break;
		case 4:
			if (leftSide) 
			{
				if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, 24, USE_DRIVE_TIMER)) 
				{
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			} 
			else 
			{
				if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, 24, USE_DRIVE_TIMER)) 
				{
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			}
			break;
		case 5:
			robot.arm.holdLift();
			robot.pneumatics.openGrabber();
			robot.arm.updateLiftMotor();
			state++;
			break;
		case 6:
			if (autoDriveRobot(robot.drivetrain, -0.4, -0.4, 0, 24, USE_DRIVE_TIMER)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 7:
			if (robot.arm.liftToGround()) {
				robot.arm.holdLift();
				System.out.println("Case 5 started");
				state++;
				robot.arm.updateLiftMotor();
			}
			robot.arm.updateLiftMotor();
			break;
		case 8:
			robot.drivetrain.stopDrive();
			break;
		}
	}

	private void resetGyroAndLeftEncoder(Robot robot) {
		robot.drivetrain.gyro.reset();
		robot.drivetrain.LeftEncoder.reset();
	}
	
	//Scores switch from the side
	public void scoreSwitch(Robot robot, boolean leftSide) {
		robot.pneumatics.setLowGear();
		switch (state) {
		case 0:
			if (autoDriveRobot(robot.drivetrain, .5, .5, 0, 90, USE_DRIVE_TIMER)) {
				resetGyroAndLeftEncoder(robot);
				robot.drivetrain.stopDrive();
				Timer.delay(.5);
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, 77, .25)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, -79, .2)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			}
			break;
		case 2:
			if (robot.arm.liftToSwitch()) {
				robot.arm.holdLift();
				System.out.println("Case 2 started");
				ScoreSwitchTimer.reset();
				ScoreSwitchTimer.start();
				state++;
			}
			robot.arm.updateLiftMotor();
			break;
		case 3:
			if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, 8, USE_DRIVE_TIMER || ScoreSwitchTimer.get() > 3 )) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 4:
			robot.pneumatics.openGrabber();
			state++;
			break;
		case 5:
			if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, -24, USE_DRIVE_TIMER)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 6:
			if (robot.arm.liftToGround()) {
				robot.arm.holdLift();
				System.out.println("Case 5 started");
				state++;
			}
			robot.arm.updateLiftMotor();
			break;
		case 7:
			robot.drivetrain.stopDrive();
			break;
		}
	}

	public void position2Switch(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setHighGear();
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
			if (autoDriveRobot(robot.drivetrain, .5 , .5, 0, 35, USE_DRIVE_TIMER)) 
			{
				resetGyroAndLeftEncoder(robot);
				state++;
			}
			break;
		case 1:
			if (robot.arm.liftToSwitch()) 
			{
				robot.arm.holdLift();
				System.out.println("Case 2 started");
				state++;
			}
			robot.arm.updateLiftMotor();
			break;

		case 2:

			if (turnGyro(robot.drivetrain, 80, .4)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				state++;
			}
			break;
		case 3:
			if (autoDriveRobot(robot.drivetrain, 0.5, 0.5, 0, 40, USE_DRIVE_TIMER)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				state++;
			}
			break;
		case 4:
			if (turnGyro(robot.drivetrain, -80, .4)) {
				Timer.delay(.1);
				robot.drivetrain.stopDrive();
				resetGyroAndLeftEncoder(robot);
				state++;
			}
			break;
		case 5:

			if (robot.drivetrain.Accel.getX() > 1 || autoDriveRobot(robot.drivetrain, .5, .5, 0, 55, false)) 
			{
				state++;
				robot.drivetrain.stopDrive();
			}
			break;
		case 6:
			robot.pneumatics.openGrabber();
			robot.pneumatics.intakeDown();
			state++;
		}
	}

	private void positionTwoTeamSwitchLeft(Robot robot) 
	{
		switch (state) 
		{
		case 0:
			if (autoDriveRobot(robot.drivetrain, .5, .5, 0, 35, USE_DRIVE_TIMER)) {
				resetGyroAndLeftEncoder(robot);
				state++;
			}
			break;
		case 1:
			if (robot.arm.liftToSwitch()) {
				robot.arm.holdLift();
				System.out.println("Case 2 started");
				state++;
			}
			robot.arm.updateLiftMotor();
			break;

		case 2:

			if (turnGyro(robot.drivetrain, -80, .4)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				state++;
			}
			break;
		case 3:
			if (autoDriveRobot(robot.drivetrain, 0.55, 0.55, 0, 65, USE_DRIVE_TIMER) ) 
			{
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				state++;
			}
			break;
		case 4:
			if (turnGyro(robot.drivetrain, 85, .4)) {
				Timer.delay(.1);
				robot.drivetrain.stopDrive();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				state++;
			}
			break;
		case 5:

			if (autoDriveRobot(robot.drivetrain, .5, .5, 0, 55, false)|| robot.drivetrain.Accel.getX() > 1 ) 
			{
				state++;
				robot.drivetrain.stopDrive();

			}
			break;
		case 6:
			robot.pneumatics.openGrabber();
			robot.pneumatics.intakeDown();
			state++;

		}
	}

	public void scaleCross(Robot robot, boolean leftSide) {
		robot.pneumatics.setLowGear();
		switch (state) {
		case 0:
			if (autoDriveRobot(robot.drivetrain, .3, .3, 0, 229, USE_DRIVE_TIMER)) {
				resetGyroAndLeftEncoder(robot);
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, 90, .3)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, -90, .3)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			}
			break;
		case 2:
			if (autoDriveRobot(robot.drivetrain, .3, .3, 0, 191, USE_DRIVE_TIMER)) {
				resetGyroAndLeftEncoder(robot);
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 3:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, -90, .3)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, 90, .3)) {
					resetGyroAndLeftEncoder(robot);
					robot.drivetrain.stopDrive();
					state++;
				}
			}
			break;
		case 4:
			if (autoDriveRobot(robot.drivetrain, .3, .3, 0, 30, USE_DRIVE_TIMER)) {
				resetGyroAndLeftEncoder(robot);
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 5:
			if (robot.arm.liftToScale()) {
				robot.arm.holdLift();
				System.out.println("Case 2 started");
				// state++;
			}
			robot.arm.updateLiftMotor();
			break;
		case 6:
			if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, 24, USE_DRIVE_TIMER)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 7:
			robot.pneumatics.openGrabber();
			state++;
			break;
		case 8:
			if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, -24, USE_DRIVE_TIMER)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 9:
			if (robot.arm.liftToGround()) {
				robot.arm.holdLift();
				System.out.println("Case 5 started");
				state++;
			}
			robot.arm.updateLiftMotor();
			break;
		case 10:
			robot.drivetrain.stopDrive();
			break;
		}
	}

	void resetDrive(Drivetrain drivetrain, boolean isTimerBased) {
		if (isTimerBased) {
			AutoDriveTimer.reset();
			AutoDriveTimer.start();
			drivetrain.gyro.reset();
			;
		} else {
			drivetrain.LeftEncoder.reset();
			drivetrain.RightEncoder.reset();
			drivetrain.gyro.reset();
		}
	}

	/**
	 * 
	 * @param drivetrain
	 * @param velocityLeft
	 * @param velocityRight
	 * @param timeSec
	 * @param targetDistanceInch
	 * @param isTimerBased // storrance - I don't see a case where this argument is passed as true
	 * @return
	 */
	boolean autoDriveRobot(Drivetrain drivetrain, double velocityLeft, double velocityRight, double timeSec,
			double targetDistanceInch, boolean isTimerBased) 
	{
		double err = 0.0;
		double driveDistInch = 0.0;
		double percentPower = 0.0;
		if (isTimerBased) 
		{
			timerBasedAutoDrive(drivetrain, velocityLeft, velocityRight, timeSec);
		} 
		else
		{
			driveDistInch = Math.abs(convertDriveTicksToInches(drivetrain.LeftEncoder.get()));
			if (driveDistInch < Math.abs(targetDistanceInch)) 
			{
				// leftDriveMotor.set(-velocityLeft);
				// rightDriveMotor.set(velocityRight);
				err = Math.abs(targetDistanceInch) - driveDistInch;
				percentPower = (err / Math.abs(targetDistanceInch));

				if (err <= 48.0) // If within 24" start slowing down
				{
					velocityLeft *= percentPower;
					velocityRight *= percentPower;

					if (velocityLeft < 0.0 && velocityLeft > -0.2)
						velocityLeft = -0.2;
					else if (velocityLeft > 0.0 && velocityLeft < 0.2)
						velocityLeft = 0.2;
					if (velocityRight < 0.0 && velocityRight > -0.2)
						velocityRight = -0.2;
					else if (velocityRight > 0.0 && velocityRight < 0.2)
						velocityRight = 0.2;
				}

				drivetrain.keepDriveStraight(velocityLeft, velocityLeft, 0);
			} 
			else 
			{
				drivetrain.stopDrive();
				return true;
			}
		}
		return false;
	}

	private boolean timerBasedAutoDrive(Drivetrain drivetrain, double velocityLeft, double velocityRight, double timeSec) 
	{
		if (AutoDriveTimer.get() <= timeSec)
		{
			// leftDriveMotor.set(-velocityLeft);
			// rightDriveMotor.set(velocityRight);
			drivetrain.keepDriveStraight(velocityLeft, velocityRight, 0);
			return false;
		} 
		else 
		{
			drivetrain.stopDrive();
			return true;
		}
	}

	double convertDriveTicksToInches(int encTicks) {
		return (encTicks / DRIVE_TICKSPERREV) * 3.14 * 6.0;
	}

	/*
	 * Used during autonomous to turn the robot to a specified angle.
	 */
	boolean turnGyro(Drivetrain drive, double rAngle, double maxTurnVelocity) {

		double error = 0.0;
		double VelocityToSet = 0.0;
		// Positive gyro angle means turning left
		if (rAngle < drive.gyro.getAngle()) 
		{
			// Start accumulating error if the rate of turning is < 2 deg/sec
			if (drive.gyro.getRate() < 2.0) 
			{
				gyroKi += 0.001;
				if (gyroKi > 0.2) // Cap the integral term
					gyroKi = 0.2;
			}

			error = Math.abs(rAngle) - drive.gyro.getAngle();
			if (drive.gyro.getAngle() <= Math.abs(rAngle) && Math.abs(error) > 2.0) 
			{
				// turn left
				VelocityToSet = (error / 3) + 0.2 + gyroKi; // 140 0.2
				if (Math.abs(VelocityToSet) > maxTurnVelocity)
					VelocityToSet = maxTurnVelocity * (VelocityToSet < 0.0 ? -1.0 : 1.0);
				drive.leftDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // 0.8
				drive.rightDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // 0.8
			} else 
			{
				gyroKi = 0.0;
				drive.stopDrive();
				// if(WaitAsyncUntil(0.5,true))
				return true;
			}
		} 
		else if (rAngle > drive.gyro.getAngle()) 
		{
			// Start accumulating error if the rate of turning is < 2 deg/sec
			if (drive.gyro.getRate() < 2.0) 
			{
				gyroKi += 0.001;
				if (gyroKi > 0.2) // Cap the integral term
					gyroKi = 0.2;
			}

			error = -rAngle - drive.gyro.getAngle();
			if (drive.gyro.getAngle() >= -rAngle && Math.abs(error) > 2.0) 
			{
				// turn right
				VelocityToSet = (error / 270) - 0.2 - gyroKi;
				if (Math.abs(VelocityToSet) > maxTurnVelocity)
					VelocityToSet = maxTurnVelocity * (VelocityToSet < 0.0 ? -1.0 : 1.0);
				drive.leftDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // -0.8
				drive.rightDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // -0.8
			} 
			else 
			{
				gyroKi = 0.0;
				drive.stopDrive();
				// if(WaitAsyncUntil(0.5,true))
				return true;
			}
		} else 
		{
			gyroKi = 0.0;
			drive.stopDrive();
			return true;
		}

		return false;
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