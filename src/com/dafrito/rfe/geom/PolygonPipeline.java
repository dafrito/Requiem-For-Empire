package com.dafrito.rfe.geom;

import java.util.List;

import com.dafrito.rfe.Terrestrial;
import com.dafrito.rfe.gui.debug.Debugger;

public class PolygonPipeline extends Thread {
	public static final String POLYGONPIPELINESTRING = "Polygon Pipeline";
	private static int threadNum = 0;

	private final DiscreteRegion region;
	private final Terrestrial terrestrial;

	public PolygonPipeline(final Terrestrial panel, final DiscreteRegion region) {
		super(POLYGONPIPELINESTRING + " " + threadNum++);
		if (panel == null) {
			throw new NullPointerException("panel must not be null");
		}
		if (region == null) {
			throw new NullPointerException("region must not be null");
		}
		this.terrestrial = panel;
		this.region = region;
	}

	@Override
	public void run() {
		Debugger.hitStopWatch();
		assert Debugger.openNode("Polygon Pipeline Executions", "Executing Polygon Pipeline");
		assert Debugger.addNode(this.region);
		List<DiscreteRegion> polygonList = Polygons.convertPolyToConvex(this.region);
		if (polygonList == null) {
			assert Debugger.closeNode("Region was degenerate");
			return;
		}
		polygonList = Polygons.joinPolygons(polygonList);
		polygonList = Polygons.optimizePolygons(polygonList);
		this.terrestrial.addValidatedRegions(polygonList);
		assert Debugger.closeNode();
		Debugger.hitStopWatch();
	}
}
