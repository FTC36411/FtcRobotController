package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

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
        //shoots balls from the position
        robot.setWindmillPower(1);
        robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        sleep(10000);

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
