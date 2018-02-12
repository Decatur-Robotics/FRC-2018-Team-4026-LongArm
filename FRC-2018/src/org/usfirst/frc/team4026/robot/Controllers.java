package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Controllers implements Subsystem {

	Joystick driveJoystick;
	Joystick manipulatorJoystick;
	boolean isInitialized = false;

	@Override
	public int init() {
		if (!isInitialized) {
			driveJoystick = new Joystick(PortMap.PRIMARYCONTROLLER);
			manipulatorJoystick = new Joystick(PortMap.SECONDARYCONTROLLER);
			isInitialized = true;
		}
		// Return 1 if tries to reinit
		return 1;
	}

	public double getPrimaryLeft() {
		return driveJoystick.getY();
	}

	public double getPrimaryRight() {
		return driveJoystick.getThrottle();
	}

	public boolean getPrimaryRawButton(int button) {
		return driveJoystick.getRawButton(button);
	}

	public double getSecondaryLeft() {
		return manipulatorJoystick.getY();
	}

	public double getSecondaryRightY() {
		return manipulatorJoystick.getThrottle();
	}

	public double getSecondaryRightX() {
		return manipulatorJoystick.getZ();

	}

	public boolean getSecondaryRawButton(int button) {
		return manipulatorJoystick.getRawButton(button);
	}

	@Override
	public int shutdown() {

		return 1;
	}

}
