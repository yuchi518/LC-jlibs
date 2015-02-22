package uk.co.geolib.geoview;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import uk.co.geolib.geopolygons.*;
import uk.co.geolib.geolib.*;




public class GeoDraw {

	
    /// <summary>
    /// Draws a rectangle
    /// </summary>
    public void Draw(C2DRect Rect, Graphics graphics)
    {
        C2DPoint pt1 = new C2DPoint(Rect.getTopLeft());
        C2DPoint pt2 = new C2DPoint(Rect.getBottomRight());
        this.ScaleAndOffSet(pt1);
        this.ScaleAndOffSet(pt2);

        int TLx = (int)pt1.x;
        if (Scale.x < 0)
            TLx = (int)pt2.x;

        int TLy = (int)pt2.y;
        if (Scale.x < 0)
            TLy = (int)pt1.y;

        graphics.drawRect(TLx, TLy, (int)Math.abs(pt1.x - pt2.x), 
                (int)Math.abs(pt1.y - pt2.y));
    }
	
	
	
    /// <summary>
    /// Creates a path based on a polygon.
    /// </summary>
    private GeneralPath CreatePath( C2DPolyBase Poly)
    {
    	GeneralPath gp = new GeneralPath();


        if (Poly.getLines().size() == 0)
            return gp;
        C2DPoint firstPt = Poly.getLines().get(0).GetPointFrom();
        ScaleAndOffSet(firstPt);
        gp.moveTo(firstPt.x, firstPt.y);
        
        for (int i = 0; i < Poly.getLines().size(); i++)
        {
        	C2DLineBase line = Poly.getLines().get(i);
            if (line instanceof C2DLine)
            {
                C2DPoint ptTo = line.GetPointTo();

                ScaleAndOffSet(ptTo);
                gp.lineTo(ptTo.x, ptTo.y);
            }
            else if (line instanceof C2DArc)
            {
            	C2DArc arc = (C2DArc)line;
            	C2DPoint mid = arc.GetMidPoint();
            	C2DPoint ptTo = arc.GetMidPoint();
                gp.quadTo(mid.x, mid.y, ptTo.x, ptTo.y);
            }
        }

        gp.closePath();

        return gp;
    }
	

    /// <summary>
    /// Draws a polygon filled.
    /// </summary>
    public void DrawFilled(C2DHoledPolyBase Poly, Graphics2D graphics)
    {
        if (Poly.getRim().getLines().size() == 0)
            return;

        GeneralPath gp = CreatePath(Poly.getRim());

        for (int h = 0; h < Poly.getHoleCount(); h++)
        {
            if (Poly.GetHole(h).getLines().size() > 2)
            {
                gp.append(CreatePath(  Poly.GetHole(h)), false);
            }
        }
        gp.setWindingRule(GeneralPath.WIND_EVEN_ODD);

        graphics.fill(gp);
    }
	
    
    /// <summary>
    /// Draws a polygon filled.
    /// </summary>
    public void DrawFilled(C2DPolyBase Poly, Graphics2D graphics)
    {
        if (Poly.getLines().size() == 0)
            return;
        
        GeneralPath gp = CreatePath(Poly);

        gp.setWindingRule(GeneralPath.WIND_EVEN_ODD);

        graphics.fill(gp);
    }
    
    
	
    /// <summary>
    /// Draws a line.
    /// </summary>
    public void Draw(C2DLine Line, Graphics graphics)
    {
        C2DPoint pt1 = Line.GetPointFrom();
        C2DPoint pt2 = Line.GetPointTo();
        this.ScaleAndOffSet(pt1);
        this.ScaleAndOffSet(pt2);
        graphics.drawLine((int)pt1.x, (int)pt1.y, (int)pt2.x, (int)pt2.y);
    }
    
    
    /// <summary>
    /// Draws an arc.
    /// </summary>
    public void Draw(C2DArc Arc, Graphics graphics)
    {
        C2DRect Rect = new C2DRect();
        Integer nStartAngle = 0;
        Integer nSweepAngle = 0;

        GetArcParameters(Arc, Rect, nStartAngle, nSweepAngle);

        if (nSweepAngle == 0)
            nSweepAngle = 1;

        int Width = (int)Rect.Width();
        if (Width == 0)
            Width = 1;
        int Height = (int)Rect.Height();
        if (Height == 0)
            Height = 1;

        graphics.drawArc((int)Rect.getTopLeft().x, (int)Rect.getBottomRight().y,
            Width, Height, nStartAngle, nSweepAngle);
    }
    
    
    
    /// <summary>
    /// Draws a circle
    /// </summary>
    public void Draw(C2DCircle Circle, Graphics graphics)
    {
        C2DRect Rect = new  C2DRect();
        Circle.GetBoundingRect(Rect);
        this.ScaleAndOffSet(Rect.getBottomRight());
        this.ScaleAndOffSet(Rect.getTopLeft());

        graphics.drawOval( (int)Rect.getTopLeft().x, (int)Rect.getBottomRight().y, (int)Rect.Width(), (int)Rect.Height());
    }
    
    
    /// <summary>
    /// Draws a polygon
    /// </summary>
    public void Draw(C2DPolyBase Poly, Graphics graphics)
    {
        for (int i = 0; i < Poly.getLines().size(); i++)
        {
        	C2DLineBase line = Poly.getLines().get(i);
            if (line instanceof C2DLine)
            {
                Draw((C2DLine)line, graphics);
            }
            else if (line instanceof C2DArc)
            {
                Draw((C2DArc)line, graphics);
            }
        }
    }
    
    /// <summary>
    /// Draws a polygon
    /// </summary>
    public void Draw(C2DHoledPolyBase Poly, Graphics graphics)
    {
        Draw(Poly.getRim(), graphics);

        for (int h = 0; h < Poly.getHoleCount(); h++)
        {
            Draw(Poly.GetHole(h), graphics);
        }
    }
    
    
    /// <summary>
    /// Gets the parameters required to draw an arc.
    /// </summary>
    private void GetArcParameters(C2DArc Arc, C2DRect Rect, Integer nStartAngle, Integer nSweepAngle)
    {
        C2DPoint Centre = Arc.GetCircleCentre();

        Rect.Set(Centre.x - Arc.Radius, Centre.y + Arc.Radius,
                                    Centre.x + Arc.Radius, Centre.y - Arc.Radius);

        ScaleAndOffSet(Rect.getTopLeft());
        ScaleAndOffSet(Rect.getBottomRight());
        ScaleAndOffSet(Centre); // CR 19-1-09

        C2DPoint bottomRightTemp = new C2DPoint(Rect.getBottomRight()); // to make valid // CR 11-3-09
        Rect.getBottomRight().Set(Rect.getTopLeft()); // to make valid // CR 11-3-09
        Rect.ExpandToInclude(bottomRightTemp); // to make valid // CR 11-3-09

        C2DPoint pt1 = Arc.getline().GetPointFrom();
        C2DPoint pt2 = Arc.getline().GetPointTo();
        this.ScaleAndOffSet(pt1);
        this.ScaleAndOffSet(pt2);

        C2DVector vec1 = new C2DVector(Centre, pt1);
        C2DVector vec2 = new C2DVector(Centre, pt2);

        C2DVector vecx = new C2DVector(100, 0); // x - axis

        double dStartAngle = vecx.AngleToLeft(vec1) * Constants.conDegreesPerRadian;
        double dSweepAngle = 0;

        boolean bAlreadyFlipped = Scale.x * Scale.y < 0;

        if (Arc.ArcOnRight ^ bAlreadyFlipped)
            dSweepAngle = vec1.AngleToLeft(vec2) * Constants.conDegreesPerRadian;
        else
            dSweepAngle = -vec1.AngleToRight(vec2) * Constants.conDegreesPerRadian;

        nStartAngle = (int)dStartAngle;
        if (nStartAngle == 360)
            nStartAngle = 0;
        nSweepAngle = (int)dSweepAngle;
    }
    
    
    /// <summary>
    /// Function to scale and offset a point.
    /// </summary>
    private void ScaleAndOffSet(C2DPoint pt)
    {
        pt.x -= Offset.x;
        pt.y -= Offset.y;
        pt.x *= Scale.x;
        pt.y *= Scale.y;
    }

    /// <summary>
    /// The offset to be used.
    /// </summary>
    public C2DPoint Offset = new C2DPoint(0, 0);

    /// <summary>
    ///  The scale to be used.
    /// </summary>
    public C2DPoint Scale = new C2DPoint(1, 1);
    
    
}
