package org.firstinspires.ftc.teamcode.notCritical.Utilities.Vision;

import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2HSV;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.findContours;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BioBuzzProcessor implements VisionProcessor, CameraStreamSource {

    // Region of the most pollen detected
    public static Rect largestPollenRect;
    public static boolean pollenDetected = false;

    public static int erodeConstant = 5;
    public static int dilateConstant = 5;

    private ArrayList<MatOfPoint> contoursPollen = new ArrayList<>();
    private List<Rect> pollenRects = new ArrayList<>();

    private static int IMG_HEIGHT = 0;
    private static int IMG_WIDTH = 0;

    private Mat output = new Mat(),
            maskPollen = new Mat();
    private Mat hierarchy = new Mat();

    private final AtomicReference<Bitmap> lastFrame =
            new AtomicReference<>(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

    @Config
    public static class BioBuzzDash {
        // BIOBUZZ Season Pollen (Yellow/Gold) HSV Color Thresholds (adjustable in Dashboard)
        public static int maxH_pollen = 35;
        public static int maxS_pollen = 255;
        public static int maxV_pollen = 255;
        public static int minH_pollen = 20;
        public static int minS_pollen = 100;
        public static int minV_pollen = 100;
    }

    @Override
    public Mat processFrame(Mat input, long captureTimeNanos) {
        input.copyTo(output);
        contoursPollen.clear();
        pollenRects.clear();

        IMG_HEIGHT = input.rows();
        IMG_WIDTH = input.cols();

        Scalar MIN_THRESH = new Scalar(BioBuzzDash.minH_pollen, BioBuzzDash.minS_pollen, BioBuzzDash.minV_pollen);
        Scalar MAX_THRESH = new Scalar(BioBuzzDash.maxH_pollen, BioBuzzDash.maxS_pollen, BioBuzzDash.maxV_pollen);

        // Convert frame from RGB to HSV
        Imgproc.cvtColor(input, maskPollen, COLOR_RGB2HSV);
        inRange(maskPollen, MIN_THRESH, MAX_THRESH, maskPollen);

        // Optional submat to ignore edges
        Rect submatRect = new Rect(new Point(4, 4), new Point(IMG_WIDTH, IMG_HEIGHT));
        maskPollen = maskPollen.submat(submatRect);

        // Noise reduction via morphological operations (erode & dilate)
        erode(maskPollen, maskPollen, new Mat(erodeConstant, erodeConstant, CV_8U));
        dilate(maskPollen, maskPollen, new Mat(dilateConstant, dilateConstant, CV_8U));

        // Find contours of pollen blobs
        findContours(maskPollen, contoursPollen, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        // Find the region of the most pollen (largest contour/rect)
        largestPollenRect = getLargestPollenObject(contoursPollen);

        // Convert output Mat to Bitmap for Dashboard streaming
        Bitmap b = Bitmap.createBitmap(output.width(), output.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(output, b);
        lastFrame.set(b);

        return output;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        Paint paintPollen = new Paint();
        paintPollen.setColor(Color.YELLOW);
        paintPollen.setStyle(Paint.Style.STROKE);
        paintPollen.setStrokeWidth(scaleCanvasDensity * 8);

        if (pollenDetected && largestPollenRect != null) {
            canvas.drawRect(makeGraphicsRect(largestPollenRect, scaleBmpPxToCanvasPx), paintPollen);
        }
    }

    @Override
    public void getFrameBitmap(Continuation<? extends Consumer<Bitmap>> continuation) {
        continuation.dispatch(bitmapConsumer -> bitmapConsumer.accept(lastFrame.get()));
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        lastFrame.set(Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565));
    }

    public Rect getLargestPollenObject(ArrayList<MatOfPoint> contours) {
        if (!contours.isEmpty()) {
            MatOfPoint largestContour = findLargestContour(contours);
            if (largestContour != null) {
                for (int i = 0; i < contours.size(); i++) {
                    Rect rect = boundingRect(contours.get(i));
                    pollenRects.add(rect);
                }
                if (!pollenRects.isEmpty()) {
                    Rect maxRect = VisionUtils.sortRectsByMaxOption(1, VisionUtils.RECT_OPTION.AREA, pollenRects).get(0);
                    pollenDetected = true;
                    return maxRect;
                }
            }
        }
        pollenDetected = false;
        return null;
    }

    private MatOfPoint findLargestContour(List<MatOfPoint> contours) {
        double maxArea = 0;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }
        return largestContour;
    }

    private android.graphics.Rect makeGraphicsRect(Rect rect, float scaleBmpPxToCanvasPx) {
        int left = Math.round(rect.x * scaleBmpPxToCanvasPx);
        int top = Math.round(rect.y * scaleBmpPxToCanvasPx);
        int right = left + Math.round(rect.width * scaleBmpPxToCanvasPx);
        int bottom = top + Math.round(rect.height * scaleBmpPxToCanvasPx);

        return new android.graphics.Rect(left, top, right, bottom);
    }
}
