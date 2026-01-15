package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import org.firstinspires.ftc.teamcode.Bases.AprilTagBase;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "Telemetry: AprilTag + IMU")
public class Telemetry extends LinearOpMode {

    private AprilTagBase vision;
    private IMU imu;

    @Override
    public void runOpMode() throws InterruptedException {

        // --- APRILTAG CAMERA ---
        vision = new AprilTagBase(hardwareMap, "Webcam 1");

        // --- IMU INITIALIZATION (BHI260AP) ---
        imu = hardwareMap.get(IMU.class, "imu");

        // Your Control Hub orientation:
        // Logo faces LEFT, USB ports point UP
        RevHubOrientationOnRobot hubOrientation = new RevHubOrientationOnRobot(
                LogoFacingDirection.LEFT,
                UsbFacingDirection.UP
        );

        IMU.Parameters params = new IMU.Parameters(hubOrientation);
        imu.initialize(params);

        telemetry.addLine("Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // --- APRILTAG DATA ---
            AprilTagDetection tag = vision.getTagById(1);

            if (tag != null) {
                double forwardInches = vision.getForwardDistance(tag);
                double euclidInches  = vision.getEuclideanDistance(tag);

                telemetry.addLine("AprilTag FOUND");
                telemetry.addData("Forward Distance (in)", forwardInches);
                telemetry.addData("Euclidean Distance (in)", euclidInches);
                telemetry.addData("Tag ID", tag.id);
            } else {
                telemetry.addLine("AprilTag NOT found");
            }

            // --- IMU ORIENTATION ---
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();

            double heading = orientation.getYaw(AngleUnit.DEGREES);
            double pitch   = orientation.getPitch(AngleUnit.DEGREES);
            double roll    = orientation.getRoll(AngleUnit.DEGREES);

            telemetry.addLine("IMU Orientation");
            telemetry.addData("Heading (Yaw)", heading);
            telemetry.addData("Pitch", pitch);
            telemetry.addData("Roll", roll);

            telemetry.update();
            sleep(20);
        }

        vision.stop();
    }
}
