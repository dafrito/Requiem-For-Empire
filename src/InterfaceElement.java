import java.awt.*;
public class InterfaceElement implements Nodeable,GraphicalElement,ScriptConvertible{
	private Stylesheet m_classStylesheet, m_uniqueStylesheet;
	private int m_xAnchor, m_yAnchor;
	private Interface_Container m_parent;
	private ScriptEnvironment m_environment;
	public InterfaceElement(ScriptEnvironment environment,Stylesheet uniqueStylesheet, Stylesheet classStylesheet){
		m_environment=environment;
		m_uniqueStylesheet=uniqueStylesheet;
		m_classStylesheet=classStylesheet;
	}
	public Rectangle getDrawingBounds(){
		return new Rectangle(m_xAnchor,m_yAnchor,getInternalWidth()-1,getInternalHeight()-1);
	}
	public boolean isFocusable(){return false;}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public void setPreferredWidth(int width){
		assert Debugger.openNode("Setting Preferred Width ("+width+")");
		assert Debugger.addSnapNode("Element",this);
		if(null==getUniqueStylesheet()){
			m_uniqueStylesheet=new Stylesheet(m_environment);
			m_uniqueStylesheet.setUnique(true);
		}
		getUniqueStylesheet().addElement(StylesheetElementType.WIDTH, new StylesheetAbsoluteWidthElement(width));
		Debugger.closeNode();
	}
	public Font getCurrentFont(){
		return new Font(((StylesheetFontElement)getStyleElement(StylesheetElementType.FONTNAME)).getFontName(),((StylesheetFontStyleElement)getStyleElement(StylesheetElementType.FONTSTYLE)).getStyle(), ((StylesheetFontSizeElement)getStyleElement(StylesheetElementType.FONTSIZE)).getFontSize());
	}
	public Color getCurrentTextColor(){
		return ((StylesheetColorElement)getStyleElement(StylesheetElementType.COLOR)).getColor();
	}
	public Stylesheet getUniqueStylesheet(){return m_uniqueStylesheet;}
	public Stylesheet getClassStylesheet(){return m_classStylesheet;}
	public void setUniqueStylesheet(Stylesheet sheet){m_uniqueStylesheet=sheet;}
	public void setClassStylesheet(Stylesheet sheet){m_classStylesheet=sheet;}
	public StylesheetElement getStyleElement(StylesheetElementType code){
		StylesheetElement element=null;
		if(getUniqueStylesheet()!=null){
			element=getUniqueStylesheet().getElement(code);
			if(element!=null){return element;}
		}
		if(getClassStylesheet()!=null){
			element=getClassStylesheet().getElement(code);
			if(element!=null){return element;}
		}
		return getParent().getContainerElement().getStyleElement(code);
	}
	public int getLeftMarginMagnitude(){return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude();}
	public int getRightMarginMagnitude(){return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();}
	public int getBottomMarginMagnitude(){return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();}
	public int getTopMarginMagnitude(){return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude();}
	public int getLeftPaddingMagnitude(){return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude();}
	public int getRightPaddingMagnitude(){return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude();}
	public int getBottomPaddingMagnitude(){return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude();}
	public int getTopPaddingMagnitude(){return ((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude();}
	public int getLeftBorderMagnitude(){return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude();}
	public int getRightBorderMagnitude(){return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude();}
	public int getBottomBorderMagnitude(){return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude();}
	public int getTopBorderMagnitude(){return ((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude();}
	public Color getBackgroundColor(){return ((StylesheetBackgroundColorElement)getStyleElement(StylesheetElementType.BACKGROUNDCOLOR)).getColor();}
	public StylesheetElement getNonRecursiveStyleElement(StylesheetElementType code){
		StylesheetElement element=null;
		if(getUniqueStylesheet()!=null){
			element=getUniqueStylesheet().getElement(code);
			if(element!=null){return element;}
		}
		if(getClassStylesheet()!=null){
			element=getClassStylesheet().getElement(code);
		}
		return element;
	}
	public int getLeftFluffMagnitude(){return getLeftMarginMagnitude()+getLeftBorderMagnitude()+getLeftPaddingMagnitude();}
	public int getRightFluffMagnitude(){return getRightMarginMagnitude()+getRightBorderMagnitude()+getRightPaddingMagnitude();}
	public int getHorizontalFluffMagnitude(){return getLeftFluffMagnitude()+getRightFluffMagnitude();}
	public int getTopFluffMagnitude(){return getTopMarginMagnitude()+getTopBorderMagnitude()+getTopPaddingMagnitude();}
	public int getBottomFluffMagnitude(){return getBottomMarginMagnitude()+getBottomBorderMagnitude()+getBottomPaddingMagnitude();}
	public int getVerticalFluffMagnitude(){return getTopFluffMagnitude()+getBottomFluffMagnitude();}
	public int getInternalWidth(){
		StylesheetWidthElement element=(StylesheetWidthElement)getStyleElement(StylesheetElementType.WIDTH);
		if(element instanceof StylesheetAbsoluteWidthElement){
			return ((Integer)element.getMagnitude()).intValue();
		}else{
			return (int)(((Double)element.getMagnitude()).doubleValue()*(double)getParent().getContainerElement().getInternalWidth()-getHorizontalFluffMagnitude());
		}
	}
	public int getInternalHeight(){
		StylesheetHeightElement element=(StylesheetHeightElement)getStyleElement(StylesheetElementType.HEIGHT);
		if(element instanceof StylesheetAbsoluteHeightElement){
			return ((Integer)element.getMagnitude()).intValue();
		}else{
			return (int)(((Double)element.getMagnitude()).doubleValue()*(double)getParent().getContainerElement().getInternalHeight()-getVerticalFluffMagnitude());
		}
	}
	public int getFullWidth(){
		return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINLEFT)).getMagnitude()+((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getMagnitude()+((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGLEFT)).getMagnitude()+getInternalWidth()+((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGRIGHT)).getMagnitude()+((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getMagnitude()+((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINRIGHT)).getMagnitude();
	}
	public int getFullHeight(){
		return ((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINTOP)).getMagnitude()+((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getMagnitude()+((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGTOP)).getMagnitude()+getInternalHeight()+((StylesheetPaddingElement)getStyleElement(StylesheetElementType.PADDINGBOTTOM)).getMagnitude()+((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getMagnitude()+((StylesheetMarginElement)getStyleElement(StylesheetElementType.MARGINBOTTOM)).getMagnitude();
	}
	public void setXAnchor(int xAnchor){m_xAnchor=xAnchor;}
	public void setYAnchor(int yAnchor){m_yAnchor=yAnchor;}
	public int getXAnchor(){return m_xAnchor;}
	public int getYAnchor(){return m_yAnchor;}
	public void addXAnchor(int addingAmount){m_xAnchor+=addingAmount;}
	public void addYAnchor(int addingAmount){m_yAnchor+=addingAmount;}
	public InterfaceElement_Root getRoot(){
		if(getParent()==null){return null;}
		return getParent().getContainerElement().getRoot();
	}
	public Interface_Container getParent(){return m_parent;}
	public void setParent(Interface_Container container){m_parent=container;}
	public void paint(Graphics2D g2d){
		if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getStyle().equals(ScriptKeywordType.none)){
			g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERLEFT)).getColor());
			int xPos=getXAnchor()+getLeftMarginMagnitude();
			int yPos=getYAnchor()+getTopMarginMagnitude();
			int width=getLeftBorderMagnitude();
			int height=getTopBorderMagnitude()+getTopPaddingMagnitude()+getInternalHeight()+getBottomPaddingMagnitude()+getBottomBorderMagnitude();
			g2d.fill(new Rectangle(xPos,yPos,width,height));
		}
		if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getStyle().equals(ScriptKeywordType.none)){
			g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERRIGHT)).getColor());
			int xPos=getXAnchor()+getLeftFluffMagnitude()+getInternalWidth()+getRightPaddingMagnitude();
			int yPos=getYAnchor()+getTopMarginMagnitude();
			int width=getRightBorderMagnitude();
			int height=getTopBorderMagnitude()+getTopPaddingMagnitude()+getInternalHeight()+getBottomPaddingMagnitude()+getBottomBorderMagnitude();
			g2d.fill(new Rectangle(xPos,yPos,width,height));
		}
		if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getStyle().equals(ScriptKeywordType.none)){
			g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERTOP)).getColor());
			int xPos=getXAnchor()+getLeftMarginMagnitude();
			int yPos=getYAnchor()+getTopMarginMagnitude();
			int width=getLeftBorderMagnitude()+getLeftPaddingMagnitude()+getInternalWidth()+getRightPaddingMagnitude()+getRightBorderMagnitude();
			int height=getLeftBorderMagnitude();
			g2d.fill(new Rectangle(xPos,yPos,width,height));
		}
		if(!((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getStyle().equals(ScriptKeywordType.none)){
			g2d.setColor(((StylesheetBorderElement)getStyleElement(StylesheetElementType.BORDERBOTTOM)).getColor());
			int xPos=getXAnchor()+getLeftMarginMagnitude();
			int yPos=getYAnchor()+getTopFluffMagnitude()+getInternalHeight()+getBottomPaddingMagnitude();
			int width=getLeftBorderMagnitude()+getLeftPaddingMagnitude()+getInternalWidth()+getRightPaddingMagnitude()+getRightBorderMagnitude();
			int height=getLeftBorderMagnitude();
			g2d.fill(new Rectangle(xPos,yPos,width,height));
		}
		addXAnchor(getLeftFluffMagnitude());
		addYAnchor(getTopFluffMagnitude());
		g2d.setColor(getBackgroundColor());
		g2d.fill(new Rectangle(getXAnchor(),getYAnchor(),getInternalWidth(),getInternalHeight()));
		g2d.setFont(getCurrentFont());
		g2d.setColor(getCurrentTextColor());
	}
	// ScriptConvertible implementation
	public Object convert(){
		FauxTemplate_InterfaceElement elem=new FauxTemplate_InterfaceElement(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING));
		elem.setElement(this);
		return elem;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Interface Element");
		assert Debugger.addSnapNode("Unique Stylesheet",m_uniqueStylesheet);
		assert Debugger.addSnapNode("Class Stylesheet",m_classStylesheet);
		assert Debugger.closeNode();
		return true;
	}
}
