import java.util.*;

//public Brand(String name, Commodity commodity, double quality)
//public Lot(Brand brand, double quality, double quantity)
//public AssetPoint(RiffDataPoint point, Planet planet, Asset asset)
//public RiffSpherePoint(Location referenceLocation, double longitude, double latitude)
//public TerrainPoint(RiffDataPoint area, Planet planet, double brushDensity, double elevation, double temperature, double waterDepth)
public class RiffDriver{
	private Planet m_planet;
	public String toString(){
		String string = new String();
		string += "RiffDriver";
		return string;
	}
	public RiffDriver() throws InvalidScenarioDateRangeException{
		try{
			GregorianCalendar startDate = new GregorianCalendar(2100, Calendar.MARCH, 1, 5, 25, 00);
			GregorianCalendar endDate = new GregorianCalendar(2200, Calendar.DECEMBER, 25, 12, 00, 00);
			Scenario scenario = new Scenario("Roman Campaign", new Planet("Earth", "Terran", null, null, 6372.795), ((365/4)*3600*24)/(3600*24), startDate, endDate);
			m_planet = (Planet)scenario.getRootLocation();
			
			RiffCommodities.addCommodity(new Commodity("Widgets"));
			RiffCommodities.addCommodity(new Commodity("Widget Fluff"));
			RiffCommodities.getCommodity("Widgets").addPrerequisiteLevel("Widget Fluff", 30);
			RiffBrands.addBrand(new Brand("Generic Fluff", "Widget Fluff", .98));
			TappableAsset component = new TappableAsset(new Brand("Power Widgets", "Widgets", .85));
			new AssetPoint(new RiffSpherePoint(m_planet, 0,0), m_planet, component);
			component.addAsset(new Lot("Generic Fluff", .75, 90));
		}catch(OverwriteException ex){
			System.out.println(ex);
		}catch(CommodityMapException ex){
			System.out.println(ex);
		}
	}
	public static void main(String[]args){
		try{
			RiffDriver riffDriver = new RiffDriver();
		}catch(InvalidScenarioDateRangeException ex){
			System.out.println(ex);
		}
	}
};
