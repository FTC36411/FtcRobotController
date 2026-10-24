package org.firstinspires.ftc.teamcode.notCritical.Utilities.Vision;

import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

public class VisionUtils {

    public static double IMG_WIDTH = 432;
    public static double IMG_HEIGHT = 240;
    public static final double X_FOV = 72;
    public static final double Y_FOV = 43;

    public enum RECT_OPTION {
        AREA, WIDTH, HEIGHT, X, Y
    }
    public enum AXES {
        X, Y
    }

    public static double pixels2Degrees(double pixels, AXES axe) {
        return (axe == AXES.X) ? pixels * (X_FOV / IMG_WIDTH) : pixels * (Y_FOV / IMG_HEIGHT);
    }

    public static List<Rect> sortRectsByMaxOption(int n, RECT_OPTION option, List<Rect> rects){
        List<Rect> sorted_rects = new ArrayList<>();
        for (int j=0; j < n; j++){
            int alpha_i = getMaxIndex(rects, option);
            sorted_rects.add(rects.get(alpha_i));

            rects.remove(alpha_i);
            if (rects.isEmpty()) break;
        }
        return sorted_rects;
    }

    public static int getMaxIndex(List<Rect> rects, RECT_OPTION option){
        int alpha_index = 0;
        double max = Integer.MIN_VALUE;
        double cur = 0;
        for (int i=0; i < rects.size(); i++){
            switch (option){
                case X:
                    cur = rects.get(i).x;
                    break;
                case Y:
                    cur = rects.get(i).y;
                    break;
                case WIDTH:
                    cur = rects.get(i).width;
                    break;
                case HEIGHT:
                    cur = rects.get(i).height;
                    break;
                case AREA:
                    cur = rects.get(i).width * rects.get(i).height;
                    break;
            }
            if (cur > max) {
                max = cur;
                alpha_index = i;
            }
        }
        return alpha_index;
    }
}
