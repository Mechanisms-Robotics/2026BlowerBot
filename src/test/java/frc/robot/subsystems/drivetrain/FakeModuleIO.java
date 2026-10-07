package frc.robot.subsystems.drivetrain;

import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Test double for a swerve module. Records the last commands it received and reports whatever
 * wheel angle the test sets. With {@code followTurnSetpoint} enabled, the wheel "snaps" to the
 * last commanded angle on the next update, like an ideal steering motor.
 */
public class FakeModuleIO implements ModuleIO {
    public Rotation2d turnPosition = Rotation2d.kZero;
    public double drivePositionRad = 0.0;
    public boolean followTurnSetpoint = false;

    public double lastDriveVelocityRadPerSec = Double.NaN;
    public Rotation2d lastTurnSetpoint = null;
    public double lastDriveOpenLoop = Double.NaN;

    @Override
    public void updateInputs(ModuleIOInputs inputs) {
        if (this.followTurnSetpoint && this.lastTurnSetpoint != null) {
            this.turnPosition = this.lastTurnSetpoint;
        }
        inputs.driveConnected = true;
        inputs.turnConnected = true;
        inputs.drivePositionRad = this.drivePositionRad;
        inputs.turnPosition = this.turnPosition;
        inputs.turnAbsolutePosition = this.turnPosition;
    }

    @Override
    public void setDriveVelocity(double velocityRadPerSec) {
        this.lastDriveVelocityRadPerSec = velocityRadPerSec;
    }

    @Override
    public void setTurnPosition(Rotation2d rotation) {
        this.lastTurnSetpoint = rotation;
    }

    @Override
    public void setDriveOpenLoop(double output) {
        this.lastDriveOpenLoop = output;
    }
}
