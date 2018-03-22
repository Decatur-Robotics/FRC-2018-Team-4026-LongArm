/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	SendableChooser<String> autoChooser = new SendableChooser<>();
	private static final String CROSSLINEAUTO = "Cross Line Auto";

	private static final String POSITION1AUTO = "Left Driver Station";
	private static final String POSITION1SCALE = "Left Driver Station SCALE";

	private static final String POSITION2AUTO = "Middle Driver Station";

	private static final String POSITION3AUTO = "Right Driver Station";
	private static final String POSITION3SCALE = "Right Driver Station SCALE";
	
	private String autoSelected;
	
	SendableChooser<String> autoPriority = new SendableChooser<>();
	
	public static final String PRIORITYSCALE = "Prioritize SCALE";
	public static final String PRIORITYSWITCH = "Prioritize SWITCH";
	static final double DRIVE_TICKSPERREV = 392;

	public String prioritySelected;
	
	double counter = 0;

	private Drivetrain drivetrain = new Drivetrain();
	private Arm arm = new Arm();
	Controllers controllers = new Controllers();
	private Pneumatics pneumatics = new Pneumatics();
	private Autonomous auto = new Autonomous();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// we can pass an object here instead of a string, so instead of deciding what we do based on a string value, 
		// the commands will execute directly from the selected object.
		// https://wpilib.screenstepslive.com/s/3120/m/7932/l/81109-choosing-an-autonomous-program-from-smartdashboard
		autoChooser.addDefault(CROSSLINEAUTO, CROSSLINEAUTO);
		autoChooser.addObject(POSITION1AUTO, POSITION1AUTO);
		autoChooser.addObject(POSITION2AUTO, POSITION2AUTO);
		autoChooser.addObject(POSITION3AUTO, POSITION3AUTO);
		SmartDashboard.putData("Auto choices", autoChooser);
		
		autoPriority.addDefault(PRIORITYSWITCH, PRIORITYSWITCH);
		autoPriority.addObject(PRIORITYSCALE, PRIORITYSCALE);
		SmartDashboard.putData("Auto Priority", autoPriority);
		
		drivetrain.init();
		arm.init();
		controllers.init();
		pneumatics.init();
		
        CameraServer.getInstance().startAutomaticCapture();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = autoChooser.getSelected();
		SmartDashboard.putString("Auto Selected", autoSelected);
		System.out.println("Auto selected: " + autoSelected);
		
		prioritySelected = autoPriority.getSelected();
		SmartDashboard.putString("Priority Selected", prioritySelected);
		System.out.println("Priority selected: " + prioritySelected);
		
		auto.getGameData();
		drivetrain.resetGyro();
		pneumatics.closeGrabber();
		drivetrain.resetLeftEncoder();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		while (isAutonomous() && isEnabled()) {

			switch (autoSelected) {
			case CROSSLINEAUTO:
				auto.crossLineAuto(this);
				updateDashboard();
				break;
			case POSITION1AUTO:
				auto.position1Auto(this);
				updateDashboard();
				break;
			case POSITION1SCALE:
				auto.position1Scale(this);
				updateDashboard();
				break;
			case POSITION2AUTO:
				auto.position2Auto(this);
				updateDashboard();
				break;
			case POSITION3AUTO:
				auto.position3Auto(this);
				updateDashboard();
				break;
			case POSITION3SCALE:
				auto.position3Scale(this);
				updateDashboard();
				break;
			}
		}
	}

	@Override

	public void teleopInit() {
		drivetrain.resetLeftEncoder();
	}
	
	

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		while (isOperatorControl() && isEnabled()) {
			drivetrain.run(this);
			pneumatics.run(this);
			arm.run(this);
			updateDashboard();
		}
		drivetrain.reset();
		shutdown();
		updateDashboard();

	}
	
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		// not needed yet
	}
	
	@Override
	public void disabledPeriodic() {
		updateDashboard();
	}
	
	public void shutdown() {
		drivetrain.shutdown();
		pneumatics.shutdown();
		arm.shutdown();
	}

	public void updateDashboard() {
		drivetrain.updateDashboard();
		arm.updateDashboard();
		pneumatics.updateDashboard();
	}

	/**
	 * 
	 * @param AutoDriveTimer
	 * @param velocityLeft
	 * @param velocityRight
	 * @param timeSec
	 * @param targetDistanceInch
	 * @param isTimerBased
	 * @return
	 */
	protected boolean autoDriveRobot(
			Timer AutoDriveTimer,
			double velocityLeft, 
			double velocityRight, 
			double timeSec,
			double targetDistanceInch) 
	{
		return autoDriveRobot(AutoDriveTimer, velocityLeft, velocityRight, timeSec, targetDistanceInch, false);
	}
	/**
	 * 
	 * @param AutoDriveTimer
	 * @param velocityLeft
	 * @param velocityRight
	 * @param timeSec
	 * @param targetDistanceInch
	 * @param isTimerBased
	 * @return
	 */
	protected boolean autoDriveRobot(
			Timer AutoDriveTimer,
			double velocityLeft, 
			double velocityRight, 
			double timeSec,
			double targetDistanceInch, 
			boolean isTimerBased) 
	{
		double err = 0.0;
		double driveDistInch = 0.0;
		double percentPower = 0.0;
		if (isTimerBased) 
		{
			if (AutoDriveTimer.get() <= timeSec) 
			{
				// leftDriveMotor.set(-velocityLeft);
				// rightDriveMotor.set(velocityRight);
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
			driveDistInch = Math.abs(convertDriveTicksToInches(drivetrain.getLeftEncoderCurrentCount()));
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
	
	double convertDriveTicksToInches(int encTicks) 
	{
		return (encTicks / DRIVE_TICKSPERREV) * 3.14 * 6.0;
	}

	protected void resetGyroAndLeftEncoder() {
		drivetrain.resetGyro();
		drivetrain.resetLeftEncoder();
	}
	
	protected void resetDrivetrainLeftEncoder() {
		drivetrain.resetLeftEncoder();
	}

	protected void resetDrivetrainRightEncoder() {
		drivetrain.resetRightEncoder();
	}

	protected void stopDrivetrain()
	{
		drivetrain.stopDrive();
	}

	void resetDriveTrain(Drivetrain drivetrain, boolean isTimerBased) 
	{
		drivetrain.resetLeftEncoder();
		drivetrain.resetRightEncoder();
		drivetrain.resetGyro();
	}

	protected double getDrivetrainAccelerometerXaxis()
	{
		return drivetrain.accelerometer.getX();
	}

	protected void openGrabber()
	{
		pneumatics.openGrabber();
	}
	
	protected void closeGrabber()
	{
		pneumatics.closeGrabber();
	}

	protected void intakeDown()
	{
		pneumatics.intakeDown();
	}
	
	protected void intakeUp()
	{
		pneumatics.intakeUp();
	}

	protected void setLowGear()
	{
		pneumatics.setLowGear();
	}
	
	protected void setHighGear()
	{
		pneumatics.setHighGear();
	}

	protected void holdLift()
	{
		arm.holdLift();
	}
	
	protected boolean liftToScale()
	{
		return arm.liftToScale();
	}
	
	protected boolean liftToSwitch()
	{
		return arm.liftToSwitch();
	}

	protected boolean liftToGround()
	{
		return arm.liftToScale();
	}

	protected void updateLiftMotor()
	{
		arm.updateLiftMotor();
	}
	
	protected void setIntakeLiftPistonSolenoidValue(Value value)
	{
		pneumatics.intakeLiftPistons.set(value);
	}
	
	protected void actuateGrabber(int inButton, int outButton)
	{
		pneumatics.actuateGrabber(inButton, outButton, controllers);
	}
	
	/*
	 * Used during autonomous to turn the robot to a specified angle.
	 */
	boolean turnGyro(double rAngle, double maxTurnVelocity) 
	{

		double gyroKi = 0;
		double error = 0.0;
		double VelocityToSet = 0.0;
		// Positive gyro angle means turning left
		if (rAngle < drivetrain.getGyroAngle()) 
		{
			// Start accumulating error if the rate of turning is < 2 deg/sec
			if (drivetrain.getGyroRate() < 2.0) 
			{
				gyroKi += 0.001;
				if (gyroKi > 0.2) // Cap the integral term
					gyroKi = 0.2;
			}

			error = Math.abs(rAngle) - drivetrain.getGyroAngle();
			if (drivetrain.getGyroAngle() <= Math.abs(rAngle) && Math.abs(error) > 2.0) 
			{
				// turn left
				VelocityToSet = (error / 3) + 0.2 + gyroKi; // 140 0.2
				if (Math.abs(VelocityToSet) > maxTurnVelocity)
					VelocityToSet = maxTurnVelocity * (VelocityToSet < 0.0 ? -1.0 : 1.0);
				drivetrain.leftDriveMotor.set(VelocityToSet * drivetrain.batteryCompensationPct()); // 0.8
				drivetrain.rightDriveMotor.set(VelocityToSet * drivetrain.batteryCompensationPct()); // 0.8
			} else 
			{
				gyroKi = 0.0;
				drivetrain.stopDrive();
				// if(WaitAsyncUntil(0.5,true))
				return true;
			}
		} 
		else if (rAngle > drivetrain.getGyroAngle()) 
		{
			// Start accumulating error if the rate of turning is < 2 deg/sec
			if (drivetrain.getGyroRate() < 2.0) 
			{
				gyroKi += 0.001;
				if (gyroKi > 0.2) // Cap the integral term
					gyroKi = 0.2;
			}

			error = -rAngle - drivetrain.getGyroAngle();
			if (drivetrain.getGyroAngle() >= -rAngle && Math.abs(error) > 2.0) 
			{
				// turn right
				VelocityToSet = (error / 270) - 0.2 - gyroKi;
				if (Math.abs(VelocityToSet) > maxTurnVelocity)
					VelocityToSet = maxTurnVelocity * (VelocityToSet < 0.0 ? -1.0 : 1.0);
				drivetrain.leftDriveMotor.set(VelocityToSet * drivetrain.batteryCompensationPct()); // -0.8
				drivetrain.rightDriveMotor.set(VelocityToSet * drivetrain.batteryCompensationPct()); // -0.8
			} 
			else 
			{
				gyroKi = 0.0;
				drivetrain.stopDrive();
				// if(WaitAsyncUntil(0.5,true))
				return true;
			}
		} else 
		{
			gyroKi = 0.0;
			drivetrain.stopDrive();
			return true;
		}

		return false;
	}
}
