package uk.co.geolib.geolib;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;

public class C2DPointSet extends ArrayList<C2DPoint> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/// <summary>
    /// Constructor.
    /// </summary>
    public C2DPointSet() { }

    /// <summary>
    /// Makes a copy of the other set.
    /// </summary>
    /// <param name="Other">The other set.</param>
    public void MakeCopy(ArrayList<C2DPoint> Other)
    {
        this.clear();
        for (int i = 0; i < Other.size(); i++)
        {
            this.add(new C2DPoint(Other.get(i)));
        }
    }
    /// <summary>
    /// Extracts all of the other set.
    /// </summary>
    /// <param name="S2">The other set.</param>
    public void ExtractAllOf(C2DPointSet S2)
    {
        for (int i = 0; i < S2.size(); i++)
        {
            add(S2.get(i));
        }
        S2.clear();
    }
    /// <summary>
    /// Adds a copy of the point.
    /// </summary>
    /// <param name="P1">The point.</param>
    public void AddCopy(C2DPoint P1)
    {
        add(new C2DPoint(P1));
    }
    /// <summary>
    /// Adds a copy of the point set.
    /// </summary>
    /// <param name="Other">The point set.</param>
    public void AddCopy(ArrayList<C2DPoint> Other)
    {
        for (int i = 0 ; i < Other.size() ; i++)
            add(new C2DPoint(Other.get(i)));
    }
    /// <summary>
    /// Extracts at the index given.
    /// </summary>
    /// <param name="nIndex">The index.</param>
    public C2DPoint ExtractAt(int nIndex)
    {
        C2DPoint Result = this.get(nIndex);
        this.remove(nIndex);
        return Result;
    }
    /// <summary>
    /// Removes the convex hull from the point set given.
    /// Will affect the input set.
    /// </summary>
    /// <param name="Other">The other set.</param>
    public void ExtractConvexHull(  C2DPointSet Other)
    {
        clear();

        if (Other.size() < 4)
        {
	        this.ExtractAllOf(Other);
	        return;
        }

        C2DPoint ptLeftMost = Other.get(0);
        int nLeftMost = 0;
    	
        // Find left most
        for (int i = 1 ; i < Other.size(); i++)
        {
	        C2DPoint pt = Other.get(i);
	        if (pt.x < ptLeftMost.x)
	        {
		        ptLeftMost = pt;
		        nLeftMost = i;
	        }
        }

        add(Other.ExtractAt(nLeftMost));

        Other.SortByAngleFromNorth( this.get(0));

        // Always add the left most and the first of the rest.
        add(Other.ExtractAt(0));

        // Add others if needed.
        int nIndx = 0;

        C2DPointSet Unused = new C2DPointSet();

        while (nIndx < Other.size())
        {
		        int nLast = size() - 1;
		        C2DLine LastLine = new C2DLine( this.get(nLast-1), this.get(nLast));

		        C2DVector Test = new C2DVector( this.get(nLast), Other.get(nIndx));

		        double dAng = Test.AngleFromNorth();

		        if (dAng < LastLine.vector.AngleFromNorth())
		        {
			        Unused.add( ExtractAt(nLast) );
		        }
		        else
		        {
			        add(Other.ExtractAt(nIndx));
		        }
        }

        Other.ExtractAllOf(Unused);	

    }

    /// <summary>
    /// Sorts by the angle from north relative to the origin given.
    /// </summary>
    /// <param name="Origin">The origin.</param>
    public void SortByAngleFromNorth(C2DPoint Origin)
    {
        AngleFromNorth Comparer = new AngleFromNorth();
        Comparer.Origin = Origin;
        Collections.sort(this, Comparer);
    }
    /// <summary>
    /// Sorts by the angle to the right of the line.
    /// </summary>
    /// <param name="Line">The Line.</param>
    public void SortByAngleToRight(C2DLine Line)
    {
        AngleToRight Comparer = new AngleToRight();
        Comparer.Line = Line;
        Collections.sort(this, Comparer);
    }

    /// <summary>
    /// Sorts by the angle to the left of the line.
    /// </summary>
    /// <param name="Line">The Line.</param>
    public void SortByAngleToLeft(C2DLine Line)
    {
        AngleToLeft Comparer = new AngleToLeft();
        Comparer.Line = Line;
        Collections.sort(this, Comparer);

    }
    /// <summary>
    /// Gets the bounding rectangle.
    /// </summary>
    /// <param name="Rect">Ouput. The Rect.</param>
    public void GetBoundingRect(C2DRect Rect)
    {
        if (size() == 0)
        {
	        Rect.Clear();
	        return;
        }
        else
        {
            Rect.Set(this.get(0));

	        for (int i = 1 ; i < size(); i++)
	        {
		        Rect.ExpandToInclude( this.get(i));
	        }
        }
    }
    /// <summary>
    /// Gets the minimum bounding circle.
    /// </summary>
    /// <param name="Circle">Ouput. The Circle.</param>
    public void GetBoundingCircle(C2DCircle Circle)
    {
        if (this.size() < 3)
        {
	        if (this.size() == 2)
	        {
		        Circle.SetMinimum(this.get(0), this.get(1) );
	        }
	        else if (this.size() == 1)
	        {
		        Circle.Set( this.get(0), 0);
	        }
	        else
	        {
             assert false : "Point set with no points. Cannot calculate bounding circle.";
	        }
	        return;
        }

        GeoInteger nIndx1 = new GeoInteger(0);
        GeoInteger nIndx2 = new GeoInteger(0);
        GeoInteger nIndx3 = new GeoInteger(0);
        GeoDouble dDist = new GeoDouble(0.0);

        // First get the points that are furthest away from each other.
        GetExtremePoints(nIndx1, nIndx2, dDist);
        // Set the circle to bound these.
        Circle.SetMinimum( this.get(nIndx1.value), this.get(nIndx2.value));
        // Set up a flag to show if we are circumscibed. (Once we are, we always will be).
        boolean bCircum = false;
        // Cycle through and if any points aren't in the circle, then set the circle to be circumscribed.
        for (int i = 0 ; i < size(); i++)
        {
	        if ( i != nIndx1.value && i != nIndx2.value)
	        {
		        if (!Circle.Contains(this.get(i)))
		        {
			        nIndx3.value = i;
			        Circle.SetCircumscribed(this.get(nIndx1.value), this.get(nIndx2.value), this.get(nIndx3.value)  );
			        bCircum = true;
			        // Break out and try again.
			        break;
		        }
	        }
        }

        // If we didn't succeed first time then go through again setting it to be circumscribed every time.
        if (bCircum)
        {
	        for ( int i = 0 ; i < size(); i++)
	        {
		        if ( i != nIndx1.value && i != nIndx2.value && i != nIndx3.value)
		        {
			        if (!Circle.Contains(  this.get(i) ))
			        {
				        double Dist1 = this.get(i).Distance(  this.get(nIndx1.value) );
				        double Dist2 = this.get(i).Distance(  this.get(nIndx2.value) );
                        double Dist3 = this.get(i).Distance( this.get(nIndx3.value));
				        if (Dist1 < Dist2 && Dist1 < Dist3)
				        {
					        // Closest to point 1 so elimitate this
					        nIndx1.value = i;
				        }
				        else if (Dist2 < Dist3)
				        {
					        // Closest to point 2 so elimitate this
					        nIndx2.value = i;
				        }
				        else
				        {
					        // Closest to point 3 so elimitate this
					        nIndx3.value = i;
				        }
				        Circle.SetCircumscribed(  this.get(nIndx1.value), this.get(nIndx2.value), this.get(nIndx3.value)  );
			        }
		        }
	        }
        }
    }
    /// <summary>
    /// Gets the points that are furthest apart as an estimate.
    /// </summary>
    /// <param name="nIndx1">Ouput. The first index.</param>
    /// <param name="nIndx2">Ouput. The second index.</param>
    /// <param name="dDist">Ouput. The distance between.</param>
    /// <param name="nStartEst">Input. The guess at one of the points.</param>
    public void GetExtremePointsEst(GeoInteger nIndx1, GeoInteger nIndx2, 
	GeoDouble dDist, int nStartEst) 
    {
        if (size() < 3)
        {
	        if (size() == 2)
	        {
		        nIndx1.value = 0;
		        nIndx2.value = 1;
	        }
	        else if (size() == 1)
	        {
		        nIndx1.value = 0;
		        nIndx2.value = 0;
	        }
	        else
	        {
		        nIndx1.value = 0;
		        nIndx2.value = 0;
            assert false : "Point set with no points. Cannot calculate extreme points.";
	        }

	        return;
        }

        // Index 1 is the provided starting guess (default to 0).
        nIndx1.value = nStartEst;
        // Index 2 is the furthest point from this.
        nIndx2.value = GetFurthestPoint(nIndx1.value, dDist);

        int nIndx3 = ~(int)0;

        while (true)
        {
	        nIndx3 = GetFurthestPoint(nIndx2.value, dDist);
	        if (nIndx3 == nIndx1.value)
	        {
		        return;
	        }
	        else
	        {
		        nIndx1.value = nIndx2.value;
		        nIndx2.value = nIndx3;
	        }
        }
    }
    /// <summary>
    /// Gets the points that are furthest apart.
    /// </summary>
    /// <param name="nIndx1">Ouput. The first index.</param>
    /// <param name="nIndx2">Ouput. The second index.</param>
    /// <param name="dDist">Ouput. The distance.</param>
    public void GetExtremePoints(GeoInteger nIndx1, GeoInteger nIndx2, 
	        GeoDouble dDist)
    {
        // First take a guess at them.
        GetExtremePointsEst(nIndx1, nIndx2, dDist, 0);

        // Set up a circle to bound the 2 guesses.
        C2DVector Vec = new C2DVector( this.get(nIndx1.value), this.get(nIndx2.value));
        Vec.Multiply( 0.5);
        C2DCircle Circle = new C2DCircle(C2DPoint.Add(this.get(nIndx1.value), new C2DPoint(Vec)), dDist.value / 2);

        // Now, if the guess was wrong, there must be a point outside the circle which is part of
        // the right solution. Go through all these, check and reset the result each time.
        for (int i = 0 ; i < size(); i++)
        {
	        if ( i != nIndx1.value && i != nIndx2.value)
	        {
		        if ( !Circle.Contains( this.get(i) ))
		        {
			        GeoDouble dDistCheck = new GeoDouble(0.0);
			        int nCheck1 = GetFurthestPoint(i,  dDistCheck);
			        if (dDistCheck.value > dDist.value)
			        {
				        nIndx1.value = i;
				        nIndx2.value = nCheck1;
				        dDist.value = dDistCheck.value;
			        }				
		        }
	        }
        }
    }
    /// <summary>
    /// Removes all repeated points.
    /// </summary>
    public void RemoveRepeatedPoints()
    {
        if (size() < 2)
	        return;

        int i = 0;
        while (i < size())
        {
	        int r = i + 1;
            while (r < size())
	        {
		        if ( this.get(i).PointEqualTo(this.get(r)))
		        {
			        this.remove(r);
		        }
		        else
		        {
			        r++;
		        }
	        }
	        i++;
        }
    }

    /// <summary>
    /// Returns the index of the furthest point from the point specified by the 
    /// index given.
    /// </summary>
    /// <param name="nIndex">Input. The index.</param>
    /// <param name="dDist">Ouput. The distance.</param>
    int GetFurthestPoint(int nIndex, GeoDouble dDist)
    {
        if (size() < 2 || nIndex >= size())
        {
	  //     Debug.Assert(false);
	        return 0;
        }
    	
        int usResult;

        if (nIndex == 0)
        {
	        dDist.value = this.get(1).Distance(this.get(nIndex));
	        usResult = 1;
        }
        else
        {
	        dDist.value = this.get(0).Distance(this.get(nIndex));
	        usResult = 0;
        }

        for (int i = 1 ; i < size(); i++)
        {
	        if (i != nIndex)
	        {
		        double dD = this.get(i).Distance(this.get(nIndex));
		        if (dD > dDist.value)
		        {
			        dDist.value = dD;
			        usResult = i;
		        }
	        }
        }

        return usResult;
    }
    /// <summary>
    /// Sorts by distance to the point.
    /// </summary>
    /// <param name="pt">Input. The point.</param>
    public void SortByDistance(C2DPoint pt)
    {
        SortByDistance Comparer = new SortByDistance();
        Comparer.Point = pt;
        Collections.sort(this, Comparer);
    }
    /// <summary>
    /// Sorts left to right.
    /// </summary>
    public void SortLeftToRight()
    {
        PointLeftToRight Comparer = new PointLeftToRight();
        Collections.sort(this, Comparer);
    }
    /// <summary>
    /// Sorts by index. The index set must have the same number of entries.
    /// The index set will also be sorted.
    /// </summary>
    /// <param name="Indexes">Input. The indexes.</param>
    public void SortByIndex(ArrayList<Integer> Indexes)
    {
        if (Indexes.size() == size())
        {
            SortByIndex(Indexes, 0, size() - 1);
        }
    }
    /// <summary>
    /// Quicksort index sorting.
    /// </summary>
    /// <param name="Indexes">Input. The indexes.</param>
    /// <param name="lo0">Input. The sort start.</param>
    /// <param name="hi0">Input. The sort end.</param>
    private void SortByIndex(ArrayList<Integer> Indexes, int lo0, int hi0)
    {
        int lo = lo0;
        int hi = hi0;
        if (lo >= hi) return;

        else if (lo == hi - 1)
        {
            // sort a two element list by swapping if necessary 
            if (Indexes.get(lo) > Indexes.get(hi))
            {
            	Integer T = Indexes.get(lo);
                Indexes.set(lo, Indexes.get(hi));
                Indexes.set(hi, T);             

                C2DPoint PAR = this.get(lo);
                this.set(lo, this.get(hi));
                this.set(hi, PAR);            
            }
            return;
        }

        //  Pick a pivot and move it out of the way

        Integer pivot = Indexes.get((lo + hi) / 2);
        Indexes.set((lo + hi) / 2, Indexes.get(hi));
        Indexes.set(hi, pivot);        

        C2DPoint ParPivot = this.get((lo + hi) / 2);
        this.set((lo + hi) / 2, this.get(hi));
        this.set(hi, ParPivot);   
 
        while (lo < hi)
        {
            //  Search forward from a[lo] until an element is found that
            //  is greater than the pivot or lo >= hi 

            while (Indexes.get(lo) <= pivot && lo < hi)
            {
                lo++;
            }
            // Search backward from a[hi] until element is found that
            //  is less than the pivot, or lo >= hi

            while (pivot <= Indexes.get(hi) && lo < hi)
            {
                hi--;
            }
            //  Swap elements a[lo] and a[hi]
            if (lo < hi)
            {
                int T = Indexes.get(lo);
                Indexes.set(lo, Indexes.get(hi));
                Indexes.set(hi, T); 

                C2DPoint PAR = this.get(lo);
                this.set(lo, this.get(hi));
                this.set(hi, PAR);    
            }
        }

        //  Put the median in the "center" of the list

        
        Indexes.set(hi0, Indexes.get(hi));
        Indexes.set(hi, pivot); 

        
        this.set(hi0, this.get(hi));
        this.set(hi, ParPivot); 

        //Recursive calls, elements a[lo0] to a[lo-1] are less than or
        //equal to pivot, elements a[hi+1] to a[hi0] are greater than
        //pivot.

        SortByIndex(Indexes, lo0, lo - 1);
        SortByIndex(Indexes,  hi + 1, hi0);
    }




	/// <summary>
	/// Sort helper.
	/// </summary>
	public class PointLeftToRight implements Comparator< C2DPoint>
	{
	    /// <summary>
	    /// Compare function.
	    /// </summary>
	    public int compare(C2DPoint A, C2DPoint B)
	    {
	        if (A == B)
	            return 0;
	        if (A.x > B.x)
	            return 1;
	        else if (A.x < B.x)
	            return -1;
	        else
	            return 0;
	    }
	}
	/// <summary>
	/// Sort helper.
	/// </summary>
	public class AngleFromNorth implements Comparator<C2DPoint>
	{
	    /// <summary>
	    /// Origin.
	    /// </summary>
	    public C2DPoint Origin;
	    /// <summary>
	    /// Compare function.
	    /// </summary>
	    public int compare(C2DPoint P1, C2DPoint P2)
	    {
	        if (P1 == P2)
	            return 0;
	
	        C2DVector Vec1 = new C2DVector(Origin, P1);
	        C2DVector Vec2 = new C2DVector(Origin, P2);
	
	        double dAng1 = Vec1.AngleFromNorth();
	        double dAng2 = Vec2.AngleFromNorth();
	
	        if (dAng1 > dAng2)
	            return 1;
	        else if (dAng1 < dAng2)
	            return -1;
	        else
	            return 0;
	    }
	}
	/// <summary>
	/// Sort helper.
	/// </summary>
	public class AngleToRight implements Comparator<C2DPoint>
	{
	    /// <summary>
	    /// Line.
	    /// </summary>
	    public C2DLine Line;
	
	    /// <summary>
	    /// Compare function.
	    /// </summary>
	    public int compare(C2DPoint P1, C2DPoint P2)
	    {
	        if (P1 == P2)
	            return 0;
	
	        C2DVector Vec1 = new C2DVector(Line.point, P1);
	        C2DVector Vec2 = new C2DVector(Line.point, P2);
	
	        double dAng1 = Line.vector.AngleToRight(Vec1);
	        double dAng2 = Line.vector.AngleToRight(Vec2);
	
	        if (dAng1 > dAng2)
	            return 1;
	        else if (dAng1 < dAng2)
	            return -1;
	        else
	            return 0;
	    }
	
	}
	/// <summary>
	/// Sort helper.
	/// </summary>
	public class AngleToLeft  implements Comparator<C2DPoint>
	{
	    /// <summary>
	    /// Line.
	    /// </summary>
	    public C2DLine Line;
	    /// <summary>
	    /// Compare function.
	    /// </summary>
	    public int compare(C2DPoint P1, C2DPoint P2)
	    {
	        if (P1 == P2)
	            return 0;
	
	        C2DVector Vec1 = new C2DVector(Line.point, P1);
	        C2DVector Vec2 = new C2DVector(Line.point, P2);
	
	        double dAng1 = Line.vector.AngleToLeft(Vec1);
	        double dAng2 = Line.vector.AngleToLeft(Vec2);
	
	        if (dAng1 > dAng2)
	            return 1;
	        else if (dAng1 < dAng2)
	            return -1;
	        else
	            return 0;
	    }
	
	}
	/// <summary>
	/// Sort helper.
	/// </summary>
	public class SortByDistance  implements Comparator<C2DPoint>
	{
	    /// <summary>
	    /// Point to calculate distance from.
	    /// </summary>
	    public C2DPoint Point;
	    /// <summary>
	    /// Compare function.
	    /// </summary>
	    public int compare(C2DPoint P1, C2DPoint P2)
	    {
	        if (P1 == P2)
	            return 0;
	
	        double d1 = P1.Distance(Point);
	        double d2 = P2.Distance(Point);
	
	        if (d1 > d2)
	            return 1;
	        else if (d1 < d2)
	            return -1;
	        else
	            return 0;
	    }
	}

}
