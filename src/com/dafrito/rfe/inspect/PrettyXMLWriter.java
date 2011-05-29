/**
 * 
 */
package com.dafrito.rfe.inspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds up a XML tree from visited content.
 * 
 * @author Aaron Faanes
 */
public class PrettyXMLWriter implements Inspector<String> {

	private final String name;
	private final Map<String, String> attributes = new HashMap<String, String>();

	private final List<Object> children = new ArrayList<Object>();

	public PrettyXMLWriter(String name) {
		this.name = name;
	}

	@Override
	public void field(String name, String value) {
		this.attributes.put(name, value);
	}

	@Override
	public void value(String value) {
		this.children.add(value);
	}

	@Override
	public Inspector<String> group(String groupName) {
		PrettyXMLWriter writer = new PrettyXMLWriter(groupName);
		this.children.add(writer);
		return writer;
	}

	@Override
	public void comment(String note) {
		this.children.add(String.format("<!-- %s -->", note));
	}

	public String toString(final String indent) {
		StringBuilder builder = new StringBuilder();
		this.toString(indent, 0, builder);
		return builder.toString();
	}

	public void toString(final String indent, final int levels, final StringBuilder builder) {
		StringBuilder indt = new StringBuilder(indent.length() * levels);
		for (int i = 0; i < levels; i++) {
			indt.append(indent);
		}
		builder.append(indt).append('<').append(this.name);
		for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
			builder.append(' ').append(entry.getKey()).append('=');
			builder.append('"').append(entry.getValue()).append('"');
		}
		builder.append(">\n");
		for (Object child : this.children) {
			if (child instanceof PrettyXMLWriter) {
				((PrettyXMLWriter) child).toString(indent, levels + 1, builder);
			} else {
				builder.append(indt).append(indent).append(child).append('\n');
			}
		}
		builder.append(indt).append("</").append(this.name).append(">\n");
	}

	@Override
	public String toString() {
		return this.toString("\t");
	}
}
