package frc.robot.subsystems.drivetrain;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.CONSTANTS.DriveConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrivetrainTest {
    private static final double EPSILON = 1e-6;
    private static final double WHEEL_RADIUS_M = DriveConstants.WHEEL_RADIUS.in(Meters);

    private FakeModuleIO frontLeft;
    private FakeModuleIO frontRight;
    private FakeModuleIO backLeft;
    private FakeModuleIO backRight;
    private Drivetrain drivetrain;

    @BeforeAll
    static void initHal() {
        HAL.initialize(500, 0);
    }

    @BeforeEach
    void setup() {
        this.frontLeft = new FakeModuleIO();
        this.frontRight = new FakeModuleIO();
        this.backLeft = new FakeModuleIO();
        this.backRight = new FakeModuleIO();
        for (FakeModuleIO io : modules()) {
            io.followTurnSetpoint = true;
        }
        this.drivetrain = new Drivetrain(
            new GyroIO() {},
            this.frontLeft,
            this.frontRight,
            this.backLeft,
            this.backRight
        );
    }

    private FakeModuleIO[] modules() {
        return new FakeModuleIO[] {
            this.frontLeft, this.frontRight, this.backLeft, this.backRight
        };
    }

    /** Runs two loops so the wheels have turned to their targets before speeds are checked. */
    private void drive(ChassisSpeeds speeds) {
        this.drivetrain.setDesiredState(speeds);
        this.drivetrain.periodic();
        this.drivetrain.periodic();
    }

    /** The wheel's commanded ground velocity as a vector (independent of the 180 deg optimization). */
    private static Translation2d wheelVelocity(FakeModuleIO io) {
        double speed = io.lastDriveVelocityRadPerSec * WHEEL_RADIUS_M;
        return new Translation2d(speed, io.lastTurnSetpoint);
    }

    private static void assertVelocity(Translation2d expected, FakeModuleIO io, String name) {
        Translation2d actual = wheelVelocity(io);
        assertEquals(expected.getX(), actual.getX(), EPSILON, name + " x");
        assertEquals(expected.getY(), actual.getY(), EPSILON, name + " y");
    }

    @Test
    void drivingForwardPointsAllWheelsForwardAtEqualSpeed() {
        drive(new ChassisSpeeds(2.0, 0.0, 0.0));

        for (FakeModuleIO io : modules()) {
            assertVelocity(new Translation2d(2.0, 0.0), io, "module");
        }
    }

    @Test
    void strafingLeftPointsAllWheelsLeft() {
        drive(new ChassisSpeeds(0.0, 1.5, 0.0));

        for (FakeModuleIO io : modules()) {
            assertVelocity(new Translation2d(0.0, 1.5), io, "module");
        }
    }

    @Test
    void spinningCounterClockwiseMovesEachWheelTangentially() {
        double omega = 1.0;
        drive(new ChassisSpeeds(0.0, 0.0, omega));

        // v = omega x r  ->  (-omega * y, omega * x) for a module at (x, y)
        assertVelocity(tangential(DriveConstants.FRONT_LEFT.LocationX, DriveConstants.FRONT_LEFT.LocationY, omega), this.frontLeft, "front left");
        assertVelocity(tangential(DriveConstants.FRONT_RIGHT.LocationX, DriveConstants.FRONT_RIGHT.LocationY, omega), this.frontRight, "front right");
        assertVelocity(tangential(DriveConstants.BACK_LEFT.LocationX, DriveConstants.BACK_LEFT.LocationY, omega), this.backLeft, "back left");
        assertVelocity(tangential(DriveConstants.BACK_RIGHT.LocationX, DriveConstants.BACK_RIGHT.LocationY, omega), this.backRight, "back right");
    }

    private static Translation2d tangential(double x, double y, double omega) {
        return new Translation2d(-omega * y, omega * x);
    }

    @Test
    void wheelSpeedsAreCappedAtMaxSpeed() {
        double maxSpeed = DriveConstants.SPEED_AT_12_VOLTS.in(MetersPerSecond);
        drive(new ChassisSpeeds(maxSpeed * 3, 0.0, 5.0));

        for (FakeModuleIO io : modules()) {
            double speed = Math.abs(io.lastDriveVelocityRadPerSec * WHEEL_RADIUS_M);
            assertTrue(speed <= maxSpeed + EPSILON, "wheel speed " + speed + " exceeds " + maxSpeed);
        }
    }

    @Test
    void stoppedRobotCommandsZeroWheelSpeed() {
        drive(new ChassisSpeeds());

        for (FakeModuleIO io : modules()) {
            assertEquals(0.0, io.lastDriveVelocityRadPerSec, EPSILON);
        }
    }
}
