package com.dafrito.rfe.geom.points;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;

public class Point_Orbital extends Point {
	private Point focus;
	private double periapsis, apoapsis;
	private double yawVelocity, pitchVelocity, rollVelocity;
	private double yaw, pitch, roll;

	// Constructors
	public Point_Orbital(ScriptEnvironment env, String name) {
		super(env, name);
	}

	public double getApoapsis() {
		return this.apoapsis;
	}

	// Accessors
	public Point getFocus() {
		return this.focus;
	}

	public double getPeriapsis() {
		return this.periapsis;
	}

	public double getPitch() {
		return this.pitch;
	}

	public double getPitchVelocity() {
		return this.pitchVelocity;
	}

	public double getRoll() {
		return this.roll;
	}

	public double getRollVelocity() {
		return this.rollVelocity;
	}

	@Override
	public Point.System getSystem() {
		return this.getFocus().getSystem();
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

	public double getYaw() {
		return this.yaw;
	}

	public double getYawVelocity() {
		return this.yawVelocity;
	}

	@Override
	public double getZ() {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: getZ");
	}

	public void setApoapsis(double apoapsis) {
		this.apoapsis = apoapsis;
	}

	// Accessor assistants
	public void setApses(double apoapsis, double periapsis) {
		this.setApoapsis(apoapsis);
		this.setPeriapsis(periapsis);
	}

	public void setFocus(Point focus) {
		this.focus = focus;
	}

	public void setPeriapsis(double periapsis) {
		this.periapsis = periapsis;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public void setPitchVelocity(double velocity) {
		this.pitchVelocity = velocity;
	}

	public void setRoll(double roll) {
		this.roll = roll;
	}

	public void setRollVelocity(double velocity) {
		this.rollVelocity = velocity;
	}

	@Override
	public void setX(double x) {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: setX");
	}

	@Override
	public void setY(double y) {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: setY");
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public void setYawPitchRoll(double yaw, double pitch, double roll) {
		this.setYaw(yaw);
		this.setPitch(pitch);
		this.setRoll(roll);
	}

	public void setYawPitchRollVelocity(double yawVel, double pitchVel, double rollVel) {
		this.setYawVelocity(yawVel);
		this.setPitchVelocity(pitchVel);
		this.setRollVelocity(rollVel);
	}

	public void setYawVelocity(double velocity) {
		this.yawVelocity = velocity;
	}

	@Override
	public void setZ(double z) {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: setZ");
	}
}
