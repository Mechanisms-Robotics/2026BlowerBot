package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.CONSTANTS.DriveConstants;
import org.junit.jupiter.api.Test;

/** Tests the joystick shaping (deadband + squaring) used by the teleop drive command. */
class RobotContainerDriveInputTest {
    private static final double EPSILON = 1e-9;

    @Test
    void smallStickWiggleInsideDeadbandIsIgnored() {
        double inside = DriveConstants.DEADBAND * 0.9;
        Translation2d v = RobotContainer.getDriveVelocity(inside, 0.0);

        assertEquals(0.0, v.getNorm(), EPSILON);
    }

    @Test
    void fullStickIsFullSpeed() {
        assertEquals(1.0, RobotContainer.getDriveVelocity(1.0, 0.0).getX(), EPSILON);
        assertEquals(-1.0, RobotContainer.getDriveVelocity(0.0, -1.0).getY(), EPSILON);
    }

    @Test
    void outputIsSquaredForFineControl() {
        double deadband = DriveConstants.DEADBAND;
        double scaled = (0.5 - deadband) / (1.0 - deadband);

        assertEquals(scaled * scaled, RobotContainer.getDriveVelocity(0.5, 0.0).getX(), EPSILON);
    }

    @Test
    void squaringKeepsDirection() {
        Translation2d backward = RobotContainer.getDriveVelocity(-0.6, 0.0);
        assertTrue(backward.getX() < 0.0, "backward stick should drive backward");

        Translation2d diagonal = RobotContainer.getDriveVelocity(-0.5, 0.5);
        assertEquals(135.0, diagonal.getAngle().getDegrees(), 1e-6);
    }

    @Test
    void fullDiagonalIsNotFasterThanFullForward() {
        Translation2d v = RobotContainer.getDriveVelocity(1.0, 1.0);

        assertEquals(1.0, v.getNorm(), EPSILON);
    }
}
