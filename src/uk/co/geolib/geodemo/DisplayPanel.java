package uk.co.geolib.geodemo;

import uk.co.geolib.geopolygons.*;
import uk.co.geolib.geolib.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import uk.co.geolib.geoview.GeoDraw;


public class DisplayPanel extends JPanel{

	PolygonData polygonData;
	ControlPanel controlPanel;
	
	Color red = new Color(255, 0, 0);
	Color blue = new Color(0, 0, 255);
	Color black = new Color(0, 0, 0);
	Color green = new Color(0, 255, 0);
	
	public DisplayPanel(PolygonData polygonDataToSet) {
		polygonData = polygonDataToSet;

	}
	
	public void setControlPanel(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}
	
	public ControlPanel getControlPanel() {
		return controlPanel;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void paint(Graphics g){
		GeoDraw drawer = new GeoDraw();
		
		Rectangle r = getBounds();
		g.clearRect(0, 0, r.width, r.height);
		
		for (C2DPoint pt : polygonData.getPoints()) {
		    g.drawOval((int)pt.x, (int)pt.y, 2, 2);	
		}
		
		if (polygonData.getPolygon1() != null) {
			g.setColor(red);
			drawPolygon(polygonData.getPolygon1() , g);
			
			if (polygonData.getConvexHull1() != null) {
				g.setColor(black);
				drawer.Draw(polygonData.getConvexHull1(), g);
			}
		}
		
		if (polygonData.getPolygon2() != null) {
			g.setColor(blue);
			drawPolygon(polygonData.getPolygon2() , g);
			
			if (polygonData.getConvexHull2() != null) {
				g.setColor(black);
				drawer.Draw(polygonData.getConvexHull2(), g);
			}
		}
		
		if (controlPanel.isMinimumDistance() && polygonData.getPolygon1() != null && polygonData.getPolygon2() != null) {
			C2DPoint p1 = new C2DPoint();
			C2DPoint p2 = new C2DPoint();
			polygonData.getPolygon1().Distance(polygonData.getPolygon2(), p1, p2);
			
			C2DLine l = new C2DLine(p1, p2);
			
			drawer.Draw(l, g);
		}
		
		if (controlPanel.isBoundingCircle()) {
			if (polygonData.getPolygon1() != null) {
				C2DCircle c = new C2DCircle();
				polygonData.getPolygon1().GetBoundingCircle(c);
				drawer.Draw(c, g);
			}
			if (polygonData.getPolygon2() != null) {
				C2DCircle c = new C2DCircle();
				polygonData.getPolygon2().GetBoundingCircle(c);
				drawer.Draw(c, g);
			}
		}
		
		if (polygonData.getPolygon1() != null && polygonData.getPolygon2() != null){
			g.setColor(green);
			if (controlPanel.isUnion()) {
				
				ArrayList<C2DHoledPolygon> polys = new ArrayList<C2DHoledPolygon>();
				polygonData.getPolygon1().GetUnion(polygonData.getPolygon2(), polys, new CGrid());
				
				for (C2DHoledPolygon p : polys){
					drawer.DrawFilled(p, (Graphics2D)g);
				}
			} else if (controlPanel.isDifference()) {
				
				ArrayList<C2DHoledPolygon> polys = new ArrayList<C2DHoledPolygon>();
				polygonData.getPolygon1().GetNonOverlaps(polygonData.getPolygon2(), polys, new CGrid());
				
				for (C2DHoledPolygon p : polys){
					drawer.DrawFilled(p, (Graphics2D)g);
				}
			} else if (controlPanel.isIntersection()) {
				
				ArrayList<C2DHoledPolygon> polys = new ArrayList<C2DHoledPolygon>();
				polygonData.getPolygon1().GetOverlaps(polygonData.getPolygon2(), polys, new CGrid());
				
				for (C2DHoledPolygon p : polys){
					drawer.DrawFilled(p, (Graphics2D)g);
				}
			}
			
			
			
		}
	}
	
	public void drawPolygon(C2DPolygon p, Graphics g){
		GeoDraw drawer = new GeoDraw();
		
		drawer.DrawFilled(p, (Graphics2D) g);
		
		if (controlPanel.isShowRectangles()) {
			g.setColor(black);
			drawer.Draw(p.getBoundingRect(), g);
			
			for (C2DRect rect : p.getLineRects()) {
				drawer.Draw(rect, g);			
			}
		}
		
		if (controlPanel.isConvexSubAreas()) {
			g.setColor(black);
			
			ArrayList<C2DPolygon> subAreas = new ArrayList<C2DPolygon>();
			p.GetConvexSubAreas(subAreas);
			for (C2DPolygon s : subAreas) {
				drawer.Draw(s, g);
			}
			
		}
	}
	
}
