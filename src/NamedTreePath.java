import javax.swing.tree.TreePath;
public class NamedTreePath extends TreePath{
	private String m_name;
	public NamedTreePath(String name, Object[]array){
		super((Object[])array);
		m_name=name;
	}
	public NamedTreePath(String name, TreePath path){
		super((Object[])path.getPath());
		m_name=name;
	}
	public String getName(){return m_name;}
	public void setName(String name){m_name=name;}
	public String toString(){return m_name;}
}
