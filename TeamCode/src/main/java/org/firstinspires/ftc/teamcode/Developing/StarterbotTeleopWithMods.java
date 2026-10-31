package org.firstinspires.ftc.teamcode.Developing;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.BioBuzzStarterbotTeleopMecanum;
import org.firstinspires.ftc.teamcode.Mechanisms.RobotHardware;

/**
 * Modified StarterBot TeleOp with different controls and features.
 * Created based on {@link BioBuzzStarterbotTeleopMecanum}
 * See CONTROLS_MAPPING.md for full details.
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

    // Turn state variables for non-blocking 180-degree turns
    private boolean isTurning = false;
    private double turnTargetHeading = 0.0;

    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // 1. Mecanum Drive & 180 Degree Turns (Driver 1)
        if (isTurning) {
            // Handle active turn routine (non-blocking)
            updateTurn();
        } else {
            // Normal Mecanum Drive
            mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            // Check for 180 degree turn triggers
            if (gamepad1.left_bumper) {
                startTurn(180.0);  // Clockwise 180
            } else if (gamepad1.right_bumper) {
                startTurn(-180.0); // Anticlockwise 180
            }
        }

        // 2. Intake Control (Operator / Driver 2)
        double intakePower = 0.0;
        if (gamepad2.left_trigger > 0.3) {
            intakePower = 1.0;  // Intake Forward via Left Trigger
        } else if ((gamepad2.right_trigger > 0.3) || gamepad2.triangle || gamepad2.y) {
            intakePower = -1.0; // Intake Backwards via Right Trigger / Triangle
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

    void startTurn(double relativeAngleDegrees) {
        // Placeholder for initiating a turn. If your RobotHardware exposes IMU headings,
        // you can calculate target heading here. For a simple time-based or power-based turn:
        isTurning = true;
        // Example: set a constant rotation power until complete
    }

    void updateTurn() {
        // Non-blocking turn update logic. Set rotation power and check if target is reached.
        // Once complete, set isTurning = false;
        // robot.setDrivePower(0, 0, 0, 0);
        isTurning = false; // Reset placeholder
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

        // Launcher activation via bumpers (L1 or R1 on Gamepad 2)
        boolean launcherActive = gamepad2.left_bumper || gamepad2.right_bumper;

        double targetVelocity = launcherActive ? (currentLauncherTargetVelocity * velocityModifier) : 0;
        
        robot.setLauncherVelocity(targetVelocity);

        // Windmill servo control: right joystick changes windmill servo speed, enabled by right trigger or right bumper
        boolean rightControlActive = (gamepad2.right_trigger > 0.3) || gamepad2.right_bumper;
        double currentVelocity = robot.getLauncherVelocity();

        if (true) {
            double windmillSpeed = -gamepad2.right_stick_y;
            robot.setWindmillPower(windmillSpeed);
        } else {
            robot.setWindmillPower(0.0);
        }
    }
}
