package frc.robot.subsystems.drivetrain;

import static edu.wpi.first.units.Units.Meters;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.CONSTANTS.DriveConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwerveModuleTest {
    private static final double EPSILON = 1e-6;
    private static final double WHEEL_RADIUS_M = DriveConstants.WHEEL_RADIUS.in(Meters);

    private FakeModuleIO io;
    private SwerveModule module;

    @BeforeAll
    static void initHal() {
        HAL.initialize(500, 0);
    }

    @BeforeEach
    void setup() {
        this.io = new FakeModuleIO();
        this.module = new SwerveModule(this.io, "Test");
    }

    private void wheelAt(double degrees) {
        this.io.turnPosition = Rotation2d.fromDegrees(degrees);
        this.module.periodic();
    }

    @Test
    void convertsMetersPerSecondToWheelRadiansPerSecond() {
        wheelAt(0);
        this.module.setModuleState(new SwerveModuleState(3.0, Rotation2d.kZero));

        assertEquals(3.0 / WHEEL_RADIUS_M, this.io.lastDriveVelocityRadPerSec, EPSILON);
        assertEquals(0.0, this.io.lastTurnSetpoint.getDegrees(), EPSILON);
    }

    @Test
    void neverTurnsMoreThan90DegreesReversesWheelInstead() {
        wheelAt(0);
        this.module.setModuleState(new SwerveModuleState(2.0, Rotation2d.fromDegrees(170)));

        // Turning to -10 deg and driving backwards is the same motion as 170 deg forwards
        assertEquals(-10.0, this.io.lastTurnSetpoint.getDegrees(), EPSILON);
        double expected = -2.0 * Math.cos(Math.toRadians(10)) / WHEEL_RADIUS_M;
        assertEquals(expected, this.io.lastDriveVelocityRadPerSec, EPSILON);
    }

    @Test
    void slowsDownWhileWheelIsOffAngle() {
        wheelAt(30);
        this.module.setModuleState(new SwerveModuleState(2.0, Rotation2d.fromDegrees(60)));

        double expected = 2.0 * Math.cos(Math.toRadians(30)) / WHEEL_RADIUS_M;
        assertEquals(expected, this.io.lastDriveVelocityRadPerSec, EPSILON);
        assertEquals(60.0, this.io.lastTurnSetpoint.getDegrees(), EPSILON);
    }

    @Test
    void doesNotDriveWhenWheelIsPerpendicularToTarget() {
        wheelAt(0);
        this.module.setModuleState(new SwerveModuleState(2.0, Rotation2d.fromDegrees(90)));

        assertEquals(0.0, this.io.lastDriveVelocityRadPerSec, EPSILON);
    }

    @Test
    void modulePositionUsesWheelRadius() {
        this.io.drivePositionRad = 10.0;
        wheelAt(45);

        assertEquals(10.0 * WHEEL_RADIUS_M, this.module.getModulePosition().distanceMeters, EPSILON);
        assertEquals(45.0, this.module.getModulePosition().angle.getDegrees(), EPSILON);
    }
}
