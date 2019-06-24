import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ParkingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();

        Customer customer = new Gson().fromJson(reader, Customer.class);

        if(customer.leaveTimestamp != null){
            customer.durationInMiliseconds = getDurationDifference(customer.enterTimestamp, customer.leaveTimestamp);
            int hours = getFullHours(customer.durationInMiliseconds);
            customer.price = hours * Float.parseFloat(getContext().getInitParameter("pricePerHour"));
        }

        addOrUpdateCustomerList(customer);

        PrintWriter out = response.getWriter();
        out.println(listToHtmlTable(getCustomerList()));

        response.setStatus(200);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action.equals("isAviable")){
            int id = Integer.parseInt(request.getParameter("id"));

            Customer customer = getCustomerList().stream().filter(c -> c.parkingSpotId == id && c.leaveTimestamp == null)
                    .findFirst().orElse(null);

            if(customer == null) {
                response.setStatus(204);
            }else{
                response.setContentType("application/json");
                response.setStatus(200);

                PrintWriter out = response.getWriter();
                out.println(new Gson().toJson(customer));
            }
        }
        else if(action.equals("clearSession")){
            response.setStatus(200);

            setCustomerList(null);
        }
    }

    private ServletContext getContext(){
        return getServletConfig().getServletContext();
    }

    private String listToHtmlTable(List<Customer> customerList){
        String table = "<tr><th>UUID</th><th>Car</th><th>ParkingSpot</th><th>Enter Time</th><th>Leave Time</th><th>Duration in m (multiplier)</th><th>Price in EUR</th></tr>";
        for (Customer customer : customerList) {
            table += "<tr>";
            table += "<td>" + customer.uuid + "</td>";
            table += "<td>" + customer.car + "</td>";
            table += "<td>" + customer.parkingSpotId + "</td>";
            table += "<td>" + customer.enterTimestamp + "</td>";
            table += "<td>" + customer.leaveTimestamp + "</td>";
            table += "<td>" + ((customer.durationInMiliseconds / 1000) % 3600) / 60 + "</td>";
            table += "<td>" + customer.price + "</td>";
            table += "</tr>";
        }
        return table;
    }

    private long getDurationDifference(Timestamp start, Timestamp end){
        long milliseconds = end.getTime() - start.getTime();

        return milliseconds * Integer.parseInt(getContext().getInitParameter("timeMutliplicator"));
    }

    private int getFullHours(long milliseconds)
    {
        int seconds = (int) milliseconds / 1000;

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        if(minutes > 0)
            hours++;

        return hours;
    }

    private void addOrUpdateCustomerList(Customer customer){
        List<Customer> customerList = getCustomerList();

        Customer oldCustomer = customerList.stream().filter(c -> c.uuid.equals(customer.uuid))
                .findFirst().orElse(null);

        if(oldCustomer != null){
            customerList.set(customerList.indexOf(oldCustomer), customer);
        }
        else{
            customerList.add(customer);
        }

        setCustomerList(customerList);
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
