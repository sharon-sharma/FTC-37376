package org.firstinspires.ftc.teamcode.Bases;
import android.util.Size;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class AprilTagBase {

    private AprilTagProcessor tagProcessor;
    private VisionPortal visionPortal;

    public AprilTagBase(HardwareMap hardwareMap, String webcamName) {

        tagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, webcamName))
                .addProcessor(tagProcessor)
                .setCameraResolution(new Size(640, 480))
                .build();
    }

    public List<AprilTagDetection> getDetections() {
        return tagProcessor.getDetections();
    }

    public AprilTagDetection getTagById(int id) {
        for (AprilTagDetection tag : tagProcessor.getDetections()) {
            if (tag.id == id) return tag;
        }
        return null;
    }

// -----------------------------
// Distance helper methods
// -----------------------------

    // Forward distance (inches) – always positive
    public double getForwardDistance(AprilTagDetection tag) {
        return Math.abs(tag.ftcPose.z);
    }

    // Full 3D Euclidean distance (inches)
    public double getEuclideanDistance(AprilTagDetection tag) {
        double x = tag.ftcPose.x;
        double y = tag.ftcPose.y;
        double z = tag.ftcPose.z;
        return Math.sqrt(x*x + y*y + z*z);
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}
