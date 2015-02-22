/*---------------------------------------------------------------------------
Copyright (C) GeoLib.
This code is used under license from GeoLib (www.geolib.co.uk). This or
any modified versions of this cannot be resold to any other party.
---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------
\file SlantRangeHeading.cpp
///Implementation file for a CSlantRangeHeading class.

Implementation file for a CSlantRangeHeading class.
---------------------------------------------------------------------------*/



package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing an albers equal area projection.
/// </summary>
public class SlantRangeHeading extends LambertAzimuthalEqualArea
{

    /// <summary>
    /// Constructor.
    /// </summary>
    public SlantRangeHeading()
    {
    }



    /// <summary>
    /// Project the given lat long to x, y using the input parameters to store the 
    /// result.
    /// </summary>
    public void Project(double dLatY, double dLongX)
    {
        super.Project(dLatY, dLongX);

        dLatY *= Constants.conEARTH_RADIUS_METRES;
        dLongX *= Constants.conEARTH_RADIUS_METRES;
    }



    /// <summary>
    /// Project the given x y to lat long using the input parameters to store /// the result.	
    /// </summary>
    public void InverseProject(double dLatY, double dLongX) 
    {
        dLatY /= Constants.conEARTH_RADIUS_METRES;
        dLongX /= Constants.conEARTH_RADIUS_METRES;

        super.InverseProject(dLatY, dLongX);
    }
}



