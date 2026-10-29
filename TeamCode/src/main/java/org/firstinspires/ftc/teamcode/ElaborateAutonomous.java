package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
/**
 * Simple autonomous code
 * uses pollen that is in the transition, and pollen in the flower and shoots it to the hive
 * parks back into starting position
 * gets 40 points from tipping the hive twice, and gets 8 swarm points
 */

@Autonomous
public class ElaborateAutonomous extends OpMode {
    private final RobotHardware robot = new RobotHardware();
    public final int LAUNCHER_TARGET_VELOCITY = 1250; //2678 RPM
    public final int LAUNCHER_MIN_VELOCITY = 800; //2571 RPM //1200
    public double intakePower = 1.0;

    enum State {
        START,
        SHOOT_BALLS,
        LEAVE_STARTING_POSITION,
        COLLECT_POLLEN_FROM_FLOWER,
        MOVE_TO_OTHER_SIDE,
        SHOOT_BALLS_AFTER_TIP,
        PARK,
        FINISHED
    }

    State state = State.START;


    @Override
    public void init() {
        robot.init(hardwareMap);
        state = State.START;
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        switch (state) {
            case START:
                telemetry.addLine("Started Program");
                state = State.SHOOT_BALLS;
                break;
            case SHOOT_BALLS:
                // Shoots balls from the position
                robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);

                // Wait until the launcher reaches the minimum velocity
                while (robot.getLauncherVelocity() < LAUNCHER_MIN_VELOCITY) {
                    sleep(1);
                }

                robot.setWindmillPower(1.0);
                sleep(3000);
                state = State.LEAVE_STARTING_POSITION;
                break;
            case LEAVE_STARTING_POSITION:
                //moves the robot forward
                robot.mecanumDrive(-1.0, 0.0, 0.0, 0.5);
                state = State.COLLECT_POLLEN_FROM_FLOWER;
                break;
            case COLLECT_POLLEN_FROM_FLOWER:
                //collects pollen from flower
                robot.mecanumDrive(0.0, -1.0, 0.0, 0.3);
                robot.setIntakePower(1.0);
                robot.mecanumDrive(1.0, 0.0, 0.0, 0.5);
                state = State.MOVE_TO_OTHER_SIDE;
                break;
            case MOVE_TO_OTHER_SIDE:
                //makes the robot move to the other side of the hive
                robot.mecanumDrive(-1.0, 1.0, 0.0, 0.5);
                robot.mecanumDrive(-1.0, 0.0, 0.0, 1.0);
                robot.mecanumDrive(0.0, 0.0, 1.0, "rotate180");
                state = State.SHOOT_BALLS_AFTER_TIP;
                break;
            case SHOOT_BALLS_AFTER_TIP:
                state = State.PARK;
                break;
            case PARK:
                state = State.FINISHED;
                break;
            default:
                telemetry.addLine("Autonomous Mode Completed!");
                stop();
        }
    }

    @Override
    public void loop() {
        robot.setIntakePower(intakePower);
        telemetry.addData("Current State", state);
    }

}
