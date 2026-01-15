package org.firstinspires.ftc.teamcode.Bases;

import com.qualcomm.robotcore.hardware.DcMotor;

public class arcadeDrive {

    private DcMotor frontLeft, frontRight, backLeft, backRight;

    public arcadeDrive(DcMotor lf, DcMotor rf, DcMotor lr, DcMotor rr) {
        this.frontLeft = lf;
        this.frontRight = rf;
        this.backLeft = lr;
        this.backRight = rr;
    }

    // Main arcade drive method
    public void drive(double drive, double turn) {

        double leftPower  = drive + turn;
        double rightPower = drive - turn;

        // Clip values to [-1, 1]
        leftPower  = Math.max(-1, Math.min(1, leftPower));
        rightPower = Math.max(-1, Math.min(1, rightPower));

        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);
    }
}
