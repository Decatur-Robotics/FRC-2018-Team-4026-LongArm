/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4026.robot;

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

	private static final String POSITION1SCALE = "Left Position Scale";
	private static final String POSITION1SWITCH = "Left Position Switch";

	private static final String POSITION2SWITCH = "Middle Position Switch";

	private static final String POSITION3SCALE = "Right Position Scale";
	private static final String POSITION3SWITCH = "Right Position Switch";

	private String autoSelected;

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
		autoChooser.addObject(POSITION1SCALE, POSITION1SCALE);
		autoChooser.addObject(POSITION1SWITCH, POSITION1SWITCH);
		autoChooser.addObject(POSITION2SWITCH, POSITION2SWITCH);
		autoChooser.addObject(POSITION3SCALE, POSITION3SCALE);
		autoChooser.addObject(POSITION3SWITCH, POSITION3SWITCH);
		SmartDashboard.putData("Auto choices", autoChooser);
		drivetrain.init();
		arm.init();
		controllers.init();
		pneumatics.init();

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
		auto.getGameData();
		drivetrain.gyro.reset();
		pneumatics.closeGrabber();
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
			case POSITION1SCALE:
				auto.position1Scale(this);
				updateDashboard();
				break;
			case POSITION1SWITCH:
				auto.position1Switch(this);
				updateDashboard();
				break;
			case POSITION2SWITCH:
				auto.position2Switch(this);
				updateDashboard();
				break;
			case POSITION3SCALE:
				auto.position3Scale(this);
				updateDashboard();
				break;
			case POSITION3SWITCH:
				auto.position3Switch(this);
				updateDashboard();
				break;
			}
		}
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
		arm.armGyro.reset();
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
	
	public void autoChoices() {
		
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
