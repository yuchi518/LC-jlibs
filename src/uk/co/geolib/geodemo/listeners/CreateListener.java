package uk.co.geolib.geodemo.listeners;

import uk.co.geolib.geopolygons.*;
import java.awt.event.ActionEvent;
import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class CreateListener extends BasicActionListener {

	
	public CreateListener(DisplayPanel displayPanelToSet, PolygonData polygonDataToSet) {
		super(displayPanelToSet, polygonDataToSet);

	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (polygonData.getPoints().size() >= 3) {
			C2DPolygon p = new C2DPolygon();
			p.Create(polygonData.getPoints(), true);
			
			if (polygonData.getPolygon1() == null) {
				polygonData.setPolygon1(p);
			}else if (polygonData.getPolygon2() == null) {
				polygonData.setPolygon2(p);			
			}
			
			polygonData.getPoints().clear();
		}
		
		super.actionPerformed(arg0);
	}
}
