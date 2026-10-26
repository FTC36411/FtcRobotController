package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Basic robot autonomous code.
 * Moves forward, moves backward, and shoots balls.
 * Shoots 4 balls into the hive and gains 8 scoring points
 */

@Autonomous(name = "Shoot Balls")
public class ShooterAutonomous extends OpMode {
    private final RobotHardware robot = new RobotHardware();

    public final int LAUNCHER_TARGET_VELOCITY = 1250; //2678 RPM
    public final int LAUNCHER_MIN_VELOCITY = 800; //2571 RPM //1200
    public double intakePower = 1.0;

    @Override
    public void init() {
        //pulls the methods to setup all of the hardware
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        // Shoots balls from the position
        robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        // Wait until the launcher reaches the minimum velocity
        while (robot.getLauncherVelocity() < LAUNCHER_MIN_VELOCITY) {}
        robot.setWindmillPower(1.0);
        sleep(10000);
        //waitUntil(robot.getLauncherVelocity() < LAUNCHER_MIN_VELOCITY);

        //moves the robot forward
        robot.setDrivePower(-1.0, -1.0, -1.0, -1.0);
        sleep(500);
        robot.setDrivePower(0, 0, 0, 0);
        sleep(500);

        //moves the robot back to park
        robot.setDrivePower(1.0, 1.0, 1.0, 1.0);
        sleep(700);
        robot.setDrivePower(0, 0, 0, 0);

        stop();

    }

    @Override
    public void loop() {
        robot.setIntakePower(intakePower);
    }
}
