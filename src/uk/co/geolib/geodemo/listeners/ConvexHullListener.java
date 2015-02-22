package uk.co.geolib.geodemo.listeners;

import uk.co.geolib.geopolygons.*;

import java.awt.event.ActionEvent;

import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class ConvexHullListener extends BasicActionListener {

	public ConvexHullListener(DisplayPanel displayPanelToSet, PolygonData polygonData) {
		super(displayPanelToSet, polygonData);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
		if (polygonData.getConvexHull1() == null) {
			C2DPolygon c1 = new C2DPolygon();
			if (polygonData.getPolygon1() != null) {
				c1.CreateConvexHull(polygonData.getPolygon1());
			}
			polygonData.setConvexHull1(c1);
		} else {
			polygonData.setConvexHull1(null);	
		}
		
		if (polygonData.getConvexHull2() == null) {
			C2DPolygon c2 = new C2DPolygon();
			if (polygonData.getPolygon2() != null) {
				c2.CreateConvexHull(polygonData.getPolygon2());
			}
			polygonData.setConvexHull2(c2);
		} else {
			polygonData.setConvexHull2(null);	
		}
		
		super.actionPerformed(arg0);
	}

}
