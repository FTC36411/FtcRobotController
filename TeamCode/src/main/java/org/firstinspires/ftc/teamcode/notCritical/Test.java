package org.firstinspires.ftc.teamcode.notCritical;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.RobotHardware;

/**
 * just a test project to verify connection between android studio, the driver hub, and the robot
 */
//@Disabled
@TeleOp
public class Test extends OpMode {
    public final RobotHardware robot = new RobotHardware();

    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Hello", "User");
    }

    public void start() {
        robot.setWindmillPower(1.0);
    }

    @Override
    public void loop(){
        robot.setWindmillPower(1.0);
    }
}
