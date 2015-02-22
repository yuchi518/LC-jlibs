package uk.co.geolib.geodemo;
import javax.swing.JFrame;
import javax.swing.border.EtchedBorder;

import uk.co.geolib.geodemo.listeners.DisplayMouseListener;




public class GeoDemo {


	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PolygonData polygonData = new PolygonData();
		
        JFrame frame = new JFrame("Geo Demo");   
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
     
        frame.setLayout(null);
        frame.setBounds(10, 10, 1000, 800);
        
        
        
        DisplayPanel displayPanel = new DisplayPanel(polygonData);
        displayPanel.setBounds(200, 0, 1000, 1000);
        displayPanel.setBorder(new EtchedBorder());
        
        DisplayMouseListener displayMouseListener = new DisplayMouseListener(polygonData, displayPanel);
        displayPanel.addMouseListener(displayMouseListener);
        displayPanel.addMouseMotionListener(displayMouseListener);
        displayPanel.addMouseWheelListener(displayMouseListener);
        frame.getContentPane().add(displayPanel);

        ControlPanel controlPanel = new ControlPanel(displayPanel, polygonData);
        controlPanel.setBounds(0, 0, 200, 600);
        frame.getContentPane().add(controlPanel);
        
        displayPanel.setControlPanel(controlPanel);
        
        frame.setVisible(true); 
	}

}
