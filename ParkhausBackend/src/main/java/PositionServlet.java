import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class PositionServlet extends HttpServlet {

    private static List<ParkingSpot> parkingSpotList = Arrays.asList(
            new ParkingSpot(1,136,422, 209, 463),
            new ParkingSpot(2,136,376, 209, 417),
            new ParkingSpot(3,136,330, 209, 371),
            new ParkingSpot(4,136,284, 209, 325),
            new ParkingSpot(5,136,238, 209, 279),
            new ParkingSpot(6,136,192, 209, 233),
            new ParkingSpot(7,136,146, 209, 187),
            new ParkingSpot(8,136,100, 209, 141),
            new ParkingSpot(9,136,54, 209, 95),
            new ParkingSpot(10,136,8, 209, 49),

            new ParkingSpot(11,288,422, 361, 463),
            new ParkingSpot(12,288,376, 361, 417),
            new ParkingSpot(13,288,330, 361, 371),
            new ParkingSpot(14,288,284, 361, 325),
            new ParkingSpot(15,288,238, 361, 279),
            new ParkingSpot(16,288,192, 361, 233),
            new ParkingSpot(17,288,146, 361, 187),
            new ParkingSpot(18,288,100, 361, 141),
            new ParkingSpot(19,288,54, 361, 95),
            new ParkingSpot(20,288,8, 361, 49),

            new ParkingSpot(21,366,422, 439, 463),
            new ParkingSpot(22,366,376, 439, 417),
            new ParkingSpot(23,366,330, 439, 371),
            new ParkingSpot(24,366,284, 439, 325),
            new ParkingSpot(25,366,238, 439, 279),
            new ParkingSpot(26,366,192, 439, 233),
            new ParkingSpot(27,366,146, 439, 187),
            new ParkingSpot(28,366,100, 439, 141),
            new ParkingSpot(29,366,54, 439, 95),
            new ParkingSpot(30,366,8, 439, 49),

            new ParkingSpot(31,518,422, 591, 463),
            new ParkingSpot(32,518,376, 591, 417),
            new ParkingSpot(33,518,330, 591, 371),
            new ParkingSpot(34,518,284, 591, 325),
            new ParkingSpot(35,518,238, 591, 279),
            new ParkingSpot(36,518,192, 591, 233),
            new ParkingSpot(37,518,146, 591, 187),
            new ParkingSpot(38,518,100, 591, 141),
            new ParkingSpot(39,518,54, 591, 95),
            new ParkingSpot(40,518,8, 591, 49));

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action.equals("byPosition")) {
            int x = Integer.parseInt(request.getParameter("posX"));
            int y = Integer.parseInt(request.getParameter("posY"));

            ParkingSpot parkingSpot = parkingSpotList.stream().filter(p -> p.xyTop.x < x && p.xyBottom.x > x && p.xyTop.y < y && p.xyBottom.y > y)
                    .findFirst().orElse(null);

            if (parkingSpot == null) {
                response.setStatus(404);
            } else {
                response.setContentType("application/json");
                response.setStatus(200);

                PrintWriter out = response.getWriter();
                out.println(new Gson().toJson(parkingSpot));
            }
        }
        else if(action.equals("byId")){
            int id = Integer.parseInt(request.getParameter("id"));

            ParkingSpot parkingSpot = parkingSpotList.stream().filter(p -> p.id == id)
                    .findFirst().orElse(null);

            if (parkingSpot == null) {
                response.setStatus(404);
            } else {
                response.setContentType("application/json");
                response.setStatus(200);

                PrintWriter out = response.getWriter();
                out.println(new Gson().toJson(parkingSpot));
            }
        }
    }
}