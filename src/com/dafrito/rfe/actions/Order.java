package com.dafrito.rfe.actions;

public abstract class Order {
	protected double iterationTime;
	protected OrderStatus orderStatus;

	public Order() {
		this.orderStatus = OrderStatus.INCOMPLETE;
	}

	public void execute(double iterationTime) throws OrderException {
		this.setIterationTime(iterationTime);
	}

	public double getIterationTime() {
		return this.iterationTime;
	}

	public OrderStatus getStatus() {
		return this.orderStatus;
	}

	public void setIterationTime(double time) {
		this.iterationTime = time;
	}

	public void setStatus(OrderStatus status) {
		this.orderStatus = status;
	}

	public void subtractTimeFromIterationTime(int time) {
		this.iterationTime -= time;
	}
}
