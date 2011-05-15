import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Asset implements Nodeable, ScriptConvertible {
	private ScriptEnvironment m_environment;
	private Map<String, Object> m_properties = new HashMap<String, Object>();
	private Point m_location;
	private List<Ace> m_aces = new LinkedList<Ace>();

	public Asset(ScriptEnvironment environment) {
		this.m_environment = environment;
	}

	public void addAce(Ace ace) {
		this.m_aces.add(ace);
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		FauxTemplate_Asset asset = new FauxTemplate_Asset(this.getEnvironment(), this.getEnvironment().getTemplate(FauxTemplate_Asset.ASSETSTRING).getType());
		asset.setAsset(this);
		return asset;
	}

	public List<Ace> getAces() {
		return this.m_aces;
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public Point getLocation() {
		return this.m_location;
	}

	public Object getProperty(String name) {
		return this.m_properties.get(name);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Asset");
		assert Debugger.addSnapNode("Location", this.getLocation());
		if (this.m_aces != null && this.m_aces.size() > 0) {
			assert Debugger.addSnapNode("Archetype Conversion Efficiencies (" + this.m_aces.size() + " ACE(s))", this.m_aces);
		}
		if (this.m_properties.size() == 1) {
			assert Debugger.addSnapNode("Properties (1 property)", this.m_properties);
		} else {
			assert Debugger.addSnapNode("Properties (" + this.m_properties.size() + " properties)", this.m_properties);
		}
		assert Debugger.closeNode();
		return true;
	}

	public void setLocation(Point location) {
		this.m_location = location;
	}

	public void setProperty(String name, Object prop) {
		this.m_properties.put(name, prop);
	}
}
