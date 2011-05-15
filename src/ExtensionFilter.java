import java.util.*;
import java.io.*;
public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter{
	private Set<String>m_extensions=new HashSet<String>();
	private String m_description;
	public ExtensionFilter(){}
	public ExtensionFilter(String desc){
		m_description=desc;
	}
	public void addExtension(String extension){
		m_extensions.add(extension.toLowerCase());
	}
	public boolean accept(File file){
		if(file.isDirectory()){return true;}
		String string=RiffToolbox.getExtension(file);
		for(String test:m_extensions){
			if(string.equals(test)){return true;}
		}
		return false;
	}
	public void setDescription(String desc){
		m_description=desc;
	}
	public String getDescription(){return m_description;}
}
