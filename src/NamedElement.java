public class NamedElement {
	String m_name, m_formalName, m_adjective;

	public NamedElement() {
	}

	public NamedElement(String name, String formal, String adj) {
		this.m_name = name;
		this.m_formalName = formal;
		this.m_adjective = adj;
	}

	public String getAdjective() {
		return this.m_adjective;
	}

	public String getFormalName() {
		return this.m_formalName;
	}

	public String getName() {
		return this.m_name;
	}

	public void setAdjective(String adjective) {
		this.m_adjective = adjective;
	}

	public void setFormalName(String name) {
		this.m_formalName = name;
	}

	public void setName(String name) {
		this.m_name = name;
	}
}
