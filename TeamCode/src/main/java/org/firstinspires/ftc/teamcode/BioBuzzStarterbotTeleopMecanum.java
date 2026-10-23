package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
public class BioBuzzStarterbotTeleopMecanum extends OpMode {
    private final RobotHardware robot = new RobotHardware();

    public final int LAUNCHER_TARGET_VELOCITY = 1250;
    public final int LAUNCHER_MIN_VELOCITY = 1240;

    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // 1. Mecanum Drive (Driver 1)
        // Negate left_stick_y because pushing forward on joystick is negative (-1.0)
        mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        // 2. Intake Control (Operator / Driver 2)
        double intakePower = -gamepad2.right_stick_y;
        robot.setIntakePower(intakePower);

        // 3. Launcher & Windmill Control
        launch();

        // Telemetry
        telemetry.addData("Launcher Velocity", robot.getLauncherVelocity());
        telemetry.addData("Motors", "LF (%.2f), RF (%.2f)", leftFrontPower, rightFrontPower);
        telemetry.update();
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
        // Use a button (e.g. left bumper or A button) to run the launcher instead of dividing by joystick y
        if (gamepad2.left_bumper) {
            robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            robot.setLauncherVelocity(0);
        }

        // Only run windmill / feeding mechanism when launcher is up to speed
        if (robot.getLauncherVelocity() >= LAUNCHER_MIN_VELOCITY && gamepad2.left_bumper) {
            robot.setWindmillPower(1.0);
        } else {
            robot.setWindmillPower(0.0);
        }
    }
}