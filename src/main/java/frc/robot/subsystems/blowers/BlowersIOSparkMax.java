package frc.robot.subsystems.blowers;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import frc.robot.CONSTANTS;

public class BlowersIOSparkMax implements BlowersIO {

    private final SparkMax blower;

       public BlowersIOSparkMax(int canId) {
        blower = new SparkMax(canId, MotorType.kBrushed);
    }
    
    
    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Implementation for updating inputs from Spark Max
        inputs.blowerConnected = true; // Example value
        inputs.blowerAppliedVolts = 12.0; // Example value
        inputs.blowerCurrentAmps = 5.0; // Example value
    }

    @Override
    public void blowerOn(BlowersIOInputs inputs) {
        // Implementation for turning blower on
        blower.getClosedLoopController().setSetpoint(1.0, ControlType.kDutyCycle);
    }

    @Override
    public void blowerOff(BlowersIOInputs inputs) {
        // Implementation for turning blower off
        blower.getClosedLoopController().setSetpoint(0.0, ControlType.kDutyCycle);
    }
}