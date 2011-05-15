public class NamedElement{
	String m_name, m_formalName, m_adjective;
	public NamedElement(){}
	public NamedElement(String name, String formal, String adj){
		m_name=name;
		m_formalName=formal;
		m_adjective=adj;
	}
	public void setFormalName(String name){m_formalName=name;}
	public String getFormalName(){return m_formalName;}
	public void setName(String name){m_name=name;}
	public String getName(){return m_name;}
	public void setAdjective(String adjective){m_adjective=adjective;}
	public String getAdjective(){return m_adjective;}
}
