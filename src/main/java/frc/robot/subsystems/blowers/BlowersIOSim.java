package frc.robot.subsystems.blowers;

import com.revrobotics.spark.SparkMax;

import com.revrobotics.sim.SparkMaxSim;  
import com.revrobotics.spark.SparkBase.ControlType; 
import com.revrobotics.spark.SparkLowLevel.MotorType; 

import edu.wpi.first.math.system.plant.DCMotor; 
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.RobotController; 
import edu.wpi.first.wpilibj.simulation.FlywheelSim; 



public class BlowersIOSim implements BlowersIO {

    // How often robot code runs (0.02 sec)
    private static final double LOOP_PERIOD_SECS = 0.02;

     // The kind of motor we are pretending to have (a NEO motor)
    private final DCMotor motorModel = DCMotor.getNEO(1);

    // A normal SparkMax, exactly like the real robot uses
    private final SparkMax blower;

    // REV's fake SparkMax that clips onto the one above
    private final SparkMaxSim blowerSim;

    // Physics of a spinning fan 
    private final FlywheelSim fanSim = new FlywheelSim(
        LinearSystemId.createFlywheelSystem(motorModel, 0.0005, 1.0),
        motorModel);

    // Constructor: runs ONE time when a blower is created
    // takes the CAN ID and builds SParkMax of that ID 
    // and clips a SparkMaxSim, on it
    public BlowersIOSim(int canId) {
        blower = new SparkMax(canId, MotorType.kBrushless);
        blowerSim = new SparkMaxSim(blower, motorModel);
    }


    @Override
    public void updateInputs(BlowersIOInputs inputs) {
        // Simulate blower 1
        // 1. Turn the SparkMax's % power into volts and push the fan physics forward
        fanSim.setInputVoltage(blowerSim.getAppliedOutput() * RobotController.getBatteryVoltage());
        fanSim.update(LOOP_PERIOD_SECS);

        // 2. Tell the fake SparkMax how fast the fan is spinning so it can work out current
        blowerSim.iterate(
        fanSim.getAngularVelocityRPM(),
        RobotController.getBatteryVoltage(),
        LOOP_PERIOD_SECS);

        inputs.blowerConnected = true;
        inputs.blowerAppliedVolts = blowerSim.getAppliedOutput() * blowerSim.getBusVoltage();
        inputs.blowerCurrentAmps = blowerSim.getMotorCurrent();

    }

    @Override
    public void blowerOn(BlowersIOInputs inputs) {
        // Implementation for turning blower on
        // Same command as the real robot: run at 100% (1.0)
        blower.getClosedLoopController().setSetpoint(1.0, ControlType.kDutyCycle);

    }

    @Override
    public void blowerOff(BlowersIOInputs inputs) {
        // Implementation for turning blower off
        // Same command as the real robot: run at 0% (off)
        blower.getClosedLoopController().setSetpoint(0.0, ControlType.kDutyCycle);

    }
}