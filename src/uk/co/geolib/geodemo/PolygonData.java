package uk.co.geolib.geodemo;

import uk.co.geolib.geopolygons.*;
import uk.co.geolib.geolib.*;

public class PolygonData {

	private C2DPolygon polygon1 = null;

	private C2DPolygon polygon2 = null;
	
	private C2DPolygon selectedPolygon = null;
	
	private C2DPointSet points = new C2DPointSet();
	
	private C2DPolygon convexHull1 = null;
	
	private C2DPolygon convexHull2 = null;
	
	public C2DPolygon getConvexHull1() {
		return convexHull1;
	}

	public void setConvexHull1(C2DPolygon convexHull) {
		this.convexHull1 = convexHull;
	}

	public C2DPolygon getConvexHull2() {
		return convexHull2;
	}

	public void setConvexHull2(C2DPolygon convexHull) {
		this.convexHull2 = convexHull;
	}
	
	
	public C2DPointSet getPoints() {
		return points;
	}

	public C2DPolygon getSelectedPolygon() {
		return selectedPolygon;
	}

	public C2DPolygon getPolygon1() {
		return polygon1;
	}
	
	public C2DPolygon getPolygon2() {
		return polygon2;
	}
	
	public void setPolygon1(C2DPolygon p){
		polygon1 = p;
	}

	public void setPolygon2(C2DPolygon p){
		polygon2 = p;
	}
	
	public void setSelectedPolygon(C2DPolygon p){
		selectedPolygon = p;
	}
}
