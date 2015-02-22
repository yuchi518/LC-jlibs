package uk.co.geolib.geoview;

import java.awt.Point;
import java.awt.Rectangle;

import uk.co.geolib.geolib.*;

public class ScreenManager {

        /// <summary>
        /// Constructor
        /// </summary>
        public ScreenManager()
        {
        }


        /// <summary>
        /// This simply grows the real world rectangle.
        /// </summary>
        /// <param name="dFactor">Factor to zoom out by.</param>
        public void ZoomOut(double dFactor)
        {
	        RealRect.Grow(dFactor);
        }

        /// <summary>
        /// This simply moves the real world rectangle.
        /// </summary>
        /// <param name="Vector">Vector to move / scroll by</param>
        public void ScrollReal(C2DVector Vector)
        {
	        RealRect.Move(Vector);
        }

        /// <summary>
        /// This simply moves the real world rectangle but by a scaled, screen based
        /// value e.g. 10 pixels to the right. The image is shown in the same place on
        /// the screen but the image scrolls.
        /// </summary>
        /// <param name="Vector">Vector to scroll / move by.</param>
        public void ScrollScreen(C2DVector Vector)
        {
	        RealRect.Move(new C2DVector( Vector.i / Scale.x, Vector.j / Scale.y) );
        }

        /// <summary>
        /// Calculates all the required offset and scale. The real world rect will 
        /// always all be shown but may have to occupy a sub section of the computer
        /// screen.
        /// </summary>
        public void Calculate()
        {
	        RealWindowsRect.Set(RealRect);

	        float fWinRatio = (float)WindowsRect.width / (float)WindowsRect.height;
	        float fRealRatio = (float)RealRect.Width() / (float)RealRect.Height();
	        boolean bFillX = fRealRatio >= fWinRatio;
	        if (bFillX)
	        {
		        Scale.x = (float)WindowsRect.width / (float)RealRect.Width();
		        Scale.y = Scale.x;
		        RealWindowsRect.GrowHeight(fRealRatio / fWinRatio);
	        }
	        else
	        {
		        Scale.y = (float)WindowsRect.height / (float)RealRect.Height();
		        Scale.x = Scale.y;
		        RealWindowsRect.GrowWidth(fWinRatio / fRealRatio );
	        }

	        if (FlipX) 
                Scale.x = - Scale.x;
	        if (FlipY) 
                Scale.y = - Scale.y;

	        // Now find 2 points to map onto each other and the rest is easy.
	        C2DPoint ptRealCen = RealRect.GetCentre();
	        Point ptWinCen = new Point( WindowsRect.x + WindowsRect.width / 2, 
                                            WindowsRect.y + WindowsRect.height / 2);

	        Offset.x = ptRealCen.x - (float)ptWinCen.x / Scale.x;
	        Offset.y = ptRealCen.y - (float)ptWinCen.y / Scale.y;
        }

        /// <summary>
        /// Returns a real world point from a point on the screen.
        /// </summary>
        /// <param name="pt">Point to map</param>
        public C2DPoint MapScreenToReal(Point pt)
        {
	        return new C2DPoint(  Offset.x + (float)pt.x  / Scale.x,
					           Offset.y + (float)pt.y / Scale.y);
        }

        /// <summary>
        /// Returns a point on the screen from a real world point.
        /// </summary>
        /// <param name="pt">Point to map.</param>
        public Point MapRealToScreen(C2DPoint pt)
        {
	        return new Point(   (int)((pt.x - Offset.x) * Scale.x),
				              (int)((pt.y - Offset.y) * Scale.y)   );

        }

        /// <summary>
        /// The windows rectangle that we are trying to project this onto.
        /// </summary>
        public Rectangle WindowsRect = new Rectangle(0, 0, 0, 0);

        /// <summary>
        /// The real world rectangle that we are viewing.
        /// </summary>
        public C2DRect RealRect = new C2DRect(0, 0, 0, 0);

        /// <summary>
        /// /// The resulting real world rectangle that the screen represents (note that this is the same as the above
        /// so long as the aspect ration is maintained).
        /// </summary>
        public C2DRect RealWindowsRect = new C2DRect(0, 0, 0, 0);

        /// <summary>
        /// True if the Y dimension is to be flipped because e.g. because of Windows.
        /// </summary>
        public boolean FlipY = true;
        /// <summary>
        /// True if the Y dimension is to be flipped.
        /// </summary>
        public boolean FlipX = false;
        /// <summary>
        /// The offest.
        /// </summary>
        public C2DPoint Offset = new C2DPoint(0, 0);

        /// <summary>
        /// The scale.
        /// </summary>
        public C2DPoint Scale = new C2DPoint(0, 0);
	
	
}
