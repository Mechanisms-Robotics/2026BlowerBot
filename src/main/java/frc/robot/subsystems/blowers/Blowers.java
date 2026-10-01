package frc.robot.subsystems.blowers;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// Holds all the blower workers and tells them what to do.
public class Blowers extends SubsystemBase {

    // List of blower workers (real or sim — the boss doesn't care which)
    private final BlowersIO[] ios;
    // One "report card" per blower (this class is made automatically when you build)
    private final BlowersIOInputsAutoLogged[] inputs;

    // Runs ONE time: takes however many blowers you give it
    public Blowers(BlowersIO... ios) {
        this.ios = ios;
        this.inputs = new BlowersIOInputsAutoLogged[ios.length];
        for (int i = 0; i < ios.length; i++) {
            inputs[i] = new BlowersIOInputsAutoLogged();
        }
    }

    // Runs every 20 ms: ask each blower how it's doing, then log its report card
    @Override
    public void periodic() {
        for (int i = 0; i < ios.length; i++) {
            ios[i].updateInputs(inputs[i]);
            Logger.processInputs("Blowers/" + (i + 1), inputs[i]);
        }
    }

    // Turn every blower on
    public void on() {
        for (int i = 0; i < ios.length; i++) {
            ios[i].blowerOn(inputs[i]);
        }
    }

    // Turn every blower off
    public void off() {
        for (int i = 0; i < ios.length; i++) {
            ios[i].blowerOff(inputs[i]);
        }
    }

    // A command: blowers ON when it starts, OFF when it ends
    public Command runBlowers() {
        return startEnd(this::on, this::off);
    }
}