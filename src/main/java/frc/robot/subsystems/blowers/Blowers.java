package frc.robot.subsystems.blowers;

import frc.robot.subsystems.blowers.BlowersIO.BlowersIOInputs;

public class Blowers {

    private final BlowersIO blower1; 
    private final BlowersIO blower2; 
    private final BlowersIO blower3;
    private final BlowersIO blower4;
    private final BlowersIOInputs inputs;
    
    public Blowers() {
        this.blower1 = new BlowersIO() {}; 
        this.blower2 = new BlowersIO() {}; 
        this.blower3 = new BlowersIO() {};
        this.blower4 = new BlowersIO() {};
        this.inputs = new BlowersIOInputs();
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
}
