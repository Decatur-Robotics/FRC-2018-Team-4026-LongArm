package org.usfirst.frc.team4026.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain implements Subsystem{
	static final double MAX_BATTERY = 12.3;
	static final double TIPPING_POINT_DEGS = 20;
	boolean isGyroresetTelop = false;
	
	boolean isInitialized = false;
	//Controllers
	Talon rightDriveMotor;
	Talon leftDriveMotor;
	//Why are these here?
	WPI_TalonSRX leftIntakeMotor;
	WPI_TalonSRX rightIntakeMotor;
	//
	//Sensors
	AnalogGyro gyro;
	AHRS navx;
	Encoder RightEncoder;
	Encoder LeftEncoder;
	Tipper tippingAlgorithm;
	//Power vars
	double right = 0;
	double left = 0;
	
	
	
	public int init(){
		if(!isInitialized){
			try{
			 navx = new AHRS(SerialPort.Port.kUSB1);
	            //navx = new AHRS(SerialPort.Port.kMXP, SerialDataType.kProcessedData, (byte)50);
	            navx.enableLogging(true);
	        } catch (RuntimeException ex ) {
	            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	        }
		RightEncoder = new Encoder (PortMap.RIGHT_ENCODER_1,PortMap.RIGHT_ENCODER_2,false);
		LeftEncoder = new Encoder (PortMap.LEFT_ENCODER_1, PortMap.LEFT_ENCODER_2, true);
		//RightEncoder.setDistancePerPulse();
		//LeftEncoder.setDistancePerPulse();
		tippingAlgorithm = new Tipper(navx,this);
		
		leftDriveMotor = new Talon(PortMap.LEFTDRIVE);
		rightDriveMotor = new Talon(PortMap.RIGHTDRIVE);
		
		/*leftIntakeMotor = new WPI_TalonSRX (PortMap.LEFTINTAKE);
		rightIntakeMotor = new WPI_TalonSRX (PortMap.RIGHTINTAKE);*/
		gyro = new AnalogGyro(PortMap.GYRO);
		gyro.calibrate();
		
		while (navx.isCalibrating()){
			//wait
		}
		
		isInitialized = true;
		return 0;
		}
		//Return 1 if tries to reinit
		return 1;
	}
	
	
	
	void tankDrive(Controllers driveGamepad)
	{
		left  = -driveGamepad.getPrimaryLeft();
		right = -driveGamepad.getPrimaryRight();
		
		//
		
		//Cut speed in half
		if(driveGamepad.getPrimaryRawButton(7))
		{
			right /= 2.0;
			left /= 2.0;
		}
		double avgStick = (right + left) / 2.0;
		
		//If the robot is tipping, tipping control will set motor values and return true.
		if (tippingAlgorithm.tippingControl())
		{
			if(!driveGamepad.getPrimaryRawButton(8) && !shouldIHelpDriverDriveStraight())
			{
			setDriveMotors(left, right);
			isGyroresetTelop = false;
			}
			else 
			{
				if(isGyroresetTelop == false)
				{
					gyro.reset();
					isGyroresetTelop = true;
				}
				keepDriveStraight(driveGamepad, avgStick, avgStick, 0);
			}
		}
		else
		{
			//DONT FREAKING TIP
		}
	}
	
	
	
	
	
	void setDriveMotors(double leftPower2, double rightPower2)
	{
			leftDriveMotor.set(-leftPower2);
			rightDriveMotor.set(rightPower2);
	}
	public boolean shouldIHelpDriverDriveStraight() {
		return false;
	}
	double batteryCompensationPct()
	{
		return MAX_BATTERY / RobotController.getBatteryVoltage();
	}

	public void keepDriveStraight(Controllers driveGamepad, double leftDriveVel, double rightDriveVel, double targetAngle) 
	{	
		double error = 0, correctionFactor;
		error = targetAngle - gyro.getAngle();
		correctionFactor = (error/75.0);

		if(leftDriveVel > 0.9)
			leftDriveVel = 0.9;
		else if(leftDriveVel < -0.9)
			leftDriveVel = -0.9;

		if(rightDriveVel > 0.9)
			rightDriveVel = 0.9;
		else if(rightDriveVel < -0.9)
			rightDriveVel = -0.9;

		if(targetAngle > (gyro.getAngle() - 0.5) || targetAngle < (gyro.getAngle() + 0.5))
		{
			leftDriveMotor.set(((-leftDriveVel) + correctionFactor) * batteryCompensationPct());
			rightDriveMotor.set((rightDriveVel + correctionFactor) * batteryCompensationPct());
		}
		else
		{
			leftDriveMotor.set(-leftDriveVel * batteryCompensationPct());
			rightDriveMotor.set(rightDriveVel * batteryCompensationPct());
		}
	}
	private void stopDrive(){
		leftDriveMotor.set(0);
		rightDriveMotor.set(0);
	}
	

	@Override
	public int shutdown() {
		stopDrive();
		return 1;
	}
	
	void updateDashboard(){
		SmartDashboard.putNumber("Right Encoder Ticks", RightEncoder.get());
		SmartDashboard.putNumber("Left Encoder Ticks", LeftEncoder.get());
	}
	
	
	
}
