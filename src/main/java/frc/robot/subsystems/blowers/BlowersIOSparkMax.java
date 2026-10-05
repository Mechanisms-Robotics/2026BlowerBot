package frc.robot.subsystems.blowers;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class BlowersIOSparkMax implements BlowersIO {

    private final SparkMax blower;

    public BlowersIOSparkMax(int blowerId) {
        blower = new SparkMax(blowerId, MotorType.kBrushed);
    }
    
    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Implementation for updating inputs from Spark Max
        inputs.blowerConnected = !blower.hasActiveFault();
        inputs.blowerAppliedVolts = blower.getAppliedOutput() * blower.getBusVoltage();
        inputs.blowerCurrentAmps = blower.getOutputCurrent();
    }

    @Override
    public void blowerOn() {
        // Implementation for turning blower on
        blower.getClosedLoopController().setSetpoint(1.0, ControlType.kDutyCycle);
    }

    @Override
    public void blowerOff() {
        // Implementation for turning blower off
        blower.getClosedLoopController().setSetpoint(0.0, ControlType.kDutyCycle);
    }
}