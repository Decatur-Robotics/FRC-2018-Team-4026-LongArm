package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake implements Subsystem {
	static final boolean USE_INTAKE_SENSOR = true;

	boolean isInitialized = false;
	double intakeSpeed = 0;
	WPI_TalonSRX rightIntakeMotor;
	WPI_TalonSRX leftIntakeMotor;
	DigitalInput IntakeSensor;
	Relay LED;

	@Override
	public int init() {
		if (!isInitialized) {
			LED = new Relay(0);
			LED.setDirection(Relay.Direction.kForward);
			rightIntakeMotor = new WPI_TalonSRX(PortMap.RIGHTINTAKE);
			leftIntakeMotor = new WPI_TalonSRX(PortMap.LEFTINTAKE);
			rightIntakeMotor.setNeutralMode(NeutralMode.Brake);
			leftIntakeMotor.setNeutralMode(NeutralMode.Brake);
			IntakeSensor = new DigitalInput(PortMap.INTAKESENSOR);
			isInitialized = true;
			return 0;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	void arcadeIntake(double x, double y, double deadzone) {
		double right = x + y;
		double left = x - y;
		right = trim(right);
		left = trim(left);

		leftIntakeMotor.set(left);
		rightIntakeMotor.set(right);
	}

	private static double trim(double v) {
		if (v > 1) {
			v = 1;
		}
		if (v < -1) {
			v = -1;
		}
		return v;
	}

	public void stopIntake() {
		intakeSpeed = 0;
		leftIntakeMotor.set(0);
		rightIntakeMotor.set(0);

	}

	private void runIntake(Robot robot) {
		intakeSpeed = robot.controllers.getSecondaryLeft() * 0.75;

		if (robot.controllers.getSecondaryRawButton(7)) {
			intake(robot, true);
		}
		if (robot.controllers.getSecondaryRawButton(9)) {
			outakeSlower();
		}
		if (robot.controllers.getSecondaryRawButton(5)) {
			intakeSpeed = .1;
		}
		updateIntakeMotors();
		SmartDashboard.putNumber("POV", robot.controllers.manipulatorJoystick.getPOV());

	}

	public void updateIntakeMotors() {
		leftIntakeMotor.set(intakeSpeed);
		rightIntakeMotor.set(intakeSpeed);
	}

	public void outakeFast() {
		intakeSpeed = -.9;
	}

	public void outakeSlow() {
		intakeSpeed = -.3;
	}

	public void outakeSlower() {
		intakeSpeed = -.25;
	}

	public void updateLED() {
		if (!IntakeSensor.get()) {
			LED.set(Relay.Value.kOn);
		} else {
			LED.set(Relay.Value.kOff);
		}
	}

	public boolean intake(Robot robot, boolean useSensor) {
		if (useSensor) {
			if (IntakeSensor.get()) {
				intakeSpeed = .75;
				robot.gotCube.forceSetBoolean(false);
				return false;
			} else {
				intakeSpeed = 0;
				robot.gotCube.forceSetBoolean(true);
				return true;
			}
		} else {
			intakeSpeed = .75;
			return false;
		}

	}

	@Override
	public void run(Robot robot) {
		// arcadeIntake(robot.joystick.getSecondaryRightX(),
		// robot.joystick.getSecondaryRightY(), .05);
		runIntake(robot);
		updateLED();
	}

	@Override
	public int shutdown() {
		stopIntake();
		return 1;
	}

	@Override
	public void updateDashboard() {
		SmartDashboard.putBoolean("Intake sensor", IntakeSensor.get());
		SmartDashboard.putNumber("Intake Speed", intakeSpeed);

	}

}
