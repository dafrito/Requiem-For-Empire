import java.util.Vector;
import java.util.List;
public class Archetype implements ScriptConvertible,Nodeable{
	private String m_name;
	private List<Ace>m_parents=new Vector<Ace>();
	private ScriptEnvironment m_environment;
	public Archetype(ScriptEnvironment env,String name){
		m_environment=env;
		m_name=name;
	}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public void addParent(Ace ace){m_parents.add(ace);}
	public String getName(){return m_name;}
	public List<Ace>getParents(){return m_parents;}
	public Archetype getRoot(){
		if(m_parents==null||m_parents.size()==0){return this;}
		return m_parents.get(0).getArchetype().getRoot();
	}
	public boolean equals(Object o){
		if(o instanceof String){return m_name.equals(o);}
		if(o instanceof Archetype){return m_name.equals(((Archetype)o).getName());}
		return false;
	}
	public Object convert(){
		FauxTemplate_Archetype archetype=new FauxTemplate_Archetype(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Archetype.ARCHETYPESTRING));
		archetype.setArchetype(this);
		return archetype;
	}
	public boolean nodificate(){
		assert Debugger.openNode("Archetype ("+m_name+")");
		assert Debugger.addSnapNode("Aces ("+m_parents.size()+" ace(s))",m_parents);
		assert Debugger.closeNode();
		return true;
	}
}
