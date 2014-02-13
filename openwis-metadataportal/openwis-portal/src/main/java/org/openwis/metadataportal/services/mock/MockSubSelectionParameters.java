package org.openwis.metadataportal.services.mock;

import org.openwis.harness.subselectionparameters.MultipleSelection;
import org.openwis.harness.subselectionparameters.MultipleSelectionType;
import org.openwis.harness.subselectionparameters.Parameters;
import org.openwis.harness.subselectionparameters.Value;

public class MockSubSelectionParameters {

	// public static Parameters getSubSelectionParamters() {
	// //getDateDependencySubSelectionParamters() {
	// Parameters params = new Parameters();
	//
	// // DatePeriode
	// DayPeriodSelection dps = new DayPeriodSelection();
	// dps.setCode("date_1");
	// dps.setLabel("Period selection");
	//
	// // Temperature
	// MultipleSelection temperature = new MultipleSelection();
	// temperature.setCode("temp_1");
	// temperature.setType(MultipleSelectionType.LISTBOX);
	// temperature.setLabel("Temperature");
	//
	// Value firstTemp = new Value();
	// firstTemp.setCode("temp_1_1");
	// firstTemp.setSelected(true);
	// firstTemp.setValue("15°C");
	// Value secTemp = new Value();
	// secTemp.setCode("temp_1_2");
	// secTemp.setSelected(false);
	// secTemp.setValue("35°C");
	// secTemp.getAvailableFor().add("2011-04-05/2011-04-10");
	// secTemp.getAvailableFor().add("2011-05-05/2011-05-10");
	// Value thirdTemp = new Value();
	// thirdTemp.setCode("temp_1_3");
	// thirdTemp.setSelected(false);
	// thirdTemp.setValue("45°C");
	// Value fourthTemp = new Value();
	// fourthTemp.setCode("temp_1_4");
	// fourthTemp.setSelected(false);
	// fourthTemp.setValue("60°C");
	//
	// temperature.getValues().add(firstTemp);
	// temperature.getValues().add(secTemp);
	// temperature.getValues().add(thirdTemp);
	// temperature.getValues().add(fourthTemp);
	//
	// for (int i=5; i<20; i++) {
	// Value temp5 = new Value();
	// temp5.setCode("temp_1_" + i);
	// temp5.setSelected(false);
	// temp5.setValue("4" + i
	// + "°C");
	// temperature.getValues().add(temp5);
	// }
	//
	//
	// // Handle order
	// dps.setNextParameter(temperature);
	//
	// // Add parameter instances to the parameters
	// params.getParameters().add(dps);
	// params.getParameters().add(temperature);
	// params.setStartParameter(dps);
	//
	// return params;
	// }

	// public static Parameters getSubSelectionParamters() {
	//
	// Parameters params = new Parameters();
	//
	// // Temperature
	// MultipleSelection temperature = new MultipleSelection();
	// temperature.setCode("temp_1");
	// temperature.setType(MultipleSelectionType.LISTBOX);
	// temperature.setLabel("Temperature");
	//
	// Value firstTemp = new Value();
	// firstTemp.setCode("temp_1_1");
	// firstTemp.setSelected(true);
	// firstTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 15°C");
	// Value secTemp = new Value();
	// secTemp.setCode("temp_1_2");
	// secTemp.setSelected(false);
	// secTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 35°C");
	// Value thirdTemp = new Value();
	// thirdTemp.setCode("temp_1_3");
	// thirdTemp.setSelected(false);
	// thirdTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 45°C");
	// Value fourthTemp = new Value();
	// fourthTemp.setCode("temp_1_4");
	// fourthTemp.setSelected(false);
	// fourthTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 60°C");
	//
	// temperature.getValues().add(firstTemp);
	// temperature.getValues().add(secTemp);
	// temperature.getValues().add(thirdTemp);
	// temperature.getValues().add(fourthTemp);
	//
	// // Altitude
	// SingleSelection altitude = new SingleSelection();
	// altitude.setCode("alti_1");
	// altitude.setType(SingleSelectionType.DROPDOWNLIST);
	// altitude.setLabel("Altitude");
	//
	// Value firstAlt = new Value();
	// firstAlt.setCode("alti_1_1");
	// firstAlt.setSelected(false);
	// firstAlt.setValue("0-50m");
	// Value secAlt = new Value();
	// secAlt.setCode("alti_1_2");
	// secAlt.setSelected(false);
	// secAlt.setValue("50-100m");
	// secAlt.getAvailableFor().add(thirdTemp.getCode());
	// Value thirdAlt = new Value();
	// thirdAlt.setCode("alti_1_3");
	// thirdAlt.setSelected(true);
	// thirdAlt.setValue("100-150m");
	//
	// altitude.getValues().add(firstAlt);
	// altitude.getValues().add(secAlt);
	// altitude.getValues().add(thirdAlt);
	//
	// // Wind
	// SingleSelection wind = new SingleSelection();
	// wind.setCode("wind_1");
	// wind.setType(SingleSelectionType.RADIO);
	// wind.setLabel("Wind");
	//
	// Value firstWind = new Value();
	// firstWind.setCode("wind_1_1");
	// firstWind.setSelected(false);
	// firstWind.setValue("Less 50m");
	// Value secWind = new Value();
	// secWind.setCode("wind_1_2");
	// secWind.setSelected(false);
	// secWind.setValue("More 50m");
	//
	// wind.getValues().add(firstWind);
	// wind.getValues().add(secWind);
	//
	// // Geographical component
	//
	// GeoConfig geoConfig = new GeoConfig();
	// geoConfig.setLayerName("basic");
	// geoConfig.setWmsUrl("http://vmap0.tiles.osgeo.org/wms/vmap0");
	// // TODO set SRS
	//
	// GeographicalAreaSelection geo = new GeographicalAreaSelection();
	// geo.setCode("geo_1");
	// geo.setLabel("Geo test");
	// geo.setGeoConfig(geoConfig);
	// geo.setGeoExtentType(GeoExtentType.POLYGON);
	//
	// String defaultSelection = "POLYGON((-6.328125 37.6171875,-6.328125 "
	// + "59.4140625,17.578125 59.4140625,17.578125000000004 "
	// + "37.6171875,-6.328125 37.6171875))";
	// String restrictedExtent =
	// "POLYGON((-123.75 -48.515625,-123.75 80.859375,127.96875 "
	// + "80.859375,127.96875 -48.515625,-123.75 -48.515625))";
	// geo.setGeoWKTMaxExtent(restrictedExtent);
	// geo.setGeoWKTSelection(defaultSelection);
	//
	// // DatePeriode
	// DatePeriodSelection dps = new DatePeriodSelection();
	// dps.setCode("date_1");
	// dps.setLabel("Period selection");
	// dps.setPeriodMinExtent("");
	// dps.setPeriodMaxExtent("");
	// dps.setFrom("");
	// dps.setTo("");
	// //dps.getExcludedDates().add("");
	//
	// TimePeriodSelection tps = new TimePeriodSelection();
	// tps.setCode("time_1");
	// tps.setLabel("Time selection");
	// tps.setPeriodMinExtent("");
	// tps.setPeriodMaxExtent("");
	// tps.setFrom("");
	// tps.setTo("");
	//
	// // Handle order
	// temperature.setNextParameter(altitude);
	// altitude.setNextParameter(wind);
	// wind.setNextParameter(geo);
	// geo.setNextParameter(dps);
	// dps.setNextParameter(tps);
	//
	// // Add parameter instances to the parameters
	// params.getParameters().add(temperature);
	// params.getParameters().add(altitude);
	// params.getParameters().add(wind);
	// params.getParameters().add(geo);
	// params.getParameters().add(dps);
	// params.getParameters().add(tps);
	//
	// params.setStartParameter(temperature);
	//
	// return params;
	//
	// }

	public static Parameters getSubSelectionParamters() {

		Parameters params = new Parameters();
		return params;
	}
	
//	public static Parameters getSubSelectionParamters() {
//
//		Parameters params = new Parameters();
//
//		// Temperature
//		MultipleSelection[] temperatures = new MultipleSelection[20];
//
//		for (int i = 0; i < temperatures.length; i++) {
//			temperatures[i] = new MultipleSelection();
//			temperatures[i].setCode("temp_" + i);
//			temperatures[i].setType(MultipleSelectionType.LISTBOX);
//			temperatures[i].setLabel("Temperature " + i);
//
//			Value firstTemp = new Value();
//			firstTemp.setCode("temp_" + i + "_1");
//			firstTemp.setSelected(true);
//			firstTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 15°C");
//			Value secTemp = new Value();
//			secTemp.setCode("temp_" + i + "_2");
//			secTemp.setSelected(false);
//			secTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 35°C");
//			Value thirdTemp = new Value();
//			thirdTemp.setCode("temp_" + i + "_3");
//			thirdTemp.setSelected(false);
//			thirdTemp.setValue("Wednesday November 09 11:31:16 UTC 2012 45°C");
//			Value fourthTemp = new Value();
//			fourthTemp.setCode("temp_" + i + "_4");
//			fourthTemp.setSelected(false);
//			fourthTemp
//					.setValue("Wednesday November 09 11:31:16 UTC 2012 60°C");
//
//			temperatures[i].getValues().add(firstTemp);
//			temperatures[i].getValues().add(secTemp);
//			temperatures[i].getValues().add(thirdTemp);
//			temperatures[i].getValues().add(fourthTemp);
//
//			// Add parameter instances to the parameters
//			params.getParameters().add(temperatures[i]);
//
//		}
//
//		for (int i = 0; i < temperatures.length; i++) {
//			if (i < temperatures.length - 1) {
//				temperatures[i].setNextParameter(temperatures[i + 1]);
//			}
//		}
//
//		params.setStartParameter(temperatures[0]);
//
//		return params;
//	}
}
