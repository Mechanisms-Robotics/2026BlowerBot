package frc.robot.subsystems.blowers;

import org.littletonrobotics.junction.AutoLog;

public interface BlowersIO {
    @AutoLog
    public static class BlowersIOInputs {
        public boolean blower1Connected = false;
        public double blower1AppliedVolts = 0.0;
        public double blower1CurrentAmps = 0.0;

        public boolean blower2Connected = false;
        public double blower2AppliedVolts = 0.0;
        public double blower2CurrentAmps = 0.0;

        public boolean blower3Connected = false;
        public double blower3AppliedVolts = 0.0;
        public double blower3CurrentAmps = 0.0;

        public boolean blower4Connected = false;
        public double blower4AppliedVolts = 0.0;
        public double blower4CurrentAmps = 0.0;
    }

    public default void updateInputs(BlowersIOInputs inputs) {}

    public default void blowerOn(BlowersIOInputs inputs) {}

    public default void blowerOff(BlowersIOInputs inputs) {}
}
