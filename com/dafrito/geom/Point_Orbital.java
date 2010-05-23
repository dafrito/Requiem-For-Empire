package com.dafrito.geom;

import com.dafrito.debug.Exception_InternalError;
import com.dafrito.script.ScriptEnvironment;

// PERIAPSIS: The closest distance the orbit gets from its focus
// APOAPSIS: The farthest distance the orbit gets from its focus
// YAW: The amount of rotation on the normal axis. (Spinning around its center
// is yaw.)
// PITCH: The amount of rotation on the transverse axis. (Pitching upwards,
// pitching downwards.)
// ROLL: The amount of rotation on the longitudinal axis. (Banking to the left,
// to the right are rolling)
public class Point_Orbital extends Point {
    private Point m_focus;
    private double m_periapsis, m_apoapsis;
    private double m_yawVelocity, m_pitchVelocity, m_rollVelocity;
    private double m_yaw, m_pitch, m_roll;

    // Constructors
    public Point_Orbital(ScriptEnvironment env, String name) {
        super(env, name);
    }

    // Accessors
    public Point getFocus() {
        return this.m_focus;
    }

    public void setFocus(Point focus) {
        this.m_focus = focus;
    }

    public void setPeriapsis(double periapsis) {
        this.m_periapsis = periapsis;
    }

    public double getPeriapsis() {
        return this.m_periapsis;
    }

    public void setApoapsis(double apoapsis) {
        this.m_apoapsis = apoapsis;
    }

    public double getApoapsis() {
        return this.m_apoapsis;
    }

    public void setYaw(double yaw) {
        this.m_yaw = yaw;
    }

    public void setPitch(double pitch) {
        this.m_pitch = pitch;
    }

    public void setRoll(double roll) {
        this.m_roll = roll;
    }

    public double getYaw() {
        return this.m_yaw;
    }

    public double getPitch() {
        return this.m_pitch;
    }

    public double getRoll() {
        return this.m_roll;
    }

    public void setYawVelocity(double velocity) {
        this.m_yawVelocity = velocity;
    }

    public void setPitchVelocity(double velocity) {
        this.m_pitchVelocity = velocity;
    }

    public void setRollVelocity(double velocity) {
        this.m_rollVelocity = velocity;
    }

    public double getYawVelocity() {
        return this.m_yawVelocity;
    }

    public double getPitchVelocity() {
        return this.m_pitchVelocity;
    }

    public double getRollVelocity() {
        return this.m_rollVelocity;
    }

    // Accessor assistants
    public void setApses(double apoapsis, double periapsis) {
        setApoapsis(apoapsis);
        setPeriapsis(periapsis);
    }

    public void setYawPitchRoll(double yaw, double pitch, double roll) {
        setYaw(yaw);
        setPitch(pitch);
        setRoll(roll);
    }

    public void setYawPitchRollVelocity(double yawVel, double pitchVel, double rollVel) {
        setYawVelocity(yawVel);
        setPitchVelocity(pitchVel);
        setRollVelocity(rollVel);
    }

    // Point implementation
    @Override
    public double getX() {
        throw new Exception_InternalError("Unsupported operation in Point_Orbital: getX");
    }

    @Override
    public double getY() {
        throw new Exception_InternalError("Unsupported operation in Point_Orbital: getY");
    }

    @Override
    public double getZ() {
        throw new Exception_InternalError("Unsupported operation in Point_Orbital: getZ");
    }

    @Override
    public void setX(double x) {
        throw new Exception_InternalError("Unsupported operation in Point_Orbital: setX");
    }

    @Override
    public void setY(double y) {
        throw new Exception_InternalError("Unsupported operation in Point_Orbital: setY");
    }

    @Override
    public void setZ(double z) {
        throw new Exception_InternalError("Unsupported operation in Point_Orbital: setZ");
    }

    @Override
    public Point.System getSystem() {
        return getFocus().getSystem();
    }
}
