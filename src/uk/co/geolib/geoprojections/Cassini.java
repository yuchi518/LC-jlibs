/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file CCassini.cpp
\brief Implementation file for a CCassini class.

Implementation file for a CCassini class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;




/// <summary>
/// Class representing an albers equal area projection.
/// </summary>
public class Cassini extends Projection
{

    /// <summary>
    /// Constructor.
    /// </summary>
    public Cassini()
    {
        m_dStandardLongitude = 0;
    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the 
    /// result.
    /// </summary>
    public void Project(double dLatY, double dLongX)
    {
        dLatY *= Constants.conRadiansPerDegree;

        dLongX *= Constants.conRadiansPerDegree;

        double dX = Math.asin(Math.cos(dLatY) * Math.sin(dLongX - m_dStandardLongitude));

        double dY = Math.atan2(Math.tan(dLatY), Math.cos(dLongX - m_dStandardLongitude));

        dLatY = dY;

        dLongX = dX;
    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the result and retaining 
    /// the lat long in the class passed.
    /// </summary>
    public void Project(GeoLatLong rLatLong, double dx, double dy)
    {
        dy = rLatLong.GetLat();
        dx = rLatLong.GetLong();

        Project(dy, dx);
    }

    /// <summary>
    /// Project the given x y to lat long using the input parameters to store the result.	
    /// </summary>
    public void InverseProject(double dLatY, double dLongX)
    {
        double dD = dLatY + m_dStandardLongitude;

        double dLat = Math.asin( Math.sin(dD) * Math.cos( dLongX) );

        double dLong = m_dStandardLongitude + Math.atan2( Math.tan(dLongX) , Math.cos(dD));

        dLatY = dLat * Constants.conDegreesPerRadian;

        dLongX = dLong * Constants.conDegreesPerRadian;
    }


    /// <summary>
    /// Project the given x y to lat long using the input lat long class to get the result.
    /// </summary>
    public void InverseProject(GeoLatLong rLatLong, double dX, double dY)
    {
        double dD = dY + m_dStandardLongitude;

        double dLat = Math.asin( Math.sin(dD) * Math.cos( dX) );

        double dLong = m_dStandardLongitude + Math.atan2( Math.tan(dX) , Math.cos(dD));

        rLatLong.SetLat(dLat);

        rLatLong.SetLong(dLong);
    }

    /// <summary>
    /// Set Standard Longitude.
    /// </summary>
    public void SetStandardLongitude(double dStandardLongitude)
    {
        m_dStandardLongitude = dStandardLongitude * Constants.conRadiansPerDegree;
    }

    private	double m_dStandardLongitude;
}
