package frc.robot.subsystems.blowers;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Blowers extends SubsystemBase {
    private final BlowersIO[] ios;
    private final BlowersIOInputsAutoLogged[] inputs;

    public Blowers(
        BlowersIO blower1,
        BlowersIO blower2,
        BlowersIO blower3,
        BlowersIO blower4,
        BlowersIO blower5
    ) {
        this.ios = new BlowersIO[] { blower1, blower2, blower3, blower4, blower5 };
        this.inputs = new BlowersIOInputsAutoLogged[ios.length];

        for (int i = 0; i < ios.length; i++) {
            inputs[i] = new BlowersIOInputsAutoLogged();
        }
    }

    @Override
    public void periodic() {
        for (int i = 0; i < ios.length; i++) {
            ios[i].updateInputs(inputs[i]);
            Logger.processInputs("Blowers/" + i, inputs[i]);
        }
    }

    /** Turns the specified blower (0-4) on. */
    private void blowerOn(int index) {
        ios[index].blowerOn();
    }

    /** Turns the specified blower (0-4) off. */
    private void blowerOff(int index) {
        ios[index].blowerOff();
    }

    /** Turns all blowers on. */
    private void allOn() {
        for (BlowersIO io : ios) {
            io.blowerOn();
        }
    }

    /** Turns all blowers off. */
    private void allOff() {
        for (BlowersIO io : ios) {
            io.blowerOff();
        }
    }

    /** Command that turns the specified blower (0-4) on. */
    public Command commandBlowerOn(int index) {
        return Commands.runOnce(() -> blowerOn(index), this);
    }

    /** Command that turns the specified blower (0-4) off. */
    public Command commandBlowerOff(int index) {
        return Commands.runOnce(() -> blowerOff(index), this);
    }

    /** Command that turns all blowers on. */
    public Command commandAllOn() {
        return Commands.runOnce(this::allOn, this);
    }

    /** Command that turns all blowers off. */
    public Command commandAllOff() {
        return Commands.runOnce(this::allOff, this);
    }
}
