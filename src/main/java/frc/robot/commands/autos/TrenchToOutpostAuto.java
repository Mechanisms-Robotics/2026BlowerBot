package frc.robot.commands.autos;

import java.util.Optional;

import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.FollowPath;
import frc.robot.subsystems.drivetrain.Drivetrain;

public class TrenchToOutpostAuto extends ParallelCommandGroup {
    public TrenchToOutpostAuto(Drivetrain drivetrain) {       // add blower here

        Optional<Trajectory<SwerveSample>> trenchToOutpost = Choreo.loadTrajectory("TrenchToOutpost");

        // add blower commands here, keep in mind this is a ParallelCommandGroup by default
        addCommands(
            new FollowPath(
                trenchToOutpost.get(), 
                drivetrain, 
                true
            )   
        );
    }
}
