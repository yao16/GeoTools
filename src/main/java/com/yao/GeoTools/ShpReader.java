package com.yao.GeoTools;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureFactory;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.junit.runner.manipulation.Sortable;
import org.omg.CORBA.FREE_MEM;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.overlay.OverlayOp;
import com.vividsolutions.jts.operation.overlay.snap.SnapOverlayOp;

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
		Layer layer = new FeatureLayer(featureSource, style);
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
		Layer layer_counties = new ShpReader().getLayer("D:\\data\\ict_landuse.shp");
		Layer layer_landuse = new ShpReader().getLayer("D:\\data\\ict_counties.shp");
		
		SimpleFeatureSource source_counties = (SimpleFeatureSource) layer_counties.getFeatureSource();
		SimpleFeatureCollection collection_counties = source_counties.getFeatures();
		SimpleFeatureSource source_landuse = (SimpleFeatureSource) layer_landuse.getFeatureSource();
		SimpleFeatureCollection collection_landuse = source_landuse.getFeatures();
		
		System.out.println("ict_landuse:" + collection_landuse.size());
		System.out.println("ict_counties:" + collection_counties.size());
		
		SimpleFeatureIterator iterator_landuse = collection_landuse.features();
	
		int i = 0;
		int j = 0;
		while (iterator_landuse.hasNext()) {
			SimpleFeature f1 = iterator_landuse.next();
			MultiPolygon g1 = (MultiPolygon)f1.getAttribute("the_geom");
			SimpleFeatureIterator iterator_counties = collection_counties.features();
			
			while (iterator_counties.hasNext()) {
				SimpleFeature f2 = iterator_counties.next();
				MultiPolygon g2 = (MultiPolygon)f2.getAttribute("the_geom");
				System.out.println(f2.getID().substring(12) + ":" + g2.getSRID());
				if (g1.intersects(g2)) {
					OverlayOpCpy op = new OverlayOpCpy(g1, g2);
					long start = System.currentTimeMillis();
					Geometry result = op.getResultGeometry(OverlayOp.INTERSECTION);
					long end = System.currentTimeMillis();
					if (!result.isEmpty()) {
					//	System.out.println("i-----" + i + "time used:" + (end - start));
						i++;
					}
				}
			}
			iterator_counties.close();
			j++;
		//	System.out.println("j=====" + j);
		}
		System.out.println("i:" + i);
		System.out.println("j:" + j);
	}
}
