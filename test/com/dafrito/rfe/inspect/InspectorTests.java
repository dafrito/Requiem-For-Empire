/**
 * 
 */
package com.dafrito.rfe.inspect;

import org.junit.Test;

import com.dafrito.rfe.Archetype;

/**
 * @author Aaron Faanes
 * 
 */
public class InspectorTests {

	@Test
	public void annotationsProvideAutomatedInspection() throws Exception {
		Archetype arch = new Archetype(null, "Grain");
		Inspection.reflect(new NoopInspector<Object>(), arch);
	}

	@Test
	public void inspectorVisitsAnImaginaryElement() {
		Inspector<String> inspector = new PrettyXMLWriter("Root");
		inspector.comment("Hello, World!");
		inspector.field("foo", "Bar");
		Inspector<String> child = inspector.group("Children");
		for (int i = 1; i <= 3; i++) {
			child.value(String.valueOf(i));
		}
	}
}
