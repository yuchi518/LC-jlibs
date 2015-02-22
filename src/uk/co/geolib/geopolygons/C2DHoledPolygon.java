package uk.co.geolib.geopolygons;

import java.util.ArrayList;

import uk.co.geolib.geolib.*;


/// <summary>
/// Class to represent a 2D polygon with holes.
/// </summary>
public class C2DHoledPolygon extends C2DHoledPolyBase {


        /// <summary>
        /// Constructor.
        /// </summary>
        public C2DHoledPolygon() 
        {
            Rim = new C2DPolygon();
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        public C2DHoledPolygon(C2DHoledPolyBase Other)
        {
            Rim = new C2DPolygon(Other.Rim);
            for (int i = 0; i < Other.getHoleCount(); i++)
            {
                Holes.add(new C2DPolygon(Other.GetHole(i) ));
            }
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        public C2DHoledPolygon(C2DHoledPolygon Other)
        {
            Rim = new C2DPolygon(Other.Rim);
            for (int i = 0; i < Other.getHoleCount(); i++)
            {
                Holes.add(new C2DPolygon(Other.GetHole(i)));
            }
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="Other">Other polygon to set this to.</param> 
        public C2DHoledPolygon(C2DPolyBase Other)
        {
            Rim = new C2DPolygon(Other);
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="Other">Other polygon to set this to.</param> 
        public C2DHoledPolygon(C2DPolygon Other)
        {
            Rim = new C2DPolygon(Other);
        }

        /// <summary>
        /// Assignment.
        /// </summary>
        /// <param name="Other">Other polygon to set this to.</param> 
        public void Set(C2DHoledPolyBase Other)
        {
            Holes.clear();
            Rim = new C2DPolygon(Other.Rim);
            for (int i = 0; i < Other.getHoleCount(); i++)
            {
                Holes.add(new C2DPolygon(Other.GetHole(i)));
            }
        }

        /// <summary>
        /// Assignment.
        /// </summary>
        /// <param name="Other">Other polygon to set this to.</param> 
        public void Set(C2DHoledPolygon Other)
        {
            Holes.clear();
            Rim = new C2DPolygon(Other.Rim);
            for (int i = 0; i < Other.getHoleCount(); i++)
            {
                Holes.add(new C2DPolygon(Other.GetHole(i)));
            }
        }

        /// <summary>
        /// Rotates to the right by the angle around the centroid
        /// </summary>
        /// <param name="dAng">Angle in radians to rotate by.</param> 
        public void RotateToRight(double dAng)
        {
            RotateToRight(dAng, GetCentroid());
        }

        /// <summary>
        /// Grows around the centroid.
        /// </summary>
        /// <param name="dFactor">Factor to grow by.</param> 
        public void Grow(double dFactor)
        {
            Grow(dFactor, GetCentroid());
        }


        /// <summary>
        /// Calculates the centroid of the polygon by moving it according to each holes
        /// weighted centroid.
        /// </summary>
        public C2DPoint GetCentroid()
        {
                 
	        C2DPoint HoleCen = new C2DPoint(0, 0);

            if (Holes.size() == 0)
                return getRim().GetCentroid();


            C2DPoint PolyCen = getRim().GetCentroid();

            double dPolyArea = getRim().GetArea();
	        double dHoleArea = 0;

	        for ( int i = 0 ; i < Holes.size(); i++)
	        {
		        dHoleArea += GetHole(i).GetArea();
	        }


	        if (dHoleArea == 0 || dHoleArea == dPolyArea)
		        return getRim().GetCentroid();
	        else
	        {
		        for (int i = 0 ; i < Holes.size(); i++)
		        {
                    C2DPoint pt = GetHole(i).GetCentroid();
                    pt.Multiply(GetHole(i).GetArea() / dHoleArea);
			        HoleCen.x += pt.x;
			        HoleCen.y += pt.y;        
		        }
	        }

	        C2DVector Vec = new C2DVector(HoleCen, PolyCen);

	        Vec.Multiply( dHoleArea / (dPolyArea - dHoleArea));

	        PolyCen.Move(Vec);

	        return PolyCen;
        }


        /// <summary>
        /// Calculates the area.
        /// </summary>
        public double GetArea() 
        {
	        double dResult = 0;

            dResult += getRim().GetArea();

	        for (int i = 0 ; i < Holes.size(); i++)
	        {
                dResult -= GetHole(i).GetArea();
	        }
	        return dResult;
        }

        /// <summary>
        /// Buffers the polygon by buffering all shapes to expand the shape.
        /// No attempt to handle resulting crossing lines as designed for 
        /// very small buffers.
        /// </summary>
        /// <param name="dBuffer">The buffer amount</param>
        public void SimpleBuffer(double dBuffer)
        {
        	getRim().SimpleBuffer(dBuffer);

            for (int i = 0; i < Holes.size(); i++)
            {
                GetHole(i).SimpleBuffer(-dBuffer);
            }
        }

        /// <summary>
        /// Removes null areas within the shape according to the tolerance.
        /// </summary>
        /// <param name="dTolerance"></param>
        /// <returns>True if the shape is no longer valid.</returns>
        public boolean RemoveNullAreas(double dTolerance)
        {
            if (Rim instanceof C2DPolygon)
            {
                if (((C2DPolygon)Rim).RemoveNullAreas(dTolerance))
                {
                    return true;
                }
            }

            int i = 0;
            while ( i < Holes.size())
            {
                if (GetHole(i).RemoveNullAreas(dTolerance))
                {
                    Holes.remove(i);
                }
                else
                {
                    i++;
                }
            }
            return false;
        }



        /// <summary>
        /// Gets the non overlaps i.e. the parts of this that aren't in the other.
        /// </summary>
        /// <param name="Other">The other shape.</param> 
        /// <param name="Polygons">The set to recieve the result.</param> 
        /// <param name="grid">The degenerate settings.</param> 
        public void GetNonOverlaps(C2DHoledPolygon Other, ArrayList<C2DHoledPolygon> Polygons,
                                            CGrid grid)
        {
            ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

            super.GetNonOverlaps(Other, NewPolys, grid);

            for (int i = 0; i < NewPolys.size(); i++)
                Polygons.add(new C2DHoledPolygon(NewPolys.get(i)));
        }

        /// <summary>
        /// Gets the union of the 2 shapes.
        /// </summary>
        /// <param name="Other">The other shape.</param> 
        /// <param name="Polygons">The set to recieve the result.</param> 
        /// <param name="grid">The degenerate settings.</param> 
        public void GetUnion(C2DHoledPolygon Other, ArrayList<C2DHoledPolygon> Polygons,
                                            CGrid grid)
        {
        	ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

            super.GetUnion(Other, NewPolys, grid);

            for (int i = 0; i < NewPolys.size(); i++)
                Polygons.add(new C2DHoledPolygon(NewPolys.get(i)));
        }


        /// <summary>
        /// Gets the overlaps of the 2 shapes.	
        /// </summary>
        /// <param name="Other">The other shape.</param> 
        /// <param name="Polygons">The set to recieve the result.</param> 
        /// <param name="grid">The degenerate settings.</param> 
        public void GetOverlaps(C2DHoledPolygon Other, ArrayList<C2DHoledPolygon> Polygons,
                                            CGrid grid)
        {
        	ArrayList<C2DHoledPolyBase> NewPolys = new ArrayList<C2DHoledPolyBase>();

            super.GetOverlaps(Other, NewPolys, grid);

            for (int i = 0; i < NewPolys.size(); i++)
                Polygons.add(new C2DHoledPolygon(NewPolys.get(i)));
        }







        /// <summary>
        /// The rim.
        /// </summary>
        public C2DPolygon getRim()
        {
            return (C2DPolygon)Rim;
        }

        /// <summary>
        /// Gets the Hole as a C2DPolygon.
        /// </summary>
        /// <param name="i">Hole index.</param> 
        public C2DPolygon GetHole(int i)
        {
            return (C2DPolygon)Holes.get(i);
        }

        /// <summary>
        /// Hole access.
        /// </summary>
        public void SetHole(int i, C2DPolygon Poly)
        {
            Holes.set(i, Poly);
        }

        /// <summary>
        /// Hole addition.
        /// </summary>
        public void AddHole(C2DPolygon Poly)
        {
            Holes.add(Poly);
        }

        /// <summary>
        /// Hole access.
        /// </summary>
        public void SetHole(int i, C2DPolyBase Poly)
        {
            if (Poly instanceof C2DPolygon)
            {
                Holes.set(i, Poly);
            }
            else
            {
              //  Debug.Assert(false, "Invalid Hole type");
            }
        }

        /// <summary>
        /// Hole addition.
        /// </summary>
        public void AddHole(C2DPolyBase Poly)
        {
            if (Poly instanceof C2DPolygon)
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
