package org.firstinspires.ftc.teamcode.notCritical;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * just a test project to verify connection between android studio, the driver hub, and the robot
 */
@Disabled
@TeleOp
public class Test extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello", "User");
    }

    public void start() {}

    @Override
    public void loop(){

    }
}
