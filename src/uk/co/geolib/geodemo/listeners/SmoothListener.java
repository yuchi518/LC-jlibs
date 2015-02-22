package uk.co.geolib.geodemo.listeners;


import java.awt.event.ActionEvent;

import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class SmoothListener  extends BasicActionListener{

	public SmoothListener(DisplayPanel displayPanelToSet,
			PolygonData polygonDataToSet) {
		super(displayPanelToSet, polygonDataToSet);

	}

	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (polygonData.getPolygon1() != null) {
			polygonData.getPolygon1().Smooth();
		}
		
		if (polygonData.getPolygon2() != null) {
			polygonData.getPolygon2().Smooth();
		}
		
		super.actionPerformed(arg0);
	}
	
}
