package uk.co.geolib.geopolygons;

import java.util.ArrayList;

import uk.co.geolib.geolib.*;

public class C2DHoledPolyArc extends C2DHoledPolyBase{
    /// <summary>
    /// Constructor.
    /// </summary>
    public C2DHoledPolyArc()
    {
        Rim = new C2DPolyArc();
    }

    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="Other">Other polygon to set this to.</param> 
    public C2DHoledPolyArc(C2DHoledPolyBase Other)
    {
        Rim = new C2DPolyArc(Other.Rim);
        for (int i = 0; i < Other.getHoleCount(); i++)
        {
            Holes.add(new C2DPolyArc(Other.GetHole(i)));
        }
    }

            /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="Other">Other polygon to set this to.</param> 
    public C2DHoledPolyArc(C2DPolyBase Other)
    {
        Rim = new C2DPolyArc(Other);
    }

    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="Other">Other polygon to set this to.</param> 
    public C2DHoledPolyArc(C2DPolyArc Other)
    {
        Rim = new C2DPolyArc(Other);
    }


    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="Other">Other polygon to set this to.</param> 
    public void Set(C2DHoledPolyBase Other)
    {
        Rim.Set(Other.Rim);
        Holes.clear();
        for (int i = 0; i < Other.getHoleCount(); i++)
        {
            Holes.add(new C2DPolyArc(Other.GetHole(i)));
        }
    }

    /// <summary>
    /// Assignment.
    /// </summary>
    /// <param name="Other">Other polygon to set this to.</param> 
    public void Set(C2DHoledPolyArc Other)
    {
        Rim.Set(Other.Rim);
        Holes.clear();
        for (int i = 0; i < Other.getHoleCount(); i++)
        {
            Holes.add(new C2DPolyArc(Other.GetHole(i)));
        }
    }


    /// <summary>
    /// Constructor.
    /// </summary>
    /// <param name="Other">Other polygon to set this to.</param> 
    public C2DHoledPolyArc(C2DHoledPolyArc Other)
    {
        Rim = new C2DPolyArc(Other.Rim);
        for (int i = 0; i < Other.getHoleCount(); i++)
        {
            Holes.add(new C2DPolyArc(Other.GetHole(i)));
        }
    }


    /// <summary>
    /// Gets the area.
    /// </summary>
     public double GetArea() 
    {
        double dResult = 0;

	    dResult += getRim().GetArea();

        for ( int i = 0 ; i < Holes.size(); i++)
        {
	        dResult -= GetHole(i).GetArea();
        }
        return dResult;


    }

    /// <summary>
    /// Gets the centroid.
    /// </summary>
    C2DPoint GetCentroid()
    {
        C2DPoint Centroid = getRim().GetCentroid();
        double dArea = getRim().GetArea();

        for (int i = 0; i < Holes.size(); i++)
        {
		        C2DVector vec = new C2DVector( Centroid, GetHole(i).GetCentroid());

		        double dHoleArea = GetHole(i).GetArea();

		        double dFactor =  dHoleArea / (dHoleArea + dArea);	

		        vec.Multiply( dFactor);
		        Centroid.x += vec.i;
                Centroid.y += vec.j;
		        dArea += dHoleArea;
        }


        return Centroid;

    }


    /// <summary>
    /// Gets the non overlaps i.e. the parts of this that aren't in the other.
    /// </summary>
    /// <param name="Other">The other shape.</param> 
    /// <param name="Polygons">The set to recieve the result.</param> 
    /// <param name="grid">The degenerate settings.</param> 
    public void GetNonOverlaps(C2DHoledPolyArc Other, ArrayList<C2DHoledPolyArc> Polygons,
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
    public void GetUnion(C2DHoledPolyArc Other, ArrayList<C2DHoledPolyArc> Polygons,
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
    public void GetOverlaps(C2DHoledPolyArc Other, ArrayList<C2DHoledPolyArc> Polygons,
                                        CGrid grid)
    {
    	ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

        super.GetOverlaps(Other, NewPolys, grid);

        for (int i = 0; i < NewPolys.size(); i++)
            Polygons.add(new C2DHoledPolyArc(NewPolys.get(i)));
    }











    /// <summary>
    /// Rim access.
    /// </summary>
    public C2DPolyArc getRim()
    {

        return (C2DPolyArc)Rim;
    }


    /// <summary>
    /// Gets the Hole as a C2DPolyArc.
    /// </summary>
    /// <param name="i">Hole index.</param> 
    public C2DPolyArc GetHole(int i)
    {
        return (C2DPolyArc)Holes.get(i);
    }

    /// <summary>
    /// Hole assignment.
    /// </summary>
    public void SetHole(int i, C2DPolyArc Poly)
    {
        Holes.set(i, Poly);
    }

    /// <summary>
    /// Hole addition.
    /// </summary>
    public void AddHole(C2DPolyArc Poly)
    {
        Holes.add(Poly);
    }

    /// <summary>
    /// Hole assignment.
    /// </summary>
    public void SetHole(int i, C2DPolyBase Poly)
    {
        if (Poly instanceof C2DPolyArc)
        {
            Holes.set(i, Poly);
        }
        else
        {
       //     Debug.Assert(false, "Invalid Hole type" );
        }
    }

    /// <summary>
    /// Hole addition.
    /// </summary>
    public void AddHole(C2DPolyBase Poly)
    {
        if (Poly instanceof C2DPolyArc)
        {
            Holes.add(Poly);
        }
        else
        {
        //    Debug.Assert(false, "Invalid Hole type");
        }
    }

    /// <summary>
    /// Hole removal.
    /// </summary>
    public void RemoveHole(int i)
    {
        Holes.remove(i);
    }

}
