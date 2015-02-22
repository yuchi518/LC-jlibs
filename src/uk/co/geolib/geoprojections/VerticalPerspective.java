/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file VerticalPerspective.cpp
///Implementation file for a CVerticalPerspective class.

Implementation file for a CVerticalPerspective class.
---------------------------------------------------------------------------*/



package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing an albers equal area projection.
/// </summary>
public class VerticalPerspective extends Projection
{

    /// <summary>
    ///Destructor.
    /// </summary>
    public VerticalPerspective()
    {
        m_dStandardLatitude = 0;

        m_dStandardLongitude = 0;

        m_P = 10.0F;
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

        double cos_dlong = Math.cos(dLongX  - m_dStandardLongitude);

        double cos_c = sin_olat * sin_lat + cos_olat * cos_lat * cos_dlong;

        double k = (m_P - 1) / (m_P - cos_c);

        dLongX = k * cos_lat * Math.sin(dLongX  - m_dStandardLongitude);

        dLatY = k * ( cos_olat * sin_lat - sin_olat * cos_lat * cos_dlong);

    //	if (pBack != NULL)
        //	*pBack = Math.Cos_c < (1 / P);


    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the result and retaining 
    /// /// the lat long in the class passed.
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
        // TO DO - Need proper inverse projection. Current inverse is valid for infinite P only
        double dHeading =  Math.atan2(dLongX,dLatY);

        double dHRange = Math.sqrt(dLongX * dLongX + dLatY * dLatY);

        // alpha is the angular Distance travelled round the earths surface
        double alpha = Math.asin( dHRange );

    //	double k = (P - 1) / (P - alpha);

        double sin_alpha = dHRange;
        double cos_alpha = Math.cos(alpha);

        double latO = m_dStandardLatitude;
        double longO = m_dStandardLongitude;

        double sin_Olat = Math.sin(latO);
        double cos_Olat = Math.cos(latO);

        double dLat = Math.asin(  sin_Olat * cos_alpha + cos_Olat * sin_alpha * Math.cos(dHeading)  );

        double dLong = longO +  Math.atan2(Math.sin(dHeading) * sin_alpha * cos_Olat , cos_alpha - sin_Olat * Math.sin(dLat));

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
    public void InverseProject(GeoLatLong rLatLong,  double dX,  double dY) 
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


    /// <summary>
    ///
    /// </summary>
    public void SetPointOfPerspective(float p)
    {
        m_P = p;
    }

    private double m_dStandardLatitude;

    private double m_dStandardLongitude;

    private float m_P;
}
