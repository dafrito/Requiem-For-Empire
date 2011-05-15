public abstract class Order {
	protected double m_iterationTime;
	protected int m_orderStatus;

	public Order() {
		this.m_orderStatus = OrderStatus.INCOMPLETE;
	}

	public void execute(double iterationTime) throws OrderException {
		this.setIterationTime(iterationTime);
	}

	public double getIterationTime() {
		return this.m_iterationTime;
	}

	public int getStatus() {
		return this.m_orderStatus;
	}

	public void setIterationTime(double time) {
		this.m_iterationTime = time;
	}

	public void setStatus(int status) {
		this.m_orderStatus = status;
	}

	public void subtractTimeFromIterationTime(int time) {
		this.m_iterationTime -= time;
	}
}
