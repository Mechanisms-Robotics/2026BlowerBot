package frc.robot.subsystems.drivetrain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.AllianceStationID;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Field-relative driving: "stick forward" must always move the robot away from the driver. */
class DrivetrainControllerTest {
    private static final double EPSILON = 1e-6;

    private Drivetrain drivetrain;
    private DrivetrainController controller;

    @BeforeAll
    static void initHal() {
        HAL.initialize(500, 0);
    }

    @BeforeEach
    void setup() {
        this.drivetrain = new Drivetrain(
            new GyroIO() {},
            new FakeModuleIO(),
            new FakeModuleIO(),
            new FakeModuleIO(),
            new FakeModuleIO()
        );
        this.controller = new DrivetrainController(this.drivetrain);
    }

    @AfterEach
    void resetAlliance() {
        setAlliance(AllianceStationID.Unknown);
    }

    private static void setAlliance(AllianceStationID station) {
        DriverStationSim.setAllianceStationId(station);
        DriverStationSim.notifyNewData();
        DriverStation.refreshData();
    }

    private ChassisSpeeds convert(double headingDegrees, ChassisSpeeds fieldSpeeds) {
        this.drivetrain.resetPose(new Pose2d(1.0, 1.0, Rotation2d.fromDegrees(headingDegrees)));
        return this.controller.fieldToRobotChassisSpeeds(fieldSpeeds);
    }

    private static void assertSpeeds(double vx, double vy, double omega, ChassisSpeeds actual) {
        assertEquals(vx, actual.vxMetersPerSecond, EPSILON, "vx");
        assertEquals(vy, actual.vyMetersPerSecond, EPSILON, "vy");
        assertEquals(omega, actual.omegaRadiansPerSecond, EPSILON, "omega");
    }

    @Test
    void blueRobotFacingAwayDrivesStraightForward() {
        setAlliance(AllianceStationID.Blue1);
        assertSpeeds(1.0, 0.0, 0.0, convert(0, new ChassisSpeeds(1.0, 0.0, 0.0)));
    }

    @Test
    void blueRobotFacingLeftDrivesToItsRight() {
        setAlliance(AllianceStationID.Blue1);
        assertSpeeds(0.0, -1.0, 0.0, convert(90, new ChassisSpeeds(1.0, 0.0, 0.0)));
    }

    @Test
    void blueRobotFacingDriverDrivesBackward() {
        setAlliance(AllianceStationID.Blue1);
        assertSpeeds(-1.0, 0.0, 0.0, convert(180, new ChassisSpeeds(1.0, 0.0, 0.0)));
    }

    @Test
    void redRobotFacingAwayFromRedDriverDrivesStraightForward() {
        // The red driver stands at the +X end of the field, so "away" is heading 180 deg
        setAlliance(AllianceStationID.Red1);
        assertSpeeds(1.0, 0.0, 0.0, convert(180, new ChassisSpeeds(1.0, 0.0, 0.0)));
    }

    @Test
    void redRobotFacingRedDriverDrivesBackward() {
        setAlliance(AllianceStationID.Red1);
        assertSpeeds(-1.0, 0.0, 0.0, convert(0, new ChassisSpeeds(1.0, 0.0, 0.0)));
    }

    @Test
    void rotationIsNotChangedByFieldConversion() {
        setAlliance(AllianceStationID.Red1);
        assertSpeeds(0.0, 0.0, 2.0, convert(37, new ChassisSpeeds(0.0, 0.0, 2.0)));
    }
}
