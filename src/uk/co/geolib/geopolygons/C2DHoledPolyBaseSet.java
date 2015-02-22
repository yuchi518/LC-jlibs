
package uk.co.geolib.geopolygons;

import uk.co.geolib.geolib.*;

import java.util.ArrayList;

public class C2DHoledPolyBaseSet extends ArrayList<C2DHoledPolyBase>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/// <summary>
    /// Constructor.
    /// </summary>
    public C2DHoledPolyBaseSet() { }

    /// <summary>
    /// Extracts all from the set provided.
    /// </summary>
    /// <param name="Polys">The set to extract from converting to holed polygns.</param>
    public void ExtractAllOf(ArrayList<C2DPolyBase> Polys)
    {
        for (int i = 0; i < Polys.size(); i++)
        {
            C2DHoledPolyBase NewPoly = new C2DHoledPolyBase();
            NewPoly.Rim = Polys.get(i);
            add(NewPoly);
        }

        Polys.clear();
    }
    /// <summary>
    /// Extracts all from the set provided.
    /// </summary>
    /// <param name="Polys">The set to extract from.</param>
 //   public void ExtractAllOf(ArrayList<C2DHoledPolyBase> Polys)
  //  {
 //       this.addAll(Polys);
 //       Polys.clear();
//    }
    /// <summary>
    /// Basic multiple unification.
    /// </summary>
    public void UnifyBasic()
    {
        C2DHoledPolyBaseSet TempSet = new C2DHoledPolyBaseSet();
        C2DHoledPolyBaseSet UnionSet = new C2DHoledPolyBaseSet();

        while (size() > 0)
        {
	        C2DHoledPolyBase pLast = this.get(size() - 1);
            this.remove(size() - 1);

	        boolean bIntersect = false;
	        int i = 0;

            while (i < size() && !bIntersect)
	        {
                CGrid grid = new CGrid();
                this.get(i).GetUnion(pLast, UnionSet, grid);

		        if (UnionSet.size() == 1)
		        {
			        this.set(i, UnionSet.get(0));
			        bIntersect = true;
		        }
		        else
		        {
                    //Debug.Assert(UnionSet.size() == 0);
			        UnionSet.clear();
			        i++;
		        }
	        }

	        if (!bIntersect)
	        {
		        TempSet.add(pLast);
	        }
        }

        this.addAll( TempSet);
    }
    /// <summary>
    /// Unification by growing shapes of fairly equal size (fastest for large groups).
    /// </summary>
    /// <param name="grid">The CGrid with the degenerate settings.</param>
    public void UnifyProgressive(CGrid grid)
    {
        // Record the degenerate handling so we can reset.
        CGrid.eDegenerateHandling DegenerateHandling = grid.DegenerateHandling;
        switch( grid.DegenerateHandling )
        {
        case RandomPerturbation:
	        for (int i = 0 ; i < size() ; i++)
	        {
		        this.get(i).RandomPerturb();
	        }
                grid.DegenerateHandling = CGrid.eDegenerateHandling.None;
	        break;
        case DynamicGrid:

	        break;
        case PreDefinedGrid:
	        for (int i = 0 ; i < size() ; i++)
	        {
		        this.get(i).SnapToGrid(grid);
	        }
	        grid.DegenerateHandling = CGrid.eDegenerateHandling.PreDefinedGridPreSnapped;
	        break;
        case PreDefinedGridPreSnapped:

	        break;
        }


        C2DHoledPolyBaseSet NoUnionSet = new C2DHoledPolyBaseSet();
        C2DHoledPolyBaseSet PossUnionSet = new C2DHoledPolyBaseSet();
        C2DHoledPolyBaseSet SizeHoldSet = new C2DHoledPolyBaseSet();
        C2DHoledPolyBaseSet UnionSet = new C2DHoledPolyBaseSet();
        C2DHoledPolyBaseSet TempSet = new C2DHoledPolyBaseSet();

        int nThreshold = GetMinLineCount();

        if (nThreshold == 0)
	        nThreshold = 10;	// avoid infinate loop.
    	
        // Assumed all are size held to start
        SizeHoldSet.addAll(this);
        this.clear();

        do
        {
	        // double the threshold
	        nThreshold *= 3;

	        // Put all the possible intersects back in this.
	        this.addAll(PossUnionSet);
            PossUnionSet.clear();

	        // Put all the size held that are small enough back (or in to start with)
	        while (SizeHoldSet.size() > 0)
	        {
		        C2DHoledPolyBase pLast = SizeHoldSet.get(SizeHoldSet.size() - 1);
                SizeHoldSet.remove(SizeHoldSet.size() - 1);

		        if (pLast.GetLineCount() > nThreshold)
		        {
			        TempSet.add(pLast);
		        }
		        else
		        {
			        this.add(pLast);
		        }
	        }
	        SizeHoldSet.addAll( TempSet);
            TempSet.clear();


	        // Cycle through all popping the last and finding a union
	        while (size() > 0)
	        {
		        C2DHoledPolyBase pLast = this.get(size()-1);
    		    this.remove(size()-1);

		        boolean bIntersect = false;

		        int i = 0;
		        while ( i < size() && !bIntersect )
		        {
			        this.get(i).GetUnion( pLast, UnionSet, grid);

			        if (UnionSet.size() == 1)
			        {
				        C2DHoledPolyBase pUnion = UnionSet.get(UnionSet.size() - 1);
                        UnionSet.remove(UnionSet.size() - 1);

				        if (pUnion.GetLineCount() > nThreshold)
				        {
					        remove(i);
					        SizeHoldSet.add(pUnion);
				        }
				        else
				        {
					        this.set(i, pUnion);
					        i++;
				        }

				        bIntersect = true;
			        }
			        else
			        {
                        if (UnionSet.size() != 0)
                        {
                            grid.LogDegenerateError();
                        }
				        UnionSet.clear();
				        i++;
			        }
		        }

		        if (!bIntersect)
		        {
			        boolean bPosInterSect = false;
			        for (int j = 0 ; j <  SizeHoldSet.size(); j ++)
			        {
				        if (pLast.Rim.BoundingRect.Overlaps( 
							        SizeHoldSet.get(j).Rim.BoundingRect))
				        {
					        bPosInterSect = true;	
					        break;
				        }
			        }

			        if (bPosInterSect)
			        {
				        PossUnionSet.add( pLast);
			        }
			        else
			        {
				        NoUnionSet.add(pLast);
			        }
		        }
	        }
        }
        while (SizeHoldSet.size() != 0);


        this.addAll( NoUnionSet);
        NoUnionSet.clear();

        grid.DegenerateHandling = DegenerateHandling;
    }
    /// <summary>
    /// Adds a new polygon and looks for a possible unification.
    /// Assumes current set is distinct.
    /// </summary>
    /// <param name="pPoly">The polygon to add and possible unify.</param>
    public void AddAndUnify(C2DHoledPolyBase pPoly)
    {
        if (!AddIfUnify(pPoly))
            add(pPoly);
    }
    /// <summary>
    /// Adds a new polygon set and looks for a possible unifications.
    /// Assumes both sets are distinct.
    /// </summary>
    /// <param name="pOther">The polygon set to add and possible unify.</param>
    public void AddAndUnify(C2DHoledPolyBaseSet pOther)
    {
        C2DHoledPolyBaseSet TempSet = new C2DHoledPolyBaseSet();

        while (pOther.size() > 0)
        {
            C2DHoledPolyBase pLast = pOther.get(pOther.size() - 1);
            pOther.remove(pOther.size() - 1);

            if (!AddIfUnify(pLast))
                TempSet.add(pLast);
        }

        this.addAll(TempSet) ;
    }
    /// <summary>
    /// Adds a new polygon ONLY if there is a unifications.
    /// Assumes current set is distinct.
    /// </summary>
    /// <param name="pPoly">The polygon to add if there is a union.</param>
    public boolean AddIfUnify(C2DHoledPolyBase pPoly)
    {
        C2DHoledPolyBaseSet TempSet = new C2DHoledPolyBaseSet();
        C2DHoledPolyBaseSet UnionSet = new C2DHoledPolyBaseSet();
        CGrid grid = new CGrid();
        while (size() > 0 && pPoly != null)
        {
            C2DHoledPolyBase pLast = this.get(size()-1);
            this.remove(size() - 1);

            pLast.GetUnion(pPoly, UnionSet, grid);

            if (UnionSet.size() == 1)
            {
                pPoly = null;
                pLast = null;

                AddAndUnify(UnionSet.get(0));
                UnionSet.clear();
            }
            else
            {
                //Debug.Assert(UnionSet.size() == 0);
                UnionSet.clear();
                TempSet.add(pLast);
            }
        }

        this.addAll( TempSet);
        TempSet.clear();
        
        return (pPoly == null);

    }

    /// <summary>
    /// Adds a set of holes to the current set of polygons as holes within them.
    /// </summary>
    /// <param name="pOther">The polygon set to add as holes.</param>
    public void AddKnownHoles(ArrayList<C2DPolyBase> pOther)
    {
        if (size() != 0)
        {
	        while (pOther.size() > 0)
	        {
		        C2DPolyBase pLast = pOther.get(pOther.size() - 1);
                pOther.remove(pOther.size() - 1);
		        if (pLast.Lines.size() > 0)
		        {
                    int i = size() - 1;
			        boolean bFound = false;
			        while ( i > 0 && !bFound)
			        {
				        if ( this.get(i).Contains( pLast.Lines.get(0).GetPointFrom()))
				        {
                            this.get(i).AddHole(pLast);
					        bFound = true;
				        }
				        i--;
			        }

			        if (!bFound)
			        {
                        this.get(0).AddHole(pLast);
			        }
		        }
	        }
        }
    }

    /// <summary>
    /// Total line count for all polygons contained.
    /// </summary>
    public int GetLineCount()
    {
        int nResult = 0 ;

        for (int n = 0; n < size() ; n ++)
        {
	        nResult += this.get(n).GetLineCount();
        }

        return nResult;
    }

    /// <summary>
    /// Minimum line count of all polygons contained.
    /// </summary>
    public int GetMinLineCount()
    {
        int nMin = ~(int)0;

        for(int i = 0 ; i < size(); i++)
        {
	        int nCount = this.get(i).GetLineCount();
	        if( nCount < nMin)
	        {
		        nMin = nCount;
	        }
        }
        return nMin;
    }

    /// <summary>
    /// Transformation.
    /// </summary>
    public void Transform(CTransformation pProject)
    {
        for (int i = 0 ; i <  this.size(); i++)
        {
	        this.get(i).Transform(pProject);
        }
    }
    /// <summary>
    /// Transformation.
    /// </summary>
    public void InverseTransform(CTransformation pProject)
    {
        for ( int i = 0 ; i < this.size(); i++)
        {
	        this.get(i).InverseTransform(pProject);
        }
    }

}
