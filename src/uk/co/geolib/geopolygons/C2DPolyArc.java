package uk.co.geolib.geopolygons;

import java.util.ArrayList;

import uk.co.geolib.geolib.*;

public class C2DPolyArc extends C2DPolyBase {
    /// <summary>
    /// Constructor.
    /// </summary>
    public C2DPolyArc() { }
    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="Other">The other polygon.</param> 
    public C2DPolyArc(C2DPolyBase Other)
    {
    	super(Other);
    }
    /// <summary>
    /// Constructor.
    /// </summary>
    public C2DPolyArc(C2DPolyArc Other)
    {
    	super(Other);
    }

    /// <summary>
    /// Sets the starting point.
    /// </summary>
    /// <param name="Point">The start point.</param> 
    public void SetStartPoint(C2DPoint Point)
    {
        Clear();

        Lines.add(new C2DLine(Point, Point));
    }

    /// <summary>
    /// Arc to a new point.
    /// </summary>
    /// <param name="Point">The point to go to.</param> 
    /// <param name="dRadius">The radius.</param> 
    /// <param name="bCentreOnRight">Indicates whether the centre of the arc is to the right of the line.</param> 
    /// <param name="bArcOnRight">indicates whether the curve is to the right i.e. anti-clockwise.</param> 
    public void LineTo(C2DPoint Point, double dRadius,
	                        boolean bCentreOnRight, boolean bArcOnRight)
    {
        if (Lines.size() == 0)
	        return;

        C2DArc pLine = new C2DArc( Lines.get(Lines.size() - 1).GetPointTo(), Point, 
							        dRadius, bCentreOnRight, bArcOnRight);

        if (Lines.size() == 1 && Lines.get(0) instanceof C2DLine &&
            Lines.get(0).GetPointTo().PointEqualTo(Lines.get(0).GetPointFrom()))  // CR 19-1-09
        {
            Lines.set(0, pLine);
        }
        else
        {
            Lines.add(pLine);
        }
    }

    /// <summary>
    /// Adds a point which is a striaght line from the previous.
    /// </summary>
    /// <param name="Point">The point to go to.</param> 
    public void LineTo(C2DPoint Point)
    {
        if (Lines.size() == 0)
	        return;

        C2DLine pLine = new C2DLine( Lines.get(Lines.size() - 1).GetPointTo(), Point );

        if (Lines.size() == 1 && Lines.get(0) instanceof C2DLine &&
            Lines.get(0).GetPointTo().PointEqualTo(Lines.get(0).GetPointFrom()))  // CR 19-1-09
        {
            Lines.set(0, pLine);
        }
        else
        {
            Lines.add(pLine);
        }
    }

    /// <summary>
    /// Close with a curved line back to the first point.
    /// </summary>
    /// <param name="dRadius">The radius.</param> 
    /// <param name="bCentreOnRight">Indicates whether the centre of the arc is to the right of the line.</param> 
    /// <param name="bArcOnRight">indicates whether the curve is to the right i.e. anti-clockwise.</param> 
    public void Close(double dRadius, boolean bCentreOnRight, boolean bArcOnRight)
    {
        if (Lines.size() == 0)
	        return;

        Lines.add( new C2DArc( Lines.get(Lines.size() - 1).GetPointTo(), Lines.get(0).GetPointFrom(), 
						        dRadius, bCentreOnRight, bArcOnRight));

        MakeLineRects();
        MakeBoundingRect();

    }

    /// <summary>
    /// Close with a straight line back to the first point.
    /// </summary>
    public void Close()
    {
        if (Lines.size() == 0)
	        return;

        Lines.add( new C2DLine( Lines.get(Lines.size() - 1).GetPointTo(), Lines.get(0).GetPointFrom() ));

        MakeLineRects();
        MakeBoundingRect();

    }
    /// <summary>
    /// Creates a random shape.
    /// </summary>
    /// <param name="cBoundary">The boundary.</param> 
    /// <param name="nMinPoints">The minimum points.</param> 
    /// <param name="nMaxPoints">The maximum points.</param> 
    public boolean CreateRandom(C2DRect cBoundary, int nMinPoints, int nMaxPoints)
    {
        C2DPolygon Poly = new C2DPolygon();
        if (!Poly.CreateRandom(cBoundary, nMinPoints, nMaxPoints))
	        return false;

        CRandomNumber rCenOnRight = new CRandomNumber(0, 1);

        this.Set( Poly );

        for (int i = 0 ; i < Lines.size(); i ++)
        {
	        C2DLineBase pLine = Lines.get(i);

	        boolean bCenOnRight = (rCenOnRight.GetInt() > 0 );
	        double dLength = pLine.GetLength();
	        CRandomNumber Radius = new CRandomNumber(dLength , dLength * 3);


	        C2DArc pNew = new C2DArc( pLine.GetPointFrom(), pLine.GetPointTo(), 
						        Radius.Get(), bCenOnRight, !bCenOnRight);

	        if (!this.Crosses( pNew ))
	        {
                Lines.set(i, pNew);
                pNew.GetBoundingRect(LineRects.get(i));
                BoundingRect.ExpandToInclude(LineRects.get(i));
	        }
        }

   //     this.MakeLineRects();
  //      this.MakeBoundingRect();

        return true;
    }


    /// <summary>
    /// Gets the non overlaps i.e. the parts of this that aren't in the other.
    /// </summary>
    /// <param name="Other">The other shape.</param> 
    /// <param name="Polygons">The set to recieve the result.</param> 
    /// <param name="grid">The degenerate settings.</param> 
    public void GetNonOverlaps(C2DPolyArc Other, ArrayList<C2DHoledPolyArc> Polygons, 
									    CGrid grid) 
    {
    	ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

        super.GetNonOverlaps(Other, NewPolys, grid);

        for (int i = 0; i < NewPolys.size(); i++)
            Polygons.add(new C2DHoledPolyArc(NewPolys.get(i)));
    }

    /// <summary>
    /// Gets the union of the 2 shapes.
    /// </summary>
    /// <param name="Other">The other shape.</param> 
    /// <param name="Polygons">The set to recieve the result.</param> 
    /// <param name="grid">The degenerate settings.</param> 
    public void GetUnion(C2DPolyArc Other, ArrayList<C2DHoledPolyArc> Polygons,
									    CGrid grid) 
    {
    	ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

        super.GetUnion(Other, NewPolys, grid);

        for (int i = 0; i < NewPolys.size(); i++)
            Polygons.add(new C2DHoledPolyArc(NewPolys.get(i)));
    }


    /// <summary>
    /// Gets the overlaps of the 2 shapes.	
    /// </summary>
    /// <param name="Other">The other shape.</param> 
    /// <param name="Polygons">The set to recieve the result.</param> 
    /// <param name="grid">The degenerate settings.</param> 
    public void GetOverlaps(C2DPolyArc Other, ArrayList<C2DHoledPolyArc> Polygons,
									    CGrid grid)
    {
    	ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

        super.GetOverlaps(Other, NewPolys, grid);

        for (int i = 0; i < NewPolys.size(); i++)
            Polygons.add(new C2DHoledPolyArc(NewPolys.get(i)));
    }

    /// <summary>
    /// Gets the area.
    /// </summary>
    public double GetArea() 
    {
        double dArea = 0;

        for (int i = 0; i < Lines.size(); i++)
        {
            C2DPoint pt1 = Lines.get(i).GetPointFrom();
            C2DPoint pt2 = Lines.get(i).GetPointTo();

            dArea += pt1.x * pt2.y - pt2.x * pt1.y;
        }
        dArea = dArea / 2.0;

        for (int i = 0; i < Lines.size(); i++)	
        {
	        if (Lines.get(i) instanceof C2DArc)
	        {
		        C2DArc Arc = (C2DArc)Lines.get(i);

		        C2DSegment Seg = new C2DSegment( Arc );

		        dArea += Seg.GetAreaSigned();
	        }
        }
    	
        return Math.abs(dArea);

    }

    /// <summary>
    /// Returns the centroid.
    /// </summary>
    public C2DPoint GetCentroid() 
    {
        // Find the centroid and area of the straight line polygon.
        C2DPoint Centroid = new C2DPoint(0, 0);
     //   C2DPoint pti = new C2DPoint();
     //   C2DPoint ptii;
        double dArea = 0;

        for (int i = 0; i < Lines.size(); i++)
        {
	        C2DPoint pti = Lines.get(i).GetPointFrom();
	        C2DPoint ptii = Lines.get(i).GetPointTo();

	        Centroid.x += (pti.x + ptii.x) * (pti.x * ptii.y - ptii.x * pti.y);
	        Centroid.y += (pti.y + ptii.y) * (pti.x * ptii.y - ptii.x * pti.y);

	        dArea += pti.x * ptii.y - ptii.x * pti.y;
        }
        dArea = dArea / 2.0;

        Centroid.x = Centroid.x / (6.0 * dArea);
        Centroid.y = Centroid.y / (6.0 * dArea);

        ArrayList<Double> dSegAreas = new ArrayList<Double>();
        double dTotalArea = dArea;
        ArrayList<C2DPoint> SegCentroids = new ArrayList<C2DPoint>();

        for (int i = 0; i < Lines.size(); i++)
        {
	        if (Lines.get(i) instanceof C2DArc)
	        {
		        C2DSegment Seg = new C2DSegment( (C2DArc)Lines.get(i) );
		        double dSegArea = Seg.GetAreaSigned();
		        dTotalArea += dSegArea;
		        dSegAreas.add( dSegArea );
		        SegCentroids.add( Seg.GetCentroid() );
	        }
        }

        Centroid.Multiply( dArea);

        for (int i = 0; i < dSegAreas.size(); i++)
        {
	        Centroid.x += SegCentroids.get(i).x * dSegAreas.get(i);
            Centroid.y += SegCentroids.get(i).y * dSegAreas.get(i);
        }

        Centroid.Multiply( 1/ dTotalArea);
        return Centroid;

    }
    
    /// Rotates the polygon to the right around the centroid.
    /// 
    /// <summary>
    /// Rotates the shape to the right about the centroid.	
    /// </summary>
    /// <param name="dAng">The angle to rotate by.</param> 
    public void RotateToRight(double dAng)
    {
        RotateToRight( dAng, GetCentroid());
    }

}
