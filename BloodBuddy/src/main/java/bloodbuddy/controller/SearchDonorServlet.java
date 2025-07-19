package bloodbuddy.controller;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/SearchDonorServlet")
public class SearchDonorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Donor model 
    public static class Donor {
        private String name, bloodGroup, location, employeeId, phone;

        public Donor(String name, String bloodGroup, String location, String employeeId, String phone) {
            this.name = name;
            this.bloodGroup = bloodGroup;
            this.location = location;
            this.employeeId = employeeId;
            this.phone = phone;
        }

        public String getName() { return name; }
        public String getBloodGroup() { return bloodGroup; }
        public String getLocation() { return location; }
        public String getEmployeeId() { return employeeId; }
        public String getPhone() { return phone; }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bloodGroup = request.getParameter("blood_group");
        String location = request.getParameter("location");

        String url = "jdbc:mysql://localhost:3306/bloodbuddy";
        String user = "root";
        String password = "Nani786Janibegum@";

        ArrayList<Donor> donorList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "SELECT name, blood_group, location, employee_id, phone_number FROM donors WHERE blood_group = ? AND location = ? AND consent = 1";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bloodGroup);
            stmt.setString(2, location);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                donorList.add(new Donor(
                    rs.getString("name"),
                    rs.getString("blood_group"),
                    rs.getString("location"),
                    rs.getString("employee_id"),
                    rs.getString("phone_number")
                ));
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("donors", donorList);
        request.setAttribute("blood_group", bloodGroup);
        request.setAttribute("location", location);
        request.setAttribute("totalDonors", donorList.size());
        getServletContext().setAttribute("lastSearchResults", donorList);


        request.getRequestDispatcher("results.jsp").forward(request, response);
    }
}