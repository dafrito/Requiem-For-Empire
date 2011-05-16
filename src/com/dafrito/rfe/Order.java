package com.dafrito.rfe;
public abstract class Order {
	protected double iterationTime;
	protected int orderStatus;

	public Order() {
		this.orderStatus = OrderStatus.INCOMPLETE;
	}

	public void execute(double iterationTime) throws OrderException {
		this.setIterationTime(iterationTime);
	}

	public double getIterationTime() {
		return this.iterationTime;
	}

	public int getStatus() {
		return this.orderStatus;
	}

	public void setIterationTime(double time) {
		this.iterationTime = time;
	}

	public void setStatus(int status) {
		this.orderStatus = status;
	}

	public void subtractTimeFromIterationTime(int time) {
		this.iterationTime -= time;
	}
}
