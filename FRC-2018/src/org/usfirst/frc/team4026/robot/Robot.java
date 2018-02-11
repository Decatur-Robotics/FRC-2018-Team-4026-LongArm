/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.DriverStation;
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
	
	private static final String POSITION1SCALE = "DriverStation 1 Scale";
	private static final String POSITION1SWITCH = "DriverStation 1 Switch";
	
	private static final String POSITION2SWITCH = "DriverStation 2 Switch";
	
	private static final String POSITION3SCALE = "DriverStation 3 Scale";
	private static final String POSITION3SWITCH = "DriverStation 3 Switch";
	
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
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = autoChooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
		auto.getGameData();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() 
	{
		while(isAutonomous() && isEnabled())
		{
			switch (autoSelected) {
			case CROSSLINEAUTO:
				auto.crossLineAuto(drivetrain);
				break;
			case POSITION1SCALE:
				auto.position1Scale(drivetrain);
				break;
			case POSITION1SWITCH:
				auto.position1Switch(drivetrain);
				break;
			case POSITION2SWITCH:
				auto.position2Switch(drivetrain);
				break;
			case POSITION3SCALE:
				auto.position3Scale(drivetrain);
				break;
			case POSITION3SWITCH:
				auto.position3Switch(drivetrain);
				break;
			}
		}
	}


	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() { 
		while(isOperatorControl() && isEnabled()) {
			drivetrain.tankDrive(controllers);
			pneumatics.shift(1,3,controllers);
			arm.lift(controllers, pneumatics);
			arm.intake(controllers, pneumatics, 1);
			updateDashboard();
		}
		drivetrain.shutdown();
		pneumatics.shutdown();
		arm.shutdown();
		updateDashboard();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		//not needed yet
	}
	
	public void updateDashboard() {
		SmartDashboard.putNumber("Lift Speed", arm.liftSpeed);
		SmartDashboard.putNumber("Motor Output Voltage", arm.armLiftMotor.getMotorOutputVoltage());
		SmartDashboard.putNumber("Output Current", arm.armLiftMotor.getOutputCurrent());
		SmartDashboard.putString("Brake Mode", arm.brakeMode.toString());
		SmartDashboard.putString("Gear State", pneumatics.gearState());
	}
}
