/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.cameraserver.CameraServer;

import java.util.*;
/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
public class Robot extends TimedRobot {
  private static final int kFrontLeftChannel = 1;
  private static final int kRearLeftChannel = 9;
  private static final int kFrontRightChannel = 0;
  private static final int kRearRightChannel = 8;
  private static final int kWindow = 5;
  private static final int kJoystickChannel = 0;
  private static final int kButtonstickChannel = 1;
  private static final int kBall = 4;
  private Timer exec_late = new Timer();
  private MecanumDrive m_robotDrive;
  private Joystick stick= new Joystick(kJoystickChannel);
  private Joystick btick= new Joystick(kButtonstickChannel);
  private Talon window;
  private Talon ball;
  private double x_pow;
  private double y_pow;
  private boolean manualControl = false;
  private boolean manualDrive = true;
  private double powerlevel = 0;
  private CameraServer server;
  //private GamepadBase m_pad;

  @Override
  public void robotInit() {
    Spark frontLeft = new Spark(kFrontLeftChannel);
    Spark rearLeft = new Spark(kRearLeftChannel);
    Spark frontRight = new Spark(kFrontRightChannel);
    Spark rearRight = new Spark(kRearRightChannel);
    ball = new Talon(kBall);
    window = new Talon(kWindow);
    server = CameraServer.getInstance();
    server.startAutomaticCapture();
    // Invert the left side motors.
    // You may need to change or remove this to match your robot.
    //frontLeft.setInverted(true);
    //rearLeft.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
  }

  @Override
  public void teleopPeriodic() {
    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.
    manualDrive = !btick.getRawButton(5);
    if (manualDrive) {
      x_pow = stick.getRawAxis(0)*-1;
      y_pow = stick.getRawAxis(1);
    } else {
      x_pow= 0.25;
      y_pow= 0;
    }
    m_robotDrive.driveCartesian(x_pow, y_pow,
        stick.getRawAxis(4), 0.0);
    if (btick.getRawButtonReleased(8) && powerlevel == 0) {
      powerlevel = 1;
      manualControl = true;
      exec_late.schedule(new TimerTask(){
      
        @Override
        public void run() { 
          powerlevel = 0;
          manualControl = false;
        }
      }, 635);
    }
    if (btick.getRawButton(2)) {
      ball.set(-0.75);
    }
    else if (btick.getRawButton(1)) {
      ball.set(1);
    }
    else {
      ball.set(0);
    }
    if (manualControl) {
      window.set(powerlevel);
    }
    else {
      var a = 0;
      if (btick.getRawButton(3)) {
        a = 1;
      }
      else if (btick.getRawButton(4)) {
        a = -1;
      }
      window.set(a);
    }
  }
  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();
  }
}
