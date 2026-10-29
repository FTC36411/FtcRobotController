package org.firstinspires.ftc.teamcode.Generated;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RobotHardware;

/**
 * Field-Relative TeleOp with controls matching {@link StarterbotTeleopWithMods}.
 */
@Disabled
@TeleOp(name = "Field Relative Modified Teleop", group = "StarterBot")
public class StarterbotFieldRelativeTeleopWithMods extends OpMode {
    private final RobotHardware robot = new RobotHardware();
    private IMU imu;

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

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        telemetry.addData("Status", "Initialized (Field Relative with Mods)");
    }

    @Override
    public void loop() {
        telemetry.addLine("Press X to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");

        // If you press the x button, reset the Yaw
        if (gamepad1.x) {
            imu.resetYaw();
        }

        // Zero Power Behavior Controls (Driver 1: B for Brake, Y/Triangle for Float)
        if (gamepad1.b) {
            setDriveZeroPowerBehavior(BRAKE);
        }
        if (gamepad1.y || gamepad1.triangle) {
            setDriveZeroPowerBehavior(FLOAT);
        }

        // Field relative drive logic (Driver 1)
        if (gamepad1.left_bumper) {
            mecanumDrive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        } else {
            driveFieldRelative(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x);
        }

        // Intake Control (Operator / Driver 2)
        double intakePower = 0.0;
        if (gamepad2.x) {
            intakePower = 1.0;  // Intake Forward
        } else if (gamepad2.y || gamepad2.triangle) {
            intakePower = -1.0; // Intake Backwards
        }
        robot.setBroomPower(intakePower);
        robot.setLeftIntakeServoPower(intakePower);
        robot.setRightIntakeServoPower(intakePower);

        // Launcher Dpad Presets & Control
        handleLauncher();

        // Telemetry
        telemetry.addData("Launcher Target Velocity", currentLauncherTargetVelocity);
        telemetry.addData("Launcher Current Velocity", robot.getLauncherVelocity());
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
