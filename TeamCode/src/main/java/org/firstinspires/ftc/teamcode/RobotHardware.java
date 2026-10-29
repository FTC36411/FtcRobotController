package org.firstinspires.ftc.teamcode;

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

    /**
     * Initialize all hardware components from the supplied HardwareMap.
     *
     * @param hwMap The HardwareMap instance from the current OpMode.
     */
    public void init(HardwareMap hwMap) {
        // Fetch devices using centralized hardware names
        leftFrontDrive = hwMap.get(DcMotor.class, RobotHardwareNames.LEFT_FRONT_DRIVE);
        rightFrontDrive = hwMap.get(DcMotor.class, RobotHardwareNames.RIGHT_FRONT_DRIVE);
        leftBackDrive = hwMap.get(DcMotor.class, RobotHardwareNames.LEFT_BACK_DRIVE);
        rightBackDrive = hwMap.get(DcMotor.class, RobotHardwareNames.RIGHT_BACK_DRIVE);
        intake = hwMap.get(DcMotor.class, RobotHardwareNames.INTAKE);
        launcher = hwMap.get(DcMotorEx.class, RobotHardwareNames.LAUNCHER);
        windmillServo = hwMap.get(CRServo.class, RobotHardwareNames.WINDMILL_SERVO);
        leftIntakeServo = hwMap.get(CRServo.class, RobotHardwareNames.LEFT_INTAKE_SERVO);
        rightIntakeServo = hwMap.get(CRServo.class, RobotHardwareNames.RIGHT_INTAKE_SERVO);

        // Motor directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Brake behavior
        leftFrontDrive.setZeroPowerBehavior(BRAKE);
        rightFrontDrive.setZeroPowerBehavior(BRAKE);
        leftBackDrive.setZeroPowerBehavior(BRAKE);
        rightBackDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(FLOAT);

        // Run using encoder
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Launcher setup
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));

        // Servo initializations
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);

        rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );

        imu.initialize(new IMU.Parameters(RevOrientation));
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
    public void setBroomPower(double power) {
        intake.setPower(power);
    }
    public void setLeftIntakeServoPower(double power) {
        leftIntakeServo.setPower(power);
    }
    public void setRightIntakeServoPower(double power) {
        rightIntakeServo.setPower(power);
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
     * Set power for the windmill continuous rotation servo.
     *
     * @param power Power level (-1.0 to 1.0).
     */
    public void setWindmillPower(double power) {
        if (windmillServo != null) windmillServo.setPower(power);
    }

    /**
     * Gets heading of robot at current time
     * @return robot heading (degrees)
     */
    public double getHeading(
            //AngleUnit angleUnit
    ) {
        return imu.getRobotYawPitchRollAngles().getYaw(
                //angleUnit
                AngleUnit.DEGREES
        );
    }

    /**
     * Move in any direction for a mecanum drivetrain.
     *
     * @param forward move the robot forward or backward (-1.0 to 1.0)
     * @param strafe strafe the robot left or right (-1.0 to 1.0)
     * @param rotate rotate the robot clockwise or anti-clockwise (-1.0 to 1.0)
     * @param time time to run motors (0.0 to -.-) #TODO change to distance rather than time
     */

    void mecanumDrive(double forward, double strafe, double rotate, double time) {
        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;

        leftFrontPower = forward + strafe + rotate;
        rightFrontPower = forward - strafe - rotate;
        leftBackPower = forward - strafe + rotate;
        rightBackPower = forward + strafe - rotate;

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
        sleep((long) (time > 10 ? time : time * 1000));
        setDrivePower(0, 0, 0, 0);
    }
}
