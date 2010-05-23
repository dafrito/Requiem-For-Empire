package com.dafrito.economy;

public abstract class Order {
    protected double iterationTime;
    protected int orderStatus;

    public Order() {
        this.orderStatus = OrderStatus.INCOMPLETE;
    }

    public double getIterationTime() {
        return this.iterationTime;
    }

    public void setIterationTime(double time) {
        this.iterationTime = time;
    }

    /**
     * TODO Document Order.execute
     * 
     * When do these exceptions fire? What happens in this function? 
     * 
     * @param time Iteration time
     * @throws OrderException Thrown sometimes
     */
    public void execute(double time) throws OrderException {
        this.setIterationTime(time);
    }

    public void subtractTimeFromIterationTime(int time) {
        this.iterationTime -= time;
    }

    public int getStatus() {
        return this.orderStatus;
    }

    public void setStatus(int status) {
        this.orderStatus = status;
    }
}
