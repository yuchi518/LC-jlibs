/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file LambertAzimuthalEqualArea.cpp
///Implementation file for a CLambertAzimuthalEqualArea class.

Implementation file for a CLambertAzimuthalEqualArea class.
---------------------------------------------------------------------------*/

package uk.co.geolib.geoprojections;


/// <summary>
/// Class representing a LambertAzimuthalEqualArea.
/// </summary>
public class LambertAzimuthalEqualArea extends Projection
{


    /// <summary>
    /// Constructor.
    /// </summary>
    public LambertAzimuthalEqualArea()
    {
        m_dStandardParallel = 0;

        m_dCentralLongitude = 0;
    }


    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the 
    /// result.
    /// </summary>
    public void Project(double dLatY, double dLongX) 
    {
        dLatY *= Constants.conRadiansPerDegree;

        dLongX *= Constants.conRadiansPerDegree;

        double sin_SP = Math.sin(m_dStandardParallel);
        double sin_lat = Math.sin(dLatY);

        double cos_SP = Math.cos(m_dStandardParallel);
        double cos_lat = Math.cos(dLatY);
        double cos_Dlong = Math.cos(dLongX - m_dCentralLongitude);

        double dK = Math.sqrt(2.0 / ( 1 + sin_SP*sin_lat + cos_SP*cos_lat*cos_Dlong));

        dLongX = dK * cos_lat * Math.sin(dLongX - m_dCentralLongitude);

        dLatY = dK*( cos_SP* sin_lat - sin_SP * cos_lat * cos_Dlong);
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
    /// Project the given x y to lat long using the input parameters to store /// the result.	
    /// </summary>
    public void InverseProject(double dLatY, double dLongX) 
    {
        double P1 = dLongX * dLongX + dLatY * dLatY;

        double P = Math.sqrt( P1 );
    	
        double C = 2 * Math.asin( 0.5 * P);

        double cos_C = Math.cos(C);

        double sin_C = Math.sin(C);

        double sin_SP = -Math.sin(m_dStandardParallel);

        double cos_SP = Math.cos(m_dStandardParallel);

        double dLat = Math.asin( cos_C* sin_SP + (dLatY * sin_C * cos_SP) / P);

        double dLong;

        if (P1 < 2)
        {
		        dLong = m_dCentralLongitude + Math.atan(dLongX * sin_C /
							        (P *cos_SP * cos_C - dLatY * sin_SP * sin_C)  );
        }
        else
        {
	        if (dLongX	> 0 )
		        dLong = m_dCentralLongitude + Constants.conPI + Math.atan( dLongX * sin_C /
								        (P *cos_SP * cos_C - dLatY* sin_SP * sin_C)  );
	        else
		        dLong = m_dCentralLongitude - Constants.conPI + Math.atan( dLongX * sin_C /
										        (P *cos_SP * cos_C - dLatY * sin_SP * sin_C)  );
        }

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
    void SetOrigin(double dLat, double dLong)
    {
        m_dStandardParallel = dLat * Constants.conRadiansPerDegree;

        m_dCentralLongitude = dLong * Constants.conRadiansPerDegree;

    }

	double m_dStandardParallel;

    double m_dCentralLongitude;
}
