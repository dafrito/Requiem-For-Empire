import java.util.List;
public class PolygonPipeline extends Thread{
	private DiscreteRegion m_region;
	private Terrestrial m_terrestrial;
	public static final String POLYGONPIPELINESTRING="Polygon Pipeline";
	private static int m_threadNum=0;
	public PolygonPipeline(Terrestrial panel,DiscreteRegion region){
		super(POLYGONPIPELINESTRING+" "+m_threadNum++);
		m_terrestrial=panel;
		m_region=region;
	}
	public void run(){
		Debugger.hitStopWatch(Thread.currentThread().getName());
		assert Debugger.openNode("Polygon Pipeline Executions","Executing Polygon Pipeline");
		assert Debugger.addNode(m_region);
		if(m_region==null){
			assert Debugger.closeNode("Region is null.");
			return;
		}
		List<DiscreteRegion>polygonList = RiffPolygonToolbox.convertPolyToConvex(m_region);
		if(polygonList==null){
			assert Debugger.closeNode("Region is null.");
			return;
		}
		polygonList=RiffPolygonToolbox.joinPolygons(polygonList);
		polygonList=RiffPolygonToolbox.optimizePolygons(polygonList);
		m_terrestrial.addValidatedRegions(polygonList);
		assert Debugger.closeNode();
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}
}
