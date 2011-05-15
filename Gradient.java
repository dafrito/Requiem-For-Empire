public interface Gradient{
	public void setKrumflex(Krumflex flex);
	public Krumflex getKrumflex();
	public Krumflex getKrumflexAt(Point point);
	public java.util.List<DiscreteRegion>getRegions(double precision);
	public double getLeftExtreme();
	public double getRightExtreme();
	public double getTopExtreme();
	public double getBottomExtreme();
}
