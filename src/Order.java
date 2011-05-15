public abstract class Order{
	protected double m_iterationTime;
	protected int m_orderStatus;
	public Order(){
		m_orderStatus=OrderStatus.INCOMPLETE;
	}
	public double getIterationTime(){return m_iterationTime;}
	public void setIterationTime(double time){m_iterationTime=time;}
	public void execute(double iterationTime) throws OrderException{
		setIterationTime(iterationTime);
	}
	public void subtractTimeFromIterationTime(int time){m_iterationTime-=time;}
	public int getStatus(){return m_orderStatus;}
	public void setStatus(int status){m_orderStatus=status;}
}
