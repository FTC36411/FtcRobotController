package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous (name = "MoveForwardAndBack")
public class StarterAutonomous extends OpMode {
    private final RobotHardware robot = new RobotHardware();

    @Override
    public void init() {
        robot.init(hardwareMap);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");

    }

    @Override
    public void start() {

        robot.setDrivePower(1.0, 1.0, 1.0, 1.0);
        sleep(1000);
        robot.setDrivePower(0, 0, 0, 0);
        sleep(1000);
        robot.setDrivePower(-1.0, -1.0, -1.0, -1.0);
        sleep(1000);
        robot.setDrivePower(0, 0, 0, 0);

    }

    @Override
    public void loop() {

    }
}
