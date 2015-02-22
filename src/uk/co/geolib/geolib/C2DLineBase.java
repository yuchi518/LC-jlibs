package uk.co.geolib.geolib;

import java.util.ArrayList;


public abstract class C2DLineBase  extends C2DBase {
    /// <summary>
    /// Intersection with another
    /// </summary>
    public abstract boolean Crosses(C2DLineBase Other,  ArrayList<C2DPoint> IntersectionPts);
        /// <summary>
    /// Intersection with another
    /// </summary>
    public abstract boolean Crosses(C2DLineBase Other);
    /// <summary>
    /// Minimum distance to a point.
    /// </summary>
    public abstract double Distance(C2DPoint TestPoint,  C2DPoint ptOnThis);
    /// <summary>
    /// Minimum distance to another.
    /// </summary>
    public abstract double Distance(C2DLineBase Other,  C2DPoint ptOnThis,  C2DPoint ptOnOther);
    /// <summary>
    /// The point from.
    /// </summary>
    public abstract C2DPoint GetPointFrom();
    /// <summary>
    /// The point to.
    /// </summary>
    public abstract C2DPoint GetPointTo();
    /// <summary>
    /// The length.
    /// </summary>
    public abstract double GetLength();
    /// <summary>
    /// Reverse direction of the line.
    /// </summary>
    public abstract void ReverseDirection();
    /// <summary>
    /// Given a set of points on the line, this function creates sub lines defined by those points.
    /// Required by intersection, union and difference functions in the C2DPolyBase class.
    /// </summary>
    public abstract void GetSubLines(ArrayList<C2DPoint> PtsOnLine,  ArrayList<C2DLineBase> LineSet);
    /// <summary>
    /// Creats a copy of the line.
     /// </summary> 
    public abstract C2DLineBase CreateCopy();

    /// <summary>
    ///  Transform by a user defined transformation. e.g. a projection.
    /// </summary>
    public abstract  void Transform(CTransformation pProject);

    /// <summary>
    ///  Transform by a user defined transformation. e.g. a projection.
    /// </summary>
    public abstract  void InverseTransform(CTransformation pProject);


}
