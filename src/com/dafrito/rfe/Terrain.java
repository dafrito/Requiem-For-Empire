package com.dafrito.rfe;

import com.dafrito.rfe.geom.GradientValue;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptEnvironment;

public class Terrain implements GradientValue<Terrain>, Nodeable {
	private ScriptEnvironment environment;
	private double brushDensity, waterDepth, temperature, groundCohesion, elevation;
	private double brushDensityWeight, elevationWeight, groundCohesionWeight,
			temperatureWeight, waterDepthWeight;

	public void duplicate(Terrain otherTerrain) {
		this.brushDensity = otherTerrain.getBrushDensity();
		this.brushDensityWeight = otherTerrain.getBrushDensityWeight();
		this.elevation = otherTerrain.getElevation();
		this.elevationWeight = otherTerrain.getElevationWeight();
		this.temperature = otherTerrain.getTemperature();
		this.temperatureWeight = otherTerrain.getTemperatureWeight();
		this.waterDepth = otherTerrain.getWaterDepth();
		this.waterDepthWeight = otherTerrain.getWaterDepthWeight();
		this.groundCohesion = otherTerrain.getGroundCohesion();
		this.groundCohesionWeight = otherTerrain.getGroundCohesionWeight();
	}

	public double getBrushDensity() {
		return this.brushDensity;
	}

	public double getBrushDensityWeight() {
		return this.brushDensityWeight;
	}

	public double getElevation() {
		return this.elevation;
	}

	public double getElevationWeight() {
		return this.elevationWeight;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public double getGroundCohesion() {
		return this.groundCohesion;
	}

	public double getGroundCohesionWeight() {
		return this.groundCohesionWeight;
	}

	@Override
	public Terrain sample(double intensity) {
		Terrain terrain = new Terrain();
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
		return this.temperature;
	}

	public double getTemperatureWeight() {
		return this.temperatureWeight;
	}

	public double getWaterDepth() {
		return this.waterDepth;
	}

	public double getWaterDepthWeight() {
		return this.waterDepthWeight;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Terrain");
		assert Debugger.addNode("Brush Density: " + this.getBrushDensity() + " (Weight: " + this.getBrushDensityWeight() + ")");
		assert Debugger.addNode("Elevation: " + this.getElevation() + " (Weight: " + this.getElevationWeight() + ")");
		assert Debugger.addNode("Ground Cohesion: " + this.getGroundCohesion() + " (Weight: " + this.getGroundCohesionWeight() + ")");
		assert Debugger.addNode("Temperature: " + this.getTemperature() + " (Weight: " + this.getTemperatureWeight() + ")");
		assert Debugger.addNode("Water Depth: " + this.getWaterDepth() + " (Weight: " + this.getWaterDepthWeight() + ")");
		assert Debugger.closeNode();
	}

	public void setBrushDensity(double brushDensity) {
		this.brushDensity = brushDensity;
	}

	public void setBrushDensity(double brushDensity, double weight) {
		this.brushDensity = brushDensity;
		this.brushDensityWeight = weight;
	}

	public void setBrushDensityWeight(double bdWeight) {
		this.brushDensityWeight = bdWeight;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public void setElevation(double elevation, double weight) {
		this.elevation = elevation;
		this.elevationWeight = weight;
	}

	public void setElevationWeight(double elevationWeight) {
		this.elevationWeight = elevationWeight;
	}

	public void setGroundCohesion(double cohesion) {
		this.groundCohesion = cohesion;
	}

	public void setGroundCohesion(double cohesion, double weight) {
		this.groundCohesion = cohesion;
		this.groundCohesionWeight = weight;
	}

	public void setGroundCohesionWeight(double gcWeight) {
		this.groundCohesionWeight = gcWeight;
	}

	public void setTemperature(double temp) {
		this.temperature = temp;
	}

	public void setTemperature(double temp, double weight) {
		this.temperature = temp;
		this.temperatureWeight = weight;
	}

	public void setTemperatureWeight(double tempWeight) {
		this.temperatureWeight = tempWeight;
	}

	public void setWaterDepth(double waterDepth) {
		this.waterDepth = waterDepth;
	}

	public void setWaterDepth(double waterDepth, double weight) {
		this.waterDepth = waterDepth;
		this.waterDepthWeight = weight;
	}

	public void setWaterDepthWeight(double waterDepthWeight) {
		this.waterDepthWeight = waterDepthWeight;
	}
}
