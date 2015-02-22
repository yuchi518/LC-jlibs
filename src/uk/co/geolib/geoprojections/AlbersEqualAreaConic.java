/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file AlbersEqualAreaConic.cpp
/// Implementation file for a CAlbersEqualAreaConic class.

Implementation file for a CAlbersEqualAreaConic class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;




/// <summary>
/// Class representing an albers equal area projection.
/// </summary>
public class AlbersEqualAreaConic extends Projection
{
    /// <summary>
    /// Destructor.
    /// </summary>
    public AlbersEqualAreaConic()   
    {
        m_dStandardParallel1 = Constants.conTHIRDPI;

        m_dStandardParallel2 = Constants.conTHIRDPI;

        m_dOriginLat = 0;

        m_dOriginLong = 0;

        CalculateConstants();
    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the 
    /// result.
    /// </summary>
    public void Project(double dLatY, double dLongX)
    {
        dLatY *= Constants.conRadiansPerDegree;

        dLongX *= Constants.conRadiansPerDegree;

        double theta = m_dn * (dLongX - m_dOriginLong);

        double dP = Math.sqrt(m_dC - 2 * m_dn * Math.sin(dLatY)) / m_dn;

        dLongX = dP * Math.sin(theta);

        dLatY = m_dP0 - dP * Math.cos(theta);
    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the result and retaining 
    /// the lat long in the class passed.
    /// </summary>
    public void Project(GeoLatLong rLatLong, double dx, double dy)
    {
        dy = rLatLong.GetLatDegrees();
        dx = rLatLong.GetLongDegrees();

        Project(dy, dx);
    }

    /// <summary>
    /// Project the given x y to lat long using the input parameters to store the result.	
    /// </summary>
    public void InverseProject(double dLatY, double dLongX)
    {
        double theta = Math.atan2(dLongX, (m_dP0 - dLatY));

        double dP = Math.sqrt(dLongX * dLongX + (m_dP0 - dLatY) * (m_dP0 - dLatY));

        dLatY = Math.asin((m_dC - dP * dP * m_dn * m_dn) / (2 * m_dn));

        dLongX = m_dOriginLong + theta / m_dn;

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
    void SetStandardParallels(double dStandardParallel, double dStandardParalle2)
    {
        m_dStandardParallel1 = dStandardParallel * Constants.conRadiansPerDegree;

        m_dStandardParallel2 = dStandardParalle2 * Constants.conRadiansPerDegree;

        CalculateConstants();
    }


    /// <summary>
    /// 
    /// </summary>
    void SetOrigin(double dLat, double dLong)
    {
        m_dOriginLat = dLat * Constants.conRadiansPerDegree;

        m_dOriginLong = dLong * Constants.conRadiansPerDegree;

        CalculateConstants();
    }

    /// <summary>
    /// 
    /// </summary>
    void CalculateConstants()
    {
        m_sin_SP1 = Math.sin( m_dStandardParallel1 );

        m_sin_SP2 = Math.sin(m_dStandardParallel2);

        m_cos_SP1 = Math.cos(m_dStandardParallel1);

        m_dn = (m_sin_SP1 + m_sin_SP2) / 2;

        m_dC = m_cos_SP1 * m_cos_SP1 + 2 * m_dn * m_sin_SP1;

        m_dP0 = Math.sqrt(m_dC - 2 * m_dn * Math.sin(m_dOriginLat)) / m_dn;

    }


	double m_dStandardParallel1;

    double m_dStandardParallel2;

    double m_dOriginLat;

    double m_dOriginLong;

// derived constants

    double m_sin_SP1;

    double m_sin_SP2;

    double m_cos_SP1;

    double m_dn;

    double m_dC;

    double m_dP0;


}
