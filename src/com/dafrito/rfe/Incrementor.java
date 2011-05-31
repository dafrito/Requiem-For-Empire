/**
 * 
 */
package com.dafrito.rfe;

public class Incrementor {
	private int value;

	public Incrementor() {
		this(0);
	}

	public Incrementor(int initial) {
		this.value = initial;
	}

	public int getValue() {
		return this.value;
	}

	public void increment() {
		this.increment(1);
	}

	public void increment(int value) {
		this.value += value;
	}
}