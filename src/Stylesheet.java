import java.util.*;

public class Stylesheet extends FauxTemplate implements Nodeable,ScriptValue_Abstract,ScriptConvertible{
	private Map<StylesheetElementType,StylesheetElement>m_styleElements=new HashMap<StylesheetElementType,StylesheetElement>(); // element code, element
	private String m_name;
	private boolean m_isUnique;
	public static final String STYLESHEETSTRING="Stylesheet";
	public Stylesheet(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,STYLESHEETSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public Stylesheet(ScriptEnvironment env, boolean flag){
		super(env,ScriptValueType.createType(env,STYLESHEETSTRING));
	}
	public StylesheetElement getElement(StylesheetElementType elementCode){return m_styleElements.get(elementCode);}
	public boolean isUnique(){return m_isUnique;}
	public void setUnique(boolean isUnique){m_isUnique=isUnique;}
	public void setName(String name){m_name=name;}
	public String getName(){return m_name;}
	public void addElement(StylesheetElementType type, StylesheetElement element){
		assert Debugger.openNode("Stylesheet Element Additions", "Adding a" + element.getElementName() + " element to this stylesheet");
		assert Debugger.addNode(this);
		assert Debugger.addNode(element);
		m_styleElements.put(type,element);
		assert Debugger.closeNode();
	}
	// ScriptConvertible implementation
	public Object convert(){return this;}
	// FauxTemplate extensions
	public ScriptTemplate instantiateTemplate(){return new Stylesheet(getEnvironment(),true);}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract>params,ScriptTemplate_Abstract template)throws Exception_Nodeable{
		return null;
	}
	// Nodeable implementation
	public boolean nodificate(){
		if(m_name==null){assert Debugger.openNode("Anonymous stylesheet (" + m_styleElements.size() + " element(s))");
		}else{assert Debugger.openNode("Stylesheet: " + m_name + " ("  + m_styleElements.size() + " element(s))");}
		assert Debugger.addNode(m_styleElements.values());
		assert Debugger.closeNode();
		return true;
	}
}
