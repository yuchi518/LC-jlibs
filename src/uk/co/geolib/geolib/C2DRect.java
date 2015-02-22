package uk.co.geolib.geolib;

public class C2DRect  extends C2DBase {
    /// <summary>
    /// Constructor.
    /// </summary>
    public C2DRect() {}


    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="Other">The other rect.</param>   
    public C2DRect(C2DRect Other)
    {
        topLeft.Set(Other.topLeft);
        bottomRight.Set(Other.bottomRight);
    }

    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="pttopLeft">The top left point.</param>  
    /// <param name="ptbottomRight">The bottom right point.</param>  
    public C2DRect(C2DPoint pttopLeft, C2DPoint ptbottomRight)
    {
        topLeft.Set(pttopLeft);
        bottomRight.Set(ptbottomRight);
    }

    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="dLeft">Left.</param>  
    /// <param name="dTop">Top.</param>  
    /// <param name="dRight">Right.</param>  
    /// <param name="dBottom">Bottom.</param>  
    public C2DRect(double dLeft, double dTop, double dRight, double dBottom)
    {
        topLeft.x = dLeft;
        topLeft.y = dTop;

        bottomRight.x = dRight;
        bottomRight.y = dBottom;
    }

    /// <summary>
    /// Constructor sets both the top left and bottom right to equal the rect.
    /// </summary>
    /// <param name="pt">Point.</param>  
    public C2DRect(C2DPoint pt )
    {
        topLeft.Set(pt);
        bottomRight.Set(pt);
    }

    /// <summary>
    /// Sets both the top left and bottom right to equal the rect.
    /// </summary>
    /// <param name="pt">Point.</param>  
    public void Set( C2DPoint pt)
    {
        topLeft.Set(pt);
        bottomRight.Set(pt);
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="pttopLeft">The top left point.</param>  
    /// <param name="ptbottomRight">The bottom right point.</param>  
    public void Set(C2DPoint pttopLeft, C2DPoint ptbottomRight)
    {
        topLeft.Set(pttopLeft);
        bottomRight.Set(ptbottomRight);
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="dLeft">Left.</param>  
    /// <param name="dTop">Top.</param>  
    /// <param name="dRight">Right.</param>  
    /// <param name="dBottom">Bottom.</param>  
    public void Set(double dLeft, double dTop, double dRight, double dBottom)
    {
        topLeft.x = dLeft;
        topLeft.y = dTop;

        bottomRight.x = dRight;
        bottomRight.y = dBottom;
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="dTop">Top.</param>  
    public void SetTop(double dTop) 
    {
        topLeft.y = dTop; 
    }


    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="dLeft">Left.</param>  
    public void SetLeft(double dLeft) 
    {
        topLeft.x = dLeft; 
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="dBottom">Bottom.</param>  
    public void SetBottom(double dBottom) 
    {
        bottomRight.y = dBottom; 
    }


    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="dRight">Right.</param>  
    public void SetRight(double dRight) 
    {
        bottomRight.x = dRight; 
    }

    /// <summary>
    /// Clears the rectangle.
    /// </summary>
    public void Clear()
    {
        topLeft.x = 0;
        topLeft.y = 0;
        bottomRight.x = 0;
        bottomRight.y = 0;
    }

    /// <summary>
    /// Expands to include the point.
    /// </summary>
    /// <param name="NewPt">Point.</param> 
    public void ExpandToInclude(C2DPoint NewPt)
    {
        if (NewPt.x > bottomRight.x) 
            bottomRight.x = NewPt.x;
        else if (NewPt.x < topLeft.x) 
            topLeft.x = NewPt.x;
        if (NewPt.y > topLeft.y) 
            topLeft.y = NewPt.y;
        else if (NewPt.y < bottomRight.y) 
            bottomRight.y = NewPt.y;
    }

    /// <summary>
    /// Expands to include the rectangle.
    /// </summary>
    /// <param name="Other">Rectangle.</param> 
    public void ExpandToInclude(C2DRect Other)
    {
        ExpandToInclude(Other.topLeft);
        ExpandToInclude(Other.bottomRight);
    }

    /// <summary>
    /// True if there is an overlap, returns the overlap.
    /// </summary>
    /// <param name="Other">Rectangle.</param> 
    /// <param name="Overlap">Output. The overlap.</param> 
    public boolean Overlaps(C2DRect Other, C2DRect Overlap)
    {
        C2DPoint ptOvTL = new C2DPoint();
        C2DPoint ptOvBR = new C2DPoint();

        ptOvTL.y = Math.min(topLeft.y, Other.topLeft.y);
        ptOvBR.y = Math.max(bottomRight.y, Other.bottomRight.y);

        ptOvTL.x = Math.max(topLeft.x, Other.topLeft.x);
        ptOvBR.x = Math.min(bottomRight.x, Other.bottomRight.x);

        Overlap.Set(ptOvTL, ptOvBR);

        return Overlap.IsValid();
    }

    /// <summary>
    /// True if the point is within the rectangle.
    /// </summary>
    /// <param name="Pt">Point.</param> 
    public boolean Contains(C2DPoint Pt)
    {
        return (Pt.x >= topLeft.x && Pt.x <= bottomRight.x &&
                 Pt.y <= topLeft.y && Pt.y >= bottomRight.y);
    }


    /// <summary>
    /// True if the entire other rectangle is within.
    /// </summary>
    /// <param name="Other">Other rectangle.</param> 
    public boolean Contains(C2DRect Other)
    {
        return (Other.GetLeft() > topLeft.x &&
                  Other.GetRight() < bottomRight.x &&
                  Other.GetBottom() > bottomRight.y &&
                  Other.GetTop() < topLeft.y);
    }

    /// <summary>
    /// True if there is an overlap.
    /// </summary>
    /// <param name="Other">Other rectangle.</param> 
    public boolean Overlaps(C2DRect Other)
    {
    	boolean bOvX = !(Other.GetLeft() >= bottomRight.x ||
                      Other.GetRight() <= topLeft.x);

    	boolean bOvY = !(Other.GetBottom() >= topLeft.y ||
                      Other.GetTop() <= bottomRight.y);

        return bOvX && bOvY;
    }

    /// <summary>
    /// If the area is positive e.g. the top is greater than the bottom.
    /// </summary>
    public boolean IsValid()
    {
        return ((topLeft.x < bottomRight.x) && (topLeft.y > bottomRight.y));
    }

    /// <summary>
    /// Returns the area.
    /// </summary>
    public double GetArea()
    {
        return ((topLeft.y - bottomRight.y) * (bottomRight.x - topLeft.x));
    }

    /// <summary>
    /// Returns the width.
    /// </summary>
    public double Width()
    {
        return (bottomRight.x - topLeft.x);
    }

    /// <summary>
    /// Returns the height.
    /// </summary>
    public double Height()
    {
        return (topLeft.y - bottomRight.y);
    }

    /// <summary>
    /// Returns the top.
    /// </summary>
    public double GetTop( ) 
    {
        return  topLeft.y;
    }

    /// <summary>
    /// Returns the left.
    /// </summary>
    public double GetLeft( )   
    {
        return  topLeft.x ;
    }


    /// <summary>
    /// Returns the bottom.
    /// </summary>
    public double GetBottom( )  
    {
        return bottomRight.y;
    }

    /// <summary>
    /// Returns the right.
    /// </summary>
    public double GetRight( )  
    {
        return bottomRight.x ;
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="Other">Other rectangle.</param> 
    public void Set(C2DRect Other)
    {
        topLeft.x = Other.topLeft.x;
        topLeft.y = Other.topLeft.y;
        bottomRight.x = Other.bottomRight.x;
        bottomRight.y = Other.bottomRight.y;
    }

    /// <summary>
    /// Grows it from its centre.
    /// </summary>
    /// <param name="dFactor">Factor to grow by.</param> 
    public void Grow(double dFactor)
    {
        C2DPoint ptCentre = new C2DPoint(GetCentre());

        bottomRight.x = (bottomRight.x - ptCentre.x) * dFactor + ptCentre.x;
        bottomRight.y = (bottomRight.y - ptCentre.y) * dFactor + ptCentre.y;

        topLeft.x = (topLeft.x - ptCentre.x) * dFactor + ptCentre.x;
        topLeft.y = (topLeft.y - ptCentre.y) * dFactor + ptCentre.y;

    }

    /// <summary>
    /// Grow the height it from its centre.
    /// </summary>
    /// <param name="dFactor">Factor to grow by.</param> 
    public void GrowHeight(double dFactor)
    {
        C2DPoint ptCentre = new C2DPoint(GetCentre());
        bottomRight.y = (bottomRight.y - ptCentre.y) * dFactor + ptCentre.y;
        topLeft.y = (topLeft.y - ptCentre.y) * dFactor + ptCentre.y;

    }

    /// <summary>
    /// Grows the width from its centre.
    /// </summary>
    /// <param name="dFactor">Factor to grow by.</param> 
    public void GrowWidth(double dFactor)
    {
        C2DPoint ptCentre = new C2DPoint(GetCentre());
        bottomRight.x = (bottomRight.x - ptCentre.x) * dFactor + ptCentre.x;
        topLeft.x = (topLeft.x - ptCentre.x) * dFactor + ptCentre.x;

    }

    /// <summary>
    /// Expands from the centre by the fixed amount given.
    /// </summary>
    /// <param name="dRange">Amount to expand by.</param> 
    public void Expand(double dRange)
    {
        bottomRight.x += dRange;
        bottomRight.y -= dRange;

        topLeft.x -= dRange;
        topLeft.y += dRange;
    }

    /// <summary>
    /// Grows it from the given point.
    /// </summary>
    /// <param name="dFactor">Factor to grow by.</param> 
    /// <param name="Origin">The origin.</param> 
    public void Grow(double dFactor, C2DPoint Origin)
    {
        bottomRight.Grow(dFactor, Origin);
        topLeft.Grow(dFactor, Origin);
    }

    /// <summary>
    /// Moves this point by the vector given.
    /// </summary>
    /// <param name="Vector">The vector.</param>
    public void Move(C2DVector Vector)
    {
        topLeft.Move(Vector);
        bottomRight.Move(Vector);
    }

    /// <summary>
    /// Reflect throught the point given. 
    /// Switches Top Left and Bottom Right to maintain validity.
    /// </summary>
    /// <param name="Point">Reflection point.</param> 
    public void Reflect(C2DPoint Point)
    {
        topLeft.Reflect(Point);
        bottomRight.Reflect(Point);

        double x = topLeft.x;
        double y = topLeft.y;

        topLeft.Set( bottomRight);
        bottomRight.x = x;
        bottomRight.y = y;
    }

    /// <summary>
    /// Reflect throught the line by reflecting the centre of the 
    /// rect and keeping the validity.
    /// </summary>
    /// <param name="Line">Reflection Line.</param> 
    public void Reflect(C2DLine Line)
    {
        C2DPoint ptCen = new C2DPoint(this.GetCentre());
        C2DPoint ptNewCen = new C2DPoint(ptCen);
        ptNewCen.Reflect(Line);
        C2DVector vec = new C2DVector(ptCen, ptNewCen);
        Move(vec);
    }

    /// <summary>
    /// Rotates this to the right about the origin provided.
    /// Note that as the horizontal/vertical line property will be
    /// preserved. If you rotate an object and its bounding box, the box may not still
    /// bound the object.
    /// </summary>
    /// <param name="dAng">The angle through which to rotate.</param>
    /// <param name="Origin">The origin about which to rotate.</param>
    public void RotateToRight(double dAng, C2DPoint Origin)
    {
        double dHalfWidth = Width() / 2;
        double dHalfHeight = Height() / 2;

        C2DPoint ptCen = new C2DPoint(GetCentre());
        ptCen.RotateToRight(dAng, Origin);

        topLeft.x = ptCen.x - dHalfWidth;
        topLeft.y = ptCen.y + dHalfHeight;
        bottomRight.x = ptCen.x + dHalfWidth;
        bottomRight.y = ptCen.y - dHalfHeight;
    }

    /// <summary>
    /// Returns the distance from this to the point. 0 if the point inside.
    /// </summary>
    /// <param name="TestPoint">Test Point.</param> 
    public double Distance(C2DPoint TestPoint)
    {
        if (TestPoint.x > bottomRight.x) // To the east half
        {
            if (TestPoint.y > topLeft.y)			// To the north east
                return TestPoint.Distance(new C2DPoint(bottomRight.x, topLeft.y));
            else if (TestPoint.y < bottomRight.y)		// To the south east
                return TestPoint.Distance(bottomRight);
            else
                return (TestPoint.x - bottomRight.x);	// To the east
        }
        else if (TestPoint.x < topLeft.x)	// To the west half
        {
            if (TestPoint.y > topLeft.y)			// To the north west
                return TestPoint.Distance(topLeft);
            else if (TestPoint.y < bottomRight.y)		// To the south west
                return TestPoint.Distance(new C2DPoint(topLeft.x, bottomRight.y));
            else
                return (topLeft.x - TestPoint.x);	// To the west
        }
        else
        {
            if (TestPoint.y > topLeft.y)		//To the north
                return (TestPoint.y - topLeft.y);
            else if (TestPoint.y < bottomRight.y)	// To the south
                return (bottomRight.y - TestPoint.y);
        }

      //  assert(Contains(TestPoint));
        return 0;	// Inside
    }

    /// <summary>
    /// Returns the distance from this to the other rect. 0 if there is an overlap.
    /// </summary>
    /// <param name="Other">Other rectangle.</param> 
   public double Distance(C2DRect Other)
   {
        if (this.Overlaps(Other))
	        return 0;

        if (Other.GetLeft() > this.bottomRight.x)
        {
	        // Other is to the right
	        if (Other.GetBottom() > this.topLeft.y)
	        {
		        // Other is to the top right
		        C2DPoint ptTopRight = new C2DPoint(bottomRight.x,  topLeft.y);
		        return ptTopRight.Distance(new C2DPoint(Other.GetLeft(), Other.GetBottom()));
	        }
	        else if (Other.GetTop() < this.bottomRight.y)
	        {
		        // Other to the bottom right
		        return bottomRight.Distance( Other.topLeft );
	        }
	        else
	        {
		        // to the right
		        return Other.GetLeft() - this.bottomRight.x;
	        }
        }
        else if ( Other.GetRight() < this.topLeft.x)
        {
	        // Other to the left
	        if (Other.GetBottom() > this.topLeft.y)
	        {
		        // Other is to the top left
		        return  topLeft.Distance(Other.bottomRight);
	        }
	        else if (Other.GetTop() < this.bottomRight.y)
	        {
		        // Other to the bottom left
		        C2DPoint ptBottomLeft = new C2DPoint(topLeft.x, bottomRight.y);
		        return ptBottomLeft.Distance ( new C2DPoint( Other.GetRight(), Other.GetTop()));
	        }
	        else
	        {
		        //Just to the left
		        return (this.topLeft.x - Other.GetRight());
	        }
        }
        else
        {
	        // There is horizontal overlap;
	        if (Other.GetBottom() >  topLeft.y)
		        return Other.GetBottom() -  topLeft.y;
	        else
		        return bottomRight.y - Other.GetTop();
        }		

    }

    /// <summary>
    /// Returns the bounding rectangle. (Required for virtual base class).
    /// </summary>
    /// <param name="Rect">Ouput. Bounding rectangle.</param> 
    public void GetBoundingRect(C2DRect Rect) 
    { 
        Rect.Set(this);
    }

    /// <summary>
    /// Scales the rectangle accordingly.
    /// </summary>
    public void Scale(C2DPoint ptScale) 
    {
        topLeft.x =  topLeft.x * ptScale.x;
        topLeft.y = topLeft.y * ptScale.y; 

	    bottomRight.x = bottomRight.x * ptScale.x;
        bottomRight.y = bottomRight.y * ptScale.y;
    }

    /// <summary>
    /// Returns the centre.
    /// </summary>
    public C2DPoint GetCentre()
    {
        return bottomRight.GetMidPoint(topLeft);
    }

    /// <summary>
    /// Returns the point which is closest to the origin (0,0).
    /// </summary>
    public C2DPoint GetPointClosestToOrigin()
    {
        C2DPoint ptResult = new C2DPoint();
        if (Math.abs(topLeft.x) < Math.abs(bottomRight.x))
        {
            // Left is closest to the origin.
            ptResult.x = topLeft.x;
        }
        else
        {
            // Right is closest to the origin
            ptResult.x = bottomRight.x;
        }

        if (Math.abs(topLeft.y) < Math.abs(bottomRight.y))
        {
            // Top is closest to the origin.
            ptResult.y = topLeft.y;
        }
        else
        {
            // Bottom is closest to the origin
            ptResult.y = bottomRight.y;
        }

        return ptResult;
    }

    /// <summary>
    /// Returns the point which is furthest from the origin (0,0).
    /// </summary>
    public C2DPoint GetPointFurthestFromOrigin()
    {
        C2DPoint ptResult = new C2DPoint();
        if (Math.abs(topLeft.x) > Math.abs(bottomRight.x))
        {
            // Left is furthest to the origin.
            ptResult.x = topLeft.x;
        }
        else
        {
            // Right is furthest to the origin
            ptResult.x = bottomRight.x;
        }

        if (Math.abs(topLeft.y) > Math.abs(bottomRight.y))
        {
            // Top is furthest to the origin.
            ptResult.y = topLeft.y;
        }
        else
        {
            // Bottom is furthest to the origin
            ptResult.y = bottomRight.y;
        }

        return ptResult;
    }

    /// <summary>
    /// Projection onto the line
    /// </summary>
    /// <param name="Line">Line to project on.</param> 
    /// <param name="Interval">Ouput. Projection.</param> 
    public void Project(C2DLine Line,  CInterval Interval)
    {
        this.topLeft.Project( Line,  Interval);
        Interval.ExpandToInclude( bottomRight.Project( Line));
        C2DPoint TR = new C2DPoint( bottomRight.x,   topLeft.y);
        C2DPoint BL = new C2DPoint( topLeft.x, bottomRight.y);
        Interval.ExpandToInclude( TR.Project( Line));
        Interval.ExpandToInclude( BL.Project( Line));

    }

    /// <summary>
    /// Projection onto the Vector.
    /// </summary>
    /// <param name="Vector">Vector to project on.</param> 
    /// <param name="Interval">Ouput. Projection.</param> 
    public void Project(C2DVector Vector,  CInterval Interval)
    {
        this.topLeft.Project( Vector,  Interval);
        Interval.ExpandToInclude( bottomRight.Project( Vector));
        C2DPoint TR = new C2DPoint( bottomRight.x,   topLeft.y);
        C2DPoint BL = new C2DPoint(topLeft.x, bottomRight.y);
        Interval.ExpandToInclude( TR.Project( Vector));
        Interval.ExpandToInclude( BL.Project( Vector));

    }

    /// <summary>
    /// Snaps this to the conceptual grid.
    /// </summary>
    /// <param name="grid">Grid to snap to.</param> 
    public void SnapToGrid(CGrid grid)
    {
        topLeft.SnapToGrid(grid);
        bottomRight.SnapToGrid(grid);

    }



    /// <summary>
    /// True if this is above or below the other
    /// </summary>
    /// <param name="Other"></param>
    /// <returns></returns>
    public boolean OverlapsVertically( C2DRect Other)
    {
        return !(Other.GetLeft() >= bottomRight.x ||
			          Other.GetRight() <=  topLeft.x);
    }


    /// <summary>
    /// True if this is above the other.
    /// </summary>
    /// <param name="Other"></param>
    /// <returns></returns>
    public boolean OverlapsAbove( C2DRect Other)
    {
        if (Other.GetLeft() >= bottomRight.x ||
			          Other.GetRight() <=  topLeft.x)
        {
	        return false;
        }
        else 
        {
	        return topLeft.y > Other.GetBottom();
        }
    }


    /// <summary>
    /// True if this is below the other.
    /// </summary>
    /// <param name="Other"></param>
    /// <returns></returns>
    public boolean OverlapsBelow( C2DRect Other)
    {
        if (Other.GetLeft() >= bottomRight.x ||
			          Other.GetRight() <=  topLeft.x)
        {
	        return false;
        }
        else 
        {
	        return bottomRight.y < Other.GetTop();
        }
    }

    
    /// <summary>
    /// Returns the top left.
    /// </summary>
    public C2DPoint GetBottomLeft()
    {
        return new C2DPoint(this.topLeft.x, this.bottomRight.y);
    }

    /// <summary>
    /// Returns the bottom right.
    /// </summary>
    public C2DPoint GetTopRight() 
    {
        return new C2DPoint(this.bottomRight.x, this.topLeft.y);
    }



    /// <summary>
    /// True if this crosses the line.
    /// </summary>
    public boolean Crosses(C2DLineBase line)
    {
        C2DLine l1 = new C2DLine(bottomRight, GetTopRight());
        if (line.Crosses(l1))
	        return true;

        C2DLine l2 = new C2DLine(GetTopRight(), topLeft);
        if (line.Crosses(l2))
	        return true;

        C2DLine l3 = new C2DLine(topLeft, GetBottomLeft());
        if (line.Crosses(l3))
	        return true;

        C2DLine l4 = new C2DLine(GetBottomLeft(), bottomRight);
        if (line.Crosses(l4))
	        return true;

        return false;
    }
    
    

    /// <summary>
    /// Top left.
    /// </summary>
    private C2DPoint topLeft = new C2DPoint();
    /// <summary>
    /// Top left.
    /// </summary>
    public C2DPoint getTopLeft()
    {
        return topLeft;
    }
    /// <summary>
    /// Bottom right.
    /// </summary>
    private C2DPoint bottomRight = new C2DPoint();
    /// <summary>
    /// Bottom right.
    /// </summary>
    public C2DPoint getBottomRight()
    {
        return bottomRight;
    }

}
