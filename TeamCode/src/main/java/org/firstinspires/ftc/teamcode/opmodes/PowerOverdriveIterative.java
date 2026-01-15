package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Bases.MecanumDrive;

@TeleOp(name = "Power Overdrive (Iterative)")
public class PowerOverdriveIterative extends OpMode {

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

    // Button edge detection flags
    private boolean prevStart = false;
    private boolean prevRB = false;
    private boolean prevA = false;

    @Override
    public void init() {

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
    }

    @Override
    public void loop() {

        driveControl();
        flywheelControl();
        stagerControl();
        adjustFlywheelPower();

        telemetry.addData("Overdrive", overdrive ? "ON" : "OFF");
        telemetry.addData("Flywheel Power", flywheelPower);
    }

    private void driveControl() {

        boolean startPressed = gamepad1.start && !prevStart;
        prevStart = gamepad1.start;

        if (startPressed) {
            overdrive = !overdrive;
        }

        // Left joystick ALWAYS 80%
        axialSpeed = 0.8;
        lateralSpeed = 0.8;

        // Overdrive only boosts turning
        if (overdrive) {
            yawSpeed = 1.0;
        } else {
            yawSpeed = 0.8;
        }

        double axial   = -gamepad1.left_stick_y * axialSpeed;   // forward/back
        double lateral =  gamepad1.left_stick_x * lateralSpeed; // strafe
        double yaw     =  gamepad1.right_stick_x * yawSpeed;    // turn

        driveBase.drive(axial, lateral, yaw);
    }
    private void flywheelControl() {

        // Detect the moment the right bumper is pressed
        boolean rbPressed = gamepad1.right_bumper && !prevRB;
        prevRB = gamepad1.right_bumper;

        // Toggle flywheel state on each press
        if (rbPressed) {
            flywheelOn = !flywheelOn;
        }

        // Manual overrides always take priority
        if (gamepad1.dpad_up) {
            flywheel.setPower(1.0);          // 100% forward
        } else if (gamepad1.left_bumper) {
            flywheel.setPower(-1.0);         // 100% backward
        } else if (flywheelOn) {
            flywheel.setPower(0.55);         // Toggle mode: 55% power
        } else {
            flywheel.setPower(0);            // Off
        }
    }
    private void stagerControl() {

        // If A is pressed, run forward for 700 ms
        if (gamepad1.a) {
            stagerActive = true;
            stagerTimer.reset();
            coreHex.setDirection(DcMotor.Direction.FORWARD);
        }

        // If B is pressed, run backward for 700 ms
        if (gamepad1.b) {
            stagerActive = true;
            stagerTimer.reset();
            coreHex.setDirection(DcMotor.Direction.REVERSE);
        }

        // If a timed movement is active, run motor for 700 ms
        if (stagerActive) {
            if (stagerTimer.milliseconds() < 700) {
                coreHex.setPower(1.0);   // 100% power
            } else {
                coreHex.setPower(0);
                stagerActive = false;    // Done
            }
        } else {
            coreHex.setPower(0);         // Idle
        }
    }

    private void adjustFlywheelPower() {
        if (gamepad1.dpad_right) {
            flywheelPower += 0.01;
        }
        if (gamepad1.dpad_left) {
            flywheelPower -= 0.01;
        }
    }
}
