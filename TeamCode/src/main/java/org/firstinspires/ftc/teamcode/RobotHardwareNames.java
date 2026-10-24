package org.firstinspires.ftc.teamcode;

/**
 * Centralized repository for all robot hardware device names.
 * Reference these constants across your OpModes so that when a device name
 * changes in the Robot Configuration on the Driver Station, you only have
 * to change it once in this file.
 */
public final class RobotHardwareNames {

    // Private constructor to prevent instantiation
    private RobotHardwareNames() {}

    // Mecanum Drivetrain Motors
    public static final String LEFT_FRONT_DRIVE = "leftFront";
    public static final String RIGHT_FRONT_DRIVE = "rightFront";
    public static final String LEFT_BACK_DRIVE = "leftBack";
    public static final String RIGHT_BACK_DRIVE = "rightBack";


    // Intake & Launcher Hardware
    public static final String INTAKE = "broom";
    public static final String LAUNCHER = "launcher";
    public static final String WINDMILL_SERVO = "ballrotator";
    public static final String LEFT_INTAKE_SERVO = "leftIntakeServo";
    public static final String RIGHT_INTAKE_SERVO = "rightIntakeServo";

}
