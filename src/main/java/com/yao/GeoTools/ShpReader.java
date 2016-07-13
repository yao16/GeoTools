package com.yao.GeoTools;

import java.io.File;
import java.io.IOException;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Tools to read shp file
 *
 */
public class ShpReader {
	public Layer getLayer(String url) throws IOException {
		
		File file = new File(url);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();
		CachingFeatureSource cache = new CachingFeatureSource(featureSource);
		Style style = SLD.createSimpleStyle(featureSource.getSchema());
		Layer layer = new FeatureLayer(cache, style);
		return layer;

	}

	public void readTools() throws IOException {
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();
		CachingFeatureSource cache = new CachingFeatureSource(featureSource);
		MapContent mapContent = new MapContent();
		mapContent.setTitle("my first geotools test");
		Style style = SLD.createSimpleStyle(featureSource.getSchema());
		Layer layer = new FeatureLayer(cache, style);
		mapContent.addLayer(layer);
		JMapFrame.showMap(mapContent);
	}

	public static void main(String[] args) throws IOException {
		Layer layer = new ShpReader().getLayer("D:\\data\\ict_counties.shp");
		ReferencedEnvelope envelope = layer.getBounds();
		System.out.println(envelope.maxX());
		FeatureSource<FeatureType, Feature> source = (FeatureSource<FeatureType, Feature>) layer.getFeatureSource();
		FeatureCollection<FeatureType, Feature> collection = source.getFeatures();
		System.out.println(collection.size());
	}
}
