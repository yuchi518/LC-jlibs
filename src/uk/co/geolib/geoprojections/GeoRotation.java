

package uk.co.geolib.geoprojections;

/// <summary>
/// Class representing a circle.
/// </summary>
public class GeoRotation extends Transformation
{
    
    /// <summary>
    /// Constructor.
    /// </summary>
    public GeoRotation()
    {
    }


    /// <summary>
    /// Transform the given point.
    /// </summary>
    public void Transform(double dx, double dy) 
    {
        Rotate(dy, dx);
    }

    /// <summary>
    /// Inverse transform the given point.
    /// </summary>
    public void InverseTransform(double dx, double dy) 
    {
        InverseRotate(dy, dx);
    }



    /// <summary>
    /// Set origin.
    /// </summary>
    void SetOrigin(double dLatDegrees, double dLongDegrees)
    {
        m_Origin.SetLatDegrees(dLatDegrees);
        m_Origin.SetLongDegrees(dLongDegrees);
    }

    /// <summary>
    /// Inverse rotate.
    /// </summary>
    void Rotate(double dLatDegrees, double dLongDegrees) 
    {
        GeoLatLong rLatLong = new GeoLatLong();
        rLatLong.SetLatDegrees(dLatDegrees);
        rLatLong.SetLongDegrees(dLongDegrees);
    	
        Rotate(rLatLong);

        dLatDegrees = rLatLong.GetLatDegrees();
        dLongDegrees = rLatLong.GetLongDegrees();

    }
    
    /// <summary>
    /// Inverse rotate.
    /// </summary>
    void Rotate(GeoLatLong rLatLong) 
    {
        Double dRange = 0.0;
        Double dHeading = 0.0;
        m_Origin.RangeAndHeading(rLatLong, dRange, dHeading);

        rLatLong.Set(dHeading, dRange, new GeoLatLong());

    }
    
    /// <summary>
    /// Inverse rotate.
    /// </summary>
    void InverseRotate(double dLatDegrees, double dLongDegrees) 
    {
        GeoLatLong rLatLong = new GeoLatLong();
        rLatLong.SetLatDegrees(dLatDegrees);
        rLatLong.SetLongDegrees(dLongDegrees);
    	
        InverseRotate(rLatLong);

        dLatDegrees = rLatLong.GetLatDegrees();
        dLongDegrees = rLatLong.GetLongDegrees();
    }

    /// <summary>
    /// Inverse rotate.
    /// </summary>
    void InverseRotate(GeoLatLong rLatLong) 
    {
        Double dRange = 0.0;
        Double dHeading = 0.0;
        new GeoLatLong().RangeAndHeading(rLatLong, dRange, dHeading);

        rLatLong.Set(dHeading, dRange, m_Origin);

    }

    private GeoLatLong m_Origin = new GeoLatLong();
    
}