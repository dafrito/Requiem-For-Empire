import java.lang.Math;
// PERIAPSIS: The closest distance the orbit gets from its focus
// APOAPSIS: The farthest distance the orbit gets from its focus
// YAW: The amount of rotation on the normal axis. (Spinning around its center is yaw.)
// PITCH: The amount of rotation on the transverse axis. (Pitching upwards, pitching downwards.)
// ROLL: The amount of rotation on the longitudinal axis. (Banking to the left, to the right are rolling)
public class Point_Orbital extends Point{
	private Point m_focus;
	private double m_periapsis,m_apoapsis;
	private double m_yawVelocity,m_pitchVelocity,m_rollVelocity;
	private double m_yaw,m_pitch,m_roll;
	// Constructors
	public Point_Orbital(ScriptEnvironment env,String name){super(env,name);}
	// Accessors
	public Point getFocus(){return m_focus;}
	public void setFocus(Point focus){m_focus=focus;}
	
	public void setPeriapsis(double periapsis){m_periapsis=periapsis;}
	public double getPeriapsis(){return m_periapsis;}
	public void setApoapsis(double apoapsis){m_apoapsis=apoapsis;}
	public double getApoapsis(){return m_apoapsis;}
	
	public void setYaw(double yaw){m_yaw=yaw;}
	public void setPitch(double pitch){m_pitch=pitch;}
	public void setRoll(double roll){m_roll=roll;}
	public double getYaw(){return m_yaw;}
	public double getPitch(){return m_pitch;}
	public double getRoll(){return m_roll;}
	
	public void setYawVelocity(double velocity){m_yawVelocity=velocity;}
	public void setPitchVelocity(double velocity){m_pitchVelocity=velocity;}
	public void setRollVelocity(double velocity){m_rollVelocity=velocity;}
	public double getYawVelocity(){return m_yawVelocity;}
	public double getPitchVelocity(){return m_pitchVelocity;}
	public double getRollVelocity(){return m_rollVelocity;}
	// Accessor assistants
	public void setApses(double apoapsis,double periapsis){
		setApoapsis(apoapsis);
		setPeriapsis(periapsis);
	}
	public void setYawPitchRoll(double yaw,double pitch,double roll){
		setYaw(yaw);
		setPitch(pitch);
		setRoll(roll);
	}
	public void setYawPitchRollVelocity(double yawVel,double pitchVel,double rollVel){
		setYawVelocity(yawVel);
		setPitchVelocity(pitchVel);
		setRollVelocity(rollVel);
	}
	// Point implementation
	public double getX(){throw new Exception_InternalError("Unsupported operation in Point_Orbital: getX");}
	public double getY(){throw new Exception_InternalError("Unsupported operation in Point_Orbital: getY");}
	public double getZ(){throw new Exception_InternalError("Unsupported operation in Point_Orbital: getZ");}
	public void setX(double x){throw new Exception_InternalError("Unsupported operation in Point_Orbital: setX");}
	public void setY(double y){throw new Exception_InternalError("Unsupported operation in Point_Orbital: setY");}
	public void setZ(double z){throw new Exception_InternalError("Unsupported operation in Point_Orbital: setZ");}
	public Point.System getSystem(){return getFocus().getSystem();}
}
