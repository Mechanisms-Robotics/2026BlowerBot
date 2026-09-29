package frc.robot.subsystems.blowers;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import frc.robot.CONSTANTS;

public class BlowersIOSparkMax implements BlowersIO {

    private final SparkMax blower1;
    private final SparkMax blower2;
    private final SparkMax blower3;
    private final SparkMax blower4;


    public BlowersIOSparkMax() {
        blower1 = new SparkMax(CONSTANTS.BlowerConstants.BLOWER1_CAN_ID, MotorType.kBrushed);
        blower2 = new SparkMax(CONSTANTS.BlowerConstants.BLOWER2_CAN_ID, MotorType.kBrushed);
        blower3 = new SparkMax(CONSTANTS.BlowerConstants.BLOWER3_CAN_ID, MotorType.kBrushed); // Example CAN ID for blower 3
        blower4 = new SparkMax(CONSTANTS.BlowerConstants.BLOWER4_CAN_ID, MotorType.kBrushed); // Example CAN ID for blower 4
    }
    
    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Implementation for updating inputs from Spark Max
        inputs.blower1Connected = true; // Example value
        inputs.blower1AppliedVolts = 12.0; // Example value
        inputs.blower1CurrentAmps = 5.0; // Example value
        
        inputs.blower2Connected = true; // Example value
        inputs.blower2AppliedVolts = 12.0; // Example value
        inputs.blower2CurrentAmps = 5.0; // Example value

        inputs.blower3Connected = true; // Example value
        inputs.blower3AppliedVolts = 12.0; // Example value
        inputs.blower3CurrentAmps = 5.0; // Example value

        inputs.blower4Connected = true; // Example value
        inputs.blower4AppliedVolts = 12.0; // Example value
        inputs.blower4CurrentAmps = 5.0; // Example value
    }

    @Override
    public void blowerOn(BlowersIOInputs inputs) {
        // Implementation for turning blower on
        blower1.getClosedLoopController().setSetpoint(1.0, ControlType.kDutyCycle);
    }

    @Override
    public void blowerOff(BlowersIOInputs inputs) {
        // Implementation for turning blower off
        blower1.getClosedLoopController().setSetpoint(0.0, ControlType.kDutyCycle);
    }
}