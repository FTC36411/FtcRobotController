package org.firstinspires.ftc.teamcode.Generated;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RobotHardware;

/**
 * Field-Relative TeleOp for the StarterBot with controls matching {@link BioBuzzStarterbotTeleopMecanum}.
 */
@TeleOp(name = "Field Relative StarterBot Teleop", group = "StarterBot")
public class StarterbotFieldRelativeTeleop extends OpMode {
    private final RobotHardware robot = new RobotHardware();
    private IMU imu;

    public final int LAUNCHER_TARGET_VELOCITY = 1250;
    public final int LAUNCHER_MIN_VELOCITY = 1240;

    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    @Override
    public void init() {
        robot.init(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        telemetry.addData("Status", "Initialized (Field Relative)");
    }

    @Override
    public void loop() {
        telemetry.addLine("Press X to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");

        if (gamepad1.x) {
            imu.resetYaw();
        }

        // Field relative drive logic (Driver 1)
        if (gamepad1.left_bumper) {
            mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        } else {
            driveFieldRelative(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
        }

        // Intake Control (Operator / Driver 2)
        double intakePower = -gamepad2.right_stick_y;
        robot.setIntakePower(intakePower);

        // Launcher & Windmill Control
        launch();

        telemetry.addData("Launcher Velocity", robot.getLauncherVelocity());
        telemetry.update();
    }

    private void driveFieldRelative(double forward, double right, double rotate) {
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        mecanumDrive(newForward, newRight, rotate);
    }

    void mecanumDrive(double forward, double strafe, double rotate) {
        leftFrontPower = forward + strafe + rotate;
        rightFrontPower = forward - strafe - rotate;
        leftBackPower = forward + strafe + rotate;
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

        robot.setDrivePower(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }

    void launch() {
        if (gamepad2.left_bumper) {
            robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            robot.setLauncherVelocity(0);
        }

        if (robot.getLauncherVelocity() >= LAUNCHER_MIN_VELOCITY && gamepad2.left_bumper) {
            robot.setWindmillPower(1.0);
        } else {
            robot.setWindmillPower(0.0);
        }
    }
}
