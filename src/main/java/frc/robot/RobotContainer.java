// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;

import java.util.HashMap;
import java.util.function.Supplier;

import frc.robot.CONSTANTS.DriveConstants;
import frc.robot.CONSTANTS.VisionConstants;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller;
import frc.robot.CONSTANTS.BlowerConstants;
import frc.robot.subsystems.blowers.Blowers;
import frc.robot.subsystems.blowers.BlowersIO;
import frc.robot.subsystems.blowers.BlowersIOSim;
import frc.robot.subsystems.blowers.BlowersIOSparkMax;
import frc.robot.subsystems.drivetrain.Drivetrain;
import frc.robot.subsystems.drivetrain.DrivetrainController;
import frc.robot.subsystems.drivetrain.GyroIO;
import frc.robot.subsystems.drivetrain.GyroIORedux;
import frc.robot.subsystems.drivetrain.ModuleIO;
import frc.robot.subsystems.drivetrain.ModuleIOSim;
import frc.robot.subsystems.drivetrain.ModuleIOTalonFXRedux;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.PoseCameraIOPhoton;
import frc.robot.subsystems.vision.PoseCameraIOSim;

public class RobotContainer {
    public final Drivetrain drivetrain;
    public final Blowers blowers;

    @SuppressWarnings("unused")
    private final Vision vision;
    private final DrivetrainController drivetrainController;
    
    public final SendableChooser<String> autoChooser = new SendableChooser<>();

    private final CommandPS4Controller controller = new CommandPS4Controller(
        CONSTANTS.CONTROLLER_PORT
    );
    
    private final HashMap<String, Supplier<Command>> autos = new HashMap<>();

    public RobotContainer() {
        if (CONSTANTS.CURRENT_MODE == CONSTANTS.SIM_MODE) {
            this.drivetrain = new Drivetrain(
                new GyroIO() {},
                new ModuleIOSim(DriveConstants.FRONT_LEFT),
                new ModuleIOSim(DriveConstants.FRONT_RIGHT),
                new ModuleIOSim(DriveConstants.BACK_LEFT),
                new ModuleIOSim(DriveConstants.BACK_RIGHT)
            );

            this.vision = new Vision(
                this.drivetrain.poseEstimator,
                new PoseCameraIOSim(
                    "Photon_Camera_Sim1",
                    Transform3d.kZero,
                    drivetrain.poseEstimator
                ));

            this.blowers = new Blowers(
                new BlowersIOSim(BlowerConstants.BLOWER1_CAN_ID),
                new BlowersIOSim(BlowerConstants.BLOWER2_CAN_ID),
                new BlowersIOSim(BlowerConstants.BLOWER3_CAN_ID),
                new BlowersIOSim(BlowerConstants.BLOWER4_CAN_ID),
                new BlowersIOSim(BlowerConstants.BLOWER5_CAN_ID)
            );
        } else if (CONSTANTS.BENCH_TEST_MODE) {
            // Bench test: only Blower1 touches real CAN hardware. Everything else
            // gets no-op IO so a bare SparkMax on the bench doesn't throw CAN errors
            // for swerve modules, the gyro, or the other blowers that aren't present.
            this.drivetrain = new Drivetrain(
                new GyroIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {}
            );

            this.vision = new Vision(this.drivetrain.poseEstimator);

            this.blowers = new Blowers(
                new BlowersIOSparkMax(BlowerConstants.BLOWER1_CAN_ID),
                new BlowersIO() {},
                new BlowersIO() {},
                new BlowersIO() {},
                new BlowersIO() {}
            );
        } else {
            this.drivetrain = new Drivetrain(
                new GyroIORedux(),
                new ModuleIOTalonFXRedux(DriveConstants.FRONT_LEFT),
                new ModuleIOTalonFXRedux(DriveConstants.FRONT_RIGHT),
                new ModuleIOTalonFXRedux(DriveConstants.BACK_LEFT),
                new ModuleIOTalonFXRedux(DriveConstants.BACK_RIGHT)

            );

            this.vision = new Vision(
                this.drivetrain.poseEstimator,
                new PoseCameraIOPhoton(VisionConstants.CAMERA1_NAME, VisionConstants.CAMERA1_TRANSFORM3D),
                new PoseCameraIOPhoton(VisionConstants.CAMERA2_NAME, VisionConstants.CAMERA2_TRANSFORM3D)
            );

            this.blowers = new Blowers(
                new BlowersIOSparkMax(BlowerConstants.BLOWER1_CAN_ID),
                new BlowersIOSparkMax(BlowerConstants.BLOWER2_CAN_ID),
                new BlowersIOSparkMax(BlowerConstants.BLOWER3_CAN_ID),
                new BlowersIOSparkMax(BlowerConstants.BLOWER4_CAN_ID),
                new BlowersIOSparkMax(BlowerConstants.BLOWER5_CAN_ID)
            );
        }

        this.drivetrainController = new DrivetrainController(this.drivetrain);

        configureBindings();
        SmartDashboard.putData("CommandScheduler", CommandScheduler.getInstance());
    }

    private void configureBindings() {
        this.controller
            .cross()
            .onTrue(
                new InstantCommand(() -> {
                    this.drivetrain.resetHeading();
                })
            );

        this.controller.L1().onTrue(this.blowers.commandBlowerOn(0))
            .onFalse(this.blowers.commandBlowerOff(0));
        this.controller.L2().onTrue(this.blowers.commandBlowerOn(1))
            .onFalse(this.blowers.commandBlowerOff(1));
        this.controller.R1().onTrue(this.blowers.commandBlowerOn(2))
            .onFalse(this.blowers.commandBlowerOff(2));
        this.controller.R2().onTrue(this.blowers.commandBlowerOn(3))
            .onFalse(this.blowers.commandBlowerOff(3));

        this.controller.circle().onTrue(this.blowers.commandAllOn());
        this.controller.square().onTrue(this.blowers.commandAllOff());

        this.drivetrain.setDefaultCommand(
            new RunCommand(
                () -> {
                    double forward = -this.controller.getLeftY(); // Negative to match FRC convention
                    double strafe = -this.controller.getLeftX();
                    Translation2d driveSpeeds = getDriveVelocity(
                        forward,
                        strafe
                    );
                    
                    double rotation = -this.controller.getRightX();

                    // apply deadbands and scaling
                    rotation = MathUtil.applyDeadband(
                        rotation,
                        CONSTANTS.DriveConstants.DEADBAND
                    );

                    rotation = Math.copySign(rotation * rotation, rotation);

                    ChassisSpeeds speeds = new ChassisSpeeds(
                        driveSpeeds.getX() *
                            CONSTANTS.DriveConstants.SPEED_AT_12_VOLTS.in(
                                MetersPerSecond
                            ),
                        driveSpeeds.getY() *
                            CONSTANTS.DriveConstants.SPEED_AT_12_VOLTS.in(
                                MetersPerSecond
                            ),
                        (rotation *
                                (CONSTANTS.DriveConstants.SPEED_AT_12_VOLTS.in(
                                        MetersPerSecond
                                    ))) /
                            CONSTANTS.DriveConstants.DRIVE_BASE_RADIUS
                    );

                    // convert to robot-oriented coordinates and pass to swerve subsystem
                    ChassisSpeeds robotOriented =
                        this.drivetrainController.fieldToRobotChassisSpeeds(
                            speeds
                        );
                    this.drivetrain.setDesiredState(robotOriented);
                },
                this.drivetrain
            )
        );
    }

    private void publishAutoNames() {
        // add commands to the autos hashmap here
        autos.put("None", () -> Commands.none());
        

        for (String name : autos.keySet()) {
            autoChooser.addOption(name, name);
        }

        autoChooser.setDefaultOption("None", "None");
        
        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    public Command getAutonomousCommand(String name) {
        Command autoCommand = this.autos.getOrDefault(name, () -> Commands.none()).get();

        autoCommand.setName(name);
        return autoCommand;
    }

    private static Translation2d getDriveVelocity(double x, double y) {
        double linearMag = MathUtil.applyDeadband(
            Math.hypot(x, y),
            DriveConstants.DEADBAND
        );
        Rotation2d direction = new Rotation2d(Math.atan2(y, x));
        linearMag = linearMag * linearMag;
        return new Pose2d(Translation2d.kZero, direction)
            .transformBy(new Transform2d(linearMag, 0.0, Rotation2d.kZero))
            .getTranslation();
    }
}
