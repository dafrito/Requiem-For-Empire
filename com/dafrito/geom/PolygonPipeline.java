package com.dafrito.geom;

import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Terrestrial;
public class PolygonPipeline extends Thread{
	private DiscreteRegion m_region;
	private Terrestrial m_terrestrial;
	public static final String POLYGONPIPELINESTRING="Polygon Pipeline";
	private static int m_threadNum=0;
	public PolygonPipeline(Terrestrial panel,DiscreteRegion region){
		super(POLYGONPIPELINESTRING+" "+m_threadNum++);
		this.m_terrestrial=panel;
		this.m_region=region;
	}
	@Override
    public void run(){
		LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
		assert LegacyDebugger.open("Polygon Pipeline Executions","Executing Polygon Pipeline");
		assert LegacyDebugger.addNode(this.m_region);
		if(this.m_region==null){
			assert LegacyDebugger.log("Region is null.");
            assert LegacyDebugger.close("Polygon Pipeline thread complete");
			return;
		}
		List<DiscreteRegion>polygonList = RiffPolygonToolbox.convertPolyToConvex(this.m_region);
		if(polygonList==null){
			assert LegacyDebugger.log("Region is null.");
	        assert LegacyDebugger.close("Polygon Pipeline thread complete");
			return;
		}
		polygonList=RiffPolygonToolbox.joinPolygons(polygonList);
		polygonList=RiffPolygonToolbox.optimizePolygons(polygonList);
		this.m_terrestrial.addValidatedRegions(polygonList);
		assert LegacyDebugger.close("Polygon Pipeline thread complete");
		LegacyDebugger.hitStopWatch(Thread.currentThread().getName());
	}
}
