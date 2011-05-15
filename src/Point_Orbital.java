
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
		return m_apoapsis;
	}

	// Accessors
	public Point getFocus() {
		return m_focus;
	}

	public double getPeriapsis() {
		return m_periapsis;
	}

	public double getPitch() {
		return m_pitch;
	}

	public double getPitchVelocity() {
		return m_pitchVelocity;
	}

	public double getRoll() {
		return m_roll;
	}

	public double getRollVelocity() {
		return m_rollVelocity;
	}

	@Override
	public Point.System getSystem() {
		return getFocus().getSystem();
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
		return m_yaw;
	}

	public double getYawVelocity() {
		return m_yawVelocity;
	}

	@Override
	public double getZ() {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: getZ");
	}

	public void setApoapsis(double apoapsis) {
		m_apoapsis = apoapsis;
	}

	// Accessor assistants
	public void setApses(double apoapsis, double periapsis) {
		setApoapsis(apoapsis);
		setPeriapsis(periapsis);
	}

	public void setFocus(Point focus) {
		m_focus = focus;
	}

	public void setPeriapsis(double periapsis) {
		m_periapsis = periapsis;
	}

	public void setPitch(double pitch) {
		m_pitch = pitch;
	}

	public void setPitchVelocity(double velocity) {
		m_pitchVelocity = velocity;
	}

	public void setRoll(double roll) {
		m_roll = roll;
	}

	public void setRollVelocity(double velocity) {
		m_rollVelocity = velocity;
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
		m_yaw = yaw;
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

	public void setYawVelocity(double velocity) {
		m_yawVelocity = velocity;
	}

	@Override
	public void setZ(double z) {
		throw new Exception_InternalError("Unsupported operation in Point_Orbital: setZ");
	}
}
