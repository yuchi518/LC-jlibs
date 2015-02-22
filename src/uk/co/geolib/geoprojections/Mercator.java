/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file CMercator.cpp
///Implementation file for a CMercator class.

Implementation file for a CMercator class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing a circle.
/// </summary>
public class Mercator extends Projection
{


    /// <summary>
    ///Constructor.
    /// </summary>
    public Mercator()
    {
        m_dStandardLongitude = 0;
    }

    /// <summary>
    ///Constructor.
    /// </summary>
    public void Project(double dLatY, double dLongX)
    {
        dLatY *= Constants.conRadiansPerDegree;

        dLongX *= Constants.conRadiansPerDegree;

        double x = dLongX - m_dStandardLongitude;

        double y = Math.log( Math.tan(dLatY) + 1 / Math.cos(dLatY));

        dLatY = y;

        dLongX = x;
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
    /// Project the given x y to lat long using the input parameters to store 
    /// the result.	
    /// </summary>
    public void InverseProject(double dLatY, double dLongX)
    {
        double dLat = Math.atan(Math.sinh(dLatY));

        double dLong = dLongX + m_dStandardLongitude;

        dLatY = dLat * Constants.conDegreesPerRadian;

        dLongX = dLong * Constants.conDegreesPerRadian;
    }


    /// <summary>
    /// Project the given x y to lat long using the input lat long class to get the result.
    /// </summary>
    public void InverseProject(GeoLatLong rLatLong, double dX, double dY)
    {
        double dLat = Math.atan(Math.sinh(dX));

        double dLong = dY + m_dStandardLongitude;

        rLatLong.SetLat(dLat);

        rLatLong.SetLong(dLong);
    }

    /// <summary>
    /// </summary>
    public void SetStandardLongitude(double dStandardLongitude)
    {
        m_dStandardLongitude = dStandardLongitude * Constants.conRadiansPerDegree;
    }

    private double m_dStandardLongitude;
}
