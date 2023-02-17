package frc.robot.subsystems.arm;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClawConstants;

public class Claw extends SubsystemBase {
    private static Claw m_instance;
    private ClawStates m_state, m_lastState;
    private final CANSparkMax m_clawMotor;

    public enum ClawStates {
        IDLE("Idle"),
        CLOSED("Closed"),
        OPEN("Open"),
        CUBE("Cube"),
        CONE("Cone");

        String stateName;

        private ClawStates(String name) {
            this.stateName = name;
        }

        public String toString() {
            return this.stateName;
        }
    }

    private Claw() {
        m_state = ClawStates.IDLE;
        m_clawMotor = new CANSparkMax(5, MotorType.kBrushless);
        m_clawMotor.restoreFactoryDefaults();
        m_clawMotor.getPIDController().setP(ClawConstants.kP);
        m_clawMotor.getPIDController().setOutputRange(-ClawConstants.OUTPUT_LIMIT, ClawConstants.OUTPUT_LIMIT);
        m_clawMotor.setIdleMode(IdleMode.kCoast);
        m_clawMotor.setInverted(false);
        // m_clawMotor.getEncoder().setPosition(0);
    }

    @Override
    public void periodic() {
        stateMachine();
        SmartDashboard.putString("Claw State", m_state.toString());
        SmartDashboard.putNumber("Claw Position", m_clawMotor.getEncoder().getPosition());
    }

    private void stateMachine() {
        Command currentClawCommand = null;
        if (!m_state.equals(m_lastState)) {
            switch (m_state) {
                case IDLE:
                    currentClawCommand = Idle();
                    break;
                case CLOSED:
                    currentClawCommand = SetClawPos(0);
                    break;
                case OPEN:
                    currentClawCommand = SetClawPos(20);
                    break;
                case CUBE:
                    currentClawCommand = SetClawPos(0);
                    break;
                case CONE:
                    currentClawCommand = SetClawPos(0);
                    break;
                default:
                    m_state = ClawStates.IDLE;
            }
        }

        m_lastState = m_state;

        if (currentClawCommand != null) {
            currentClawCommand.schedule();
        }
    }

    private Command Idle() {
        return null;
    }

    private Command SetClawPos(double setpoint) {
        return new InstantCommand(() -> {
            SmartDashboard.putNumber("Claw setpoint", setpoint);
            m_clawMotor.getPIDController().setReference(setpoint, ControlType.kPosition);
        }, this);
    }

    public void setState(ClawStates state) {
        m_state = state;
    }

    public static Claw getInstance() {
        if (m_instance == null) {
            m_instance = new Claw();
        }
        return m_instance;

    }
}
