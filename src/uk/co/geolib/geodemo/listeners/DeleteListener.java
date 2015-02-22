package uk.co.geolib.geodemo.listeners;


import java.awt.event.ActionEvent;

import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class DeleteListener  extends BasicActionListener {

	public DeleteListener(DisplayPanel displayPanelToSet,
			PolygonData polygonDataToSet) {
		super(displayPanelToSet, polygonDataToSet);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (polygonData.getPolygon2() != null) {
			polygonData.setPolygon2(null);
			polygonData.setConvexHull2(null);
		} else if (polygonData.getPolygon1() != null) {
			polygonData.setPolygon1(null);
			polygonData.setConvexHull1(null);
		} 
		
		super.actionPerformed(arg0);
	}

}
