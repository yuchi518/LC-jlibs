/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file ConicEquidistant.cpp
///Implementation file for a CConicEquidistant class.

Implementation file for a CConicEquidistant class.
---------------------------------------------------------------------------*/



package uk.co.geolib.geoprojections;




/// <summary>
/// Class representing a CConicEquidistant.
/// </summary>
public class ConicEquidistant extends Projection
{

    /// <summary>
    /// Constructor.
    /// </summary>
    public ConicEquidistant()
    {
        m_dStandardParallel1 = 0;

        m_dStandardParallel2 = Constants.conTHIRDPI;

        m_dOriginLat = 0;

        m_dOriginLong = 0;

        CalculateConstants();
    }



    /// <summary>
    ///Project the given lat long to x, y using the input parameters to store the 
    /// result.
    /// </summary>
    public void Project(double dLatY, double dLongX) 
    {
        dLatY *= Constants.conRadiansPerDegree;

        dLongX *= Constants.conRadiansPerDegree;

        double dTheta = m_dn * (dLongX - m_dOriginLong);

        double dP = m_dG - dLatY;

        dLongX = dP * Math.sin(dTheta);

        dLatY = m_dP0 - dP * Math.cos(dTheta);
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

        double dP = Math.sqrt(dLongX * dLongX + (m_dP0 - dLatY) * (m_dP0 - dLatY) );
        if (m_dn < 0)
	        dP = -dP;

    //	double dTheta = a Math.tan(dLongX / (m_dP0 - dLatY) );
        double dTheta =  Math.atan2(dLongX , (m_dP0 - dLatY) );


        dLatY = m_dG - dP;

        dLongX = m_dOriginLong + dTheta / m_dn;

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
    public void SetOrigin(double dLat, double dLong)
    {
        m_dOriginLat = dLat * Constants.conRadiansPerDegree;

        m_dOriginLong = dLong * Constants.conRadiansPerDegree;

        CalculateConstants();
    }

    /// <summary>
    ///
    /// </summary>
    public void CalculateConstants()
    {
        double cos_SP1 = Math.cos( m_dStandardParallel1 );

        m_dn = (cos_SP1 - Math.cos( m_dStandardParallel2 ))/
			        ( m_dStandardParallel2 - m_dStandardParallel1);

        m_dG = cos_SP1 / m_dn +  m_dStandardParallel1;

        m_dP0 = m_dG - m_dOriginLat;
    }

    private double m_dStandardParallel1;

    private double m_dStandardParallel2;

    private double m_dOriginLat;

    private double m_dOriginLong;

    // derived constants

    private double m_dn;

    private double m_dG;

    private double m_dP0;
}
