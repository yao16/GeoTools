package com.yao.GeoTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.Layer;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.algorithm.BoundaryNodeRule;
import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geomgraph.GeometryGraph;
import com.vividsolutions.jts.operation.overlay.OverlayOp;

public class Intersect {
	
	protected GeometryGraph[] arg; 
	 
	{
		arg = new GeometryGraph[2];
	}
	
	public void intersectPoint(SimpleFeatureCollection c1, SimpleFeatureCollection c2) throws IOException {
		List<Point> points = new ArrayList<Point>();
		SimpleFeatureIterator iterator1 = c1.features();
		while (iterator1.hasNext()) {
			SimpleFeature f1 = iterator1.next();
			MultiPolygon g1 = (MultiPolygon) f1.getAttribute("the_geom");
			SimpleFeatureIterator iterator2 = c2.features();
			while (iterator2.hasNext()) {
				SimpleFeature f2 = iterator2.next();
				MultiPolygon g2 = (MultiPolygon) f2.getAttribute("the_geom");
			    arg[0] = new GeometryGraph(0, g1, BoundaryNodeRule.OGC_SFS_BOUNDARY_RULE);
			    arg[1] = new GeometryGraph(1, g2, BoundaryNodeRule.OGC_SFS_BOUNDARY_RULE);
			    LineIntersector li = new RobustLineIntersector();
			    arg[0].computeSelfNodes(li, false);
			    arg[1].computeSelfNodes(li, false);
			 // compute intersections between edges of the two input geometries
			    if (g1.intersects(g2)) {
			    	arg[0].computeEdgeIntersections(arg[1], li, true);
			    	System.out.println(li.hasIntersection());
					int count = li.getIntersectionNum();
					for (int i = 0; i < count; i++) {
						System.out.println(li.getIntersection(i).x + ":" + li.getIntersection(i).y );
					}
			    }
			    if (g1.contains(g2)) {
			    	System.out.println("counties contains landuse");
			    }
			    if (g2.contains(g1)) {
			    	System.out.println("landuse contains counties");
			    }
			    li = null;
			}
			iterator2.close();
		}
		iterator1.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		ShpAccess access = new ShpAccess();
//		SimpleFeatureCollection features = access.readFeatures("/home/yaoxiao/data/shp/ict_landuse.shp");
//		
//		SimpleFeatureIterator iterator = features.features();
//		
//		long start = System.currentTimeMillis();
		List<Geometry> result = new ArrayList<>();
		int i = 0;
//		while (iterator.hasNext()) {
//			SimpleFeatureCollection features1 = access.readFeatures("/home/yaoxiao/data/shp/ict_counties.shp");
//			SimpleFeatureIterator iterator1 = features1.features();
//			while (iterator1.hasNext()) {
//				OverlayOp op = new OverlayOp((Geometry)iterator.next().getDefaultGeometry(), (Geometry)iterator1.next().getDefaultGeometry());
//				Geometry geometry = op.getResultGeometry(OverlayOp.INTERSECTION);
//				//System.out.println(geometry);
//				if (!geometry.isEmpty()) {
//					result.add(geometry);
//					i++;
//					System.out.println(i);
//				}
//			}
//		}
//		long end = System.currentTimeMillis();
//		System.out.println("result size:" + result.size());
//		System.out.println("time used:" + (end - start));
		List<Geometry> geos1 = access.readGeometries("/home/yaoxiao/data/shp/ict_landuse.shp");
		List<Geometry> geos2 = access.readGeometries("/home/yaoxiao/data/shp/ict_counties.shp");
		long start = System.currentTimeMillis();
		for (Geometry geo1 : geos1) {
			for (Geometry geo2 : geos2) {
				if (geo1.intersects(geo2) || geo1.contains(geo2) || geo2.contains(geo1)){
					OverlayOpCpy op = new OverlayOpCpy(geo1, geo2);
				Geometry geometry = op.getResultGeometryWithoutInsections(OverlayOp.INTERSECTION);
				//System.out.println(geometry);
				if (!geometry.isEmpty()) {
					result.add(geometry);
					i++;
				//	System.out.println(i);
			}
				}
		}
	}
		long end = System.currentTimeMillis();
		System.out.println("time used:" + (end - start));
		System.out.println("result size:" + result.size());
}
}
