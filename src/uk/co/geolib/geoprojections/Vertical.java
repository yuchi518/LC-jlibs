/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file Vertical.cpp
///Implementation file for a CVertical class.

Implementation file for a CVertical class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing an albers equal area projection.
/// </summary>
public class Vertical extends Projection
{
    /// <summary>
    /// Constructor.
    /// </summary>
    public Vertical()
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


        double sin_lat = Math.sin(dLatY);

        double cos_lat = Math.cos(dLatY);

        double sin_olat = Math.sin(m_dStandardLatitude);

        double cos_olat = Math.cos(m_dStandardLatitude);

        double cos_dlong = Math.cos(dLongX - m_dStandardLongitude);

  //      double cos_c = sin_olat * sin_lat + cos_olat * cos_lat * cos_dlong;

        dLongX = cos_lat * Math.sin(dLongX - m_dStandardLongitude);

        dLatY = cos_olat * sin_lat - sin_olat * cos_lat * cos_dlong;

        //if (pBack !=NULL)
        //	*pBack = Math.Cos_c < 0;

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
    /// Project the given x y to lat long using the input parameters to store /// the result.	
    /// </summary>
    public void InverseProject(double dLatY, double dLongX)
    {
        double dHeading =  Math.atan2(dLongX, dLatY);

        double dHRange = Math.sqrt(dLongX * dLongX + dLatY * dLatY);

        // alpha is the angular Distance travelled round the earths surface
        double alpha = Math.asin(dHRange);
        double sin_alpha = dHRange;
        double cos_alpha = Math.cos(alpha);

        double latO = m_dStandardLatitude;
        double longO = m_dStandardLongitude;

        double sin_Olat = Math.sin(latO);
        double cos_Olat = Math.cos(latO);

        double dLat = Math.asin(sin_Olat * cos_alpha + cos_Olat * sin_alpha * Math.cos(dHeading));

        double dLong = longO +  Math.atan2(Math.sin(dHeading) * sin_alpha * cos_Olat, cos_alpha - sin_Olat * Math.sin(dLat));

        while (dLong > Constants.conPI)
            dLong -= Constants.conTWOPI;
        while (dLong < -Constants.conPI)
            dLong += Constants.conTWOPI;


        dLatY = dLat * Constants.conDegreesPerRadian;

        dLongX = dLong * Constants.conDegreesPerRadian;
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
    void SetOrigin(double dLat, double dLong)
    {
        m_dStandardLatitude = dLat * Constants.conRadiansPerDegree;

        m_dStandardLongitude = dLong * Constants.conRadiansPerDegree;
    }


    private double m_dStandardLatitude;

    private double m_dStandardLongitude;
}
