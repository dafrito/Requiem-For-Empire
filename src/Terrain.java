public class Terrain implements Krumflex, ScriptConvertible, Nodeable {
	private ScriptEnvironment m_environment;
	private double m_brushDensity, m_waterDepth, m_temperature, m_groundCohesion, m_elevation;
	private double m_brushDensityWeight, m_elevationWeight, m_groundCohesionWeight,
			m_temperatureWeight, m_waterDepthWeight;

	public Terrain(ScriptEnvironment env) {
		this.m_environment = env;
	}

	@Override
	public Object convert() {
		FauxTemplate_Terrain terrain = new FauxTemplate_Terrain(this.getEnvironment(), this.getEnvironment().getTemplate(FauxTemplate_Terrain.TERRAINSTRING).getType());
		terrain.setTerrain(this);
		return terrain;
	}

	public void duplicate(Terrain otherTerrain) {
		this.m_brushDensity = otherTerrain.getBrushDensity();
		this.m_brushDensityWeight = otherTerrain.getBrushDensityWeight();
		this.m_elevation = otherTerrain.getElevation();
		this.m_elevationWeight = otherTerrain.getElevationWeight();
		this.m_temperature = otherTerrain.getTemperature();
		this.m_temperatureWeight = otherTerrain.getTemperatureWeight();
		this.m_waterDepth = otherTerrain.getWaterDepth();
		this.m_waterDepthWeight = otherTerrain.getWaterDepthWeight();
		this.m_groundCohesion = otherTerrain.getGroundCohesion();
		this.m_groundCohesionWeight = otherTerrain.getGroundCohesionWeight();
	}

	public double getBrushDensity() {
		return this.m_brushDensity;
	}

	public double getBrushDensityWeight() {
		return this.m_brushDensityWeight;
	}

	public double getElevation() {
		return this.m_elevation;
	}

	public double getElevationWeight() {
		return this.m_elevationWeight;
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public double getGroundCohesion() {
		return this.m_groundCohesion;
	}

	public double getGroundCohesionWeight() {
		return this.m_groundCohesionWeight;
	}

	@Override
	public Terrain getKrumflexFromIntensity(double intensity) {
		Terrain terrain = new Terrain(this.getEnvironment());
		terrain.setBrushDensity(this.getBrushDensity(), this.getBrushDensityWeight() * intensity);
		terrain.setElevation(this.getElevation(), this.getElevationWeight() * intensity);
		terrain.setTemperature(this.getTemperature(), this.getTemperatureWeight() * intensity);
		terrain.setWaterDepth(this.getWaterDepth(), this.getWaterDepthWeight() * intensity);
		return terrain;
	}

	@Override
	public String getName() {
		return "Terrain";
	}

	public double getTemperature() {
		return this.m_temperature;
	}

	public double getTemperatureWeight() {
		return this.m_temperatureWeight;
	}

	public double getWaterDepth() {
		return this.m_waterDepth;
	}

	public double getWaterDepthWeight() {
		return this.m_waterDepthWeight;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Terrain");
		assert Debugger.addNode("Brush Density: " + this.getBrushDensity() + " (Weight: " + this.getBrushDensityWeight() + ")");
		assert Debugger.addNode("Elevation: " + this.getElevation() + " (Weight: " + this.getElevationWeight() + ")");
		assert Debugger.addNode("Ground Cohesion: " + this.getGroundCohesion() + " (Weight: " + this.getGroundCohesionWeight() + ")");
		assert Debugger.addNode("Temperature: " + this.getTemperature() + " (Weight: " + this.getTemperatureWeight() + ")");
		assert Debugger.addNode("Water Depth: " + this.getWaterDepth() + " (Weight: " + this.getWaterDepthWeight() + ")");
		assert Debugger.closeNode();
		return true;
	}

	public void setBrushDensity(double brushDensity) {
		this.m_brushDensity = brushDensity;
	}

	public void setBrushDensity(double brushDensity, double weight) {
		this.m_brushDensity = brushDensity;
		this.m_brushDensityWeight = weight;
	}

	public void setBrushDensityWeight(double bdWeight) {
		this.m_brushDensityWeight = bdWeight;
	}

	public void setElevation(double elevation) {
		this.m_elevation = elevation;
	}

	public void setElevation(double elevation, double weight) {
		this.m_elevation = elevation;
		this.m_elevationWeight = weight;
	}

	public void setElevationWeight(double elevationWeight) {
		this.m_elevationWeight = elevationWeight;
	}

	public void setGroundCohesion(double cohesion) {
		this.m_groundCohesion = cohesion;
	}

	public void setGroundCohesion(double cohesion, double weight) {
		this.m_groundCohesion = cohesion;
		this.m_groundCohesionWeight = weight;
	}

	public void setGroundCohesionWeight(double gcWeight) {
		this.m_groundCohesionWeight = gcWeight;
	}

	public void setTemperature(double temp) {
		this.m_temperature = temp;
	}

	public void setTemperature(double temp, double weight) {
		this.m_temperature = temp;
		this.m_temperatureWeight = weight;
	}

	public void setTemperatureWeight(double tempWeight) {
		this.m_temperatureWeight = tempWeight;
	}

	public void setWaterDepth(double waterDepth) {
		this.m_waterDepth = waterDepth;
	}

	public void setWaterDepth(double waterDepth, double weight) {
		this.m_waterDepth = waterDepth;
		this.m_waterDepthWeight = weight;
	}

	public void setWaterDepthWeight(double waterDepthWeight) {
		this.m_waterDepthWeight = waterDepthWeight;
	}
}
