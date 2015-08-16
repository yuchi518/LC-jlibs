package me.tuple.draw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Yuchi on 2015/2/22.
 */
public class ScalingBitmap {

    final double fromX, fromY;
    final double toX, toY;
    final public double sX, sY;
    final int width, height;
    BufferedImage bufImage;
    Graphics2D graphics;

    public ScalingBitmap(double fromX, double fromY, double toX, double toY, int width, int height) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.width = width;
        this.height = height;
        bufImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        graphics = bufImage.createGraphics();

        sX = width/(toX-fromX);
        sY = height/(toY-fromY);

        graphics.translate(-fromX*sX, -fromY*sY);
    }

    public void setColor(Color strokeColor)
    {
        graphics.setColor(strokeColor);
    }

    public void setAntialias(boolean antialias)
    {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias?RenderingHints.VALUE_ANTIALIAS_ON:RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void drawLine(double fx, double fy, double tx, double ty)
    {
        graphics.draw(new Line2D.Double(fx * sX, fy * sY, tx * sX, ty * sY));
    }

    public void drawLines(double xy[])
    {
        drawLines(xy, 0, xy.length, false);
    }

    public void drawLines(double xy[], int fromIndex/*Include*/, int toIndex/*Exclude*/)
    {
        drawLines(xy, fromIndex, toIndex, false);
    }

    public void drawLines(double xy[], int fromIndex/*Include*/, int toIndex/*Exclude*/, boolean autoClose)
    {
        int len = toIndex-fromIndex;
        if (len < 4) throw new IllegalArgumentException("fromIndex ~ toIndex should contains more than 4 double elements.");
        if ((len%2) != 0) throw new IllegalArgumentException("fromIndex ~ toIndex should contains 2x double elements.");

        int i=0;
        double firstX = xy[fromIndex+(i++)];
        double firstY = xy[fromIndex+(i++)];
        double lastX = firstX;
        double lastY = firstY;

        for (; i<len; )
        {
            double x = xy[fromIndex+(i++)];
            double y = xy[fromIndex+(i++)];
            graphics.draw(new Line2D.Double(lastX*sX, lastY*sY, x*sX, y*sY));
            lastX = x;
            lastY = y;
        }

        if (autoClose)
        {
            if (firstX!=lastX && firstY!=lastY)
            {
                graphics.draw(new Line2D.Double(lastX*sX, lastY*sY, firstX*sX, firstY*sY));
            }
        }
    }

    public void draw(Shape shape)
    {
        //shape = AffineTransform.getScaleInstance(sX, sY).createTransformedShape(shape);
        graphics.draw(shape);
    }

    public void fill(Shape shape)
    {
        //shape = AffineTransform.getScaleInstance(sX, sY).createTransformedShape(shape);
        graphics.fill(shape);
    }

    public void saveToPNGFile(String fileName) throws IOException {
        this.saveToFile(new File(fileName), "png");
    }

    public void saveToPNGFile(File f) throws IOException {
        this.saveToFile(f, "png");
    }

    public void saveToFile(String fileName, String format) throws IOException {
        this.saveToFile(new File(fileName), format);
    }

    public void saveToFile(File f, String format) throws IOException {
        ImageIO.write(bufImage, format, f);
    }
}
