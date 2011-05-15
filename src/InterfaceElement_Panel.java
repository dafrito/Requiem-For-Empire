import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.*;
import java.awt.Color;
public class InterfaceElement_Panel extends InterfaceElement implements Interface_Container,RiffInterface_MouseListener,Nodeable,ScriptConvertible{
	private List<GraphicalElement>m_elements;
	private Point_Euclidean m_offset;
	private Terrestrial m_terrestrial;
	private ScriptTemplate_Abstract m_dali;
	private Graphics2D m_graphics;
	public InterfaceElement_Panel(ScriptEnvironment env, Stylesheet uniqueStyle,Stylesheet classStyle){
		super(env,uniqueStyle,classStyle);
		m_offset=new Point_Euclidean(env,0,0,0);
		m_elements=new LinkedList<GraphicalElement>();
		m_dali=env.getTemplate(FauxTemplate_RiffDali.RIFFDALISTRING);
	}
	public Terrestrial getTerrestrial(){return m_terrestrial;}
	public void setTerrestrial(Terrestrial terrestrial){m_terrestrial=terrestrial;}
	public InterfaceElement getContainerElement(){return this;}
	public void setRiffDali(ScriptTemplate_Abstract dali){m_dali=dali;}
	public void clear(){m_elements.clear();}
	public void add(GraphicalElement element){
		m_elements.add(element);
		element.setParent(this);
		if(getRoot()!=null){getRoot().repaint();}
	}
	public void addAll(List list){
		for(int i=0;i<list.size();i++){
			add((GraphicalElement)list.get(i));
		}
		if(getRoot()!=null){getRoot().repaint();}
	}
	public int size(){return m_elements.size();}
	public List<GraphicalElement>getElements(){return Collections.unmodifiableList(m_elements);}
	public void setPreferredWidth(int width){}
	public Point getOffset(){return m_offset;}
	public void drawTransformedRegion(DiscreteRegion region){
		DiscreteRegion.paint(getGraphics(),DiscreteRegion.transform(region,getOffset(),getDrawingBounds(),false),getDrawingBounds(),false);
	}
	public void fillTransformedRegion(DiscreteRegion region){
		DiscreteRegion.paint(getGraphics(),DiscreteRegion.transform(region,getOffset(),getDrawingBounds(),false),getDrawingBounds(),true);
	}
	public void drawRegion(DiscreteRegion region){
		DiscreteRegion.paint(getGraphics(),DiscreteRegion.transform(region,getOffset(),getDrawingBounds(),true),getDrawingBounds(),false);
	}
	public void fillRegion(DiscreteRegion region){
		DiscreteRegion.paint(getGraphics(),DiscreteRegion.transform(region,getOffset(),getDrawingBounds(),true),getDrawingBounds(),true);
	}
	public void drawString(String string,Color color,Point location){
		Color oldColor=getGraphics().getColor();
		getGraphics().setColor(color);
		Point point=DiscreteRegion.transformPoint(getEnvironment(),location,getOffset(),getDrawingBounds(),true);
		getGraphics().drawString(string,(int)point.getX(),(int)point.getY());
		getGraphics().setColor(oldColor);
	}
	public Graphics2D getGraphics(){return m_graphics;}
	public void paint(Graphics2D g2d){
		Set<DiscreteRegion>regions;
		m_graphics=g2d;
		if(m_terrestrial.getTree()==null){regions=new HashSet<DiscreteRegion>();
		}else{regions=m_terrestrial.getTree().getRegionList();}
		assert Debugger.openNode("Painting Panel Elements ("+m_elements.size()+" element(s), "+regions.size()+" region(s))");
		super.paint(g2d);
		assert Debugger.addNode("X-offset: "+m_offset.getX());
		assert Debugger.addNode("Y-offset: "+m_offset.getY());
		assert Debugger.addNode("Zoom factor: "+m_offset.getZ());
		try{
			List<ScriptValue_Abstract>params=new LinkedList<ScriptValue_Abstract>();
			params.add((ScriptValue_Abstract)convert());
			List<ScriptValue_Abstract>regionList=new LinkedList<ScriptValue_Abstract>();
			List<ScriptValue_Abstract>assetList=new LinkedList<ScriptValue_Abstract>();
			for(DiscreteRegion region:regions){
				regionList.add((ScriptValue_Abstract)region.convert());
				if(region.getProperty("Archetypes")!=null){
					for(Asset asset:((ArchetypeMapNode)region.getProperty("Archetypes")).getAllAssets()){
						assetList.add((ScriptValue_Abstract)asset.convert());
					}
				}
			}
			params.add(Parser.getRiffList(getEnvironment(),regionList));
			params.add(Parser.getRiffList(getEnvironment(),assetList));
			if(m_dali!=null){ScriptExecutable_CallFunction.callFunction(getEnvironment(),null,m_dali,"paintPanel",params);}
		}catch(Exception_Nodeable exception){
			throw new Exception_InternalError(getEnvironment(),exception);
		}
		for(GraphicalElement elem:m_elements){
			elem.paint(g2d);
		}
		g2d.setColor(getBackgroundColor());
		g2d.draw(getDrawingBounds());
		assert Debugger.closeNode();
	}
	public boolean isFocusable(){return true;}
	public void riffMouseEvent(RiffInterface_MouseEvent event){
		if(event instanceof RiffInterface_DragEvent){
			if(event.getButton()==RiffInterface_MouseListener.MouseButton.LEFT){
				m_offset.addX(-((RiffInterface_DragEvent)event).getXOffset()/Math.pow(2,m_offset.getZ()));
				m_offset.addY(-((RiffInterface_DragEvent)event).getYOffset()/Math.pow(2,m_offset.getZ()));
			}
			if(event.getButton()==RiffInterface_MouseListener.MouseButton.RIGHT){
				m_offset.addZ(((RiffInterface_DragEvent)event).getDistance()/50);
			}
		}
	}
	// ScriptConvertible implementation
	public Object convert(){
		FauxTemplate_Panel panel=new FauxTemplate_Panel(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Panel.PANELSTRING));
		panel.setElement(this);
		return panel;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Panel Interface Element");
		assert super.nodificate();
		assert Debugger.addSnapNode("Terrestrial",m_terrestrial);
		assert Debugger.addSnapNode("Graphical Elements: (" + m_elements.size() +" element(s))",m_elements);
		assert Debugger.closeNode();
		return true;
	}
}
