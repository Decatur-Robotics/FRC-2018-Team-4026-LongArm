package org.usfirst.frc.team4026.robot;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Wraps an analog input for a Rev Robotics Analog Pressure sensor.
 *
 * http://www.revrobotics.com/wp-content/uploads/2015/11/REV-11-1107-DS-00.pdf
 */
public class RevRoboticsAirPressureSensor {
    private final AnalogInput analogInput;

    public RevRoboticsAirPressureSensor(int analogInputNumber) {
        analogInput = new AnalogInput(analogInputNumber);
    }

    public double getAirPressurePsi() {
        // taken from the datasheet
        return 250.0 * analogInput.getVoltage() / 5.0 - 25.0;
    }
}