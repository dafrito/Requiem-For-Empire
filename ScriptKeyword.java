enum ScriptKeywordType{
	UNKNOWN,NULL,
	VOID,BOOLEAN,SHORT,INT,LONG,FLOAT,DOUBLE,STRING,STYLESHEET,
	NEW,IF,FOR,ELSE,RETURN,THIS,SUPER,
	CLASS,EXTENDS,IMPLEMENTS,
	PRIVATE,PROTECTED,PUBLIC,STATIC,ABSTRACT,UNIQUE,
	dotted,solid,none
}
public class ScriptKeyword extends ScriptElement implements Nodeable{
	private final ScriptKeywordType m_type;
	// Constructors
	public ScriptKeyword(Referenced ref,ScriptKeywordType type)throws Exception_Nodeable{
		super(ref);
		m_type=type;
	}
	public boolean equals(Object o){
		if(o instanceof ScriptValueType){
			return ((ScriptValueType)o)==getValueType();
		}else if(o instanceof ScriptKeywordType){
			return ((ScriptKeywordType)o)==getType();
		}else{
			return ((ScriptKeyword)o).getType()==m_type;
		}
	}
	public ScriptKeywordType getType(){return m_type;}
	public ScriptValueType getValueType(){return getValueType(this.getType());}
	public static ScriptValueType getValueType(ScriptKeywordType type){
		switch(type){
			case VOID:return ScriptValueType.VOID;
			case BOOLEAN:return ScriptValueType.BOOLEAN;
			case SHORT:return ScriptValueType.SHORT;
			case INT:return ScriptValueType.INT;
			case LONG:return ScriptValueType.LONG;
			case FLOAT:return ScriptValueType.FLOAT;
			case DOUBLE:return ScriptValueType.DOUBLE;
			case STRING:return ScriptValueType.STRING;
			default:return null;
		}
	}
	// String<-->Keyword functions
	public static ScriptKeywordType getType(String string){
		if("unique".equals(string)){return ScriptKeywordType.UNIQUE;}
		if("Stylesheet".equals(string)){return ScriptKeywordType.STYLESHEET;}
		if("new".equals(string)){return ScriptKeywordType.NEW;}
		if("if".equals(string)){return ScriptKeywordType.IF;}
		if("public".equals(string)){return ScriptKeywordType.PUBLIC;}
		if("class".equals(string)){return ScriptKeywordType.CLASS;}
		if("extends".equals(string)){return ScriptKeywordType.EXTENDS;}
		if("implements".equals(string)){return ScriptKeywordType.IMPLEMENTS;}
		if("short".equals(string)){return ScriptKeywordType.SHORT;}
		if("int".equals(string)){return ScriptKeywordType.INT;}
		if("long".equals(string)){return ScriptKeywordType.LONG;}
		if("float".equals(string)){return ScriptKeywordType.FLOAT;}
		if("double".equals(string)){return ScriptKeywordType.DOUBLE;}
		if("return".equals(string)){return ScriptKeywordType.RETURN;}
		if("static".equals(string)){return ScriptKeywordType.STATIC;}
		if("void".equals(string)){return ScriptKeywordType.VOID;}
		if("string".equals(string)){return ScriptKeywordType.STRING;}
		if("private".equals(string)){return ScriptKeywordType.PRIVATE;}
		if("protected".equals(string)){return ScriptKeywordType.PROTECTED;}
		if("else".equals(string)){return ScriptKeywordType.ELSE;}
		if("for".equals(string)){return ScriptKeywordType.FOR;}
		if("abstract".equals(string)){return ScriptKeywordType.ABSTRACT;}
		if("null".equals(string)){return ScriptKeywordType.NULL;}
		if("dotted".equals(string)){return ScriptKeywordType.dotted;}
		if("solid".equals(string)){return ScriptKeywordType.solid;}
		if("none".equals(string)){return ScriptKeywordType.none;}
		if("this".equals(string)){return ScriptKeywordType.THIS;}
		if("super".equals(string)){return ScriptKeywordType.SUPER;}
		return ScriptKeywordType.UNKNOWN;
	}
	public boolean nodificate(){
		assert Debugger.openNode("ScriptKeyword ("+getType()+")");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
	public String toString(){return getType().toString();}
}
