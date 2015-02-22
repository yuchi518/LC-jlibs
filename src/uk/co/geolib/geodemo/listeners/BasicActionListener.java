package uk.co.geolib.geodemo.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class BasicActionListener implements ActionListener {
	DisplayPanel displayPanel;
	PolygonData polygonData;
	
	public BasicActionListener(DisplayPanel displayPanelToSet, PolygonData polygonDataToSet) {
		displayPanel = displayPanelToSet;
		polygonData = polygonDataToSet;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		displayPanel.invalidate();
		displayPanel.repaint();
	}

}
