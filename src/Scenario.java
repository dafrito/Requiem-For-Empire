public class Scenario {
	private ScriptEnvironment m_environment;
	private Scheduler m_scheduler;
	private String m_name;
	private Terrestrial m_terrestrial;

	public Scenario(ScriptEnvironment env, Terrestrial terrestrial, String name) {
		this.m_environment = env;
		this.m_name = name;
		this.m_terrestrial = terrestrial;
		this.m_scheduler = new Scheduler(env);
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public long getGameTime() {
		return this.m_scheduler.getCurrentGameTime();
	}

	public String getName() {
		return this.m_name;
	}

	public Scheduler getScheduler() {
		return this.m_scheduler;
	}

	public Terrestrial getTerrestrial() {
		return this.m_terrestrial;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public void setTerrestrial(Terrestrial terrestrial) {
		this.m_terrestrial = terrestrial;
	}

	public void start() {
		this.m_scheduler.start();
	}
}
