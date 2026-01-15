package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Bases.MecanumDrive;

@TeleOp(name = "Power Overdrive (Linear)")
public class PowerOverdriveLinear extends LinearOpMode {

    private DcMotor backLeft, backRight, frontLeft, frontRight;
    private DcMotor coreHex, flywheel;

    private MecanumDrive driveBase;

    private double axialSpeed = 0.8;
    private double lateralSpeed = 0.8;
    private double yawSpeed = 0.8;

    private boolean flywheelOn = false;
    private double flywheelPower = 0.55;

    private boolean overdrive = false;

    private boolean stagerActive = false;
    private ElapsedTime stagerTimer = new ElapsedTime();

    @Override
    public void runOpMode() {

        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        coreHex = hardwareMap.get(DcMotor.class, "coreHex");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        driveBase = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);

        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        coreHex.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        coreHex.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            driveControl();
            flywheelControl();
            stagerControl();
            adjustFlywheelPower();

            telemetry.addData("Overdrive", overdrive ? "ON" : "OFF");
            telemetry.addData("Flywheel Power", flywheelPower);
            telemetry.update();
        }
    }

    private void driveControl() {

        if (gamepad1.start) {
            overdrive = !overdrive;
            sleep(250);
        }

        if (overdrive) {
            axialSpeed = 1.0;
            lateralSpeed = 1.0;
            yawSpeed = 1.0;
        } else {
            axialSpeed = 0.8;
            lateralSpeed = 0.8;
            yawSpeed = 0.8;
        }

        double axial = -gamepad1.left_stick_x * axialSpeed;
        double lateral = gamepad1.left_stick_y * lateralSpeed;
        double yaw = gamepad1.right_stick_x * yawSpeed;

        driveBase.drive(axial, lateral, yaw);
    }

    private void flywheelControl() {

        if (gamepad1.right_bumper) {
            flywheelOn = !flywheelOn;
            sleep(250);
        }

        if (gamepad1.dpad_up) {
            flywheel.setPower(1);
        } else if (gamepad1.left_bumper) {
            flywheel.setPower(-1);
        } else if (flywheelOn) {
            flywheel.setPower(flywheelPower);
        } else {
            flywheel.setPower(0);
        }
    }

    private void stagerControl() {

        if (gamepad1.b) {
            coreHex.setPower(1);
            coreHex.setDirection(DcMotor.Direction.REVERSE);
        }

        if (gamepad1.a) {
            stagerActive = true;
            stagerTimer.reset();
            sleep(250);
        }

        if (!gamepad1.b && stagerActive) {
            if (stagerTimer.milliseconds() < 700) {
                coreHex.setDirection(DcMotor.Direction.FORWARD);
                coreHex.setPower(1);
            } else {
                coreHex.setPower(0);
                stagerActive = false;
            }
        }
    }

    private void adjustFlywheelPower() {
        if (gamepad1.dpad_right) {
            flywheelPower += 0.01;
            sleep(150);
        }
        if (gamepad1.dpad_left) {
            flywheelPower -= 0.01;
            sleep(150);
        }
    }
}
