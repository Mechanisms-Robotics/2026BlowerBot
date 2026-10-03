package frc.robot.subsystems.blowers;

import org.littletonrobotics.junction.AutoLog;

public interface BlowersIO {
    @AutoLog
    public static class BlowersIOInputs {
        public boolean blowerConnected = false;
        public double blowerAppliedVolts = 0.0;
        public double blowerCurrentAmps = 0.0;
    }

    /** Updates the set of loggable inputs. */
    public default void updateInputs(BlowersIOInputs inputs) {}

    /** Runs the blower at full output. */
    public default void blowerOn() {}

    /** Stops the blower. */
    public default void blowerOff() {}
}
