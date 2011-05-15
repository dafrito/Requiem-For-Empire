import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
	private Set<String> m_extensions = new HashSet<String>();
	private String m_description;

	public ExtensionFilter() {
	}

	public ExtensionFilter(String desc) {
		m_description = desc;
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String string = RiffToolbox.getExtension(file);
		for (String test : m_extensions) {
			if (string.equals(test)) {
				return true;
			}
		}
		return false;
	}

	public void addExtension(String extension) {
		m_extensions.add(extension.toLowerCase());
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	public void setDescription(String desc) {
		m_description = desc;
	}
}
