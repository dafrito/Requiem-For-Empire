import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
public class Debug_Hotspots extends JPanel implements ActionListener,MouseListener{
	private Debug_Tree m_treePanel;
	private DefaultListModel m_treePaths;
	private JList m_hotspots;
	private JButton m_createHotspot,m_showHotspot,m_removeHotspot;
	public Debug_Hotspots(Debug_Tree tree){
		m_treePanel=tree;
		setLayout(new BorderLayout());
		add(new JScrollPane(m_hotspots=new JList(m_treePaths=new DefaultListModel())));
		// Set up hotspot-buttons
		JPanel hotspotButtons=new JPanel();
		add(hotspotButtons,BorderLayout.NORTH);
		hotspotButtons.setLayout(new GridLayout(3,0));
		hotspotButtons.add(m_showHotspot=new JButton("Show Hotspot"));
		hotspotButtons.add(m_createHotspot=new JButton("Create Hotspot"));
		hotspotButtons.add(m_removeHotspot=new JButton("Remove Hotspot"));
		m_hotspots.addMouseListener(this);
		m_showHotspot.addActionListener(this);
		m_createHotspot.addActionListener(this);
		m_removeHotspot.addActionListener(this);
	}
	public Debug_Tree getTreePanel(){return m_treePanel;}
	public void reset(){
		m_treePaths.clear();
	}
	public void createHotspot(Debug_TreeNode node,String name){
		createHotspot(node.getTreePath(name));
	}
	public void createHotspot(TreePath path){
		m_treePaths.addElement(path);
	}
	public void actionPerformed(ActionEvent event){
		if(event.getSource().equals(m_showHotspot)){
			if(m_hotspots.getSelectedValue()!=null){
				getTreePanel().showTreePath((TreePath)m_hotspots.getSelectedValue());
			}
		}else if(event.getSource().equals(m_createHotspot)){
			String string;
			if(getTreePanel().getSelectionPath()==null){return;}
			do{
				string=JOptionPane.showInputDialog(null,"Enter hotspot name",((Debug_TreeNode)getTreePanel().getSelectionPath().getLastPathComponent()).getData());
				if(string==null){return;}
				if("".equals(string)){JOptionPane.showMessageDialog(this,"Please insert a name.");string=null;}
			}while(string==null);
			m_treePaths.addElement(new NamedTreePath(string,getTreePanel().getSelectionPath()));
		}else if(event.getSource().equals(m_removeHotspot)){
			if(m_hotspots.getSelectedValue()!=null){
				m_treePaths.remove(m_hotspots.getSelectedIndex());
			}
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){
			if(m_hotspots.getSelectedValue()!=null){
				getTreePanel().showTreePath((TreePath)m_hotspots.getSelectedValue());
			}
		}
	}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
}
