package bloodbuddy.controller;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterDonorServlet")
public class RegisterDonorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L; // Added to avoid warning

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String bloodGroup = request.getParameter("blood_group");
        String location = request.getParameter("location");
        String phone = request.getParameter("phone_number");
        String empId = request.getParameter("employee_id");
        String consent = request.getParameter("consent"); // Capture consent checkbox

        if (consent == null) {
            // Consent not given
            response.sendRedirect("register.html?toast=fail");
            return;
        }

        System.out.println("Consent received. Proceeding with registration...");
        System.out.println("Received: " + name + ", " + bloodGroup + ", " + location + ", " + phone + ", " + empId);

        String url = "jdbc:mysql://localhost:3306/bloodbuddy";
        String user = "root";
        String password = "Nani786Janibegum@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            // Duplicate check
            String checkSql = "SELECT * FROM donors WHERE name = ? AND phone_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name);
            checkStmt.setString(2, phone);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                conn.close();
                response.sendRedirect("register.html?toast=duplicate");
                return;
            }

            // Insert donor
            String sql = "INSERT INTO donors (name, blood_group, location, phone_number, employee_id, consent) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, bloodGroup);
            stmt.setString(3, location);
            stmt.setString(4, phone);
            stmt.setString(5, empId);
            stmt.setInt(6, 1); // 1 means 'consent given'


            int rowsInserted = stmt.executeUpdate();
            conn.close();

            if (rowsInserted > 0) {
                response.sendRedirect("register.html?toast=true");
            } else {
                response.sendRedirect("register.html?toast=fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h2>❌ Server Error: " + e.getMessage() + "</h2>");
        }
    }
}
