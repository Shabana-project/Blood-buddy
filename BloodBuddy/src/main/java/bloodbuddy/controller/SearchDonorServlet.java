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

    // ✅ Updated Donor model without phone number
    public static class Donor {
        private String name, bloodGroup, location, employeeId;

        public Donor(String name, String bloodGroup, String location, String employeeId) {
            this.name = name;
            this.bloodGroup = bloodGroup;
            this.location = location;
            this.employeeId = employeeId;
        }

        public String getName() { return name; }
        public String getBloodGroup() { return bloodGroup; }
        public String getLocation() { return location; }
        public String getEmployeeId() { return employeeId; }
    }

    @Override
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

            // ✅ Only select what's needed (no phone number)
            String sql = "SELECT name, blood_group, location, employee_id FROM donors WHERE blood_group = ? AND location LIKE ? AND consent = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bloodGroup);
            stmt.setString(2, "%" + location + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                donorList.add(new Donor(
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("location"),
                        rs.getString("employee_id")
                ));
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Forward updated donor list to new results.jsp layout
        request.setAttribute("donors", donorList);
        request.getRequestDispatcher("results.jsp").forward(request, response);
    }
}
