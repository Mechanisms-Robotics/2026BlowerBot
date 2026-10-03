package frc.robot.subsystems.blowers;

import frc.robot.CONSTANTS;
import frc.robot.subsystems.blowers.BlowersIO.BlowersIOInputs;

public class Blowers {

    private final BlowersIO blower1; 
    private final BlowersIO blower2; 
    private final BlowersIO blower3;
    private final BlowersIO blower4;
    private final BlowersIOInputs inputs;
    
    public Blowers() {
        if (CONSTANTS.CURRENT_MODE == CONSTANTS.SIM_MODE) {
            this.blower1 = new BlowersIOSim(CONSTANTS.BlowerConstants.BLOWER1_CAN_ID) {}; 
            this.blower2 = new BlowersIOSim(CONSTANTS.BlowerConstants.BLOWER2_CAN_ID) {};
            this.blower3 = new BlowersIOSim(CONSTANTS.BlowerConstants.BLOWER3_CAN_ID) {};
            this.blower4 = new BlowersIOSim(CONSTANTS.BlowerConstants.BLOWER4_CAN_ID) {};
            this.inputs = new BlowersIOInputs() {};
        } else {
            this.blower1 = new BlowersIOSparkMax(CONSTANTS.BlowerConstants.BLOWER1_CAN_ID) {};
            this.blower2 = new BlowersIOSparkMax(CONSTANTS.BlowerConstants.BLOWER2_CAN_ID) {};
            this.blower3 = new BlowersIOSparkMax(CONSTANTS.BlowerConstants.BLOWER3_CAN_ID) {};
            this.blower4 = new BlowersIOSparkMax(CONSTANTS.BlowerConstants.BLOWER4_CAN_ID) {};
            this.inputs = new BlowersIOInputs() {};
        }
        // this.blower1 = new BlowersIO() {};
        // this.blower2 = new BlowersIO() {};
        // this.blower3 = new BlowersIO() {};
        // this.blower4 = new BlowersIO() {};
        // this.inputs = new BlowersIOInputs() {};
    }

    public void blowersOn() {
        this.blower1.blowerOn(this.inputs);
        this.blower2.blowerOn(this.inputs);
        this.blower3.blowerOn(this.inputs);
        this.blower4.blowerOn(this.inputs);
    }

    public void blowersOff() {
        this.blower1.blowerOff(this.inputs);
        this.blower2.blowerOff(this.inputs);
        this.blower3.blowerOff(this.inputs);
        this.blower4.blowerOff(this.inputs);
    }

    public void updateInputs() {
        this.blower1.updateInputs(this.inputs);
        this.blower2.updateInputs(this.inputs);
        this.blower3.updateInputs(this.inputs);
        this.blower4.updateInputs(this.inputs);
    }

    public void periodic() {
        this.updateInputs();
    }
}
