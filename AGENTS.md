# AGENTS.md

Guidance for AI coding agents (Claude Code, Codex, Copilot, Cursor, Gemini, etc.) working in this repository.

FRC Team 8736 (The Mechanisms) 2026 robot code. Java 17, WPILib GradleRIO 2026, command-based, AdvantageKit logging. Swerve drivetrain (CTRE Phoenix 6 TalonFX + Redux Canandgyro/encoders) and PhotonVision pose cameras.

## Before every commit: update STUDENTS.md

**Required:** Before committing, check whether your change affects anything described in `STUDENTS.md`, the student guide to this codebase. That includes subsystems, commands, hardware, IO interfaces, the control flow, constants, auto routines, build/sim steps, and the "Starter challenges" (e.g. a bug listed there that you just fixed). If it does, update `STUDENTS.md` in the same commit, keeping its high-school-level tone. If nothing in it is affected, leave it alone.

## Commands

Use the Gradle wrapper from the repo root (WPILib VS Code "Build Robot Code" / "Simulate Robot Code" run the same tasks):

```bash
./gradlew build            # compile + run tests
./gradlew simulateJava     # run desktop simulation (sim GUI + driver station enabled by default)
./gradlew deploy           # deploy to the roboRIO (team number from .wpilib/wpilib_preferences.json)
./gradlew test             # JUnit 5 tests (src/test/java — none exist yet)
./gradlew test --tests 'frc.robot.SomeTest'   # single test class
./gradlew replayWatch      # AdvantageKit replay watcher
```

There is no linter/formatter configured.

## Architecture

**Entry point.** `Main` → `Robot` (extends AdvantageKit `LoggedRobot`) → `RobotContainer`. `Robot` configures AdvantageKit data receivers per `CONSTANTS.CURRENT_MODE` (REAL/SIM → NT4; REPLAY → reads a WPILOG and writes a `_sim` log), then starts the logger *before* constructing `RobotContainer`. Anything logged must be set up after `Logger.start()`.

**Hardware abstraction (AdvantageKit IO pattern).** Each subsystem takes IO interfaces rather than owning hardware directly:
- `XxxIO` interface with an `@AutoLog`-annotated `XxxIOInputs` class and default no-op methods. The annotation processor generates `XxxIOInputsAutoLogged` at build time — those classes won't exist until you build.
- Implementations: real hardware (`ModuleIOTalonFXRedux`, `GyroIORedux`, `PoseCameraIOPhoton`), sim (`ModuleIOSim`, `PoseCameraIOSim`), and an anonymous `new GyroIO() {}` for no-op/replay.
- `RobotContainer` picks implementations based on `CONSTANTS.CURRENT_MODE`. When adding a mechanism, follow this pattern so it works in sim and replay.
- Subsystems call `io.updateInputs(inputs)` then `Logger.processInputs(...)` in `periodic()`, and read only from `inputs`, so replay is deterministic.

**Mode selection.** `CONSTANTS.CURRENT_MODE` is `REAL` on the robot, otherwise `SIM_MODE`. To replay a log, change `SIM_MODE` to `Mode.REPLAY`.

**Drivetrain / odometry.** `Drivetrain` owns four `SwerveModule`s (each wrapping a `ModuleIO`), a `GyroIO`, and a public `PoseEstimator8736` (wraps WPILib `SwerveDrivePoseEstimator`, plus a separate sim-only estimator). High-frequency odometry comes from the singleton `PhoenixOdometryThread`, which samples Phoenix signals into queues that the module IOs register with. `Drivetrain.odometryLock` must be held while draining those queues. `ModuleIOTalonFX`/`GyroIOCTRE` are the CTRE-only alternatives to the Redux variants.

**Vision.** `Vision` takes the drivetrain's `PoseEstimator8736` and any number of `PoseCameraIO`s, and feeds accepted measurements back via `addVisionMeasurement`.

**Driving and autos.** The default drive command in `RobotContainer.configureBindings()` builds field-relative `ChassisSpeeds` from a PS4 controller (squared inputs, deadband) and converts them to robot-relative ones through `DrivetrainController`. Autos are `Supplier<Command>` entries in `RobotContainer.autos`, keyed by name and exposed through `autoChooser`. `Robot.disabledPeriodic` rebuilds the selected auto whenever the chooser changes. `FollowPath` follows Choreo `Trajectory<SwerveSample>`s with a `HolonomicDriveController` and handles alliance flipping via `FieldUtil`.

**Coordinate conventions.** WPILib standard: +X forward, +Y left, CCW-positive rotation. Units via `edu.wpi.first.units` in constants.

## Constants

`src/main/java/frc/robot/CONSTANTS.java` is the single constants file, organized into nested static classes (`FieldConstants`, `VisionConstants`, `DriveConstants`, …). Per-robot templates live in `src/config/constants/*.java.template`. `build.gradle` has a commented-out `prepareConstants` task that would copy the template chosen by `-PtargetRobot=` / `gradle.properties` over `CONSTANTS.java`. That task is currently disabled, so edit `CONSTANTS.java` directly. If it's re-enabled, edit the template instead, because the build would overwrite `CONSTANTS.java` and fail if it had drifted.

## Git workflow (from README)

- Never push directly to `main`.
- Branch from the integration branch `develop` as `feature/...` or `fix/...`, and open a PR back to it after a successful simulation run.
- Competition: `COMP-*` branches cut from `main`; pit fixes go on `fix/...` branches merged into the COMP branch, and each match is tagged (e.g. `git tag -a Qual-1 -m "..."`).

## Other files

- `vendordeps/*.json`: vendor libraries (Phoenix 6, REVLib, ReduxLib, PhotonLib, ChoreoLib, AdvantageKit). Update these through the WPILib vendordep tooling, not by hand-editing.
- `src/main/deploy/`: files copied to `/home/lvuser/deploy` on the roboRIO (Choreo trajectories go here).
- `ascope_assets/`: AdvantageScope robot model/config.
- `STUDENTS.md`: a high-school-level walkthrough of the architecture for new team members.
