package com.dafrito.util;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.gui.Krumflex;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_Terrain;

public class Terrain implements Krumflex, ScriptConvertible, Nodeable {
    private ScriptEnvironment m_environment;
    private double m_brushDensity, m_waterDepth, m_temperature, m_groundCohesion, m_elevation;
    private double m_brushDensityWeight, m_elevationWeight, m_groundCohesionWeight, m_temperatureWeight,
            m_waterDepthWeight;

    public Terrain(ScriptEnvironment env) {
        this.m_environment = env;
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

    public String getName() {
        return "Terrain";
    }

    public ScriptEnvironment getEnvironment() {
        return this.m_environment;
    }

    public Terrain getKrumflexFromIntensity(double intensity) {
        Terrain terrain = new Terrain(getEnvironment());
        terrain.setBrushDensity(getBrushDensity(), getBrushDensityWeight() * intensity);
        terrain.setElevation(getElevation(), getElevationWeight() * intensity);
        terrain.setTemperature(getTemperature(), getTemperatureWeight() * intensity);
        terrain.setWaterDepth(getWaterDepth(), getWaterDepthWeight() * intensity);
        return terrain;
    }

    public double getBrushDensityWeight() {
        return this.m_brushDensityWeight;
    }

    public double getElevationWeight() {
        return this.m_elevationWeight;
    }

    public double getGroundCohesionWeight() {
        return this.m_groundCohesionWeight;
    }

    public double getTemperatureWeight() {
        return this.m_temperatureWeight;
    }

    public double getWaterDepthWeight() {
        return this.m_waterDepthWeight;
    }

    public void setWaterDepthWeight(double waterDepthWeight) {
        this.m_waterDepthWeight = waterDepthWeight;
    }

    public void setElevationWeight(double elevationWeight) {
        this.m_elevationWeight = elevationWeight;
    }

    public void setGroundCohesionWeight(double gcWeight) {
        this.m_groundCohesionWeight = gcWeight;
    }

    public void setTemperatureWeight(double tempWeight) {
        this.m_temperatureWeight = tempWeight;
    }

    public void setBrushDensityWeight(double bdWeight) {
        this.m_brushDensityWeight = bdWeight;
    }

    public double getBrushDensity() {
        return this.m_brushDensity;
    }

    public double getElevation() {
        return this.m_elevation;
    }

    public double getGroundCohesion() {
        return this.m_groundCohesion;
    }

    public double getTemperature() {
        return this.m_temperature;
    }

    public double getWaterDepth() {
        return this.m_waterDepth;
    }

    public void setBrushDensity(double brushDensity) {
        this.m_brushDensity = brushDensity;
    }

    public void setBrushDensity(double brushDensity, double weight) {
        this.m_brushDensity = brushDensity;
        this.m_brushDensityWeight = weight;
    }

    public void setElevation(double elevation) {
        this.m_elevation = elevation;
    }

    public void setElevation(double elevation, double weight) {
        this.m_elevation = elevation;
        this.m_elevationWeight = weight;
    }

    public void setGroundCohesion(double cohesion) {
        this.m_groundCohesion = cohesion;
    }

    public void setGroundCohesion(double cohesion, double weight) {
        this.m_groundCohesion = cohesion;
        this.m_groundCohesionWeight = weight;
    }

    public void setTemperature(double temp) {
        this.m_temperature = temp;
    }

    public void setTemperature(double temp, double weight) {
        this.m_temperature = temp;
        this.m_temperatureWeight = weight;
    }

    public void setWaterDepth(double waterDepth) {
        this.m_waterDepth = waterDepth;
    }

    public void setWaterDepth(double waterDepth, double weight) {
        this.m_waterDepth = waterDepth;
        this.m_waterDepthWeight = weight;
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Terrain");
        assert LegacyDebugger.addNode("Brush Density: " + getBrushDensity() + " (Weight: " + getBrushDensityWeight() + ")");
        assert LegacyDebugger.addNode("Elevation: " + getElevation() + " (Weight: " + getElevationWeight() + ")");
        assert LegacyDebugger.addNode("Ground Cohesion: "
            + getGroundCohesion()
            + " (Weight: "
            + getGroundCohesionWeight()
            + ")");
        assert LegacyDebugger.addNode("Temperature: " + getTemperature() + " (Weight: " + getTemperatureWeight() + ")");
        assert LegacyDebugger.addNode("Water Depth: " + getWaterDepth() + " (Weight: " + getWaterDepthWeight() + ")");
        assert LegacyDebugger.close();
        return true;
    }

    public Object convert() {
        FauxTemplate_Terrain terrain = new FauxTemplate_Terrain(getEnvironment(), getEnvironment().getTemplate(
            FauxTemplate_Terrain.TERRAINSTRING).getType());
        terrain.setTerrain(this);
        return terrain;
    }
}
