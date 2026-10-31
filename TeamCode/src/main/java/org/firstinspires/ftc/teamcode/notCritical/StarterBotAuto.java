package org.firstinspires.ftc.teamcode.notCritical;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.RobotHardware;

/**
 * This is the starter Autonomus code that came with the starter **Arcade Drive** bot.
 *
 * This code was pulled from GoBilda and optimized for Mecanum drive and to use RobotHardware.java.
 *
 * The Arcade drive version of this code can be downloaded from this link:
 * https://www.gobilda.com/content/downloads/3200-2627-0003_example-code.zip
 */

@Disabled
@Autonomous(name="StarterBotAuto (Mecanum)", group="StarterBot")
public class StarterBotAuto extends OpMode {
    RobotHardware robot = new RobotHardware();
    public final int LAUNCHER_TARGET_VELOCITY = 1250;
    public final int LAUNCHER_MIN_VELOCITY = 1200;

    final double DRIVE_SPEED = 0.5;
    final double WHEEL_DIAMETER_MM = 96;
    final double ENCODER_TICKS_PER_REV = 537.7;
    final double TICKS_PER_MM = (ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI));
    final double TRACK_WIDTH_MM = 402;

    double intakePower = 0;

    private ElapsedTime driveTimer = new ElapsedTime();
    private ElapsedTime autoTimer = new ElapsedTime();

    private enum AutonomousState {
        LAUNCH,
        DRIVE,
        STRAFE,
        COMPLETE
    }

    private AutonomousState autonomousState;

    @Override
    public void init() {
        autonomousState = AutonomousState.LAUNCH;
        robot.init(hardwareMap);
    }

    @Override
    public void start() {
        autoTimer.reset();
    }

    @Override
    public void loop() {
        switch (autonomousState) {
            case LAUNCH:
                launch(true);
                if (autoTimer.seconds() > 10) {
                    launch(false);
                    intakePower = 0;
                    driveTimer.reset();
                    autoTimer.reset();
                    autonomousState = AutonomousState.DRIVE;
                }
                break;
            case DRIVE:
                if (drive(DRIVE_SPEED, -120, DistanceUnit.MM, 1.5)) {
                    driveTimer.reset();
                    autonomousState = AutonomousState.STRAFE;
                }
                break;
            case STRAFE:
                if (strafe(DRIVE_SPEED, 60, DistanceUnit.MM, 1.5)) {
                    autonomousState = AutonomousState.COMPLETE;
                }
                break;
            case COMPLETE:
                robot.setDrivePower(0, 0, 0, 0);
                telemetry.addLine("Auto Complete!");
                break;
        }

        robot.setIntakePower(intakePower);

        // Telemetry code
        telemetry.addData("AutoState", autonomousState);
        telemetry.addData("Motor Positions", "LF (%d), RF (%d), LB (%d), RB (%d)",
                robot.leftFrontDrive.getCurrentPosition(),
                robot.rightFrontDrive.getCurrentPosition(),
                robot.leftBackDrive.getCurrentPosition(),
                robot.rightBackDrive.getCurrentPosition());
        telemetry.update();
    }

    void launch(boolean input) {
        if (input) {
            robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            robot.setLauncherVelocity(0);
        }

        if (input && robot.getLauncherVelocity() > LAUNCHER_MIN_VELOCITY) {
            robot.setWindmillPower(1);
            intakePower = 0.5;
        } else {
            robot.setWindmillPower(0);
            intakePower = 0;
        }
    }

    /**
     * Drive forward/backward using mecanum kinematics.
     */
    boolean drive(double speed, double distance, DistanceUnit distanceUnit, double holdSeconds) {
        final double TOLERANCE_MM = 10;
        double targetPosition = (distanceUnit.toMm(distance) * TICKS_PER_MM);

        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        setTargetPositions((int) targetPosition, (int) targetPosition, (int) targetPosition, (int) targetPosition);
        robot.setDrivePower(speed, speed, speed, speed);

        if (Math.abs(targetPosition - robot.leftFrontDrive.getCurrentPosition()) > (TOLERANCE_MM * TICKS_PER_MM)) {
            driveTimer.reset();
        }

        return (driveTimer.seconds() > holdSeconds);
    }

    /**
     * Strafe left/right using mecanum kinematics.
     * Positive distance = strafe right, Negative distance = strafe left.
     */
    boolean strafe(double speed, double distance, DistanceUnit distanceUnit, double holdSeconds) {
        final double TOLERANCE_MM = 10;
        double targetPosition = (distanceUnit.toMm(distance) * TICKS_PER_MM);

        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Mecanum strafe: LF (+), RF (-), LB (-), RB (+)
        setTargetPositions((int) targetPosition, (int) -targetPosition, (int) -targetPosition, (int) targetPosition);
        robot.setDrivePower(speed, speed, speed, speed);

        if (Math.abs(targetPosition - robot.leftFrontDrive.getCurrentPosition()) > (TOLERANCE_MM * TICKS_PER_MM)) {
            driveTimer.reset();
        }

        return (driveTimer.seconds() > holdSeconds);
    }

    /**
     * Rotate in place using mecanum kinematics.
     */
    boolean rotate(double speed, double angle, AngleUnit angleUnit, double holdSeconds) {
        final double TOLERANCE_MM = 10;
        double targetMm = angleUnit.toRadians(angle) * (TRACK_WIDTH_MM / 2);
        double targetPosition = targetMm * TICKS_PER_MM;

        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Rotate: Left side negative, Right side positive
        setTargetPositions((int) -targetPosition, (int) targetPosition, (int) -targetPosition, (int) targetPosition);
        robot.setDrivePower(speed, speed, speed, speed);

        if (Math.abs(targetPosition - robot.leftFrontDrive.getCurrentPosition()) > (TOLERANCE_MM * TICKS_PER_MM)) {
            driveTimer.reset();
        }

        return (driveTimer.seconds() > holdSeconds);
    }

    private void setRunMode(DcMotor.RunMode mode) {
        robot.leftFrontDrive.setMode(mode);
        robot.rightFrontDrive.setMode(mode);
        robot.leftBackDrive.setMode(mode);
        robot.rightBackDrive.setMode(mode);
    }

    private void setTargetPositions(int lf, int rf, int lb, int rb) {
        robot.leftFrontDrive.setTargetPosition(lf);
        robot.rightFrontDrive.setTargetPosition(rf);
        robot.leftBackDrive.setTargetPosition(lb);
        robot.rightBackDrive.setTargetPosition(rb);
    }
}
