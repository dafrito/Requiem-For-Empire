import javax.swing.tree.TreePath;

public class NamedTreePath extends TreePath {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5723264748755821508L;
	private String m_name;

	public NamedTreePath(String name, Object[] array) {
		super(array);
		this.m_name = name;
	}

	public NamedTreePath(String name, TreePath path) {
		super(path.getPath());
		this.m_name = name;
	}

	public String getName() {
		return this.m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	@Override
	public String toString() {
		return this.m_name;
	}
}
