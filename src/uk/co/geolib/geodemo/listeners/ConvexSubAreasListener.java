package uk.co.geolib.geodemo.listeners;

import uk.co.geolib.geopolygons.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class ConvexSubAreasListener  extends BasicActionListener{

	public ConvexSubAreasListener(DisplayPanel displayPanelToSet,
			PolygonData polygonDataToSet) {
		super(displayPanelToSet, polygonDataToSet);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		
		
		toggleConvexSubAreas(polygonData.getPolygon1());
		toggleConvexSubAreas(polygonData.getPolygon2());

		super.actionPerformed(arg0);
	}
	
	private void toggleConvexSubAreas(C2DPolygon polygon) {
		if (polygon != null) {
			ArrayList<C2DPolygon> subAreas = new ArrayList<C2DPolygon>();
			polygon.GetConvexSubAreas(subAreas);
			if (subAreas.size() > 1) {
				polygon.ClearConvexSubAreas();
			} else {
				polygon.CreateConvexSubAreas();
			}
		}
	}
	
}
