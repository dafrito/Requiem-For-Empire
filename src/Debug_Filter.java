import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
public class Debug_Filter extends JPanel implements ActionListener{
	private JButton m_addFilter,m_removeFilter;
	private DefaultListModel m_filters=new DefaultListModel();
	private JList m_filterList;
	private boolean m_isListening;
	private Debug_Listener m_listener;
	public Debug_Filter(Debug_Listener listener){
		m_listener=listener;
		setLayout(new BorderLayout());
		JPanel filterButtons=new JPanel();
		add(filterButtons,BorderLayout.NORTH);
		filterButtons.setLayout(new GridLayout(2,0));
		filterButtons.add(m_addFilter=new JButton("Add Filter"));
		filterButtons.add(m_removeFilter=new JButton("Remove Filter"));
		add(new JScrollPane(m_filterList=new JList(m_filters)));
		m_addFilter.addActionListener(this);
		m_removeFilter.addActionListener(this);
	}
	public DefaultListModel getFilters(){return m_filters;}
	public Debug_Listener getListener(){return m_listener;}
	public void setListening(boolean listening){m_isListening=listening;}
	public boolean isListening(){
		if(getListener().isUnfiltered()){return true;}
		if(getListener().isCapturing()||getListener().isSynchronizing()){return m_isListening;}
		return false;
	}
	public void sniffNode(Debug_TreeNode node){
		for(int i=0;i<m_filters.size();i++){
			if(m_isListening==true){return;}
			m_isListening=node.getData().equals(m_filters.get(i))||(node.getGroup()!=null&&node.getGroup().equals(m_filters.get(i)));
		}
	}
	public boolean addFilter(Object filter){
		if(filter==null||getListener().getDebugger().isFilterUsed(filter.toString(),m_listener.getThreadName())!=null){return false;}
		m_filters.add(m_filters.size(),filter);
		return true;
	}
	public boolean isFilterUsed(Object test){
		for(int i=0;i<m_filters.size();i++){
			if(m_filters.get(i).equals(test)){return true;}
		}
		return false;
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource().equals(m_addFilter)){
			Object data;
			if(getListener().getTreePanel().getSelectedNode()!=null){
				data=JOptionPane.showInputDialog(null,"Insert the name of the filter you wish to add","Filter Additions",JOptionPane.PLAIN_MESSAGE,null,null,getListener().getTreePanel().getFirstAvailableFilter());
			}else{
				data=getListener().getTreePanel().getFirstAvailableFilter();
			}
			if(data!=null){addFilter(data);}
		}else if(e.getSource().equals(m_removeFilter)){
			m_filters.remove(m_filters.indexOf(m_filterList.getSelectedValue()));
		}
	}
}
