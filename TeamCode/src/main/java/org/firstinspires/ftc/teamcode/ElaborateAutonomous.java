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
        state = state.START;
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
                //shoots balls from the position
                robot.setLauncherVelocity(LAUNCHER_TARGET_VELOCITY);
                while (robot.getLauncherVelocity() < LAUNCHER_MIN_VELOCITY) {
                    if (robot.getLauncherVelocity() >= LAUNCHER_MIN_VELOCITY) {
                        break;
                    }
                }
                robot.setWindmillPower(1.0);
                sleep(3000);
                state = State.LEAVE_STARTING_POSITION;
                break;
            case LEAVE_STARTING_POSITION:
                //moves the robot forward
                robot.setDrivePower(-1.0, -1.0, -1.0, -1.0);
                sleep(500);
                robot.setDrivePower(0, 0, 0, 0);
                sleep(500);
                state = State.COLLECT_POLLEN_FROM_FLOWER;
                break;
            case COLLECT_POLLEN_FROM_FLOWER:
                //collects pollen from flower
                robot.setDrivePower(-1.0, -1.0, -1.0, -1.0);
                sleep(500);
                robot.setDrivePower(0, 0, 0, 0);
                sleep(500);
                state = State.MOVE_TO_OTHER_SIDE;
                break;
            case MOVE_TO_OTHER_SIDE:
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
