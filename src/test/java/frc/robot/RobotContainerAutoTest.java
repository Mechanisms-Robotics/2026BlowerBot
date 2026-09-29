package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** The auto chooser must be on the dashboard and every choice must build a runnable command. */
class RobotContainerAutoTest {
    private static RobotContainer robotContainer;

    @BeforeAll
    static void setup() {
        HAL.initialize(500, 0);
        robotContainer = new RobotContainer();
        SmartDashboard.updateValues();
    }

    private static List<String> publishedAutoNames() {
        String[] options = NetworkTableInstance.getDefault()
            .getTable("SmartDashboard")
            .getSubTable("Auto Chooser")
            .getEntry("options")
            .getStringArray(new String[0]);
        return Arrays.asList(options);
    }

    @Test
    void autoChooserIsPublishedWithNoneAsDefault() {
        assertTrue(publishedAutoNames().contains("None"), "Auto Chooser missing from dashboard");
        assertEquals("None", robotContainer.autoChooser.getSelected());
    }

    @Test
    void everyAutoChoiceBuildsANamedCommand() {
        for (String name : publishedAutoNames()) {
            Command command = robotContainer.getAutonomousCommand(name);
            assertNotNull(command, name);
            assertEquals(name, command.getName());
        }
    }

    @Test
    void unknownAutoNameFallsBackToDoingNothing() {
        Command command = robotContainer.getAutonomousCommand("Does Not Exist");
        assertNotNull(command);
        assertEquals("Does Not Exist", command.getName());
    }
}
