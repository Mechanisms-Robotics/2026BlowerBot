package frc.robot.subsystems.blowers;

import org.littletonrobotics.junction.AutoLog;

import com.revrobotics.spark.SparkMax;

public interface BlowersIO {
    @AutoLog
    public static class BlowersIOInputs {
        public boolean blowerConnected = false;
        public double blowerAppliedVolts = 0.0;
        public double blowerCurrentAmps = 0.0;
    }

    public default void updateInputs(BlowersIOInputs inputs) {}

    public default void blowerOn(BlowersIOInputs inputs) {}

    public default void blowerOff(BlowersIOInputs inputs) {}
}
