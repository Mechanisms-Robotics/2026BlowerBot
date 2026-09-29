package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;
import frc.robot.CONSTANTS.DriveConstants;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Catches copy-paste mistakes in CONSTANTS.java that would only show up on the real robot. */
class ConstantsTest {
    private static final List<SwerveModuleConstants<TalonFXConfiguration, TalonFXConfiguration, CANcoderConfiguration>> MODULES =
        List.of(DriveConstants.FRONT_LEFT, DriveConstants.FRONT_RIGHT, DriveConstants.BACK_LEFT, DriveConstants.BACK_RIGHT);

    @Test
    void talonFxCanIdsAreUnique() {
        // Drive and steer motors are all TalonFXs, so they share one ID space
        Set<Integer> ids = new HashSet<>();
        for (var module : MODULES) {
            assertTrue(ids.add(module.DriveMotorId), "duplicate TalonFX CAN ID " + module.DriveMotorId);
            assertTrue(ids.add(module.SteerMotorId), "duplicate TalonFX CAN ID " + module.SteerMotorId);
        }
    }

    @Test
    void encoderCanIdsAreUnique() {
        Set<Integer> ids = new HashSet<>();
        for (var module : MODULES) {
            assertTrue(ids.add(module.EncoderId), "duplicate encoder CAN ID " + module.EncoderId);
        }
    }

    @Test
    void modulesAreInTheCorrectCorners() {
        // WPILib convention: +X is forward, +Y is left
        assertCorner("front left", DriveConstants.FRONT_LEFT, +1, +1);
        assertCorner("front right", DriveConstants.FRONT_RIGHT, +1, -1);
        assertCorner("back left", DriveConstants.BACK_LEFT, -1, +1);
        assertCorner("back right", DriveConstants.BACK_RIGHT, -1, -1);
    }

    private static void assertCorner(String name, SwerveModuleConstants<?, ?, ?> module, int xSign, int ySign) {
        assertEquals(xSign, (int) Math.signum(module.LocationX), name + " X is on the wrong side");
        assertEquals(ySign, (int) Math.signum(module.LocationY), name + " Y is on the wrong side");
    }

    @Test
    void deadbandIsAFractionOfStickTravel() {
        assertTrue(DriveConstants.DEADBAND > 0.0 && DriveConstants.DEADBAND < 0.5);
    }
}
