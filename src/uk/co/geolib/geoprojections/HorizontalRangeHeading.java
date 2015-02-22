/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file HorizontalRangeHeading.cpp
///Implementation file for a CHorizontalRangeHeading class.

Implementation file for a CHorizontalRangeHeading class.
---------------------------------------------------------------------------*/


package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing a circle.
/// </summary>
public class HorizontalRangeHeading extends Projection
{


    /// <summary>
    /// Constructor.
    /// </summary>
    public HorizontalRangeHeading()
    {
    	
    }

    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the 
    /// result.
    /// </summary>
    public void Project(double dLatY, double dLongX) 
    {
        GeoLatLong LatLong = new GeoLatLong(dLatY * Constants.conRadiansPerDegree, dLongX * Constants.conRadiansPerDegree);

        Double dRange = 0.0;
        Double dHeading = 0.0;

        m_Origin.RangeAndHeading(LatLong, dRange, dHeading);

        dRange = Constants.conEARTH_RADIUS_METRES * Math.sin(dRange / Constants.conEARTH_RADIUS_METRES);

        dLatY = dRange * Math.cos(dHeading);

        dLongX = dRange * Math.sin(dHeading);
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
        double dHdng =  Math.atan2(dLongX, dLatY);

        double dRange = Math.sqrt(dLongX * dLongX + dLatY * dLatY);

        dRange = Math.asin(dRange / Constants.conEARTH_RADIUS_METRES) * Constants.conEARTH_RADIUS_METRES;

        GeoLatLong Result = new GeoLatLong();

        Result.Set(dHdng, dRange, m_Origin);

        dLatY = Result.GetLatDegrees();

        dLongX = Result.GetLongDegrees();
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
        m_Origin.SetLatDegrees(dLat);

        m_Origin.SetLongDegrees(dLong);

    }

    /// <summary>
    ///
    /// </summary>
    GeoLatLong GetOrigin() 
    {
        return m_Origin;
    }

    private GeoLatLong m_Origin = new GeoLatLong();
}
