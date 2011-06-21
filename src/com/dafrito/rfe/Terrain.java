package com.dafrito.rfe;

import com.dafrito.rfe.geom.GradientValue;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class Terrain implements GradientValue<Terrain> {
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

	@Inspectable
	public double getBrushDensity() {
		return this.brushDensity;
	}

	@Inspectable
	public double getBrushDensityWeight() {
		return this.brushDensityWeight;
	}

	@Inspectable
	public double getElevation() {
		return this.elevation;
	}

	@Inspectable
	public double getElevationWeight() {
		return this.elevationWeight;
	}

	@Inspectable
	public double getGroundCohesion() {
		return this.groundCohesion;
	}

	@Inspectable
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

	@Inspectable
	public double getTemperature() {
		return this.temperature;
	}

	@Inspectable
	public double getTemperatureWeight() {
		return this.temperatureWeight;
	}

	@Inspectable
	public double getWaterDepth() {
		return this.waterDepth;
	}

	@Inspectable
	public double getWaterDepthWeight() {
		return this.waterDepthWeight;
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
