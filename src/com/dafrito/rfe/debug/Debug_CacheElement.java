/**
 * 
 */
package com.dafrito.rfe.debug;

class Debug_CacheElement implements Comparable<Debug_CacheElement> {
	private int accesses = 1;
	private Object data;

	public Debug_CacheElement(Object data, int value) {
		this.data = data;
		this.accesses = value;
	}

	@Override
	public int compareTo(Debug_CacheElement elem) {
		if (this.accesses < elem.getAccessed()) {
			return -1;
		}
		if (this.accesses == elem.getAccessed()) {
			return 0;
		}
		return 1;
	}

	public int getAccessed() {
		return this.accesses;
	}

	public Object getData() {
		return this.data;
	}

	public void increment() {
		this.accesses++;
	}

	@Override
	public String toString() {
		return "(Used " + this.accesses + " time(s)) '" + this.data + "'";
	}
}