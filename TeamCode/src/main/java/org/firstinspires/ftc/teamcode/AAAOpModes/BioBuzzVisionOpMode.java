package org.firstinspires.ftc.teamcode.AAAOpModes;

import static org.firstinspires.ftc.teamcode.BioBuzzVision.Subsystems.Constants.cx;
import static org.firstinspires.ftc.teamcode.BioBuzzVision.Subsystems.Constants.cy;
import static org.firstinspires.ftc.teamcode.BioBuzzVision.Subsystems.Constants.fx;
import static org.firstinspires.ftc.teamcode.BioBuzzVision.Subsystems.Constants.fy;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.BioBuzzVision.Utilities.Vision.BioBuzzProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp(name = "BioBuzz Vision Test")
public class BioBuzzVisionOpMode extends OpMode {
    WebcamName webcam1;
    BioBuzzProcessor bioBuzzProcessor;
    AprilTagProcessor aprilTagProcessor;
    VisionPortal visionPortal;

    static Size cameraResolution = new Size(1280, 720);

    @Override
    public void init() {
        bioBuzzProcessor = new BioBuzzProcessor();
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setLensIntrinsics(fx, fy, cx, cy)
                .build();

        webcam1 = hardwareMap.get(WebcamName.class, "Webcam 1");
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam1)
                .addProcessors(bioBuzzProcessor, aprilTagProcessor)
                .setCameraResolution(cameraResolution)
                .build();

        FtcDashboard.getInstance().startCameraStream(visionPortal, 0);
        telemetry = FtcDashboard.getInstance().getTelemetry();
    }

    @Override
    public void loop() {
    }
}
