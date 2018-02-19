package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	static final boolean USE_DRIVE_TIMER = false;
	static final double DRIVE_TICKSPERREV = 392;
	int state = 0;
	double gyroKi = 0;
	Timer autoDriveTimer = new Timer();

	String gameData;
	String teamSwitch;
	String scale;
	String opponentSwitch;

	public void crossLineAuto(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (!autoDriveRobot(robot.drivetrain, 0.3, 0.3, 0, 70, USE_DRIVE_TIMER)) {

		} else {
			robot.drivetrain.stopDrive();
		}

	}

	public void position1Scale(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (scale.equals("Left")) {

		} else {

		}
	}

	public void position1Switch(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (teamSwitch.equals("Left")) {

		} else {

		}
	}

	public void position2Switch(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (teamSwitch.equals("Left")) {

			switch (state) {
			case 0:
				state++;
				break;
			case 1:

				if (turnGyro(robot.drivetrain, -35, .5)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 2:
				if (autoDriveRobot(robot.drivetrain, 0.3, 0.3, 0, 130 - 30, USE_DRIVE_TIMER)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 3:
				if (turnGyro(robot.drivetrain, 18, .5)) {
					state++;
				}
				break;
			case 4:
				robot.drivetrain.setDriveMotors(.8, .8);
				if (Math.abs(robot.drivetrain.Accel.getX()) > 1) {
					state++;
					robot.drivetrain.stopDrive();
				}
				break;
			case 5:
				robot.pneumatics.openGrabber();
				state++;

			}
		} else {
			switch (state) {
			case 0:
				if (autoDriveRobot(robot.drivetrain, .3, .3, 0, 15, false)) {
					robot.drivetrain.gyro.reset();
					robot.drivetrain.LeftEncoder.reset();
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

				if (turnGyro(robot.drivetrain, 60, .3)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 3:
				if (autoDriveRobot(robot.drivetrain, 0.4, 0.4, 0, 45, USE_DRIVE_TIMER)) {
					robot.drivetrain.LeftEncoder.reset();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 4:
				if (turnGyro(robot.drivetrain, -45, .2)) {
					Timer.delay(.1);
					robot.drivetrain.stopDrive();
					robot.drivetrain.gyro.reset();
					state++;
				}
				break;
			case 5:
				robot.drivetrain.keepDriveStraight(.5, .5, 0);
				if (robot.drivetrain.Accel.getX() > 1) {
					state++;
					robot.drivetrain.stopDrive();
				}
				break;
			case 6:
				robot.pneumatics.openGrabber();
				state++;

			}
		}
	}

	public void position3Scale(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (scale.equals("Left")) {

		} else {

		}
	}

	public void position3Switch(Robot robot) {
		decodeGameData();
		updateDashboard();
		robot.pneumatics.setLowGear();
		if (teamSwitch.equals("Left")) {

		} else {

		}
	}

	void resetDrive(Drivetrain drivetrain, boolean isTimerBased) {
		if (isTimerBased) {
			autoDriveTimer.reset();
			autoDriveTimer.start();
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
		double err = 0.0;
		double driveDistInch = 0.0;
		double percentPower = 0.0;
		if (isTimerBased) {
			if (autoDriveTimer.get() <= timeSec) {
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
				VelocityToSet = (error / 300) + 0.2 + gyroKi; // 140 0.2
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

	public void updateDashboard() {
		SmartDashboard.putString("Team Switch", teamSwitch);
		SmartDashboard.putString("Scale", scale);
		SmartDashboard.putString("Opponent Switch", opponentSwitch);

	}
}
