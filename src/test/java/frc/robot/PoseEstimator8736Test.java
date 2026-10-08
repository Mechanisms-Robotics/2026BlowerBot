package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.CONSTANTS.DriveConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoseEstimator8736Test {
    private static final double EPSILON = 1e-6;

    private SwerveDriveKinematics kinematics;
    private PoseEstimator8736 estimator;

    @BeforeEach
    void setup() {
        this.kinematics = new SwerveDriveKinematics(
            new Translation2d(DriveConstants.FRONT_LEFT.LocationX, DriveConstants.FRONT_LEFT.LocationY),
            new Translation2d(DriveConstants.FRONT_RIGHT.LocationX, DriveConstants.FRONT_RIGHT.LocationY),
            new Translation2d(DriveConstants.BACK_LEFT.LocationX, DriveConstants.BACK_LEFT.LocationY),
            new Translation2d(DriveConstants.BACK_RIGHT.LocationX, DriveConstants.BACK_RIGHT.LocationY)
        );
        this.estimator = new PoseEstimator8736(this.kinematics, Rotation2d.kZero, Pose2d.kZero);
    }

    private static SwerveModulePosition[] allWheels(double meters, Rotation2d angle) {
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for (int i = 0; i < 4; i++) {
            positions[i] = new SwerveModulePosition(meters, angle);
        }
        return positions;
    }

    @Test
    void rollingAllWheelsForwardMovesRobotForward() {
        this.estimator.updateOdometry(allWheels(1.0, Rotation2d.kZero), Rotation2d.kZero, 1.0);

        Pose2d pose = this.estimator.getEstimatedPose();
        assertEquals(1.0, pose.getX(), EPSILON);
        assertEquals(0.0, pose.getY(), EPSILON);
    }

    @Test
    void headingComesFromGyroWhenConnected() {
        this.estimator.updateOdometry(allWheels(0.0, Rotation2d.kZero), Rotation2d.fromDegrees(90), 1.0);

        assertEquals(90.0, this.estimator.getEstimatedPose().getRotation().getDegrees(), EPSILON);
    }

    @Test
    void headingComesFromWheelsWhenGyroIsDisconnected() {
        // Wheel motion for spinning 0.1 rad in place
        SwerveModuleState[] states = this.kinematics.toSwerveModuleStates(new ChassisSpeeds(0.0, 0.0, 0.1));
        SwerveModulePosition[] positions = new SwerveModulePosition[4];
        for (int i = 0; i < 4; i++) {
            positions[i] = new SwerveModulePosition(states[i].speedMetersPerSecond, states[i].angle);
        }

        this.estimator.updateOdometry(positions, null, 1.0);

        assertEquals(0.1, this.estimator.getRawGyroRotation().getRadians(), 1e-4);
    }

    @Test
    void resetPoseMovesEstimateAndOdometryContinuesFromThere() {
        SwerveModulePosition[] start = allWheels(1.0, Rotation2d.kZero);
        this.estimator.updateOdometry(start, Rotation2d.kZero, 1.0);

        this.estimator.resetPose(new Pose2d(5.0, 2.0, Rotation2d.kZero), start);
        assertEquals(new Pose2d(5.0, 2.0, Rotation2d.kZero), this.estimator.getEstimatedPose());

        this.estimator.updateOdometry(allWheels(1.5, Rotation2d.kZero), Rotation2d.kZero, 1.02);
        assertEquals(5.5, this.estimator.getEstimatedPose().getX(), EPSILON);
    }
}
