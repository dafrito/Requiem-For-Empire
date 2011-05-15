public class Scenario {
	private ScriptEnvironment m_environment;
	private Scheduler m_scheduler;
	private String m_name;
	private Terrestrial m_terrestrial;

	public Scenario(ScriptEnvironment env, Terrestrial terrestrial, String name) {
		m_environment = env;
		m_name = name;
		m_terrestrial = terrestrial;
		m_scheduler = new Scheduler(env);
	}

	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	public long getGameTime() {
		return m_scheduler.getCurrentGameTime();
	}

	public String getName() {
		return m_name;
	}

	public Scheduler getScheduler() {
		return m_scheduler;
	}

	public Terrestrial getTerrestrial() {
		return m_terrestrial;
	}

	public void setName(String name) {
		m_name = name;
	}

	public void setTerrestrial(Terrestrial terrestrial) {
		m_terrestrial = terrestrial;
	}

	public void start() {
		m_scheduler.start();
	}
}
