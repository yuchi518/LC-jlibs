package uk.co.geolib.geolib;

public abstract class CTransformation {
    /// <summary>
    /// Transform the given point.
    /// </summary>
    public abstract void Transform(double dx, double dy);
    /// <summary>
    /// Inverse transform the given point.
    /// </summary>
    public abstract void InverseTransform(double dx, double dy);
}
