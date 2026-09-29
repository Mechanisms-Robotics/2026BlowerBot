package frc.robot.subsystems.blowers;

public class BlowersIOSim implements BlowersIO {

    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Simulate blower 1
        inputs.blower1Connected = true;
        inputs.blower1AppliedVolts = 12.0;
        inputs.blower1CurrentAmps = 5.0;

        // Simulate blower 2
        inputs.blower2Connected = true;
        inputs.blower2AppliedVolts = 12.0;
        inputs.blower2CurrentAmps = 5.0;
    }

    @Override
    public void blowerOn(BlowersIOInputs inputs) {
        // Implementation for turning blower on
    }

    @Override
    public void blowerOff(BlowersIOInputs inputs) {
        // Implementation for turning blower off
    }
}