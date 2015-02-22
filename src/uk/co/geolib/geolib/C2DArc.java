package uk.co.geolib.geolib;

import java.util.ArrayList;

public class C2DArc extends C2DLineBase{
    /// <summary>
    /// Contructor.
    /// </summary>
    public C2DArc() {}

    /// <summary>
    /// Contructor.
    /// </summary>
    /// <param name="Other">Arc to which this will be assigned.</param>
    public C2DArc(C2DArc Other)
    {
        Set(Other);
    }


    /// <summary>
    /// Contructor.
    /// </summary>
    /// <param name="PtFrom">The point the arc is to go from.</param>
    /// <param name="PtTo">The point the arc is to go to.</param>
    /// <param name="dRadius">The corresponding circles radius.</param>
    /// <param name="bCentreOnRight">Whether the centre is on the right.</param>
    /// <param name="bArcOnRight">Whether the arc is to the right of the line.</param>
    public C2DArc(C2DPoint PtFrom, C2DPoint PtTo, double dRadius, 
	    boolean bCentreOnRight, boolean bArcOnRight)
    {
        line.Set(PtFrom, PtTo);
        Radius = dRadius;
        CentreOnRight = bCentreOnRight;
        ArcOnRight = bArcOnRight;
    }

    /// <summary>
    /// Contructor.
    /// </summary>
    /// <param name="PtFrom">The point the arc is to go from.</param>
    /// <param name="Vector">The vector defining the end point.</param>
    /// <param name="dRadius">The corresponding circles radius.</param>
    /// <param name="bCentreOnRight">Whether the centre is on the right.</param>
    /// <param name="bArcOnRight">Whether the arc is to the right of the line.</param>
    public C2DArc(C2DPoint PtFrom, C2DVector Vector, double dRadius, 
	    boolean bCentreOnRight, boolean bArcOnRight)
    {
        line.Set(PtFrom, Vector);
        Radius = dRadius;
        CentreOnRight = bCentreOnRight;
        ArcOnRight = bArcOnRight;
    }

    /// <summary>
    /// Contructor.
    /// </summary>
    /// <param name="Arcline">The line defining the start and end point of the arc.</param>
    /// <param name="dRadius">The corresponding circles radius.</param>
    /// <param name="bCentreOnRight">Whether the centre is on the right.</param>
    /// <param name="bArcOnRight">Whether the arc is to the right of the line.</param>
    public C2DArc(C2DLine Arcline, double dRadius, 
	    boolean bCentreOnRight, boolean bArcOnRight)
    {
        line.Set(Arcline);
        Radius = dRadius;
        CentreOnRight = bCentreOnRight;
        ArcOnRight = bArcOnRight;

    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="other">The arc to set this to.</param>
    public void Set(C2DArc other)
    {
        line.Set(other.line);
        Radius = other.Radius;
        CentreOnRight = other.CentreOnRight;
        ArcOnRight = other.ArcOnRight;

    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="PtFrom">The point the arc is to go from.</param>
    /// <param name="PtTo">The point the arc is to go to.</param>
    /// <param name="dRadius">The corresponding circles radius.</param>
    /// <param name="bCentreOnRight">Whether the centre is on the right.</param>
    /// <param name="bArcOnRight">Whether the arc is to the right of the line.</param>
    public void Set(C2DPoint PtFrom, C2DPoint PtTo, double dRadius, 
	    boolean bCentreOnRight, boolean bArcOnRight)
    {
        line.Set(PtFrom, PtTo);
        Radius = dRadius;
        CentreOnRight = bCentreOnRight;
        ArcOnRight = bArcOnRight;

    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="PtFrom">The point the arc is to go from.</param>
    /// <param name="Vector">The vector defining the end point.</param>
    /// <param name="dRadius">The corresponding circles radius.</param>
    /// <param name="bCentreOnRight">Whether the centre is on the right.</param>
    /// <param name="bArcOnRight">Whether the arc is to the right of the line.</param>
    public void Set(C2DPoint PtFrom, C2DVector Vector, double dRadius, 
	    boolean bCentreOnRight , boolean bArcOnRight)
    {
        line.Set(PtFrom, Vector);
        Radius = dRadius;
        CentreOnRight = bCentreOnRight;
        ArcOnRight = bArcOnRight;
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="Arcline">The line defining the start and end point of the arc.</param>
    /// <param name="dRadius">The corresponding circles radius.</param>
    /// <param name="bCentreOnRight">Whether the centre is on the right.</param>
    /// <param name="bArcOnRight">Whether the arc is to the right of the line.</param>
    public void Set(C2DLine Arcline, double dRadius, 
	    boolean bCentreOnRight, boolean bArcOnRight)
    {
        line.Set(Arcline);
        Radius = dRadius;
        CentreOnRight = bCentreOnRight;
        ArcOnRight = bArcOnRight;

    }

    /// <summary>
    /// Assignment given a straight line defining the end points and a point on the arc.
    /// </summary>
    /// <param name="Arcline">The line defining the start and end point of the arc.</param>
    /// <param name="ptOnArc">A point on the arc.</param>
    public void Set(C2DLine Arcline, C2DPoint ptOnArc)
    {
        line.Set(Arcline);
        C2DPoint ptTo = new C2DPoint(line.GetPointTo());

        C2DCircle Circle = new C2DCircle();
        Circle.SetCircumscribed( line.point , ptTo,  ptOnArc) ;
        Radius = line.point.Distance( Circle.getCentre() );
        ArcOnRight = line.IsOnRight(ptOnArc);
        CentreOnRight = line.IsOnRight(Circle.getCentre());
    }


    /// <summary>
    /// Creates a copy of this as a new object.
    /// </summary>
    public C2DLineBase CreateCopy()
    {
        return new C2DArc(this);
    }

    /// <summary>
    /// Tests to see if the radius is large enough to connect the end points.
    /// </summary>
    public boolean IsValid()
    {
        return (line.vector.GetLength() <= 2 * Radius);
    }

    /// <summary>
    /// Returns the corresponding circle's centre.
    /// </summary>
    public C2DPoint GetCircleCentre() 
    {
        if (!IsValid() ) 
	        return new C2DPoint(0, 0);

        C2DPoint MidPoint = new C2DPoint(line.GetMidPoint());
        double dMinToStart = MidPoint.Distance( line.point);

        double dMidToCentre = Math.sqrt( Radius * Radius - dMinToStart * dMinToStart);

        C2DVector MidToCentre = new C2DVector(line.vector);
        if ( CentreOnRight) 
            MidToCentre.TurnRight();
        else 
            MidToCentre.TurnLeft();

        MidToCentre.SetLength(dMidToCentre);

        return (MidPoint.GetPointTo(MidToCentre));
    }

    /// <summary>
    /// Returns the length of the curve.
    /// </summary>
    public double GetLength() 
    {
        if (CentreOnRight ^ ArcOnRight)
        {
	        return( Radius * GetSegmentAngle());
        }
        else
        {
	        return( Radius * (Constants.conTWOPI - GetSegmentAngle()) );
        }
    }

    /// <summary>
    ///  Gets the bounding rectangle.	
    /// </summary>
    /// <param name="Rect">The bounding rectangle to recieve the result.</param>	
    public void GetBoundingRect( C2DRect Rect)
    {
        if (!IsValid()) 
            return;

        C2DPoint CentrePoint = new C2DPoint( GetCircleCentre());
        C2DPoint EndPoint = new C2DPoint( line.GetPointTo());

        // First set up the rect that bounds the 2 points then check for if the arc expands this.
        line.GetBoundingRect( Rect);
    	
        // If the arc crosses the y axis..
        if (  ( (line.point.x - CentrePoint.x) * (EndPoint.x - CentrePoint.x) ) < 0 )
        {
	        // if the +ve y axis..
	        if ( line.GetMidPoint().y > CentrePoint.y)
	        {
		        if (CentreOnRight ^ ArcOnRight) 
		        {
			        Rect.SetTop(CentrePoint.y + Radius);

		        }
		        else
		        {
			        // If the segment is the "Big" bit....
			        Rect.SetBottom(CentrePoint.y - Radius);	
		        }
	        }
	        else // if the -ve y axis...
	        {
		        if (CentreOnRight ^ ArcOnRight)
		        {
			        Rect.SetBottom(CentrePoint.y - Radius);
		        }
		        else
		        {
			        // If the segment is th "Big" bit then...
			        Rect.SetTop(CentrePoint.y + Radius);
		        }
	        }
        }
        else if (!(CentreOnRight ^ ArcOnRight))
        {
	        Rect.SetBottom(CentrePoint.y - Radius);	
	        Rect.SetTop(CentrePoint.y + Radius);
        }

        // If the arc crosses the x axis..
        if (  ( (line.point.y - CentrePoint.y) * (EndPoint.y - CentrePoint.y) ) < 0 )
        {
	        // if the +ve x axis..
	        if ( line.GetMidPoint().x > CentrePoint.x)
	        {
		        if (CentreOnRight ^ ArcOnRight)
		        {
			        Rect.SetRight(CentrePoint.x + Radius);
		        }
		        else
		        {
			        // If the segment is th "Big" bit then...
			        Rect.SetLeft (CentrePoint.x - Radius);
		        }
	        }
	        else // if the -ve x axis...
	        {
		        if (CentreOnRight ^ ArcOnRight)
		        {
			        Rect.SetLeft(CentrePoint.x - Radius);
		        }
		        else
		        {
			        // If the segment is th "Big" bit then...
			        Rect.SetRight(CentrePoint.x + Radius);
		        }
	        }
        }
        else if (!(CentreOnRight ^ ArcOnRight))
        {
	        Rect.SetLeft(CentrePoint.x - Radius);
	        Rect.SetRight(CentrePoint.x + Radius);
        }

    }

    /// <summary>
    /// Gets the angle of the minimum segment. Always +ve and less than PI. In radians.	
    /// </summary>
    public double GetSegmentAngle() 
    {
        if (!IsValid()) 
            return 0;

        return ( 2 * Math.asin( (line.vector.GetLength() / 2) / Radius));
    }


    /// <summary>
    /// Returns the first point as a new object.	
    /// </summary>
    public C2DPoint GetPointFrom() 
    { 
        return new C2DPoint(line.point) ;
    }

    /// <summary>
    /// Returns the second point as a new object.	
    /// </summary>
    public C2DPoint GetPointTo() 
    { 
        return new C2DPoint(line.GetPointTo()) ;
    }

    /// <summary>
    /// True if this line crosses the other line, returns the intersection pt.
    /// </summary>
    /// <param name="Other">The other line to test.</param>
    public boolean Crosses(C2DLineBase Other)
    {
        return Crosses(Other, new ArrayList<C2DPoint>());
    }
    
    
    /// <summary>
    /// True if this crosses the line given as a base class.
    /// </summary>
    /// <param name="Other">The other line as an abstract base class.</param>
    /// <param name="IntersectionPts">The interection point list to recieve the result.</param>
    public boolean Crosses(C2DLineBase Other,  ArrayList<C2DPoint> IntersectionPts)
    {

        if (Other instanceof C2DLine)
        {
	        return Crosses((C2DLine) Other,  IntersectionPts);
        }
        else if (Other instanceof C2DArc)
        {
	        return Crosses((C2DArc) Other,  IntersectionPts);
        }
        else
        {
            assert false :"Invalid line type";
            return false;
        }

    }

    /// <summary>
    /// True if this crosses the straight line.
    /// </summary>
    /// <param name="Testline">The other line.</param>
    /// <param name="IntersectionPts">The interection point list to recieve the result.</param>
    public boolean Crosses(C2DLine Testline, ArrayList<C2DPoint> IntersectionPts)
    {
        ArrayList<C2DPoint> IntPtsTemp = new ArrayList<C2DPoint>();
        C2DCircle TestCircle = new C2DCircle(GetCircleCentre(), Radius);
        if (TestCircle.Crosses(Testline,  IntPtsTemp))
        {
	        for (int i = IntPtsTemp.size() - 1 ; i >= 0  ; i--)
	        {
                if (line.IsOnRight(IntPtsTemp.get(i)) ^ ArcOnRight ||
                    IntPtsTemp.get(i).PointEqualTo( line.point) ||
                    IntPtsTemp.get(i).PointEqualTo( line.GetPointTo()))
		        {
			        IntPtsTemp.remove(i);
		        }

	        }

	        if (IntPtsTemp.size() == 0)
		        return false;
	        else
	        {
                IntersectionPts.addAll(0,  IntPtsTemp);
            //    .( IntersectionPts );
		  //      IntersectionPts << IntPtsTemp;
		        return true;
	        }
        }
        else
        {
	        return false;
        }

    }

    /// <summary>
    /// True if this crosses a curved line.
    /// </summary>
    /// <param name="Other">The other arc.</param>
    /// <param name="IntersectionPts">The interection point list to recieve the result.</param>
    public boolean Crosses(C2DArc Other,   ArrayList<C2DPoint> IntersectionPts) 
    {
        C2DCircle TestCircleThis = new C2DCircle (GetCircleCentre(), Radius);
        C2DCircle TestCircleOther= new C2DCircle (Other.GetCircleCentre(), Other.Radius);

        ArrayList<C2DPoint> IntPtsTemp = new ArrayList<C2DPoint>();

        if (TestCircleThis.Crosses(TestCircleOther,  IntPtsTemp))
        {

	        for (int i = IntPtsTemp.size() - 1; i >= 0 ; i--)
	        {
		        if ((line.IsOnRight(IntPtsTemp.get(i)) ^ ArcOnRight) ||
			        Other.line.IsOnRight(IntPtsTemp.get(i)) ^ Other.ArcOnRight ||
			        IntPtsTemp.get(i).PointEqualTo( line.point) ||
			        IntPtsTemp.get(i).PointEqualTo( line.GetPointTo()) ||
			        IntPtsTemp.get(i).PointEqualTo( Other.GetPointFrom()) ||
			        IntPtsTemp.get(i).PointEqualTo( Other.GetPointTo()))
		        {
			        IntPtsTemp.remove(i);
		        }
	        }

	        if (IntPtsTemp.size() == 0)
		        return false;
	        else
	        {

                IntersectionPts.addAll(0, IntPtsTemp);
            //    IntPtsTemp.CopyTo( IntersectionPts );

			//        (*IntersectionPts) << IntPtsTemp;
		        return true;
	        }
        }
        else
        {
	        return false;
        }


    }

    /// <summary>
    /// True if this crosses the ray given. The ray is an infinite line represented by a line.
    /// The line is assumed no to end.
    /// </summary>
    /// <param name="Ray">The ray.</param>
    /// <param name="IntersectionPts">The interection point list to recieve the result.</param>
    public boolean CrossesRay(C2DLine Ray,   ArrayList<C2DPoint> IntersectionPts) 
    {
        double dDist = Ray.point.Distance(GetCircleCentre());
        C2DLine RayCopy = new C2DLine(Ray);
        // Ensure the copy line will go through the circle if the ray would.
        RayCopy.vector.SetLength((dDist + Radius) * 2);

        return Crosses(RayCopy,  IntersectionPts);

    }

    /// <summary>
    /// Distance between this and the test point.
    /// </summary>
    /// <param name="TestPoint">The test point.</param>
    public double Distance(C2DPoint TestPoint) 
    {
        C2DPoint pt = new C2DPoint();
        return Distance(TestPoint,  pt);
    }

    /// <summary>
    /// Distance between this and the test point.
    /// </summary>
    /// <param name="TestPoint">The test point.</param>
    /// <param name="ptOnThis">The closest point on this to the given point as a returned value.</param>
    public double Distance(C2DPoint TestPoint,  C2DPoint ptOnThis) 
    {
        C2DPoint ptCen = new C2DPoint( GetCircleCentre());
        C2DCircle Circle = new C2DCircle(ptCen, Radius);
        C2DPoint ptOnCircle = new C2DPoint();
        double dCircleDist = Circle.Distance(TestPoint,  ptOnCircle);

        if (ArcOnRight ^ line.IsOnRight(ptOnCircle))
        {
	        // The closest point on the circle isn't on the curve
	        double d1 = TestPoint.Distance(line.point);
	        double d2 = TestPoint.Distance(line.GetPointTo());
    		
	        if (d1 < d2)
	        {
			    ptOnThis.Set(line.point);
		        return d1;
	        }
	        else
	        {
		        ptOnThis.Set(line.GetPointTo());
		        return d2;
	        }
        }
        else
        {
	        // The closest point on the circle IS on the curve
	        ptOnThis.Set(ptOnCircle);
	        return Math.abs(dCircleDist);
        }


    }

    /// <summary>
    /// The distance between this and another arc.
    /// </summary>
    /// <param name="Other">The test point.</param>
    /// <param name="ptOnThis">The closest point on this to the other as a returned value.</param>
    /// <param name="ptOnOther">The closest point on the other to this as a returned value.</param>     
    public double Distance(C2DArc Other,  C2DPoint ptOnThis,  C2DPoint ptOnOther)
    {
        ArrayList<C2DPoint> IntPts1 = new ArrayList<C2DPoint>();
        ArrayList<C2DPoint> IntPts2 = new ArrayList<C2DPoint>();

        C2DPoint ptThisCen = new C2DPoint( GetCircleCentre() );
        C2DPoint ptOtherCen = new C2DPoint(Other.GetCircleCentre());

        C2DCircle CircleThis = new C2DCircle( ptThisCen, Radius);
        C2DCircle CircleOther = new C2DCircle( ptOtherCen, Other.Radius );

        if (CircleThis.Crosses(  CircleOther ,  IntPts1 ) )
        {
	        for (int i = 0; i < IntPts1.size(); i++)
	        {
		        if (  (line.IsOnRight( IntPts1.get(i) ) == ArcOnRight ) &&
			          (Other.line.IsOnRight( IntPts1.get(i) ) == Other.ArcOnRight )     )
		        {
				    ptOnThis.Set(IntPts1.get(i));
				    ptOnOther.Set(IntPts1.get(i));
			        return 0;
		        }
	        }

	        IntPts1.clear();
        }


        C2DLine lineCenToOther = new C2DLine();
        lineCenToOther.point = new C2DPoint(ptThisCen);
        lineCenToOther.vector = new C2DVector(ptThisCen, ptOtherCen);
        lineCenToOther.GrowFromCentre( Math.max(Radius, Other.Radius) * 10);

        double dMinDist = 1.7E308;
        double dDist = 0;

        if ( Crosses(lineCenToOther,  IntPts1) && Other.Crosses(lineCenToOther,  IntPts2))
        {
	        for (int i = 0 ; i < IntPts1.size(); i++)
	        {
		        for (int j = 0 ; j < IntPts2.size(); j++)
		        {
			        dDist = IntPts1.get(i).Distance(IntPts2.get(j));
			        if (dDist < dMinDist)
			        {
					    ptOnThis.Set( IntPts1.get(i));
					    ptOnOther.Set( IntPts2.get(j));

				        dMinDist = dDist;
			        }
		        }
	        }
        }

        C2DPoint ptOnThisTemp = new C2DPoint();
        dDist = Distance(Other.GetPointFrom(),  ptOnThisTemp);
        if (dDist < dMinDist)
        {
		    ptOnThis.Set(ptOnThisTemp);
		    ptOnOther.Set(Other.GetPointFrom());

	        dMinDist = dDist;
        }

        dDist = Distance(Other.GetPointTo(),  ptOnThisTemp);
        if (dDist < dMinDist)
        {
		    ptOnThis.Set(ptOnThisTemp);
		    ptOnOther.Set(Other.GetPointTo());

	        dMinDist = dDist;
        }

        C2DPoint ptOnOtherTemp = new C2DPoint();
        dDist = Other.Distance(GetPointFrom(),  ptOnOtherTemp);
        if (dDist < dMinDist)
        {
		    ptOnThis.Set( GetPointFrom());
		    ptOnOther.Set( ptOnOtherTemp);
	        dMinDist = dDist;
        }

        dDist = Other.Distance(GetPointTo(),  ptOnOtherTemp);
        if (dDist < dMinDist)
        {
		    ptOnThis.Set( GetPointTo());
		    ptOnOther.Set( ptOnOtherTemp);
	        dMinDist = dDist;
        }

        return dMinDist;

    }

    /// <summary>
    /// Distance between this and another straight line.
    /// </summary>
    /// <param name="Testline">The test line.</param>
    /// <param name="ptOnThis">The closest point on this to the other as a returned value.</param>
    /// <param name="ptOnOther">The closest point on the other to this as a returned value.</param>   
    public double Distance(C2DLine Testline,  C2DPoint ptOnThis,  C2DPoint ptOnOther) 
    {
        C2DCircle Circle = new C2DCircle( GetCircleCentre(), Radius);

        double dCircDist = Circle.Distance(Testline,  ptOnThis,  ptOnOther);
        double dDist = 0;

        if (Testline.IsOnRight(ptOnThis) ^ ArcOnRight)
        {
	        // The point found isn't on this. 
	        // This means the 2 closest points cannot be ON both lines, we must have a end point as one.

            ptOnThis.Set(line.point);
            dDist = Testline.Distance(ptOnThis,  ptOnOther);

            C2DPoint ptThisTemp = new C2DPoint(line.GetPointTo());
	        C2DPoint ptOtherTemp = new C2DPoint();
            double d2 = Testline.Distance(ptThisTemp,  ptOtherTemp);
	        if (d2 < dDist)
	        {
		        dDist = d2;
                ptOnThis.Set(ptThisTemp);
                ptOnOther.Set(ptOtherTemp);
	        }
	        // If the line was outside the circle then stop here as no need to go any further.
	        // This is because the closest point on this must be one of the end points.
	        if (dCircDist < 0)
	        {
                double d3 = Distance(Testline.point,  ptThisTemp);
		        if (d3 < dDist)
		        {
			        dDist = d3;
                    ptOnThis.Set(ptThisTemp);
                    ptOnOther.Set(line.point);
		        }
                double d4 = Distance(Testline.GetPointTo(),  ptThisTemp);
		        if (d4 < dDist)
		        {
			        dDist = d4;
                    ptOnThis.Set(ptThisTemp);
                    ptOnOther.Set(line.GetPointTo());
		        }	
	        }
        }
        else
        {
	        dDist = Math.abs(dCircDist);
        }

	//    ptOnThis.Set(ptThis);
	//    ptOnOther.Set(ptOther);

        return dDist;


    }

    /// <summary>
    /// Returns the minimum distance from the other line to this providing closest points.
    /// </summary>
    /// <param name="Other">The test point.</param>
    /// <param name="ptOnThis">The closest point on this to the other as a returned value.</param>
    /// <param name="ptOnOther">The closest point on the other to this as a returned value.</param> 
    public double Distance(C2DLineBase Other,  C2DPoint ptOnThis ,  C2DPoint ptOnOther )
    {
        if (Other instanceof C2DLine)
        {
            return Distance((C2DLine)Other,  ptOnThis,  ptOnOther);
        }
        else if (Other instanceof C2DArc)
        {
            return Distance((C2DArc)Other ,  ptOnThis,  ptOnOther);
        }
        else
        {
        	assert false : "Invalid line type";
        
            return 0;
        }



   }

   /// <summary>
   /// Returns the projection of this onto the line provided, given as the interval on
   /// (or off) the line. Interval given as distance from the start of the line.
   /// </summary>
   /// <param name="Testline">The projection line.</param>
   /// <param name="Interval">The interval to recieve the result.</param>
    public void Project(C2DLine Testline,  CInterval Interval) 
    {
        C2DArc ThisCopy = new C2DArc(this);
        C2DLine lineCopy = new C2DLine(Testline);

        double dAng = lineCopy.vector.AngleFromNorth();

        lineCopy.vector.TurnLeft(dAng);
        ThisCopy.RotateToRight( -dAng, lineCopy.point);

        C2DRect rect = new C2DRect();
        ThisCopy.GetBoundingRect( rect);

        Interval.dMax = rect.GetTop() - lineCopy.point.y;
        Interval.dMin = Interval.dMax;

        Interval.ExpandToInclude(rect.GetBottom() - lineCopy.point.y);


    }

    /// <summary>
    /// Returns the projection of this onto the vector provided, given as the interval on
    /// (or off) the vector. Interval given as distance from the start of the vector.
    /// The vector is equivalent to a line from (0, 0).
    /// </summary>
    /// <param name="Vector">The projection vector.</param>
    /// <param name="Interval">The interval to recieve the result.</param>
    public void Project(C2DVector Vector,  CInterval Interval)
    {
        C2DArc ThisCopy = new C2DArc(this);
        C2DVector VecCopy = new C2DVector(Vector);

        double dAng = VecCopy.AngleFromNorth();

        VecCopy.TurnLeft(dAng);
        ThisCopy.RotateToRight( -dAng, new C2DPoint(0, 0));

        C2DRect rect = new C2DRect();
        ThisCopy.GetBoundingRect( rect);

        Interval.dMax = rect.GetTop() - VecCopy.j;
        Interval.dMin = Interval.dMax;

        Interval.ExpandToInclude( rect.GetBottom() - VecCopy.j );
    }

    /// <summary>
    /// Gets the point half way along the curve as a new object.
    /// </summary>
    public C2DPoint GetMidPoint() 
    {
    	assert IsValid() : "Invalid arc defined, cannot calculate midpoint";
        // Make a line from the circle centre to the middle of the line
        C2DPoint ptCentre = new C2DPoint(GetCircleCentre());

        C2DPoint ptlineCentre = new C2DPoint(line.GetMidPoint());

        C2DLine CenToMid = new C2DLine(ptCentre, ptlineCentre);

        if ( CenToMid.vector.i == 0 && CenToMid.vector.j == 0)
        {
	        // The centre of the line is the same as the centre of the circle
	        // i.e. this arc is 180 degrees or half a circle.
	        CenToMid.Set(line);
	        CenToMid.SetPointFrom( ptlineCentre );
	        if ( ArcOnRight )
		        CenToMid.vector.TurnRight();
	        else
		        CenToMid.vector.TurnLeft();
        }
        else
        {
	        // extend it to the edge of the arc
	        CenToMid.SetLength( Radius );
	        // if the arc on the opposite side to the centre then reverse the line.
	        if ( ArcOnRight == CentreOnRight)
	        {
		        CenToMid.vector.Reverse();
	        }
        }

        return CenToMid.GetPointTo();

    }

    /// <summary>
    /// Gets the point on the curve determined by the factor as a new object.
    /// </summary>
    public C2DPoint GetPointOn(double dFactorFromStart) 
    {
      //  Debug.Assert(IsValid(), "Invalid arc defined, function failure." );
        // make 2 lines from the centre to the ends of the line
        C2DPoint ptCentre = new C2DPoint(GetCircleCentre());

        C2DLine CenToStart = new C2DLine(ptCentre, line.point);

        C2DLine CenToEnd = new C2DLine(ptCentre, line.GetPointTo());

        if ( !ArcOnRight)	// clockwise
        {
	        // Find the angle from one to the other and muliply it by the factor
	        // before turning the line's vector by the result.
	        double dAngleToRight = CenToStart.vector.AngleToRight( CenToEnd.vector );
	        double dNewAngle = dAngleToRight* dFactorFromStart;
	        CenToStart.vector.TurnRight( dNewAngle );
	        return CenToStart.GetPointTo();
        }
        else	// anticlockwise
        {
	        double dAngleToLeft = CenToStart.vector.AngleToLeft( CenToEnd.vector );
	        double dNewAngle = dAngleToLeft* dFactorFromStart;
	        CenToStart.vector.TurnLeft( dNewAngle );
	        return CenToStart.GetPointTo();
        }
    }

    /// <summary>
    /// Move by the vector given.
    /// </summary>
    /// <param name="vector">The vector.</param>
    public void Move(C2DVector vector) 
    {
        line.Move(vector);
    }

    /// <summary>
    /// Rotates this to the right about the origin provided.
    /// </summary>
    /// <param name="dAng">The angle through which to rotate.</param>
    /// <param name="Origin">The origin about which to rotate.</param>
    public void RotateToRight(double dAng, C2DPoint Origin)  
	{
        line.RotateToRight(dAng, Origin);
    }

    /// <summary>
    /// Grow relative to the origin.
    /// </summary>
    /// <param name="dFactor">The factor to grow this by.</param>
    /// <param name="Origin">The origin about which this is to be grown.</param>  
    public void Grow(double dFactor, C2DPoint Origin)
    {
    	line.Grow(dFactor, Origin);
        Radius *= dFactor;
    }

    /// <summary>
    /// Reflect in the point.
    /// </summary>
    /// <param name="point">The point through which this will be reflected.</param> 
    public void Reflect(C2DPoint point)
    {
        line.Reflect( point);
    }

    /// <summary>
    /// Reflects throught the line provided.
    /// </summary>
    /// <param name="Testline">The line through which this will be reflected.</param> 
    public void Reflect(C2DLine Testline)
    {
        line.Reflect(Testline);
        ArcOnRight = !ArcOnRight;
        CentreOnRight = !CentreOnRight;
    }

    /// <summary>
    /// Reverses the direction.
    /// </summary>
    public void ReverseDirection()
    {
        line.ReverseDirection();
        ArcOnRight = !ArcOnRight;
        CentreOnRight = !CentreOnRight;
    }

    /// <summary>
    /// Returns the lines that go to make this up based on the set of points 
    /// provided which are assumed to be on the line.
    /// </summary>
    /// <param name="PtsOnline">The points defining how the line is to be split.</param> 
    /// <param name="lineSet">The line set to recieve the result.</param> 
	@Override
    public void GetSubLines(ArrayList<C2DPoint> PtsOnline,  ArrayList<C2DLineBase> lineSet) 
    {
        
        // if there are no points on the line to split on then add a copy of this and return.
        int usPointssize = PtsOnline.size();
        if (usPointssize == 0 )
        {
	        lineSet.add(new C2DArc(this));
	        return;
        }
        else
        {
	        // Make a copy of the points for sorting.
	        C2DPointSet TempPts = new C2DPointSet();
	        TempPts.MakeCopy(PtsOnline);

	        if (usPointssize > 1) // They need sorting
	        {
		        // Make a line from the mid point of my line to the start
		        C2DLine CenToStart = new C2DLine( line.GetMidPoint(), line.point );
		        // Now sort the points according to the order in which they will be encountered
		        if (ArcOnRight)
			        TempPts.SortByAngleToLeft( CenToStart );
		        else
			        TempPts.SortByAngleToRight( CenToStart );
	        }

	        C2DPoint ptCentre = new C2DPoint(GetCircleCentre());

	        // Add the line from the start of this to the first.
	        C2DLine Newline = new C2DLine( line.point, TempPts.get(0) );
            lineSet.add(new C2DArc(Newline, Radius,
                                      Newline.IsOnRight(ptCentre), ArcOnRight));

	        // Add all the sub lines.
	        for (int i = 1; i < usPointssize; i++)
	        {
                Newline.Set(TempPts.get(i - 1), TempPts.get(i));
                lineSet.add(new C2DArc(Newline, Radius,
                                        Newline.IsOnRight(ptCentre), ArcOnRight));
	        }
	        // Add the line from the last point on this to the end of this.
            Newline.Set(TempPts.get(TempPts.size() - 1), line.GetPointTo());
            lineSet.add(new C2DArc(Newline, Radius,
                                       Newline.IsOnRight(ptCentre), ArcOnRight));
        }
        
    }

    /// <summary>
    /// Snaps this to the conceptual grid.
    /// </summary>
    /// <param name="grid">The grid object to snap this to.</param> 
    public void SnapToGrid(CGrid grid)
    {
        line.SnapToGrid(grid);

        double dLength = line.vector.GetLength();

        if (dLength > (2 * Radius))
        {
            Radius = dLength / 1.999999999999;	// To ensure errors in the right way.
        }
    }


    /// <summary>
    ///  Transform by a user defined transformation. e.g. a projection.
    /// </summary>
    public void Transform(CTransformation pProject)
    {
        this.line.Transform(pProject);
    }

    /// <summary>
    ///  Transform by a user defined transformation. e.g. a projection.
    /// </summary>
    public void InverseTransform(CTransformation pProject)
    {
        this.line.InverseTransform(pProject);
    }




    /// <summary>
    /// The radius.
    /// </summary>
    public double Radius;
    /// <summary>
    /// Whether the associated circle centre is to the right of the line.
    /// </summary>
    public boolean CentreOnRight;
    /// <summary>
    /// Whether the arc is to the right of the line.
    /// </summary>
    public boolean ArcOnRight;
    /// <summary>
    /// The straight line used to define the start and end points of the line.
    /// </summary> 
    protected C2DLine line = new C2DLine();
    /// <summary>
    /// The straight line used to define the start and end points of the line.
    /// </summary> 
    public C2DLine getline()
    {
         return line;
    }



}
