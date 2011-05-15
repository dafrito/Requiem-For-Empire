public class Terrain implements Krumflex,ScriptConvertible,Nodeable{
	private ScriptEnvironment m_environment;
	private double m_brushDensity, m_waterDepth, m_temperature, m_groundCohesion, m_elevation;
	private double m_brushDensityWeight, m_elevationWeight, m_groundCohesionWeight, m_temperatureWeight, m_waterDepthWeight;
	public Terrain(ScriptEnvironment env){
		m_environment=env;
	}
	public void duplicate(Terrain otherTerrain){
		m_brushDensity=otherTerrain.getBrushDensity();
		m_brushDensityWeight=otherTerrain.getBrushDensityWeight();
		m_elevation=otherTerrain.getElevation();
		m_elevationWeight=otherTerrain.getElevationWeight();
		m_temperature=otherTerrain.getTemperature();
		m_temperatureWeight=otherTerrain.getTemperatureWeight();
		m_waterDepth=otherTerrain.getWaterDepth();
		m_waterDepthWeight=otherTerrain.getWaterDepthWeight();
		m_groundCohesion=otherTerrain.getGroundCohesion();
		m_groundCohesionWeight=otherTerrain.getGroundCohesionWeight();
	}
	public String getName(){return "Terrain";}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public Terrain getKrumflexFromIntensity(double intensity){
		Terrain terrain=new Terrain(getEnvironment());
		terrain.setBrushDensity(getBrushDensity(),getBrushDensityWeight()*intensity);
		terrain.setElevation(getElevation(),getElevationWeight()*intensity);
		terrain.setTemperature(getTemperature(),getTemperatureWeight()*intensity);
		terrain.setWaterDepth(getWaterDepth(),getWaterDepthWeight()*intensity);
		return terrain;
	}
	public double getBrushDensityWeight(){return m_brushDensityWeight;}
	public double getElevationWeight(){return m_elevationWeight;}
	public double getGroundCohesionWeight(){return m_groundCohesionWeight;}
	public double getTemperatureWeight(){return m_temperatureWeight;}
	public double getWaterDepthWeight(){return m_waterDepthWeight;}
	public void setWaterDepthWeight(double waterDepthWeight){m_waterDepthWeight=waterDepthWeight;}
	public void setElevationWeight(double elevationWeight){m_elevationWeight=elevationWeight;}
	public void setGroundCohesionWeight(double gcWeight){m_groundCohesionWeight=gcWeight;}
	public void setTemperatureWeight(double tempWeight){m_temperatureWeight=tempWeight;}
	public void setBrushDensityWeight(double bdWeight){m_brushDensityWeight=bdWeight;}
	public double getBrushDensity(){return m_brushDensity;}
	public double getElevation(){return m_elevation;}
	public double getGroundCohesion(){return m_groundCohesion;}
	public double getTemperature(){return m_temperature;}
	public double getWaterDepth(){return m_waterDepth;}
	public void setBrushDensity(double brushDensity){m_brushDensity=brushDensity;}
	public void setBrushDensity(double brushDensity,double weight){
		m_brushDensity=brushDensity;
		m_brushDensityWeight=weight;
	}
	public void setElevation(double elevation){m_elevation=elevation;}
	public void setElevation(double elevation,double weight){
		m_elevation=elevation;
		m_elevationWeight=weight;
	}
	public void setGroundCohesion(double cohesion){m_groundCohesion=cohesion;}
	public void setGroundCohesion(double cohesion,double weight){
		m_groundCohesion=cohesion;
		m_groundCohesionWeight=weight;
	}
	public void setTemperature(double temp){m_temperature=temp;}
	public void setTemperature(double temp,double weight){
		m_temperature=temp;
		m_temperatureWeight=weight;
	}
	public void setWaterDepth(double waterDepth){m_waterDepth=waterDepth;}
	public void setWaterDepth(double waterDepth,double weight){
		m_waterDepth=waterDepth;
		m_waterDepthWeight=weight;
	}
	public boolean nodificate(){
		assert Debugger.openNode("Terrain");
		assert Debugger.addNode("Brush Density: "+getBrushDensity()+" (Weight: "+getBrushDensityWeight()+")");
		assert Debugger.addNode("Elevation: "+getElevation()+" (Weight: "+getElevationWeight()+")");
		assert Debugger.addNode("Ground Cohesion: "+getGroundCohesion()+" (Weight: "+getGroundCohesionWeight()+")");
		assert Debugger.addNode("Temperature: "+getTemperature()+" (Weight: "+getTemperatureWeight()+")");
		assert Debugger.addNode("Water Depth: "+getWaterDepth()+" (Weight: "+getWaterDepthWeight()+")");
		assert Debugger.closeNode();
		return true;
	}
	public Object convert(){
		FauxTemplate_Terrain terrain=new FauxTemplate_Terrain(getEnvironment(),getEnvironment().getTemplate(FauxTemplate_Terrain.TERRAINSTRING).getType());
		terrain.setTerrain(this);
		return terrain;
	}
}
