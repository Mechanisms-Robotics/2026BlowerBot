package frc.robot.subsystems.blowers;

import com.ctre.phoenix6.sim.TalonFXSimState.MotorType;
import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.spark.SparkMax;

import frc.robot.CONSTANTS;

public class BlowersIOSim implements BlowersIO {

    private double blowerOutput = 0.0;
    private SparkMaxSim blowerSim;

    public BlowersIOSim() {
        blowerSim = new SparkMaxSim(CONSTANTS.BlowerConstants.BLOWER_CAN_ID, MotorType.kBrushed);
    }

    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Simulate blower 1
        inputs.blowerConnected = true;
        inputs.blowerAppliedVolts = 12.0;
        inputs.blowerCurrentAmps = 5.0;
    }

    @Override
    public void blowerOn(BlowersIOInputs inputs) {
        // Implementation for turning blower on
        blowerOutput = 1.0; // Simulate blower being on
    }

    @Override
    public void blowerOff(BlowersIOInputs inputs) {
        // Implementation for turning blower off
        blowerOutput = 0.0; // Simulate blower being off
    }
}