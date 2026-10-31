package org.firstinspires.ftc.teamcode.Mechanisms;

import static android.os.SystemClock.sleep;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Hardware Abstraction Class for the Robot.
 * This class fetches all hardware components from the HardwareMap using
 * {@link RobotHardwareNames} and configures their directions, brake modes, and motor modes.
 */
public class RobotHardware {

    // Hardware components
    public DcMotor leftFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor rightBackDrive = null;
    public DcMotorEx launcher = null;
    public DcMotor intake = null;
    public CRServo leftIntakeServo = null;
    public CRServo rightIntakeServo = null;
    public CRServo windmillServo = null;
    public Servo windmillPositionalServo = null;
    public IMU imu = null;

    public String initStatus = "OK";

    /**
     * Initialize all hardware components from the supplied HardwareMap.
     *
     * @param hwMap The HardwareMap instance from the current OpMode.
     */
    public void init(HardwareMap hwMap) {
        StringBuilder statusLog = new StringBuilder();

        // Safe hardware retrieval
        leftFrontDrive = getHardware(hwMap, DcMotor.class, RobotHardwareNames.LEFT_FRONT_DRIVE, statusLog);
        rightFrontDrive = getHardware(hwMap, DcMotor.class, RobotHardwareNames.RIGHT_FRONT_DRIVE, statusLog);
        leftBackDrive = getHardware(hwMap, DcMotor.class, RobotHardwareNames.LEFT_BACK_DRIVE, statusLog);
        rightBackDrive = getHardware(hwMap, DcMotor.class, RobotHardwareNames.RIGHT_BACK_DRIVE, statusLog);
        intake = getHardware(hwMap, DcMotor.class, RobotHardwareNames.INTAKE, statusLog);
        launcher = getHardware(hwMap, DcMotorEx.class, RobotHardwareNames.LAUNCHER, statusLog);

        leftIntakeServo = getHardware(hwMap, CRServo.class, RobotHardwareNames.LEFT_INTAKE_SERVO, statusLog);
        rightIntakeServo = getHardware(hwMap, CRServo.class, RobotHardwareNames.RIGHT_INTAKE_SERVO, statusLog);

        // Windmill Servo: Try CRServo first with "ballrotator" name
        windmillServo = getHardware(hwMap, CRServo.class, RobotHardwareNames.WINDMILL_SERVO, statusLog);
        if (windmillServo == null) {
            // Fallback 1: Check if configured as standard Servo
            windmillPositionalServo = getHardware(hwMap, Servo.class, RobotHardwareNames.WINDMILL_SERVO, statusLog);
            if (windmillPositionalServo == null) {
                // Fallback 2: Check alternative device name "windmill"
                windmillServo = getHardware(hwMap, CRServo.class, "windmill", statusLog);
                if (windmillServo == null) {
                    windmillPositionalServo = getHardware(hwMap, Servo.class, "windmill", statusLog);
                }
            }
        }

        imu = getHardware(hwMap, IMU.class, RobotHardwareNames.IMU, statusLog);

        // Motor directions
        if (leftFrontDrive != null) leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        if (rightFrontDrive != null) rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        if (leftBackDrive != null) leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        if (rightBackDrive != null) rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Brake behavior
        if (leftFrontDrive != null) leftFrontDrive.setZeroPowerBehavior(BRAKE);
        if (rightFrontDrive != null) rightFrontDrive.setZeroPowerBehavior(BRAKE);
        if (leftBackDrive != null) leftBackDrive.setZeroPowerBehavior(BRAKE);
        if (rightBackDrive != null) rightBackDrive.setZeroPowerBehavior(BRAKE);
        if (intake != null) intake.setZeroPowerBehavior(FLOAT);

        // Run using encoder
        if (leftFrontDrive != null) leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (leftBackDrive != null) leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (rightFrontDrive != null) rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (rightBackDrive != null) rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Launcher setup
        if (launcher != null) {
            launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));
        }

        // Servo initializations
        if (leftIntakeServo != null) leftIntakeServo.setPower(0);
        if (rightIntakeServo != null) {
            rightIntakeServo.setPower(0);
            rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        if (windmillServo != null) {
            windmillServo.setPower(0);
            windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        if (imu != null) {
            try {
                RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                );
                imu.initialize(new IMU.Parameters(RevOrientation));
            } catch (Exception e) {
                statusLog.append("IMU Error: ").append(e.getMessage()).append("\n");
            }
        }

        initStatus = statusLog.length() == 0 ? "OK" : statusLog.toString();
    }

    private <T> T getHardware(HardwareMap hwMap, Class<T> classOrInterface, String name, StringBuilder statusLog) {
        try {
            return hwMap.get(classOrInterface, name);
        } catch (Exception e) {
            statusLog.append("Missing: ").append(name).append(" (").append(classOrInterface.getSimpleName()).append("); ");
            return null;
        }
    }

    /**
     * Set motor powers for the drive motors.
     *
     * @param leftFront  Power for left front drive motor.
     * @param rightFront Power for right front drive motor.
     * @param leftBack   Power for left back drive motor.
     * @param rightBack  Power for right back drive motor.
     */
    public void setDrivePower(double leftFront, double rightFront, double leftBack, double rightBack) {
        if (leftFrontDrive != null) leftFrontDrive.setPower(leftFront);
        if (rightFrontDrive != null) rightFrontDrive.setPower(rightFront);
        if (leftBackDrive != null) leftBackDrive.setPower(leftBack);
        if (rightBackDrive != null) rightBackDrive.setPower(rightBack);
    }

    /**
     * Set power to the intake motor and both intake servos simultaneously.
     *
     * @param power Power level (-1.0 to 1.0).
     */
    public void setIntakePower(double power) {
        if (intake != null) intake.setPower(power);
        if (leftIntakeServo != null) leftIntakeServo.setPower(power);
        if (rightIntakeServo != null) rightIntakeServo.setPower(power);
    }
    /**
     * Set power to the intake motor.
     *
     * @param power Power level (-1.0 to 1.0).
     */
    public void setBroomPower(double power) {
        if (intake != null) intake.setPower(power);
    }
    /**
     * Set power to the left intake servo.
     *
     * @param power Power level (-1.0 to 1.0).
     */
    public void setLeftIntakeServoPower(double power) {
        if (leftIntakeServo != null) leftIntakeServo.setPower(power);
    }
    /**
     * Set power to the right intake servo.
     *
     * @param power Power level (-1.0 to 1.0).
     */
    public void setRightIntakeServoPower(double power) {
        if (rightIntakeServo != null) rightIntakeServo.setPower(power);
    }

    /**
     * Set target velocity for the launcher motor in encoder ticks per second.
     *
     * @param velocity Target velocity in encoder ticks/sec.
     */
    public void setLauncherVelocity(double velocity) {
        if (launcher != null) launcher.setVelocity(velocity);
    }

    /**
     * Get current velocity of the launcher motor.
     *
     * @return Velocity in encoder ticks per second.
     */
    public double getLauncherVelocity() {
        return launcher != null ? launcher.getVelocity() : 0;
    }

    /**
     * Set power/position for the windmill continuous rotation servo or positional servo.
     *
     * @param power Power level (-1.0 to 1.0).
     */
    public void setWindmillPower(double power) {
        if (windmillServo != null) {
            windmillServo.setPower(power);
        } else if (windmillPositionalServo != null) {
            // Convert power (-1.0 to 1.0) or (0.0 to 1.0) to servo position (0.0 to 1.0)
            double position = power <= 0 ? 0.0 : power;
            windmillPositionalServo.setPosition(position);
        }
    }

    /**
     * Gets heading of robot at current time
     * @return robot heading (degrees)
     */
    public double getHeading() {
        double angle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        while (angle > 180) {
            angle -= 360; // subtract until in target range
        }
        while (angle <= -180) {
            angle += 360; // add until in target range
        }
        return angle;
    }

    /**
     * Move in any direction for a mecanum drivetrain continuously without stopping automatically.
     *
     * @param forward move the robot forward or backward (-1.0 to 1.0)
     * @param strafe  strafe the robot left or right (-1.0 to 1.0)
     * @param rotate  rotate the robot clockwise or anti-clockwise (-1.0 to 1.0)
     */
    public void mecanumDrive(double forward, double strafe, double rotate) {
        double leftFrontPower = forward + strafe + rotate;
        double rightFrontPower = forward - strafe - rotate;
        double leftBackPower = forward - strafe + rotate;
        double rightBackPower = forward + strafe - rotate;

        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        setDrivePower(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }

    /**
     * Move in any direction for a mecanum drivetrain for a set duration.
     *
     * @param forward move the robot forward or backward (-1.0 to 1.0)
     * @param strafe  strafe the robot left or right (-1.0 to 1.0)
     * @param rotate  rotate the robot clockwise or anti-clockwise (-1.0 to 1.0)
     * @param time    time to run motors (in seconds or milliseconds)
     */
    public void mecanumDrive(double forward, double strafe, double rotate, double time) {
        mecanumDrive(forward, strafe, rotate);
        sleep((long) (time > 10 ? time : time * 1000));
        setDrivePower(0, 0, 0, 0);
    }

    /**
     * Move in any direction with a string control command (e.g., "stop" to stop motors).
     *
     * @param forward       move the robot forward or backward (-1.0 to 1.0)
     * @param strafe        strafe the robot left or right (-1.0 to 1.0)
     * @param rotate        rotate the robot clockwise or anti-clockwise (-1.0 to 1.0)
     * @param stopCondition String condition command (e.g. "stop" to stop motors)
     */
    public void mecanumDrive(double forward, double strafe, double rotate, String stopCondition) {
        if ("rotate180".equalsIgnoreCase(stopCondition)) {
            while (getHeading() < 180) {
                mecanumDrive(forward, strafe, rotate);
            }
            mecanumDrive(0.0, 0.0, 0.0);
        } else {
            mecanumDrive(forward, strafe, rotate);
        }
    }
}
