package uk.co.geolib.geodemo.listeners;

import uk.co.geolib.geolib.*;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import uk.co.geolib.geodemo.DisplayPanel;
import uk.co.geolib.geodemo.PolygonData;

public class DisplayMouseListener implements MouseListener, MouseMotionListener , MouseWheelListener {

	PolygonData polygonData;
	DisplayPanel displayPanel;

	C2DPoint oldPoint = null;
	
	public DisplayMouseListener(PolygonData polygonDataToSet, DisplayPanel displayPanelToSet){
		polygonData = polygonDataToSet;
		displayPanel = displayPanelToSet;

	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Point pt = arg0.getPoint();
		C2DPoint geoPt = new C2DPoint(pt.x, pt.y);
		
		if (polygonData.getPolygon2() == null){
			polygonData.getPoints().add(geoPt);
			displayPanel.invalidate();
			displayPanel.repaint();
		} else {
			if (polygonData.getSelectedPolygon() == null) {
				if (polygonData.getPolygon1() != null && polygonData.getPolygon1().Contains(geoPt)) {
					polygonData.setSelectedPolygon(polygonData.getPolygon1());
				} else if  (polygonData.getPolygon2() != null && polygonData.getPolygon2().Contains(geoPt)) {
					polygonData.setSelectedPolygon(polygonData.getPolygon2());	
				}
			} else {
				polygonData.setSelectedPolygon(null);
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	//	polygonData.setSelectedPolygon(null);		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouseMovedOrDragged(new C2DPoint(arg0.getPoint().x, arg0.getPoint().y));	
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseMovedOrDragged(new C2DPoint(arg0.getPoint().x, arg0.getPoint().y));		
	}
	
	
	private void mouseMovedOrDragged(C2DPoint newPoint){
		if (polygonData.getSelectedPolygon() != null) {
			C2DVector v = new C2DVector(oldPoint, newPoint);
			polygonData.getSelectedPolygon().Move(v);
			if (polygonData.getConvexHull1() != null && polygonData.getPolygon1() == polygonData.getSelectedPolygon()) {
				polygonData.getConvexHull1().Move(v);		
			}
			if (polygonData.getConvexHull2() != null && polygonData.getPolygon2() == polygonData.getSelectedPolygon()) {
				polygonData.getConvexHull2().Move(v);		
			}
			
			avoid();
			
			displayPanel.invalidate();
			displayPanel.repaint();
		}

		oldPoint = newPoint;	
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
		if (polygonData.getSelectedPolygon() != null) {
			double rotation = 0.2;
			if (arg0.getWheelRotation() > 0) {
				rotation = -0.2;
			}
			
			C2DPoint centre = polygonData.getSelectedPolygon().GetCentroid();
			
			polygonData.getSelectedPolygon().RotateToRight(rotation, centre);	
			
			if (polygonData.getPolygon1() == polygonData.getSelectedPolygon() && polygonData.getConvexHull1() != null) {
				polygonData.getConvexHull1().RotateToRight(rotation, centre);	
			}
			if (polygonData.getPolygon2() == polygonData.getSelectedPolygon()&& polygonData.getConvexHull2() != null) {
				polygonData.getConvexHull2().RotateToRight(rotation, centre);	
			}
			
			avoid();
			
			displayPanel.invalidate();
			displayPanel.repaint();

		}
	}
	
	
	private void avoid() {
		if (displayPanel.getControlPanel().isAvoid()) {
			if (polygonData.getSelectedPolygon() == polygonData.getPolygon1() && polygonData.getPolygon2() != null) {
				polygonData.getPolygon2().Avoid(polygonData.getSelectedPolygon());
			} else if (polygonData.getSelectedPolygon() == polygonData.getPolygon2() && polygonData.getPolygon1() != null) {
				polygonData.getPolygon1().Avoid(polygonData.getSelectedPolygon());
			}
		}
	}

}
