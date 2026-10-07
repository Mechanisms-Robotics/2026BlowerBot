package frc.robot.subsystems.blowers;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.CONSTANTS;

public class BlowersIOSim implements BlowersIO {

    private SparkMax blowerMotor;
    private SparkMaxSim blowerSim;
    private boolean on = false;

    public BlowersIOSim(int blowerId) {
        blowerMotor = new SparkMax(blowerId, MotorType.kBrushed);
        blowerSim = new SparkMaxSim(blowerMotor, DCMotor.getNEO(1));
    }

    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        double vbus = RobotController.getBatteryVoltage();

        // Update the simulation
        blowerSim.iterate(on ? CONSTANTS.BlowerConstants.BLOWER_MAX_RPM : 0.0, vbus, CONSTANTS.ROBOT_LOOP_PERIOD);

        // Read values from simulation
        inputs.blowerConnected = true;
        inputs.blowerAppliedVolts = blowerSim.getAppliedOutput() * vbus;
        inputs.blowerCurrentAmps = blowerSim.getMotorCurrent();
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