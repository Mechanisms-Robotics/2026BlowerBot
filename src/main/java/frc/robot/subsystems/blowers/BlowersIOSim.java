package frc.robot.subsystems.blowers;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;

import edu.wpi.first.math.system.plant.DCMotor;
import frc.robot.CONSTANTS;

public class BlowersIOSim implements BlowersIO {

    private double blowerVelocity = 0.0;
    private double blowerPosition = 0.0;
    private static final double MAX_RPM = 5676.0; // NEO max RPM
    private SparkMax blowerMotor;
    private SparkMaxSim blowerSim;
    private boolean on = false;

    public BlowersIOSim(int blowerId) {
        blowerMotor = new SparkMax(blowerId, MotorType.kBrushed);
        blowerSim = new SparkMaxSim(blowerMotor, DCMotor.getNEO(1));
    }

    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Update the simulation
        blowerSim.iterate(on ? CONSTANTS.BlowerConstants.BLOWER_MAX_RPM : 0.0, 12.0, 0.02);
        
        // Read values from simulation
        inputs.blowerConnected = true;
        inputs.blowerAppliedVolts = blowerSim.getAppliedOutput() * 12.0;
        inputs.blowerCurrentAmps = blowerSim.getMotorCurrent();
        
        // Manually calculate velocity based on applied output
        // double targetVelocity = blowerSim.getAppliedOutput() * MAX_RPM;
        // blowerVelocity += (targetVelocity - blowerVelocity) * 0.1; // Simple ramp-up
        // blowerPosition += blowerVelocity * 0.02 / 60.0; // Convert RPM to rotations per 20ms
        
        // inputs.blowerVelocityRPM = blowerVelocity;
        // inputs.blowerPositionRots = blowerPosition;
    }

    @Override
    public void blowerOn() {
        blowerMotor.getClosedLoopController().setSetpoint(1.0, ControlType.kDutyCycle);
        on = true;
    }

    @Override
    public void blowerOff() {
        blowerMotor.getClosedLoopController().setSetpoint(0.0, ControlType.kDutyCycle);
        on = false;
    }
}