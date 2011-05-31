/**
 * 
 */
package com.dafrito.rfe.inspect;

import java.lang.reflect.Method;

/**
 * A collection of methods that deal with inspection.
 * 
 * @author Aaron Faanes
 * @see Inspector
 */
public final class Inspection {

	private Inspection() {
		throw new AssertionError("Instantiation is not allowed");
	}

	/**
	 * Inspect the specified object using reflection. Methods marked with
	 * {@link Inspectable} will be inspected.
	 * 
	 * @param parentInspector
	 *            the inspector used to inspect the object
	 * @param target
	 *            the inspected object
	 * @throws InspectionException
	 *             if reflected method invocation fails
	 */
	public static void reflect(Inspector<Object> parentInspector, Object target) throws InspectionException {
		if (target == null) {
			parentInspector.value("null");
			return;
		}
		Inspectable typeInspectable = target.getClass().getAnnotation(Inspectable.class);
		if (typeInspectable == null) {
			parentInspector.value(target.toString());
			return;
		}
		Inspector<Object> inspector = parentInspector.group(target.toString());
		for (Method method : target.getClass().getMethods()) {
			Inspectable inspectable = method.getAnnotation(Inspectable.class);
			if (inspectable == null) {
				continue;
			}
			if (method.getParameterTypes().length > 0) {
				throw new UnsupportedOperationException("Inspectable method must not require parameters");
			}
			String logicalName = inspectable.value();
			if (logicalName.equals("")) {
				logicalName = method.getName();
			}
			Class<?> returned = method.getReturnType();
			try {
				if (Iterable.class.isAssignableFrom(returned)) {
					Inspector<Object> groupInspector = inspector.group(logicalName);
					for (Object v : (Iterable<?>) method.invoke(target)) {
						groupInspector.value(v);
					}
				} else {
					inspector.field(logicalName, method.invoke(target));
				}
			} catch (Exception e) {
				throw new InspectionException(e);
			}
		}
	}
}
