/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file CylindricalEquidistant.cpp
///Implementation file for a CCylindricalEquidistant class.

Implementation file for a CCylindricalEquidistant class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing a circle.
/// </summary>
public class CylindricalEquidistant extends Projection
{

    /// <summary>
    /// Constructor.
    /// </summary>
    public CylindricalEquidistant()
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

        dLongX = (dLongX - m_dStandardLongitude) * Math.cos(m_dStandardLatitude);
    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the result and retaining 
    /// the lat long in the class passed.
    /// </summary>
    public void Project( GeoLatLong rLatLong, double dx, double dy) 
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
        dLongX = m_dStandardLongitude + dLongX / Math.cos(m_dStandardLatitude);

        dLatY *= Constants.conDegreesPerRadian;

        dLongX *= Constants.conDegreesPerRadian;
    }


    /// <summary>
    /// Project the given x y to lat long using the input lat long class to get the result.
    /// </summary>
    public void InverseProject(GeoLatLong rLatLong,  double dX,  double dY) 
    {
        double dLatY = dY;

        double dLongX = dX;

        InverseProject(dLatY, dLongX);

        rLatLong.SetLat(dLatY);

        rLatLong.SetLong(dLongX);
    }

    /// <summary>
    /// </summary>
    public void SetOrigin(double dStandardLatitude, double dStandardLongitude)
    {
        m_dStandardLatitude = dStandardLatitude;

        m_dStandardLongitude = dStandardLongitude;
    }

    private double m_dStandardLongitude;
    private double m_dStandardLatitude;

}