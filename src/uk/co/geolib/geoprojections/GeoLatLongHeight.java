


package uk.co.geolib.geoprojections;

public class GeoLatLongHeight extends GeoLatLong{

	

    /// <summary>
    /// Constructor initialises members to 0.
    /// </summary>
	public GeoLatLongHeight() 
	{
		super();
		m_dHeight = 0.0;
	}


	/// <summary>
	/// Constructor.
	/// </summary>
	public GeoLatLongHeight(double dLatitudeRadians, 
				double dLongitudeRadians, double dAltMetres) 
	{
		super(dLatitudeRadians, dLongitudeRadians);
		m_dHeight = dAltMetres;
	}

	/// <summary>
	/// Constructor.
	/// </summary>
	public GeoLatLongHeight(GeoLatLongHeight Other)
	{
		m_dHeight = Other.GetHeight();
		m_dLat = Other.GetLat();
		m_dLong = Other.GetLong();
	}

	/// <summary>
	/// Constructor.
	/// </summary>
	public GeoLatLongHeight(GeoLatLong Other, double dHeight)
	{
		m_dLat = Other.GetLat();
		m_dLong = Other.GetLong();
		m_dHeight = dHeight;
	}


	/// <summary>
	/// Conversion to geocentric.
	/// </summary>
	public void GeocentricWSG84(C3DPoint xyzPoint)
	{
		if(!IsValid())
			return;

		double Sin_Lat = Math.sin(m_dLat);  //  sin(Latitude)
		double Cos_Lat = Math.cos(m_dLat);  //  cos(Latitude)
		double Sin2_Lat = Sin_Lat * Sin_Lat; // Square of sin(Latitude)
		double Rn = Constants.conGeocent_Major / (Math.sqrt(1.0e0 - Constants.conGeocent_e2 * Sin2_Lat)); //  Earth radius at location

		xyzPoint.x = (Rn + m_dHeight) * Cos_Lat * Math.cos(m_dLong);

		xyzPoint.y = (Rn + m_dHeight) * Cos_Lat * Math.sin(m_dLong);

		xyzPoint.z = ((Rn * (1 - Constants.conGeocent_e2)) + m_dHeight) * Sin_Lat;

		return;
	}


	/// <summary>
	/// Conversion to geocentric.
	/// </summary>
	public void Geocentric(C3DPoint xyzPoint)
	{
		if(!IsValid())
			return;

		double Sin_Lat = Math.sin(m_dLat);  //  sin(Latitude)
		double Cos_Lat = Math.cos(m_dLat);  //  cos(Latitude)

		xyzPoint.x = (Constants.conEARTH_RADIUS_METRES + m_dHeight) * Cos_Lat * Math.cos(m_dLong);

		xyzPoint.y = (Constants.conEARTH_RADIUS_METRES + m_dHeight) * Cos_Lat * Math.sin(m_dLong);

		xyzPoint.z = (Constants.conEARTH_RADIUS_METRES + m_dHeight) * Sin_Lat;

		return;
	}

	/// <summary>
	/// Slant Range.
	/// </summary>
	public double SlantRangeWSG84(GeoLatLongHeight Other)
	{
		C3DPoint ptThis = new C3DPoint();

		this.GeocentricWSG84(ptThis);

		C3DPoint ptOther = new C3DPoint();

		Other.GeocentricWSG84(ptOther);

		return ptThis.Distance(ptOther);

	}


	/// <summary>
	/// Slant Range.
	/// </summary>
	public double SlantRange(GeoLatLongHeight Other)
	{
		C3DPoint ptThis = new C3DPoint();

		this.Geocentric(ptThis);

		C3DPoint ptOther = new C3DPoint();

		Other.Geocentric(ptOther);

		return ptThis.Distance(ptOther);
	}


	/// <summary>
	/// Finds the range from one position to another based on sperical earth and
	/// great circles. Range is in metres. Error between this and WSG84 slant range is
	/// less than 1% at ranges up to 2000km. (Spherical Law of cosines).
	/// </summary>
	public double Range(GeoLatLongHeight Other)
	{
		double dLatO = Other.GetLat();
		double dLongO = Other.GetLong();

		if (m_dLat == dLatO && m_dLong == dLongO) return 0;

		double dA1 = Math.sin(m_dLat)* Math.sin(dLatO) + 
		Math.cos(m_dLat) * Math.cos(dLatO)* Math.cos(m_dLong - dLongO);

		double dA = Math.acos(dA1) * (Constants.conEARTH_RADIUS_METRES + (m_dHeight + Other.GetHeight()) / 2.0);

		return  dA;
	}

	/// <summary>
	/// Finds the range from one position to another based on sperical earth and
	/// great circles. Range is in metres. Error between this and WSG84 slant range is
	/// less than 1% at ranges up to 2000km. (Spherical Law of cosines).
	/// </summary>
	public double Range(GeoLatLong Other)
	{
		return super.Range(Other);
	}


	/// <summary>
	/// Assignment.
	/// </summary>
	public GeoLatLongHeight Set(GeoLatLong Other)
	{
		m_dLat = Other.GetLat();
		m_dLong= Other.GetLong();
		m_dHeight = 0;
		
        return this;
	}

	/// <summary>
	/// Assignment.
	/// </summary>
	public GeoLatLongHeight Set(GeoLatLongHeight Other)
	{
		m_dLat = Other.GetLat();
		m_dLong= Other.GetLong();
		m_dHeight = Other.GetHeight();

		return this;
	}


	/// <summary>
	/// Equality.
	/// </summary>
	public boolean equals(GeoLatLongHeight Other)
	{
		if (!super.equals(Other))
			return false;

		boolean bHeightClose;

		if ( m_dHeight == 0 )
			bHeightClose = Other.GetHeight() == 0;
		else
			bHeightClose = Math.abs(( Other.GetHeight() - m_dHeight ) / m_dHeight ) 
													< Constants.conEqualityTolerance;
		return bHeightClose;

	}



	/// <summary>
	/// Equality.
	/// </summary>
	public boolean equals(GeoLatLong Other)
	{
		return super.equals(Other);
	}



	/// <summary>
	/// Assignment.
	/// </summary>
	public void Set(double dHeadingRadians, double dRange, 
								GeoLatLong LLOrigin)
	{
		super.Set( dHeadingRadians, dRange,  LLOrigin);
		m_dHeight = 0;
	}
	
	/// <summary>
	/// Assignment.
	/// </summary>
	public double GetHeight()
	{
		return m_dHeight;
	}
	
	private double m_dHeight = 0.0;
}
