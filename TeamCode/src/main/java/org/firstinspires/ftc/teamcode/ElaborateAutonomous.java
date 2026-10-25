package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
/**
 * Simple autonomous code
 * uses pollen that is in the transition, and pollen in the flower and shoots it to the hive
 * parks back into starting position
 * gets 40 points from tipping the hive twice, and gets 8 swarm points
 */

public class ElaborateAutonomous extends OpMode {
    private final RobotHardware robot = new RobotHardware();
    public final int LAUNCHER_TARGET_VELOCITY = 1250; //2678 RPM
    public final int LAUNCHER_MIN_VELOCITY = 800; //2571 RPM //1200
    public double intakePower = 1.0;


    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
    }
    
    @Override
    public void start() {
        //shoots balls from the position
        robot.setWindmillPower(1);
        robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
        while (robot.getLauncherVelocity() < LAUNCHER_MIN_VELOCITY) {
            if (robot.getLauncherVelocity() >= LAUNCHER_MIN_VELOCITY) {
                break;
            }
        }
        robot.setWindmillPower(1.0);
        sleep(3000);

        //collects pollen from flower
        robot.setDrivePower(-1.0, -1.0, -1.0, -1.0);
        sleep(500);
        robot.setDrivePower(0, 0, 0, 0);
        sleep(500);

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
