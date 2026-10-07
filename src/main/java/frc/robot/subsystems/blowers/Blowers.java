package frc.robot.subsystems.blowers;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.CONSTANTS;
import frc.robot.subsystems.blowers.BlowersIO.BlowersIOInputs;

public class Blowers extends SubsystemBase {
    private final BlowersIO[] ios;
    private final BlowersIOInputsAutoLogged[] inputs;

    public Blowers(BlowersIO blower1, BlowersIO blower2, BlowersIO blower3, BlowersIO blower4, BlowersIO blower5) {
        this.ios = new BlowersIO[] {blower1, blower2, blower3, blower4, blower5};
        this.inputs = new BlowersIOInputsAutoLogged[ios.length];
        for (int i = 0; i < ios.length; i++) {
            this.inputs[i] = new BlowersIOInputsAutoLogged();
        }
    }

    @Override
    public void periodic() {
        for (int i = 0; i < ios.length; i++) {
            ios[i].updateInputs(this.inputs[i]);
            Logger.processInputs("Blowers/"+i, inputs[i]);
        }
    }

    private void blowerOn(int index) {
        ios[index].blowerOn();
    }

    private void blowerOff(int index) {
        ios[index].blowerOff();
    }

    private void allOn() {
        for (BlowersIO io : ios) {
            io.blowerOn();
        }
    }

    private void allOff() {
        for (BlowersIO io : ios) {
            io.blowerOff();
        }
    }

    public Command commandBlowerOn(int index) {
        return runOnce(() -> blowerOn(index));
    }

    public Command commandBlowerOff(int index) {
        return runOnce(() -> blowerOff(index));
    }

    public Command commandAllOn() {
        return runOnce(() -> allOn());
    }

    public Command commandAllOff() {
        return runOnce(() -> allOff());
    }
}
