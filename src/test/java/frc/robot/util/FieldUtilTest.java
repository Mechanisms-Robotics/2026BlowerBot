package frc.robot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.CONSTANTS.FieldConstants;
import org.junit.jupiter.api.Test;

class FieldUtilTest {
    private static final double EPSILON = 1e-9;

    @Test
    void flippingTwiceReturnsOriginalPose() {
        Pose2d pose = new Pose2d(2.3, 1.1, Rotation2d.fromDegrees(35));

        Pose2d twice = FieldUtil.flipPose(FieldUtil.flipPose(pose));

        assertEquals(pose.getX(), twice.getX(), EPSILON);
        assertEquals(pose.getY(), twice.getY(), EPSILON);
        assertEquals(pose.getRotation().getDegrees(), twice.getRotation().getDegrees(), EPSILON);
    }

    @Test
    void flipMirrorsAcrossFieldWidth() {
        Pose2d flipped = FieldUtil.flipPose(new Pose2d(2.0, 1.0, Rotation2d.fromDegrees(30)));

        assertEquals(2.0, flipped.getX(), EPSILON);
        assertEquals(FieldConstants.WIDTH - 1.0, flipped.getY(), EPSILON);
        assertEquals(-30.0, flipped.getRotation().getDegrees(), EPSILON);
    }

    @Test
    void poseOnCenterLineDoesNotMove() {
        Pose2d center = new Pose2d(4.0, FieldConstants.WIDTH / 2.0, Rotation2d.kZero);

        assertEquals(center, FieldUtil.flipPose(center));
    }
}
