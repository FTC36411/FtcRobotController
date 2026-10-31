package org.firstinspires.ftc.teamcode.notCritical;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.RobotHardware;

/**
 * Test OpMode to verify connection and test the windmill servo directly with diagnostic telemetry.
 */
//@Disabled
@TeleOp(name = "Test Windmill Servo", group = "Test")
public class Test extends OpMode {
    public final RobotHardware robot = new RobotHardware();

    @Override
    public void init() {
        robot.init(hardwareMap);
        telemetry.addData("Init Status", robot.initStatus);
        telemetry.update();
    }

    @Override
    public void start() {
        robot.setWindmillPower(1.0);
    }

    @Override
    public void loop() {
        robot.setWindmillPower(1.0);

        telemetry.addData("Init Log", robot.initStatus);
        if (robot.windmillServo != null) {
            telemetry.addData("Windmill Type", "CRServo (Continuous)");
            telemetry.addData("Windmill Power Set", 1.0);
            telemetry.addData("Windmill Active Power", robot.windmillServo.getPower());
        } else if (robot.windmillPositionalServo != null) {
            telemetry.addData("Windmill Type", "Standard Servo (Positional)");
            telemetry.addData("Windmill Position", robot.windmillPositionalServo.getPosition());
        } else {
            telemetry.addData("Windmill Status", "NOT FOUND ('ballrotator' or 'windmill')");
            telemetry.addData("Action Required", "Check Robot Config on Driver Hub for device name 'ballrotator'");
        }
        telemetry.update();
    }
}
