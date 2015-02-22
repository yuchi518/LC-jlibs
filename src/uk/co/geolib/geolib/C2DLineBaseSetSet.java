package uk.co.geolib.geolib;

import java.util.ArrayList;

public class C2DLineBaseSetSet extends ArrayList<C2DLineBaseSet>{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/// <summary>
    /// Constructor
    /// </summary>
    public C2DLineBaseSetSet() { }


    /// <summary>
    /// Extracts all of the line sets frm the other.
    /// </summary>
    /// <param name="S2">The other set.</param>
    public void ExtractAllOf(ArrayList<C2DLineBaseSet> S2)
    {
        for (int i = 0; i < S2.size(); i++)
        {
            add(S2.get(i));
        }
        S2.clear();
    }

    /// <summary>
    /// Extracts at the index supplied.
    /// </summary>
    /// <param name="nIndex">The index to extract at.</param>
    public C2DLineBaseSet ExtractAt(int nIndex)
    {
        C2DLineBaseSet Result = this.get(nIndex);
        this.remove(nIndex);
        return Result;
    }
    /// <summary>
    /// Merges the joining routes together if there are any.
    /// </summary>
    public void MergeJoining()
    {
        C2DLineBaseSetSet Temp = new C2DLineBaseSetSet();

        while (size() > 0)
        {
	        // pop the last one.
	        C2DLineBaseSet pLast = this.get(size() - 1);
            this.remove(size() - 1);

	        if (!pLast.IsClosed(true))
	        {
		        int i = 0 ;
		        while ( i < size() )
		        {
			        if ( ! this.get(i).IsClosed(true))
			        {
				        if (this.get(i).AddIfCommonEnd( pLast))
				        {
					        pLast = null;
                            i += size();	// escape
				        }
			        }

			        i++;
		        }
	        }

	        if (pLast != null)
	        {
		        Temp.add( pLast);
	        }
        }

        this.ExtractAllOf(Temp);
    }

//public void DebugOut()
//{
//   // String strOut = new string("r");

//    String strOut = String.Format("size() {0} \n", size());
//    System.Diagnostics.Trace.TraceInformation(strOut);

//    for (int i = 0; i < size(); i++)
//    {
//        String strOut0 = String.Format("{0}: size() {1} ", i, this.get(i).size());
//      //  System.Diagnostics.Trace.TraceInformation(strOut);

//        //for (int j = 0 ; j < this->GetAt(i)->size(); j++)
//        //{
//        //	C2DPoint p1 = this->GetAt(i)->GetAt(j)->GetPointFrom();
//        //	sprintf(buff, "x:%f  y:%f", p1.x, p1.y);
//        //}

//        C2DPoint p1 = this.get(i).get(0).GetPointFrom();
//        String strOut1 = String.Format("From x:{0}  y:{1}", p1.x, p1.y);
//      //  System.Diagnostics.Trace.TraceInformation(strOut);

//        C2DPoint p2 = this.get(i).get(this.get(i).size() - 1).GetPointTo();
//        String strOut2 = String.Format("To x:{0}  y:{1} \n", p2.x, p2.y);
//        System.Diagnostics.Trace.TraceInformation(strOut0 + strOut1 + strOut2);
//    }
//}



    /// <summary>
    /// Adds all the routes from the other to this if the join a routes in this.
    /// </summary>
    /// <param name="Other">The other set.</param>
    public void AddJoining(  C2DLineBaseSetSet Other )
    {
        C2DLineBaseSetSet Temp = new C2DLineBaseSetSet();
    	
        while (Other.size() > 0 )
        {
	        C2DLineBaseSet pLast = Other.ExtractAt(Other.size() - 1);

	        int i = 0;
	        while ( i < size())
	        {
		        if ( !this.get(i).IsClosed(true) && this.get(i).AddIfCommonEnd( pLast))
		        {
			        pLast = null;
                    i += size();	// escape
		        }

		        i++;
	        }

	        if (pLast != null)
	        {
		        Temp.add(pLast);
	        }
        }

        while (Temp.size() > 0)
        {
            Other.add(Temp.ExtractAt(Temp.size() - 1));
        }
    }

    /// <summary>
    /// Adds the routes in the other set that are closed.        
    /// </summary>
    /// <param name="Other">The other set.</param>
    /// <param name="bEndsOnly">True if only the ends require checking.</param>
    public void AddClosed( C2DLineBaseSetSet Other , boolean bEndsOnly)
    {
        C2DLineBaseSetSet Temp = new C2DLineBaseSetSet();

        while (Other.size() > 0)
        {
            C2DLineBaseSet pLast = Other.ExtractAt(Other.size() - 1);
            if (pLast.IsClosed(bEndsOnly))
            {
                this.add(pLast);
            }
            else
            {
                Temp.add(pLast);
            }
        }

        while (Temp.size() > 0)
        {
            Other.add(Temp.ExtractAt(Temp.size() - 1));
        }
    }

}
