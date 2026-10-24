package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Modified StarterBot TeleOp with different controls and features.
 * Created based on {@link BioBuzzStarterbotTeleopMecanum}
 *
 * <h2>Controls Mapping Summary:</h2>
 * <ul>
 *   <li><b>Gamepad 1 (Drivetrain & Behavior):</b>
 *     <ul>
 *       <li>Left Stick Y/X & Right Stick X: Mecanum Drive (Movement, Strafe, Rotation)</li>
 *       <li>X Button: Set Drive Zero Power Behavior to BRAKE</li>
 *       <li>Y / Triangle Button: Set Drive Zero Power Behavior to FLOAT</li>
 *     </ul>
 *   </li>
 *   <li><b>Gamepad 2 (Intake, Launcher & Feeder):</b>
 *     <ul>
 *       <li>X Button: Toggle Intake Direction (Forward / Reverse)</li>
 *       <li>Y / Triangle Button: Run Main Intake Motor (Broom)</li>
 *       <li>Left / Right Bumpers: Run Left / Right Intake Servos</li>
 *       <li>D-pad Up/Right/Down/Left: Launcher Velocity Presets (High: 1500, Med: 1250, Low: 1000, Off: 0)</li>
 *       <li>Left Trigger / Left Bumper: Activate Launcher Flywheel (modulated by Left Stick Y)</li>
 *       <li>Right Trigger / Right Bumper: Feed Game Piece via Windmill Servo (when launcher is spun up)</li>
 *     </ul>
 *   </li>
 * </ul>
 * <p>See CONTROLS_MAPPING.md for full details.</p>
 */
@TeleOp(name = "Modified Teleop", group = "StarterBot")
public class StarterbotTeleopWithMods extends OpMode {
    private final RobotHardware robot = new RobotHardware();

    // Flywheel velocity presets
    public final int VELOCITY_HIGH = 1500;
    public final int VELOCITY_MEDIUM = 1250;
    public final int VELOCITY_LOW = 1000;
    public final int VELOCITY_OFF = 0;

    public int currentLauncherTargetVelocity = VELOCITY_MEDIUM;
    public final int LAUNCHER_MIN_THRESHOLD = 50; // Tolerance for min velocity

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
        mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        // 2. Intake Control (Operator / Driver 2)
        double intakePower = 0.0;
        if (gamepad2.x) {
            intakePower = 1.0;  // Intake Forward
        } else if (gamepad2.y || gamepad2.triangle) {
            intakePower = -1.0; // Intake Backwards
        }
        robot.setBroomPower(intakePower);
        robot.setLeftIntakeServoPower(intakePower);
        robot.setRightIntakeServoPower(intakePower);

        // 3. Zero Power Behavior Controls (Driver 1)
        if (gamepad1.x) {
            setDriveZeroPowerBehavior(BRAKE);
        }
        if (gamepad1.y || gamepad1.triangle) {
            setDriveZeroPowerBehavior(FLOAT);
        }

        // 4. Launcher Dpad Presets & Control
        handleLauncher();

        // Telemetry
        telemetry.addData("Launcher Target Velocity", currentLauncherTargetVelocity);
        telemetry.addData("Launcher Current Velocity", robot.getLauncherVelocity());
        telemetry.addData("Motors", "LF (%.2f), RF (%.2f)", leftFrontPower, rightFrontPower);
        telemetry.update();
    }

    void mecanumDrive(double forward, double strafe, double rotate) {
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

        robot.setDrivePower(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }

    void setDriveZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        robot.leftFrontDrive.setZeroPowerBehavior(behavior);
        robot.rightFrontDrive.setZeroPowerBehavior(behavior);
        robot.leftBackDrive.setZeroPowerBehavior(behavior);
        robot.rightBackDrive.setZeroPowerBehavior(behavior);
    }

    double normalisedYValues(double gamepadInput) {
        double invertedInput = -gamepadInput;
        return (invertedInput + 1.0) / 2.0;
    }

    void handleLauncher() {
        // Dpad presets for launcher velocity
        if (gamepad2.dpad_up) {
            currentLauncherTargetVelocity = VELOCITY_HIGH;
        } else if (gamepad2.dpad_right) {
            currentLauncherTargetVelocity = VELOCITY_MEDIUM;
        } else if (gamepad2.dpad_down) {
            currentLauncherTargetVelocity = VELOCITY_LOW;
        } else if (gamepad2.dpad_left) {
            currentLauncherTargetVelocity = VELOCITY_OFF;
        }

        // Left joystick changes flywheel speed / throttle modifier
        double velocityModifier = normalisedYValues(gamepad2.left_stick_y);

        // Left trigger or left bumper activates launcher
        boolean launcherActive = (gamepad2.left_trigger > 0.3) || gamepad2.left_bumper;

        double targetVelocity = launcherActive ? (currentLauncherTargetVelocity * velocityModifier) : 0;
        
        robot.setLauncherVelocity(targetVelocity);

        // Windmill servo control: right joystick changes windmill servo speed, enabled by right trigger or right bumper
        boolean rightControlActive = (gamepad2.right_trigger > 0.3) || gamepad2.right_bumper;
        double currentVelocity = robot.getLauncherVelocity();

        if (launcherActive && currentVelocity >= (currentLauncherTargetVelocity - LAUNCHER_MIN_THRESHOLD) && currentLauncherTargetVelocity > 0 && rightControlActive) {
            double windmillSpeed = -gamepad2.right_stick_y;
            robot.setWindmillPower(windmillSpeed);
        } else {
            robot.setWindmillPower(0.0);
        }
    }
}
