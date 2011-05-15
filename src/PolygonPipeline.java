import java.util.List;

public class PolygonPipeline extends Thread {
	private DiscreteRegion m_region;
	private Terrestrial m_terrestrial;
	public static final String POLYGONPIPELINESTRING = "Polygon Pipeline";
	private static int m_threadNum = 0;

	public PolygonPipeline(Terrestrial panel, DiscreteRegion region) {
		super(POLYGONPIPELINESTRING + " " + m_threadNum++);
		this.m_terrestrial = panel;
		this.m_region = region;
	}

	@Override
	public void run() {
		Debugger.hitStopWatch(Thread.currentThread().getName());
		assert Debugger.openNode("Polygon Pipeline Executions", "Executing Polygon Pipeline");
		assert Debugger.addNode(this.m_region);
		if (this.m_region == null) {
			assert Debugger.closeNode("Region is null.");
			return;
		}
		List<DiscreteRegion> polygonList = RiffPolygonToolbox.convertPolyToConvex(this.m_region);
		if (polygonList == null) {
			assert Debugger.closeNode("Region is null.");
			return;
		}
		polygonList = RiffPolygonToolbox.joinPolygons(polygonList);
		polygonList = RiffPolygonToolbox.optimizePolygons(polygonList);
		this.m_terrestrial.addValidatedRegions(polygonList);
		assert Debugger.closeNode();
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}
}
