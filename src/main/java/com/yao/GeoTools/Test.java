package com.yao.GeoTools;

import java.io.IOException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.Layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;

public class Test {

	public static void main(String[] args) throws IOException {
		Layer layer_counties = new ShpReader().getLayer("D:\\data\\ict_landuse.shp");
		Layer layer_landuse = new ShpReader().getLayer("D:\\data\\ict_counties.shp");
		
		SimpleFeatureSource source_counties = (SimpleFeatureSource) layer_counties.getFeatureSource();
		SimpleFeatureCollection collection_counties = source_counties.getFeatures();
		SimpleFeatureSource source_landuse = (SimpleFeatureSource) layer_landuse.getFeatureSource();
		SimpleFeatureCollection collection_landuse = source_landuse.getFeatures();
	}
}
