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
		m_environment = environment;
	}

	public void addAce(Ace ace) {
		m_aces.add(ace);
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		FauxTemplate_Asset asset = new FauxTemplate_Asset(getEnvironment(), getEnvironment().getTemplate(FauxTemplate_Asset.ASSETSTRING).getType());
		asset.setAsset(this);
		return asset;
	}

	public List<Ace> getAces() {
		return m_aces;
	}

	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	public Point getLocation() {
		return m_location;
	}

	public Object getProperty(String name) {
		return m_properties.get(name);
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Asset");
		assert Debugger.addSnapNode("Location", getLocation());
		if (m_aces != null && m_aces.size() > 0) {
			assert Debugger.addSnapNode("Archetype Conversion Efficiencies (" + m_aces.size() + " ACE(s))", m_aces);
		}
		if (m_properties.size() == 1) {
			assert Debugger.addSnapNode("Properties (1 property)", m_properties);
		} else {
			assert Debugger.addSnapNode("Properties (" + m_properties.size() + " properties)", m_properties);
		}
		assert Debugger.closeNode();
		return true;
	}

	public void setLocation(Point location) {
		m_location = location;
	}

	public void setProperty(String name, Object prop) {
		m_properties.put(name, prop);
	}
}
