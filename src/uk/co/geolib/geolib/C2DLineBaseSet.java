package uk.co.geolib.geolib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/// <summary>
/// Set of abstract lines.
/// </summary>
public class C2DLineBaseSet extends ArrayList<C2DLineBase>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/// <summary>
    /// Constructor
    /// </summary>
    public C2DLineBaseSet() { }

    /// <summary>
    /// Makes a value copy of the other set.
    /// </summary>
    /// <param name="Other">The other set.</param>
    public void MakeValueCopy(ArrayList<C2DLineBase> Other)
    {
        this.clear();
        for (int i = 0; i < Other.size(); i++)
        {
            this.add(Other.get(i).CreateCopy());
        }
    }

    /// <summary>
    /// Makes a refenence copy of the other set.
    /// </summary>
    /// <param name="Other">The other set.</param>
    public void MakeRefCopy(ArrayList<C2DLineBase> Other)
    {
        this.clear();
        for (int i = 0; i < Other.size(); i++)
        {
            this.add(Other.get(i));
        }
    }

    /// <summary>
    /// Adds a copy of the item.
    /// </summary>
    /// <param name="NewItem">The line as a line base.</param>
    public void AddCopy(C2DLineBase NewItem)
    {
        if (NewItem instanceof C2DLine)
        {
            this.add(new C2DLine((C2DLine)NewItem  ));
        }
        else if (NewItem instanceof C2DArc)
        {
            this.add(new C2DArc((C2DArc)NewItem));
        }
    }

    /// <summary>
    /// Extracts all of the other set.
    /// </summary>
    /// <param name="S2">The other set.</param>
    public void ExtractAllOf(ArrayList<C2DLineBase> S2)
    {
        for (int i = 0; i < S2.size(); i++)
        {
            add(S2.get(i));
        }
        S2.clear();
    }

    /// <summary>
    /// Extracts at the index given.
    /// </summary>
    /// <param name="nIndex">The index.</param>
    public C2DLineBase ExtractAt(int nIndex)
    {
        C2DLineBase Result = this.get(nIndex);
        this.remove(nIndex);
        return Result;
    }

    /// <summary>
    /// Class to hold a line reference and bounding rect for line intersection algorithm.
    /// </summary>
    public class CLineBaseRect
    {
        /// <summary>
        /// Line reference
        /// </summary>
        public C2DLineBase Line = null;
        /// <summary>
        /// Line bounding rect
        /// </summary>
        public C2DRect Rect = new C2DRect();
        /// <summary>
        /// Line index
        /// </summary>
        public int usIndex = 0;
        /// <summary>
        /// Set flag
        /// </summary>
        public boolean bSetFlag = true;
    };

    /// <summary>
    /// Returns the intersections within the set. Each intersection has and
    /// associated point an 2 indexes corresponding to the lines that
    /// created the intersection.
    /// </summary>
    /// <param name="pPoints">Output. The point set.</param>
    /// <param name="pIndexes1">Output. The indexes.</param>
    /// <param name="pIndexes2">Output. The indexes.</param>
    public void GetIntersections( ArrayList<C2DPoint> pPoints,  ArrayList<Integer> pIndexes1,
                         ArrayList<Integer> pIndexes2)
    {
        ArrayList<CLineBaseRect> Lines = new ArrayList<CLineBaseRect>();

        for (int i = 0 ; i < size() ; i++)
        {
	        CLineBaseRect LineRect = new CLineBaseRect();
            LineRect.Line = this.get(i);
            LineRect.Line.GetBoundingRect( LineRect.Rect);
            LineRect.usIndex = i;
	        Lines.add(LineRect);
        }

        CLineBaseRectLeftToRight Comparitor = new CLineBaseRectLeftToRight();
        Collections.sort(Lines, Comparitor);

        int j = 0;
        ArrayList<C2DPoint> IntPt = new ArrayList<C2DPoint>();
        // For each line...
        while (j < Lines.size())
        {
	        int r = j + 1;

	        double dXLimit = Lines.get(j).Rect.GetRight();
	        // ...search forward untill the end or a line whose rect starts after this ends
	        while (r < Lines.size() && Lines.get(r).Rect.GetLeft() < dXLimit)
	        {
    			
		        if ( Lines.get(j).Rect.Overlaps(  Lines.get(r).Rect) &&
			        Lines.get(j).Line.Crosses(  Lines.get(r).Line,  IntPt ))
		        {
			        while (IntPt.size() > 0)
			        {
				        pPoints.add( IntPt.get(IntPt.size() - 1) );
                        IntPt.remove(IntPt.size() - 1);

				        pIndexes1.add( Lines.get(j).usIndex );
				        pIndexes2.add( Lines.get(r).usIndex );
			        }
		        }
		        r++;
	        }	
	        j++;
        }
    }


    /// <summary>
    /// Returns the intersections with this set and the other. 
    /// Each intersection has an associated point and 2 indexes 
    /// corresponding to the lines that created the intersection.
    /// </summary>
    /// <param name="Other">Input. The other line set.</param>
    /// <param name="pPoints">Output. The intersection points.</param>
    /// <param name="pIndexesThis">Output. The indexes for this.</param>
    /// <param name="pIndexesOther">Output. The indexes for the other set.</param>
    /// <param name="pBoundingRectThis">Input. The bounding rect for this.</param>
    /// <param name="pBoundingRectOther">Input. The bounding rect for the other.</param>
    public void GetIntersections(ArrayList<C2DLineBase> Other,  ArrayList<C2DPoint> pPoints,
         ArrayList<Integer> pIndexesThis,  ArrayList<Integer> pIndexesOther,
        C2DRect pBoundingRectThis, C2DRect pBoundingRectOther)
    {

        ArrayList<CLineBaseRect> Lines = new ArrayList<CLineBaseRect>();

        for (int i = 0 ; i <  size() ; i++)
        {
	        CLineBaseRect LineRect = new CLineBaseRect();
            LineRect.Line = this.get(i);
            LineRect.Line.GetBoundingRect( LineRect.Rect);
            LineRect.usIndex = i;
            LineRect.bSetFlag = true;

	        if ( pBoundingRectOther.Overlaps( LineRect.Rect))
	        {
		        Lines.add(LineRect);
	        }
        }

        for (int d = 0 ; d <  Other.size() ; d++)
        {
	        CLineBaseRect LineRect = new CLineBaseRect();
	        LineRect.Line = Other.get(d);
	        LineRect.Line.GetBoundingRect( LineRect.Rect);
	        LineRect.usIndex = d;
	        LineRect.bSetFlag = false;

	        if ( pBoundingRectThis.Overlaps( LineRect.Rect))
	        {
		        Lines.add(LineRect);
	        }
        }

        CLineBaseRectLeftToRight Comparitor = new CLineBaseRectLeftToRight();
        Collections.sort(Lines, Comparitor);

        int j = 0;
        ArrayList<C2DPoint> IntPt = new ArrayList<C2DPoint>();
        while (j < Lines.size())
        {
	        int r = j + 1;

	        double dXLimit = Lines.get(j).Rect.GetRight();

	        while (r < Lines.size() && 
		           Lines.get(r).Rect.GetLeft() < dXLimit)
	        {
    			
		        if (  ( Lines.get(j).bSetFlag ^ Lines.get(r).bSetFlag  ) &&				
				        Lines.get(j).Rect.Overlaps(  Lines.get(r).Rect) &&
				        Lines.get(j).Line.Crosses(  Lines.get(r).Line,  IntPt) )
		        {
			        while (IntPt.size() > 0)
			        {
				        pPoints.add( IntPt.get( IntPt.size() - 1));
                        IntPt.remove( IntPt.size() - 1);


				        if (Lines.get(j).bSetFlag)
					        pIndexesThis.add( Lines.get(j).usIndex );
				        else
					        pIndexesThis.add( Lines.get(r).usIndex );

				        if (Lines.get(j).bSetFlag)
					        pIndexesOther.add( Lines.get(r).usIndex );
				        else
					        pIndexesOther.add( Lines.get(j).usIndex );

			        }
		        }
		        r++;
	        }	
	        j++;
        }

    }

    /// <summary>
    /// True if there are crossing lines within the set.
    /// </summary>
    public boolean HasCrossingLines()
    {

        // Set up an array of these structures and the left most points of the line rects
        ArrayList<CLineBaseRect> Lines = new ArrayList<CLineBaseRect>();


        for (int i = 0 ; i <  size() ; i++)
        {
	        CLineBaseRect LineRect = new CLineBaseRect();
            LineRect.Line = this.get(i);
            LineRect.Line.GetBoundingRect( LineRect.Rect);
	        Lines.add(LineRect);
        }

        CLineBaseRectLeftToRight Comparitor = new CLineBaseRectLeftToRight();
        Collections.sort(Lines, Comparitor);

        int j = 0;
        ArrayList<C2DPoint> IntPt = new ArrayList<C2DPoint>();
        boolean bIntersect = false;
        // For each line...
        while (j < Lines.size() && !bIntersect)
        {
	        int r = j + 1;

	        double dXLimit = Lines.get(j).Rect.GetRight();
	        // ...search forward untill the end or a line whose rect starts after this ends
	        while ( !bIntersect && r < Lines.size() && Lines.get(r).Rect.GetLeft() < dXLimit )
	        {
		        if ( Lines.get(j).Rect.Overlaps(  Lines.get(r).Rect) &&
			        Lines.get(j).Line.Crosses(  Lines.get(r).Line,  IntPt) )
		        {
			        bIntersect = true;
		        }
		        r++;
	        }	
	        j++;
        }

        return bIntersect;

    }

    /// <summary>
    /// Checks for closure i.e. it forms a closed shape.
    /// </summary>
    /// <param name="bEndsOnly">Input. True to only check the ends of the array.</param>
    public boolean IsClosed(boolean bEndsOnly)
    {
        int usSize = size();
    	
        if (bEndsOnly)
        {
	        if (this.get(0).GetPointFrom().PointEqualTo(  this.get(usSize - 1).GetPointTo())  )
		        return true;
	        else
		        return false;
        }
        else
        {
	        for (int i = 0; i < usSize; i++)
	        {
		        int usNext = (i + 1) % usSize;

		        if (!this.get(i).GetPointTo().PointEqualTo(  this.get(usNext).GetPointFrom() ))
			        return false;
	        }

	        return true;
        }
    }

    /// <summary>
    /// Adds the other to this if there is a common end i.e. they can be joined up.
    /// </summary>
    /// <param name="Other">Input. The other set.</param>
    public boolean AddIfCommonEnd( C2DLineBaseSet Other)
    {
     assert !IsClosed(true);
     assert !Other.IsClosed(true);

        int nThisCount = size();
        if (nThisCount < 1) 
            return false;

        int nOtherCount = Other.size();
        if (nOtherCount < 1) 
            return false;

        if (this.get(0).GetPointFrom().PointEqualTo(  Other.get(0).GetPointFrom())  )
        {
            ReverseDirection();

            this.ExtractAllOf(Other);

            return true;
        }
        else if (this.get(0).GetPointFrom().PointEqualTo(Other.get(nOtherCount - 1).GetPointTo()) )
        {
            ReverseDirection();

            Other.ReverseDirection();

            this.ExtractAllOf(Other);

            return true;
        }
        else if (this.get(nThisCount - 1).GetPointTo().PointEqualTo(  Other.get(0).GetPointFrom()))
        {
            this.ExtractAllOf(Other);

            return true;
        }
        else if (this.get(nThisCount - 1).GetPointTo().PointEqualTo( Other.get(nOtherCount - 1).GetPointTo()) )
        {
            Other.ReverseDirection();

            this.ExtractAllOf(Other);

            return true;
        }

        return false;

    }

    /// <summary>
    /// Removes lines that are small, based on the tolerance. 
    /// </summary>
    /// <param name="dTolerance">Input. The length defined to be null.</param>
    public void Remove0Lines(double dTolerance)
    {
        for (int i = 0 ; i < size(); i++)
        {
	        double dLength = this.get(i).GetLength();

	        if (dLength < dTolerance)
	        {
		        remove(i);
	        }
        }
    }
    /// <summary>
    /// Reverses the direction. 
    /// </summary>
    public void ReverseDirection()
    {
        Collections.reverse(this);

        for (int i = 0; i < size() ; i++)
        {
	        this.get(i).ReverseDirection();
        }	
    }

    /// <summary>
    /// Class to help with sorting. 
    /// </summary>
    public class CLineBaseRectLeftToRight implements Comparator<CLineBaseRect>
    {
        /// <summary>
        /// Compare function. 
        /// </summary>
        public int compare(CLineBaseRect L1, CLineBaseRect L2)
        {
            if (L1 == L2)
                return 0;
            if (L1.Rect.getTopLeft().x > L2.Rect.getTopLeft().x)
                return 1;
            else if (L1.Rect.getTopLeft().x == L2.Rect.getTopLeft().x)
                return 0;
            else
                return -1;
        }
    }	

}
