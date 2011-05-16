package com.dafrito.rfe;
public class OrderStatus {
	protected static final int COMPLETE = 0;
	protected static final int GENERALFAILURE = 1;
	protected static final int MISSINGRESOURCE = 2;
	protected static final int INCOMPLETE = 3;
	protected static final int READY = 4;
	protected final int statusCode;

	public OrderStatus(int code) {
		this.statusCode = code;
	}

	public int getOrderStatus() {
		return this.statusCode;
	}
}
