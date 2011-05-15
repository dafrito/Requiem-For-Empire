import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Stylesheet extends FauxTemplate implements Nodeable, ScriptValue_Abstract, ScriptConvertible {
	private Map<StylesheetElementType, StylesheetElement> m_styleElements = new HashMap<StylesheetElementType, StylesheetElement>(); // element code, element
	private String m_name;
	private boolean m_isUnique;
	public static final String STYLESHEETSTRING = "Stylesheet";

	public Stylesheet(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, STYLESHEETSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public Stylesheet(ScriptEnvironment env, boolean flag) {
		super(env, ScriptValueType.createType(env, STYLESHEETSTRING));
	}

	public void addElement(StylesheetElementType type, StylesheetElement element) {
		assert Debugger.openNode("Stylesheet Element Additions", "Adding a" + element.getElementName() + " element to this stylesheet");
		assert Debugger.addNode(this);
		assert Debugger.addNode(element);
		this.m_styleElements.put(type, element);
		assert Debugger.closeNode();
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		return this;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract template) throws Exception_Nodeable {
		return null;
	}

	public StylesheetElement getElement(StylesheetElementType elementCode) {
		return this.m_styleElements.get(elementCode);
	}

	public String getName() {
		return this.m_name;
	}

	// FauxTemplate extensions
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new Stylesheet(this.getEnvironment(), true);
	}

	public boolean isUnique() {
		return this.m_isUnique;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		if (this.m_name == null) {
			assert Debugger.openNode("Anonymous stylesheet (" + this.m_styleElements.size() + " element(s))");
		} else {
			assert Debugger.openNode("Stylesheet: " + this.m_name + " (" + this.m_styleElements.size() + " element(s))");
		}
		assert Debugger.addNode(this.m_styleElements.values());
		assert Debugger.closeNode();
		return true;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public void setUnique(boolean isUnique) {
		this.m_isUnique = isUnique;
	}
}
