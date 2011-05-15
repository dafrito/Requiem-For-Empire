public class Point_Orbital extends Point {
	private Point m_focus;
	private double m_periapsis, m_apoapsis;
	private double m_yawVelocity, m_pitchVelocity, m_rollVelocity;
	private double m_yaw, m_pitch, m_roll;

	// Constructors
	public Point_Orbital(ScriptEnvironment env, String name) {
		super(env, name);
	}

	public double getApoapsis() {
		return this.m_apoapsis;
	}

	// Accessors
	public Point getFocus() {
		return this.m_focus;
	}

	public double getPeriapsis() {
		return this.m_periapsis;
	}

	public double getPitch() {
		return this.m_pitch;
	}

	public double getPitchVelocity() {
		return this.m_pitchVelocity;
	}

	public double getRoll() {
		return this.m_roll;
	}

	public double getRollVelocity() {
		return this.m_rollVelocity;
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
		return this.m_yaw;
	}

	public double getYawVelocity() {
		return this.m_yawVelocity;
	}

	@Override
	public double getZ() {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: getZ");
	}

	public void setApoapsis(double apoapsis) {
		this.m_apoapsis = apoapsis;
	}

	// Accessor assistants
	public void setApses(double apoapsis, double periapsis) {
		this.setApoapsis(apoapsis);
		this.setPeriapsis(periapsis);
	}

	public void setFocus(Point focus) {
		this.m_focus = focus;
	}

	public void setPeriapsis(double periapsis) {
		this.m_periapsis = periapsis;
	}

	public void setPitch(double pitch) {
		this.m_pitch = pitch;
	}

	public void setPitchVelocity(double velocity) {
		this.m_pitchVelocity = velocity;
	}

	public void setRoll(double roll) {
		this.m_roll = roll;
	}

	public void setRollVelocity(double velocity) {
		this.m_rollVelocity = velocity;
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
		this.m_yaw = yaw;
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
		this.m_yawVelocity = velocity;
	}

	@Override
	public void setZ(double z) {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: setZ");
	}
}
