import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Board<T extends Drawable> {
    private final List<T> shapes = new ArrayList<>();

    public void addNewShape(T shape) {
        shapes.add(shape);
    }

    public double allPerimeter() {
        return shapes.stream().mapToDouble(T::getPerimeter).sum();
    }

    public double allSurface() {
        return shapes.stream().mapToDouble(T::getSurface).sum();
    }

    public double allSide() {
        return shapes.stream().mapToDouble(shape -> {
            try {
                return shape.getSide();
            } catch (SideNotDefinedException exception) {
                return 0;
            }
        }).sum();
    }

    public double allSideException() throws SideNotDefinedException {
        double sideSum = 0;
        for (T shape : shapes)
            sideSum += shape.getSide();
        return sideSum;
    }

    public T minimumSurface() {
        return Collections.min(shapes, Comparator.comparingDouble(T::getSurface));
    }

    public ArrayList<T> sortedList(double x) {
        return shapes.stream().filter(shape -> shape.getPerimeter() > x)
                .sorted(Comparator.comparingDouble(T::getSurface))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}