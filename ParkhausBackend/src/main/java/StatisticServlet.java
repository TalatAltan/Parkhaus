import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action.equals("topParkingSpot")) {
            List<Customer> customerList = getCustomerList();

            Map parkingSpotCount = customerList.stream().collect(Collectors.groupingBy(c -> c.parkingSpotId, Collectors.counting()));
            int topParkingSpot = Integer.parseInt(parkingSpotCount.entrySet().stream().max(Map.Entry.comparingByValue()).get().toString().split("=")[0]);

            PrintWriter out = response.getWriter();
            out.println(topParkingSpot);
        }
        else if(action.equals("avg")){
            List<Customer> customerList = getCustomerList();
            double avgDuration = customerList.stream().filter(c -> c.enterTimestamp != null)
                    .mapToDouble(c -> c.durationInMiliseconds / 1000)
                    .average().getAsDouble();
            double avgPrice = customerList.stream().filter(c -> c.enterTimestamp != null)
                    .mapToDouble(c -> c.price)
                    .average().getAsDouble();

            PrintWriter out = response.getWriter();
            out.println(avgDuration + " s / " + avgPrice + " EUR");
        }
        else if(action.equals("max")){
            List<Customer> customerList = getCustomerList();
            double maxDuration = customerList.stream().filter(c -> c.enterTimestamp != null)
                    .mapToDouble(c -> c.durationInMiliseconds / 1000).max().getAsDouble();
            double maxPrice = customerList.stream().filter(c -> c.enterTimestamp != null)
                    .mapToDouble(c -> c.price).max().getAsDouble();

            PrintWriter out = response.getWriter();
            out.println(maxDuration + " s / " + maxPrice + " EUR");
        }
        else if(action.equals("colorChart")){
            List<Customer> customerList = getCustomerList();

            int blue = (int) customerList.stream().filter(c -> c.car.contains("blue")).count();
            int green = (int) customerList.stream().filter(c -> c.car.contains("green")).count();
            int orange = (int) customerList.stream().filter(c -> c.car.contains("orange")).count();
            int purple = (int) customerList.stream().filter(c -> c.car.contains("purple")).count();
            int red = (int) customerList.stream().filter(c -> c.car.contains("red")).count();
            int yellow = (int) customerList.stream().filter(c -> c.car.contains("yellow")).count();

            response.setContentType("application/json");
            response.setStatus(200);
            PrintWriter out = response.getWriter();
            out.println("{\"blue\": " + blue + ", \"green\": " + green + ", \"orange\": " + orange + ", \"purple\": " + purple + ", \"red\": " + red + ", \"yellow\": " + yellow + "}");
        }
    }

    private ServletContext getContext(){
        return getServletConfig().getServletContext();
    }

    private void setCustomerList(List<Customer> customerList){
        getContext().setAttribute("customerList", customerList);
    }

    private List<Customer> getCustomerList(){
        List<Customer> customerList = (List<Customer>)getContext().getAttribute("customerList");

        if(customerList == null){
            List<Customer> newCustomerList = new ArrayList<>();
            setCustomerList(newCustomerList);
            return newCustomerList;
        }

        return customerList;
    }
}
