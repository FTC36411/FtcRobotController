

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.RobotHardware;

/**
 * This is the starter TeleOp code that came with the starter bot.
 *
 * This code was pulled from GoBilda and optimized to use RobotHardware.java.
 *
 * The code can be downloaded from this link:
 * www.gobilda.com/content/downloads/3200-2627-0004_example-code.zip
 */

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
public class BioBuzzStarterbotTeleopMecanum extends OpMode {
    private final RobotHardware robot = new RobotHardware();

    public final int LAUNCHER_TARGET_VELOCITY = 1250;
    public final int LAUNCHER_MIN_VELOCITY = 1000;

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
        robot.mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        // 2. Intake Control (Operator / Driver 2)
        double intakePower = gamepad1.right_trigger - gamepad1.left_trigger;

        robot.setIntakePower(intakePower);

        // 3. Launcher & Windmill Control
        launch();

        // Telemetry
        telemetry.addData("Launcher Velocity", robot.getLauncherVelocity());
        telemetry.addData("Windmill Power", robot.windmillServo != null ? robot.windmillServo.getPower() : 0);
        telemetry.addData("Motors", "LF (%.2f), RF (%.2f)", leftFrontPower, rightFrontPower);
        telemetry.update();
    }

    void launch() {
        // Use a button (e.g. left bumper) to run the launcher
        if (gamepad1.left_bumper) {
            robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        } else {
            robot.setLauncherVelocity(0);
        }

        // Run windmill / feeding mechanism when launcher is up to speed (using Math.abs for velocity direction),
        // or allow manual override using right bumper or A button
        boolean launcherIsUpToSpeed = Math.abs(robot.getLauncherVelocity()) >= LAUNCHER_MIN_VELOCITY;
        if ((gamepad1.left_bumper && launcherIsUpToSpeed) || gamepad1.right_bumper || gamepad1.a) {
            robot.setWindmillPower(1.0);
        } else {
            robot.setWindmillPower(0.0);
        }
    }
}