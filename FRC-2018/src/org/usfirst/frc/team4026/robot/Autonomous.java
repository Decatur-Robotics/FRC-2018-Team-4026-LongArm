package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	static final boolean USE_DRIVE_TIMER = false;
	static final double DRIVE_TICKSPERREV = 392;
	int state = 0;
	double gyroKi = 0;
	Timer AutoDriveTimer = new Timer();
	Timer ScoreScaleTimer = new Timer();
	Timer ScoreSwitchTimer = new Timer();
	Timer IntakeTimer = new Timer();
	String gameData;
	String teamSwitch;
	String scale;
	String opponentSwitch;

	/*
	 * NetworkTableInstance inst = Robot.inst; NetworkTable autoTable =
	 * inst.getTable("autonomous");
	 * 
	 * NetworkTableEntry distanceOne = autoTable.getEntry("D1");
	 * NetworkTableEntry turnOne = autoTable.getEntry("T1"); NetworkTableEntry
	 * distanceTwo = autoTable.getEntry("D2"); NetworkTableEntry turnTwo =
	 * autoTable.getEntry("T2"); NetworkTableEntry distanceThree =
	 * autoTable.getEntry("D3"); NetworkTableEntry turnThree =
	 * autoTable.getEntry("T3"); NetworkTableEntry distanceFour =
	 * autoTable.getEntry("D4"); NetworkTableEntry turnFour =
	 * autoTable.getEntry("T4");
	 * 
	 * public void netTablerun() { autoTable.addEntryListener("X", (autoTable,
	 * key, entry, value, flags) -> { System.out.println("X Value has Changed:"
	 * + value.getValue()); }, EntryListenerFlags.kNew
	 * |EntryListenerFlags.kUpdate); }
	 */

	// Logic depending on game data and selected starting position
	public void TwoCubeAuto(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals("Left") && teamSwitch.equals("Left")) {

		}
	}

	public void crossLineAuto(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (autoDriveRobot(robot.drivetrain, 0.3, 0.3, 0, 70, USE_DRIVE_TIMER)) {
			robot.drivetrain.stopDrive();
		} else {

		}

	}

	public void position1Auto(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals("Left") && teamSwitch.equals("Left")) {
			if (robot.prioritySelected == Robot.PRIORITYSWITCH) {
				scoreSwitch(robot, true);
			} else {
				scoreScale(robot, true);
			}
			return;
		}

		if (scale.equals("Left")) {
			scoreScale(robot, true);
		} else {
			if (teamSwitch.equals("Left")) {
				scoreSwitch(robot, true);
			} else {
				scaleCross(robot, true);
			}
		}
	}

	public void position2Auto(Robot robot) {
		position2Switch(robot);
	}

	public void position3Auto(Robot robot) {
		decodeGameData();
		updateDashboard();
		if (scale.equals("Right")) {
			scoreScale(robot, false);
		} else {
			if (teamSwitch.equals("Right")) {
				scoreSwitch(robot, false);
			} else {
				crossLineAuto(robot);
			}
		}
	}

	// Methods that run depending on the logic above
	public void scoreScale(Robot robot, boolean leftSide) {
		robot.pneumatics.setHighGear();

		switch (state) {
		case 0:
			ScoreScaleTimer.reset();
			ScoreScaleTimer.start();
			state++;
			break;
		case 1:
			if (ScoreScaleTimer.get() > .5) {
				ScoreScaleTimer.stop();
				ScoreScaleTimer.reset();
				state++;
			}
			break;
		case 2:
			robot.arm.liftToScaleAuto();
			robot.arm.updateLiftMotor();
			if (autoDriveRobot(robot.drivetrain, .75, .75, 0, actualInches(200), USE_DRIVE_TIMER, false, 0)) {
				// robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.arm.updateLiftMotor();
				// robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 3:
			// robot.arm.liftToScaleAuto();
			robot.arm.updateLiftMotor();
			if (autoDriveRobot(robot.drivetrain, .6, .6, 0, actualInches(30), USE_DRIVE_TIMER, false, -20)
					&& robot.arm.liftToScaleAuto()) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.stopDrive();
				robot.arm.updateLiftMotor();
				state++;
			}
			break;
		case 4:
			if (autoDriveRobot(robot.drivetrain, .3, .3, 0, actualInches(33), USE_DRIVE_TIMER, false, 0)) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				robot.arm.updateLiftMotor();
				robot.drivetrain.stopDrive();
				IntakeTimer.reset();
				IntakeTimer.start();
				state++;
			}
			break;
		case 5:
			robot.intake.outakeSlow();
			robot.intake.updateIntakeMotors();
			if (IntakeTimer.get() > .5) {
				robot.intake.stopIntake();
				robot.intake.updateIntakeMotors();
				robot.drivetrain.gyro.reset();
				state++;
			}
			break;
		case 6:
			robot.arm.liftToGroundAuto();
			robot.arm.updateLiftMotor();
			if (turnGyro(robot.drivetrain, 115, .4) && robot.arm.liftToGroundAuto()) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.stopDrive();
				ScoreScaleTimer.reset();
				ScoreScaleTimer.start();
				state++;
			}
			break;
		case 7:
			robot.arm.intakeDown(robot);
			robot.intake.intake(robot, Intake.USE_INTAKE_SENSOR);
			robot.intake.updateIntakeMotors();
			robot.arm.updateLiftMotor();
			if (autoDriveRobot(robot.drivetrain, .32, .32, 0, actualInches(60), USE_DRIVE_TIMER, false, 0)) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.arm.updateLiftMotor();
				robot.drivetrain.stopDrive();
				IntakeTimer.reset();
				IntakeTimer.start();
				state++;
			}
			break;
		case 8:
			robot.intake.intake(robot, Intake.USE_INTAKE_SENSOR);
			robot.intake.updateIntakeMotors();
			if (IntakeTimer.get() > .5) {
				robot.intake.stopIntake();
				robot.intake.updateIntakeMotors();
				robot.drivetrain.gyro.reset();
				IntakeTimer.reset();
				IntakeTimer.start();
				state++;
			}
			break;
		case 9:

			robot.arm.intakeUp(robot);
			if (IntakeTimer.get() > .3) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.arm.updateLiftMotor();
				robot.drivetrain.stopDrive();
				state++;

			}
			break;
		case 10:
			robot.arm.liftToScaleAuto();
			if (turnGyro(robot.drivetrain, -135, .3) && robot.arm.liftToScaleAuto()) {
				robot.arm.holdLift();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				System.out.println("Case 5 started");
				state++;
			}
			robot.intake.stopIntake();
			robot.intake.updateIntakeMotors();
			robot.arm.updateLiftMotor();
			break;
		case 11:
			if (autoDriveRobot(robot.drivetrain, .5, .5, 0, actualInches(35), USE_DRIVE_TIMER, true, 0)) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				robot.arm.updateLiftMotor();
				robot.drivetrain.stopDrive();
				IntakeTimer.reset();
				IntakeTimer.start();
				state++;
			}
			break;
		case 12:
			robot.intake.outakeSlow();
			robot.intake.updateIntakeMotors();
			if (IntakeTimer.get() > .7) {
				robot.intake.stopIntake();
				robot.intake.updateIntakeMotors();
				robot.drivetrain.gyro.reset();
				// state++;
			}
			break;
		}

	}

	// Scores switch from the side
	public void scoreSwitch(Robot robot, boolean leftSide) {
		robot.pneumatics.setLowGear();
		switch (state) {
		case 0:
			if (autoDriveRobot(robot.drivetrain, .5, .5, 0, 90, USE_DRIVE_TIMER)) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.stopDrive();
				Timer.delay(.5);
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, 77, .25)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.stopDrive();
					state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, -79, .2)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.stopDrive();
					state++;
				}
			}
			break;
		case 2:

			ScoreSwitchTimer.reset();
			ScoreSwitchTimer.start();
			state++;

			robot.arm.updateLiftMotor();
			break;
		case 3:
			if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, 8, USE_DRIVE_TIMER) || ScoreSwitchTimer.get() > 2) {
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.gyro.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 4:
			robot.intake.outakeFast();
			robot.intake.updateIntakeMotors();
			if (IntakeTimer.get() > 2) {
				robot.intake.stopIntake();
				state++;
			}
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
			if (robot.arm.liftToGroundAuto()) {
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

	// Scores switch from middle position
	public void position2Switch(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setHighGear();
		if (teamSwitch.equals("Left")) {
			switch (state) {
			case 0:
				if (autoDriveRobot(robot.drivetrain, .5, .5, 0, 35, USE_DRIVE_TIMER)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					state = 2;
				}
				break;
			case 1:
				state++;
				robot.arm.liftSpeed = 0;
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

				if (autoDriveRobot(robot.drivetrain, 0.55, 0.55, 0, 65, USE_DRIVE_TIMER)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 4:
				if (turnGyro(robot.drivetrain, 78, .4)) {
					Timer.delay(.1);
					robot.drivetrain.stopDrive();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 5:

				if (autoDriveRobot(robot.drivetrain, .6, .6, 0, 80, false) || robot.drivetrain.Accel.getX() > 1) {
					state++;
					robot.drivetrain.stopDrive();
					IntakeTimer.reset();
					IntakeTimer.start();
				}
				break;
			case 6:
				robot.intake.outakeFast();
				robot.intake.updateIntakeMotors();
				if (IntakeTimer.get() > 2) {
					robot.intake.stopIntake();
					state++;
				}
				break;

			}

		} else if (teamSwitch.equals("Right")) {
			switch (state) {
			case 0:
				if (autoDriveRobot(robot.drivetrain, .5, .5, 0, 35, USE_DRIVE_TIMER)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					state = 2;
				}
				break;
			case 1:
				state++;

			case 2:

				if (turnGyro(robot.drivetrain, 80, .4)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 3:

				if (autoDriveRobot(robot.drivetrain, 0.5, 0.5, 0, 45, USE_DRIVE_TIMER)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 4:
				if (turnGyro(robot.drivetrain, -75, .4)) {
					Timer.delay(.1);
					robot.drivetrain.stopDrive();
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					state++;
				}
				break;
			case 5:

				if (robot.drivetrain.Accel.getX() > 1 || autoDriveRobot(robot.drivetrain, .5, .5, 0, 80, false)) {
					state++;
					robot.drivetrain.stopDrive();
					IntakeTimer.reset();
					IntakeTimer.start();

				}
				break;
			case 6:

				robot.intake.outakeFast();
				if (IntakeTimer.get() > 2) {
					robot.intake.stopIntake();
					state++;
				}
				robot.intake.updateIntakeMotors();
				break;

			}
		}
	}

	public void scaleCross(Robot robot, boolean leftSide) {
		robot.pneumatics.setHighGear();
		switch (state) {
		case 0:
			if (autoDriveRobot(robot.drivetrain, .8, 8, 0, actualInches(223), USE_DRIVE_TIMER)) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 1:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, 80, .3)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.stopDrive();
					state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, -80, .3)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.stopDrive();
					state++;
				}
			}
			break;
		case 2:
			if (autoDriveRobot(robot.drivetrain, .8, .8, 0, 163, USE_DRIVE_TIMER)) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.stopDrive();
				state++;
			}
			break;
		case 3:
			if (leftSide) {
				if (turnGyro(robot.drivetrain, -80, .3)) {
					// robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.stopDrive();
					// state++;
				}
			} else {
				if (turnGyro(robot.drivetrain, 80, .3)) {
					// robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.stopDrive();
					// state++;
				}
			}
			break;
		case 4:
			if (autoDriveRobot(robot.drivetrain, .3, .3, 0, 30, USE_DRIVE_TIMER)) {
				robot.drivetrain.gyro.reset();
				robot.drivetrain.LeftEncoder.reset();
				robot.drivetrain.stopDrive();
				// state++;
			}
			break;
		case 5:
			if (robot.arm.liftToScaleAuto()) {
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
			if (robot.arm.liftToGroundAuto()) {
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

	boolean autoDriveRobot(Drivetrain drivetrain, double velocityLeft, double velocityRight, double timeSec,
			double targetDistanceInch, boolean isTimerBased) {
		return autoDriveRobot(drivetrain, velocityLeft, velocityRight, timeSec, targetDistanceInch, isTimerBased, true,
				0);
	}

	boolean autoDriveRobot(Drivetrain drivetrain, double velocityLeft, double velocityRight, double timeSec,
			double targetDistanceInch, boolean isTimerBased, boolean slowDown, double targetAngle) {
		double err = 0.0;
		double driveDistInch = 0.0;
		double percentPower = 0.0;
		if (isTimerBased) {
			if (AutoDriveTimer.get() <= timeSec) {
				// leftDriveMotor.set(-velocityLeft);
				// rightDriveMotor.set(velocityRight);
				drivetrain.keepDriveStraight(velocityLeft, velocityRight, 0);
			} else {
				drivetrain.stopDrive();
				return true;
			}
		} else {
			driveDistInch = Math.abs(convertDriveTicksToInches(drivetrain.LeftEncoder.get()));
			if (driveDistInch < Math.abs(targetDistanceInch)) {
				// leftDriveMotor.set(-velocityLeft);
				// rightDriveMotor.set(velocityRight);
				err = Math.abs(targetDistanceInch) - driveDistInch;
				percentPower = (err / Math.abs(targetDistanceInch));

				if (err <= 30.0 && slowDown) // If within 24" start slowing down
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

				drivetrain.keepDriveStraight(velocityLeft, velocityLeft, targetAngle);
			} else {
				drivetrain.stopDrive();
				return true;
			}
		}
		return false;
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
		if (rAngle < drive.gyro.getAngle()) {
			// Start accumulating error if the rate of turning is < 2 deg/sec
			if (drive.gyro.getRate() < 2.0) {
				gyroKi += 0.001;
				if (gyroKi > 0.2) // Cap the integral term
					gyroKi = 0.2;
			}

			error = Math.abs(rAngle) - drive.gyro.getAngle();
			if (drive.gyro.getAngle() <= Math.abs(rAngle) && Math.abs(error) > 2.0) {
				// turn left
				VelocityToSet = (error / 3) + 0.2 + gyroKi; // 140 0.2
				if (Math.abs(VelocityToSet) > maxTurnVelocity)
					VelocityToSet = maxTurnVelocity * (VelocityToSet < 0.0 ? -1.0 : 1.0);
				drive.leftDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // 0.8
				drive.rightDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // 0.8
			} else {
				gyroKi = 0.0;
				drive.stopDrive();
				// if(WaitAsyncUntil(0.5,true))
				return true;
			}
		} else if (rAngle > drive.gyro.getAngle()) {
			// Start accumulating error if the rate of turning is < 2 deg/sec
			if (drive.gyro.getRate() < 2.0) {
				gyroKi += 0.001;
				if (gyroKi > 0.2) // Cap the integral term
					gyroKi = 0.2;
			}

			error = -rAngle - drive.gyro.getAngle();
			if (drive.gyro.getAngle() >= -rAngle && Math.abs(error) > 2.0) {
				// turn right
				VelocityToSet = (error / 270) - 0.2 - gyroKi;
				if (Math.abs(VelocityToSet) > maxTurnVelocity)
					VelocityToSet = maxTurnVelocity * (VelocityToSet < 0.0 ? -1.0 : 1.0);
				drive.leftDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // -0.8
				drive.rightDriveMotor.set(VelocityToSet * drive.batteryCompensationPct()); // -0.8
			} else {
				gyroKi = 0.0;
				drive.stopDrive();
				// if(WaitAsyncUntil(0.5,true))
				return true;
			}
		} else {
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
				teamSwitch = "Left";
			} else {
				teamSwitch = "Right";
			}

			// Check Scale
			if (gameData.charAt(1) == 'L') {
				scale = "Left";
			} else {
				scale = "Right";
			}

			// Check opponent switch
			if (gameData.charAt(2) == 'L') {
				opponentSwitch = "Left";
			} else {
				opponentSwitch = "Right";
			}
		} else {
			teamSwitch = "Not Recieved Data Yet";
			scale = "Not Recieved Data Yet";
			opponentSwitch = "Not Recieved Data Yet";
		}
	}

	private double actualInches(double inchesWanted) {
		return (inchesWanted * .8333);
	}

	public void updateDashboard() {
		SmartDashboard.putString("Team Switch", teamSwitch);
		SmartDashboard.putString("Scale", scale);
		SmartDashboard.putString("Opponent Switch", opponentSwitch);

	}

	@SuppressWarnings("unused")
	public void cheesecakeAuto() {
		int cheeseState = 0;
		Timer cheeseTimer = new Timer();
		cheeseTimer.reset();
		double cheeseLeft = .5;
		double cheeseRight = -.5;
		switch (cheeseState) {
		case 0:
			while (cheeseTimer.get() < 3 /* && isEnabled() */) {
				// fLeftMotor.set(cheeseLeft);
				// BLeftMotor.set(cheeseLeft);
				// fRightMotor.set(cheeseRight);
				// bRightMotor.set(cheeseRight);
			}
			state++;
			break;
		case 1:
			// fLeftMotor.set(0);
			// BLeftMotor.set(0);
			// fRightMotor.set(0);
			// bRightMotor.set(0);
			break;
		}
	}
}