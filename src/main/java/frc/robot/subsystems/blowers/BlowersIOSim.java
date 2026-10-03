package frc.robot.subsystems.blowers;

import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.CONSTANTS;

public class BlowersIOSim implements BlowersIO {

    private static final double BLOWER_FREE_SPEED_RPM = 5700.0;

    // Placeholder motor model, just for SparkMaxSim's current-draw math; we don't care
    // about the real motor's torque/current curve.
    private static final DCMotor BLOWER_GEARBOX = DCMotor.getNEO(1);

    private final SparkMax blower;
    private final SparkMaxSim blowerSim;
    private boolean on = false;

    public BlowersIOSim(int canId) {
        blower = new SparkMax(canId, MotorType.kBrushed);
        blowerSim = new SparkMaxSim(blower, BLOWER_GEARBOX);
    }

    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        double vbus = RobotController.getBatteryVoltage();

        // No spin-up/spin-down modeled: velocity snaps instantly to full speed or zero
        blowerSim.iterate(
            on ? BLOWER_FREE_SPEED_RPM : 0.0,
            vbus,
            CONSTANTS.ROBOT_LOOP_PERIOD
        );

        inputs.blowerConnected = true;
        inputs.blowerAppliedVolts = blowerSim.getAppliedOutput() * vbus;
        inputs.blowerCurrentAmps = blowerSim.getMotorCurrent();
    }

    @Override
    public void blowerOn() {
        on = true;
        blower.getClosedLoopController().setSetpoint(1.0, ControlType.kDutyCycle);
    }

    @Override
    public void blowerOff() {
        on = false;
        blower.getClosedLoopController().setSetpoint(0.0, ControlType.kDutyCycle);
    }
}
