package bloodbuddy.controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RequestContactServlet")
public class RequestContactServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String donorName = request.getParameter("donorName");
        String donorPhone = request.getParameter("donorPhone");

        try {
            // Simulate SMS via Fast2SMS formatting
            String apiKey = "VCw8odQev3MF2TCKrOFETAB9ewtqP4OL6aVGH98I0PGfCcLxBn9GxcoXHM6F";
            String message = "Hi " + donorName + ",\nA BloodBuddy user is trying to reach you.\nPlease expect a call soon.";
            String encodedMessage = URLEncoder.encode(message, "UTF-8");

            String url = "https://www.fast2sms.com/dev/bulkV2?" +
                         "authorization=" + apiKey +
                         "&sender_id=FSTSMS" +
                         "&message=" + encodedMessage +
                         "&language=english" +
                         "&route=q" +
                         "&numbers=" + donorPhone;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("cache-control", "no-cache");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int code = conn.getResponseCode();
            System.out.println("✅ SMS sent! Response code: " + code);

            // Store donor name in session and redirect with flag
            HttpSession session = request.getSession();
            session.setAttribute("smsSentTo", donorName);
            response.sendRedirect("results.jsp?fromSMS=true");

        } catch (Exception e) {
            System.out.println("❌ Error sending SMS: " + e.getMessage());
            response.setContentType("text/html");
            response.getWriter().println(
                "<script>alert('❌ Failed to send SMS. Please try again.'); window.location='results.jsp';</script>"
            );
        }
    }
}
