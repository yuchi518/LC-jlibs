/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file CProjection.cpp
/// Implementation file for a CProjection class.

Implementation file for a CProjection class.
---------------------------------------------------------------------------*/



package uk.co.geolib.geoprojections;

/// <summary>
/// Abstract class for a projection.
/// </summary>
public abstract class Projection extends Transformation
{


    /// <summary>
    /// Constructor.
    /// </summary>
    public Projection()
    {
    }

    	/// Project the given lat long to x, y using the input parameters to store the result.
    public abstract void Project(double dLatY, double dLongX);

    /// Project the given lat long to x, y using the input parameters to store the result and retaining 
    /// the lat long in the class passed.
    public abstract void Project(GeoLatLong rLatLong, double dx, double dy);

    /// Project the given x y to lat long using the input parameters to store the result.	
    public abstract void InverseProject(double dLatY, double dLongX);
    /// Project the given x y to lat long using the input lat long class to get the result.
    public abstract void InverseProject(GeoLatLong rLatLong, double dX, double dY);

    /// <summary>
    /// Transform override function.
    /// </summary>
    public void Transform(double dx, double dy)
    {
        Project(dy, dx);
    }
    /// <summary>
    /// Inverse transform the given point.
    /// </summary>
    public void InverseTransform(double dx, double dy)
    {
        InverseProject(dy, dx);
    }



    /// <summary>
    /// GetProjectionName.
    /// </summary>
    String GetProjectionName(int nIndex)
    {
        if (nIndex < const_strMercator.length)
        {
	        return const_strMercator[nIndex];
        }
        return "";
    }


    /// <summary>
    /// GetProjectionCount.
    /// </summary>
    int GetProjectionCount()
    {
        return const_strMercator.length;
    }


    /// <summary>
    /// CreateProjection.
    /// </summary>
    Projection CreateProjection(String strName)
    {
        if (strName.equals("Albers Equal Area Conic"))
        {
	        return new AlbersEqualAreaConic();
        }
        if (strName.equals("Bonne"))
        {
	        return new BonneProjection();
        }
        if (strName.equals("Cassini"))
        {
	        return new Cassini();
        }
        if (strName.equals("Conic Equidistant"))
        {
	        return new ConicEquidistant();
        }
        if (strName.equals("Cylindrical Equal Area"))
        {
	        return new CylindricalEqualArea();
        }
        if (strName.equals("Cylindrical Equidistant"))
        {
	        return new CylindricalEquidistant();
        }
        if (strName.equals("Eckert VI"))
        {
	        return new EckertVI();
        }
        if (strName.equals("Eckert IV"))
        {
	        return new EckertIV();
        }
        if (strName.equals("Gnomonic"))
        {
	        return new Gnomonic();
        }
        if (strName.equals("HorizontalRangeHeading"))
        {
	        return new HorizontalRangeHeading();
        }

        if (strName.equals("Lambert Azimuthal Equal Area"))
        {
	        return new LambertAzimuthalEqualArea();
        }
        if (strName.equals("Lambert Conformal Conic"))
        {
	        return new LambertConformalConic();
        }
        if (strName.equals("Mercator"))
        {
	        return new Mercator();
        }
        if (strName.equals("Miller Cylindrical"))
        {
	        return new MillerCylindrical();
        }
        if (strName.equals("Mollweide"))
        {
	        return new Mollweide();
        }
        if (strName.equals("Orthographic"))
        {
	        return new Orthographic();
        }
        if (strName.equals("Polyconic"))
        {
	        return new Polyconic();
        }
        if (strName.equals("RangeHeading"))
        {
	        return new RangeHeading();
        }
        if (strName.equals("Sinusoidal"))
        {
	        return new Sinusoidal();
        }
        if (strName.equals("SlantRangeHeading"))
        {
	        return new SlantRangeHeading();
        }
        if (strName.equals("Stereographic"))
        {
	        return new Stereographic();
        }
        if (strName.equals("Van Der Grinten"))
        {
	        return new VanDerGrinten();
        }
        if (strName.equals("Vertical Perspective"))
        {
	        return new VerticalPerspective();
        }
        if (strName.equals("Vertical"))
        {
	        return new Vertical();
        }
    	
    	
        return null;
    }

    static String[] const_strMercator =
    {
       "Albers Equal Area Conic",
        "Bonne",
        "Cassini",
        "Conic Equidistant",
        "Cylindrical Equal Area",
        "Cylindrical Equidistant",
        "Eckert VI",
        "Eckert IV",
        "Gnomonic",
        "HorizontalRangeHeading",
        "Lambert Azimuthal Equal Area",
        "Lambert Conformal Conic",
        "Mercator",
        "Miller Cylindrical",
        "Mollweide",
        "Orthographic",
        "Polyconic",
        "RangeHeading",
        "Sinusoidal",
        "SlantRangeHeading",
        "Stereographic",
        "Van Der Grinten",
        "Vertical Perspective",
        "Vertical",
    };
}
