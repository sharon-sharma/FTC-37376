package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Bases.AprilTagBase;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "Auto: AprilTag + Launch 3 Balls (Rewritten)")
public class Autonomous1 extends LinearOpMode {

    private DcMotor leftFront, rightFront, leftRear, rightRear;
    private DcMotor launcher;
    private AprilTagBase vision;

    @Override
    public void runOpMode() throws InterruptedException {

        initHardware();
        telemetry.addLine("Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) return;
        // Step 1: Move forward
        driveForward(0.5, 1000);
        // Step 2: Turn 90° clockwise
        turnRight(0.5, 500);
        // Step 3: Detect AprilTag
        AprilTagDetection tag = detectTag(1, 5000);

        if (tag != null) {
            double forwardInches = vision.getForwardDistance(tag);
            double euclidInches  = vision.getEuclideanDistance(tag);

            telemetry.addLine("Tag found!");
            telemetry.addData("Forward Distance (in)", forwardInches);
            telemetry.addData("Euclidean Distance (in)", euclidInches);
            telemetry.update();

            long driveTime = (long)(forwardInches * 20);  // tune multiplier
            driveForward(0.5, driveTime);

        } else {
            telemetry.addLine("Tag NOT found. Skipping distance move.");
            telemetry.update();
        }
        // Step 4: Launch 3 balls
        launchBalls(3);

        vision.stop();
    }
    // Initialization
    private void initHardware() {
        leftFront  = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFront = hardwareMap.get(DcMotor.class, "frontRight");
        leftRear   = hardwareMap.get(DcMotor.class, "backLeft");
        rightRear  = hardwareMap.get(DcMotor.class, "backRight");

        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightRear.setDirection(DcMotor.Direction.REVERSE);

        launcher = hardwareMap.get(DcMotor.class, "flywheel");

        vision = new AprilTagBase(hardwareMap, "Webcam 1");
    }
    // AprilTag detection helper
    private AprilTagDetection detectTag(int id, long timeoutMs) {
        long start = System.currentTimeMillis();
        AprilTagDetection tag = null;

        while (opModeIsActive() &&
                tag == null &&
                System.currentTimeMillis() - start < timeoutMs) {

            tag = vision.getTagById(id);

            telemetry.addLine("Searching for AprilTag ID " + id + "...");
            telemetry.update();
        }

        return tag;
    }
    // Drive helpers
    private void driveForward(double power, long timeMs) {
        setDrivePower(power, power, power, power);
        sleep(timeMs);
        stopDrive();
    }

    private void turnRight(double power, long timeMs) {
        setDrivePower(power, -power, power, -power);
        sleep(timeMs);
        stopDrive();
    }

    private void stopDrive() {
        setDrivePower(0, 0, 0, 0);
    }

    private void setDrivePower(double lf, double rf, double lr, double rr) {
        leftFront.setPower(lf);
        rightFront.setPower(rf);
        leftRear.setPower(lr);
        rightRear.setPower(rr);
    }

    // Launcher helper
    private void launchBalls(int count) {
        for (int i = 0; i < count; i++) {
            launcher.setPower(1.0);
            sleep(500);   // spin-up time
            launcher.setPower(0.0);
            sleep(500);   // pause between shots
        }
    }
}
