/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
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
	
	public String prioritySelected;
	
	double counter = 0;

	Drivetrain drivetrain = new Drivetrain();
	Arm arm = new Arm();
	Controllers controllers = new Controllers();
	Pneumatics pneumatics = new Pneumatics();
	Autonomous auto = new Autonomous();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
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
		drivetrain.gyro.reset();
		pneumatics.closeGrabber();
		drivetrain.LeftEncoder.reset();
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
			case POSITION2AUTO:
				auto.position2Auto(this);
				updateDashboard();
				break;
			case POSITION3AUTO:
				auto.position3Auto(this);
				updateDashboard();
				break;
			}
		}
	}

	@Override

	public void teleopInit() {
		drivetrain.LeftEncoder.reset();
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
}
