package uk.co.geolib.geodemo;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import uk.co.geolib.geodemo.listeners.BasicActionListener;
import uk.co.geolib.geodemo.listeners.ConvexHullListener;
import uk.co.geolib.geodemo.listeners.ConvexSubAreasListener;
import uk.co.geolib.geodemo.listeners.CreateListener;
import uk.co.geolib.geodemo.listeners.DeleteListener;
import uk.co.geolib.geodemo.listeners.SmoothListener;

public class ControlPanel extends JPanel{
	JCheckBox showRectangles = null;
	JCheckBox convexHull = null;
	JCheckBox avoid = null;
	JCheckBox convexSubAreas = null;
	JCheckBox minimumDistance = null;
	JCheckBox boundingCircle = null;
	JComboBox operation = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControlPanel(DisplayPanel displayPanel, PolygonData polygonData){
		setLayout(new GridBagLayout());
		
		BasicActionListener basicActionListener = new BasicActionListener(displayPanel, polygonData);
		
		JButton create = new JButton("Create");
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		create.addActionListener(new CreateListener(displayPanel, polygonData));
		this.add(create, constraints);
		
		JButton delete = new JButton("Delete");
		delete.addActionListener(new DeleteListener(displayPanel, polygonData));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy =1;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(delete, constraints);
		
		showRectangles = new JCheckBox("Rectangles");
		showRectangles.addActionListener(basicActionListener);
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(showRectangles, constraints);
		
		
		convexHull = new JCheckBox("Convex Hull");
		convexHull.addActionListener(new ConvexHullListener(displayPanel, polygonData));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(convexHull, constraints);
		
		avoid = new JCheckBox("Avoid");
		
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 5;
		constraints.ipady = 5;
		constraints.weighty = 0;
		this.add(avoid, constraints);
		
		convexSubAreas = new JCheckBox("Convex Sub-areas");
		convexSubAreas.addActionListener(new ConvexSubAreasListener(displayPanel, polygonData));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(convexSubAreas, constraints);
		
		minimumDistance = new JCheckBox("Show min-distance");
		showRectangles.addActionListener(new BasicActionListener(displayPanel, polygonData));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(minimumDistance, constraints);
		

		boundingCircle = new JCheckBox("Bounding Circle");
		boundingCircle.addActionListener(new BasicActionListener(displayPanel, polygonData));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(boundingCircle, constraints);
		
		JButton smooth = new JButton("Smooth");
		smooth.addActionListener(new SmoothListener(displayPanel, polygonData));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		this.add(smooth, constraints);
		
		
		String[] operations = {"None", "Union", "Difference", "Intersection"};
		
		operation = new JComboBox(operations);
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.ipadx = 10;
		constraints.ipady = 10;
		constraints.weighty = 0;
		operation.setSelectedIndex(0);
		this.add(operation, constraints);
		
		JLabel dummy = new JLabel();
		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.weighty = .70;
		this.add(dummy, constraints);
	}
	
	public boolean isUnion(){
		return ((String)operation.getSelectedItem()).equals("Union");
	}
	
	public boolean isDifference(){
		return ((String)operation.getSelectedItem()).equals("Difference");
	}
	
	public boolean isIntersection(){
		return ((String)operation.getSelectedItem()).equals("Intersection");
	}
	
	public boolean isShowRectangles() {
		return showRectangles.isSelected();
	}
	
	public boolean isConvexHull() {
		return convexHull.isSelected();
	}
	
	public boolean isAvoid() {
		return avoid.isSelected();
	}
	
	public boolean isConvexSubAreas() {
		return convexSubAreas.isSelected();
	}
	
	public boolean isMinimumDistance() {
		return minimumDistance.isSelected();
	}
	
	public boolean isBoundingCircle() {
		return boundingCircle.isSelected();
	}
	
	
	
	
	
}
