# Copilot Instructions

The full agent guidance for this repo is in [AGENTS.md](../AGENTS.md) at the repo root; read it first. Key points:

- FRC Team 8736 robot code: Java 17, WPILib 2026 command-based, AdvantageKit logging, CTRE Phoenix 6 + Redux swerve, PhotonVision.
- Build/sim/deploy with `./gradlew build`, `./gradlew simulateJava`, `./gradlew deploy`.
- Hardware goes behind AdvantageKit IO interfaces (`XxxIO` with `@AutoLog` inputs + real/sim implementations chosen in `RobotContainer` by `CONSTANTS.CURRENT_MODE`). `*AutoLogged` classes are generated at build time.
- All tunables live in nested classes in `frc.robot.CONSTANTS`.
- WPILib coordinates: +X forward, +Y left, CCW-positive.
- Never push to `main`; branch from `develop` and PR back after testing in simulation.
- Before committing, make sure `STUDENTS.md` (the student guide) still matches the code, and update it in the same commit if your change affects anything it describes.
