import java.awt.*;

public class ParkingSpot {
    int id;
    Point xyTop;
    Point xyBottom;

    public ParkingSpot(int id, int x1, int y1, int x2, int y2){
        this.id = id;
        this.xyTop = new Point(x1, y1);
        this.xyBottom = new Point(x2, y2);
    }
}
