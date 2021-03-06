package lets.cool.geo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

public class GridCanvas {
    final public int width, height, marginWidth, marginHeight;
    final private BufferedImage bufferedImage;
    final private Graphics2D graphics;
    final private Rectangle2D boundary;
    private double scaleX, scaleY;
    private double scale;
    private float lineWidth;
    private float[] dash = null;

    static private Rectangle2D pointsToRectangle(GPoint... points) {
        return pointsToRectangle(Arrays.asList(points));
    }
    static private Rectangle2D pointsToRectangle(java.util.List<GPoint> points) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (GPoint p: points) {
            if (p instanceof GPoint.Virtual) continue;
            double x = p.getX();
            double y = p.getY();
            if (x > maxX)
                maxX = x;
            if (x < minX)
                minX = x;
            if (y > maxY)
                maxY = y;
            if (y < minY)
                minY = y;
        }

        return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
    }

    public GridCanvas(GPoint... points) {
        this(pointsToRectangle(points));
    }

    public GridCanvas(java.util.List<GPoint> points) {
        this(pointsToRectangle(points));
    }

    public GridCanvas(int width, int height, int marginWidth, int marginHeight, GPoint... points) {
        this(pointsToRectangle(points), width, height, marginWidth, marginHeight);
    }

    public GridCanvas(double minX, double minY, double maxX, double maxY) {
        this(new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY));
    }

    public GridCanvas(Rectangle2D rect) {
        this(rect, 4200, 4200, 100, 100);
    }

    public GridCanvas(Rectangle2D rect, int width, int height, int marginWidth, int marginHeight) {
        double w = width-marginWidth*2;
        double h = height-marginHeight*2;
        if (w/h > rect.getWidth()/rect.getHeight()) {
            w = h * rect.getWidth() / rect.getHeight();
        } else {
            h = w * rect.getHeight() / rect.getWidth();
        }
        width = (int)Math.ceil(w + marginWidth*2);
        height = (int)Math.ceil(h + marginHeight*2);

        //
        this.width = width;
        this.height = height;
        this.marginWidth = marginWidth;
        this.marginHeight = marginHeight;
        this.boundary = rect;

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        graphics = bufferedImage.createGraphics();
        graphics.translate(marginWidth, height-marginHeight);
        scaleX = (width-marginWidth*2)/rect.getWidth();
        scaleY = (height-marginHeight*2)/rect.getHeight();
        scale = Math.min(scaleX, scaleY);
        graphics.scale(scale, - scale);
        graphics.translate(-rect.getX(), -rect.getY());
        lineWidth = 1;
        graphics.setStroke(new BasicStroke((float)(lineWidth/scale)));
        Font font = new Font("Monospaced", Font.PLAIN, 16);
        Font fontSc = font.deriveFont(AffineTransform.getScaleInstance(1/scaleX, -1/scaleY));
        graphics.setFont(fontSc);
        //graphics.setColor(Color.white);
        //graphics.fill(rect);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public Color getColor() {
        return graphics.getColor();
    }

    public void setColor(Color c) {
        graphics.setColor(c);
    }

    private void updateStrokeSetting() {
        if (dash == null) {
            graphics.setStroke(new BasicStroke((float)(lineWidth/scale)));
        } else {
            BasicStroke dashed = new BasicStroke((float)(lineWidth/scale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10.0f, dash, 0.0f);
            graphics.setStroke(dashed);
        }
    }

    public void setLineWidth(float width) {
        lineWidth = width;
        updateStrokeSetting();
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void enableDashLine(float[] dashPattern) {
        this.dash = dashPattern;
        updateStrokeSetting();
    }

    public void disableDashLine() {
        this.enableDashLine(null);
    }

    public void drawLine(GPoint p1, GPoint p2) {
        Line2D line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        graphics.draw(line);
    }

    private Path2D createPathByLines(Iterator<GPoint> lines) {
        Path2D path = new Path2D.Double();
        GPoint last = lines.next();
        path.moveTo(last.getX(), last.getY());
        while (lines.hasNext()) {
            last = lines.next();
            path.lineTo(last.getX(), last.getY());
        }
        return path;
    }

    public void drawLines(Iterable<GPoint> lines) {
        drawLines(lines.iterator());
    }

    public void drawLines(Iterator<GPoint> lines) {
        Path2D path = createPathByLines(lines);
        graphics.draw(path);
    }

    public void fillLines(Iterable<GPoint> lines) {
        fillLines(lines.iterator());
    }

    public void fillLines(Iterator<GPoint> lines) {
        Path2D path = createPathByLines(lines);
        //path.closePath();
        graphics.fill(path);
    }

    public void saveToPNG(String filename) {
        saveToPNG(new File(filename));
    }

    public void saveToPNG(File file) {
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawCircle(GPoint center, double r) {
        double rr = r / scale;
        double hr = rr / 2;
        graphics.draw(new Ellipse2D.Double(center.getX()-hr, center.getY()-hr, rr, rr));
    }

    public void drawSquare(GPoint center, double r) {
        double rr = r / scale;
        double hr = rr / 2;
        graphics.draw(new Rectangle.Double(center.getX()-hr, center.getY()-hr, rr, rr));
    }

    public void drawText(String text, GPoint point) {
        double shift = boundary.getWidth() * 12 / width;
        IntStream.range(0, text.length()).forEach(i -> {
            char c = text.charAt(i);
            graphics.drawString(Character.toString(c), (float)(point.getX()+shift*i), (float)point.getY());
        });
        //graphics.drawString(text, (float)point.getX(), (float)point.getY());
    }

    public void draw(Shape shape) {
        graphics.draw(shape);
    }

    public void fillBackground() {
        graphics.fill(boundary);
    }

    public void drawDebug(java.util.List<GPoint> points, String filename) {
        // background
        setColor(Color.WHITE);
        fillBackground();
        setLineWidth(4);
        setColor(Color.RED);
        // content
        GPoint first = null, last = null;
        for (GPoint p: points) {
            if (p instanceof GPoint.Virtual) {
                last = null;
                continue;
            }
            if (first==null) {
                first = last = p;
                continue;
            }
            if (last != null) {
                graphics.draw(new Line2D.Double(last.toPoint2D(), p.toPoint2D()));
            }
            last = p;
        }
        /*if (first!=null && last!=null) {
            graphics.draw(new Line2D.Double(last.toPoint2D(), first.toPoint2D()));
        }*/
        saveToPNG(new File(filename));
    }
}
