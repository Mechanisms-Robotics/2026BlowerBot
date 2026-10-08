package frc.robot.commands.autos;

import java.util.Optional;

import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.FollowPath;
import frc.robot.subsystems.drivetrain.Drivetrain;

public class TestAuto extends ParallelCommandGroup {
    public TestAuto(Drivetrain drivetrain) {       // add blower here

        Optional<Trajectory<SwerveSample>> path1 = Choreo.loadTrajectory("path1");

        // add blower commands here, keep in mind this is a ParallelCommandGroup by default
        addCommands(
            new FollowPath(
                path1.get(), 
                drivetrain, 
                true
            )   
        );
    }
}
