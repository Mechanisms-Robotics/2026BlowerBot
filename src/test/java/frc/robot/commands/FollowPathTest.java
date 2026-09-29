package frc.robot.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.hal.AllianceStationID;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj.simulation.SimHooks;
import frc.robot.CONSTANTS.FieldConstants;
import frc.robot.subsystems.drivetrain.Drivetrain;
import frc.robot.subsystems.drivetrain.FakeModuleIO;
import frc.robot.subsystems.drivetrain.GyroIO;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FollowPathTest {
    private static final double EPSILON = 1e-6;
    private static final double START_Y = 1.0;

    private Drivetrain drivetrain;

    @BeforeAll
    static void initHal() {
        HAL.initialize(500, 0);
    }

    @BeforeEach
    void setup() {
        SimHooks.pauseTiming();
        DriverStationSim.setAllianceStationId(AllianceStationID.Blue1);
        DriverStationSim.notifyNewData();
        DriverStation.refreshData();

        this.drivetrain = new Drivetrain(
            new GyroIO() {},
            new FakeModuleIO(),
            new FakeModuleIO(),
            new FakeModuleIO(),
            new FakeModuleIO()
        );
    }

    @AfterEach
    void teardown() {
        SimHooks.resumeTiming();
        DriverStationSim.setAllianceStationId(AllianceStationID.Unknown);
        DriverStationSim.notifyNewData();
        DriverStation.refreshData();
    }

    /** A 2 second path driving straight along +X at 1 m/s from (1, 1) to (3, 1). */
    private static Trajectory<SwerveSample> straightPath() {
        double[] zeros = new double[4];
        List<SwerveSample> samples = List.of(
            new SwerveSample(0.0, 1.0, START_Y, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, zeros, zeros),
            new SwerveSample(1.0, 2.0, START_Y, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, zeros, zeros),
            new SwerveSample(2.0, 3.0, START_Y, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, zeros, zeros)
        );
        return new Trajectory<>("straight", samples, List.of(), List.of());
    }

    @Test
    void resetPoseMovesRobotToPathStart() {
        FollowPath command = new FollowPath(straightPath(), this.drivetrain, true);
        command.initialize();

        Pose2d pose = this.drivetrain.getPose();
        assertEquals(1.0, pose.getX(), EPSILON);
        assertEquals(START_Y, pose.getY(), EPSILON);
    }

    @Test
    void mirroredPathStartsOnOtherSideOfField() {
        FollowPath command = new FollowPath(straightPath(), this.drivetrain, true, true);
        command.initialize();

        Pose2d pose = this.drivetrain.getPose();
        assertEquals(1.0, pose.getX(), EPSILON);
        assertEquals(FieldConstants.WIDTH - START_Y, pose.getY(), EPSILON);
    }

    @Test
    void robotOnPathIsDrivenAtPathVelocity() {
        FollowPath command = new FollowPath(straightPath(), this.drivetrain, true);
        command.initialize();
        command.execute();

        ChassisSpeeds speeds = this.drivetrain.getDesiredState();
        assertEquals(1.0, speeds.vxMetersPerSecond, EPSILON);
        assertEquals(0.0, speeds.vyMetersPerSecond, EPSILON);
        assertEquals(0.0, speeds.omegaRadiansPerSecond, EPSILON);
    }

    @Test
    void robotBehindPathIsDrivenFasterToCatchUp() {
        FollowPath command = new FollowPath(straightPath(), this.drivetrain, false);
        this.drivetrain.resetPose(new Pose2d(0.5, START_Y, this.drivetrain.getPose().getRotation()));
        command.initialize();
        command.execute();

        assertTrue(this.drivetrain.getDesiredState().vxMetersPerSecond > 1.0);
    }

    @Test
    void finishesAtEndOfPath() {
        FollowPath command = new FollowPath(straightPath(), this.drivetrain, true);
        command.initialize();

        SimHooks.stepTiming(1.9);
        assertFalse(command.isFinished());

        SimHooks.stepTiming(0.2);
        assertTrue(command.isFinished());
    }

    @Test
    void stopsRobotWhenDone() {
        FollowPath command = new FollowPath(straightPath(), this.drivetrain, true);
        command.initialize();
        command.execute();
        command.end(false);

        ChassisSpeeds speeds = this.drivetrain.getDesiredState();
        assertEquals(0.0, speeds.vxMetersPerSecond, EPSILON);
        assertEquals(0.0, speeds.vyMetersPerSecond, EPSILON);
        assertEquals(0.0, speeds.omegaRadiansPerSecond, EPSILON);
    }
}
