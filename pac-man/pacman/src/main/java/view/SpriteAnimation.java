package view;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

// source: https://netopyr.com/2012/03/09/creating-a-sprite-animation-with-javafx/
// added modifications to support separation of different sprite frames
public class SpriteAnimation extends Transition {

    private final ImageView imageView;
    private final int count;
    private final int columns;
    private final int seperation;
    private final int offsetX;
    private final int offsetY;
    private final int width;
    private final int height;
    private int lastIndex;

    public SpriteAnimation(ImageView imageView, Duration duration, int count, int columns, int seperation,
            int offsetX, int offsetY, int width, int height) {
        this.imageView = imageView;
        this.count = count;
        this.columns = columns;
        this.seperation = seperation;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

    // single row sprite
    public SpriteAnimation(ImageView imageView, Duration duration, int count, int seperation,
            int offsetX, int offsetY, int width, int height) {
        this(imageView,duration, count, count, seperation, offsetX, offsetY, width, height);
    }

    protected void interpolate(double frac) {
        final int index = Math.min((int) Math.floor(frac * count), count - 1);
        if (index != lastIndex) {
            final int x = (index % columns) * (width + seperation) + offsetX;
            final int y = (index / columns) * height + offsetY;
            imageView.setViewport(new Rectangle2D(x, y, width, height));
            lastIndex = index;
        }
    }
}
