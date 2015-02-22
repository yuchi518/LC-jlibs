package uk.co.geolib.geolib;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class CTravellingSalesman extends LinkedList<C2DPoint>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/// <summary>
    /// Constructor
    /// </summary>
    public CTravellingSalesman() {}


    /// <summary>
    /// Allocates points from a set by removing them from the set
    /// </summary>
    public void SetPointsDirect(ArrayList<C2DPoint> Points)
    {
        clear();

        for (int i = 0; i < Points.size(); i++)
        {
            this.addLast(Points.get(i));
        }
        Points.clear();
    }

    /// <summary>
    /// Extracts the points into the set
    /// </summary>
    public void ExtractPoints(ArrayList<C2DPoint> Points)
    {
        Points.addAll(this);
    }

    /// <summary>
    /// Inserts a point optimally into this 
    /// </summary>
    public void InsertOptimallyWithIterator(C2DPoint pt)
    {
        // Special cases if there are less than 3 points
        if (size() < 2)
        {
	        if (size() == 1)
		        this.addLast(pt);
	        return;
        }

        // a pointer to a point and the point after it
        C2DPoint ptH1;
        C2DPoint ptH2;
        // Set up an iterator and the 2 points.

        ptH1 = get(0);
        ptH2 = get(1);
        
        ListIterator<C2DPoint> IterInsert = this.listIterator();
        IterInsert.next();
        ListIterator<C2DPoint> Iter = this.listIterator();
        Iter.next();      

        // Find the assumed minimum distance expansion. i.e. if we insert the point
        // between the first and second points what is the increase in the route.
        double dMinExp = ptH1.Distance(pt) + pt.Distance(ptH2) - ptH1.Distance(ptH2);

        // Now go through all the other positions and do the same test, recording the
        // optimal position.
       
        while (Iter.hasNext())
        {
	        ptH1 = ptH2;
	        ptH2 = Iter.next();

	        double dExp = ptH1.Distance(pt) + pt.Distance(ptH2) - ptH1.Distance(ptH2);
	        if (dExp < dMinExp)
	        {
		        dMinExp = dExp;
		        IterInsert = Iter;
	        }
        }
        // Finally just insert it in the list at the best place.
        this.add(IterInsert.previousIndex(), pt);
    }
    
    /// <summary>
    /// Inserts a point optimally into this 
    /// </summary>
    public void InsertOptimally(C2DPoint pt)
    {
        // Special cases if there are less than 3 points
        if (size() < 2)
        {
	        if (size() == 1)
		        this.addLast(pt);
	        return;
        }

        // a pointer to a point and the point after it
        C2DPoint ptH1 = get(0);
        C2DPoint ptH2 = get(1);
        // Set up an iterator and the 2 points.

        int IterInsert = 1;

     

        // Find the assumed minimum distance expansion. i.e. if we insert the point
        // between the first and second points what is the increase in the route.
        double dMinExp = ptH1.Distance(pt) + pt.Distance(ptH2) - ptH1.Distance(ptH2);

        // Now go through all the other positions and do the same test, recording the
        // optimal position.
        int Iter = 2;
        while (Iter < size())
        {
	        ptH1 = ptH2;
	        ptH2 = get(Iter);

	        double dExp = ptH1.Distance(pt) + pt.Distance(ptH2) - ptH1.Distance(ptH2);
	        if (dExp < dMinExp)
	        {
		        dMinExp = dExp;
		        IterInsert = Iter;
	        }
	        Iter++;
        }
        // Finally just insert it in the list at the best place.
        this.add(IterInsert, pt);
    }
    

    /// <summary>
    /// Refines the set, trying to find optimal positions for the points
    /// </summary>
    public void Refine()
    {
        // CHECK FOR LESS THAN 2 ITEMS.
        if (size() < 2)
	        return;

        boolean bRepeat = true;

        int nIt = 0;

        while (bRepeat && nIt < conMaxIterations)
        {
	        nIt++;
	        int IterRemove = 1;

	        bRepeat = false;
	        while (IterRemove < size() - 1)
	        {

	        	int IterInsert = IterRemove - 1;


                int nCountBack = 1;
		        boolean bFound = false;
		        int IterRemoveBefore;
                int IterRemoveAfter;
                int IterInsertBefore;

                while (nCountBack < conRefineProximity && IterInsert != 0)
		        {
			        IterRemoveBefore = IterRemove - 1;
			        IterRemoveAfter = IterRemove + 1;
			        IterInsertBefore = IterInsert - 1;


			        double dCurrentPerimPart = get(IterRemoveBefore).Distance(get(IterRemove)) + 
			        get(IterRemove).Distance(get(IterRemoveAfter)) +
			        		get(IterInsertBefore).Distance(get(IterInsert));
			        double dNewPerimPart = get(IterRemoveBefore).Distance(get(IterRemoveAfter)) +
			        		get(IterInsertBefore).Distance(get(IterRemove)) +
			        				get(IterRemove).Distance(get(IterInsert));
			        if (dNewPerimPart < dCurrentPerimPart)
			        {
				        C2DPoint ptRemove = get(IterRemove);
                        this.remove(IterRemove);
                        this.add(IterInsert, ptRemove);

				        bFound = true;
				        IterRemove = IterRemoveAfter; // WE HAVE GONE BACK SO PUT THE REMOVAL POINT BACK HERE AND SEARCH AGAIN.
				        break;
			        }
                    IterInsert = IterInsert - 1;
			        nCountBack++;
		        }
    			
		        // Now go forward along the list untill we find a better place. Only go so far.
		        int nCountForward = 2;
		        IterInsert = IterRemove; // The item considered for a new place.
                IterInsert = IterInsert + 1;
		        if (IterInsert != size())
                    IterInsert = IterInsert + 1; // Go forward 2 to avoid taking it out and putting it back in the same place.
		        else
			        nCountForward = conRefineProximity; // get out

		        while (!bFound && nCountForward < conRefineProximity && IterInsert < size() )
		        {
			        IterRemoveBefore = IterRemove;
                    IterRemoveBefore = IterRemoveBefore - 1; // This is the point before the removal
			        IterRemoveAfter = IterRemove;
                    IterRemoveAfter = IterRemoveAfter + 1; // This is the point after the removal.
			        IterInsertBefore = IterInsert;
                    IterInsertBefore = IterInsertBefore -1;// This is the point before the potential insertion point.


			        double dCurrentPerimPart = get(IterRemoveBefore).Distance(get(IterRemove)) + 
			        		get(IterRemove).Distance(get(IterRemoveAfter)) +
			        				get(IterInsertBefore).Distance(get(IterInsert));
			        double dNewPerimPart = get(IterRemoveBefore).Distance(get(IterRemoveAfter)) +
			        		get(IterInsertBefore).Distance(get(IterRemove)) +
			        				get(IterRemove).Distance(get(IterInsert));
			        if (dNewPerimPart  < dCurrentPerimPart)
			        {
				        C2DPoint ptRemove = get(IterRemove);
                        this.remove( IterRemove );
                        this.add(  IterInsert, ptRemove );

				        IterRemove = IterRemoveAfter; 

				        bFound = true; 
				        break;
			        }
                    IterInsert = IterInsert + 1;
			        nCountForward++;
		        }
    			
		        if (bFound) bRepeat = true;

		        if (!bFound)
			        IterRemove = IterRemove + 1;
	        }	// while Remove.
        } // while bRepeat.

    }

    /// <summary>
    /// Refines the set, trying to find optimal positions for the points
    /// </summary>
    public void Refine2()
    {
        int nSize = size();
        if (nSize < 4)
	        return;
        
        

        int Iter = 0;
        int Iter1;
        int Iter2;
        int Iter3;

        int nIndex = 0;
        int nIndexLimit = nSize - 3;

        boolean bRepeat = true;

        int nIt = 0;

        while (bRepeat && nIt < conMaxIterations)
        {
	        nIt++;
	        bRepeat = false;
	        while (nIndex < nIndexLimit)
	        {
		        Iter1 = Iter;
                Iter1 = Iter1 + 1;
		        Iter2 = Iter;	
		        Iter2 = Iter2 + 1;
                Iter2 = Iter2 + 1;
		        Iter3 = Iter;
                Iter3 = Iter3 + 1;
		        Iter3 = Iter3 + 1;
		        Iter3 = Iter3 + 1;

		        double dCurrentPerimPart = get(Iter).Distance(get(Iter1)) + 
		        		get(Iter1).Distance(get(Iter2)) +
		        				get(Iter2).Distance(get(Iter3));

		        double dNewPerimPart = get(Iter).Distance(get(Iter2)) +
		        		get(Iter2).Distance(get(Iter1)) +
		        				get(Iter1).Distance(get(Iter3));

		        if (dCurrentPerimPart > dNewPerimPart)
		        {
			        C2DPoint pRem = get(Iter2);
                    this.remove(Iter2);
                    this.add(Iter1, pRem);

			        bRepeat = true;
		        }


		        Iter = Iter + 1;
		        nIndex++;
	        }	

        }
    }

    /// <summary>
    /// Brute force optimisation
    /// </summary>
    public void SimpleReorder()
    {
        // CHECK FOR LESS THAN 2 ITEMS.
        if (size() < 2)
	        return;

        int IterRemove = 0;
        IterRemove = IterRemove + 1;
        int IterRemoveLimit = size() - 1;

        while (IterRemove != IterRemoveLimit)
        {
            int IterInsert = 0;
            IterInsert = IterInsert + 1;

	        while (IterInsert != size())
	        {
		        if (IterInsert == IterRemove)
		        {
			        IterInsert  = IterInsert + 1;
			        continue; // No point removing it and putting back in the same place.
		        }

                int IterRemoveBefore = IterRemove;
                IterRemoveBefore = IterRemoveBefore - 1;
                int IterRemoveAfter = IterRemove;
                IterRemoveAfter = IterRemoveAfter + 1;
                int IterInsertBefore = IterInsert;
                IterInsertBefore = IterInsertBefore - 1;

                double dCurrentPerimPart = get(IterRemoveBefore).Distance(get(IterRemove)) +
                		get(IterRemove).Distance(get(IterRemoveAfter)) +
                				get(IterInsertBefore).Distance(get(IterInsert));
                double dNewPerimPart = get(IterRemoveBefore).Distance(get(IterRemoveAfter)) +
                		get(IterInsertBefore).Distance(get(IterRemove)) +
                				get(IterRemove).Distance(get(IterInsert));
		        if (dNewPerimPart < dCurrentPerimPart)
		        {
                    C2DPoint ptRemove = get(IterRemove);
                    this.remove(IterRemove);
                    this.add(IterInsert, ptRemove);

			        break;
		        }
                IterInsert = IterInsert + 1;

	        }

            IterRemove = IterRemove + 1;
        }


    }

    /// <summary>
    /// Optimises the position of the points
    /// </summary>
    public void Optimize()
    {
        if (size() < 4)
            return;

        // Take out the start.
        C2DPoint pStart = get(0);
        this.removeFirst();

        // Take out the end.
        C2DPoint pEnd = get(size() - 1);
        this.removeLast();

        // Take all the rest out.
        C2DPointSet Points = new C2DPointSet();
        this.ExtractPoints(Points);

        // Put the ends back in.
        this.addFirst(pStart);
        this.addLast(pEnd);

        // Sort the rest by approx distance from the line in reverse order so we can get them off the end.
        Points.SortByDistance(pStart.GetMidPoint(pEnd));
        Collections.reverse(Points);
 
        // Add them all in the most sensible place (not gauranteed).
        while (Points.size() > 0)
        {
            this.InsertOptimally(Points.get(Points.size() - 1));
            Points.remove(Points.size() - 1);
        }

    }

    private static int conRefineProximity = 10;
    private static int conMaxIterations = 5;

}
