/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file CylindricalEqualArea.cpp
///Implementation file for a CCylindricalEqualArea class.

Implementation file for a CCylindricalEqualArea class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing a circle.
/// </summary>
public class CylindricalEqualArea extends Projection
{

    /// <summary>
    /// constructor.
    /// </summary>
    public CylindricalEqualArea()
    {
        m_dStandardLatitude = 0;

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

        double cos_SL = Math.cos(m_dStandardLatitude);

        dLongX = (dLongX - m_dStandardLongitude) * cos_SL;

        dLatY = Math.sin(dLatY) / cos_SL;
    }


    /// <summary>
    ///Project the given lat long to x, y using the input parameters to store the result and retaining 
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
        double cos_SL = Math.cos(m_dStandardLatitude);

        dLatY = Math.asin(dLatY * cos_SL);

        dLongX = dLongX / cos_SL + m_dStandardLongitude;

        dLatY *= Constants.conDegreesPerRadian;

        dLongX *= Constants.conDegreesPerRadian;
    }


    /// <summary>
    /// Project the given x y to lat long using the input lat long class to get the result.
    /// </summary>
    public void InverseProject(GeoLatLong rLatLong, double dX, double dY)
    {
        double dLat = dY;

        double dLong = dX;

        InverseProject(dLat, dLong);

        rLatLong.SetLatDegrees(dLat);

        rLatLong.SetLongDegrees(dLong);
    }




    /// <summary>
    ///
    /// </summary>
    public void SetOrigin(double dLat, double dLong)
    {
        m_dStandardLatitude = dLat * Constants.conRadiansPerDegree;

        m_dStandardLongitude = dLong * Constants.conRadiansPerDegree;

    }

    private double m_dStandardLatitude;

    private double m_dStandardLongitude;
}
