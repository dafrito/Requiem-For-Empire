package com.dafrito.rfe.script;

import java.awt.Color;
import java.util.List;

import com.dafrito.rfe.Ace;
import com.dafrito.rfe.Archetype;
import com.dafrito.rfe.Asset;
import com.dafrito.rfe.Scenario;
import com.dafrito.rfe.Terrain;
import com.dafrito.rfe.Terrestrial;
import com.dafrito.rfe.actions.Scheduler;
import com.dafrito.rfe.geom.DiscreteRegion;
import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.geom.points.Point_Path;
import com.dafrito.rfe.gui.GraphicalElement;
import com.dafrito.rfe.gui.GraphicalElement_Line;
import com.dafrito.rfe.gui.InterfaceElement;
import com.dafrito.rfe.gui.style.Stylesheet;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.proxies.FauxTemplate_Ace;
import com.dafrito.rfe.script.proxies.FauxTemplate_Archetype;
import com.dafrito.rfe.script.proxies.FauxTemplate_Asset;
import com.dafrito.rfe.script.proxies.FauxTemplate_Color;
import com.dafrito.rfe.script.proxies.FauxTemplate_DiscreteRegion;
import com.dafrito.rfe.script.proxies.FauxTemplate_InterfaceElement;
import com.dafrito.rfe.script.proxies.FauxTemplate_Line;
import com.dafrito.rfe.script.proxies.FauxTemplate_List;
import com.dafrito.rfe.script.proxies.FauxTemplate_Path;
import com.dafrito.rfe.script.proxies.FauxTemplate_Point;
import com.dafrito.rfe.script.proxies.FauxTemplate_Scheduler;
import com.dafrito.rfe.script.proxies.FauxTemplate_Terrain;
import com.dafrito.rfe.script.proxies.FauxTemplate_Terrestrial;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Boolean;
import com.dafrito.rfe.script.values.ScriptValue_Numeric;
import com.dafrito.rfe.script.values.ScriptValue_String;

/**
 * A collection of methods that convert between script values and raw objects.
 * 
 * @author Aaron Faanes
 * 
 */
public final class Conversions {

	private Conversions() {
		throw new AssertionError("Instantiation is not allowed");
	}

	public static Object convert(ScriptEnvironment env, boolean value) throws ScriptException {
		return new ScriptValue_Boolean(env, value);
	}

	public static Object convert(ScriptEnvironment env, Color color) throws ScriptException {
		FauxTemplate_Color fauxColor = new FauxTemplate_Color(env, ScriptValueType.createType(env, FauxTemplate_Color.COLORSTRING));
		fauxColor.setColor(color);
		return fauxColor;
	}

	public static Object convert(ScriptEnvironment env, double num) throws ScriptException {
		return new ScriptValue_Numeric(env, Double.valueOf(num));
	}

	public static Object convert(ScriptEnvironment env, float num) throws ScriptException {
		return new ScriptValue_Numeric(env, Float.valueOf(num));
	}

	public static Object convert(ScriptEnvironment env, int num) throws ScriptException {
		return new ScriptValue_Numeric(env, Integer.valueOf(num));
	}

	public static Object convert(ScriptEnvironment env, List<ScriptValue> elements) throws ScriptException {
		FauxTemplate_List list = new FauxTemplate_List(env, ScriptValueType.createType(env, FauxTemplate_List.LISTSTRING));
		list.setList(elements);
		return list;
	}

	public static Object convert(ScriptEnvironment env, long num) throws ScriptException {
		return new ScriptValue_Numeric(env, Long.valueOf(num));
	}

	// Generic-conversion fxns
	public static Object convert(ScriptEnvironment env, Number num) throws ScriptException {
		return new ScriptValue_Numeric(env, num);
	}

	public static Object convert(ScriptEnvironment env, Object object) throws ScriptException {
		if (env == null) {
			throw new NullPointerException("env must not be null");
		}
		if (object instanceof ScriptConvertible<?>) {
			return convert(env, (ScriptConvertible<?>) object);
		}
		if (object instanceof Terrain) {
			// XXX This is a serious hack. Our RiffScript uses a getProperty(String):Object that returns
			// a terrain. Unfortunately, we don't have a way to wrap objects of arbitrary type; we need to
			// know the type at compile-time. As a result, we end up defaulting in this method.
			//
			// It turns out that the property returned is a terrain, so my "fix" is to explicitly check for
			// terrains here and convert them properly. Ordinarily, this wouldn't be sufficient, but I have
			// plans for a much better system of conversion that will fix this issue.
			return getRiffTerrain(env, (Terrain) object);
		}
		return convert(env, (ScriptConvertible<?>) ((ScriptValue) object).getValue());
	}

	public static <T> T convert(ScriptEnvironment env, ScriptConvertible<T> object) throws ScriptException {
		return object.convert(env);
	}

	public static Object convert(ScriptEnvironment env, short num) throws ScriptException {
		return new ScriptValue_Numeric(env, Short.valueOf(num));
	}

	public static Object convert(ScriptEnvironment env, String string) throws ScriptException {
		return new ScriptValue_String(env, string);
	}

	public static Ace getAce(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Ace) convert(env, obj);
	}

	public static Archetype getArchetype(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Archetype) convert(env, obj);
	}

	public static Asset getAsset(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Asset) convert(env, obj);
	}

	public static Boolean getBoolean(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Boolean) convert(env, obj);
	}

	public static Color getColor(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Color) convert(env, obj);
	}

	// Conversion assistance functions
	public static ScriptValue getCoreValue(Object obj) throws ScriptException {
		return ((ScriptValue) obj).getValue();
	}

	public static DiscreteRegion getDiscreteRegion(ScriptEnvironment env, Object obj) throws ScriptException {
		return (DiscreteRegion) convert(env, obj);
	}

	public static Double getDouble(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Double) convert(env, ((ScriptValue) obj).castToType(null, ScriptValueType.DOUBLE));
	}

	public static InterfaceElement getElement(ScriptEnvironment env, Object obj) throws ScriptException {
		return (InterfaceElement) convert(env, obj);
	}

	public static Float getFloat(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Float) convert(env, ((ScriptValue) obj).castToType(null, ScriptValueType.FLOAT));
	}

	public static GraphicalElement getGraphicalElement(ScriptEnvironment env, Object obj) throws ScriptException {
		return (GraphicalElement) convert(env, obj);
	}

	public static Integer getInteger(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Integer) convert(env, ((ScriptValue) obj).castToType(null, ScriptValueType.INT));
	}

	public static List<ScriptValue> getList(Object obj) throws ScriptException {
		return ((FauxTemplate_List) getCoreValue(obj)).getList();
	}

	public static Long getLong(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Long) convert(env, ((ScriptValue) obj).castToType(null, ScriptValueType.LONG));
	}

	public static Number getNumber(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Number) convert(env, obj);
	}

	public static Object getObject(ScriptEnvironment env, Object obj) throws ScriptException {
		return convert(env, obj);
	}

	public static Point getPoint(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Point) convert(env, obj);
	}

	// Engine->Script Conversion functions
	public static FauxTemplate_Ace getRiffAce(ScriptEnvironment env, Ace ace) throws ScriptException {
		FauxTemplate_Ace wrappedAce = new FauxTemplate_Ace(env, ScriptValueType.createType(env, FauxTemplate_Ace.ACESTRING));
		wrappedAce.setAce(ace);
		return wrappedAce;
	}

	public static FauxTemplate_Archetype getRiffArchetype(ScriptEnvironment env, Archetype archetype) throws ScriptException {
		FauxTemplate_Archetype wrapped = new FauxTemplate_Archetype(env, ScriptValueType.createType(env, FauxTemplate_Archetype.ARCHETYPESTRING));
		wrapped.setArchetype(archetype);
		return wrapped;
	}

	public static FauxTemplate_Asset getRiffAsset(ScriptEnvironment env, Asset asset) throws ScriptException {
		FauxTemplate_Asset wrapped = new FauxTemplate_Asset(env, env.getTemplate(FauxTemplate_Asset.ASSETSTRING).getType());
		wrapped.setAsset(asset);
		return wrapped;
	}

	public static ScriptValue_Boolean getRiffBoolean(ScriptEnvironment env, boolean value) throws ScriptException {
		return (ScriptValue_Boolean) convert(env, value);
	}

	public static FauxTemplate_Color getRiffColor(ScriptEnvironment env, Color color) throws ScriptException {
		return (FauxTemplate_Color) convert(env, color);
	}

	public static FauxTemplate_Color getRiffColor(ScriptEnvironment env, Object obj) throws ScriptException {
		return (FauxTemplate_Color) convert(env, obj);
	}

	public static FauxTemplate_DiscreteRegion getRiffDiscreteRegion(ScriptEnvironment env, DiscreteRegion region) throws ScriptException {
		FauxTemplate_DiscreteRegion wrapped = new FauxTemplate_DiscreteRegion(env, env.getTemplate(FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING).getType());
		wrapped.setRegion(region);
		return wrapped;
	}

	public static ScriptValue_Numeric getRiffDouble(ScriptEnvironment env, double value) throws ScriptException {
		return (ScriptValue_Numeric) convert(env, value);
	}

	public static FauxTemplate_InterfaceElement getRiffElement(ScriptEnvironment env, InterfaceElement elem) throws ScriptException {
		FauxTemplate_InterfaceElement wrapped = new FauxTemplate_InterfaceElement(env, ScriptValueType.createType(env, FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING));
		wrapped.setElement(elem);
		return wrapped;
	}

	public static ScriptValue_Numeric getRiffFloat(ScriptEnvironment env, float value) throws ScriptException {
		return (ScriptValue_Numeric) convert(env, value);
	}

	public static ScriptValue_Numeric getRiffInt(ScriptEnvironment env, int value) throws ScriptException {
		return (ScriptValue_Numeric) convert(env, value);
	}

	public static FauxTemplate_Line getRiffLine(ScriptEnvironment env, GraphicalElement_Line line) {
		FauxTemplate_Line wrapper = new FauxTemplate_Line(env);
		wrapper.setPointA(line.getPointA());
		wrapper.setPointB(line.getPointB());
		return wrapper;
	}

	public static FauxTemplate_List getRiffList(ScriptEnvironment env, List<ScriptValue> list) throws ScriptException {
		return (FauxTemplate_List) convert(env, list);
	}

	public static ScriptValue_Numeric getRiffLong(ScriptEnvironment env, long value) throws ScriptException {
		return (ScriptValue_Numeric) convert(env, value);
	}

	public static ScriptValue_Numeric getRiffNumber(ScriptEnvironment env, Number value) throws ScriptException {
		return (ScriptValue_Numeric) convert(env, value);
	}

	public static FauxTemplate_Path getRiffPath(ScriptEnvironment env, Point_Path path) throws ScriptException {
		FauxTemplate_Path wrapped = new FauxTemplate_Path(env, ScriptValueType.createType(env, FauxTemplate_Path.PATHSTRING));
		wrapped.setPoint(path);
		return wrapped;
	}

	public static FauxTemplate_Point getRiffPoint(ScriptEnvironment env, Point point) throws ScriptException {
		FauxTemplate_Point wrapped = new FauxTemplate_Point(env);
		wrapped.setPoint(point);
		return wrapped;
	}

	public static FauxTemplate_Scheduler getRiffScheduler(ScriptEnvironment env, Scheduler scheduler) throws ScriptException {
		FauxTemplate_Scheduler wrapped = new FauxTemplate_Scheduler(env, ScriptValueType.createType(env, FauxTemplate_Scheduler.SCHEDULERSTRING));
		wrapped.setScheduler(scheduler);
		return wrapped;
	}

	public static ScriptValue_Numeric getRiffShort(ScriptEnvironment env, short value) throws ScriptException {
		return (ScriptValue_Numeric) convert(env, value);
	}

	public static ScriptValue_String getRiffString(ScriptEnvironment env, String value) throws ScriptException {
		return (ScriptValue_String) convert(env, value);
	}

	public static Stylesheet getRiffStylesheet(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Stylesheet) convert(env, obj);
	}

	public static FauxTemplate_Terrain getRiffTerrain(ScriptEnvironment env, Terrain terrain) throws ScriptException {
		FauxTemplate_Terrain wrapped = new FauxTemplate_Terrain(env, ScriptValueType.createType(env, FauxTemplate_Terrain.TERRAINSTRING));
		wrapped.setTerrain(terrain);
		return wrapped;
	}

	public static FauxTemplate_Terrestrial getRiffTerrestrial(ScriptEnvironment env, Terrestrial terrestrial) throws ScriptException {
		FauxTemplate_Terrestrial wrapped = new FauxTemplate_Terrestrial(env, ScriptValueType.createType(env, FauxTemplate_Terrestrial.TERRESTRIALSTRING));
		wrapped.setTerrestrial(terrestrial);
		return wrapped;
	}

	public static Scenario getScenario(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Scenario) convert(env, obj);
	}

	public static Scheduler getScheduler(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Scheduler) convert(env, obj);
	}

	public static ScriptTemplate_Abstract getSchedulerListener(Object obj) throws ScriptException {
		return (ScriptTemplate_Abstract) getCoreValue(obj);
	}

	public static Short getShort(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Short) convert(env, ((ScriptValue) obj).castToType(null, ScriptValueType.SHORT));
	}

	// Script->Engine conversion functions
	public static String getString(ScriptEnvironment env, Object obj) throws ScriptException {
		return (String) convert(env, obj);
	}

	public static Stylesheet getStylesheet(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Stylesheet) convert(env, obj);
	}

	public static ScriptTemplate_Abstract getTemplate(Object obj) throws ScriptException {
		return (ScriptTemplate_Abstract) ((ScriptValue) obj).getValue();
	}

	public static Terrestrial getTerrestrial(ScriptEnvironment env, Object obj) throws ScriptException {
		return (Terrestrial) convert(env, obj);
	}
}
