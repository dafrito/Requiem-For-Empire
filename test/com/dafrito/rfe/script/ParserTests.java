/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Aaron Faanes
 * 
 */
public class ParserTests {

	@Test
	public void listIteratorAllowsMultipleAdditions() throws Exception {
		List<Object> foo = new LinkedList<Object>();
		foo.add(1);
		foo.add(1);
		foo.add(1);

		ListIterator<Object> i = foo.listIterator();
		i.next();
		i.add(2);
		i.add(3);
		Assert.assertEquals(Arrays.asList(1, 2, 3, 1, 1), foo);
	}
}
